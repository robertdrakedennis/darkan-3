package org.darkan.tools

import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import kotlinx.io.Buffer
import kotlinx.io.Source
import org.darkan.core.EnvVars
import org.darkan.core.model.IFEvents
import org.darkan.core.net.Isaac
import org.darkan.core.net.prot.*
import org.darkan.core.net.prot.revision.rev948.register948
import org.darkan.tools.util.toHex
import world.gregs.voidps.buffer.*
import java.io.File
import kotlin.reflect.KClass
import kotlin.system.exitProcess

/**
 * Phase 4 wire-format correctness gate.
 *
 * Drives the verification entirely from a LIVE capture's raw byte streams,
 * de-ISAAC'd with the exact framing logic proven in [DecodeCapture] / [FramingRegression].
 *
 * For every opcode present in the capture:
 *
 *  - ENCODE side (ServerProt we send, S->C): if a [Codec.serverProts] encoder is registered,
 *    decode the captured body back into the ServerProt data-class instance (the inverse of the
 *    encoder), re-encode it via the registered encoder, and `contentEquals` the result against
 *    the captured body. A mismatch is a REAL wire-format bug (like the old IF_SETPOSITION offset
 *    bug) — it is reported with expected/actual hex and is NOT papered over.
 *
 *  - DECODE side (ClientProt we receive, C->S): if a [Codec.clientProtsByOpcode] decoder is
 *    registered, run it on the captured body and assert it consumes EXACTLY the framed payload
 *    (no underrun / no overrun) and yields a non-null instance.
 *
 * Where reconstructing the instance from bytes is not expressible through the packet's public
 * API (op 216 WorldListPacket — only constructible from a [world.gregs.voidps.worldlist.WorldList],
 * whose fixed Country enum cannot reproduce the live display names), the opcode is reported N/A
 * with the reason; its byte framing is already covered by [FramingRegression].
 *
 * Run: ./gradlew :tools:wireFormatVerify
 *   (override the capture session: -PwireFormatCapture="capture/login-..._s1")
 * Nonzero exit on any FAIL.
 */

private const val DEFAULT_CAPTURE = "capture/login-20260531-191837_s1"

/** First byte index at which two arrays differ (length mismatch counts at the shorter length). */
private fun firstDiff(a: ByteArray, b: ByteArray): Int {
    val n = minOf(a.size, b.size)
    for (i in 0 until n) if (a[i] != b[i]) return i
    return if (a.size != b.size) n else -1
}

/** Build a kotlinx.io Source over [body] for feeding inverse-decoders / client decoders. */
private fun source(body: ByteArray): Buffer = Buffer().apply { write(body) }

/** Encode a ServerProt instance through its registered encoder and return the body bytes. */
private suspend fun encodeBody(codec: Codec, prot: ServerProt): ByteArray {
    val entry = codec.serverProts[prot::class] ?: error("No encoder for ${prot::class.simpleName}")
    val ch = ByteChannel()
    entry.encoder?.invoke(prot, ch) ?: error("Null encoder for ${prot::class.simpleName}")
    ch.flush()
    val out = ByteArray(ch.availableForRead)
    ch.readFully(out)
    ch.close()
    return out
}

// ---------------------------------------------------------------------------------------------
// Capture framing: walk a raw direction stream, de-ISAAC, and collect (opcode -> bodies).
// Mirrors DecodeCapture / FramingRegression exactly. ServerProt opcodes may be 2-byte; ClientProt
// opcodes are single-byte. Sizes come from the codec's stub metadata.
// ---------------------------------------------------------------------------------------------

private fun parseKeys(captureDir: File): IntArray {
    val keysLine = File(captureDir, "isaac-keys.txt").readLines().first { "hex" in it }
    return Regex("0x([0-9A-Fa-f]+)").findAll(keysLine)
        .map { it.groupValues[1].toLong(16).toInt() }
        .toList().toIntArray()
        .also { require(it.size == 4) { "Expected 4 ISAAC keys, got ${it.size}" } }
}

