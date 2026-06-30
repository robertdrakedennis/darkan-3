//! macOS rs2client struct field offsets, capture-point signatures and the
//! game-state enum — the recorder dylib's struct/offset source of truth.
//!
//! SOURCE OF TRUTH FOR STRUCT / DERIVED OFFSETS: the migrator-generated file
//! `re-resources/updater/recorder/offsets_<rev>.rs`, produced by
//! `RS3RecorderUpdaterMac` (the mac-native offset migrator) and `include!`d here
//! verbatim. We do NOT hand-maintain offset VALUES any more — per-rev the flow is
//! simply: regenerate `offsets_<rev>.rs` with the migrator, point the active rev
//! at it (see `build.rs` / `DARKAN_RECORDER_OFFSET_REV`), rebuild.
//!
//! The generated file is `include!`d into the private `gen` module below and then
//! re-exported so every consumer call-site is UNCHANGED
//! (`offsets::server_connection::CURRENT_OPCODE`, `offsets::client::MAIN_STATE`,
//! `offsets::client_oracle as o`, …). This module additionally owns the things
//! the migrator does NOT generate:
//!   * the capture-point byte SIGNATURES (`SIG_*` — runtime sig-scan stays here);
//!   * the `Client::SetMainState` game-state enum + `state_name`;
//!   * two `connection_manager` fields the mac anchor registry does not yet own
//!     (`OWNER_CLIENT`, `TINYKEY`) — see the `connection_manager` shim below.
//!
//! These offsets are deliberately NOT shared with the engine's `Offsets.kt`
//! (which holds the *Linux* values; the mac Client offsets differ — `MAIN_STATE`
//! and `CONNECTION_MANAGER`). Documentation source of truth:
//!   - re-resources/docs/binary/patch-targets-macos-recorder.md
//!   - re-resources/docs/net/recorder-capture-points.md
//!
//! Every function is resolved at ctor time by sig-scanning the loaded Mach-O
//! image (see `sig.rs`) — we never hardcode a runtime address. The `file`
//! offsets in the `SIG_*` patterns are kept only as a post-scan sanity check
//! (`expected_file`) and for log messages; the load-bearing data are the byte
//! `SIG` patterns.

#![allow(dead_code)]

use crate::sig::Pattern;

// ---------------------------------------------------------------------------
// Generated struct/derived offsets (the migrator artifact) — the SOURCE OF
// TRUTH for every struct field / derived offset.
//
// `build.rs` selects the active revision (default `948-5`, overridable via the
// `DARKAN_RECORDER_OFFSET_REV` build-time env var) and writes a one-line shim
// into `OUT_DIR` that `include!`s the canonical
// `re-resources/updater/recorder/offsets_<rev>.rs`. Switching revs is therefore
// a one-liner: regenerate that file for the new rev and bump the default in
// `build.rs` (or set the env var) — no offset VALUE is ever edited by hand here.
//
// The generated file declares `pub mod client { ... }`,
// `pub mod server_connection { ... }`, etc.; including it inside this private
// `gen` module namespaces those, so we can re-export them under the public names
// the consumers import while supplementing the one module that needs extra
// (registry-ungenerated) fields.
// ---------------------------------------------------------------------------
#[allow(dead_code)]
mod gen {
    include!(concat!(env!("OUT_DIR"), "/offset_include.rs"));
}

// Re-export every generated module unchanged so existing call-sites resolve
// exactly as before (e.g. `crate::offsets::server_connection::CURRENT_OPCODE`).
// These are the public offset surface; a given module may only be referenced
// from `#[cfg(test)]` code (e.g. `isaac`), so allow unused re-exports.
#[allow(unused_imports)]
pub use gen::{
    client, client_stream, isaac, login_state_machine, packet, queue_node, server_connection,
};

// ---------------------------------------------------------------------------
// Client-state oracle fields.
//
// The generated `gen::client_oracle` owns the currently migrated varp/player/stat
// fields. The VARC record-tree anchors are already present in
// `re-resources/updater/anchor_registry_mac.json`, but the migrator artifact does
// not yet emit them, so expose them here under the same public module until the
// generated `offsets_<rev>.rs` catches up.
pub mod client_oracle {
    pub use super::gen::client_oracle::*;

    /// Live client cycle counter / "baseTick" (`Client + 0x518`, int) — the
    /// per-frame render tick. VERIFIED rs2client 948-5: read at the top-level loop
    /// call site `0x10027d060` (`MOV ESI,[RBX+0x518]`) and passed as the tick arg
    /// into `AdvanceRenderPosition`; doc `player-appearance-948.md` §"Tick
    /// reference". The anim-trace poller reads it off the client base each sample so
    /// the avatar's lerp window (`+0xDBC`/`+0xDC0`) is interpretable against it.
    pub const BASE_TICK: usize = 0x518;

    /// `Client + 0x196D8` is a pointer to the client-var domain (`vt-varc`).
    pub const VARC_DOMAIN: usize = 0x196D8;
    pub const VARC_RECORD_TREE_HEADER: usize = 0x1E120;
    pub const VARC_RECORD_TREE_ROOT: usize = 0x1E130;
    pub const VARC_RECORD_TREE_COUNT: usize = 0x1E140;
    pub const VARC_ACTIVE_TREE_HEADER: usize = 0x20;
    pub const VARC_ACTIVE_TREE_ROOT: usize = 0x30;
    pub const VARC_ACTIVE_TREE_COUNT: usize = 0x40;
    pub const VARC_NODE_LEFT: usize = 0x00;
    pub const VARC_NODE_RIGHT: usize = 0x08;
    pub const VARC_NODE_PARENT: usize = 0x10;
    pub const VARC_NODE_COLOR: usize = 0x18;
    pub const VARC_NODE_RECORD_KIND: usize = 0x20;
    pub const VARC_NODE_VAR_ID: usize = 0x24;
    pub const VARC_NODE_RECORD_PAYLOAD: usize = 0x28;
    pub const VARC_ACTIVE_NODE_RECORD: usize = 0x20;
    pub const VARC_RECORD_KIND: usize = 0x00;
    pub const VARC_RECORD_VAR_ID: usize = 0x04;
    pub const VARC_RECORD_VALUE: usize = 0x20;
    pub const VARC_RECORD_VALUE_KIND: usize = 0x38;
    pub const VARC_RECORD_STRING_INLINE_TAG: usize = 0x37;
    pub const VARC_RECORD_STRING_LENGTH: usize = 0x28;
    pub const VARC_RECORD_STRING_CAP_TAG: usize = 0x30;
    pub const VARC_NODE_VALUE: usize = 0x48;
    pub const VARC_NODE_VALUE_KIND: usize = 0x60;
    pub const VARC_STRING_INLINE_LIMIT: usize = 0x18;
    pub const VARC_STRING_INLINE_TAG: usize = 0x5F;
    pub const VARC_STRING_LENGTH: usize = 0x50;
    pub const VARC_STRING_CAP_TAG: usize = 0x58;

    /// `Client + 0x197D8` is a pointer to the persistent item-container store.
    pub const ITEM_CONTAINER_STORE: usize = 0x197D8;
    pub const ITEM_CONTAINER_ENTRY_BEGIN: usize = 0x08;
    pub const ITEM_CONTAINER_ENTRY_END: usize = 0x10;
    pub const ITEM_CONTAINER_ENTRY_CAP: usize = 0x18;
    pub const ITEM_CONTAINER_ENTRY_STRIDE: usize = 0x48;
    pub const ITEM_CONTAINER_ENTRY_KEY: usize = 0x00;
    pub const ITEM_CONTAINER_ENTRY_PAYLOAD: usize = 0x08;
    pub const ITEM_CONTAINER_INVENTORY_ID: usize = 0x08;
    pub const ITEM_CONTAINER_SLOT_BEGIN: usize = 0x10;
    pub const ITEM_CONTAINER_SLOT_END: usize = 0x18;
    pub const ITEM_CONTAINER_SLOT_CAP: usize = 0x20;
    pub const ITEM_CONTAINER_SLOT_STRIDE: usize = 0x08;
    pub const ITEM_CONTAINER_SLOT_ITEM_ID: usize = 0x00;
    pub const ITEM_CONTAINER_SLOT_QUANTITY: usize = 0x04;
    pub const ITEM_CONTAINER_DIRTY_MANAGER: usize = 0x197A8;

    /// Scene-entity oracle STAGE 1 (players/NPCs; positions only).
    pub const NPC_MANAGER: usize = 0x19740;
    pub const PM_PLAYER_LIST_CAPACITY: usize = 0x800;
    pub const PM_RENDER_LIST_BEGIN: usize = 0x4038;
    pub const PM_RENDER_LIST_END: usize = 0x4040;
    pub const PM_RENDER_LIST_CAP: usize = 0x4048;
    pub const PM_PENDING_LIST_BEGIN: usize = 0x6060;
    pub const PM_PENDING_LIST_END: usize = 0x6068;
    pub const PM_PENDING_LIST_CAP: usize = 0x6070;
    pub const PM_EXTINFO_LIST_BEGIN: usize = 0xA0D0;
    pub const PM_EXTINFO_LIST_END: usize = 0xA0D8;
    pub const PM_EXTINFO_LIST_CAP: usize = 0xA0E0;
    pub const NPC_BUCKETS: usize = 0x10;
    pub const NPC_BUCKET_COUNT: usize = 0x18;
    pub const NPC_MAP_COUNT: usize = 0x20;
    pub const NPC_ACTIVE_INDICES: usize = 0xA0A0;
    pub const NPC_ACTIVE_COUNT: usize = 0xB0A0;
    pub const NPC_COORD_BITS: usize = 0xC0E8;
    pub const NPC_EXTINFO_LIST_BEGIN: usize = 0xC0F0;
    pub const NPC_EXTINFO_LIST_END: usize = 0xC0F8;
    pub const NPC_EXTINFO_LIST_CAP: usize = 0xC100;
    pub const NPC_NODE_KEY: usize = 0x00;
    pub const NPC_NODE_ENTITY: usize = 0x10;
    pub const NPC_NODE_NEXT: usize = 0x18;
    pub const NPC_ENTITY_TYPE_ID: usize = 0x1060;

