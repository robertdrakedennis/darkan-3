use anyhow::{Context, Result};
use std::path::Path;
use std::process::{Command, Stdio};

pub struct LaunchParams<'a> {
    pub session_id: &'a str,
    pub account_id: &'a str,
    pub display_name: &'a str,
}

/// Launch the RS3 client binary
///
/// If `rsa_modulus` is provided (hex-encoded 1024-bit modulus), the patcher
/// shared library will be loaded into the client to replace its embedded RSA
/// public key with the provided one. The mechanism is platform-specific:
///   * Linux  — LD_PRELOAD on the `rs3linux` launcher process; the env var
///     propagates to the child `rs2client` process and the dynamic linker
///     loads `libdarkan_patcher.so` before any client code runs.
///   * Windows — the launcher invokes `darkan_injector.exe <binary> [args...]`,
///     which spawns the target suspended and remote-LoadLibraryW's
///     `darkan_patcher.dll` into it before resuming. The injector inherits the
///     env block we set here, and the DLL is env-gated on `DARKAN_RSA_MODULUS`.
///
/// Returns the spawned process id on success so callers can drive follow-up work
/// against the exact child (e.g. Undercut engine injection) rather than scanning
/// `/proc`.
pub fn launch_rs3(
    binary: &Path,
    config_uri: &str,
    params: Option<&LaunchParams>,
    data_dir: &Path,
    custom_command: Option<&str>,
    rsa_modulus: Option<&str>,
    working_dir: Option<&Path>,
) -> Result<u32> {
    let binary_str = binary.to_string_lossy().to_string();
    let data_dir_str = data_dir.to_string_lossy().to_string();
    let needs_patcher = rsa_modulus.is_some();

    // Decide on the program + args to run. On a normal launch we run the
    // target binary directly with `--configURI <uri>`. A custom_command
    // overrides this with the user's launch wrapper (e.g. `gamemoderun
    // %command%`), expanding %command% to the default argv.
    let target_argv: Vec<String> = if let Some(launch_cmd) = custom_command {
        resolve_launch_command(launch_cmd, &binary_str, config_uri)
    } else {
        vec![
            binary_str.clone(),
            "--configURI".to_string(),
            config_uri.to_string(),
        ]
    };

    // Build the actual Command. On Windows in patch mode we substitute the
    // injector as argv[0] and shift the real argv into the injector's args;
    // the injector spawns the target itself. Otherwise we spawn target_argv
    // directly. Each platform builder also wires up its own patcher env
    // (LD_PRELOAD + DARKAN_* on Linux when the .so is found, DARKAN_* always
    // on Windows when the injector is found — preserves historical gating).
    #[cfg(windows)]
    let mut cmd = build_windows_command(&target_argv, needs_patcher, rsa_modulus);
    #[cfg(target_os = "macos")]
    let mut cmd = build_macos_command(&target_argv, binary, needs_patcher, rsa_modulus);
    #[cfg(all(unix, not(target_os = "macos")))]
    let mut cmd = build_unix_command(&target_argv, binary, needs_patcher, rsa_modulus);
    #[cfg(not(any(windows, unix)))]
    let mut cmd = {
        let _ = needs_patcher;
        let _ = rsa_modulus;
        let mut c = Command::new(&target_argv[0]);
        for a in &target_argv[1..] {
            c.arg(a);
        }
        c
    };

    // Set working directory if provided
    if let Some(dir) = working_dir {
        cmd.current_dir(dir);
    }

    // Set environment variables consumed by the client itself (Jagex's auth
    // hand-off) — unchanged across platforms.
    if let Some(p) = params {
        cmd.env("JX_SESSION_ID", p.session_id);
        cmd.env("JX_CHARACTER_ID", p.account_id);
        cmd.env("JX_DISPLAY_NAME", p.display_name);
    }

    // Linux-only client-runtime env (X11/PulseAudio hints + HOME redirect so
    // the NXT client lands in our data dir, not the user's real ~).
    #[cfg(unix)]
    {
        cmd.env("HOME", &data_dir_str);
        cmd.env("SDL_VIDEODRIVER", "x11");
        cmd.env("SDL_VIDEO_X11_WMCLASS", "RuneScape");
        cmd.env(
            "PULSE_PROP_OVERRIDE",
            "application.name='RuneScape' application.icon_name='runescape' media.role='game'",
        );
    }
    #[cfg(not(unix))]
    let _ = data_dir_str;

    cmd.stdin(Stdio::null());

    let child = cmd.spawn().context("Failed to spawn RS3 process")?;
    let pid = child.id();
    log::info!("Spawned RS3 process with pid {}", pid);

    Ok(pid)
}

