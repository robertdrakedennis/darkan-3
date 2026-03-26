package org.darkan.tools

import org.darkan.core.net.Isaac
import org.darkan.core.net.prot.revision.rev947.register947
import java.io.File

fun main() {
    val dir = File("capture").listFiles()?.filter { it.isDirectory }?.maxByOrNull { it.name }?.absolutePath ?: error("No captures")
    val codec = register947()
    val keysLine = File("$dir/isaac-keys.txt").readLines().first { "hex" in it }
    val keys = Regex("0x([0-9A-Fa-f]+)").findAll(keysLine).map { it.groupValues[1].toLong(16).toInt() }.toList().toIntArray()
    val isaac = Isaac(IntArray(4) { keys[it] + 50 })
    val raw = File("$dir/raw-s2c.bin").readBytes()

    var pos = 9; pos++; val ll = raw[pos].toInt() and 0xFF; pos++; pos += ll

    val segments = mutableListOf<ByteArray>()
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
        if (opcode == 159) { segments.add(data.copyOfRange(1, data.size)); if ((data[0].toInt() and 0xFF) == 1) break }
    }

    val buf = ByteArray(segments.sumOf { it.size })
    var off = 0; for (seg in segments) { System.arraycopy(seg, 0, buf, off, seg.size); off += seg.size }

    // Skip to player count section: parse header, countries, worlds, revision
    var p = 0
    fun g1() = buf[p++].toInt() and 0xFF
    fun g2(): Int { val v = ((buf[p].toInt() and 0xFF) shl 8) or (buf[p+1].toInt() and 0xFF); p += 2; return v }
    fun g4(): Int { val v = ((buf[p].toInt() and 0xFF) shl 24) or ((buf[p+1].toInt() and 0xFF) shl 16) or ((buf[p+2].toInt() and 0xFF) shl 8) or (buf[p+3].toInt() and 0xFF); p += 4; return v }
    fun smart(): Int = if ((buf[p].toInt() and 0xFF) < 0x80) g1() else g2() - 0x8000
    fun gjstr2(): String { g1(); val sb = StringBuilder(); while (buf[p].toInt() != 0) { sb.append(buf[p].toInt().toChar()); p++ }; p++; return sb.toString() }

    val refresh = g1(); val sep = g1()
    val countryCount = smart(); repeat(countryCount) { smart(); gjstr2() }
    val minWorld = smart(); val maxWorld = smart(); val worldCount = smart()
    println("minWorldId=$minWorld, maxWorldId=$maxWorld, worldCount=$worldCount")

    repeat(worldCount) { smart(); g1(); g4(); val ap = smart(); if (ap != 0) gjstr2(); gjstr2(); gjstr2() }
    val rev = g4()
    println("revision=$rev")
    println("Player count section starts at byte $p/${buf.size}")

    // Now decode player counts
    println("\nFirst 10 player count entries:")
    for (i in 0 until minOf(10, worldCount)) {
        val rawByte = buf[p].toInt() and 0xFF
        val wn: Int
        if (rawByte < 0x80) { wn = g1() } else { wn = g2() - 0x8000 }
        val pc = g2()
        val actualWorld = minWorld + wn  // if offset-based
        println("  raw_smart=$wn (actualIfOffset=${minWorld+wn}) players=${if (pc == 0xFFFF) "unchanged" else pc.toString()}")
    }
}
