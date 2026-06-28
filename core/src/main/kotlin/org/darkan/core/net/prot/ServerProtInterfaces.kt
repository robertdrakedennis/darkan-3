package org.darkan.core.net.prot

import org.darkan.core.model.IFEvents

// --- Interfaces ---

/** IF_OPENTOP — rev948 op 39, 6B. Opens a top-level interface. */
data class IfOpenTop(val topLevelId: Int, val subId: Int = 0) : ServerProt

/** IF_SETTOPLEVELINTERFACE — rev948 op 3, 19B. Switches the active top-level interface. */
data class IfSetTopLevelInterface(val topLevelId: Int) : ServerProt

/** IF_OPENSUB — rev948 op 94, 8B. Opens a sub-interface inside a parent. */
data class IfOpenSub(val subId: Int, val walkable: Int, val parentHash: Int) : ServerProt

/** IF_SETPOSITION — rev948 op 82, 23B. Sets a component's layer/position descriptor. */
data class IfSetPosition(val componentId: Int, val layer: Int, val position: Int) : ServerProt

/** IF_CLOSESUB — rev948 op 62, 4B. Closes a sub-interface by component hash. */
data class IfCloseSub(val componentHash: Int) : ServerProt

/**
 * IF_MOVESUB is not mapped in the rev948 codec. [mode] is a tri-state operator:
 * 0x71 = close all subs under top, 0x7F = mark subs active, anything else = mark inactive.
 */
data class IfMoveSub(val topId: Int, val mode: Int) : ServerProt

/**
 * IF_SETEVENTS — rev948 op 35, 12B fixed. Sets the event mask for
 * a range of slots on an interface component. Use [IFEvents] to build the settings bitfield.
 */
data class IfSetEvents(val events: IFEvents) : ServerProt

/**
 * IF_SETEVENTS1 — rev948 op 97, 10B fixed. Distinct from IF_SETEVENTS: this variant
 * sends the events bitmask on the wire (vs IF_SETEVENTS2 hard-coding it to all-events).
 */
data class IfSetEvents1(
    val componentHash: Int,
    val eventsMask: Int,
    val endSlot: Int = -1,
    val startSlot: Int = -1,
) : ServerProt

/** IF_SETHIDE — rev948 op 91, 5B. Toggles a component's hidden state. */
data class IfSetHide(val componentHash: Int, val hide: Boolean) : ServerProt

/** IF_SETANGLE — rev948 op 4, 32B. Full angle/zoom/component packing. */
data class IfSetAngle(
    val componentId: Int,
    val angle1: Int,
    val angle3: Int,
    val colourIndex: Int,
    val angleZoom: Int,
    val packedAngle2: Int,
) : ServerProt

/** IF_SET_HTTP_IMAGE — rev948 op 152, varByte. */
data class IfSetHttpImage(val imageUrl: String) : ServerProt

// --- Interface property setters (A1 §2.1, SetComponentProperty) ---

/** IF_SETOBJECT_ACTIVE — rev948 op 101, 4B. */
data class IfSetObjectActive(val componentHash: Int) : ServerProt

/** IF_SETMODEL — rev948 op 102, 8B. */
data class IfSetModel(val value: Int, val componentHash: Int) : ServerProt

/** IF_SETANIM_ACTIVE — rev948 op 96, 4B. */
data class IfSetAnimActive(val componentHash: Int) : ServerProt

/** IF_SET_COMPONENT_PROPERTY_TYPE7 — rev948 op 115, 10B. Legacy class name kept for API stability. */
data class IfSetNpcHead(val scale: Int, val componentHash: Int, val partA: Int, val partB: Int) : ServerProt

/** IF_SETOBJECT — rev948 op 84, 10B. */
data class IfSetObject(val objectSlot: Int, val objectCount: Int, val componentHash: Int) : ServerProt

/**
 * Unknown SetComponentProperty propType-3 packet (op 86 in 948 / op 106 in 947, 10B).
 * Wire shape: componentHash (int, LE) + frame (short, BE) + animId (int, BE). Canonical name
 * UNKNOWN — this is NOT IF_SETANIM (the canonical IF_SETANIM is op103, see [IfSetAnim]). Renamed
 * from the misleading `IfSetAnim` (binary-verified 2026-06-25; op86 = SetComponentProperty
 * propType 3, distinct from op103 propType 5). Bound at op86 by Rev948ServerCodecsInterface.
 */
data class IfSetComponentProp3(val componentHash: Int, val frame: Int, val animId: Int) : ServerProt

/** IF_SETCOLOUR — rev948 op 32, 8B. */
data class IfSetColour(val colour24: Int, val componentHash: Int) : ServerProt

/** IF_SETOBJECT_SMALL — rev948 op 180, 5B. Value derived as `-2 - smallIdx` client-side. */
data class IfSetObjectSmall(val componentHash: Int, val smallIdx: Int) : ServerProt

/** IF_SETANIM_SMALL — rev948 op 136, 5B. Value derived as `-2 - smallIdx` client-side. */
data class IfSetAnimSmall(val smallIdx: Int, val componentHash: Int) : ServerProt

// --- Interface direct-update setters (A1 §2.2, CreateOrFindUpdateEntry) ---

/** IF_SETPLAYERHEAD_ACTIVE — rev948 op 8, 5B. flag=1 if rawByte == 0x01. */
data class IfSetPlayerHeadActive(val flag: Int, val componentHash: Int) : ServerProt

/** IF_SETRECOL — rev948 op 99, 6B. RGB-555 expanded client-side to 24-bit. */
data class IfSetRecol(val rgb555: Int, val componentHash: Int) : ServerProt

