//! Session directory + on-disk capture writers (the OUTPUT CONTRACT).
//!
//! Layout under `$DARKAN_RECORD_DIR` (default `~/.undercut/recordings`):
//!
//!   session-<UTC yyyyMMdd-HHmmss>-<pid>-<mode>/
//!     session.json        summary (rewritten on start + at stop)
//!     framed-s2c.jsonl     one flat JSON object per S->C framed packet
//!     framed-c2s.jsonl     one flat JSON object per C->S framed packet
//!     socket.jsonl         raw ClientStream Read/Write bytes (ciphertext + RSA)
//!     events.jsonl         state_change / connect / disconnect / process
//!     blobs/<sha256-prefix>.bin   bodies > 8192 bytes (referenced by body_ref)
//!     raw-<role>-s2c.bin / raw-<role>-c2s.bin   optional cross-validation
//!     isaac-keys.txt       optional ISAAC seeds for tools/DecodeCapture.kt
//!
//! All writes go through a single `Mutex` and flush each line so a `kill -9` of
//! the client still leaves a coherent, line-delimited capture.

use base64::engine::general_purpose::STANDARD as B64;
use base64::Engine;
use chrono::Utc;
use parking_lot::Mutex;
use serde_json::json;
use sha2::{Digest, Sha256};
use std::fs::{self, File, OpenOptions};
use std::io::Write;
use std::path::{Path, PathBuf};
use std::time::{SystemTime, UNIX_EPOCH};

/// Bodies larger than this are spilled to `blobs/` and referenced by hash.
const BODY_INLINE_LIMIT: usize = 8192;

/// Which process this dylib instance is running in.
#[derive(Clone, Copy, PartialEq, Eq)]
pub enum Proc {
    Wrapper,
    Rs2client,
}

impl Proc {
    pub fn as_str(self) -> &'static str {
        match self {
            Proc::Wrapper => "wrapper",
            Proc::Rs2client => "rs2client",
        }
    }
}

struct Files {
    framed_s2c: File,
    framed_c2s: File,
    socket: File,
    events: File,
    /// Client-state oracle snapshots (the "client is king" cross-check). One
    /// JSON object per snapshot: periodic + an authoritative one at exit.
    state_snapshots: File,
}

pub struct Session {
    dir: PathBuf,
    blobs_dir: PathBuf,
    proc: Proc,
    pid: u32,
    /// Monotonic origin (nanoseconds since an arbitrary boot epoch) captured at
    /// session start; `mono_us` fields are deltas from this in microseconds.
    mono_origin: u128,
    files: Mutex<Files>,
    /// Optional cross-validation sinks, created lazily on first raw byte.
    raw_bins: Mutex<RawBins>,
    isaac: Mutex<IsaacKeys>,
}

#[derive(Default)]
struct RawBins {
    login_s2c: Option<File>,
    login_c2s: Option<File>,
    game_s2c: Option<File>,
    game_c2s: Option<File>,
}

#[derive(Default)]
struct IsaacKeys {
    /// The file, opened lazily on the first seed.
    file: Option<File>,
    /// True once the header comment has been written (write-once).
    header_written: bool,
    /// Role tags (`game-s2c`, `game-c2s`, `login-s2c`, `login-c2s`) already
    /// written, so each (conn, dir) seed line is emitted exactly once even though
    /// the emit hooks call the writer repeatedly as connections resolve.
    roles_written: Vec<String>,
}

fn mono_now_ns() -> u128 {
    // CLOCK_MONOTONIC via std::time::Instant is not convertible to an absolute
    // origin we can subtract across calls without storing the Instant, so we
    // use a process-wide monotonic clock through SystemTime as a fallback is
    // wrong (wall clock). Instead read the raw monotonic clock directly.
    let mut ts = libc::timespec {
        tv_sec: 0,
        tv_nsec: 0,
    };
    unsafe {
        // CLOCK_UPTIME_RAW is monotonic and unaffected by NTP on macOS.
        libc::clock_gettime(libc::CLOCK_UPTIME_RAW, &mut ts);
    }
    (ts.tv_sec as u128) * 1_000_000_000 + (ts.tv_nsec as u128)
}

fn wall_iso_now() -> String {
    Utc::now().format("%Y-%m-%dT%H:%M:%S%.3fZ").to_string()
}

fn wall_unix_ms() -> u128 {
    SystemTime::now()
        .duration_since(UNIX_EPOCH)
        .map(|d| d.as_millis())
        .unwrap_or(0)
}

