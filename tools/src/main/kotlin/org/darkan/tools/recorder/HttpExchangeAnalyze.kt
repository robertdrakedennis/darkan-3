package org.darkan.tools.recorder

import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.EOFException
import java.io.File

/**
 * Wire-level analyzer for the macOS NXT 948-5 recorder capture, focused on the
 * index-40 `/ms` HTTP loop (group 38557 over our localhost:8829 content server).
 *
 * Unlike [CaptureReader]/[RecorderDeframe] — which load every record into RAM and
 * concatenate each fd's directional stream (losing recv() boundaries) — this tool
 * STREAMS the capture in one pass and keeps only small per-fd HTTP state, so it
 * handles the ~325 MB capture without heap pressure and PRESERVES:
 *   - every individual write()/read()/recv() boundary and its ns timestamp,
 *   - connect()/close() lifecycle per fd,
 *   - the exact byte count the client recv'd for each HTTP response,
 *   - the parsed status line + headers and the Content-Length-vs-body comparison.
 *
 * ## Per-fd byte-exact HTTP/1.1 response framer (the load-bearing correctness piece)
 *
 * The client reuses a kept-alive socket for many `/ms` requests (libcurl connection
 * pool). On one fd the inbound stream is therefore `resp1 || resp2 || ...` with NO
 * delimiter other than each response's own `Content-Length`. To attribute bytes to
 * the right request we run a real HTTP/1.1 client-side response framer per fd:
 *   - outbound `GET ...` lines are pushed onto a FIFO of pending requests,
 *   - inbound bytes feed a parser that reads status line + headers up to CRLFCRLF,
 *     then EXACTLY `Content-Length` body bytes, then completes that response and
 *     pops the matching request, leaving any surplus bytes for the next response.
 * This makes each exchange's body-vs-Content-Length comparison byte-exact even
 * across recv() boundaries and across many pipelined responses on one socket.
 *
 * Usage:
 *   ./gradlew :tools:httpExchangeAnalyze -PhttpArgs="<capture.bin> [--filter g=38557] [--max 8] [--port 8829]"
 */
object HttpExchangeAnalyze {

    private const val FILE_MAGIC = 0x444B5243L // "DKRC"

    private const val REC_IO = 0x01
    private const val REC_CONNECT = 0x02
    private const val REC_CLOSE = 0x03
    private const val REC_SEEDS = 0x04
    private const val REC_NOTE = 0x05
    private const val DIR_IN = 0
    // syscall kinds: 0=recv 1=read 2=recvfrom 3=send 4=write 5=sendto

    @JvmStatic
    fun main(args: Array<String>) {
        require(args.isNotEmpty()) {
            "usage: HttpExchangeAnalyze <capture.bin> [--filter <substr-in-request-line>] [--max N] [--port P]"
        }
        val file = File(args[0])
        var filter: String? = null
        var max = 6
        var httpPort = 8829
        var traceFd: Int? = null
        var traceLimit = 80
        var i = 1
        while (i < args.size) {
            when (args[i]) {
                "--filter" -> filter = args[++i]
                "--max" -> max = args[++i].toInt()
                "--port" -> httpPort = args[++i].toInt()
                "--trace-fd" -> traceFd = args[++i].toInt()
                "--trace-limit" -> traceLimit = args[++i].toInt()
                else -> System.err.println("ignoring unknown arg: ${args[i]}")
            }
            i++
        }

        if (traceFd != null) {
            RawFdTracer(traceFd, traceLimit, httpPort).also { tracer ->
                DataInputStream(BufferedInputStream(file.inputStream(), 1 shl 20)).use { input ->
                    require(readU32LE(input) == FILE_MAGIC) { "bad magic" }
                    readU16LE(input); val pid = readU32LE(input)
                    println("# RAW TRACE fd=$traceFd  capture=${file.name} pid=$pid")
                    tracer.run(input)
                }
            }
            return
        }

        val analyzer = Analyzer(filter, max, httpPort)
        DataInputStream(BufferedInputStream(file.inputStream(), 1 shl 20)).use { input ->
            val magic = readU32LE(input)
            require(magic == FILE_MAGIC) { "bad magic 0x${magic.toString(16)} (expected DKRC)" }
            val version = readU16LE(input)
            val pid = readU32LE(input)
            println("# capture: ${file.name}  pid=$pid version=$version  filterReqLine=${filter ?: "(none)"}  httpPort=$httpPort")
            analyzer.run(input)
        }
        analyzer.report()
    }

