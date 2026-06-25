package org.darkan.lobby.server

import org.darkan.core.EnvVars
import org.darkan.core.model.Account
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Wire-format regression for the world-connect target tail in the LOBBY login response.
 *
 * Ground truth: docs/protocol/lobby-world-switch-948.md §10.3, decompiled against rs2client.948-5
 * `LoginStepHandleLoginData` @0x001cd360 (LOBBY branch). After the player/account block the client
 * parses, in order:
 *
 *     worldId  g2   (BE u16; 0xffff → -1 = "no world")
 *     host     gjStr (version byte 0, NUL-terminated CP1252, FUN_00126e90 param_3=1)
 *     portA    g2   (BE u16)
 *     portB    g2   (BE u16)
 *     sid1     u64  (BE)
 *     sid2     u64  (BE)
 *
 * It builds a WorldTarget from {worldId, host, portA, portB}, stages it PENDING at WorldSwitcher
 * +0xa8, and CommitWorldTargetFromLogin promotes it to the CURRENT slot (+0x20) that the world
 * (type-2) connect reads via LoginStepWaitingConnectionOpened → OpenConnection("host:port"). This
 * is the entire cold-lobby "Play Now" connect mechanism — op212/op213 are NOT involved (§10).
 *
 * THE REGRESSION THIS PINS: displayName and host are versioned strings. FUN_00126e90 is called with
 * param_3=1, so it consumes one leading byte and requires that byte to be 0 before reading the
 * NUL-terminated CP1252 string. If the first byte is the first text char, the parser returns an empty
 * string, advances one byte, and desyncs the rest of the login-data block.
 *
 * The expected world-target values come from EnvVars (the same source the encoder reads), so the
 * test stays correct under any .env override. sid1/sid2 (the compact LoginToken) are nondeterministic
 * (random nonce + timestamp), so the tail is matched as: <worldId host portA portB> immediately
 * followed by 16 token bytes at end-of-block.
 */
class LobbyLoginResponseTest {

    private fun u16be(v: Int): ByteArray = byteArrayOf((v ushr 8).toByte(), v.toByte())

    /** gjStr: version byte 0, CP1252 bytes, trailing NUL. */
    private fun gjstr(s: String): ByteArray = byteArrayOf(0) + s.toByteArray(Charsets.ISO_8859_1) + 0x00.toByte()

    private fun hex(b: ByteArray) = b.joinToString(" ") { "%02x".format(it) }

    private val prodPrefixBeforeDisplay = byteArrayOf(
        0x00, 0x00, 0x00, 0x00,
        0xff.toByte(), 0xfa.toByte(), 0xfa.toByte(),
        0x00, 0x00, 0x01,
        0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00,
        0x61, 0x04, 0x17, 0xa2.toByte(), 0x08,
        0x00,
        0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00,
        0x00, 0x00,
        0x00, 0x00,
        0x22, 0xb3.toByte(),
        0xad.toByte(), 0xa9.toByte(), 0x0a, 0x4a,
        0x03,
        0xd2.toByte(), 0x1f,
        0xd2.toByte(), 0x1f,
        0x00, 0x00,
    )

    /** The world-connect target slice the client parses (worldId, host, portA, portB) — pre-sid. */
    private fun expectedWorldTarget(): ByteArray =
        u16be(EnvVars.worldId) +
            gjstr(EnvVars.worldHost) +
            u16be(EnvVars.worldPort) +   // portA
            u16be(EnvVars.worldPort)     // portB

    @Test
    fun `lobby login response encodes the world-connect target tail before the session tokens`() {
        val block = LoginServer().buildLobbyData(Account(username = "tester", displayName = "Tester"))

        // The block ends with sid1(u64) + sid2(u64) = 16 token bytes; the world-target slice sits
        // immediately before them.
        val target = expectedWorldTarget()
        val tokenBytes = 16
        val expectedStart = block.size - tokenBytes - target.size
        assertTrue(expectedStart >= 0, "block too short to contain the world-target tail + tokens")

        val actualSlice = block.copyOfRange(expectedStart, expectedStart + target.size)
        assertEquals(
            hex(target), hex(actualSlice),
            "world-target tail (worldId, host[gjStr], portA, portB) must match §10.3 " +
                "and sit immediately before the 16 session-token bytes",
        )
    }