/// Serialize the LOCAL-PLAYER AVATAR render state (RE §10.6) into a JSON object.
/// Pointer fields are hex strings (`0x<addr>`, matching `prot-table.json`); every
/// field is emitted ONLY when it was readable (omit, never fabricate). The shape
/// is the diagnostic contract the user reads to tell apart the failure modes:
/// `avatar`==null ⇒ no entity; `visible_flag`==0 ⇒ not render-bound;
/// `render_model`=="0x0" ⇒ model not loaded; `render_tile` drifting while
/// `target_tile` is held ⇒ a client render-interp issue (not the logical tile).
fn encode_local_player(lp: &crate::oracle::LocalPlayer) -> serde_json::Value {
    let mut o = serde_json::Map::new();
    let ptr = |p: usize| json!(format!("0x{p:x}"));

    if let Some(p) = lp.lip {
        o.insert("lip".into(), ptr(p));
    }
    if let Some(idx) = lp.server_index {
        o.insert("server_index".into(), json!(idx));
    }
    if let Some(b) = lp.index_is_one {
        o.insert("index_is_one".into(), json!(b));
    }
    if let Some(p) = lp.override_avatar {
        o.insert("override_avatar".into(), ptr(p));
    }
    if let Some(p) = lp.avatar {
        o.insert("avatar".into(), ptr(p));
    }
    o.insert("avatar_source".into(), json!(lp.avatar_source));
    if let Some(v) = lp.visible_flag {
        o.insert("visible_flag".into(), json!(v));
    }
    if let Some(p) = lp.render_model {
        o.insert("render_model".into(), ptr(p));
    }
    if let Some(p) = lp.render_graph_node {
        o.insert("render_graph_node".into(), ptr(p));
    }
    if let Some(p) = lp.scene_bucket_graph_node {
        o.insert("scene_bucket_graph_node".into(), ptr(p));
    }
    if let Some((x, y, plane)) = lp.render_tile {
        o.insert("render_tile".into(), json!({ "x": x, "y": y, "plane": plane }));
    }
    if let Some((x, y)) = lp.render_scene_fine {
        o.insert("render_scene_fine".into(), json!({ "x": x, "y": y }));
    }
    if let Some((x, y)) = lp.render_pos_double {
        o.insert("render_pos_double".into(), json!({ "x": x, "y": y }));
    }
    if let Some((x, y)) = lp.target_waypoint_fine {
        o.insert("target_waypoint_fine".into(), json!({ "x": x, "y": y }));
    }
    if let Some((x, y)) = lp.target_tile {
        o.insert("target_tile".into(), json!({ "x": x, "y": y }));
    }
    if let Some((x, y)) = lp.prev_waypoint_fine {
        o.insert("prev_waypoint_fine".into(), json!({ "x": x, "y": y }));
    }
    if let Some(p) = lp.plane {
        o.insert("plane".into(), json!(p));
    }
    if let Some(s) = lp.size {
        o.insert("size".into(), json!(s));
    }
    if let Some(c) = lp.extra_models {
        o.insert("extra_models".into(), json!(c));
    }
    // Pending-appearance async gate. `pending_appearance` is always emitted when the
    // avatar was readable (as a ptr, "0x0" => no pending / compose already ran),
    // making "compose blocked" (non-zero ptr + composed_flag 0) distinguishable from
    // "compose ran but produced null" (ptr 0x0). The gate bytes/keys are emitted
    // only when the pending pointer was non-null (omit, never fabricate).
    if let Some(p) = lp.pending_appearance {
        o.insert("pending_appearance".into(), ptr(p));
    }
    // Applied/current appearance (Avatar::SetAppearance ran). Emitted as a ptr whenever
    // the avatar was readable ("0x0" => never applied / compose never ran; non-zero =>
    // compose started but the model build produced null). Same serialization as
    // `pending_appearance`; together they disambiguate the null-render_model failure mode.
    if let Some(p) = lp.current_appearance {
        o.insert("current_appearance".into(), ptr(p));
    }
    if let Some(v) = lp.pending_needs_async_load {
        o.insert("pending_needs_async_load".into(), json!(v));
    }
    if let Some(v) = lp.pending_0x89 {
        o.insert("pending_0x89".into(), json!(v));
    }
    if let Some(v) = lp.pending_composed_flag {
        o.insert("pending_composed_flag".into(), json!(v));
    }
    if let Some(v) = lp.pending_res_7c {
        o.insert("pending_res_7c".into(), json!(v));
    }
    if let Some(v) = lp.pending_res_80 {
        o.insert("pending_res_80".into(), json!(v));
    }
    if let Some(v) = lp.pending_0x84 {
        o.insert("pending_0x84".into(), json!(v));
    }
    // Avatar-lifecycle witnesses (decode -> compose -> mesh -> bind -> drift). Pointer
    // fields are hex strings (a "0x0" => readable-but-null = that stage not reached);
    // every field is emitted ONLY when it was readable (omit, never fabricate). The
    // diagnosis per spawn: `decode_witness`/`current_appearance` != 0 => DecodeAppearance
    // ran; `body_type_model`/`compose_handle` => compose ran/dirty; `render_model` =>
    // mesh built; `map_square_bind`/`model_bind_state` => bound; `lerp_end_tick` == -1
    // => the open-loop drift fallback.
    if let Some(p) = lp.body_type_model {
        o.insert("body_type_model".into(), ptr(p));
    }
    if let Some(v) = lp.decode_witness {
        o.insert("decode_witness".into(), json!(v));
    }
    if let Some(v) = lp.lerp_end_tick {
        o.insert("lerp_end_tick".into(), json!(v));
    }
    if let Some(p) = lp.map_square_bind {
        o.insert("map_square_bind".into(), ptr(p));
    }
    if let Some((x, y, z)) = lp.target_fine {
        o.insert("target_fine".into(), json!({ "x": x, "elev": y, "z": z }));
    }
    if let Some((x, y, z)) = lp.prev_fine {
        o.insert("prev_fine".into(), json!({ "x": x, "elev": y, "z": z }));
    }
    if let Some(v) = lp.model_bind_state {
        o.insert("model_bind_state".into(), json!(v));
    }
    if let Some(v) = lp.compose_dirty {
        o.insert("compose_dirty".into(), json!(v));
    }
    if let Some(v) = lp.compose_cacheid {
        o.insert("compose_cacheid".into(), json!(v));
    }
    if let Some(p) = lp.compose_handle {
        o.insert("compose_handle".into(), ptr(p));
    }
    if let Some(v) = lp.compose_gender {
        o.insert("compose_gender".into(), json!(v));
    }
    serde_json::Value::Object(o)
}

