package org.darkan.tools.recorder

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import org.darkan.core.EnvVars
import org.darkan.core.net.prot.revision.rev948.register948
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.Base64
import kotlin.system.exitProcess
import kotlinx.serialization.json.Json as KotlinJson

object UndercutSocketDeframe {
    @JvmStatic
    fun main(args: Array<String>) {
        val opts = UndercutSocketDeframeOptions.parse(args)
        val jsonEvents = opts.eventsFile.useLines { lines ->
            lines.filter { it.isNotBlank() }.mapIndexed { index, line -> parseEvent(index + 1, line) }.toList()
        }
        val pcapEvents = if (opts.usePcap && jsonEvents.none { it.type == "socket" }) {
            PcapSocketEventExtractor.extract(jsonEvents, opts)
        } else {
            emptyList()
        }
        val events = jsonEvents + pcapEvents
        if (pcapEvents.isNotEmpty()) {
            System.err.println("[undercut-deframe] pcap fallback: synthesized ${pcapEvents.size} socket event(s)")
        }
        val connections = UndercutConnectionAssembler.assemble(events, opts)
        val seedRecords = events.mapNotNull { it.seedRecord() }.sortedBy { it.epochMs ?: Long.MIN_VALUE }
        val fileSeeds = if (opts.seeds == null && seedRecords.isEmpty()) readIsaacKeysFile(opts.eventsFile.parentFile) else null
        val globalSeeds = opts.seeds ?: fileSeeds
        val codec = register948()

        if (globalSeeds == null && seedRecords.isEmpty()) {
            System.err.println(
                "[undercut-deframe] WARNING: no ISAAC seeds. Lobby/world streams will emit raw dumps only."
            )
            if (opts.requireSeeds) {
                System.err.println("[undercut-deframe] FAIL: --require-seeds set but no seeds were available")
                exitProcess(2)
            }
        } else {
            if (globalSeeds != null) {
                System.err.println("[undercut-deframe] global seeds (raw C2S): ${formatSeeds(globalSeeds)}")
            }
            if (seedRecords.isNotEmpty()) {
                System.err.println("[undercut-deframe] timestamped seed records: ${seedRecords.size}")
            }
        }

        val out = opts.outFile?.bufferedWriter()
        var packetCount = 0
        var desyncCount = 0
        var truncationCount = 0

        fun emit(line: String) {
            if (out != null) out.appendLine(line) else println(line)
        }

        for (conn in connections) {
            val connSeeds = globalSeeds ?: seedRecords.seedFor(conn)
            System.err.println(
                "[undercut-deframe] connection epoch=${conn.epoch} fd=${conn.fd} role=${conn.role} " +
                    "peer=${conn.peer} c2s=${conn.c2s.size}B s2c=${conn.s2c.size}B"
            )
            emit(connHeaderJson(conn, connSeeds))

            when (conn.role) {
                Role.JS5 -> {
                    emitJs5(conn, ::emit).also { packetCount += it }
                }
                Role.LOBBY, Role.WORLD -> {
                    if (connSeeds == null) {
                        emit(rawDumpJson(conn, "no-seeds"))
                        continue
                    }
                    val result = IsaacDeframer(codec, connSeeds, opts.isaacOffset).deframe(conn, ::emit)
                    packetCount += result.packets
                    desyncCount += result.desyncs
                    truncationCount += result.truncations
                }
                Role.UNKNOWN -> {
                    if (connSeeds != null) {
                        val result = IsaacDeframer(codec, connSeeds, opts.isaacOffset).deframe(conn, ::emit)
                        packetCount += result.packets
                        desyncCount += result.desyncs
                        truncationCount += result.truncations
                    } else {
                        emit(rawDumpJson(conn, "unknown-role-no-seeds"))
                    }
                }
            }
        }

        out?.flush()
        out?.close()

        System.err.println(
            "[undercut-deframe] DONE: ${connections.size} connections, $packetCount packets, " +
                "$desyncCount desync(s), $truncationCount truncation(s)"
        )
        if (opts.outFile != null) System.err.println("[undercut-deframe] transcript: ${opts.outFile.absolutePath}")
        if (opts.failOnDesync && desyncCount > 0) {
            System.err.println("[undercut-deframe] FAIL: --fail-on-desync set and transcript has $desyncCount desync(s)")
            exitProcess(3)
        }
        if (opts.failOnTruncation && truncationCount > 0) {
            System.err.println(
                "[undercut-deframe] FAIL: --fail-on-truncation set and transcript has $truncationCount truncation(s)"
            )
            exitProcess(4)
        }
    }

