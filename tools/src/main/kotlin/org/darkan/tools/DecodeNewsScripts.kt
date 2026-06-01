package org.darkan.tools

import org.darkan.core.net.Isaac
import org.darkan.core.net.prot.revision.rev948.register948
import java.io.File

fun main() {
    val dir = File("capture").listFiles()?.filter { it.isDirectory }?.maxByOrNull { it.name }?.absolutePath ?: error("No captures")
    val codec = register948()
    val keysLine = File("$dir/isaac-keys.txt").readLines().first { "hex" in it }
    val keys = Regex("0x([0-9A-Fa-f]+)").findAll(keysLine).map { it.groupValues[1].toLong(16).toInt() }.toList().toIntArray()
    val isaac = Isaac(IntArray(4) { keys[it] + 50 })
    val raw = File("$dir/raw-s2c.bin").readBytes()
    var pos = 9; pos++; val ll = raw[pos].toInt() and 0xFF; pos++; pos += ll

    val newsPackets = mutableListOf<ByteArray>()
    while (pos < raw.size) {
        val rb = raw[pos].toInt() and 0xFF; pos++
        val decoded = (rb - isaac.nextInt()) and 0xFF
        val opcode = if (decoded < 128) decoded else {
            val rb2 = raw[pos].toInt() and 0xFF; pos++; (decoded - 128) * 256 + ((rb2 - isaac.nextInt()) and 0xFF)
        }
        if (opcode > 217) break
        val sm = codec.serverProtSize(opcode)
        val pktSize = when { sm == 0 -> 0; sm == -1 -> { val s = raw[pos].toInt() and 0xFF; pos++; s }; sm == -2 -> { val s = ((raw[pos].toInt() and 0xFF) shl 8) or (raw[pos+1].toInt() and 0xFF); pos += 2; s }; else -> sm }
        if (pos + pktSize > raw.size) break
        val data = raw.copyOfRange(pos, pos + pktSize)
        pos += pktSize
        // Collect large RUNCLIENTSCRIPT (news scripts have type "iiiissssi")
        if (opcode == 121 && pktSize > 20) newsPackets.add(data)
    }

    println("Found ${newsPackets.size} news RUNCLIENTSCRIPT packets\n")

    for ((idx, data) in newsPackets.take(3).withIndex()) {
        var p = 0
        fun g1() = data[p++].toInt() and 0xFF
        fun g4(): Int { val v = ((data[p].toInt() and 0xFF) shl 24) or ((data[p+1].toInt() and 0xFF) shl 16) or ((data[p+2].toInt() and 0xFF) shl 8) or (data[p+3].toInt() and 0xFF); p += 4; return v }
        fun rsStr(): String { val sb = StringBuilder(); while (data[p].toInt() != 0) { sb.append(data[p].toInt().toChar()); p++ }; p++; return sb.toString() }

        // Read type descriptor
        val typeDesc = rsStr()
        println("[$idx] size=${data.size}, type='$typeDesc'")

        // Read args in REVERSED order
        val args = arrayOfNulls<Any>(typeDesc.length)
        for (i in typeDesc.indices.reversed()) {
            when (typeDesc[i]) {
                'i' -> args[i] = g4()
                's' -> args[i] = rsStr()
                'l' -> { p += 8; args[i] = 0L } // skip longs
            }
        }
        val scriptId = g4()
        println("  scriptId = $scriptId")
        for (i in args.indices) {
            val a = args[i]
            when {
                a is Int && a > 65535 -> println("  arg[$i] (${typeDesc[i]}): $a = if ${a shr 16} comp ${a and 0xFFFF}")
                a is String && a.length > 40 -> println("  arg[$i] (${typeDesc[i]}): '${a.take(80)}...' (${a.length} chars)")
                else -> println("  arg[$i] (${typeDesc[i]}): $a")
            }
        }
        println()
    }
}
