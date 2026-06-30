//! Darkan packet recorder — macOS dylib (`DYLD_INSERT_LIBRARIES` target).
//!
//! Sibling of `client/launcher/patcher-mac`. Inserted into BOTH the
//! RuneScape.app wrapper and the rs2client process (the DYLD env is inherited
//! across the wrapper -> client spawn). Its constructor:
//!
//!   1. Bails immediately unless `DARKAN_RECORD=1` (hard no-op otherwise — safe
//!      to leave inserted, exactly like the patcher's RSA-modulus gating).
//!   2. Identifies whether it is running in the wrapper or in rs2client by
//!      sig-scanning the loaded Mach-O for the client's capture points.
//!   3. Creates a session directory under `$DARKAN_RECORD_DIR`
//!      (default `~/.undercut/recordings`).
//!   4. In rs2client: installs inline detours on the documented capture points
//!      (`retour` — the self-contained analogue of the engine's funchook) and
//!      interposes libc connect()/close().
//!   5. In the wrapper: emits only the socket plane (via the same libc
//!      interpose) plus a process/spawn event — no client struct exists there.
//!
//! It NEVER mutates client code beyond the detour trampolines and is written in
//! the patcher's defensive style: every pointer is null-checked, every read is
//! bounded, and a capture error degrades to a missing line — never a crash.

#![cfg(target_os = "macos")]

mod detour;
mod hooks;
mod interpose;
mod mem;
mod offsets;
mod oracle;
mod prot_table;
mod session;
mod sig;
mod state;

use session::{Proc, Session};
use std::env;
use std::path::PathBuf;

const RECORDER_TAG: &str = "[darkan-recorder-mac]";

/// Single logging funnel (stderr, like the patcher). Never logs packet bytes.
pub(crate) fn log(msg: &str) {
    eprintln!("{RECORDER_TAG} {msg}");
}

// -- Constructor (runs before host main, like the patcher's ctor) -------------

#[ctor::ctor]
fn recorder_init() {
    // Gate: identical contract to the patcher — presence without DARKAN_RECORD=1
    // is a pure no-op, so the dylib is safe to leave in DYLD_INSERT_LIBRARIES.
    if env::var("DARKAN_RECORD").ok().as_deref() != Some("1") {
        return;
    }

    // Guard the whole ctor so nothing can unwind into the host loader.
    let result = std::panic::catch_unwind(|| {
        run_ctor();
    });
    if result.is_err() {
        log("ctor panicked (recording disabled); client continues unaffected");
    }
}

fn run_ctor() {
    let record_dir = record_dir();
    // The session-dir tag and session.json `server_mode` are the SAME label.
    let server_mode = server_mode();
    let wrapper_pid = env::var("DARKAN_WRAPPER_PID")
        .ok()
        .and_then(|s| s.parse::<u32>().ok());

    // Resolve the main image and decide which process we are in.
    let image = sig::resolve_main_image();
    let proc = identify_proc(&image);

    let build = image
        .as_ref()
        .and_then(read_build_string)
        .unwrap_or_else(|| "unknown".to_string());

    log(&format!(
        "recorder loaded: proc={} server_mode={} build={} dir={}",
        proc.as_str(),
        server_mode,
        build,
        record_dir.display()
    ));

    let session = match Session::create(
        &record_dir,
        proc,
        server_mode,
        &build,
        server_mode,
        wrapper_pid,
    ) {
        Ok(s) => s,
        Err(e) => {
            log(&format!(
                "failed to create session dir: {e} — recording disabled"
            ));
            return;
        }
    };
    let session_dir = session.dir().to_path_buf();
    state::set_meta(build.clone(), server_mode.to_string(), wrapper_pid);
    state::init_session(session);

    // Emit a process/spawn event from BOTH procs so the timeline has an origin.
    if let Some(s) = state::session() {
        s.event_process(
            "ctor",
            serde_json::json!({
                "image": image.as_ref().map(|i| i.name.clone()).unwrap_or_default(),
            }),
        );
    }

    // The libc connect()/close() interposers are wired via the static
    // __interpose table (installed by the linker) and become active simply by
    // this dylib being loaded — they consult `state::session()` at call time,
    // so they cover BOTH the wrapper and rs2client with no extra setup here.

    match proc {
        Proc::Rs2client => {
            let Some(image) = image else {
                log("rs2client: main image unresolved — socket plane only");
                return;
            };
            let installed = hooks::install_all(&image);
            log(&format!(
                "rs2client: {installed}/{} capture-point hooks installed",
                hooks::HOOK_COUNT
            ));
            if installed == 0 {
                log("rs2client: NO function hooks installed — only socket + connect events will appear");
            }
            // Publish the image slide so the hot-hook retry can dump the live
            // opcode table, then make a best-effort attempt NOW. The client's
            // `ServerProt::RegisterAll` (a C++ static initializer) may run before
            // OR after this ctor; if the table isn't populated yet this no-ops and
            // the guarded retry from the s2c-dispatch / SetMainState hooks emits
            // `prot-table.json` the moment it is (always before any packet flows).
            state::publish_image_slide(image.slide);
            state::maybe_dump_prot_table();

            // Per-frame anim trace (OPT-IN, default OFF): a register-SAFE poller
            // thread that samples the LOCAL avatar's animation state every ~16ms and
            // writes `anim-trace.jsonl`. Gated on DARKAN_ANIM_TRACE=1 — when unset we
            // do NOT spawn it, so normal/production captures are completely
            // unaffected. It is NOT an inline hook on the render function (that
            // crashes the client by clobbering its XMM registers — see hooks.rs); a
            // poller only reads memory and can never perturb the client.
            if env::var("DARKAN_ANIM_TRACE").ok().as_deref() == Some("1") {
                state::spawn_anim_trace_poller();
            }
        }
        Proc::Wrapper => {
            log("wrapper: socket plane + process events only (no client struct)");
        }
    }

    // Best-effort: flush a stop record + connection list at process exit.
    install_atexit();

    // Arm the libc connect()/close() interposers LAST — only now is all of our
    // state (session, locks) safe to touch. Before this point they are pure
    // passthroughs (see interpose::ARMED) so libSystem's own early close()/etc.
    // during bootstrap never reach our TLS-backed code.
    interpose::arm();

    log(&format!("recording to {}", session_dir.display()));
}

