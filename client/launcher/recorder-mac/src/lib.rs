//! Darkan boot-transcript recorder — macOS `DYLD_INSERT_LIBRARIES` dylib.
//!
//! SIBLING of the RSA patcher (`client/launcher/patcher-mac/`). Where the
//! patcher *modifies* the NXT client to redirect it, the recorder *observes* it
//! and writes a faithful raw transcript of the boot to disk for offline
//! deframing into an annotated JSONL packet transcript.
//!
//! ## What it captures (Phase 1)
//!
//! 1. **Game TCP stream** — `recv/read/recvfrom/send/write/sendto` interposed
//!    (zero code-patching, Rosetta-safe). Every transfer → a framed record
//!    `{ts, fd, dir, len, bytes}`. Plus `connect`(peer addr:port) + `close` so
//!    the deframer can attribute fds to JS5/lobby/world by port. (`interpose.rs`)
//! 2. **ISAAC seeds** — the 4 RAW C2S seeds, captured by an inline hook on the
//!    `MOVDQU XMM0,[R14+0x48]` client-seed load inside `SendLoginPacket`,
//!    located by pattern-scan. (`seedhook.rs`) This is the one Rosetta-risk
//!    surface; it degrades to interpose-only if it can't install.
//!
//! ## Gating + coexistence
//!
//! The whole thing is a no-op unless `DARKAN_RECORD=1`, so the dylib can ride
//! alongside the patcher in a colon-separated `DYLD_INSERT_LIBRARIES` without
//! interfering. (The patcher gates on `DARKAN_RSA_MODULUS`/`DARKAN_PROXY_MODE`;
//! the two gates are orthogonal.)
//!
//! ## Init ordering
//!
//! Like the patcher, the `#[ctor]` runs BEFORE the main image's
//! `__mod_init_func` C++ static initializers — so the interpose table is wired
//! and the seed hook is installed long before the client's first network or
//! login call. The interpose section is wired by dyld at load regardless of the
//! ctor; the ctor's job is to open the capture file + install the seed hook.
//!
//! See `docs/tools/recorder-plan.md` and `docs/binary/patch-targets-948-mac.md`.

mod capture;
mod image;
mod interpose; // the #[link_section] interpose table lives here (referenced via #[used])
mod seedhook;

use std::env;

/// Default capture directory: `~/.darkan3-mac/recorder/`. Overridable via
/// `DARKAN_RECORD_DIR`. The file is `capture-<pid>.bin`.
fn resolve_capture_path() -> Result<String, String> {
    if let Ok(explicit) = env::var("DARKAN_RECORD_FILE") {
        if !explicit.is_empty() {
            return Ok(explicit);
        }
    }
    let dir = env::var("DARKAN_RECORD_DIR")
        .ok()
        .filter(|d| !d.is_empty())
        .unwrap_or_else(|| {
            // Resolve the REAL home from passwd, not $HOME — the launcher
            // overrides $HOME to its own data dir (see run-client-mac.sh /
            // undercut_bootstrap.cpp resolve_real_home). For Phase-1 manual
            // runs $HOME is usually fine, but passwd is the robust source.
            let home = real_home();
            format!("{home}/.darkan3-mac/recorder")
        });
    std::fs::create_dir_all(&dir).map_err(|e| format!("mkdir {dir}: {e}"))?;
    Ok(format!("{dir}/capture-{}.bin", std::process::id()))
}

/// Real user home from the passwd db (robust against `HOME` overrides).
fn real_home() -> String {
    unsafe {
        let uid = libc::getuid();
        let pw = libc::getpwuid(uid);
        if !pw.is_null() && !(*pw).pw_dir.is_null() {
            if let Ok(s) = std::ffi::CStr::from_ptr((*pw).pw_dir).to_str() {
                if !s.is_empty() {
                    return s.to_string();
                }
            }
        }
    }
    env::var("HOME").unwrap_or_else(|_| "/tmp".to_string())
}