    private val parser = KotlinJson { ignoreUnknownKeys = true }

    private fun parseEvent(lineNumber: Int, line: String): UndercutSocketEvent {
        val obj = try {
            parser.parseToJsonElement(line).jsonObject
        } catch (e: Exception) {
            error("invalid JSON at line $lineNumber: ${e.message}")
        }
        return UndercutSocketEvent(
            lineNumber = lineNumber,
            type = obj.string("type") ?: "unknown",
            epochMs = obj.long("epoch_ms"),
            clientCycle = obj.int("client_cycle"),
            stateName = obj.string("main_state_name") ?: obj.string("state_name"),
            direction = obj.string("direction"),
            fd = obj.int("fd"),
            size = obj.int("size"),
            capturedSize = obj.int("captured_size"),
            truncated = obj.boolean("truncated") ?: false,
            payloadBase64 = obj.string("payload_base64"),
            payloadHex = obj.string("payload_hex"),
            syscall = obj.string("syscall"),
            address = obj.string("address"),
            port = obj.int("port"),
            result = obj.int("result"),
            seedSequence = obj.int("sequence"),
            seedSource = obj.string("source"),
            seedsRawC2s = obj.string("seeds_raw_c2s")?.let(::parseSeeds),
            seedFields = parseSeedFields(obj),
        )
    }

    private fun emitJs5(conn: Connection, emit: (String) -> Unit): Int {
        if (conn.c2s.isNotEmpty()) {
            emit(
                Json.obj(
                    "record" to "raw_stream",
                    "dir" to "C2S",
                    "fd" to conn.fd,
                    "conn" to "js5",
                    "kind" to "js5_raw",
                    "len" to conn.c2s.size,
                    "payload_hex" to Json.hex(conn.c2s, 512)
                )
            )
        }
        if (conn.s2c.isNotEmpty()) {
            emit(
                Json.obj(
                    "record" to "raw_stream",
                    "dir" to "S2C",
                    "fd" to conn.fd,
                    "conn" to "js5",
                    "kind" to "js5_raw",
                    "len" to conn.s2c.size,
                    "payload_hex" to Json.hex(conn.s2c, 512)
                )
            )
        }
        return if (conn.c2s.isNotEmpty() || conn.s2c.isNotEmpty()) 1 else 0
    }

    private fun connHeaderJson(conn: Connection, seeds: IntArray?): String = Json.obj(
        "record" to "connection",
        "source" to "undercut-session",
        "epoch" to conn.epoch,
        "fd" to conn.fd,
        "role" to conn.role.name.lowercase(),
        "peer" to (conn.peer ?: "?"),
        "port" to conn.port,
        "c2s_bytes" to conn.c2s.size,
        "s2c_bytes" to conn.s2c.size,
        "seeds_raw_c2s" to (seeds?.let { formatSeeds(it) } ?: ""),
        "isaac_delta" to EnvVars.ISAAC_DELTA
    )

    private fun rawDumpJson(conn: Connection, reason: String): String = Json.obj(
        "record" to "raw_dump",
        "source" to "undercut-session",
        "fd" to conn.fd,
        "conn" to conn.role.name.lowercase(),
        "reason" to reason,
        "c2s_len" to conn.c2s.size,
        "s2c_len" to conn.s2c.size,
        "c2s_hex" to Json.hex(conn.c2s, 512),
        "s2c_hex" to Json.hex(conn.s2c, 512)
    )

    private fun formatSeeds(seeds: IntArray): String = seeds.joinToString(",") { "0x%08x".format(it) }

    private data class SeedRecord(
        val epochMs: Long?,
        val lineNumber: Int,
        val sequence: Int?,
        val source: String?,
        val seeds: IntArray,
    )

    private fun UndercutSocketEvent.seedRecord(): SeedRecord? {
        if (type != "isaac_seeds") return null
        val seeds = seedsRawC2s ?: seedFields ?: return null
        return SeedRecord(epochMs, lineNumber, seedSequence, seedSource, seeds)
    }

