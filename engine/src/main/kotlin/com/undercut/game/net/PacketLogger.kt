package com.undercut.game.net

import kotlinx.coroutines.runBlocking
import kotlinx.io.Buffer
import kotlinx.io.write
import org.darkan.core.net.prot.Codec
import org.darkan.core.net.prot.revision.rev948.register948
import world.gregs.voidps.gameval.Gameval
import java.io.BufferedWriter
import java.io.File
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Packet dump pipeline. Opcode names, sizes and structured decoders all come from the shared
 * `:core` protocol ([register948]) — the engine no longer carries its own opcode tables.
 *
 * - **C->S**: named + structurally decoded via core's rev948 client decoders (falls back to raw
 *   hex when an opcode has no decoder, e.g. keepalives or not-yet-mapped opcodes).
 * - **S->C**: named + structurally decoded via core's rev948 server display decoders
 *   ([Codec.serverDecodersByOpcode], the inverse of the server encoders). Falls back to raw hex
 *   for opcodes without a decoder (e.g. bit-packed PLAYER_INFO/REBUILD_NORMAL).
 * - **Raw**: the login/RSA handshake (pre-prot, ISAAC not yet meaningful) is dumped byte-for-byte
 *   via [logRaw].
 */
object PacketLogger {
    private val codec: Codec = register948()
    private var writer: BufferedWriter? = null
    private val timeFmt = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")
    private val fileFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")

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
        val logFile = File(logDir, "packets-${LocalDateTime.now().format(fileFmt)}.log")
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
        emit('S', codec.serverProtName(opcode), opcode, size, payload, decoded = decodeServer(opcode, payload))

    /**
     * Incoming packet whose payload could not be read coherently (no connection's in-flight
     * (opcode, size) matched the hook). The body is withheld rather than fabricated — a withheld,
     * clearly-marked entry is honest; a mislabeled body silently corrupts every downstream analysis.
     */
    fun logServerPacketDesync(opcode: Int, size: Int, detail: String) {
        val name = runCatching { codec.serverProtName(opcode) }.getOrDefault("UNKNOWN")
        val time = LocalTime.now().format(timeFmt)
        runCatching {
            onPacketLogged?.invoke(PacketEntry(time, 'S', "DESYNC:$name", opcode, size, "", "payload withheld ($detail)"))
        }
        writeLine("[$time] S> [DESYNC] $name (op=$opcode, claimed ${size}B) payload withheld — $detail")
    }

    fun logClientPacket(opcode: Int, size: Int, payload: ByteArray) =
        emit('C', codec.clientProtName(opcode), opcode, size, payload, decoded = decodeClient(opcode, payload))

    /** Raw on-the-wire bytes (login/RSA handshake, before prots/ISAAC carry meaning). */
    fun logRaw(direction: Char, bytes: ByteArray) =
        emit(direction, "RAW", opcode = -1, size = bytes.size, payload = bytes, decoded = null)

    /** A varbit (already unpacked from its parent var) whose value changed within a var update. */
    data class VarBitDelta(val id: Int, val prev: Int, val value: Int)

    /**
     * Player var (varp) update, by gameval name + id, plus any varbits PACKED into it whose unpacked
     * value changed. The server batches packed varps to save bandwidth — one varp update can carry
     * several varbit changes; a varp with no packed varbit prints on its own line.
     */
    fun logVarp(varId: Int, prev: Int, value: Int, varbits: List<VarBitDelta>) =
        logVar("VARP", Gameval.varp(varId), varId, prev, value, varbits)

    /** Client var (varc) update + any varbits packed into it whose unpacked value changed. */
    fun logVarc(varId: Int, prev: Int, value: Int, varbits: List<VarBitDelta>) =
        logVar("VARC", Gameval.varc(varId), varId, prev, value, varbits)

    private fun logVar(kind: String, name: String?, varId: Int, prev: Int, value: Int, varbits: List<VarBitDelta>) {
        val time = LocalTime.now().format(timeFmt)
        val sb = StringBuilder(80)
        sb.append('[').append(time).append("] ").append(kind).append(' ')
            .append(name ?: "?").append(" (").append(varId).append("): ").append(prev).append(" -> ").append(value)
        for (vb in varbits) {
            sb.append("\n    varbit ").append(Gameval.varbit(vb.id) ?: "?")
                .append(" (").append(vb.id).append("): ").append(vb.prev).append(" -> ").append(vb.value)
        }
        runCatching {
            val summary = if (varbits.isEmpty()) "$prev -> $value"
            else varbits.joinToString("; ") { "${Gameval.varbit(it.id) ?: it.id}: ${it.prev}->${it.value}" }
            onPacketLogged?.invoke(PacketEntry(time, 'S', "$kind ${name ?: varId}", varId, 0, "", summary))
        }
        writeLine(sb.toString())
    }

    private fun decodeClient(opcode: Int, payload: ByteArray): String? {
        val decoder = codec.clientProtsByOpcode[opcode]?.decoder ?: return null
        return runCatching {
            val source = Buffer().apply { write(payload) }
            runBlocking { decoder.invoke(source, payload.size) }.toString()
        }.getOrNull()
    }

    private fun decodeServer(opcode: Int, payload: ByteArray): String? {
        val decoder = codec.serverDecodersByOpcode[opcode] ?: return null
        return runCatching {
            val source = Buffer().apply { write(payload) }
            runBlocking { decoder.invoke(source, payload.size) }
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
