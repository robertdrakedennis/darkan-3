use anyhow::Result;
use serde::{Deserialize, Serialize};
use std::path::Path;
use std::sync::atomic::{AtomicBool, Ordering};
use std::sync::{Arc, Mutex};
use tokio::sync::mpsc;

use crate::auth::types::{Account, Session};
use crate::config::{Config, Credentials, Paths, SavedSession};
use crate::game::process::LaunchParams;

/// Messages from JS to Rust
#[derive(Debug, Deserialize)]
#[serde(tag = "type")]
pub enum IpcMessage {
    /// Sent once by app.js when the frontend has booted and installed
    /// `window.__bolt_callback`. We reply with `Init` (no startup sleep/race).
    #[serde(rename = "ready")]
    Ready,
    #[serde(rename = "login")]
    Login,
    #[serde(rename = "logout")]
    Logout { user_id: String },
    #[serde(rename = "launch")]
    Launch {
        account_id: String,
        display_name: String,
    },
    #[serde(rename = "launch_custom")]
    LaunchCustom,
    #[serde(rename = "save_config")]
    SaveConfig { config: Config },
    #[serde(rename = "close")]
    Close,
    #[serde(rename = "open_url")]
    OpenUrl { url: String },
}

/// Events from Rust to JS
#[derive(Debug, Clone, Serialize)]
#[serde(tag = "type")]
pub enum IpcEvent {
    #[serde(rename = "init")]
    Init {
        config: Config,
        sessions: Vec<SessionInfo>,
    },
    #[serde(rename = "login_complete")]
    LoginComplete { session: SessionInfo },
    #[serde(rename = "login_error")]
    LoginError { message: String },
    #[serde(rename = "logout_complete")]
    LogoutComplete { user_id: String },
    #[serde(rename = "launch_status")]
    LaunchStatus { message: String },
    #[serde(rename = "launch_error")]
    LaunchError { message: String },
    #[serde(rename = "config_saved")]
    ConfigSaved,
}

#[derive(Debug, Clone, Serialize)]
pub struct SessionInfo {
    pub user_id: String,
    pub display_name: String,
    pub accounts: Vec<Account>,
    pub session_id: String,
}

/// Commands sent from IPC handler to the main event loop
pub enum AppCommand {
    OpenLoginWindow,
    CloseWindow,
    SendToWebview(String),
}

/// Serialize an event and route it to the webview via `cmd_tx`. Shared by the
/// spawned launch tasks so the JS-callback formatting lives in one place.
fn send_webview_event(cmd_tx: &mpsc::UnboundedSender<AppCommand>, event: &IpcEvent) {
    let js = format!(
        "window.__bolt_callback({})",
        serde_json::to_string(event).unwrap()
    );
    let _ = cmd_tx.send(AppCommand::SendToWebview(js));
}

/// Launch the live/proxy client and report the result to the webview. Dedupes
/// the two identical launch call sites in `handle_launch` (the normal path and
/// the "package check failed, launch existing binary" fallback).
#[allow(clippy::too_many_arguments)]
fn do_launch_live(
    cmd_tx: &mpsc::UnboundedSender<AppCommand>,
    binary: &Path,
    config_uri: &str,
    params: &LaunchParams,
    data_dir: &Path,
    custom_cmd: Option<&str>,
    rsa_modulus: Option<&str>,
    proxy_mode: bool,
    close_after: bool,
) {
    match crate::game::process::launch_rs3(
        binary,
        config_uri,
        Some(params),
        data_dir,
        custom_cmd,
        rsa_modulus,
        None, // no working_dir for live/proxy mode
        proxy_mode,
    ) {
        Ok(()) => {
            send_webview_event(
                cmd_tx,
                &IpcEvent::LaunchStatus {
                    message: "Game launched!".to_string(),
                },
            );
            if close_after {
                let _ = cmd_tx.send(AppCommand::CloseWindow);
            }
        }
        Err(e) => {
            send_webview_event(
                cmd_tx,
                &IpcEvent::LaunchError {
                    message: format!("Failed to launch: {}", e),
                },
            );
        }
    }
}

/// RAII guard that clears the launch-in-progress flag when dropped, so the flag
/// is released on every exit path of the spawned launch task (success, error,
/// or early return).
struct LaunchGuard(Arc<AtomicBool>);

impl Drop for LaunchGuard {
    fn drop(&mut self) {
        self.0.store(false, Ordering::SeqCst);
    }
}

