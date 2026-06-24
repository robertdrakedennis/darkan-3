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
 *     host     gStr (NUL-terminated CP1252, FUN_00126e90 — NO leading length byte)
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
 * THE REGRESSION THIS PINS: the host MUST be encoded with writeString (CP1252 + NUL, no leading
 * byte). The prior code used writePrefixedString, which prepends an extra 0x00 — gStr reads that
 * leading 0x00 as an immediate terminator (empty host) and then misparses the real host bytes as
 * portA/portB, desyncing the whole tail. A leading 0x00 before the host bytes is the bug; this test
 * fails if it ever comes back.
 *
 * The expected world-target values come from EnvVars (the same source the encoder reads), so the
 * test stays correct under any .env override. sid1/sid2 (the compact LoginToken) are nondeterministic
 * (random nonce + timestamp), so the tail is matched as: <worldId host portA portB> immediately
 * followed by 16 token bytes at end-of-block.
 */
class LobbyLoginResponseTest {

    private fun u16be(v: Int): ByteArray = byteArrayOf((v ushr 8).toByte(), v.toByte())

    /** gStr / writeString: CP1252 bytes + a single NUL terminator, NO leading length byte. */
    private fun gstr(s: String): ByteArray = s.toByteArray(Charsets.ISO_8859_1) + 0x00.toByte()

    private fun hex(b: ByteArray) = b.joinToString(" ") { "%02x".format(it) }

    /** The world-connect target slice the client parses (worldId, host, portA, portB) — pre-sid. */
    private fun expectedWorldTarget(): ByteArray =
        u16be(EnvVars.worldId) +
            gstr(EnvVars.worldHost) +
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
            "world-target tail (worldId, host[gStr, no leading byte], portA, portB) must match §10.3 " +
                "and sit immediately before the 16 session-token bytes",
        )
    }

    @Test
    fun `host is NUL-terminated with NO leading length byte (writePrefixedString regression guard)`() {
        val block = LoginServer().buildLobbyData(Account(username = "tester", displayName = "Tester"))

        // Right after the BE u16 worldId the first host byte must be the first CP1252 char of the
        // host — NOT a 0x00 (which is what writePrefixedString would emit, and what gStr would read
        // as an empty string).
        val hostBytes = EnvVars.worldHost.toByteArray(Charsets.ISO_8859_1)
        assertTrue(hostBytes.isNotEmpty(), "test assumes a non-empty world host")

        val target = expectedWorldTarget()
        val expectedStart = block.size - 16 - target.size
        val worldIdLen = 2
        val firstHostByte = block[expectedStart + worldIdLen]
        assertEquals(
            hostBytes[0], firstHostByte,
            "host must start immediately after worldId with its first CP1252 byte (0x%02x), not a leading 0x00"
                .format(hostBytes[0]),
        )
    }

    @Test
    fun `displayName is encoded with gStr (no leading 0x00) and a single trailing NUL`() {
        // §10.3: the client reads EXACTLY TWO gStr strings in the lobby login-data block — the
        // displayName (record +0x68, @0x001cd7c7) and the world host (local_88, @0x001cd878) — both
        // via FUN_00126e90 (NUL-terminated CP1252, NO leading length/flag byte). displayName MUST be
        // writeString, NOT writePrefixedString: a leading 0x00 is read by gStr as an immediate
        // terminator → empty displayName → the real name bytes and the entire world-target tail
        // desync. This mirrors the host guard below for the OTHER gStr field.
        val displayName = "Tester"
        val block = LoginServer().buildLobbyData(Account(username = "tester", displayName = displayName))

        // displayName is the only variable-length field in the player/account body. It sits right
        // after field #22 isMembersWorld (a single 0x01 byte) and is followed by the fixed
        // #24 unknown9 (0x00) + #25 unknown10 (0x00000000) bytes that precede the world-target tail.
        // Locate it relative to the world-target tail (which itself precedes the 16 token bytes):
        //   [ ...body..., displayName(gStr), #24=00, #25=00 00 00 00, <world-target>, <16 token bytes> ]
        val target = expectedWorldTarget()
        val tailFixedBytes = 1 + 4 // #24 unknown9 (byte) + #25 unknown10 (int)
        val displayNameEnd = block.size - 16 - target.size - tailFixedBytes // exclusive; == index of #24
        val expectedName = gstr(displayName) // CP1252 + single trailing NUL — NO leading byte
        val displayNameStart = displayNameEnd - expectedName.size
        assertTrue(displayNameStart >= 1, "block too short to contain displayName + tail")

        // The first displayName byte must be the first CP1252 char ('T'), NOT a leading 0x00 (which
        // is exactly what writePrefixedString would emit and what regresses the whole tail).
        val firstNameByte = block[displayNameStart]
        assertEquals(
            expectedName[0], firstNameByte,
            "displayName must start with its first CP1252 byte (0x%02x), not a leading 0x00 (the writePrefixedString regression)"
                .format(expectedName[0]),
        )

        // And the full field must round-trip as CP1252 + a single trailing NUL.
        val actualName = block.copyOfRange(displayNameStart, displayNameEnd)
        assertEquals(
            hex(expectedName), hex(actualName),
            "displayName must be gStr-encoded (CP1252 + single trailing NUL, no leading byte) per §10.3",
        )
        // Belt-and-braces: the byte immediately before displayName is #22 isMembersWorld = 0x01,
        // confirming there is no stray 0x00 padding ahead of the name.
        assertEquals(
            0x01.toByte(), block[displayNameStart - 1],
            "byte before displayName must be #22 isMembersWorld=0x01, i.e. no leading 0x00 prefix on the name",
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
