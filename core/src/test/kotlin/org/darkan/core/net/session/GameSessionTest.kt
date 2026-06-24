package org.darkan.core.net.session

import io.ktor.utils.io.ByteChannel
import io.ktor.utils.io.close
import io.ktor.utils.io.writeFully
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.darkan.core.net.Isaac
import org.darkan.core.net.prot.AntiCheatChallenge
import org.darkan.core.net.prot.MacOsLobbyHandoff
import org.darkan.core.net.prot.Ping
import org.darkan.core.net.prot.UnhandledClientProt
import org.darkan.core.net.prot.revision.rev948.register948
import java.io.ByteArrayOutputStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GameSessionTest {
    @Test
    fun `anti-cheat pending state marks clears and times out`() {
        val session = GameSession(
            write = ByteChannel(),
            isaacIn = Isaac(IntArray(4)),
            isaacOut = Isaac(IntArray(4)),
            ip = "127.0.0.1",
            codec = register948(),
            username = "tester",
        )
        val challenge = AntiCheatChallenge(0x01020304, 0x05060708)

        session.markAntiCheatChallengePending(challenge, sentAtMs = 1_000L)

        assertEquals(challenge, session.pendingAntiCheatChallenge)
        assertFalse(session.isAntiCheatChallengeTimedOut(nowMs = 20_999L, timeoutMs = 20_000L))
        assertTrue(session.isAntiCheatChallengeTimedOut(nowMs = 21_001L, timeoutMs = 20_000L))

        session.clearAntiCheatChallenge()

        assertNull(session.pendingAntiCheatChallenge)
        assertFalse(session.isAntiCheatChallengeTimedOut(nowMs = 100_000L, timeoutMs = 20_000L))
    }

    @Test
    fun `client opcode reader accepts single-byte high opcodes`() = runBlocking {
        val seeds = intArrayOf(0x13572468, 0x24681357, 0x10203040, 0x55667788)
        val input = ByteChannel(autoFlush = true)
        val session = GameSession(
            write = ByteChannel(),
            isaacIn = Isaac(seeds.copyOf()),
            isaacOut = Isaac(IntArray(4)),
            ip = "127.0.0.1",
            codec = register948(),
            username = "tester",
        )

        input.writeFully(
            encodeClientPackets(
                seeds,
                240 to byteArrayOf(0x5e, 0x02, 0x0d, 0x80.toByte(), 0x08, 0x38, 0x01),
                218 to ByteArray(70) { it.toByte() },
                51 to byteArrayOf(),
            )
        )
        input.close(null)

        val reader = launch { session.readPackets(input) }

        assertEquals(UnhandledClientProt(240, "UNKNOWN_240", 7), withTimeout(1_000L) { session.readChannel.receive() })
        assertEquals(MacOsLobbyHandoff(null), withTimeout(1_000L) { session.readChannel.receive() })
        assertEquals(Ping(), withTimeout(1_000L) { session.readChannel.receive() })

        reader.join()
    }

    @Test
    fun `truly-unknown opcode does not close the session and later packets still decode`() = runBlocking {
        // docs/protocol/world-ingame-transition-948.md §8 task #5 / §4: a C2S opcode with NO size
        // metadata (e.g. macOS-only op156, absent from the Linux-derived ClientProt table) must NOT
        // close the session. Previously readPackets `return`ed → finally closed the socket → the
        // client "fell back to login". Now it logs and keeps reading. We frame op156 (no payload in
        // this stream — only the opcode byte) between two known size-0 keepalives; the trailing
        // op51 must still decode, proving the session stayed alive and in ISAAC sync.
        val seeds = intArrayOf(0x0BADF00D, 0x1234ABCD, 0x55AA55AA, 0x0F0F0F0F)
        val input = ByteChannel(autoFlush = true)
        val session = GameSession(
            write = ByteChannel(),
            isaacIn = Isaac(seeds.copyOf()),
            isaacOut = Isaac(IntArray(4)),
            ip = "127.0.0.1",
            codec = register948(),
            username = "tester",
        )

        input.writeFully(
            encodeClientPackets(
                seeds,
                51 to byteArrayOf(),    // NO_TIMEOUT keepalive (known, size 0)
                156 to byteArrayOf(),   // op156 — no size metadata; must be tolerated, not fatal
                51 to byteArrayOf(),    // another keepalive AFTER the unknown op — must still decode
            )
        )
        input.close(null)

        val reader = launch { session.readPackets(input) }

        // First keepalive decodes.
        assertEquals(Ping(), withTimeout(1_000L) { session.readChannel.receive() })
        // op156 produces NO decoded packet (logged + skipped), and the session is NOT closed, so the
        // SECOND keepalive after it still arrives.
        assertEquals(Ping(), withTimeout(1_000L) { session.readChannel.receive() })

        reader.join()
    }

    private fun encodeClientPackets(seeds: IntArray, vararg packets: Pair<Int, ByteArray>): ByteArray {
        val cipher = Isaac(seeds.copyOf())
        val out = ByteArrayOutputStream()
        for ((opcode, payload) in packets) {
            out.write((opcode + cipher.nextInt()) and 0xff)
            out.write(payload)
        }
        return out.toByteArray()
    }
}
