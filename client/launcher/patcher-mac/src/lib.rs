//! Darkan RSA patcher — macOS `DYLD_INSERT_LIBRARIES` dylib
//!
//! The macOS analogue of the Linux `LD_PRELOAD` patcher
//! (`client/launcher/patcher/src/lib.rs`) and the Windows DLL-injection patcher
//! (`client/launcher/patcher-win/src/lib.rs`).
//!
//! When loaded via `DYLD_INSERT_LIBRARIES=/path/to/libdarkan_patcher.dylib`
//! before the NXT client (`rs2client`, Mach-O x86_64, rev 948-5) starts, a
//! `#[ctor]` constructor scans the rs2client main image's `__TEXT` segment for
//! known Jagex patterns and overwrites them so the client talks to the private
//! server.
//!
//! **Init ordering guarantee:** dyld runs inserted-dylib initializers BEFORE
//! the main executable's `__mod_init_func` C++ static initializers. rs2client
//! parses the RSA hex strings into BigIntegers in those static initializers, so
//! patching the strings from a `#[ctor]` lands in time — the same guarantee the
//! Linux `LD_PRELOAD` path relies on.
//!
//! Patches applied (when `DARKAN_RSA_MODULUS` is set, or `DARKAN_PROXY_MODE=1`):
//!
//! 1. **P1 — rs2client login RSA modulus** — 256-char lowercase hex ASCII string
//!    in `__TEXT,__cstring` (prefix `aad4a780…`). RELOCATED-identical to the
//!    Linux/Windows 948 value. Replaced with `DARKAN_RSA_MODULUS`, left-padded
//!    with `'0'` to 256 chars.
//!
//! 2. **P2 — rs2client JS5 RSA modulus** — 1024-char lowercase hex ASCII string
//!    (prefix `a6400fbc…`) used for master-index signature verification.
//!    Replaced with `DARKAN_JS5_RSA_MODULUS`, left-padded to 1024 chars.
//!    Skipped in proxy mode (JS5 goes directly to Jagex; their key must remain).
//!
//! 3. **P3 — rs2client HTTP port** — hardcoded port 80 inside the inlined
//!    `jag::WorldLobbyData::GetHTTPURL` (in `FUN_1002733c0`). On macOS the port
//!    is a 16-bit `short`, lowered by clang to `MOV AX, 0x50` = `66 b8 50 00`.
//!    The 2-byte LE u16 at offset +2 is overwritten with `DARKAN_HTTP_PORT`.
//!    Skipped in proxy mode (HTTP JS5 goes directly to Jagex on port 80).
//!
//! 4. **Launcher wrapper patches** — when loaded into
//!    `RuneScape.app/Contents/MacOS/RuneScape`, patch its download verifier RSA
//!    modulus and codebase regex so it can fetch the local client binary from
//!    `localhost`.
//!
//!    ⚠️ The Linux pattern `41 b8 50 00 00 00 74` and Windows pattern
//!    `b8 50 00 00 00 eb` are NOT used here. On macOS the latter matches two
//!    vtable method-offset dispatch sites (false positives) — patching those
//!    would corrupt virtual-method dispatch and crash the client. The 16-bit
//!    `66 b8 50 00` form is unique (1 match) and does not collide with them.
//!
//! If `DARKAN_RSA_MODULUS` is not set (and not proxy mode), the patcher does
//! nothing — so the same dylib is harmlessly present in live mode. If the
//! rs2client image is not found (e.g. we were injected into a wrapper process),
//! the patcher no-ops cleanly.
//!
//! See `docs/binary/patch-targets-948-mac.md` for the byte-precise spec.

use mach2::kern_return::KERN_SUCCESS;
use mach2::traps::mach_task_self;
use mach2::vm::mach_vm_protect;
use mach2::vm_prot::{VM_PROT_COPY, VM_PROT_EXECUTE, VM_PROT_READ, VM_PROT_WRITE};
use memchr::memmem::Finder;
use std::env;
use std::ffi::CStr;
use std::os::raw::c_char;
use std::ptr;

// ---------------------------------------------------------------------------
// Patch-target constants — mirror the Linux patcher verbatim where shared.
// ---------------------------------------------------------------------------