impl Session {
    /// Create the session directory and open all writers. `build` is the client
    /// build string (best-effort), `server_mode` is "production"/"local".
    pub fn create(
        record_dir: &Path,
        proc: Proc,
        mode: &str,
        build: &str,
        server_mode: &str,
        wrapper_pid: Option<u32>,
    ) -> std::io::Result<Session> {
        let pid = std::process::id();
        let stamp = Utc::now().format("%Y%m%d-%H%M%S").to_string();
        let dir = record_dir.join(format!("session-{stamp}-{pid}-{mode}"));
        fs::create_dir_all(&dir)?;
        let blobs_dir = dir.join("blobs");
        fs::create_dir_all(&blobs_dir)?;

        let open = |name: &str| -> std::io::Result<File> {
            OpenOptions::new()
                .create(true)
                .append(true)
                .open(dir.join(name))
        };

        let files = Files {
            framed_s2c: open("framed-s2c.jsonl")?,
            framed_c2s: open("framed-c2s.jsonl")?,
            socket: open("socket.jsonl")?,
            events: open("events.jsonl")?,
            state_snapshots: open("state-snapshots.jsonl")?,
        };

        let session = Session {
            dir,
            blobs_dir,
            proc,
            pid,
            mono_origin: mono_now_ns(),
            files: Mutex::new(files),
            raw_bins: Mutex::new(RawBins::default()),
            isaac: Mutex::new(IsaacKeys::default()),
        };

        session.write_session_json(build, server_mode, wrapper_pid, None, false);
        Ok(session)
    }

    pub fn dir(&self) -> &Path {
        &self.dir
    }

    fn mono_us(&self) -> u128 {
        mono_now_ns().saturating_sub(self.mono_origin) / 1000
    }

    /// (Re)write session.json. Called at start (rs2client_pid unknown) and at
    /// stop (with stop fields set).
    pub fn write_session_json(
        &self,
        build: &str,
        server_mode: &str,
        wrapper_pid: Option<u32>,
        connections: Option<serde_json::Value>,
        stopped: bool,
    ) {
        let rs2client_pid = if self.proc == Proc::Rs2client {
            Some(self.pid)
        } else {
            None
        };
        let mut obj = json!({
            "session_id": self.dir.file_name().and_then(|s| s.to_str()).unwrap_or(""),
            "started_at": wall_iso_now(),
            "build": build,
            "server_mode": server_mode,
            "proc": self.proc.as_str(),
            "wrapper_pid": wrapper_pid,
            "rs2client_pid": rs2client_pid,
            "connections": connections.unwrap_or_else(|| json!([])),
        });
        if stopped {
            obj["stopped_at"] = json!(wall_iso_now());
            obj["session_stop"] = json!(true);
        }
        let path = self.dir.join("session.json");
        if let Ok(s) = serde_json::to_string_pretty(&obj) {
            let _ = fs::write(path, s);
        }
    }

    /// Encode a body for a framed/socket line: inline base64 when small, else
    /// spill to `blobs/<sha256-prefix>.bin` and return a (key, value) of
    /// `"body_ref"`. Returns `(field_name, json_value)`.
    fn encode_body(&self, body: &[u8]) -> (&'static str, serde_json::Value) {
        if body.len() <= BODY_INLINE_LIMIT {
            ("body", json!(B64.encode(body)))
        } else {
            let mut hasher = Sha256::new();
            hasher.update(body);
            let digest = hasher.finalize();
            let hex: String = digest.iter().map(|b| format!("{b:02x}")).collect();
            let prefix = &hex[..16.min(hex.len())];
            let blob_path = self.blobs_dir.join(format!("{prefix}.bin"));
            if !blob_path.exists() {
                let _ = fs::write(&blob_path, body);
            }
            ("body_ref", json!(format!("blobs/{prefix}.bin")))
        }
    }

    fn write_line(file: &mut File, value: &serde_json::Value) {
        if let Ok(mut s) = serde_json::to_string(value) {
            s.push('\n');
            let _ = file.write_all(s.as_bytes());
            let _ = file.flush();
        }
    }

    /// Emit one framed-plane line. `dir` = "s2c"/"c2s".
    #[allow(clippy::too_many_arguments)]
    pub fn framed(
        &self,
        dir: &str,
        conn: &str,
        state: i32,
        op: i32,
        body: &[u8],
        xtea_body: bool,
    ) {
        let (body_key, body_val) = self.encode_body(body);
        let mut obj = json!({
            "ts": wall_iso_now(),
            "mono_us": self.mono_us() as u64,
            "proc": self.proc.as_str(),
            "plane": "framed",
            "dir": dir,
            "conn": conn,
            "state": state,
            "op": op,
            "len": body.len(),
        });
        obj[body_key] = body_val;
        if xtea_body {
            obj["xtea_body"] = json!(true);
        }
        let mut files = self.files.lock();
        let f = if dir == "s2c" {
            &mut files.framed_s2c
        } else {
            &mut files.framed_c2s
        };
        Self::write_line(f, &obj);
    }

    /// Emit one socket-plane (raw ClientStream) line. `dir` = "s2c"/"c2s",
    /// `conn` = "login"/"game"/"unknown".
    pub fn socket(&self, dir: &str, conn: &str, body: &[u8]) {
        let (body_key, body_val) = self.encode_body(body);
        let mut obj = json!({
            "ts": wall_iso_now(),
            "mono_us": self.mono_us() as u64,
            "proc": self.proc.as_str(),
            "plane": "socket",
            "dir": dir,
            "conn": conn,
            "len": body.len(),
        });
        obj[body_key] = body_val;
        let mut files = self.files.lock();
        Self::write_line(&mut files.socket, &obj);
    }

