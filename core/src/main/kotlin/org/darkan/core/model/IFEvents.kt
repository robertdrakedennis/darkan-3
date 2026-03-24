package org.darkan.core.model

/**
 * Bitfield builder for IF_SETEVENTS settings.
 * Each bit controls what interactions the client allows on an interface component's slots.
 * Verified against rs2client rev 947 binary — all bit positions confirmed from decompilation.
 *
 * Layout:
 *   Bit 0        = continue button
 *   Bits 1..10   = right-click options 0..9
 *   Bits 11..17  = use-on target flags (ground item, npc, loc, player, self, component, tile)
 *   Bits 18..20  = depth (0..7) — parent layers to traverse for drag target
 *   Bit 21       = drag enabled
 *   Bit 22       = use-targetable (component can be targeted by use-on)
 *   Bit 23       = ignore depth (skip layer traversal for drag)
 *   Bit 24       = allow target send (enables sending 3D target actions to server)
 *   Bits 25..31  = unused (no client code reads these)
 */
class IFEvents(
    val interfaceId: Int,
    val componentId: Int,
    val fromSlot: Int = 0,
    val toSlot: Int = 0,
    var settings: Int = 0,
) {
    companion object {
        /**
         * Decode from raw 12-byte IF_SETEVENTS wire payload (rev 947 encoding).
         * Wire: [4B LE settings][2B LE fromSlot][4B CDAB componentHash][2B +0x80 toSlot]
         */
        fun fromWire(data: ByteArray, offset: Int = 0): IFEvents {
            fun u8(i: Int) = data[offset + i].toInt() and 0xFF
            val settings = u8(0) or (u8(1) shl 8) or (u8(2) shl 16) or (u8(3) shl 24)
            var fromSlot = u8(4) or (u8(5) shl 8)
            if (fromSlot == 0xFFFF) fromSlot = -1
            // writeIntInverseMiddle writes [shr16, shr24, shr0, shr8] = [CC, AA, DD, BB]
            // bytes [6]=CC [7]=AA [8]=DD [9]=BB → reconstruct AABBCCDD
            val hash = (u8(7) shl 24) or (u8(6) shl 16) or (u8(9) shl 8) or u8(8)
            val interfaceId = hash ushr 16
            val componentId = hash and 0xFFFF
            var toSlot = ((u8(10) + 0x80) and 0xFF) or (u8(11) shl 8)
            if (toSlot == 0xFFFF) toSlot = -1
            return IFEvents(interfaceId, componentId, fromSlot, toSlot, settings)
        }
    }

    constructor(interfaceId: Int, componentId: Int, fromSlot: Int, toSlot: Int, init: IFEvents.() -> Unit)
        : this(interfaceId, componentId, fromSlot, toSlot, 0) { init() }

    // --- Continue button (bit 0) ---

    fun enableContinueButton() = apply { settings = settings or 0x1 }

    // --- Right-click options (bits 1..10) ---

    fun enableRightClickOption(id: Int) = apply {
        require(id in 0..9)
        settings = settings or (1 shl (id + 1))
    }

    fun enableRightClickOptions(vararg ids: Int) = apply { ids.forEach { enableRightClickOption(it) } }

    // --- Use-on target flags (bits 11..17) ---

    fun enableUseOption(flag: UseFlag) = apply { settings = settings or (flag.flag shl 11) }

    fun enableUseOptions(vararg flags: UseFlag) = apply { flags.forEach { enableUseOption(it) } }

    fun getUseOptionFlags(): Int = (settings shr 11) and 0x7f

    // --- Depth (bits 18..20) ---

    fun setDepth(depth: Int) = apply {
        settings = (settings and (0x7 shl 18).inv()) or ((depth and 0x7) shl 18)
    }

    fun getDepth(): Int = (settings shr 18) and 0x7

    // --- Drag (bit 21) ---

    fun enableDrag() = apply { settings = settings or (1 shl 21) }

    // --- Use-targetable (bit 22) ---

    fun enableUseTargetability() = apply { settings = settings or (1 shl 22) }

    // --- Ignore depth (bit 23) ---

    fun enableIgnoreDepth() = apply { settings = settings or (1 shl 23) }

    // --- Allow target send (bit 24) — NEW in modern client ---

    fun enableAllowTargetSend() = apply { settings = settings or (1 shl 24) }

    /**
     * Produces a copy-pasteable Kotlin builder expression.
     * e.g. `IFEvents(907, 39, 0, 0).enableRightClickOption(0).enableAllowTargetSend()`
     */
    override fun toString(): String {
        val sb = StringBuilder("IFEvents($interfaceId, $componentId")
        if (fromSlot != 0 || toSlot != 0) sb.append(", $fromSlot, $toSlot")
        sb.append(")")
        if (settings == 0) return sb.toString()

        if (settings and 0x1 != 0) sb.append(".enableContinueButton()")

        val ops = (0..9).filter { settings and (1 shl (it + 1)) != 0 }
        if (ops.size > 1) sb.append(".enableRightClickOptions(${ops.joinToString(", ")})")
        else if (ops.size == 1) sb.append(".enableRightClickOption(${ops[0]})")

        val useFlags = UseFlag.entries.filter { (settings shr 11) and it.flag != 0 }
        if (useFlags.size > 1) sb.append(".enableUseOptions(${useFlags.joinToString(", ") { "UseFlag.${it.name}" }})")
        else if (useFlags.size == 1) sb.append(".enableUseOption(UseFlag.${useFlags[0].name})")

        val depth = (settings shr 18) and 0x7
        if (depth != 0) sb.append(".setDepth($depth)")

        if (settings and (1 shl 21) != 0) sb.append(".enableDrag()")
        if (settings and (1 shl 22) != 0) sb.append(".enableUseTargetability()")
        if (settings and (1 shl 23) != 0) sb.append(".enableIgnoreDepth()")
        if (settings and (1 shl 24) != 0) sb.append(".enableAllowTargetSend()")

        return sb.toString()
    }
}

enum class UseFlag(val flag: Int) {
    GROUND_ITEM(0x1),
    NPC(0x2),
    WORLD_OBJECT(0x4),
    PLAYER(0x8),
    SELF(0x10),
    ICOMPONENT(0x20),
    WORLD_TILE(0x40);
}
