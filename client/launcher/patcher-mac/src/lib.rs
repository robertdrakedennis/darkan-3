//! Darkan RSA patcher — macOS dylib (DYLD_INSERT_LIBRARIES preload target)
//!
//! Mirrors the Linux LD_PRELOAD patcher at `client/launcher/patcher/src/lib.rs`,
//! adapted for the Mach-O x86-64 build of rs2client (rev 948-5).
//!
//! Loaded via `DYLD_INSERT_LIBRARIES=<this.dylib>` (set by the launcher's
//! `build_macos_command`), the constructor below runs before the host's
//! `main()`. It locates the main executable's `__TEXT` image range, scans it
//! for known Jagex patterns, and overwrites them in place to redirect the
//! client to the private server.
//!
//! Patches applied (when `DARKAN_RSA_MODULUS` is set):
//!
//! 1. **rs2client login RSA modulus** — 256-char lowercase hex ASCII string in
//!    `__TEXT,__const`. The ASCII pattern is IDENTICAL to the Linux/Windows
//!    builds (Jagex ships the same key across OS builds); only the in-memory
//!    address differs. Verified: prefix `aad4a780...` appears exactly once in
//!    `data/client/macos/rs2client`. Replaced with `DARKAN_RSA_MODULUS`,
//!    left-padded to 256 chars with `'0'`.
//!
//! 2. **rs2client JS5 RSA modulus** — 1024-char lowercase hex ASCII string.
//!    Pattern identical to other OS builds (prefix `a6400fbc...`, 1 match).
//!    Replaced with `DARKAN_JS5_RSA_MODULUS`.
//!
//! 3. **rs2client HTTP port** — hardcoded port 80 (0x50) inside
//!    `jag::WorldLobbyData::GetHTTPURL`. On macOS, clang emits
//!    `MOV EAX, 0x50` + `JMP short` (`b8 50 00 00 00 eb`, same form as the
//!    Windows MSVC build) — NOT the Linux `MOV R8D, 0x50` + `JZ` form. A raw
//!    scan finds TWO `b8 50 00 00 00 eb` sites in the Mach-O, so a unique
//!    pattern + patch offset must be confirmed by the ghidra-reverse-engineer
//!    agent before this patch can be enabled. Until then it is GATED: the patch
//!    runs ONLY if the configured pattern resolves to exactly ONE match (see
//!    `HTTP_PORT_PATTERN` / `HTTP_PORT_RESOLVED`). When the RE doc
//!    (`docs/binary/patch-targets-macos.md`) lands, update those two consts.
//!
//! Patches that do NOT apply to the Mach-O rs2client (verified absent in the
//! binary — they are rs3linux-launcher-only): codebase URL regex, LZMA flag.
//! Those live in the Linux patcher and are skipped here entirely.
//!
//! If `DARKAN_RSA_MODULUS` is not set, the patcher does nothing — so the same
//! dylib can be harmlessly inserted in live (un-patched) mode.

#![cfg(target_os = "macos")]

use memchr::memmem::Finder;
use std::env;
use std::ffi::CStr;
use std::os::raw::{c_char, c_void};
use std::ptr;

// -- Patch constants (kept in lockstep with the Linux + Windows patchers) -----

/// First 32 chars of the rs2client login RSA modulus hex string (1024-bit key).
/// Identical to the Linux 948-2 prefix; only the in-memory address changed.
/// Byte-verified to appear exactly once in `data/client/macos/rs2client`.
const RS2CLIENT_MODULUS_PREFIX: &[u8] = b"aad4a7804c34bb788d52dbd5f70e5721";

/// Full length of the rs2client login RSA modulus hex string (256 chars).
const RS2CLIENT_MODULUS_HEX_LEN: usize = 256;

/// First 32 chars of the rs2client JS5 RSA modulus hex string (4096-bit key).
/// Identical to the Linux 948-2 prefix; only the in-memory address changed.
/// Byte-verified to appear exactly once in `data/client/macos/rs2client`.
const RS2CLIENT_JS5_MODULUS_PREFIX: &[u8] = b"a6400fbcbd9dd09f48045caf3f543dd6";

/// Full length of the rs2client JS5 RSA modulus hex string (1024 chars).
const RS2CLIENT_JS5_MODULUS_HEX_LEN: usize = 1024;

