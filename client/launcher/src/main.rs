mod auth;
mod config;
mod game;
mod ui;

use anyhow::{Context, Result};
use fs2::FileExt;
use std::fs::File;
use std::sync::{Arc, OnceLock};
use tao::event::{Event, WindowEvent};
use tao::event_loop::{ControlFlow, EventLoopBuilder};
use tao::window::WindowBuilder;
use tokio::sync::mpsc;

use config::{load_config, load_credentials, Paths};
use ui::ipc::{AppCommand, IpcState};
use ui::webview::UserEvent;

/// Process-wide shared reqwest client. Building one Client per request creates a
/// fresh TLS config + connection pool each time, so keep-alive is lost across the
/// token-exchange → session-create → user-fetch → accounts-fetch chain. Reuse one.
static HTTP_CLIENT: OnceLock<reqwest::Client> = OnceLock::new();

/// Returns the shared HTTP client (cheap clone — the inner state is `Arc`-backed).
pub fn http_client() -> reqwest::Client {
    HTTP_CLIENT
        .get_or_init(reqwest::Client::new)
        .clone()
}

fn main() -> Result<()> {
    env_logger::Builder::from_env(env_logger::Env::default().default_filter_or("info")).init();

    // Work around GBM buffer creation failures on some GPU/driver combos
    if std::env::var("WEBKIT_DISABLE_DMABUF_RENDERER").is_err() {
        std::env::set_var("WEBKIT_DISABLE_DMABUF_RENDERER", "1");
    }

    // One-time rebrand migration: relocate the launcher's data dir from the old
    // `bolt-rs3` ProjectDirs path to the new `darkan-launcher` path. Must run
    // before any data-dir access (Paths::new resolves the NEW dir). Best-effort:
    // never blocks launch.
    migrate_data_dir();

    // Setup paths and acquire lockfile
    let paths = Arc::new(Paths::new().context("Failed to initialize paths")?);
    paths.ensure_dirs()?;

    let lock_file = File::create(&paths.lock_file).context("Failed to create lockfile")?;
    if lock_file.try_lock_exclusive().is_err() {
        log::error!("Another instance is already running");
        std::process::exit(1);
    }

    // Load config and credentials
    let cfg = load_config(&paths.config_file);
    let creds = load_credentials(&paths.creds_file);

    // Build event loop first (initializes GTK)
    let event_loop = EventLoopBuilder::<UserEvent>::with_user_event()
        .build();
    let proxy = event_loop.create_proxy();

    // Setup tokio runtime after GTK init
    let runtime = tokio::runtime::Runtime::new().context("Failed to create tokio runtime")?;
    let _guard = runtime.enter();

    // Refresh saved sessions on startup, then restore into active Session objects
    let creds = runtime.block_on(refresh_saved_sessions(creds, &paths));
    let sessions = restore_sessions(&creds);

    // Channel for commands from IPC/async tasks to the event loop
    let (cmd_tx, mut cmd_rx) = mpsc::unbounded_channel::<AppCommand>();

    let state = Arc::new(IpcState::new(cfg.clone(), creds, paths.clone(), cmd_tx, sessions));

    let main_window = WindowBuilder::new()
        .with_title("Darkan Launcher")
        .with_inner_size(tao::dpi::LogicalSize::new(520.0, 640.0))
        .with_min_inner_size(tao::dpi::LogicalSize::new(400.0, 500.0))
        .build(&event_loop)
        .context("Failed to create main window")?;

    let webview =
        ui::webview::create_main_webview(&main_window, state.clone(), proxy.clone())?;

    // The Init event is no longer pushed on a timer (a fixed sleep both adds
    // latency and races JS readiness). Instead the frontend posts a `ready` IPC
    // message once app.js boots, and `IpcState::handle_message` responds with
    // Init — see IpcMessage::Ready. This guarantees the payload lands after the
    // page's __darkan_callback is installed.

    // Auth window state
    let mut auth_window: Option<tao::window::Window> = None;
    let mut auth_webview: Option<wry::WebView> = None;

    // Poll command channel via proxy
    let poll_proxy = proxy.clone();
    std::thread::spawn(move || {
        while let Some(cmd) = cmd_rx.blocking_recv() {
            match cmd {
                AppCommand::OpenLoginWindow => {
                    let _ = poll_proxy.send_event(UserEvent::OpenLogin);
                }
                AppCommand::CloseWindow => {
                    let _ = poll_proxy.send_event(UserEvent::CloseApp);
                }
                AppCommand::SendToWebview(js) => {
                    let _ = poll_proxy.send_event(UserEvent::EvalScript(js));
                }
            }
        }
    });

    event_loop.run(move |event, event_loop, control_flow| {
        *control_flow = ControlFlow::Wait;

        match event {
            Event::WindowEvent {
                event: WindowEvent::CloseRequested,
                window_id,
                ..
            } => {
                if auth_window
                    .as_ref()
                    .map(|w| w.id() == window_id)
                    .unwrap_or(false)
                {
                    auth_webview = None;
                    auth_window = None;
                } else {
                    *control_flow = ControlFlow::Exit;
                }
            }

            Event::UserEvent(user_event) => match user_event {
                UserEvent::EvalScript(js) => {
                    if js.starts_with("__AUTH_NAVIGATE__:") {
                        let url = &js["__AUTH_NAVIGATE__:".len()..];
                        if let Some(ref wv) = auth_webview {
                            let _ = wv.load_url(url);
                        }
                    } else if js == "__AUTH_CLOSE__" {
                        auth_webview = None;
                        auth_window = None;
                    } else {
                        let _ = webview.evaluate_script(&js);
                    }
                }
                UserEvent::CloseApp => {
                    *control_flow = ControlFlow::Exit;
                }
                UserEvent::OpenLogin => {
                    if auth_window.is_some() {
                        if let Some(ref w) = auth_window {
                            w.set_focus();
                        }
                        return;
                    }

                    let pkce = auth::oauth::generate_pkce();
                    let login_url = auth::oauth::build_login_url(&pkce);

                    let window = WindowBuilder::new()
                        .with_title("Login - Jagex Account")
                        .with_inner_size(tao::dpi::LogicalSize::new(480.0, 720.0))
                        .build(event_loop)
                        .expect("Failed to create auth window");

                    let wv = ui::webview::create_auth_webview(
                        &window,
                        &login_url,
                        state.clone(),
                        proxy.clone(),
                        pkce.verifier,
                    )
                    .expect("Failed to create auth webview");

                    auth_window = Some(window);
                    auth_webview = Some(wv);
                }
            },

            _ => {}
        }
    });
}

