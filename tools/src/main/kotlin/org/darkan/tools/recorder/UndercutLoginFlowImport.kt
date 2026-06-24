package org.darkan.tools.recorder

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import org.darkan.core.net.prot.Codec
import org.darkan.core.net.prot.ProtSize
import org.darkan.core.net.prot.revision.rev948.register948
import java.io.File
import kotlin.system.exitProcess
import kotlinx.serialization.json.Json as KotlinJson

object UndercutLoginFlowImport {
    @JvmStatic
    fun main(args: Array<String>) {
        val opts = Options.parse(args)
        val events = opts.eventsFile.useLines { lines ->
            lines.filter { it.isNotBlank() }.mapIndexed { index, line -> parseEvent(index + 1, line) }.toList()
        }
        val codec = register948()
        val report = ReportBuilder(codec, events).build()
        val output = report.lines.joinToString(separator = "\n", postfix = "\n")

        if (opts.outFile != null) {
            opts.outFile.parentFile?.mkdirs()
            opts.outFile.writeText(output)
        } else {
            print(output)
        }

        System.err.println(
            "[undercut-flow] ${opts.eventsFile.absolutePath}: " +
                "${events.size} events, phases=${report.phases.joinToString(", ")}, " +
                "packets=${report.packetEvents}, sockets=${report.socketEvents}, " +
                "codec_gaps=${report.codecGaps}"
        )

        if (opts.requireFullLogin && !report.hasFullLogin) {
            System.err.println("[undercut-flow] FAIL: capture does not include LOGIN -> WORLD_SELECT -> IN_GAME")
            exitProcess(1)
        }
        if (opts.requireWorldTraffic && !report.hasWorldTraffic) {
            System.err.println("[undercut-flow] FAIL: capture does not include required IN_GAME world traffic")
            exitProcess(3)
        }
        if (opts.requireNoUnresolvedLabels && report.unresolvedPacketKinds > 0) {
            System.err.println(
                "[undercut-flow] FAIL: ${report.unresolvedPacketKinds} packet kind(s) have no capture or codec label"
            )
            exitProcess(2)
        }
    }

    private val parser = KotlinJson { ignoreUnknownKeys = true }

    private fun parseEvent(lineNumber: Int, line: String): Event {
        val obj = try {
            parser.parseToJsonElement(line).jsonObject
        } catch (e: Exception) {
            error("invalid JSON at line $lineNumber: ${e.message}")
        }
        val stateName = obj.string("main_state_name") ?: obj.string("state_name")
        val state = obj.int("main_state") ?: obj.int("state")
        return Event(
            lineNumber = lineNumber,
            type = obj.string("type") ?: "unknown",
            epochMs = obj.long("epoch_ms"),
            clientCycle = obj.int("client_cycle"),
            state = state,
            stateName = stateName,
            direction = obj.string("direction"),
            fd = obj.int("fd"),
            opcode = obj.int("opcode"),
            name = obj.string("name"),
            size = obj.int("size"),
            category = obj.string("category"),
            payloadHex = obj.string("payload_hex"),
            syscall = obj.string("syscall"),
            address = obj.string("address"),
            port = obj.int("port"),
            result = obj.int("result"),
            sessionId = obj.string("session_id"),
        )
    }

    private data class Event(
        val lineNumber: Int,
        val type: String,
        val epochMs: Long?,
        val clientCycle: Int?,
        val state: Int?,
        val stateName: String?,
        val direction: String?,
        val fd: Int?,
        val opcode: Int?,
        val name: String?,
        val size: Int?,
        val category: String?,
        val payloadHex: String?,
        val syscall: String?,
        val address: String?,
        val port: Int?,
        val result: Int?,
        val sessionId: String?,
    )