    /// Local-avatar appearance oracle STAGE 2 (committed appearance identity).
    pub const AVATAR_APPEARANCE_PENDING: usize = 0x1298;
    pub const AVATAR_APPEARANCE_APPLIED: usize = 0x12A0;
    pub const AVATAR_APPEARANCE_NEXT: usize = 0x12A8;
    pub const APPEARANCE_EQUIP_CONTEXT: usize = 0x98;
    pub const EQUIP_CTX_SLOT_COUNT: usize = 0x30;
    pub const EQUIP_CTX_SLOT_PAIRS: usize = 0x38;
    pub const EQUIP_CTX_SLOT_STRIDE: usize = 0x08;
    pub const EQUIP_CTX_SLOT_KIT_ID: usize = 0x00;
    pub const EQUIP_CTX_SLOT_ITEM_ID: usize = 0x04;
}

// ---------------------------------------------------------------------------
// ConnectionManager field offsets.
//
// The generated `gen::connection_manager` supplies GAME_CONNECTION (0x18),
// LOGIN_CONNECTION (0x28), the GAME/LOGIN msg-ctx offsets, and MSG_CTX_CONN
// (0x08). It does NOT yet supply `OWNER_CLIENT` / `TINYKEY`: those two are real,
// documented mac offsets but are not present in `re-resources/updater/
// anchor_registry_mac.json`, so the migrator cannot emit them today. We re-export
// the generated fields and ADD those two here (NOT a re-derivation of a generated
// value — they are a documented registry gap). See the spawned migrator task /
// the note in this crate's README for closing the gap so this shim can drop them.
pub mod connection_manager {
    pub use super::gen::connection_manager::*;

    /// `*(ConnMgr+8) == Client*` (TcpIn reads MAIN_STATE off it). Consumed by the
    /// ReadPacket/OpenLoginStream client-base publication. Not in the mac anchor
    /// registry yet → supplemented here until the migrator owns it.
    pub const OWNER_CLIENT: usize = 0x08;
    /// Overlaps the game msg-ctx; XTEA tinyKey for special senders. Documented but
    /// not currently consumed by the dylib; supplemented for completeness.
    pub const TINYKEY: usize = 0x10;
}

// ---------------------------------------------------------------------------
// ServerProt opcode table (`g_serverProtTable`) — for the live prot-table dump.
//
// These are the table's IMAGE-RELATIVE data-global offset + the ProtEntry struct
// layout, used by `prot_table.rs` to read the client's OWN opcode table at
// session start (so framing/naming are rev-agnostic — sourced from the client's
// table, not a static one). VERIFIED against the mac binary (Ghidra DB,
// rs2client 948-5):
//   * `jag::ServerProt::RegisterProt` (vmaddr 0x1004155e0): builds each ProtEntry
//     (`*entry=opcode; entry[1]=sizeClass; entry+0x08=name; entry+0x30=0`) and
//     pushes `entry*` into the table eastl::vector at `DAT_100f0eaf0`.
//   * `jag::ConnectionManager::ReadPacket` (vmaddr 0x10006c920): looks up
//     `entry = *(ProtEntry**)(g_serverProtTable + opcode*8)`, reads
//     `sizeClass = entry[1]`, and gates `opcode < 0xDA` (so valid opcodes are
//     0..=0xD9). The handler dispatched at 0x10006d8ea is
//     `*(*(entry + 0x30)) + 0x30` (handler-category object → its vtable → +0x30),
//     cross-checked by RE §10.4 (run-energy op 0x0d handler 0x1000448a0).
//
// NOTE — registry gap (same status as the `SIG_*` patterns and the
// `connection_manager` shim fields): the table base is NOT yet emitted by the mac
// offset migrator. It is image-relative (a `DAT_` data global, not a struct
// field), so the migrator would capture it via the `call_target`/RIP-relative
// data-ref DSL rather than the struct-offset recipes. Until an anchor for it is
// added to `re-resources/updater/anchor_registry_mac.json` (capture the RIP-rel
// `mov`/`lea` to `g_serverProtTable` inside `ReadPacket` or `RegisterProt`), it is
// hand-owned here. See the README "Known limitations".
pub mod server_prot {
    /// Image-relative offset (`vmaddr - 0x100000000`) of the `g_serverProtTable`
    /// data global. The global itself is a POINTER to the start of the array of
    /// `ProtEntry*` (an eastl::vector: begin here, end at +0x08, cap at +0x10).
    /// Runtime address = `image_base + slide + TABLE_GLOBAL`; read the pointer
    /// THERE to get the array base, then index `[opcode * ENTRY_PTR_STRIDE]`.
    pub const TABLE_GLOBAL: usize = 0xf0eaf0;
    /// Highest valid opcode. `ReadPacket` gates `opcode < 0xDA`, so the dump walks
    /// 0..=0xD9 inclusive (opcode 0xDA and above are rejected by the client).
    pub const MAX_OPCODE: usize = 0xD9;
    /// Stride between `ProtEntry*` slots in the table array (8 = one pointer).
    pub const ENTRY_PTR_STRIDE: usize = 8;
    /// `ProtEntry.opcode` (int) — should equal the index it was found at.
    pub const ENTRY_OPCODE: usize = 0x00;
    /// `ProtEntry.sizeClass` (int): -1 = 1-byte size prefix (varByte), -2 = 2-byte
    /// BE size prefix (varShort), >= 0 = fixed payload byte count.
    pub const ENTRY_SIZE_CLASS: usize = 0x04;
    /// `ProtEntry` field holding the handler-category object pointer (zeroed by
    /// `InitEntry`, populated by registration). The dispatched handler FUNCTION is
    /// `*(*(entry + ENTRY_HANDLER_CATEGORY)) + HANDLER_VTABLE_SLOT)`.
    pub const ENTRY_HANDLER_CATEGORY: usize = 0x30;
    /// Slot in the handler-category object's vtable that holds the handler
    /// function pointer (the `CALL [vtbl+0x30]` at the 0x6d8ea dispatch site).
    pub const HANDLER_VTABLE_SLOT: usize = 0x30;
}

// ---------------------------------------------------------------------------
// Local-player AVATAR (graphEntity / PathingEntity) render-state offsets.
//
// Read FROM the Ghidra DB this session (mac rs2client 948-5) — NOT yet emitted by
// the mac offset migrator and NOT in `anchor_registry_mac.json`, so (like the
// `server_prot` table base and the `connection_manager` shim fields) they are
// hand-owned here until an anchor is added. The avatar is the SAME object the
// oracle tile path reaches as `entity` (playerList[serverIdx].node + 0x38):
// `RefreshLocalVisibility @0x100033260` and `DecodeAppearance @0x100031480` both
// resolve the local avatar as `*(playerList[lip+0x48] + 0x38)` (or the explicit
// override `*(lip+0x58)`). Every offset below is a BYTE offset on that avatar.
//
// Source functions (renamed this session): RefreshLocalVisibility @0x100033260
// (visible-flag gate), AttachToMapSquare @0x10039c7c0 (map-square bind gate),
// AdvanceRenderPosition @0x1003a2500 + CommitRenderTransform @0x1003a2390 (render
// position), DecodeAppearance @0x100031480 (model/equipment/headbar writes).
// See re-resources/docs/net/recorder-capture-points.md §10.6 and the agent memory
// note `local-avatar-render-state-offsets.md`.
pub mod avatar {
    /// Explicit local-avatar override pointer on `LoggedInPlayer` (`lip + 0x58`).
    /// When non-zero, RefreshLocalVisibility/DecodeAppearance use it DIRECTLY as
    /// the local avatar (instead of resolving via the player-list slot). Dumped so
    /// we can see whether the local avatar is being chosen by override vs by slot.
    pub const LIP_LOCAL_OVERRIDE: usize = 0x58;

    /// VISIBLE flag (byte). RefreshLocalVisibility sets `*(avatar+0x1070)=1` ONLY
    /// when this avatar resolves as THE local player (its slot == LoggedInPlayer
    /// +0x48), else 0. THE ghidra-proven render-bind gate: 0 => the local avatar is
    /// not marked visible (won't be submitted as the local player).
    pub const VISIBLE_FLAG: usize = 0x1070;

    /// PathingEntity render-model pointer (OEntity.RENDER_MODEL). NULL => no model
    /// handle attached to the avatar (model not loaded/built).
    pub const RENDER_MODEL: usize = 0xC58;

    /// Live move-speed the per-frame animator reads (`avatar + 0x1F0`, **inline
    /// int32 — NOT a pointer**). `SelectMovementAnimation @0x1003a4e90` compares it
    /// against the threshold float at `+0x1EC` and branches on the run flag at
    /// `+0x1F8` to pick the walk/run vs idle BAS seq. This SUPERSEDES the engine
    /// `Offsets.kt` `OPathingEntity.LAST_MOVESPEED = 0x98` (a `UInt32*`), which is
    /// WRONG for this binary — `+0x98` derefs to a non-pointer on the live avatar
    /// and the resolved speed never appears. SOURCE OF TRUTH:
    /// `re-resources/docs/net/serverprot/player-appearance-948.md` §"Per-frame
    /// idle/walk/run selection". `Offsets.kt` needs a separate user-approved sync;
    /// do NOT `assert_eq!` this against `0x98`.
    pub const MOVE_SPEED: usize = 0x1F0;
    /// Move-speed threshold float (`avatar + 0x1EC`, int32 read as raw bits) the
    /// animator compares `MOVE_SPEED` against to decide walk vs run. Per the RE doc
    /// §"Per-frame idle/walk/run selection".
    pub const MOVE_SPEED_THRESHOLD: usize = 0x1EC;
    /// Run flag (`avatar + 0x1F8`, `== 1` ⇒ running) read by the animator alongside
    /// `MOVE_SPEED`. Per the RE doc §"Per-frame idle/walk/run selection".
    pub const RUN_FLAG: usize = 0x1F8;

