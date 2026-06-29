//! Process-global recorder state: the active `Session`, the resolved client
//! image, and connection-identity resolution (login vs game).
//!
//! Initialised once from the ctor. Everything is read through small accessors
//! so the hooks and the libc interposers share one source of truth and we never
//! touch a half-initialised struct.

use crate::mem;
use crate::offsets::{
    client as oc, connection_manager as ocm, login_state_machine as olsm, server_connection as osc,
};
use crate::session::Session;
use once_cell::sync::OnceCell;
use parking_lot::Mutex;
use std::sync::atomic::{AtomicBool, AtomicIsize, AtomicU64, Ordering};

static SESSION: OnceCell<Session> = OnceCell::new();

/// The runtime base of the client struct (`*CLIENT` global). We resolve the
/// Client lazily from a live ServerConnection's owner is not possible, so the
/// hooks that hold a ConnectionManager `this` pointer publish the Client base
/// here the first time they see it (TcpIn-adjacent reads). Until then,
/// state-tagging falls back to "unknown".
static CLIENT_BASE: Mutex<usize> = Mutex::new(0);

/// Connections observed via connect() (for session.json `connections`).
static CONNECTIONS: Mutex<Vec<(String, u16)>> = Mutex::new(Vec::new());

/// EARLIEST login `ClientStream*` (RE §8a). Published by the `OpenLoginStream`
/// hook from `*(*(LoginStateMachine+0x30)) + 0x08` the instant the login socket
/// is created — BEFORE ConnMgr+0x28 (LOGIN_CONNECTION) resolves a beat later. The
/// `Fill`/`ClientStream` role taggers consult this so the very first login s2c
/// recv is tagged "login" instead of being mis-bucketed as "js5".
static EARLY_LOGIN_STREAM: Mutex<usize> = Mutex::new(0);

/// Running count of login-direction s2c bytes observed on the socket plane. Used
/// to stamp the `login_cipher_ready` marker with the Phase A (plaintext) byte
/// length so the offline deframer can split the login s2c stream at the boundary.
static LOGIN_S2C_BYTES: AtomicU64 = AtomicU64::new(0);

/// Set once the `login_cipher_ready` boundary marker has been emitted, so the
/// SetupLoginCiphers hook publishes it exactly once per session.
static LOGIN_CIPHER_MARKED: AtomicBool = AtomicBool::new(false);

/// Monotonically-increasing client-state snapshot sequence number (the `tick`
/// field). The client exposes no stable tick counter at a documented offset, so
/// this recorder-side sequence both orders the snapshots and lets the verifier
/// identify the final/at-exit one (highest tick).
static SNAPSHOT_SEQ: AtomicU64 = AtomicU64::new(0);

/// Last snapshot's monotonic timestamp (ns) for the periodic time gate. 0 = none
/// taken yet (so the first eligible call always snapshots).
static LAST_SNAPSHOT_NS: AtomicU64 = AtomicU64::new(0);

/// Minimum spacing between PERIODIC snapshots. ~1s keeps `state-snapshots.jsonl`
/// small while still tracking varp/skill/energy changes closely enough for the
/// cross-check. Overridable via `DARKAN_SNAPSHOT_INTERVAL_MS` (clamped sane).
const DEFAULT_SNAPSHOT_INTERVAL_MS: u64 = 1000;
const MIN_SNAPSHOT_INTERVAL_MS: u64 = 100;
const MAX_SNAPSHOT_INTERVAL_MS: u64 = 60_000;

fn snapshot_interval_ns() -> u64 {
    let ms = std::env::var("DARKAN_SNAPSHOT_INTERVAL_MS")
        .ok()
        .and_then(|s| s.trim().parse::<u64>().ok())
        .map(|v| v.clamp(MIN_SNAPSHOT_INTERVAL_MS, MAX_SNAPSHOT_INTERVAL_MS))
        .unwrap_or(DEFAULT_SNAPSHOT_INTERVAL_MS);
    ms * 1_000_000
}

#[inline]
fn mono_ns() -> u64 {
    let mut ts = libc::timespec {
        tv_sec: 0,
        tv_nsec: 0,
    };
    unsafe {
        libc::clock_gettime(libc::CLOCK_UPTIME_RAW, &mut ts);
    }
    (ts.tv_sec as u64) * 1_000_000_000 + (ts.tv_nsec as u64)
}

