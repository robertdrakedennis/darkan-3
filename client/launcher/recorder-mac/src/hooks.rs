//! Inline detours for the documented capture points.
//!
//! Every hook follows the engine's defensive contract:
//!   * call the trampoline (original) — never skip or alter the client's logic;
//!   * read fields only through `mem::*` null-checked helpers;
//!   * wrap the capture body so a read error can never unwind into the client
//!     (panic = abort is set, but we also `catch`-style guard via Option chains).
//!
//! Mechanism: our own iced-x86 trampoline engine (`detour.rs`) — the
//! self-contained analogue of the engine's funchook. Each target is resolved by
//! sig-scan at ctor time; we install an inline detour and keep the trampoline
//! (the callable original) in a `OnceCell` per hook.

use crate::detour::{self, Detour, Regs};
use crate::mem;
use crate::offsets::{
    self, client as oc, client_stream as ocs, connection_manager as ocm,
    login_state_machine as olsm, packet as op, queue_node as oq, server_connection as osc,
};
use crate::sig::{self, MainImage};
use crate::state;
use once_cell::sync::OnceCell;
use std::os::raw::c_void;
use std::sync::atomic::{AtomicBool, Ordering};

// -- Typed target signatures (System V AMD64; same arg order as Linux) --------

type FnReadPacket = unsafe extern "C" fn(this: *mut c_void, msg_ctx: *mut c_void) -> u64;
type FnTcpMsgInit = unsafe extern "C" fn(
    packet: *mut c_void,
    opcode_desc: *const i32,
    size_class: u32,
    isaac_out: *mut c_void,
);
type FnFlushQueue = unsafe extern "C" fn(this: *mut c_void) -> u64;
type FnClientStreamRw = unsafe extern "C" fn(this: *mut c_void, buf: *mut c_void, len: u64) -> u64;
type FnSetMainState = unsafe extern "C" fn(this: *mut c_void, new_state: i32);
type FnIsaacInit = unsafe extern "C" fn(isaac_state: *mut c_void, seed4: *const i32);
type FnClientStreamFill = unsafe extern "C" fn(this: *mut c_void);
/// `jag::LoginStateMachine::OpenLoginStream(self)` — SysV: RDI = self. Returns a
/// status the callers consume; we mirror it through the trampoline untouched.
type FnOpenLoginStream = unsafe extern "C" fn(this: *mut c_void) -> u64;
/// `jag::ConnectionManager::SetupLoginCiphers(self)` — SysV: RDI = self. Return
/// value passed through verbatim.
type FnSetupLoginCiphers = unsafe extern "C" fn(this: *mut c_void) -> u64;

// Each hook keeps the original-function pointer (the trampoline) so its detour
// body can call through. The `Detour` objects themselves are parked in
// INSTALLED to keep the trampoline pages alive for the process lifetime.
static TRAMP_READ_PACKET: OnceCell<FnReadPacket> = OnceCell::new();
static TRAMP_TCPMSG_INIT: OnceCell<FnTcpMsgInit> = OnceCell::new();
static TRAMP_FLUSH_QUEUE: OnceCell<FnFlushQueue> = OnceCell::new();
static TRAMP_CS_READ: OnceCell<FnClientStreamRw> = OnceCell::new();
static TRAMP_CS_WRITE: OnceCell<FnClientStreamRw> = OnceCell::new();
static TRAMP_SET_MAIN_STATE: OnceCell<FnSetMainState> = OnceCell::new();
static TRAMP_ISAAC_INIT: OnceCell<FnIsaacInit> = OnceCell::new();
static TRAMP_CS_FILL: OnceCell<FnClientStreamFill> = OnceCell::new();
static TRAMP_OPEN_LOGIN_STREAM: OnceCell<FnOpenLoginStream> = OnceCell::new();
static TRAMP_SETUP_LOGIN_CIPHERS: OnceCell<FnSetupLoginCiphers> = OnceCell::new();

static INSTALLED: parking_lot::Mutex<Vec<Detour>> = parking_lot::Mutex::new(Vec::new());

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

/// Publish the Client base inferred from a ConnectionManager pointer
/// (`*(ConnMgr+8)`), but ONLY if the candidate reads back a recognised
/// MAIN_STATE enum value — so a wrong inference can never poison state tagging.
fn try_publish_client_from_connmgr(connmgr: usize) {
    if state::client_base() != 0 {
        return;
    }
    if let Some(client) = mem::deref(connmgr, ocm::OWNER_CLIENT) {
        if let Some(st) = mem::read_i32(client, oc::MAIN_STATE) {
            if is_known_state(st) {
                state::publish_client_base(client);
            }
        }
    }
}

fn is_known_state(s: i32) -> bool {
    matches!(s, 0 | 10 | 20 | 23 | 30 | 35 | 37 | 40)
}

