package org.darkan.tools.recorder

import org.darkan.core.EnvVars
import org.darkan.core.net.Isaac
import org.darkan.core.net.prot.Codec
import org.darkan.core.net.prot.revision.rev948.register948
import java.io.File
import kotlin.system.exitProcess

/**
 * Offline deframer for `libdarkan_recorder.dylib` captures.
 *
 * Reads the binary capture (raw per-fd TCP streams + connect/close lifecycle +
 * the ISAAC seeds), reconstructs each connection's two directional streams,
 * walks the documented login handshake, then ISAAC-deframes the post-login game
 * stream into individual packets and emits an annotated JSONL transcript.
 *
 * The deframe logic is ported VERBATIM from the proven [org.darkan.tools.loginproxy.LoginProxy]
 * /[org.darkan.tools.DecodeCapture] state machine: connection-type byte → 9-byte
 * first response → 1B result → 1B data-len → N data → POST_LOGIN ISAAC. Opcode
 * de-obfuscation is `(rawByte - cipher.nextInt()) & 0xFF` with the 1-or-2-byte
 * rule (decoded ≥ 128 → second byte, `(d0-128)*256 + d1`), both bytes consuming
 * an ISAAC value, with partial-read pending state so a packet split across
 * recv() boundaries never double-consumes an ISAAC value. Sizes/names come from
 * [register948] (`fixed=N`, `varByte=-1`, `varShort=-2`).
 *
 * The bytes are AUTHORITATIVE: where our 948 size table disagrees with what
 * cleanly frames the stream, that disagreement is emitted as a `desync` line —
 * a finding, not a deframer bug.
 *
 * ## fd classification (the one genuinely-new piece vs the single-pipe proxy)
 *
 * JS5, lobby, and world live on different sockets. We key all state on
 * `(epoch, fd)` where `epoch` increments per `connect` on a given fd (so fd
 * reuse across the lobby→world hop is disambiguated). Each connection is tagged
 * by peer port from its `connect` record:
 *   - JS5 is NOT ISAAC-obfuscated → routed to a raw/non-ISAAC framer.
 *   - lobby/world ARE → routed to the ISAAC login+post-login framer.
 *
 * Usage (Gradle):
 *   ./gradlew :tools:recorderDeframe -PdeframeArgs="<capture.bin> [--out transcript.jsonl] \
 *       [--seeds s0,s1,s2,s3] [--lobby-port 43596] [--world-port 43597] [--js5-port 8829] \
 *       [--isaac-offset N|auto] [--strict]"
 */
object RecorderDeframe {

    private const val SERVER_PROT_COUNT = 218
    private const val CLIENT_PROT_COUNT = 130

