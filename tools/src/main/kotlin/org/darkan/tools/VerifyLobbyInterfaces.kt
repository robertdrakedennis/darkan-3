package org.darkan.tools

import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import org.darkan.core.net.prot.Codec
import org.darkan.core.net.prot.IfSetPosition
import org.darkan.core.net.prot.IfSetTopLevelInterface
import org.darkan.core.net.prot.ServerProt
import org.darkan.core.net.prot.revision.rev948.register948

/**
 * Verifies that the rev948 IF_SETTOPLEVELINTERFACE (op 3) and IF_SETPOSITION (op 82) encoders
 * reproduce, byte-for-byte, the BODIES captured from the live Jagex 948 lobby
 * (capture/login-20260531-191837_s1/decoded.log, ISAAC-stripped).
 *
 * Run: ./gradlew :tools:run -PmainClass=org.darkan.tools.VerifyLobbyInterfacesKt
 */
private fun hex(s: String): ByteArray =
    s.trim().split(Regex("\\s+")).map { it.toInt(16).toByte() }.toByteArray()

private fun ByteArray.toHex(): String = joinToString(" ") { "%02x".format(it) }

private suspend fun encodeBody(codec: Codec, prot: ServerProt): ByteArray {
    val entry = codec.serverProts[prot::class] ?: error("No encoder for ${prot::class.simpleName}")
    val ch = ByteChannel()
    entry.encoder?.invoke(prot, ch)
    ch.flush()
    val out = ByteArray(ch.availableForRead)
    ch.readFully(out)
    ch.close()
    return out
}

fun main() = runBlocking {
    val codec = register948()
    var allMatch = true

    fun check(label: String, expected: ByteArray, actual: ByteArray) {
        val ok = expected.contentEquals(actual)
        if (!ok) allMatch = false
        println("[${if (ok) "PASS" else "FAIL"}] $label")
        if (!ok) {
            println("    expected: ${expected.toHex()}")
            println("    actual:   ${actual.toHex()}")
        }
    }

    // op 3 — IF_SETTOPLEVELINTERFACE, parent 906
    check(
        "op3 IfSetTopLevelInterface(906)",
        hex("00 00 00 00 00 00 00 00 00 0A 03 00 00 00 00 00 00 00 00"),
        encodeBody(codec, IfSetTopLevelInterface(906)),
    )

    // op 82 — IF_SETPOSITION samples from capture.
    // Decoded fields: layer=1, position=(906<<16)|slot, componentId=childInterfaceId.
    // (slot, child, expectedBodyHex)
    val samples = listOf(
        Triple(44, 907, "7F 00 00 00 00 8A 03 2C 00 00 00 00 00 00 00 00 00 00 00 00 00 8B 03"),
        Triple(45, 910, "7F 00 00 00 00 8A 03 2D 00 00 00 00 00 00 00 00 00 00 00 00 00 8E 03"),
        Triple(0x90, 0x0392, "7F 00 00 00 00 8A 03 90 00 00 00 00 00 00 00 00 00 00 00 00 00 92 03"),
        Triple(0xAB, 0x052A, "7F 00 00 00 00 8A 03 AB 00 00 00 00 00 00 00 00 00 00 00 00 00 2A 05"),
    )
    for ((slot, child, h) in samples) {
        val position = (906 shl 16) or (slot and 0xFFFF)
        check(
            "op82 IfSetPosition slot=$slot child=$child",
            hex(h),
            encodeBody(codec, IfSetPosition(componentId = child, layer = 1, position = position)),
        )
    }

    println(if (allMatch) "\nALL MATCH" else "\nMISMATCH(ES) FOUND")
}