/// Strip the on-wire header (opcode byte(s) + size prefix) from a built C2S
/// buffer to recover the pre-encrypt body, per the RE doc's field math.
fn strip_c2s_header(opcode: i32, size_class: i32, buf: &[u8]) -> Vec<u8> {
    let opcode_bytes = if opcode >= 128 { 2 } else { 1 };
    let prefix_bytes = match size_class {
        -1 => 1,
        -2 => 2,
        _ => 0,
    };
    let off = (opcode_bytes + prefix_bytes).min(buf.len());
    buf[off..].to_vec()
}

// ---------------------------------------------------------------------------
// S2C — PRIMARY: ConnectionManager::ReadPacket INLINE dispatch site (0x6d8ea)
//
// Per RE §3a/§1c this is the ONLY point where (opcode, body) are coherent: the
// packet is fully decoded into BUF_DATA and CURRENT_OPCODE (conn+0x2C) still
// holds THIS packet's opcode (reset to -1 only after the handler dispatch). The
// stale post-`ReadPacket` LAST_OPCODE read is what mistagged op82's body as op3;
// reading CURRENT_OPCODE here eliminates that desync at the source.
//
// We hook it with a register-PRESERVING inline observer (detour::install_inline)
// — a read-only tap that cannot perturb the client's live registers/control
// flow. Register state at 0x6d8ea (verified from disassembly):
//   RSI = msg context  -> conn = *(RSI + 0x8)
//   RDI = ConnectionManager (the original `this`, reloaded at 0x6d8e6)
// ---------------------------------------------------------------------------

/// Set once the inline dispatch observer is installed. When true, the post-
/// `ReadPacket` entry hook does NOT emit framed s2c (the inline path owns it),
/// so a packet is never emitted twice or via the stale LAST_OPCODE read.
static S2C_INLINE_ACTIVE: AtomicBool = AtomicBool::new(false);

unsafe extern "C" fn s2c_dispatch_observer(regs: *const Regs) {
    if regs.is_null() {
        return;
    }
    let r = &*regs;
    // conn = *(RSI + 0x8) — the connection this packet belongs to.
    let msg_ctx = r.rsi as usize;
    // RDI = ConnectionManager here; publish the client base for state tagging.
    try_publish_client_from_connmgr(r.rdi as usize);
    // Backstop trigger for the live opcode-table dump (the table is long-since
    // populated by the time any s2c packet dispatches). Once-only + guarded.
    state::maybe_dump_prot_table();
    emit_s2c_coherent(msg_ctx);
}

/// Coherent framed-s2c emit, reading every field at the SAME instant off the
/// single connection this dispatch belongs to. Used by the inline observer.
fn emit_s2c_coherent(msg_ctx: usize) {
    let Some(s) = state::session() else { return };
    let Some(conn) = mem::deref(msg_ctx, ocm::MSG_CTX_CONN) else {
        return;
    };
    let role = state::conn_role(conn);
    if role != "game" && role != "login" {
        return; // not a known processing connection — never emit off a garbage conn
    }
    // CURRENT_OPCODE (conn+0x2C) is THIS packet's opcode at the dispatch site —
    // it can never disagree with the body still in BUF_DATA.
    let op = match mem::read_i32(conn, osc::CURRENT_OPCODE) {
        Some(v) => v,
        None => return,
    };
    let size = match mem::read_i32(conn, osc::RESOLVED_SIZE) {
        Some(v) => v,
        None => return,
    };
    if !is_plausible_opcode(op) || size < 0 || size as usize > mem::MAX_COPY {
        return;
    }
    let body = if size == 0 {
        Vec::new()
    } else {
        match mem::deref(conn, osc::BUF_DATA).and_then(|b| mem::copy_bytes(b, size as usize)) {
            Some(b) => b,
            None => return,
        }
    };
    s.framed("s2c", role, state::main_state(), op, &body, false);

    // Cross-validation: ensure the per-(conn,dir) ISAAC seeds are written. By the
    // time a packet dispatches, the connections + their cipher pointers are wired,
    // so reconciliation now resolves the game/login send+recv seeds even if the
    // `Isaac::Init` calls fired before the connections were readable. The
    // independent socket-plane raw deframe is fed by Fill (true wire bytes), so we
    // do NOT also push this decoded body into raw_bin (that would double-count s2c
    // against the Fill plane). We only ensure the seed lines are present.
    write_isaac_seeds(s);
}

/// Snapshot every reconciled (conn, dir) ISAAC seed and hand the full set to the
/// `isaac-keys.txt` writer. Cheap and idempotent (the writer only grows the file
/// when a newly-resolved role appears), so it is safe to call from every emit
/// path — that is exactly how a seed captured before its connection was wired
/// still lands once the connection resolves.
fn write_isaac_seeds(s: &crate::session::Session) {
    let seeds = state::isaac_seeds_by_role();
    if !seeds.is_empty() {
        s.isaac_seeds(&seeds);
    }
}

