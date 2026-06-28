package com.undercut.game.nxt

object OGlobal {
    const val CLIENT = 0x015bf1f8L                                  //948-5 verified unchanged from 948-2-2 (data section stable across sub-patch) — jag::Client::Client global label
}

object OFunctions {
    // Migrated 948-2-2 -> 948-5 (sub-revision bump). 32 entries relocated via auto-updater
    // (re-resources/sigs-results/results_948-5.ndjson); 5 AMBIGUOUS entries resolved by sig-scan from 948-2-2.
    const val CONNECTIONMANAGER_TCPIN = 0x0013d9f0L             //948-5 (was 0x0013d870 in 948-2-2) — Phase 1 anchor
    const val CLIENT_SETMAINSTATE = 0x004900d0L                 //948-5 (was 0x0048ff20 in 948-2-2) — jag::Client::SetMainState
    const val STATTABLE_UPDATESTAT = 0x000ef2a0L                //948-5 unchanged from 948-2-2 — jag::game::StatTable::UpdateStat
    const val CLIENT_MAINLOGIC = 0x004b37f0L                    //948-5 (was 0x004b3490 in 948-2-2) — jag::Client::MainLogic
    const val SERVERPROT_DECODE_UPDATE_INV_PARTIAL = 0x00184320L //948-5 (was 0x001841a0 in 948-2-2) — jag::packethandlers::Inventory::UPDATE_INV_PARTIAL
    const val SERVERPROT_DECODE_UPDATE_INV_FULL = 0x001a8d30L   //948-5 (was 0x001a8b70 in 948-2-2) — UPDATE_INV_FULL_impl
    const val CHATHISTORY_ADDCHAT_HOOKABLE = 0x0019e6f0L        //948-5 (was 0x0019e530 in 948-2-2) — jag::ChatHistory::AddChat_hookable
    const val PLAYERVARDOMAIN_SET = 0x004e1da0L                 //948-5 (was 0x004e1a40 in 948-2-2) — jag::game::PlayerVarDomain::set
    const val PLAYERVARDOMAIN_SETBIT = 0x004ce260L              //948-5 (was 0x004cdf00 in 948-2-2) — jag::game::PlayerVarDomain::setBit
    const val CLIENTVARDOMAIN_SETVARVALUE = 0x001b7080L         //948-5 (was 0x001b6ec0 in 948-2-2) — jag::ClientVarDomain::SetVarValue
    const val MINIMENU_DOACTIONENTRY = 0x0018cf80L              //948-5 (was 0x0018ce00 in 948-2-2) — jag::MiniMenu::DoActionEntry
    const val INTERFACEMANAGER_IFBUTTONXINNER = 0x00297a90L     //948-5 (was 0x002978d0 in 948-2-2) — jag::InterfaceManager::IfButtonXInner
    const val PROJECTILELIST_ADD = 0x00b23260L                  //948-5 (was 0x00b22ad0 in 948-2-2) — jag::ProjectileList::Add
    const val ENTITY_ENTITY = 0x0013f310L                       //948-5 (was 0x0013f190 in 948-2-2) — jag::game::Entity::Entity
    const val ENTITY_ENTITY_DESTRUCT = 0x004546c0L              //948-5 (was 0x00454590 in 948-2-2) — jag::game::Entity::~Entity
    const val INVENTORYMANAGER_GETINVENTORY = 0x004e5210L       //948-5 (was 0x004e4eb0 in 948-2-2) — jag::game::InventoryManager::GetInventory
    const val HITMARKSANDHEADBARS_ADDHEADBAR = 0x00625cb0L      //948-5 (was 0x00625920 in 948-2-2) — jag::HitmarksAndHeadbars::AddHeadbar
    const val HITMARKSANDHEADBARS_ADDHITMARK = 0x00626ab0L      //948-5 (was 0x00626720 in 948-2-2) — jag::HitmarksAndHeadbars::AddHitmark
    const val INPUT_INPUT_ONKEYDOWNINNER = 0x00aa5ae0L          //948-5 (was 0x00aa63a0 in 948-2-2) — jag::input::Input::OnKeyDownInner
    const val INPUT_INPUT_ONKEYUPINNER = 0x00aa5580L            //948-5 (was 0x00aa5e40 in 948-2-2) — jag::input::Input::OnKeyUpInner
    const val SCRIPTRUNNER_EXECUTEHOOKINNER = 0x00317320L       //948-5 (was 0x00317130 in 948-2-2) — jag::ScriptRunner::ExecuteHookInner
    const val SCRIPTRUNNER_EXECUTESCRIPT = 0x00315920L          //948-5 (was 0x00315730 in 948-2-2) — jag::ScriptRunner::ExecuteScript
    const val SCRIPTRUNNER_GETCLIENTSCRIPTSTATE = 0x00307710L   //948-5 (was 0x00307520 in 948-2-2) — jag::ScriptRunner::GetClientScriptState
    const val SCRIPTRUNNER_CONTINUESCRIPT = 0x00315510L         //948-5 (was 0x00315320 in 948-2-2) — jag::ScriptRunner::ContinueScript
    const val SCRIPTRUNNER_GET_BY_ID = 0x003163f0L              //948-5 (was 0x00316200 in 948-2-2) — jag::ClientScriptHelpers::GetByID
    const val INPUT_INPUT_ONMOUSEMOTION = 0x00ab40c0L           //948-5 (was 0x00ab4980 in 948-2-2) — jag::input::Input::OnMouseMotion
    const val INPUT_INPUT_ONLEFTBUTTONDOWN = 0x00ab50d0L        //948-5 (was 0x00ab5990 in 948-2-2) — AMBIGUOUS; sig-scan distinguished via leftButtonState write = 1
    const val INPUT_INPUT_ONLEFTBUTTONUP = 0x00ab4cd0L          //948-5 (was 0x00ab5590 in 948-2-2) — AMBIGUOUS; sig-scan distinguished via leftButtonState write = 0
    const val INPUT_INPUT_ONSCROLLWHEEL = 0x00ab3840L           //948-5 (was 0x00ab4100 in 948-2-2) — jag::input::Input::OnScrollWheel
    const val INPUT_INPUT_INJECTSYNTHETICRIGHTCLICK = 0x00ab3640L //948-5 (was 0x00ab3f00 in 948-2-2) — jag::input::Input::InjectSyntheticRightClick
    const val TCPCONNECTIONMESSAGE_INIT = 0x001220a0L           //948-5 (was 0x00121f20 in 948-2-2) — AMBIGUOUS; outgoing, called from CreatePacket only (sig-scan via caller)
    const val TCPCONNECTIONMESSAGE_INIT_INCOMING = 0x001ab7a0L  //948-5 (was 0x001ab5e0 in 948-2-2) — AMBIGUOUS; incoming, called from TcpIn after payload read (sig-scan via caller)
    const val SENDCLIENTMESSAGE = 0x00c2b9c0L                   //948-5 (was 0x00c2b6b0 in 948-2-2) — AMBIGUOUS; sig-scan via caller set (~80 general senders) vs login-only sibling FUN_00c2b900
    const val REBUILD_NORMAL_HANDLER = 0x001203e0L              //948-5 (was 0x00120260 in 948-2-2) — REBUILD_NORMAL
    const val BUILD_AREA_INIT = 0x006d8be0L                     //948-5 (was 0x00678740 in 948-2-2) — jag::game::RebuildSceneEntry::Reset
    const val INTERFACEMANAGER_DRAWSLOTCHILDREN = 0x002b3210L   //948-5 (was 0x002b3020 in 948-2-2) — jag::InterfaceManager::DrawSlotChildren
    const val CLIENTPROT_SENDEVENTMOUSECLICK = 0x00180730L      //948-5 (was 0x001805b0 in 948-2-2) — jag::ClientProt::SendEventMouseClick
    const val CLIENTERROR_REPORTERROR = 0x006ef050L            //948-5 — jag::game::ClientError::ReportError (single crash/error telemetry funnel → libcurl POST to nxtclienterror.ws)
    // Raw socket byte funnels (sig: ulong(this, buf, len)). Verified 948-5: Read called from
    // TcpIn/ReadBytes/MainLogic; Write from FlushClientMessages/MainLogic. Used by the raw login
    // dump (RawLoginDump) to capture the RSA/handshake bytes the decoded prot hooks can't see.
    const val CLIENTSTREAM_READ = 0x0090d060L                  //948-5 — jag::ClientStream::Read (socket→buffer)
    const val CLIENTSTREAM_WRITE = 0x0090c870L                 //948-5 — jag::ClientStream::Write (buffer→socket)
}