/// First 32 chars of the rs2client login RSA modulus hex string (1024-bit key).
/// Stored as a 256-char lowercase hex ASCII string in `__TEXT,__cstring`
/// (Ghidra symbol `jag::LoginManager::RSA_LOGIN_MODULUS_HEX`, VA `0x100E2346D`).
/// RELOCATED-identical to the Linux 948 / Windows 948-5
/// (`0x140bb7800`) value — same bytes, moved address. Parsed at startup by the
/// inlined `jag::math::BigInteger` hex parser in `__mod_init_func`.
const RS2CLIENT_MODULUS_PREFIX: &[u8] = b"aad4a7804c34bb788d52dbd5f70e5721";

/// Full length of the rs2client login RSA modulus hex string
/// (1024-bit = 128 bytes = 256 hex chars).
const RS2CLIENT_MODULUS_HEX_LEN: usize = 256;

/// First 32 chars of the rs2client JS5 RSA modulus hex string (4096-bit key).
/// Stored as a 1024-char lowercase hex ASCII string in `__TEXT,__cstring`
/// (Ghidra symbol `jag::Js5MasterIndex::RSA_JS5_MODULUS_HEX`, VA `0x100E2356E`).
/// RELOCATED-identical to the Linux 948 / Windows 948-5 value. Consumed by the
/// JS5 master-index signature verifier.
const RS2CLIENT_JS5_MODULUS_PREFIX: &[u8] = b"a6400fbcbd9dd09f48045caf3f543dd6";

/// Full length of the rs2client JS5 RSA modulus hex string
/// (4096-bit = 512 bytes = 1024 hex chars).
const RS2CLIENT_JS5_MODULUS_HEX_LEN: usize = 1024;

/// Byte pattern for the hardcoded HTTP port 80 (0x50) in rs2client's inlined
/// `jag::WorldLobbyData::GetHTTPURL` (macOS 948-5, VA `0x100273834`).
///
/// `66 b8 50 00` = `MOV AX, 0x50` (operand-size prefix `0x66` → 16-bit move,
/// because the port field is a `short`). At the site the bytes are
/// `66 b8 50 00 eb 05` (`MOV AX,0x50` ; `JMP +5`). Unique: exactly 1 match in
/// the whole binary.
///
/// Do NOT add the Linux `41 b8 50 00 00 00 74` or Windows `b8 50 00 00 00 eb`
/// patterns here: on macOS the latter matches two vtable method-offset dispatch
/// sites (false positives) — patching those would crash the client.
const HTTP_PORT_PATTERN: &[u8] = &[0x66, 0xb8, 0x50, 0x00];

/// Offset of the 2-byte LE u16 port immediate within `HTTP_PORT_PATTERN`
/// (the `50 00` following the `66 b8` opcode). Patch length is 2 bytes — the
/// macOS port field is 16-bit, unlike the 4-byte Linux/Windows immediate.
const HTTP_PORT_PATCH_OFFSET: usize = 2;

/// First 32 chars of the macOS launcher wrapper RSA modulus hex string.
const WRAPPER_MODULUS_PREFIX: &[u8] = b"a49962fc0737fddcd94c0daf84e5d214";

/// Full length of the launcher wrapper RSA modulus hex string.
const WRAPPER_MODULUS_HEX_LEN: usize = 1024;

/// The codebase URL validation regex in the macOS launcher wrapper.
const CODEBASE_REGEX: &[u8] = b"^https?://[a-z0-9\\-]*\\.?runescape.com(:[0-9]+)?/";

/// Permissive replacement for the codebase regex.
const CODEBASE_REGEX_REPLACEMENT: &[u8] = b"^https?://.*/";

// ---------------------------------------------------------------------------
// Mach-O / dyld FFI.
//
// The Mach-O header/load-command structs are declared here rather than pulled
// from `libc` (its `mach_header_64`/`segment_command_64` are deprecated → "use
// mach2") or `mach2` (whose `loader` module only ships the 32-bit `mach_header`).
// These layouts are a stable Apple ABI (`<mach-o/loader.h>`); declaring exactly
// the fields we read keeps the build warning-clean and self-contained.
// ---------------------------------------------------------------------------

const LC_SEGMENT_64: u32 = 0x19;

