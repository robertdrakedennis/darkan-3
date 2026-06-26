package org.darkan.tools.recorder

import org.darkan.core.EnvVars
import org.darkan.core.net.RequestOpcode
import world.gregs.voidps.cache.secure.RSA
import java.math.BigInteger

object LoginSeedExtractor {
    private val loginRsaMod = BigInteger(EnvVars.loginRsaModulus)
    private val loginRsaExp = BigInteger(EnvVars.loginRsaExponent)
    private val worldRsaMod = BigInteger(EnvVars.worldRsaModulus)
    private val worldRsaExp = BigInteger(EnvVars.worldRsaExponent)

    fun extract(conn: Connection): IntArray? {
        if (conn.role != Role.LOBBY && conn.role != Role.WORLD) return null
        val raw = conn.c2s
        if (raw.size < 4) return null
        if ((raw[0].toInt() and 0xFF) != RequestOpcode.CONNECT_LOGIN) return null

        val loginOpcode = raw[1].toInt() and 0xFF
        val blockSize = u16(raw, 2)
        val blockStart = 4
        val blockEnd = blockStart + blockSize
        if (blockSize <= 0 || blockEnd > raw.size) return null
        if (!loginOpcodeMatchesRole(conn.role, loginOpcode)) return null

        return when (conn.role) {
            Role.LOBBY -> extractFromLoginBlock(raw, blockStart, blockEnd, hasWorldStateByte = false)
            Role.WORLD -> extractFromLoginBlock(raw, blockStart, blockEnd, hasWorldStateByte = true)
        }
    }

    private fun loginOpcodeMatchesRole(role: Role, opcode: Int): Boolean = when (role) {
        Role.LOBBY -> opcode == RequestOpcode.LOBBY || opcode == RequestOpcode.LOGIN
        Role.WORLD -> opcode == RequestOpcode.LOGIN || opcode == RequestOpcode.RECONNECT
        else -> false
    }

    private fun extractFromLoginBlock(
        raw: ByteArray,
        blockStart: Int,
        blockEnd: Int,
        hasWorldStateByte: Boolean,
    ): IntArray? {
        var pos = blockStart
        if (pos + 8 > blockEnd) return null
        pos += 8
        if (hasWorldStateByte) {
            if (pos >= blockEnd) return null
            pos++
        }
        if (pos + 2 > blockEnd) return null
        val rsaSize = u16(raw, pos)
        pos += 2
        if (rsaSize <= 0 || pos + rsaSize > blockEnd) return null
        val rsaBytes = raw.copyOfRange(pos, pos + rsaSize)
        val decrypted = RSA.crypt(
            rsaBytes,
            if (hasWorldStateByte) worldRsaMod else loginRsaMod,
            if (hasWorldStateByte) worldRsaExp else loginRsaExp,
        )
        return seedsFromDecryptedRsa(decrypted)
    }

    private fun seedsFromDecryptedRsa(decrypted: ByteArray): IntArray? {
        for (magicAt in decrypted.indices) {
            if ((decrypted[magicAt].toInt() and 0xFF) != 10) continue
            if (magicAt + 17 > decrypted.size) continue
            return IntArray(4) { i -> i32(decrypted, magicAt + 1 + i * 4) }
        }
        return null
    }

    private fun u16(raw: ByteArray, off: Int): Int =
        ((raw[off].toInt() and 0xFF) shl 8) or (raw[off + 1].toInt() and 0xFF)

    private fun i32(raw: ByteArray, off: Int): Int =
        ((raw[off].toInt() and 0xFF) shl 24) or
            ((raw[off + 1].toInt() and 0xFF) shl 16) or
            ((raw[off + 2].toInt() and 0xFF) shl 8) or
            (raw[off + 3].toInt() and 0xFF)
}
