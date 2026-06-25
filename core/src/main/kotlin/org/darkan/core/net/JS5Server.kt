package org.darkan.core.net

import io.ktor.utils.io.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.withTimeoutOrNull
import org.darkan.core.EnvVars
import org.darkan.core.Logger.logFinest
import org.darkan.core.Logger.logInfo
import org.darkan.core.Logger.logTrace
import org.darkan.core.Logger.logWarn
import world.gregs.voidps.buffer.*
import world.gregs.voidps.cache.file.FileProvider
import java.io.EOFException
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicIntegerArray

data class JS5Request(val index: Int, val group: Int, val urgent: Boolean, val priority: Int, val ref: Long)

sealed interface JS5QueueItem {
    data class FileRequest(val request: JS5Request) : JS5QueueItem
    data class XorKeyUpdate(val key: Int) : JS5QueueItem
}

private class JS5ConnectionStats {
    val requests = AtomicInteger()
    val urgent = AtomicInteger()
    val prefetch = AtomicInteger()
    val served = AtomicInteger()
    val misses = AtomicInteger()
    val requestsByIndex = AtomicIntegerArray(256)
    val servedByIndex = AtomicIntegerArray(256)
    val missesByIndex = AtomicIntegerArray(256)
    val lastGroupByIndex = AtomicIntegerArray(IntArray(256) { -1 })
    @Volatile var lastIndex = -1
    @Volatile var lastGroup = -1
    @Volatile var lastPriority = -1
    @Volatile var lastUrgent = false

    fun markRequest(index: Int, group: Int) {
        if (index !in 0..255) return
        requestsByIndex.incrementAndGet(index)
        lastGroupByIndex.set(index, group)
    }

    fun markServed(index: Int) {
        if (index in 0..255) servedByIndex.incrementAndGet(index)
    }

    fun markMiss(index: Int) {
        if (index in 0..255) missesByIndex.incrementAndGet(index)
    }
}

class JS5Server(val provider: FileProvider, val prefetchKeys: IntArray) {
    val limiter = MultilogLimiter()

    suspend fun init(input: ByteReadChannel, output: ByteWriteChannel, ip: String) {
        if (!limiter.add(ip)) {
            logWarn("JS5 connection rejected (limit): $ip")
            output.finish(ResponseOpcode.LOGIN_LIMIT_EXCEEDED)
            return
        }
        logInfo("JS5 connection from $ip")
        try {
            if (!handshake(input, output, ip)) return
            requestLoop(input, output, ip)
        } finally {
            logFinest("JS5 connection closed: $ip")
            limiter.remove(ip)
        }
    }

    private suspend fun handshake(input: ByteReadChannel, output: ByteWriteChannel, ip: String): Boolean {
        // Read handshake: size(1) + major(4) + minor(4) + token(N+1) + platform(1)
        val size = input.readByte().toInt()
        val major = input.readInt()
        val minor = input.readInt()
        if (major != EnvVars.majorVersion || minor != EnvVars.minorVersion) {
            logWarn("JS5 version mismatch from $ip — got $major.$minor, expected ${EnvVars.majorVersion}.${EnvVars.minorVersion}. Accepting anyway.")
        }
        val token = input.readRSString()
        if (token != EnvVars.js5ServerToken) {
            logWarn("JS5 invalid token from $ip: $token")
            output.writeByte(ResponseOpcode.BAD_SESSION_ID)
            output.flushAndClose()
            return false
        }
        input.readByte() // trailing platform/language byte

        logInfo("JS5 handshake complete for $ip — version $major.$minor")

        // Send SYNC response (no prefetch keys in 946+)
        output.writeByte(ResponseOpcode.JS5_SYNC)
        output.flush()
        logInfo("JS5 sent SYNC to $ip")

        // Read ACK (opcode 6) + READY (opcode 3)
        val ackOpcode = input.readByte().toInt() and 0xFF
        if (ackOpcode != RequestOpcode.ACKNOWLEDGE) {
            logWarn("Expected ACK (6), got $ackOpcode from $ip")
            output.writeByte(ResponseOpcode.LOGIN_SERVER_REJECTED_SESSION)
            output.flushAndClose()
            return false
        }
        readControlPayload(input)

        val readyOpcode = input.readByte().toInt() and 0xFF
        if (readyOpcode != RequestOpcode.STATUS_LOGGED_OUT) {
            logWarn("Expected READY (3), got $readyOpcode from $ip")
        }
        readControlPayload(input)

        logInfo("JS5 ACK + READY complete for $ip")
        return true
    }