    private class ReportBuilder(
        private val codec: Codec,
        private val events: List<Event>,
    ) {
        private val loginFd = events.firstOrNull {
            it.type == "socket" && it.stateName == "LOGIN_SCREEN" && it.fd != null
        }?.fd
        private val handoffStartCycle = events.firstOrNull {
            it.type == "socket" &&
                it.stateName == "LOBBY_SCREEN" &&
                it.fd != null &&
                loginFd != null &&
                it.fd != loginFd
        }?.clientCycle

        fun build(): Report {
            val phases = linkedMapOf<String, PhaseStats>()
            val packets = linkedMapOf<PacketKey, PacketStats>()
            val sockets = linkedMapOf<SocketKey, SocketStats>()
            val typeCounts = linkedMapOf<String, Long>()
            val stateTransitions = ArrayList<Event>()
            val socketLifecycle = ArrayList<Event>()
            var codecGaps = 0
            var unknownCapturePacketKinds = 0
            var unknownCapturePacketEvents = 0L
            var unknownCodecPacketKinds = 0
            var unknownCodecPacketEvents = 0L
            var codecResolvedCaptureUnknownPacketKinds = 0
            var codecResolvedCaptureUnknownPacketEvents = 0L
            var unresolvedPacketKinds = 0
            var unresolvedPacketEvents = 0L

            for (event in events) {
                typeCounts[event.type] = (typeCounts[event.type] ?: 0L) + 1L
                val phaseName = phaseOf(event)
                val phase = phases.getOrPut(phaseName) { PhaseStats(phaseName) }
                phase.accept(event)

                when (event.type) {
                    "main_state" -> stateTransitions.add(event)
                    "socket_connect", "socket_close" -> socketLifecycle.add(event)
                    "packet" -> {
                        val dir = event.direction ?: "?"
                        val opcode = event.opcode ?: -1
                        val name = event.name ?: "UNKNOWN_$opcode"
                        val key = PacketKey(phaseName, dir, opcode, name)
                        packets.getOrPut(key) { PacketStats(event.category) }.accept(event)
                    }
                    "socket" -> {
                        val dir = event.direction ?: "?"
                        val fd = event.fd ?: -1
                        val key = SocketKey(phaseName, dir, fd)
                        sockets.getOrPut(key) { SocketStats() }.accept(event)
                    }
                }
            }

            val lines = ArrayList<String>()
            lines.add(summaryJson(typeCounts))
            for (transition in stateTransitions) lines.add(stateJson(transition))
            for (phase in phases.values) lines.add(phaseJson(phase))
            for (event in socketLifecycle) lines.add(socketLifecycleJson(event))

            for ((key, stats) in packets.entries.sortedWith(packetComparator())) {
                val codecInfo = codecInfo(key.direction, key.opcode)
                if (codecInfo.status == "unregistered") codecGaps++
                val captureUnknown = key.name.isUnknownLabel()
                val codecUnknown = codecInfo.name.isUnknownLabel()
                if (captureUnknown) {
                    unknownCapturePacketKinds++
                    unknownCapturePacketEvents += stats.count
                }
                if (codecUnknown) {
                    unknownCodecPacketKinds++
                    unknownCodecPacketEvents += stats.count
                }
                if (captureUnknown && !codecUnknown) {
                    codecResolvedCaptureUnknownPacketKinds++
                    codecResolvedCaptureUnknownPacketEvents += stats.count
                }
                if (captureUnknown && codecUnknown) {
                    unresolvedPacketKinds++
                    unresolvedPacketEvents += stats.count
                }
                lines.add(packetJson(key, stats, codecInfo))
            }
            lines.add(
                labelSummaryJson(
                    unknownCapturePacketKinds,
                    unknownCapturePacketEvents,
                    unknownCodecPacketKinds,
                    unknownCodecPacketEvents,
                    codecResolvedCaptureUnknownPacketKinds,
                    codecResolvedCaptureUnknownPacketEvents,
                    unresolvedPacketKinds,
                    unresolvedPacketEvents,
                    codecGaps,
                )
            )

            for ((key, stats) in sockets.entries.sortedWith(socketComparator())) {
                lines.add(socketJson(key, stats))
            }

            val worldTraffic = worldTrafficSummary(packets, sockets)
            lines.add(worldTrafficJson(worldTraffic))

            val phaseNames = phases.keys.toList()
            val hasFullLogin = listOf("LOGIN", "WORLD_SELECT", "IN_GAME").all { it in phaseNames }
            return Report(
                lines = lines,
                phases = phaseNames,
                packetEvents = typeCounts["packet"] ?: 0L,
                socketEvents = typeCounts["socket"] ?: 0L,
                codecGaps = codecGaps,
                unresolvedPacketKinds = unresolvedPacketKinds,
                hasFullLogin = hasFullLogin,
                hasWorldTraffic = worldTraffic.hasRequiredTraffic,
            )
        }

        private fun phaseOf(event: Event): String = when (event.stateName) {
            "LOGIN_SCREEN" -> "LOGIN"
            "LOBBY_SCREEN" -> {
                val handoff = handoffStartCycle
                if (handoff != null && event.clientCycle != null && event.clientCycle >= handoff) {
                    "WORLD_LOGIN_HANDOFF"
                } else {
                    "WORLD_SELECT"
                }
            }
            "LOGGED_IN" -> "IN_GAME"
            null -> if (event.type.startsWith("session_")) "SESSION" else "UNKNOWN"
            else -> event.stateName
        }

        private fun summaryJson(typeCounts: Map<String, Long>): String {
            val sessionId = events.firstNotNullOfOrNull { it.sessionId } ?: ""
            val firstEpoch = events.asSequence().mapNotNull { it.epochMs }.firstOrNull()
            val lastEpoch = events.asReversed().asSequence().mapNotNull { it.epochMs }.firstOrNull()
            return Json.obj(
                "record" to "capture_summary",
                "session_id" to sessionId,
                "events" to events.size,
                "duration_ms" to if (firstEpoch != null && lastEpoch != null) lastEpoch - firstEpoch else null,
                "session_start" to (typeCounts["session_start"] ?: 0L),
                "main_state" to (typeCounts["main_state"] ?: 0L),
                "tick" to (typeCounts["tick"] ?: 0L),
                "packet" to (typeCounts["packet"] ?: 0L),
                "socket" to (typeCounts["socket"] ?: 0L),
                "socket_connect" to (typeCounts["socket_connect"] ?: 0L),
                "socket_close" to (typeCounts["socket_close"] ?: 0L),
                "session_stop" to (typeCounts["session_stop"] ?: 0L),
                "lobby_fd" to loginFd,
                "world_handoff_cycle" to handoffStartCycle,
            )
        }

        private fun stateJson(event: Event): String = Json.obj(
            "record" to "main_state",
            "phase" to phaseOf(event),
            "state" to event.state,
            "state_name" to event.stateName,
            "cycle" to event.clientCycle,
            "epoch_ms" to event.epochMs,
        )

        private fun phaseJson(phase: PhaseStats): String = Json.obj(
            "record" to "phase",
            "phase" to phase.name,
            "first_cycle" to phase.firstCycle,
            "last_cycle" to phase.lastCycle,
            "duration_ms" to phase.durationMs(),
            "events" to phase.events,
            "ticks" to phase.ticks,
            "packets" to phase.packets,
            "server_packets" to phase.serverPackets,
            "client_packets" to phase.clientPackets,
            "socket_events" to phase.socketEvents,
            "socket_bytes" to phase.socketBytes,
            "socket_connects" to phase.socketConnects,
            "socket_closes" to phase.socketCloses,
        )

        private fun socketLifecycleJson(event: Event): String = Json.obj(
            "record" to event.type,
            "phase" to phaseOf(event),
            "fd" to event.fd,
            "address" to event.address,
            "port" to event.port,
            "result" to event.result,
            "cycle" to event.clientCycle,
            "epoch_ms" to event.epochMs,
        )

        private fun packetJson(key: PacketKey, stats: PacketStats, codecInfo: CodecInfo): String = Json.obj(
            *run {
                val observed = observedLabel(key, codecInfo)
                arrayOf(
                    "record" to "packet_count",
                    "phase" to key.phase,
                    "dir" to key.direction,
                    "opcode" to key.opcode,
                    "capture_name" to key.name,
                    "capture_label" to if (key.name.isUnknownLabel()) "unknown" else "labelled",
                    "codec_name" to codecInfo.name,
                    "codec_label" to if (codecInfo.name.isUnknownLabel()) "unknown" else "labelled",
                    "observed_label" to observed.first,
                    "observed_label_source" to observed.second,
                    "codec_size" to codecInfo.sizeKind,
                    "codec_status" to codecInfo.status,
                    "category" to stats.category,
                    "count" to stats.count,
                    "min_size" to stats.minSize,
                    "max_size" to stats.maxSize,
                    "total_payload_bytes" to stats.totalBytes,
                    "first_cycle" to stats.firstCycle,
                    "last_cycle" to stats.lastCycle,
                )
            }
        )

        private fun labelSummaryJson(
            unknownCapturePacketKinds: Int,
            unknownCapturePacketEvents: Long,
            unknownCodecPacketKinds: Int,
            unknownCodecPacketEvents: Long,
            codecResolvedCaptureUnknownPacketKinds: Int,
            codecResolvedCaptureUnknownPacketEvents: Long,
            unresolvedPacketKinds: Int,
            unresolvedPacketEvents: Long,
            codecGaps: Int,
        ): String = Json.obj(
            "record" to "label_summary",
            "unknown_capture_packet_kinds" to unknownCapturePacketKinds,
            "unknown_capture_packet_events" to unknownCapturePacketEvents,
            "unknown_codec_packet_kinds" to unknownCodecPacketKinds,
            "unknown_codec_packet_events" to unknownCodecPacketEvents,
            "codec_resolved_capture_unknown_packet_kinds" to codecResolvedCaptureUnknownPacketKinds,
            "codec_resolved_capture_unknown_packet_events" to codecResolvedCaptureUnknownPacketEvents,
            "unresolved_packet_kinds" to unresolvedPacketKinds,
            "unresolved_packet_events" to unresolvedPacketEvents,
            "unregistered_codec_packet_kinds" to codecGaps,
        )

        private fun socketJson(key: SocketKey, stats: SocketStats): String = Json.obj(
            "record" to "socket_summary",
            "phase" to key.phase,
            "dir" to key.direction,
            "fd" to key.fd,
            "count" to stats.count,
            "total_bytes" to stats.totalBytes,
            "min_size" to stats.minSize,
            "max_size" to stats.maxSize,
            "first_cycle" to stats.firstCycle,
            "last_cycle" to stats.lastCycle,
            "leading_bytes" to stats.leadingBytesSummary(),
        )

        private fun worldTrafficJson(worldTraffic: WorldTraffic): String = Json.obj(
            "record" to "world_traffic",
            "has_required_traffic" to worldTraffic.hasRequiredTraffic,
            "player_info_packets" to worldTraffic.playerInfoPackets,
            "npc_info_packets" to worldTraffic.npcInfoPackets,
            "zone_update_packets" to worldTraffic.zoneUpdatePackets,
            "anti_cheat_challenge_packets" to worldTraffic.antiCheatChallengePackets,
            "client_keepalive_packets" to worldTraffic.clientKeepalivePackets,
            "client_socket_events" to worldTraffic.clientSocketEvents,
            "client_socket_bytes" to worldTraffic.clientSocketBytes,
        )

        private fun worldTrafficSummary(
            packets: Map<PacketKey, PacketStats>,
            sockets: Map<SocketKey, SocketStats>,
        ): WorldTraffic {
            val playerInfo = packetCount(packets, "IN_GAME", "S", 22)
            val npcInfo = packetCount(packets, "IN_GAME", "S", 52)
            val zoneUpdates = packetCount(packets, "IN_GAME", "S", 76)
            val antiCheatChallenges = packetCount(packets, "IN_GAME", "S", 174)
            val keepalives = packetCount(packets, "IN_GAME", "C", 51)
            val clientSockets = sockets
                .filterKeys { it.phase == "IN_GAME" && it.direction == "C" }
                .values
            val clientSocketEvents = clientSockets.sumOf { it.count }
            val clientSocketBytes = clientSockets.sumOf { it.totalBytes }
            return WorldTraffic(
                playerInfoPackets = playerInfo,
                npcInfoPackets = npcInfo,
                zoneUpdatePackets = zoneUpdates,
                antiCheatChallengePackets = antiCheatChallenges,
                clientKeepalivePackets = keepalives,
                clientSocketEvents = clientSocketEvents,
                clientSocketBytes = clientSocketBytes,
            )
        }

        private fun packetCount(
            packets: Map<PacketKey, PacketStats>,
            phase: String,
            direction: String,
            opcode: Int,
        ): Long = packets.entries
            .filter { (key, _) -> key.phase == phase && key.direction == direction && key.opcode == opcode }
            .sumOf { (_, stats) -> stats.count }

        private fun observedLabel(key: PacketKey, codecInfo: CodecInfo): Pair<String, String> = when {
            !codecInfo.name.isUnknownLabel() -> codecInfo.name to "codec"
            !key.name.isUnknownLabel() -> key.name to "capture"
            else -> "" to "unknown"
        }

        private fun codecInfo(dir: String, opcode: Int): CodecInfo {
            val info = if (dir == "S") codec.serverProtInfo[opcode] else codec.clientProtInfo[opcode]
            if (info == null) return CodecInfo("UNKNOWN_$opcode", "", "unregistered")
            return CodecInfo(info.name, info.size.sizeKind(), "registered")
        }

        private fun packetComparator(): Comparator<Map.Entry<PacketKey, PacketStats>> =
            compareByDescending<Map.Entry<PacketKey, PacketStats>> { it.value.count }
                .thenBy { it.key.phase }
                .thenBy { it.key.direction }
                .thenBy { it.key.opcode }

        private fun socketComparator(): Comparator<Map.Entry<SocketKey, SocketStats>> =
            compareBy<Map.Entry<SocketKey, SocketStats>> { it.key.phase }
                .thenBy { it.key.direction }
                .thenBy { it.key.fd }
    }

