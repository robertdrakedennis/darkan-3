//! Client-state ORACLE — read the client's OWN decoded state (the "client is
//! king" cross-check, RE §10).
//!
//! The offline verifier replays our captured s2c packets and confirms they
//! reproduce the client's own state. To do that it needs ground-truth snapshots
//! of what the client actually believes: its varps, its local-player tile, its
//! skills, run energy/weight, and the main game-state. This module reads those
//! straight out of the live Client struct via the documented pointer chains.
//!
//! DISCIPLINE (identical to the rest of the recorder): every hop is null-checked
//! through `mem::*`; a single unreadable pointer yields `None` for that field and
//! NEVER faults the client. varps are meaningful as early as the lobby; the
//! player/skills/energy chains are only populated once in-world, so they are
//! simply absent (omitted) until then.

use crate::mem;
use crate::offsets::avatar as av;
use crate::offsets::client_oracle as o;

/// The server index our server assigns to the local player (the slot the avatar
/// must occupy for RefreshLocalVisibility to mark it visible).
const EXPECTED_LOCAL_INDEX: i32 = 1;

/// Per-skill snapshot entry. `id` is the skill index; the rest are the client's
/// own decoded values (boosted/current level, base/real level, total xp).
#[derive(Clone, Copy)]
pub struct SkillEntry {
    pub id: i32,
    pub level: i32,
    pub base: i32,
    pub xp: i32,
}

