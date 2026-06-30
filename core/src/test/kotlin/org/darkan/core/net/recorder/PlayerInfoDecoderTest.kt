package org.darkan.core.net.recorder

import world.gregs.voidps.buffer.write.BufferWriter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PlayerInfoDecoderTest {

    @Test
    fun `decode retains known walk index and dir`() {
        val localIndex = 100
        val northDir = 6
        val prefix = gpiPrefixBody(localIndex = localIndex, x = 3200, y = 3200, plane = 0)
        val body = playerInfoKnownWalkBody(dir = northDir)

        val scene = PlayerInfoDecoder.decode(
            bytes = body,
            gpiPrefix = PlayerInfoDecoder.GpiPrefix(prefix, localIndex),
        )

        val movement = scene.movements.single()
        assertEquals(localIndex, movement.index)
        assertEquals(1, movement.movementType)
        assertEquals(northDir, movement.dir)
        assertFalse(movement.hasExt)
        assertNull(movement.followup)
    }

    /**
     * The fix: a LOCAL mvt=1 walk must MAINTAIN the slot's high-res tile (apply the verified `DX/DY`
     * delta), not null it. North (dir=6) from (3200,3200) → (3200,3201). The movement record carries
     * the post-step tile, byte-exact, and the folded scene player tracks it. This is what lets the
     * probe follow the local avatar's walk path tick-by-tick.
     */
    @Test
    fun `decode maintains the local tile through a north walk step`() {
        val localIndex = 100
        val northDir = 6 // (dx=0, dy=+1)
        val prefix = gpiPrefixBody(localIndex = localIndex, x = 3200, y = 3200, plane = 0)
        val body = playerInfoKnownWalkBody(dir = northDir)

        val scene = PlayerInfoDecoder.decode(
            bytes = body,
            gpiPrefix = PlayerInfoDecoder.GpiPrefix(prefix, localIndex),
        )

        val movement = scene.movements.single()
        assertEquals(1, movement.movementType)
        assertTrue(movement.tileExact, "a verified DX/DY walk delta is byte-exact")
        val moved = assertNotNull(movement.tileAfter, "walk must maintain the tile, not discard it")
        assertEquals(3200, moved.x, "north step keeps x")
        assertEquals(3201, moved.y, "north step increments y (RS y increases north)")
        assertEquals(0, moved.plane)
        // The folded scene player position is the same maintained tile.
        val player = assertNotNull(scene.players[localIndex])
        assertEquals(moved, player.tile)
    }

    /**
     * A LOCAL mvt=0 (still / no forced movement) tick must keep the tile UNCHANGED and must NOT consume
     * a trailing demote bit (that bit exists only for a NON-local hold), so the rest of the packet stays
     * in sync. Regression guard for the early-return removal: the local hold is recorded as mvt=0 with
     * the prior tile, and the 4-pass decode still completes cleanly.
     */
    @Test
    fun `decode keeps the local tile on a stationary mvt0 tick without desync`() {
        val localIndex = 100
        val prefix = gpiPrefixBody(localIndex = localIndex, x = 3200, y = 3200, plane = 0)
        val body = playerInfoLocalStillBody()

        val scene = PlayerInfoDecoder.decode(
            bytes = body,
            gpiPrefix = PlayerInfoDecoder.GpiPrefix(prefix, localIndex),
        )

        val movement = scene.movements.single { it.index == localIndex }
        assertEquals(0, movement.movementType, "stationary local tick is mvt=0")
        val tile = assertNotNull(movement.tileAfter, "mvt=0 keeps the (unchanged) tile, not null")
        assertEquals(3200, tile.x)
        assertEquals(3200, tile.y)
        val player = assertNotNull(scene.players[localIndex], "still local player stays present at its tile")
        assertEquals(tile, player.tile)
    }

    private fun gpiPrefixBody(localIndex: Int, x: Int, y: Int, plane: Int): ByteArray {
        val out = BufferWriter(8192)
        out.startBitAccess()
        out.writeBits(30, ((plane and 0x3) shl 28) or (x shl 14) or y)
        val defaultWord = ((plane and 0x3) shl 16) or ((x ushr 6) shl 8) or (y ushr 6)
        for (slot in 1 until 2048) {
            if (slot != localIndex) out.writeBits(20, defaultWord)
        }
        out.stopBitAccess()
        return out.toArray()
    }

    private fun playerInfoKnownWalkBody(dir: Int): ByteArray {
        val out = BufferWriter(8192)
        out.startBitAccess()
        out.writeBits(1, 1) // local slot has an update in pass 1.
        out.writeBits(1, 0) // hasExtendedInfo = false.
        out.writeBits(2, 1) // movementType = walk.
        out.writeBits(3, dir)
        out.writeBits(1, 0) // no walk follow-up payload.
        out.stopBitAccess()

        out.startBitAccess()
        out.stopBitAccess()

        out.startBitAccess()
        writeSkipRun(out, 2046)
        out.stopBitAccess()

        out.startBitAccess()
        out.stopBitAccess()
        return out.toArray()
    }

    /**
     * Local slot stationary `[hasUpdate=1][hasExt=0][mvt=0]` in pass 1, then the 2046 external slots
     * skip-run in pass 3. No trailing demote bit (the local hold carries none). Mirrors the structure
     * of [playerInfoKnownWalkBody].
     */
    private fun playerInfoLocalStillBody(): ByteArray {
        val out = BufferWriter(8192)
        out.startBitAccess()
        out.writeBits(1, 1) // local slot has an update in pass 1 (known active=false).
        out.writeBits(1, 0) // hasExtendedInfo = false.
        out.writeBits(2, 0) // movementType = 0 (stationary). LOCAL → no trailing demote bit.
        out.stopBitAccess()

        out.startBitAccess() // pass 2 (known active=true): empty.
        out.stopBitAccess()

        out.startBitAccess() // pass 3 (external active=true): skip the 2046 pending slots.
        writeSkipRun(out, 2046)
        out.stopBitAccess()

        out.startBitAccess() // pass 4 (external active=false): empty.
        out.stopBitAccess()
        return out.toArray()
    }

    private fun writeSkipRun(out: BufferWriter, count: Int) {
        var remaining = count
        while (remaining > 0) {
            val following = minOf(remaining - 1, 2047)
            out.writeBits(1, 0)
            writeSkipCount(out, following)
            remaining -= following + 1
        }
    }

    private fun writeSkipCount(out: BufferWriter, following: Int) {
        when {
            following == 0 -> out.writeBits(2, 0)
            following < 32 -> {
                out.writeBits(2, 1)
                out.writeBits(5, following)
            }
            following < 256 -> {
                out.writeBits(2, 2)
                out.writeBits(8, following)
            }
            else -> {
                out.writeBits(2, 3)
                out.writeBits(11, following)
            }
        }
    }
}
