package org.darkan.tools

import org.darkan.core.net.Isaac
import org.darkan.core.net.prot.revision.rev947.register947
import java.io.File

fun main() {
    val dir = File("capture").listFiles()?.filter { it.isDirectory }?.maxByOrNull { it.name }?.absolutePath
        ?: error("No captures")
    val codec = register947()
    val keysLine = File("$dir/isaac-keys.txt").readLines().first { "hex" in it }
    val keys = Regex("0x([0-9A-Fa-f]+)").findAll(keysLine).map { it.groupValues[1].toLong(16).toInt() }.toList().toIntArray()
    val s2cKeys = IntArray(4) { keys[it] + 50 }
    val isaac = Isaac(s2cKeys)
    val raw = File("$dir/raw-s2c.bin").readBytes()

    var pos = 9; pos++; val ll = raw[pos].toInt() and 0xFF; pos++; pos += ll

    // Collect ALL worldlist segments including frame bytes
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
            segments.add(data) // full packet data including frame byte
            val frame = data[0].toInt() and 0xFF
            println("Segment ${segments.size}: ${data.size} bytes, frame=$frame")
            if (frame == 1) break
        }
    }
    
    // Write each segment to a file for the server to replay
    val outDir = File("data/worldlist-capture")
    outDir.mkdirs()
    segments.forEachIndexed { i, seg ->
        File(outDir, "segment-$i.bin").writeBytes(seg)
        println("Wrote segment-$i.bin: ${seg.size} bytes")
    }
    println("Total segments: ${segments.size}")
}
