package org.darkan.tools.recorder

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import org.darkan.core.net.prot.Codec
import org.darkan.core.net.recorder.ClientStateCrossCheck
import java.io.File
import java.util.Base64

/**
 * The **client-is-king** trust section. Reads the captured s2c packets, decodes the *state-bearing*
 * ones via the core [ClientStateCrossCheck] (varps, skills, run energy — same wire formats register948
 * serves), folds them into the EXPECTED client state, and compares that to the client's OWN final
 * state snapshot (`state-snapshots.jsonl`, last line = authoritative). The client is the ground
 * truth: a varp/skill/scalar disagreement, or a varp the client holds that we never decoded, is a
 * REAL correctness failure (the verifier escalates it to FAIL).
 *
 * This is the headline upgrade over the verifier's self-consistency proofs: byte-accounting proves we
 * *framed* every byte, clean-decode proves our decoders don't *throw* — but only this comparison
 * proves the *values* our decoders produce are the values the client actually committed. A decoder
 * can be byte-clean and still wrong; the client's committed state is the only oracle that catches it.
 *
 * When `state-snapshots.jsonl` is absent (older captures, before the recorder dylib emitted it) the
 * section reports "client cross-check unavailable — no snapshot (run a fresh capture)" and is NOT a
 * failure: completeness then rests on the other three proofs.
 */
class ClientStateCrossChecker(private val codec: Codec) {

    private val json = Json { ignoreUnknownKeys = true }
    private val base64 = Base64.getDecoder()

    /** The rendered result: the structured [ClientStateCrossCheck.CrossCheck] plus a one-line summary. */
    data class Result(val check: ClientStateCrossCheck.CrossCheck, val summary: String) {
        val available: Boolean get() = check.available
        /** True when a real client-vs-decode disagreement (or a dropped state packet) was found. */
        val hasFailure: Boolean get() = check.hasFailure
    }

    /**
     * Run the cross-check for [session]. Builds expected state from `framed-s2c.jsonl` (the client's
     * own decoded packets — already the right opcode/body), reads the at-exit snapshot, compares.
     */
    fun crossCheck(session: File): Result {
        val snapshot = readSnapshot(session)
            ?: return Result(
                ClientStateCrossCheck.CrossCheck.unavailable(SNAPSHOT_ABSENT),
                "Client cross-check unavailable — no `state-snapshots.jsonl` snapshot (run a fresh capture).",
            )

        val packets = readStateBearingS2c(session)
        val expected = ClientStateCrossCheck.buildExpected(packets, codec)
        val check = ClientStateCrossCheck.compare(expected, snapshot)
        return Result(check, summarize(check, expected, snapshot))
    }

    // ---- read the at-exit client snapshot --------------------------------------------------------

    /**
     * The authoritative snapshot is the LAST `kind == "state_snapshot"` line in
     * `state-snapshots.jsonl` (the recorder writes one at exit; intermediate snapshots may be
     * partial). Returns null when the file is absent or has no usable snapshot line.
     */
    private fun readSnapshot(session: File): ClientStateCrossCheck.Snapshot? {
        val file = File(session, SNAPSHOT_FILE)
        if (!file.exists()) return null
        var last: JsonObject? = null
        file.forEachLine { rawLine ->
            val line = rawLine.trim()
            if (line.isEmpty()) return@forEachLine
            val obj = runCatching { json.parseToJsonElement(line) as? JsonObject }.getOrNull() ?: return@forEachLine
            if (obj["kind"]?.jsonPrimitive?.contentOrNull == "state_snapshot") last = obj
        }
        val obj = last ?: return null
        return parseSnapshot(obj)
    }

    private fun parseSnapshot(obj: JsonObject): ClientStateCrossCheck.Snapshot {
        val mainState = obj["main_state"]?.jsonPrimitive?.intOrNull

        val varps = LinkedHashMap<Int, Int>()
        (obj["varps"] as? JsonObject)?.forEach { (k, v) ->
            val id = k.toIntOrNull() ?: return@forEach
            val value = v.jsonPrimitive.intOrNull ?: return@forEach
            varps[id] = value
        }

        val player = (obj["player"] as? JsonObject)?.let { p ->
            val x = p["x"]?.jsonPrimitive?.intOrNull
            val y = p["y"]?.jsonPrimitive?.intOrNull
            val plane = p["plane"]?.jsonPrimitive?.intOrNull ?: 0
            if (x != null && y != null) ClientStateCrossCheck.Tile(x, y, plane) else null
        }

        val skills = LinkedHashMap<Int, ClientStateCrossCheck.SkillSnapshot>()
        (obj["skills"] as? JsonArray)?.forEach { el ->
            val s = el as? JsonObject ?: return@forEach
            val id = s["id"]?.jsonPrimitive?.intOrNull ?: return@forEach
            val level = s["level"]?.jsonPrimitive?.intOrNull ?: 0
            val base = s["base"]?.jsonPrimitive?.intOrNull ?: level
            val xp = s["xp"]?.jsonPrimitive?.intOrNull ?: 0
            skills[id] = ClientStateCrossCheck.SkillSnapshot(id, level, base, xp)
        }

        val runEnergy = obj["run_energy"]?.jsonPrimitive?.intOrNull
        val runWeight = obj["run_weight"]?.jsonPrimitive?.intOrNull
        return ClientStateCrossCheck.Snapshot(mainState, varps, player, skills, runEnergy, runWeight)
    }