    /// Emit one socket-plane line WITHOUT the payload — just `{conn, dir, len}`
    /// plus a `len_only: true` marker. Used for the js5/cache HTTP stream: we
    /// keep the byte-accounting metadata (a transfer happened, how big) without
    /// spilling the (multi-hundred-MB) cache bodies to disk. NO `body`/`body_ref`
    /// key is written, so nothing lands in `blobs/` for these entries.
    pub fn socket_len_only(&self, dir: &str, conn: &str, len: usize) {
        let obj = json!({
            "ts": wall_iso_now(),
            "mono_us": self.mono_us() as u64,
            "proc": self.proc.as_str(),
            "plane": "socket",
            "dir": dir,
            "conn": conn,
            "len": len,
            "len_only": true,
        });
        let mut files = self.files.lock();
        Self::write_line(&mut files.socket, &obj);
    }

    pub fn event_state_change(&self, old_state: i32, new_state: i32) {
        let obj = json!({
            "ts": wall_iso_now(),
            "mono_us": self.mono_us() as u64,
            "proc": self.proc.as_str(),
            "plane": "event",
            "kind": "state_change",
            "old_state": old_state,
            "old_state_name": crate::offsets::state_name(old_state),
            "new_state": new_state,
            "new_state_name": crate::offsets::state_name(new_state),
        });
        let mut files = self.files.lock();
        Self::write_line(&mut files.events, &obj);
    }

    pub fn event_connect(&self, fd: i32, peer: &str, port: u16) {
        let obj = json!({
            "ts": wall_iso_now(),
            "mono_us": self.mono_us() as u64,
            "proc": self.proc.as_str(),
            "plane": "event",
            "kind": "connect",
            "fd": fd,
            "peer": peer,
            "port": port,
        });
        let mut files = self.files.lock();
        Self::write_line(&mut files.events, &obj);
    }

    pub fn event_disconnect(&self, fd: i32, peer: &str, port: u16) {
        let obj = json!({
            "ts": wall_iso_now(),
            "mono_us": self.mono_us() as u64,
            "proc": self.proc.as_str(),
            "plane": "event",
            "kind": "disconnect",
            "fd": fd,
            "peer": peer,
            "port": port,
        });
        let mut files = self.files.lock();
        Self::write_line(&mut files.events, &obj);
    }

    /// A process/spawn lifecycle event (emitted by the wrapper instance, and
    /// once by rs2client at startup so the timeline has a clear origin).
    pub fn event_process(&self, phase: &str, extra: serde_json::Value) {
        let mut obj = json!({
            "ts": wall_iso_now(),
            "mono_us": self.mono_us() as u64,
            "proc": self.proc.as_str(),
            "plane": "event",
            "kind": "process",
            "phase": phase,
            "pid": self.pid,
            "unix_ms": wall_unix_ms() as u64,
        });
        if let serde_json::Value::Object(m) = extra {
            for (k, v) in m {
                obj[k] = v;
            }
        }
        let mut files = self.files.lock();
        Self::write_line(&mut files.events, &obj);
    }

    /// Emit a `login_cipher_ready` marker (RE §8a): the Phase A→B boundary on the
    /// login s2c stream. Before this point the login reply protocol is PLAINTEXT
    /// (Phase A handshake); after it the lobby s2c is ISAAC ciphertext (Phase B).
    /// `login_s2c_bytes` is the running count of login-direction s2c bytes the
    /// recorder has observed BEFORE the cipher became ready, so the offline
    /// deframer can split `socket.jsonl` (conn=login, dir=s2c) / `raw-login-s2c.bin`
    /// at that byte offset: decode `[0, offset)` as plaintext and `[offset, end)`
    /// with the `login-s2c` ISAAC seed from keystream position 0.
    pub fn event_login_cipher_ready(&self, login_s2c_bytes: u64) {
        let obj = json!({
            "ts": wall_iso_now(),
            "mono_us": self.mono_us() as u64,
            "proc": self.proc.as_str(),
            "plane": "event",
            "kind": "login_cipher_ready",
            // Byte offset into the login s2c stream where Phase A (plaintext) ends
            // and Phase B (ISAAC ciphertext) begins.
            "login_s2c_bytes": login_s2c_bytes,
        });
        let mut files = self.files.lock();
        Self::write_line(&mut files.events, &obj);
    }

    /// Write one client-state oracle snapshot to `state-snapshots.jsonl` (RE §10).
    /// This is the ground truth the offline verifier replays our decoded s2c
    /// against ("client is king"). Every field is emitted ONLY when the client had
    /// it readable: `main_state` is omitted pre-login; `varps` is an object
    /// `{ "<id>": value }` emitted whenever the varp table was reachable (it can
    /// be read in the lobby); `player`/`skills`/`run_energy`/`run_weight` appear
    /// once in-world. `tick` is the recorder's monotonically-increasing snapshot
    /// sequence number (the client exposes no stable tick counter at a documented
    /// offset; the sequence lets the verifier order snapshots and identify the
    /// final/at-exit one as the highest tick).
    pub fn state_snapshot(&self, tick: u64, snap: &crate::oracle::Snapshot) {
        let mut obj = json!({
            "ts": wall_iso_now(),
            "mono_us": self.mono_us() as u64,
            "tick": tick,
            "proc": self.proc.as_str(),
            "kind": "state_snapshot",
        });
        if let Some(ms) = snap.main_state {
            obj["main_state"] = json!(ms);
        }
        // varps: emit the object whenever the table was reachable, even if empty
        // (an empty object is a meaningful "no varps set yet"; an ABSENT key means
        // the table was unreadable). Keys are the decimal varId as a string so the
        // verifier can look up a specific var directly.
        if snap.varps_readable {
            let mut varps = serde_json::Map::with_capacity(snap.varps.len());
            for (id, value) in &snap.varps {
                varps.insert(id.to_string(), json!(value));
            }
            obj["varps"] = serde_json::Value::Object(varps);
        }
        if let Some((x, y, plane)) = snap.player {
            obj["player"] = json!({ "x": x, "y": y, "plane": plane });
        }
        if !snap.skills.is_empty() {
            let skills: Vec<_> = snap
                .skills
                .iter()
                .map(|s| json!({ "id": s.id, "level": s.level, "base": s.base, "xp": s.xp }))
                .collect();
            obj["skills"] = json!(skills);
        }
        if let Some(e) = snap.run_energy {
            obj["run_energy"] = json!(e);
        }
        if let Some(w) = snap.run_weight {
            obj["run_weight"] = json!(w);
        }
        // local_player: the AVATAR render state. Emit the object whenever the
        // client base is known (main_state present) so a pre-avatar "lip null"
        // state is itself visible. Pointers are hex strings; every sub-field is
        // emitted only when it was readable (omit, never fabricate).
        if snap.main_state.is_some() || snap.local_player.lip.is_some() {
            obj["local_player"] = encode_local_player(&snap.local_player);
        }
        let mut files = self.files.lock();
        Self::write_line(&mut files.state_snapshots, &obj);
    }

