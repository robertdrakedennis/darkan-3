package org.darkan.tools

import org.darkan.tools.recorder.Capture
import org.darkan.tools.recorder.CaptureReader
import org.darkan.tools.recorder.CaptureRecord
import org.darkan.tools.recorder.ConnectionAssembler
import org.darkan.tools.recorder.Dir
import org.darkan.tools.recorder.Options
import org.darkan.tools.recorder.Role
import java.io.File

private const val BLOCK_SIZE = 102_400
private const val CONTINUATION_HEADER_LEN = 5
private const val RESPONSE_HEADER_LEN = 10
private const val MAX_COMPRESSED_SIZE = 50_000_000

private data class Js5FileRequest(val opcode: Int, val index: Int, val group: Int)
private data class Js5FileResponse(val index: Int, val group: Int, val wireBytes: Int)

fun main(args: Array<String>) {
    require(args.isNotEmpty()) {
        "usage: Js5CaptureSummary <capture.bin> [lobby/js5-port] [world-port]"
    }
    val captureFile = File(args[0])
    val js5Port = args.getOrNull(1)?.toIntOrNull() ?: 43596
    val worldPort = args.getOrNull(2)?.toIntOrNull() ?: 43597
    val capture = CaptureReader.read(captureFile).dedupeIo()
    val opts = Options(captureFile, null, null, js5Port, worldPort, js5Port, -1, false, false, false)
    val js5Connections = ConnectionAssembler.assemble(capture, opts).filter { it.role == Role.JS5 }

    println("capture=${captureFile.name} pid=${capture.pid} records=${capture.records.size} js5Connections=${js5Connections.size}")
    val rows = js5Connections.map { conn ->
        val requests = parseRequests(conn.c2s)
        val responses = parseResponses(conn.s2c)
        val req12 = requests.filter { it.index == 12 }.map { it.group }
        val resp12 = responses.filter { it.index == 12 }.map { it.group }
        val allReq = requests.groupingBy { it.index }.eachCount().toSortedMap()
        val allResp = responses.groupingBy { it.index }.eachCount().toSortedMap()
        val reqGroups = requests.groupBy({ it.index }, { it.group }).toSortedMap()
        val respGroups = responses.groupBy({ it.index }, { it.group }).toSortedMap()
        ConnSummary(
            epoch = conn.epoch,
            fd = conn.fd,
            c2sBytes = conn.c2s.size,
            s2cBytes = conn.s2c.size,
            req12 = req12,
            resp12 = resp12,
            reqByIndex = allReq,
            respByIndex = allResp,
            reqGroups = reqGroups,
            respGroups = respGroups,
        )
    }

    println("epoch fd c2s s2c req12 uniqReq12 firstReq12 lastReq12 resp12 uniqResp12 firstResp12 lastResp12")
    rows.filter { it.req12.isNotEmpty() || it.resp12.isNotEmpty() }
        .let { summaries ->
            val head = summaries.take(16)
            val tail = summaries.takeLast(16).filter { it !in head }
            (head + tail).forEach { println(it.format()) }
            if (summaries.size > head.size + tail.size) println("... ${summaries.size - head.size - tail.size} row(s) omitted ...")
        }

    val uniqueReq12 = rows.flatMap { it.req12 }.toSortedSet()
    val uniqueResp12 = rows.flatMap { it.resp12 }.toSortedSet()
    println(
        "unique index12 requests=${uniqueReq12.size} range=${uniqueReq12.rangeString()} " +
            "responses=${uniqueResp12.size} range=${uniqueResp12.rangeString()} " +
            "requestedNotResponded=${(uniqueReq12 - uniqueResp12).size}"
    )
    val reqByIndex = rows.flatMap { row -> row.reqByIndex.entries.map { it.key to it.value } }
        .groupingBy { it.first }.fold(0) { acc, item -> acc + item.second }.toSortedMap()
    val respByIndex = rows.flatMap { row -> row.respByIndex.entries.map { it.key to it.value } }
        .groupingBy { it.first }.fold(0) { acc, item -> acc + item.second }.toSortedMap()
    println("requestsByIndex=$reqByIndex")
    println("responsesByIndex=$respByIndex")
    println("requestGroups=${formatGroups(rows.flatMap { row -> row.reqGroups.entries })}")
    println("responseGroups=${formatGroups(rows.flatMap { row -> row.respGroups.entries })}")
}