/// The LOCAL-PLAYER AVATAR render state, read straight off the client's avatar
/// (graphEntity / PathingEntity) object so we can SEE why the local avatar won't
/// render / drifts (instead of predicting from the protocol). The avatar is the
/// SAME object the tile path reaches as `entity` (playerList[serverIdx].node
/// +0x38); RefreshLocalVisibility/DecodeAppearance resolve it identically. Every
/// field is `Option` — a field is `Some` only when its hop was readable, so the
/// writer omits whatever the client hasn't populated (never fabricates).
///
/// `Default` is hand-implemented so `avatar_source` defaults to `"none"` (not
/// `""`): an unfilled block — the pre-login / no-avatar state — already reads as
/// "none" without any populated read.
pub struct LocalPlayer {
    /// `LoggedInPlayer*` (`*(Client + 0x19DB8)`). `None` => not logged in.
    pub lip: Option<usize>,
    /// `LoggedInPlayer + 0x48` (serverIndex / the slot the avatar must occupy).
    pub server_index: Option<i32>,
    /// Whether `server_index` equals the index our server assigns (1). Only set
    /// when `server_index` is readable.
    pub index_is_one: Option<bool>,
    /// Explicit local-avatar override pointer (`lip + 0x58`): non-zero means the
    /// client is using it directly as the local avatar (bypassing the slot).
    pub override_avatar: Option<usize>,
    /// The resolved avatar/graphEntity handle (`playerList[serverIndex].node+0x38`,
    /// or the override). `None`/0 => no avatar entity to render.
    pub avatar: Option<usize>,
    /// How the avatar was resolved: "override" | "slot" | "none".
    pub avatar_source: &'static str,
    /// VISIBLE flag `*(avatar+0x1070)` (0/1) — the ghidra-proven render-bind gate.
    pub visible_flag: Option<i32>,
    /// PathingEntity render-model pointer (`avatar+0xC58`). 0 => no model handle
    /// attached (model not loaded/built).
    pub render_model: Option<usize>,
    /// Render scene-graph GraphNode (`avatar+0x8`) — the node the integrator
    /// commits onto. 0 => avatar has no render node.
    pub render_graph_node: Option<usize>,
    /// Scene-bucket GraphNode (`avatar+0x268`) inserted by AttachToMapSquare. 0 =>
    /// not scene-attached.
    pub scene_bucket_graph_node: Option<usize>,
    /// The avatar's logical tile (x, y, plane) derived from the render GraphNode
    /// scene-fine — i.e. the INTERPOLATED render position rounded to a tile (this
    /// is what the existing `Snapshot.player` reports; included here for locality).
    pub render_tile: Option<(i32, i32, i32)>,
    /// Render scene-fine position (X, Y) as floats off the render GraphNode
    /// (`+0xF0`/`+0xF8`) — the live interpolated draw position (jitters mid-move).
    pub render_scene_fine: Option<(f32, f32)>,
    /// Committed render position doubles written by AdvanceRenderPosition
    /// (`avatar+0x2A8`=X, `avatar+0x2B8`=Y), absolute fine coords.
    pub render_pos_double: Option<(f64, f64)>,
    /// TARGET GPI waypoint (X, Y) floats (`avatar+0xDB0`/`+0xDB8`), absolute fine —
    /// the non-interpolated LOGICAL destination set by the movement decode.
    pub target_waypoint_fine: Option<(f32, f32)>,
    /// TARGET waypoint converted to a tile (the logical destination tile).
    pub target_tile: Option<(i32, i32)>,
    /// Previous GPI waypoint (X, Y) floats (`avatar+0xDA0`/`+0xDA8`), absolute fine.
    pub prev_waypoint_fine: Option<(f32, f32)>,
    /// Entity plane (`entity+0x40`) and size low byte (`entity+0x184`).
    pub plane: Option<i32>,
    pub size: Option<i32>,
    /// Extra obj-customisation models span (end - begin)/8 (`avatar+0x1270`/`+0x1278`):
    /// the count of appended custom models. 0 = none.
    pub extra_models: Option<i64>,
    /// `PlayerAppearancePending*` (`*(avatar+0x1298)`): the op22 ext-info appearance
    /// object whose async-load gate must pass before `ProcessPendingAppearance`
    /// composes `render_model`. `Some(0)` => slot readable but null (compose already
    /// ran / no pending); `Some(p)` non-zero => a compose is queued and its gate
    /// fields below tell us whether it is BLOCKED. `None` => avatar unreadable.
    pub pending_appearance: Option<usize>,
    /// `Appearance*` (`*(avatar+0x12A0)`): the APPLIED/current appearance, set by
    /// `Avatar::SetAppearance @0x100411a60` — which the per-tick compose calls just
    /// before building `render_model`. Read with the SAME guards as `pending_appearance`:
    /// `Some(0)` => avatar readable but no appearance applied yet; `Some(p)` non-zero =>
    /// SetAppearance ran (the compose STARTED). `None` => avatar unreadable. THE
    /// disambiguator when `render_model`/`pending_appearance` are both null: non-zero
    /// => compose ran but the model build failed; zero (with pending also 0) => the
    /// appearance was never applied (compose never ran for the local avatar).
    pub current_appearance: Option<usize>,
    /// `pending+0x88` (byte): needsAsyncLoad — 1 for op22-delivered appearances,
    /// which makes the compose wait on `SceneLoadRegistry::IsResourceGroupReady`.
    pub pending_needs_async_load: Option<i32>,
    /// `pending+0x89` (byte): adjacent gate/state byte (dumped for context).
    pub pending_0x89: Option<i32>,
    /// `pending+0x8a` (byte): composed flag — set to 1 once the async gate passes and
    /// `ProcessPendingAppearance` builds the model. 0 while the compose is still
    /// blocked on the resource group; this is the field that tells us BLOCKED vs done.
    pub pending_composed_flag: Option<i32>,
    /// `pending+0x7c` (u32): resource-group key A keyed into the SceneLoadRegistry.
    pub pending_res_7c: Option<i32>,
    /// `pending+0x80` (u32): resource-group key B keyed into the SceneLoadRegistry.
    pub pending_res_80: Option<i32>,
    /// `pending+0x84` (u32): adjacent resource/state word (dumped for context).
    pub pending_0x84: Option<i32>,