object OClient {
    const val CLIENT_CYCLE = 0x500L                                 //947-3
    const val CLIENT_RENDER_CYCLE = 0x508L                          //947-3
    const val SDL_MANAGER = 0x18680L                                //947-3
    const val BUILD_AREA_VECTOR = 0x193d8L                          //947-3 EASTL vector of 0x68-byte build area entries
    const val CAMERA = 0x19420L                                     //948-5 jag::game::Camera (was mislabeled OBJECT_MANAGER; confirmed via manager_class consensus, 5-7 callsites)
    const val CONNECTION_MANAGER = 0x19440L                         //947-3
    const val HINTTRAIL_LIST = 0x19480L                             //946-2 (unconfirmed, unused)
    const val INTERFACE_LIST = 0x19488L                             //947-3
    const val MAINLOGIC_MANAGER = 0x194a8L                          //947-3
    const val NPC_MANAGER = 0x194b8L                                //947-3
    const val ITEMSTACK_LIST = 0x194c0L                             //947-3
    const val PLAYER_MANAGER = 0x194d8L                             //947-3
    const val PROJECTILE_LIST = 0x194e8L                            //947-3
    const val SPOTANIM_MANAGER = 0x19510L                           //947-3
    const val INVENTORY_MANAGER = 0x19550L                          //947-3
    const val SCENE_MANAGER = 0x19558L                              //947-3
    const val MAIN_STATE = 0x19b28L                                 //947-3
    const val LOGGED_IN_PLAYER = 0x19b30L                           //947-3
    const val PLAYER_VAR_DOMAIN = 0x19b40L                          //947-3
}

