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
//! 2. **JS5 RSA modulus** — a 1024-char lowercase hex ASCII string (4096-bit
//!    key), replaced with `DARKAN_JS5_RSA_MODULUS`. There are TWO distinct
//!    4096-bit keys depending on which Mach-O image this dylib lands in (the
//!    same dylib is inserted into the RuneScape.app wrapper AND inherited by the
//!    rs2client it spawns):
//!
//!    a. **rs2client game-client JS5 key** — prefix `a6400fbc...` (1 match in
//!       `rs2client`). Verifies the JS5 master-index signature.
//!    b. **RuneScape.app launcher key** — prefix `a49962fc...` (1 match in the
//!       wrapper). This is the SAME key Linux's `rs3linux` launcher embeds
//!       (`RS3LINUX_MODULUS_PREFIX`); on macOS it is 4096-bit and verifies the
//!       `jav_config.ws` `download_hash`. `ConfigServer` signs the macOS
//!       jav_config with the JS5 key (512-byte sig), so if this wrapper key is
//!       left as Jagex's, the wrapper rejects our config with
//!       "Error saving file (14)" BEFORE it ever downloads the binary.
//!
//!    Both keys are overwritten with `DARKAN_JS5_RSA_MODULUS`. The patcher tries
//!    every known JS5 prefix and patches each one present (so the wrapper process
//!    patches `a49962fc...`, the rs2client process patches `a6400fbc...`).
//!
//! 3. **rs2client HTTP JS5 content path** — hardcoded port 80 (0x50) inside the
//!    inlined `jag::WorldLobbyData::GetHTTPURL`. RESOLVED by the RE agent
//!    (`docs/binary/patch-targets-macos.md` §P3): clang emits port 80 as a
//!    **16-bit** immediate (`66`-prefixed MOV), in two shapes spread across 3
//!    inlined copies. The old `b8 50 00 00 00 eb` (32-bit) pattern is a FALSE
//!    POSITIVE (switch-table arms). We scan both documented patterns
//!    (`HTTP_PORT_PATTERNS`), expect exactly 3 sites total, and write the new
//!    port as a 2-byte LE16 at +2 in each. That patch covers the documented
//!    param-driven URL-builder copies; local captures also proved post-world
//!    HTTP content can still dial the binary's external CDN host. In local mode
//!    we therefore additionally interpose connect() and rewrite external `:80`
//!    HTTP content connects to `127.0.0.1:DARKAN_HTTP_PORT`. External `:443` is
//!    left alone because it is TLS and cannot be served by the plain local Ktor
//!    content listener.
//!
//! Patches that do NOT apply to the Mach-O rs2client (verified absent in the
//! binary — they are rs3linux-launcher-only): codebase URL regex, LZMA flag.
//! Those live in the Linux patcher and are skipped here entirely.
//!
//! 4. **World-server connect() port redirect** — the client dials game WORLDS
//!    on port 443 (TLS-wrapped world protocol). Our local world server listens
//!    on `DARKAN_WORLD_PORT` (default 43597). A `__DATA,__interpose` tap on
//!    libc `connect()` rewrites a LOOPBACK `:443` destination to the local
//!    world port, leaving every external (`<jagex-ip>:443`) HTTPS connection and
//!    every other loopback port (43596 lobby, 8829 HTTP JS5) untouched. See
//!    `world_redirect.rs` for the host+port-precise selector and bootstrap
//!    safety. Active only when `DARKAN_WORLD_PORT` is set AND `DARKAN_RSA_MODULUS`
//!    is set (i.e. local/custom mode); production unsets both → no-op.
//!
//! If `DARKAN_RSA_MODULUS` is not set, the patcher does nothing — so the same
//! dylib can be harmlessly inserted in live (un-patched) mode.

#![cfg(target_os = "macos")]

mod world_redirect;

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