    // -- Avatar-lifecycle witnesses (decode -> compose -> mesh -> bind -> drift) --
    // Each pins the avatar's death to a stage; read with the SAME safe guards as
    // every field above (null/unreadable => `None` => the writer omits it).
    /// Body/skeleton model handle (`avatar+0x1068`) — COMPOSE WITNESS. `Some(0)` =>
    /// avatar readable but no body model composed; `Some(p)` non-zero => body built.
    /// `None` => avatar unreadable.
    pub body_type_model: Option<usize>,
    /// Title/prefix sprite (`avatar+0x10AC`, u32) — DECODE WITNESS. Tracks whether
    /// `DecodeAppearance` parsed the appearance blob for this avatar.
    pub decode_witness: Option<i32>,
    /// Lerp end-tick (`avatar+0xDBC`, i32) — DRIFT WITNESS. `-1` => no active
    /// waypoint => open-loop drift fallback in AdvanceRenderPosition.
    pub lerp_end_tick: Option<i32>,
    /// Bound map-square (`avatar+0xAA8`) — BIND WITNESS. `Some(0)` => not bound;
    /// `Some(p)` non-zero => bound to a loaded square.
    pub map_square_bind: Option<usize>,
    /// TARGET lerp waypoint {x, elev, z} fine floats (`avatar+0xDB0/+0xDB4/+0xDB8`) —
    /// the logical destination (x/z duplicate `target_waypoint_fine`; this adds elev).
    pub target_fine: Option<(f32, f32, f32)>,
    /// PREV lerp waypoint {x, elev, z} fine floats (`avatar+0xDA0/+0xDA4/+0xDA8`) —
    /// the lerp source (previous waypoint).
    pub prev_fine: Option<(f32, f32, f32)>,
    /// Per-frame model bind byte (`render_model+0x135`) — BIND WITNESS read ONLY when
    /// `render_model` is non-null (else `None`). Low bit = bound-this-frame.
    pub model_bind_state: Option<i32>,
    /// `body_obj+0x98` (byte): compose dirty flag — read only when `body_type_model`
    /// (== `body_obj`) is non-null.
    pub compose_dirty: Option<i32>,
    /// `body_obj+0x9C` (u32): compose cache id; `0xffffffff` is the dirty sentinel.
    pub compose_cacheid: Option<i32>,
    /// `body_obj+0xA0` (ptr): composed model handle; `Some(0)` => not yet built.
    pub compose_handle: Option<usize>,
    /// `body_obj+0x84` (u32): gender/body param stamped by `Avatar::SetAppearance`.
    pub compose_gender: Option<i32>,
}

impl Default for LocalPlayer {
    fn default() -> Self {
        LocalPlayer {
            lip: None,
            server_index: None,
            index_is_one: None,
            override_avatar: None,
            avatar: None,
            avatar_source: "none",
            visible_flag: None,
            render_model: None,
            render_graph_node: None,
            scene_bucket_graph_node: None,
            render_tile: None,
            render_scene_fine: None,
            render_pos_double: None,
            target_waypoint_fine: None,
            target_tile: None,
            prev_waypoint_fine: None,
            plane: None,
            size: None,
            extra_models: None,
            pending_appearance: None,
            current_appearance: None,
            pending_needs_async_load: None,
            pending_0x89: None,
            pending_composed_flag: None,
            pending_res_7c: None,
            pending_res_80: None,
            pending_0x84: None,
            body_type_model: None,
            decode_witness: None,
            lerp_end_tick: None,
            map_square_bind: None,
            target_fine: None,
            prev_fine: None,
            model_bind_state: None,
            compose_dirty: None,
            compose_cacheid: None,
            compose_handle: None,
            compose_gender: None,
        }
    }
}

/// A point-in-time snapshot of the client's decoded state. Every field is
/// optional: a field is `Some` only when its full pointer chain was readable, so
/// the writer can omit anything the client hasn't populated yet (the contract is
/// "omit a field, never crash / never fabricate").
#[derive(Default)]
pub struct Snapshot {
    /// `Client + MAIN_STATE` (0/10/20/23/30/35/37/40). `None` if the client base
    /// is unknown.
    pub main_state: Option<i32>,
    /// Active varps: `(varId, value)` for every node currently in the VALUES
    /// table. Readable in the lobby too. Empty (not `None`) when the table is
    /// reachable but holds nothing yet.
    pub varps: Vec<(i32, i32)>,
    /// Whether the varp VALUES table itself was reachable (so an EMPTY `varps`
    /// can be distinguished from "table unreadable" — only emit `varps` when this
    /// is true).
    pub varps_readable: bool,
    /// Local-player tile (x, y, plane) — meaningful once in-world.
    pub player: Option<(i32, i32, i32)>,
    /// Skills table — meaningful once in-world.
    pub skills: Vec<SkillEntry>,
    /// Run energy 0..255 — meaningful once in-world.
    pub run_energy: Option<i32>,
    /// Run weight (signed) — meaningful once in-world.
    pub run_weight: Option<i32>,
    /// LOCAL-PLAYER AVATAR render state (visible flag, model handle, render vs
    /// logical position). `lip` is `None` until logged in. Always present (the
    /// writer emits a `local_player` object whenever the client base is known so a
    /// "no avatar yet" state is itself visible).
    pub local_player: LocalPlayer,
}

