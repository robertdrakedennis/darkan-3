//! Client control plane for the Undercut engine's hot-reload feature.
//!
//! Once injected, the engine runs a permanent supervisor that listens on a
//! per-pid Unix-domain socket (see `engine-supervisor`'s `ControlSocket`). The
//! launcher's "Clients" panel uses this module to discover running `rs2client`
//! processes and drive uninject/reinject without restarting the client.
//!
//! Protocol (one request line, one response line):
//!   STATUS   -> `OK <state> pid=<p> version=<v> reloads=<n>` (state: active|unloaded|error)
//!   PING     -> `OK pong`
//!   INJECT   -> loads the engine if unloaded -> `OK active version=<v>`
//!   UNINJECT -> tears the engine down        -> `OK unloaded`
//!   REINJECT -> unload + reload the jar       -> `OK active version=<v>`
//!   (unknown -> `ERR <msg>`)
//!
//! The FIRST injection of a process is still the GDB-`dlopen` path (needs
//! elevation) in [`crate::game::inject`]; once the `.so` is mapped, every
//! subsequent control action is a plain socket command with no elevation.

use serde::Serialize;
use std::path::PathBuf;

/// One discovered `rs2client` process and its engine state.
///
/// `state` is one of:
///   - `"not-injected"` — a live `rs2client` with the engine `.so` not yet mapped
///   - `"active"` — engine loaded (from the supervisor's STATUS reply)
///   - `"unloaded"` — engine injected but currently uninjected (vanilla client)
///   - `"error"` — `.so` is mapped but the socket is unreachable, or the
///     supervisor reported an error state
#[derive(Debug, Clone, Serialize)]
pub struct ClientStatus {
    pub pid: u32,
    pub state: String,
    pub version: Option<String>,
    pub reloads: u32,
}

/// Per-pid control-socket path, matching the engine supervisor's
/// `ControlSocket.resolveSocketPath` exactly:
///   - `$XDG_RUNTIME_DIR/undercut/<pid>.sock` when `XDG_RUNTIME_DIR` is set+non-empty
///   - else `/tmp/undercut-<user>/<pid>.sock`
///
/// The engine (a JVM) uses the `user.name` system property for `<user>`, which on
/// Linux is derived from `$USER` (default `"user"`). We mirror that: `$USER`, then
/// `$LOGNAME`, then the `"user"` JVM default.
pub fn socket_path(pid: u32) -> PathBuf {
    let base = match std::env::var("XDG_RUNTIME_DIR") {
        Ok(runtime) if !runtime.is_empty() => PathBuf::from(runtime).join("undercut"),
        _ => {
            let user = std::env::var("USER")
                .ok()
                .filter(|s| !s.is_empty())
                .or_else(|| std::env::var("LOGNAME").ok().filter(|s| !s.is_empty()))
                .unwrap_or_else(|| "user".to_string());
            PathBuf::from("/tmp").join(format!("undercut-{}", user))
        }
    };
    base.join(format!("{}.sock", pid))
}

/// Connect the per-pid control socket, write `cmd\n`, and read exactly one
/// response line. Uses short connect/read/write timeouts so an unresponsive
/// supervisor can never hang the UI dispatch.
///
/// Linux-only (Unix-domain sockets via `std::os::unix::net::UnixStream`).
#[cfg(unix)]
pub fn send_command(pid: u32, cmd: &str) -> anyhow::Result<String> {
    use anyhow::Context;
    use std::io::{BufRead, BufReader, Write};
    use std::os::unix::net::UnixStream;
    use std::time::Duration;

    const TIMEOUT: Duration = Duration::from_secs(5);

    let path = socket_path(pid);
    let stream = UnixStream::connect(&path)
        .with_context(|| format!("connecting to control socket {}", path.display()))?;
    stream.set_read_timeout(Some(TIMEOUT))?;
    stream.set_write_timeout(Some(TIMEOUT))?;

    let mut writer = stream.try_clone().context("cloning control socket")?;
    writer
        .write_all(cmd.as_bytes())
        .context("writing control command")?;
    writer.write_all(b"\n").context("writing command newline")?;
    writer.flush().context("flushing control command")?;

    let mut reader = BufReader::new(stream);
    let mut line = String::new();
    reader
        .read_line(&mut line)
        .context("reading control response")?;
    let line = line.trim_end_matches(['\r', '\n']).to_string();
    if line.is_empty() {
        anyhow::bail!("empty response from control socket");
    }
    Ok(line)
}