/// Restore saved sessions from disk into active Session objects.
/// Reconstructs locally from saved data — no Jagex API calls needed.
fn restore_sessions(creds: &config::Credentials) -> Vec<auth::types::Session> {
    let mut sessions = Vec::new();

    for saved in &creds.sessions {
        // Skip sessions that don't have saved accounts (pre-migration creds.json)
        if saved.accounts.is_empty() {
            log::warn!(
                "Skipping session for {} — no saved accounts (re-login required)",
                saved.display_name
            );
            continue;
        }

        let session_id = match &saved.session_id {
            Some(id) => id.clone(),
            None => {
                log::warn!(
                    "Skipping session for {} — no saved session_id (re-login required)",
                    saved.display_name
                );
                continue;
            }
        };

        log::info!(
            "Restored session for {} ({} accounts)",
            saved.display_name,
            saved.accounts.len()
        );
        sessions.push(auth::types::Session {
            user: auth::types::User {
                id: None,
                user_id: saved.user_id.clone(),
                display_name: saved.display_name.clone(),
                suffix: String::new(),
            },
            accounts: saved.accounts.clone(),
            tokens: auth::types::AuthTokens {
                access_token: saved.access_token.clone(),
                id_token: saved.id_token.clone(),
                refresh_token: saved.refresh_token.clone(),
                sub: saved.user_id.clone(),
                expiry: saved.expiry,
            },
            session_id,
            consent_id_token: saved.consent_id_token.clone(),
        });
    }

    sessions
}

/// Refresh any saved sessions that are expired or near expiry.
///
/// After refreshing OAuth tokens, also creates a new game session_id
/// from the fresh id_token. Game sessions expire independently of OAuth
/// tokens, so the stored session_id must be renewed too.
async fn refresh_saved_sessions(
    mut creds: config::Credentials,
    paths: &Paths,
) -> config::Credentials {
    let client = http_client();
    let now_ms = std::time::SystemTime::now()
        .duration_since(std::time::UNIX_EPOCH)
        .unwrap_or(std::time::Duration::ZERO)
        .as_millis() as u64;

    let mut to_remove = Vec::new();

    for (i, session) in creds.sessions.iter_mut().enumerate() {
        // Refresh if expired or within 5 minutes of expiry
        if session.expiry.saturating_sub(now_ms) < 300_000 {
            log::info!("Refreshing token for user {}", session.display_name);
            match auth::oauth::refresh_token(&client, &session.refresh_token).await {
                Ok(tokens) => {
                    session.access_token = tokens.access_token;
                    session.id_token = tokens.id_token;
                    session.refresh_token = tokens.refresh_token;
                    session.expiry = tokens.expiry;
                    // Note: we do NOT create a new game session here because
                    // create_session requires the consent id_token (from the
                    // consent flow), not the launcher id_token from refresh.
                    // Fresh game sessions are created at launch time using
                    // the stored consent_id_token.
                }
                Err(e) => {
                    log::warn!(
                        "Failed to refresh token for {}: {}. Removing.",
                        session.display_name,
                        e
                    );
                    to_remove.push(i);
                }
            }
        }
    }

    // Remove failed sessions (in reverse order to preserve indices)
    for i in to_remove.into_iter().rev() {
        creds.sessions.remove(i);
    }

    let _ = config::save_credentials(&paths.creds_file, &creds);
    creds
}