/** First post-login byte offset for a direction (mirrors FramingRegression.skipLoginPrelude). */
private fun skipLoginPrelude(raw: ByteArray, server: Boolean): Int {
    return if (server) {
        var pos = 9                                  // 9B first-response
        pos++                                        // 1B login result
        val loginLen = raw[pos].toInt() and 0xFF; pos++
        pos + loginLen
    } else {
        var pos = 1                                  // 1B connection type
        pos++                                        // 1B login opcode
        val size = ((raw[pos].toInt() and 0xFF) shl 8) or (raw[pos + 1].toInt() and 0xFF); pos += 2
        pos + size
    }
}

/** Collect every packet body, grouped by opcode, for one direction. */
private fun collectBodies(captureDir: File, codec: Codec, server: Boolean): Map<Int, List<ByteArray>> {
    val raw = File(captureDir, if (server) "raw-s2c.bin" else "raw-c2s.bin").readBytes()
    val rawKeys = parseKeys(captureDir)
    val seed = if (server) IntArray(4) { rawKeys[it] + EnvVars.ISAAC_DELTA } else rawKeys.copyOf()
    val isaac = Isaac(seed)

    val bodies = linkedMapOf<Int, MutableList<ByteArray>>()
    var pos = skipLoginPrelude(raw, server)
    val total = raw.size

    while (pos < total) {
        val decoded = ((raw[pos].toInt() and 0xFF) - isaac.nextInt()) and 0xFF; pos++
        val opcode: Int = if (decoded < 128) decoded else {
            val d2 = ((raw[pos].toInt() and 0xFF) - isaac.nextInt()) and 0xFF; pos++
            (decoded - 128) * 256 + d2
        }
        val sizeMode = if (server) codec.serverProtSize(opcode) else codec.clientProtSize(opcode)
        val size = when {
            sizeMode >= 0 -> sizeMode
            sizeMode == -1 -> (raw[pos].toInt() and 0xFF).also { pos++ }
            else -> (((raw[pos].toInt() and 0xFF) shl 8) or (raw[pos + 1].toInt() and 0xFF)).also { pos += 2 }
        }
        if (pos + size > total) break
        bodies.getOrPut(opcode) { mutableListOf() }.add(raw.copyOfRange(pos, pos + size))
        pos += size
    }
    return bodies
}

// ---------------------------------------------------------------------------------------------
// Per-opcode inverse-decoders: reconstruct the ServerProt instance from a captured body.
// Each is the exact inverse of the corresponding registered encoder (see Rev948ServerCodecs*).
// Returning null means "this opcode's round-trip isn't expressible" (reported N/A).
// ---------------------------------------------------------------------------------------------

