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
use std::collections::BTreeMap;

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

/// Numeric client-var record from the VARC record tree. The tree key is
/// `(recordKind,varId)`, so both `kind` and `id` are emitted.
pub struct VarcNumberEntry {
    pub kind: i32,
    pub id: i32,
    pub value_kind: i32,
    pub val: i64,
}

/// String client-var record from the VARC record tree. String values are EA
/// small strings at `node+0x48` (inline or heap).
pub struct VarcStringEntry {
    pub kind: i32,
    pub id: i32,
    pub value_kind: i32,
    pub str_val: String,
}

/// Occupied item-container slot from the client's persistent inventory store.
/// Empty slots (`item == -1`) are omitted by the reader.
pub struct InventorySlotEntry {
    pub slot: i32,
    pub item: i32,
    pub count: u32,
}

/// Item-container entry keyed by the raw client key `inventoryId*2 + domainBit`.
/// The derived `inv_id` and `domain_bit` are emitted too; callers must not key by
/// inventory id alone because the binary keeps both domains in the same vector.
pub struct InventoryEntry {
    pub key: i32,
    pub inv_id: i32,
    pub domain_bit: i32,
    pub slots: Vec<InventorySlotEntry>,
}

/// Scene player read from PlayerManager's full slot array. `idx` is the server
/// index/slot id; position is the client entity's rounded render tile.
pub struct ScenePlayerEntry {
    pub idx: i32,
    pub x: i32,
    pub y: i32,
    pub plane: i32,
}

/// Scene NPC read from NpcManager's active-index list and hash map. `type_id` is
/// the raw NPC type id for recorder-side `npc` gameval naming.
pub struct SceneNpcEntry {
    pub idx: i32,
    pub type_id: i32,
    pub x: i32,
    pub y: i32,
    pub plane: i32,
}