    // --- Route-anim queue: the ACTUAL walk/run movement-seq driver (NOT the seq
    // override `ANIMATION_ID`, NOT the BAS-set `RENDER_ANIM_SET_ID`). The 4 movement
    // seq ids decoded from the PLAYER_INFO op22 ext-info block gated by mask bit 0x20
    // are PUSHED into this EASTL `int` vector by
    // `jag::graphics::GraphEntity::SetMovementAnimSet @0x1003a69f0` (vtable +0x1e0);
    // `AdvanceRenderPosition @0x1003a2500` starts the walk seq from the queue head when
    // it is non-empty and `*(avatar+0xad4) <= priority`. SOURCE OF TRUTH:
    // `re-resources/docs/net/serverprot/player-appearance-948.md` §"What ACTUALLY
    // animates a remote-player walk". Element type/stride VERIFIED by decompiling
    // `SetMovementAnimSet`: begin/end/cap are typed `int *`, the 4 ids are written as
    // `*begin / begin[1] / begin[2] / begin[3]`, and the live count is `(end-begin) >> 2`
    // — a **4-byte (int32) stride**, max 4 elements.
    /// Route-anim queue BEGIN (`avatar + 0x2c8`, == `AdvanceRenderPosition`
    /// `param_1[0x59]`). EASTL `int` vector holding the live walk/run movement seq ids.
    pub const ROUTE_ANIM_QUEUE_BEGIN: usize = 0x2c8;
    /// Route-anim queue END (`avatar + 0x2d0`, == `param_1[0x5a]`). `(END-BEGIN)>>2` =
    /// the number of queued movement seq ids (4-byte stride). `END == BEGIN` => empty
    /// (no movement anim active, e.g. after `AttachToMapSquare` clears it on a GPI step).
    pub const ROUTE_ANIM_QUEUE_END: usize = 0x2d0;
    /// Stride between route-anim queue elements (4 = one `int32` seq id). Verified from
    /// `SetMovementAnimSet`'s `int *` element accesses + the `>> 2` count math.
    pub const ROUTE_ANIM_QUEUE_STRIDE: usize = 4;
    /// Movement-anim PRIORITY (`avatar + 0xb64`, u32) — the max anim priority of the 4
    /// queued seqs, written by `SetMovementAnimSet`. `AdvanceRenderPosition`'s walk-start
    /// gate requires `*(avatar+0xad4) <= *(avatar+0xb64)` before kicking the walk seq.
    pub const ROUTE_ANIM_QUEUE_PRIORITY: usize = 0xb64;

    /// Current RENDER animation id on the PathingEntity/GraphEntity (`avatar + 0x958`,
    /// int32) — the player-side equivalent of the engine `ONPC.RENDER_ANIM = 0x958`
    /// (`NPC.renderAnim`). Read by `AdvanceRenderPosition @0x1003a2500` at `0x1003a2dda`
    /// (`MOV ECX,[RBX+0x958]; CMP ECX,[RBX+0xa88]`) as the live render-anim the avatar is
    /// playing. Distinct from the seq OVERRIDE at `ANIMATION_ID` (`0xA88`). NOT in the
    /// engine `Offsets.kt` for the PathingEntity (only `ONPC` has it) — see the flag in
    /// the handoff note.
    pub const RENDER_ANIM: usize = 0x958;

    /// Current animation id (OEntity.ANIMATION_ID), stored inline on the entity.
    /// NOTE: this is ALSO the anim controller's secondary/incoming seq slot
    /// (`controller+0x788`, where `controller == avatar+0x300`): `0x300 + 0x788 ==
    /// 0xA88`. `StartAnimSeq @0x100589f80` reads it as the incoming-seq guard. So
    /// the per-frame trace's `ctrl_secondary_seq` and this field are the SAME
    /// memory — a built-in cross-check.
    pub const ANIMATION_ID: usize = 0xA88;
    /// `avatar+0xAA0` (OEntity.ANIMATION_SHARED_PTR). Retained ONLY for the
    /// pre-existing (best-effort, RE-inconclusive) `state_snapshot` `animation_frame`
    /// read — NOT used by the anim-trace poller. CAUTION: the engine's
    /// `Entity.animation` shared_ptr chain would read the object pointer from
    /// `0xAA0+0x8 == 0xAA8`, but `0xAA8` is `MAP_SQUARE_BIND` on this PathingEntity
    /// layout (proven in `AdvanceRenderPosition`: `MOV RCX,[RBX+0xaa8]; … [RCX+0xa60]`
    /// = a map square), so that chain does NOT resolve an Animation object here. The
    /// real GraphNode playback-frame field is unverified; do not derive one from this.
    pub const ANIMATION_SHARED_PTR: usize = 0xAA0;
    /// OAnimation.CURRENT_FRAME (`+0x24`) — used only by the pre-existing snapshot
    /// `animation_frame` read off `*(avatar+0xAA0)` (see the caution above; this is
    /// NOT a verified per-frame counter on this layout).
    pub const ANIMATION_CURRENT_FRAME: usize = 0x24;

    // --- Animation controller (embedded sub-object at `avatar+0x300`). Verified
    // rs2client 948-5 via `jag::graphics::AnimController::StartAnimSeq @0x100589f80`
    // (called as `StartAnimSeq((long)(avatar+0x60 /*qword index*/), …)` ⇒ controller
    // base == `avatar + 0x60*8 == avatar+0x300`) and `BindSecondaryAnim @0x10058a370`
    // (RDI == `avatar+0x300`). Offsets below are ABSOLUTE on the avatar (controller
    // base + the controller-relative field). These distinguish "a seq is bound but
    // frozen on frame 0" from "the controller never bound a walk seq".
    /// Anim controller base (`avatar+0x300`). Not read directly; documents the base
    /// the absolute controller offsets below are derived from.
    pub const ANIM_CONTROLLER: usize = 0x300;
    /// Controller PRIMARY active seq id (`controller+0x818` ⇒ `avatar+0xB18`, int;
    /// `-1` = none). `StartAnimSeq` stores the new seq here (`MOV [RDI+0x818],seqId`)
    /// and early-outs when it already equals `seqId`. The authoritative "what seq is
    /// the controller playing".
    pub const ANIM_CONTROLLER_PRIMARY_SEQ: usize = 0xB18;
    /// Controller SECONDARY/incoming seq id (`controller+0x788` ⇒ `avatar+0xA88`,
    /// int) — the SAME memory as `ANIMATION_ID`. `StartAnimSeq` reads it as the
    /// incoming-seq guard (only when `+0x7a8` is non-null). Emitted so the trace can
    /// cross-check it against `anim_id`.
    pub const ANIM_CONTROLLER_SECONDARY_SEQ: usize = 0xA88;
    /// Controller BLEND ticks (`controller+0x860` ⇒ `avatar+0xB60`, int) — the
    /// crossfade duration `StartAnimSeq` applies (inherits the previous value when a
    /// 0 is passed). Non-zero during a walk→idle blend, the witness for a natural
    /// (blended) vs snapped transition.
    pub const ANIM_CONTROLLER_BLEND_TICKS: usize = 0xB60;

    /// Entity size (int; low byte is the tile footprint). OEntity.SIZE — same field
    /// the tile formula uses.
    pub const SIZE: usize = 0x184;

    /// The render scene-graph GraphNode the position integrator commits onto
    /// (`avatar + 0x8`, == `entity + 0x8`, the SAME node the tile path reads). Its
    /// scene-fine floats live at the standard GraphNode `+0xF0`(X)/`+0xF8`(Y), so
    /// the oracle "tile" is already the INTERPOLATED render position.
    pub const RENDER_GRAPH_NODE: usize = 0x8;
    /// Scene-bucket GraphNode (`avatar + 0x268`, `param_1[0x4d]`) inserted into the
    /// scene graph by AttachToMapSquare. NULL before the avatar is scene-attached.
    ///
    /// This SAME pointer is the `jag::graphics::RouteWaypointManager` (the route node
    /// does scene-bucket insertion via its vtable) — see the [`route`] submodule for
    /// the ring internals. The bare pointer is `scene_bucket_graph_node`; the ring
    /// fields below are the client-prediction witness.
    pub const SCENE_BUCKET_GRAPH_NODE: usize = 0x268;

    /// RouteWaypointManager ring internals — offsets RELATIVE to the heap object at
    /// `*(avatar + SCENE_BUCKET_GRAPH_NODE)` (NOT the avatar). RE: rs2client 948-5
    /// `jag::graphics::RouteWaypointManager` (AppendWaypoint `0x1003983f0`, RouteStep
    /// `0x100398920`, ctor `0x10039b700`). This is the client's OWN pathfind route; the
    /// per-frame route-step in `AdvanceRenderPosition` walks it ONLY in the drift path
    /// (server lerp window `LERP_END_TICK == -1`). So **`COUNT > 0` during a walk proves
    /// the local avatar is CLIENT-PREDICTED** — a server-forced GPI move (mvt=1/2/3) calls
    /// `AttachToMapSquare`, which EMPTIES the ring rather than filling it.
    pub mod route {
        /// Whole-struct size; gate the heap object mapped to here before reading.
        pub const STRUCT_EXTENT: usize = 0xC0;
        /// READ cursor (ptr) — the ring entry the route-step is currently consuming.
        pub const HEAD: usize = 0x20;
        /// WRITE cursor (ptr) — next append / one-past-last.
        pub const TAIL: usize = 0x28;
        /// Active waypoint COUNT (i32). **THE prediction signal:** `>0` = client routed
        /// a walk locally; `0` = idle (the client's own idle test, used at `0x100014f95`).
        pub const COUNT: usize = 0x30;

