//! Darkan RSA patcher — LD_PRELOAD shared library
//!
//! When loaded via LD_PRELOAD before the NXT client starts, this library
//! scans the process's memory for known Jagex patterns and replaces them
//! with custom values to redirect the client to the private server.
//!
//! Patches applied (when `DARKAN_RSA_MODULUS` is set):
//!
//! 1. **rs2client RSA modulus** — 128-byte binary pattern in .rodata/.data,
//!    replaced with the custom 1024-bit modulus from the env var.
//!
//! 2. **rs3linux RSA modulus** — 1024-char lowercase hex ASCII string in
//!    .rodata used by the launcher for download signature verification.
//!    The env var value is left-padded with '0' to 1024 chars.
//!
//! 3. **rs3linux codebase URL regex** — The regex that validates the
//!    `codebase` URL is replaced with a permissive pattern so that
//!    custom config server URLs pass validation.
//!
//! If `DARKAN_RSA_MODULUS` is not set, the patcher does nothing — this
//! allows the same LD_PRELOAD to be harmlessly present in live mode.

use std::env;
use std::fs;
use std::ptr;

/// The Jagex RSA public modulus (1024-bit, big-endian bytes) used by rs2client.
///
/// Decimal value:
/// 117525752735533423040644219776209926525585489242340044375332234679786347045466594509203355398209678968096551043842518449703703964361320462967286756268851663407950384008240524570966471744081769815157355561961607944067477858512067883877129283799853947605780903005188603658779539811385137666347647991072028080201
const JAGEX_MODULUS: [u8; 128] = [
    0xa7, 0x5c, 0xba, 0xed, 0x30, 0xed, 0xdd, 0x55, 0x6f, 0xaa, 0x17, 0x7b, 0x5a, 0x11, 0x1d, 0xfa,
    0x41, 0xc5, 0x95, 0xd2, 0xb1, 0x66, 0x71, 0xd8, 0x23, 0xf1, 0x88, 0x34, 0xd5, 0xa0, 0x4b, 0xf2,
    0xb1, 0xf2, 0x18, 0xfb, 0x0f, 0xf9, 0x30, 0x5a, 0x9d, 0x0c, 0xb7, 0xfc, 0xc2, 0x69, 0x9c, 0x62,
    0x50, 0x72, 0xdc, 0x9e, 0xfa, 0x88, 0x76, 0x49, 0x94, 0x53, 0x8f, 0x20, 0x99, 0xd4, 0x65, 0x14,
    0x04, 0x18, 0xcc, 0x4d, 0x53, 0x06, 0xce, 0x61, 0x8e, 0x1a, 0x2b, 0x2e, 0xe5, 0xfe, 0xf7, 0x25,
    0xef, 0x83, 0xea, 0xe6, 0x4c, 0x30, 0x31, 0xb2, 0xd8, 0x36, 0xb9, 0x99, 0xa8, 0xa3, 0xce, 0x03,
    0x78, 0xa7, 0xd6, 0x3d, 0xbb, 0x06, 0xad, 0x63, 0x5c, 0x29, 0xd2, 0x24, 0x1a, 0x10, 0xf1, 0xcf,
    0x72, 0x5a, 0x0a, 0xbf, 0x82, 0xbd, 0x48, 0xbb, 0x8c, 0xe4, 0xab, 0x56, 0x43, 0xa7, 0xb8, 0x49,
];

/// First 32 chars of the rs3linux launcher RSA modulus hex string (4096-bit key).
/// Used as a unique search pattern to locate the full 1024-char hex string in .rodata.
const RS3LINUX_MODULUS_PREFIX: &[u8] = b"a49962fc0737fddcd94c0daf84e5d214";

/// Full length of the rs3linux RSA modulus hex string (4096-bit = 512 bytes = 1024 hex chars).
const RS3LINUX_MODULUS_HEX_LEN: usize = 1024;

/// The codebase URL validation regex in rs3linux .rodata.
const CODEBASE_REGEX: &[u8] = b"^https?://[a-z0-9\\-]*\\.?runescape.com(:[0-9]+)?/";