// -- Process identification ---------------------------------------------------

/// rs2client iff the client's distinctive capture-point signatures resolve in
/// the main image. The image NAME is a secondary hint only (the app bundle may
/// expose the binary under a non-"rs2client" path). The wrapper is a different
/// Mach-O and will not contain these signatures.
fn identify_proc(image: &Option<sig::MainImage>) -> Proc {
    let Some(image) = image else {
        return Proc::Wrapper;
    };
    // SetMainState is the most distinctive (its entry encodes MAIN_STATE 0x19DB0).
    if sig::resolve(image, &offsets::SIG_SET_MAIN_STATE).is_some() {
        return Proc::Rs2client;
    }
    // Fallback hint by name.
    if image.name.contains("rs2client") {
        return Proc::Rs2client;
    }
    Proc::Wrapper
}

// -- Build string -------------------------------------------------------------

/// Read the `RS2Engine-948-NXT-5`-style build string. The RE doc places it at
/// file offset 0xe1f439, but rather than trust a fixed offset across sub-revs we
/// scan the readable image for the `RS2Engine-` prefix and return the rest of
/// the ASCII token.
fn read_build_string(image: &sig::MainImage) -> Option<String> {
    use memchr::memmem;
    let needle = b"RS2Engine-";
    let finder = memmem::Finder::new(needle);
    for region in sig::readable_regions(image) {
        let len = region.end - region.start;
        if len < needle.len() {
            continue;
        }
        let slice = unsafe { std::slice::from_raw_parts(region.start as *const u8, len) };
        for pos in finder.find_iter(slice) {
            // Read a tightly-bounded build token: ASCII letters/digits/dots and
            // single internal hyphens only. Stop at the first byte outside that
            // set (NUL terminator, punctuation, etc.) so we never run into an
            // adjacent string. Validate the shape (`RS2Engine-<num>-NXT-<num>`)
            // before accepting — anything else is a false hit and we keep going.
            let mut end = pos;
            while end < slice.len() && end - pos < 48 {
                let b = slice[end];
                let ok = b.is_ascii_alphanumeric() || b == b'-' || b == b'.';
                if !ok {
                    break;
                }
                end += 1;
            }
            if let Ok(s) = std::str::from_utf8(&slice[pos..end]) {
                if looks_like_build(s) {
                    return Some(s.to_string());
                }
            }
        }
    }
    None
}

/// Accept only `RS2Engine-<digits>-NXT-<digits>` (the documented build form).
fn looks_like_build(s: &str) -> bool {
    let rest = match s.strip_prefix("RS2Engine-") {
        Some(r) => r,
        None => return false,
    };
    // <num>-NXT-<num>
    let mut parts = rest.splitn(3, '-');
    let major = parts.next().unwrap_or("");
    let nxt = parts.next().unwrap_or("");
    let minor = parts.next().unwrap_or("");
    !major.is_empty()
        && major.bytes().all(|b| b.is_ascii_digit())
        && nxt == "NXT"
        && !minor.is_empty()
        && minor.bytes().all(|b| b.is_ascii_digit())
}