    /** One completed (or terminated) HTTP response, attributed to its request. */
    private class Exchange(
        val fd: Int,
        val index: Int,
        val reqLine: String,
        val reqRaw: ByteArray,
        val reqStartNs: Long,
    ) {
        var statusLine: String? = null
        var headersText: String? = null
        var contentLength: Int = -1
        var contentType: String? = null
        var transferEncoding: String? = null
        var connectionHeader: String? = null
        var headerByteCount: Int = -1          // status line + headers + CRLFCRLF
        var bodyBytesReceived: Int = 0         // exact body bytes attributed to THIS response
        var inboundTotalThisExchange: Int = 0  // headerByteCount + bodyBytesReceived
        var firstInNs: Long = -1
        var lastInNs: Long = -1
        val bodyReadSizes = ArrayList<Int>()   // recv() chunk sizes that delivered THIS response's bytes
        var complete = false                   // full Content-Length body received
        var terminatedByClose = false
        var terminatedByEof = false
        var bodyTailHex: String? = null        // last bytes of the body (version-suffix check)
    }

    /**
     * Byte-exact HTTP/1.1 client framer for a single fd. Owns the FIFO of pending
     * requests and the streaming parse state for the response currently being read.
     */
    private class FdHttp(val fd: Int) {
        val pendingReqs = ArrayDeque<Exchange>()
        // Parser state for the in-progress response.
        var parsingHeaders = true
        val headerAcc = StringBuilder()        // accumulates header bytes as ISO-8859-1
        var headerComplete = false
        var bodyRemaining = 0                  // Content-Length body bytes still to consume
        var current: Exchange? = null
        // tail capture for version-suffix check
        val tailRing = ByteArray(8)
        var tailLen = 0
    }

    private class Analyzer(val filter: String?, val max: Int, val httpPort: Int) {
        private val httpFds = HashSet<Int>()
        private val fdState = HashMap<Int, FdHttp>()
        private var reqOrdinal = 0

        // De-dup state for the recorder's double-recording of each syscall: this
        // client (macOS CFNetwork/libcurl) routes each socket I/O through TWO
        // interposed entry points, so every transfer appears twice — once as
        // sendto/recvfrom and once as send/recv — with IDENTICAL bytes ~15µs apart.
        // We collapse a consecutive duplicate (same fd, dir, len, head+tail bytes,
        // within a small ns window) so byte accounting reflects the real wire.
        private var lastIoFd = Int.MIN_VALUE
        private var lastIoDir = -1
        private var lastIoLen = -1
        private var lastIoNs = -1L
        private var lastIoHead = -1
        private var lastIoTail = -1
        var dedupedRecords = 0

        // counters
        var totalConnectsToPort = 0
        var totalCloses = 0
        var totalGetMs = 0
        var emitted = 0

        val connectNsByFd = HashMap<Int, ArrayList<Long>>()
        val closeNsByFd = HashMap<Int, ArrayList<Long>>()

        val matchedReqNs = ArrayList<Long>()
        val matchedReqCountByFd = HashMap<Int, Int>()
        val matchedCompleteByInbound = HashMap<Int, Int>()    // inboundTotalThisExchange -> count (complete only)
        var matchedComplete = 0
        var matchedShortBody = 0
        var matchedClosedMid = 0
        var matchedEofMid = 0
        var matchedNoHeaders = 0
        val contentLengthValues = HashMap<Int, Int>()
        val contentTypeValues = HashMap<String, Int>()
        val bodyTailHexValues = HashMap<String, Int>()