/// Permissive replacement for the codebase regex — matches any http(s) URL.
const CODEBASE_REGEX_REPLACEMENT: &[u8] = b"^https?://.*/";

/// Byte pattern for the LZMA decompression flag initialization in rs3linux.
///
/// In FUN_003e9490, the download task init sets three consecutive flags:
///   003e965a: C6 80 81 01 00 00 00    MOV byte ptr [RAX + 0x181], 0x0  ; written flag = 0
///   003e9661: C6 80 82 01 00 00 01    MOV byte ptr [RAX + 0x182], 0x1  ; LZMA decompress = ENABLED
///   003e9668: C6 80 83 01 00 00 01    MOV byte ptr [RAX + 0x183], 0x1  ; post-write check = 1
///
/// We patch the middle instruction's immediate from 0x01 to 0x00 to disable LZMA decompression,
/// so rs3linux saves the downloaded binary as-is (uncompressed).
const LZMA_FLAG_PATTERN: [u8; 21] = [
    0xC6, 0x80, 0x81, 0x01, 0x00, 0x00, 0x00, // MOV byte ptr [RAX+0x181], 0x0
    0xC6, 0x80, 0x82, 0x01, 0x00, 0x00, 0x01, // MOV byte ptr [RAX+0x182], 0x1  <-- patch this 0x01
    0xC6, 0x80, 0x83, 0x01, 0x00, 0x00, 0x01, // MOV byte ptr [RAX+0x183], 0x1
];

/// Offset within LZMA_FLAG_PATTERN of the immediate byte to change (0x01 → 0x00).
const LZMA_FLAG_PATCH_OFFSET: usize = 13; // The 0x01 at the end of the second MOV

const PAGE_SIZE: usize = 4096;

/// A memory region with its original permission flags.
struct Region {
    start: usize,
    end: usize,
    prot: i32, // original mprotect flags
}

