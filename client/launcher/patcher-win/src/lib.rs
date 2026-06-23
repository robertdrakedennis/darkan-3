//! Darkan RSA patcher — Windows DLL (cross-process LoadLibrary injection target)
//!
//! Mirrors the Linux LD_PRELOAD patcher at `client/launcher/patcher/src/lib.rs`,
//! adapted for the Windows PE32+ build of rs2client (rev 948-5).
//!
//! Patches applied on DLL_PROCESS_ATTACH (when `DARKAN_RSA_MODULUS` is set):
//!
//! 1. **rs2client login RSA modulus** — 256-char lowercase hex ASCII string in
//!    `.rdata` (VA 0x140bb7800 in 948-5). Replaced with the modulus from
//!    `DARKAN_RSA_MODULUS`, left-padded to 256 chars with `'0'`.
//!
//! 2. **rs2client JS5 RSA modulus** — 1024-char lowercase hex ASCII string in
//!    `.rdata` (VA 0x140bb73f0 in 948-5). Replaced with `DARKAN_JS5_RSA_MODULUS`.
//!
//! 3. **rs2client HTTP port** — Hardcoded port 80 (0x50) inside
//!    `jag::WorldLobbyData::GetHTTPURL` (VA 0x14015eec2 in 948-5). Replaced
//!    with `DARKAN_HTTP_PORT`.
//!
//! **What is NOT patched here** (and why):
//! - **rs3linux launcher patches (modulus, codebase regex, LZMA flag)** — those
//!   targets only exist in the Linux launcher binary. `rs3windows.exe` was not
//!   present in the 948-5 archive (see `docs/binary/patch-targets-948-5.md`,
//!   Patch Target P5 / Open Items #1). Add them here once the RE agent
//!   documents the Windows launcher's patch sites.
//! - **ISAAC delta** — MSVC inlined the `+50` step as scattered `ADD reg,0x32`
//!   instructions; there is no central data site to rewrite. The server-side
//!   contract is unchanged; no patcher action is required.
//!
//! If `DARKAN_RSA_MODULUS` is not set, the patcher does nothing — this lets the
//! DLL be harmlessly present in live (un-patched) mode.

#![cfg(windows)]

use memchr::memmem::Finder;
use std::ffi::c_void;
use std::ptr;
use std::slice;

use windows_sys::Win32::Foundation::{BOOL, FALSE, HMODULE, TRUE};
use windows_sys::Win32::System::Diagnostics::Debug::OutputDebugStringW;
use windows_sys::Win32::System::LibraryLoader::GetModuleHandleW;
use windows_sys::Win32::System::Memory::{
    VirtualProtect, PAGE_EXECUTE_READ, PAGE_EXECUTE_READWRITE, PAGE_PROTECTION_FLAGS,
    PAGE_READONLY, PAGE_READWRITE,
};
use windows_sys::Win32::System::ProcessStatus::{GetModuleInformation, MODULEINFO};
use windows_sys::Win32::System::SystemServices::{
    DLL_PROCESS_ATTACH, DLL_PROCESS_DETACH, DLL_THREAD_ATTACH, DLL_THREAD_DETACH,
};
use windows_sys::Win32::System::Threading::GetCurrentProcess;

// -- Patch constants (kept in lockstep with Linux patcher) --------------------

/// First 32 chars of the rs2client login RSA modulus hex string (1024-bit key).
/// Identical to the Linux 948-2 prefix; only the in-memory address changed.
/// See docs/binary/patch-targets-948-5.md, Patch Target P1.
const RS2CLIENT_MODULUS_PREFIX: &[u8] = b"aad4a7804c34bb788d52dbd5f70e5721";

/// Full length of the rs2client login RSA modulus hex string (256 chars).
const RS2CLIENT_MODULUS_HEX_LEN: usize = 256;

