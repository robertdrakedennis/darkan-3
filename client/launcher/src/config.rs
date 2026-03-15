use anyhow::{Context, Result};
use directories::ProjectDirs;
use serde::{Deserialize, Serialize};
use std::fs;
use std::path::PathBuf;

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
        Ok(())
    }
}

pub fn load_config(path: &PathBuf) -> Config {
    fs::read_to_string(path)
        .ok()
        .and_then(|s| serde_json::from_str(&s).ok())
        .unwrap_or_default()
}

pub fn save_config(path: &PathBuf, config: &Config) -> Result<()> {
    let json = serde_json::to_string_pretty(config)?;
    fs::write(path, json)?;
    Ok(())
}

pub fn load_credentials(path: &PathBuf) -> Credentials {
    fs::read_to_string(path)
        .ok()
        .and_then(|s| serde_json::from_str(&s).ok())
        .unwrap_or_default()
}

pub fn save_credentials(path: &PathBuf, creds: &Credentials) -> Result<()> {
    let json = serde_json::to_string_pretty(creds)?;
    fs::write(path, json)?;
    Ok(())
}