        /// Per-waypoint ring-entry stride (bytes).
        pub const WP_STRIDE: usize = 0x18;
        /// Entry plane/level (i32; `-1` = empty sentinel).
        pub const WP_PLANE: usize = 0x00;
        /// Entry destination X in FINE units — 256 fine = 1 tile (f32). Tile = `x >> 8`.
        pub const WP_X_FINE: usize = 0x04;
        /// Entry destination Z in FINE units (f32). Tile = `z >> 8`.
        pub const WP_Z_FINE: usize = 0x0C;
        /// Per-step move-mode descriptor (ptr) — compare (minus dyld slide) vs MODE_*.
        pub const WP_MOVE_MODE: usize = 0x10;

        // Move-mode descriptor LINK-TIME addresses (image base 0x100000000; the live
        // token = link-time + the main image's dyld slide). Dumped from the `__data`
        // holders `0x100ed2a90..ab0` (the stored value is the descriptor they point to).
        pub const MODE_IDLE: usize = 0x100f13a30;
        pub const MODE_CRAWL: usize = 0x100f13a34;
        pub const MODE_WALK: usize = 0x100f13a38;
        pub const MODE_RUN: usize = 0x100f13a3c;
        pub const MODE_SMOOTH: usize = 0x100f13a40;
    }

    /// Committed render position, ABSOLUTE FINE coords as doubles, written every
    /// frame by AdvanceRenderPosition (`param_1[0x55]`=X, `param_1[0x57]`=Y). This
    /// is what is actually drawn this frame (interpolated toward the target).
    pub const RENDER_POS_X_DOUBLE: usize = 0x2A8;
    pub const RENDER_POS_Y_DOUBLE: usize = 0x2B8;

    /// Previous GPI waypoint (X/Y, float, fine = tile*512) — `param_1[0x1b4/0x1b5]`.
    pub const PREV_WAYPOINT_X_FINE: usize = 0xDA0;
    pub const PREV_WAYPOINT_Y_FINE: usize = 0xDA8;
    /// TARGET GPI waypoint (X/Y, float, fine) — `param_1[0x1b6/0x1b7]`. The
    /// non-interpolated LOGICAL destination set by the GPI movement decode. Drift
    /// test: TARGET held while the render scene-fine creeps => render-interp drift;
    /// TARGET itself moving => the logical tile is moving. (X here is the +0xDB0
    /// horizontal axis and "Y" is the +0xDB8 scene-Z map axis — the same horizontal
    /// pair the GraphNode scene-fine uses at +0xF0/+0xF8. The middle +0xDB4 float is
    /// the ELEVATION; the full {x,elev,z} triples are TARGET_FINE_*/PREV_FINE_* below.)
    pub const TARGET_WAYPOINT_X_FINE: usize = 0xDB0;
    pub const TARGET_WAYPOINT_Y_FINE: usize = 0xDB8;

    // --- Avatar-lifecycle WITNESSES (mac rs2client 948-5; verified this session) --
    // The exact stage the local avatar dies at: decode -> compose -> mesh -> bind ->
    // drift. Each maps to a write the binary performs in the named fn, so a witness
    // being null/0/-1 pins the failure to a specific stage. Sourced from the Ghidra
    // DB entry-point comments + decompilation of:
    //   DecodeAppearance          @0x100031480  (decode witness, body-vector reset)
    //   ProcessPendingAppearance  @0x10002ba20  (body-type model compose gate)
    //   Avatar::SetAppearance     @0x100411a60  (compose-object dirty fields)
    //   AdvanceRenderPosition     @0x1003a2500  (lerp end-tick, map-square bind,
    //                                            render_model+0x135 bind byte,
    //                                            TARGET/PREV {x,elev,z} fine triples)

    /// Body/skeleton model handle (`avatar + 0x1068`) — the COMPOSE WITNESS.
    /// `ProcessPendingAppearance` gates the per-tick compose on this being null
    /// (`*(avatar+0x1068) == 0` => not yet composed); non-zero => the body model was
    /// built. This is ALSO the object whose `+0x98/+0x9C/+0xA0/+0x84` dirty fields
    /// below are read (`body_obj = *(avatar+0x1068)`).
    pub const BODY_TYPE_MODEL: usize = 0x1068;

    /// Title/prefix sprite (`avatar + 0x10AC`, u32) — the DECODE WITNESS. Written by
    /// `DecodeAppearance` (`*(uint *)(avatar + 0x10ac) = decoded_prefix`) every time
    /// the appearance blob is parsed, so a non-default value here proves
    /// DecodeAppearance ran for this avatar (independently of whether compose did).
    pub const DECODE_WITNESS: usize = 0x10AC;

    /// Lerp end-tick (`avatar + 0x0DBC`, i32) — the DRIFT WITNESS. -1 means no active
    /// prev->target waypoint, so `AdvanceRenderPosition` takes the open-loop drift
    /// fallback (re-deriving position from the nav/collision grid) instead of
    /// interpolating a real segment. -1 here while the avatar should be moving =>
    /// the drift fallback (the documented invisible+creeping-avatar symptom).
    pub const LERP_END_TICK: usize = 0x0DBC;

    /// Secondary lerp end-tick (`avatar + 0x0DC0`, i32) — the second window bound
    /// `AdvanceRenderPosition` reads alongside `LERP_END_TICK` (`MOV EAX,[RBX+0x180]
    /// ; CMP [RBX+0xdc0],EAX`, file 0x3a2585). Emitted by the per-frame trace next
    /// to `LERP_END_TICK` so a two-segment glide window is fully visible.
    pub const LERP_END_TICK_2: usize = 0x0DC0;

    /// Bound map-square (`avatar + 0x0AA8`, ptr) — the BIND WITNESS. Non-zero => the
    /// avatar is bound to a loaded map square; `AdvanceRenderPosition` only submits
    /// the render bind when `*(*(bind+0x10))+0xa64 == 1` (square built). Null => the
    /// avatar never bound (it is also the root of the drift fallback path).
    pub const MAP_SQUARE_BIND: usize = 0x0AA8;

    /// TARGET lerp waypoint as the full {x, elev, z} fine triple
    /// (`avatar + 0x0DB0/0x0DB4/0x0DB8`, f32) — the logical DESTINATION the render
    /// position interpolates toward. (X/Z duplicate TARGET_WAYPOINT_X/Y_FINE above;
    /// +0xDB4 adds the elevation axis.)
    pub const TARGET_FINE_X: usize = 0x0DB0;
    pub const TARGET_FINE_Y: usize = 0x0DB4;
    pub const TARGET_FINE_Z: usize = 0x0DB8;
    /// PREV lerp waypoint as the full {x, elev, z} fine triple
    /// (`avatar + 0x0DA0/0x0DA4/0x0DA8`, f32) — the lerp SOURCE (previous waypoint).
    pub const PREV_FINE_X: usize = 0x0DA0;
    pub const PREV_FINE_Y: usize = 0x0DA4;
    pub const PREV_FINE_Z: usize = 0x0DA8;

    /// Per-frame bind-state byte (`render_model + 0x135`) — a BIND WITNESS read ONLY
    /// when `render_model` (avatar+0xC58) is non-null. `AdvanceRenderPosition` writes
    /// its low bit each frame (`*(byte*)(model+0x135) = ... | bound`) inside the
    /// `if (render_model != 0)` tail, so it is only meaningful once a mesh exists.
    pub const MODEL_BIND_STATE: usize = 0x135;

    // Compose-object (`body_obj = *(avatar + BODY_TYPE_MODEL)`) dirty state, written
    // by `Avatar::SetAppearance` just before the model build. A freshly-set (still
    // dirty / not-yet-composed) body reads compose_handle==null AND
    // compose_cacheid==0xffffffff; once compose runs these are populated. So a
    // non-null `body_obj` with a null `compose_handle` => SetAppearance ran but the
    // mesh build has not produced a handle yet.
    /// `body_obj + 0x98` (byte): dirty/needs-recompose flag (SetAppearance writes 0).
    pub const COMPOSE_DIRTY: usize = 0x98;
    /// `body_obj + 0x9C` (u32): cache id; `0xffffffff` is the just-set dirty sentinel.
    pub const COMPOSE_CACHEID: usize = 0x9C;
    /// `body_obj + 0xA0` (ptr): composed model handle; null => not yet built (dirty).
    pub const COMPOSE_HANDLE: usize = 0xA0;
    /// `body_obj + 0x84` (u32): gender/body param SetAppearance stamps into the
    /// compose object (`*(body_obj+0x84) = genderParam`).
    pub const COMPOSE_GENDER: usize = 0x84;

    /// Obj-customisation / extra-models EASTL vector appended by DecodeAppearance
    /// when appearance flag bit 0x02 is set: begin/cursor/end. A non-empty span
    /// (end != begin) means extra models were attached.
    pub const EXTRA_MODELS_BEGIN: usize = 0x1270;
    pub const EXTRA_MODELS_END: usize = 0x1278;

    /// `PlayerAppearancePending*` (`avatar + 0x1298`) — the op22 ext-info appearance
    /// object built by `PlayerAppearancePending::Construct @0x100034d40` and consumed
    /// per-tick by `ProcessPendingAppearance @0x10002ba20`, which composes the avatar's
    /// `render_model` (avatar+0xC58) ONLY once this object's async-load gate passes
    /// (`PENDING_COMPOSED_FLAG` becomes 1). NULL once compose has run (or no pending).
    /// THE diagnostic for the invisible local avatar: non-null here + composed_flag 0
    /// means the model COMPOSE is still blocked on the async resource group.
    pub const PENDING_APPEARANCE: usize = 0x1298;

