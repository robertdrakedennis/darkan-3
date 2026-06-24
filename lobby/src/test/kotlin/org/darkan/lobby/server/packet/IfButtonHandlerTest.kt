package org.darkan.lobby.server.packet

import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import org.darkan.core.net.Isaac
import org.darkan.core.net.prot.Codec
import org.darkan.core.net.prot.IfButton
import org.darkan.core.net.prot.revision.rev948.register948
import org.darkan.core.net.session.GameSession
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Dispatch + wire test for the lobby "Play Now" trigger.
 *
 * This drives the REAL [GameSession.send] path — any handler packets would be ISAAC-framed onto a
 * channel exactly as on the wire, then we de-ISAAC the opcode stream (mirroring the out cipher) to
 * assert which ServerProt opcodes were emitted. So it proves dispatch AND the 948 framing together.
 */
class IfButtonHandlerTest {

    companion object {
        private val CODEC: Codec = register948()
        // Known ISAAC seed so the test can mirror the out cipher and decode the opcode stream.
        private val SEED = intArrayOf(1, 2, 3, 4)
    }

    private val handler = IfButtonHandler()

    private fun click(interfaceId: Int, componentId: Int) =
        IfButton(buttonId = 1, interfaceHash = (interfaceId shl 16) or componentId, slotId = -1, itemId = -1)

    /** Run the handler against a fresh session and return the ServerProt opcodes it emitted, in order. */
    private fun emittedOpcodes(packet: IfButton): List<Int> = runBlocking {
        val channel = ByteChannel(autoFlush = true)
        val session = GameSession(
            write = channel,
            isaacIn = Isaac(SEED.copyOf()),
            isaacOut = Isaac(SEED.copyOf()),
            ip = "test",
            codec = CODEC,
            username = "tester",
        )
        handler.handle(session, packet)
        session.flush()

        // Drain whatever is buffered without blocking, then de-ISAAC the opcode stream.
        val available = channel.availableForRead
        if (available == 0) return@runBlocking emptyList()
        val raw = ByteArray(available)
        channel.readFully(raw)

        val mirror = Isaac(SEED.copyOf())
        val opcodes = mutableListOf<Int>()
        var pos = 0
        while (pos < raw.size) {
            val decoded = ((raw[pos].toInt() and 0xFF) - mirror.nextInt()) and 0xFF; pos++
            val opcode = if (decoded < 128) decoded else {
                val d2 = ((raw[pos].toInt() and 0xFF) - mirror.nextInt()) and 0xFF; pos++
                (decoded - 128) * 256 + d2
            }
            opcodes += opcode
            // Skip the body so the next opcode aligns. 212/213 are varByte (size mode -1).
            val sizeMode = CODEC.serverProtSize(opcode)
            val size = when {
                sizeMode >= 0 -> sizeMode
                sizeMode == -1 -> (raw[pos].toInt() and 0xFF).also { pos++ }
                else -> (((raw[pos].toInt() and 0xFF) shl 8) or (raw[pos + 1].toInt() and 0xFF)).also { pos += 2 }
            }
            pos += size
        }
        opcodes
    }

    @Test
    fun `Play Now click 906-81 does NOT emit server world switch`() {
        assertTrue(emittedOpcodes(click(906, 81)).isEmpty(),
            "906/81 must stay client-driven so the lobby joining overlay remains intact")
    }

    @Test
    fun `old component 32 does NOT switch worlds`() {
        assertTrue(emittedOpcodes(click(906, 32)).isEmpty(),
            "906/32 is a world-row Select hotspot — must NOT switch")
    }

    @Test
    fun `world-row Select click 906-3 does NOT switch worlds`() {
        assertTrue(emittedOpcodes(click(906, 3)).isEmpty(),
            "906/3 is world selection, not entering — must NOT switch")
    }

    @Test
    fun `click on a different interface does NOT switch worlds`() {
        assertTrue(emittedOpcodes(click(1477, 81)).isEmpty(),
            "only interface 906 may trigger the switch")
    }
}