#[cfg(not(unix))]
pub fn send_command(pid: u32, cmd: &str) -> anyhow::Result<String> {
    let _ = (pid, cmd);
    anyhow::bail!("control socket is only supported on Unix")
}

/// Scan `/proc` for live `rs2client` processes and report each one's engine
/// state. Reuses the `/proc`-scanning helpers in [`crate::game::inject`] so the
/// rs2client / injected detection stays in one place.
///
/// Per pid:
///   - `.so` not mapped               -> `"not-injected"`
///   - `.so` mapped + STATUS reachable -> parse state/version/reloads from the reply
///   - `.so` mapped + socket missing/unreachable / malformed reply -> `"error"`
#[cfg(all(unix, not(target_os = "macos")))]
pub fn discover_clients() -> Vec<ClientStatus> {
    use crate::game::inject::linux::{is_already_injected, proc_is_rs2client};

    let mut clients = Vec::new();

    let entries = match std::fs::read_dir("/proc") {
        Ok(e) => e,
        Err(e) => {
            log::warn!("discover_clients: cannot read /proc: {}", e);
            return clients;
        }
    };

    for entry in entries.flatten() {
        let pid: u32 = match entry.file_name().to_str().and_then(|s| s.parse().ok()) {
            Some(p) => p,
            None => continue,
        };

        if !proc_is_rs2client(pid) {
            continue;
        }

        if !is_already_injected(pid) {
            clients.push(ClientStatus {
                pid,
                state: "not-injected".to_string(),
                version: None,
                reloads: 0,
            });
            continue;
        }

        // The .so is mapped: ask the supervisor for its current state.
        match send_command(pid, "STATUS") {
            Ok(resp) => clients.push(parse_status(pid, &resp)),
            Err(e) => {
                log::debug!(
                    "discover_clients: pid {} has the engine mapped but STATUS failed: {}",
                    pid,
                    e
                );
                clients.push(ClientStatus {
                    pid,
                    state: "error".to_string(),
                    version: None,
                    reloads: 0,
                });
            }
        }
    }

    clients.sort_by_key(|c| c.pid);
    clients
}

#[cfg(not(all(unix, not(target_os = "macos"))))]
pub fn discover_clients() -> Vec<ClientStatus> {
    Vec::new()
}

/// First-inject the engine into a specific pid via the GDB-`dlopen` path
/// (delegates to [`crate::game::inject::inject_into_pid`]). May block on a
/// pkexec/sudo prompt, so callers must run this off the UI thread.
pub fn inject_pid(pid: u32) -> anyhow::Result<()> {
    crate::game::inject::inject_into_pid(pid)
}

/// True when the engine `.so` is currently mapped into `pid` — i.e. the engine
/// was injected at least once, so control actions go through the socket rather
/// than the GDB first-injection path.
#[cfg(all(unix, not(target_os = "macos")))]
pub fn is_injected(pid: u32) -> bool {
    crate::game::inject::linux::is_already_injected(pid)
}

#[cfg(not(all(unix, not(target_os = "macos"))))]
pub fn is_injected(_pid: u32) -> bool {
    false
}

/// Parse a supervisor `STATUS` reply into a [`ClientStatus`].
///
/// Active/unloaded form: `OK <state> pid=<p> version=<v> reloads=<n>` with an
/// optional trailing ` error=<...>`. Anything that isn't a recognizable `OK`
/// reply (including `ERR ...`) maps to `"error"`.
#[cfg(unix)]
fn parse_status(pid: u32, resp: &str) -> ClientStatus {
    let mut tokens = resp.split_whitespace();

    // Expect `OK <state> ...`; anything else (e.g. `ERR ...`) is an error state.
    if tokens.next() != Some("OK") {
        return ClientStatus {
            pid,
            state: "error".to_string(),
            version: None,
            reloads: 0,
        };
    }

    let state = tokens.next().unwrap_or("error").to_string();

    let mut version: Option<String> = None;
    let mut reloads: u32 = 0;
    for tok in tokens {
        if let Some(v) = tok.strip_prefix("version=") {
            // The supervisor sends `-` for an unloaded engine; treat as None.
            version = if v == "-" || v.is_empty() {
                None
            } else {
                Some(v.to_string())
            };
        } else if let Some(n) = tok.strip_prefix("reloads=") {
            reloads = n.parse().unwrap_or(0);
        }
    }

    ClientStatus {
        pid,
        state,
        version,
        reloads,
    }
}