/// Take the next snapshot sequence number (the `tick` value).
fn next_snapshot_seq() -> u64 {
    SNAPSHOT_SEQ.fetch_add(1, Ordering::Relaxed)
}

/// Emit a client-state oracle snapshot NOW (RE §10), unconditionally. Used for
/// the authoritative at-exit snapshot. Reads every field defensively off the
/// published client base; a null/unreadable chain just omits that field.
pub fn emit_state_snapshot() {
    let Some(s) = session() else { return };
    let snap = crate::oracle::snapshot(client_base());
    s.state_snapshot(next_snapshot_seq(), &snap);
}

/// Emit a PERIODIC client-state snapshot if the time gate has elapsed (cheap to
/// call from a hot hook — it does an atomic time check first and only builds a
/// snapshot when due). The first eligible call always snapshots. No-op until the
/// client base is known (nothing to read pre-login).
pub fn maybe_emit_state_snapshot() {
    if session().is_none() || client_base() == 0 {
        return;
    }
    let now = mono_ns();
    let last = LAST_SNAPSHOT_NS.load(Ordering::Relaxed);
    if last != 0 && now.saturating_sub(last) < snapshot_interval_ns() {
        return;
    }
    // Claim this slot: only the thread that wins the CAS takes the snapshot, so a
    // burst of hook fires across threads still produces one snapshot per interval.
    if LAST_SNAPSHOT_NS
        .compare_exchange(last, now, Ordering::SeqCst, Ordering::Relaxed)
        .is_err()
    {
        return; // another thread just took it
    }
    emit_state_snapshot();
}

/// The main image's dyld vmaddr slide, published once at ctor (rs2client only).
/// `prot_table::try_dump` needs only the slide to locate `g_serverProtTable`, so
/// caching it here lets the hot-hook retry path attempt the dump without
/// threading the whole `MainImage` through every hook. Sentinel `isize::MIN` =
/// "not published" (a real slide can be negative but never `isize::MIN`).
static IMAGE_SLIDE: AtomicIsize = AtomicIsize::new(isize::MIN);

/// Publish the main image's vmaddr slide (rs2client ctor). First write wins.
pub fn publish_image_slide(slide: isize) {
    let _ = IMAGE_SLIDE.compare_exchange(isize::MIN, slide, Ordering::SeqCst, Ordering::Relaxed);
}

/// Attempt the one-shot live prot-table dump if the image slide is known. No-op
/// once dumped or before the slide is published. Cheap and idempotent — safe to
/// call from the hot hooks: it bails fast until the client's `ServerProt::
/// RegisterAll` has populated `g_serverProtTable`, then writes `prot-table.json`
/// exactly once. The actual once-guard lives in `prot_table::try_dump`.
pub fn maybe_dump_prot_table() {
    let slide = IMAGE_SLIDE.load(Ordering::Relaxed);
    if slide != isize::MIN {
        crate::prot_table::try_dump(slide);
    }
}

/// Build string + server mode, captured at ctor for session.json rewrites.
static META: Mutex<Option<Meta>> = Mutex::new(None);

struct Meta {
    build: String,
    server_mode: String,
    wrapper_pid: Option<u32>,
}

pub fn init_session(session: Session) {
    let _ = SESSION.set(session);
}

pub fn session() -> Option<&'static Session> {
    SESSION.get()
}

pub fn is_recording() -> bool {
    SESSION.get().is_some()
}

pub fn set_meta(build: String, server_mode: String, wrapper_pid: Option<u32>) {
    *META.lock() = Some(Meta {
        build,
        server_mode,
        wrapper_pid,
    });
}

/// The client build string captured at ctor (e.g. `RS2Engine-948-NXT-5`), or
/// `"unknown"` if meta has not been set yet. Used to stamp `prot-table.json`.
pub fn build_string() -> String {
    META.lock()
        .as_ref()
        .map(|m| m.build.clone())
        .unwrap_or_else(|| "unknown".to_string())
}

pub fn note_connection(ip: &str, port: u16) {
    let mut c = CONNECTIONS.lock();
    if !c.iter().any(|(i, p)| i == ip && *p == port) {
        c.push((ip.to_string(), port));
    }
}