    /// `Appearance*` (`avatar + 0x12A0`) — the APPLIED/current appearance, set by
    /// `Avatar::SetAppearance @0x100411a60` (which the per-tick compose calls just
    /// before building `render_model`). NULL until SetAppearance has run for this
    /// avatar. THE disambiguator vs `PENDING_APPEARANCE`/`render_model` both being
    /// null: non-null here => the compose STARTED (SetAppearance ran) but the model
    /// build produced null; null here (with `PENDING_APPEARANCE` also null) =>
    /// the appearance was NEVER applied (the compose never ran for the local avatar).
    /// Mirrors the engine's `Offsets.AVATAR.CURRENT_APPEARANCE = 0x12A0`. Still
    /// emitted as a context pointer, but NO LONGER the BAS source — see
    /// `RENDER_ANIM_SET_ID` below.
    pub const CURRENT_APPEARANCE: usize = 0x12A0;
    /// LIVE applied BAS / render-animation-set id (`avatar + 0xF38`, **inline
    /// int32**). This is the id that actually drives the playing animation: `-1` =
    /// none, `2699` = our default-char bas once composed. Written by
    /// `ComposeAppearanceModel @0x100032cf0` (`MOV [avatar+0xf38], ECX`) and read
    /// every frame via vtable slot `+0x1d0` (`GetRenderAnimSetId @0x100038d00`,
    /// literally `return *(avatar+0xf38)`). SOURCE OF TRUTH:
    /// `re-resources/docs/net/serverprot/player-appearance-948.md` §"Live applied
    /// BAS + walk/idle animation". This REPLACES the old `*(current_appearance+0x0C)`
    /// read, which the same RE pass proved is the pending object's **title** field
    /// (not the bas) — hence the historical garbage/0. `Offsets.kt` (`+0x0C`-as-bas)
    /// is WRONG for this binary and needs a separate user-approved sync; do NOT
    /// `assert_eq!` the bas against `0x0C`.
    pub const RENDER_ANIM_SET_ID: usize = 0xF38;
    /// `Appearance + 0x0C` (u16): the appearance object's TITLE field (was wrongly
    /// read as the bas — see `RENDER_ANIM_SET_ID`). Retained only so the stale name
    /// resolves; not the bas, not currently read for it.
    pub const APPEARANCE_BAS: usize = 0x0C;

    /// `pending + 0x88` (byte): needsAsyncLoad. 1 for op22-ext-info appearances, which
    /// gates the compose on `SceneLoadRegistry::IsResourceGroupReady` keyed by
    /// `PENDING_RES_7C`/`PENDING_RES_80`. 0 => compose runs immediately.
    pub const PENDING_NEEDS_ASYNC_LOAD: usize = 0x88;
    /// `pending + 0x89` (byte): adjacent gate/state byte (dumped for context).
    pub const PENDING_BYTE_0X89: usize = 0x89;
    /// `pending + 0x8a` (byte): composed flag. Set to 1 once the async gate passes and
    /// `ProcessPendingAppearance` builds the model; stays 0 while blocked. This is the
    /// field that distinguishes a BLOCKED compose from a completed one.
    pub const PENDING_COMPOSED_FLAG: usize = 0x8a;
    /// `pending + 0x7c` (u32): resource-group key A keyed into the SceneLoadRegistry.
    pub const PENDING_RES_7C: usize = 0x7c;
    /// `pending + 0x80` (u32): resource-group key B keyed into the SceneLoadRegistry.
    pub const PENDING_RES_80: usize = 0x80;
    /// `pending + 0x84` (u32): adjacent resource/state word (dumped for context).
    pub const PENDING_WORD_0X84: usize = 0x84;
}

// ---------------------------------------------------------------------------
// Game-state enum (`Client::SetMainState` newState values). Identical to Linux.
// Owned here (not a struct offset; the migrator does not generate it).
// ---------------------------------------------------------------------------
pub const STATE_INITIALIZING: i32 = 0;
pub const STATE_LOGIN_SCREEN: i32 = 10;
pub const STATE_LOBBY_SCREEN: i32 = 20;
pub const STATE_ACCOUNT_CREATION: i32 = 23;
pub const STATE_LOGGED_IN: i32 = 30;
pub const STATE_REESTABLISH: i32 = 35;
pub const STATE_RECONNECTING: i32 = 37;
pub const STATE_LOADING: i32 = 40;

pub fn state_name(state: i32) -> &'static str {
    match state {
        0 => "INITIALIZING",
        10 => "LOGIN_SCREEN",
        20 => "LOBBY_SCREEN",
        23 => "ACCOUNT_CREATION",
        30 => "LOGGED_IN",
        35 => "REESTABLISH",
        37 => "RECONNECTING",
        40 => "LOADING",
        _ => "UNKNOWN",
    }
}

// ---------------------------------------------------------------------------
// Capture-point byte signatures (verified to match exactly once in __text of
// re-resources/948-5/rs2client.948-5-mac). `??` = full-byte wildcard.
//
// These stay hand-owned (the runtime sig-scan is the load-bearing locator; the
// migrator owns only the struct/derived offsets the signatures' disassembly
// yields). Each `Pattern` carries its expected file offset
// (`vmaddr - 0x100000000`) so a scan that lands somewhere unexpected can be
// logged/refused rather than silently hooking the wrong address. They double as
// the post-scan sanity-check values for the migrator's function file-offsets.
// ---------------------------------------------------------------------------

/// `jag::ConnectionManager::ReadPacket` — S2C: post-hook reads LAST_OPCODE.
/// Kept as a coherence BACKSTOP only — the primary s2c capture is the inline
/// dispatch hook below (`SIG_S2C_DISPATCH`), which reads CURRENT_OPCODE coherently.
pub const SIG_READ_PACKET: Pattern = Pattern::new(
    "ReadPacket",
    0x6c920,
    "55 48 89 E5 41 57 41 56 41 55 41 54 53 48 83 EC 68 4C 8B 66 08 4D 8B 6C 24 08 4D 85 ED",
);

/// `jag::ConnectionManager::ReadPacket` INLINE s2c dispatch site (file 0x6d8ea).
/// This is the coherent (opcode, body) read point per RE §3a / §1c: at this
/// instruction the packet is fully decoded+buffered and `CURRENT_OPCODE`
/// (conn+0x2C) is STILL this packet's opcode (reset to -1 only at 0x6d955, after
/// the handler dispatch at 0x6d930). We install a register-PRESERVING inline
/// observer here (detour::install_inline) and read:
///   conn   = *(RSI + 0x8)           // RSI = msg context; this packet's connection
///   opcode = *(int*)(conn + 0x2C)   // CURRENT_OPCODE — valid here, never lags
///   size   = *(int*)(conn + 0x30)   // RESOLVED_SIZE
///   body   = (*(void**)(conn+0x2D0))[0..size]   // BUF_DATA (decrypted)
/// opcode and body are from the SAME packet at the SAME instant — the op82-body-
/// tagged-op3 desync (the stale post-hook LAST_OPCODE) cannot occur here.
///
/// Stolen window (15 bytes, all whole, no RIP-relative): MOV RAX,[R15+0x30];
/// MOV R8,[RAX+0x30]; MOV RAX,[RSI+0x8]; TEST R8,R8. The 27-byte signature below
/// is unique in __text (verified) so no wildcards are needed.
pub const SIG_S2C_DISPATCH: Pattern = Pattern::new(
    "ReadPacket::s2cDispatch",
    0x6d8ea,
    "49 8B 47 30 4C 8B 40 30 48 8B 46 08 4D 85 C0 74 4C 48 89 FB 49 89 F6 41 8B 4F 04",
);

/// `jag::game::TcpConnectionMessage::Init` — C2S opcode/size/isaac choke point.
pub const SIG_TCPMSG_INIT: Pattern = Pattern::new(
    "TcpConnectionMessage::Init",
    0x57d20,
    "55 48 89 E5 41 57 41 56 41 55 41 54 53 48 83 EC 28 49 89 CE 49 89 F7 48 89 FB 8B 06 89 07 89 57 04 0F 57 C0 0F 11 47 10",
);

/// `jag::ServerConnection::FlushOutgoingQueue` — C2S complete framed body.
pub const SIG_FLUSH_QUEUE: Pattern = Pattern::new(
    "FlushOutgoingQueue",
    0x41d120,
    "55 48 89 E5 41 57 41 56 41 54 53 48 83 EC 10 48 8B 47 08 48 85 C0 0F 84 AA 01 00 00 49 89 FE",
);

/// `jag::ClientStream::Read` — raw recv funnel (login/RSA S->C).
pub const SIG_CLIENTSTREAM_READ: Pattern = Pattern::new(
    "ClientStream::Read",
    0x8dbf60,
    "55 48 89 E5 41 57 41 56 53 50 48 FF 05 ?? ?? ?? ?? 48 8B 87 80 00 00 00 48 F7 D0",
);

/// `jag::ClientStream::Write` — raw send funnel (login/RSA C->S).
pub const SIG_CLIENTSTREAM_WRITE: Pattern = Pattern::new(
    "ClientStream::Write",
    0x8dc340,
    "55 48 89 E5 41 57 41 56 41 55 41 54 53 48 83 EC 18 83 3F 00 74 ?? 49 89 D4 49 89 F7 48 89 FB 80 BF E8 00 00 00 01",
);

/// `jag::Client::SetMainState` — game-state setter.
pub const SIG_SET_MAIN_STATE: Pattern = Pattern::new(
    "Client::SetMainState",
    0x27ff50,
    "39 B7 B0 9D 01 00 74 ?? 55 48 89 E5 41 57 41 56 53 50 89 F3 49 89 FE 4C 8B BF 70 96 01 00",
);