/// First 32 chars of the rs2client JS5 RSA modulus hex string (4096-bit key).
/// Identical to the Linux 948-2 prefix; only the in-memory address changed.
/// See docs/binary/patch-targets-948-5.md, Patch Target P2.
const RS2CLIENT_JS5_MODULUS_PREFIX: &[u8] = b"a6400fbcbd9dd09f48045caf3f543dd6";

/// Full length of the rs2client JS5 RSA modulus hex string (1024 chars).
const RS2CLIENT_JS5_MODULUS_HEX_LEN: usize = 1024;

/// Byte pattern for the hardcoded HTTP port 80 (0x50) in MSVC-emitted
/// `jag::WorldLobbyData::GetHTTPURL` (VA 0x14015eec2 in 948-5).
///
/// MSVC emits `MOV EAX, 0x50` (no REX prefix) followed by `JMP short`,
/// versus gcc/clang's `MOV R8D, 0x50` + `JZ short` on Linux. The control
/// flow shape is structurally the same — different register, different
/// branch — but the search pattern and patch offset both shift.
///
/// See docs/binary/patch-targets-948-5.md, Patch Target P3 (1 unique match
/// in the entire 948-5 binary).
const HTTP_PORT_PATTERN: &[u8] = &[0xb8, 0x50, 0x00, 0x00, 0x00, 0xeb];

/// Offset within `HTTP_PORT_PATTERN` of the 4-byte LE port immediate.
/// Windows: +1 (no REX prefix). Linux was +2 (after `0x41` REX prefix).
const HTTP_PORT_PATCH_OFFSET: usize = 1;

// -- DllMain ------------------------------------------------------------------

/// DLL entry point. Called by the loader on PROCESS_ATTACH (right after the
/// injected `LoadLibraryW` resolves the DLL) and PROCESS_DETACH. We do all
/// patching synchronously inside PROCESS_ATTACH so it completes before the
/// main thread is resumed by the injector.
#[no_mangle]
#[allow(non_snake_case, unused_variables)]
pub extern "system" fn DllMain(
    _dll_module: HMODULE,
    call_reason: u32,
    _reserved: *mut c_void,
) -> BOOL {
    match call_reason {
        DLL_PROCESS_ATTACH => {
            // Swallow any panic — DllMain MUST NOT propagate Rust unwinds across
            // the FFI boundary. The patcher logs errors via OutputDebugStringW
            // and returns TRUE either way: failing the DLL load would crash the
            // host process, which is worse than silently leaving the binary
            // un-patched (the user can then run in live mode anyway).
            let _ = std::panic::catch_unwind(|| apply_patches());
            TRUE
        }
        DLL_PROCESS_DETACH | DLL_THREAD_ATTACH | DLL_THREAD_DETACH => TRUE,
        _ => TRUE,
    }
}

// -- Patch driver -------------------------------------------------------------

