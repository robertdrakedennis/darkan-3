package org.darkan.tools

import org.darkan.core.EnvVars
import org.darkan.core.net.Isaac
import org.darkan.core.net.prot.Codec
import org.darkan.core.net.prot.revision.rev948.register948
import java.io.File
import kotlin.system.exitProcess

/**
 * Automated framing-regression harness — the hard gate that proves every packet SIZE is correct.
 *
 * It re-decodes real packet captures in BOTH directions using the exact ISAAC + opcode/size
 * framing logic proven out in [DecodeCapture] (S->C only), and turns the decode into an
 * ASSERTION: a capture/direction PASSES iff it decodes to EOF with `bytesConsumed == totalBytes`
 * and never hits an out-of-range opcode or a truncated payload.
 *
 * A desync is a real packet-size bug — the harness reports the exact offending opcode, offset,
 * decoded size and remaining bytes so the size can be corrected (Phase 5). It does NOT and must
 * NOT "fix" anything by mutating the codec.
 *
 * Direction conventions (must match the live MITM capture + the running server):
 *   - S->C opcodes use ISAAC seeded with `keys + 50` (EnvVars.ISAAC_DELTA). Opcodes may be
 *     1 or 2 bytes (`decoded >= 128` => 2-byte). Sizes come from [Codec.serverProtSize].
 *   - C->S opcodes use ISAAC seeded with the RAW keys. Opcodes are single-byte only
 *     (ClientProt max opcode is 129). Sizes come from [Codec.clientProtSize].
 *
 * Size mode encoding (shared by both directions): a fixed size is the value itself (>= 0),
 * -1 means a 1-byte length prefix, -2 means a 2-byte big-endian length prefix.
 */
object FramingRegression {

    /** Valid ServerProt opcode range (vector has 218 entries). */
    private const val SERVER_PROT_COUNT = 218

    /** Valid ClientProt opcode range (vector has 130 entries; max opcode 129). */
    private const val CLIENT_PROT_COUNT = 130

    data class Result(
        val direction: String,           // "S->C" or "C->S"
        val captureDir: String,
        val packetsDecoded: Int,
        val bytesConsumed: Int,
        val totalBytes: Int,
        val desyncAt: Int?,              // byte offset of the out-of-range opcode, or null
        val truncatedAt: Int?,           // byte offset of the truncated packet, or null
        val opcodesSeen: Set<Int>,
    ) {
        /** A direction passes iff it consumed every byte with no desync and no truncation. */
        val passed: Boolean
            get() = desyncAt == null && truncatedAt == null && bytesConsumed == totalBytes
    }

    /**
     * Parse the 4 ISAAC keys from the capture's `isaac-keys.txt`.
     * Mirrors [DecodeCapture]: the "hex" line holds 4 `0x........` ints.
     */
    private fun parseKeys(captureDir: File): IntArray {
        val keysFile = File(captureDir, "isaac-keys.txt")
        val keysLine = keysFile.readLines().first { "hex" in it }
        return Regex("0x([0-9A-Fa-f]+)")
            .findAll(keysLine)
            .map { it.groupValues[1].toLong(16).toInt() }
            .toList()
            .toIntArray()
            .also { require(it.size == 4) { "Expected 4 ISAAC keys in ${keysFile.path}, got ${it.size}" } }
    }