    private fun List<SeedRecord>.seedFor(conn: Connection): IntArray? {
        if (isEmpty()) return null
        if (size == 1) return single().seeds

        val start = conn.startEpochMs
        val end = conn.endEpochMs
        if (start != null && end != null) {
            val inWindow = filter { seed ->
                val ts = seed.epochMs ?: return@filter false
                ts in (start - 5_000L)..(end + 5_000L)
            }
            selectForRole(conn.role, inWindow)?.let { return it.seeds }
        }

        if (end != null) {
            val beforeEnd = filter { (it.epochMs ?: Long.MIN_VALUE) <= end + 5_000L }
            selectForRole(conn.role, beforeEnd)?.let { return it.seeds }
        }
        return selectForRole(conn.role, this)?.seeds ?: last().seeds
    }

    private fun selectForRole(role: Role, seeds: List<SeedRecord>): SeedRecord? {
        if (seeds.isEmpty()) return null
        return when (role) {
            Role.LOBBY -> seeds.first()
            Role.WORLD -> seeds.last()
            Role.JS5, Role.UNKNOWN -> seeds.last()
        }
    }

    private fun parseSeedFields(obj: JsonObject): IntArray? {
        val values = IntArray(4)
        for (i in 0..3) {
            values[i] = obj.int("seed$i") ?: return null
        }
        return values
    }

    private fun parseSeeds(s: String): IntArray {
        val matches = Regex("""0x[0-9a-fA-F]+|-?\d+""").findAll(s).map { it.value }.toList()
        require(matches.size >= 4) { "seed string needs at least 4 ints: $s" }
        return matches.take(4).map { part ->
            if (part.startsWith("0x") || part.startsWith("0X")) part.substring(2).toLong(16).toInt()
            else part.toLong().toInt()
        }.toIntArray()
    }

    private fun readIsaacKeysFile(dir: File?): IntArray? {
        val file = dir?.resolve("isaac-keys.txt")?.takeIf { it.isFile } ?: return null
        val line = file.readLines().firstOrNull { "hex" in it.lowercase() }
            ?: file.readLines().firstOrNull()
            ?: return null
        return runCatching { parseSeeds(line) }.getOrNull()
    }

    private fun JsonObject.string(name: String): String? =
        (this[name] as? JsonPrimitive)?.contentOrNull

    private fun JsonObject.int(name: String): Int? =
        this[name]?.jsonPrimitive?.intOrNull

    private fun JsonObject.long(name: String): Long? =
        this[name]?.jsonPrimitive?.longOrNull

    private fun JsonObject.boolean(name: String): Boolean? =
        this[name]?.jsonPrimitive?.contentOrNull?.toBooleanStrictOrNull()
}

private data class UndercutSocketEvent(
    val lineNumber: Int,
    val type: String,
    val epochMs: Long?,
    val clientCycle: Int?,
    val stateName: String?,
    val direction: String?,
    val fd: Int?,
    val size: Int?,
    val capturedSize: Int?,
    val truncated: Boolean,
    val payloadBase64: String?,
    val payloadHex: String?,
    val syscall: String?,
    val address: String?,
    val port: Int?,
    val result: Int?,
    val seedSequence: Int?,
    val seedSource: String?,
    val seedsRawC2s: IntArray?,
    val seedFields: IntArray?,
) {
    fun payloadBytes(): ByteArray {
        payloadBase64?.let { return Base64.getDecoder().decode(it) }
        val hex = payloadHex?.substringBefore("...+") ?: return ByteArray(0)
        val cleaned = hex.filter { it.isDigit() || it.lowercaseChar() in 'a'..'f' }
        require(cleaned.length % 2 == 0) { "payload_hex has odd nibble count at line $lineNumber" }
        return ByteArray(cleaned.length / 2) { i ->
            cleaned.substring(i * 2, i * 2 + 2).toInt(16).toByte()
        }
    }
}

private object UndercutConnectionAssembler {
    private const val LOGIN_SCREEN = "LOGIN_SCREEN"
    private const val LOBBY_SCREEN = "LOBBY_SCREEN"
    private const val LOGGED_IN = "LOGGED_IN"