/// `struct mach_header_64` from `<mach-o/loader.h>` (the dyld image header for
/// a 64-bit Mach-O). We only read `ncmds`; the rest is laid out for ABI fidelity
/// so `size_of` is correct for stepping past the header to the load commands.
#[repr(C)]
struct MachHeader64 {
    magic: u32,
    cputype: i32,
    cpusubtype: i32,
    filetype: u32,
    ncmds: u32,
    sizeofcmds: u32,
    flags: u32,
    reserved: u32,
}

/// `struct load_command` — the common prefix of every load command.
#[repr(C)]
struct LoadCommand {
    cmd: u32,
    cmdsize: u32,
}

/// `struct segment_command_64` (`LC_SEGMENT_64`) from `<mach-o/loader.h>`.
#[repr(C)]
struct SegmentCommand64 {
    cmd: u32,
    cmdsize: u32,
    segname: [c_char; 16],
    vmaddr: u64,
    vmsize: u64,
    fileoff: u64,
    filesize: u64,
    maxprot: i32,
    initprot: i32,
    nsects: u32,
    flags: u32,
}

extern "C" {
    fn _dyld_image_count() -> u32;
    fn _dyld_get_image_name(image_index: u32) -> *const c_char;
    fn _dyld_get_image_header(image_index: u32) -> *const MachHeader64;
    fn _dyld_get_image_vmaddr_slide(image_index: u32) -> isize;
    /// Insurance against I-cache staleness after a code patch (P3). On x86_64
    /// the I-cache is coherent so this is generally a no-op, but it is cheap.
    fn sys_icache_invalidate(start: *mut libc::c_void, len: libc::size_t);
}

/// A loaded segment of the rs2client image (runtime-slid bounds + protection).
struct Segment {
    name: String,
    start: usize, // vmaddr + slide
    end: usize,   // vmaddr + slide + vmsize
    initprot: i32,
}

#[derive(Clone, Copy, Debug, Eq, PartialEq)]
enum TargetImage {
    Rs2Client,
    LauncherWrapper,
}

// ---------------------------------------------------------------------------
// Constructor — env-gated exactly like the Linux patcher.
// ---------------------------------------------------------------------------