    @Test
    fun `host is versioned gjStr with leading zero byte`() {
        val block = LoginServer().buildLobbyData(Account(username = "tester", displayName = "Tester"))

        val hostBytes = EnvVars.worldHost.toByteArray(Charsets.ISO_8859_1)
        assertTrue(hostBytes.isNotEmpty(), "test assumes a non-empty world host")

        val target = expectedWorldTarget()
        val expectedStart = block.size - 16 - target.size
        val worldIdLen = 2
        assertEquals(0x00.toByte(), block[expectedStart + worldIdLen], "host gjStr version byte must be 0")
        assertEquals(hostBytes[0], block[expectedStart + worldIdLen + 1], "host text must follow version byte")
    }

    @Test
    fun `displayName is encoded with gjStr`() {
        val displayName = "Tester"
        val block = LoginServer().buildLobbyData(Account(username = "tester", displayName = displayName))

        //   [ ...body..., displayName(gjStr), #24=00, #25=00 00 00 00, <world-target>, <16 token bytes> ]
        val target = expectedWorldTarget()
        val tailFixedBytes = 1 + 4 // byte + int before the world-target tail
        val displayNameEnd = block.size - 16 - target.size - tailFixedBytes // exclusive; == index of #24
        val expectedName = gjstr(displayName)
        val displayNameStart = displayNameEnd - expectedName.size
        assertTrue(displayNameStart >= 1, "block too short to contain displayName + tail")
        assertEquals(
            prodPrefixBeforeDisplay.size, displayNameStart,
            "displayName must start at the prod-observed 948 lobby offset",
        )

        assertEquals(0x00.toByte(), block[displayNameStart], "displayName gjStr version byte must be 0")
        assertEquals('T'.code.toByte(), block[displayNameStart + 1], "displayName text must follow version byte")

        val actualName = block.copyOfRange(displayNameStart, displayNameEnd)
        assertEquals(
            hex(expectedName), hex(actualName),
            "displayName must be gjStr-encoded per §10.3",
        )
        // Byte immediately before displayName is part of the prod-observed status prefix, not a string prefix.
        assertEquals(
            0x00.toByte(), block[displayNameStart - 1],
            "byte before displayName must be prod status data, not a leading string prefix",
        )
    }

    @Test
    fun `lobby login response keeps prod-shaped prefix before displayName`() {
        val block = LoginServer().buildLobbyData(Account(username = "tester", displayName = "Tester"))

        val actualPrefix = block.copyOfRange(0, prodPrefixBeforeDisplay.size)

        assertEquals(
            hex(prodPrefixBeforeDisplay), hex(actualPrefix),
            "lobby login prefix before displayName must match prod 948 shape",
        )
    }

    @Test
    fun `worldId is the real world number, not the 0xffff no-world sentinel`() {
        // CommitWorldTargetFromLogin (@0x001acdf0) early-returns when worldId == -1 (0xffff on the
        // wire), leaving the CURRENT connect target empty. So the lobby MUST send a real world id.
        val block = LoginServer().buildLobbyData(Account(username = "tester", displayName = "Tester"))
        val target = expectedWorldTarget()
        val start = block.size - 16 - target.size
        val worldId = ((block[start].toInt() and 0xFF) shl 8) or (block[start + 1].toInt() and 0xFF)
        assertEquals(EnvVars.worldId, worldId, "encoded worldId must equal EnvVars.worldId")
        assertTrue(worldId != 0xFFFF, "worldId must NOT be 0xffff (the client maps it to -1 = no world)")
    }
}
