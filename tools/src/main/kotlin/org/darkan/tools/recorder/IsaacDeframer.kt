package org.darkan.tools.recorder

import org.darkan.core.EnvVars
import org.darkan.core.net.Isaac
import org.darkan.core.net.prot.Codec
import world.gregs.voidps.cache.Cache
import world.gregs.voidps.cache.config.decoder.ClientVariableParameterDecoder

/**
 * ISAAC post-login deframer for one lobby/world [Connection]. Ports the proven
 * [org.darkan.tools.loginproxy.LoginProxy] / [org.darkan.tools.DecodeCapture]
 * logic to the recorder's offline capture model.
 *
 * Per direction we:
 *   1. Walk the cleartext login prelude by byte count to find where ISAAC
 *      framing begins (the "ISAAC engagement boundary"):
 *        - S2C: [9B first-response] [1B login-result] [1B data-len] [N data].
 *        - C2S: [1B connection-type] [1B login-opcode] [2B BE login-block size]
 *               [N login block].
 *   2. From that offset, decode packets: ISAAC-deobfuscate the opcode
 *      (`(raw - cipher.nextInt()) & 0xFF`), resolve size from the codec, slice
 *      the (cleartext) payload, emit a JSONL line.
 *
 * The ISAAC engagement offset can be forced (`isaacOffset >= 0`) or auto-found:
 * we try the structurally-parsed offset and, if it desyncs immediately, scan a
 * small window of nearby offsets for the one that yields the longest clean
 * decode. This is exactly the world-login boundary tunable the plan calls for.
 */