// ---------------------------------------------------------------------------
// S2C — BACKSTOP: ConnectionManager::ReadPacket entry hook (post-trampoline)
//
// Retained as a fallback for the case where the inline observer fails to install
// (e.g. a future binary drift moves the dispatch site). When the inline path is
// active this hook does NOT emit framed s2c — it only keeps publishing the
// client base. When inline is INACTIVE it falls back to the guarded LAST_OPCODE
// read (best-effort; see the op3/op82 caveat in RE §1c — this path can still
// mis-pair, which is exactly why inline is preferred).
// ---------------------------------------------------------------------------

unsafe extern "C" fn read_packet_detour(this: *mut c_void, msg_ctx: *mut c_void) -> u64 {
    let ret = TRAMP_READ_PACKET.get().unwrap()(this, msg_ctx);
    try_publish_client_from_connmgr(this as usize);
    maybe_emit_post_handler_varc_snapshot(msg_ctx as usize);
    if !S2C_INLINE_ACTIVE.load(Ordering::Relaxed) {
        capture_s2c_backstop(msg_ctx as usize);
    }
    ret
}

fn maybe_emit_post_handler_varc_snapshot(msg_ctx: usize) {
    if !state::is_recording() || state::client_base() == 0 {
        return;
    }
    let Some(conn) = mem::deref(msg_ctx, ocm::MSG_CTX_CONN) else {
        return;
    };
    let role = state::conn_role(conn);
    if role != "game" && role != "login" {
        return;
    }
    let Some(op) = mem::read_i32(conn, osc::LAST_OPCODE) else {
        return;
    };
    if is_varc_state_opcode(op) {
        state::emit_state_snapshot();
    }
}

fn is_varc_state_opcode(op: i32) -> bool {
    matches!(op, 5 | 47 | 48 | 64 | 69 | 92 | 116 | 196)
}

/// Post-`ReadPacket` S2C capture (BACKSTOP ONLY). Reads the stale `LAST_OPCODE`
/// (conn+0x2E0) with a coherence guard. Only reached when the coherent inline
/// observer is unavailable. See RE §1c for why this can mis-pair op N with op
/// N+1's body — the inline observer is the real fix.
fn capture_s2c_backstop(msg_ctx: usize) {
    let Some(s) = state::session() else { return };
    let Some(conn) = mem::deref(msg_ctx, ocm::MSG_CTX_CONN) else {
        return;
    };
    let role = state::conn_role(conn);
    if role != "game" && role != "login" {
        return;
    }
    let op = match mem::read_i32(conn, osc::LAST_OPCODE) {
        Some(v) => v,
        None => return,
    };
    let size = match mem::read_i32(conn, osc::RESOLVED_SIZE) {
        Some(v) => v,
        None => return,
    };
    if !is_plausible_opcode(op) || size < 0 || size as usize > mem::MAX_COPY {
        return;
    }
    let body = if size == 0 {
        Vec::new()
    } else {
        match mem::deref(conn, osc::BUF_DATA).and_then(|b| mem::copy_bytes(b, size as usize)) {
            Some(b) => b,
            None => return,
        }
    };
    s.framed("s2c", role, state::main_state(), op, &body, false);
    write_isaac_seeds(s);
}

// ---------------------------------------------------------------------------
// C2S — TcpConnectionMessage::Init (opcode/size/isaac choke point)
//
// At Init the body is not yet written; we record (packet -> opcode/size/isaac)
// so the FlushOutgoingQueue hook can attach the finished body. We keep the most
// recent Init per packet pointer in a small map.
// ---------------------------------------------------------------------------

use parking_lot::Mutex;
use std::collections::HashMap;

struct PendingC2S {
    opcode: i32,
    size_class: i32,
    isaac_active: bool,
}

static PENDING: Mutex<Option<HashMap<usize, PendingC2S>>> = Mutex::new(None);

fn pending() -> parking_lot::MutexGuard<'static, Option<HashMap<usize, PendingC2S>>> {
    let mut g = PENDING.lock();
    if g.is_none() {
        *g = Some(HashMap::new());
    }
    g
}

unsafe extern "C" fn tcpmsg_init_detour(
    packet: *mut c_void,
    opcode_desc: *const i32,
    size_class: u32,
    isaac_out: *mut c_void,
) {
    // Record BEFORE the trampoline: opcode_desc is read by Init, and the
    // pre-encrypt opcode is *opcodeDesc right now.
    if state::is_recording() {
        let opcode = if opcode_desc.is_null() {
            -1
        } else {
            opcode_desc.read_unaligned()
        };
        let entry = PendingC2S {
            opcode,
            size_class: size_class as i32,
            isaac_active: !isaac_out.is_null(),
        };
        pending().as_mut().unwrap().insert(packet as usize, entry);
    }
    TRAMP_TCPMSG_INIT.get().unwrap()(packet, opcode_desc, size_class, isaac_out);
}

