//! Darkan RSA patcher — LD_PRELOAD shared library
//!
//! When loaded via LD_PRELOAD before the NXT client starts, this library
//! scans the process's memory for known Jagex patterns and replaces them
//! with custom values to redirect the client to the private server.
//!
//! Patches applied (when `DARKAN_RSA_MODULUS` is set):
//!
//! 1. **rs2client login RSA modulus** — 256-char lowercase hex ASCII string
//!    in .rodata, parsed at startup by jag::math::BigInteger via FUN_001706b0.
//!    Replaced with the custom 1024-bit modulus hex from the env var,
//!    left-padded with '0' to 256 chars.
//!
//! 2. **rs2client JS5 RSA modulus** — 1024-char lowercase hex ASCII string
//!    in .rodata used for master index signature verification. Replaced with
//!    the custom 4096-bit modulus hex from `DARKAN_JS5_RSA_MODULUS`.
//!
//! 3. **rs2client HTTP port** — Hardcoded port 80 in GetHTTPURL (two inlined
//!    copies) replaced with the port from `DARKAN_HTTP_PORT`.
//!
//! 4. **rs3linux RSA modulus** — 1024-char lowercase hex ASCII string in
//!    .rodata used by the launcher for download signature verification.
//!    The env var value is left-padded with '0' to 1024 chars.
//!
//! 5. **rs3linux codebase URL regex** — The regex that validates the
//!    `codebase` URL is replaced with a permissive pattern so that
//!    custom config server URLs pass validation.
//!
//! 6. **rs3linux LZMA decompression flag** — Disables LZMA decompression
//!    so rs3linux saves the downloaded binary as-is (uncompressed).
//!
//! If `DARKAN_RSA_MODULUS` is not set, the patcher does nothing — this
//! allows the same LD_PRELOAD to be harmlessly present in live mode.

use std::env;
use std::fs;
use std::ptr;

/// First 32 chars of the rs2client login RSA modulus hex string (1024-bit key).
/// Found via Ghidra in .init_array, loaded into a global BigInteger.
/// Used by jag::LoginManager::CreateLoginRSAPacket via jag::math::BigInteger::ModPow.
/// Updated for rev 947-1 (Jagex rotated login RSA key).
const RS2CLIENT_MODULUS_PREFIX: &[u8] = b"8f389edb4b56fdafc410be11bd0b4dd2";

/// Full length of the rs2client login RSA modulus hex string (1024-bit = 128 bytes = 256 hex chars).
const RS2CLIENT_MODULUS_HEX_LEN: usize = 256;

/// First 32 chars of the rs2client JS5 RSA modulus hex string (4096-bit key).
/// Found via Ghidra in .init_array, loaded into a global BigInteger.
/// Used by jag::Js5MasterIndex::Js5MasterIndex for version table signature verification.
/// Updated for rev 947-1 (Jagex rotated JS5 RSA key).
const RS2CLIENT_JS5_MODULUS_PREFIX: &[u8] = b"87300ccecc0674194a79ac92a9e18f10";

/// Full length of the rs2client JS5 RSA modulus hex string (4096-bit = 512 bytes = 1024 hex chars).
const RS2CLIENT_JS5_MODULUS_HEX_LEN: usize = 1024;

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

/// Byte patterns for the hardcoded HTTP port 80 (0x50) in rs2client's GetHTTPURL.
/// The compiler inlined this function at TWO call sites, so both must be patched.
///
/// In ModeWhere=LIVE, the client does: port = 80 (hardcoded).
/// We patch the immediate to our configHttpPort so HTTP JS5 content requests
/// hit our server instead of port 80.
///
/// Copy 1 (at 0x0024fc60): MOV R8D, 0x50 followed by JZ
const HTTP_PORT_PATTERN_1: &[u8] = &[0x41, 0xb8, 0x50, 0x00, 0x00, 0x00, 0x74];

/// Copy 2 (at 0x00417b70): MOV R8D, 0x50 followed by two-byte JCC (0F xx)
const HTTP_PORT_PATTERN_2: &[u8] = &[0x41, 0xb8, 0x50, 0x00, 0x00, 0x00, 0x0f];

/// Offset of the 4-byte LE port immediate within the patterns above.
const HTTP_PORT_PATCH_OFFSET: usize = 2;

