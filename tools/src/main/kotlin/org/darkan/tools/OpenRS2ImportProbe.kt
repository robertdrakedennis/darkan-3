package org.darkan.tools

import org.darkan.tools.util.parseRefTable
import world.gregs.voidps.buffer.read.BufferReader
import world.gregs.voidps.cache.compress.DecompressionContext
import java.nio.file.Files
import java.nio.file.Paths

/** Ad-hoc probe: decompress a raw openrs2 group .dat and report its decompressed size and
 *  multi-file header, alongside the index's ref-table file count for that archive. */
fun main(args: Array<String>) {
    val base = Paths.get(args.getOrNull(0) ?: "/Users/robert/darkan-3/openrs2-948/cache")
    val index = args.getOrNull(1)?.toInt() ?: 19

    val ctx = DecompressionContext()

    // Scan mode: probe EVERY multi-file group in `index` and report how each layout fares.
    if (args.getOrNull(2) == "scan") {
        scanIndex(ctx, base, index)
        ctx.close()
        return
    }

    val archive = args.getOrNull(2)?.toInt() ?: 0

    // Ref-table file count for this archive.
    val refRaw = Files.readAllBytes(base.resolve("255/$index.dat"))
    val refTable = parseRefTable(ctx, refRaw, parseFiles = true)
    val entry = refTable?.entries?.firstOrNull { it.id == archive }
    println("index $index archive $archive: refTable format=${refTable?.format} fileCount=${entry?.fileCount} version=${entry?.version} crc=${entry?.crc}")

    // Decompress the group (strip trailer first, same as converter).
    val raw = Files.readAllBytes(base.resolve("$index/$archive.dat"))
    val comp = raw[0].toInt() and 0xFF
    val csize = ((raw[1].toInt() and 0xFF) shl 24) or ((raw[2].toInt() and 0xFF) shl 16) or
        ((raw[3].toInt() and 0xFF) shl 8) or (raw[4].toInt() and 0xFF)
    val containerLen = 5 + csize + if (comp != 0) 4 else 0
    val blob = if (containerLen < raw.size) raw.copyOfRange(0, containerLen) else raw
    println("raw=${raw.size} comp=$comp csize=$csize containerLen=$containerLen blob=${blob.size}")

    val dec = ctx.decompress(blob)
    if (dec == null) {
        println("DECOMPRESS RETURNED NULL")
        return
    }
    println("decompressed=${dec.size} firstByte=${dec[0].toInt() and 0xFF}")

    // If multi-file modern format (first byte == 1), print the offset table for fileCount files.
    val fc = entry?.fileCount ?: 0
    if (dec.isNotEmpty() && (dec[0].toInt() and 0xFF) == 1 && fc > 0) {
        val r = BufferReader(dec)
        r.readByte()
        val offsets = IntArray(fc + 1) { r.readUnsignedMedium() }
        println("modern multi-file: offsets[0]=${offsets[0]} offsets[last]=${offsets[fc]} sum-implied-size=${offsets[fc] - offsets[0]}")
        println("bytes available after header = ${dec.size - r.position()} (need ${offsets[fc] - offsets[0]})")
    } else {
        println("single-file or non-modern layout (fileCount=$fc)")
    }

    // Try the canonical RS3 trailing-stripe layout (parseLegacyMultiFile equivalent):
    // last byte = chunk count; preceding (chunks * fileCount * 4) ints are per-chunk size deltas.
    if (fc > 1) {
        try {
            val r = BufferReader(dec)
            var off = dec.size
            val chunks = dec[--off].toInt() and 0xFF
            val tableStart = off - chunks * fc * 4
            r.position(tableStart)
            val sizes = IntArray(fc)
            for (c in 0 until chunks) {
                var prev = 0
                for (f in 0 until fc) {
                    prev += r.readInt()
                    sizes[f] += prev
                }
            }
            val totalFileBytes = sizes.sum()
            val payloadBytes = tableStart // file data occupies [0, tableStart)
            println("legacy trailing-stripe: chunks=$chunks tableStart=$tableStart totalFileBytes=$totalFileBytes payloadBytes=$payloadBytes match=${totalFileBytes == payloadBytes}")
            println("  first 4 file sizes = ${sizes.take(4)}  nonzero files = ${sizes.count { it > 0 }}")
        } catch (e: Exception) {
            println("legacy parse EXC ${e::class.simpleName}: ${e.message}")
        }
    }
    ctx.close()
}

private fun scanIndex(ctx: DecompressionContext, base: java.nio.file.Path, index: Int) {
    val refRaw = Files.readAllBytes(base.resolve("255/$index.dat"))
    val refTable = parseRefTable(ctx, refRaw, parseFiles = true) ?: error("ref parse failed")
    var multi = 0
    var legacyOk = 0
    var modernOk = 0
    var firstByteOne = 0
    val modernOnly = ArrayList<Int>()
    for (e in refTable.entries) {
        if (e.fileCount <= 1) continue
        multi++
        val raw = Files.readAllBytes(base.resolve("$index/${e.id}.dat"))
        val comp = raw[0].toInt() and 0xFF
        val csize = ((raw[1].toInt() and 0xFF) shl 24) or ((raw[2].toInt() and 0xFF) shl 16) or
            ((raw[3].toInt() and 0xFF) shl 8) or (raw[4].toInt() and 0xFF)
        val cl = 5 + csize + if (comp != 0) 4 else 0
        val blob = if (cl < raw.size) raw.copyOfRange(0, cl) else raw
        val dec = ctx.decompress(blob) ?: continue
        if ((dec[0].toInt() and 0xFF) == 1) firstByteOne++
        // legacy
        var lok = false
        try {
            val r = BufferReader(dec); var off = dec.size
            val chunks = dec[--off].toInt() and 0xFF
            val ts = off - chunks * e.fileCount * 4
            if (ts >= 0) {
                r.position(ts); val s = IntArray(e.fileCount)
                for (c in 0 until chunks) { var p = 0; for (f in 0 until e.fileCount) { p += r.readInt(); s[f] += p } }
                lok = s.sum() == ts && s.all { it >= 0 }
            }
        } catch (_: Exception) {}
        if (lok) legacyOk++
        // modern
        var mok = false
        try {
            val r = BufferReader(dec); r.readByte()
            val o = IntArray(e.fileCount + 1) { r.readUnsignedMedium() }
            mok = o[0] == 0 && (1..e.fileCount).all { o[it] >= o[it - 1] } && o[e.fileCount] <= dec.size
        } catch (_: Exception) {}
        if (mok) modernOk++
        if (mok && !lok) modernOnly.add(e.id)
    }
    println("index $index: multiFileGroups=$multi firstByte==1: $firstByteOne legacyTrailingStripeOk=$legacyOk modernOffsetTableOk=$modernOk modernOnly=${modernOnly.take(20)}")
}