        fun run(input: DataInputStream) {
            var records = 0L
            while (true) {
                val typeId = try { input.read() } catch (e: EOFException) { -1 }
                if (typeId < 0) break
                val ts = try { readU64LE(input) } catch (e: EOFException) {
                    System.err.println("[http] truncated at record header after $records records"); break
                }
                try {
                    when (typeId) {
                        REC_IO -> onIo(input, ts)
                        REC_CONNECT -> onConnect(input, ts)
                        REC_CLOSE -> onClose(input, ts)
                        REC_SEEDS -> skipSeeds(input)
                        REC_NOTE -> skipNote(input)
                        else -> { System.err.println("[http] unknown rec type $typeId; stopping"); return }
                    }
                } catch (e: EOFException) {
                    System.err.println("[http] truncated mid-record (type $typeId) after $records records"); break
                }
                records++
                if (records % 5_000_000L == 0L) System.err.println("[http] ... $records records")
            }
            System.err.println("[http] scanned $records records")
        }

        private fun onConnect(input: DataInputStream, ts: Long) {
            val fd = readI32LE(input)
            input.readUnsignedByte()                 // family
            val port = readU16LE(input)
            val addrLen = input.readUnsignedByte()
            val addr = ByteArray(addrLen); input.readFully(addr)
            if (port == httpPort) {
                httpFds.add(fd)
                totalConnectsToPort++
                connectNsByFd.getOrPut(fd) { ArrayList() }.add(ts)
                // A fresh connect on this fd: reset framer (old socket closed/recycled).
                fdState[fd] = FdHttp(fd)
            } else {
                retireFd(fd, ts, eof = false)
                httpFds.remove(fd)
            }
        }

        private fun onClose(input: DataInputStream, ts: Long) {
            val fd = readI32LE(input)
            totalCloses++
            if (fd in httpFds) closeNsByFd.getOrPut(fd) { ArrayList() }.add(ts)
            retireFd(fd, ts, eof = false)
            httpFds.remove(fd)
        }

        /** Finalize any in-progress response + drop pending requests for an fd (close or port change). */
        private fun retireFd(fd: Int, ts: Long, eof: Boolean) {
            val st = fdState.remove(fd) ?: return
            st.current?.let { ex ->
                if (eof) ex.terminatedByEof = true else ex.terminatedByClose = true
                ex.lastInNs = ts
                finishExchange(ex)
            }
            // Pending-but-never-answered requests: count nothing (no response bytes).
            st.pendingReqs.clear()
        }

        private fun onIo(input: DataInputStream, ts: Long) {
            val fd = readI32LE(input)
            val dir = input.readUnsignedByte()
            input.readUnsignedByte()                 // syscall
            val len = readU32LE(input).toInt()
            val bytes = ByteArray(len)
            input.readFully(bytes)

            // Collapse the recorder's twin-recording of one physical syscall.
            val head = if (len > 0) (bytes[0].toInt() and 0xFF) else -1
            val tail = if (len > 0) (bytes[len - 1].toInt() and 0xFF) else -1
            val isDup = fd == lastIoFd && dir == lastIoDir && len == lastIoLen &&
                head == lastIoHead && tail == lastIoTail && (ts - lastIoNs) in 0..2_000_000L // <=2ms
            lastIoFd = fd; lastIoDir = dir; lastIoLen = len; lastIoNs = ts; lastIoHead = head; lastIoTail = tail
            if (isDup) { dedupedRecords++; return }

            if (dir != DIR_IN) {
                if (startsWithGet(bytes)) {
                    val reqLine = firstLine(bytes)
                    if (reqLine.startsWith("GET /ms")) totalGetMs++
                    val st = fdState.getOrPut(fd) { httpFds.add(fd); FdHttp(fd) }
                    val ex = Exchange(fd, reqOrdinal++, reqLine, bytes.copyOf(minOf(bytes.size, 2048)), ts)
                    st.pendingReqs.addLast(ex)
                }
                return
            }

            // INBOUND: feed the per-fd framer.
            val st = fdState[fd] ?: return
            feedInbound(st, bytes, ts)
        }