    private data class Report(
        val lines: List<String>,
        val phases: List<String>,
        val packetEvents: Long,
        val socketEvents: Long,
        val codecGaps: Int,
        val unresolvedPacketKinds: Int,
        val hasFullLogin: Boolean,
        val hasWorldTraffic: Boolean,
    )

    private data class PacketKey(val phase: String, val direction: String, val opcode: Int, val name: String)
    private data class SocketKey(val phase: String, val direction: String, val fd: Int)
    private data class CodecInfo(val name: String, val sizeKind: String, val status: String)
    private data class WorldTraffic(
        val playerInfoPackets: Long,
        val npcInfoPackets: Long,
        val zoneUpdatePackets: Long,
        val antiCheatChallengePackets: Long,
        val clientKeepalivePackets: Long,
        val clientSocketEvents: Long,
        val clientSocketBytes: Long,
    ) {
        val hasRequiredTraffic: Boolean =
            playerInfoPackets > 0 &&
                npcInfoPackets > 0 &&
                zoneUpdatePackets > 0 &&
                antiCheatChallengePackets > 0 &&
                (clientKeepalivePackets > 0 || (clientSocketEvents > 0 && clientSocketBytes > 0))
    }

    private class PhaseStats(val name: String) {
        var events = 0L
        var ticks = 0L
        var packets = 0L
        var serverPackets = 0L
        var clientPackets = 0L
        var socketEvents = 0L
        var socketBytes = 0L
        var socketConnects = 0L
        var socketCloses = 0L
        var firstEpoch: Long? = null
        var lastEpoch: Long? = null
        var firstCycle: Int? = null
        var lastCycle: Int? = null