/// Whether the HTTP-port patch site has been UNIQUELY confirmed by the
/// ghidra-reverse-engineer agent for the Mach-O build. While `false`, the
/// HTTP-port patch is applied only if `HTTP_PORT_PATTERN` happens to resolve to
/// exactly one match (the safe condition); a multi-match pattern is refused so
/// we never patch the wrong (graphics/matrix) `MOV EAX, 0x50` site.
///
/// Flip to `true` and tighten `HTTP_PORT_PATTERN` once
/// `docs/binary/patch-targets-macos.md` documents the unique signature.
const HTTP_PORT_RESOLVED: bool = false;

/// Candidate byte pattern for the hardcoded HTTP port 80 (0x50) in the macOS
/// `jag::WorldLobbyData::GetHTTPURL` (clang `MOV EAX, 0x50` + `JMP short`).
///
/// NOTE: this 6-byte pattern matches TWO sites in the 948-5 Mach-O (file
/// offsets 0x8e9168 and 0x8f3605); it is NOT yet unique. The patch driver
/// refuses to apply it while it matches more than one site (see
/// `HTTP_PORT_RESOLVED`). Extend this pattern with surrounding context bytes
/// from the RE doc to make it unique, then set `HTTP_PORT_RESOLVED = true`.
const HTTP_PORT_PATTERN: &[u8] = &[0xb8, 0x50, 0x00, 0x00, 0x00, 0xeb];

/// Offset within `HTTP_PORT_PATTERN` of the 4-byte LE port immediate.
/// macOS/clang: +1 (no REX prefix), same as the Windows MSVC build.
const HTTP_PORT_PATCH_OFFSET: usize = 1;

const PAGE_SIZE: usize = 4096;

// -- dyld + Mach-O FFI --------------------------------------------------------
//
// We locate the main executable's mapped image via the dyld inspection API
// rather than parsing /proc (which does not exist on macOS). `_dyld_*` walks
// the list of loaded images; image index 0 is always the main executable.

extern "C" {
    fn _dyld_image_count() -> u32;
    fn _dyld_get_image_header(image_index: u32) -> *const MachHeader64;
    fn _dyld_get_image_name(image_index: u32) -> *const c_char;
    fn _dyld_get_image_vmaddr_slide(image_index: u32) -> isize;
}

const MH_MAGIC_64: u32 = 0xfeed_facf;
const LC_SEGMENT_64: u32 = 0x19;

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

#[repr(C)]
struct LoadCommand {
    cmd: u32,
    cmdsize: u32,
}

#[repr(C)]
struct SegmentCommand64 {
    cmd: u32,
    cmdsize: u32,
    segname: [u8; 16],
    vmaddr: u64,
    vmsize: u64,
    fileoff: u64,
    filesize: u64,
    maxprot: i32,
    initprot: i32,
    nsects: u32,
    flags: u32,
}

/// A mapped, readable segment of the main executable image.
struct Region {
    start: usize,
    end: usize,
}

// -- Constructor (runs before host main, like the Linux ctor patcher) ---------

