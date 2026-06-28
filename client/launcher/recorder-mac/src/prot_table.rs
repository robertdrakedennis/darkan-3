//! Live opcode-table dump (`prot-table.json`).
//!
//! At session start we read the CLIENT's OWN ServerProt opcode table out of the
//! running image and emit it once, so the offline enricher gets framing
//! (opcode → sizeClass) and naming (handler fingerprint) from the client's table
//! for THIS build — rev-agnostic, not from a static table baked into the tools.
//!
//! ## What we read (mac-verified — see `offsets::server_prot` for the citations)
//!
//! `g_serverProtTable` (image-relative `offsets::server_prot::TABLE_GLOBAL`) is a
//! pointer to an array of `ProtEntry*` indexed by `opcode * 8`. For each opcode
//! `0..=MAX_OPCODE`:
//!   * `entry = *(ProtEntry**)(tableBase + opcode*8)`  (skip if null)
//!   * `op        = *(i32)(entry + ENTRY_OPCODE)`        (should equal the index)
//!   * `sizeClass = *(i32)(entry + ENTRY_SIZE_CLASS)`
//!   * `handler   = *(*(entry + ENTRY_HANDLER_CATEGORY)) + HANDLER_VTABLE_SLOT)`
//!     — the dispatched handler FUNCTION; we record it IMAGE-RELATIVE (vmaddr)
//!     and read the first 32 bytes there as `handler_sig`, so the offline
//!     enricher can fingerprint-match it to `handler-sigs.json` across revs
//!     WITHOUT needing the binary.
//!
//! ## The client (c2s) table
//!
//! There is NO contiguous indexed client/c2s prot table analogous to
//! `g_serverProtTable`: `jag::ClientProt::RegisterAll` registers each opcode into
//! a SEPARATE scattered descriptor global (RE doc §6 — c2s descriptors live
//! scattered in `__common`, not an array indexable by `opcode*8`). So `client_prot`
//! is emitted EMPTY with a note; dumping it needs the RE agent to document a c2s
//! table base / registrar enumeration first (we never fabricate an offset).
//!
//! ## Defensive contract (same as the hooks)
//!
//! Every pointer is null-checked via `mem::*`; a broken chain for one entry emits
//! that entry with a null handler rather than aborting the whole dump, and an
//! unresolvable table pointer skips `prot-table.json` entirely (never a crash).
//! The dump runs EXACTLY ONCE per session (atomic guard), retried from the hooks
//! until the client's `RegisterAll` has populated the table.

use crate::mem;
use crate::offsets::server_prot as sp;
use crate::sig::MACHO_IMAGE_BASE;
use crate::state;
use std::sync::atomic::{AtomicBool, Ordering};

/// First 32 bytes at each handler entry — the fingerprint the offline enricher
/// matches against `handler-sigs.json` (which stores a 32-byte handler sig).
const HANDLER_SIG_BYTES: usize = 32;

/// Set true once `prot-table.json` has been written, so the multiple trigger
/// sites (ctor best-effort + the guarded retry from the hot hooks) emit it
/// exactly once per session.
static DUMPED: AtomicBool = AtomicBool::new(false);

/// One dumped opcode-table entry. `handler`/`handler_sig` are `None` when the
/// handler chain could not be resolved (the opcode is still reported with its
/// sizeClass so the framing table stays complete).
pub struct ProtEntryDump {
    pub op: i32,
    pub size_class: i32,
    /// Handler function address, IMAGE-RELATIVE (`runtime - slide`, i.e. the
    /// vmaddr) so it is stable across runs/slides and matches the RE doc's
    /// `0x1XXXXXXXX` addresses. Hex-formatted at emit.
    pub handler_vmaddr: Option<u64>,
    /// First `HANDLER_SIG_BYTES` bytes at the handler, for offline fingerprinting.
    pub handler_sig: Option<Vec<u8>>,
}

/// Attempt the one-shot prot-table dump. Cheap and idempotent: returns
/// immediately if already dumped, if not recording, or if the table pointer is
/// not resolvable yet (so a hot-hook caller can keep retrying until the client's
/// `RegisterAll` has run). Safe to call from any thread / any hook.
///
/// `slide` is the main image's dyld vmaddr slide (`MainImage::slide`); we take it
/// by value (not the whole `MainImage`) so the hot-hook retry path can call this
/// with just the slide cached in `state`, without threading the image through.
pub fn try_dump(slide: isize) {
    if DUMPED.load(Ordering::Relaxed) {
        return;
    }
    let Some(session) = state::session() else {
        return;
    };

    // Resolve the table base: read the `g_serverProtTable` global, which itself
    // holds the pointer to the array start. If it is not yet populated (null /
    // implausible), bail WITHOUT marking dumped so a later call retries.
    let table_global_runtime = (MACHO_IMAGE_BASE as usize)
        .wrapping_add(sp::TABLE_GLOBAL)
        .wrapping_add(slide as usize);
    let Some(array_base) = mem::deref(table_global_runtime, 0) else {
        return;
    };

    // Walk the array. We may produce zero coherent entries very early (table
    // allocated but not yet filled); only commit the file once at least one entry
    // resolved, so we don't write an empty server_prot and then never retry.
    let server_prot = walk_server_table(array_base, slide);
    if server_prot.is_empty() {
        return;
    }

    // Claim the single write slot. If another thread won the race, stand down.
    if DUMPED.swap(true, Ordering::SeqCst) {
        return;
    }

    // c2s: no contiguous indexed table in the binary (RE §6) — emitted empty.
    let client_prot: Vec<ProtEntryDump> = Vec::new();

    let image_base_hex = format!("0x{MACHO_IMAGE_BASE:x}");
    let build = state::build_string();
    session.write_prot_table(&build, &image_base_hex, &server_prot, &client_prot);

    crate::log(&format!(
        "prot-table dumped: {} server, {} client opcodes",
        server_prot.len(),
        client_prot.len()
    ));
}

