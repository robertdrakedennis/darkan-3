//! Optional Undercut engine injection after the client is spawned.
//!
//! Mirrors `launch/run-undercut.sh`'s `inject_engine()`: once `rs2client` is up
//! and initialized, GDB-`dlopen` `libundercutbootstrap.so` into it so the engine
//! (funchook hooks, ImGui overlay, in-process MCP, TcpIn sniffer) loads in-process.
//!
//! This is **Linux-only** (the mechanism is GDB `dlopen`). On every other
//! platform `maybe_inject_undercut` is a no-op that just logs.
//!
//! Everything here is best-effort: a failure to locate the `.so`, attach with
//! GDB, or obtain privileges logs a warning and returns — it never blocks or
//! kills the client launch.

/// Kick off Undercut engine injection for a freshly-spawned client.
///
/// `launcher_pid` is the pid the launcher spawned (`rs3linux` on Linux). The real
/// game process is its `rs2client` descendant, so the injector resolves the
/// actual target by scanning `/proc` for the newest non-injected `rs2client`,
/// preferring one descended from `launcher_pid`.
///
/// The wait + inject runs on a detached background thread so the launcher UI is
/// never blocked for the init delay.
pub fn maybe_inject_undercut(launcher_pid: u32) {
    #[cfg(all(unix, not(target_os = "macos")))]
    {
        std::thread::Builder::new()
            .name("undercut-inject".to_string())
            .spawn(move || linux::inject(launcher_pid))
            .map(|_| ())
            .unwrap_or_else(|e| {
                log::warn!("Failed to spawn Undercut injection thread: {}", e);
            });
    }

    #[cfg(not(all(unix, not(target_os = "macos"))))]
    {
        let _ = launcher_pid;
        log::warn!(
            "Auto-inject Undercut is enabled but only supported on Linux; skipping injection."
        );
    }
}

/// Inject the Undercut engine into a SPECIFIC, already-running `rs2client` pid via
/// the GDB-`dlopen` path. Unlike [`maybe_inject_undercut`], this does not scan for
/// the newest descendant — the caller picks the exact target (e.g. the Clients
/// panel injecting one row). Runs synchronously and may block on a pkexec/sudo
/// prompt, so callers must invoke it off the UI thread.
///
/// Linux-only; on every other platform this is an error (the mechanism is GDB
/// `dlopen`).
#[cfg(all(unix, not(target_os = "macos")))]
pub fn inject_into_pid(pid: u32) -> anyhow::Result<()> {
    linux::inject_pid(pid)
}

#[cfg(not(all(unix, not(target_os = "macos"))))]
pub fn inject_into_pid(pid: u32) -> anyhow::Result<()> {
    let _ = pid;
    anyhow::bail!("Undercut engine injection is only supported on Linux")
}

#[cfg(all(unix, not(target_os = "macos")))]
pub(crate) mod linux {
    use std::path::{Path, PathBuf};
    use std::process::{Command, Stdio};
    use std::time::Duration;

    pub(crate) const ENGINE_SO_NAME: &str = "libundercutbootstrap.so";
    /// `dlopen` flags used by `engine/inject` / run-undercut.sh:
    /// `RTLD_NOW | RTLD_GLOBAL | RTLD_NODELETE` == 0x2 | 0x100 | 0x1000 == 4362.
    const DLOPEN_FLAGS: i32 = 4362;
    const DEFAULT_INJECT_DELAY_SECS: u64 = 8;

    /// Resolved injection inputs: the absolute `.so` path, the JDK home, and the
    /// `UNDERCUT_HOME_DIR` (the directory containing the `.so`). Shared by both the
    /// auto-inject (newest-descendant) path and the per-pid path so the env
    /// resolution lives in one place.
    struct InjectEnv {
        engine_so: PathBuf,
        java_home: String,
        home_dir: PathBuf,
    }