pub struct IpcState {
    pub config: Mutex<Config>,
    pub credentials: Mutex<Credentials>,
    pub paths: Arc<Paths>,
    pub sessions: Mutex<Vec<Session>>,
    pub cmd_tx: mpsc::UnboundedSender<AppCommand>,
    /// True while a launch task (live or custom) is downloading/extracting/
    /// launching. Prevents concurrent launches from racing on the same binary.
    launching: Arc<AtomicBool>,
}

impl IpcState {
    pub fn new(
        config: Config,
        credentials: Credentials,
        paths: Arc<Paths>,
        cmd_tx: mpsc::UnboundedSender<AppCommand>,
        initial_sessions: Vec<Session>,
    ) -> Self {
        Self {
            config: Mutex::new(config),
            credentials: Mutex::new(credentials),
            paths,
            sessions: Mutex::new(initial_sessions),
            cmd_tx,
            launching: Arc::new(AtomicBool::new(false)),
        }
    }

    /// Try to claim the launch slot. Returns a guard on success; on failure a
    /// launch is already in progress and the caller should reject the request.
    fn try_begin_launch(&self) -> Option<LaunchGuard> {
        if self.launching.swap(true, Ordering::SeqCst) {
            None
        } else {
            Some(LaunchGuard(self.launching.clone()))
        }
    }

    /// Handle an incoming IPC message from JS
    pub fn handle_message(&self, msg_str: &str) -> Result<()> {
        let msg: IpcMessage = serde_json::from_str(msg_str)?;
        log::info!("IPC message: {:?}", msg);

        match msg {
            IpcMessage::Ready => {
                let sessions = self.build_session_infos();
                let config = self.config.lock().unwrap().clone();
                self.send_event(&IpcEvent::Init { config, sessions });
            }
            IpcMessage::Login => {
                self.cmd_tx.send(AppCommand::OpenLoginWindow)?;
            }
            IpcMessage::Logout { ref user_id } => {
                self.handle_logout(user_id);
            }
            IpcMessage::Launch {
                ref account_id,
                ref display_name,
            } => {
                self.handle_launch(account_id, display_name);
            }
            IpcMessage::LaunchCustom => {
                self.handle_launch_custom();
            }
            IpcMessage::SaveConfig { ref config } => {
                self.handle_save_config(config);
            }
            IpcMessage::Close => {
                let _ = self.cmd_tx.send(AppCommand::CloseWindow);
            }
            IpcMessage::OpenUrl { ref url } => {
                let _ = crate::game::process::open_url(url);
            }
        }

        Ok(())
    }

    fn handle_logout(&self, user_id: &str) {
        // Remove from credentials, grabbing the access token first so we can
        // revoke it server-side on a best-effort basis.
        let access_token = {
            let mut creds = self.credentials.lock().unwrap();
            let token = creds
                .sessions
                .iter()
                .find(|s| s.user_id == user_id)
                .map(|s| s.access_token.clone());
            creds.sessions.retain(|s| s.user_id != user_id);
            let _ = crate::config::save_credentials(&self.paths.creds_file, &creds);
            token
        };

        // Remove from active sessions
        {
            let mut sessions = self.sessions.lock().unwrap();
            sessions.retain(|s| s.tokens.sub != user_id);
        }

        // Best-effort token revocation: fire-and-forget, never block logout on it.
        if let Some(token) = access_token {
            let client = crate::http_client();
            tokio::spawn(async move {
                if let Err(e) = crate::auth::oauth::revoke_token(&client, &token).await {
                    log::warn!("Token revocation failed during logout (ignored): {}", e);
                }
            });
        }

        let event = IpcEvent::LogoutComplete {
            user_id: user_id.to_string(),
        };
        self.send_event(&event);
    }