object OSDLManager {
    const val WINDOW = 0x90L
    const val SDL_WINDOW = 0x28L
    const val WINDOW_FLAGS_BITFIELD = 0x40L
}

object OSDLWindow { //https://github.com/libsdl-org/SDL/blob/dd4f5df8242b36ab61f843a5e3fc8cf024156514/src/video/SDL_sysvideo.h#L45
    const val WINDOW_NAME = 0x10L
    const val WINDOW_DATA = 0xD8L
}

object OSDLWindowData { //https://github.com/libsdl-org/SDL/blob/dd4f5df8242b36ab61f843a5e3fc8cf024156514/src/video/x11/SDL_x11window.h#L43

}

object OItemStackList {
    const val RB_TREE_BASE = 0x18L
}

object OItemStackNode {
    const val LEFT = 0x0L
    const val RIGHT = 0x8L
    const val PARENT = 0x10L
    const val POS_PLANE = 0x20L
    const val POS_X = 0x24L
    const val POS_Y = 0x28L
    const val ITEM_STACK = 0x38L
}

object OItemStack {
    const val ITEM_VECTOR = 0x70L
    const val ITEM_VECTOR_ELEM_SIZE = 0x90L
    const val ITEM_ID = 0x0L
    const val ITEM_AMOUNT = 0x4L
}

object OProjectileList {
    const val SIZE = 0x50L
}

object OProjectile {
    const val ID = 0x60L
    const val LOCKON_SERVER_INDEX = 0x70L
}

object OInterfaceManager {
    const val INTERFACE_LIST_PTR = 0x30L
}

object OInterfaceList {
    const val START_PTR = 0x38L
    const val END_PTR = 0x40L
    const val CAPACITY_PTR = 0x48L
    const val PARENT_OFFSET = 0x08L
}

object OInterfaceParent {
    // EASTL vector triple at parent+0x8: { begin, end, capacity }. Each element is 0x18 bytes:
    // byte 0 = "removed" flag (0 = live slot), bytes 0x10..0x17 = shared_ptr value (the
    // InterfaceComponent pointer). Derived from jag::InterfaceManager::DrawSlotChildren
    // @ 0x00428480 — the loop reads *(char*)(elem+0) for the live check and *(void**)(elem+0x10)
    // for the component pointer, stepping by 0x18 from begin to end.
    const val CHILD_ARRAY_BEGIN = 0x8L
    const val CHILD_ARRAY_END = 0x10L
    const val CHILD_SLOT_STRIDE = 0x18L
    const val CHILD_SLOT_REMOVED_FLAG = 0x0L
    const val CHILD_SLOT_PTR = 0x10L
}

object OInterfaceComponent {
    const val INTERFACE_ID = 0x8L
    const val COMPONENT_ID = 0xAL
    const val SLOT_ID = 0xCL
    const val COMPONENT_TYPE = 0xEL
    const val PARENT_SHAREDPTR = 0x10L
    // Bitfield. Bit 0x20 = visible flag (set/cleared by jag::game::InterfaceComponent::SetVisibleFlag @ 0x00216600).
    // Other bits (e.g. 0x04, 0x08) are not yet decoded.
    const val FLAGS = 0x31L
    const val X_ORIGIN_MODE = 0x32L      // byte: 0=use X_ORIGIN_OFFSET, 1=center (-w/2), 2=right-anchor (-w)
    const val Y_ORIGIN_MODE = 0x33L      // byte: same semantics as X_ORIGIN_MODE for vertical axis
    const val X_ANCHOR_MODE = 0x34L      // byte: 0=absolute, 1=parent-w-minus, 2=fraction-of-parent-w (read by Layer::LayoutChild @ 0x004bda10)
    const val Y_ANCHOR_MODE = 0x35L      // byte: same semantics as X_ANCHOR_MODE for vertical axis
    const val X_ORIGIN_OFFSET = 0x38L    // int: applied when X_ORIGIN_MODE == 0
    const val Y_ORIGIN_OFFSET = 0x3CL    // int: applied when Y_ORIGIN_MODE == 0
    const val RAW_X = 0x40L              // int: unanchored X input passed through anchor-mode formula
    const val RAW_Y = 0x44L              // int: unanchored Y input
    // Parent-LOCAL position written each frame by the layout pass (Layer::LayoutChildren -> Layer::LayoutChild).
    // NOT screen-absolute. Absolute X/Y is reconstructed each frame in jag::InterfaceManager::DrawSlotChildren by adding
    // the slot's screen-absolute origin to these values; that data is ephemeral and only available via the
    // INTERFACEMANAGER_DRAWSLOTCHILDREN hook (see hooks.impl.InterfaceComponentRectCapture).
    const val PARENT_REL_X = 0x50L       // int: parent-local X within parent layer's interior coord system
    const val PARENT_REL_Y = 0x54L       // int: parent-local Y
    const val SCREEN_WIDTH = 0x58L       // int: width in pixels
    const val SCREEN_HEIGHT = 0x5CL      // int: height in pixels
    const val TEXT = 0x160L
    const val SPRITE_ID = 0x168L
    const val ITEM_ID = 0x180L
    const val STACK_SIZE = 0x188L
    // A component's child components are NOT a single vector at a fixed offset — a Layer stores
    // them in a paged array at +0x1A0 (begin) / +0x1A8 (end), 0x68-byte page entries (see
    // jag::game::Layer draw FUN_003eccd0). The canonical, flat way to reach every component is
    // InterfaceParent's slot vector at +0x8, indexed by component id (InterfaceParent.get / the
    // InterfaceList.getComponent path). There is no per-component CHILDREN vector; the old
    // CHILDREN=0x178 was bogus (it overlapped ITEM_ID/STACK_SIZE and read them as a vector header).
    const val SLOT_CHILDREN = 0x1A8L //vector<0x18>
}