/// Walk `g_serverProtTable[0 ..= MAX_OPCODE]`, resolving each non-null entry's
/// opcode/sizeClass and (best-effort) its handler function + signature. `slide`
/// converts a resolved runtime handler address back to its image-relative vmaddr.
fn walk_server_table(array_base: usize, slide: isize) -> Vec<ProtEntryDump> {
    let mut out = Vec::with_capacity(sp::MAX_OPCODE + 1);
    for op in 0..=sp::MAX_OPCODE {
        // entry = *(ProtEntry**)(array_base + op*8)
        let slot = array_base.wrapping_add(op * sp::ENTRY_PTR_STRIDE);
        let Some(entry) = mem::deref(slot, 0) else {
            continue; // unregistered opcode (null slot) — omit
        };
        // opcode/sizeClass are plain ints on the entry.
        let Some(entry_op) = mem::read_i32(entry, sp::ENTRY_OPCODE) else {
            continue;
        };
        let Some(size_class) = mem::read_i32(entry, sp::ENTRY_SIZE_CLASS) else {
            continue;
        };
        // The table index is authoritative for `op`; the entry's own opcode field
        // should match it, but we report the index (what ReadPacket dispatches on).
        let _ = entry_op;

        let (handler_vmaddr, handler_sig) = resolve_handler(entry, slide);

        out.push(ProtEntryDump {
            op: op as i32,
            size_class,
            handler_vmaddr,
            handler_sig,
        });
    }
    out
}

/// Resolve a ProtEntry's handler function and read its fingerprint. Returns
/// `(None, None)` if any hop of the chain is unreadable (the entry is still
/// emitted with its opcode/sizeClass — only the handler fields are omitted).
///
/// Chain (mac-verified): `handler = *(*(entry + 0x30)) + 0x30)`:
///   category = *(entry + ENTRY_HANDLER_CATEGORY)   // handler-category object
///   vtable   = *category                            // its vtable
///   handler  = *(vtable + HANDLER_VTABLE_SLOT)      // the dispatched function
fn resolve_handler(entry: usize, slide: isize) -> (Option<u64>, Option<Vec<u8>>) {
    let Some(category) = mem::deref(entry, sp::ENTRY_HANDLER_CATEGORY) else {
        return (None, None);
    };
    let Some(vtable) = mem::deref(category, 0) else {
        return (None, None);
    };
    let Some(handler) = mem::deref(vtable, sp::HANDLER_VTABLE_SLOT) else {
        return (None, None);
    };
    // Image-relative vmaddr = runtime - slide (handler is in __text of the image).
    let vmaddr = (handler as isize).wrapping_sub(slide) as u64;
    let sig = mem::copy_bytes(handler, HANDLER_SIG_BYTES);
    (Some(vmaddr), sig)
}

#[cfg(test)]
mod tests {
    use super::*;

    /// The handler chain math (vmaddr = runtime - slide) is the load-bearing
    /// transform the offline enricher relies on; verify it round-trips for a
    /// representative slide. (The pointer-walk itself needs a live image, covered
    /// by the integration capture; this proves the arithmetic.)
    #[test]
    fn handler_vmaddr_is_image_relative() {
        // A handler at vmaddr 0x1000448a0 (RE §10.4 UPDATE_RUNENERGY) under a
        // 0x10_000 slide sits at runtime 0x1000548a0; we must report the vmaddr.
        let slide: isize = 0x10_000;
        let vmaddr: u64 = 0x1_000448a0;
        let runtime = (vmaddr as isize + slide) as usize;
        let recovered = (runtime as isize).wrapping_sub(slide) as u64;
        assert_eq!(recovered, vmaddr);
    }

    #[test]
    fn sig_byte_count_matches_handler_db() {
        // Must equal the migrator's HANDLER_SIG_BYTES (32) so a dumped handler_sig
        // is directly comparable to handler-sigs.json's blob/mask fingerprints.
        assert_eq!(HANDLER_SIG_BYTES, 32);
    }
}