    private class Acc(
        val epoch: Int,
        var peer: String?,
        var port: Int,
        var role: Role,
        var startEpochMs: Long?,
        var endEpochMs: Long?,
        val c2s: ByteArrayOutputStream = ByteArrayOutputStream(),
        val s2c: ByteArrayOutputStream = ByteArrayOutputStream(),
    )

    fun assemble(events: List<UndercutSocketEvent>, opts: UndercutSocketDeframeOptions): List<Connection> {
        val loginFd = events.firstOrNull {
            it.type == "socket" && it.stateName == LOGIN_SCREEN && it.fd != null
        }?.fd
        val handoffStartCycle = events.firstOrNull {
            it.type == "socket" &&
                it.stateName == LOBBY_SCREEN &&
                it.fd != null &&
                loginFd != null &&
                it.fd != loginFd
        }?.clientCycle
        val epochOf = HashMap<Int, Int>()
        val current = HashMap<Int, Acc>()
        val finished = ArrayList<Connection>()

        fun finalize(fd: Int) {
            val acc = current.remove(fd) ?: return
            finished.add(
                Connection(
                    acc.epoch,
                    fd,
                    acc.peer,
                    acc.port,
                    acc.role,
                    acc.c2s.toByteArray(),
                    acc.s2c.toByteArray(),
                    acc.startEpochMs,
                    acc.endEpochMs,
                )
            )
        }

        fun currentOrSynthetic(fd: Int): Acc = current.getOrPut(fd) {
            val epoch = (epochOf[fd] ?: -1) + 1
            epochOf[fd] = epoch
            Acc(epoch, null, -1, Role.UNKNOWN, null, null)
        }

        for (event in events) {
            val fd = event.fd ?: continue
            when (event.type) {
                "socket_connect" -> {
                    finalize(fd)
                    val epoch = (epochOf[fd] ?: -1) + 1
                    epochOf[fd] = epoch
                    val port = event.port ?: -1
                    val role = opts.roleForPort(port)
                    current[fd] = Acc(
                        epoch = epoch,
                        peer = event.address?.let { "$it:$port" },
                        port = port,
                        role = role,
                        startEpochMs = event.epochMs,
                        endEpochMs = event.epochMs,
                    )
                }
                "socket" -> {
                    val acc = currentOrSynthetic(fd)
                    if (acc.startEpochMs == null) acc.startEpochMs = event.epochMs
                    acc.endEpochMs = event.epochMs ?: acc.endEpochMs
                    if (acc.role == Role.UNKNOWN) {
                        acc.role = inferRole(event, loginFd, handoffStartCycle, opts)
                    }
                    if (acc.peer == null && event.address != null) acc.peer = "${event.address}:${event.port ?: -1}"
                    if (event.port != null && acc.port == -1) acc.port = event.port

                    val payload = event.payloadBytes()
                    when (event.direction) {
                        "S" -> acc.s2c.write(payload)
                        "C" -> acc.c2s.write(payload)
                    }
                }
                "socket_close" -> {
                    current[fd]?.endEpochMs = event.epochMs ?: current[fd]?.endEpochMs
                    finalize(fd)
                }
            }
        }
        current.keys.toList().forEach { finalize(it) }
        return finished
            .filter { it.c2s.isNotEmpty() || it.s2c.isNotEmpty() }
            .sortedWith(compareBy({ it.epoch }, { it.fd }))
    }

    private fun inferRole(
        event: UndercutSocketEvent,
        loginFd: Int?,
        handoffStartCycle: Int?,
        opts: UndercutSocketDeframeOptions,
    ): Role {
        event.port?.let { port ->
            val portRole = opts.roleForPort(port)
            if (portRole != Role.UNKNOWN) return portRole
        }
        return when (event.stateName) {
            LOGIN_SCREEN -> Role.LOBBY
            LOBBY_SCREEN -> {
                if (loginFd != null && event.fd != loginFd) {
                    Role.WORLD
                } else if (handoffStartCycle != null && event.clientCycle != null && event.clientCycle >= handoffStartCycle) {
                    Role.WORLD
                } else {
                    Role.LOBBY
                }
            }
            LOGGED_IN -> Role.WORLD
            else -> Role.UNKNOWN
        }
    }
}