pub fn publish_client_base(base: usize) {
    if mem::is_plausible(base) {
        let mut g = CLIENT_BASE.lock();
        if *g == 0 {
            *g = base;
        }
    }
}

pub fn client_base() -> usize {
    *CLIENT_BASE.lock()
}

/// Current MAIN_STATE, or -1 if the client base is unknown / unreadable.
pub fn main_state() -> i32 {
    let base = client_base();
    if base == 0 {
        return -1;
    }
    mem::read_i32(base, oc::MAIN_STATE).unwrap_or(-1)
}

/// Resolve the ConnectionManager from the published client base.
pub fn connection_manager() -> Option<usize> {
    let base = client_base();
    if base == 0 {
        return None;
    }
    mem::deref(base, oc::CONNECTION_MANAGER)
}

/// The GAME `ServerConnection*` (ConnMgr+0x18), if resolvable.
pub fn game_conn() -> Option<usize> {
    let cm = connection_manager()?;
    mem::deref(cm, ocm::GAME_CONNECTION)
}

/// The LOGIN `ServerConnection*`, resolved via BOTH paths (RE §8a):
///   1. primary: ConnMgr+0x28 (LOGIN_CONNECTION);
///   2. cross-check: `*(Client+0x19720 /*LoginStateMachine*/ + 0x30)` — the same
///      object, but populated independently during connect. Either hit counts,
///      so the login connection is recognised as early as EITHER resolves.
pub fn login_conn() -> Option<usize> {
    if let Some(cm) = connection_manager() {
        if let Some(c) = mem::deref(cm, ocm::LOGIN_CONNECTION) {
            return Some(c);
        }
    }
    // Cross-check through the LoginStateMachine at Client+0x19720.
    let base = client_base();
    if base != 0 {
        if let Some(lsm) = mem::deref(base, oc::LOGIN_STATE_MACHINE) {
            if let Some(c) = mem::deref(lsm, olsm::LOGIN_CONNECTION) {
                return Some(c);
            }
        }
    }
    None
}

/// The GAME `ClientStream*` (game conn → CLIENT_STREAM), if resolvable.
pub fn game_stream() -> Option<usize> {
    mem::deref(game_conn()?, osc::CLIENT_STREAM)
}

/// The LOGIN `ClientStream*`, resolved via THREE paths in priority order (RE §8a):
///   1. the EARLY registry published by `OpenLoginStream` at socket-creation time
///      (the earliest — before ConnMgr+0x28 or even the LoginStateMachine field is
///      readable from our side);
///   2. ConnMgr+0x28 (LOGIN_CONNECTION) → CLIENT_STREAM;
///   3. LoginStateMachine+0x30 → CLIENT_STREAM.
/// This is what lets the Fill hook tag the login stream "login" the instant ANY
/// path is populated, instead of mis-bucketing the early login s2c as "js5".
pub fn login_stream() -> Option<usize> {
    let early = *EARLY_LOGIN_STREAM.lock();
    if mem::is_plausible(early) {
        return Some(early);
    }
    mem::deref(login_conn()?, osc::CLIENT_STREAM)
}

/// Publish the earliest login `ClientStream*` (from the `OpenLoginStream` hook).
/// First plausible value wins (frozen) so a later reconnect cannot move it.
pub fn register_login_stream(stream: usize) {
    if mem::is_plausible(stream) {
        let mut g = EARLY_LOGIN_STREAM.lock();
        if *g == 0 {
            *g = stream;
        }
    }
}

/// Add to the running login-s2c socket byte count and return the new total. Called
/// from the socket capture path whenever login-direction s2c bytes are recorded,
/// so the `login_cipher_ready` marker can stamp the Phase-A (plaintext) length.
pub fn add_login_s2c_bytes(n: usize) -> u64 {
    LOGIN_S2C_BYTES.fetch_add(n as u64, Ordering::Relaxed) + n as u64
}

/// The login-s2c bytes observed so far (the Phase-A length at the cipher boundary).
pub fn login_s2c_bytes() -> u64 {
    LOGIN_S2C_BYTES.load(Ordering::Relaxed)
}

/// Returns true exactly once — the first call wins. Used by the SetupLoginCiphers
/// hook to emit the `login_cipher_ready` boundary marker a single time.
pub fn take_login_cipher_marker() -> bool {
    !LOGIN_CIPHER_MARKED.swap(true, Ordering::SeqCst)
}