    /**
     * Locate the post-login offset where ISAAC framing begins, by parsing the login prelude
     * structurally. Returns -1 if the prelude cannot be parsed structurally for this capture.
     *
     * S->C prelude (mirrors [DecodeCapture]):
     *   [9B first-response: 1B response + 8B session key]
     *   [1B login result][1B login-data length][N login data]
     *
     * C->S prelude (mirrors [LoginServer.handleLogin]):
     *   [1B connection type (14 LOBBY / 16 LOGIN)]
     *   [1B login opcode (19 LOBBY / 16 LOGIN)][2B BE login-block size][N login block]
     */
    fun skipLoginPrelude(raw: ByteArray, direction: String): Int {
        return when (direction) {
            "S->C" -> {
                // 9B first-response, then 1B result + 1B len + len-data.
                if (raw.size < 11) {
                    System.err.println("    [prelude] S->C capture too short (${raw.size}B) to hold login prelude")
                    return -1
                }
                var pos = 9
                @Suppress("UNUSED_VARIABLE")
                val loginResult = raw[pos].toInt() and 0xFF; pos++
                val loginLen = raw[pos].toInt() and 0xFF; pos++
                val post = pos + loginLen
                if (post > raw.size) {
                    System.err.println("    [prelude] S->C login-data length $loginLen overruns capture (post=$post > ${raw.size})")
                    return -1
                }
                post
            }
            "C->S" -> {
                if (raw.size < 4) {
                    System.err.println("    [prelude] C->S capture too short (${raw.size}B) to hold login prelude")
                    return -1
                }
                var pos = 0
                val connType = raw[pos].toInt() and 0xFF; pos++
                // Connection types that route to the login handler (see RequestOpcode).
                if (connType !in intArrayOf(14, 16, 15, 19)) {
                    System.err.println("    [prelude] C->S unexpected connection-type byte $connType (0x${"%02x".format(connType)}); not a login stream?")
                    return -1
                }
                val loginOpcode = raw[pos].toInt() and 0xFF; pos++
                val size = ((raw[pos].toInt() and 0xFF) shl 8) or (raw[pos + 1].toInt() and 0xFF); pos += 2
                val post = pos + size
                if (size <= 0 || post > raw.size) {
                    System.err.println("    [prelude] C->S login-block size $size overruns capture (post=$post > ${raw.size}); loginOpcode=$loginOpcode")
                    return -1
                }
                post
            }
            else -> error("Unknown direction: $direction")
        }
    }

    /**
     * Decode a single direction of a capture and assert full consumption with no desync.
     */
    private fun verifyDirection(
        captureDir: File,
        rawFile: File,
        direction: String,
        codec: Codec,
        rawKeys: IntArray,
    ): Result {
        val raw = rawFile.readBytes()
        val total = raw.size
        val opcodesSeen = sortedSetOf<Int>()

        val isServer = direction == "S->C"
        val seed = if (isServer) IntArray(4) { rawKeys[it] + EnvVars.ISAAC_DELTA } else rawKeys.copyOf()
        val isaac = Isaac(seed)
        val protCount = if (isServer) SERVER_PROT_COUNT else CLIENT_PROT_COUNT

        val post = skipLoginPrelude(raw, direction)
        if (post < 0) {
            // Could not parse prelude structurally — report as a hard failure, never silently mis-skip.
            return Result(direction, captureDir.path, 0, 0, total, desyncAt = 0, truncatedAt = null, opcodesSeen = emptySet())
        }

        var pos = post
        var pktCount = 0
        var desyncAt: Int? = null
        var truncatedAt: Int? = null

        while (pos < total) {
            val pktStart = pos
            val rawByte = raw[pos].toInt() and 0xFF; pos++
            val decoded = (rawByte - isaac.nextInt()) and 0xFF

            val opcode: Int
            if (decoded < 128) {
                opcode = decoded
            } else {
                if (!isServer) {
                    // ClientProt opcodes are single-byte only (max opcode 129 < 128*2).
                    // A 2-byte opcode here means the C->S stream has desynced.
                    System.err.println(
                        "[$direction] DESYNC at pkt#$pktCount byte $pktStart: " +
                            "decoded=$decoded implies 2-byte opcode, but ClientProt is single-byte only"
                    )
                    desyncAt = pktStart
                    break
                }
                if (pos >= total) { truncatedAt = pktStart; break }
                val rawByte2 = raw[pos].toInt() and 0xFF; pos++
                val decoded2 = (rawByte2 - isaac.nextInt()) and 0xFF
                opcode = (decoded - 128) * 256 + decoded2
            }

            if (opcode !in 0 until protCount) {
                val hexStart = maxOf(0, pktStart - 8)
                val hexEnd = minOf(total, pktStart + 24)
                System.err.println(
                    "[$direction] DESYNC at pkt#$pktCount byte $pktStart: " +
                        "raw=0x${"%02X".format(raw[pktStart].toInt() and 0xFF)} decoded=$decoded opcode=$opcode " +
                        "(out of range 0..${protCount - 1})"
                )
                System.err.println(
                    "    hex: ${raw.slice(hexStart until hexEnd).joinToString(" ") { "%02x".format(it.toInt() and 0xFF) }}"
                )
                desyncAt = pktStart
                break
            }

            val sizeMode = if (isServer) codec.serverProtSize(opcode) else codec.clientProtSize(opcode)
            val pktSize: Int = when {
                sizeMode >= 0 -> sizeMode
                sizeMode == -1 -> {
                    if (pos >= total) { truncatedAt = pktStart; break }
                    (raw[pos].toInt() and 0xFF).also { pos++ }
                }
                sizeMode == -2 -> {
                    if (pos + 1 >= total) { truncatedAt = pktStart; break }
                    (((raw[pos].toInt() and 0xFF) shl 8) or (raw[pos + 1].toInt() and 0xFF)).also { pos += 2 }
                }
                else -> error("Unknown size mode $sizeMode")
            }

            if (pos + pktSize > total) {
                val name = if (isServer) codec.serverProtName(opcode) else codec.clientProtName(opcode)
                System.err.println(
                    "[$direction] TRUNCATED at pkt#$pktCount byte $pktStart: op=$opcode ($name) " +
                        "needs ${pktSize}B but only ${total - pos}B remain"
                )
                truncatedAt = pktStart
                break
            }

            pos += pktSize
            pktCount++
            opcodesSeen.add(opcode)
        }

        return Result(
            direction = direction,
            captureDir = captureDir.path,
            packetsDecoded = pktCount,
            bytesConsumed = pos,
            totalBytes = total,
            desyncAt = desyncAt,
            truncatedAt = truncatedAt,
            opcodesSeen = opcodesSeen,
        )
    }