/// Build a full snapshot from the published client base (`0` => everything that
/// needs the base is omitted). Reads are independent: a failure in one chain
/// (e.g. no logged-in player yet) does not stop the others.
pub fn snapshot(client_base: usize) -> Snapshot {
    let mut snap = Snapshot::default();
    if !mem::is_plausible(client_base) {
        return snap;
    }

    snap.main_state = mem::read_i32(client_base, crate::offsets::client::MAIN_STATE);

    read_varps(client_base, &mut snap);
    snap.player = read_player_tile(client_base);
    snap.skills = read_skills(client_base);
    let (energy, weight) = read_run(client_base);
    snap.run_energy = energy;
    snap.run_weight = weight;
    snap.local_player = read_local_player(client_base);

    snap
}

/// Read the LOCAL-PLAYER AVATAR render state (RE §10.6). Mirrors the binary's own
/// local-avatar resolution (`RefreshLocalVisibility @0x100033260` /
/// `DecodeAppearance @0x100031480`): the avatar is the explicit override
/// `*(lip+0x58)` if non-zero, else `*(playerList[lip+0x48].node + 0x38)`. Then it
/// dumps the visible-flag gate, the model handle, and BOTH the interpolated render
/// position and the logical target waypoint so the drift can be SEEN rather than
/// predicted. Every hop is null-checked; a missing hop just leaves that field
/// `None` (never faults the client).
fn read_local_player(client_base: usize) -> LocalPlayer {
    let mut lp = LocalPlayer::default(); // avatar_source defaults to "none"

    let lip = match mem::deref(client_base, o::LOGGED_IN_PLAYER) {
        Some(l) => {
            lp.lip = Some(l);
            l
        }
        None => return lp, // not logged in yet — nothing else to read
    };

    // serverIndex (the slot the avatar must occupy) + the "== 1" check.
    if let Some(idx) = mem::read_i32(lip, o::LIP_SERVER_INDEX) {
        lp.server_index = Some(idx);
        lp.index_is_one = Some(idx == EXPECTED_LOCAL_INDEX);
    }

    // Resolve the avatar EXACTLY as the binary does: explicit override first
    // (`*(lip+0x58)`), else the player-list slot `*(playerList[serverIndex].node
    // + 0x38)`. Read with `read_ptr` so a readable-but-null override is `Some(0)`
    // (distinguishable from an unreadable lip).
    let override_avatar = mem::read_ptr(lip, av::LIP_LOCAL_OVERRIDE).unwrap_or(0);
    lp.override_avatar = Some(override_avatar);
    let (avatar, source) = if mem::is_plausible(override_avatar) {
        (Some(override_avatar), "override")
    } else {
        // slot path: playerList[serverIndex].node + 0x38
        let slot = match (lp.server_index, mem::deref(client_base, o::PLAYER_MANAGER)) {
            (Some(idx), Some(pm)) if idx >= 0 => mem::deref(pm, o::PM_PLAYER_LIST)
                .and_then(|list| mem::deref(list, (idx as usize) * 8))
                .and_then(|node| mem::deref(node, o::NODE_ENTITY)),
            _ => None,
        };
        let src = if slot.is_some() { "slot" } else { "none" };
        (slot, src)
    };
    lp.avatar_source = source;

    let avatar = match avatar {
        Some(a) => {
            lp.avatar = Some(a);
            a
        }
        None => return lp, // no avatar entity to render
    };

    // The render-bind gate + the model handle (the two prime "won't render" causes).
    lp.visible_flag = mem::read_i32(avatar, av::VISIBLE_FLAG).map(|v| v & 0xff);
    lp.render_model = mem::read_ptr(avatar, av::RENDER_MODEL);
    lp.render_graph_node = mem::read_ptr(avatar, av::RENDER_GRAPH_NODE);
    lp.scene_bucket_graph_node = mem::read_ptr(avatar, av::SCENE_BUCKET_GRAPH_NODE);

    lp.plane = mem::read_i32(avatar, o::ENTITY_PLANE);
    lp.size = mem::read_i32(avatar, av::SIZE).map(|s| s & 0xff);
    let size = lp.size.unwrap_or(0);

    // INTERPOLATED render position: scene-fine floats off the render GraphNode
    // (avatar+0x8 → +0xF0/+0xF8). This is what is drawn this frame (jitters).
    if let Some(gn) = lp.render_graph_node.filter(|p| mem::is_plausible(*p)) {
        if let (Some(sx), Some(sy)) = (
            read_f32(gn, o::GRAPH_SCENE_X_FINE),
            read_f32(gn, o::GRAPH_SCENE_Y_FINE),
        ) {
            lp.render_scene_fine = Some((sx, sy));
            lp.render_tile = Some((
                scene_to_tile(sx, size),
                scene_to_tile(sy, size),
                lp.plane.unwrap_or(0),
            ));
        }
    }

    // Committed render position doubles (avatar+0x2A8 / +0x2B8).
    if let (Some(rx), Some(ry)) = (
        read_f64(avatar, av::RENDER_POS_X_DOUBLE),
        read_f64(avatar, av::RENDER_POS_Y_DOUBLE),
    ) {
        lp.render_pos_double = Some((rx, ry));
    }

    // LOGICAL destination: the GPI target waypoint (avatar+0xDB0/+0xDB8) and the
    // tile it maps to — held steady while the render pos interpolates toward it.
    if let (Some(tx), Some(ty)) = (
        read_f32(avatar, av::TARGET_WAYPOINT_X_FINE),
        read_f32(avatar, av::TARGET_WAYPOINT_Y_FINE),
    ) {
        lp.target_waypoint_fine = Some((tx, ty));
        lp.target_tile = Some((scene_to_tile(tx, size), scene_to_tile(ty, size)));
    }
    if let (Some(px), Some(py)) = (
        read_f32(avatar, av::PREV_WAYPOINT_X_FINE),
        read_f32(avatar, av::PREV_WAYPOINT_Y_FINE),
    ) {
        lp.prev_waypoint_fine = Some((px, py));
    }

    // Count of appended obj-customisation models ((end - begin)/8).
    if let (Some(begin), Some(end)) = (
        mem::read_ptr(avatar, av::EXTRA_MODELS_BEGIN),
        mem::read_ptr(avatar, av::EXTRA_MODELS_END),
    ) {
        lp.extra_models = Some(((end as i64) - (begin as i64)) / 8);
    }

    // PENDING-APPEARANCE async-load gate (why render_model stays null after op22).
    // `*(avatar+0x1298)` is the PlayerAppearancePending object built by op22
    // ext-info; the per-tick ProcessPendingAppearance composes render_model ONLY
    // once its async gate passes (composed_flag becomes 1). Read with `read_ptr`
    // so a readable-but-null slot is `Some(0)` (compose ran / no pending) — distinct
    // from an unreadable avatar (`None`). Only when the pointer is plausible do we
    // dump the gate bytes (needs-async-load / composed) + the resource-group keys.
    let pending = mem::read_ptr(avatar, av::PENDING_APPEARANCE).unwrap_or(0);
    lp.pending_appearance = Some(pending);
    // APPLIED/current appearance (`*(avatar+0x12A0)`): set by Avatar::SetAppearance just
    // before the model build. Read with the SAME guards as `pending` above (avatar is
    // already known-readable past the early return) so a readable-but-null slot is
    // `Some(0)` — distinguishing "SetAppearance ran, model build failed" (non-zero) from
    // "appearance never applied / compose never ran" (zero, with pending also zero).
    lp.current_appearance = Some(mem::read_ptr(avatar, av::CURRENT_APPEARANCE).unwrap_or(0));
    if mem::is_plausible(pending) {
        lp.pending_needs_async_load = mem::read_i32(pending, av::PENDING_NEEDS_ASYNC_LOAD).map(|v| v & 0xff);
        lp.pending_0x89 = mem::read_i32(pending, av::PENDING_BYTE_0X89).map(|v| v & 0xff);
        lp.pending_composed_flag = mem::read_i32(pending, av::PENDING_COMPOSED_FLAG).map(|v| v & 0xff);
        lp.pending_res_7c = mem::read_i32(pending, av::PENDING_RES_7C);
        lp.pending_res_80 = mem::read_i32(pending, av::PENDING_RES_80);
        lp.pending_0x84 = mem::read_i32(pending, av::PENDING_WORD_0X84);
    }

    // AVATAR-LIFECYCLE WITNESSES (decode -> compose -> mesh -> bind -> drift). `avatar`
    // is known-plausible here (past the early return), so each read is `Some(_)` when
    // the field was readable and `None` only on an unreadable hop — identical guard
    // discipline to every block above. Together they make the exact stage the local
    // avatar dies at directly visible per spawn.
    //
    // COMPOSE: body model handle (read with `read_ptr` so a readable-but-null body is
    // `Some(0)` => "not composed", distinct from an unreadable avatar `None`).
    let body_obj = mem::read_ptr(avatar, av::BODY_TYPE_MODEL).unwrap_or(0);
    lp.body_type_model = Some(body_obj);
    // DECODE: title/prefix sprite written by DecodeAppearance.
    lp.decode_witness = mem::read_i32(avatar, av::DECODE_WITNESS);
    // DRIFT: lerp end-tick (-1 => open-loop drift fallback).
    lp.lerp_end_tick = mem::read_i32(avatar, av::LERP_END_TICK);
    // BIND: bound map-square (`read_ptr` => `Some(0)` when readable-but-null).
    lp.map_square_bind = Some(mem::read_ptr(avatar, av::MAP_SQUARE_BIND).unwrap_or(0));

    // TARGET / PREV lerp waypoints as {x, elev, z} fine triples (emit only when all
    // three components read).
    if let (Some(x), Some(y), Some(z)) = (
        read_f32(avatar, av::TARGET_FINE_X),
        read_f32(avatar, av::TARGET_FINE_Y),
        read_f32(avatar, av::TARGET_FINE_Z),
    ) {
        lp.target_fine = Some((x, y, z));
    }
    if let (Some(x), Some(y), Some(z)) = (
        read_f32(avatar, av::PREV_FINE_X),
        read_f32(avatar, av::PREV_FINE_Y),
        read_f32(avatar, av::PREV_FINE_Z),
    ) {
        lp.prev_fine = Some((x, y, z));
    }

    // BIND (per-frame): model bind byte — ONLY when render_model is non-null (it is
    // only written by AdvanceRenderPosition inside the `if (render_model != 0)` tail).
    if let Some(model) = lp.render_model.filter(|p| mem::is_plausible(*p)) {
        lp.model_bind_state = mem::read_i32(model, av::MODEL_BIND_STATE).map(|v| v & 0xff);
    }

    // COMPOSE-OBJECT dirty state — only when `body_obj` (= *(avatar+0x1068)) is
    // non-null. SetAppearance leaves a just-set body with compose_handle==null AND
    // compose_cacheid==0xffffffff; once compose runs they populate.
    if mem::is_plausible(body_obj) {
        lp.compose_dirty = mem::read_i32(body_obj, av::COMPOSE_DIRTY).map(|v| v & 0xff);
        lp.compose_cacheid = mem::read_i32(body_obj, av::COMPOSE_CACHEID);
        lp.compose_handle = Some(mem::read_ptr(body_obj, av::COMPOSE_HANDLE).unwrap_or(0));
        lp.compose_gender = mem::read_i32(body_obj, av::COMPOSE_GENDER);
    }

    lp
}