    @JvmStatic
    fun main(args: Array<String>) {
        val opts = Options.parse(args)
        val capture = CaptureReader.read(opts.captureFile).dedupeIo()

        System.err.println("[deframe] capture pid=${capture.pid} version=${capture.version} records=${capture.records.size}")
        capture.notes().forEach { System.err.println("[deframe] note: $it") }

        // Seeds: explicit CLI override (server-logged seeds, the interpose-only
        // validation path) > captured Seeds record (the inline-hook path).
        val rawSeeds = opts.seeds ?: capture.seeds()
        if (rawSeeds == null) {
            System.err.println(
                "[deframe] WARNING: no ISAAC seeds (no Seeds record in capture and no --seeds). " +
                    "Game streams cannot be opcode-decoded; only raw framing + JS5 will be emitted."
            )
        } else {
            System.err.println("[deframe] seeds (raw C2S): ${rawSeeds.joinToString(", ") { "0x%08x".format(it) }}")
        }

        val codec = register948()
        val connections = ConnectionAssembler.assemble(capture, opts)
        if (opts.requireSeeds && rawSeeds == null && connections.none { LoginSeedExtractor.extract(it) != null }) {
            System.err.println("[deframe] FAIL: --require-seeds set but no seeds were available")
            exitProcess(2)
        }

        val out = opts.outFile?.bufferedWriter()
        var packetCount = 0
        var desyncCount = 0
        var truncationCount = 0

        fun emit(line: String) {
            if (out != null) out.appendLine(line) else println(line)
        }

        for (conn in connections) {
            System.err.println(
                "[deframe] connection epoch=${conn.epoch} fd=${conn.fd} role=${conn.role} " +
                    "peer=${conn.peer} c2s=${conn.c2s.size}B s2c=${conn.s2c.size}B"
            )
            val extractedSeeds = LoginSeedExtractor.extract(conn)
            val connSeeds = extractedSeeds ?: rawSeeds
            val seedSource = when {
                extractedSeeds != null -> "login-rsa"
                rawSeeds != null -> "capture"
                else -> ""
            }
            if (extractedSeeds != null && rawSeeds != null && !extractedSeeds.contentEquals(rawSeeds)) {
                System.err.println(
                    "[deframe] connection epoch=${conn.epoch} fd=${conn.fd} role=${conn.role} " +
                        "uses login RSA seeds ${formatSeeds(extractedSeeds)} (capture seeds differ)"
                )
            }
            emit(connHeaderJson(conn, connSeeds, seedSource))

            when (conn.role) {
                Role.JS5 -> {
                    // JS5 is not ISAAC-obfuscated. We emit it as raw framed
                    // segments (the cache-library-engineer owns JS5 framing; the
                    // recorder's job is to surface the bytes, attributed by fd).
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
                    if (rawSeeds != null && opts.probeUnknown) {
                        val result = IsaacDeframer(codec, rawSeeds, opts.isaacOffset).deframe(conn, ::emit)
                        packetCount += result.packets
                        desyncCount += result.desyncs
                        truncationCount += result.truncations
                    } else {
                        emit(rawDumpJson(conn, if (rawSeeds == null) "unknown-role-no-seeds" else "unknown-role"))
                    }
                }
            }
        }

        out?.flush()
        out?.close()

        System.err.println(
            "[deframe] DONE: ${connections.size} connections, $packetCount packets, " +
                "$desyncCount desync(s), $truncationCount truncation(s)"
        )
        if (opts.outFile != null) System.err.println("[deframe] transcript: ${opts.outFile.absolutePath}")
        if (opts.failOnDesync && desyncCount > 0) {
            System.err.println("[deframe] FAIL: --fail-on-desync set and transcript has $desyncCount desync(s)")
            exitProcess(3)
        }
        if (opts.failOnTruncation && truncationCount > 0) {
            System.err.println("[deframe] FAIL: --fail-on-truncation set and transcript has $truncationCount truncation(s)")
            exitProcess(4)
        }
    }

    // --- JS5 (non-ISAAC) emit: surface request/response bytes per fd ---
    private fun emitJs5(conn: Connection, emit: (String) -> Unit): Int {
        // The JS5 protocol is request-driven and not ISAAC-obfuscated. We do not
        // fully decode it here (out of scope for the recorder); we emit the raw
        // directional segments so the cache/JS5 engineer can compare bytes.
        if (conn.c2s.isNotEmpty()) {
            emit(
                Json.obj(
                    "dir" to "C2S", "fd" to conn.fd, "conn" to "js5",
                    "kind" to "js5_raw", "len" to conn.c2s.size,
                    "payload_hex" to Json.hex(conn.c2s, 256)
                )
            )
        }
        if (conn.s2c.isNotEmpty()) {
            emit(
                Json.obj(
                    "dir" to "S2C", "fd" to conn.fd, "conn" to "js5",
                    "kind" to "js5_raw", "len" to conn.s2c.size,
                    "payload_hex" to Json.hex(conn.s2c, 256)
                )
            )
        }
        return if (conn.c2s.isNotEmpty() || conn.s2c.isNotEmpty()) 1 else 0
    }

    private fun connHeaderJson(conn: Connection, seeds: IntArray?, seedSource: String): String = Json.obj(
        "record" to "connection",
        "epoch" to conn.epoch,
        "fd" to conn.fd,
        "role" to conn.role.name.lowercase(),
        "peer" to (conn.peer ?: "?"),
        "c2s_bytes" to conn.c2s.size,
        "s2c_bytes" to conn.s2c.size,
        "seed_source" to seedSource,
        "seeds_raw_c2s" to (seeds?.joinToString(",") { "0x%08x".format(it) } ?: ""),
        "isaac_delta" to EnvVars.ISAAC_DELTA
    )

    private fun formatSeeds(seeds: IntArray): String =
        seeds.joinToString(", ") { "0x%08x".format(it) }

    private fun rawDumpJson(conn: Connection, reason: String): String = Json.obj(
        "record" to "raw_dump",
        "fd" to conn.fd,
        "conn" to conn.role.name.lowercase(),
        "reason" to reason,
        "c2s_hex" to Json.hex(conn.c2s, 512),
        "s2c_hex" to Json.hex(conn.s2c, 512)
    )
}
