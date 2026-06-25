package org.darkan.core.net.session

import io.ktor.utils.io.ByteChannel
import io.ktor.utils.io.availableForRead
import io.ktor.utils.io.close
import io.ktor.utils.io.writeFully
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import org.darkan.core.model.ChatMessageType
import org.darkan.core.net.Isaac
import org.darkan.core.net.prot.AntiCheatChallenge
import org.darkan.core.net.prot.GameMessage
import org.darkan.core.net.prot.IfButton
import org.darkan.core.net.prot.MacOsLobbyHandoff
import org.darkan.core.net.prot.Ping
import org.darkan.core.net.prot.RequestWorldList
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
        assertFalse(session.canIssueAntiCheatChallenge(nowMs = 21_001L, intervalMs = 6_000L))

        session.clearAntiCheatChallenge()

        assertNull(session.pendingAntiCheatChallenge)
        assertFalse(session.isAntiCheatChallengeTimedOut(nowMs = 100_000L, timeoutMs = 20_000L))
        assertTrue(session.canIssueAntiCheatChallenge(nowMs = 7_000L, intervalMs = 6_000L))
        assertFalse(session.canIssueAntiCheatChallenge(nowMs = 6_999L, intervalMs = 6_000L))
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
    fun `macOS lobby op174 stays framed before Play Now click`() = runBlocking {
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
                174 to hexBytes("0000000574FFFFFFFF10"),
                51 to byteArrayOf(),
                54 to hexBytes("27B8926D"),
                127 to hexBytes("51008A037FFFFF7F"),
            )
        )
        input.close(null)

        val reader = launch { session.readPackets(input) }

        assertEquals(UnhandledClientProt(174, "UNKNOWN_174", 10), withTimeout(1_000L) { session.readChannel.receive() })
        assertEquals(Ping(), withTimeout(1_000L) { session.readChannel.receive() })
        assertEquals(RequestWorldList(0x27B8926D), withTimeout(1_000L) { session.readChannel.receive() })
        assertEquals(
            IfButton(
                buttonId = 1,
                interfaceHash = (906 shl 16) or 81,
                slotId = 65535,
                itemId = -1,
            ),
            withTimeout(1_000L) { session.readChannel.receive() },
        )

        reader.join()
    }

    @Test
    fun `truly-unknown opcode stops c2s reads without closing the session`() = runBlocking {
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
                51 to byteArrayOf(),
                156 to byteArrayOf(),
                51 to byteArrayOf(),
            )
        )
        input.close(null)

        val reader = launch { session.readPackets(input) }

        assertEquals(Ping(), withTimeout(1_000L) { session.readChannel.receive() })
        reader.join()

        assertFalse(session.disconnected)
        assertNull(withTimeoutOrNull(100L) { session.readChannel.receive() })
    }

    @Test
    fun `oversized VarByte server packet is dropped before opcode write`() = runBlocking {
        val output = ByteChannel(autoFlush = true)
        val session = GameSession(
            write = output,
            isaacIn = Isaac(IntArray(4)),
            isaacOut = Isaac(IntArray(4)),
            ip = "127.0.0.1",
            codec = register948(),
            username = "tester",
        )

        session.send(GameMessage(ChatMessageType.FRIEND_NOTIFICATION, "x".repeat(300)))

        assertEquals(0, output.availableForRead)
        assertFalse(session.disconnected)
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

    private fun hexBytes(hex: String): ByteArray =
        hex.filterNot(Char::isWhitespace).chunked(2).map { it.toInt(16).toByte() }.toByteArray()
}