fn apply_patches() {
    let modulus_hex = read_env("DARKAN_RSA_MODULUS");
    let js5_modulus_hex = read_env("DARKAN_JS5_RSA_MODULUS");
    let http_port = read_env("DARKAN_HTTP_PORT");

    if modulus_hex.is_none() {
        // Same semantics as the Linux patcher: presence without configuration
        // is a no-op so the DLL is safe to leave injected in live mode.
        debug_log("DARKAN_RSA_MODULUS not set — patcher idle (live mode).");
        return;
    }

    debug_log("Patcher loaded, scanning rs2client.exe image for patch targets...");

    let module = match get_main_module_range() {
        Some(m) => m,
        None => {
            debug_log("ERROR: failed to query main module range — aborting.");
            return;
        }
    };

    debug_log(&format!(
        "Main module image: base=0x{:x}, size={} bytes, end=0x{:x}",
        module.base,
        module.size,
        module.base + module.size
    ));

    // SAFETY: the main module image is mapped readable from `base` for `size`
    // bytes for the lifetime of the process. We only borrow this slice for the
    // duration of the pattern search; before any write we re-acquire pointers
    // and call VirtualProtect to make the relevant page writable.
    let image = unsafe { slice::from_raw_parts(module.base as *const u8, module.size) };

    // --- Patch 1: login RSA modulus ------------------------------------------
    if let Some(hex) = modulus_hex.as_deref() {
        match pad_modulus_hex(hex, RS2CLIENT_MODULUS_HEX_LEN) {
            Some(padded) => {
                patch_pattern(
                    image,
                    module.base,
                    "login RSA modulus",
                    RS2CLIENT_MODULUS_PREFIX,
                    padded.as_bytes(),
                    /* offset_within_pattern = */ 0,
                    /* patch_all_matches = */ false,
                );
            }
            None => debug_log(&format!(
                "WARNING: DARKAN_RSA_MODULUS is longer than {} chars — skipping login patch.",
                RS2CLIENT_MODULUS_HEX_LEN
            )),
        }
    }

    // --- Patch 2: JS5 RSA modulus --------------------------------------------
    if let Some(hex) = js5_modulus_hex.as_deref() {
        match pad_modulus_hex(hex, RS2CLIENT_JS5_MODULUS_HEX_LEN) {
            Some(padded) => {
                patch_pattern(
                    image,
                    module.base,
                    "JS5 RSA modulus",
                    RS2CLIENT_JS5_MODULUS_PREFIX,
                    padded.as_bytes(),
                    0,
                    false,
                );
            }
            None => debug_log(&format!(
                "WARNING: DARKAN_JS5_RSA_MODULUS is longer than {} chars — skipping JS5 patch.",
                RS2CLIENT_JS5_MODULUS_HEX_LEN
            )),
        }
    } else {
        debug_log("DARKAN_JS5_RSA_MODULUS not set — skipping JS5 patch.");
    }

    // --- Patch 3: HTTP port (default 80) -------------------------------------
    if let Some(port_str) = http_port.as_deref() {
        match port_str.trim().parse::<u16>() {
            Ok(port) => {
                let port_le = port.to_le_bytes();
                let replacement = [port_le[0], port_le[1], 0x00, 0x00];
                patch_pattern(
                    image,
                    module.base,
                    "HTTP port (0x50 → custom)",
                    HTTP_PORT_PATTERN,
                    &replacement,
                    HTTP_PORT_PATCH_OFFSET,
                    /* patch_all_matches = */ true,
                );
            }
            Err(_) => debug_log(&format!(
                "WARNING: DARKAN_HTTP_PORT='{}' is not a valid u16 — skipping HTTP port patch.",
                port_str
            )),
        }
    } else {
        debug_log("DARKAN_HTTP_PORT not set — skipping HTTP port patch.");
    }

    debug_log("Patcher pass complete.");
}

// -- Pattern scan + patch -----------------------------------------------------

/// Locate `needle` in `image`, then overwrite `replacement` bytes at
/// `match_offset + offset_within_pattern`. When `patch_all_matches` is true
/// (HTTP port case), patches every match; otherwise patches only the first.
fn patch_pattern(
    image: &[u8],
    base: usize,
    label: &str,
    needle: &[u8],
    replacement: &[u8],
    offset_within_pattern: usize,
    patch_all_matches: bool,
) {
    let finder = Finder::new(needle);
    let mut matches: Vec<usize> = finder.find_iter(image).collect();

    if matches.is_empty() {
        debug_log(&format!(
            "WARNING: {} pattern not found — skipping.",
            label
        ));
        return;
    }

    if !patch_all_matches {
        matches.truncate(1);
    }

    debug_log(&format!(
        "{}: pattern matched {} site(s)",
        label,
        matches.len()
    ));

    let mut patched = 0usize;
    for image_offset in matches {
        let va = base + image_offset + offset_within_pattern;
        debug_log(&format!(
            "  {} match at image_offset=0x{:x}, patching VA=0x{:x} ({} bytes)",
            label,
            image_offset,
            va,
            replacement.len()
        ));
        match write_with_unprotect(va, replacement) {
            Ok(()) => patched += 1,
            Err(e) => debug_log(&format!("    ERROR patching VA=0x{:x}: {}", va, e)),
        }
    }
    debug_log(&format!(
        "{}: patched {}/{} site(s)",
        label,
        patched,
        if patch_all_matches { patched } else { 1 }
    ));
}