/// Walk the PlayerVarDomain VALUES hashtable and collect every `(varId, value)`.
/// PlayerVarDomain is EMBEDDED in Client (NOT a pointer), so its base is
/// `client + PLAYER_VAR_DOMAIN`. We iterate each bucket's singly-linked node
/// chain (value @ node+0x8, next @ node+0x28) reading the varId from node+0x0.
///
/// Bounded on every axis (bucket count, chain length, total entries) so a
/// corrupt/poisoned table can never spin or allocate unboundedly.
fn read_varps(client_base: usize, snap: &mut Snapshot) {
    const MAX_BUCKETS: usize = 1 << 20; // sanity ceiling on bucketCount
    const MAX_CHAIN: usize = 4096; // per-bucket chain guard
    const MAX_ENTRIES: usize = 1 << 16; // total varps captured

    let pvd = client_base + o::PLAYER_VAR_DOMAIN; // embedded — address arithmetic only
    let bucket_array = match mem::deref(pvd, o::PVD_VALUES_BUCKET_ARRAY) {
        Some(b) => b,
        None => return, // table not reachable yet → leave varps_readable=false
    };
    let bucket_count = match mem::read_ptr(pvd, o::PVD_VALUES_BUCKET_COUNT) {
        Some(c) if c > 0 && c <= MAX_BUCKETS => c,
        _ => return,
    };

    // The table is reachable and has a plausible bucket count: from here on an
    // empty result is a genuine "no varps set", which the writer should emit.
    snap.varps_readable = true;

    for i in 0..bucket_count {
        if snap.varps.len() >= MAX_ENTRIES {
            break;
        }
        // bucketArray[i] is a node pointer (or null for an empty bucket).
        let mut node = match mem::deref(bucket_array, i * 8) {
            Some(n) => n,
            None => continue,
        };
        let mut chain = 0usize;
        let mut seen_first = node;
        while mem::is_plausible(node) && chain < MAX_CHAIN {
            chain += 1;
            if let (Some(var_id), Some(value)) = (
                mem::read_i32(node, o::PVD_NODE_VAR_ID),
                mem::read_i32(node, o::PVD_NODE_VALUE),
            ) {
                snap.varps.push((var_id, value));
                if snap.varps.len() >= MAX_ENTRIES {
                    break;
                }
            }
            let next = match mem::deref(node, o::PVD_NODE_NEXT) {
                Some(n) => n,
                None => break,
            };
            // Defend against a self/loop link without allocating a per-bucket set
            // (chains here are short; the first-node + counter guard suffices).
            if next == node || next == seen_first {
                break;
            }
            seen_first = node;
            node = next;
        }
    }
}

