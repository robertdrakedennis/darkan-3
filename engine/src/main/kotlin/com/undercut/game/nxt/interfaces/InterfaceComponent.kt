package com.undercut.game.nxt.interfaces

import com.undercut.game.memory.NativeAccess.deref
import com.undercut.game.memory.NativeAccess.getOrNull
import com.undercut.game.memory.NativeAccess.pointerAtOffset
import com.undercut.game.memory.NativeAccess.readByte
import com.undercut.game.memory.NativeAccess.readInt
import com.undercut.game.memory.NativeAccess.readLong
import com.undercut.game.memory.NativeAccess.readShort
import com.undercut.game.memory.NativeAccess.toMemorySegment
import com.undercut.game.memory.NativeAccess.toShared
import com.undercut.game.hooks.impl.InterfaceComponentRectCapture
import com.undercut.game.memory.eastl.EastlString
import com.undercut.game.nxt.OInterfaceComponent
import com.undercut.game.nxt.OInterfaceComponent.ITEM_ID
import com.undercut.game.nxt.OInterfaceComponent.PARENT_REL_X
import com.undercut.game.nxt.OInterfaceComponent.PARENT_REL_Y
import com.undercut.game.nxt.OInterfaceComponent.SCREEN_HEIGHT
import com.undercut.game.nxt.OInterfaceComponent.SCREEN_WIDTH
import com.undercut.game.nxt.OInterfaceComponent.STACK_SIZE
import com.undercut.game.nxt.OInterfaceParent
import com.undercut.game.nxt.types.Vector
import java.lang.foreign.MemorySegment

class InterfaceComponent(val ptr: MemorySegment) {
    val interfaceId: Int
        get() = ptr.readShort(OInterfaceComponent.INTERFACE_ID).toInt()
    val componentId: Int
        get() = ptr.readShort(OInterfaceComponent.COMPONENT_ID).toInt()
    val spriteId : Int
        get() = ptr.readShort(OInterfaceComponent.SPRITE_ID).toInt()
    val slotId: Int
        get() = ptr.readShort(OInterfaceComponent.SLOT_ID).toInt()
    val type: Int
        get() = ptr.readShort(OInterfaceComponent.COMPONENT_TYPE).toInt()
    /**
     * The FLAGS bit 0x20 false-negatives for some dialog interfaces (the game leaves it
     * unset even while the component is on screen). Non-zero width/height after the
     * layout pass is the reliable signal we previously used.
     */
    val visible: Boolean
        get() = ptr.readInt(SCREEN_WIDTH) != 0 || ptr.readInt(SCREEN_HEIGHT) != 0
    /** Parent-local X. NOT screen-absolute — see [screenRect] for the live screen position. */
    val parentRelX: Int
        get() = ptr.readInt(PARENT_REL_X)
    /** Parent-local Y. NOT screen-absolute — see [screenRect] for the live screen position. */
    val parentRelY: Int
        get() = ptr.readInt(PARENT_REL_Y)
    /** Resolved width in pixels. */
    val screenWidth: Int
        get() = ptr.readInt(SCREEN_WIDTH)
    /** Resolved height in pixels. */
    val screenHeight: Int
        get() = ptr.readInt(SCREEN_HEIGHT)
    /** Absolute screen rect captured during the most recent draw pass, or null if not laid out this frame. */
    val screenRect: ScreenRect?
        get() = InterfaceComponentRectCapture.lookup(ptr)
    val xOriginOffset: Int
        get() = ptr.readInt(OInterfaceComponent.X_ORIGIN_OFFSET)
    val yOriginOffset: Int
        get() = ptr.readInt(OInterfaceComponent.Y_ORIGIN_OFFSET)
    val rawX: Int
        get() = ptr.readInt(OInterfaceComponent.RAW_X)
    val rawY: Int
        get() = ptr.readInt(OInterfaceComponent.RAW_Y)
    val xOriginMode: Int
        get() = ptr.readByte(OInterfaceComponent.X_ORIGIN_MODE).toInt() and 0xFF
    val yOriginMode: Int
        get() = ptr.readByte(OInterfaceComponent.Y_ORIGIN_MODE).toInt() and 0xFF
    val xAnchorMode: Int
        get() = ptr.readByte(OInterfaceComponent.X_ANCHOR_MODE).toInt() and 0xFF
    val yAnchorMode: Int
        get() = ptr.readByte(OInterfaceComponent.Y_ANCHOR_MODE).toInt() and 0xFF
    val parent: InterfaceComponent?
        get() = ptr.deref(OInterfaceComponent.PARENT_SHAREDPTR + 0x8L, 0x200L).getOrNull?.let { InterfaceComponent(it) }
    val text: String
        get() = EastlString(ptr.pointerAtOffset(OInterfaceComponent.TEXT, 0x24)).toString()
    val itemId
        get() = ptr.readInt(ITEM_ID)
    val stackSize
        get() = ptr.readInt(STACK_SIZE)
    val slotChildren: List<InterfaceComponent>
        get() = walkChildVector(OInterfaceComponent.SLOT_CHILDREN)

    // Same slot layout as the parent's child vector — derived from
    // jag::InterfaceManager::DrawSlotChildren: stride 0x18, removed byte at offset 0,
    // shared_ptr value at offset 0x10. Mirroring InterfaceParent.get(), skip removed
    // slots and null pointers; treat begin==0 / end==0 / end<begin as empty (an
    // uninitialized or torn-down vector — DrawSlotChildren walks nothing in that case).
    private fun walkChildVector(vectorOffset: Long): List<InterfaceComponent> {
        val vecPtr = ptr.pointerAtOffset(vectorOffset, 0x18L).getOrNull ?: return emptyList()
        val begin = vecPtr.readLong(Vector.OVector.VECTOR_BEGIN)
        val end = vecPtr.readLong(Vector.OVector.VECTOR_END)
        if (begin == 0L || end == 0L || end < begin) return emptyList()
        val stride = OInterfaceParent.CHILD_SLOT_STRIDE
        val out = mutableListOf<InterfaceComponent>()
        var slotAddr = begin
        while (slotAddr + stride <= end) {
            val slot = slotAddr.toMemorySegment(stride)
            if (slot.readByte(OInterfaceParent.CHILD_SLOT_REMOVED_FLAG) == 0.toByte()) {
                val compAddr = slot.readLong(OInterfaceParent.CHILD_SLOT_PTR)
                if (compAddr != 0L) out += InterfaceComponent(compAddr.toMemorySegment(0x200L))
            }
            slotAddr += stride
        }
        return out
    }
}