/// Write `bytes` at virtual address `addr` after temporarily making the
/// containing pages writable via VirtualProtect, then restore the original
/// protection. Returns the OS error message on failure.
fn write_with_unprotect(addr: usize, bytes: &[u8]) -> Result<(), &'static str> {
    let len = bytes.len();
    if len == 0 {
        return Ok(());
    }

    let mut old_protect: PAGE_PROTECTION_FLAGS = 0;
    let unprotect_ok = unsafe {
        VirtualProtect(
            addr as *const c_void,
            len,
            PAGE_READWRITE,
            &mut old_protect as *mut PAGE_PROTECTION_FLAGS,
        )
    };
    if unprotect_ok == FALSE {
        return Err("VirtualProtect(PAGE_READWRITE) failed");
    }

    unsafe {
        ptr::copy_nonoverlapping(bytes.as_ptr(), addr as *mut u8, len);
    }

    // Restore the original protection. If the page was previously
    // PAGE_EXECUTE_READ (typical for .text), we put it back exactly. For
    // .rdata (PAGE_READONLY) we likewise restore the original flag. We do
    // NOT fall back to PAGE_EXECUTE_READWRITE — leaving pages permanently
    // writable would weaken DEP for the rest of the process.
    let restore_flag = if old_protect == 0 { PAGE_READONLY } else { old_protect };
    let mut scratch: PAGE_PROTECTION_FLAGS = 0;
    let restore_ok = unsafe {
        VirtualProtect(
            addr as *const c_void,
            len,
            restore_flag,
            &mut scratch as *mut PAGE_PROTECTION_FLAGS,
        )
    };
    if restore_ok == FALSE {
        // The write itself succeeded; not strictly fatal. Log via the caller.
        // We bias toward leaving the patch in place rather than failing.
        debug_log(&format!(
            "  WARNING: VirtualProtect(restore=0x{:x}) failed at 0x{:x}; patch is applied but protection not restored.",
            restore_flag, addr
        ));
    }

    // Silence unused-import lints when the cfg path is narrow.
    let _ = (
        PAGE_EXECUTE_READ,
        PAGE_EXECUTE_READWRITE,
    );

    Ok(())
}

// -- Module range discovery ---------------------------------------------------

struct ModuleRange {
    base: usize,
    size: usize,
}

/// Resolve the main module's image base and image size.
///
/// `GetModuleHandleW(NULL)` returns the HMODULE of the executable that started
/// the current process (rs2client.exe — into which our DLL was just injected).
/// `GetModuleInformation` then gives us the linear range of the loaded image
/// so we can scan the whole thing as one contiguous slice.
fn get_main_module_range() -> Option<ModuleRange> {
    let h_module = unsafe { GetModuleHandleW(ptr::null()) };
    if h_module.is_null() {
        debug_log("ERROR: GetModuleHandleW(NULL) returned null.");
        return None;
    }

    let mut info: MODULEINFO = unsafe { std::mem::zeroed() };
    let h_process = unsafe { GetCurrentProcess() };
    let info_size = std::mem::size_of::<MODULEINFO>() as u32;

    let ok =
        unsafe { GetModuleInformation(h_process, h_module, &mut info, info_size) };
    if ok == FALSE {
        debug_log("ERROR: GetModuleInformation failed on main module.");
        return None;
    }

    Some(ModuleRange {
        base: info.lpBaseOfDll as usize,
        size: info.SizeOfImage as usize,
    })
}

// -- env helpers --------------------------------------------------------------