/// Resolve the local player's tile (x, y, plane) via the LoggedInPlayer →
/// PlayerManager → player-list → entity → graphNode chain (RE §10.2), then apply
/// the engine's float→tile formula. `None` if any hop is unreadable (i.e. not
/// in-world yet).
fn read_player_tile(client_base: usize) -> Option<(i32, i32, i32)> {
    let lip = mem::deref(client_base, o::LOGGED_IN_PLAYER)?;
    let server_idx = mem::read_i32(lip, o::LIP_SERVER_INDEX)?;
    if server_idx < 0 {
        return None;
    }
    let pm = mem::deref(client_base, o::PLAYER_MANAGER)?;
    let list = mem::deref(pm, o::PM_PLAYER_LIST)?;
    let node = mem::deref(list, (server_idx as usize) * 8)?;
    let entity = mem::deref(node, o::NODE_ENTITY)?;
    let graph_node = mem::deref(entity, o::ENTITY_GRAPH_NODE)?;

    let scene_x = read_f32(graph_node, o::GRAPH_SCENE_X_FINE)?;
    let scene_y = read_f32(graph_node, o::GRAPH_SCENE_Y_FINE)?;
    let plane = mem::read_i32(entity, o::ENTITY_PLANE)?;
    // size is a single byte; read the low byte of the i32 at +0x184.
    let size = mem::read_i32(entity, o::ENTITY_SIZE)? & 0xff;

    let tile_x = scene_to_tile(scene_x, size);
    let tile_y = scene_to_tile(scene_y, size);
    Some((tile_x, tile_y, plane))
}