    // ---- read the state-bearing s2c packets from the framed plane --------------------------------

    /**
     * Pull the state-bearing s2c packets (in file order) from `framed-s2c.jsonl`. We read the framed
     * plane (the opcode/body the CLIENT itself resolved) so the comparison reflects exactly what the
     * client decoded, and we only keep the families [ClientStateCrossCheck] models.
     */
    private fun readStateBearingS2c(session: File): List<ClientStateCrossCheck.S2cPacket> {
        val file = File(session, "framed-s2c.jsonl")
        if (!file.exists()) return emptyList()
        val blobsDir = File(session, "blobs")
        val out = ArrayList<ClientStateCrossCheck.S2cPacket>()
        file.forEachLine { rawLine ->
            val line = rawLine.trim()
            if (line.isEmpty()) return@forEachLine
            val obj = runCatching { json.parseToJsonElement(line) as? JsonObject }.getOrNull() ?: return@forEachLine
            val op = obj["op"]?.jsonPrimitive?.intOrNull ?: return@forEachLine
            if (op !in ClientStateCrossCheck.STATE_BEARING_OPCODES) return@forEachLine
            val body = resolveBody(obj, blobsDir) ?: return@forEachLine
            out += ClientStateCrossCheck.S2cPacket(op, body)
        }
        return out
    }

    private fun resolveBody(obj: JsonObject, blobsDir: File): ByteArray? {
        obj["body"]?.jsonPrimitive?.contentOrNull?.let { b64 ->
            return runCatching { base64.decode(b64) }.getOrNull()
        }
        obj["body_ref"]?.jsonPrimitive?.contentOrNull?.let { ref ->
            val f = if (File(ref).isAbsolute) File(ref) else File(blobsDir, File(ref).name)
            if (f.exists()) return runCatching { f.readBytes() }.getOrNull()
        }
        return null
    }

    // ---- summary line ----------------------------------------------------------------------------

    private fun summarize(
        check: ClientStateCrossCheck.CrossCheck,
        expected: ClientStateCrossCheck.ExpectedState,
        snapshot: ClientStateCrossCheck.Snapshot,
    ): String {
        val sb = StringBuilder("Client-is-king cross-check (the client's final state is ground truth):\n")
        sb.append("  - VARPS: ${check.varpMatches} match")
        if (check.varpMismatches.isNotEmpty()) {
            sb.append(", ${check.varpMismatches.size} MISMATCH [")
            sb.append(check.varpMismatches.take(MAX_LIST).joinToString { "var${it.varId}: ours=${it.ourValue} client=${it.clientValue}" })
            if (check.varpMismatches.size > MAX_LIST) sb.append(", …")
            sb.append("]")
        }
        if (check.varpMissed.isNotEmpty()) {
            sb.append(", ${check.varpMissed.size} MISSED (client holds, we never set) [")
            sb.append(check.varpMissed.take(MAX_LIST).joinToString { "var$it" })
            if (check.varpMissed.size > MAX_LIST) sb.append(", …")
            sb.append("]")
        }
        if (check.varpExtra.isNotEmpty()) sb.append(", ${check.varpExtra.size} extra (advisory)")
        sb.append(" — we decoded ${expected.varps.size} varps vs ${snapshot.varps.size} in the snapshot\n")

        sb.append("  - SKILLS: ${check.skillMatches} match")
        if (check.skillMismatches.isNotEmpty()) {
            sb.append(", ${check.skillMismatches.size} MISMATCH [")
            sb.append(check.skillMismatches.take(MAX_LIST).joinToString { "skill${it.skillId}.${it.field}: ours=${it.ourValue} client=${it.clientValue}" })
            sb.append("]")
        }
        if (check.skillMissed.isNotEmpty()) sb.append(", ${check.skillMissed.size} MISSED")
        sb.append("\n")

        val energyLine = when {
            expected.runEnergy == null -> "no UPDATE_RUNENERGY decoded"
            snapshot.runEnergy == null -> "decoded ${expected.runEnergy}; snapshot has none"
            check.scalarMismatches.any { it.field == "runEnergy" } ->
                "MISMATCH ours=${expected.runEnergy} client=${snapshot.runEnergy}"
            else -> "match (${expected.runEnergy})"
        }
        sb.append("  - RUN ENERGY: $energyLine\n")

        val tileScalar = check.scalarMismatches.firstOrNull { it.field == "playerTile" }
        val clientTile = snapshot.player
        sb.append("  - PLAYER TILE: ")
        sb.append(
            when {
                tileScalar != null -> "MISMATCH ours=${tileScalar.ourValue} client=${tileScalar.clientValue}"
                clientTile == null -> "snapshot carried no player tile"
                else -> "no position/coord packet decoded offline (snapshot tile=${clientTile.x},${clientTile.y},${clientTile.plane})"
            }
        )
        if (check.decodeFailures > 0) sb.append("\n  - NOTE: ${check.decodeFailures} state-bearing body(ies) failed to decode against the documented wire format")

        sb.append("\n  => ")
        sb.append(
            if (check.hasFailure) {
                "FAIL: our decoded state does not reproduce the client's committed state (client is authoritative)."
            } else {
                "OK: every value we decoded matches the client's own final state."
            }
        )
        return sb.toString()
    }

    companion object {
        const val SNAPSHOT_FILE = "state-snapshots.jsonl"
        const val SNAPSHOT_ABSENT = "no state-snapshots.jsonl"
        private const val MAX_LIST = 12
    }
}