#[ctor::ctor]
fn patch_rsa() {
    let modulus_hex = env::var("DARKAN_RSA_MODULUS").ok().filter(|v| !v.is_empty());
    let js5_modulus_hex = env::var("DARKAN_JS5_RSA_MODULUS")
        .ok()
        .filter(|v| !v.is_empty());
    let proxy_mode = env::var("DARKAN_PROXY_MODE").map(|v| v == "1").unwrap_or(false);

    // In proxy mode the patcher still runs (P1 login key patch redirects logins
    // to our proxy). In non-proxy mode an RSA modulus is required — otherwise
    // there is nothing to do, and the dylib is harmlessly present (live mode).
    if modulus_hex.is_none() && !proxy_mode {
        return;
    }

    eprintln!(
        "[darkan-patcher-mac] Patcher loaded, applying patches (proxy_mode={}, has_rsa={})...",
        proxy_mode,
        modulus_hex.is_some()
    );

    // Clean + validate the RSA modulus hex (if provided), mirroring Linux.
    let modulus_hex_clean = modulus_hex.map(|hex| {
        let s = hex.trim();
        s.strip_prefix("0x")
            .or_else(|| s.strip_prefix("0X"))
            .unwrap_or(s)
            .to_string()
    });

    if let Some(ref hex) = modulus_hex_clean {
        if hex.len() % 2 != 0 || hex.is_empty() {
            eprintln!("[darkan-patcher-mac] ERROR: DARKAN_RSA_MODULUS is not valid hex (odd length or empty)");
            if !proxy_mode {
                return;
            }
        } else if hex_to_bytes(hex).is_none() {
            eprintln!("[darkan-patcher-mac] ERROR: DARKAN_RSA_MODULUS is not valid hex");
            if !proxy_mode {
                return;
            }
        }
    }

    // Locate the target main image and its ASLR slide, then bound __TEXT.
    let (target, segments, image_name) = match find_target_segments() {
        Some(v) => v,
        None => {
            eprintln!(
                "[darkan-patcher-mac] target image not found among loaded dyld images; nothing to patch"
            );
            return;
        }
    };

    let text = match segments.iter().find(|s| s.name == "__TEXT") {
        Some(t) => t,
        None => {
            eprintln!("[darkan-patcher-mac] ERROR: image '{image_name}' has no __TEXT segment");
            return;
        }
    };

    eprintln!(
        "[darkan-patcher-mac] Found {:?} image '{}' — __TEXT runtime range 0x{:x}-0x{:x} ({} bytes)",
        target,
        image_name,
        text.start,
        text.end,
        text.end - text.start
    );

    if target == TargetImage::LauncherWrapper {
        patch_launcher_wrapper(text, js5_modulus_hex.as_deref(), proxy_mode);
        eprintln!("[darkan-patcher-mac] Done.");
        return;
    }

    // --- Patch P1: login RSA modulus (256-char hex ASCII in __TEXT,__cstring) ---
    if let Some(ref modulus_hex_clean) = modulus_hex_clean {
        match pad_modulus_hex(modulus_hex_clean, RS2CLIENT_MODULUS_HEX_LEN) {
            Some(replacement_str) => {
                let replacement_bytes = replacement_str.as_bytes();
                debug_assert_eq!(replacement_bytes.len(), RS2CLIENT_MODULUS_HEX_LEN);

                let finder = Finder::new(RS2CLIENT_MODULUS_PREFIX);
                match find_first_in_segment(text, &finder) {
                    Some(addr) => {
                        eprintln!(
                            "[darkan-patcher-mac] Found rs2client login RSA modulus hex string at address 0x{:x}",
                            addr
                        );
                        if patch_memory(addr, replacement_bytes, text.initprot) {
                            eprintln!(
                                "[darkan-patcher-mac] Successfully patched login RSA modulus ({} hex chars)",
                                RS2CLIENT_MODULUS_HEX_LEN
                            );
                        } else {
                            eprintln!(
                                "[darkan-patcher-mac] ERROR: Failed to patch login RSA modulus at 0x{:x}",
                                addr
                            );
                        }
                    }
                    None => eprintln!(
                        "[darkan-patcher-mac] login RSA modulus pattern not found in __TEXT (unexpected for rs2client 948-5)"
                    ),
                }
            }
            None => eprintln!(
                "[darkan-patcher-mac] WARNING: DARKAN_RSA_MODULUS hex is {} chars, exceeds rs2client max of {}",
                modulus_hex_clean.len(),
                RS2CLIENT_MODULUS_HEX_LEN
            ),
        }
    } else {
        eprintln!("[darkan-patcher-mac] No RSA modulus provided, skipping login RSA patch");
    }

    // --- Patch P2: JS5 RSA modulus (1024-char hex ASCII). Skipped in proxy mode. ---
    if proxy_mode {
        eprintln!("[darkan-patcher-mac] Proxy mode: skipping JS5 RSA modulus patch (Jagex key needed for direct JS5)");
    } else if let Some(ref js5_hex) = js5_modulus_hex {
        match pad_modulus_hex(js5_hex, RS2CLIENT_JS5_MODULUS_HEX_LEN) {
            Some(padded) if hex_to_bytes(&padded).is_some() => {
                let replacement_bytes = padded.as_bytes();
                let finder = Finder::new(RS2CLIENT_JS5_MODULUS_PREFIX);
                match find_first_in_segment(text, &finder) {
                    Some(addr) => {
                        eprintln!(
                            "[darkan-patcher-mac] Found rs2client JS5 RSA modulus hex string at address 0x{:x}",
                            addr
                        );
                        if patch_memory(addr, replacement_bytes, text.initprot) {
                            eprintln!(
                                "[darkan-patcher-mac] Successfully patched JS5 RSA modulus ({} hex chars)",
                                RS2CLIENT_JS5_MODULUS_HEX_LEN
                            );
                        } else {
                            eprintln!(
                                "[darkan-patcher-mac] ERROR: Failed to patch JS5 RSA modulus at 0x{:x}",
                                addr
                            );
                        }
                    }
                    None => eprintln!(
                        "[darkan-patcher-mac] JS5 RSA modulus pattern not found in __TEXT (unexpected for rs2client 948-5)"
                    ),
                }
            }
            _ => eprintln!("[darkan-patcher-mac] WARNING: DARKAN_JS5_RSA_MODULUS is invalid or too long"),
        }
    } else {
        eprintln!("[darkan-patcher-mac] No JS5 RSA modulus provided, skipping JS5 RSA patch");
    }

    // --- Patch P3: HTTP port 80 → DARKAN_HTTP_PORT. Skipped in proxy mode. ---
    if proxy_mode {
        eprintln!("[darkan-patcher-mac] Proxy mode: skipping HTTP port patch (HTTP JS5 goes directly to Jagex on port 80)");
    } else if let Ok(port_str) = env::var("DARKAN_HTTP_PORT") {
        match port_str.trim().parse::<u16>() {
            Ok(port) => {
                let port_le = port.to_le_bytes(); // 2-byte LE u16 — 16-bit field on macOS
                let finder = Finder::new(HTTP_PORT_PATTERN);
                // Unique on macOS 948-5 (1 match), but scan all to stay correct
                // across minor rebuilds — mirrors the Linux patch-every-match policy.
                let matches = find_all_in_segment(text, &finder);
                if matches.is_empty() {
                    eprintln!(
                        "[darkan-patcher-mac] WARNING: HTTP port pattern (66 b8 50 00) matched 0 sites — \
                         client HTTP requests will still hit port 80!"
                    );
                } else {
                    eprintln!(
                        "[darkan-patcher-mac] HTTP port pattern matched {} site(s)",
                        matches.len()
                    );
                    let mut patched_count = 0usize;
                    for offset in &matches {
                        let patch_addr = offset + HTTP_PORT_PATCH_OFFSET;
                        if patch_memory(patch_addr, &port_le, text.initprot) {
                            eprintln!(
                                "[darkan-patcher-mac] Patched HTTP port at 0x{:x} (80 -> {})",
                                offset, port
                            );
                            patched_count += 1;
                        } else {
                            eprintln!(
                                "[darkan-patcher-mac] ERROR: Failed to patch HTTP port at 0x{:x}",
                                offset
                            );
                        }
                    }
                    eprintln!(
                        "[darkan-patcher-mac] HTTP port: patched {}/{} site(s)",
                        patched_count,
                        matches.len()
                    );
                }
            }
            Err(_) => eprintln!(
                "[darkan-patcher-mac] WARNING: DARKAN_HTTP_PORT='{}' is not a valid u16 port; skipping HTTP port patch",
                port_str
            ),
        }
    }

    eprintln!("[darkan-patcher-mac] Done.");
}