// -- Environment / paths ------------------------------------------------------

fn record_dir() -> PathBuf {
    if let Ok(dir) = env::var("DARKAN_RECORD_DIR") {
        if !dir.is_empty() {
            return PathBuf::from(dir);
        }
    }
    // Default: ~/.undercut/recordings
    let home = env::var("HOME").unwrap_or_else(|_| "/tmp".to_string());
    PathBuf::from(home).join(".undercut").join("recordings")
}

/// The capture's server mode: `"production"` (pristine client talking to LIVE
/// Jagex) or `"local"` (patcher has redirected the client to the local Darkan
/// server). This single label drives both the session-dir name and the
/// session.json `server_mode` field.
///
/// IMPORTANT — the relationship to the patcher is the INVERSE of the obvious
/// reading: a SET `DARKAN_RSA_MODULUS` / `DARKAN_JS5_RSA_MODULUS` means the
/// sibling patcher dylib has rewritten the client's RSA keys to redirect it to
/// the LOCAL server (= "local"). No modulus means the patcher is a hard no-op
/// and the pristine client talks to LIVE Jagex (= "production").
///
/// Resolution order:
///   1. Explicit override: `DARKAN_RECORD_MODE=production|local` always wins.
///   2. Inference: production = (no RSA modulus set) AND (not pointed at a
///      localhost/127.0.0.1 `--configURI`, surfaced as `DARKAN_CONFIG_URI`).
fn server_mode() -> &'static str {
    // 1. Explicit override.
    if let Ok(m) = env::var("DARKAN_RECORD_MODE") {
        match m.trim().to_ascii_lowercase().as_str() {
            "production" | "prod" | "live" => return "production",
            "local" | "custom" => return "local",
            "" => {}
            other => log(&format!(
                "DARKAN_RECORD_MODE={other:?} not recognised — falling back to inference"
            )),
        }
    }

    // 2. Inference. A patcher RSA modulus => the client was redirected local.
    if patcher_redirecting() || config_uri_is_local() {
        "local"
    } else {
        "production"
    }
}

/// True when a sibling-patcher RSA modulus env var is set non-empty (i.e. the
/// patcher has rewritten the client's keys to point it at the local server).
fn patcher_redirecting() -> bool {
    env_set_nonempty("DARKAN_RSA_MODULUS") || env_set_nonempty("DARKAN_JS5_RSA_MODULUS")
}

/// True when the configURI handed to the client targets a local server. The
/// launch script surfaces the URI it passed as `DARKAN_CONFIG_URI` so we can
/// classify even when no modulus is set (e.g. a proxy-mode local launch).
fn config_uri_is_local() -> bool {
    match env::var("DARKAN_CONFIG_URI") {
        Ok(uri) => {
            let u = uri.to_ascii_lowercase();
            u.contains("localhost") || u.contains("127.0.0.1") || u.contains("[::1]")
        }
        Err(_) => false,
    }
}

fn env_set_nonempty(name: &str) -> bool {
    env::var(name)
        .map(|v| !v.trim().is_empty())
        .unwrap_or(false)
}

// -- atexit -------------------------------------------------------------------

extern "C" fn on_exit() {
    // Authoritative END-STATE oracle snapshot FIRST (RE §10): this final snapshot
    // is the ground truth the offline verifier checks our decoded s2c against.
    // Taken before we touch session.json so even a teardown that races other
    // shutdown work still leaves the end-state on disk.
    state::emit_state_snapshot();

    state::flush_session_json(true);
    // Final ISAAC-seed reconciliation + flush: a connection (esp. game/world)
    // may have resolved its cipher pointers only late in the session, so make a
    // last attempt to associate every captured Isaac::Init seed with its
    // {conn, dir} and write any still-unwritten lines to isaac-keys.txt. This
    // guarantees the game seed lands even if the session ended right after world
    // entry with few s2c/c2s emits.
    if let Some(s) = state::session() {
        let seeds = state::isaac_seeds_by_role();
        if !seeds.is_empty() {
            s.isaac_seeds(&seeds);
        }
        s.event_process("exit", serde_json::json!({}));
    }
}

fn install_atexit() {
    unsafe {
        libc::atexit(on_exit);
    }
}