object OLoggedInPlayer {
    const val PLAYER_RIGHTS = 0x8L
    const val PLAYER_INDEX = 0x48L
    const val PLAYER_NAME = 0x68L
    const val TARGET_INDEX = 0x98L
    const val TARGET_TYPE = 0x9AL
}

object OPlayerManager {
    const val PLAYER_LIST = 0x10L
    const val PLAYER_LIST_NODE_ENTITY = 0x30L
}

object ONPCManager {
    const val HASH_TABLE = 0x10L
    const val LOCAL_NPC_INDICES = 0xA0A0L
}

object OSceneManager {
    const val WORLD_ARRAY = 0x58L
    const val CURRENT_WORLD_INDEX = 0x70L
}

object OWorld {
    const val VIEW_MATRIX = 0x130e0L
    const val PROJECTION_MATRIX = 0x131e0L

    const val MAPSQUARE_X_OFFSET = 0x13fb4L                    //948-2 (was 0x13c14 in 947-3, +0x3a0 shift) — verified
    const val MAPSQUARE_Y_OFFSET = 0x13fb8L                    //948-2 (was 0x13c18 in 947-3, +0x3a0 shift) — verified
    const val MAPSQUARES_VECTOR = 0x14000L                     //948-2 (was 0x13c60 in 947-3, +0x3a0 shift) — verified
    const val ROOT_GRAPH_NODE = 0x10150L                       //948-2 (was 0x10110 which pointed at AABB float-init area, not a GraphNode ptr)
    const val HEIGHT_MAP = 0x14038L                            //948-2 (was 0x13c98 in 947-3, +0x3a0 shift) — embedded HeightMap; pass &World[HEIGHT_MAP] to game::HeightMap::GetFineHeight
    const val LINK_MAP = 0x14048L                              //948-2 (was 0x13ca8 in 947-3, +0x3a0 shift) — embedded LinkMap; pass &World[LINK_MAP] to game::LinkMap::HasBridgeFlag
}

object OMapSquare {
    // 948-2-2: total size 0xED50 (60,752) bytes.
    //
    // Scene-object enumeration in 948-2-2 differs from 947-3: the inline LocationContainers
    // (50 of them at MS+0x270..+0xEB30) are NOT Entity-derived — their +0x8 holds a refcount,
    // not a GraphNode pointer. The 947-3 "LC.graphNode.sceneObjects" path cannot work on these.
    // Live iteration goes MS+LOCATION_CONTAINERS vector → LC → PRIMARY/MULTI/TERTIARY/DYNAMIC
    // slot vectors (the same path UPDATE_ZONE_FULL_FOLLOWS and the renderer use). The outer
    // vector triple at MS+0xEB68 has stride 0x18, the inner vector has stride 8 (raw LC
    // pointers, NOT shared_ptrs). chunkSize comes from CHUNK_SIZE_FLAG below.
    const val MAPSQUARE_X = 0x40L                              //948-2 (was 0x5C in 947-3)
    const val CHUNK_SIZE_FLAG = 0x70L                          //948-2 qword. == 0 → chunkSize=16, ≥1 → chunkSize=8. Read by GetLocationContainerAt @ 0x0041f8b0.
    const val MAPSQUARE_Y = 0x9CL                              //948-2 (was 0x60 in 947-3)
    const val COMPOSITE_DATA_COUNTER = 0xA8L                   //948-2 (was 0x68 in 947-3) — shared_ptr counterPtr for ClientCompositeData
    const val COMPOSITE_DATA = 0xB0L                           //948-2 (was 0x70 in 947-3) — shared_ptr ptr → ClientCompositeData
    const val LOCATION_CONTAINERS = 0xEB68L                    //948-2 (was 0x260 in 947-3) — outer 2D vector triple of LocationContainer pointers. Stride 0x18 outer, 0x8 inner.
}