/// `jag::Isaac::Init` — ISAAC seeder. Read the 4 seed ints at RSI on entry.
/// 1st call/session = OUT/SEND seed (raw keys); 2nd = IN/RECV seed (keys+50).
pub const SIG_ISAAC_INIT: Pattern = Pattern::new(
    "Isaac::Init",
    0xa71a10,
    "55 48 89 E5 41 57 41 56 41 54 53 49 89 F6 48 89 FB 48 83 C7 04 BE 00 08",
);

/// `jag::ClientStream::Fill` — recv() funnel (game + login raw s2c bytes).
/// PRE: before = *(this+0xA8); POST: n = *(this+0xA8) - before, bytes at *(this+0xC8).
pub const SIG_CLIENTSTREAM_FILL: Pattern = Pattern::new(
    "ClientStream::Fill",
    0x8dc070,
    "55 48 89 E5 41 57 41 56 53 50 48 89 FB 8B 7F 08 85 FF 0F 84 E3 00 00 00",
);

/// `jag::LoginStateMachine::OpenLoginStream` — the EARLIEST login anchor (RE §8a).
/// Hooked at ENTRY (the `this` = LoginStateMachine in RDI is intact): we publish
/// the Client base from `*(RDI + 0x18)` and register the login `ClientStream`
/// (`*(*(RDI + 0x30)) + 0x08`) so the very first login `Fill` recv is tagged
/// "login" — BEFORE ConnMgr+0x28 (LOGIN_CONNECTION) resolves a beat later. This
/// is what stops the early login s2c from being mis-bucketed as "js5".
///
/// Prologue (verified against the binary): `cmp [rdi+0x10],0x0e; jne ...;
/// push rbp; mov rbp,rsp; push rbx; push rax; xor edx,edx` — the first 14 bytes
/// (the abs-jmp steal window). The `jne` displacement is wildcarded so a sub-rev
/// that shifts the branch body still matches; its target is outside the stolen
/// window so the trampoline relocates the rel8→rel32 cleanly. `83 7F 10 0E` =
/// `cmp dword [rdi+0x10],0x0e` (the state-0x0e gate that names this function).
pub const SIG_OPEN_LOGIN_STREAM: Pattern = Pattern::new(
    "LoginStateMachine::OpenLoginStream",
    0xc96a0,
    "83 7F 10 0E 75 ?? 55 48 89 E5 53 50 31 D2 83 7F 20 02 0F 95 C2 48 8B 47",
);

// NOTE — there is deliberately NO `SIG_ADVANCE_RENDER_POSITION`. The per-frame anim
// trace does NOT hook `AdvanceRenderPosition @0x1003a2500`: it is a wall-to-wall SSE
// per-frame render function and `detour::install_inline` does not save/restore
// XMM0-15, so an inline observer corrupts its float registers → `libc++abi:
// terminating` (verified prod crash at the login screen). The trace is instead a
// register-safe POLLER that reads the local avatar off `mem::*` (see
// `oracle::read_anim_frame`). The avatar struct offsets it uses (controller
// +0x300/+0xB18/+0xB60, LERP_END_TICK_2) live in the `avatar` mod below; the
// per-frame tick is `client_oracle::BASE_TICK` (Client+0x518).

/// `jag::ConnectionManager::SetupLoginCiphers` — the Phase A→B login boundary
/// (RE §8a). This runs at reply-state 0x50, exactly where the login handshake's
/// PLAINTEXT phase ends and the ISAAC CIPHERTEXT phase begins. We hook it at
/// ENTRY only to emit a `login_cipher_ready` marker (with the running login-s2c
/// byte offset) so the offline deframer can split `raw-login-s2c.bin` /
/// `socket.jsonl` login-s2c at that boundary. We do NOT read the seed here — the
/// `Isaac::Init` hook already captures all seeds; this is purely a timing marker.
/// `83 7F 10 50` = `cmp dword [rdi+0x10],0x50` (the login-state gate).
pub const SIG_SETUP_LOGIN_CIPHERS: Pattern = Pattern::new(
    "ConnectionManager::SetupLoginCiphers",
    0xca170,
    "55 48 89 E5 41 57 41 56 41 55 41 54 53 48 83 EC 78 B3 01 83 7F 10 50 0F",
);

#[cfg(test)]
mod tests {
    use super::*;

    /// These assertions are the byte-identity gate between the GENERATED offsets
    /// (now the source of truth, re-exported above) and the known-good 948-5
    /// values. They pass iff `include!`-ing the migrator output reproduces every
    /// offset the dylib relies on — i.e. wiring the migrator in did not change a
    /// single resolved value. Bump these only when the migrator legitimately
    /// reports drift for a NEW rev (and the RE doc confirms the new layout).
    #[test]
    fn struct_offsets_match_re_doc() {
        assert_eq!(server_connection::CLIENT_STREAM, 0x08);
        assert_eq!(server_connection::LAST_OPCODE, 0x2E0);
        assert_eq!(server_connection::BUF_DATA, 0x2D0);
        assert_eq!(server_connection::ISAAC_IN_PTR, 0x2B8);
        assert_eq!(server_connection::ISAAC_OUT_PTR, 0x40);
        assert_eq!(connection_manager::GAME_CONNECTION, 0x18);
        assert_eq!(connection_manager::LOGIN_CONNECTION, 0x28);
        // mac-specific Client offsets (must NOT be the Linux values).
        assert_eq!(client::CONNECTION_MANAGER, 0x196C8);
        assert_eq!(client::MAIN_STATE, 0x19DB0);
        assert_ne!(client::MAIN_STATE, 0x19B28);
        // c2s flush-side corrections (the op=0/empty bug).
        assert_eq!(queue_node::PACKET, 0x18);
        assert_eq!(packet::FINAL_PAYLOAD_SIZE, 0x28);
        // Fill-path raw s2c accounting + ISAAC seed read.
        assert_eq!(client_stream::RING_COUNT, 0xA8);
        assert_eq!(client_stream::SCRATCH_BUF, 0xC8);
        assert_eq!(isaac::RANDRSL, 0x04);
        // Inline s2c dispatch read: conn = *(msgCtx + 0x8); opcode = CURRENT_OPCODE.
        assert_eq!(connection_manager::MSG_CTX_CONN, 0x08);
        assert_eq!(server_connection::CURRENT_OPCODE, 0x2C);
        // Lobby/login early-tag cross-check (RE §8a).
        assert_eq!(client::LOGIN_STATE_MACHINE, 0x19720);
        assert_eq!(login_state_machine::LOGIN_CONNECTION, 0x30);
        assert_eq!(login_state_machine::OWNER_CLIENT, 0x18);
        // Shim-supplemented connection_manager fields (registry gap; see above).
        assert_eq!(connection_manager::OWNER_CLIENT, 0x08);
        assert_eq!(connection_manager::TINYKEY, 0x10);
    }

