package com.undercut.game.hooks.impl

import com.undercut.game.hooks.Hook
import com.undercut.game.hooks.HookManager
import com.undercut.game.memory.NativeAccess.readByte
import com.undercut.game.memory.NativeAccess.readInt
import com.undercut.game.memory.NativeAccess.readLong
import com.undercut.game.nxt.OFunctions
import com.undercut.game.nxt.OInterfaceComponent
import com.undercut.game.nxt.OInterfaceParent
import com.undercut.game.nxt.interfaces.ScreenRect
import com.undercut.game.nxt.types.Vector
import java.lang.foreign.MemorySegment
import java.util.concurrent.ConcurrentHashMap

object InterfaceComponentRectCapture {
    private const val VECTOR_HEADER_SIZE = 0x18L
    private const val SLOT_ELEMENT_SIZE = 0x18L
    private const val SHARED_PTR_DATA_OFFSET = 0x10L
    private const val COMPONENT_SIZE = 0x200L
    private const val FRESHNESS_NANOS = 50_000_000L

    private data class Snapshot(val rect: ScreenRect, val capturedAtNanos: Long)

    private val byPtr = ConcurrentHashMap<Long, Snapshot>()

    fun lookup(componentPtr: MemorySegment): ScreenRect? {
        val snap = byPtr[componentPtr.address()] ?: return null
        if (System.nanoTime() - snap.capturedAtNanos > FRESHNESS_NANOS) return null
        return snap.rect
    }

    @JvmStatic
    @Hook(OFunctions.INTERFACEMANAGER_DRAWSLOTCHILDREN)
    fun drawSlotChildrenHook(
        ifManager: MemorySegment,
        slotVec: MemorySegment,
        slotScreenX: Int,
        slotScreenY: Int,
        origScreenX: Int,
        origScreenY: Int,
        parentW: Int,
        parentH: Int,
        dt: Float,
    ) {
        try { captureSlotRects(slotVec, slotScreenX, slotScreenY) } catch (_: Throwable) {}
        HookManager.trampoline(::drawSlotChildrenHook.name)
            .invoke(ifManager, slotVec, slotScreenX, slotScreenY, origScreenX, origScreenY, parentW, parentH, dt)
    }

    private fun captureSlotRects(slotVec: MemorySegment, slotScreenX: Int, slotScreenY: Int) {
        if (slotVec.address() == 0L) return
        val now = System.nanoTime()
        for (entry in Vector(slotVec.reinterpret(VECTOR_HEADER_SIZE), SLOT_ELEMENT_SIZE)) {
            // Skip removed slots: a removed slot keeps a stale shared_ptr to a torn-down component.
            // DrawSlotChildren itself only draws slots whose removed byte is 0; non-zero means removed.
            if (entry.readByte(OInterfaceParent.CHILD_SLOT_REMOVED_FLAG) != 0.toByte()) continue
            val componentAddr = entry.readLong(SHARED_PTR_DATA_OFFSET)
            if (componentAddr == 0L) continue
            val component = MemorySegment.ofAddress(componentAddr).reinterpret(COMPONENT_SIZE)
            byPtr[componentAddr] = Snapshot(
                ScreenRect(
                    x = component.readInt(OInterfaceComponent.PARENT_REL_X) + slotScreenX,
                    y = component.readInt(OInterfaceComponent.PARENT_REL_Y) + slotScreenY,
                    width = component.readInt(OInterfaceComponent.SCREEN_WIDTH),
                    height = component.readInt(OInterfaceComponent.SCREEN_HEIGHT),
                ),
                now,
            )
        }
    }
}