#[ctor::ctor]
fn recorder_init() {
    // Env gate — inert unless DARKAN_RECORD=1. Mirrors the patcher's gating so
    // the dylib is harmless in DYLD_INSERT_LIBRARIES when recording is off.
    let enabled = env::var("DARKAN_RECORD").map(|v| v == "1").unwrap_or(false);
    if !enabled {
        return;
    }

    // Definitive "ctor ran" breadcrumb, written before ANY other work and
    // independent of stderr (which the client may freopen). If this file does
    // not appear, the ctor itself never executed under Rosetta.
    {
        use std::io::Write as _;
        if let Ok(mut f) = std::fs::OpenOptions::new()
            .create(true)
            .append(true)
            .open("/tmp/darkan-recorder-ctor.log")
        {
            let _ = writeln!(f, "ctor ran pid={}", std::process::id());
        }
    }

    let pid = std::process::id();

    // Open the capture file. If this fails we cannot record anything useful, so
    // bail (the interpose table is still wired by dyld, but record_* no-ops with
    // no writer installed — harmless).
    let path = match resolve_capture_path().and_then(|p| capture::init(&p)) {
        Ok(p) => p,
        Err(e) => {
            eprintln!("[darkan-recorder] FAILED to open capture file: {e} — recorder inert");
            return;
        }
    };

    // Arm the capture path now: the writer is installed and we are safely
    // inside the ctor (std runtime is up). The interposes were pure
    // passthroughs until this store (dyld wires the interpose section during
    // fixups, BEFORE the ctor runs, so the client's earliest read/close calls
    // must not enter the capture path — the ARMED gate ensures that).
    capture::arm();

    eprintln!("[darkan-recorder] loaded, pid={pid}, capture={path}");
    capture::record_note(&format!(
        "recorder loaded pid={pid} target={}",
        env::args().next().unwrap_or_default()
    ));

    // Locate the rs2client image (needed only for the seed hook). The interpose
    // taps need no image knowledge and are already wired by dyld.
    let (segments, image_name) = match image::find_rs2client_segments() {
        Some(v) => v,
        None => {
            let msg = "rs2client image not found among dyld images — \
                       interpose-only (injected into a wrapper process?); seed hook skipped";
            eprintln!("[darkan-recorder] {msg}");
            capture::record_note(msg);
            // The interposes still capture whatever sockets this process opens.
            return;
        }
    };
    eprintln!(
        "[darkan-recorder] found image '{image_name}', {} segments",
        segments.len()
    );

    // Escape hatch: DARKAN_RECORD_NO_SEEDHOOK=1 skips the inline seed hook
    // entirely (interpose-only). This is the documented graceful-degradation
    // path AND the isolation knob for diagnosing whether the trampoline is what
    // perturbs the client under Rosetta (review B8). On our server the seeds
    // come from the server log, so interpose-only is fully sufficient there.
    if env::var("DARKAN_RECORD_NO_SEEDHOOK")
        .map(|v| v == "1")
        .unwrap_or(false)
    {
        let msg = "seed hook DISABLED via DARKAN_RECORD_NO_SEEDHOOK=1 — interpose-only";
        eprintln!("[darkan-recorder] {msg}");
        capture::record_note(msg);
        eprintln!("[darkan-recorder] init done (interpose capture armed).");
        return;
    }

    // Install the ISAAC seed hook (the Rosetta-risk surface). Report honestly.
    match seedhook::install(&segments) {
        seedhook::HookResult::Installed { hook_addr } => {
            eprintln!(
                "[darkan-recorder] seed hook INSTALLED at {hook_addr:#x} (seeds captured at login)"
            );
        }
        seedhook::HookResult::NotLocated(why) => {
            let msg = format!(
                "seed hook NOT LOCATED ({why}) — degrading to INTERPOSE-ONLY. \
                 Raw streams still captured; opcodes cannot be decoded without seeds \
                 (on our server use the server-logged seeds)."
            );
            eprintln!("[darkan-recorder] {msg}");
            capture::record_note(&msg);
        }
        seedhook::HookResult::InstallFailed(why) => {
            let msg = format!("seed hook INSTALL FAILED ({why}) — degrading to INTERPOSE-ONLY.");
            eprintln!("[darkan-recorder] {msg}");
            capture::record_note(&msg);
        }
        seedhook::HookResult::SelftestFailed(why) => {
            let msg = format!(
                "RWX self-test FAILED ({why}) — trampoline unsafe under Rosetta on this host; \
                 INTERPOSE-ONLY, __TEXT untouched."
            );
            eprintln!("[darkan-recorder] {msg}");
            capture::record_note(&msg);
        }
    }

    eprintln!("[darkan-recorder] init done (interpose capture armed).");
}
