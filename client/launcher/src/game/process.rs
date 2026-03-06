use anyhow::{Context, Result};
use std::path::Path;
use std::process::{Command, Stdio};

pub struct LaunchParams<'a> {
    pub session_id: &'a str,
    pub account_id: &'a str,
    pub display_name: &'a str,
}

/// Launch the RS3 client binary
pub fn launch_rs3(
    binary: &Path,
    config_uri: &str,
    params: Option<&LaunchParams>,
    data_dir: &Path,
    custom_command: Option<&str>,
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

    // Set environment variables
    if let Some(p) = params {
        cmd.env("JX_SESSION_ID", p.session_id);
        cmd.env("JX_CHARACTER_ID", p.account_id);
        cmd.env("JX_DISPLAY_NAME", p.display_name);
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