/// Linux command construction. Spawns the target argv directly and, if the
/// patcher is needed AND the .so is locatable, sets LD_PRELOAD plus the
/// DARKAN_* env vars the .so reads. The env var propagates to the
/// rs3linux→rs2client child chain via the dynamic linker.
///
/// Preserves the historical gating exactly: DARKAN_RSA_MODULUS is only set when
/// the patcher library is found, so behavior is unchanged for any existing Linux
/// deploy.
#[cfg(all(unix, not(target_os = "macos")))]
fn build_unix_command(
    target_argv: &[String],
    binary: &Path,
    needs_patcher: bool,
    rsa_modulus: Option<&str>,
) -> Command {
    let mut cmd = Command::new(&target_argv[0]);
    for arg in &target_argv[1..] {
        cmd.arg(arg);
    }
    if needs_patcher {
        if let Some(patcher_path) = find_patcher_library(binary) {
            log::info!("Setting LD_PRELOAD to {}", patcher_path.display());
            cmd.env("LD_PRELOAD", &patcher_path);
            if let Some(modulus) = rsa_modulus {
                cmd.env("DARKAN_RSA_MODULUS", modulus);
            }
        } else {
            log::warn!(
                "Patcher needed but libdarkan_patcher.so not found. \
                 Build it with: cd client/launcher/patcher && cargo build --release"
            );
        }
    }
    cmd
}

/// macOS command construction. Mirrors `build_unix_command` but uses the dyld
/// equivalents of LD_PRELOAD:
///   * `DYLD_INSERT_LIBRARIES` — the macOS preload mechanism (= LD_PRELOAD).
///   * `DYLD_FORCE_FLAT_NAMESPACE=1` — required so the injected dylib's symbols
///     participate in the flat lookup; without it, two-level-namespace binaries
///     (rs2client is `TWOLEVEL`) won't honour interposition and, more relevant
///     here, the dylib's `__attribute__((constructor))` may not run reliably for
///     all preload styles. We only need the constructor to fire (the patcher
///     scans memory in its ctor, it does not interpose libc symbols), but
///     forcing the flat namespace is the documented, robust way to guarantee
///     the insert library is initialised before the host's main().
///
/// The env var name differs from Linux, but the DARKAN_* gating contract is
/// identical: they are only set when the dylib is actually located, so an
/// un-patched (live) launch is the safe fallback when the dylib is missing.
#[cfg(target_os = "macos")]
fn build_macos_command(
    target_argv: &[String],
    binary: &Path,
    needs_patcher: bool,
    rsa_modulus: Option<&str>,
) -> Command {
    let mut cmd = Command::new(&target_argv[0]);
    for arg in &target_argv[1..] {
        cmd.arg(arg);
    }
    if needs_patcher {
        if let Some(patcher_path) = find_patcher_library(binary) {
            log::info!(
                "Setting DYLD_INSERT_LIBRARIES to {}",
                patcher_path.display()
            );
            cmd.env("DYLD_INSERT_LIBRARIES", &patcher_path);
            cmd.env("DYLD_FORCE_FLAT_NAMESPACE", "1");
            if let Some(modulus) = rsa_modulus {
                cmd.env("DARKAN_RSA_MODULUS", modulus);
            }
        } else {
            log::warn!(
                "Patcher needed but libdarkan_patcher.dylib not found. \
                 Build it with: cd client/launcher/patcher-mac && cargo build --release"
            );
        }
    }
    cmd
}

