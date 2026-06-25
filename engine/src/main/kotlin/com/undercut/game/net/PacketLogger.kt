package com.undercut.game.net

import kotlinx.coroutines.runBlocking
import kotlinx.io.Buffer
import kotlinx.io.write
import org.darkan.core.net.prot.Codec
import org.darkan.core.net.prot.revision.rev948.register948
import java.io.BufferedWriter
import java.io.File
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Packet dump pipeline. Opcode names, sizes and structured decoders all come from the shared
 * `:core` protocol ([register948]) — the engine no longer carries its own opcode tables.
 *
 * - **C->S**: named + structurally decoded via core's rev948 client decoders (falls back to raw
 *   hex when an opcode has no decoder, e.g. keepalives or not-yet-mapped opcodes).
 * - **S->C**: named + raw body. Core only ships server *encoders* (decoding S->C is server-side
 *   only), so there is no structured decode for incoming packets here — by design.
 * - **Raw**: the login/RSA handshake (pre-prot, ISAAC not yet meaningful) is dumped byte-for-byte
 *   via [logRaw].
 */
object PacketLogger {
    private val codec: Codec = register948()
    private var writer: BufferedWriter? = null
    private val timeFmt = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")

    data class PacketEntry(
        val time: String,
        val direction: Char,   // 'S' (server->client), 'C' (client->server), 'R' (raw login)
        val name: String,
        val opcode: Int,       // -1 for raw chunks with no opcode
        val size: Int,
        val hex: String,
        val decoded: String?,  // structured fields (C->S with a decoder), else null
        val category: String? = null,
    )

    /** Callback for the UI packets tab. Set by PacketsTab. */
    var onPacketLogged: ((PacketEntry) -> Unit)? = null

    fun init() {
        val logDir = File(System.getProperty("user.home"), ".undercut/logs")
        logDir.mkdirs()
        val logFile = File(logDir, "packets-${LocalDate.now()}.log")
        writer = logFile.bufferedWriter(Charsets.UTF_8, bufferSize = 8192).also {
            it.write("# Packet log started at ${LocalTime.now().format(timeFmt)}\n")
            it.flush()
        }
        println("[PacketLogger] Logging to ${logFile.absolutePath}")
    }

    fun close() {
        writer?.let {
            runCatching {
                it.write("# Packet log closed at ${LocalTime.now().format(timeFmt)}\n")
                it.flush()
                it.close()
            }
        }
        writer = null
    }

    fun logServerPacket(opcode: Int, size: Int, payload: ByteArray) =
        emit('S', codec.serverProtName(opcode), opcode, size, payload, decoded = null)

    fun logClientPacket(opcode: Int, size: Int, payload: ByteArray) =
        emit('C', codec.clientProtName(opcode), opcode, size, payload, decoded = decodeClient(opcode, payload))

    /** Raw on-the-wire bytes (login/RSA handshake, before prots/ISAAC carry meaning). */
    fun logRaw(direction: Char, bytes: ByteArray) =
        emit(direction, "RAW", opcode = -1, size = bytes.size, payload = bytes, decoded = null)

    private fun decodeClient(opcode: Int, payload: ByteArray): String? {
        val decoder = codec.clientProtsByOpcode[opcode]?.decoder ?: return null
        return runCatching {
            val source = Buffer().apply { write(payload) }
            runBlocking { decoder.invoke(source, payload.size) }.toString()
        }.getOrNull()
    }

    private fun emit(direction: Char, name: String, opcode: Int, size: Int, payload: ByteArray, decoded: String?) {
        val time = LocalTime.now().format(timeFmt)
        val hex = hexDump(payload)
        runCatching { onPacketLogged?.invoke(PacketEntry(time, direction, name, opcode, size, hex, decoded)) }

        val sb = StringBuilder(96)
        sb.append('[').append(time).append("] ").append(direction).append("> ").append(name)
        if (opcode >= 0) sb.append(" (op=").append(opcode).append(", ").append(size).append("B)")
        else sb.append(" (").append(size).append("B)")
        if (decoded != null) sb.append("\n    ").append(decoded)
        if (payload.isNotEmpty()) sb.append("\n    hex: ").append(hex)
        writeLine(sb.toString())
    }

    @Synchronized
    private fun writeLine(line: String) {
        runCatching {
            writer?.let {
                it.write(line)
                it.newLine()
                it.flush()
            }
        }
    }

    private fun hexDump(bytes: ByteArray): String =
        bytes.joinToString(" ") { "%02x".format(it.toInt() and 0xFF) }
}
