package org.darkan.tools.recorder

import java.io.DataInputStream
import java.io.EOFException
import java.io.File

/**
 * Reader for the binary capture format written by `libdarkan_recorder.dylib`
 * (see `client/launcher/recorder-mac/src/capture.rs` for the authoritative
 * spec). Little-endian throughout; the dylib runs x86_64.
 *
 * File header: magic u32 "DKRC" (0x444B5243) + version u16 + pid u32.
 * Then a stream of records, each: rec_type u8 + ts_nanos u64 + type body.
 */

private const val FILE_MAGIC = 0x444B5243 // "DKRC"

enum class RecType(val id: Int) {
    IO(0x01), CONNECT(0x02), CLOSE(0x03), SEEDS(0x04), NOTE(0x05);

    companion object {
        fun of(id: Int): RecType? = entries.firstOrNull { it.id == id }
    }
}

/** Direction of an IO record. */
enum class Dir { IN, OUT }

/** Which libc fn produced an IO record (matches capture.rs SyscallKind). */
enum class SyscallKind(val id: Int) {
    RECV(0), READ(1), RECVFROM(2), SEND(3), WRITE(4), SENDTO(5);

    companion object {
        fun of(id: Int): SyscallKind = entries.firstOrNull { it.id == id } ?: RECV
    }
}

sealed class CaptureRecord {
    abstract val tsNanos: Long

    data class Io(
        override val tsNanos: Long,
        val fd: Int,
        val dir: Dir,
        val syscall: SyscallKind,
        val bytes: ByteArray,
    ) : CaptureRecord()

    data class Connect(
        override val tsNanos: Long,
        val fd: Int,
        val family: Int,
        val port: Int,
        val addr: ByteArray,
    ) : CaptureRecord() {
        /** Dotted/bracketed peer address string for logs. */
        fun addrString(): String = when (family) {
            2 -> addr.joinToString(".") { (it.toInt() and 0xFF).toString() } // AF_INET
            30 -> // AF_INET6
                (0 until addr.size step 2).joinToString(":") {
                    "%02x%02x".format(addr[it].toInt() and 0xFF, addr.getOrElse(it + 1) { 0 }.toInt() and 0xFF)
                }
            else -> addr.joinToString("") { "%02x".format(it.toInt() and 0xFF) }
        }
    }

    data class Close(override val tsNanos: Long, val fd: Int) : CaptureRecord()

    data class Seeds(
        override val tsNanos: Long,
        val fdHint: Int,
        val seeds: IntArray, // 4 RAW C2S seeds (S2C = each + 50)
    ) : CaptureRecord()

    data class Note(override val tsNanos: Long, val text: String) : CaptureRecord()
}

data class Capture(
    val pid: Int,
    val version: Int,
    val records: List<CaptureRecord>,
) {
    fun seeds(): IntArray? = records.filterIsInstance<CaptureRecord.Seeds>().lastOrNull()?.seeds
    fun notes(): List<String> = records.filterIsInstance<CaptureRecord.Note>().map { it.text }
}

fun Capture.dedupeIo(): Capture {
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
    if (dropped > 0) System.err.println("[capture] deduped IO records: $dropped")
    return copy(records = filtered)
}

object CaptureReader {

    fun read(file: File): Capture {
        require(file.isFile) { "capture file not found: ${file.absolutePath}" }
        DataInputStream(file.inputStream().buffered()).use { input ->
            val magic = readU32LE(input)
            require(magic == FILE_MAGIC.toLong()) {
                "bad capture magic 0x${magic.toString(16)} (expected DKRC) in ${file.name}"
            }
            val version = readU16LE(input)
            val pid = readU32LE(input).toInt()

            val records = ArrayList<CaptureRecord>()
            while (true) {
                val typeId = try {
                    input.read()
                } catch (e: EOFException) {
                    -1
                }
                if (typeId < 0) break // clean EOF

                val ts = try {
                    readU64LE(input)
                } catch (e: EOFException) {
                    // Truncated record (client crashed mid-write) — stop cleanly.
                    System.err.println("[capture] truncated at record header; stopping (have ${records.size} records)")
                    break
                }

                val rec = try {
                    when (RecType.of(typeId)) {
                        RecType.IO -> readIo(input, ts)
                        RecType.CONNECT -> readConnect(input, ts)
                        RecType.CLOSE -> CaptureRecord.Close(ts, readI32LE(input))
                        RecType.SEEDS -> readSeeds(input, ts)
                        RecType.NOTE -> readNote(input, ts)
                        null -> {
                            System.err.println("[capture] unknown record type $typeId; stopping")
                            null
                        }
                    }
                } catch (e: EOFException) {
                    System.err.println("[capture] truncated mid-record (type $typeId); stopping")
                    null
                }
                if (rec == null) break
                records.add(rec)
            }
            return Capture(pid, version, records)
        }
    }

    private fun readIo(input: DataInputStream, ts: Long): CaptureRecord.Io {
        val fd = readI32LE(input)
        val dirByte = input.readUnsignedByte()
        val syscall = SyscallKind.of(input.readUnsignedByte())
        val len = readU32LE(input).toInt()
        val bytes = ByteArray(len)
        input.readFully(bytes)
        return CaptureRecord.Io(ts, fd, if (dirByte == 0) Dir.IN else Dir.OUT, syscall, bytes)
    }

    private fun readConnect(input: DataInputStream, ts: Long): CaptureRecord.Connect {
        val fd = readI32LE(input)
        val family = input.readUnsignedByte()
        val port = readU16LE(input)
        val addrLen = input.readUnsignedByte()
        val addr = ByteArray(addrLen)
        input.readFully(addr)
        return CaptureRecord.Connect(ts, fd, family, port, addr)
    }

    private fun readSeeds(input: DataInputStream, ts: Long): CaptureRecord.Seeds {
        val fdHint = readI32LE(input)
        val seeds = IntArray(4) { readI32LE(input) }
        return CaptureRecord.Seeds(ts, fdHint, seeds)
    }

    private fun readNote(input: DataInputStream, ts: Long): CaptureRecord.Note {
        val len = readU16LE(input)
        val bytes = ByteArray(len)
        input.readFully(bytes)
        return CaptureRecord.Note(ts, String(bytes, Charsets.US_ASCII))
    }

    // --- LE primitives (DataInputStream is big-endian by default) ---
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
}