/// Tag a ServerConnection pointer as "login" / "game" / "js5" by comparing it
/// against the ConnectionManager's GAME_CONNECTION / LOGIN_CONNECTION slots
/// (login also via the LoginStateMachine cross-check, RE §8a).
///
/// Per RE §9 there are exactly two ClientStream producers: the game/login
/// `ServerConnection` and the HTTP cache pump. A ServerConnection that is
/// neither game nor login is therefore the cache transport ⇒ "js5". We only
/// fall back to "unknown" while the ConnectionManager itself is still
/// unresolved (pre-login), so the verifier never mislabels a known plane.
pub fn conn_role(conn: usize) -> &'static str {
    // Resolve the ConnectionManager once; if it isn't up yet we can still tag a
    // login connection via the LoginStateMachine cross-check (early connect).
    let cm = connection_manager();
    if let Some(cm) = cm {
        if mem::read_ptr(cm, ocm::GAME_CONNECTION) == Some(conn) {
            return "game";
        }
    }
    if login_conn() == Some(conn) {
        return "login";
    }
    match cm {
        // ConnMgr is up and the conn is neither game nor login ⇒ cache pump.
        Some(_) => "js5",
        // ConnMgr not up yet and not the (cross-checked) login conn ⇒ unknown.
        None => "unknown",
    }
}

// ---------------------------------------------------------------------------
// ISAAC seed capture — EVERY `Isaac::Init`, associated to its {conn, dir}.
//
// THE BUG THIS FIXES: each TCP connection (login AND game/world) builds its OWN
// pair of ISAAC ciphers, so `Isaac::Init` fires multiple times per session with
// DIFFERENT 4-int session keys. The previous code kept only the FIRST seed (the
// lobby/login send seed), so an offline deframe of GAME traffic had the wrong
// keystream and could never agree (brute-force confirmed: the captured seed
// matched no captured stream). We now capture EVERY call — both the 4 seed ints
// (RSI[0..4]) AND the ISAAC state pointer (RDI) — and associate each captured
// state pointer with a {conn, dir} by matching it against each connection's
// cipher-state pointers:
//   send (c2s/OUT) = `conn + 0x40`  (ISAAC_OUT_PTR)
//   recv (s2c/IN)  = `conn + 0x2B8` (ISAAC_IN_PTR)
// for BOTH the game connection (ConnMgr+0x18) and the login connection
// (ConnMgr+0x28). Per RE §7 the recv seed is the send seed + 50 per int, which
// we also use as a direction tie-breaker when only one connection is resolvable.
//
// The match is LAZY: at `Isaac::Init` time the connection's `conn+0x40` /
// `conn+0x2B8` field may not yet hold the just-built state pointer (the setup
// function stores it after constructing the ISAAC). So we keep every
// `(state_ptr, seed)` capture and reconcile on demand (`reconcile_isaac_seeds`)
// whenever the connections + their cipher pointers become readable — at the
// flush/emit hooks and at session stop. Once a (conn, dir) is resolved it is
// frozen (first match wins) so a later reconnect re-seed cannot overwrite it.
// ---------------------------------------------------------------------------

/// Send-side (c2s / OUT) ISAAC state pointer field on a `ServerConnection`.
const ISAAC_OUT_PTR: usize = osc::ISAAC_OUT_PTR; // 0x40
/// Recv-side (s2c / IN) ISAAC state pointer field on a `ServerConnection`.
const ISAAC_IN_PTR: usize = osc::ISAAC_IN_PTR; // 0x2B8
/// Per RE §7: the IN/RECV cipher is seeded with each of the 4 send-seed ints
/// plus this delta (32-bit wrap). Used as a direction tie-breaker.
const S2C_ISAAC_DELTA: i32 = 50;

/// A connection role + direction the seed belongs to. The string tag is what
/// the offline `CrossValidator` selects on (`game-s2c`, `game-c2s`,
/// `login-s2c`, `login-c2s`).
#[derive(Clone, Copy, PartialEq, Eq)]
enum SeedRole {
    GameRecv,  // game-s2c (conn+0x2B8 on the game connection)
    GameSend,  // game-c2s (conn+0x40  on the game connection)
    LoginRecv, // login-s2c (conn+0x2B8 on the login connection)
    LoginSend, // login-c2s (conn+0x40  on the login connection)
}