#[ctor::ctor]
fn patch_rsa() {
    let modulus_hex = match env::var("DARKAN_RSA_MODULUS") {
        Ok(val) if !val.is_empty() => val,
        _ => return,
    };

    eprintln!("[darkan-patcher] RSA patcher loaded, applying patches...");

    // Detect which binary we're running in
    let exe_name = fs::read_link("/proc/self/exe")
        .ok()
        .and_then(|p| p.file_name().map(|n| n.to_string_lossy().to_string()));
    let is_rs2client = exe_name.as_deref().map_or(false, |n| n.contains("rs2client"));
    let is_rs3linux = exe_name.as_deref().map_or(false, |n| n.contains("rs3linux"));

    eprintln!("[darkan-patcher] Detected binary: {:?} (rs2client={}, rs3linux={})",
        exe_name.as_deref().unwrap_or("unknown"), is_rs2client, is_rs3linux);

    // Strip optional 0x prefix and validate hex
    let modulus_hex_clean = {
        let s = modulus_hex.trim();
        s.strip_prefix("0x")
            .or_else(|| s.strip_prefix("0X"))
            .unwrap_or(s)
            .to_string()
    };

    if modulus_hex_clean.len() % 2 != 0 || modulus_hex_clean.is_empty() {
        eprintln!("[darkan-patcher] ERROR: DARKAN_RSA_MODULUS is not valid hex (odd length or empty)");
        return;
    }

    let new_modulus = match hex_to_bytes(&modulus_hex_clean) {
        Some(bytes) => bytes,
        None => {
            eprintln!("[darkan-patcher] ERROR: DARKAN_RSA_MODULUS is not valid hex");
            return;
        }
    };

    if new_modulus.len() != 128 {
        eprintln!(
            "[darkan-patcher] WARNING: DARKAN_RSA_MODULUS is {} bytes ({}-bit), rs2client patch expects 128 bytes (1024-bit)",
            new_modulus.len(),
            new_modulus.len() * 8,
        );
    }

    let maps = match fs::read_to_string("/proc/self/maps") {
        Ok(m) => m,
        Err(e) => {
            eprintln!("[darkan-patcher] ERROR: Failed to read /proc/self/maps: {}", e);
            return;
        }
    };

    // Only scan regions belonging to the current binary — scanning all regions
    // can SIGBUS on memory-mapped files that aren't fully backed
    let binary_name = if is_rs3linux { "rs3linux" } else { "rs2client" };
    let mut regions = parse_maps_for_binary(&maps, binary_name);
    if !regions.is_empty() {
        eprintln!("[darkan-patcher] Found {} {} regions to scan", regions.len(), binary_name);
    } else {
        // Fallback: try the other binary name
        let alt = if is_rs3linux { "rs2client" } else { "rs3linux" };
        regions = parse_maps_for_binary(&maps, alt);
        if !regions.is_empty() {
            eprintln!("[darkan-patcher] Found {} {} regions to scan (fallback)", regions.len(), alt);
        }
    }

    // --- Patch 1: rs2client binary RSA modulus (128-byte binary pattern) ---
    if new_modulus.len() == 128 {
        let mut patched = false;
        for region in &regions {
            if let Some(offset) = scan_for_pattern(region.start, region.end, &JAGEX_MODULUS) {
                eprintln!(
                    "[darkan-patcher] Found rs2client RSA modulus at address 0x{:x}",
                    offset
                );

                if patch_memory(offset, &new_modulus, region.prot) {
                    eprintln!("[darkan-patcher] Successfully patched rs2client RSA modulus");
                    patched = true;
                    break;
                } else {
                    eprintln!("[darkan-patcher] ERROR: Failed to patch rs2client RSA modulus at 0x{:x}", offset);
                }
            }
        }

        if !patched {
            eprintln!("[darkan-patcher] rs2client RSA modulus pattern not found (not rs2client process?)");
        }
    } else {
        eprintln!("[darkan-patcher] Skipping rs2client RSA patch (modulus is not 1024-bit)");
    }

    // --- Patches 2-4 are rs3linux-only; skip for rs2client ---
    if is_rs2client {
        eprintln!("[darkan-patcher] rs2client detected, skipping rs3linux-specific patches");
        return;
    }

    // --- Patch 2: rs3linux launcher RSA modulus (1024-char hex ASCII string) ---
    {
        // Build the replacement: left-pad the hex modulus with '0' to 1024 chars, lowercase
        let padded_hex = {
            let hex_lower = modulus_hex_clean.to_ascii_lowercase();
            if hex_lower.len() > RS3LINUX_MODULUS_HEX_LEN {
                eprintln!(
                    "[darkan-patcher] WARNING: DARKAN_RSA_MODULUS hex string is {} chars, exceeds rs3linux max of {}",
                    hex_lower.len(),
                    RS3LINUX_MODULUS_HEX_LEN
                );
                None
            } else {
                let padding = RS3LINUX_MODULUS_HEX_LEN - hex_lower.len();
                let mut s = "0".repeat(padding);
                s.push_str(&hex_lower);
                Some(s)
            }
        };

        if let Some(replacement_str) = padded_hex {
            let replacement_bytes = replacement_str.as_bytes();
            debug_assert_eq!(replacement_bytes.len(), RS3LINUX_MODULUS_HEX_LEN);

            let mut patched = false;
            for region in &regions {
                if let Some(offset) = scan_for_pattern(region.start, region.end, RS3LINUX_MODULUS_PREFIX) {
                    eprintln!(
                        "[darkan-patcher] Found rs3linux RSA modulus hex string at address 0x{:x}",
                        offset
                    );

                    if patch_memory(offset, replacement_bytes, region.prot) {
                        eprintln!("[darkan-patcher] Successfully patched rs3linux RSA modulus ({} hex chars)", RS3LINUX_MODULUS_HEX_LEN);
                        patched = true;
                        break;
                    } else {
                        eprintln!("[darkan-patcher] ERROR: Failed to patch rs3linux RSA modulus at 0x{:x}", offset);
                    }
                }
            }

            if !patched {
                eprintln!("[darkan-patcher] rs3linux RSA modulus pattern not found (not rs3linux process?)");
            }
        }
    }

    // --- Patch 3: rs3linux codebase URL validation regex ---
    {
        let mut patched = false;
        for region in &regions {
            if let Some(offset) = scan_for_pattern(region.start, region.end, CODEBASE_REGEX) {
                eprintln!(
                    "[darkan-patcher] Found codebase URL regex at address 0x{:x}",
                    offset
                );

                // Build replacement: permissive regex + null padding to fill original length
                let original_len = CODEBASE_REGEX.len();
                let mut replacement = Vec::with_capacity(original_len);
                replacement.extend_from_slice(CODEBASE_REGEX_REPLACEMENT);
                // Null-pad the remainder so we don't leave stale bytes
                replacement.resize(original_len, 0u8);

                if patch_memory(offset, &replacement, region.prot) {
                    eprintln!("[darkan-patcher] Successfully patched codebase URL regex");
                    patched = true;
                    break;
                } else {
                    eprintln!("[darkan-patcher] ERROR: Failed to patch codebase URL regex at 0x{:x}", offset);
                }
            }
        }

        if !patched {
            eprintln!("[darkan-patcher] Codebase URL regex pattern not found (not rs3linux process?)");
        }
    }

    // --- Patch 4: rs3linux LZMA decompression flag (disable for uncompressed binary serving) ---
    {
        let mut patched = false;
        for region in &regions {
            if let Some(offset) = scan_for_pattern(region.start, region.end, &LZMA_FLAG_PATTERN) {
                let patch_addr = offset + LZMA_FLAG_PATCH_OFFSET;
                eprintln!(
                    "[darkan-patcher] Found LZMA flag init at address 0x{:x} (patching byte at 0x{:x})",
                    offset,
                    patch_addr,
                );

                // Change the immediate byte from 0x01 to 0x00
                if patch_memory(patch_addr, &[0x00], region.prot) {
                    eprintln!("[darkan-patcher] Successfully patched LZMA decompression flag (disabled)");
                    patched = true;
                    break;
                } else {
                    eprintln!("[darkan-patcher] ERROR: Failed to patch LZMA flag at 0x{:x}", patch_addr);
                }
            }
        }

        if !patched {
            eprintln!("[darkan-patcher] LZMA flag pattern not found (not rs3linux process?)");
        }
    }
}

