package org.darkan.core.net.login

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LoginTokenTest {
    @Test
    fun `compact token verifies for matching user before expiry`() {
        val token = LoginToken.issueCompact(
            username = "probe",
            nowMs = 1_000_000L,
            ttlMs = 60_000L,
            secret = "secret",
        )

        assertTrue(LoginToken.verifyCompact("probe", token, nowMs = 1_010_000L, secret = "secret"))
        assertFalse(LoginToken.verifyCompact("other", token, nowMs = 1_010_000L, secret = "secret"))
        assertFalse(LoginToken.verifyCompact("probe", token, nowMs = 1_010_000L, secret = "wrong"))
        assertFalse(LoginToken.verifyCompact("probe", token, nowMs = 1_061_000L, secret = "secret"))
    }

    @Test
    fun `real credential tail parses password branch and session longs`() {
        val bytes = buildList {
            add(2)
            addAll(listOf(0, 0, 0, 0))
            add(0)
            addString("password")
            addLong(0x0102030405060708L)
            addLong(0x1112131415161718L)
        }.toByteArray()

        val parsed = RsaCredentialTailParser.parse(bytes)

        assertEquals(2, parsed.credentialType)
        assertEquals("password", parsed.password)
        assertEquals("", parsed.tokenString)
        assertEquals(0x0102030405060708L, parsed.sessionNonce1)
        assertEquals(0x1112131415161718L, parsed.sessionNonce2)
    }

    @Test
    fun `legacy credential tail still parses probe layout`() {
        val bytes = buildList {
            addString("")
            addString("password")
            addLong(0x0102030405060708L)
            addLong(0x1112131415161718L)
        }.toByteArray()

        val parsed = RsaCredentialTailParser.parse(bytes)

        assertEquals(null, parsed.credentialType)
        assertEquals("password", parsed.password)
        assertEquals("", parsed.tokenString)
        assertEquals(0x0102030405060708L, parsed.sessionNonce1)
        assertEquals(0x1112131415161718L, parsed.sessionNonce2)
    }

    private fun MutableList<Int>.addString(value: String) {
        value.toByteArray(Charsets.ISO_8859_1).forEach { add(it.toInt() and 0xFF) }
        add(0)
    }

    private fun MutableList<Int>.addLong(value: Long) {
        for (shift in 56 downTo 0 step 8) {
            add(((value ushr shift) and 0xFF).toInt())
        }
    }

    private fun List<Int>.toByteArray(): ByteArray =
        ByteArray(size) { this[it].toByte() }
}