impl SeedRole {
    /// The `<role>-<dir>` tag written to `isaac-keys.txt` and selected on by the
    /// offline deframer.
    fn tag(self) -> &'static str {
        match self {
            SeedRole::GameRecv => "game-s2c",
            SeedRole::GameSend => "game-c2s",
            SeedRole::LoginRecv => "login-s2c",
            SeedRole::LoginSend => "login-c2s",
        }
    }
}

/// One raw `Isaac::Init` capture: the ISAAC state pointer it seeded (RDI) and
/// the 4 seed ints (RSI[0..4]). Recorded for EVERY call, in arrival order.
#[derive(Clone, Copy)]
struct SeedCapture {
    state_ptr: usize,
    seed: [i32; 4],
}

/// All `Isaac::Init` captures seen this session, in arrival order. Bounded — a
/// session realistically produces a handful (2 per connection setup); the cap
/// just stops an adversarial/looping client from growing this unboundedly.
static SEED_CAPTURES: Mutex<Vec<SeedCapture>> = Mutex::new(Vec::new());

/// The reconciled {role -> 4-int seed} map. Each entry is frozen once set (first
/// resolution wins) so a reconnect re-seed cannot clobber a known role. The
/// stored seed is EXACTLY the ints `Isaac::Init` received for that (conn, dir):
/// a `*Send` (c2s) entry holds the raw send keys; a `*Recv` (s2c) entry holds the
/// recv keys, which already include the `+50` per int. So the offline deframer
/// must use each line's values VERBATIM for its plane and must NOT add 50 again.
static SEED_BY_ROLE: Mutex<[Option<[i32; 4]>; 4]> = Mutex::new([None; 4]);

const MAX_SEED_CAPTURES: usize = 64;

#[inline]
fn role_index(role: SeedRole) -> usize {
    match role {
        SeedRole::GameRecv => 0,
        SeedRole::GameSend => 1,
        SeedRole::LoginRecv => 2,
        SeedRole::LoginSend => 3,
    }
}

const ALL_SEED_ROLES: [SeedRole; 4] = [
    SeedRole::GameRecv,
    SeedRole::GameSend,
    SeedRole::LoginRecv,
    SeedRole::LoginSend,
];

/// Record one `Isaac::Init` call: the seeded ISAAC state pointer (RDI) and the 4
/// seed ints (RSI). EVERY call is kept (deduped by state pointer) so both the
/// login and the game/world connections' cipher seeds are captured. We attempt a
/// reconciliation immediately (cheap, and the connections may already be wired),
/// and again lazily from the emit/stop paths.
pub fn record_isaac_seed(state_ptr: usize, seed: [i32; 4]) {
    if mem::is_plausible(state_ptr) {
        let mut caps = SEED_CAPTURES.lock();
        // Dedup on the state pointer: a re-seed of the SAME ISAAC reuses the
        // same state buffer; keep the latest seed for that buffer rather than
        // appending duplicates.
        if let Some(existing) = caps.iter_mut().find(|c| c.state_ptr == state_ptr) {
            existing.seed = seed;
        } else if caps.len() < MAX_SEED_CAPTURES {
            caps.push(SeedCapture { state_ptr, seed });
        }
    }
    reconcile_isaac_seeds();
}

/// Associate every still-unresolved (conn, dir) with a captured seed by matching
/// the connection's cipher-state pointer (`conn+0x40` send / `conn+0x2B8` recv)
/// against the captured `Isaac::Init` state pointers. Idempotent and cheap;
/// called from `record_isaac_seed` and from the emit/stop paths so a seed
/// captured before its connection was wired still resolves later. First match
/// per role wins (frozen) so a reconnect re-seed cannot overwrite a known role.
pub fn reconcile_isaac_seeds() {
    let caps = SEED_CAPTURES.lock();
    if caps.is_empty() {
        return;
    }

    // Read each connection's two cipher-state pointers (may be None early; the
    // pure matcher just skips a plane whose pointer isn't readable yet). This is
    // the only step that touches live client memory — the matching itself is a
    // pure function so it can be unit-tested without a live client.
    let cipher = |conn: Option<usize>, field: usize| -> Option<usize> {
        conn.and_then(|c| mem::deref(c, field))
    };
    let game = game_conn();
    let login = login_conn();
    let ptrs = ConnCipherPtrs {
        game_send: cipher(game, ISAAC_OUT_PTR),
        game_recv: cipher(game, ISAAC_IN_PTR),
        login_send: cipher(login, ISAAC_OUT_PTR),
        login_recv: cipher(login, ISAAC_IN_PTR),
    };

    let mut by_role = SEED_BY_ROLE.lock();
    match_seeds(&caps, &ptrs, &mut by_role);
}