fn patch_launcher_wrapper(text: &Segment, js5_modulus_hex: Option<&str>, proxy_mode: bool) {
    if proxy_mode {
        eprintln!("[darkan-patcher-mac] Proxy mode: skipping launcher wrapper patches");
        return;
    }

    if let Some(js5_hex) = js5_modulus_hex {
        match pad_modulus_hex(js5_hex, WRAPPER_MODULUS_HEX_LEN) {
            Some(padded) if hex_to_bytes(&padded).is_some() => {
                let finder = Finder::new(WRAPPER_MODULUS_PREFIX);
                match find_first_in_segment(text, &finder) {
                    Some(addr) => {
                        if patch_memory(addr, padded.as_bytes(), text.initprot) {
                            eprintln!(
                                "[darkan-patcher-mac] Successfully patched launcher wrapper RSA modulus ({} hex chars)",
                                WRAPPER_MODULUS_HEX_LEN
                            );
                        } else {
                            eprintln!(
                                "[darkan-patcher-mac] ERROR: Failed to patch launcher wrapper RSA modulus at 0x{:x}",
                                addr
                            );
                        }
                    }
                    None => eprintln!("[darkan-patcher-mac] launcher wrapper RSA modulus pattern not found"),
                }
            }
            _ => eprintln!("[darkan-patcher-mac] WARNING: DARKAN_JS5_RSA_MODULUS is invalid or too long for launcher wrapper"),
        }
    } else {
        eprintln!("[darkan-patcher-mac] No JS5 RSA modulus provided, skipping launcher wrapper RSA patch");
    }

    let finder = Finder::new(CODEBASE_REGEX);
    match find_first_in_segment(text, &finder) {
        Some(addr) => {
            let mut replacement = Vec::with_capacity(CODEBASE_REGEX.len());
            replacement.extend_from_slice(CODEBASE_REGEX_REPLACEMENT);
            replacement.resize(CODEBASE_REGEX.len(), 0);

            if patch_memory(addr, &replacement, text.initprot) {
                eprintln!("[darkan-patcher-mac] Successfully patched launcher wrapper codebase regex");
            } else {
                eprintln!(
                    "[darkan-patcher-mac] ERROR: Failed to patch launcher wrapper codebase regex at 0x{:x}",
                    addr
                );
            }
        }
        None => eprintln!("[darkan-patcher-mac] launcher wrapper codebase regex pattern not found"),
    }
}

