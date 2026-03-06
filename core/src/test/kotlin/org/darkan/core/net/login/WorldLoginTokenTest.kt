package org.darkan.core.net.login

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class WorldLoginTokenTest {

    @Test
    fun `token roundtrip verifies and exposes fields`() {
        val secret = "unit-test-secret"
        val now = 1_000_000L
        val token = LoginToken.issue(
            username = "test_user",
            nowMs = now,
            ttlMs = 60_000L,
            secret = secret,
        )

        val verified = LoginToken.verify(token, nowMs = now + 1, secret = secret)
        assertNotNull(verified)
        assertTrue(verified.username == "test_user")
        assertTrue(verified.expiresAtMs > now)
    }

    @Test
    fun `token tamper fails`() {
        val secret = "unit-test-secret"
        val now = 1_000_000L
        val token = LoginToken.issue(
            username = "test_user",
            nowMs = now,
            ttlMs = 60_000L,
            secret = secret,
        )

        // Flip a character in the payload portion to simulate tampering
        val dotIndex = token.indexOf('.')
        val payload = token.substring(0, dotIndex)
        val signature = token.substring(dotIndex)
        val flipped = payload.mapIndexed { i, c ->
            if (i == payload.lastIndex) (if (c == 'A') 'B' else 'A') else c
        }.joinToString("")
        val tampered = flipped + signature
        val verified = LoginToken.verify(tampered, nowMs = now + 1, secret = secret)
        assertNull(verified)
    }

    @Test
    fun `expired token fails`() {
        val secret = "unit-test-secret"
        val now = 1_000_000L
        val token = LoginToken.issue(
            username = "test_user",
            nowMs = now,
            ttlMs = 10L,
            secret = secret,
        )

        val verified = LoginToken.verify(token, nowMs = now + 11, secret = secret)
        assertNull(verified)
    }
}