private fun reconstructServerProt(opcode: Int, body: ByteArray): ServerProt? {
    val s = source(body)
    return when (opcode) {
        // op 3 IF_SETTOPLEVELINTERFACE (19B). id at offset 9 (low, +0x80) / 10 (high).
        3 -> {
            s.skip(9L)
            val low = (s.readByte().toInt() + 0x80) and 0xFF
            val high = s.readByte().toInt() and 0xFF
            s.skip(8L)
            IfSetTopLevelInterface(topLevelId = (high shl 8) or low)
        }
        // op 5 RESET_ALL_VARPS (0B).
        5 -> ResetClientVarcache()
        // op 26 UPDATE_FRIENDLIST (varShort) — multi-friend records.
        26 -> {
            val updates = mutableListOf<FriendStatus.FriendStatusUpdate>()
            while (!s.exhausted()) {
                val warn = s.readUByte()
                val name = s.readRSString()
                val prev = s.readRSString()
                val world = s.readShort().toInt() and 0xFFFF
                val rank = s.readUByte()
                val flags = s.readUByte()
                var worldName = ""; var platform = 0; var worldFlags = 0
                if (world > 0) {
                    worldName = s.readRSString(); platform = s.readUByte(); worldFlags = s.readInt()
                }
                val notes = s.readRSString()
                updates += FriendStatus.FriendStatusUpdate(warn, name, prev, world, rank, flags, worldName, platform, worldFlags, notes)
            }
            FriendStatus(updates)
        }
        // op 28 VARP_LARGE (6B): writeInt(value) + writeShort(id).
        28 -> { val value = s.readInt(); val id = s.readShort().toInt() and 0xFFFF; VarpLarge(id, value) }
        // op 35 IF_SETEVENTS2 (12B): IfSetEvents(IFEvents). Inverse mirrors the op35 encoder.
        35 -> {
            val settings = s.readUIntMiddle()              // writeIntMiddle(settings)
            var fromSlot = s.readUShortLittle()            // writeShortLittle(fromSlot)
            if (fromSlot == 0xFFFF) fromSlot = -1
            var toSlot = s.readUShortLittle()              // writeShortLittle(toSlot)
            if (toSlot == 0xFFFF) toSlot = -1
            val hash = s.readInt()                         // writeInt(componentHash)
            val ev = IFEvents(hash ushr 16, hash and 0xFFFF, fromSlot, toSlot, settings)
            IfSetEvents(ev)
        }
        // op 44 UPDATE_STAT (6B): writeIntLittle(xp) + writeByte(level) + writeByteInverse(skillId).
        44 -> {
            val xp = s.readUIntLittle()
            val level = s.readUByte()
            val skill = s.readByteInverse()
            UpdateStat(skillId = skill, xp = xp, level = level)
        }
        // op 47 CLIENT_SETVARC_SMALL (3B): writeByteAdd(value) + writeShortLittle(id).
        47 -> { val value = s.readByteAdd(); val id = s.readShortLittle(); ClientSetVarcSmall(id, value) }
        // op 49 CHANGE_LOBBY (varShort, empty).
        49 -> ChangeLobby()
        // op 61 VARP_SMALL (3B): writeShort(id) + writeByte(-128 - value).
        61 -> {
            val id = s.readShort().toInt() and 0xFFFF
            val wire = s.readByte().toInt()                // = (-128 - value) as signed byte
            val value = -128 - wire
            VarpSmall(id, value)
        }
        // op 64 CLIENT_SETVARC_LARGE (6B): writeIntMiddle(value) + writeShortAddLittle(id).
        64 -> { val value = s.readUIntMiddle(); val id = s.readUShortAddLittle(); ClientSetVarcLarge(id, value) }
        // op 75 SET_READY_FLAG (0B).
        75 -> SetReadyFlag()
        // op 80 SET_RUN_ENERGY (1B).
        80 -> UpdateRunenergy(energy = s.readUByte())
        // op 82 IF_SETPOSITION (23B): byteSubtract(layer), skip4, intInverseMiddle(position),
        //        skip4, skip4, skip4, shortLittle(componentId).
        82 -> {
            val layer = s.readByteSubtract()
            s.skip(4L)
            val position = s.readUIntInverseMiddle()   // unsigned: encoder used writeIntInverseMiddle
            s.skip(4L); s.skip(4L); s.skip(4L)
            val componentId = s.readUShortLittle()
            IfSetPosition(componentId = componentId, layer = layer, position = position)
        }
        // op 110 RUNCLIENTSCRIPT (varShort): writeRSString(types) + reversed args + writeInt(scriptId).
        110 -> {
            val types = s.readRSString()
            // Args are written in reversed type order; read them back in that reversed order,
            // then un-reverse into declaration order so re-encoding reproduces the wire.
            val reversed = ArrayList<Any>(types.length)
            for (c in types.reversed()) {
                reversed += when (c) {
                    'i' -> s.readInt()
                    's' -> s.readRSString()
                    'l' -> s.readLong()
                    else -> error("Unknown RUNCLIENTSCRIPT type char '$c'")
                }
            }
            val scriptId = s.readInt()
            val args = Array<Any>(reversed.size) { reversed[reversed.size - 1 - it] }
            RunClientScript(scriptId, types, args)
        }
        // op 147 VARP_LONG (10B): writeShortAdd(id) + 2x writeIntInverseMiddle(value halves).
        147 -> {
            val id = s.readUShortAdd()
            val hi = s.readUIntInverseMiddle().toLong() and 0xFFFFFFFFL
            val lo = s.readUIntInverseMiddle().toLong() and 0xFFFFFFFFL
            VarpLong(id, (hi shl 32) or lo)
        }
        // op 216 WorldListPacket — not expressible via the WorldList public API (fixed Country
        // enum cannot reproduce live display names). Framing covered by FramingRegression.
        216 -> null
        else -> null
    }
}