// ---------------------------------------------------------------------------
// dyld image enumeration + LC_SEGMENT_64 walk.
// ---------------------------------------------------------------------------

fn find_target_segments() -> Option<(TargetImage, Vec<Segment>, String)> {
    unsafe {
        let count = _dyld_image_count();
        for i in 0..count {
            let name_ptr = _dyld_get_image_name(i);
            if name_ptr.is_null() {
                continue;
            }
            let name = CStr::from_ptr(name_ptr).to_string_lossy();
            let target = if name.contains("rs2client") {
                TargetImage::Rs2Client
            } else if name.ends_with("/RuneScape.app/Contents/MacOS/RuneScape")
                || name.ends_with("/RuneScape")
            {
                TargetImage::LauncherWrapper
            } else {
                continue;
            };
            let hdr = _dyld_get_image_header(i);
            if hdr.is_null() {
                continue;
            }
            let slide = _dyld_get_image_vmaddr_slide(i);
            let segments = parse_segments(hdr, slide);
            if segments.is_empty() {
                continue;
            }
            return Some((target, segments, name.into_owned()));
        }
    }
    None
}

/// Walk the load commands of a Mach-O header, recording each `LC_SEGMENT_64`'s
/// runtime-slid `[vmaddr+slide, vmaddr+slide+vmsize)` range, name, and initprot.
///
/// # Safety
/// `hdr` must point at a valid, mapped `mach_header_64` followed by `ncmds`
/// well-formed load commands (guaranteed by dyld for a loaded image).
fn parse_segments(hdr: *const MachHeader64, slide: isize) -> Vec<Segment> {
    let mut out = Vec::new();
    unsafe {
        let ncmds = (*hdr).ncmds;
        // Load commands begin immediately after the 64-bit Mach-O header.
        let mut lc =
            (hdr as *const u8).add(std::mem::size_of::<MachHeader64>()) as *const LoadCommand;
        for _ in 0..ncmds {
            let cmd = (*lc).cmd;
            let cmdsize = (*lc).cmdsize as usize;
            if cmdsize == 0 {
                break; // malformed; stop walking
            }
            if cmd == LC_SEGMENT_64 {
                let sg = lc as *const SegmentCommand64;
                let name = cstr_field_to_string(&(*sg).segname);
                let start = ((*sg).vmaddr as isize).wrapping_add(slide) as usize;
                let end = start.wrapping_add((*sg).vmsize as usize);
                out.push(Segment {
                    name,
                    start,
                    end,
                    initprot: (*sg).initprot,
                });
            }
            lc = (lc as *const u8).add(cmdsize) as *const LoadCommand;
        }
    }
    out
}

/// Convert a fixed-size, possibly-non-NUL-terminated C char array (the Mach-O
/// `segname` 16-byte field) into a Rust `String`.
fn cstr_field_to_string(field: &[c_char]) -> String {
    let bytes: Vec<u8> = field
        .iter()
        .take_while(|&&c| c != 0)
        .map(|&c| c as u8)
        .collect();
    String::from_utf8_lossy(&bytes).into_owned()
}

// ---------------------------------------------------------------------------
// Pattern search within a segment.
// ---------------------------------------------------------------------------

/// Scan `segment`'s runtime byte range with `finder`, returning the runtime
/// address of the first match (or `None`).
fn find_first_in_segment(segment: &Segment, finder: &Finder) -> Option<usize> {
    let len = segment.end.checked_sub(segment.start)?;
    if len < finder.needle().len() {
        return None;
    }
    let slice = unsafe { std::slice::from_raw_parts(segment.start as *const u8, len) };
    finder.find(slice).map(|i| segment.start + i)
}

/// Scan `segment`'s runtime byte range with `finder`, returning the runtime
/// address of every match.
fn find_all_in_segment(segment: &Segment, finder: &Finder) -> Vec<usize> {
    let mut out = Vec::new();
    let len = match segment.end.checked_sub(segment.start) {
        Some(l) if l >= finder.needle().len() => l,
        _ => return out,
    };
    let slice = unsafe { std::slice::from_raw_parts(segment.start as *const u8, len) };
    for i in finder.find_iter(slice) {
        out.push(segment.start + i);
    }
    out
}