// ---------------------------------------------------------------------------
// C2S — ServerConnection::FlushOutgoingQueue (complete framed body, post-build)
//
// Walks the outgoing queue, reads each node's finished buffer, matches it to the
// Init record by the Packet sub-struct pointer, strips the header, emits.
// ---------------------------------------------------------------------------

unsafe extern "C" fn flush_queue_detour(this: *mut c_void) -> u64 {
    // Capture BEFORE the trampoline drains/frees the queue nodes.
    capture_c2s_queue(this as usize);
    TRAMP_FLUSH_QUEUE.get().unwrap()(this)
}

/// Walk the outgoing queue at flush. For each node we read the body DIRECTLY
/// from the Packet sub-struct using the RE-corrected layout (the cause of the
/// op=0/empty-body bug): node+0x18 IS the Packet sub-struct (objectPtr ==
/// TcpConnectionMessage+0x20) — there is NO extra +0x20 — and the wire length is
/// `packet+0x28` (finalPayloadSize), NOT `packet+0x20` (bufWritePos, zeroed at
/// enqueue). The Init choke-point record (keyed by that same packet pointer)
/// supplies the pre-encrypt opcode/sizeClass + the isaac-active bit.
fn capture_c2s_queue(conn: usize) {
    let Some(s) = state::session() else { return };
    let role = state::conn_role(conn);

    // head at conn+0x48; doubly-linked list, node->next at node+0x00.
    let mut node = match mem::deref(conn, 0x48) {
        Some(n) => n,
        None => return,
    };
    let mut guard = 0;
    let mut seen = std::collections::HashSet::new();
    while mem::is_plausible(node) && guard < 512 {
        guard += 1;
        if !seen.insert(node) {
            break; // cycle guard
        }
        emit_c2s_node(s, role, node);
        match mem::deref(node, oq::NEXT) {
            Some(next) if next != node && !seen.contains(&next) => node = next,
            _ => break,
        }
    }
}

fn emit_c2s_node(s: &crate::session::Session, role: &str, node: usize) {
    // node+0x18 is ALREADY the Packet sub-struct (objectPtr == TcpConnectionMessage
    // +0x20). NO extra +0x20 — adding one lands in garbage (the original op=0 bug).
    let Some(packet) = mem::deref(node, oq::PACKET) else {
        return;
    };

    // Prefer the Init choke-point record (pre-encrypt opcode/sizeClass captured
    // before the buffer was built); the logical opcode at packet+0x00 is NOT
    // zeroed at enqueue so it is a valid fallback when no Init record exists
    // (e.g. the Init hook failed to install). Drain the record to bound the map.
    let rec = pending().as_mut().unwrap().remove(&packet);
    let (op, size_class) = match rec.as_ref() {
        Some(r) if is_plausible_opcode(r.opcode) => (r.opcode, r.size_class),
        _ => {
            let op = match mem::read_i32(packet, op::OPCODE) {
                Some(v) => v,
                None => return,
            };
            if !is_plausible_opcode(op) {
                return; // not a coherent Packet sub-struct; skip rather than fabricate
            }
            (op, mem::read_i32(packet, op::SIZE_CLASS).unwrap_or(0))
        }
    };

    // Length is finalPayloadSize (packet+0x28), copied from bufWritePos at enqueue
    // which then ZEROES bufWritePos (+0x20) — so +0x20 reads 0 here and must not
    // be used for the length.
    let len = mem::read_i32(packet, op::FINAL_PAYLOAD_SIZE).unwrap_or(0);
    if len <= 0 || (len as usize) > mem::MAX_COPY {
        return;
    }
    let Some(buf_data) = mem::deref(packet, op::BUF_DATA) else {
        return;
    };
    let Some(wire) = mem::copy_bytes(buf_data, len as usize) else {
        return;
    };

    let body = strip_c2s_header(op, size_class, &wire);

    // A few senders XTEA-encrypt their BODY with the ConnMgr tinyKey; `isaac_active`
    // (whether ISAAC was applied to the opcode) is recorded but XTEA is a separate
    // per-sender concern we cannot identify generically — so we DO NOT decrypt and
    // DO NOT set xtea_body for normal prots. The enricher decides xtea_body. We
    // surface isaac_active only in the raw cross-check, never as a framed field.
    let _ = rec.as_ref().map(|r| r.isaac_active);
    let xtea = false;

    s.framed("c2s", role, state::main_state(), op, &body, xtea);

    if role == "login" || role == "game" {
        s.raw_bin(role, "c2s", &wire);
        write_isaac_seeds(s);
    }
}

fn is_plausible_opcode(op: i32) -> bool {
    (0..=0xDA).contains(&op)
}