    fn handle_launch(&self, account_id: &str, display_name: &str) {
        let (stored_session_id, consent_id_token) = {
            let sessions = self.sessions.lock().unwrap();
            let session = sessions
                .iter()
                .find(|s| s.accounts.iter().any(|a| a.account_id == account_id));

            match session {
                Some(s) => (
                    s.session_id.clone(),
                    s.consent_id_token.clone(),
                ),
                None => {
                    self.send_event(&IpcEvent::LaunchError {
                        message: "No active session found for this account".to_string(),
                    });
                    return;
                }
            }
        };

        // Reject re-entrant launches so two concurrent launch IPCs can't race on
        // downloading/extracting/writing the same binary.
        let guard = match self.try_begin_launch() {
            Some(g) => g,
            None => {
                self.send_event(&IpcEvent::LaunchError {
                    message: "A launch is already in progress.".to_string(),
                });
                return;
            }
        };

        let config = self.config.lock().unwrap().clone();
        let paths = self.paths.clone();
        let account_id = account_id.to_string();
        let display_name = display_name.to_string();
        let cmd_tx = self.cmd_tx.clone();
        let close_after = config.close_after_launch;
        let custom_cmd = config.custom_launch_command.clone();
        let is_proxy = matches!(config.server_mode, crate::config::ServerMode::Proxy);
        let config_uri = config
            .custom_config_uri
            .clone()
            .unwrap_or_else(|| crate::game::rs3::DEFAULT_CONFIG_URI.to_string());

        tokio::spawn(async move {
            // Held for the whole task; clears the launch flag on drop (all paths).
            let _guard = guard;

            let send_status = |msg: &str| {
                send_webview_event(
                    &cmd_tx,
                    &IpcEvent::LaunchStatus {
                        message: msg.to_string(),
                    },
                );
            };
            let send_error = |msg: &str| {
                send_webview_event(
                    &cmd_tx,
                    &IpcEvent::LaunchError {
                        message: msg.to_string(),
                    },
                );
            };

            let client = crate::http_client();

            // Create a fresh game session using the consent id_token.
            // The consent id_token (from the consent flow) is different from the
            // launcher id_token (from OAuth refresh) — only the consent one works.
            send_status("Preparing game session...");
            let session_id = match &consent_id_token {
                Some(cit) => {
                    match crate::auth::session::create_session(&client, cit).await {
                        Ok(sid) => {
                            log::info!("Created fresh game session");
                            sid
                        }
                        Err(e) => {
                            log::warn!("Session creation failed: {}. Using stored session.", e);
                            stored_session_id.clone()
                        }
                    }
                }
                None => {
                    log::info!("No consent_id_token saved (pre-migration login). Using stored session.");
                    stored_session_id.clone()
                }
            };

            // In proxy mode, fetch the proxy's jav_config to extract RSA modulus
            let proxy_rsa_modulus = if is_proxy {
                send_status("Proxy mode: fetching config from proxy...");
                match crate::game::rs3::fetch_jav_config_params(&client, &config_uri).await {
                    Ok(params) => crate::game::rs3::extract_rsa_modulus(&params),
                    Err(e) => {
                        send_error(&format!("Failed to fetch proxy jav_config: {}", e));
                        return;
                    }
                }
            } else {
                None
            };

            // Check for updates
            send_status("Checking for updates...");
            let pkg_info = match crate::game::rs3::fetch_package_info(&client).await {
                Ok(info) => info,
                Err(e) => {
                    log::error!("Failed to fetch package info: {}", e);
                    // Try to launch existing binary anyway
                    if paths.rs3_binary.exists() {
                        send_status("Launching existing client...");
                        let params = LaunchParams {
                            session_id: &session_id,
                            account_id: &account_id,
                            display_name: &display_name,
                        };
                        do_launch_live(
                            &cmd_tx,
                            &paths.rs3_binary,
                            &config_uri,
                            &params,
                            &paths.data_dir,
                            custom_cmd.as_deref(),
                            proxy_rsa_modulus.as_deref(),
                            is_proxy,
                            close_after,
                        );
                    } else {
                        send_error(&format!("Failed to check for updates and no client installed: {}", e));
                    }
                    return;
                }
            };

            // Check if up to date
            if crate::game::rs3::is_up_to_date(&paths.rs3_hash, &pkg_info.sha256) {
                send_status("Client is up-to-date");
            } else {
                // Download
                send_status("Downloading client...");
                let deb_bytes = match crate::game::rs3::download_deb(&client, &pkg_info.filename).await {
                    Ok(bytes) => bytes,
                    Err(e) => {
                        send_error(&format!("Download failed: {}", e));
                        return;
                    }
                };

                // Verify hash
                if !crate::game::rs3::verify_hash(&deb_bytes, &pkg_info.sha256) {
                    send_error("Hash verification failed");
                    return;
                }

                // Extract off the async workers — xz decompress + tar walk + the
                // atomic temp-file write are all blocking. `extract_rs3_binary`
                // writes via temp file + rename, so the binary is never truncated.
                send_status("Extracting client...");
                let out = paths.rs3_binary.clone();
                let extract_result = tokio::task::spawn_blocking(move || {
                    crate::game::deb::extract_rs3_binary(deb_bytes, &out)
                })
                .await;
                match extract_result {
                    Ok(Ok(())) => {}
                    Ok(Err(e)) => {
                        send_error(&format!("Extraction failed: {}", e));
                        return;
                    }
                    Err(e) => {
                        send_error(&format!("Extraction task failed: {}", e));
                        return;
                    }
                }

                if let Err(e) = crate::game::rs3::save_hash(&paths.rs3_hash, &pkg_info.sha256) {
                    log::error!("Failed to save hash: {}", e);
                }

                send_status("Client updated successfully");
            }

            // Launch
            send_status("Launching game...");
            let params = LaunchParams {
                session_id: &session_id,
                account_id: &account_id,
                display_name: &display_name,
            };
            do_launch_live(
                &cmd_tx,
                &paths.rs3_binary,
                &config_uri,
                &params,
                &paths.data_dir,
                custom_cmd.as_deref(),
                proxy_rsa_modulus.as_deref(),
                is_proxy,
                close_after,
            );
        });
    }