    /// Locate the `.so`, canonicalize it (pkexec/sudo reset cwd, so the path handed
    /// to gdb must be absolute), and read JAVA_HOME. Returns an error string the
    /// caller can log or surface.
    fn resolve_inject_env() -> anyhow::Result<InjectEnv> {
        use anyhow::anyhow;

        let engine_so = locate_engine_so().ok_or_else(|| {
            anyhow!(
                "{} not found. Build it with `./gradlew :engine:buildNativeBootstrap`.",
                ENGINE_SO_NAME
            )
        })?;
        let engine_so = std::fs::canonicalize(&engine_so).unwrap_or(engine_so);

        let java_home = match std::env::var("JAVA_HOME") {
            Ok(v) if !v.is_empty() => v,
            _ => {
                return Err(anyhow!(
                    "JAVA_HOME is unset; the engine needs a JDK 25 home."
                ))
            }
        };

        // UNDERCUT_HOME_DIR = the directory containing the .so (matches run-undercut.sh).
        let home_dir = engine_so
            .parent()
            .map(|p| p.to_path_buf())
            .unwrap_or_else(|| PathBuf::from("."));

        Ok(InjectEnv {
            engine_so,
            java_home,
            home_dir,
        })
    }

    pub fn inject(launcher_pid: u32) {
        let env = match resolve_inject_env() {
            Ok(e) => e,
            Err(e) => {
                log::warn!("Auto-inject enabled but {}. Skipping injection.", e);
                return;
            }
        };

        let delay = inject_delay();
        log::info!(
            "Auto-inject: waiting {}s for the client to initialize before injecting Undercut",
            delay.as_secs()
        );
        std::thread::sleep(delay);

        let target_pid = match resolve_target_pid(launcher_pid) {
            Some(p) => p,
            None => {
                log::warn!(
                    "Auto-inject: no live, non-injected rs2client process found; skipping."
                );
                return;
            }
        };

        if is_already_injected(target_pid) {
            log::info!(
                "Auto-inject: {} already mapped in pid {}; skipping.",
                ENGINE_SO_NAME, target_pid
            );
            return;
        }

        log::info!(
            "Auto-inject: injecting {} into rs2client pid {} (UNDERCUT_HOME_DIR={})",
            env.engine_so.display(),
            target_pid,
            env.home_dir.display()
        );

        match run_gdb_inject(target_pid, &env.engine_so, &env.java_home, &env.home_dir) {
            Ok(()) => log::info!("Auto-inject: Undercut engine injected into pid {}", target_pid),
            Err(e) => log::warn!("Auto-inject: injection failed (continuing): {}", e),
        }
    }

    /// First-inject into a SPECIFIC pid. Unlike [`inject`], this does not wait or
    /// scan for a descendant — the caller already chose the exact target. Returns
    /// an error so the UI can report failure (the auto path only logs).
    pub fn inject_pid(pid: u32) -> anyhow::Result<()> {
        use anyhow::{anyhow, bail};

        if !proc_is_rs2client(pid) {
            bail!("pid {} is not a live rs2client process", pid);
        }
        if is_already_injected(pid) {
            // Already mapped — the engine is in-process; the caller should drive
            // reinject/uninject via the control socket, not GDB.
            return Ok(());
        }

        let env = resolve_inject_env().map_err(|e| anyhow!("{}", e))?;

        log::info!(
            "Inject: injecting {} into rs2client pid {} (UNDERCUT_HOME_DIR={})",
            env.engine_so.display(),
            pid,
            env.home_dir.display()
        );

        run_gdb_inject(pid, &env.engine_so, &env.java_home, &env.home_dir)
    }

    fn inject_delay() -> Duration {
        let secs = std::env::var("INJECT_DELAY")
            .ok()
            .and_then(|v| v.parse::<u64>().ok())
            .unwrap_or(DEFAULT_INJECT_DELAY_SECS);
        Duration::from_secs(secs)
    }

    /// Locate `libundercutbootstrap.so`. The launcher may run from anywhere, so
    /// check several sensible slots in order:
    /// 1. `$UNDERCUT_HOME_DIR/<so>` if the env var is set
    /// 2. relative to the current working dir: `engine/build/libs/<so>`
    /// 3. walk up from the launcher exe looking for `engine/build/libs/<so>`
    ///    (handles `client/launcher/target/<profile>/bolt-rs3` dev layouts and
    ///    deploys nested under the repo)
    fn locate_engine_so() -> Option<PathBuf> {
        if let Ok(home) = std::env::var("UNDERCUT_HOME_DIR") {
            if !home.is_empty() {
                let candidate = Path::new(&home).join(ENGINE_SO_NAME);
                if candidate.is_file() {
                    return Some(candidate);
                }
            }
        }

        let rel = Path::new("engine").join("build").join("libs").join(ENGINE_SO_NAME);

        if let Ok(cwd) = std::env::current_dir() {
            let candidate = cwd.join(&rel);
            if candidate.is_file() {
                return Some(candidate);
            }
        }

        if let Ok(exe) = std::env::current_exe() {
            let mut dir = exe.parent();
            while let Some(d) = dir {
                let candidate = d.join(&rel);
                if candidate.is_file() {
                    return Some(candidate);
                }
                dir = d.parent();
            }
        }

        None
    }