// ---------------------------------------------------------------------------
// ISAAC seed — jag::Isaac::Init (capture EVERY seed + its ISAAC state pointer)
//
// Each TCP connection (login AND game/world) builds its OWN pair of ISAAC
// ciphers, so this fires multiple times per session with DIFFERENT 4-int session
// keys. We MUST capture every call — not just the first (the old bug kept only
// the lobby seed, so game/world traffic could never be deframed).
//
// For each call we record BOTH the 4 seed ints (RSI = seed4[0..4]) AND the ISAAC
// state pointer (RDI = isaac_state). `state::record_isaac_seed` keeps every
// capture and reconciles each state pointer to a {conn, dir} by matching it
// against each connection's send (`conn+0x40`) / recv (`conn+0x2B8`) cipher
// pointers (RE §7). Read BEFORE the trampoline — the seed ints at RSI are
// untouched by us; the state pointer at RDI is the buffer about to be seeded.
// ---------------------------------------------------------------------------

unsafe extern "C" fn isaac_init_detour(isaac_state: *mut c_void, seed4: *const i32) {
    if state::is_recording() && !seed4.is_null() {
        // seed4[0..4] — the 4 raw session-key ints for THIS cipher (send = raw,
        // recv = the matching send keys + 50; we store exactly what was passed).
        let seed = [
            seed4.read_unaligned(),
            seed4.add(1).read_unaligned(),
            seed4.add(2).read_unaligned(),
            seed4.add(3).read_unaligned(),
        ];
        // RDI = the ISAAC state buffer being seeded — the key we reconcile
        // against `conn+0x40` (send) / `conn+0x2B8` (recv) for game and login.
        state::record_isaac_seed(isaac_state as usize, seed);
    }
    TRAMP_ISAAC_INIT.get().unwrap()(isaac_state, seed4);
}

// ---------------------------------------------------------------------------
// Raw socket — ClientStream::Read (post) / ::Write (pre)
//
// RAW-PLANE SOURCING (RE §8a, the login/s2c double-capture fix):
//   * C2S raw bytes come from `ClientStream::Write` — the lowest-level send
//     funnel (the exact bytes about to hit `send`, incl. the RSA login block).
//   * S2C raw bytes come from `ClientStream::Fill` ONLY — the recv() funnel that
//     deposits freshly-recv'd bytes into the ring (see `cs_fill_detour`).
//
// `ClientStream::Read` is a framed-FIELD reader: it DRAINS bytes the ring
// already holds (bytes `Fill` already recv'd AND already emitted on the s2c
// plane), topping up via `Fill` when short. So feeding `Read`'s output back onto
// the s2c raw plane DOUBLE-CAPTURES every byte and scrambles send-order:
//   - the Phase-A handshake block is emitted once by `Fill` (recv) and AGAIN by
//     the `Read` that drains it (the identical duplicate entries [0]/[1]);
//   - each small fixed-chunk re-read (e.g. 0x02, 0x6e) re-emits bytes already
//     inside the preceding `Fill` block, interleaving them out of order.
// Net byte count stays correct (the bytes net out), but a positional offline
// deframe cannot follow the scrambled order. Per §8a: use the `Fill` plane ONLY
// for s2c raw bytes; treat `Read` as a framed-field reader, NOT a second raw
// source. We therefore keep the `Read` hook installed (its trampoline runs the
// original; it remains available for future framed-field taps) but it does NOT
// emit onto the raw socket plane.
// ---------------------------------------------------------------------------

unsafe extern "C" fn cs_read_detour(this: *mut c_void, buf: *mut c_void, len: u64) -> u64 {
    // Framed-field reader: drains already-recv'd (already-emitted-by-Fill) ring
    // bytes. Do NOT emit onto the raw s2c socket plane here — that would
    // double-capture against the Fill plane (RE §8a). The hook stays installed so
    // the trampoline runs the client's logic untouched and the capture point is
    // available if a future framed-field tap needs it.
    let _ = (buf, len);
    TRAMP_CS_READ.get().unwrap()(this, buf, len)
}

unsafe extern "C" fn cs_write_detour(this: *mut c_void, buf: *mut c_void, len: u64) -> u64 {
    if len > 0 {
        // C2S raw source: the bytes about to hit `send` (BEFORE the trampoline).
        capture_socket_c2s(this as usize, buf as usize, len as usize);
    }
    TRAMP_CS_WRITE.get().unwrap()(this, buf, len)
}

/// Emit C2S raw bytes onto the socket plane. C2S has a single raw source
/// (`ClientStream::Write`), so there is no double-capture concern here — unlike
/// the s2c plane, where `Fill` is the sole source and `Read` must NOT also emit
/// (RE §8a). js5/cache HTTP request bytes are recorded length-only so the
/// (multi-hundred-MB) cache flood never churns through memory or disk.
fn capture_socket_c2s(stream: usize, buf: usize, len: usize) {
    let Some(s) = state::session() else { return };
    let role = stream_role(stream);
    if role == "js5" {
        s.socket_len_only("c2s", role, len);
        return;
    }
    if let Some(bytes) = mem::copy_bytes(buf, len) {
        s.socket("c2s", role, &bytes);
    }
}