/// `tile = round((sceneFine - 256 - (size<<8)) / 512)` — reproduces the engine's
/// `Entity.tile` exactly (RE §10.2).
fn scene_to_tile(scene_fine: f32, size: i32) -> i32 {
    let adj = scene_fine - 256.0 - ((size << 8) as f32);
    (adj / 512.0).round() as i32
}

/// Read the per-skill table (boosted level, base level, xp) from the status
/// object `*(MLM + 0x7520)` (RE §10.3). Bounded by the table's own size field
/// (and a hard ceiling). Empty when not in-world.
fn read_skills(client_base: usize) -> Vec<SkillEntry> {
    const MAX_SKILLS: i32 = 256;
    let mut out = Vec::new();
    let Some(status) = status_object(client_base) else {
        return out;
    };
    let Some(begin) = mem::deref(status, o::STAT_TABLE_BEGIN) else {
        return out;
    };
    let num = mem::read_i32(status, o::STAT_TABLE_SIZE).unwrap_or(0);
    if num <= 0 || num > MAX_SKILLS {
        return out;
    }
    for id in 0..num {
        let entry = begin + (id as usize) * o::STAT_ENTRY_STRIDE;
        // Read all three; only emit a fully-readable entry.
        if let (Some(xp), Some(base), Some(level)) = (
            mem::read_i32(entry, o::STAT_XP),
            mem::read_i32(entry, o::STAT_BASE_LEVEL),
            mem::read_i32(entry, o::STAT_BOOST_LEVEL),
        ) {
            out.push(SkillEntry { id, level, base, xp });
        }
    }
    out
}