    /// Write the one-shot `prot-table.json`: the client's OWN live opcode table,
    /// dumped at session start so the offline enricher's framing (opcode →
    /// sizeClass) and naming (handler fingerprint) come from the client's table
    /// for THIS build — rev-agnostic. Written once per session (the caller guards
    /// the single emit). Shape:
    /// ```json
    /// { "build": "RS2Engine-948-NXT-5", "image_base": "0x100000000",
    ///   "server_prot": [ {"op":N,"size_class":S,"handler":"0x<vmaddr>","handler_sig":"<hex 32B>"}, … ],
    ///   "client_prot":  [ … same shape … ] }
    /// ```
    /// `handler`/`handler_sig` are omitted (JSON `null`) for an entry whose handler
    /// chain could not be resolved; the opcode + `size_class` are still recorded so
    /// the framing table stays complete. `handler` is IMAGE-RELATIVE (a vmaddr) so
    /// it is stable across runs and matches the RE docs / `handler-sigs.json`.
    pub fn write_prot_table(
        &self,
        build: &str,
        image_base: &str,
        server_prot: &[crate::prot_table::ProtEntryDump],
        client_prot: &[crate::prot_table::ProtEntryDump],
    ) {
        let encode = |entries: &[crate::prot_table::ProtEntryDump]| -> Vec<serde_json::Value> {
            entries
                .iter()
                .map(|e| {
                    let mut obj = json!({ "op": e.op, "size_class": e.size_class });
                    // Emit handler/handler_sig only when the chain resolved; an
                    // absent (null) handler means "opcode registered, handler not
                    // resolvable" — never a fabricated address.
                    obj["handler"] = match e.handler_vmaddr {
                        Some(va) => json!(format!("0x{va:x}")),
                        None => serde_json::Value::Null,
                    };
                    obj["handler_sig"] = match &e.handler_sig {
                        Some(bytes) => {
                            let hex: String = bytes.iter().map(|b| format!("{b:02x}")).collect();
                            json!(hex)
                        }
                        None => serde_json::Value::Null,
                    };
                    obj
                })
                .collect()
        };

        let obj = json!({
            "build": build,
            "image_base": image_base,
            "server_prot": encode(server_prot),
            "client_prot": encode(client_prot),
        });
        let path = self.dir.join("prot-table.json");
        if let Ok(s) = serde_json::to_string_pretty(&obj) {
            let _ = fs::write(path, s);
        }
    }

    // -- Optional cross-validation sinks --------------------------------------

    /// Append raw bytes to `raw-<role>-<dir>.bin` (role = login|game, dir = s2c|c2s).
    pub fn raw_bin(&self, role: &str, dir: &str, body: &[u8]) {
        let mut bins = self.raw_bins.lock();
        let slot = match (role, dir) {
            ("login", "s2c") => &mut bins.login_s2c,
            ("login", "c2s") => &mut bins.login_c2s,
            ("game", "s2c") => &mut bins.game_s2c,
            ("game", "c2s") => &mut bins.game_c2s,
            _ => return,
        };
        if slot.is_none() {
            let path = self.dir.join(format!("raw-{role}-{dir}.bin"));
            *slot = OpenOptions::new().create(true).append(true).open(path).ok();
        }
        if let Some(f) = slot.as_mut() {
            let _ = f.write_all(body);
            let _ = f.flush();
        }
    }

