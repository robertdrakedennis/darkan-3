mod auth;
mod config;
mod game;
mod ui;

use anyhow::{Context, Result};
use fs2::FileExt;
use std::fs::File;
use std::sync::Arc;
use tao::event::{Event, WindowEvent};
use tao::event_loop::{ControlFlow, EventLoopBuilder};
use tao::window::WindowBuilder;
use tokio::sync::mpsc;

use config::{load_config, load_credentials, Paths};
use ui::ipc::{AppCommand, IpcEvent, IpcState};
use ui::webview::UserEvent;

fn main() -> Result<()> {
    env_logger::Builder::from_env(env_logger::Env::default().default_filter_or("info")).init();

    // Work around GBM buffer creation failures on some GPU/driver combos
    if std::env::var("WEBKIT_DISABLE_DMABUF_RENDERER").is_err() {
        std::env::set_var("WEBKIT_DISABLE_DMABUF_RENDERER", "1");
    }

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
        .with_title("Bolt RS3")
        .with_inner_size(tao::dpi::LogicalSize::new(520.0, 640.0))
        .with_min_inner_size(tao::dpi::LogicalSize::new(400.0, 500.0))
        .build(&event_loop)
        .context("Failed to create main window")?;

    let webview =
        ui::webview::create_main_webview(&main_window, state.clone(), proxy.clone())?;

    // Send init event once page loads (small delay to ensure JS is ready)
    let init_state = state.clone();
    let init_proxy = proxy.clone();
    std::thread::spawn(move || {
        std::thread::sleep(std::time::Duration::from_millis(200));
        let sessions = init_state.build_session_infos();
        let config = init_state.config.lock().unwrap().clone();
        let event = IpcEvent::Init { config, sessions };
        let js = format!(
            "window.__bolt_callback({})",
            serde_json::to_string(&event).unwrap()
        );
        let _ = init_proxy.send_event(UserEvent::EvalScript(js));
    });

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
        });
    }

    sessions
}

/// Refresh any saved sessions that are near expiry
async fn refresh_saved_sessions(
    mut creds: config::Credentials,
    paths: &Paths,
) -> config::Credentials {
    let client = reqwest::Client::new();
    let now_ms = std::time::SystemTime::now()
        .duration_since(std::time::UNIX_EPOCH)
        .unwrap()
        .as_millis() as u64;

    let mut to_remove = Vec::new();

    for (i, session) in creds.sessions.iter_mut().enumerate() {
        // Refresh if less than 30 seconds remaining
        if session.expiry.saturating_sub(now_ms) < 30_000 {
            log::info!("Refreshing token for user {}", session.display_name);
            match auth::oauth::refresh_token(&client, &session.refresh_token).await {
                Ok(tokens) => {
                    session.access_token = tokens.access_token;
                    session.id_token = tokens.id_token;
                    session.refresh_token = tokens.refresh_token;
                    session.expiry = tokens.expiry;
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
