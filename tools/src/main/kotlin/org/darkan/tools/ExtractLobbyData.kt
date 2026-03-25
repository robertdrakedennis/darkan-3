package org.darkan.tools

import org.darkan.core.net.Isaac
import org.darkan.core.net.prot.Codec
import org.darkan.core.net.prot.revision.rev947.register947
import java.io.File
import java.io.PrintWriter

/**
 * Extracts varps, varcs, and varc strings from a raw capture,
 * outputting them in the format used by lobby-varps.txt and lobby-varcs.txt.
 */
fun main(args: Array<String>) {
    val dir = args.getOrNull(0) ?: File("capture").listFiles()?.filter { it.isDirectory }?.maxByOrNull { it.name }?.absolutePath
        ?: error("No captures found")
    println("Extracting lobby data from: $dir")

    val codec = register947()
    val keysLine = File("$dir/isaac-keys.txt").readLines().first { "hex" in it }
    val keys = Regex("0x([0-9A-Fa-f]+)").findAll(keysLine).map { it.groupValues[1].toLong(16).toInt() }.toList().toIntArray()
    val s2cKeys = IntArray(4) { keys[it] + 50 }
    val isaac = Isaac(s2cKeys)
    val raw = File("$dir/raw-s2c.bin").readBytes()

    // Skip login header
    var pos = 9
    pos++ // login result
    val loginLen = raw[pos].toInt() and 0xFF; pos++
    pos += loginLen

    data class VarpEntry(val id: Int, val value: Long, val isLong: Boolean = false)
    data class VarcEntry(val id: Int, val value: Any) // Int or String

    val varps = mutableListOf<VarpEntry>()
    val preIfVarcs = mutableListOf<VarcEntry>()
    val postIfVarcs = mutableListOf<VarcEntry>()
    val varcStrs = mutableListOf<Pair<Int, String>>()
    var seenInterface = false
    var seenSetEvents = false

    while (pos < raw.size) {
        val rawByte = raw[pos].toInt() and 0xFF; pos++
        val decoded = (rawByte - isaac.nextInt()) and 0xFF
        val opcode = if (decoded < 128) decoded else {
            if (pos >= raw.size) break
            val rb2 = raw[pos].toInt() and 0xFF; pos++
            val d2 = (rb2 - isaac.nextInt()) and 0xFF
            (decoded - 128) * 256 + d2
        }
        if (opcode > 217) break

        val sm = codec.serverProtSize(opcode)
        val pktSize = when {
            sm == 0 -> 0
            sm == -1 -> { val s = raw[pos].toInt() and 0xFF; pos++; s }
            sm == -2 -> { val s = ((raw[pos].toInt() and 0xFF) shl 8) or (raw[pos+1].toInt() and 0xFF); pos += 2; s }
            else -> sm
        }
        if (pos + pktSize > raw.size) break
        val data = raw.copyOfRange(pos, pos + pktSize)
        pos += pktSize

        when (opcode) {
            // VarpSmall (10, 3B): LE short id + raw byte value
            10 -> {
                val id = (data[0].toInt() and 0xFF) or ((data[1].toInt() and 0xFF) shl 8)
                val value = data[2].toInt() // signed byte
                varps.add(VarpEntry(id, value.toLong()))
            }
            // VarpLarge (111, 6B): middle-endian int value + LE short id
            111 -> {
                val v = ((data[2].toInt() and 0xFF) shl 24) or ((data[3].toInt() and 0xFF) shl 16) or
                        ((data[0].toInt() and 0xFF) shl 8) or (data[1].toInt() and 0xFF)
                val id = (data[4].toInt() and 0xFF) or ((data[5].toInt() and 0xFF) shl 8)
                varps.add(VarpEntry(id, v.toLong()))
            }
            // VarpLong (170, 10B): 8B long + LE short id
            170 -> {
                var v = 0L
                for (i in 0..7) v = (v shl 8) or (data[i].toLong() and 0xFF)
                val id = (data[8].toInt() and 0xFF) or ((data[9].toInt() and 0xFF) shl 8)
                varps.add(VarpEntry(id, v, isLong = true))
            }
            // ClientSetVarcSmall (1, 3B): byteSubtract(value) + LE short id
            1 -> {
                val rawVal = data[0].toInt() and 0xFF
                val value = (0x80 - rawVal) and 0xFF
                val signedVal = if (value > 127) value - 256 else value
                val id = (data[1].toInt() and 0xFF) or ((data[2].toInt() and 0xFF) shl 8)
                val target = if (seenSetEvents) postIfVarcs else if (seenInterface) postIfVarcs else preIfVarcs
                target.add(VarcEntry(id, signedVal))
            }
            // ClientSetVarcLarge (112, 6B): intBE(value) + LE short id
            112 -> {
                val v = ((data[0].toInt() and 0xFF) shl 24) or ((data[1].toInt() and 0xFF) shl 16) or
                        ((data[2].toInt() and 0xFF) shl 8) or (data[3].toInt() and 0xFF)
                val id = (data[4].toInt() and 0xFF) or ((data[5].toInt() and 0xFF) shl 8)
                val target = if (seenSetEvents) postIfVarcs else if (seenInterface) postIfVarcs else preIfVarcs
                target.add(VarcEntry(id, v))
            }
            // ClientSetVarcStr (67, varByte): string + LE short id
            67 -> {
                val sb = StringBuilder()
                var i = 0
                while (i < data.size && data[i].toInt() != 0) { sb.append(data[i].toInt().toChar()); i++ }
                i++ // skip null
                val id = if (i + 1 < data.size) (data[i].toInt() and 0xFF) or ((data[i+1].toInt() and 0xFF) shl 8) else 0
                varcStrs.add(id to sb.toString())
            }
            // Track interface open for pre/post varc split
            94 -> seenInterface = true
            35 -> seenSetEvents = true
        }
    }

    // Sort varps by ID
    varps.sortBy { it.id }

    // Write lobby-varps.txt
    val varpsFile = File("lobby/src/main/resources/capture/lobby-varps.txt")
    PrintWriter(varpsFile).use { pw ->
        pw.println("# Varps from Jagex 947-1 live capture ($dir)")
        pw.println("# Format: id value")
        pw.println("# ${varps.size} entries, ${varps.count { it.isLong }} longs")
        for (v in varps) {
            pw.println("${v.id} ${v.value}")
        }
    }
    println("Wrote ${varps.size} varps to ${varpsFile.path}")

    // Write lobby-varcs.txt
    val varcsFile = File("lobby/src/main/resources/capture/lobby-varcs.txt")
    PrintWriter(varcsFile).use { pw ->
        pw.println("# Varcs from Jagex 947-1 live capture ($dir)")
        pw.println("# Format: id value (ints only, strings handled separately)")
        pw.println("# Lines before '---' are pre-interface, after are post-interface")
        for (v in preIfVarcs) pw.println("${v.id} ${v.value}")
        pw.println("---")
        for (v in postIfVarcs) pw.println("${v.id} ${v.value}")
    }
    println("Wrote ${preIfVarcs.size} pre-if + ${postIfVarcs.size} post-if varcs to ${varcsFile.path}")

    // Print varc strings
    if (varcStrs.isNotEmpty()) {
        println("\nVarc strings:")
        for ((id, str) in varcStrs) println("  varc[$id] = \"$str\"")
    }

    // Summary
    println("\nSummary:")
    println("  Varps: ${varps.size} (${varps.count { !it.isLong }} int + ${varps.count { it.isLong }} long)")
    println("  Varcs: ${preIfVarcs.size} pre-if + ${postIfVarcs.size} post-if = ${preIfVarcs.size + postIfVarcs.size} total")
    println("  Varc strings: ${varcStrs.size}")
}