// ---------------------------------------------------------------------------
// Memory patching via mach_vm_protect (VM_PROT_COPY breaks COW on r-x __TEXT).
// ---------------------------------------------------------------------------

/// Patch memory at `addr` with `new_bytes`.
///
/// `__TEXT` is r-x and copy-on-write shared, so we cannot simply add WRITE. We
/// first `mach_vm_protect(..., VM_PROT_READ | VM_PROT_WRITE | VM_PROT_COPY)` to
/// make a private writable copy (breaking COW), write, then restore the
/// original `__TEXT` protection (`VM_PROT_READ | VM_PROT_EXECUTE`). The binary
/// is unsigned/adhoc-signed, so the COW break alone is sufficient.
///
/// `original_prot` is the segment's `initprot` (Mach `vm_prot_t` flags), used to
/// restore the exact original protection after writing.
fn patch_memory(addr: usize, new_bytes: &[u8], original_prot: i32) -> bool {
    let page_size = page_size();
    let page_start = addr & !(page_size - 1);
    let page_end = (addr + new_bytes.len() + page_size - 1) & !(page_size - 1);
    let total_len = (page_end - page_start) as u64;
    let task = unsafe { mach_task_self() };

    unsafe {
        // 1) Make writable + break COW (VM_PROT_COPY is the critical flag — without
        //    it the kernel refuses to make a shared, executable, file-backed page
        //    writable and returns KERN_PROTECTION_FAILURE).
        let kr = mach_vm_protect(
            task,
            page_start as u64,
            total_len,
            0, // set_maximum = FALSE
            VM_PROT_READ | VM_PROT_WRITE | VM_PROT_COPY,
        );
        if kr != KERN_SUCCESS {
            eprintln!(
                "[darkan-patcher-mac] mach_vm_protect(+W,COPY) failed for 0x{:x}..0x{:x}: kr={}",
                page_start,
                page_start + total_len as usize,
                kr
            );
            return false;
        }

        // 2) Write the patch bytes at the (slid) runtime address.
        ptr::copy_nonoverlapping(new_bytes.as_ptr(), addr as *mut u8, new_bytes.len());

        // 3) Restore the original __TEXT protection. If initprot looks bogus
        //    (0 or write-only), fall back to r-x — the documented __TEXT prot.
        let restore_prot = if original_prot & (VM_PROT_READ | VM_PROT_EXECUTE) != 0 {
            original_prot
        } else {
            VM_PROT_READ | VM_PROT_EXECUTE
        };
        let kr = mach_vm_protect(task, page_start as u64, total_len, 0, restore_prot);
        if kr != KERN_SUCCESS {
            eprintln!(
                "[darkan-patcher-mac] WARNING: mach_vm_protect(restore) failed for 0x{:x}: kr={}",
                page_start, kr
            );
            // The write already landed; a failed restore is non-fatal.
        }

        // 4) I-cache flush insurance for code patches (P3). No-op on coherent
        //    x86_64 I-caches, but cheap.
        sys_icache_invalidate(addr as *mut libc::c_void, new_bytes.len());
    }

    true
}

/// Runtime page size (typically 4096 on x86_64 macOS, incl. under Rosetta).
fn page_size() -> usize {
    let sz = unsafe { libc::sysconf(libc::_SC_PAGESIZE) };
    if sz > 0 {
        sz as usize
    } else {
        4096
    }
}

// ---------------------------------------------------------------------------
// Hex / padding helpers — identical to the Linux patcher.
// ---------------------------------------------------------------------------

/// Clean a hex modulus string (strip `0x`/`0X`, trim, lowercase) and left-pad
/// with `'0'` to exactly `len` chars. Returns `None` if the cleaned hex is
/// longer than `len`.
fn pad_modulus_hex(hex: &str, len: usize) -> Option<String> {
    let s = hex.trim();
    let s = s
        .strip_prefix("0x")
        .or_else(|| s.strip_prefix("0X"))
        .unwrap_or(s);
    let hex_lower = s.to_ascii_lowercase();
    if hex_lower.len() > len {
        return None;
    }
    let mut padded = "0".repeat(len - hex_lower.len());
    padded.push_str(&hex_lower);
    Some(padded)
}