// 948-2 LocationContainer layout — 0x2E0 bytes. The inline LCs (MS+0x270..+0xEB30) are NOT
// Entity-derived (refcount at +0x8, not GraphNode), so LC.graphNode.sceneObjects from 947-3
// does not apply. The four vectors below ARE the scene-object source — they hold the same
// data the binary's UPDATE_ZONE_FULL_FOLLOWS and renderer iterate. LC+0xC8 (PRIMARY) is the
// direct equivalent of 947-3's `SPAWNED_LOCATIONS_VECTOR`. Entry stride is 16 bytes per
// {shared_ptr ctrl, Location*} pair; the Location pointer lives at entry+8.
object OLocationContainer {
    const val ENTRY_STRIDE = 0x10L                              // 16 bytes per shared_ptr-style entry
    const val ENTRY_LOCATION_PTR_OFFSET = 0x8L                  // MapSquareLocation* at entry+0x8

    const val PRIMARY_LOCATIONS_BEGIN = 0xC8L                   // main scene-object list begin ptr
    const val PRIMARY_LOCATIONS_END = 0xD0L
    const val MULTI_LOCATIONS_BEGIN = 0xB0L                     // multi-tile span entries begin ptr
    const val MULTI_LOCATIONS_END = 0xB8L
    const val TERTIARY_LOCATIONS_BEGIN = 0xE0L                  // animated scenery / extras begin ptr
    const val TERTIARY_LOCATIONS_END = 0xE8L
    const val DYNAMIC_LOCATIONS_BEGIN = 0x60L                   // LOC_ADD-spawned locations begin ptr
    const val DYNAMIC_LOCATIONS_END = 0x68L

    const val STATIC_LOCATIONS_RBTREE_SENTINEL = 0x8L           // rbtree sentinel offset
    const val STATIC_LOCATIONS_RBTREE_BEGIN = 0x10L             // leftmost node ptr offset

    const val DISPATCH_SLOT_TABLE_START = 0xF8L                 // 5 sub-slots × 0x48 stride (composite-location dispatch)
    const val DISPATCH_SLOT_STRIDE = 0x48L
    const val DISPATCH_SLOT_COUNT = 5
}

object OLocation {
    const val RENDER_NODE_CTRL = 0x58L   // shared_ptr control block for render node
    const val RENDER_NODE = 0x60L        // Render node object ptr (same layout as ORenderModel: transform@+0x30, meshComponents@+0x288)
    const val VISIBLE_TYPE = 0xC8L
    const val ORIGINAL_TYPE = 0xD8L
    const val SHAPE = 0x120L
    const val ROTATION = 0x121L
    const val POS_X = 0x16CL
    const val POS_Y = 0x170L
    const val IS_DELETED = 0x18DL
    const val IS_HIDDEN = 0x18EL
    const val TYPE_ID = 0x1A8L
    // Highlight intensity (float). Per-frame update function FUN_005e4ce0 writes this from its
    // float parameter when its conditions allow. The renderer reads this; if it's 0.0f no
    // highlight is drawn. We write 1.0f to override.
    const val HIGHLIGHT_INTENSITY = 0x31CL
    // Highlight category index (int). FUN_005e4ce0 writes from its int parameter. Index into
    // g_highlightCategoryTable @ 0x013a3de0 — 0 = IMPORTANT.
    const val HIGHLIGHT_CATEGORY = 0x324L
    // Boolean (byte) — "show as important" flag. Verified in
    // jag::opcode::highlight_set_loc_show_as_important @ 0x00269200 (writes `*(bool *)(this+0x32c) = bVar5`
    // after entityType check `this[0x10] == 0`). On its own NOT sufficient — the renderer also
    // needs HIGHLIGHT_INTENSITY and HIGHLIGHT_CATEGORY set, which the binary writes via FUN_005e4ce0
    // (a per-Location vtable method) only when its own conditions pass. We bypass by writing all
    // three fields directly.
    const val SHOW_AS_IMPORTANT = 0x32CL
}

object OCombinedLocation {
    const val LOCATION_DATA_VECTOR = 0xA0L
    const val VECTOR_ELEMENT_LOC_SHAREDPTR = 0xA0L
    const val RENDER_MODEL = 0x958L              // Render model ptr (same layout as ORenderModel: transform@+0x30, meshComponents@+0x288)
}