/// Windows command construction. In live/unpatched mode we spawn the target
/// argv directly. In patched mode we look up `darkan_injector.exe` and rewrite
/// the command into `injector.exe <target.exe> [target args...]`. The injector
/// inherits our env block, including DARKAN_* vars set by the caller above.
///
/// If the injector cannot be found we fall back to spawning the target
/// directly and emit a warning — same shape as the Linux missing-patcher path
/// (unpatched launch beats no launch at all).
#[cfg(windows)]
fn build_windows_command(
    target_argv: &[String],
    needs_patcher: bool,
    rsa_modulus: Option<&str>,
) -> Command {
    if needs_patcher {
        if let Some(injector) = find_injector_exe() {
            log::info!("Using DLL injector at {}", injector.display());
            // Diagnostic: surface whether the DLL is locatable from the
            // launcher's perspective. The injector does its own lookup at
            // runtime (and may find it in a slot we don't check), so a None
            // here is only a hint, not an error.
            if let Some(dll) = find_patcher_dll() {
                log::info!("Patcher DLL located at {}", dll.display());
            } else {
                log::warn!(
                    "darkan_patcher.dll not visible to the launcher's search; \
                     the injector will perform its own search at spawn time."
                );
            }
            let mut cmd = Command::new(&injector);
            // injector usage: darkan_injector.exe <path-to-rs2client.exe> [args...]
            // target_argv[0] is the client exe path; target_argv[1..] are its args.
            cmd.arg(&target_argv[0]);
            for arg in &target_argv[1..] {
                cmd.arg(arg);
            }
            // DARKAN_* are inherited by the injector and forwarded into the
            // suspended target by CreateProcessW's default env block. The DLL
            // is no-op without DARKAN_RSA_MODULUS, so omitting it = unpatched
            // run from the DLL's perspective even if injection succeeded.
            if let Some(modulus) = rsa_modulus {
                cmd.env("DARKAN_RSA_MODULUS", modulus);
            }
            return cmd;
        } else {
            log::warn!(
                "Patcher needed but darkan_injector.exe not found in any known \
                 location. Launching un-patched. Build it with: \
                 cd client/launcher/patcher-win && cargo build --release"
            );
        }
    }

    // Unpatched / live mode (or injector missing): spawn target directly.
    let mut cmd = Command::new(&target_argv[0]);
    for arg in &target_argv[1..] {
        cmd.arg(arg);
    }
    cmd
}

/// The host OS folder name under `data/client/` for the current platform.
///
/// The per-OS data layout (introduced alongside the Kotlin client-manager) is:
///   data/client/linux/    rs2client     rs3linux       libdarkan_patcher.so
///   data/client/windows/  rs2client.exe rs3windows.exe darkan_patcher.dll + darkan_injector.exe
///   data/client/macos/    rs2client     rs3mac         libdarkan_patcher.dylib
///
/// The launcher always operates on the host's folder — there is no manual
/// override; we cross-compile per target and auto-detect at runtime.
pub fn host_os_dir() -> &'static str {
    if cfg!(target_os = "windows") {
        "windows"
    } else if cfg!(target_os = "macos") {
        "macos"
    } else {
        "linux"
    }
}

/// The host's patcher shared-library file name for the current platform.
///   linux   → libdarkan_patcher.so
///   windows → darkan_patcher.dll
///   macos   → libdarkan_patcher.dylib
pub fn patcher_lib_name() -> &'static str {
    if cfg!(target_os = "windows") {
        "darkan_patcher.dll"
    } else if cfg!(target_os = "macos") {
        "libdarkan_patcher.dylib"
    } else {
        "libdarkan_patcher.so"
    }
}

/// Determine the Jagex launcher (`rs3*`) binary name for the current platform.
///   linux   → rs3linux
///   windows → rs3windows.exe
///   macos   → rs3mac
pub fn launcher_binary_name() -> &'static str {
    if cfg!(target_os = "windows") {
        "rs3windows.exe"
    } else if cfg!(target_os = "macos") {
        "rs3mac"
    } else {
        "rs3linux"
    }
}

