package org.darkan.lobby.server.packet

import io.ktor.utils.io.ByteChannel
import io.ktor.utils.io.availableForRead
import io.ktor.utils.io.readFully
import kotlinx.coroutines.runBlocking
import org.darkan.core.net.Isaac
import org.darkan.core.net.prot.Codec
import org.darkan.core.net.prot.RequestWorldList
import org.darkan.core.net.prot.revision.rev948.register948
import org.darkan.core.net.session.GameSession
import org.darkan.core.worldlist.WorldList.Companion.REV948_PROD_WORLDLIST_REVISION
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class WorldlistFetchHandlerTest {
    private val handler = WorldlistFetchHandler()

    private data class EmittedPacket(val opcode: Int, val payload: ByteArray)

    companion object {
        private val CODEC: Codec = register948()
        private val SEED = intArrayOf(1, 2, 3, 4)
    }

    private fun emittedPackets(version: Int): List<EmittedPacket> = runBlocking {
        val channel = ByteChannel(autoFlush = true)
        val session = GameSession(
            write = channel,
            isaacIn = Isaac(SEED.copyOf()),
            isaacOut = Isaac(SEED.copyOf()),
            ip = "test",
            codec = CODEC,
            username = "tester",
        )

        handler.handle(session, RequestWorldList(version))
        session.flush()

        val available = channel.availableForRead
        if (available == 0) return@runBlocking emptyList()
        val raw = ByteArray(available)
        channel.readFully(raw)

        val mirror = Isaac(SEED.copyOf())
        val packets = mutableListOf<EmittedPacket>()
        var pos = 0
        while (pos < raw.size) {
            val decoded = ((raw[pos].toInt() and 0xFF) - mirror.nextInt()) and 0xFF
            pos++
            val opcode = if (decoded < 128) {
                decoded
            } else {
                val second = ((raw[pos].toInt() and 0xFF) - mirror.nextInt()) and 0xFF
                pos++
                (decoded - 128) * 256 + second
            }
            val sizeMode = CODEC.serverProtSize(opcode)
            val size = when {
                sizeMode >= 0 -> sizeMode
                sizeMode == -1 -> (raw[pos].toInt() and 0xFF).also { pos++ }
                else -> (((raw[pos].toInt() and 0xFF) shl 8) or (raw[pos + 1].toInt() and 0xFF)).also { pos += 2 }
            }
            val payload = raw.copyOfRange(pos, pos + size)
            packets += EmittedPacket(opcode, payload)
            pos += size
        }
        packets
    }

    @Test
    fun `matching prod worldlist revision sends full refresh`() {
        val packets = emittedPackets(REV948_PROD_WORLDLIST_REVISION)

        assertEquals(listOf(216), packets.map { it.opcode })
        assertContentEquals(byteArrayOf(1, 2, 1), packets.single().payload.copyOfRange(0, 3))
    }

    @Test
    fun `stale worldlist revision sends full refresh`() {
        val packets = emittedPackets(-1)

        assertEquals(listOf(216), packets.map { it.opcode })
        assertContentEquals(byteArrayOf(1, 2, 1), packets.single().payload.copyOfRange(0, 3))
    }
}
