package org.darkan.core.net.login

import world.gregs.voidps.buffer.Cp1252

data class RsaCredentialTail(
    val credentialType: Int?,
    val password: String,
    val tokenString: String,
    val sessionNonce1: Long,
    val sessionNonce2: Long,
) {
    val hasRealCredentialSelector: Boolean get() = credentialType != null
}

object RsaCredentialTailParser {
    fun parse(bytes: ByteArray): RsaCredentialTail {
        parseReal(bytes)?.let { return it }
        return parseLegacy(bytes)
    }

    private fun parseReal(bytes: ByteArray): RsaCredentialTail? {
        val reader = Reader(bytes)
        val credentialType = reader.u8OrNull() ?: return null
        if (credentialType !in 0..3) return null

        if (!reader.skip(4)) return null
        val hasTokenString = reader.u8OrNull() ?: return null

        return when (hasTokenString) {
            0 -> {
                val password = reader.rsStringOrNull() ?: return null
                val sessionNonce1 = reader.i64OrNull() ?: return null
                val sessionNonce2 = reader.i64OrNull() ?: return null
                RsaCredentialTail(
                    credentialType = credentialType,
                    password = password,
                    tokenString = "",
                    sessionNonce1 = sessionNonce1,
                    sessionNonce2 = sessionNonce2,
                )
            }
            1 -> {
                val tokenString = reader.rsStringOrNull() ?: return null
                RsaCredentialTail(
                    credentialType = credentialType,
                    password = "",
                    tokenString = tokenString,
                    sessionNonce1 = 0L,
                    sessionNonce2 = 0L,
                )
            }
            else -> null
        }
    }

    private fun parseLegacy(bytes: ByteArray): RsaCredentialTail {
        val reader = Reader(bytes)
        val authToken = reader.rsStringOrEmpty()
        val password = reader.rsStringOrEmpty()
        val sessionNonce1 = reader.i64OrZero()
        val sessionNonce2 = reader.i64OrZero()
        return RsaCredentialTail(
            credentialType = null,
            password = password,
            tokenString = authToken,
            sessionNonce1 = sessionNonce1,
            sessionNonce2 = sessionNonce2,
        )
    }

    private class Reader(private val bytes: ByteArray) {
        private var pos = 0

        fun u8OrNull(): Int? {
            if (pos >= bytes.size) return null
            return bytes[pos++].toInt() and 0xFF
        }

        fun skip(count: Int): Boolean {
            if (pos + count > bytes.size) return false
            pos += count
            return true
        }

        fun rsStringOrNull(): String? {
            val start = pos
            while (pos < bytes.size && bytes[pos].toInt() != 0) pos++
            if (pos >= bytes.size) return null
            val value = Cp1252.decode(bytes, start, pos - start)
            pos++
            return value
        }

        fun rsStringOrEmpty(): String = rsStringOrNull() ?: ""

        fun i64OrNull(): Long? {
            if (pos + Long.SIZE_BYTES > bytes.size) return null
            var value = 0L
            repeat(Long.SIZE_BYTES) {
                value = (value shl 8) or (bytes[pos++].toLong() and 0xFF)
            }
            return value
        }

        fun i64OrZero(): Long = i64OrNull() ?: 0L
    }
}