/// The four cipher-state pointers (`conn+0x40` send / `conn+0x2B8` recv for the
/// game and login connections), each `None` until the connection has wired it.
#[derive(Clone, Copy, Default)]
struct ConnCipherPtrs {
    game_send: Option<usize>,
    game_recv: Option<usize>,
    login_send: Option<usize>,
    login_recv: Option<usize>,
}

/// PURE seed→role matcher (no live memory): freeze each still-unresolved role to
/// the seed whose `Isaac::Init` state pointer equals that role's cipher pointer.
/// First match per role wins. Extracted from `reconcile_isaac_seeds` so the
/// matching invariants are unit-testable against synthetic captures.
///
/// Direction tie-breaker (RE §7): if a connection resolved its SEND seed by a
/// direct pointer match but its RECV pointer is not yet readable, we can still
/// recover the recv seed from the captures by the `recv = send + 50` per-int
/// relationship (the client seeds the recv ISAAC with the send keys each +50).
/// We only apply this when exactly one captured seed satisfies the relationship,
/// so it can never mis-assign.
fn match_seeds(caps: &[SeedCapture], ptrs: &ConnCipherPtrs, by_role: &mut [Option<[i32; 4]>; 4]) {
    let lookup = |state_ptr: usize| -> Option<[i32; 4]> {
        caps.iter()
            .find(|c| c.state_ptr == state_ptr)
            .map(|c| c.seed)
    };

    // (role, that role's cipher pointer) for each of the 4 planes.
    let planes: [(SeedRole, Option<usize>); 4] = [
        (SeedRole::GameSend, ptrs.game_send),
        (SeedRole::GameRecv, ptrs.game_recv),
        (SeedRole::LoginSend, ptrs.login_send),
        (SeedRole::LoginRecv, ptrs.login_recv),
    ];

    // Pass 1: direct pointer matches (the authoritative association).
    for (role, cipher_ptr) in planes {
        let idx = role_index(role);
        if by_role[idx].is_some() {
            continue; // already frozen
        }
        if let Some(p) = cipher_ptr {
            if let Some(seed) = lookup(p) {
                by_role[idx] = Some(seed);
            }
        }
    }

    // Pass 2: +50 tie-breaker. For a connection whose SEND seed is known but
    // whose RECV seed is still missing, recover RECV from the unique captured
    // seed that is `send + 50` per int.
    let recover_recv = |send: [i32; 4]| -> Option<[i32; 4]> {
        let mut hit = None;
        for c in caps {
            if (0..4).all(|i| c.seed[i] == send[i].wrapping_add(S2C_ISAAC_DELTA)) {
                if hit.is_some() {
                    return None; // ambiguous — refuse rather than mis-assign
                }
                hit = Some(c.seed);
            }
        }
        hit
    };
    for (send_role, recv_role) in [
        (SeedRole::GameSend, SeedRole::GameRecv),
        (SeedRole::LoginSend, SeedRole::LoginRecv),
    ] {
        let recv_idx = role_index(recv_role);
        if by_role[recv_idx].is_some() {
            continue;
        }
        if let Some(send) = by_role[role_index(send_role)] {
            if let Some(recv) = recover_recv(send) {
                by_role[recv_idx] = Some(recv);
            }
        }
    }
}

/// Snapshot the reconciled per-role seeds for the `isaac-keys.txt` writer.
/// Returns the raw 4-int seed for every role resolved so far, tagged with its
/// `<role>-<dir>` string. The recv-direction seeds are the RAW ints the client
/// seeded the recv ISAAC with (already `+50` vs the matching send seed); the
/// offline deframer must therefore use them VERBATIM (it should NOT add 50
/// again — see the writer/Kotlin contract in `session.rs::isaac_seeds`).
pub fn isaac_seeds_by_role() -> Vec<(&'static str, [i32; 4])> {
    // Reconcile once more so a late-resolving connection is picked up before we
    // snapshot (e.g. when called at session stop).
    reconcile_isaac_seeds();
    let by_role = SEED_BY_ROLE.lock();
    let mut out = Vec::new();
    for role in ALL_SEED_ROLES {
        if let Some(seed) = by_role[role_index(role)] {
            out.push((role.tag(), seed));
        }
    }
    out
}