object OCombinedLocationSection {
    const val TYPE_SHAREDPTR = 0xA8L //also 0xB0 if A8 is wrong transform in some situations?
    const val HIDDEN = 0xF2L
    const val SHAPE = 0xB8L
    const val ROTATION = 0xB9L
    const val POS_X = 0x9CL
    const val POS_Y = 0xA0L
    const val RENDER_NODE_CTRL = 0x268L          // shared_ptr control block for render node (used when no parent CombinedLocation)
    const val RENDER_NODE = 0x270L               // Render node data ptr (same layout as ORenderModel: transform@+0x30, meshComponents@+0x288)
    const val PARENT_COMBINED_LOCATION = 0x330L  // ptr to parent CombinedLocation; when non-null, use parent's render model at OCombinedLocation.RENDER_MODEL instead
}

object OLocationType {
    const val ID = 0x8L
    // Footprint size in tiles. Verified against Ghidra LocType@0x37C/0x380 (DecodeType opcodes
    // 14/15) and the runtime readers LocType::GetHillData and Location build (FUN_0062b2e0), which
    // both read `*(int*)(typePtr + 0x37C)`/`+0x380` from the same LocType pointer the engine holds.
    const val SIZE_X = 0x37CL
    const val SIZE_Y = 0x380L
}

object OSpotAnimManager {
    const val VTABLE = 0x0L
    const val CLIENT_REF = 0x8L

    const val FIRST_NODE = 0x10L //TODO most likely eastl::list or linkedlist can be abstracted
    const val POOL_START = 0x30L
    const val POOL_END = 0x48L
    const val LIST_SIZE = 0x50L
}

object OSpotAnimNode {
    const val NEXT = 0x0L
    const val VALUE = 0x10L
}

object OPlayerVarDomain {
    const val HASH_TABLE = 0x1C0D0L
    const val VAR_ID = 0x0L
    const val VAR_VALUE = 0x8L
}

object OClientVarDomain {
    const val HASH_TABLE = 0x18L
    const val VAR_ID = 0x0L
    const val VAR_VALUE = 0x8L
}

object OMiniMenuEntry {
    const val TARGET_STRING = 0x0L
    const val ACTION_STRING = 0x18L
    const val HIGHLIGHT_TYPE = 0x30L
    const val ACTION = 0x38L
    const val UNK_1 = 0x40L
    const val ITEM_ID = 0x44L
    const val PARAM_1 = 0x48L
    const val PARAM_2 = 0x4CL
    const val PARAM_3 = 0x50L
    const val TARGETED_ENTITY = 0x120L
}

object OMiniMenuAction {
    const val ACTION_VTABLE = 0x0L
    const val VTABLE_ACTION_SEND_FUNCTION = 0x18L
    const val ACTION_ID = 0x20L
}

object OMainLogicManager {
    const val STAT_TABLE = 0x73a0L
    const val CLIENT_VAR_DOMAIN = 0x73a8L

    const val CLICK_BUFFER_BASE = 0x4590L
    const val CLICK_BUFFER_TAIL = 0x4d60L
    const val CLICK_BUFFER_HEAD = 0x4d64L
    const val CLICK_BUFFER_CAPACITY = 50
    const val CLICK_ENTRY_SIZE = 0x28L
}

object OMouseClickEntry {
    const val BUTTON_FLAG = 0x0L
    const val Y = 0x4L
    const val X = 0x8L
    const val TIMESTAMP_MS = 0x18L
}

object OStatTable {
    const val TABLE_SIZE = 0x8L
    const val TABLE_BEGIN = 0x10L
    const val ENTRY_SIZE = 0x18L
}

object OStat {
    const val INFO_PTR = 0x0L
    const val UNKINT32_0 = 0x8L
    const val EXPERIENCE = 0xcL
    const val REAL_LEVEL = 0x10L
    const val CURRENT_LEVEL = 0x14L
}

object OInventory {
    const val INVENTORY_ID = 0x8L
    const val INVENTORY_ITEMS = 0x10L
    const val OBJ_VAR_DOMAINS = 0x28L
}

object OGraphNode {
    const val DIRECTION_X = 0x0L
    const val DIRECTION_Y = 0x8L
    const val DIRECTION_Z = 0x10L
    // Current transformed AABB (axis-aligned bounding box)
    // Stored as two SSE-aligned float4 vectors in raw {X, Z, Y} memory order
    // (same axis layout as scene position at 0xF0/0xF4/0xF8)
    const val BOUNDS_MIN_X = 0x40L
    const val BOUNDS_MIN_Z = 0x44L  // height axis (raw component 1)
    const val BOUNDS_MIN_Y = 0x48L  // depth axis (raw component 2)
    // 0x4C = min_w padding (unused)
    const val BOUNDS_MAX_X = 0x50L
    const val BOUNDS_MAX_Z = 0x54L  // height axis (raw component 1)
    const val BOUNDS_MAX_Y = 0x58L  // depth axis (raw component 2)
    // 0x5C = marker (-1.0)
    // Source/untransformed AABB (same {X, Z, Y} layout)
    const val SRC_BOUNDS_MIN_X = 0x60L
    const val SRC_BOUNDS_MIN_Z = 0x64L  // height axis
    const val SRC_BOUNDS_MIN_Y = 0x68L  // depth axis
    // 0x6C = src min_w padding
    const val SRC_BOUNDS_MAX_X = 0x70L
    const val SRC_BOUNDS_MAX_Z = 0x74L  // height axis
    const val SRC_BOUNDS_MAX_Y = 0x78L  // depth axis
    // 0x7C = src marker
    // Local position (before parent accumulation)
    const val LOCAL_X = 0xD0L
    const val LOCAL_Z = 0xD4L
    const val LOCAL_Y = 0xD8L
    const val DEPTH = 0xDCL
    // Scene position (accumulated world position = local + parent chain)
    const val SCENE_X = 0xF0L
    const val SCENE_Y = 0xF8L
    const val SCENE_Z = 0xF4L
    const val FLAGS = 0xFCL
    const val CHILDREN = 0x138L
    const val ENTITY = 0x1A0L
    const val FRAME_COUNT = 0x1ACL
}

