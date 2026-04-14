package org.darkan.tools.cachedownloader

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.withContext
import world.gregs.voidps.cache.secure.CRC
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket
import java.nio.ByteBuffer
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger

data class FileRequest(val index: Int, val archive: Int, val version: Int = 0, val crc: Int = 0)

class JS5Connection(
    private val id: Int,
    private val host: String,
    private val port: Int,
    private val major: Int,
    private val minor: Int,
    private val token: String,
    private val queue: Channel<FileRequest>,
    private val storage: CacheStorage,
    private val progress: ProgressTracker
) {
    private val retryQueue = ConcurrentLinkedQueue<FileRequest>()
    private val sentRequests = ConcurrentLinkedQueue<FileRequest>()
    private val inflight = Semaphore(20)
    private val completedThisConnection = AtomicInteger(0)

    suspend fun run() {
        delay(id * 500L)

        var backoff = 1000L
        var consecutiveFailures = 0
        while (true) {
            try {
                completedThisConnection.set(0)
                connect()
                return
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                while (sentRequests.isNotEmpty()) {
                    val req = sentRequests.poll() ?: break
                    retryQueue.add(req)
                    inflight.release()
                }

                if (completedThisConnection.get() == 0 && retryQueue.isNotEmpty()) {
                    consecutiveFailures++
                    if (consecutiveFailures >= 3) {
                        val bad = retryQueue.poll()
                        if (bad != null) {
                            System.err.println("\nConnection $id: skipping unservable (${bad.index}, ${bad.archive})")
                            inflight.release()
                            progress.fail()
                        }
                        consecutiveFailures = 0
                    }
                } else {
                    consecutiveFailures = 0
                }

                if (completedThisConnection.get() > 0) {
                    backoff = 1000L
                }

                System.err.println("\nConnection $id error: ${e::class.simpleName}: ${e.message}. Reconnecting in ${backoff}ms...")
                delay(backoff)
                backoff = (backoff * 2).coerceAtMost(30_000)
            }
        }
    }

    private suspend fun connect() {
        val socket = withContext(Dispatchers.IO) {
            Socket(host, port).also { it.soTimeout = 30_000 }
        }
        val input = DataInputStream(BufferedInputStream(socket.getInputStream(), 1024 * 1024))
        val output = DataOutputStream(BufferedOutputStream(socket.getOutputStream(), 64 * 1024))

        try {
            withContext(Dispatchers.IO) {
                val response = JS5Protocol.handshake(output, input, major, minor, token)
                when (response) {
                    0 -> { /* SYNC */ }
                    6 -> throw IllegalStateException("GAME_UPDATE: version $major.$minor rejected")
                    else -> throw IllegalStateException("Handshake failed: $response")
                }
                JS5Protocol.sendConnectionInit(output, major)
            }

            coroutineScope {
                val senderJob = launch(Dispatchers.IO) { sender(output) }
                val receiverJob = launch(Dispatchers.IO) { receiver(input) }
                senderJob.join()
                while (sentRequests.isNotEmpty()) {
                    delay(100)
                }
                withContext(Dispatchers.IO) { socket.close() }
                receiverJob.cancel()
            }
        } catch (_: Exception) {
        } finally {
            withContext(Dispatchers.IO) {
                try { socket.close() } catch (_: Exception) {}
            }
        }
    }

    private suspend fun sender(output: DataOutputStream) {
        while (retryQueue.isNotEmpty()) {
            val request = retryQueue.poll() ?: break
            inflight.acquire()
            sentRequests.add(request)
            JS5Protocol.sendFileRequest(output, request.index, request.archive, major)
        }

        for (request in queue) {
            inflight.acquire()
            sentRequests.add(request)
            JS5Protocol.sendFileRequest(output, request.index, request.archive, major)
        }
    }

    private fun receiver(input: DataInputStream) {
        val pending = HashMap<Long, PendingResponse>()
        var segmentCount = 0

        try {
            while (true) {
                val headerIndex = input.readUnsignedByte()
                val headerHash = input.readInt()
                val headerArchive = headerHash and 0x7FFFFFFF
                segmentCount++

                val key = (headerIndex.toLong() shl 32) or headerArchive.toLong()

                val resp = pending.getOrPut(key) {
                    PendingResponse(headerIndex, headerArchive)
                }
                resp.offset = 5

                if (resp.buffer == null) {
                    val compression = input.readUnsignedByte()
                    val compressedSize = input.readInt()
                    resp.offset = 10

                    if (compressedSize < 0 || compressedSize > 50_000_000) {
                        throw IllegalStateException(
                            "Bad compressedSize=$compressedSize for index=$headerIndex archive=$headerArchive"
                        )
                    }

                    val totalDataLen = compressedSize + (if (compression != 0) 4 else 0)
                    val container = ByteBuffer.allocate(5 + totalDataLen)
                    container.put(compression.toByte())
                    container.putInt(compressedSize)
                    resp.buffer = container
                    resp.totalSize = 5 + totalDataLen
                }

                val buffer = resp.buffer!!
                val remaining = resp.totalSize - buffer.position()
                val blockSpace = JS5Protocol.BLOCK_SIZE - resp.offset
                val toRead = remaining.coerceAtMost(blockSpace)

                if (toRead > 0) {
                    val tmp = ByteArray(toRead)
                    input.readFully(tmp)
                    buffer.put(tmp)
                    resp.offset += toRead
                }

                if (buffer.position() == resp.totalSize) {
                    pending.remove(key)
                    val container = buffer.array()
                    val crc = CRC.calculate(container, 0, container.size)
                    val request = sentRequests.find { it.index == resp.index && it.archive == resp.archive }
                    storage.store(resp.index, resp.archive, container, request?.version ?: 0, crc)
                    progress.complete(container.size)
                    completedThisConnection.incrementAndGet()
                    sentRequests.removeIf { it.index == resp.index && it.archive == resp.archive }
                    inflight.release()
                }
            }
        } catch (_: kotlinx.coroutines.CancellationException) {
        } catch (_: InterruptedException) {
        } catch (e: Exception) {
            System.err.println("\nConnection $id receiver error after $segmentCount segments, ${completedThisConnection.get()} completed, ${pending.size} pending: ${e::class.simpleName}: ${e.message}")
            throw e
        }
    }
}

private class PendingResponse(
    val index: Int,
    val archive: Int
) {
    var buffer: ByteBuffer? = null
    var totalSize: Int = 0
    var offset: Int = 0
}