    private suspend fun requestLoop(input: ByteReadChannel, output: ByteWriteChannel, ip: String) {
        val urgentChannel = Channel<JS5QueueItem>(URGENT_QUEUE_CAPACITY)
        // UNLIMITED so the reader never suspends on prefetch backpressure: with a bounded
        // prefetch channel, a full prefetch backlog blocked the reader and head-of-line
        // blocked urgent requests sitting behind it in the TCP stream. Requests are tiny
        // (a few dozen bytes each), so an unbounded in-memory queue is safe.
        val prefetchChannel = Channel<JS5QueueItem>(Channel.UNLIMITED)
        val stats = JS5ConnectionStats()

        try {
            coroutineScope {
                val readerJob = launch { reader(input, urgentChannel, prefetchChannel, ip, stats) }
                val writerJob = launch { writer(output, urgentChannel, prefetchChannel, ip, stats) }

                readerJob.invokeOnCompletion { cause ->
                    if (cause != null) {
                        writerJob.cancel()
                    }
                }
                writerJob.invokeOnCompletion { readerJob.cancel() }
            }
        } finally {
            logInfo(
                "JS5 session ended for $ip: requests=${stats.requests.get()} urgent=${stats.urgent.get()} prefetch=${stats.prefetch.get()} " +
                    "served=${stats.served.get()} misses=${stats.misses.get()} last=${stats.lastIndex}/${stats.lastGroup} " +
                    "pri=${stats.lastPriority} ${if (stats.lastUrgent) "urgent" else "prefetch"} " +
                    "requestsByIndex=${stats.requestsByIndex.nonZeroMapString()} servedByIndex=${stats.servedByIndex.nonZeroMapString()} " +
                    "missesByIndex=${stats.missesByIndex.nonZeroMapString()} lastGroupByIndex=${stats.lastGroupByIndex.lastGroupMapString()}"
            )
        }
    }

    private suspend fun reader(
        input: ByteReadChannel,
        urgentChannel: Channel<JS5QueueItem>,
        prefetchChannel: Channel<JS5QueueItem>,
        ip: String,
        stats: JS5ConnectionStats
    ) {
        var requestCount = 0
        var lastRequestTime = System.currentTimeMillis()

        try {
            while (true) {
                var opcodeByte: Byte? = null
                while (opcodeByte == null) {
                    opcodeByte = withTimeoutOrNull(5000) { input.readByte() }
                    if (opcodeByte == null) {
                        val idleSec = (System.currentTimeMillis() - lastRequestTime) / 1000
                        // DIAGNOSTIC (JS5 stall): surfaced at INFO so a pilot run at the default
                        // TRACE/FINER level shows whether the reader is alive and idling on the
                        // persistent socket (i.e. the client sent nothing) vs. parsing requests.
                        logInfo("JS5 idle ${idleSec}s after $requestCount requests from $ip")
                    }
                }

                val opcode = opcodeByte.toInt() and 0xFF
                requestCount++
                lastRequestTime = System.currentTimeMillis()

                when {
                    RequestOpcode.isFileRequest(opcode) -> {
                        val urgent = RequestOpcode.isUrgent(opcode)
                        val priority = (opcode shr 4) and 0x07
                        val index = input.readByte().toInt() and 0xFF
                        val group = input.readInt()
                        input.readInt() // padding
                        stats.requests.incrementAndGet()
                        if (urgent) stats.urgent.incrementAndGet() else stats.prefetch.incrementAndGet()
                        stats.lastIndex = index
                        stats.lastGroup = group
                        stats.lastPriority = priority
                        stats.lastUrgent = urgent
                        stats.markRequest(index, group)
                        if (requestCount <= 20 || requestCount % 1000 == 0) {
                            logInfo("JS5 processed $requestCount requests from $ip (latest index=$index group=$group ${if (urgent) "urgent" else "prefetch"} pri=$priority)")
                        }
                        logFinest("JS5 request: index=$index group=$group opcode=$opcode (${if (urgent) "urgent" else "prefetch"} pri=$priority) from $ip")
                        val ref = (index.toLong() shl 32) or (group.toLong() and 0xFFFFFFFFL)
                        val request = JS5Request(index, group, urgent, priority, ref)
                        val item = JS5QueueItem.FileRequest(request)
                        if (urgent) {
                            urgentChannel.send(item)
                        } else {
                            prefetchChannel.send(item)
                        }
                    }

                    opcode == RequestOpcode.STATUS_LOGGED_IN -> {
                        logFinest("JS5 logged in from $ip")
                        readControlPayload(input)
                    }

                    opcode == RequestOpcode.STATUS_LOGGED_OUT -> {
                        logFinest("JS5 logged out from $ip")
                        readControlPayload(input)
                    }

                    opcode == RequestOpcode.XOR_KEY_UPDATE -> {
                        val key = input.readByte().toInt() and 0xFF
                        input.readShort()
                        input.readInt()
                        input.readShort()
                        logFinest("JS5 XOR key set to $key from $ip")
                        // Send to urgent channel so it's applied before any subsequent requests
                        urgentChannel.send(JS5QueueItem.XorKeyUpdate(key))
                    }

                    opcode == RequestOpcode.ACKNOWLEDGE -> {
                        logFinest("JS5 ACK from $ip")
                        readControlPayload(input)
                    }

                    opcode == RequestOpcode.DISCONNECT -> {
                        logInfo("JS5 disconnect from $ip")
                        readControlPayload(input)
                        return
                    }

                    else -> {
                        logWarn("JS5 unknown opcode $opcode from $ip — skipping 9 bytes")
                        input.discard(9)
                    }
                }
            }
        } catch (e: EOFException) {
            logFinest("JS5 input closed from $ip")
        } catch (e: ClosedReadChannelException) {
            logFinest("JS5 input closed from $ip")
        } finally {
            urgentChannel.close()
            prefetchChannel.close()
        }
    }

