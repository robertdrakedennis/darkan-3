# Launcher / Patcher Hardening (2026-06-12)

Audit fix pass over `client/launcher/**`. All changes inside the launcher domain.

## Key architecture notes discovered/established
- **Shared HTTP client**: `crate::http_client()` in `src/main.rs` — `OnceLock<reqwest::Client>`,
  cheap clone (Arc-backed). Reuse everywhere (auth chain keep-alive). NEVER `reqwest::Client::new()`.
- **Frontend init is `ready`-driven**, not timer-driven. `ui/app.js` posts `{type:"ready"}` at end of
  IIFE; `IpcState::handle_message` → `IpcMessage::Ready` replies with `IpcEvent::Init{config,sessions}`
  via `send_event`. No more 200ms sleep race in main.rs.
- **Launch guard**: `IpcState.launching: Arc<AtomicBool>`. `try_begin_launch()` returns a `LaunchGuard`
  (RAII, clears flag on Drop) or None → reject with LaunchError "A launch is already in progress."
  Shared by BOTH `handle_launch` (live/proxy) and `handle_launch_custom`.
- **Config persistence** (`src/config.rs`): `atomic_write` = temp file in same dir (0600 unix) → fsync →
  rename over target; parent dir forced 0700. `load_or_default` distinguishes absent(→default) vs
  unparseable(→error log + rename to `<name>.bak`/`.bak.N` + default). Applies to config.json + creds.json.
  `ensure_dirs` chmods config_dir + data_dir to 0700.
- **deb extraction** (`src/game/deb.rs`): `extract_rs3_binary(impl AsRef<[u8]>, &Path)` — accepts
  `bytes::Bytes` (no Vec copy). Writes temp file + atomic rename (no ETXTBSY, no truncation window).
- `download_deb` returns `bytes::Bytes` (added `bytes` dep to launcher Cargo.toml).
- Blocking work off async workers: xz/tar extraction → `tokio::task::spawn_blocking`; simple fs ops in
  custom-launch seeding → `tokio::fs::{create_dir_all,copy,write}`.
- **logout** revokes the access token best-effort fire-and-forget (`auth::oauth::revoke_token`, spawned,
  never blocks). `revoke_token` is now live code (removed `#[allow(dead_code)]`).
- Log redaction: webview.rs logs "Game session created" (no session-id prefix); user.rs logs only byte
  count of accounts body (was full body incl. userHash), error context no longer echoes body.

## Patcher (`patcher/src/lib.rs`) scanning rewrite
- Added `memchr` dep. Pattern scan uses `memchr::memmem::Finder` (built once per pattern). Replaced the
  O(n·m) `scan_for_pattern`. Helpers: `find_first_in_region`, `find_first`, `find_all`,
  `find_first_with_fallback`.
- **all_regions fallback pool is now LAZY** — built only on first binary-specific miss
  (`find_first_with_fallback`), not unconditionally. Matters: runs in LD_PRELOAD ctor every launch.
- RSA login + JS5 modulus patches use `find_first_with_fallback` (binary regions → lazy all-file-backed).
  rs3linux RSA / codebase regex / LZMA flag scan binary regions only (unchanged scope).
- **HTTP port patch now patches ALL matches**, not just the first. `find_all` collects every match,
  patches each, logs count + each offset, warns loudly on 0. The 7-byte sig `41 b8 50 00 00 00 74`
  (MOV R8D,0x50 + JZ rel8) already rejects the documented false positives (graphics/matrix sites have a
  different +6 byte). 948 has 1 site; 947-3 had 2 — patch-all stays correct across revisions. No hard
  "exactly one" assert (would break multi-site revisions). Ref: docs/binary/patch-targets-948.md P4.
- `pad_modulus_hex(hex, len)` dedupes the 3 clean-hex/zero-pad blocks (login/JS5/rs3linux moduli).
- L5 ipc dedup: `send_webview_event(cmd_tx, event)` free fn behind the `send_status`/`send_error`
  closures; `do_launch_live(...)` free fn behind the two identical live launch call sites (has
  `#[allow(clippy::too_many_arguments)]` — 9 args).

## Verification
- `cargo build --release` (launcher): clean except pre-existing `mime_type` unused (assets.rs, untouched).
- `patcher/build.sh`: exit 0, all 10 deploy slots verified (948 marker present, 947 absent).
- patcher `cargo test`: 6/6 pass (added `test_pad_modulus_hex`).
- All clippy warnings are pre-existing (ServerMode Default derive, launch_rs3 arity, op_ref deb.rs:51,
  manual_strip main.rs, collapsible_if webview.rs, patcher map_or/`%2`). Introduced none; tightened the
  4 config fn signatures `&PathBuf`→`&Path`.