#[ctor::ctor]
fn patch_rsa() {
    let modulus_hex = env::var("DARKAN_RSA_MODULUS").ok().filter(|v| !v.is_empty());
    let js5_modulus_hex = env::var("DARKAN_JS5_RSA_MODULUS")
        .ok()
        .filter(|v| !v.is_empty());
    let proxy_mode = env::var("DARKAN_PROXY_MODE").map(|v| v == "1").unwrap_or(false);

    // Same semantics as the Linux patcher: presence without configuration is a
    // no-op so the dylib is safe to leave inserted in live mode. (No rs3linux
    // launcher patches exist on macOS, so we only act when a modulus is set.)
    if modulus_hex.is_none() {
        return;
    }

    eprintln!(
        "[darkan-patcher-mac] Patcher loaded, applying patches (proxy_mode={}, has_rsa={})...",
        proxy_mode,
        modulus_hex.is_some()
    );

    let modulus_hex_clean = modulus_hex.map(|hex| {
        let s = hex.trim();
        s.strip_prefix("0x")
            .or_else(|| s.strip_prefix("0X"))
            .unwrap_or(s)
            .to_string()
    });

    if let Some(ref hex) = modulus_hex_clean {
        if hex.is_empty() || hex.len() % 2 != 0 || hex_to_bytes(hex).is_none() {
            eprintln!("[darkan-patcher-mac] ERROR: DARKAN_RSA_MODULUS is not valid hex");
            return;
        }
    }

    let regions = main_image_regions();
    if regions.is_empty() {
        eprintln!("[darkan-patcher-mac] ERROR: could not resolve main executable image regions — aborting");
        return;
    }
    for (i, r) in regions.iter().enumerate() {
        eprintln!(
            "[darkan-patcher-mac]   image region[{}]: 0x{:x}-0x{:x} ({} bytes)",
            i,
            r.start,
            r.end,
            r.end - r.start
        );
    }

    // --- Patch 1: login RSA modulus (256-char hex ASCII) ---
    if let Some(ref modulus_hex_clean) = modulus_hex_clean {
        match pad_modulus_hex(modulus_hex_clean, RS2CLIENT_MODULUS_HEX_LEN) {
            Some(replacement) => {
                let finder = Finder::new(RS2CLIENT_MODULUS_PREFIX);
                match find_first(&regions, &finder) {
                    Some(addr) => {
                        eprintln!(
                            "[darkan-patcher-mac] Found login RSA modulus hex string at 0x{:x}",
                            addr
                        );
                        if patch_memory(addr, replacement.as_bytes()) {
                            eprintln!("[darkan-patcher-mac] Successfully patched login RSA modulus ({} hex chars)", RS2CLIENT_MODULUS_HEX_LEN);
                        } else {
                            eprintln!("[darkan-patcher-mac] ERROR: failed to patch login RSA modulus at 0x{:x}", addr);
                        }
                    }
                    None => eprintln!("[darkan-patcher-mac] login RSA modulus pattern not found"),
                }
            }
            None => eprintln!(
                "[darkan-patcher-mac] WARNING: DARKAN_RSA_MODULUS is {} chars, exceeds max of {}",
                modulus_hex_clean.len(),
                RS2CLIENT_MODULUS_HEX_LEN
            ),
        }
    }

    // --- Patch 2: JS5 RSA modulus (1024-char hex ASCII) ---
    // Skipped in proxy mode: JS5 goes directly to Jagex, so their key must remain.
    if proxy_mode {
        eprintln!("[darkan-patcher-mac] Proxy mode: skipping JS5 RSA modulus patch");
    } else if let Some(ref js5_hex) = js5_modulus_hex {
        match pad_modulus_hex(js5_hex, RS2CLIENT_JS5_MODULUS_HEX_LEN) {
            Some(replacement) if hex_to_bytes(&replacement).is_some() => {
                let finder = Finder::new(RS2CLIENT_JS5_MODULUS_PREFIX);
                match find_first(&regions, &finder) {
                    Some(addr) => {
                        eprintln!(
                            "[darkan-patcher-mac] Found JS5 RSA modulus hex string at 0x{:x}",
                            addr
                        );
                        if patch_memory(addr, replacement.as_bytes()) {
                            eprintln!("[darkan-patcher-mac] Successfully patched JS5 RSA modulus ({} hex chars)", RS2CLIENT_JS5_MODULUS_HEX_LEN);
                        } else {
                            eprintln!("[darkan-patcher-mac] ERROR: failed to patch JS5 RSA modulus at 0x{:x}", addr);
                        }
                    }
                    None => eprintln!("[darkan-patcher-mac] JS5 RSA modulus pattern not found"),
                }
            }
            _ => eprintln!("[darkan-patcher-mac] WARNING: DARKAN_JS5_RSA_MODULUS is invalid or too long"),
        }
    } else {
        eprintln!("[darkan-patcher-mac] DARKAN_JS5_RSA_MODULUS not set — skipping JS5 patch");
    }

    // --- Patch 3: HTTP port (GATED on a unique match — see HTTP_PORT_RESOLVED) ---
    // Skipped in proxy mode: HTTP JS5 goes directly to Jagex on port 80.
    if proxy_mode {
        eprintln!("[darkan-patcher-mac] Proxy mode: skipping HTTP port patch");
    } else if let Ok(port_str) = env::var("DARKAN_HTTP_PORT") {
        if let Ok(port) = port_str.parse::<u16>() {
            let port_le = port.to_le_bytes();
            let replacement = [port_le[0], port_le[1], 0x00, 0x00];

            let finder = Finder::new(HTTP_PORT_PATTERN);
            let matches = find_all(&regions, &finder);
            match matches.len() {
                0 => eprintln!(
                    "[darkan-patcher-mac] WARNING: HTTP port pattern matched 0 sites — \
                     client HTTP JS5 requests will still hit port 80!"
                ),
                1 => {
                    let addr = matches[0] + HTTP_PORT_PATCH_OFFSET;
                    if patch_memory(addr, &replacement) {
                        eprintln!(
                            "[darkan-patcher-mac] Patched HTTP content port at 0x{:x} (80 -> {})",
                            matches[0], port
                        );
                    } else {
                        eprintln!("[darkan-patcher-mac] ERROR: failed to patch HTTP port at 0x{:x}", matches[0]);
                    }
                }
                n if HTTP_PORT_RESOLVED => {
                    // A confirmed-unique pattern that still multi-matches means
                    // the binary drifted; refuse rather than guess.
                    eprintln!(
                        "[darkan-patcher-mac] WARNING: HTTP port pattern marked resolved but matched {} sites — refusing to patch (binary drift?).",
                        n
                    );
                }
                n => eprintln!(
                    "[darkan-patcher-mac] HTTP port pattern matched {} sites and is NOT yet \
                     disambiguated by RE (see docs/binary/patch-targets-macos.md) — \
                     refusing to patch to avoid hitting the wrong MOV EAX,0x50 site.",
                    n
                ),
            }
        } else {
            eprintln!("[darkan-patcher-mac] WARNING: DARKAN_HTTP_PORT is not a valid u16 — skipping HTTP port patch");
        }
    }

    eprintln!("[darkan-patcher-mac] Patcher pass complete.");
}