/**
 * IF_SETGRAPHIC (op 30 in 948 / op 53 in 947, 8B). update-type 0xd. value = graphicId/spriteId.
 * Was misnamed `IfSet2DAngle`: the handler the Ghidra DB labeled IF_SET2DANGLE actually decodes as
 * the official IF_SETGRAPHIC (binary-verified 2026-06-25, handler @0x00193f10). Bound at op30 by
 * Rev948ServerCodecsInterface.
 */
data class IfSetGraphic(val graphicId: Int, val componentHash: Int) : ServerProt

/**
 * IF_SET2DANGLE — rev948 op 30, 8B. Retained alias of the op30 packet under its legacy name; the
 * landed world HUD (`GameHud`) still emits this. NOTE: the rev948 codec binds [IfSetGraphic] (not
 * this class) at op30, so emitting `IfSet2DAngle` currently has no registered encoder — see the
 * migration note. Wire shape is identical (two ints): `angle` is the same field as `graphicId`.
 */
data class IfSet2DAngle(val angle: Int, val componentHash: Int) : ServerProt

/** IF_SET_MODEL_FRAME — rev948 op 38, 8B. */
data class IfSetModelFrame(val frame: Int, val componentHash: Int) : ServerProt

/** IF_SETNPCMODEL — rev948 op 59, 10B. npcId 0xFFFF means null. */
data class IfSetNpcModel(val componentHash: Int, val modelId: Int, val npcId: Int) : ServerProt

/** IF_SETMODELORIGIN — rev948 op 68, 10B. */
data class IfSetModelOrigin(val componentHash: Int, val x: Int, val y: Int, val z: Int) : ServerProt

/**
 * IF_SETANIM (op 103 in 948 / op 92 in 947, 8B). update-type 5. value = animationId (seq id).
 * Was misnamed `IfSetGraphic`: the handler the Ghidra DB labeled IF_SETGRAPHIC actually decodes as
 * the official IF_SETANIM (binary-verified 2026-06-25, handler @0x00193fe0). This is the canonical
 * IF_SETANIM; op86 (see [IfSetComponentProp3]) is a distinct SetComponentProperty propType-3 packet.
 * Bound at op103 by Rev948ServerCodecsInterface.
 */
data class IfSetAnim(val animationId: Int, val componentHash: Int) : ServerProt

/** IF_SETSPRITE — rev948 op 14, 8B. */
data class IfSetSprite(val componentHash: Int, val spriteValue: Int) : ServerProt

/** IF_SETSCROLLSIZE — rev948 op 158, 9B. */
data class IfSetScrollSize(
    val scrollW: Int,
    val scrollH: Int,
    val componentHash: Int,
    val subSlot: Int,
) : ServerProt

/** IF_SETNPCHEAD_ACTIVE — rev948 op 206, 5B. flag=1 if rawByte == 0x7F. */
data class IfSetNpcHeadActive(val flag: Int, val componentHash: Int) : ServerProt

/** IF_SETMODEL_COORD — rev948 op 165, 14B. */
data class IfSetModelCoord(val npcId: Int, val componentHash: Int, val part1: Int, val part2: Int) : ServerProt

/** IF_SETSCROLLPOS — rev948 op 179, 9B. */
data class IfSetScrollPos(
    val scrollY: Int,
    val componentHash: Int,
    val scrollX: Int,
    val subSlot: Int,
) : ServerProt

// --- Interface complex/direct-allocation setters (A1 §2.3) ---

/** IF_SETPLAYERMODEL_OTHER — rev948 op 70, 25B. Opaque payload until B4 unpacks. */
data class IfSetPlayerModelOther(val payload: ByteArray) : ServerProt {
    override fun equals(other: Any?): Boolean = this === other ||
        (other is IfSetPlayerModelOther && payload.contentEquals(other.payload))
    override fun hashCode(): Int = payload.contentHashCode()
}

/** IF_SETPLAYERMODEL_SELF — rev948 op 60, 25B. Opaque payload until B4. */
data class IfSetPlayerModelSelf(val payload: ByteArray) : ServerProt {
    override fun equals(other: Any?): Boolean = this === other ||
        (other is IfSetPlayerModelSelf && payload.contentEquals(other.payload))
    override fun hashCode(): Int = payload.contentHashCode()
}

/** IF_SETPLAYERMODEL_SNAPSHOT — rev948 op 118, 29B. Opaque until B4. */
data class IfSetPlayerModelSnapshot(val payload: ByteArray) : ServerProt {
    override fun equals(other: Any?): Boolean = this === other ||
        (other is IfSetPlayerModelSnapshot && payload.contentEquals(other.payload))
    override fun hashCode(): Int = payload.contentHashCode()
}

/** IF_SUBSWAP — rev948 op 40, 8B. Atomic close-A-then-open-B for sub-interface swap. */
data class IfSubSwap(val componentA: Int, val componentB: Int) : ServerProt

// --- Interface trigger / close variants (A1 §2.4) ---

/** IF_TRIGGER_CLOSE — rev948 op 123, 0B. Fires event 0x29 on current top-level. */
@JvmInline
value class IfTriggerClose(val dummy: Int = 0) : ServerProt

/** IF_CLOSESUB_BY_ID — rev948 op 148, 2B. Closes a sub by 16-bit id. */
data class IfCloseSubById(val id: Int) : ServerProt

/** IF_SETTEXT — rev948 op 122, varShort. Sets the text content of a component. */
data class IfSetText(val componentHash: Int, val text: String) : ServerProt

// NOTE: IF_OPENSUB_THUNK is intentionally not modelled; the active rev948 codec does not emit it.