object OEntity {
    const val GRAPH_NODE = 0x8L
    const val ENTITY_TYPE = 0x10L
    // Screen-space picking data (populated by render pipeline, used by ScenePicker::TestEntityHit)
    const val PICK_TYPE = 0x14L           // int: 0=point/circle, non-zero=line segment
    const val SCREEN_X1 = 0x18L           // int: line endpoint 1 X (line mode)
    const val SCREEN_Y1 = 0x1CL           // int: line endpoint 1 Y (line mode)
    const val SCREEN_X2 = 0x20L           // int: line endpoint 2 X (line mode)
    const val SCREEN_Y2 = 0x24L           // int: line endpoint 2 Y (line mode)
    const val LINE_RADIUS = 0x28L         // int: picking radius (line mode)
    const val SCREEN_CENTER_X = 0x30L     // int: screen center X (point mode)
    const val SCREEN_CENTER_Y = 0x34L     // int: screen center Y (point mode)
    const val POINT_RADIUS = 0x38L        // int: picking radius (point mode)
    const val ENTITY_PLANE = 0x40L
    const val SIZE = 0x184L
    const val ANIMATION_ID = 0xA88L
    const val ANIMATION_SHARED_PTR = 0xAA0L
    // Render model data (PathingEntity ONLY - NPC/Player, NOT Location)
    // Location entities use OLocation.RENDER_NODE (0x60) instead, which has the same ORenderModel layout
    const val RENDER_MODEL = 0xC58L         // ptr: render model data for mesh projection/picking (PathingEntity only)
    // Avatar-lifecycle witnesses (decode -> compose -> bind -> drift) used to pin the stage a local
    // avatar stalls at. Mac rs2client 948-5 vmaddrs cited; the byte offsets match the Linux layout.
    const val BODY_TYPE_MODEL = 0x1068L     // ptr: body/skeleton model; nonzero => body composed. Gate read by jag::game::PlayerAvatar::ProcessPendingAppearance @0x10002ba20
    const val DECODE_WITNESS = 0x10ACL      // u32: title/prefix sprite written by jag::game::PlayerAvatar::DecodeAppearance @0x100031480 (tracks whether decode ran)
    const val MAP_SQUARE_BIND = 0x0AA8L     // ptr: bound map square; nonzero => bound to a loaded square. Read/gated by jag::graphics::GraphEntity::AdvanceRenderPosition @0x1003a2500
    const val LERP_END_TICK = 0x0DBCL       // i32: lerp end-tick (-1 => no active waypoint = open-loop drift fallback). Written by jag::graphics::GraphEntity::AdvanceRenderPosition @0x1003a2500
    // PlayerAppearancePending* built by op22 ext-info (Construct @0x100034d40); consumed per-tick by
    // ProcessPendingAppearance @0x10002ba20, which composes RENDER_MODEL ONLY once the object's async-load
    // gate passes. Gate fields on the pending object: +0x88 needsAsyncLoad, +0x8a composed.
    const val PENDING_APPEARANCE = 0x1298L  // ptr: queued appearance awaiting async resource-group load before model compose
    const val CURRENT_APPEARANCE = 0x12A0L  // ptr: the composed/active appearance (set after PENDING_APPEARANCE's gate clears)
}

object ORenderModel {
    const val WORLD_TRANSFORM = 0x30L       // float[16]: 4x4 model-to-world transform matrix
    const val MESH_COMPONENTS = 0x288L      // ptr -> EASTL vector<ptr> of mesh components
}

object OMeshComponent {
    const val MESH_DATA = 0x20L             // ptr: mesh data (vertices, indices, etc.)
    const val BONE_COUNT = 0x2F0L           // ulong: number of bones for skinning
    const val BONE_MATRICES = 0x2F8L        // ptr: array of 3x4 column-major bone matrices (12 floats each)
    const val BONE_ANIM_CHECK = 0x350L      // ptr: non-null when bone animation data is active
}