// -- Main image discovery -----------------------------------------------------

/// Resolve the readable, mapped segment ranges of the MAIN executable image
/// (dyld image index 0). We include all `LC_SEGMENT_64` segments with non-zero
/// vmsize and readable initprot — the RSA hex strings live in `__TEXT,__const`
/// and the port literal in `__TEXT,__text`, both inside `__TEXT`.
fn main_image_regions() -> Vec<Region> {
    let mut regions = Vec::new();
    let count = unsafe { _dyld_image_count() };
    if count == 0 {
        return regions;
    }

    // Index 0 is always the main executable. Defensive: also confirm the header
    // is a 64-bit Mach-O. (We do not match on the image NAME because a Mach-O
    // app bundle may expose the binary under a path that does not literally
    // contain "rs2client".)
    let header = unsafe { _dyld_get_image_header(0) };
    if header.is_null() {
        return regions;
    }
    let slide = unsafe { _dyld_get_image_vmaddr_slide(0) };
    let name = unsafe {
        let np = _dyld_get_image_name(0);
        if np.is_null() {
            "<unknown>".to_string()
        } else {
            CStr::from_ptr(np).to_string_lossy().to_string()
        }
    };

    let hdr = unsafe { &*header };
    if hdr.magic != MH_MAGIC_64 {
        eprintln!(
            "[darkan-patcher-mac] main image '{}' is not MH_MAGIC_64 (magic=0x{:x}) — aborting region scan",
            name, hdr.magic
        );
        return regions;
    }
    eprintln!(
        "[darkan-patcher-mac] main image '{}' header @ {:p}, slide=0x{:x}, ncmds={}",
        name, header, slide, hdr.ncmds
    );

    // Walk the load commands immediately following the header.
    let mut cmd_ptr = unsafe { (header as *const u8).add(std::mem::size_of::<MachHeader64>()) };
    for _ in 0..hdr.ncmds {
        let lc = unsafe { &*(cmd_ptr as *const LoadCommand) };
        if lc.cmdsize == 0 {
            break; // malformed; stop walking
        }
        if lc.cmd == LC_SEGMENT_64 {
            let seg = unsafe { &*(cmd_ptr as *const SegmentCommand64) };
            // initprot bit 0 = VM_PROT_READ. Only scan readable, non-empty segs.
            let readable = (seg.initprot & 0x1) != 0;
            if readable && seg.vmsize > 0 {
                let start = (seg.vmaddr as isize + slide) as usize;
                let end = start.wrapping_add(seg.vmsize as usize);
                if end > start {
                    regions.push(Region { start, end });
                }
            }
        }
        cmd_ptr = unsafe { cmd_ptr.add(lc.cmdsize as usize) };
    }

    regions
}

// -- Scan helpers (mirror the Linux patcher's API) ----------------------------