/// Tag a ClientStream* as login/game/js5 by matching it against the
/// CLIENT_STREAM (conn+0x08) of the known connections. Per RE §9 there are
/// exactly two ClientStream producers — the game/login `ServerConnection` and
/// the HTTP cache pump — so a stream owned by neither known connection is the
/// cache transport ⇒ "js5".
///
/// The login stream is resolved via BOTH the ConnMgr+0x28 slot AND the
/// LoginStateMachine cross-check (RE §8a) so the EARLY login s2c read at the
/// Fill hook is tagged "login" as soon as either path is populated — instead of
/// mis-bucketing it as "js5" before ConnMgr+0x28 resolves (the login/s2c
/// under-capture). We only fall back to "unknown" while the ConnectionManager is
/// still unresolved AND the login cross-check is also empty (true pre-connect).
fn stream_role(stream: usize) -> &'static str {
    if state::game_stream() == Some(stream) {
        return "game";
    }
    if state::login_stream() == Some(stream) {
        return "login";
    }
    match state::connection_manager() {
        // ConnMgr is up and the stream is neither game nor login ⇒ the cache HTTP
        // pump's standalone ClientStream (RE §9 elimination — the js5 flood).
        Some(_) => "js5",
        // ConnMgr not up yet and not the cross-checked login stream ⇒ unknown
        // (true pre-connect; minimised — the login cross-check resolves early).
        None => "unknown",
    }
}

// ---------------------------------------------------------------------------
// S2C raw — jag::ClientStream::Fill (the recv() funnel; the SOLE s2c raw source)
//
// This is the ONE place s2c raw bytes are emitted (RE §8 / §8a). `Fill` is the
// recv() funnel: it deposits freshly-recv'd bytes into the recv ring, for BOTH
//   * the in-game path — `ReadPacket` inlines the recv-ring drain and tops the
//     ring up via `Fill` (it does NOT call `ClientStream::Read`); and
//   * the login Phase-A handshake + Phase-B lobby ciphertext — both phases recv
//     through `Fill` (the LoginStateMachine handlers drain via `ClientStream::
//     Read`, but `Read` only RE-reads bytes `Fill` already deposited).
// So every inbound byte crosses `Fill` exactly once, in recv order — making it
// the single, in-order, non-duplicated raw s2c source. The `ClientStream::Read`
// hook deliberately does NOT emit s2c (it would double-capture; see that hook).
//
// Per RE §8: PRE before = *(this+0xA8) (ringCount); POST n = *(this+0xA8) -
// before; the raw bytes are the first n bytes of the contiguous scratch buffer
// *(this+0xC8) (no ring wraparound). We emit them on the socket plane for the
// owning conn.
// ---------------------------------------------------------------------------

unsafe extern "C" fn cs_fill_detour(this: *mut c_void) {
    let stream = this as usize;
    let before = if state::is_recording() {
        mem::read_ptr(stream, ocs::RING_COUNT)
    } else {
        None
    };
    TRAMP_CS_FILL.get().unwrap()(this);
    if let Some(before) = before {
        capture_fill(stream, before);
    }
    // Fill fires on every recv (lobby + in-world), so it is the recorder's most
    // reliable periodic pulse for the client-state oracle snapshots. Time-gated
    // inside, so this is cheap on the hot recv path.
    state::maybe_emit_state_snapshot();
}

/// Emit the bytes Fill just recv'd as socket-plane s2c. `before` is ringCount
/// captured before the trampoline; the delta is how many bytes landed.
fn capture_fill(stream: usize, before: usize) {
    let Some(s) = state::session() else { return };
    let Some(after) = mem::read_ptr(stream, ocs::RING_COUNT) else {
        return;
    };
    // ringCount is a long; a non-positive delta means nothing was recv'd this
    // call (clamp defends against any transient where the consumer raced ahead).
    let n = after.wrapping_sub(before);
    if n == 0 || n > mem::MAX_COPY {
        return;
    }
    let role = stream_role(stream);
    // The cache HTTP pump also recv's through Fill (RE §9). Record length-only
    // for js5 so the cache flood is accounted but its bytes are not persisted
    // (Fix 3) — skip the scratch copy entirely.
    if role == "js5" {
        s.socket_len_only("s2c", role, n);
        return;
    }
    // The Fill plane is the SOLE raw login s2c source (RE §8a: use Fill, not
    // ClientStream::Read — Read only RE-reads bytes Fill already delivered). Count
    // login s2c bytes HERE so the `login_cipher_ready` marker stamps the correct
    // Phase-A length. Because Read no longer emits onto the s2c plane, this count
    // (and the socket.jsonl login/s2c stream it stamps) is the true, non-duplicated
    // recv-order byte stream — what the offline phase-split deframer consumes.
    if role == "login" {
        state::add_login_s2c_bytes(n);
    }
    let Some(scratch) = mem::deref(stream, ocs::SCRATCH_BUF) else {
        return;
    };
    if let Some(bytes) = mem::copy_bytes(scratch, n) {
        s.socket("s2c", role, &bytes);
    }
}