        /** Stream inbound bytes through the HTTP/1.1 response framer for this fd. */
        private fun feedInbound(st: FdHttp, bytes: ByteArray, ts: Long) {
            var off = 0
            val n = bytes.size
            while (off < n) {
                // Ensure we have a current response object bound to the head request.
                if (st.current == null) {
                    val req = st.pendingReqs.removeFirstOrNull()
                        ?: // unexpected inbound with no pending request: make a synthetic holder
                        Exchange(st.fd, reqOrdinal++, "(no-pending-request)", ByteArray(0), ts)
                    st.current = req
                    st.parsingHeaders = true
                    st.headerComplete = false
                    st.headerAcc.setLength(0)
                    st.bodyRemaining = 0
                }
                val ex = st.current!!
                if (ex.firstInNs < 0) ex.firstInNs = ts
                ex.lastInNs = ts

                if (st.parsingHeaders) {
                    // Append bytes one at a time until CRLFCRLF (cheap; headers are tiny).
                    while (off < n && !st.headerComplete) {
                        val c = bytes[off].toInt() and 0xFF
                        st.headerAcc.append(c.toChar())
                        ex.inboundTotalThisExchange++
                        off++
                        val L = st.headerAcc.length
                        if (L >= 4 &&
                            st.headerAcc[L - 4] == '\r' && st.headerAcc[L - 3] == '\n' &&
                            st.headerAcc[L - 2] == '\r' && st.headerAcc[L - 1] == '\n'
                        ) {
                            st.headerComplete = true
                        }
                    }
                    if (!st.headerComplete) return // need more bytes for headers
                    // Parse the completed header block.
                    parseHeaders(ex, st.headerAcc.toString())
                    ex.headerByteCount = st.headerAcc.length
                    st.parsingHeaders = false
                    st.bodyRemaining = if (ex.contentLength >= 0) ex.contentLength else 0
                    if (ex.contentLength < 0) {
                        // No Content-Length (and we don't see chunked here) → cannot frame; finish best-effort.
                        finishExchange(ex)
                        st.current = null
                        continue
                    }
                }

                // BODY phase: consume up to bodyRemaining from this chunk.
                if (st.bodyRemaining > 0) {
                    val take = minOf(st.bodyRemaining, n - off)
                    if (take > 0) {
                        ex.bodyBytesReceived += take
                        ex.inboundTotalThisExchange += take
                        ex.bodyReadSizes.add(take)
                        // capture tail for version-suffix check
                        captureTail(st, bytes, off, take)
                        off += take
                        st.bodyRemaining -= take
                    }
                }
                if (st.bodyRemaining == 0) {
                    ex.complete = ex.contentLength >= 0 && ex.bodyBytesReceived >= ex.contentLength
                    ex.bodyTailHex = tailHex(st)
                    finishExchange(ex)
                    st.current = null
                    st.tailLen = 0
                }
            }
        }

        private fun captureTail(st: FdHttp, bytes: ByteArray, off: Int, take: Int) {
            // keep last up-to-8 body bytes
            val start = maxOf(off, off + take - 8)
            var k = start
            while (k < off + take) {
                st.tailRing[st.tailLen % 8] = bytes[k]
                st.tailLen++
                k++
            }
        }

        private fun tailHex(st: FdHttp): String {
            val count = minOf(st.tailLen, 8)
            if (count == 0) return ""
            val sb = StringBuilder()
            // reconstruct last `count` bytes in order
            val startIdx = st.tailLen - count
            for (j in 0 until count) {
                val b = st.tailRing[(startIdx + j) % 8].toInt() and 0xFF
                sb.append("%02x".format(b))
            }
            return sb.toString()
        }

