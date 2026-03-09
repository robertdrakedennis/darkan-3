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
/// shared library will be loaded via LD_PRELOAD to replace the client's
/// embedded RSA public key with the provided one.
pub fn launch_rs3(
    binary: &Path,
    config_uri: &str,
    params: Option<&LaunchParams>,
    data_dir: &Path,
    custom_command: Option<&str>,
    rsa_modulus: Option<&str>,
    working_dir: Option<&Path>,
) -> Result<()> {
    let binary_str = binary.to_string_lossy().to_string();
    let data_dir_str = data_dir.to_string_lossy().to_string();

    let mut cmd = if let Some(launch_cmd) = custom_command {
        let parts = resolve_launch_command(launch_cmd, &binary_str, config_uri);
        let mut cmd = Command::new(&parts[0]);
        for arg in &parts[1..] {
            cmd.arg(arg);
        }
        cmd
    } else {
        let mut cmd = Command::new(&binary_str);
        cmd.arg("--configURI").arg(config_uri);
        cmd
    };

    // Set working directory if provided
    if let Some(dir) = working_dir {
        cmd.current_dir(dir);
    }

    // Set environment variables
    if let Some(p) = params {
        cmd.env("JX_SESSION_ID", p.session_id);
        cmd.env("JX_CHARACTER_ID", p.account_id);
        cmd.env("JX_DISPLAY_NAME", p.display_name);
    }

    // Set up LD_PRELOAD for RSA patching if a custom modulus is provided
    if let Some(modulus) = rsa_modulus {
        if let Some(patcher_path) = find_patcher_library(binary) {
            log::info!("Setting LD_PRELOAD to {}", patcher_path.display());
            cmd.env("LD_PRELOAD", &patcher_path);
            cmd.env("DARKAN_RSA_MODULUS", modulus);
        } else {
            log::warn!(
                "RSA modulus provided but libdarkan_patcher.so not found. \
                 Build it with: cd client/launcher/patcher && cargo build --release"
            );
        }
    }

    cmd.env("HOME", &data_dir_str);
    cmd.env("SDL_VIDEODRIVER", "x11");
    cmd.env("SDL_VIDEO_X11_WMCLASS", "RuneScape");
    cmd.env(
        "PULSE_PROP_OVERRIDE",
        "application.name='RuneScape' application.icon_name='runescape' media.role='game'",
    );

    cmd.stdin(Stdio::null());

    let child = cmd.spawn().context("Failed to spawn RS3 process")?;
    log::info!("Spawned RS3 process with pid {}", child.id());

    Ok(())
}

/// Determine the rs3linux launcher binary name for the current platform
pub fn launcher_binary_name() -> &'static str {
    if cfg!(target_os = "windows") {
        "rs3windows.exe"
    } else {
        "rs3linux"
    }
}

/// Locate the patcher shared library (libdarkan_patcher.so).
///
/// Search order:
/// 1. Next to the launcher executable itself
/// 2. In the same directory as the client binary
/// 3. In ../patcher/target/release/ relative to the launcher executable (dev builds)
pub fn find_patcher_library(client_binary: &Path) -> Option<std::path::PathBuf> {
    let lib_name = "libdarkan_patcher.so";

    // Next to the launcher executable
    if let Ok(exe) = std::env::current_exe() {
        let candidate = exe.parent()?.join(lib_name);
        if candidate.exists() {
            return Some(candidate);
        }
    }

    // Next to the client binary
    if let Some(parent) = client_binary.parent() {
        let candidate = parent.join(lib_name);
        if candidate.exists() {
            return Some(candidate);
        }
    }

    // Dev build location: relative to launcher exe at ../patcher/target/release/
    if let Ok(exe) = std::env::current_exe() {
        if let Some(parent) = exe.parent() {
            let candidate = parent
                .join("..")
                .join("..")
                .join("..")
                .join("patcher")
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