private data class ConnSummary(
    val epoch: Int,
    val fd: Int,
    val c2sBytes: Int,
    val s2cBytes: Int,
    val req12: List<Int>,
    val resp12: List<Int>,
    val reqByIndex: Map<Int, Int>,
    val respByIndex: Map<Int, Int>,
    val reqGroups: Map<Int, List<Int>>,
    val respGroups: Map<Int, List<Int>>,
) {
    fun format(): String {
        val reqSet = req12.toSortedSet()
        val respSet = resp12.toSortedSet()
        return listOf(
            epoch,
            fd,
            c2sBytes,
            s2cBytes,
            req12.size,
            reqSet.size,
            reqSet.firstOrNull() ?: "-",
            reqSet.lastOrNull() ?: "-",
            resp12.size,
            respSet.size,
            respSet.firstOrNull() ?: "-",
            respSet.lastOrNull() ?: "-",
        ).joinToString(" ")
    }
}

private fun Capture.dedupeIo(): Capture {
    var lastFd = Int.MIN_VALUE
    var lastDir: Dir? = null
    var lastLen = -1
    var lastHead = -1
    var lastTail = -1
    var lastNs = Long.MIN_VALUE
    var dropped = 0
    val filtered = records.filterNot { record ->
        if (record !is CaptureRecord.Io) return@filterNot false
        val bytes = record.bytes
        val head = bytes.firstOrNull()?.toInt()?.and(0xFF) ?: -1
        val tail = bytes.lastOrNull()?.toInt()?.and(0xFF) ?: -1
        val duplicate = record.fd == lastFd &&
            record.dir == lastDir &&
            bytes.size == lastLen &&
            head == lastHead &&
            tail == lastTail &&
            record.tsNanos - lastNs in 0..2_000_000L
        lastFd = record.fd
        lastDir = record.dir
        lastLen = bytes.size
        lastHead = head
        lastTail = tail
        lastNs = record.tsNanos
        if (duplicate) dropped++
        duplicate
    }
    if (dropped > 0) println("dedupedIo=$dropped")
    return copy(records = filtered)
}

private fun parseRequests(bytes: ByteArray): List<Js5FileRequest> {
    val requests = mutableListOf<Js5FileRequest>()
    var offset = 0
    while (offset < bytes.size) {
        val opcode = bytes[offset].toInt() and 0xFF
        when {
            opcode == 15 && offset + 2 <= bytes.size -> {
                val size = bytes[offset + 1].toInt() and 0xFF
                offset += 2 + size
            }
            opcode in intArrayOf(3, 6, 7) && offset + 10 <= bytes.size -> offset += 10
            (opcode and 0x0E) == 0 && offset + 10 <= bytes.size -> {
                val index = bytes[offset + 1].toInt() and 0xFF
                val group = bytes.readInt(offset + 2)
                requests += Js5FileRequest(opcode, index, group)
                offset += 10
            }
            else -> offset++
        }
    }
    return requests
}

private fun parseResponses(bytes: ByteArray): List<Js5FileResponse> {
    val responses = mutableListOf<Js5FileResponse>()
    var offset = 0
    while (offset + RESPONSE_HEADER_LEN <= bytes.size) {
        val index = bytes[offset].toInt() and 0xFF
        val hash = bytes.readInt(offset + 1)
        val compression = bytes[offset + 5].toInt() and 0xFF
        val compressedSize = bytes.readInt(offset + 6)
        if (compression !in 0..4 || compressedSize < 0 || compressedSize > MAX_COMPRESSED_SIZE) {
            offset++
            continue
        }
        val bodyBytes = compressedSize + if (compression != 0) 4 else 0
        val totalContent = RESPONSE_HEADER_LEN + bodyBytes
        val continuations = if (totalContent > BLOCK_SIZE) {
            (totalContent - BLOCK_SIZE + (BLOCK_SIZE - CONTINUATION_HEADER_LEN) - 1) / (BLOCK_SIZE - CONTINUATION_HEADER_LEN)
        } else {
            0
        }
        val wireBytes = totalContent + continuations * CONTINUATION_HEADER_LEN
        if (offset + wireBytes > bytes.size) break
        responses += Js5FileResponse(index, hash and 0x7FFFFFFF.toInt(), wireBytes)
        offset += wireBytes
    }
    return responses
}

private fun ByteArray.readInt(offset: Int): Int {
    return ((this[offset].toInt() and 0xFF) shl 24) or
        ((this[offset + 1].toInt() and 0xFF) shl 16) or
        ((this[offset + 2].toInt() and 0xFF) shl 8) or
        (this[offset + 3].toInt() and 0xFF)
}

private fun Iterable<Int>.rangeString(): String {
    val sorted = toSortedSet()
    return if (sorted.isEmpty()) "-" else "${sorted.first()}..${sorted.last()}"
}

private fun formatGroups(entries: List<Map.Entry<Int, List<Int>>>): Map<Int, String> {
    return entries.groupBy({ it.key }, { it.value })
        .mapValues { (_, lists) ->
            val groups = lists.flatten().toSortedSet()
            if (groups.size <= 20) groups.joinToString(",", prefix = "[", postfix = "]")
            else "${groups.first()}..${groups.last()} (${groups.size})"
        }
        .toSortedMap()
}
