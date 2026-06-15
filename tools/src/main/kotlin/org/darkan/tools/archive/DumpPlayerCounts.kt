// org.darkan.tools.archive: unmaintained one-shot experiments kept for reference only.
// These tools were written against specific captures/revisions, are not part of any
// build task, and may rely on stale capture data or stale opcode identities.
package org.darkan.tools.archive

import org.darkan.core.net.Isaac
import org.darkan.core.net.prot.revision.rev948.register948
import world.gregs.voidps.buffer.read.BufferReader
import java.io.File

fun main() {
    val dir = File("capture").listFiles()?.filter { it.isDirectory }?.maxByOrNull { it.name }?.absolutePath ?: error("No captures")
    val codec = register948()
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
    val reader = BufferReader(buf)
    fun gjstr2(): String { reader.readUnsignedByte(); return reader.readString() }

    val refresh = reader.readUnsignedByte(); val sep = reader.readUnsignedByte()
    val countryCount = reader.readSmart(); repeat(countryCount) { reader.readSmart(); gjstr2() }
    val minWorld = reader.readSmart(); val maxWorld = reader.readSmart(); val worldCount = reader.readSmart()
    println("minWorldId=$minWorld, maxWorldId=$maxWorld, worldCount=$worldCount")

    repeat(worldCount) { reader.readSmart(); reader.readUnsignedByte(); reader.readInt(); val ap = reader.readSmart(); if (ap != 0) gjstr2(); gjstr2(); gjstr2() }
    val rev = reader.readInt()
    println("revision=$rev")
    println("Player count section starts at byte ${reader.position()}/${buf.size}")

    // Now decode player counts
    println("\nFirst 10 player count entries:")
    for (i in 0 until minOf(10, worldCount)) {
        val wn = reader.readSmart()
        val pc = reader.readUnsignedShort()
        println("  raw_smart=$wn (actualIfOffset=${minWorld+wn}) players=${if (pc == 0xFFFF) "unchanged" else pc.toString()}")
    }
}
