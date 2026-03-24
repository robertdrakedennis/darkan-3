package org.darkan.tools.cachedownloader

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import world.gregs.voidps.buffer.read.BufferReader
import world.gregs.voidps.cache.compress.DecompressionContext
import world.gregs.voidps.cache.secure.CRC
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URI
import java.nio.file.Paths

data class ArchiveEntry(val id: Int, val crc: Int, val version: Int)

class CacheDownloader(
    private val host: String,
    private val port: Int,
    private var major: Int,
    private var minor: Int,
    private val connectionCount: Int,
    private val outputPath: String,
    private val token: String,
    private val contentUrl: String = "http://content.runescape.com"
) {
    private val storage = CacheStorage(Paths.get(outputPath))
    private val progress = ProgressTracker()

    companion object {
        val HTTP_INDICES = setOf(40) // Music — served via HTTP, not TCP
    }

    suspend fun download() {
        try {
            println("Phase 1: Detecting version...")
            detectVersion()
            println("  Detected version: $major.$minor")

            println("Phase 2: Downloading master index and ref tables...")
            val archivesByIndex = downloadRefTables()
            println("  Downloaded ref tables for ${archivesByIndex.size} indices")

            // Separate HTTP indices from TCP indices
            val httpIndices = archivesByIndex.filterKeys { it in HTTP_INDICES }
            val tcpArchivesByIndex = archivesByIndex.filterKeys { it !in HTTP_INDICES }

            println("Phase 3: Validating TCP indices and building download queue...")
            val (servableIndices, badArchives) = validateIndices(tcpArchivesByIndex)
            println("  ${servableIndices.size}/${tcpArchivesByIndex.size} TCP indices servable, ${badArchives.size} bad archives")

            val queue = Channel<FileRequest>(Channel.UNLIMITED)
            var totalFiles = 0
            for ((index, archives) in tcpArchivesByIndex) {
                if (index !in servableIndices) {
                    println("  Index $index: skipped (not served by content server)")
                    continue
                }
                val existingVersions = storage.allVersions(index)
                var skipped = 0
                var badSkipped = 0
                var stale = 0
                for (entry in archives) {
                    val key = (index.toLong() shl 32) or entry.id.toLong()
                    if (key in badArchives) {
                        badSkipped++
                    } else {
                        val cached = existingVersions[entry.id]
                        if (cached == null) {
                            // Not cached at all
                            queue.send(FileRequest(index, entry.id))
                            totalFiles++
                        } else if (cached.first != entry.version || cached.second != entry.crc) {
                            // Cached but version/CRC changed — re-download
                            queue.send(FileRequest(index, entry.id))
                            totalFiles++
                            stale++
                        } else {
                            skipped++
                        }
                    }
                }
                val queued = archives.size - skipped - badSkipped
                if (skipped > 0 || badSkipped > 0 || stale > 0)
                    println("  Index $index: skipped $skipped cached, $stale stale (re-downloading), $badSkipped unservable, queued $queued")
            }
            progress.totalFiles.set(totalFiles)
            println("  $totalFiles TCP files to download (skipped already-cached)")

            if (totalFiles > 0) {
                println("Phase 4: Downloading TCP files with $connectionCount connections...")
                queue.close()

                coroutineScope {
                    repeat(connectionCount) { id ->
                        launch {
                            JS5Connection(id, host, port, major, minor, token, queue, storage, progress).run()
                        }
                    }
                    launch {
                        while (isActive && (progress.completedFiles.get() + progress.failedFiles.get()) < totalFiles) {
                            progress.report()
                            delay(2000)
                        }
                        progress.report()
                    }
                }

                progress.summary()
            } else {
                queue.close()
                println("  All TCP files already cached.")
            }

            // Download HTTP indices (music etc.)
            if (httpIndices.isNotEmpty()) {
                println("Phase 5: Downloading HTTP indices (music)...")
                for ((index, archives) in httpIndices) {
                    downloadHttpIndex(index, archives)
                }
            }

            println("Download complete!")
        } finally {
            storage.close()
        }
    }

    private suspend fun downloadHttpIndex(index: Int, archives: Array<ArchiveEntry>) {
        val existingVersions = storage.allVersions(index)
        var remaining = archives.filter { entry ->
            val cached = existingVersions[entry.id]
            cached == null || cached.first != entry.version || cached.second != entry.crc
        }
        println("  Index $index: ${archives.size} total, ${archives.size - remaining.size} cached, ${remaining.size} to download via HTTP")

        if (remaining.isEmpty()) return

        val concurrency = 8
        var totalCompleted = 0
        var totalFailed = 0
        var pass = 0

        while (remaining.isNotEmpty()) {
            pass++
            val completed = java.util.concurrent.atomic.AtomicInteger(0)
            val failedEntries = java.util.concurrent.ConcurrentLinkedQueue<ArchiveEntry>()
            val total = remaining.size

            if (pass > 1) println("  Index $index pass $pass: retrying $total archives...")

            coroutineScope {
                val semaphore = kotlinx.coroutines.sync.Semaphore(concurrency)
                for (entry in remaining) {
                    semaphore.acquire()
                    launch(Dispatchers.IO) {
                        try {
                            val data = httpGetArchive(index, entry.id, entry.crc, entry.version)
                            if (data != null) {
                                val crc = CRC.calculate(data, 0, data.size)
                                storage.store(index, entry.id, data, entry.version, crc)
                                val c = completed.incrementAndGet()
                                if (c % 500 == 0) {
                                    print("\r  Index $index: $c/$total downloaded, ${failedEntries.size} failed")
                                    System.out.flush()
                                }
                            } else {
                                failedEntries.add(entry)
                            }
                        } catch (_: Exception) {
                            failedEntries.add(entry)
                        } finally {
                            semaphore.release()
                        }
                    }
                }
            }

            totalCompleted += completed.get()
            totalFailed = failedEntries.size
            println("\r  Index $index: $totalCompleted downloaded total, $totalFailed failed this pass                    ")

            if (failedEntries.isEmpty() || pass >= 5) break
            remaining = failedEntries.toList()
            delay(2000L * pass) // backoff between retry passes
        }

        if (totalFailed > 0) {
            println("  Index $index: $totalFailed archives could not be downloaded after $pass passes")
        }
    }

    private fun httpGetArchive(index: Int, groupId: Int, crc: Int, version: Int): ByteArray? {
        val url = "$contentUrl/ms?m=0&a=$index&k=0&g=$groupId&c=$crc&v=$version"
        val conn = URI(url).toURL().openConnection() as HttpURLConnection
        conn.connectTimeout = 15_000
        conn.readTimeout = 30_000
        conn.requestMethod = "GET"
        try {
            val responseCode = conn.responseCode
            if (responseCode != 200) return null
            val contentType = conn.contentType ?: ""
            if (!contentType.contains("octet-stream")) return null
            return conn.inputStream.readAllBytes()
        } finally {
            conn.disconnect()
        }
    }

    private fun detectVersion() {
        for (delta in 0..50) {
            if (delta == 0) {
                val result = tryHandshake(major, minor)
                if (result == 0) return
                println("  $major.$minor -> response $result")
            } else {
                val upResult = tryHandshake(major, minor + delta)
                if (upResult == 0) { minor += delta; return }
                if (upResult != -1) println("  $major.${minor + delta} -> response $upResult")

                if (minor - delta >= 0) {
                    val downResult = tryHandshake(major, minor - delta)
                    if (downResult == 0) { minor -= delta; return }
                    if (downResult != -1) println("  $major.${minor - delta} -> response $downResult")
                }
            }
        }

        for (m in 0..20) {
            val result = tryHandshake(major + 1, m)
            if (result == 0) { major += 1; minor = m; return }
        }

        throw IllegalStateException("Could not detect a valid version near $major.$minor")
    }

    private fun tryHandshake(majorVersion: Int, minorVersion: Int): Int {
        return try {
            val sock = java.net.Socket(host, port)
            sock.soTimeout = 10_000
            try {
                val out = DataOutputStream(sock.getOutputStream())
                val inp = DataInputStream(sock.getInputStream())
                JS5Protocol.handshake(out, inp, majorVersion, minorVersion, token)
            } finally {
                sock.close()
            }
        } catch (_: Exception) {
            -1
        }
    }

    private fun validateIndices(archivesByIndex: Map<Int, Array<ArchiveEntry>>): Pair<Set<Int>, Set<Long>> {
        val servable = mutableSetOf<Int>()
        val badArchives = mutableSetOf<Long>()

        for ((index, archives) in archivesByIndex) {
            if (archives.isEmpty()) continue
            var found = false
            for (entry in archives.take(5)) {
                if (probeArchive(index, entry.id)) {
                    found = true
                    break
                } else {
                    badArchives.add((index.toLong() shl 32) or entry.id.toLong())
                }
            }
            if (found) servable.add(index)
        }
        return Pair(servable, badArchives)
    }

    private fun probeArchive(index: Int, archive: Int): Boolean {
        return try {
            val (sock, inp, out) = JS5Protocol.connect(host, port, major, minor, token, soTimeout = 10_000)
            JS5Protocol.sendFileRequest(out, index, archive, major)
            inp.readUnsignedByte() // index
            inp.readInt()          // hash
            inp.readUnsignedByte() // compression
            val size = inp.readInt()
            sock.close()
            size in 0..50_000_000
        } catch (_: Exception) {
            false
        }
    }

    private fun downloadRefTables(): Map<Int, Array<ArchiveEntry>> {
        val context = DecompressionContext()

        var (sock, inp, out) = JS5Protocol.connect(host, port, major, minor, token)
        var currentSocket = sock

        val (indexEntries, indexCount) = downloadMasterIndex(inp, out, context)

        val archivesByIndex = mutableMapOf<Int, Array<ArchiveEntry>>()
        var requestsOnConnection = 0
        val maxRequestsPerConnection = 30

        for (indexId in 0 until indexCount) {
            val entry = indexEntries[indexId]
            if (entry.crc == 0 && entry.version == 0) continue

            print("  Index $indexId: requesting... ")
            System.out.flush()

            if (requestsOnConnection >= maxRequestsPerConnection) {
                try { currentSocket.close() } catch (_: Exception) {}
                val conn = JS5Protocol.connect(host, port, major, minor, token)
                currentSocket = conn.first; inp = conn.second; out = conn.third
                requestsOnConnection = 0
            }

            try {
                val archives = downloadIndexRefTable(inp, out, indexId, context)
                requestsOnConnection++
                if (archives != null) {
                    archivesByIndex[indexId] = archives
                    println("${archives.size} archives")
                } else {
                    println("failed to parse ref table")
                }
            } catch (e: Exception) {
                System.err.println("  Index $indexId: error (${e.message}), reconnecting...")
                try { currentSocket.close() } catch (_: Exception) {}
                val conn = JS5Protocol.connect(host, port, major, minor, token)
                currentSocket = conn.first; inp = conn.second; out = conn.third
                requestsOnConnection = 0

                // Retry once
                try {
                    val archives = downloadIndexRefTable(inp, out, indexId, context)
                    requestsOnConnection++
                    if (archives != null) {
                        archivesByIndex[indexId] = archives
                        println("${archives.size} archives (retry)")
                    } else {
                        println("failed to parse ref table (retry)")
                    }
                } catch (e2: Exception) {
                    System.err.println("  Index $indexId: retry also failed (${e2.message})")
                }
            }
        }

        try { currentSocket.close() } catch (_: Exception) {}
        return archivesByIndex
    }

    private fun downloadMasterIndex(
        inp: DataInputStream, out: DataOutputStream, context: DecompressionContext
    ): Pair<Array<IndexEntry>, Int> {
        JS5Protocol.sendFileRequest(out, 255, 255, major)
        println("  Requested master index (255, 255)...")
        val masterResponse = JS5Protocol.readResponse(inp)
        println("  Master index received: ${masterResponse.container.size} bytes")

        val masterCrc = CRC.calculate(masterResponse.container)
        storage.storeRefTable(255, masterResponse.container, 0, masterCrc)

        val masterDecompressed = context.decompress(masterResponse.container)
            ?: throw IllegalStateException("Failed to decompress master index")

        val masterReader = BufferReader(masterDecompressed)
        val indexCount = masterReader.readUnsignedByte()

        val indexEntries = Array(indexCount) {
            val crc = masterReader.readInt()
            val version = masterReader.readInt()
            val files = masterReader.readInt()
            val size = masterReader.readInt()
            masterReader.skip(64)
            IndexEntry(crc, version, files, size)
        }

        println("  Master index: $indexCount indices")
        return Pair(indexEntries, indexCount)
    }

    private fun downloadIndexRefTable(
        inp: DataInputStream, out: DataOutputStream,
        indexId: Int, context: DecompressionContext
    ): Array<ArchiveEntry>? {
        JS5Protocol.sendFileRequest(out, 255, indexId, major)
        val refResponse = JS5Protocol.readResponse(inp)

        val refCrc = CRC.calculate(refResponse.container)
        storage.storeRefTable(refResponse.archive, refResponse.container, 0, refCrc)

        return parseRefTable(context, refResponse.container)
    }

    private fun parseRefTable(context: DecompressionContext, rawTable: ByteArray): Array<ArchiveEntry>? {
        val decompressed = context.decompress(rawTable) ?: return null
        val reader = BufferReader(decompressed)

        val version = reader.readUnsignedByte()
        if (version < 5 || version > 7) return null
        if (version >= 6) reader.readInt() // revision

        val flags = reader.readUnsignedByte()
        val archiveCount = if (version >= 7) reader.readBigSmart() else reader.readUnsignedShort()

        // Archive IDs (delta-encoded)
        var previous = 0
        val archiveIds = IntArray(archiveCount) {
            val archiveId = if (version >= 7)
                reader.readBigSmart() + previous
            else
                reader.readUnsignedShort() + previous
            previous = archiveId
            archiveId
        }

        // Name hashes (if flags & 1)
        if (flags and 1 != 0) reader.skip(archiveCount * 4)

        // CRCs
        val crcs = IntArray(archiveCount) { reader.readInt() }

        // Unknown hashes (if flags & 8)
        if (flags and 8 != 0) reader.skip(archiveCount * 4)

        // Whirlpool (if flags & 2)
        if (flags and 2 != 0) reader.skip(archiveCount * 64)

        // Sizes (if flags & 4)
        if (flags and 4 != 0) reader.skip(archiveCount * 8)

        // Versions
        val versions = IntArray(archiveCount) { reader.readInt() }

        return Array(archiveCount) { i ->
            ArchiveEntry(archiveIds[i], crcs[i], versions[i])
        }
    }

    private data class IndexEntry(val crc: Int, val version: Int, val files: Int, val size: Int)
}