/// First 32 chars of the **RuneScape.app launcher** RSA modulus hex string
/// (4096-bit key). This is the macOS wrapper's `jav_config` download-hash
/// verification key — byte-identical to the key Linux's `rs3linux` launcher
/// embeds (`RS3LINUX_MODULUS_PREFIX` in `patcher/src/lib.rs`). Byte-verified to
/// appear exactly once in the RuneScape.app executable (1024 hex chars there;
/// the same prefix is only 256 hex on rs3linux but 1024 on the mac wrapper).
/// `ConfigServer` signs the macOS jav_config with `DARKAN_JS5_RSA_MODULUS`, so
/// this key must be overwritten with the same modulus or the wrapper rejects
/// jav_config with "Error saving file (14)".
const RUNESCAPE_WRAPPER_JS5_MODULUS_PREFIX: &[u8] = b"a49962fc0737fddcd94c0daf84e5d214";

/// All known 4096-bit JS5/launcher modulus prefixes that must be overwritten
/// with `DARKAN_JS5_RSA_MODULUS`. The same dylib is inserted into the wrapper
/// AND inherited by the rs2client it spawns, so we scan for every prefix and
/// patch whichever is present in the current image.
const JS5_MODULUS_PREFIXES: &[&[u8]] = &[
    RS2CLIENT_JS5_MODULUS_PREFIX,
    RUNESCAPE_WRAPPER_JS5_MODULUS_PREFIX,
];

/// Full length of every JS5 RSA modulus hex string (4096-bit = 1024 hex chars).
const RS2CLIENT_JS5_MODULUS_HEX_LEN: usize = 1024;

/// macOS HTTP JS5 content-port patch sites — RESOLVED by the ghidra-reverse-engineer
/// agent (see `docs/binary/patch-targets-macos.md` §P3, rev 948-5).
///
/// clang emits port 80 as a **16-bit** immediate (operand-size prefix `0x66`),
/// NOT the 32-bit `MOV EAX, 0x50` form used on Linux/Windows. The URL-builder
/// (`jag::WorldLobbyData::GetHTTPURL`, inlined into 3 callers) appears in two
/// instruction shapes. Each pattern's port immediate is a 2-byte LE16 at +2.
///
/// CRITICAL: do NOT use the old `b8 50 00 00 00 eb` pattern — its two matches are
/// llvm switch-table arms (struct-offset/enum returns), NOT port constants; the
/// genuine sites all carry the `0x1b58` (worldId+7000) alternate-port marker.
///
/// Total expected matches across both patterns: 2 + 1 = 3 (all must be patched).
struct HttpPortPattern {
    /// Distinctive byte signature (includes the `0x1b58` alternate-port marker
    /// and the trailing `44 0f b7 c?` to be globally unique).
    pattern: &'static [u8],
    /// Offset of the 2-byte LE16 port immediate within `pattern`.
    port_offset: usize,
    /// How many matches this pattern is expected to find (for the abort check).
    expected: usize,
}

/// Type-1: `MOV SI, 0x50` / `JMP +6` / `ADD ESI, 0x1b58`. Two sites.
/// Type-2: `MOV AX, 0x50` / `JMP +5` / `ADD EAX, 0x1b58`. One site.
const HTTP_PORT_PATTERNS: &[HttpPortPattern] = &[
    HttpPortPattern {
        // 66 be 50 00  eb 06  81 c6 58 1b 00 00  44 0f b7 c6
        pattern: &[
            0x66, 0xbe, 0x50, 0x00, 0xeb, 0x06, 0x81, 0xc6, 0x58, 0x1b, 0x00, 0x00, 0x44, 0x0f,
            0xb7, 0xc6,
        ],
        port_offset: 2,
        expected: 2,
    },
    HttpPortPattern {
        // 66 b8 50 00  eb 05  05 58 1b 00 00  44 0f b7 c0
        pattern: &[
            0x66, 0xb8, 0x50, 0x00, 0xeb, 0x05, 0x05, 0x58, 0x1b, 0x00, 0x00, 0x44, 0x0f, 0xb7,
            0xc0,
        ],
        port_offset: 2,
        expected: 1,
    },
];