private object PcapSocketEventExtractor {
    private const val SYNTHETIC_LINE_BASE = 1_000_000
    private val base64 = Base64.getEncoder()

    private data class Key(
        val localAddress: String,
        val localPort: Int,
        val remoteAddress: String,
        val remotePort: Int,
    )

    private data class Packet(
        val key: Key,
        val epochMs: Long,
        val direction: String,
        val seq: Long,
        val payload: ByteArray,
    )

    fun extract(jsonEvents: List<UndercutSocketEvent>, opts: UndercutSocketDeframeOptions): List<UndercutSocketEvent> {
        val pcap = opts.pcapFile?.takeIf { it.isFile } ?: return emptyList()
        val packets = readPackets(pcap, opts)
        if (packets.isEmpty()) return emptyList()

        val timeline = jsonEvents
            .filter { it.type == "main_state" && it.epochMs != null && it.stateName != null }
            .sortedBy { it.epochMs }

        fun stateAt(epochMs: Long): String? {
            val previous = timeline.lastOrNull { (it.epochMs ?: Long.MIN_VALUE) <= epochMs }
            return previous?.stateName ?: timeline.firstOrNull()?.stateName
        }

        val grouped = linkedMapOf<Key, MutableList<Packet>>()
        for (packet in packets) {
            grouped.getOrPut(packet.key) { ArrayList() }.add(packet)
        }

        val events = ArrayList<UndercutSocketEvent>()
        var syntheticLine = SYNTHETIC_LINE_BASE
        for ((key, rawConnPackets) in grouped) {
            val connPackets = reassembleConnection(rawConnPackets)
            if (!keepConnection(key, connPackets, opts)) continue
            val first = connPackets.first()
            val last = connPackets.last()
            events.add(
                pcapEvent(
                    lineNumber = syntheticLine++,
                    type = "socket_connect",
                    epochMs = first.epochMs,
                    stateName = stateAt(first.epochMs),
                    fd = key.localPort,
                    direction = null,
                    payload = null,
                    address = key.remoteAddress,
                    port = key.remotePort,
                    result = 0,
                )
            )
            for (packet in connPackets) {
                events.add(
                    pcapEvent(
                        lineNumber = syntheticLine++,
                        type = "socket",
                        epochMs = packet.epochMs,
                        stateName = stateAt(packet.epochMs),
                        fd = key.localPort,
                        direction = packet.direction,
                        payload = packet.payload,
                        address = key.remoteAddress,
                        port = key.remotePort,
                        result = null,
                    )
                )
            }
            events.add(
                pcapEvent(
                    lineNumber = syntheticLine++,
                    type = "socket_close",
                    epochMs = last.epochMs,
                    stateName = stateAt(last.epochMs),
                    fd = key.localPort,
                    direction = null,
                    payload = null,
                    address = key.remoteAddress,
                    port = key.remotePort,
                    result = null,
                )
            )
        }
        return events
    }

    private fun keepConnection(
        key: Key,
        packets: List<Packet>,
        opts: UndercutSocketDeframeOptions,
    ): Boolean {
        if (opts.pcapHosts.isNotEmpty() && key.remoteAddress !in opts.pcapHosts) return false
        if (packets.sumOf { it.payload.size } == 0) return false
        if (opts.pcapHosts.isNotEmpty()) return true

        val firstClientPayload = packets.firstOrNull { it.direction == "C" }?.payload ?: packets.first().payload
        return !looksTlsRecord(firstClientPayload)
    }

    private fun readPackets(pcap: File, opts: UndercutSocketDeframeOptions): List<Packet> {
        val command = listOf(
            opts.tsharkPath,
            "-r",
            pcap.absolutePath,
            "-Y",
            "tcp.len > 0 && ip",
            "-T",
            "fields",
            "-E",
            "separator=|",
            "-e",
            "frame.time_epoch",
            "-e",
            "ip.src",
            "-e",
            "tcp.srcport",
            "-e",
            "ip.dst",
            "-e",
            "tcp.dstport",
            "-e",
            "tcp.seq_raw",
            "-e",
            "tcp.payload",
        )
        val process = ProcessBuilder(command)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start()
        val packets = process.inputStream.bufferedReader().useLines { lines ->
            lines.mapNotNull { parseTsharkRow(it, opts) }.toList()
        }
        val exit = process.waitFor()
        require(exit == 0) { "tshark failed with exit code $exit while reading ${pcap.absolutePath}" }
        return packets
    }

