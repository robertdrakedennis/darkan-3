package com.undercut.game.net

import com.undercut.game.net.decode.ClientPacketDecoder
import com.undercut.game.net.decode.ServerPacketDecoder
import com.undercut.game.net.model.ClientPacket
import com.undercut.game.net.model.ServerPacket
import java.io.BufferedWriter
import java.io.File
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object PacketLogger {
    private var writer: BufferedWriter? = null
    private val timeFmt = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")

    data class PacketEntry(
        val time: String,
        val direction: Char,
        val name: String,
        val opcode: Int,
        val size: Int,
        val hex: String,
        val decoded: String?,
        val category: String?
    )

    /** Callback for the UI packets tab. Set by PacketsTab.registerCallback(). */
    var onPacketLogged: ((PacketEntry) -> Unit)? = null

    // 947-3 opcodes — filter high-frequency spam packets from file log
    private val filteredServerOpcodes = setOf(
        216,        // NO_TIMEOUT (keepalive)
        171,        // SERVER_TICK_END
        27,         // PLAYER_INFO (main sync)
        78, 180,    // PLAYER_INFO_DECODE / _2
        12, 205,    // NPC_INFO / NPC_INFO_THUNK
    )

    // 947-3: Most client opcodes are UNKNOWN — filter nothing until identified.
    // Once event/camera/mouse opcodes are confirmed, add them here.
    private val filteredClientOpcodes = setOf<Int>()

    fun init() {
        val logDir = File(System.getProperty("user.home"), ".undercut/logs")
        logDir.mkdirs()
        val date = LocalDate.now().toString()
        val logFile = File(logDir, "packets-$date.log")
        writer = logFile.bufferedWriter(Charsets.UTF_8, bufferSize = 8192).also {
            it.write("# Packet log started at ${LocalTime.now().format(timeFmt)}\n")
            it.flush()
        }
        println("[PacketLogger] Logging to ${logFile.absolutePath}")
    }

    fun close() {
        writer?.let {
            it.write("# Packet log closed at ${LocalTime.now().format(timeFmt)}\n")
            it.flush()
            it.close()
        }
        writer = null
    }

    fun logServerPacket(opcode: Int, size: Int, payload: ByteArray) {
        val prot = ServerProt.forOpcode(opcode)
        val name = prot?.name ?: "UNKNOWN_$opcode"
        val hex = PacketReader.hexDump(payload)
        val time = LocalTime.now().format(timeFmt)

        val decoded = if (prot != null) {
            val d = ServerPacketDecoder.decode(prot, payload)
            if (d != null && d !is ServerPacket.Raw) d.toString() else null
        } else null

        // Push to UI callback (all packets, before file filter)
        try {
            onPacketLogged?.invoke(PacketEntry(time, 'S', name, opcode, size, hex, decoded, prot?.category?.name))
        } catch (_: Throwable) {}

        // File logging (filtered)
        if (opcode in filteredServerOpcodes) return

        val sb = StringBuilder(128)
        sb.append("[$time] [S] $name($opcode) size=$size")
        if (payload.isNotEmpty()) sb.append(" | $hex")
        if (decoded != null) sb.append("\n\t-> $decoded")

        writeLine(sb.toString())
    }

    fun logClientPacket(opcode: Int, size: Int, payload: ByteArray) {
        val prot = ClientProt.forOpcode(opcode)
        val name = prot?.name ?: "UNKNOWN_$opcode"
        val hex = PacketReader.hexDump(payload)
        val time = LocalTime.now().format(timeFmt)

        val decoded = if (prot != null) {
            val d = ClientPacketDecoder.decode(prot, payload)
            if (d != null && d !is ClientPacket.Raw) d.toString() else null
        } else null

        // Push to UI callback (all packets, before file filter)
        try {
            onPacketLogged?.invoke(PacketEntry(time, 'C', name, opcode, size, hex, decoded, prot?.category?.name))
        } catch (_: Throwable) {}

        // File logging (filtered)
        if (opcode in filteredClientOpcodes) return

        val sb = StringBuilder(128)
        sb.append("[$time] [C] $name($opcode) size=$size")
        if (payload.isNotEmpty()) sb.append(" | $hex")
        if (decoded != null) sb.append("\n\t-> $decoded")

        writeLine(sb.toString())
    }

    @Synchronized
    private fun writeLine(line: String) {
        try {
            writer?.let {
                it.write(line)
                it.newLine()
                it.flush()
            }
        } catch (e: Exception) {
            // Silently ignore write failures to avoid impacting game
        }
    }
}
