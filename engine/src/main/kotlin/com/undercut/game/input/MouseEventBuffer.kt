package com.undercut.game.input

import com.undercut.game.bootstrap.Bootstrap
import com.undercut.game.nxt.OClient
import com.undercut.game.nxt.OMainLogicManager
import com.undercut.game.nxt.OMouseClickEntry
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout.JAVA_FLOAT
import java.lang.foreign.ValueLayout.JAVA_INT
import java.lang.foreign.ValueLayout.JAVA_LONG

/**
 * The MainLogicManager ring buffer drained by `jag::ClientProt::SendEventMouseClick`
 * (947-3 @ 0x2e0710). Holds BOTH move events (buttonFlag=0) and click events
 * (buttonFlag!=0) - this is the only server-bound mouse pipeline.
 *
 * Layout (per OMainLogicManager / OMouseClickEntry):
 *  - base   = MainLogicManager + 0x4590
 *  - tail   = MainLogicManager + 0x4d60 (u32, next-read index)
 *  - head   = MainLogicManager + 0x4d64 (u32, next-write index)
 *  - cap    = 50 entries, stride 0x28
 *  - entry  = { int buttonFlag@0, float y@4, float x@8, long timestampMs@0x18 }
 *
 * All access bypasses the SDL input pipeline; the visible cursor is never touched.
 * The game thread reads and writes this buffer without locks (single-producer in
 * the lambda chain), so pushes from the engine MUST happen on the game thread -
 * use only from script API / hook callbacks, never from a background executor.
 */
object MouseEventBuffer {
    data class Entry(val buttonFlag: Int, val x: Float, val y: Float, val timestampMs: Long)

    /** Public so MouseClickPacketSender can write an entry + advance head atomically. */
    fun managerOrNull(): MemorySegment? {
        return try {
            val client = Bootstrap.client.ptr
            val managerAddr = client.get(JAVA_LONG, OClient.MAINLOGIC_MANAGER)
            if (managerAddr == 0L) null
            else MemorySegment.ofAddress(managerAddr).reinterpret(OMainLogicManager.CLICK_BUFFER_HEAD + 8L)
        } catch (_: Throwable) { null }
    }

    fun isReady(): Boolean = managerOrNull() != null

    fun head(): Int = managerOrNull()?.get(JAVA_INT, OMainLogicManager.CLICK_BUFFER_HEAD) ?: -1
    fun tail(): Int = managerOrNull()?.get(JAVA_INT, OMainLogicManager.CLICK_BUFFER_TAIL) ?: -1

    fun pendingCount(): Int {
        val mgr = managerOrNull() ?: return 0
        val h = mgr.get(JAVA_INT, OMainLogicManager.CLICK_BUFFER_HEAD)
        val t = mgr.get(JAVA_INT, OMainLogicManager.CLICK_BUFFER_TAIL)
        val cap = OMainLogicManager.CLICK_BUFFER_CAPACITY
        return ((h - t) % cap + cap) % cap
    }

    fun readEntry(index: Int): Entry? {
        val mgr = managerOrNull() ?: return null
        val cap = OMainLogicManager.CLICK_BUFFER_CAPACITY
        val safeIdx = ((index % cap) + cap) % cap
        val entryOffset = OMainLogicManager.CLICK_BUFFER_BASE + safeIdx * OMainLogicManager.CLICK_ENTRY_SIZE
        val flag = mgr.get(JAVA_INT, entryOffset + OMouseClickEntry.BUTTON_FLAG)
        val y = mgr.get(JAVA_FLOAT, entryOffset + OMouseClickEntry.Y)
        val x = mgr.get(JAVA_FLOAT, entryOffset + OMouseClickEntry.X)
        val ts = mgr.get(JAVA_LONG, entryOffset + OMouseClickEntry.TIMESTAMP_MS)
        return Entry(flag, x, y, ts)
    }

    /**
     * Append one entry to the ring. **PERMANENTLY DISABLED.**
     *
     * Two separate experiments confirmed that the game's local processor consumes
     * this buffer to drive real interactions:
     *   1. Click entries triggered ~500k cascading DoActions via raycast.
     *   2. Motion-only entries (buttonFlag = 0) ALSO triggered real clicks.
     *
     * So the buffer is shared between server-bound packet build and local input
     * dispatch — pushing anything into it side-effects the bot. Use a different
     * injection point (rewrite-in-place in SendEventMouseClick hook, or build
     * packets directly via CreatePacket + SendClientMessage).
     */
    fun push(buttonFlag: Int, x: Float, y: Float, timestampMs: Long): Boolean {
        return false
    }
}