/// Parse /proc/self/maps for regions belonging to a specific binary name.
fn parse_maps_for_binary(maps: &str, binary_name: &str) -> Vec<Region> {
    let mut regions = Vec::new();
    for line in maps.lines() {
        if !line.contains(binary_name) {
            continue;
        }
        if let Some(region) = parse_map_line(line) {
            regions.push(region);
        }
    }
    regions
}

/// Parse all readable memory regions (fallback).
fn parse_all_readable_maps(maps: &str) -> Vec<Region> {
    let mut regions = Vec::new();
    for line in maps.lines() {
        if line.contains("[vdso]") || line.contains("[vsyscall]") || line.contains("[stack]") {
            continue;
        }
        if let Some(region) = parse_map_line(line) {
            regions.push(region);
        }
    }
    regions
}

/// Parse a single line from /proc/self/maps.
/// Returns Some(Region) if the region is readable.
fn parse_map_line(line: &str) -> Option<Region> {
    let mut parts = line.split_whitespace();
    let addr_range = parts.next()?;
    let perms = parts.next()?;

    if !perms.starts_with('r') {
        return None;
    }

    let mut addr_parts = addr_range.split('-');
    let start = usize::from_str_radix(addr_parts.next()?, 16).ok()?;
    let end = usize::from_str_radix(addr_parts.next()?, 16).ok()?;

    if end <= start || (end - start) < RS3LINUX_MODULUS_PREFIX.len() {
        return None;
    }

    // Parse permission string (e.g. "r-xp", "r--p", "rw-p") into mprotect flags
    let perm_bytes = perms.as_bytes();
    let mut prot = 0i32;
    if perm_bytes.len() >= 3 {
        if perm_bytes[0] == b'r' { prot |= libc::PROT_READ; }
        if perm_bytes[1] == b'w' { prot |= libc::PROT_WRITE; }
        if perm_bytes[2] == b'x' { prot |= libc::PROT_EXEC; }
    }

    Some(Region { start, end, prot })
}