    /// Find the rs2client pid to inject into. Scans `/proc` for the newest
    /// `rs2client` process that is not already injected, preferring one that is a
    /// descendant of `launcher_pid` (the rs3linux we spawned).
    fn resolve_target_pid(launcher_pid: u32) -> Option<u32> {
        let mut best: Option<(u32, u64)> = None; // (pid, start_time)
        let mut best_descendant: Option<(u32, u64)> = None;

        let entries = std::fs::read_dir("/proc").ok()?;
        for entry in entries.flatten() {
            let name = entry.file_name();
            let pid: u32 = match name.to_str().and_then(|s| s.parse().ok()) {
                Some(p) => p,
                None => continue,
            };

            if !proc_is_rs2client(pid) {
                continue;
            }
            if is_already_injected(pid) {
                continue;
            }

            let start = proc_start_time(pid).unwrap_or(0);

            if is_descendant_of(pid, launcher_pid) {
                if best_descendant.map(|(_, s)| start >= s).unwrap_or(true) {
                    best_descendant = Some((pid, start));
                }
            }
            if best.map(|(_, s)| start >= s).unwrap_or(true) {
                best = Some((pid, start));
            }
        }

        best_descendant.or(best).map(|(pid, _)| pid)
    }

    pub(crate) fn proc_is_rs2client(pid: u32) -> bool {
        let cmdline = match std::fs::read(format!("/proc/{}/cmdline", pid)) {
            Ok(c) => c,
            Err(_) => return false,
        };
        // cmdline is NUL-separated; argv[0] is the executable path.
        cmdline
            .split(|&b| b == 0)
            .next()
            .map(|arg0| String::from_utf8_lossy(arg0).contains("rs2client"))
            .unwrap_or(false)
    }

    pub(crate) fn is_already_injected(pid: u32) -> bool {
        match std::fs::read_to_string(format!("/proc/{}/maps", pid)) {
            Ok(maps) => maps.contains(ENGINE_SO_NAME),
            Err(_) => false,
        }
    }

    fn proc_start_time(pid: u32) -> Option<u64> {
        let meta = std::fs::metadata(format!("/proc/{}", pid)).ok()?;
        use std::os::unix::fs::MetadataExt;
        Some(meta.mtime() as u64)
    }

    /// Walk the `PPid` chain of `pid` up to a small depth, returning true if
    /// `ancestor` is reached. Cheap guard against picking an unrelated rs2client.
    fn is_descendant_of(pid: u32, ancestor: u32) -> bool {
        let mut current = pid;
        for _ in 0..8 {
            if current == ancestor {
                return true;
            }
            match proc_ppid(current) {
                Some(0) | None => return false,
                Some(ppid) => current = ppid,
            }
        }
        false
    }

    fn proc_ppid(pid: u32) -> Option<u32> {
        let status = std::fs::read_to_string(format!("/proc/{}/status", pid)).ok()?;
        for line in status.lines() {
            if let Some(rest) = line.strip_prefix("PPid:") {
                return rest.trim().parse().ok();
            }
        }
        None
    }