    private fun reassembleConnection(packets: List<Packet>): List<Packet> {
        val client = reassembleDirection(packets.filter { it.direction == "C" })
        val server = reassembleDirection(packets.filter { it.direction == "S" })
        return client + server
    }

    private fun reassembleDirection(packets: List<Packet>): List<Packet> {
        val result = ArrayList<Packet>()
        var nextSeq: Long? = null
        for (packet in packets.sortedWith(compareBy({ it.seq }, { it.epochMs }))) {
            val expected = nextSeq
            val payloadStart = packet.seq
            val payloadEnd = payloadStart + packet.payload.size
            if (expected == null || payloadEnd > expected) {
                val overlap = if (expected == null) 0 else (expected - payloadStart).coerceAtLeast(0).toInt()
                val payload = if (overlap == 0) packet.payload else packet.payload.copyOfRange(overlap, packet.payload.size)
                if (payload.isNotEmpty()) {
                    result.add(packet.copy(payload = payload, seq = payloadStart + overlap))
                }
                nextSeq = payloadEnd
            }
        }
        return result
    }

    private fun parseTsharkRow(line: String, opts: UndercutSocketDeframeOptions): Packet? {
        val fields = line.split('|')
        if (fields.size < 7) return null
        val epochMs = ((fields[0].toDoubleOrNull() ?: return null) * 1000.0).toLong()
        val src = fields[1]
        val srcPort = fields[2].toIntOrNull() ?: return null
        val dst = fields[3]
        val dstPort = fields[4].toIntOrNull() ?: return null
        val seq = fields[5].toLongOrNull() ?: return null
        val payload = decodeHex(fields[6]).takeIf { it.isNotEmpty() } ?: return null

        val srcIsLocal = srcPort >= 49152 && dstPort in opts.pcapPorts
        val dstIsLocal = dstPort >= 49152 && srcPort in opts.pcapPorts
        return when {
            srcIsLocal -> Packet(Key(src, srcPort, dst, dstPort), epochMs, "C", seq, payload)
            dstIsLocal -> Packet(Key(dst, dstPort, src, srcPort), epochMs, "S", seq, payload)
            else -> null
        }
    }

    private fun pcapEvent(
        lineNumber: Int,
        type: String,
        epochMs: Long,
        stateName: String?,
        fd: Int,
        direction: String?,
        payload: ByteArray?,
        address: String,
        port: Int,
        result: Int?,
    ): UndercutSocketEvent = UndercutSocketEvent(
        lineNumber = lineNumber,
        type = type,
        epochMs = epochMs,
        clientCycle = null,
        stateName = stateName,
        direction = direction,
        fd = fd,
        size = payload?.size,
        capturedSize = payload?.size,
        truncated = false,
        payloadBase64 = payload?.let { base64.encodeToString(it) },
        payloadHex = null,
        syscall = "pcap",
        address = address,
        port = port,
        result = result,
        seedSequence = null,
        seedSource = null,
        seedsRawC2s = null,
        seedFields = null,
    )

    private fun looksTlsRecord(payload: ByteArray): Boolean {
        if (payload.size < 3) return false
        val contentType = payload[0].toInt() and 0xff
        val major = payload[1].toInt() and 0xff
        val minor = payload[2].toInt() and 0xff
        return contentType in 0x14..0x17 && major == 0x03 && minor in 0x00..0x04
    }

    private fun decodeHex(hex: String): ByteArray {
        val cleaned = hex.filter { it.isDigit() || it.lowercaseChar() in 'a'..'f' }
        if (cleaned.length < 2 || cleaned.length % 2 != 0) return ByteArray(0)
        return ByteArray(cleaned.length / 2) { i ->
            cleaned.substring(i * 2, i * 2 + 2).toInt(16).toByte()
        }
    }
}