/// Total expected HTTP-port patch sites across all patterns (2 + 1).
const HTTP_PORT_EXPECTED_TOTAL: usize = 3;

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

    /// Mach VM protect — required to make code-signed `__TEXT` pages writable.
    /// On a hardened/code-signed image (the RuneScape.app wrapper), plain
    /// `mprotect(PROT_WRITE)` on an executable page returns EACCES; the kernel
    /// refuses to grant WRITE to a code-signed mapping. The supported way to
    /// edit such a page is a copy-on-write break via `VM_PROT_COPY`, which
    /// `mach_vm_protect` performs (the page is privately copied, decoupling it
    /// from the code-signature, then made writable).
    fn mach_vm_protect(
        target_task: u32,
        address: u64,
        size: u64,
        set_maximum: i32,
        new_protection: i32,
    ) -> i32;
    /// `mach_task_self()` is exposed as a macro in C; the underlying symbol is
    /// the global `mach_task_self_`. Declare it directly.
    static mach_task_self_: u32;
}

const MH_MAGIC_64: u32 = 0xfeed_facf;
const LC_SEGMENT_64: u32 = 0x19;

// Mach VM protection bits (sys/vm_prot.h).
const VM_PROT_READ: i32 = 0x1;
const VM_PROT_WRITE: i32 = 0x2;
const VM_PROT_EXECUTE: i32 = 0x4;
/// `VM_PROT_COPY` — when OR'd into a protect request, forces a copy-on-write
/// break so a shared/code-signed page can be made writable. This is the macOS
/// equivalent of how the Linux patcher writes to `.rodata`/`__TEXT`.
const VM_PROT_COPY: i32 = 0x10;

