use anyhow::{Context, Result};
use directories::ProjectDirs;
use serde::de::DeserializeOwned;
use serde::{Deserialize, Serialize};
use std::fs;
use std::io::Write;
use std::path::{Path, PathBuf};

#[derive(Debug, Clone, Serialize, Deserialize)]
pub enum ServerMode {
    Live,
    Proxy,
    Custom,
}

impl Default for ServerMode {
    fn default() -> Self {
        ServerMode::Live
    }
}

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct Config {
    #[serde(default = "default_true")]
    pub dark_theme: bool,
    #[serde(default)]
    pub close_after_launch: bool,
    #[serde(default)]
    pub custom_launch_command: Option<String>,
    #[serde(default)]
    pub server_mode: ServerMode,
    #[serde(default)]
    pub custom_server_host: Option<String>,
    #[serde(default)]
    pub custom_server_port: Option<u16>,
    #[serde(default)]
    pub custom_config_uri: Option<String>,
    #[serde(default)]
    pub custom_rsa_modulus: Option<String>,
}

fn default_true() -> bool {
    true
}

impl Default for Config {
    fn default() -> Self {
        Self {
            dark_theme: true,
            close_after_launch: false,
            custom_launch_command: None,
            server_mode: ServerMode::default(),
            custom_server_host: None,
            custom_server_port: None,
            custom_config_uri: None,
            custom_rsa_modulus: None,
        }
    }
}

#[derive(Debug, Clone, Serialize, Deserialize, Default)]
pub struct Credentials {
    pub sessions: Vec<SavedSession>,
}

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct SavedSession {
    pub user_id: String,
    pub display_name: String,
    pub access_token: String,
    pub id_token: String,
    pub refresh_token: String,
    pub expiry: u64,
    #[serde(default)]
    pub accounts: Vec<crate::auth::types::Account>,
    #[serde(default)]
    pub session_id: Option<String>,
    /// Consent id_token (from the consent flow, different from launcher id_token).
    /// Used to create fresh game sessions at launch time.
    #[serde(default)]
    pub consent_id_token: Option<String>,
}

pub struct Paths {
    pub config_dir: PathBuf,
    pub data_dir: PathBuf,
    pub runtime_dir: PathBuf,
    pub config_file: PathBuf,
    pub creds_file: PathBuf,
    pub lock_file: PathBuf,
    pub rs3_binary: PathBuf,
    pub rs3_hash: PathBuf,
}

impl Paths {
    pub fn new() -> Result<Self> {
        let dirs = ProjectDirs::from("", "", "bolt-rs3")
            .context("Failed to determine project directories")?;

        let config_dir = dirs.config_dir().to_path_buf();
        let data_dir = dirs.data_dir().to_path_buf();
        let runtime_dir = dirs
            .runtime_dir()
            .map(|p| p.to_path_buf())
            .unwrap_or_else(|| data_dir.join("run"));

        Ok(Self {
            config_file: config_dir.join("config.json"),
            creds_file: data_dir.join("creds.json"),
            lock_file: runtime_dir.join("lock"),
            rs3_binary: data_dir.join("rs3linux"),
            rs3_hash: data_dir.join("rs3linux.sha256"),
            config_dir,
            data_dir,
            runtime_dir,
        })
    }

    pub fn ensure_dirs(&self) -> Result<()> {
        fs::create_dir_all(&self.config_dir)?;
        fs::create_dir_all(&self.data_dir)?;
        fs::create_dir_all(&self.runtime_dir)?;
        // The config/data dirs hold OAuth tokens — keep them owner-only.
        restrict_dir_permissions(&self.config_dir);
        restrict_dir_permissions(&self.data_dir);
        Ok(())
    }
}

/// Restrict a directory to owner-only access (0700). No-op on non-unix.
fn restrict_dir_permissions(dir: &Path) {
    #[cfg(unix)]
    {
        use std::os::unix::fs::PermissionsExt;
        if let Err(e) = fs::set_permissions(dir, fs::Permissions::from_mode(0o700)) {
            log::warn!("Failed to set 0700 on {}: {}", dir.display(), e);
        }
    }
    #[cfg(not(unix))]
    let _ = dir;
}