/// Local avatar committed appearance slot token. Array order is the client body
/// slot index; each pair is `{kitId,itemId}` with `-1` in the unused half.
pub struct AppearanceSlotEntry {
    pub kit_id: i32,
    pub item_id: i32,
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
    /// Numeric/long client-vars from the VARC record tree. `kind` is recordKind,
    /// `id` is varId, and `value_kind` is the client's stored discriminator
    /// (`0=int32`, `1=int64`).
    pub varcs: Vec<VarcNumberEntry>,
    /// String client-vars from the VARC record tree. Values are decoded from EA
    /// small-string storage at `node+0x48`.
    pub varcstrings: Vec<VarcStringEntry>,
    /// Whether the VARC record tree itself was reachable. Empty arrays are emitted
    /// only when this is true; absent arrays mean the domain/tree was unreadable.
    pub varcs_readable: bool,
    /// Occupied slots from the persistent item-container store. Each entry carries
    /// the raw key plus derived inventory id/domain bit. Empty slots are omitted.
    pub inventories: Vec<InventoryEntry>,
    /// Whether the item-container store vector itself was reachable. Empty arrays
    /// are emitted only when true; absent arrays mean the store was unreadable.
    pub inventories_readable: bool,
    /// Local-player tile (x, y, plane) — meaningful once in-world.
    pub player: Option<(i32, i32, i32)>,
    /// All active/readable player entities from PlayerManager slot array.
    pub players: Vec<ScenePlayerEntry>,
    /// Whether PlayerManager slot array was reachable. Empty means no readable
    /// active player entities only when this is true.
    pub players_readable: bool,
    /// All active/readable NPC entities from NpcManager active-index list.
    pub npcs: Vec<SceneNpcEntry>,
    /// Whether NpcManager active list + hash buckets were reachable. Empty means
    /// no readable active NPC entities only when this is true.
    pub npcs_readable: bool,
    /// Skills table — meaningful once in-world.
    pub skills: Vec<SkillEntry>,
    /// Run energy 0..255 — meaningful once in-world.
    pub run_energy: Option<i32>,
    /// Run weight (signed) — meaningful once in-world.
    pub run_weight: Option<i32>,
    /// LOCAL-avatar committed appearance identity from the applied appearance's
    /// equip context. This intentionally emits only stable kit/item tokens, not
    /// worn equipment inventory slots (inv94 already covers those) and not
    /// transient model handles.
    pub appearance: Vec<AppearanceSlotEntry>,
    /// Whether the applied appearance's slot-pair vector was fully reachable.
    /// Empty means zero slots only when true; absent means unreadable/not applied.
    pub appearance_readable: bool,
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
    read_varcs(client_base, &mut snap);
    read_inventories(client_base, &mut snap);
    snap.player = read_player_tile(client_base);
    read_scene_players(client_base, &mut snap);
    read_scene_npcs(client_base, &mut snap);
    snap.skills = read_skills(client_base);
    let (energy, weight) = read_run(client_base);
    snap.run_energy = energy;
    snap.run_weight = weight;
    let local_player = read_local_player(client_base);
    read_local_appearance(&local_player, &mut snap);
    snap.local_player = local_player;

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
        lp.pending_needs_async_load =
            mem::read_i32(pending, av::PENDING_NEEDS_ASYNC_LOAD).map(|v| v & 0xff);
        lp.pending_0x89 = mem::read_i32(pending, av::PENDING_BYTE_0X89).map(|v| v & 0xff);
        lp.pending_composed_flag =
            mem::read_i32(pending, av::PENDING_COMPOSED_FLAG).map(|v| v & 0xff);
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

/// Read LOCAL-avatar committed appearance token pairs. Source is the avatar's
/// APPLIED appearance object (`avatar+0x12A0`) and its equip context
/// (`appearance+0x98`), not the worn-equipment inventory and not model handles.
/// The emitted array preserves body-slot order; if any slot is unreadable, omit
/// the whole field rather than producing an ambiguous partial array.
fn read_local_appearance(lp: &LocalPlayer, snap: &mut Snapshot) {
    const MAX_APPEARANCE_SLOTS: usize = 64;

    let appearance = match lp.current_appearance {
        Some(p) if mem::is_plausible(p) => p,
        _ => match lp.avatar {
            Some(a) if mem::is_plausible(a) => match mem::deref(a, o::AVATAR_APPEARANCE_APPLIED) {
                Some(p) => p,
                None => return,
            },
            _ => return,
        },
    };
    let equip_ctx = match mem::deref(appearance, o::APPEARANCE_EQUIP_CONTEXT) {
        Some(p) => p,
        None => return,
    };
    let slot_count = match mem::read_ptr(equip_ctx, o::EQUIP_CTX_SLOT_COUNT) {
        Some(c) if c <= MAX_APPEARANCE_SLOTS => c,
        _ => return,
    };

    if slot_count == 0 {
        snap.appearance_readable = true;
        return;
    }

    let slot_pairs = match mem::deref(equip_ctx, o::EQUIP_CTX_SLOT_PAIRS) {
        Some(p) => p,
        None => return,
    };
    let mut out = Vec::with_capacity(slot_count);
    for slot in 0..slot_count {
        let pair = slot_pairs + slot * o::EQUIP_CTX_SLOT_STRIDE;
        let (Some(kit_id), Some(item_id)) = (
            mem::read_i32(pair, o::EQUIP_CTX_SLOT_KIT_ID),
            mem::read_i32(pair, o::EQUIP_CTX_SLOT_ITEM_ID),
        ) else {
            return;
        };
        out.push(AppearanceSlotEntry { kit_id, item_id });
    }

    snap.appearance = out;
    snap.appearance_readable = true;
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

/// Walk the client-var record trees. VARC storage is NOT varp-style buckets:
/// `Client+VARC_DOMAIN` is a pointer to the domain. `GetOrCreateInterfaceRecord`
/// inserts canonical record nodes under `domain+0x1E120`, but handlers receive a
/// record payload pointer (`node+0x28`) and `ActivateInterfaceRecord` also links
/// those payloads through the active update tree at `domain+0x20`.
///
/// Live captures showed the periodic/exit oracle can miss transient active
/// records and that the canonical tree may contain non-varc interface records
/// (e.g. record kind 7). We therefore enumerate both tree shapes, normalize to
/// the record payload layout, filter to varc record kinds 1/2, and dedupe by
/// `(recordKind,varId)`.
fn read_varcs(client_base: usize, snap: &mut Snapshot) {
    const MAX_NODES: usize = 1 << 16;

    let domain = match mem::deref(client_base, o::VARC_DOMAIN) {
        Some(d) => d,
        None => return,
    };

    let mut numbers = BTreeMap::<(i32, i32), VarcNumberEntry>::new();
    let mut strings = BTreeMap::<(i32, i32), VarcStringEntry>::new();
    let mut readable = false;

    if let Some(count) = mem::read_ptr(domain, o::VARC_RECORD_TREE_COUNT) {
        readable = true;
        if count > 0 {
            if let Some(root) = mem::deref(domain, o::VARC_RECORD_TREE_ROOT) {
                walk_varc_tree(root, MAX_NODES, |node| {
                    let record = node.saturating_add(o::VARC_NODE_RECORD_PAYLOAD);
                    read_varc_record(record, &mut numbers, &mut strings);
                });
            }
        }
    }

    if let Some(count) = mem::read_ptr(domain, o::VARC_ACTIVE_TREE_COUNT) {
        readable = true;
        if count > 0 {
            if let Some(root) = mem::deref(domain, o::VARC_ACTIVE_TREE_ROOT) {
                walk_varc_tree(root, MAX_NODES, |node| {
                    if let Some(record) = mem::deref(node, o::VARC_ACTIVE_NODE_RECORD) {
                        read_varc_record(record, &mut numbers, &mut strings);
                    }
                });
            }
        }
    }

    if !readable {
        return;
    }

    snap.varcs_readable = true;
    snap.varcs = numbers.into_values().collect();
    snap.varcstrings = strings.into_values().collect();
}

/// Walk the persistent item-container store written by UPDATE_INV_FULL_OP85 and
/// UPDATE_INV_PARTIAL_OP121. The store is an EASTL sorted vector at
/// `*(Client+0x197D8)`, keyed by `inventoryId*2 + (flags&1)`. Each container's
/// slots are another vector of inline u64 words: low i32 item id, high u32
/// quantity. Empty slots (`item == -1`) are omitted.
fn read_inventories(client_base: usize, snap: &mut Snapshot) {
    const MAX_ENTRIES: usize = 4096;
    const MAX_SLOTS: usize = 4096;

    let store = match mem::deref(client_base, o::ITEM_CONTAINER_STORE) {
        Some(s) => s,
        None => return,
    };
    let (entry_begin, entry_count) = match read_eastl_vector(
        store,
        o::ITEM_CONTAINER_ENTRY_BEGIN,
        o::ITEM_CONTAINER_ENTRY_END,
        o::ITEM_CONTAINER_ENTRY_CAP,
        o::ITEM_CONTAINER_ENTRY_STRIDE,
        MAX_ENTRIES,
    ) {
        Some(v) => v,
        None => return,
    };

    snap.inventories_readable = true;
    if entry_count == 0 {
        return;
    }

    for index in 0..entry_count {
        let entry = entry_begin + index * o::ITEM_CONTAINER_ENTRY_STRIDE;
        let Some(key) = mem::read_i32(entry, o::ITEM_CONTAINER_ENTRY_KEY) else {
            continue;
        };
        if key < 0 {
            continue;
        }

        let container = entry + o::ITEM_CONTAINER_ENTRY_PAYLOAD;
        let (slot_begin, slot_count) = match read_eastl_vector(
            container,
            o::ITEM_CONTAINER_SLOT_BEGIN,
            o::ITEM_CONTAINER_SLOT_END,
            o::ITEM_CONTAINER_SLOT_CAP,
            o::ITEM_CONTAINER_SLOT_STRIDE,
            MAX_SLOTS,
        ) {
            Some(v) => v,
            None => continue,
        };

        let mut slots = Vec::new();
        for slot in 0..slot_count {
            let Some(raw) = mem::read_ptr(slot_begin, slot * o::ITEM_CONTAINER_SLOT_STRIDE) else {
                continue;
            };
            let item = (raw as u32) as i32;
            if item == -1 {
                continue;
            }
            let count = (raw >> 32) as u32;
            slots.push(InventorySlotEntry {
                slot: slot as i32,
                item,
                count,
            });
        }

        if !slots.is_empty() {
            snap.inventories.push(InventoryEntry {
                key,
                inv_id: key >> 1,
                domain_bit: key & 1,
                slots,
            });
        }
    }
}

/// Enumerate every readable player entity by scanning PlayerManager's full slot
/// array (1..0x7ff). Render/pending vectors are packet-order helpers; the slot
/// array is the complete scene oracle source.
fn read_scene_players(client_base: usize, snap: &mut Snapshot) {
    let pm = match mem::deref(client_base, o::PLAYER_MANAGER) {
        Some(p) => p,
        None => return,
    };
    let slot_array = match mem::deref(pm, o::PM_PLAYER_LIST) {
        Some(s) => s,
        None => return,
    };

    snap.players_readable = true;
    for idx in 1..o::PM_PLAYER_LIST_CAPACITY {
        let Some(slot_rec) = mem::deref(slot_array, idx * 8) else {
            continue;
        };
        let Some(entity) = mem::deref(slot_rec, o::NODE_ENTITY) else {
            continue;
        };
        if let Some((x, y, plane)) = read_entity_tile(entity) {
            snap.players.push(ScenePlayerEntry {
                idx: idx as i32,
                x,
                y,
                plane,
            });
        }
    }
}

/// Enumerate active NPCs through NpcManager's compact active-index list, then
/// resolve each id through the manager hash table. The active list bounds the
/// scene cohort; hash lookup gives the entity pointer and type id.
fn read_scene_npcs(client_base: usize, snap: &mut Snapshot) {
    const MAX_NPCS: usize = 0x400; // npcMgr+0xA0A0..+0xB0A0 = 1024 u32 slots
    const MAX_BUCKETS: usize = 1 << 20;

    let npc_mgr = match mem::deref(client_base, o::NPC_MANAGER) {
        Some(n) => n,
        None => return,
    };
    let buckets = match mem::deref(npc_mgr, o::NPC_BUCKETS) {
        Some(b) => b,
        None => return,
    };
    let bucket_count = match mem::read_ptr(npc_mgr, o::NPC_BUCKET_COUNT) {
        Some(c) if c > 0 && c <= MAX_BUCKETS => c,
        _ => return,
    };
    let active_count = match mem::read_i32(npc_mgr, o::NPC_ACTIVE_COUNT) {
        Some(c) if c >= 0 => (c as usize).min(MAX_NPCS),
        _ => return,
    };

    snap.npcs_readable = true;
    for i in 0..active_count {
        let Some(idx) = mem::read_i32(npc_mgr, o::NPC_ACTIVE_INDICES + i * 4) else {
            continue;
        };
        if idx < 0 {
            continue;
        }
        let Some(entity) = lookup_npc_entity(buckets, bucket_count, idx as u32) else {
            continue;
        };
        let Some(type_id) = mem::read_i32(entity, o::NPC_ENTITY_TYPE_ID) else {
            continue;
        };
        if let Some((x, y, plane)) = read_entity_tile(entity) {
            snap.npcs.push(SceneNpcEntry {
                idx,
                type_id,
                x,
                y,
                plane,
            });
        }
    }
}

fn lookup_npc_entity(buckets: usize, bucket_count: usize, npc_index: u32) -> Option<usize> {
    const MAX_CHAIN: usize = 4096;
    let bucket = (npc_index as usize) % bucket_count;
    let mut node = mem::deref(buckets, bucket * 8)?;

    for _ in 0..MAX_CHAIN {
        if !mem::is_plausible(node) {
            return None;
        }
        if mem::read_i32(node, o::NPC_NODE_KEY)? as u32 == npc_index {
            return mem::deref(node, o::NPC_NODE_ENTITY);
        }
        let next = mem::read_ptr(node, o::NPC_NODE_NEXT).unwrap_or(0);
        if next == 0 || next == node {
            return None;
        }
        node = next;
    }
    None
}

/// Read EASTL vector begin/end/cap fields and return `(begin,count)`. Empty
/// vectors (`begin=end=0`) are readable and produce count 0. Non-empty vectors
/// must have plausible begin/end/cap, monotonic pointers, and stride alignment.
fn read_eastl_vector(
    base: usize,
    begin_off: usize,
    end_off: usize,
    cap_off: usize,
    stride: usize,
    max_count: usize,
) -> Option<(usize, usize)> {
    if stride == 0 || max_count == 0 {
        return None;
    }
    let begin = mem::read_ptr(base, begin_off)?;
    let end = mem::read_ptr(base, end_off)?;
    let cap = mem::read_ptr(base, cap_off)?;

    if begin == 0 && end == 0 {
        return Some((0, 0));
    }
    if !mem::is_plausible(begin) || end < begin || cap < end {
        return None;
    }

    let bytes = end - begin;
    if bytes % stride != 0 {
        return None;
    }
    Some((begin, (bytes / stride).min(max_count)))
}

fn walk_varc_tree<F>(root: usize, max_nodes: usize, mut visit: F)
where
    F: FnMut(usize),
{
    let mut stack = Vec::with_capacity(64);
    let mut seen = Vec::new();
    stack.push(root);

    while let Some(node) = stack.pop() {
        if seen.len() >= max_nodes || !mem::is_plausible(node) || seen.contains(&node) {
            continue;
        }
        seen.push(node);

        // Touch left/right/parent through mem::* and traverse only plausible
        // children. Parent/color are not emitted, but reading them keeps this walk
        // aligned to the documented red-black node layout and catches bad offsets
        // in tests.
        let left = mem::read_ptr(node, o::VARC_NODE_LEFT).unwrap_or(0);
        let right = mem::read_ptr(node, o::VARC_NODE_RIGHT).unwrap_or(0);
        let _parent = mem::read_ptr(node, o::VARC_NODE_PARENT).unwrap_or(0);
        let _color = mem::read_u8(node, o::VARC_NODE_COLOR).unwrap_or(0);
        for child in [right, left] {
            if mem::is_plausible(child) && !seen.contains(&child) && stack.len() < max_nodes {
                stack.push(child);
            }
        }

        visit(node);
    }
}

fn read_varc_record(
    record: usize,
    numbers: &mut BTreeMap<(i32, i32), VarcNumberEntry>,
    strings: &mut BTreeMap<(i32, i32), VarcStringEntry>,
) {
    let (Some(kind), Some(id), Some(value_kind)) = (
        mem::read_i32(record, o::VARC_RECORD_KIND),
        mem::read_i32(record, o::VARC_RECORD_VAR_ID),
        mem::read_i32(record, o::VARC_RECORD_VALUE_KIND),
    ) else {
        return;
    };

    match (kind, value_kind) {
        (1, 0) => {
            if let Some(v) = mem::read_i32(record, o::VARC_RECORD_VALUE) {
                numbers.insert(
                    (kind, id),
                    VarcNumberEntry {
                        kind,
                        id,
                        value_kind,
                        val: v as i64,
                    },
                );
            }
        }
        (1, 1) => {
            if let Some(v) = read_i64(record, o::VARC_RECORD_VALUE) {
                numbers.insert(
                    (kind, id),
                    VarcNumberEntry {
                        kind,
                        id,
                        value_kind,
                        val: v,
                    },
                );
            }
        }
        (2, 2) => {
            if let Some(s) = read_varc_string(record) {
                strings.insert(
                    (kind, id),
                    VarcStringEntry {
                        kind,
                        id,
                        value_kind,
                        str_val: s,
                    },
                );
            }
        }
        _ => {}
    }
}

fn read_varc_string(record: usize) -> Option<String> {
    const MAX_STRING_BYTES: usize = 0x4000;
    let tag = mem::read_u8(record, o::VARC_RECORD_STRING_INLINE_TAG)?;
    if (tag as usize) < o::VARC_STRING_INLINE_LIMIT {
        let len = (o::VARC_STRING_INLINE_LIMIT - 1).saturating_sub(tag as usize);
        if len > MAX_STRING_BYTES {
            return None;
        }
        let bytes = if len == 0 {
            Vec::new()
        } else {
            mem::copy_bytes(record + o::VARC_RECORD_VALUE, len)?
        };
        return Some(String::from_utf8_lossy(&bytes).into_owned());
    }

    let len = mem::read_ptr(record, o::VARC_RECORD_STRING_LENGTH)?;
    let cap = mem::read_ptr(record, o::VARC_RECORD_STRING_CAP_TAG)?;
    let heap_bit = 1usize << (usize::BITS as usize - 1);
    if len > MAX_STRING_BYTES || (cap & heap_bit) == 0 {
        return None;
    }
    let ptr = mem::deref(record, o::VARC_RECORD_VALUE)?;
    let bytes = if len == 0 {
        Vec::new()
    } else {
        mem::copy_bytes(ptr, len)?
    };
    Some(String::from_utf8_lossy(&bytes).into_owned())
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
    read_entity_tile(entity)
}

fn read_entity_tile(entity: usize) -> Option<(i32, i32, i32)> {
    let graph_node = mem::deref(entity, o::ENTITY_GRAPH_NODE)?;

    let scene_x = read_f32(graph_node, o::GRAPH_SCENE_X_FINE)?;
    let scene_y = read_f32(graph_node, o::GRAPH_SCENE_Y_FINE)?;
    if !scene_x.is_finite() || !scene_y.is_finite() {
        return None;
    }
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
            out.push(SkillEntry {
                id,
                level,
                base,
                xp,
            });
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

/// Read an `i64` at `base + off`, or `None` if `base` is implausible. Used for
/// long varcs (`valueKind=1`) stored raw at `node+0x48`.
#[inline]
fn read_i64(base: usize, off: usize) -> Option<i64> {
    mem::read_ptr(base, off).map(|raw| raw as u64 as i64)
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
        assert!(!snap.varcs_readable);
        assert!(snap.varcs.is_empty());
        assert!(snap.varcstrings.is_empty());
        assert!(!snap.inventories_readable);
        assert!(snap.inventories.is_empty());
        assert!(!snap.appearance_readable);
        assert!(snap.appearance.is_empty());
        assert!(!snap.players_readable);
        assert!(snap.players.is_empty());
        assert!(!snap.npcs_readable);
        assert!(snap.npcs.is_empty());
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
    fn local_appearance_reads_applied_slot_token_pairs_in_order() {
        let mut appearance = vec![0u8; o::APPEARANCE_EQUIP_CONTEXT + 8];
        let mut equip_ctx = vec![0u8; o::EQUIP_CTX_SLOT_PAIRS + 8];
        let mut slots = vec![0u8; o::EQUIP_CTX_SLOT_STRIDE * 3];

        write_i32(&mut slots, o::EQUIP_CTX_SLOT_KIT_ID, 8);
        write_i32(&mut slots, o::EQUIP_CTX_SLOT_ITEM_ID, -1);
        write_i32(
            &mut slots,
            o::EQUIP_CTX_SLOT_STRIDE + o::EQUIP_CTX_SLOT_KIT_ID,
            -1,
        );
        write_i32(
            &mut slots,
            o::EQUIP_CTX_SLOT_STRIDE + o::EQUIP_CTX_SLOT_ITEM_ID,
            1205,
        );
        write_i32(
            &mut slots,
            o::EQUIP_CTX_SLOT_STRIDE * 2 + o::EQUIP_CTX_SLOT_KIT_ID,
            -1,
        );
        write_i32(
            &mut slots,
            o::EQUIP_CTX_SLOT_STRIDE * 2 + o::EQUIP_CTX_SLOT_ITEM_ID,
            -1,
        );

        write_ptr(&mut equip_ctx, o::EQUIP_CTX_SLOT_COUNT, 3);
        write_ptr(
            &mut equip_ctx,
            o::EQUIP_CTX_SLOT_PAIRS,
            slots.as_ptr() as usize,
        );
        write_ptr(
            &mut appearance,
            o::APPEARANCE_EQUIP_CONTEXT,
            equip_ctx.as_ptr() as usize,
        );

        let lp = LocalPlayer {
            current_appearance: Some(appearance.as_ptr() as usize),
            ..Default::default()
        };
        let mut snap = Snapshot::default();
        read_local_appearance(&lp, &mut snap);

        assert!(snap.appearance_readable);
        assert_eq!(snap.appearance.len(), 3);
        assert_eq!(snap.appearance[0].kit_id, 8);
        assert_eq!(snap.appearance[0].item_id, -1);
        assert_eq!(snap.appearance[1].kit_id, -1);
        assert_eq!(snap.appearance[1].item_id, 1205);
        assert_eq!(snap.appearance[2].kit_id, -1);
        assert_eq!(snap.appearance[2].item_id, -1);
    }

    #[test]
    fn local_appearance_omits_unbounded_slot_count() {
        let mut appearance = vec![0u8; o::APPEARANCE_EQUIP_CONTEXT + 8];
        let mut equip_ctx = vec![0u8; o::EQUIP_CTX_SLOT_COUNT + 8];
        write_ptr(&mut equip_ctx, o::EQUIP_CTX_SLOT_COUNT, 65);
        write_ptr(
            &mut appearance,
            o::APPEARANCE_EQUIP_CONTEXT,
            equip_ctx.as_ptr() as usize,
        );

        let lp = LocalPlayer {
            current_appearance: Some(appearance.as_ptr() as usize),
            ..Default::default()
        };
        let mut snap = Snapshot::default();
        read_local_appearance(&lp, &mut snap);

        assert!(!snap.appearance_readable);
        assert!(snap.appearance.is_empty());
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

    fn write_i32(buf: &mut [u8], off: usize, value: i32) {
        buf[off..off + 4].copy_from_slice(&value.to_le_bytes());
    }

    fn write_i64(buf: &mut [u8], off: usize, value: i64) {
        buf[off..off + 8].copy_from_slice(&value.to_le_bytes());
    }

    fn write_f32(buf: &mut [u8], off: usize, value: f32) {
        write_i32(buf, off, value.to_bits() as i32);
    }

    fn write_ptr(buf: &mut [u8], off: usize, value: usize) {
        buf[off..off + 8].copy_from_slice(&value.to_le_bytes());
    }

    fn write_u8(buf: &mut [u8], off: usize, value: u8) {
        buf[off] = value;
    }

    fn slot_word(item: i32, count: u32) -> u64 {
        (item as u32 as u64) | ((count as u64) << 32)
    }

    fn varc_node() -> Vec<u8> {
        vec![0u8; o::VARC_NODE_VALUE_KIND + 4]
    }

    fn write_varc_record_key(buf: &mut [u8], kind: i32, id: i32, value_kind: i32) -> usize {
        let record = o::VARC_NODE_RECORD_PAYLOAD;
        write_i32(buf, record + o::VARC_RECORD_KIND, kind);
        write_i32(buf, record + o::VARC_RECORD_VAR_ID, id);
        write_i32(buf, record + o::VARC_RECORD_VALUE_KIND, value_kind);
        record
    }

    fn read_staged_varcs(root: usize, count: usize) -> Snapshot {
        let mut client = vec![0u8; o::VARC_DOMAIN + 8];
        let mut domain = vec![0u8; o::VARC_RECORD_TREE_COUNT + 8];
        write_ptr(&mut domain, o::VARC_RECORD_TREE_ROOT, root);
        write_ptr(&mut domain, o::VARC_RECORD_TREE_COUNT, count);
        write_ptr(&mut client, o::VARC_DOMAIN, domain.as_ptr() as usize);

        let mut snap = Snapshot::default();
        read_varcs(client.as_ptr() as usize, &mut snap);
        snap
    }

    fn read_staged_inventories(entries: &mut [u8]) -> Snapshot {
        let mut client = vec![0u8; o::ITEM_CONTAINER_STORE + 8];
        let mut store = vec![0u8; o::ITEM_CONTAINER_ENTRY_CAP + 8];
        let begin = entries.as_ptr() as usize;
        let end = begin + entries.len();
        write_ptr(&mut store, o::ITEM_CONTAINER_ENTRY_BEGIN, begin);
        write_ptr(&mut store, o::ITEM_CONTAINER_ENTRY_END, end);
        write_ptr(&mut store, o::ITEM_CONTAINER_ENTRY_CAP, end);
        write_ptr(
            &mut client,
            o::ITEM_CONTAINER_STORE,
            store.as_ptr() as usize,
        );

        let mut snap = Snapshot::default();
        read_inventories(client.as_ptr() as usize, &mut snap);
        snap
    }

    fn write_inventory_entry(entry: &mut [u8], key: i32, slots: &[u64]) {
        write_i32(entry, o::ITEM_CONTAINER_ENTRY_KEY, key);
        let container = o::ITEM_CONTAINER_ENTRY_PAYLOAD;
        write_i32(entry, container + o::ITEM_CONTAINER_INVENTORY_ID, key >> 1);
        let begin = slots.as_ptr() as usize;
        let end = begin + slots.len() * o::ITEM_CONTAINER_SLOT_STRIDE;
        write_ptr(entry, container + o::ITEM_CONTAINER_SLOT_BEGIN, begin);
        write_ptr(entry, container + o::ITEM_CONTAINER_SLOT_END, end);
        write_ptr(entry, container + o::ITEM_CONTAINER_SLOT_CAP, end);
    }

    fn write_entity_tile(entity: &mut [u8], graph: &mut [u8], x: i32, y: i32, plane: i32) {
        write_ptr(entity, o::ENTITY_GRAPH_NODE, graph.as_ptr() as usize);
        write_i32(entity, o::ENTITY_PLANE, plane);
        write_i32(entity, o::ENTITY_SIZE, 1);
        let scene_x = 256.0 + ((1 << 8) as f32) + 512.0 * x as f32;
        let scene_y = 256.0 + ((1 << 8) as f32) + 512.0 * y as f32;
        write_f32(graph, o::GRAPH_SCENE_X_FINE, scene_x);
        write_f32(graph, o::GRAPH_SCENE_Y_FINE, scene_y);
    }

    #[test]
    fn scene_player_reader_scans_full_player_manager_slot_array() {
        let mut client = vec![0u8; o::PLAYER_MANAGER + 8];
        let mut pm = vec![0u8; o::PM_PLAYER_LIST + 8];
        let mut slots = vec![0u8; o::PM_PLAYER_LIST_CAPACITY * 8];
        let mut slot_rec = vec![0u8; o::NODE_ENTITY + 8];
        let mut entity = vec![0u8; o::ENTITY_SIZE + 4];
        let mut graph = vec![0u8; o::GRAPH_SCENE_Y_FINE + 4];

        write_entity_tile(&mut entity, &mut graph, 3222, 3222, 0);
        write_ptr(&mut slot_rec, o::NODE_ENTITY, entity.as_ptr() as usize);
        write_ptr(&mut slots, 1302 * 8, slot_rec.as_ptr() as usize);
        write_ptr(&mut pm, o::PM_PLAYER_LIST, slots.as_ptr() as usize);
        write_ptr(&mut client, o::PLAYER_MANAGER, pm.as_ptr() as usize);

        let mut snap = Snapshot::default();
        read_scene_players(client.as_ptr() as usize, &mut snap);

        assert!(snap.players_readable);
        assert_eq!(snap.players.len(), 1);
        assert_eq!(snap.players[0].idx, 1302);
        assert_eq!(snap.players[0].x, 3222);
        assert_eq!(snap.players[0].y, 3222);
        assert_eq!(snap.players[0].plane, 0);
    }

    #[test]
    fn scene_npc_reader_uses_active_indices_hash_lookup_and_type_id() {
        let mut client = vec![0u8; o::NPC_MANAGER + 8];
        let mut npc_mgr = vec![0u8; o::NPC_ACTIVE_COUNT + 4];
        let mut buckets = vec![0u8; 4 * 8];
        let mut node = vec![0u8; o::NPC_NODE_NEXT + 8];
        let mut entity = vec![0u8; o::NPC_ENTITY_TYPE_ID + 4];
        let mut graph = vec![0u8; o::GRAPH_SCENE_Y_FINE + 4];
        let npc_index = 11684i32;

        write_entity_tile(&mut entity, &mut graph, 3219, 3218, 0);
        write_i32(&mut entity, o::NPC_ENTITY_TYPE_ID, 11);
        write_i32(&mut node, o::NPC_NODE_KEY, npc_index);
        write_ptr(&mut node, o::NPC_NODE_ENTITY, entity.as_ptr() as usize);
        write_ptr(
            &mut buckets,
            (npc_index as usize % 4) * 8,
            node.as_ptr() as usize,
        );
        write_ptr(&mut npc_mgr, o::NPC_BUCKETS, buckets.as_ptr() as usize);
        write_ptr(&mut npc_mgr, o::NPC_BUCKET_COUNT, 4);
        write_i32(&mut npc_mgr, o::NPC_ACTIVE_INDICES, npc_index);
        write_i32(&mut npc_mgr, o::NPC_ACTIVE_COUNT, 1);
        write_ptr(&mut client, o::NPC_MANAGER, npc_mgr.as_ptr() as usize);

        let mut snap = Snapshot::default();
        read_scene_npcs(client.as_ptr() as usize, &mut snap);

        assert!(snap.npcs_readable);
        assert_eq!(snap.npcs.len(), 1);
        assert_eq!(snap.npcs[0].idx, 11684);
        assert_eq!(snap.npcs[0].type_id, 11);
        assert_eq!(snap.npcs[0].x, 3219);
        assert_eq!(snap.npcs[0].y, 3218);
        assert_eq!(snap.npcs[0].plane, 0);
    }

    #[test]
    fn item_container_store_reads_occupied_slots_with_raw_key_and_domain() {
        let slots_795 = vec![slot_word(52555, 1000)];
        let slots_93 = vec![slot_word(315, 1)];
        let slots_94 = vec![
            slot_word(-1, 0),
            slot_word(-1, 0),
            slot_word(-1, 0),
            slot_word(1205, 1),
        ];

        let mut entries = vec![0u8; o::ITEM_CONTAINER_ENTRY_STRIDE * 3];
        write_inventory_entry(
            &mut entries[0..o::ITEM_CONTAINER_ENTRY_STRIDE],
            1590,
            &slots_795,
        );
        write_inventory_entry(
            &mut entries[o::ITEM_CONTAINER_ENTRY_STRIDE..o::ITEM_CONTAINER_ENTRY_STRIDE * 2],
            186,
            &slots_93,
        );
        write_inventory_entry(
            &mut entries[o::ITEM_CONTAINER_ENTRY_STRIDE * 2..o::ITEM_CONTAINER_ENTRY_STRIDE * 3],
            188,
            &slots_94,
        );

        let snap = read_staged_inventories(&mut entries);

        assert!(snap.inventories_readable);
        assert_eq!(snap.inventories.len(), 3);
        assert_eq!(snap.inventories[0].key, 1590);
        assert_eq!(snap.inventories[0].inv_id, 795);
        assert_eq!(snap.inventories[0].domain_bit, 0);
        assert_eq!(snap.inventories[0].slots.len(), 1);
        assert_eq!(snap.inventories[0].slots[0].slot, 0);
        assert_eq!(snap.inventories[0].slots[0].item, 52555);
        assert_eq!(snap.inventories[0].slots[0].count, 1000);

        assert_eq!(snap.inventories[1].key, 186);
        assert_eq!(snap.inventories[1].inv_id, 93);
        assert_eq!(snap.inventories[1].slots[0].slot, 0);
        assert_eq!(snap.inventories[1].slots[0].item, 315);
        assert_eq!(snap.inventories[1].slots[0].count, 1);

        assert_eq!(snap.inventories[2].key, 188);
        assert_eq!(snap.inventories[2].inv_id, 94);
        assert_eq!(snap.inventories[2].slots.len(), 1);
        assert_eq!(snap.inventories[2].slots[0].slot, 3);
        assert_eq!(snap.inventories[2].slots[0].item, 1205);
        assert_eq!(snap.inventories[2].slots[0].count, 1);
    }

    #[test]
    fn item_container_store_emits_domain_bit_and_omits_empty_containers() {
        let slots_empty = vec![slot_word(-1, 0), slot_word(-1, 0)];
        let slots_domain = vec![slot_word(100, 2)];
        let mut entries = vec![0u8; o::ITEM_CONTAINER_ENTRY_STRIDE * 2];
        write_inventory_entry(
            &mut entries[0..o::ITEM_CONTAINER_ENTRY_STRIDE],
            10,
            &slots_empty,
        );
        write_inventory_entry(
            &mut entries[o::ITEM_CONTAINER_ENTRY_STRIDE..o::ITEM_CONTAINER_ENTRY_STRIDE * 2],
            11,
            &slots_domain,
        );

        let snap = read_staged_inventories(&mut entries);

        assert!(snap.inventories_readable);
        assert_eq!(snap.inventories.len(), 1);
        assert_eq!(snap.inventories[0].key, 11);
        assert_eq!(snap.inventories[0].inv_id, 5);
        assert_eq!(snap.inventories[0].domain_bit, 1);
        assert_eq!(snap.inventories[0].slots[0].slot, 0);
        assert_eq!(snap.inventories[0].slots[0].item, 100);
        assert_eq!(snap.inventories[0].slots[0].count, 2);
    }

    #[test]
    fn varc_tree_reads_numeric_long_and_inline_string_records() {
        let mut root = varc_node();
        let mut left = varc_node();
        let mut right = varc_node();

        write_ptr(&mut root, o::VARC_NODE_LEFT, left.as_ptr() as usize);
        write_ptr(&mut root, o::VARC_NODE_RIGHT, right.as_ptr() as usize);
        let root_record = write_varc_record_key(&mut root, 1, 20, 0);
        write_i32(&mut root, root_record + o::VARC_RECORD_VALUE, -42);

        write_ptr(&mut left, o::VARC_NODE_PARENT, root.as_ptr() as usize);
        let left_record = write_varc_record_key(&mut left, 2, 20, 2);
        left[left_record + o::VARC_RECORD_VALUE..left_record + o::VARC_RECORD_VALUE + 5]
            .copy_from_slice(b"hello");
        write_u8(
            &mut left,
            left_record + o::VARC_RECORD_STRING_INLINE_TAG,
            0x17 - 5,
        );

        write_ptr(&mut right, o::VARC_NODE_PARENT, root.as_ptr() as usize);
        let right_record = write_varc_record_key(&mut right, 1, 30, 1);
        write_i64(
            &mut right,
            right_record + o::VARC_RECORD_VALUE,
            0x1122_3344_5566_7788,
        );

        let snap = read_staged_varcs(root.as_ptr() as usize, 3);

        assert!(snap.varcs_readable);
        assert_eq!(snap.varcs.len(), 2);
        assert_eq!(snap.varcs[0].kind, 1);
        assert_eq!(snap.varcs[0].id, 20);
        assert_eq!(snap.varcs[0].value_kind, 0);
        assert_eq!(snap.varcs[0].val, -42);
        assert_eq!(snap.varcs[1].kind, 1);
        assert_eq!(snap.varcs[1].id, 30);
        assert_eq!(snap.varcs[1].value_kind, 1);
        assert_eq!(snap.varcs[1].val, 0x1122_3344_5566_7788);
        assert_eq!(snap.varcstrings.len(), 1);
        assert_eq!(snap.varcstrings[0].kind, 2);
        assert_eq!(snap.varcstrings[0].id, 20);
        assert_eq!(snap.varcstrings[0].value_kind, 2);
        assert_eq!(snap.varcstrings[0].str_val, "hello");
    }

    #[test]
    fn varc_heap_string_uses_pointer_length_and_heap_tag() {
        let heap = b"abcdefghijklmnopqrstuvwxyz".to_vec();
        let mut node = varc_node();
        let record = write_varc_record_key(&mut node, 2, 100, 2);
        write_ptr(
            &mut node,
            record + o::VARC_RECORD_VALUE,
            heap.as_ptr() as usize,
        );
        write_ptr(&mut node, record + o::VARC_RECORD_STRING_LENGTH, heap.len());
        write_ptr(
            &mut node,
            record + o::VARC_RECORD_STRING_CAP_TAG,
            (1usize << (usize::BITS as usize - 1)) | heap.len(),
        );

        let snap = read_staged_varcs(node.as_ptr() as usize, 1);

        assert_eq!(snap.varcstrings.len(), 1);
        assert_eq!(snap.varcstrings[0].kind, 2);
        assert_eq!(snap.varcstrings[0].id, 100);
        assert_eq!(snap.varcstrings[0].str_val, "abcdefghijklmnopqrstuvwxyz");
    }

    #[test]
    fn varc_tree_cycle_guard_emits_self_linked_node_once() {
        let mut node = varc_node();
        let ptr = node.as_ptr() as usize;
        write_ptr(&mut node, o::VARC_NODE_LEFT, ptr);
        write_ptr(&mut node, o::VARC_NODE_RIGHT, ptr);
        write_ptr(&mut node, o::VARC_NODE_PARENT, ptr);
        let record = write_varc_record_key(&mut node, 1, 7, 0);
        write_i32(&mut node, record + o::VARC_RECORD_VALUE, 99);

        let snap = read_staged_varcs(ptr, 64);

        assert!(snap.varcs_readable);
        assert_eq!(snap.varcs.len(), 1);
        assert_eq!(snap.varcs[0].kind, 1);
        assert_eq!(snap.varcs[0].id, 7);
        assert_eq!(snap.varcs[0].val, 99);
    }

    #[test]
    fn varc_active_tree_payloads_are_read_and_non_varc_record_kinds_filtered() {
        let mut client = vec![0u8; o::VARC_DOMAIN + 8];
        let mut domain = vec![0u8; o::VARC_RECORD_TREE_COUNT + 8];
        let mut active_root = varc_node();
        let mut active_left = varc_node();
        let mut numeric_record = vec![0u8; o::VARC_RECORD_VALUE_KIND + 4];
        let mut string_record = vec![0u8; o::VARC_RECORD_VALUE_KIND + 4];
        let mut ignored_node = varc_node();

        write_ptr(
            &mut active_root,
            o::VARC_NODE_LEFT,
            active_left.as_ptr() as usize,
        );
        write_ptr(
            &mut active_root,
            o::VARC_ACTIVE_NODE_RECORD,
            numeric_record.as_ptr() as usize,
        );
        write_ptr(
            &mut active_left,
            o::VARC_ACTIVE_NODE_RECORD,
            string_record.as_ptr() as usize,
        );

        write_i32(&mut numeric_record, o::VARC_RECORD_KIND, 1);
        write_i32(&mut numeric_record, o::VARC_RECORD_VAR_ID, 4969);
        write_i32(&mut numeric_record, o::VARC_RECORD_VALUE_KIND, 0);
        write_i32(&mut numeric_record, o::VARC_RECORD_VALUE, 17);

        write_i32(&mut string_record, o::VARC_RECORD_KIND, 2);
        write_i32(&mut string_record, o::VARC_RECORD_VAR_ID, 147);
        write_i32(&mut string_record, o::VARC_RECORD_VALUE_KIND, 2);
        string_record[o::VARC_RECORD_VALUE..o::VARC_RECORD_VALUE + 3].copy_from_slice(b"hey");
        write_u8(
            &mut string_record,
            o::VARC_RECORD_STRING_INLINE_TAG,
            0x17 - 3,
        );

        let ignored_record = write_varc_record_key(&mut ignored_node, 7, 96797272, 0);
        write_i32(&mut ignored_node, ignored_record + o::VARC_RECORD_VALUE, 1);

        write_ptr(
            &mut domain,
            o::VARC_ACTIVE_TREE_ROOT,
            active_root.as_ptr() as usize,
        );
        write_ptr(&mut domain, o::VARC_ACTIVE_TREE_COUNT, 2);
        write_ptr(
            &mut domain,
            o::VARC_RECORD_TREE_ROOT,
            ignored_node.as_ptr() as usize,
        );
        write_ptr(&mut domain, o::VARC_RECORD_TREE_COUNT, 1);
        write_ptr(&mut client, o::VARC_DOMAIN, domain.as_ptr() as usize);

        let mut snap = Snapshot::default();
        read_varcs(client.as_ptr() as usize, &mut snap);

        assert!(snap.varcs_readable);
        assert_eq!(snap.varcs.len(), 1);
        assert_eq!(snap.varcs[0].kind, 1);
        assert_eq!(snap.varcs[0].id, 4969);
        assert_eq!(snap.varcs[0].val, 17);
        assert_eq!(snap.varcstrings.len(), 1);
        assert_eq!(snap.varcstrings[0].kind, 2);
        assert_eq!(snap.varcstrings[0].id, 147);
        assert_eq!(snap.varcstrings[0].str_val, "hey");
    }
}