/// KERN_SUCCESS.
const KERN_SUCCESS: i32 = 0;

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
    let modulus_hex = env::var("DARKAN_RSA_MODULUS")
        .ok()
        .filter(|v| !v.is_empty());
    let js5_modulus_hex = env::var("DARKAN_JS5_RSA_MODULUS")
        .ok()
        .filter(|v| !v.is_empty());
    let proxy_mode = env::var("DARKAN_PROXY_MODE")
        .map(|v| v == "1")
        .unwrap_or(false);

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
    //
    // Two distinct 4096-bit keys share this patch (see JS5_MODULUS_PREFIXES):
    //   - rs2client game-client JS5 key (a6400fbc...) — present in rs2client.
    //   - RuneScape.app launcher key   (a49962fc...) — present in the wrapper,
    //     verifies the jav_config download_hash (ConfigServer signs macOS
    //     jav_config with the JS5 key).
    // The SAME dylib is inserted into the wrapper and inherited by the rs2client
    // it spawns, so we scan for every prefix and patch each one present. At least
    // one prefix must match per image, or the JS5/jav_config verification fails.
    if proxy_mode {
        eprintln!("[darkan-patcher-mac] Proxy mode: skipping JS5 RSA modulus patch");
    } else if let Some(ref js5_hex) = js5_modulus_hex {
        match pad_modulus_hex(js5_hex, RS2CLIENT_JS5_MODULUS_HEX_LEN) {
            Some(replacement) if hex_to_bytes(&replacement).is_some() => {
                let mut total_patched = 0usize;
                for prefix in JS5_MODULUS_PREFIXES {
                    let finder = Finder::new(*prefix);
                    let matches = find_all(&regions, &finder);
                    // SAFETY: prefixes are ASCII, so lossy is exact here (used only for logging).
                    let prefix_str = String::from_utf8_lossy(prefix);
                    if matches.is_empty() {
                        continue;
                    }
                    for addr in matches {
                        eprintln!(
                            "[darkan-patcher-mac] Found JS5 RSA modulus ({}…) at 0x{:x}",
                            &prefix_str[..prefix_str.len().min(8)],
                            addr
                        );
                        if patch_memory(addr, replacement.as_bytes()) {
                            total_patched += 1;
                            eprintln!(
                                "[darkan-patcher-mac] Successfully patched JS5 RSA modulus ({}… , {} hex chars) at 0x{:x}",
                                &prefix_str[..prefix_str.len().min(8)],
                                RS2CLIENT_JS5_MODULUS_HEX_LEN,
                                addr
                            );
                        } else {
                            eprintln!(
                                "[darkan-patcher-mac] ERROR: failed to patch JS5 RSA modulus ({}…) at 0x{:x}",
                                &prefix_str[..prefix_str.len().min(8)],
                                addr
                            );
                        }
                    }
                }
                if total_patched == 0 {
                    eprintln!(
                        "[darkan-patcher-mac] JS5 RSA modulus pattern not found (tried {} known prefixes) \
                         — JS5/jav_config verification will FAIL in this image",
                        JS5_MODULUS_PREFIXES.len()
                    );
                }
            }
            _ => eprintln!(
                "[darkan-patcher-mac] WARNING: DARKAN_JS5_RSA_MODULUS is invalid or too long"
            ),
        }
    } else {
        eprintln!("[darkan-patcher-mac] DARKAN_JS5_RSA_MODULUS not set — skipping JS5 patch");
    }

    // --- Patch 3: HTTP JS5 content port (RESOLVED — docs/binary/patch-targets-macos.md §P3) ---
    // Skipped in proxy mode: HTTP JS5 goes directly to Jagex on port 80.
    //
    // The port-80 immediate is 16-bit on macOS (clang `66`-prefixed MOV), so we
    // write a 2-byte LE16 at +2 within each pattern. Two pattern variants cover
    // the 3 inlined GetHTTPURL copies. Only present in rs2client (the wrapper has
    // no GetHTTPURL — 0 matches there is expected).
    if proxy_mode {
        eprintln!("[darkan-patcher-mac] Proxy mode: skipping HTTP port patch");
    } else if let Ok(port_str) = env::var("DARKAN_HTTP_PORT") {
        if let Ok(port) = port_str.parse::<u16>() {
            if port == 0 {
                eprintln!("[darkan-patcher-mac] WARNING: DARKAN_HTTP_PORT is 0 — skipping HTTP port/content patches");
            } else {
                let port_le = port.to_le_bytes(); // 2-byte LE16
                world_redirect::arm_http_content(port);
                eprintln!(
                    "[darkan-patcher-mac] HTTP content redirect ARMED: external :80 -> 127.0.0.1:{} \
                     (external :443 TLS left untouched)",
                    port
                );

                // Collect matches for every pattern first so we can validate the
                // total before writing anything (abort-on-drift, per the RE doc).
                let mut per_pattern: Vec<(usize, Vec<usize>)> = Vec::new();
                let mut total = 0usize;
                for (i, p) in HTTP_PORT_PATTERNS.iter().enumerate() {
                    let finder = Finder::new(p.pattern);
                    let matches = find_all(&regions, &finder);
                    total += matches.len();
                    per_pattern.push((i, matches));
                }

                if total == 0 {
                    // Expected in the wrapper image (no GetHTTPURL); a real warning
                    // only in rs2client. We can't cheaply tell which image we're in,
                    // so log at WARNING and move on.
                    eprintln!(
                        "[darkan-patcher-mac] HTTP port: 0 sites in this image (expected for the \
                         RuneScape.app wrapper; in rs2client this means requests still hit port 80)"
                    );
                } else if total != HTTP_PORT_EXPECTED_TOTAL {
                    // Drift guard: the RE doc pins exactly 3 sites. Any other non-zero
                    // count means the binary changed — refuse rather than mis-patch.
                    eprintln!(
                        "[darkan-patcher-mac] WARNING: HTTP port matched {} sites, expected {} \
                         (binary drift?) — refusing to patch. Re-run the RE agent on the Mach-O.",
                        total, HTTP_PORT_EXPECTED_TOTAL
                    );
                } else {
                    let mut patched = 0usize;
                    for (i, matches) in &per_pattern {
                        let p = &HTTP_PORT_PATTERNS[*i];
                        if matches.len() != p.expected {
                            eprintln!(
                                "[darkan-patcher-mac] NOTE: HTTP port pattern[{}] matched {} sites (expected {})",
                                i,
                                matches.len(),
                                p.expected
                            );
                        }
                        for site in matches {
                            let addr = site + p.port_offset;
                            if patch_memory(addr, &port_le) {
                                patched += 1;
                                eprintln!(
                                    "[darkan-patcher-mac] Patched HTTP content port at 0x{:x} (80 -> {})",
                                    site, port
                                );
                            } else {
                                eprintln!(
                                    "[darkan-patcher-mac] ERROR: failed to patch HTTP port at 0x{:x}",
                                    site
                                );
                            }
                        }
                    }
                    eprintln!(
                        "[darkan-patcher-mac] HTTP port: patched {}/{} sites (80 -> {})",
                        patched, HTTP_PORT_EXPECTED_TOTAL, port
                    );
                }
            }
        } else {
            eprintln!("[darkan-patcher-mac] WARNING: DARKAN_HTTP_PORT is not a valid u16 — skipping HTTP port patch");
        }
    }

    // --- Patch 4: world-server connect() port redirect (loopback:443 -> world) ---
    // The interpose table is linked unconditionally (dyld activates it the moment
    // the dylib maps), but its replacement is a PURE passthrough until we arm it
    // here. We arm ONLY in local/custom mode: this whole ctor has already
    // returned early if DARKAN_RSA_MODULUS is unset (production unsets it), so
    // reaching this point means custom mode. Arming additionally requires
    // DARKAN_WORLD_PORT to be a valid u16 (run-client-mac.sh sets it in local
    // mode and unsets it in production).
    //
    // Skipped in proxy mode: with an external MITM proxy the world host is not
    // our loopback, so there is nothing to redirect (and we must not touch the
    // proxy's own :443 path).
    if proxy_mode {
        eprintln!("[darkan-patcher-mac] Proxy mode: skipping world-port redirect");
    } else {
        // The port the client dials worlds on by default (443). Overridable via
        // DARKAN_WORLD_FROM_PORT for forward-compat if Jagex ever changes it.
        let from_port = env::var("DARKAN_WORLD_FROM_PORT")
            .ok()
            .and_then(|s| s.trim().parse::<u16>().ok())
            .unwrap_or(world_redirect::DEFAULT_WORLD_FROM_PORT);

        match env::var("DARKAN_WORLD_PORT") {
            Ok(port_str) => match port_str.trim().parse::<u16>() {
                Ok(to_port) if to_port != 0 && to_port != from_port => {
                    world_redirect::arm(from_port, to_port);
                    eprintln!(
                        "[darkan-patcher-mac] World-port redirect ARMED: loopback:{} -> :{} \
                         (external :{} HTTPS left untouched)",
                        from_port, to_port, from_port
                    );
                }
                Ok(to_port) if to_port == from_port => {
                    eprintln!(
                        "[darkan-patcher-mac] World-port redirect: DARKAN_WORLD_PORT == {} (the \
                         world dial port) — nothing to rewrite, redirect inactive",
                        from_port
                    );
                }
                Ok(_) => {
                    eprintln!(
                        "[darkan-patcher-mac] World-port redirect: DARKAN_WORLD_PORT is 0 — \
                         redirect inactive"
                    );
                }
                Err(_) => {
                    eprintln!(
                        "[darkan-patcher-mac] WARNING: DARKAN_WORLD_PORT ('{}') is not a valid u16 \
                         — world-port redirect inactive (client will dial :{} and fail to reach \
                         the local world)",
                        port_str, from_port
                    );
                }
            },
            Err(_) => {
                eprintln!(
                    "[darkan-patcher-mac] DARKAN_WORLD_PORT not set — world-port redirect inactive \
                     (client will dial :{}; set DARKAN_WORLD_PORT to reach the local world)",
                    from_port
                );
            }
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
/// and restoring R+X afterward.
///
/// macOS maps `__TEXT` (and the `__TEXT,__const` strings we patch) r-x. On a
/// code-signed image — the RuneScape.app wrapper is adhoc-signed — plain
/// `mprotect(PROT_WRITE)` on those pages returns EACCES (errno 13): the kernel
/// refuses to grant WRITE to a code-signed executable mapping. The supported
/// route is a copy-on-write break via `mach_vm_protect` with `VM_PROT_COPY`,
/// which privately copies the page (decoupling it from the signature) and makes
/// it writable. This mirrors how the Linux patcher writes to `.rodata`/`__TEXT`.
///
/// We try `mach_vm_protect(VM_PROT_COPY | READ | WRITE)` first (works on both
/// the signed wrapper and the unsigned rs2client), and fall back to plain
/// `mprotect` only if the mach call is unavailable. After writing we restore the
/// pages to READ|EXEC.
fn patch_memory(addr: usize, new_bytes: &[u8]) -> bool {
    let page_start = addr & !(PAGE_SIZE - 1);
    let page_end_addr = addr + new_bytes.len();
    let page_end = (page_end_addr + PAGE_SIZE - 1) & !(PAGE_SIZE - 1);
    let total_len = page_end - page_start;

    unsafe {
        let task = mach_task_self_;

        // COW-break + grant WRITE. VM_PROT_COPY makes a private copy of any
        // shared/code-signed page so it can be written without violating the
        // signature.
        let kr = mach_vm_protect(
            task,
            page_start as u64,
            total_len as u64,
            0, // set_maximum = false
            VM_PROT_READ | VM_PROT_WRITE | VM_PROT_COPY,
        );
        if kr != KERN_SUCCESS {
            // Fall back to mprotect (covers any environment where the mach call
            // is refused but the page was never code-signed to begin with).
            let ret = libc::mprotect(
                page_start as *mut c_void,
                total_len,
                libc::PROT_READ | libc::PROT_WRITE | libc::PROT_EXEC,
            );
            if ret != 0 {
                eprintln!(
                    "[darkan-patcher-mac] mach_vm_protect(+WRITE|COPY) failed (kr={}) and mprotect(+WRITE) failed for 0x{:x}..0x{:x}: errno={}",
                    kr,
                    page_start,
                    page_start + total_len,
                    *libc::__error()
                );
                return false;
            }
        }

        ptr::copy_nonoverlapping(new_bytes.as_ptr(), addr as *mut u8, new_bytes.len());

        // Restore READ|EXEC (drop WRITE). The page is now a private copy; this
        // keeps it executable for the code that reads the patched string/literal.
        let kr = mach_vm_protect(
            task,
            page_start as u64,
            total_len as u64,
            0,
            VM_PROT_READ | VM_PROT_EXECUTE,
        );
        if kr != KERN_SUCCESS {
            let ret = libc::mprotect(
                page_start as *mut c_void,
                total_len,
                libc::PROT_READ | libc::PROT_EXEC,
            );
            if ret != 0 {
                eprintln!(
                    "[darkan-patcher-mac] WARNING: protect(restore R+X) failed for 0x{:x} (mach kr={}, errno={})",
                    page_start,
                    kr,
                    *libc::__error()
                );
            }
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
        assert_eq!(
            hex_to_bytes("0xDEADBEEF"),
            Some(vec![0xde, 0xad, 0xbe, 0xef])
        );
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
        // The RuneScape.app wrapper's 4096-bit launcher key (== rs3linux's key).
        // Must be byte-identical to the Linux patcher's RS3LINUX_MODULUS_PREFIX.
        assert_eq!(RUNESCAPE_WRAPPER_JS5_MODULUS_PREFIX.len(), 32);
        assert_eq!(
            RUNESCAPE_WRAPPER_JS5_MODULUS_PREFIX,
            b"a49962fc0737fddcd94c0daf84e5d214"
        );
        // Both JS5 prefixes are tried; each is a 32-char hex prefix.
        assert_eq!(JS5_MODULUS_PREFIXES.len(), 2);
        assert!(JS5_MODULUS_PREFIXES.contains(&RS2CLIENT_JS5_MODULUS_PREFIX));
        assert!(JS5_MODULUS_PREFIXES.contains(&RUNESCAPE_WRAPPER_JS5_MODULUS_PREFIX));
        for p in JS5_MODULUS_PREFIXES {
            assert_eq!(p.len(), 32);
        }
        // HTTP port patterns (clang 16-bit `66`-prefixed MOV) — RE-resolved, 3 sites total.
        assert_eq!(HTTP_PORT_PATTERNS.len(), 2);
        assert_eq!(
            HTTP_PORT_EXPECTED_TOTAL,
            HTTP_PORT_PATTERNS.iter().map(|p| p.expected).sum::<usize>()
        );
        for p in HTTP_PORT_PATTERNS {
            // Each pattern carries the operand-size prefix and the port at +2.
            assert_eq!(p.pattern[0], 0x66);
            assert_eq!(p.port_offset, 2);
            // The 2 bytes at +2 encode port 80 (0x0050) little-endian.
            assert_eq!(&p.pattern[p.port_offset..p.port_offset + 2], &[0x50, 0x00]);
            // The 0x1b58 (worldId+7000) alternate-port marker must be present.
            assert!(
                p.pattern.windows(2).any(|w| w == [0x58, 0x1b]),
                "pattern must contain the 0x1b58 alternate-port marker"
            );
        }
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