// ---------------------------------------------------------------------------
// State — Client::SetMainState
// ---------------------------------------------------------------------------

unsafe extern "C" fn set_main_state_detour(this: *mut c_void, new_state: i32) {
    let client = this as usize;
    state::publish_client_base(client);
    let old_state = mem::read_i32(client, oc::MAIN_STATE).unwrap_or(-1);
    if state::is_recording() && old_state != new_state {
        if let Some(s) = state::session() {
            s.event_state_change(old_state, new_state);
        }
    }
    // By any state transition the client's `ServerProt::RegisterAll` has run, so
    // this is a reliable (early) trigger for the one-shot live opcode-table dump
    // if the ctor's best-effort attempt was too early. Guarded + once-only inside.
    state::maybe_dump_prot_table();
    TRAMP_SET_MAIN_STATE.get().unwrap()(this, new_state);
    // Snapshot the client's state right AFTER a transition (esp. entering the
    // lobby/world) — the new-state oracle reads are valid once the setter has
    // written the field and the trampoline returned. Time-gated, so back-to-back
    // transitions don't spam snapshots.
    state::maybe_emit_state_snapshot();
}

// ---------------------------------------------------------------------------
// LOBBY/LOGIN — LoginStateMachine::OpenLoginStream (RE §8a, the EARLIEST anchor)
//
// At entry, RDI = the LoginStateMachine `self`. The login socket has just been
// (or is about to be) created; `self+0x18` is the owning Client and `self+0x30`
// is the login ServerConnection (verified in the prologue:
// `mov rax,[rdi+0x30]; mov rcx,[rdi+0x18]`). We publish the Client base and
// register the login ClientStream (`*(self+0x30) + 0x08`) BEFORE the trampoline
// runs, so the very first login `Fill` recv is tagged "login" — fixing the early
// login s2c under-capture (it was previously bucketed "js5" until ConnMgr+0x28
// resolved a beat later).
// ---------------------------------------------------------------------------

unsafe extern "C" fn open_login_stream_detour(this: *mut c_void) -> u64 {
    let lsm = this as usize;
    if state::is_recording() {
        // Publish the Client base (gated on a recognised MAIN_STATE so a bogus
        // pointer can never poison state tagging — same guard as the ConnMgr path).
        if let Some(client) = mem::deref(lsm, olsm::OWNER_CLIENT) {
            if let Some(st) = mem::read_i32(client, oc::MAIN_STATE) {
                if is_known_state(st) {
                    state::publish_client_base(client);
                }
            }
        }
        // Register the login ClientStream the moment the socket exists:
        //   loginConn = *(self + 0x30)  ->  stream = *(loginConn + 0x08)
        if let Some(login_conn) = mem::deref(lsm, olsm::LOGIN_CONNECTION) {
            if let Some(stream) = mem::deref(login_conn, osc::CLIENT_STREAM) {
                state::register_login_stream(stream);
            }
        }
    }
    TRAMP_OPEN_LOGIN_STREAM.get().unwrap()(this)
}

// ---------------------------------------------------------------------------
// LOBBY/LOGIN — ConnectionManager::SetupLoginCiphers (RE §8a, Phase A→B boundary)
//
// This runs at login reply-state 0x50: the point where the login handshake's
// PLAINTEXT phase (Phase A) ends and the lobby s2c becomes ISAAC ciphertext
// (Phase B). We emit a one-time `login_cipher_ready` marker stamped with the
// running login-s2c byte count so the offline deframer can split the login s2c
// stream at that offset (plaintext before, ISAAC-from-position-0 after). We do
// NOT read the seed here — the `Isaac::Init` hook already captures every seed.
// ---------------------------------------------------------------------------

unsafe extern "C" fn setup_login_ciphers_detour(this: *mut c_void) -> u64 {
    if state::is_recording() && state::take_login_cipher_marker() {
        if let Some(s) = state::session() {
            s.event_login_cipher_ready(state::login_s2c_bytes());
        }
    }
    TRAMP_SETUP_LOGIN_CIPHERS.get().unwrap()(this)
}

// ---------------------------------------------------------------------------
// Installation
// ---------------------------------------------------------------------------

/// Install one inline detour: resolve the signature, install via the iced-x86
/// trampoline engine, store the trampoline (callable original) typed in `$cell`.
/// The `Detour` is parked in INSTALLED so its trampoline page outlives this fn.
/// Returns whether it installed. A failure here is non-fatal — we simply lose
/// that one capture point (never crash the client).
macro_rules! install {
    ($image:expr, $sig:expr, $cell:expr, $ty:ty, $detour:expr, $label:expr) => {{
        match sig::resolve($image, &$sig) {
            Some(addr) => {
                match unsafe { detour::install(addr, $detour as $ty as *const () as usize) } {
                    Ok(d) => {
                        let tramp: $ty = unsafe { std::mem::transmute(d.trampoline()) };
                        let _ = $cell.set(tramp);
                        INSTALLED.lock().push(d);
                        crate::log(&format!("hook installed: {} @ 0x{:x}", $label, addr));
                        true
                    }
                    Err(e) => {
                        crate::log(&format!("hook install failed: {} ({e})", $label));
                        false
                    }
                }
            }
            None => false,
        }
    }};
}

