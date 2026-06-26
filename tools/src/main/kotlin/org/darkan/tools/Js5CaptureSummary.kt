package org.darkan.tools

import org.darkan.core.net.RequestOpcode
import org.darkan.tools.recorder.Capture
import org.darkan.tools.recorder.CaptureReader
import org.darkan.tools.recorder.CaptureRecord
import org.darkan.tools.recorder.Dir
import org.darkan.tools.recorder.Options
import org.darkan.tools.recorder.Role
import java.io.ByteArrayOutputStream
import java.io.File

private const val BLOCK_SIZE = 102_400
private const val CONTINUATION_HEADER_LEN = 5
private const val RESPONSE_HEADER_LEN = 10
private const val REQUEST_LEN = 10
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
    val opts = Options(captureFile, null, null, js5Port, worldPort, js5Port, -1, false, false, false, false, false)
    val rows = summarizeJs5Connections(capture, opts)

    println("capture=${captureFile.name} pid=${capture.pid} records=${capture.records.size} js5Connections=${rows.size}")
    println("epoch fd c2s s2c req12 uniqReq12 firstReq12 lastReq12 resp12 uniqResp12 firstResp12 lastResp12")
    rows.filter { it.req12.isNotEmpty() || it.resp12.isNotEmpty() }
        .let { summaries ->
            val head = summaries.take(16)
            val tail = summaries.takeLast(16).filter { it !in head }
            (head + tail).forEach { println(it.format()) }
            if (summaries.size > head.size + tail.size) println("... ${summaries.size - head.size - tail.size} row(s) omitted ...")
        }

    printIndexSummary(rows, 5)
    printIndexSummary(rows, 12)
    println("requestsByIndex=${countByIndex(rows) { it.reqByIndex }}")
    println("responsesByIndex=${countByIndex(rows) { it.respByIndex }}")
    println("requestGroups=${formatGroups(rows.flatMap { row -> row.reqGroups.entries })}")
    println("responseGroups=${formatGroups(rows.flatMap { row -> row.respGroups.entries })}")
}

private fun summarizeJs5Connections(capture: Capture, opts: Options): List<ConnSummary> {
    class Acc(
        val epoch: Int,
        var peer: String?,
        var port: Int,
        var role: Role,
    ) {
        var c2sBytes = 0
        var s2cBytes = 0
        val requests = ArrayList<Js5FileRequest>()
        val responses = ArrayList<Js5FileResponse>()
        val requestParser = Js5RequestParser()
        val responseParser = Js5ResponseParser()

        fun summary(fd: Int): ConnSummary {
            val req12 = requests.filter { it.index == 12 }.map { it.group }
            val resp12 = responses.filter { it.index == 12 }.map { it.group }
            return ConnSummary(
                epoch = epoch,
                fd = fd,
                c2sBytes = c2sBytes,
                s2cBytes = s2cBytes,
                req12 = req12,
                resp12 = resp12,
                reqByIndex = requests.groupingBy { it.index }.eachCount().toSortedMap(),
                respByIndex = responses.groupingBy { it.index }.eachCount().toSortedMap(),
                reqGroups = requests.groupBy({ it.index }, { it.group }).toSortedMap(),
                respGroups = responses.groupBy({ it.index }, { it.group }).toSortedMap(),
            )
        }
    }

    val epochOf = HashMap<Int, Int>()
    val current = HashMap<Int, Acc>()
    val finished = ArrayList<ConnSummary>()

    fun trackPort(port: Int): Boolean = port == opts.js5Port || port == opts.lobbyPort

    fun finalize(fd: Int) {
        val acc = current.remove(fd) ?: return
        if (acc.role != Role.JS5 || (acc.c2sBytes == 0 && acc.s2cBytes == 0)) return
        finished.add(acc.summary(fd))
    }

    for (rec in capture.records) {
        when (rec) {
            is CaptureRecord.Connect -> {
                finalize(rec.fd)
                if (!trackPort(rec.port)) continue
                val epoch = (epochOf[rec.fd] ?: -1) + 1
                epochOf[rec.fd] = epoch
                current[rec.fd] = Acc(
                    epoch = epoch,
                    peer = "${rec.addrString()}:${rec.port}",
                    port = rec.port,
                    role = opts.roleForPort(rec.port),
                )
            }
            is CaptureRecord.Io -> {
                val firstByte = rec.bytes.firstOrNull()?.toInt()
                val acc = current[rec.fd] ?: run {
                    if (rec.dir != Dir.OUT || firstByte == null) continue
                    val role = opts.roleForFirstClientByte(-1, firstByte) ?: continue
                    if (role != Role.JS5) continue
                    val epoch = (epochOf[rec.fd] ?: -1) + 1
                    epochOf[rec.fd] = epoch
                    Acc(epoch, null, -1, role).also { current[rec.fd] = it }
                }
                if (rec.dir == Dir.OUT && rec.bytes.isNotEmpty() && acc.role == Role.UNKNOWN) {
                    opts.roleForFirstClientByte(acc.port, rec.bytes[0].toInt())?.let { acc.role = it }
                }
                if (acc.role != Role.JS5 && acc.role != Role.UNKNOWN) continue
                when (rec.dir) {
                    Dir.IN -> if (acc.role == Role.JS5) {
                        acc.s2cBytes += rec.bytes.size
                        acc.responses += acc.responseParser.feed(rec.bytes)
                    }
                    Dir.OUT -> if (acc.role == Role.JS5) {
                        acc.c2sBytes += rec.bytes.size
                        acc.requests += acc.requestParser.feed(rec.bytes)
                    }
                }
            }
            is CaptureRecord.Close -> finalize(rec.fd)
            else -> {}
        }
    }
    current.keys.toList().forEach { finalize(it) }
    return finished.sortedWith(compareBy({ it.epoch }, { it.fd }))
}