/// One-time rebrand migration (Bolt RS3 → Darkan Launcher).
///
/// The launcher's ProjectDirs application name changed from `bolt-rs3` to
/// `darkan-launcher`, which moves the data dir (e.g. on Linux
/// `~/.local/share/bolt-rs3/` → `~/.local/share/darkan-launcher/`). That dir
/// holds live state — `creds.json` (OAuth login), the downloaded `Jagex/`
/// client, `libdarkan_patcher.so`, webview storage — so without migrating it the
/// user would be silently logged out and re-download the whole client.
///
/// If the NEW data dir is missing or empty AND the OLD one exists with content,
/// move old → new (rename, falling back to a recursive copy across filesystems).
/// Idempotent and best-effort: any failure logs a warning and lets launch
/// continue (a fresh data dir is still usable, just unmigrated).
fn migrate_data_dir() {
    use directories::ProjectDirs;

    let old_dirs = match ProjectDirs::from("", "", "bolt-rs3") {
        Some(d) => d,
        None => return,
    };
    let new_dirs = match ProjectDirs::from("", "", "darkan-launcher") {
        Some(d) => d,
        None => return,
    };

    let old_data = old_dirs.data_dir();
    let new_data = new_dirs.data_dir();

    // Nothing to migrate, or already migrated, or both resolve to the same path.
    if old_data == new_data || !dir_has_contents(old_data) {
        return;
    }
    if dir_has_contents(new_data) {
        log::info!(
            "Skipping data-dir migration: {} already has data",
            new_data.display()
        );
        return;
    }

    log::info!(
        "Migrating launcher data dir {} -> {} (Bolt RS3 -> Darkan Launcher rebrand)",
        old_data.display(),
        new_data.display()
    );

    // Ensure the new parent exists so a cross-dir rename can land.
    if let Some(parent) = new_data.parent() {
        if let Err(e) = std::fs::create_dir_all(parent) {
            log::warn!(
                "Data-dir migration: failed to create {}: {}. Continuing without migration.",
                parent.display(),
                e
            );
            return;
        }
    }

    match std::fs::rename(old_data, new_data) {
        Ok(()) => log::info!("Data-dir migration complete (renamed)."),
        Err(rename_err) => {
            // rename() fails across filesystems (EXDEV) — fall back to copy+remove.
            log::info!(
                "Data-dir rename failed ({}); falling back to recursive copy.",
                rename_err
            );
            match copy_dir_recursive(old_data, new_data) {
                Ok(()) => {
                    if let Err(e) = std::fs::remove_dir_all(old_data) {
                        log::warn!(
                            "Data-dir migration: copied to {} but failed to remove old {}: {}",
                            new_data.display(),
                            old_data.display(),
                            e
                        );
                    } else {
                        log::info!("Data-dir migration complete (copied).");
                    }
                }
                Err(e) => log::warn!(
                    "Data-dir migration: recursive copy failed: {}. Continuing without migration.",
                    e
                ),
            }
        }
    }
}

/// True if `dir` exists and contains at least one entry. A missing dir or an
/// empty dir both count as "no contents" (treat as needing/eligible-for migration).
fn dir_has_contents(dir: &std::path::Path) -> bool {
    match std::fs::read_dir(dir) {
        Ok(mut entries) => entries.next().is_some(),
        Err(_) => false,
    }
}

/// Recursively copy `src` into `dst`, creating directories as needed. Used as a
/// cross-filesystem fallback when `std::fs::rename` fails with EXDEV.
fn copy_dir_recursive(src: &std::path::Path, dst: &std::path::Path) -> std::io::Result<()> {
    std::fs::create_dir_all(dst)?;
    for entry in std::fs::read_dir(src)? {
        let entry = entry?;
        let file_type = entry.file_type()?;
        let from = entry.path();
        let to = dst.join(entry.file_name());
        if file_type.is_dir() {
            copy_dir_recursive(&from, &to)?;
        } else {
            // Covers regular files and symlinks (copies the target contents).
            std::fs::copy(&from, &to)?;
        }
    }
    Ok(())
}