/// Convert a hex string to bytes (validation helper). Strips `0x`/`0X`/whitespace.
fn hex_to_bytes(hex: &str) -> Option<Vec<u8>> {
    let hex = hex.trim();
    let hex = hex
        .strip_prefix("0x")
        .or_else(|| hex.strip_prefix("0X"))
        .unwrap_or(hex);

    if hex.len() % 2 != 0 {
        return None;
    }

    let mut bytes = Vec::with_capacity(hex.len() / 2);
    for i in (0..hex.len()).step_by(2) {
        let byte = u8::from_str_radix(&hex[i..i + 2], 16).ok()?;
        bytes.push(byte);
    }
    Some(bytes)
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_hex_to_bytes() {
        assert_eq!(hex_to_bytes("deadbeef"), Some(vec![0xde, 0xad, 0xbe, 0xef]));
        assert_eq!(hex_to_bytes("0xDEADBEEF"), Some(vec![0xde, 0xad, 0xbe, 0xef]));
        assert_eq!(hex_to_bytes(""), Some(vec![]));
        assert_eq!(hex_to_bytes("abc"), None);
        assert_eq!(hex_to_bytes("zz"), None);
    }

    #[test]
    fn test_rs2client_modulus_constants() {
        assert_eq!(RS2CLIENT_MODULUS_PREFIX.len(), 32);
        assert_eq!(RS2CLIENT_MODULUS_HEX_LEN, 256);
        assert_eq!(RS2CLIENT_JS5_MODULUS_PREFIX.len(), 32);
        assert_eq!(RS2CLIENT_JS5_MODULUS_HEX_LEN, 1024);
        assert_eq!(WRAPPER_MODULUS_PREFIX.len(), 32);
        assert_eq!(WRAPPER_MODULUS_HEX_LEN, 1024);
    }

    #[test]
    fn test_http_port_pattern() {
        // macOS uses the 16-bit MOV AX,0x50 form, patched at +2 for 2 bytes.
        assert_eq!(HTTP_PORT_PATTERN, &[0x66, 0xb8, 0x50, 0x00]);
        assert_eq!(HTTP_PORT_PATCH_OFFSET, 2);
        // The 2-byte LE encoding of a sample port lands correctly.
        let port: u16 = 8829;
        assert_eq!(port.to_le_bytes(), [0x7d, 0x22]);
    }

    #[test]
    fn test_pad_modulus_hex() {
        // Strips 0x prefix, lowercases, left-pads to length.
        let p = pad_modulus_hex("0xDEADBEEF", 16).unwrap();
        assert_eq!(p, "00000000deadbeef");
        assert_eq!(p.len(), 16);
        // Exact-length input is returned unchanged (lowercased).
        assert_eq!(pad_modulus_hex("ABCD", 4).unwrap(), "abcd");
        // Over-length input returns None.
        assert!(pad_modulus_hex("deadbeef", 4).is_none());
    }

    #[test]
    fn test_pad_modulus_hex_to_login_len() {
        // A short hex string left-pads to the 256-char login modulus length.
        let p = pad_modulus_hex("deadbeef", RS2CLIENT_MODULUS_HEX_LEN).unwrap();
        assert_eq!(p.len(), RS2CLIENT_MODULUS_HEX_LEN);
        assert!(p.starts_with("0000"));
        assert!(p.ends_with("deadbeef"));
    }

    #[test]
    fn test_pad_modulus_hex_to_js5_len() {
        // A short hex string left-pads to the 1024-char JS5 modulus length.
        let p = pad_modulus_hex("abcd", RS2CLIENT_JS5_MODULUS_HEX_LEN).unwrap();
        assert_eq!(p.len(), RS2CLIENT_JS5_MODULUS_HEX_LEN);
        assert!(p.ends_with("abcd"));
    }

    #[test]
    fn test_cstr_field_to_string() {
        // NUL-terminated segname field within a 16-byte buffer.
        let mut field = [0 as c_char; 16];
        for (i, b) in b"__TEXT".iter().enumerate() {
            field[i] = *b as c_char;
        }
        assert_eq!(cstr_field_to_string(&field), "__TEXT");
        // Full-width field (no trailing NUL) is read up to the array bound.
        let full = [b'A' as c_char; 16];
        assert_eq!(cstr_field_to_string(&full), "A".repeat(16));
    }
}
