package org.darkan.tools

import org.darkan.core.net.Isaac
import org.darkan.core.net.prot.Codec
import org.darkan.core.net.prot.revision.rev948.register948
import java.io.File

fun main(args: Array<String>) {
    val dir = args.getOrNull(0) ?: run {
        // Find latest capture
        val captureDir = File("capture")
        val latest = captureDir.listFiles()?.filter { it.isDirectory }?.maxByOrNull { it.name }
            ?: error("No captures found in capture/")
        latest.absolutePath
    }

    println("Decoding capture from: $dir")

    val codec = register948()

    val keysLine = File("$dir/isaac-keys.txt").readLines().first { "hex" in it }
    val keys = Regex("0x([0-9A-Fa-f]+)").findAll(keysLine).map { it.groupValues[1].toLong(16).toInt() }.toList().toIntArray()

    val s2cKeys = IntArray(4) { keys[it] + 50 }
    val isaac = Isaac(s2cKeys)

    val raw = File("$dir/raw-s2c.bin").readBytes()

    // Skip: 9B first response + 1B login result + 1B login data len + login_data
    var pos = 9
    val loginResult = raw[pos].toInt() and 0xFF; pos++
    val loginLen = raw[pos].toInt() and 0xFF; pos++
    pos += loginLen
    println("Login result=$loginResult, data=${loginLen}B, post-login at byte $pos")

    var pktCount = 0
    while (pos < raw.size) {
        val pktStart = pos
        val rawByte = raw[pos].toInt() and 0xFF; pos++
        val iv = isaac.nextInt()
        val decoded = (rawByte - iv) and 0xFF

        val opcode: Int
        if (decoded < 128) {
            opcode = decoded
        } else {
            if (pos >= raw.size) break
            val rawByte2 = raw[pos].toInt() and 0xFF; pos++
            val iv2 = isaac.nextInt()
            val decoded2 = (rawByte2 - iv2) and 0xFF
            opcode = (decoded - 128) * 256 + decoded2
        }

        if (opcode < 0 || opcode > 217) {
            println("\n*** DESYNC at pkt#$pktCount byte $pktStart: raw=0x${"%02X".format(raw[pktStart].toInt() and 0xFF)} isaac=$iv decoded=$decoded opcode=$opcode")
            val hexStart = maxOf(0, pktStart - 8)
            val hexEnd = minOf(raw.size, pktStart + 24)
            println("    hex: ${raw.slice(hexStart until hexEnd).joinToString(" ") { "%02x".format(it.toInt() and 0xFF) }}")
            break
        }

        val sizeMode = codec.serverProtSize(opcode)
        val pktSize: Int = when {
            sizeMode == 0 -> 0
            sizeMode == -1 -> { if (pos >= raw.size) break; val s = raw[pos].toInt() and 0xFF; pos++; s }
            sizeMode == -2 -> { if (pos+1 >= raw.size) break; val s = ((raw[pos].toInt() and 0xFF) shl 8) or (raw[pos+1].toInt() and 0xFF); pos += 2; s }
            else -> sizeMode
        }

        if (pos + pktSize > raw.size) {
            println("[$pktCount] Truncated: op=$opcode need ${pktSize}B")
            break
        }

        val data = raw.copyOfRange(pos, pos + pktSize)
        pos += pktSize
        pktCount++

        val name = codec.serverProtName(opcode)
        val modeStr = when(sizeMode) { -1 -> "vB"; -2 -> "vS"; else -> "f$sizeMode" }

        // Print all non-varp packets, and every 100th varp
        if (opcode !in intArrayOf(10, 111) || pktCount % 100 == 0) {
            println("[${"%4d".format(pktCount)}] op=${"%3d".format(opcode)} (${name.take(28).padEnd(28)}) ${"%-4s".format(modeStr)} size=${"%5d".format(pktSize)} @${"%6d".format(pktStart)}  ${data.take(24).joinToString(" ") { "%02x".format(it.toInt() and 0xFF) }}")
        }
    }
    println("\nTotal: $pktCount packets decoded, $pos/${raw.size} bytes consumed")
}