private class Js5RequestParser {
    private val buffer = ByteArrayOutputStream()

    fun feed(bytes: ByteArray): List<Js5FileRequest> {
        buffer.write(bytes)
        val data = buffer.toByteArray()
        val requests = ArrayList<Js5FileRequest>()
        var offset = 0
        parse@ while (offset < data.size) {
            val opcode = data[offset].toInt() and 0xFF
            if (opcode == RequestOpcode.JS5_INIT) {
                if (offset + 2 > data.size) break@parse
                val size = data[offset + 1].toInt() and 0xFF
                if (offset + 2 + size > data.size) break@parse
                offset += 2 + size
                continue@parse
            }
            if (offset + REQUEST_LEN > data.size) break@parse
            if (RequestOpcode.isFileRequest(opcode)) {
                val group = data.readInt(offset + 2)
                if (group >= 0) {
                    requests += Js5FileRequest(opcode, data[offset + 1].toInt() and 0xFF, group)
                }
            }
            offset += REQUEST_LEN
        }
        retainTail(data, offset)
        return requests
    }

    private fun retainTail(data: ByteArray, offset: Int) {
        buffer.reset()
        if (offset < data.size) buffer.write(data, offset, data.size - offset)
    }
}

private data class PendingJs5Response(
    val response: Js5FileResponse,
    var remainingWireBytes: Int,
)

private class Js5ResponseParser {
    private val buffer = ByteArrayOutputStream()
    private var pending: PendingJs5Response? = null
    private var syncSkipped = false

    fun feed(bytes: ByteArray): List<Js5FileResponse> {
        val responses = ArrayList<Js5FileResponse>()
        var offset = 0
        if (!syncSkipped) {
            if (bytes.isEmpty()) return responses
            offset = 1
            syncSkipped = true
        }
        pending?.let { current ->
            val take = minOf(current.remainingWireBytes, bytes.size - offset)
            current.remainingWireBytes -= take
            offset += take
            if (current.remainingWireBytes == 0) {
                responses += current.response
                pending = null
            } else {
                return responses
            }
        }

        if (offset < bytes.size) buffer.write(bytes, offset, bytes.size - offset)
        drainBuffer(responses)
        return responses
    }

    private fun drainBuffer(responses: MutableList<Js5FileResponse>) {
        val data = buffer.toByteArray()
        var offset = 0
        parse@ while (offset + RESPONSE_HEADER_LEN <= data.size) {
            val index = data[offset].toInt() and 0xFF
            val hash = data.readInt(offset + 1)
            val compression = data[offset + 5].toInt() and 0xFF
            val compressedSize = data.readInt(offset + 6)
            if (compression !in 0..4 || compressedSize < 0 || compressedSize > MAX_COMPRESSED_SIZE) {
                offset++
                continue@parse
            }
            val bodyBytes = compressedSize + if (compression != 0) 4 else 0
            val totalContent = RESPONSE_HEADER_LEN + bodyBytes
            val continuations = if (totalContent > BLOCK_SIZE) {
                (totalContent - BLOCK_SIZE + (BLOCK_SIZE - CONTINUATION_HEADER_LEN) - 1) / (BLOCK_SIZE - CONTINUATION_HEADER_LEN)
            } else {
                0
            }
            val wireBytes = totalContent + continuations * CONTINUATION_HEADER_LEN
            val response = Js5FileResponse(index, hash and Int.MAX_VALUE, wireBytes)
            if (offset + wireBytes <= data.size) {
                responses += response
                offset += wireBytes
            } else {
                pending = PendingJs5Response(response, wireBytes - (data.size - offset))
                offset = data.size
                break@parse
            }
        }
        buffer.reset()
        if (pending == null && offset < data.size) buffer.write(data, offset, data.size - offset)
    }
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

private fun countByIndex(rows: List<ConnSummary>, selector: (ConnSummary) -> Map<Int, Int>): Map<Int, Int> {
    return rows.flatMap { row -> selector(row).entries.map { it.key to it.value } }
        .groupingBy { it.first }.fold(0) { acc, item -> acc + item.second }.toSortedMap()
}

private fun printIndexSummary(rows: List<ConnSummary>, index: Int) {
    val uniqueReq = rows.flatMap { it.reqGroups[index].orEmpty() }.toSortedSet()
    val uniqueResp = rows.flatMap { it.respGroups[index].orEmpty() }.toSortedSet()
    println(
        "unique index$index requests=${uniqueReq.size} range=${uniqueReq.rangeString()} " +
            "responses=${uniqueResp.size} range=${uniqueResp.rangeString()} " +
            "requestedNotResponded=${(uniqueReq - uniqueResp).size}"
    )
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