/// Locate the host's patcher shared library on a unix host.
///   Linux → `libdarkan_patcher.so`   macOS → `libdarkan_patcher.dylib`
///
/// Search order:
/// 1. `data/client/<host-os>/` relative to the launcher's working dir — the new
///    canonical per-OS layout, checked first so a fresh per-OS build always wins.
/// 2. Next to the launcher executable itself
/// 3. In the same directory as the client binary
/// 4. ~/darkan-3/ (project runtime directory used by custom mode)
/// 5. In ../patcher{,-mac}/target/release/ relative to the launcher exe (dev builds)
///
/// On Windows this returns None — the equivalent DLL lookup is exposed via
/// `find_patcher_dll`, which is a diagnostic affordance only. The Windows
/// runtime patcher is loaded by `darkan_injector.exe`, which performs its own
/// independent DLL search at injection time.
#[cfg(unix)]
pub fn find_patcher_library(client_binary: &Path) -> Option<std::path::PathBuf> {
    let lib_name = patcher_lib_name();

    // Canonical per-OS layout: data/client/<host-os>/<lib>. This is the slot the
    // user requires the launcher to load from at launch time.
    let os_slot = std::path::PathBuf::from("data")
        .join("client")
        .join(host_os_dir())
        .join(lib_name);
    if os_slot.exists() {
        return Some(os_slot);
    }

    // Next to the launcher executable
    if let Ok(exe) = std::env::current_exe() {
        if let Some(parent) = exe.parent() {
            let candidate = parent.join(lib_name);
            if candidate.exists() {
                return Some(candidate);
            }
        }
    }

    // Next to the client binary
    if let Some(parent) = client_binary.parent() {
        let candidate = parent.join(lib_name);
        if candidate.exists() {
            return Some(candidate);
        }
    }

    // ~/darkan-3/ — the project runtime directory used by custom mode
    if let Ok(home) = std::env::var("HOME") {
        let candidate = std::path::PathBuf::from(home).join("darkan-3").join(lib_name);
        if candidate.exists() {
            return Some(candidate);
        }
    }

    // Dev build location: relative to launcher exe at ../patcher{,-mac}/target/release/.
    // macOS dylibs come from the `patcher-mac` crate, Linux .so from `patcher`.
    let dev_crate = if cfg!(target_os = "macos") {
        "patcher-mac"
    } else {
        "patcher"
    };
    if let Ok(exe) = std::env::current_exe() {
        if let Some(parent) = exe.parent() {
            let candidate = parent
                .join("..")
                .join("..")
                .join("..")
                .join(dev_crate)
                .join("target")
                .join("release")
                .join(lib_name);
            if candidate.exists() {
                return Some(candidate);
            }
        }
    }

    None
}

#[cfg(windows)]
pub fn find_patcher_library(_client_binary: &Path) -> Option<std::path::PathBuf> {
    // Windows uses CreateRemoteThread+LoadLibraryW via darkan_injector.exe.
    // Direct preloading is not how it gets into the process. Surface the
    // DLL via find_patcher_dll() for diagnostics, but don't pretend it's
    // a preload candidate.
    None
}

/// Locate `darkan_injector.exe` for runtime DLL injection into `rs2client.exe`.
///
/// Returns the first hit from the canonical search slots, in order. On miss,
/// logs every slot we tried so the user can see where to drop the binary.
///
/// Slot order:
/// 1. Next to the running darkan-launcher executable (release deploy layout)
/// 2. The launcher's data dir (ProjectDirs root for `darkan-launcher`)
/// 3. `<data_dir>\Jagex\launcher\` (matches Jagex's installed-launcher layout)
/// 4. `%APPDATA%\Darkan3\`
/// 5. `%LOCALAPPDATA%\Darkan3\`
/// 6. Dev convenience: `<project tree>\client\launcher\patcher-win\target\release\`
///    — only searched in debug builds or when `DARKAN_DEV` is set, so release
///    artifacts never leak a dependency on the source tree.
#[cfg(windows)]
pub fn find_injector_exe() -> Option<std::path::PathBuf> {
    let exe_name = "darkan_injector.exe";
    let candidates = injector_search_slots(exe_name);
    for c in &candidates {
        if c.is_file() {
            log::debug!("found {}: {}", exe_name, c.display());
            return Some(c.clone());
        }
    }
    log::warn!(
        "{} not found in any of:\n  {}",
        exe_name,
        candidates
            .iter()
            .map(|p| p.display().to_string())
            .collect::<Vec<_>>()
            .join("\n  ")
    );
    None
}