/// Load and deserialize a JSON file, distinguishing three cases:
/// - absent → default (first run, expected)
/// - present but unreadable → log error, default (do not destroy the file)
/// - present but unparseable → log error, back the file up to `<name>.bak`
///   (numbered if a backup already exists), then default. This prevents the
///   next save from silently overwriting recoverable data.
fn load_or_default<T: DeserializeOwned + Default>(path: &Path, label: &str) -> T {
    match fs::read_to_string(path) {
        Ok(contents) => match serde_json::from_str(&contents) {
            Ok(value) => value,
            Err(e) => {
                log::error!(
                    "Failed to parse {} at {}: {}. Backing up and using defaults.",
                    label,
                    path.display(),
                    e
                );
                backup_corrupt_file(path);
                T::default()
            }
        },
        Err(e) if e.kind() == std::io::ErrorKind::NotFound => T::default(),
        Err(e) => {
            log::error!(
                "Failed to read {} at {}: {}. Using defaults (file left intact).",
                label,
                path.display(),
                e
            );
            T::default()
        }
    }
}

/// Move a corrupt file aside to `<path>.bak`, or `<path>.bak.N` if that exists.
fn backup_corrupt_file(path: &Path) {
    let mut backup = {
        let mut name = path.as_os_str().to_owned();
        name.push(".bak");
        PathBuf::from(name)
    };
    let mut n: u32 = 1;
    while backup.exists() {
        let mut name = path.as_os_str().to_owned();
        name.push(format!(".bak.{}", n));
        backup = PathBuf::from(name);
        n += 1;
    }
    match fs::rename(path, &backup) {
        Ok(()) => log::warn!(
            "Backed up corrupt file {} to {}",
            path.display(),
            backup.display()
        ),
        Err(e) => log::error!(
            "Failed to back up corrupt file {}: {}",
            path.display(),
            e
        ),
    }
}

/// Atomically write `contents` to `path`: write to a temp file in the same
/// directory (0600 on unix so tokens never land world-readable), fsync, then
/// rename over the target. The parent dir is created and restricted to 0700.
fn atomic_write(path: &Path, contents: &[u8]) -> Result<()> {
    let parent = path
        .parent()
        .context("Cannot determine parent directory for atomic write")?;
    fs::create_dir_all(parent)
        .with_context(|| format!("Failed to create {}", parent.display()))?;
    restrict_dir_permissions(parent);

    let file_name = path
        .file_name()
        .map(|n| n.to_string_lossy().to_string())
        .unwrap_or_else(|| "config".to_string());
    let tmp = parent.join(format!(".{}.{}.tmp", file_name, std::process::id()));

    let write_result = (|| -> Result<()> {
        let mut file = fs::File::create(&tmp)
            .with_context(|| format!("Failed to create temp file {}", tmp.display()))?;
        #[cfg(unix)]
        {
            use std::os::unix::fs::PermissionsExt;
            file.set_permissions(fs::Permissions::from_mode(0o600))
                .context("Failed to set 0600 on temp file")?;
        }
        file.write_all(contents)
            .context("Failed to write temp file contents")?;
        file.sync_all().context("Failed to fsync temp file")?;
        Ok(())
    })();

    if let Err(e) = write_result {
        let _ = fs::remove_file(&tmp);
        return Err(e);
    }

    fs::rename(&tmp, path).with_context(|| {
        format!("Failed to rename {} over {}", tmp.display(), path.display())
    })?;
    Ok(())
}

pub fn load_config(path: &Path) -> Config {
    load_or_default(path, "config.json")
}

pub fn save_config(path: &Path, config: &Config) -> Result<()> {
    let json = serde_json::to_string_pretty(config)?;
    atomic_write(path, json.as_bytes())
}

pub fn load_credentials(path: &Path) -> Credentials {
    load_or_default(path, "creds.json")
}

pub fn save_credentials(path: &Path, creds: &Credentials) -> Result<()> {
    let json = serde_json::to_string_pretty(creds)?;
    atomic_write(path, json.as_bytes())
}