const PAGE_SIZE: usize = 4096;

/// A memory region with its original permission flags.
struct Region {
    start: usize,
    end: usize,
    prot: i32, // original mprotect flags
}

#[ctor::ctor]
fn patch_rsa() {
    let modulus_hex = env::var("DARKAN_RSA_MODULUS").ok().filter(|v| !v.is_empty());
    let js5_modulus_hex = env::var("DARKAN_JS5_RSA_MODULUS").ok().filter(|v| !v.is_empty());
    let proxy_mode = env::var("DARKAN_PROXY_MODE").map(|v| v == "1").unwrap_or(false);

    // In proxy mode, the patcher runs even without an RSA modulus (for codebase regex patch).
    // In non-proxy mode, an RSA modulus is required — otherwise there's nothing to do.
    if modulus_hex.is_none() && !proxy_mode {
        return;
    }

    eprintln!("[darkan-patcher] Patcher loaded, applying patches (proxy_mode={}, has_rsa={})...",
        proxy_mode, modulus_hex.is_some());

    // Detect which binary we're running in
    let exe_name = fs::read_link("/proc/self/exe")
        .ok()
        .and_then(|p| p.file_name().map(|n| n.to_string_lossy().to_string()));
    let is_rs2client = exe_name.as_deref().map_or(false, |n| n.contains("rs2client"));
    let is_rs3linux = exe_name.as_deref().map_or(false, |n| n.contains("rs3linux"));

    eprintln!("[darkan-patcher] Detected binary: {:?} (rs2client={}, rs3linux={})",
        exe_name.as_deref().unwrap_or("unknown"), is_rs2client, is_rs3linux);

    // Clean and validate RSA modulus hex (if provided)
    let modulus_hex_clean = modulus_hex.map(|hex| {
        let s = hex.trim();
        s.strip_prefix("0x")
            .or_else(|| s.strip_prefix("0X"))
            .unwrap_or(s)
            .to_string()
    });

    if let Some(ref hex) = modulus_hex_clean {
        if hex.len() % 2 != 0 || hex.is_empty() {
            eprintln!("[darkan-patcher] ERROR: DARKAN_RSA_MODULUS is not valid hex (odd length or empty)");
            if !proxy_mode { return; }
        } else if hex_to_bytes(hex).is_none() {
            eprintln!("[darkan-patcher] ERROR: DARKAN_RSA_MODULUS is not valid hex");
            if !proxy_mode { return; }
        }
    }

    let maps = match fs::read_to_string("/proc/self/maps") {
        Ok(m) => m,
        Err(e) => {
            eprintln!("[darkan-patcher] ERROR: Failed to read /proc/self/maps: {}", e);
            return;
        }
    };

    // Scan regions belonging to the current binary first, then fall back to all
    // readable file-backed regions if the pattern isn't found (the ctor may run
    // before all segments are mapped under the expected binary name).
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

    // If binary-specific scan fails for any pattern, we'll retry with all file-backed regions
    let all_regions = parse_all_file_backed_regions(&maps);
    eprintln!("[darkan-patcher] All file-backed regions: {} (fallback pool)", all_regions.len());
    for (i, r) in regions.iter().enumerate() {
        eprintln!("[darkan-patcher]   binary region[{}]: 0x{:x}-0x{:x} ({} bytes, prot={})",
            i, r.start, r.end, r.end - r.start, r.prot);
    }

    // --- Patch 1: rs2client binary RSA modulus (256-char hex ASCII string) ---
    //
    // The rs2client binary stores the login RSA modulus as a lowercase hex ASCII
    // string in .rodata (256 chars for 1024-bit key). At startup, FUN_001706b0
    // parses it into a jag::math::BigInteger. We replace the hex string in-place
    // so the BigInteger parser loads our custom modulus instead.
    if let Some(ref modulus_hex_clean) = modulus_hex_clean {
        let padded_hex = {
            let hex_lower = modulus_hex_clean.to_ascii_lowercase();
            if hex_lower.len() > RS2CLIENT_MODULUS_HEX_LEN {
                eprintln!(
                    "[darkan-patcher] WARNING: DARKAN_RSA_MODULUS hex is {} chars, exceeds rs2client max of {}",
                    hex_lower.len(),
                    RS2CLIENT_MODULUS_HEX_LEN,
                );
                None
            } else {
                let padding = RS2CLIENT_MODULUS_HEX_LEN - hex_lower.len();
                let mut s = "0".repeat(padding);
                s.push_str(&hex_lower);
                Some(s)
            }
        };

        if let Some(replacement_str) = padded_hex {
            let replacement_bytes = replacement_str.as_bytes();
            debug_assert_eq!(replacement_bytes.len(), RS2CLIENT_MODULUS_HEX_LEN);

            let mut patched = false;
            // Try binary-specific regions first, then all file-backed regions
            for scan_regions in [&regions, &all_regions] {
                for region in scan_regions.iter() {
                    if let Some(offset) = scan_for_pattern(region.start, region.end, RS2CLIENT_MODULUS_PREFIX) {
                        eprintln!(
                            "[darkan-patcher] Found rs2client RSA modulus hex string at address 0x{:x}",
                            offset,
                        );

                        if patch_memory(offset, replacement_bytes, region.prot) {
                            eprintln!("[darkan-patcher] Successfully patched rs2client RSA modulus ({} hex chars)", RS2CLIENT_MODULUS_HEX_LEN);
                            patched = true;
                            break;
                        } else {
                            eprintln!("[darkan-patcher] ERROR: Failed to patch rs2client RSA modulus at 0x{:x}", offset);
                        }
                    }
                }
                if patched { break; }
            }

            if !patched {
                eprintln!("[darkan-patcher] rs2client RSA modulus pattern not found (not rs2client process?)");
            }
        }
    } else {
        eprintln!("[darkan-patcher] No RSA modulus provided, skipping rs2client RSA patch");
    }

    // --- Patch 1b: rs2client JS5 RSA modulus (1024-char hex ASCII string, 4096-bit key) ---
    //
    // The version table / master index signature is verified using a separate 4096-bit key
    // stored at DAT_016e7330, loaded from a 1024-char hex string in .rodata.
    // Skipped in proxy mode: JS5 goes directly to Jagex, so their key must remain.
    if proxy_mode {
        eprintln!("[darkan-patcher] Proxy mode: skipping JS5 RSA modulus patch");
    } else if let Some(ref js5_hex) = js5_modulus_hex {
        let js5_hex_clean = {
            let s = js5_hex.trim();
            s.strip_prefix("0x")
                .or_else(|| s.strip_prefix("0X"))
                .unwrap_or(s)
                .to_ascii_lowercase()
        };

        if js5_hex_clean.len() <= RS2CLIENT_JS5_MODULUS_HEX_LEN && hex_to_bytes(&js5_hex_clean).is_some() {
            let padding = RS2CLIENT_JS5_MODULUS_HEX_LEN - js5_hex_clean.len();
            let mut padded = "0".repeat(padding);
            padded.push_str(&js5_hex_clean);
            let replacement_bytes = padded.as_bytes();

            let mut patched = false;
            for scan_regions in [&regions, &all_regions] {
                for region in scan_regions.iter() {
                    if let Some(offset) = scan_for_pattern(region.start, region.end, RS2CLIENT_JS5_MODULUS_PREFIX) {
                        eprintln!(
                            "[darkan-patcher] Found rs2client JS5 RSA modulus hex string at address 0x{:x}",
                            offset,
                        );

                        if patch_memory(offset, replacement_bytes, region.prot) {
                            eprintln!("[darkan-patcher] Successfully patched rs2client JS5 RSA modulus ({} hex chars)", RS2CLIENT_JS5_MODULUS_HEX_LEN);
                            patched = true;
                            break;
                        } else {
                            eprintln!("[darkan-patcher] ERROR: Failed to patch rs2client JS5 RSA modulus at 0x{:x}", offset);
                        }
                    }
                }
                if patched { break; }
            }

            if !patched {
                eprintln!("[darkan-patcher] rs2client JS5 RSA modulus pattern not found (not rs2client process?)");
            }
        } else {
            eprintln!("[darkan-patcher] WARNING: DARKAN_JS5_RSA_MODULUS is invalid or too long");
        }
    }

    // --- Patch 1c: rs2client HTTP JS5 content port (hardcoded port 80 → custom port) ---
    //
    // WorldLobbyData::GetHTTPURL in ModeWhere=LIVE hardcodes HTTP port to 80.
    // The compiler inlined this at two call sites, so we patch both.
    // The port is read from DARKAN_HTTP_PORT env var (default: no patch).
    // Skipped in proxy mode: HTTP JS5 goes directly to Jagex on port 80.
    if is_rs2client && proxy_mode {
        eprintln!("[darkan-patcher] Proxy mode: skipping HTTP port patch");
    } else if is_rs2client {
        if let Ok(port_str) = env::var("DARKAN_HTTP_PORT") {
            if let Ok(port) = port_str.parse::<u16>() {
                let port_le = port.to_le_bytes();
                let replacement = [port_le[0], port_le[1], 0x00, 0x00]; // 4-byte LE dword

                let patterns: &[(&[u8], &str)] = &[
                    (HTTP_PORT_PATTERN_1, "copy 1"),
                    (HTTP_PORT_PATTERN_2, "copy 2"),
                ];

                for (pattern, label) in patterns {
                    let mut patched = false;
                    for region in &regions {
                        if let Some(offset) = scan_for_pattern(region.start, region.end, pattern) {
                            let patch_addr = offset + HTTP_PORT_PATCH_OFFSET;
                            eprintln!(
                                "[darkan-patcher] Found HTTP port pattern ({}) at 0x{:x}",
                                label, offset
                            );
                            if patch_memory(patch_addr, &replacement, region.prot) {
                                eprintln!(
                                    "[darkan-patcher] Successfully patched HTTP content port {} -> {} ({})",
                                    80, port, label
                                );
                                patched = true;
                                break;
                            }
                        }
                    }
                    if !patched {
                        eprintln!("[darkan-patcher] HTTP port pattern ({}) not found", label);
                    }
                }
            }
        }
    }

    // --- Patches 4-6 are rs3linux-only; skip for rs2client ---
    if is_rs2client {
        eprintln!("[darkan-patcher] rs2client detected, skipping rs3linux-specific patches");
        return;
    }

    // --- Patch 2: rs3linux launcher RSA modulus (1024-char hex ASCII string) ---
    // In custom mode: always patch — our ConfigServer signs download_hash_0 with
    // our RSA key, so rs3linux needs our public key to verify.
    // In proxy mode: SKIP — rs3linux downloads from Jagex and must verify with
    // Jagex's public key. Patching would break download_hash verification.
    if proxy_mode {
        eprintln!("[darkan-patcher] Proxy mode: skipping rs3linux RSA modulus patch (Jagex key needed for download verification)");
    } else if let Some(ref modulus_hex_clean) = modulus_hex_clean {
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
    } else {
        eprintln!("[darkan-patcher] No RSA modulus provided, skipping rs3linux RSA patch");
    }

    // --- Patch 3: rs3linux codebase URL validation regex ---
    // In proxy mode, the codebase URL is still Jagex's (matches the original regex).
    // Skip patching to avoid potential null-padding issues with the regex engine.
    if proxy_mode {
        eprintln!("[darkan-patcher] Proxy mode: skipping codebase regex patch (Jagex URL matches original regex)");
    } else {
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
    // Skipped in proxy mode: rs3linux handles decompression normally with Jagex binaries.
    if proxy_mode {
        eprintln!("[darkan-patcher] Proxy mode: skipping LZMA decompression flag patch");
        return;
    }
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

/// Parse /proc/self/maps for ALL readable file-backed regions (fallback scanner).
/// Skips anonymous/heap/stack/vdso regions to reduce SIGBUS risk.
fn parse_all_file_backed_regions(maps: &str) -> Vec<Region> {
    let mut regions = Vec::new();
    for line in maps.lines() {
        // File-backed regions have a path (6th field) starting with '/'
        let parts: Vec<&str> = line.split_whitespace().collect();
        if parts.len() < 6 || !parts[5].starts_with('/') {
            continue;
        }
        // Skip special filesystems
        if parts[5].starts_with("/dev/") || parts[5].starts_with("/proc/") || parts[5].starts_with("/sys/") {
            continue;
        }
        if let Some(region) = parse_map_line(line) {
            regions.push(region);
        }
    }
    regions
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
    fn test_rs2client_modulus_constants() {
        assert_eq!(RS2CLIENT_MODULUS_PREFIX.len(), 32);
        assert_eq!(RS2CLIENT_MODULUS_HEX_LEN, 256);
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