/// Rewrite session.json with the connection list and (optionally) stop fields.
pub fn flush_session_json(stopped: bool) {
    let Some(s) = SESSION.get() else { return };
    let meta = META.lock();
    let (build, server_mode, wrapper_pid) = match meta.as_ref() {
        Some(m) => (m.build.clone(), m.server_mode.clone(), m.wrapper_pid),
        None => ("unknown".to_string(), "local".to_string(), None),
    };
    let conns = CONNECTIONS.lock();
    let conn_json: Vec<_> = conns
        .iter()
        .map(|(ip, port)| {
            serde_json::json!({
                "role": role_for_peer(ip, *port),
                "peer": format!("{ip}:{port}"),
                "port": port,
            })
        })
        .collect();
    s.write_session_json(
        &build,
        &server_mode,
        wrapper_pid,
        Some(serde_json::Value::Array(conn_json)),
        stopped,
    );
}

/// Best-effort role label for a peer in session.json. The live game/login slots
/// hold pointers, not peers; we can't always map a peer to a role from outside a
/// packet, so we use a port heuristic only as a hint (JS5/content is HTTP:80,
/// everything else is the game/login TCP). The authoritative per-packet role is
/// emitted on each framed line via `conn_role`.
fn role_for_peer(_ip: &str, port: u16) -> &'static str {
    match port {
        80 | 443 => "http",
        _ => "game-or-login",
    }
}

// ---------------------------------------------------------------------------
// Unit tests for the pure ISAAC seed→role matcher (the multi-connection fix).
// `match_seeds` is the load-bearing logic; it takes synthetic captures + cipher
// pointers so we can prove the association invariants without a live client.
// ---------------------------------------------------------------------------
#[cfg(test)]
mod seed_tests {
    use super::*;

    fn cap(ptr: usize, seed: [i32; 4]) -> SeedCapture {
        SeedCapture {
            state_ptr: ptr,
            seed,
        }
    }

    /// THE BUG FIX: each connection is seeded independently, so the game plane
    /// and the login plane must resolve to DIFFERENT seeds — not one shared seed.
    #[test]
    fn distinct_seed_per_connection_and_direction() {
        // Four distinct ISAAC state buffers, four distinct seeds (the raw send
        // seeds and their +50 recv counterparts for two connections).
        let login_send_seed: [i32; 4] = [0x11111111, 0x22222222, 0x33333333, 0x44444444];
        let login_recv_seed = login_send_seed.map(|k| k.wrapping_add(S2C_ISAAC_DELTA));
        let game_send_seed = [
            0x0a0b0c0du32 as i32,
            0x10203040,
            0x50607080u32 as i32,
            0x0badf00du32 as i32,
        ];
        let game_recv_seed = game_send_seed.map(|k| k.wrapping_add(S2C_ISAAC_DELTA));

        // Distinct state-buffer pointers (the RDI of each Isaac::Init call).
        let (login_send_ptr, login_recv_ptr) = (0x1000, 0x2000);
        let (game_send_ptr, game_recv_ptr) = (0x3000, 0x4000);

        let caps = vec![
            cap(login_send_ptr, login_send_seed),
            cap(login_recv_ptr, login_recv_seed),
            cap(game_send_ptr, game_send_seed),
            cap(game_recv_ptr, game_recv_seed),
        ];
        let ptrs = ConnCipherPtrs {
            game_send: Some(game_send_ptr),
            game_recv: Some(game_recv_ptr),
            login_send: Some(login_send_ptr),
            login_recv: Some(login_recv_ptr),
        };

        let mut by_role = [None; 4];
        match_seeds(&caps, &ptrs, &mut by_role);

        assert_eq!(
            by_role[role_index(SeedRole::GameSend)],
            Some(game_send_seed)
        );
        assert_eq!(
            by_role[role_index(SeedRole::GameRecv)],
            Some(game_recv_seed)
        );
        assert_eq!(
            by_role[role_index(SeedRole::LoginSend)],
            Some(login_send_seed)
        );
        assert_eq!(
            by_role[role_index(SeedRole::LoginRecv)],
            Some(login_recv_seed)
        );
        // The game seed must NOT equal the login seed — the whole point of the fix.
        assert_ne!(
            by_role[role_index(SeedRole::GameSend)],
            by_role[role_index(SeedRole::LoginSend)],
        );
    }