    private suspend fun writer(
        output: ByteWriteChannel,
        urgentChannel: Channel<JS5QueueItem>,
        prefetchChannel: Channel<JS5QueueItem>,
        ip: String,
        stats: JS5ConnectionStats
    ) {
        var xorKey = 0

        while (true) {
            // Drain all available urgent items first
            while (true) {
                val item = urgentChannel.tryReceive().getOrNull() ?: break
                xorKey = processItem(item, output, xorKey, ip, stats)
            }

            // Try a prefetch item
            val prefetchItem = prefetchChannel.tryReceive().getOrNull()
            if (prefetchItem != null) {
                xorKey = processItem(prefetchItem, output, xorKey, ip, stats)
                continue
            }

            // Both channels empty — suspend until something arrives, preferring urgent
            val item = select<JS5QueueItem?> {
                urgentChannel.onReceiveCatching { result ->
                    result.getOrNull()
                }
                prefetchChannel.onReceiveCatching { result ->
                    result.getOrNull()
                }
            }

            if (item == null) {
                // Both channels closed
                return
            }
            xorKey = processItem(item, output, xorKey, ip, stats)
        }
    }

    private suspend fun processItem(item: JS5QueueItem, output: ByteWriteChannel, xorKey: Int, ip: String, stats: JS5ConnectionStats): Int {
        return when (item) {
            is JS5QueueItem.XorKeyUpdate -> {
                item.key
            }
            is JS5QueueItem.FileRequest -> {
                val req = item.request
                val ok = provider.serve(output, req.ref, prefetch = !req.urgent, xorKey = xorKey)
                if (!ok) {
                    stats.misses.incrementAndGet()
                    stats.markMiss(req.index)
                    logWarn("JS5 miss: index=${req.index} group=${req.group} from $ip")
                } else {
                    stats.served.incrementAndGet()
                    stats.markServed(req.index)
                }
                xorKey
            }
        }
    }

    companion object {
        /** Urgent requests are served promptly, so a small bound is plenty. */
        private const val URGENT_QUEUE_CAPACITY = 200

        /** Read 9 bytes of control message payload: medium(3) + int(4) + short(2) */
        private suspend fun readControlPayload(input: ByteReadChannel) {
            input.readMedium()
            input.readInt()
            input.readShort()
        }
    }
}

private fun AtomicIntegerArray.nonZeroMapString(): String {
    val values = ArrayList<String>()
    for (index in 0 until length()) {
        val count = get(index)
        if (count != 0) values += "$index=$count"
    }
    return values.joinToString(prefix = "{", postfix = "}")
}

private fun AtomicIntegerArray.lastGroupMapString(): String {
    val values = ArrayList<String>()
    for (index in 0 until length()) {
        val group = get(index)
        if (group >= 0) values += "$index=$group"
    }
    return values.joinToString(prefix = "{", postfix = "}")
}
