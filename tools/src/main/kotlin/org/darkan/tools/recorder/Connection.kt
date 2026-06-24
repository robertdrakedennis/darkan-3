package org.darkan.tools.recorder

import java.io.ByteArrayOutputStream
import java.io.File

/** Role of a connection, inferred from its peer port. */
enum class Role { JS5, LOBBY, WORLD, UNKNOWN }

/**
 * One assembled TCP connection: a `(epoch, fd)` pair with its two reassembled
 * directional byte streams (concatenated in capture order) and a role inferred
 * from the connect() peer port.
 */
data class Connection(
    val epoch: Int,
    val fd: Int,
    val peer: String?,
    val port: Int,
    val role: Role,
    val c2s: ByteArray, // outbound (client → server)
    val s2c: ByteArray, // inbound (server → client)
    val startEpochMs: Long? = null,
    val endEpochMs: Long? = null,
)

/** CLI options for the deframer. */
class Options(
    val captureFile: File,
    val outFile: File?,
    val seeds: IntArray?,
    val lobbyPort: Int,
    val worldPort: Int,
    val js5Port: Int,
    /** ISAAC engagement offset within the post-login S2C stream: -1 = auto. */
    val isaacOffset: Int,
    val requireSeeds: Boolean,
    val failOnDesync: Boolean,
    val failOnTruncation: Boolean,
) {
    fun roleForPort(port: Int): Role = when (port) {
        js5Port -> Role.JS5
        lobbyPort -> Role.LOBBY
        worldPort -> Role.WORLD
        else -> Role.UNKNOWN
    }

    companion object {
        fun parse(args: Array<String>): Options {
            require(args.isNotEmpty()) {
                "usage: RecorderDeframe <capture.bin> [--out f.jsonl] [--seeds s0,s1,s2,s3] " +
                    "[--lobby-port 43596] [--world-port 43597] [--js5-port 8829] [--isaac-offset N|auto] " +
                    "[--require-seeds] [--fail-on-desync] [--fail-on-truncation] [--strict]"
            }
            val capture = File(args[0])
            var out: File? = null
            var seeds: IntArray? = null
            var lobby = 43596
            var world = 43597
            var js5 = 8829
            var isaacOffset = -1
            var requireSeeds = false
            var failOnDesync = false
            var failOnTruncation = false
            var i = 1
            while (i < args.size) {
                when (args[i]) {
                    "--out" -> out = File(args[++i])
                    "--seeds" -> seeds = parseSeeds(args[++i])
                    "--lobby-port" -> lobby = args[++i].toInt()
                    "--world-port" -> world = args[++i].toInt()
                    "--js5-port" -> js5 = args[++i].toInt()
                    "--isaac-offset" -> isaacOffset = if (args[i + 1] == "auto") { i++; -1 } else args[++i].toInt()
                    "--require-seeds" -> requireSeeds = true
                    "--fail-on-desync" -> failOnDesync = true
                    "--fail-on-truncation" -> failOnTruncation = true
                    "--strict" -> {
                        requireSeeds = true
                        failOnDesync = true
                        failOnTruncation = true
                    }
                    else -> System.err.println("[deframe] ignoring unknown arg: ${args[i]}")
                }
                i++
            }
            return Options(capture, out, seeds, lobby, world, js5, isaacOffset, requireSeeds, failOnDesync, failOnTruncation)
        }

        /** Parse "s0,s1,s2,s3" — each int may be decimal or 0x-hex. */
        private fun parseSeeds(s: String): IntArray {
            val parts = s.split(",").map { it.trim() }
            require(parts.size == 4) { "--seeds needs exactly 4 comma-separated ints, got ${parts.size}" }
            return parts.map { p ->
                if (p.startsWith("0x") || p.startsWith("0X")) p.substring(2).toLong(16).toInt()
                else p.toLong().toInt()
            }.toIntArray()
        }
    }
}

/**
 * Reassembles the capture's flat record list into [Connection]s.
 *
 * Algorithm: walk records in order. A `connect` opens (or re-opens) a logical
 * connection for that fd — incrementing the fd's epoch (so fd reuse across the
 * lobby→world hop yields distinct connections). `io` records append to the
 * current epoch's C2S/S2C accumulator for that fd. `close` finalizes nothing
 * special (we keep the bytes; the epoch just stops receiving until the next
 * connect on that fd). IO records on an fd with no prior connect still get a
 * synthetic connection (epoch 0, UNKNOWN role) so nothing is lost.
 */
object ConnectionAssembler {
    private class Acc(
        val epoch: Int,
        var peer: String?,
        var port: Int,
        var role: Role,
        val c2s: ByteArrayOutputStream = ByteArrayOutputStream(),
        val s2c: ByteArrayOutputStream = ByteArrayOutputStream(),
    )

    fun assemble(capture: Capture, opts: Options): List<Connection> {
        val epochOf = HashMap<Int, Int>()       // fd -> current epoch
        val current = HashMap<Int, Acc>()        // fd -> current accumulator
        val finished = ArrayList<Connection>()

        fun finalize(fd: Int) {
            val acc = current.remove(fd) ?: return
            finished.add(
                Connection(
                    acc.epoch, fd, acc.peer, acc.port, acc.role,
                    acc.c2s.toByteArray(), acc.s2c.toByteArray()
                )
            )
        }

        for (rec in capture.records) {
            when (rec) {
                is CaptureRecord.Connect -> {
                    finalize(rec.fd) // close out any prior epoch on this fd
                    val epoch = (epochOf[rec.fd] ?: -1) + 1
                    epochOf[rec.fd] = epoch
                    current[rec.fd] = Acc(
                        epoch = epoch,
                        peer = "${rec.addrString()}:${rec.port}",
                        port = rec.port,
                        role = opts.roleForPort(rec.port),
                    )
                }
                is CaptureRecord.Io -> {
                    val acc = current.getOrPut(rec.fd) {
                        // IO before any connect on this fd — synthetic connection.
                        val epoch = (epochOf[rec.fd] ?: -1) + 1
                        epochOf[rec.fd] = epoch
                        Acc(epoch, null, -1, Role.UNKNOWN)
                    }
                    when (rec.dir) {
                        Dir.IN -> acc.s2c.write(rec.bytes)
                        Dir.OUT -> acc.c2s.write(rec.bytes)
                    }
                }
                is CaptureRecord.Close -> finalize(rec.fd)
                else -> { /* Seeds / Note handled by the caller */ }
            }
        }
        // Finalize any still-open connections (client exited without close).
        current.keys.toList().forEach { finalize(it) }

        // Emit in (epoch, fd) order for stable, readable transcripts: JS5/lobby
        // (epoch 0) before world (later epochs).
        return finished
            .filter { it.c2s.isNotEmpty() || it.s2c.isNotEmpty() }
            .sortedWith(compareBy({ it.epoch }, { it.fd }))
    }
}
