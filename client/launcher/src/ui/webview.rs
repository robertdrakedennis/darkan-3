use anyhow::{Context, Result};
use std::sync::atomic::{AtomicBool, Ordering};
use std::sync::{Arc, Mutex};
use tao::event_loop::EventLoopProxy;
use tao::platform::unix::WindowExtUnix;
use wry::WebViewBuilder;
use wry::WebViewBuilderExtUnix;

use super::assets::UiAssets;
use super::ipc::IpcState;

/// Custom user event for the tao event loop
#[derive(Debug)]
pub enum UserEvent {
    /// Execute JS in the main webview
    EvalScript(String),
    /// Close the application
    CloseApp,
    /// Open the auth login window
    OpenLogin,
}

/// Build inline HTML with CSS and JS embedded
fn build_inline_html() -> String {
    let html = UiAssets::get("index.html")
        .map(|a| String::from_utf8_lossy(&a.data).to_string())
        .unwrap_or_else(|| "<html><body>Failed to load UI</body></html>".into());

    let css = UiAssets::get("style.css")
        .map(|a| String::from_utf8_lossy(&a.data).to_string())
        .unwrap_or_default();

    let js = UiAssets::get("app.js")
        .map(|a| String::from_utf8_lossy(&a.data).to_string())
        .unwrap_or_default();

    let html = html.replace(
        r#"<link rel="stylesheet" href="style.css">"#,
        &format!("<style>{}</style>", css),
    );

    html.replace(
        r#"<script src="app.js"></script>"#,
        &format!("<script>{}</script>", js),
    )
}

/// Create the main launcher webview
pub fn create_main_webview(
    window: &tao::window::Window,
    state: Arc<IpcState>,
    _proxy: EventLoopProxy<UserEvent>,
) -> Result<wry::WebView> {
    let ipc_state = state.clone();
    let html = build_inline_html();

    let vbox = window
        .default_vbox()
        .context("No default vbox on tao window")?;

    let builder = WebViewBuilder::new()
        .with_html(&html)
        .with_ipc_handler(move |msg| {
            let body = msg.body();
            if let Err(e) = ipc_state.handle_message(body) {
                log::error!("IPC error: {}", e);
            }
        })
        .with_devtools(cfg!(debug_assertions));

    let webview = builder
        .build_gtk(vbox)
        .context("Failed to create webview")?;

    Ok(webview)
}

/// Create the auth popup window + webview for OAuth login.
///
/// The login redirect is caught by `on_page_load_handler` (Started event).
/// The consent redirect (`http://localhost#id_token=...`) is handled two ways:
/// 1. Primary: a callback server on port 80 serves JS that forwards the fragment
/// 2. Fallback: if port 80 can't bind, the page load handler tries to extract
///    the id_token from the URL fragment directly (works if WebKitGTK includes
///    the fragment in the reported URL for failed loads)
pub fn create_auth_webview(
    window: &tao::window::Window,
    login_url: &str,
    state: Arc<IpcState>,
    proxy: EventLoopProxy<UserEvent>,
    verifier: String,
) -> Result<wry::WebView> {
    let login_handled = Arc::new(AtomicBool::new(false));
    // Shared state: set by handle_code_exchange when port 80 server fails,
    // read by the page load handler to complete consent via URL fragment fallback
    let consent_tokens: Arc<Mutex<Option<crate::auth::types::AuthTokens>>> =
        Arc::new(Mutex::new(None));

    let vbox = window
        .default_vbox()
        .context("No default vbox on auth window")?;

    let builder = WebViewBuilder::new()
        .with_url(login_url)
        .with_on_page_load_handler({
            let state = state.clone();
            let proxy = proxy.clone();
            let verifier = verifier.clone();
            let login_handled = login_handled.clone();
            let consent_tokens = consent_tokens.clone();

            move |event, url| {
                let event_name = if matches!(event, wry::PageLoadEvent::Started) {
                    "started"
                } else {
                    "finished"
                };
                log::info!("Auth page load ({}): {}", event_name, &url[..url.len().min(120)]);

                // Catch login redirect (step 1 of auth)
                if matches!(event, wry::PageLoadEvent::Started) {
                    if crate::auth::oauth::is_login_redirect(&url) {
                        if login_handled.swap(true, Ordering::SeqCst) {
                            return;
                        }
                        log::info!("Caught login redirect!");
                        match crate::auth::oauth::extract_auth_code(&url) {
                            Ok(code) => {
                                let state = state.clone();
                                let proxy = proxy.clone();
                                let verifier = verifier.clone();
                                let consent_tokens = consent_tokens.clone();

                                tokio::spawn(async move {
                                    handle_code_exchange(
                                        code,
                                        verifier,
                                        state,
                                        proxy,
                                        consent_tokens,
                                    )
                                    .await;
                                });
                            }
                            Err(e) => {
                                login_handled.store(false, Ordering::SeqCst);
                                log::error!("Failed to extract auth code: {}", e);
                            }
                        }
                        return;
                    }
                }

                // Fallback consent capture: if port 80 server couldn't bind,
                // consent_tokens will be Some and we try to extract id_token
                // from the URL fragment of the http://localhost redirect.
                if crate::auth::oauth::is_consent_redirect(&url) {
                    log::info!("Detected consent redirect URL in page load handler");
                    if let Some(id_token) =
                        crate::auth::oauth::extract_id_token_from_fragment(&url)
                    {
                        if let Some(tokens) = consent_tokens.lock().unwrap().take() {
                            log::info!("Extracted id_token from URL fragment (fallback path)");
                            let state = state.clone();
                            let proxy = proxy.clone();
                            tokio::spawn(async move {
                                handle_consent_complete(id_token, tokens, state, proxy).await;
                            });
                        }
                    } else {
                        log::warn!(
                            "Consent redirect detected but no id_token in fragment. URL: {}",
                            url
                        );
                    }
                }
            }
        })
        .with_devtools(cfg!(debug_assertions));

    let webview = builder
        .build_gtk(vbox)
        .context("Failed to create auth webview")?;

    Ok(webview)
}