fn read_env(name: &str) -> Option<String> {
    std::env::var(name).ok().filter(|s| !s.is_empty())
}

/// Clean a hex modulus string (strip `0x`/`0X`, trim, lowercase) and left-pad
/// with `'0'` to exactly `len` chars. Returns `None` if the cleaned hex is
/// longer than `len` (caller logs the size warning).
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

// -- Logging via OutputDebugStringW + %TEMP%\darkan-patcher.log ---------------
//
// rs2client.exe is a Windows GUI subsystem binary — it has no inherited
// console, so stdout/stderr writes go nowhere visible. We tee every log line
// to two sinks:
//   1. OutputDebugStringW — visible to any attached debugger or DebugView
//      (the standard way to observe DLL-injected code).
//   2. `%TEMP%\darkan-patcher.log` — a file sink so the host can inspect
//      patcher behavior after the fact without DebugView running. Truncated
//      once per process at the first debug_log() call.
//
// Each line is prefixed with the patcher tag and the host PID so multiple
// injected processes don't get confused if they ever share the log file.

static LOG_INIT: std::sync::Once = std::sync::Once::new();

fn log_file_path() -> std::path::PathBuf {
    std::env::temp_dir().join("darkan-patcher.log")
}

fn write_log_file(line: &str) {
    LOG_INIT.call_once(|| {
        let _ = std::fs::OpenOptions::new()
            .write(true)
            .create(true)
            .truncate(true)
            .open(log_file_path());
    });
    use std::io::Write;
    if let Ok(mut f) = std::fs::OpenOptions::new()
        .append(true)
        .create(true)
        .open(log_file_path())
    {
        let _ = f.write_all(line.as_bytes());
    }
}

fn debug_log(msg: &str) {
    let pid = std::process::id();
    let line = format!("[darkan-patcher pid={}] {}\n", pid, msg);
    let wide: Vec<u16> = line.encode_utf16().chain(std::iter::once(0u16)).collect();
    unsafe {
        OutputDebugStringW(wide.as_ptr());
    }
    write_log_file(&line);
}

// -- Tests --------------------------------------------------------------------

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn pad_modulus_strips_prefix_and_lowercases() {
        let p = pad_modulus_hex("0xDEADBEEF", 16).unwrap();
        assert_eq!(p, "00000000deadbeef");
        assert_eq!(p.len(), 16);
    }

    #[test]
    fn pad_modulus_exact_length_unchanged() {
        assert_eq!(pad_modulus_hex("ABCD", 4).unwrap(), "abcd");
    }

    #[test]
    fn pad_modulus_over_length_returns_none() {
        assert!(pad_modulus_hex("deadbeef", 4).is_none());
    }

    #[test]
    fn constants_match_doc() {
        assert_eq!(RS2CLIENT_MODULUS_PREFIX.len(), 32);
        assert_eq!(RS2CLIENT_MODULUS_HEX_LEN, 256);
        assert_eq!(RS2CLIENT_JS5_MODULUS_PREFIX.len(), 32);
        assert_eq!(RS2CLIENT_JS5_MODULUS_HEX_LEN, 1024);
        // HTTP port pattern: MOV EAX, 0x50 + JMP short — 6 bytes total
        assert_eq!(HTTP_PORT_PATTERN, &[0xb8, 0x50, 0x00, 0x00, 0x00, 0xeb]);
        assert_eq!(HTTP_PORT_PATCH_OFFSET, 1);
    }

    /// Encode-utf16 sanity check — the `debug_log` path encodes via the same
    /// primitive, so any drift in standard-library UTF-16 semantics would
    /// silently break our OutputDebugStringW logging.
    #[test]
    fn utf16_encode_includes_all_chars() {
        let s = "hello";
        let wide: Vec<u16> = s.encode_utf16().collect();
        assert_eq!(wide, vec![b'h' as u16, b'e' as u16, b'l' as u16, b'l' as u16, b'o' as u16]);
    }
}