private data class UndercutSocketDeframeOptions(
    val eventsFile: File,
    val outFile: File?,
    val seeds: IntArray?,
    val lobbyPort: Int,
    val worldPort: Int,
    val js5Port: Int,
    val isaacOffset: Int,
    val requireSeeds: Boolean,
    val failOnDesync: Boolean,
    val failOnTruncation: Boolean,
    val usePcap: Boolean,
    val pcapFile: File?,
    val pcapHosts: Set<String>,
    val pcapPorts: Set<Int>,
    val tsharkPath: String,
) {
    fun roleForPort(port: Int): Role = when (port) {
        js5Port -> Role.JS5
        lobbyPort -> Role.LOBBY
        worldPort -> Role.WORLD
        else -> Role.UNKNOWN
    }

    companion object {
        fun parse(args: Array<String>): UndercutSocketDeframeOptions {
            require(args.isNotEmpty()) {
                "usage: UndercutSocketDeframe <events.jsonl> [--out f.jsonl] [--seeds s0,s1,s2,s3] " +
                    "[--lobby-port 43596] [--world-port 43597] [--js5-port 8829] [--isaac-offset N|auto] " +
                    "[--pcap network.pcapng] [--pcap-hosts ip,ip] [--pcap-ports 443,43594] [--no-pcap] " +
                    "[--require-seeds] [--fail-on-desync] [--fail-on-truncation] [--strict]"
            }
            val eventsFile = File(args[0])
            var out: File? = null
            var seeds: IntArray? = null
            var lobby = 43596
            var world = 43597
            var js5 = 8829
            var isaacOffset = -1
            var requireSeeds = false
            var failOnDesync = false
            var failOnTruncation = false
            var usePcap = true
            var pcapFile: File? = null
            var pcapHosts = emptySet<String>()
            var pcapPorts = setOf(443, 43594, 43595, 43596, 43597, 43598, 43599)
            var tsharkPath = "/Applications/Wireshark.app/Contents/MacOS/tshark"
            var i = 1
            while (i < args.size) {
                when (args[i]) {
                    "--out" -> out = File(args[++i])
                    "--seeds" -> seeds = parseSeeds(args[++i])
                    "--lobby-port" -> lobby = args[++i].toInt()
                    "--world-port" -> world = args[++i].toInt()
                    "--js5-port" -> js5 = args[++i].toInt()
                    "--isaac-offset" -> isaacOffset = if (args[i + 1] == "auto") { i++; -1 } else args[++i].toInt()
                    "--pcap" -> pcapFile = File(args[++i])
                    "--pcap-hosts" -> pcapHosts = parseCsv(args[++i]).toSet()
                    "--pcap-ports" -> pcapPorts = parseCsv(args[++i]).map { it.toInt() }.toSet()
                    "--tshark" -> tsharkPath = args[++i]
                    "--no-pcap" -> usePcap = false
                    "--require-seeds" -> requireSeeds = true
                    "--fail-on-desync" -> failOnDesync = true
                    "--fail-on-truncation" -> failOnTruncation = true
                    "--strict" -> {
                        requireSeeds = true
                        failOnDesync = true
                        failOnTruncation = true
                    }
                    else -> System.err.println("[undercut-deframe] ignoring unknown arg: ${args[i]}")
                }
                i++
            }
            val autoPcap = eventsFile.parentFile?.resolve("network.pcapng")?.takeIf { it.isFile }
            return UndercutSocketDeframeOptions(
                eventsFile = eventsFile,
                outFile = out,
                seeds = seeds,
                lobbyPort = lobby,
                worldPort = world,
                js5Port = js5,
                isaacOffset = isaacOffset,
                requireSeeds = requireSeeds,
                failOnDesync = failOnDesync,
                failOnTruncation = failOnTruncation,
                usePcap = usePcap,
                pcapFile = pcapFile ?: autoPcap,
                pcapHosts = pcapHosts,
                pcapPorts = pcapPorts,
                tsharkPath = tsharkPath,
            )
        }

        private fun parseCsv(s: String): List<String> =
            s.split(",").map { it.trim() }.filter { it.isNotEmpty() }

        private fun parseSeeds(s: String): IntArray {
            val parts = s.split(",").map { it.trim() }
            require(parts.size == 4) { "--seeds needs exactly 4 comma-separated ints, got ${parts.size}" }
            return parts.map { part ->
                if (part.startsWith("0x") || part.startsWith("0X")) part.substring(2).toLong(16).toInt()
                else part.toLong().toInt()
            }.toIntArray()
        }
    }
}