    /**
     * Verify both directions (S->C and C->S) of a single capture session directory.
     * A direction with no corresponding raw file is skipped (not failed).
     */
    fun verify(captureDir: File, codec: Codec): List<Result> {
        val rawKeys = parseKeys(captureDir)
        val results = mutableListOf<Result>()

        val s2c = File(captureDir, "raw-s2c.bin")
        if (s2c.isFile) results += verifyDirection(captureDir, s2c, "S->C", codec, rawKeys)

        val c2s = File(captureDir, "raw-c2s.bin")
        if (c2s.isFile) results += verifyDirection(captureDir, c2s, "C->S", codec, rawKeys)

        return results
    }

    /**
     * Verify every session directory under [captureRoot] (each session has its own isaac-keys.txt).
     */
    fun verifyAll(captureRoot: File, codec: Codec): List<Result> {
        val sessions = captureRoot.listFiles()
            ?.filter { it.isDirectory && File(it, "isaac-keys.txt").isFile }
            ?.sortedBy { it.name }
            ?: emptyList()
        if (sessions.isEmpty()) {
            System.err.println("No capture sessions found under ${captureRoot.path}")
        }
        return sessions.flatMap { verify(it, codec) }
    }
}

private fun printResult(r: FramingRegression.Result) {
    val status = if (r.passed) "PASS" else "FAIL"
    println(
        "[$status] ${r.direction}  ${File(r.captureDir).name}  " +
            "packets=${r.packetsDecoded}  consumed=${r.bytesConsumed}/${r.totalBytes}" +
            (r.desyncAt?.let { "  desyncAt=$it" } ?: "") +
            (r.truncatedAt?.let { "  truncatedAt=$it" } ?: "")
    )
    if (r.opcodesSeen.isNotEmpty()) {
        println("        opcodesSeen (${r.opcodesSeen.size}): ${r.opcodesSeen.joinToString(", ")}")
    }
}

fun main(args: Array<String>) {
    val codec = register948()

    val target = args.getOrNull(0)?.let { File(it) }
    val results: List<FramingRegression.Result> = when {
        // Explicit session dir (has isaac-keys.txt)
        target != null && File(target, "isaac-keys.txt").isFile -> FramingRegression.verify(target, codec)
        // Explicit capture root (scan every session under it)
        target != null && target.isDirectory -> FramingRegression.verifyAll(target, codec)
        // Default: scan the whole capture/ tree
        else -> FramingRegression.verifyAll(File("capture"), codec)
    }

    println("=== Framing Regression: ${results.size} direction(s) checked ===")
    results.forEach(::printResult)

    val failures = results.filter { !it.passed }
    if (results.isEmpty()) {
        System.err.println("FATAL: no captures/directions were verified — treating as failure.")
        exitProcess(1)
    }
    if (failures.isEmpty()) {
        println("\nALL GREEN: every capture/direction fully consumed with zero desync.")
        exitProcess(0)
    } else {
        System.err.println("\n${failures.size} direction(s) FAILED framing regression — size bug(s) to hand to Phase 5.")
        exitProcess(1)
    }
}