    /// Invoke GDB to `dlopen` the engine `.so` into the target.
    ///
    /// Attaching to an already-running, non-child process needs privileges
    /// (`ptrace_scope` is typically `1`). Escalation order, preferring the most
    /// user-friendly automatic prompt:
    ///   1. plain `gdb` when `ptrace_scope == 0` (no elevation needed);
    ///   2. `pkexec gdb` — PolicyKit pops a GRAPHICAL password dialog that works
    ///      in a GUI app with no TTY (the desktop polkit agent renders it);
    ///   3. `sudo -n gdb` — non-interactive fallback for NOPASSWD/headless setups.
    ///
    /// On total failure we report cleanly (never hang on a prompt, never kill the
    /// launch). pkexec/sudo reset cwd+env, but the `.so` is passed as an absolute
    /// path and the target's JAVA_HOME/UNDERCUT_HOME_DIR are set via gdb `setenv`
    /// calls inside the target, so the reset is harmless.
    fn run_gdb_inject(
        pid: u32,
        engine_so: &Path,
        java_home: &str,
        home_dir: &Path,
    ) -> anyhow::Result<()> {
        use anyhow::{anyhow, Context};

        let so = engine_so.to_string_lossy().to_string();
        let home = home_dir.to_string_lossy().to_string();

        let gdb_args = |program: &str, extra: &[&str]| -> Vec<String> {
            let mut v: Vec<String> = extra.iter().map(|s| s.to_string()).collect();
            v.push(program.to_string());
            v.extend([
                "-p".to_string(),
                pid.to_string(),
                "-batch".to_string(),
                "-ex".to_string(),
                format!("call (int) setenv(\"JAVA_HOME\", \"{}\", 1)", java_home),
                "-ex".to_string(),
                format!("call (int) setenv(\"UNDERCUT_HOME_DIR\", \"{}\", 1)", home),
                "-ex".to_string(),
                format!("call (void*) dlopen(\"{}\", {})", so, DLOPEN_FLAGS),
                "-ex".to_string(),
                "call (char*) dlerror()".to_string(),
                "-ex".to_string(),
                "detach".to_string(),
                "-ex".to_string(),
                "quit".to_string(),
            ]);
            v
        };

        // Attempt 1: plain gdb, only worthwhile when ptrace is unrestricted.
        if ptrace_scope_allows_plain() {
            log::info!("Auto-inject: ptrace_scope permits unprivileged attach; trying plain gdb");
            let args = gdb_args("gdb", &[]);
            match run_command(&args[0], &args[1..]) {
                Ok(true) => return Ok(()),
                Ok(false) => log::warn!("Auto-inject: plain gdb attach failed; escalating via pkexec"),
                Err(e) => log::warn!("Auto-inject: plain gdb not runnable ({}); escalating via pkexec", e),
            }
        }

        // Attempt 2: pkexec gdb — PolicyKit shows a GRAPHICAL auth dialog via the
        // desktop's polkit agent, which works in a GUI app with no controlling
        // terminal. This is the user-friendly elevation path.
        let args = gdb_args("gdb", &["pkexec"]);
        match run_command(&args[0], &args[1..]) {
            Ok(true) => return Ok(()),
            Ok(false) => log::warn!(
                "Auto-inject: pkexec gdb did not succeed (auth dismissed, or attach rejected); \
                 trying sudo -n gdb"
            ),
            Err(e) => log::warn!("Auto-inject: pkexec unavailable ({}); trying sudo -n gdb", e),
        }

        // Attempt 3: sudo -n gdb (non-interactive — for NOPASSWD/headless setups;
        // -n makes sudo fail fast instead of hanging on a TTY-less password prompt).
        let args = gdb_args("gdb", &["sudo", "-n"]);
        match run_command(&args[0], &args[1..]) {
            Ok(true) => Ok(()),
            Ok(false) => Err(anyhow!(
                "Engine injection could not get privileges: the pkexec prompt was \
                 dismissed/unavailable and `sudo -n gdb` failed. Install a PolicyKit \
                 agent for a graphical prompt, add a NOPASSWD sudoers rule for gdb, or \
                 relax /proc/sys/kernel/yama/ptrace_scope."
            )),
            Err(e) => Err(e).context("Failed to run sudo -n gdb for injection"),
        }
    }

    fn ptrace_scope_allows_plain() -> bool {
        std::fs::read_to_string("/proc/sys/kernel/yama/ptrace_scope")
            .map(|s| s.trim() == "0")
            .unwrap_or(false)
    }

    /// Run a command to completion, returning Ok(true) on a zero exit status.
    /// stdin is `/dev/null` so `sudo -n` can never stall waiting for input.
    fn run_command(program: &str, args: &[String]) -> anyhow::Result<bool> {
        use anyhow::Context;
        let status = Command::new(program)
            .args(args)
            .stdin(Stdio::null())
            .stdout(Stdio::null())
            .stderr(Stdio::null())
            .status()
            .with_context(|| format!("Failed to spawn {}", program))?;
        Ok(status.success())
    }
}