fn find_first_in_region(region: &Region, finder: &Finder) -> Option<usize> {
    let len = region.end - region.start;
    if len < finder.needle().len() {
        return None;
    }
    let slice = unsafe { std::slice::from_raw_parts(region.start as *const u8, len) };
    finder.find(slice).map(|i| region.start + i)
}

fn find_first(regions: &[Region], finder: &Finder) -> Option<usize> {
    regions
        .iter()
        .find_map(|region| find_first_in_region(region, finder))
}

fn find_all(regions: &[Region], finder: &Finder) -> Vec<usize> {
    let needle_len = finder.needle().len();
    let mut out = Vec::new();
    for region in regions {
        let len = region.end - region.start;
        if len < needle_len {
            continue;
        }
        let slice = unsafe { std::slice::from_raw_parts(region.start as *const u8, len) };
        for i in finder.find_iter(slice) {
            out.push(region.start + i);
        }
    }
    out
}

// -- Patch primitive ----------------------------------------------------------

/// Patch memory at `addr` with `new_bytes`, making the containing pages writable
/// via `mprotect` (present on macOS) and restoring R+X afterward.
///
/// macOS hardened pages: `__TEXT` is mapped r-x. We add WRITE for the write,
/// then restore READ|EXEC. (`__const` is r--; restoring READ|EXEC there is
/// harmless — it stays readable. We don't have the original prot from dyld
/// cheaply, so we restore the common safe case.)
fn patch_memory(addr: usize, new_bytes: &[u8]) -> bool {
    let page_start = addr & !(PAGE_SIZE - 1);
    let page_end_addr = addr + new_bytes.len();
    let page_end = (page_end_addr + PAGE_SIZE - 1) & !(PAGE_SIZE - 1);
    let total_len = page_end - page_start;

    unsafe {
        let ret = libc::mprotect(
            page_start as *mut c_void,
            total_len,
            libc::PROT_READ | libc::PROT_WRITE | libc::PROT_EXEC,
        );
        if ret != 0 {
            eprintln!(
                "[darkan-patcher-mac] mprotect(+WRITE) failed for 0x{:x}..0x{:x}: errno={}",
                page_start,
                page_start + total_len,
                *libc::__error()
            );
            return false;
        }

        ptr::copy_nonoverlapping(new_bytes.as_ptr(), addr as *mut u8, new_bytes.len());

        let ret = libc::mprotect(
            page_start as *mut c_void,
            total_len,
            libc::PROT_READ | libc::PROT_EXEC,
        );
        if ret != 0 {
            eprintln!(
                "[darkan-patcher-mac] WARNING: mprotect(restore) failed for 0x{:x}: errno={}",
                page_start,
                *libc::__error()
            );
        }
    }

    true
}

// -- hex helpers (shared shape with the Linux/Windows patchers) ---------------

/// Clean a hex modulus string (strip `0x`/`0X`, trim, lowercase) and left-pad
/// with `'0'` to exactly `len` chars. Returns `None` if longer than `len`.
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
        bytes.push(u8::from_str_radix(&hex[i..i + 2], 16).ok()?);
    }
    Some(bytes)
}

// -- Tests --------------------------------------------------------------------

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
    fn test_constants_match_other_os_builds() {
        // The shared ASCII patterns must be byte-identical to the Linux patcher,
        // since the Jagex keys are the same across OS builds.
        assert_eq!(RS2CLIENT_MODULUS_PREFIX.len(), 32);
        assert_eq!(RS2CLIENT_MODULUS_HEX_LEN, 256);
        assert_eq!(RS2CLIENT_JS5_MODULUS_PREFIX.len(), 32);
        assert_eq!(RS2CLIENT_JS5_MODULUS_HEX_LEN, 1024);
        // HTTP port pattern (clang MOV EAX,0x50 + JMP short) and its immediate offset.
        assert_eq!(HTTP_PORT_PATTERN, &[0xb8, 0x50, 0x00, 0x00, 0x00, 0xeb]);
        assert_eq!(HTTP_PORT_PATCH_OFFSET, 1);
    }

    #[test]
    fn test_pad_modulus_hex() {
        let p = pad_modulus_hex("0xDEADBEEF", 16).unwrap();
        assert_eq!(p, "00000000deadbeef");
        assert_eq!(p.len(), 16);
        assert_eq!(pad_modulus_hex("ABCD", 4).unwrap(), "abcd");
        assert!(pad_modulus_hex("deadbeef", 4).is_none());
    }
}