        private fun parseHeaders(ex: Exchange, raw: String) {
            val headerStr = raw.removeSuffix("\r\n\r\n").removeSuffix("\r\n")
            val lines = headerStr.split("\r\n")
            ex.statusLine = lines.firstOrNull()
            ex.headersText = headerStr
            for (line in lines.drop(1)) {
                val idx = line.indexOf(':')
                if (idx <= 0) continue
                val name = line.substring(0, idx).trim().lowercase()
                val value = line.substring(idx + 1).trim()
                when (name) {
                    "content-length" -> ex.contentLength = value.toIntOrNull() ?: -1
                    "transfer-encoding" -> ex.transferEncoding = value
                    "connection" -> ex.connectionHeader = value
                    "content-type" -> ex.contentType = value
                }
            }
        }

        private fun finishExchange(ex: Exchange) {
            val matches = filter == null || ex.reqLine.contains(filter)
            if (!matches) return
            matchedReqNs.add(ex.reqStartNs)
            matchedReqCountByFd.merge(ex.fd, 1, Int::plus)
            if (ex.statusLine == null) matchedNoHeaders++
            ex.contentType?.let { contentTypeValues.merge(it, 1, Int::plus) }
            if (ex.contentLength >= 0) contentLengthValues.merge(ex.contentLength, 1, Int::plus)
            ex.bodyTailHex?.let { if (it.isNotEmpty()) bodyTailHexValues.merge(it, 1, Int::plus) }

            when {
                ex.complete -> { matchedComplete++; matchedCompleteByInbound.merge(ex.inboundTotalThisExchange, 1, Int::plus) }
                ex.terminatedByClose -> matchedClosedMid++
                ex.terminatedByEof -> matchedEofMid++
                ex.contentLength >= 0 && ex.bodyBytesReceived < ex.contentLength -> matchedShortBody++
            }

            if (emitted < max) { emitted++; dumpExchange(ex) }
        }

        private fun dumpExchange(ex: Exchange) {
            val durMs = if (ex.firstInNs > 0) (ex.lastInNs - ex.reqStartNs) / 1_000_000.0 else -1.0
            println("================ EXCHANGE #${ex.index}  fd=${ex.fd} ================")
            println("  REQUEST (outbound, ${ex.reqRaw.size}B captured):")
            String(ex.reqRaw, Charsets.ISO_8859_1).split("\r\n").forEach { if (it.isNotEmpty()) println("    > $it") }
            println("  RESPONSE (inbound, byte-exact framed):")
            println("    status: ${ex.statusLine ?: "(no headers received)"}")
            ex.headersText?.split("\r\n")?.drop(1)?.forEach { if (it.isNotEmpty()) println("      < $it") }
            println("    Content-Length header:  ${ex.contentLength}")
            println("    Content-Type header:    ${ex.contentType}")
            println("    Transfer-Encoding:      ${ex.transferEncoding ?: "(absent)"}")
            println("    Connection header:      ${ex.connectionHeader ?: "(absent)"}")
            println("    header byte count (status+headers+CRLFCRLF): ${ex.headerByteCount}")
            println("    body bytes received (exact, this response):  ${ex.bodyBytesReceived}")
            println("    total inbound this exchange (hdr+body):      ${ex.inboundTotalThisExchange}")
            if (ex.contentLength >= 0) {
                val delta = ex.bodyBytesReceived - ex.contentLength
                println("    body-vs-Content-Length delta: $delta  (${if (delta == 0) "EXACT MATCH" else if (delta < 0) "SHORT by ${-delta}" else "OVER by $delta"})")
            }
            println("    body tail (last <=8 bytes hex): ${ex.bodyTailHex}")
            println("    complete: ${ex.complete}   closedMid: ${ex.terminatedByClose}   eofMid: ${ex.terminatedByEof}")
            println("    body recv() chunk sizes (this response): ${ex.bodyReadSizes}")
            println("    response duration (req->lastIn): ${"%.2f".format(durMs)} ms")
            println()
        }