    #[test]
    fn oracle_offsets_match_re_doc() {
        use client_oracle as o;
        // Live per-frame cycle counter "baseTick" (RE: read at 0x10027d060 as
        // [Client+0x518], passed into AdvanceRenderPosition; player-appearance-948.md).
        assert_eq!(o::BASE_TICK, 0x518);
        // Client-base embeds shifted +0x288 vs Linux (RE §10).
        assert_eq!(o::PLAYER_VAR_DOMAIN, 0x19DC8);
        assert_eq!(o::LOGGED_IN_PLAYER, 0x19DB8);
        assert_eq!(o::PLAYER_MANAGER, 0x19760);
        assert_eq!(o::MAIN_LOGIC_MANAGER, 0x19730);
        // VARP VALUES table (PVD+0x20): bucketArray @ +0x28, count @ +0x30;
        // node value @ +0x8, next @ +0x28.
        assert_eq!(o::PVD_VALUES_BUCKET_ARRAY, 0x28);
        assert_eq!(o::PVD_VALUES_BUCKET_COUNT, 0x30);
        assert_eq!(o::PVD_NODE_VALUE, 0x08);
        assert_eq!(o::PVD_NODE_NEXT, 0x28);
        // VARC record tree: Client+0x196D8 is a pointer, not embedded; values live
        // in InterfaceManager record-tree nodes keyed by (recordKind,varId).
        assert_eq!(o::VARC_DOMAIN, 0x196D8);
        assert_eq!(o::VARC_RECORD_TREE_HEADER, 0x1E120);
        assert_eq!(o::VARC_RECORD_TREE_ROOT, 0x1E130);
        assert_eq!(o::VARC_RECORD_TREE_COUNT, 0x1E140);
        assert_eq!(o::VARC_NODE_LEFT, 0x00);
        assert_eq!(o::VARC_NODE_RIGHT, 0x08);
        assert_eq!(o::VARC_NODE_PARENT, 0x10);
        assert_eq!(o::VARC_NODE_COLOR, 0x18);
        assert_eq!(o::VARC_NODE_RECORD_KIND, 0x20);
        assert_eq!(o::VARC_NODE_VAR_ID, 0x24);
        assert_eq!(o::VARC_NODE_VALUE, 0x48);
        assert_eq!(o::VARC_NODE_VALUE_KIND, 0x60);
        assert_eq!(o::VARC_STRING_INLINE_LIMIT, 0x18);
        assert_eq!(o::VARC_STRING_INLINE_TAG, 0x5F);
        assert_eq!(o::VARC_STRING_LENGTH, 0x50);
        assert_eq!(o::VARC_STRING_CAP_TAG, 0x58);
        // Item-container store: Client+0x197D8 is a pointer to a persistent
        // sorted vector keyed by inventoryId*2+(flags&1), with inline u64 slots.
        assert_eq!(o::ITEM_CONTAINER_STORE, 0x197D8);
        assert_eq!(o::ITEM_CONTAINER_ENTRY_BEGIN, 0x08);
        assert_eq!(o::ITEM_CONTAINER_ENTRY_END, 0x10);
        assert_eq!(o::ITEM_CONTAINER_ENTRY_CAP, 0x18);
        assert_eq!(o::ITEM_CONTAINER_ENTRY_STRIDE, 0x48);
        assert_eq!(o::ITEM_CONTAINER_ENTRY_KEY, 0x00);
        assert_eq!(o::ITEM_CONTAINER_ENTRY_PAYLOAD, 0x08);
        assert_eq!(o::ITEM_CONTAINER_INVENTORY_ID, 0x08);
        assert_eq!(o::ITEM_CONTAINER_SLOT_BEGIN, 0x10);
        assert_eq!(o::ITEM_CONTAINER_SLOT_END, 0x18);
        assert_eq!(o::ITEM_CONTAINER_SLOT_CAP, 0x20);
        assert_eq!(o::ITEM_CONTAINER_SLOT_STRIDE, 0x08);
        assert_eq!(o::ITEM_CONTAINER_SLOT_ITEM_ID, 0x00);
        assert_eq!(o::ITEM_CONTAINER_SLOT_QUANTITY, 0x04);
        assert_eq!(o::ITEM_CONTAINER_DIRTY_MANAGER, 0x197A8);
        // Scene entity oracle STAGE 1: all player/NPC positions.
        assert_eq!(o::NPC_MANAGER, 0x19740);
        assert_eq!(o::PM_PLAYER_LIST_CAPACITY, 0x800);
        assert_eq!(o::PM_RENDER_LIST_BEGIN, 0x4038);
        assert_eq!(o::PM_RENDER_LIST_END, 0x4040);
        assert_eq!(o::PM_RENDER_LIST_CAP, 0x4048);
        assert_eq!(o::PM_PENDING_LIST_BEGIN, 0x6060);
        assert_eq!(o::PM_PENDING_LIST_END, 0x6068);
        assert_eq!(o::PM_PENDING_LIST_CAP, 0x6070);
        assert_eq!(o::PM_EXTINFO_LIST_BEGIN, 0xA0D0);
        assert_eq!(o::PM_EXTINFO_LIST_END, 0xA0D8);
        assert_eq!(o::PM_EXTINFO_LIST_CAP, 0xA0E0);
        assert_eq!(o::NPC_BUCKETS, 0x10);
        assert_eq!(o::NPC_BUCKET_COUNT, 0x18);
        assert_eq!(o::NPC_MAP_COUNT, 0x20);
        assert_eq!(o::NPC_ACTIVE_INDICES, 0xA0A0);
        assert_eq!(o::NPC_ACTIVE_COUNT, 0xB0A0);
        assert_eq!(o::NPC_COORD_BITS, 0xC0E8);
        assert_eq!(o::NPC_EXTINFO_LIST_BEGIN, 0xC0F0);
        assert_eq!(o::NPC_EXTINFO_LIST_END, 0xC0F8);
        assert_eq!(o::NPC_EXTINFO_LIST_CAP, 0xC100);
        assert_eq!(o::NPC_NODE_KEY, 0x00);
        assert_eq!(o::NPC_NODE_ENTITY, 0x10);
        assert_eq!(o::NPC_NODE_NEXT, 0x18);
        assert_eq!(o::NPC_ENTITY_TYPE_ID, 0x1060);
        // Local-avatar appearance oracle STAGE 2: committed kit/item slot pairs.
        assert_eq!(o::AVATAR_APPEARANCE_PENDING, 0x1298);
        assert_eq!(o::AVATAR_APPEARANCE_APPLIED, 0x12A0);
        assert_eq!(o::AVATAR_APPEARANCE_NEXT, 0x12A8);
        assert_eq!(o::APPEARANCE_EQUIP_CONTEXT, 0x98);
        assert_eq!(o::EQUIP_CTX_SLOT_COUNT, 0x30);
        assert_eq!(o::EQUIP_CTX_SLOT_PAIRS, 0x38);
        assert_eq!(o::EQUIP_CTX_SLOT_STRIDE, 0x08);
        assert_eq!(o::EQUIP_CTX_SLOT_KIT_ID, 0x00);
        assert_eq!(o::EQUIP_CTX_SLOT_ITEM_ID, 0x04);
        // Tile chain inner offsets (MATCH Linux).
        assert_eq!(o::LIP_SERVER_INDEX, 0x48);
        assert_eq!(o::PM_PLAYER_LIST, 0x10);
        assert_eq!(o::NODE_ENTITY, 0x38);
        assert_eq!(o::ENTITY_GRAPH_NODE, 0x08);
        assert_eq!(o::ENTITY_PLANE, 0x40);
        assert_eq!(o::ENTITY_SIZE, 0x184);
        assert_eq!(o::GRAPH_SCENE_X_FINE, 0xF0);
        assert_eq!(o::GRAPH_SCENE_Y_FINE, 0xF8);
        // StatTable / status object (MLM+0x7520 DIFFERS; inner MATCH).
        assert_eq!(o::MLM_STAT_TABLE, 0x7520);
        assert_eq!(o::STAT_TABLE_BEGIN, 0x10);
        assert_eq!(o::STAT_ENTRY_STRIDE, 0x18);
        assert_eq!(o::STAT_XP, 0x0C);
        assert_eq!(o::STAT_BASE_LEVEL, 0x10);
        assert_eq!(o::STAT_BOOST_LEVEL, 0x14);
        assert_eq!(o::STATUS_RUN_ENERGY, 0x18);
        assert_eq!(o::STATUS_RUN_WEIGHT, 0x1C);
    }

