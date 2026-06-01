package org.darkan.tools

import org.darkan.core.net.Isaac
import org.darkan.core.net.prot.revision.rev948.register948
import java.io.File

/**
 * Extracts and decodes the full world list from a capture's raw-s2c.bin.
 */
fun main() {
    val dir = File("capture").listFiles()?.filter { it.isDirectory }?.maxByOrNull { it.name }?.absolutePath
        ?: error("No captures")
    println("Using: $dir")
    
    val codec = register948()
    val keysLine = File("$dir/isaac-keys.txt").readLines().first { "hex" in it }
    val keys = Regex("0x([0-9A-Fa-f]+)").findAll(keysLine).map { it.groupValues[1].toLong(16).toInt() }.toList().toIntArray()
    val s2cKeys = IntArray(4) { keys[it] + 50 }
    val isaac = Isaac(s2cKeys)
    val raw = File("$dir/raw-s2c.bin").readBytes()

    var pos = 9; pos++; val ll = raw[pos].toInt() and 0xFF; pos++; pos += ll

    // Collect worldlist segments
    val segments = mutableListOf<ByteArray>()
    while (pos < raw.size) {
        val rb = raw[pos].toInt() and 0xFF; pos++
        val iv = isaac.nextInt()
        val decoded = (rb - iv) and 0xFF
        val opcode = if (decoded < 128) decoded else {
            val rb2 = raw[pos].toInt() and 0xFF; pos++
            val d2 = (rb2 - isaac.nextInt()) and 0xFF
            (decoded - 128) * 256 + d2
        }
        if (opcode > 217) break
        val sm = codec.serverProtSize(opcode)
        val pktSize = when { sm == 0 -> 0; sm == -1 -> { val s = raw[pos].toInt() and 0xFF; pos++; s }; sm == -2 -> { val s = ((raw[pos].toInt() and 0xFF) shl 8) or (raw[pos+1].toInt() and 0xFF); pos += 2; s }; else -> sm }
        if (pos + pktSize > raw.size) break
        val data = raw.copyOfRange(pos, pos + pktSize)
        pos += pktSize
        
        if (opcode == 159) {
            val frame = data[0].toInt() and 0xFF
            segments.add(data.copyOfRange(1, data.size)) // skip frame byte
            println("WorldList segment: frame=$frame, ${data.size-1} payload bytes")
            if (frame == 1) break // last segment
        }
    }
    
    // Reassemble
    val total = segments.sumOf { it.size }
    val buf = ByteArray(total)
    var off = 0
    for (seg in segments) { System.arraycopy(seg, 0, buf, off, seg.size); off += seg.size }
    println("Reassembled: $total bytes\n")
    
    // Now decode
    var p = 0
    fun g1() = buf[p++].toInt() and 0xFF
    fun g2(): Int { val v = ((buf[p].toInt() and 0xFF) shl 8) or (buf[p+1].toInt() and 0xFF); p += 2; return v }
    fun g4(): Int { val v = ((buf[p].toInt() and 0xFF) shl 24) or ((buf[p+1].toInt() and 0xFF) shl 16) or ((buf[p+2].toInt() and 0xFF) shl 8) or (buf[p+3].toInt() and 0xFF); p += 4; return v }
    fun smart(): Int = if ((buf[p].toInt() and 0xFF) < 0x80) g1() else g2() - 0x8000
    fun gjstr2(): String {
        val ver = g1()
        if (ver != 0) return "<ver=$ver>"
        val sb = StringBuilder()
        while (buf[p].toInt() != 0) { sb.append(buf[p].toInt().toChar()); p++ }
        p++
        return sb.toString()
    }
    
    val refresh = g1(); println("refresh=$refresh")
    val sep = g1(); println("separator=$sep")
    
    val countryCount = smart(); println("countryCount=$countryCount")
    val countries = mutableListOf<Pair<Int, String>>()
    repeat(countryCount) {
        val cid = smart(); val cname = gjstr2()
        countries.add(cid to cname)
        println("  country[$it]: id=$cid name='$cname'")
    }
    
    val minWorld = smart(); println("minWorldId=$minWorld")
    val maxWorld = smart(); println("maxWorldId=$maxWorld")
    val worldCount = smart(); println("worldCount=$worldCount")
    
    repeat(worldCount.coerceAtMost(5)) { i ->
        val offset = smart()
        val idx = g1()
        val flags = g4()
        val actPres = smart()
        val act = if (actPres != 0) gjstr2() else ""
        val host = gjstr2()
        val addr = gjstr2()
        println("  world[$i]: num=${minWorld+offset} idx=$idx flags=0x${"%X".format(flags)} actPres=$actPres act='$act' host='$host' addr='$addr'")
    }
    // Skip remaining worlds
    if (worldCount > 5) {
        println("  ... (${worldCount - 5} more worlds)")
        repeat(worldCount - 5) {
            smart(); g1(); g4()
            val ap = smart(); if (ap != 0) gjstr2()
            gjstr2(); gjstr2()
        }
    }
    
    val rev = g4(); println("revision=$rev")
    
    println("\n=== Player counts ===")
    var pcCount = 0
    while (p < buf.size && pcCount < 5) {
        val wn = smart(); val pc = g2()
        println("  world $wn: players=${if (pc == 0xFFFF) "unchanged" else pc.toString()}")
        pcCount++
    }
    if (p < buf.size) println("  ... (more)")
    println("\nTotal parsed: $p/$total bytes")
}