/// Diagnostic helper: locate `darkan_patcher.dll` from the launcher's
/// perspective so we can log it for the user. The injector does its own
/// independent search at spawn time — this is not authoritative.
#[cfg(windows)]
pub fn find_patcher_dll() -> Option<std::path::PathBuf> {
    let dll_name = "darkan_patcher.dll";
    let candidates = injector_search_slots(dll_name);
    for c in &candidates {
        if c.is_file() {
            return Some(c.clone());
        }
    }
    None
}

/// Build the canonical Windows search list for an artifact name. Used for both
/// the injector exe and the patcher DLL — they ship side by side in every
/// deploy slot, so they share a lookup.
#[cfg(windows)]
fn injector_search_slots(name: &str) -> Vec<std::path::PathBuf> {
    let mut slots: Vec<std::path::PathBuf> = Vec::new();

    // 1. Same dir as the running launcher exe
    if let Ok(exe) = std::env::current_exe() {
        if let Some(dir) = exe.parent() {
            slots.push(dir.join(name));
        }
    }

    // 2 & 3. ProjectDirs-based slots (matches what config.rs uses for the
    // launcher's own data dir).
    if let Some(dirs) = directories::ProjectDirs::from("", "", "darkan-launcher") {
        slots.push(dirs.data_dir().join(name));
        slots.push(dirs.data_dir().join("Jagex").join("launcher").join(name));
    }

    // 4. %APPDATA%\Darkan3\
    if let Ok(appdata) = std::env::var("APPDATA") {
        slots.push(std::path::PathBuf::from(appdata).join("Darkan3").join(name));
    }
    // 5. %LOCALAPPDATA%\Darkan3\
    if let Ok(local) = std::env::var("LOCALAPPDATA") {
        slots.push(std::path::PathBuf::from(local).join("Darkan3").join(name));
    }

    // 6. Dev convenience: walk up from the launcher exe into the source tree
    // and check the patcher-win release artifacts directory. Gated so release
    // deployments don't even try (avoids spurious paths in the failure log).
    let dev_enabled = cfg!(debug_assertions) || std::env::var_os("DARKAN_DEV").is_some();
    if dev_enabled {
        if let Ok(exe) = std::env::current_exe() {
            if let Some(parent) = exe.parent() {
                // Typical dev layout: <repo>\client\launcher\target\<profile>\darkan-launcher.exe
                // so go up to <repo>\client\launcher\ then into patcher-win\target\release.
                slots.push(
                    parent
                        .join("..")
                        .join("..")
                        .join("patcher-win")
                        .join("target")
                        .join("release")
                        .join(name),
                );
            }
        }
    }

    slots
}

fn resolve_launch_command(launch_cmd: &str, binary: &str, config_uri: &str) -> Vec<String> {
    let mut result = Vec::new();
    let default_args = vec![binary.to_string(), "--configURI".to_string(), config_uri.to_string()];

    for part in launch_cmd.split_whitespace() {
        if part == "%command%" {
            result.extend(default_args.clone());
        } else {
            result.push(part.to_string());
        }
    }

    if result.is_empty() {
        result = default_args;
    }

    result
}

/// Open a URL in the default browser via xdg-open
pub fn open_url(url: &str) -> Result<()> {
    Command::new("xdg-open")
        .arg(url)
        .stdin(Stdio::null())
        .stdout(Stdio::null())
        .stderr(Stdio::null())
        .spawn()
        .context("Failed to open URL with xdg-open")?;
    Ok(())
}