/// Install a register-preserving INLINE observer at a resolved signature. Unlike
/// `install!` there is no callable trampoline to keep — the observer is a pure
/// read-only tap, so we just park the `Detour` to hold its trampoline page.
/// Returns whether it installed.
fn install_inline_observer(
    image: &MainImage,
    sig: &sig::Pattern,
    observer: detour::InlineObserver,
    label: &str,
) -> bool {
    match sig::resolve(image, sig) {
        Some(addr) => match unsafe { detour::install_inline(addr, observer) } {
            Ok(d) => {
                INSTALLED.lock().push(d);
                crate::log(&format!("inline observer installed: {label} @ 0x{addr:x}"));
                true
            }
            Err(e) => {
                crate::log(&format!("inline observer install failed: {label} ({e})"));
                false
            }
        },
        None => false,
    }
}

/// Install all capture-point hooks. Returns the count installed.
pub fn install_all(image: &MainImage) -> usize {
    let mut n = 0;

    // PRIMARY s2c: the coherent inline dispatch observer. Install it FIRST so the
    // ReadPacket entry hook (the backstop, installed next) knows to stand down.
    if install_inline_observer(
        image,
        &offsets::SIG_S2C_DISPATCH,
        s2c_dispatch_observer,
        "ReadPacket::s2cDispatch(s2c-coherent)",
    ) {
        S2C_INLINE_ACTIVE.store(true, Ordering::SeqCst);
        n += 1;
    } else {
        crate::log(
            "s2c inline dispatch observer NOT installed — falling back to the \
             ReadPacket LAST_OPCODE backstop (op3/op82 desync possible; see RE §1c)",
        );
    }

    n += install!(
        image,
        offsets::SIG_READ_PACKET,
        TRAMP_READ_PACKET,
        FnReadPacket,
        read_packet_detour,
        "ReadPacket(s2c-backstop)"
    ) as usize;
    n += install!(
        image,
        offsets::SIG_TCPMSG_INIT,
        TRAMP_TCPMSG_INIT,
        FnTcpMsgInit,
        tcpmsg_init_detour,
        "TcpConnectionMessage::Init(c2s)"
    ) as usize;
    n += install!(
        image,
        offsets::SIG_FLUSH_QUEUE,
        TRAMP_FLUSH_QUEUE,
        FnFlushQueue,
        flush_queue_detour,
        "FlushOutgoingQueue(c2s)"
    ) as usize;
    n += install!(
        image,
        offsets::SIG_CLIENTSTREAM_READ,
        TRAMP_CS_READ,
        FnClientStreamRw,
        cs_read_detour,
        "ClientStream::Read(socket)"
    ) as usize;
    n += install!(
        image,
        offsets::SIG_CLIENTSTREAM_WRITE,
        TRAMP_CS_WRITE,
        FnClientStreamRw,
        cs_write_detour,
        "ClientStream::Write(socket)"
    ) as usize;
    n += install!(
        image,
        offsets::SIG_SET_MAIN_STATE,
        TRAMP_SET_MAIN_STATE,
        FnSetMainState,
        set_main_state_detour,
        "Client::SetMainState(state)"
    ) as usize;
    n += install!(
        image,
        offsets::SIG_ISAAC_INIT,
        TRAMP_ISAAC_INIT,
        FnIsaacInit,
        isaac_init_detour,
        "Isaac::Init(seed)"
    ) as usize;
    n += install!(
        image,
        offsets::SIG_CLIENTSTREAM_FILL,
        TRAMP_CS_FILL,
        FnClientStreamFill,
        cs_fill_detour,
        "ClientStream::Fill(game-s2c)"
    ) as usize;
    n += install!(
        image,
        offsets::SIG_OPEN_LOGIN_STREAM,
        TRAMP_OPEN_LOGIN_STREAM,
        FnOpenLoginStream,
        open_login_stream_detour,
        "LoginStateMachine::OpenLoginStream(login-anchor)"
    ) as usize;
    n += install!(
        image,
        offsets::SIG_SETUP_LOGIN_CIPHERS,
        TRAMP_SETUP_LOGIN_CIPHERS,
        FnSetupLoginCiphers,
        setup_login_ciphers_detour,
        "ConnectionManager::SetupLoginCiphers(cipher-boundary)"
    ) as usize;
    n
}

/// Total number of capture-point hooks `install_all` attempts (for the log line):
/// the inline s2c dispatch observer + the 8 original entry/backstop hooks + the
/// 2 new lobby hooks (OpenLoginStream anchor, SetupLoginCiphers boundary).
pub const HOOK_COUNT: usize = 11;