    fn handle_launch_custom(&self) {
        // Reject re-entrant launches (shares the flag with live/proxy launches).
        let guard = match self.try_begin_launch() {
            Some(g) => g,
            None => {
                self.send_event(&IpcEvent::LaunchError {
                    message: "A launch is already in progress.".to_string(),
                });
                return;
            }
        };

        let config = self.config.lock().unwrap().clone();
        let cmd_tx = self.cmd_tx.clone();
        let close_after = config.close_after_launch;
        let custom_cmd = config.custom_launch_command.clone();

        // Build config URI from custom settings
        let host = config
            .custom_server_host
            .as_deref()
            .unwrap_or("localhost");
        let config_uri = config.custom_config_uri.clone().unwrap_or_else(|| {
            format!("http://{}:8829/jav_config.ws", host)
        });

        // ~/darkan-3 is the sole runtime directory for custom mode
        let darkan_dir = std::path::PathBuf::from(
            std::env::var("HOME").unwrap_or_else(|_| ".".to_string()),
        )
        .join("darkan-3");

        let launcher_name = crate::game::process::launcher_binary_name();

        tokio::spawn(async move {
            // Held for the whole task; clears the launch flag on drop (all paths).
            let _guard = guard;

            let send_status = |msg: &str| {
                send_webview_event(
                    &cmd_tx,
                    &IpcEvent::LaunchStatus {
                        message: msg.to_string(),
                    },
                );
            };
            let send_error = |msg: &str| {
                send_webview_event(
                    &cmd_tx,
                    &IpcEvent::LaunchError {
                        message: msg.to_string(),
                    },
                );
            };

            // Ensure ~/darkan-3 exists
            if let Err(e) = tokio::fs::create_dir_all(&darkan_dir).await {
                send_error(&format!(
                    "Failed to create {}: {}",
                    darkan_dir.display(),
                    e
                ));
                return;
            }

            // Ensure rs3linux is in ~/darkan-3; seed from ./data/client/ if needed
            let target_binary = darkan_dir.join(launcher_name);
            if !target_binary.exists() {
                let source = std::path::PathBuf::from("data")
                    .join("client")
                    .join(launcher_name);
                if source.exists() {
                    send_status("Installing launcher binary...");
                    if let Err(e) = tokio::fs::copy(&source, &target_binary).await {
                        send_error(&format!("Failed to copy {} to ~/darkan-3: {}", launcher_name, e));
                        return;
                    }
                    // Preserve executable permission
                    #[cfg(unix)]
                    {
                        use std::os::unix::fs::PermissionsExt;
                        let _ = std::fs::set_permissions(
                            &target_binary,
                            std::fs::Permissions::from_mode(0o755),
                        );
                    }
                } else {
                    send_error(&format!(
                        "{} not found in ~/darkan-3 or ./data/client/",
                        launcher_name
                    ));
                    return;
                }
            }

            // Also seed libdarkan_patcher.so if available and not yet in ~/darkan-3
            let patcher_name = "libdarkan_patcher.so";
            let target_patcher = darkan_dir.join(patcher_name);
            if !target_patcher.exists() {
                // Check next to launcher exe, then ./data/client/, then dev build path
                let candidates = [
                    std::env::current_exe()
                        .ok()
                        .and_then(|e| e.parent().map(|p| p.join(patcher_name))),
                    Some(
                        std::path::PathBuf::from("data")
                            .join("client")
                            .join(patcher_name),
                    ),
                    std::env::current_exe().ok().and_then(|e| {
                        e.parent().map(|p| {
                            p.join("..")
                                .join("..")
                                .join("..")
                                .join("patcher")
                                .join("target")
                                .join("release")
                                .join(patcher_name)
                        })
                    }),
                ];
                for candidate in candidates.iter().flatten() {
                    if candidate.exists() {
                        let _ = tokio::fs::copy(candidate, &target_patcher).await;
                        break;
                    }
                }
            }

            // Create default preferences.cfg if missing
            let prefs_path = darkan_dir.join("preferences.cfg");
            if !prefs_path.exists() {
                let prefs_content = format!(
                    "cache_folder={dir}\nLanguage=0\nuser_folder={dir}\n",
                    dir = darkan_dir.display()
                );
                if let Err(e) = tokio::fs::write(&prefs_path, &prefs_content).await {
                    log::warn!("Failed to write preferences.cfg: {}", e);
                }
            }

            // Fetch jav_config.ws to extract RSA modulus from param=99
            send_status("Fetching config from server...");
            let client = crate::http_client();
            let jav_params =
                match crate::game::rs3::fetch_jav_config_params(&client, &config_uri).await {
                    Ok(params) => params,
                    Err(e) => {
                        send_error(&format!("Failed to fetch jav_config.ws: {}", e));
                        return;
                    }
                };
            log::info!("Parsed {} jav_config params", jav_params.len());

            // Determine RSA modulus: prefer explicit config, fall back to param=99
            let rsa_modulus = config
                .custom_rsa_modulus
                .clone()
                .or_else(|| crate::game::rs3::extract_rsa_modulus(&jav_params));

            // Launch from ~/darkan-3 — rs3linux will auto-download rs2client from config server
            send_status("Launching rs3linux (Custom server)...");
            match crate::game::process::launch_rs3(
                &target_binary,
                &config_uri,
                None,
                &darkan_dir,
                custom_cmd.as_deref(),
                rsa_modulus.as_deref(),
                Some(&darkan_dir), // CWD = ~/darkan-3
                false, // not proxy mode — full patching for private server
            ) {
                Ok(()) => {
                    send_status("Game launched!");
                    if close_after {
                        let _ = cmd_tx.send(AppCommand::CloseWindow);
                    }
                }
                Err(e) => {
                    send_error(&format!("Failed to launch: {}", e));
                }
            }
        });
    }