/// Read run energy (0..255) + run weight (signed) from the same status object as
/// the StatTable (RE §10.4). Each is independently optional.
fn read_run(client_base: usize) -> (Option<i32>, Option<i32>) {
    match status_object(client_base) {
        Some(status) => (
            mem::read_i32(status, o::STATUS_RUN_ENERGY),
            mem::read_i32(status, o::STATUS_RUN_WEIGHT),
        ),
        None => (None, None),
    }
}

/// `*(MainLogicManager + 0x7520)` — the shared StatTable / run-status object
/// (RE §10.3/§10.4). `None` until the MainLogicManager + its status object exist.
fn status_object(client_base: usize) -> Option<usize> {
    let mlm = mem::deref(client_base, o::MAIN_LOGIC_MANAGER)?;
    mem::deref(mlm, o::MLM_STAT_TABLE)
}

/// Read an `f32` at `base + off`, or `None` if `base` is implausible. (Mirror of
/// `mem::read_i32` for the GraphNode scene-fine floats.)
#[inline]
fn read_f32(base: usize, off: usize) -> Option<f32> {
    mem::read_i32(base, off).map(|raw| f32::from_bits(raw as u32))
}

/// Read an `f64` at `base + off`, or `None` if `base` is implausible. Used for the
/// avatar's committed render-position doubles (`avatar+0x2A8`/`+0x2B8`).
#[inline]
fn read_f64(base: usize, off: usize) -> Option<f64> {
    mem::read_ptr(base, off).map(|raw| f64::from_bits(raw as u64))
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn scene_to_tile_matches_engine_formula() {
        // size 1 (1x1), sceneFine for tile origin: tile = round((fine-256-256)/512).
        // fine = 256 + (1<<8) + 512*t  =>  tile == t.
        for t in [0, 1, 50, 3200, 6400] {
            let fine = 256.0 + ((1 << 8) as f32) + 512.0 * t as f32;
            assert_eq!(scene_to_tile(fine, 1), t);
        }
        // A larger entity (size 2) uses (size<<8) in the same formula.
        let fine = 256.0 + ((2 << 8) as f32) + 512.0 * 100.0;
        assert_eq!(scene_to_tile(fine, 2), 100);
    }

    #[test]
    fn read_f32_roundtrips_through_i32_bits() {
        // Stage a value in a local and read it back via the same bit transform.
        let v: f32 = 3328.0;
        let raw = v.to_bits() as i32;
        assert_eq!(f32::from_bits(raw as u32), v);
    }

    #[test]
    fn null_base_yields_empty_snapshot() {
        let snap = snapshot(0);
        assert!(snap.main_state.is_none());
        assert!(!snap.varps_readable);
        assert!(snap.varps.is_empty());
        assert!(snap.player.is_none());
        assert!(snap.skills.is_empty());
        assert!(snap.run_energy.is_none());
        assert!(snap.run_weight.is_none());
        // The local-player block is empty too (no lip, no avatar) and never faults.
        assert!(snap.local_player.lip.is_none());
        assert!(snap.local_player.avatar.is_none());
        assert_eq!(snap.local_player.avatar_source, "none");
    }

    #[test]
    fn read_f64_roundtrips_through_u64_bits() {
        // Avatar render-pos doubles are read via the same bit transform as the
        // pointer read; prove the f64 reinterpretation is lossless.
        let v: f64 = 1_654_528.0; // a plausible fine X (≈ tile 3231 * 512)
        let raw = v.to_bits();
        assert_eq!(f64::from_bits(raw), v);
    }

    #[test]
    fn null_client_base_local_player_is_none_source() {
        // read_local_player on a null base returns the empty "none"-source block
        // without dereferencing anything.
        let lp = read_local_player(0);
        assert!(lp.lip.is_none());
        assert!(lp.avatar.is_none());
        assert!(lp.visible_flag.is_none());
        assert_eq!(lp.avatar_source, "none");
    }
}