        fun accept(event: Event) {
            events++
            firstEpoch = minNullable(firstEpoch, event.epochMs)
            lastEpoch = maxNullable(lastEpoch, event.epochMs)
            firstCycle = minNullable(firstCycle, event.clientCycle)
            lastCycle = maxNullable(lastCycle, event.clientCycle)
            when (event.type) {
                "tick" -> ticks++
                "packet" -> {
                    packets++
                    if (event.direction == "S") serverPackets++ else if (event.direction == "C") clientPackets++
                }
                "socket" -> {
                    socketEvents++
                    socketBytes += (event.size ?: 0)
                }
                "socket_connect" -> socketConnects++
                "socket_close" -> socketCloses++
            }
        }

        fun durationMs(): Long? {
            val first = firstEpoch ?: return null
            val last = lastEpoch ?: return null
            return last - first
        }
    }

    private class PacketStats(val category: String?) {
        var count = 0L
        var minSize: Int? = null
        var maxSize: Int? = null
        var totalBytes = 0L
        var firstCycle: Int? = null
        var lastCycle: Int? = null

        fun accept(event: Event) {
            count++
            val size = event.size ?: 0
            minSize = minNullable(minSize, size)
            maxSize = maxNullable(maxSize, size)
            totalBytes += size
            firstCycle = minNullable(firstCycle, event.clientCycle)
            lastCycle = maxNullable(lastCycle, event.clientCycle)
        }
    }