class IsaacDeframer(
    private val codec: Codec,
    private val rawSeeds: IntArray,
    private val isaacOffsetOverride: Int,
) {
    data class Outcome(val packets: Int, val desyncs: Int, val truncations: Int)

    fun deframe(conn: Connection, emit: (String) -> Unit): Outcome {
        var packets = 0
        var desyncs = 0
        var truncations = 0

        // ---- S2C ----
        run {
            val raw = conn.s2c
            val structuralOffset = parseS2CPrelude(raw, conn, emit)
            val offset = if (isaacOffsetOverride >= 0) isaacOffsetOverride else structuralOffset
            if (offset in 0..raw.size) {
                val seed = IntArray(4) { rawSeeds[it] + EnvVars.ISAAC_DELTA } // S2C = +50
                val res = decodeStream(
                    raw, offset, conn, dir = "S2C", isServer = true,
                    seed = seed, maxOpcode = SERVER_PROT_COUNT, autoBoundary = isaacOffsetOverride < 0, emit = emit
                )
                packets += res.packets; desyncs += res.desyncs
                truncations += res.truncations
            } else {
                emit(Json.obj("dir" to "S2C", "fd" to conn.fd, "conn" to conn.role.name.lowercase(),
                    "kind" to "prelude_unparsed", "note" to "could not locate ISAAC offset (got $offset)"))
            }
        }

        // ---- C2S ----
        run {
            val raw = conn.c2s
            val structuralOffset = parseC2SPrelude(raw, conn, emit)
            // C2S engagement is structural; the override is S2C-centric so we don't apply it here.
            val offset = structuralOffset
            if (offset in 0..raw.size) {
                val seed = rawSeeds.copyOf() // C2S = raw
                val res = decodeStream(
                    raw, offset, conn, dir = "C2S", isServer = false,
                    seed = seed, maxOpcode = CLIENT_PROT_COUNT, autoBoundary = false, emit = emit
                )
                packets += res.packets; desyncs += res.desyncs
                truncations += res.truncations
            } else {
                emit(Json.obj("dir" to "C2S", "fd" to conn.fd, "conn" to conn.role.name.lowercase(),
                    "kind" to "prelude_unparsed", "note" to "could not locate ISAAC offset (got $offset)"))
            }
        }

        return Outcome(packets, desyncs, truncations)
    }

    // -----------------------------------------------------------------------
    // Prelude parsing (mirrors DecodeCapture / FramingRegression.skipLoginPrelude).
    // -----------------------------------------------------------------------

    /**
     * S2C prelude: [9B first-response (1B code + 8B session key)] then a
     * role-specific success prelude before ISAAC framing begins.
     */
    private fun parseS2CPrelude(raw: ByteArray, conn: Connection, emit: (String) -> Unit): Int {
        if (raw.size < 9) {
            emit(loginEvt(conn, "S2C", "first_response_truncated", mapOf("have" to raw.size)))
            return -1
        }
        val code = raw[0].toInt() and 0xFF
        val sessionKey = beLong(raw, 1)
        emit(loginEvt(conn, "S2C", "first_response", mapOf("code" to code, "session_key" to "0x%016x".format(sessionKey))))
        if (code != 0) return -1 // no post-login stream on a non-OK first response

        var pos = 9
        if (pos >= raw.size) return raw.size
        val result = raw[pos].toInt() and 0xFF; pos++
        emit(loginEvt(conn, "S2C", "login_result", mapOf("code" to result)))
        if (result != 2) {
            // Non-success: there may still be ISAAC traffic (TOTP etc.) right after.
            return pos
        }

        if (conn.role == Role.WORLD) return parseWorldS2CPrelude(raw, conn, pos, emit)
        return parseLobbyS2CPrelude(raw, conn, pos, emit)
    }

    private fun parseLobbyS2CPrelude(raw: ByteArray, conn: Connection, start: Int, emit: (String) -> Unit): Int {
        var pos = start
        if (pos >= raw.size) return raw.size
        val dataLen = raw[pos].toInt() and 0xFF; pos++
        emit(loginEvt(conn, "S2C", "login_data_len", mapOf("len" to dataLen)))
        pos += dataLen
        return pos.coerceAtMost(raw.size)
    }

    private fun parseWorldS2CPrelude(raw: ByteArray, conn: Connection, start: Int, emit: (String) -> Unit): Int {
        var pos = start
        if (pos + 2 > raw.size) return raw.size

        val serverClientVarBlockLen = ((raw[pos].toInt() and 0xFF) shl 8) or (raw[pos + 1].toInt() and 0xFF)
        pos += 2
        emit(loginEvt(conn, "S2C", "server_client_var_block_len", mapOf("len" to serverClientVarBlockLen)))

        val serverClientVarEnd = pos + serverClientVarBlockLen
        if (serverClientVarEnd > raw.size) {
            emit(loginEvt(conn, "S2C", "server_client_var_block_truncated",
                mapOf("len" to serverClientVarBlockLen, "remaining" to (raw.size - pos))))
            return -1
        }
        if (serverClientVarBlockLen > 0) {
            val ackFlag = raw[pos].toInt() and 0xFF
            emit(loginEvt(conn, "S2C", "server_client_var_ack", mapOf("flag" to ackFlag)))
            parseServerClientVarEntries(raw, pos, serverClientVarEnd, conn, emit)
        }
        pos = serverClientVarEnd

        if (pos >= raw.size) return raw.size
        val playersByte = raw[pos].toInt() and 0xFF; pos++
        emit(loginEvt(conn, "S2C", "players_byte", mapOf("value" to playersByte)))

        if (pos >= raw.size) return raw.size
        val loginDataLen = raw[pos].toInt() and 0xFF; pos++
        emit(loginEvt(conn, "S2C", "world_login_data_len", mapOf("len" to loginDataLen)))
        pos += loginDataLen
        return pos.coerceAtMost(raw.size)
    }

    private fun parseServerClientVarEntries(
        raw: ByteArray,
        start: Int,
        end: Int,
        conn: Connection,
        emit: (String) -> Unit,
    ) {
        var pos = start + 1
        var index = 0
        while (pos < end) {
            val entryStart = pos
            if (pos + 2 > end) {
                emit(serverClientVarError(conn, index, entryStart, "truncated id"))
                return
            }
            val id = ((raw[pos].toInt() and 0xFF) shl 8) or (raw[pos + 1].toInt() and 0xFF)
            pos += 2
            val type = clientVarType(id)
            when (type) {
                's' -> {
                    val valueStart = pos
                    while (pos < end && raw[pos].toInt() != 0) pos++
                    if (pos >= end) {
                        emit(serverClientVarError(conn, index, entryStart, "unterminated string id=$id"))
                        return
                    }
                    val value = String(raw, valueStart, pos - valueStart, Charsets.ISO_8859_1)
                    pos++
                    emit(serverClientVarEntry(conn, index, entryStart, id, "string", value, pos - entryStart))
                }
                'l' -> {
                    if (pos + 8 > end) {
                        emit(serverClientVarError(conn, index, entryStart, "truncated long id=$id"))
                        return
                    }
                    val value = beLong(raw, pos)
                    pos += 8
                    emit(serverClientVarEntry(conn, index, entryStart, id, "long", value, pos - entryStart))
                }
                else -> {
                    if (pos + 4 > end) {
                        emit(serverClientVarError(conn, index, entryStart, "truncated int id=$id type=${type?.code ?: 0}"))
                        return
                    }
                    val value = beInt(raw, pos)
                    pos += 4
                    emit(serverClientVarEntry(conn, index, entryStart, id, "int", value, pos - entryStart))
                }
            }
            index++
        }
        emit(loginEvt(conn, "S2C", "server_client_var_entries", mapOf("count" to index)))
    }

    /**
     * C2S prelude: [1B connection-type][1B login-opcode][2B BE login-block size]
     * [N login block]. Emits header lines and returns the post-login offset.
     */
    private fun parseC2SPrelude(raw: ByteArray, conn: Connection, emit: (String) -> Unit): Int {
        if (raw.isEmpty()) return -1
        val connType = raw[0].toInt() and 0xFF
        emit(loginEvt(conn, "C2S", "connection_type", mapOf("type" to connType)))
        var pos = 1
        if (pos + 3 > raw.size) return raw.size // only the type byte present so far
        val loginOpcode = raw[pos].toInt() and 0xFF; pos++
        val blockSize = ((raw[pos].toInt() and 0xFF) shl 8) or (raw[pos + 1].toInt() and 0xFF); pos += 2
        emit(loginEvt(conn, "C2S", "login_packet", mapOf("opcode" to loginOpcode, "block_size" to blockSize)))
        pos += blockSize
        if (conn.role == Role.WORLD && pos < raw.size) {
            val hasExtra = raw[pos].toInt() and 0xFF
            emit(loginEvt(conn, "C2S", "world_login_has_extra", mapOf("value" to hasExtra)))
            pos++
        }
        return pos.coerceAtMost(raw.size)
    }

    // -----------------------------------------------------------------------
    // Core ISAAC packet decode (ported from LoginProxy.processPostLoginS2C /
    // parseClientPostLogin — single-pass over an already-complete byte array).
    // -----------------------------------------------------------------------

    private data class StreamResult(val packets: Int, val desyncs: Int, val truncations: Int)

    private fun decodeStream(
        raw: ByteArray,
        startOffset: Int,
        conn: Connection,
        dir: String,
        isServer: Boolean,
        seed: IntArray,
        maxOpcode: Int,
        autoBoundary: Boolean,
        emit: (String) -> Unit,
    ): StreamResult {
        // Auto-boundary: if the structural offset desyncs on the very first
        // packet, scan a small window for a better one (the world-login tunable).
        val candidateOffsets: List<Int> = if (autoBoundary) {
            (listOf(startOffset) + (startOffset - 4..startOffset + 8).toList())
                .filter { it in 0..raw.size }.distinct()
        } else {
            listOf(startOffset)
        }

        var best: DecodeRun? = null
        for (off in candidateOffsets) {
            val run = decodeFrom(raw, off, isServer, seed, maxOpcode)
            val currentBest = best
            if (currentBest == null || run.packets > currentBest.packets || (run.packets == currentBest.packets && run.consumed > currentBest.consumed)) {
                best = run
            }
            // A clean full-consume decode is the winner — stop scanning.
            if (run.desyncAt == null && run.truncationAt == null && run.consumed == raw.size - off) break
        }
        val chosen = best!!
        if (autoBoundary && chosen.offset != startOffset) {
            emit(loginEvt(conn, dir, "isaac_boundary_adjusted",
                mapOf("structural" to startOffset, "chosen" to chosen.offset, "packets" to chosen.packets)))
        }

        // Emit the chosen run's packets.
        var seq = 0
        for (p in chosen.packets_) {
            emit(packetJson(conn, dir, seq++, p))
        }
        if (chosen.desyncAt != null) {
            emit(Json.obj(
                "record" to "desync", "dir" to dir, "fd" to conn.fd, "conn" to conn.role.name.lowercase(),
                "at_byte" to chosen.desyncAt, "decoded_opcode" to chosen.desyncOpcode,
                "isaac_index" to chosen.desyncIsaacIndex,
                "context_hex" to Json.hex(
                    raw.copyOfRange(
                        maxOf(0, chosen.desyncAt - 8),
                        minOf(raw.size, chosen.desyncAt + 24)
                    ), 64
                ),
                "note" to "decoded opcode out of range [0,$maxOpcode) — size table or boundary mismatch (a finding)"
            ))
        }
        if (chosen.truncationAt != null) {
            emit(Json.obj(
                "record" to "truncated", "dir" to dir, "fd" to conn.fd, "conn" to conn.role.name.lowercase(),
                "at_byte" to chosen.truncationAt,
                "isaac_index" to chosen.truncationIsaacIndex,
                "reason" to chosen.truncationReason,
                "context_hex" to Json.hex(
                    raw.copyOfRange(
                        maxOf(0, chosen.truncationAt - 8),
                        minOf(raw.size, chosen.truncationAt + 24)
                    ), 64
                )
            ))
        }
        return StreamResult(
            chosen.packets,
            if (chosen.desyncAt != null) 1 else 0,
            if (chosen.truncationAt != null) 1 else 0,
        )
    }

    private data class DecodedPacket(
        val opcode: Int,
        val name: String,
        val sizeKind: String,
        val size: Int,
        val payload: ByteArray,
        val atByte: Int,
        val isaacIndex: Int,
    )

    private class DecodeRun(
        val offset: Int,
        val packets_: MutableList<DecodedPacket>,
        val consumed: Int,
        val desyncAt: Int?,
        val desyncOpcode: Int,
        val desyncIsaacIndex: Int,
        val truncationAt: Int?,
        val truncationReason: String,
        val truncationIsaacIndex: Int,
    ) {
        val packets: Int get() = packets_.size
    }

    /**
     * Decode packets from [start] using a fresh ISAAC seeded with [seed].
     * Implements the 1-or-2-byte opcode rule, both-bytes-consume-ISAAC, and
     * stops at the first out-of-range opcode (desync) or truncated payload.
     */
    private fun decodeFrom(raw: ByteArray, start: Int, isServer: Boolean, seed: IntArray, maxOpcode: Int): DecodeRun {
        val cipher = Isaac(seed.copyOf())
        val packets = ArrayList<DecodedPacket>()
        var pos = start
        var isaacIndex = 0

        while (pos < raw.size) {
            val pktStart = pos
            val rawByte = raw[pos].toInt() and 0xFF; pos++
            val d0 = (rawByte - cipher.nextInt()) and 0xFF; isaacIndex++
            val opcode: Int
            if (!isServer || d0 < 128) {
                opcode = d0
            } else {
                if (pos >= raw.size) {
                    return DecodeRun(start, packets, pos - start, null, -1, -1, pktStart, "partial 2-byte opcode at EOF", isaacIndex)
                }
                val rb2 = raw[pos].toInt() and 0xFF; pos++
                val d1 = (rb2 - cipher.nextInt()) and 0xFF; isaacIndex++
                opcode = (d0 - 128) * 256 + d1
            }

            if (!isKnownOpcode(opcode, isServer, maxOpcode)) {
                return DecodeRun(start, packets, pos - start, pktStart, opcode, isaacIndex, null, "", -1)
            }

            val sizeMode = if (isServer) codec.serverProtSize(opcode) else codec.clientProtSize(opcode)
            val size: Int = when (sizeMode) {
                0 -> 0
                -1 -> {
                    if (pos >= raw.size) {
                        return DecodeRun(start, packets, pos - start, null, -1, -1, pktStart, "missing varByte size for opcode $opcode", isaacIndex)
                    }
                    val s = raw[pos].toInt() and 0xFF; pos++; s
                }
                -2 -> {
                    if (pos + 1 >= raw.size) {
                        return DecodeRun(start, packets, pos - start, null, -1, -1, pktStart, "missing varShort size for opcode $opcode", isaacIndex)
                    }
                    val s = ((raw[pos].toInt() and 0xFF) shl 8) or (raw[pos + 1].toInt() and 0xFF); pos += 2; s
                }
                else -> sizeMode
            }

            if (pos + size > raw.size) {
                return DecodeRun(
                    start,
                    packets,
                    pos - start,
                    null,
                    -1,
                    -1,
                    pktStart,
                    "opcode $opcode payload needs ${size}B but only ${raw.size - pos}B remain",
                    isaacIndex,
                )
            }

            val payload = raw.copyOfRange(pos, pos + size)
            pos += size

            val name = if (isServer) codec.serverProtName(opcode) else codec.clientProtName(opcode)
            val kind = when (sizeMode) { -1 -> "varByte"; -2 -> "varShort"; else -> "fixed" }
            packets.add(DecodedPacket(opcode, name, kind, size, payload, pktStart, isaacIndex))
        }
        return DecodeRun(start, packets, pos - start, null, -1, -1, null, "", -1)
    }

    private fun isKnownOpcode(opcode: Int, isServer: Boolean, maxOpcode: Int): Boolean {
        if (opcode < 0 || opcode >= maxOpcode) return false
        return if (isServer) {
            codec.serverProtInfo.containsKey(opcode)
        } else {
            codec.clientProtInfo.containsKey(opcode)
        }
    }

    // -----------------------------------------------------------------------
    // JSON emit helpers.
    // -----------------------------------------------------------------------

    private fun packetJson(conn: Connection, dir: String, seq: Int, p: DecodedPacket): String = Json.obj(
        "dir" to dir,
        "fd" to conn.fd,
        "conn" to conn.role.name.lowercase(),
        "seq" to seq,
        "opcode" to p.opcode,
        "name" to p.name,
        "size_kind" to p.sizeKind,
        "size" to p.size,
        "at_byte" to p.atByte,
        "isaac_index" to p.isaacIndex,
        "payload_hex" to Json.hex(p.payload, Int.MAX_VALUE)
    )

    private fun loginEvt(conn: Connection, dir: String, event: String, fields: Map<String, Any?>): String {
        val base = linkedMapOf<String, Any?>(
            "record" to "login_event", "dir" to dir, "fd" to conn.fd,
            "conn" to conn.role.name.lowercase(), "event" to event
        )
        base.putAll(fields)
        return Json.obj(*base.entries.map { it.key to it.value }.toTypedArray())
    }

    private fun serverClientVarEntry(
        conn: Connection,
        index: Int,
        atByte: Int,
        id: Int,
        type: String,
        value: Any,
        encodedSize: Int,
    ): String = Json.obj(
        "record" to "server_client_var_entry",
        "dir" to "S2C",
        "fd" to conn.fd,
        "conn" to conn.role.name.lowercase(),
        "index" to index,
        "id" to id,
        "type" to type,
        "value" to value,
        "encoded_size" to encodedSize,
        "at_byte" to atByte,
    )

    private fun serverClientVarError(conn: Connection, index: Int, atByte: Int, reason: String): String = Json.obj(
        "record" to "server_client_var_decode_error",
        "dir" to "S2C",
        "fd" to conn.fd,
        "conn" to conn.role.name.lowercase(),
        "index" to index,
        "at_byte" to atByte,
        "reason" to reason,
    )

    private fun beInt(b: ByteArray, off: Int): Int =
        ((b[off].toInt() and 0xFF) shl 24) or
            ((b[off + 1].toInt() and 0xFF) shl 16) or
            ((b[off + 2].toInt() and 0xFF) shl 8) or
            (b[off + 3].toInt() and 0xFF)

    private fun beLong(b: ByteArray, off: Int): Long {
        var v = 0L
        for (i in 0 until 8) v = (v shl 8) or (b[off + i].toLong() and 0xFF)
        return v
    }

    private fun clientVarType(id: Int): Char? =
        clientVariableDefinitions.getOrNull(id)?.aChar3210

    companion object {
        private const val SERVER_PROT_COUNT = 218
        private const val CLIENT_PROT_COUNT = 256

        private val clientVariableDefinitions by lazy {
            ClientVariableParameterDecoder().load(Cache.get())
        }
    }
}