    /// Write the REAL ISAAC construction seeds — one line PER (conn, dir) — to
    /// `isaac-keys.txt`. These are the genuine 4-int session keys captured at the
    /// `jag::Isaac::Init` hook and reconciled to each connection+direction by
    /// matching the seeded ISAAC state pointer against `conn+0x40` (send / c2s)
    /// and `conn+0x2B8` (recv / s2c) on the game and login connections (RE §7).
    ///
    /// Each TCP connection (login AND game/world) builds its OWN ciphers with
    /// DIFFERENT keys, so a single seed cannot deframe both — this is the multi-
    /// connection fix. `seeds` is `[(role_tag, [4 ints]), ...]` where `role_tag`
    /// is `game-s2c` / `game-c2s` / `login-s2c` / `login-c2s`. The stored ints
    /// are EXACTLY what the client seeded that cipher with (the recv seed already
    /// includes the +50 vs the matching send seed), so the offline deframer must
    /// use a per-line seed VERBATIM (it must NOT add 50 again when it reads a
    /// `-s2c` line written here).
    ///
    /// INCREMENTAL + write-once-per-role: the emit hooks call this repeatedly as
    /// connections resolve; we only append a role's line the first time it
    /// appears, so the file grows from (say) the login pair to all four lines
    /// without duplicates and never drops a connection.
    ///
    /// CRITICAL parser contract (see `CrossValidator.classifyIsaac` /
    /// `CaptureDeframer.parseHexKeys`):
    ///   * every seed line must contain the token `hex` and its four 0x-prefixed
    ///     values, and
    ///   * the file must NOT contain the substring "fingerprint, not a seed"
    ///     (any case) anywhere — its presence flags the file as fingerprint-only
    ///     and disables the independent deframe.
    /// The header below avoids that marker, the token `hex`, and any literal
    /// 0x-word so a comment line is never mistaken for a seed line. Each value
    /// line is prefixed with its `<role>-<dir>` tag so the deframer can select
    /// the correct seed PER PLANE (e.g. the `game-s2c` seed for the game s2c
    /// socket) instead of using one connection's seed for every plane.
    pub fn isaac_seeds(&self, seeds: &[(&str, [i32; 4])]) {
        if seeds.is_empty() {
            return;
        }
        let mut ik = self.isaac.lock();
        if ik.file.is_none() {
            ik.file = OpenOptions::new()
                .create(true)
                .append(true)
                .open(self.dir.join("isaac-keys.txt"))
                .ok();
        }
        // Write the header exactly once, before any value line.
        if !ik.header_written {
            if let Some(f) = ik.file.as_mut() {
                let header = "\
# Real ISAAC construction seeds — one line per connection and direction.
# Each line carries the four raw session keys captured at the client's ISAAC
# seeder and matched to that connection+direction by its cipher-state pointer.
# Every TCP connection (login and game/world) is seeded independently, so the
# game lines and the login lines hold different keys. A recv (s2c) line already
# includes the standard recv delta versus the matching send (c2s) line, so the
# offline deframer should use each line's values directly for its plane.
";
                let _ = f.write_all(header.as_bytes());
            }
            ik.header_written = true;
        }
        for (role, seed) in seeds {
            if ik.roles_written.iter().any(|r| r == role) {
                continue; // this (conn, dir) line is already in the file
            }
            // The token `hex` makes the parser select this line; the four 0x
            // words are the raw seed for THIS (conn, dir). Mask to u32 first.
            let line = format!(
                "{role} ISAAC keys (hex): 0x{:08x},0x{:08x},0x{:08x},0x{:08x}\n",
                seed[0] as u32, seed[1] as u32, seed[2] as u32, seed[3] as u32,
            );
            if let Some(f) = ik.file.as_mut() {
                let _ = f.write_all(line.as_bytes());
                let _ = f.flush();
            }
            ik.roles_written.push((*role).to_string());
        }
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use crate::oracle::{SkillEntry, Snapshot};

    fn temp_session() -> (Session, PathBuf) {
        let mut base = std::env::temp_dir();
        base.push(format!(
            "darkan-rec-test-{}-{}",
            std::process::id(),
            mono_now_ns()
        ));
        let s = Session::create(&base, Proc::Rs2client, "local", "RS2Engine-948-NXT-5", "local", None)
            .expect("create session");
        let dir = s.dir().to_path_buf();
        (s, dir)
    }

    fn read_jsonl(dir: &Path, name: &str) -> Vec<serde_json::Value> {
        let txt = fs::read_to_string(dir.join(name)).unwrap_or_default();
        txt.lines()
            .filter(|l| !l.trim().is_empty())
            .map(|l| serde_json::from_str::<serde_json::Value>(l).expect("valid json line"))
            .collect()
    }

    /// The state-snapshots.jsonl contract: a full in-world snapshot emits every
    /// documented key with the right shapes.
    #[test]
    fn state_snapshot_emits_full_contract() {
        let (s, dir) = temp_session();
        let snap = Snapshot {
            main_state: Some(30),
            varps: vec![(173, 1), (1021, 7)],
            varps_readable: true,
            player: Some((3200, 3200, 0)),
            skills: vec![SkillEntry { id: 0, level: 10, base: 10, xp: 1154 }],
            run_energy: Some(200),
            run_weight: Some(-5),
            ..Default::default()
        };
        s.state_snapshot(42, &snap);

        let lines = read_jsonl(&dir, "state-snapshots.jsonl");
        assert_eq!(lines.len(), 1);
        let o = &lines[0];
        assert_eq!(o["kind"], "state_snapshot");
        assert_eq!(o["tick"], 42);
        assert_eq!(o["main_state"], 30);
        assert!(o.get("ts").is_some() && o.get("mono_us").is_some());
        // varps is an OBJECT keyed by stringified varId.
        assert_eq!(o["varps"]["173"], 1);
        assert_eq!(o["varps"]["1021"], 7);
        // player tile object.
        assert_eq!(o["player"]["x"], 3200);
        assert_eq!(o["player"]["y"], 3200);
        assert_eq!(o["player"]["plane"], 0);
        // skills array of {id,level,base,xp}.
        assert_eq!(o["skills"][0]["id"], 0);
        assert_eq!(o["skills"][0]["level"], 10);
        assert_eq!(o["skills"][0]["base"], 10);
        assert_eq!(o["skills"][0]["xp"], 1154);
        assert_eq!(o["run_energy"], 200);
        assert_eq!(o["run_weight"], -5);

        let _ = fs::remove_dir_all(&dir);
    }

    /// Pre-login / unreadable chains: only the fields that were readable appear;
    /// the rest are OMITTED (never null, never fabricated).
    #[test]
    fn state_snapshot_omits_unreadable_fields() {
        let (s, dir) = temp_session();
        // Lobby-ish: main_state + varp table reachable but empty; nothing in-world.
        let snap = Snapshot {
            main_state: Some(20),
            varps: vec![],
            varps_readable: true,
            ..Default::default()
        };
        s.state_snapshot(0, &snap);

        let o = &read_jsonl(&dir, "state-snapshots.jsonl")[0];
        assert_eq!(o["main_state"], 20);
        // varps present but empty (table reachable, nothing set yet).
        assert!(o["varps"].is_object());
        assert_eq!(o["varps"].as_object().unwrap().len(), 0);
        // in-world fields absent entirely.
        assert!(o.get("player").is_none());
        assert!(o.get("skills").is_none());
        assert!(o.get("run_energy").is_none());
        assert!(o.get("run_weight").is_none());

        // A snapshot where the varp table itself is unreadable omits `varps` too.
        let snap2 = Snapshot::default();
        s.state_snapshot(1, &snap2);
        let lines = read_jsonl(&dir, "state-snapshots.jsonl");
        let o2 = &lines[1];
        assert!(o2.get("varps").is_none());
        assert!(o2.get("main_state").is_none());
        assert_eq!(o2["kind"], "state_snapshot");
        assert_eq!(o2["tick"], 1);

        let _ = fs::remove_dir_all(&dir);
    }

    /// The local_player object contract: pointers as hex strings, the
    /// visible-flag/model handle present, and BOTH the render tile and the logical
    /// target tile so the drift can be read directly. Fields that were unreadable
    /// (None) must be ABSENT.
    #[test]
    fn state_snapshot_emits_local_player_block() {
        use crate::oracle::LocalPlayer;
        let (s, dir) = temp_session();
        let mut lp = LocalPlayer::default();
        lp.lip = Some(0xdead_beef);
        lp.server_index = Some(1);
        lp.index_is_one = Some(true);
        lp.override_avatar = Some(0);
        lp.avatar = Some(0x1_2345_6780);
        lp.avatar_source = "slot";
        lp.visible_flag = Some(0);
        lp.render_model = Some(0); // model NOT loaded
        lp.render_graph_node = Some(0xabc0);
        lp.render_tile = Some((3231, 3110, 0));
        lp.render_scene_fine = Some((1_655_040.0, 1_592_576.0));
        lp.render_pos_double = Some((1_655_040.0, 1_592_576.0));
        lp.target_tile = Some((3231, 3112)); // logical Y two tiles north of render
        lp.target_waypoint_fine = Some((1_655_040.0, 1_593_600.0));
        lp.plane = Some(0);
        lp.size = Some(1);
        lp.extra_models = Some(0);
        // BLOCKED-compose state: pending object present but composed_flag 0 (async
        // gate not satisfied) — exactly why render_model above is still null.
        lp.pending_appearance = Some(0x9_9990_0000);
        // SetAppearance ran (compose started): current appearance applied + non-null.
        lp.current_appearance = Some(0x8_8880_0000);
        lp.pending_needs_async_load = Some(1);
        lp.pending_0x89 = Some(0);
        lp.pending_composed_flag = Some(0);
        lp.pending_res_7c = Some(0x111);
        lp.pending_res_80 = Some(0x222);
        lp.pending_0x84 = Some(0);
        // Lifecycle witnesses: decode ran (decode_witness != 0) but compose has not
        // produced a model — body present yet still dirty (handle null, cacheid
        // sentinel), and the lerp has no active waypoint (-1 = drift fallback).
        lp.body_type_model = Some(0x7_7770_0000);
        lp.decode_witness = Some(42);
        lp.lerp_end_tick = Some(-1);
        lp.map_square_bind = Some(0); // not bound
        lp.target_fine = Some((1_655_040.0, 0.0, 1_593_600.0));
        lp.prev_fine = Some((1_655_040.0, 0.0, 1_592_576.0));
        lp.model_bind_state = None; // render_model null => byte not read
        lp.compose_dirty = Some(1);
        lp.compose_cacheid = Some(-1); // 0xffffffff dirty sentinel
        lp.compose_handle = Some(0); // mesh not built yet
        lp.compose_gender = Some(0);

        let snap = Snapshot {
            main_state: Some(30),
            local_player: lp,
            ..Default::default()
        };
        s.state_snapshot(7, &snap);

        let o = &read_jsonl(&dir, "state-snapshots.jsonl")[0];
        let p = &o["local_player"];
        assert!(p.is_object());
        // pointers are hex strings
        assert_eq!(p["lip"], "0xdeadbeef");
        assert_eq!(p["avatar"], "0x123456780");
        assert_eq!(p["avatar_source"], "slot");
        assert_eq!(p["server_index"], 1);
        assert_eq!(p["index_is_one"], true);
        // the two prime "won't render" signals
        assert_eq!(p["visible_flag"], 0);
        assert_eq!(p["render_model"], "0x0");
        // render vs logical position both present (the drift read)
        assert_eq!(p["render_tile"]["y"], 3110);
        assert_eq!(p["target_tile"]["y"], 3112);
        // pending-appearance gate: non-null pointer + composed_flag 0 = compose BLOCKED
        // on the async resource group (the documented cause of the null render_model).
        assert_eq!(p["pending_appearance"], "0x999900000");
        // current_appearance non-null => SetAppearance ran (compose started).
        assert_eq!(p["current_appearance"], "0x888800000");
        assert_eq!(p["pending_needs_async_load"], 1);
        assert_eq!(p["pending_composed_flag"], 0);
        assert_eq!(p["pending_res_7c"], 0x111);
        assert_eq!(p["pending_res_80"], 0x222);
        // Lifecycle witnesses: ptrs are hex, ints are numbers, the {x,elev,z} fine
        // triples are objects. body composed-but-dirty + no waypoint (drift).
        assert_eq!(p["body_type_model"], "0x777700000");
        assert_eq!(p["decode_witness"], 42);
        assert_eq!(p["lerp_end_tick"], -1);
        assert_eq!(p["map_square_bind"], "0x0");
        assert_eq!(p["target_fine"]["z"], 1_593_600.0);
        assert_eq!(p["target_fine"]["elev"], 0.0);
        assert_eq!(p["prev_fine"]["z"], 1_592_576.0);
        assert_eq!(p["compose_dirty"], 1);
        assert_eq!(p["compose_cacheid"], -1);
        assert_eq!(p["compose_handle"], "0x0");
        assert_eq!(p["compose_gender"], 0);
        // model_bind_state was None (render_model null) => ABSENT.
        assert!(p.get("model_bind_state").is_none());
        let _ = fs::remove_dir_all(&dir);
    }

    /// When the pending-appearance pointer is readable-but-null (`Some(0)`), the
    /// pointer field is still emitted as "0x0" (compose already ran / no pending),
    /// while the gate bytes/keys — unreadable because there is no object — are ABSENT.
    /// This is the "compose ran but produced null" signal, distinct from "blocked".
    #[test]
    fn local_player_null_pending_emits_ptr_omits_gate() {
        use crate::oracle::LocalPlayer;
        let (s, dir) = temp_session();
        let mut lp = LocalPlayer::default();
        lp.avatar = Some(0x1_2345_6780);
        lp.avatar_source = "slot";
        lp.pending_appearance = Some(0); // readable, null
        lp.current_appearance = Some(0); // readable, null => appearance never applied

        let snap = Snapshot {
            main_state: Some(30),
            local_player: lp,
            ..Default::default()
        };
        s.state_snapshot(8, &snap);
        let o = &read_jsonl(&dir, "state-snapshots.jsonl")[0];
        let p = &o["local_player"];
        assert_eq!(p["pending_appearance"], "0x0");
        // current_appearance "0x0" + pending "0x0" => the compose never ran for the avatar.
        assert_eq!(p["current_appearance"], "0x0");
        assert!(p.get("pending_composed_flag").is_none());
        assert!(p.get("pending_needs_async_load").is_none());
        let _ = fs::remove_dir_all(&dir);
    }

    /// When `lip` is null and `main_state` is set (logged-out-but-base-known), the
    /// local_player block is still present and shows `avatar_source="none"` so the
    /// "no avatar yet" state is itself visible; the avatar pointer is ABSENT.
    #[test]
    fn local_player_block_present_but_empty_when_not_logged_in() {
        let (s, dir) = temp_session();
        let snap = Snapshot {
            main_state: Some(20), // lobby; base known, no avatar
            ..Default::default()
        };
        s.state_snapshot(0, &snap);
        let o = &read_jsonl(&dir, "state-snapshots.jsonl")[0];
        let p = &o["local_player"];
        assert!(p.is_object());
        assert_eq!(p["avatar_source"], "none");
        assert!(p.get("lip").is_none());
        assert!(p.get("avatar").is_none());
        assert!(p.get("visible_flag").is_none());
        let _ = fs::remove_dir_all(&dir);
    }

    /// The login_cipher_ready boundary marker carries the Phase-A byte offset.
    #[test]
    fn login_cipher_ready_marker_contract() {
        let (s, dir) = temp_session();
        s.event_login_cipher_ready(13_312);
        let evs = read_jsonl(&dir, "events.jsonl");
        let m = evs
            .iter()
            .find(|e| e["kind"] == "login_cipher_ready")
            .expect("marker present");
        assert_eq!(m["plane"], "event");
        assert_eq!(m["login_s2c_bytes"], 13_312);
        assert!(m.get("mono_us").is_some());

        let _ = fs::remove_dir_all(&dir);
    }

    /// The prot-table.json contract (the load-bearing serialization — `strings`/
    /// section-grep can't see the inlined `json!` key literals, so this round-trip
    /// is the proof the keys/shape are right). A resolved entry carries
    /// `handler`+`handler_sig`; an unresolved one carries them as JSON `null` but
    /// still reports `op`+`size_class`.
    #[test]
    fn prot_table_json_contract() {
        use crate::prot_table::ProtEntryDump;
        let (s, dir) = temp_session();

        let server = vec![
            ProtEntryDump {
                op: 0x0d,
                size_class: 1,
                handler_vmaddr: Some(0x1_000448a0),
                handler_sig: Some(vec![0x55, 0x48, 0x89, 0xE5]),
            },
            ProtEntryDump {
                // handler chain unresolved → handler/handler_sig must be null,
                // but the opcode + sizeClass are still recorded.
                op: 0x51,
                size_class: -1,
                handler_vmaddr: None,
                handler_sig: None,
            },
        ];
        let client: Vec<ProtEntryDump> = Vec::new();
        s.write_prot_table("RS2Engine-948-NXT-5", "0x100000000", &server, &client);

        let txt = fs::read_to_string(dir.join("prot-table.json")).expect("prot-table.json written");
        let v: serde_json::Value = serde_json::from_str(&txt).expect("valid json");

        assert_eq!(v["build"], "RS2Engine-948-NXT-5");
        assert_eq!(v["image_base"], "0x100000000");
        // client_prot present (empty) — an absent key would be wrong.
        assert!(v["client_prot"].is_array());
        assert_eq!(v["client_prot"].as_array().unwrap().len(), 0);

        let sp = v["server_prot"].as_array().expect("server_prot array");
        assert_eq!(sp.len(), 2);
        // Resolved entry: hex-formatted vmaddr + hex sig string.
        assert_eq!(sp[0]["op"], 0x0d);
        assert_eq!(sp[0]["size_class"], 1);
        assert_eq!(sp[0]["handler"], "0x1000448a0");
        assert_eq!(sp[0]["handler_sig"], "554889e5");
        // Unresolved entry: handler/handler_sig are JSON null, op/size_class kept.
        assert_eq!(sp[1]["op"], 0x51);
        assert_eq!(sp[1]["size_class"], -1);
        assert!(sp[1]["handler"].is_null());
        assert!(sp[1]["handler_sig"].is_null());

        let _ = fs::remove_dir_all(&dir);
    }
}