    private class SocketStats {
        var count = 0L
        var minSize: Int? = null
        var maxSize: Int? = null
        var totalBytes = 0L
        var firstCycle: Int? = null
        var lastCycle: Int? = null
        private val leadingBytes = linkedMapOf<String, Long>()

        fun accept(event: Event) {
            count++
            val size = event.size ?: 0
            minSize = minNullable(minSize, size)
            maxSize = maxNullable(maxSize, size)
            totalBytes += size
            firstCycle = minNullable(firstCycle, event.clientCycle)
            lastCycle = maxNullable(lastCycle, event.clientCycle)
            val leading = event.payloadHex?.trim()?.split(' ')?.firstOrNull()?.uppercase() ?: ""
            if (leading.isNotEmpty()) leadingBytes[leading] = (leadingBytes[leading] ?: 0L) + 1L
        }

        fun leadingBytesSummary(): String = leadingBytes.entries
            .sortedByDescending { it.value }
            .take(12)
            .joinToString(",") { "0x${it.key}:${it.value}" }
    }

    private class Options(
        val eventsFile: File,
        val outFile: File?,
        val requireFullLogin: Boolean,
        val requireWorldTraffic: Boolean,
        val requireNoUnresolvedLabels: Boolean,
    ) {
        companion object {
            fun parse(args: Array<String>): Options {
                require(args.isNotEmpty()) {
                    "usage: UndercutLoginFlowImport <events.jsonl> [--out flow.jsonl] [--require-full-login] [--require-world-traffic] [--require-no-unresolved-labels]"
                }
                var out: File? = null
                var requireFullLogin = false
                var requireWorldTraffic = false
                var requireNoUnresolvedLabels = false
                var i = 1
                while (i < args.size) {
                    when (args[i]) {
                        "--out" -> out = File(args[++i])
                        "--require-full-login" -> requireFullLogin = true
                        "--require-world-traffic" -> requireWorldTraffic = true
                        "--require-no-unresolved-labels" -> requireNoUnresolvedLabels = true
                        else -> System.err.println("[undercut-flow] ignoring unknown arg: ${args[i]}")
                    }
                    i++
                }
                return Options(File(args[0]), out, requireFullLogin, requireWorldTraffic, requireNoUnresolvedLabels)
            }
        }
    }

    private fun ProtSize.sizeKind(): String = when (this) {
        is ProtSize.Fixed -> "fixed:$length"
        ProtSize.VarByte -> "varByte"
        ProtSize.VarShort -> "varShort"
    }

    private fun String.isUnknownLabel(): Boolean = startsWith("UNKNOWN_")

    private fun JsonObject.string(name: String): String? =
        (this[name] as? JsonPrimitive)?.contentOrNull

    private fun JsonObject.int(name: String): Int? =
        this[name]?.jsonPrimitive?.intOrNull

    private fun JsonObject.long(name: String): Long? =
        this[name]?.jsonPrimitive?.longOrNull

    private fun <T : Comparable<T>> minNullable(a: T?, b: T?): T? = when {
        a == null -> b
        b == null -> a
        else -> minOf(a, b)
    }

    private fun <T : Comparable<T>> maxNullable(a: T?, b: T?): T? = when {
        a == null -> b
        b == null -> a
        else -> maxOf(a, b)
    }
}