    /// A seed captured before its connection's cipher pointer is readable must
    /// still resolve on a LATER reconcile once the pointer appears (the deferred /
    /// lazy path). We model it as two `match_seeds` passes over a growing pointer
    /// set, with the role frozen after the first hit.
    #[test]
    fn deferred_resolution_picks_up_late_connection() {
        let game_send_seed = [1, 2, 3, 4];
        let game_send_ptr = 0x5000;
        let caps = vec![cap(game_send_ptr, game_send_seed)];

        let mut by_role = [None; 4];
        // 1st pass: the game connection isn't wired yet → nothing resolves.
        match_seeds(&caps, &ConnCipherPtrs::default(), &mut by_role);
        assert_eq!(by_role[role_index(SeedRole::GameSend)], None);

        // 2nd pass: the connection now exposes its send cipher pointer → resolves.
        let ptrs = ConnCipherPtrs {
            game_send: Some(game_send_ptr),
            ..Default::default()
        };
        match_seeds(&caps, &ptrs, &mut by_role);
        assert_eq!(
            by_role[role_index(SeedRole::GameSend)],
            Some(game_send_seed)
        );
    }

    /// The +50 tie-breaker recovers the recv seed when the send seed resolved by
    /// pointer but the recv cipher pointer isn't readable (RE §7: recv = send+50).
    #[test]
    fn plus_fifty_recovers_recv_when_only_send_pointer_known() {
        let send_seed: [i32; 4] = [100, 200, 300, 400];
        let recv_seed = send_seed.map(|k| k.wrapping_add(S2C_ISAAC_DELTA));
        let send_ptr = 0x6000;
        let recv_ptr = 0x7000; // captured, but the conn hasn't wired recv yet
        let caps = vec![cap(send_ptr, send_seed), cap(recv_ptr, recv_seed)];

        // Only the game SEND cipher pointer is readable; recv is None.
        let ptrs = ConnCipherPtrs {
            game_send: Some(send_ptr),
            ..Default::default()
        };
        let mut by_role = [None; 4];
        match_seeds(&caps, &ptrs, &mut by_role);

        assert_eq!(by_role[role_index(SeedRole::GameSend)], Some(send_seed));
        // Recovered via the +50 relationship, not a pointer match.
        assert_eq!(by_role[role_index(SeedRole::GameRecv)], Some(recv_seed));
    }

    /// First match per role is frozen: a reconnect re-seed (new pointer, new seed)
    /// must NOT overwrite an already-resolved role.
    #[test]
    fn first_match_per_role_is_frozen() {
        let first_seed = [9, 9, 9, 9];
        let first_ptr = 0x8000;
        let mut by_role = [None; 4];
        match_seeds(
            &[cap(first_ptr, first_seed)],
            &ConnCipherPtrs {
                game_send: Some(first_ptr),
                ..Default::default()
            },
            &mut by_role,
        );
        assert_eq!(by_role[role_index(SeedRole::GameSend)], Some(first_seed));

        // Reconnect: the connection now points at a DIFFERENT (re-seeded) buffer.
        let second_seed = [7, 7, 7, 7];
        let second_ptr = 0x9000;
        match_seeds(
            &[cap(first_ptr, first_seed), cap(second_ptr, second_seed)],
            &ConnCipherPtrs {
                game_send: Some(second_ptr),
                ..Default::default()
            },
            &mut by_role,
        );
        // Still the FIRST seed — frozen.
        assert_eq!(by_role[role_index(SeedRole::GameSend)], Some(first_seed));
    }

    #[test]
    fn role_tags_are_the_kotlin_contract() {
        assert_eq!(SeedRole::GameRecv.tag(), "game-s2c");
        assert_eq!(SeedRole::GameSend.tag(), "game-c2s");
        assert_eq!(SeedRole::LoginRecv.tag(), "login-s2c");
        assert_eq!(SeedRole::LoginSend.tag(), "login-c2s");
    }
}
