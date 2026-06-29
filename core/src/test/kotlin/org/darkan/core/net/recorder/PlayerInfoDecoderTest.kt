package org.darkan.core.net.recorder

import world.gregs.voidps.buffer.write.BufferWriter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

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