/// Scan a memory region for a byte pattern. Returns the address of the first match.
fn scan_for_pattern(start: usize, end: usize, pattern: &[u8]) -> Option<usize> {
    if end - start < pattern.len() {
        return None;
    }

    let region_len = end - start;
    let slice = unsafe { std::slice::from_raw_parts(start as *const u8, region_len) };

    for i in 0..=(region_len - pattern.len()) {
        if &slice[i..i + pattern.len()] == pattern {
            return Some(start + i);
        }
    }

    None
}

/// Patch memory at the given address with new bytes.
/// Makes pages writable, writes the bytes, then restores original permissions.
fn patch_memory(addr: usize, new_bytes: &[u8], original_prot: i32) -> bool {
    let page_start = addr & !(PAGE_SIZE - 1);
    let page_end_addr = addr + new_bytes.len();
    let page_end = (page_end_addr + PAGE_SIZE - 1) & !(PAGE_SIZE - 1);
    let total_len = page_end - page_start;

    unsafe {
        // Make pages writable (add WRITE to whatever was there)
        let ret = libc::mprotect(
            page_start as *mut libc::c_void,
            total_len,
            original_prot | libc::PROT_WRITE,
        );
        if ret != 0 {
            eprintln!(
                "[darkan-patcher] mprotect(+WRITE) failed for 0x{:x}..0x{:x}: errno={}",
                page_start,
                page_start + total_len,
                *libc::__errno_location()
            );
            return false;
        }

        // Write the new bytes
        ptr::copy_nonoverlapping(new_bytes.as_ptr(), addr as *mut u8, new_bytes.len());

        // Restore original permissions exactly
        let ret = libc::mprotect(
            page_start as *mut libc::c_void,
            total_len,
            original_prot,
        );
        if ret != 0 {
            eprintln!(
                "[darkan-patcher] WARNING: mprotect(restore) failed for 0x{:x}: errno={}",
                page_start,
                *libc::__errno_location()
            );
        }
    }

    true
}

/// Convert a hex string to bytes.
fn hex_to_bytes(hex: &str) -> Option<Vec<u8>> {
    let hex = hex.trim();
    let hex = hex.strip_prefix("0x").or_else(|| hex.strip_prefix("0X")).unwrap_or(hex);

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
    fn test_jagex_modulus_length() {
        assert_eq!(JAGEX_MODULUS.len(), 128);
    }

    #[test]
    fn test_rs3linux_constants() {
        assert_eq!(RS3LINUX_MODULUS_PREFIX.len(), 32);
        assert_eq!(RS3LINUX_MODULUS_HEX_LEN, 1024);
        // The codebase regex replacement must be shorter than the original
        assert!(CODEBASE_REGEX_REPLACEMENT.len() < CODEBASE_REGEX.len());
    }

    #[test]
    fn test_zero_pad_modulus() {
        // A short hex string should be left-padded to 1024 chars
        let short_hex = "deadbeef";
        let padding = RS3LINUX_MODULUS_HEX_LEN - short_hex.len();
        let mut padded = "0".repeat(padding);
        padded.push_str(short_hex);
        assert_eq!(padded.len(), RS3LINUX_MODULUS_HEX_LEN);
        assert!(padded.starts_with("0000"));
        assert!(padded.ends_with("deadbeef"));
    }

    #[test]
    fn test_parse_map_line() {
        let line = "7f1234000000-7f1234001000 r--p 00000000 08:01 12345 /path/to/rs3linux";
        let result = parse_map_line(line);
        assert!(result.is_some());
        let region = result.unwrap();
        assert_eq!(region.start, 0x7f1234000000);
        assert_eq!(region.end, 0x7f1234001000);
        assert_eq!(region.prot, libc::PROT_READ);

        let line = "7f1234000000-7f1234001000 r-xp 00000000 08:01 12345 /path/to/rs3linux";
        let region = parse_map_line(line).unwrap();
        assert_eq!(region.prot, libc::PROT_READ | libc::PROT_EXEC);

        let line = "7f1234000000-7f1234001000 --xp 00000000 08:01 12345 /path/to/rs3linux";
        assert!(parse_map_line(line).is_none());
    }
}