object OMeshData {
    const val VERTEX_SCALE = 0x2CL          // float: dequantization scale for int16 vertices (GPU's uVertexScale)
    const val INDEX_BUFFER = 0xB8L          // ptr: uint16 index buffer start (CPU-resident)
    const val INDEX_BUFFER_END = 0xC0L      // ptr: uint16 index buffer end (CPU-resident)
    const val VERTEX_POSITIONS = 0x118L     // ptr: float4 vertex positions stride=0x10 (GPU-only, NULL for skinned)
    const val SKINNING_DATA = 0x138L        // ptr/count: non-null = skinned mesh (controls skinned vs unskinned path)
    const val SKINNED_VERTEX_DATA = 0x140L  // ptr: short3+bone_label per vertex stride=8 (CPU-resident, always valid for skinned)
    const val VERTEX_WEIGHTS = 0x148L       // ptr: per-vertex bone/weight data (8 bytes/vertex, CPU-resident)
}

object OAnimation {
    const val ID = 0x0L
    const val CURRENT_FRAME = 0x24L
}

object ONPC {
    const val FRAMES_ALIVE = 0x910L
    const val RENDER_ANIM = 0x958L
    const val ID = 0x1060L
    const val TYPE_ID = 0x1064L
    const val CURRENT_HP = 0x112CL
    const val MAX_HP = 0x1148L
    const val HIDDEN_MENUOP_FLAGS = 0x115CL
    // Boolean (byte) — "show as important" flag the binary's renderer reads to apply the IMPORTANT
    // category highlight color from g_highlightCategoryTable[0]. Verified in
    // jag::opcode::highlight_set_npc_show_as_important @ 0x00269280 (writes `*(bool *)(this+0x1100) = bVar5`
    // after entityType check `this[0x10] == 1`).
    const val SHOW_AS_IMPORTANT = 0x1100L
}

object ONPCType {
    const val ID = 0x4L
}

object OPathingEntity {
    const val SERVER_INDEX = 0x88L
    const val NAME = 0x90L
    const val INTERACTING_NPC_SID = 0x1B4L
    const val ROUTE_WAYPOINT_MANAGER = 0x268L
    const val WAYPOINT_COUNT = 0x30L
    const val LAST_MOVESPEED = 0x98L //UInt32*
    const val NEXT_X_OFFSET_FINE = 0xA0L //float
    const val NEXT_Y_OFFSET_FINE = 0xA8L //float
    const val HITMARKS_AND_HEADBARS = 0xef8L
}

object OHitmarksAndHeadbars {
    const val HIT_VECTOR = 0x20L
    const val HEADBAR_LINKEDLIST_VECTOR_START = 0x28L
}

object OHit {
    const val TYPE = 0x0L
    const val DAMAGE = 0x4L
    const val CLIENTCYCLE_CREATED = 0x8L //uint32
    const val UNKNEG1_1 = 0xCL
    const val UNKNEG1_2 = 0x10L
    const val DURATION_CLIENTCYLES = 0x14L
}

object OHeadbar {
    const val TYPE_PTR = 0x8L
    const val TYPE_ID = 0x8L
    const val CLIENTCYCLE_CREATED = 0x20L //uint32
    const val FROM_FILL = 0x24L
    const val ZERO = 0x28L
    const val TO_FILL = 0x2CL
    const val ZERO2 = 0x30L
    const val DURATION_CLIENTCYCLES = 0x34L
}

object OHitmark

object OSpotAnim {
    const val ID = 0x74L
    const val CREATED_CLIENTCYCLE = 0x18CL
}

object OHintArrow {
    // HintArrow entity is 0x98 bytes total (FUN_0016b500 @ 0x0016b500, alloc size 0x98).
    // Entity type 0xd written at +0x10 (OEntity.ENTITY_TYPE).
    const val TARGET_ENTITY_SHARED_PTR = 0x88L                // null shared_ptr {ctrl=0, ptr=0} at +0x88..+0x97
    const val TARGET_POS_VEC3 = 0x70L                         // 16-byte inline Vec3 at +0x70
}

object OHintTrailList {
    const val VECTOR_BEGIN = 0x18L
    const val VECTOR_END = 0x20L
    const val VECTOR_CAPACITY = 0x28L
}

object OServerConnection {
    const val CLIENT_STREAM = 0x08L   // ptr to jag::ClientStream (the raw TCP socket wrapper)
    const val CURRENT_OPCODE = 0x2CL
    const val RESOLVED_SIZE = 0x30L
    const val ISAAC_PTR = 0x2B8L
    const val PACKET_BASE = 0x2C0L
    const val BUF_DATA = 0x2D0L
    const val BUF_POS = 0x2D8L
}

object OConnectionManager {
    const val GAME_CONNECTION = 0x18L    // ServerConnection* (active when LOGGED_IN / mainState 30)
    const val LOGIN_CONNECTION = 0x28L   // ServerConnection* (active during login states)
}