    fn handle_save_config(&self, new_config: &Config) {
        let mut config = self.config.lock().unwrap();
        *config = new_config.clone();
        let _ = crate::config::save_config(&self.paths.config_file, &config);
        self.send_event(&IpcEvent::ConfigSaved);
    }

    pub fn send_event(&self, event: &IpcEvent) {
        let js = format!(
            "window.__bolt_callback({})",
            serde_json::to_string(event).unwrap()
        );
        let _ = self.cmd_tx.send(AppCommand::SendToWebview(js));
    }

    /// Build init data with current sessions
    pub fn build_session_infos(&self) -> Vec<SessionInfo> {
        let sessions = self.sessions.lock().unwrap();
        sessions
            .iter()
            .map(|s| SessionInfo {
                user_id: s.tokens.sub.clone(),
                display_name: s.user.display_name.clone(),
                accounts: s.accounts.clone(),
                session_id: s.session_id.clone(),
            })
            .collect()
    }

    /// Add a completed session (after login) and save credentials
    pub fn add_session(&self, session: Session) {
        // Save to credentials
        {
            let mut creds = self.credentials.lock().unwrap();
            // Remove old entry for same user
            creds
                .sessions
                .retain(|s| s.user_id != session.tokens.sub);
            creds.sessions.push(SavedSession {
                user_id: session.tokens.sub.clone(),
                display_name: session.user.display_name.clone(),
                access_token: session.tokens.access_token.clone(),
                id_token: session.tokens.id_token.clone(),
                refresh_token: session.tokens.refresh_token.clone(),
                expiry: session.tokens.expiry,
                accounts: session.accounts.clone(),
                session_id: Some(session.session_id.clone()),
                consent_id_token: session.consent_id_token.clone(),
            });
            let _ = crate::config::save_credentials(&self.paths.creds_file, &creds);
        }

        let info = SessionInfo {
            user_id: session.tokens.sub.clone(),
            display_name: session.user.display_name.clone(),
            accounts: session.accounts.clone(),
            session_id: session.session_id.clone(),
        };

        // Add to active sessions
        {
            let mut sessions = self.sessions.lock().unwrap();
            sessions.retain(|s| s.tokens.sub != session.tokens.sub);
            sessions.push(session);
        }

        self.send_event(&IpcEvent::LoginComplete { session: info });
    }
}