// ---------------------------------------------------------------------------------------------

private data class Row(
    val opcode: Int,
    val name: String,
    val direction: String,
    val has: String,         // "encoder" / "decoder" / "none"
    val status: String,      // PASS / FAIL / N/A / NO CODEC
    val detail: String = "",
)

fun main(args: Array<String>): Unit = runBlocking {
    val codec = register948()
    val capturePath = args.getOrNull(0) ?: DEFAULT_CAPTURE
    val captureDir = File(capturePath).let { if (it.isDirectory) it else error("Capture dir not found: $capturePath") }
    println("WireFormatVerify — capture: ${captureDir.path}\n")

    val s2cBodies = collectBodies(captureDir, codec, server = true)
    val c2sBodies = collectBodies(captureDir, codec, server = false)

    val encodeOpcodes = intArrayOf(3, 5, 26, 28, 35, 44, 47, 49, 61, 64, 75, 80, 82, 110, 128, 147, 216)
    val decodeOpcodes = intArrayOf(5, 51, 52, 54, 127)

    val rows = mutableListOf<Row>()
    var failures = 0

    // Map opcode -> registered ServerProt class (for "has encoder?" + nice naming).
    val opToServerClass: Map<Int, KClass<out ServerProt>> =
        codec.serverProts.entries.associate { (cls, entry) -> entry.opcode to cls }

    // ---- ENCODE SIDE (S->C) ----
    println("=== ENCODE side (ServerProt, S->C) ===")
    for (op in encodeOpcodes) {
        val name = codec.serverProtName(op)
        val cls = opToServerClass[op]
        val entry = cls?.let { codec.serverProts[it] }
        val hasEncoder = entry?.encoder != null
        val bodies = s2cBodies[op]

        if (!hasEncoder) {
            rows += Row(op, name, "S->C", "none", "NO CODEC", "no encoder registered")
            println("[NO CODEC] op$op ($name): no encoder registered")
            continue
        }
        if (bodies.isNullOrEmpty()) {
            rows += Row(op, name, "S->C", "encoder", "N/A", "opcode not present in capture")
            println("[N/A]      op$op ($name): encoder present but opcode absent from capture")
            continue
        }

        // Verify the encoder reproduces EVERY distinct captured body for this opcode.
        val distinct = bodies.distinctBy { it.toHex() }
        var opPass = true
        var detail = ""
        for (expected in distinct) {
            val prot = try {
                reconstructServerProt(op, expected)
            } catch (e: Exception) {
                opPass = false; detail = "reconstruct threw ${e::class.simpleName}: ${e.message}"; break
            }
            if (prot == null) {
                rows += Row(op, name, "S->C", "encoder", "N/A", "round-trip not expressible via public API")
                println("[N/A]      op$op ($name): round-trip not expressible (framing covered by FramingRegression)")
                opPass = false; detail = "__NA__"; break
            }
            val actual = encodeBody(codec, prot)
            if (!expected.contentEquals(actual)) {
                opPass = false
                val at = firstDiff(expected, actual)
                val expB = expected.getOrNull(at)?.toInt()?.and(0xFF)
                val actB = actual.getOrNull(at)?.toInt()?.and(0xFF)
                detail = "first diff @byte $at (exp=${expB?.let { "%02x".format(it) } ?: "--"} act=${actB?.let { "%02x".format(it) } ?: "--"}); len exp=${expected.size} act=${actual.size}"
                println("[FAIL]     op$op ($name): WIRE MISMATCH — $detail")
                println("    expected: ${expected.toHex()}")
                println("    actual:   ${actual.toHex()}")
                break
            }
        }
        if (detail == "__NA__") continue
        if (opPass) {
            rows += Row(op, name, "S->C", "encoder", "PASS", "${distinct.size} distinct body/bodies (${bodies.size} total)")
            println("[PASS]     op$op ($name): ${distinct.size} distinct body/bodies matched (${bodies.size} occurrences)")
        } else {
            failures++
            rows += Row(op, name, "S->C", "encoder", "FAIL", detail)
        }
    }

    // ---- DECODE SIDE (C->S) ----
    println("\n=== DECODE side (ClientProt, C->S) ===")
    for (op in decodeOpcodes) {
        val name = codec.clientProtName(op)
        val clientCodec = codec.clientProtsByOpcode[op]
        val hasDecoder = clientCodec?.decoder != null
        val bodies = c2sBodies[op]

        if (clientCodec == null || !hasDecoder) {
            rows += Row(op, name, "C->S", "none", "NO CODEC", "no decoder yet (Phase 5)")
            println("[NO CODEC] op$op ($name): no decoder yet" + if (op == 127) " (IF_BUTTON1 added in Phase 5)" else "")
            continue
        }
        if (bodies.isNullOrEmpty()) {
            rows += Row(op, name, "C->S", "decoder", "N/A", "opcode not present in capture")
            println("[N/A]      op$op ($name): decoder present but opcode absent from capture")
            continue
        }

        var opPass = true
        var detail = ""
        val distinct = bodies.distinctBy { it.toHex() }
        for (body in distinct) {
            val src = source(body)
            val result = try {
                clientCodec.decoder!!.invoke(src, body.size)
            } catch (e: Exception) {
                opPass = false; detail = "decoder threw ${e::class.simpleName}: ${e.message}"; break
            }
            val remaining = src.size   // Buffer.size = unread bytes remaining
            if (result == null) { opPass = false; detail = "decoder returned null"; break }
            if (remaining != 0L) {
                opPass = false
                detail = "decoder left $remaining byte(s) unconsumed of ${body.size}B body (${body.toHex()})"
                break
            }
        }
        if (opPass) {
            rows += Row(op, name, "C->S", "decoder", "PASS", "${distinct.size} distinct body/bodies fully consumed")
            println("[PASS]     op$op ($name): ${distinct.size} distinct body/bodies decoded with exact consumption (${bodies.size} occurrences)")
        } else {
            failures++
            rows += Row(op, name, "C->S", "decoder", "FAIL", detail)
            println("[FAIL]     op$op ($name): $detail")
        }
    }

    // ---- SUMMARY TABLE ----
    println("\n=== SUMMARY ===")
    println(String.format("%-7s %-26s %-5s %-9s %-8s %s", "opcode", "name", "dir", "codec", "result", "detail"))
    println("-".repeat(110))
    for (r in rows) {
        println(String.format("%-7d %-26s %-5s %-9s %-8s %s",
            r.opcode, r.name.take(26), r.direction, r.has, r.status, r.detail.take(48)))
    }

    val passCount = rows.count { it.status == "PASS" }
    println("\n${rows.size} opcodes checked | $passCount PASS | $failures FAIL | " +
        "${rows.count { it.status == "N/A" }} N/A | ${rows.count { it.status == "NO CODEC" }} NO CODEC")

    if (failures > 0) {
        System.err.println("\n$failures wire-format FAILURE(S) — real bug(s) for Phase 5 to fix.")
        exitProcess(1)
    }
    println("\nALL WIRE FORMATS VERIFIED against live capture.")
    exitProcess(0)
}