async fn handle_code_exchange(
    code: String,
    verifier: String,
    state: Arc<IpcState>,
    proxy: EventLoopProxy<UserEvent>,
    consent_tokens: Arc<Mutex<Option<crate::auth::types::AuthTokens>>>,
) {
    log::info!("Exchanging auth code for tokens...");
    let client = reqwest::Client::new();
    let tokens = match crate::auth::oauth::exchange_code(&client, &code, &verifier).await {
        Ok(t) => t,
        Err(e) => {
            log::error!("Token exchange failed: {}", e);
            state.send_event(&super::ipc::IpcEvent::LoginError {
                message: format!("Token exchange failed: {}", e),
            });
            return;
        }
    };

    log::info!("Token exchange successful, building consent URL...");
    let nonce = crate::auth::oauth::generate_pkce().state;
    let consent_url = crate::auth::oauth::build_consent_url(&tokens.id_token, &nonce);
    log::info!(
        "Consent URL: {}",
        &consent_url[..consent_url.len().min(120)]
    );

    // Try the primary path: callback server on port 80
    match crate::auth::oauth::start_consent_callback_server() {
        Ok(join_handle) => {
            // Navigate auth webview to consent URL
            let _ = proxy.send_event(UserEvent::EvalScript(format!(
                "__AUTH_NAVIGATE__:{}",
                consent_url
            )));

            // Wait for the callback server to receive the id_token
            let consent_id_token =
                match tokio::task::spawn_blocking(move || join_handle.join()).await {
                    Ok(Ok(Ok(token))) => token,
                    Ok(Ok(Err(e))) => {
                        log::error!("Consent callback server failed: {}", e);
                        state.send_event(&super::ipc::IpcEvent::LoginError {
                            message: format!("Consent callback failed: {}", e),
                        });
                        return;
                    }
                    _ => {
                        log::error!("Consent callback thread panicked");
                        state.send_event(&super::ipc::IpcEvent::LoginError {
                            message: "Consent callback failed unexpectedly".to_string(),
                        });
                        return;
                    }
                };

            log::info!("Consent callback server received id_token");
            handle_consent_complete(consent_id_token, tokens, state, proxy).await;
        }
        Err(e) => {
            // Port 80 unavailable — fall back to page load handler
            log::warn!(
                "Can't bind port 80 ({}). Falling back to page load URL capture. \
                 If this fails, grant CAP_NET_BIND_SERVICE or set \
                 net.ipv4.ip_unprivileged_port_start=80",
                e
            );

            // Store tokens so the page load handler can complete the flow
            *consent_tokens.lock().unwrap() = Some(tokens);

            // Navigate auth webview to consent URL — the page load handler
            // will try to extract id_token from the redirect URL fragment
            let _ = proxy.send_event(UserEvent::EvalScript(format!(
                "__AUTH_NAVIGATE__:{}",
                consent_url
            )));
        }
    }
}

async fn handle_consent_complete(
    consent_id_token: String,
    tokens: crate::auth::types::AuthTokens,
    state: Arc<IpcState>,
    proxy: EventLoopProxy<UserEvent>,
) {
    log::info!("Creating game session...");
    let client = reqwest::Client::new();

    let session_id = match crate::auth::session::create_session(&client, &consent_id_token).await {
        Ok(id) => {
            log::info!("Game session created: {}", &id[..id.len().min(20)]);
            id
        }
        Err(e) => {
            log::error!("Session creation failed: {}", e);
            state.send_event(&super::ipc::IpcEvent::LoginError {
                message: format!("Session creation failed: {}", e),
            });
            return;
        }
    };

    log::info!("Fetching user info...");
    let user = match crate::auth::user::get_user(&client, &tokens.sub, &tokens.access_token).await
    {
        Ok(u) => {
            log::info!("User: {}", u.display_name);
            u
        }
        Err(e) => {
            log::error!("User fetch failed: {}", e);
            state.send_event(&super::ipc::IpcEvent::LoginError {
                message: format!("User fetch failed: {}", e),
            });
            return;
        }
    };

    log::info!("Fetching game accounts...");
    let accounts = match crate::auth::user::get_accounts(&client, &session_id).await {
        Ok(a) => {
            log::info!("Found {} game accounts", a.len());
            a
        }
        Err(e) => {
            log::error!("Accounts fetch failed: {}", e);
            state.send_event(&super::ipc::IpcEvent::LoginError {
                message: format!("Accounts fetch failed: {}", e),
            });
            return;
        }
    };

    let session = crate::auth::types::Session {
        user,
        accounts,
        tokens,
        session_id,
        consent_id_token: Some(consent_id_token),
    };

    state.add_session(session);
    log::info!("Login complete! Closing auth window.");
    let _ = proxy.send_event(UserEvent::EvalScript("__AUTH_CLOSE__".to_string()));
}