        fun report() {
            // finalize anything still open at EOF
            fdState.keys.toList().forEach { fd -> retireFd(fd, ts = Long.MAX_VALUE, eof = true) }

            println("================ SUMMARY ================")
            println("recorder twin-record duplicates collapsed: $dedupedRecords")
            println("connects to port $httpPort: $totalConnectsToPort")
            println("closes (total):            $totalCloses")
            println("'GET /ms' requests seen (deduped): $totalGetMs")
            val matchedTotal = matchedReqNs.size
            println("matched exchanges (filter=${filter ?: "none"}): $matchedTotal")
            println()
            println("Connection lifecycle (matched /ms):")
            println("  distinct fds carrying matched /ms: ${matchedReqCountByFd.size}")
            val reused = matchedReqCountByFd.filterValues { it > 1 }
            println("  fds carrying >1 matched /ms (keep-alive reuse): ${reused.size}")
            if (reused.isNotEmpty()) {
                val sample = reused.entries.sortedByDescending { it.value }.take(6)
                    .joinToString { "fd${it.key}:${it.value}" }
                println("    top reused fds (req count): $sample")
            }
            println("  fds carrying exactly 1 matched /ms: ${matchedReqCountByFd.filterValues { it == 1 }.size}")
            println("  connects-to-port vs matched /ms count: $totalConnectsToPort vs $matchedTotal")
            println()
            println("Response completeness (matched /ms):")
            println("  complete (full Content-Length body):  $matchedComplete")
            println("  short body (recv < Content-Length):   $matchedShortBody")
            println("  closed mid-response (FIN pre-body):   $matchedClosedMid")
            println("  EOF mid-response (capture end):       $matchedEofMid")
            println("  no headers received:                  $matchedNoHeaders")
            println()
            println("  Content-Length header values seen (value -> count):")
            contentLengthValues.entries.sortedByDescending { it.value }.take(6).forEach { (v, c) -> println("    $v -> $c") }
            println("  Content-Type header values seen (value -> count):")
            contentTypeValues.entries.sortedByDescending { it.value }.take(6).forEach { (v, c) -> println("    '$v' -> $c") }
            println("  body tail (last <=8 bytes) values seen (hex -> count):")
            bodyTailHexValues.entries.sortedByDescending { it.value }.take(6).forEach { (v, c) -> println("    $v -> $c") }
            if (matchedCompleteByInbound.isNotEmpty()) {
                println("  total inbound bytes per COMPLETE exchange (hdr+body -> count):")
                matchedCompleteByInbound.entries.sortedByDescending { it.value }.take(6).forEach { (v, c) -> println("    ${v}B -> $c") }
            }
            println()
            if (matchedReqNs.size >= 2) {
                val sorted = matchedReqNs.sorted()
                val gaps = (1 until sorted.size).map { (sorted[it] - sorted[it - 1]) / 1_000_000.0 }
                val span = (sorted.last() - sorted.first()) / 1_000_000_000.0
                println("Loop cadence (matched /ms request starts):")
                println("  count=${sorted.size}  span=${"%.1f".format(span)}s")
                println("  inter-request gap ms: min=${"%.2f".format(gaps.min())}  median=${"%.2f".format(median(gaps))}  mean=${"%.2f".format(gaps.average())}  max=${"%.2f".format(gaps.max())}")
            }
        }

        private fun median(xs: List<Double>): Double {
            if (xs.isEmpty()) return 0.0
            val s = xs.sorted(); val m = s.size / 2
            return if (s.size % 2 == 1) s[m] else (s[m - 1] + s[m]) / 2.0
        }
    }

    /**
     * Raw, interpretation-free record tracer for ONE fd: prints every
     * connect/io/close record touching the fd in capture order with relative
     * timestamps, so the true wire choreography (request → response chunks →
     * close, and whether a duplicate GET rides the same socket) is visible
     * without any HTTP framing assumptions.
     */
    private class RawFdTracer(val fd: Int, val limit: Int, val httpPort: Int) {
        private var t0 = -1L
        private var printed = 0
        private val sysName = arrayOf("recv", "read", "recvfrom", "send", "write", "sendto")