    #[test]
    fn avatar_render_offsets_match_ghidra() {
        // All BYTE offsets on the avatar/graphEntity (== node+0x38). Read from the
        // mac 948-5 Ghidra DB this session (RefreshLocalVisibility / AttachToMapSquare
        // / AdvanceRenderPosition / DecodeAppearance). Bump only on a genuine layout
        // change confirmed in the binary.
        assert_eq!(avatar::LIP_LOCAL_OVERRIDE, 0x58);
        assert_eq!(avatar::VISIBLE_FLAG, 0x1070);
        assert_eq!(avatar::RENDER_MODEL, 0xC58); // OEntity.RENDER_MODEL
        // Live move-speed / threshold / run-flag the per-frame animator reads
        // (SelectMovementAnimation @0x1003a4e90). SOURCE OF TRUTH = the RE doc
        // player-appearance-948.md §"Per-frame idle/walk/run selection"; NOT the
        // engine `Offsets.kt` LAST_MOVESPEED@0x98 (a `UInt32*`, WRONG for this
        // binary — flagged for a separate user-approved Offsets.kt sync). Hence NO
        // cross-check against 0x98 here.
        assert_eq!(avatar::MOVE_SPEED, 0x1F0);
        assert_eq!(avatar::MOVE_SPEED_THRESHOLD, 0x1EC);
        assert_eq!(avatar::RUN_FLAG, 0x1F8);
        // Route-anim queue (the real walk/run movement-seq driver): begin/end an EASTL
        // `int` vector @ +0x2c8/+0x2d0, stride 4 (int32), priority @ +0xb64. VERIFIED by
        // decompiling SetMovementAnimSet @0x1003a69f0 (RE doc §"What ACTUALLY animates a
        // remote-player walk"); the count is `(end-begin)>>2`.
        assert_eq!(avatar::ROUTE_ANIM_QUEUE_BEGIN, 0x2c8);
        assert_eq!(avatar::ROUTE_ANIM_QUEUE_END, 0x2d0);
        assert_eq!(avatar::ROUTE_ANIM_QUEUE_STRIDE, 4);
        assert_eq!(avatar::ROUTE_ANIM_QUEUE_PRIORITY, 0xb64);
        // Player-side current render-anim @ +0x958 — the PathingEntity equivalent of the
        // engine ONPC.RENDER_ANIM (0x958); read in AdvanceRenderPosition @0x1003a2dda.
        assert_eq!(avatar::RENDER_ANIM, 0x958);
        assert_eq!(avatar::ANIMATION_ID, 0xA88); // OEntity.ANIMATION_ID
        assert_eq!(avatar::ANIMATION_SHARED_PTR, 0xAA0); // OEntity.ANIMATION_SHARED_PTR
        assert_eq!(avatar::ANIMATION_CURRENT_FRAME, 0x24); // OAnimation.CURRENT_FRAME
        // WHY the anim-trace poller does NOT use the engine's shared_ptr→Animation
        // chain for a playback frame: that chain reads the object ptr from
        // `0xAA0+0x8 == 0xAA8`, which is `MAP_SQUARE_BIND` on this layout — proven in
        // AdvanceRenderPosition. This assert pins that collision so nobody re-adds a
        // (fabricated) frame read off the shared_ptr `+0x8` slot.
        assert_eq!(avatar::ANIMATION_SHARED_PTR + 0x8, avatar::MAP_SQUARE_BIND);
        // Anim controller (avatar+0x300). Verified via StartAnimSeq @0x100589f80 +
        // the AdvanceRenderPosition disassembly: `LEA R15,[RBX+0x300]` is the
        // controller base; `CMP [RBX+0xb18],-1` = primary seq; the incoming-seq
        // guard reads controller+0x788 == avatar+0xA88 (== ANIMATION_ID); blend is
        // controller+0x860 == avatar+0xB60.
        assert_eq!(avatar::ANIM_CONTROLLER, 0x300);
        assert_eq!(avatar::ANIM_CONTROLLER_PRIMARY_SEQ, 0xB18);
        assert_eq!(avatar::ANIM_CONTROLLER + 0x818, avatar::ANIM_CONTROLLER_PRIMARY_SEQ);
        assert_eq!(avatar::ANIM_CONTROLLER_SECONDARY_SEQ, 0xA88);
        assert_eq!(avatar::ANIM_CONTROLLER + 0x788, avatar::ANIM_CONTROLLER_SECONDARY_SEQ);
        // The controller's secondary-seq slot IS the entity's ANIMATION_ID memory.
        assert_eq!(avatar::ANIM_CONTROLLER_SECONDARY_SEQ, avatar::ANIMATION_ID);
        assert_eq!(avatar::ANIM_CONTROLLER_BLEND_TICKS, 0xB60);
        assert_eq!(avatar::ANIM_CONTROLLER + 0x860, avatar::ANIM_CONTROLLER_BLEND_TICKS);
        assert_eq!(avatar::SIZE, 0x184); // OEntity.SIZE — same as the oracle tile path
        assert_eq!(avatar::RENDER_GRAPH_NODE, 0x8); // param_1[1] == entity+0x8
        assert_eq!(avatar::SCENE_BUCKET_GRAPH_NODE, 0x268); // param_1[0x4d]
        assert_eq!(avatar::RENDER_POS_X_DOUBLE, 0x2A8); // param_1[0x55]
        assert_eq!(avatar::RENDER_POS_Y_DOUBLE, 0x2B8); // param_1[0x57]
        assert_eq!(avatar::PREV_WAYPOINT_X_FINE, 0xDA0); // param_1[0x1b4]
        assert_eq!(avatar::PREV_WAYPOINT_Y_FINE, 0xDA8); // param_1[0x1b5]
        assert_eq!(avatar::TARGET_WAYPOINT_X_FINE, 0xDB0); // param_1[0x1b6]
        assert_eq!(avatar::TARGET_WAYPOINT_Y_FINE, 0xDB8); // param_1[0x1b7]
        assert_eq!(avatar::EXTRA_MODELS_BEGIN, 0x1270);
        assert_eq!(avatar::EXTRA_MODELS_END, 0x1278);
        // Pending-appearance async gate: PlayerAppearancePending @ avatar+0x1298
        // (Construct @0x100034d40 / ProcessPendingAppearance @0x10002ba20). Gate bytes
        // +0x88 needsAsyncLoad / +0x8a composed; resource-group keys +0x7c/+0x80.
        assert_eq!(avatar::PENDING_APPEARANCE, 0x1298);
        // Applied/current appearance @ avatar+0x12A0 (Avatar::SetAppearance @0x100411a60).
        assert_eq!(avatar::CURRENT_APPEARANCE, 0x12A0);
        // LIVE applied BAS @ avatar+0xF38 (inline int32; GetRenderAnimSetId
        // @0x100038d00 / ComposeAppearanceModel @0x100032cf0). THE bas the oracle
        // reads. SOURCE OF TRUTH = the RE doc §"Live applied BAS …"; NOT the engine
        // `Offsets.kt` `+0x0C`-as-bas (that is the appearance TITLE, WRONG for this
        // binary — flagged for a separate Offsets.kt sync). So no bas cross-check vs 0x0C.
        assert_eq!(avatar::RENDER_ANIM_SET_ID, 0xF38);
        assert_eq!(avatar::APPEARANCE_BAS, 0x0C); // appearance TITLE field (NOT the bas)
        assert_eq!(avatar::PENDING_NEEDS_ASYNC_LOAD, 0x88);
        assert_eq!(avatar::PENDING_BYTE_0X89, 0x89);
        assert_eq!(avatar::PENDING_COMPOSED_FLAG, 0x8a);
        assert_eq!(avatar::PENDING_RES_7C, 0x7c);
        assert_eq!(avatar::PENDING_RES_80, 0x80);
        assert_eq!(avatar::PENDING_WORD_0X84, 0x84);
        // Avatar-lifecycle witnesses (DecodeAppearance / ProcessPendingAppearance /
        // SetAppearance / AdvanceRenderPosition). Compose: body model @ +0x1068.
        assert_eq!(avatar::BODY_TYPE_MODEL, 0x1068);
        // Decode: title/prefix sprite @ +0x10AC.
        assert_eq!(avatar::DECODE_WITNESS, 0x10AC);
        // Drift: lerp end-tick @ +0xDBC (-1 = no waypoint).
        assert_eq!(avatar::LERP_END_TICK, 0x0DBC);
        // Bind: bound map-square @ +0xAA8 (== param_1[0x155]).
        assert_eq!(avatar::MAP_SQUARE_BIND, 0x0AA8);
        // TARGET/PREV fine {x,elev,z} triples (stride 4); x/z alias the 2D waypoints.
        assert_eq!(avatar::TARGET_FINE_X, 0x0DB0);
        assert_eq!(avatar::TARGET_FINE_Y, 0x0DB4);
        assert_eq!(avatar::TARGET_FINE_Z, 0x0DB8);
        assert_eq!(avatar::PREV_FINE_X, 0x0DA0);
        assert_eq!(avatar::PREV_FINE_Y, 0x0DA4);
        assert_eq!(avatar::PREV_FINE_Z, 0x0DA8);
        assert_eq!(avatar::TARGET_FINE_X, avatar::TARGET_WAYPOINT_X_FINE);
        assert_eq!(avatar::TARGET_FINE_Z, avatar::TARGET_WAYPOINT_Y_FINE);
        assert_eq!(avatar::PREV_FINE_X, avatar::PREV_WAYPOINT_X_FINE);
        assert_eq!(avatar::PREV_FINE_Z, avatar::PREV_WAYPOINT_Y_FINE);
        // Per-frame model bind byte @ render_model+0x135.
        assert_eq!(avatar::MODEL_BIND_STATE, 0x135);
        // Compose-object dirty fields off body_obj (= *(avatar+0x1068)).
        assert_eq!(avatar::COMPOSE_DIRTY, 0x98);
        assert_eq!(avatar::COMPOSE_CACHEID, 0x9C);
        assert_eq!(avatar::COMPOSE_HANDLE, 0xA0);
        assert_eq!(avatar::COMPOSE_GENDER, 0x84);
        // The avatar IS the oracle's `entity` (node+0x38): VISIBLE/render-model sit
        // on the same object the tile chain dereferences, so the visible-flag read
        // and the tile read share the node+0x38 resolution.
        assert_eq!(avatar::RENDER_GRAPH_NODE, client_oracle::ENTITY_GRAPH_NODE);
        assert_eq!(avatar::SIZE, client_oracle::ENTITY_SIZE);
    }

    #[test]
    fn new_lobby_sigs_target_documented_offsets() {
        // OpenLoginStream entry (RE §8a) — file 0xc96a0; SetupLoginCiphers entry
        // (the Phase A→B boundary) — file 0xca170.
        assert_eq!(SIG_OPEN_LOGIN_STREAM.expected_file, 0xc96a0);
        assert_eq!(SIG_SETUP_LOGIN_CIPHERS.expected_file, 0xca170);
        // The OpenLoginStream sig must start with the state-0x0e gate that names
        // the function (cmp dword [rdi+0x10], 0x0e = 83 7F 10 0E).
        let toks: Vec<&str> = SIG_OPEN_LOGIN_STREAM.text.split_whitespace().collect();
        assert_eq!(&toks[0..4], &["83", "7F", "10", "0E"]);
        // SetupLoginCiphers gate: cmp dword [rdi+0x10], 0x50 (83 7F 10 50) at +19.
        let toks: Vec<&str> = SIG_SETUP_LOGIN_CIPHERS.text.split_whitespace().collect();
        assert_eq!(&toks[19..23], &["83", "7F", "10", "50"]);
    }

    #[test]
    fn s2c_dispatch_sig_targets_inline_site() {
        // The PRIMARY coherent s2c hook is the inline dispatch site at file
        // 0x6d8ea (RE §3a) — distinct from the ReadPacket entry (0x6c920 backstop).
        assert_eq!(SIG_S2C_DISPATCH.expected_file, 0x6d8ea);
        assert_ne!(
            SIG_S2C_DISPATCH.expected_file,
            SIG_READ_PACKET.expected_file
        );
        // Sanity: the inline pattern parses (no malformed tokens → no all-wildcard).
        // The first three bytes are MOV RAX,[R15+0x30] = 49 8B 47.
        let toks: Vec<&str> = SIG_S2C_DISPATCH.text.split_whitespace().collect();
        assert_eq!(&toks[0..3], &["49", "8B", "47"]);
    }

    #[test]
    fn state_enum_values() {
        assert_eq!(STATE_LOGGED_IN, 30);
        assert_eq!(state_name(20), "LOBBY_SCREEN");
        assert_eq!(state_name(999), "UNKNOWN");
    }

    #[test]
    fn server_prot_table_layout_matches_re_doc() {
        // g_serverProtTable @ vmaddr 0x100f0eaf0 → image-relative 0xf0eaf0.
        assert_eq!(server_prot::TABLE_GLOBAL, 0xf0eaf0);
        // ReadPacket gates `opcode < 0xDA` → valid opcodes 0..=0xD9.
        assert_eq!(server_prot::MAX_OPCODE, 0xD9);
        assert_eq!(server_prot::ENTRY_PTR_STRIDE, 8);
        // ProtEntry: opcode@+0, sizeClass@+4 (RegisterProt 0x4155e0).
        assert_eq!(server_prot::ENTRY_OPCODE, 0x00);
        assert_eq!(server_prot::ENTRY_SIZE_CLASS, 0x04);
        // Handler chain: *(*(entry+0x30))+0x30 (dispatch 0x6d8ea / RE §10.4).
        assert_eq!(server_prot::ENTRY_HANDLER_CATEGORY, 0x30);
        assert_eq!(server_prot::HANDLER_VTABLE_SLOT, 0x30);
    }
}