        fun run(input: DataInputStream) {
            while (printed < limit) {
                val typeId = try { input.read() } catch (e: EOFException) { -1 }
                if (typeId < 0) break
                val ts = try { readU64LE(input) } catch (e: EOFException) { break }
                when (typeId) {
                    REC_IO -> {
                        val f = readI32LE(input); val dir = input.readUnsignedByte()
                        val sc = input.readUnsignedByte(); val len = readU32LE(input).toInt()
                        val bytes = ByteArray(len); input.readFully(bytes)
                        if (f == fd) line(ts, "IO", "${if (dir == DIR_IN) "IN " else "OUT"} ${sysName.getOrElse(sc){"?"}} len=$len  ${preview(bytes)}")
                    }
                    REC_CONNECT -> {
                        val f = readI32LE(input); input.readUnsignedByte()
                        val port = readU16LE(input); val al = input.readUnsignedByte()
                        val addr = ByteArray(al); input.readFully(addr)
                        if (f == fd) line(ts, "CONNECT", "port=$port addr=${addr.joinToString("."){(it.toInt() and 0xFF).toString()}}")
                    }
                    REC_CLOSE -> {
                        val f = readI32LE(input)
                        if (f == fd) line(ts, "CLOSE", "")
                    }
                    REC_SEEDS -> repeat(20) { input.readUnsignedByte() }
                    REC_NOTE -> { val l = readU16LE(input); repeat(l) { input.readUnsignedByte() } }
                    else -> return
                }
            }
        }

        private fun line(ts: Long, kind: String, detail: String) {
            if (t0 < 0) t0 = ts
            val relMs = (ts - t0) / 1_000_000.0
            println("  [%9.3f ms] %-7s %s".format(relMs, kind, detail))
            printed++
        }

        private fun preview(b: ByteArray): String {
            // If it looks like ASCII (request/headers), show first line; else hex of first 16.
            val n = minOf(b.size, 48)
            val ascii = (0 until n).all { val c = b[it].toInt() and 0xFF; c == 13 || c == 10 || c in 32..126 }
            return if (ascii && b.isNotEmpty()) {
                "\"" + String(b, 0, n, Charsets.ISO_8859_1).replace("\r", "\\r").replace("\n", "\\n") + (if (b.size > n) "..." else "") + "\""
            } else {
                val sb = StringBuilder("hex[")
                for (k in 0 until minOf(b.size, 16)) sb.append("%02x".format(b[k].toInt() and 0xFF))
                sb.append(if (b.size > 16) "...]" else "]")
                sb.toString()
            }
        }
    }

    // --- byte helpers -------------------------------------------------------
    private val GET_PREFIX = "GET ".toByteArray(Charsets.US_ASCII)

    private fun startsWithGet(b: ByteArray): Boolean {
        if (b.size < GET_PREFIX.size) return false
        for (i in GET_PREFIX.indices) if (b[i] != GET_PREFIX[i]) return false
        return true
    }

    private fun firstLine(b: ByteArray): String {
        var end = b.size
        for (i in b.indices) if (b[i] == 13.toByte() || b[i] == 10.toByte()) { end = i; break }
        return String(b, 0, end, Charsets.ISO_8859_1)
    }

    // --- LE primitives (mirror CaptureReader) ------------------------------
    private fun readU16LE(i: DataInputStream): Int {
        val b0 = i.readUnsignedByte(); val b1 = i.readUnsignedByte()
        return b0 or (b1 shl 8)
    }

    private fun readU32LE(i: DataInputStream): Long {
        val b0 = i.readUnsignedByte().toLong(); val b1 = i.readUnsignedByte().toLong()
        val b2 = i.readUnsignedByte().toLong(); val b3 = i.readUnsignedByte().toLong()
        return b0 or (b1 shl 8) or (b2 shl 16) or (b3 shl 24)
    }

    private fun readI32LE(i: DataInputStream): Int = readU32LE(i).toInt()

    private fun readU64LE(i: DataInputStream): Long {
        var v = 0L
        for (s in 0 until 64 step 8) v = v or (i.readUnsignedByte().toLong() shl s)
        return v
    }

    private fun skipSeeds(input: DataInputStream) { repeat(20) { input.readUnsignedByte() } }

    private fun skipNote(input: DataInputStream) {
        val len = readU16LE(input)
        repeat(len) { input.readUnsignedByte() }
    }
}
