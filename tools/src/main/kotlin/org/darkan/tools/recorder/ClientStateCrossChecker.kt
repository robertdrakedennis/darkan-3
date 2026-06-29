package org.darkan.tools.recorder

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import org.darkan.core.net.prot.Codec
import org.darkan.core.net.recorder.ClientStateCrossCheck
import java.io.File
import java.util.Base64

/**
 * The **client-is-king** trust section. Reads the captured s2c packets, decodes the *state-bearing*
 * ones via the core [ClientStateCrossCheck] (varps, skills, run energy — same wire formats register948
 * serves), folds them into the EXPECTED client state, and compares that to the client's OWN final
 * state snapshot (`state-snapshots.jsonl`). Persistent state (varps, skills, run energy, tile) uses
 * the final snapshot. Varcs are transient in the recorder oracle: each post-handler snapshot carries
 * the client's active varc update tree, which drains after the burst, so varc ground truth is the
 * ordered union across every snapshot, last-write-wins by `(recordKind,varId)`. The client is the
 * ground truth: a varp/skill/scalar/varc disagreement, or state the client holds that we never
 * decoded, is a REAL correctness failure (the verifier escalates it to FAIL).
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
    private val gamevals = GamevalNameResolver.default()

    /** One local-player tile sampled from the recorder's oracle snapshots. */
    data class AvatarTrajectoryPoint(
        val tick: Int?,
        val monoUs: Long?,
        val tile: ClientStateCrossCheck.Tile,
    )

    /** The rendered result: the structured [ClientStateCrossCheck.CrossCheck] plus a one-line summary. */
    data class Result(
        val check: ClientStateCrossCheck.CrossCheck,
        val summary: String,
        val avatarTrajectory: List<AvatarTrajectoryPoint>,
    ) {
        val available: Boolean get() = check.available
        /** True when a real client-vs-decode disagreement (or a dropped state packet) was found. */
        val hasFailure: Boolean get() = check.hasFailure
    }

    /**
     * Run the cross-check for [session]. Builds expected state from `framed-s2c.jsonl` (the client's
     * own decoded packets — already the right opcode/body), reads the at-exit snapshot, compares.
     */
    fun crossCheck(session: File): Result {
        val resetTimes = readVarcResetTimes(session)
        val avatarTrajectory = readAvatarTrajectory(session)
        val snapshotRead = readSnapshot(session, resetTimes)
            ?: return Result(
                ClientStateCrossCheck.CrossCheck.unavailable(SNAPSHOT_ABSENT),
                "Client cross-check unavailable — no `state-snapshots.jsonl` snapshot (run a fresh capture).",
                emptyList(),
            )
        val snapshot = snapshotRead.snapshot

        val localTimeline = readLocalTimeline(session)
        val packets = readStateBearingS2c(session, localTimeline, snapshotRead.monoUs)
        val expected = ClientStateCrossCheck.buildExpected(packets, codec)
        val dynamicVarpEvidence = readDynamicVarpEvidence(session, packets, expected, snapshot)
        val check = ClientStateCrossCheck.compare(expected, snapshot, dynamicVarpEvidence = dynamicVarpEvidence)
        return Result(check, summarize(check, expected, snapshot), avatarTrajectory)
    }

    // ---- read the client snapshot/oracle state ---------------------------------------------------

    private data class SnapshotRead(
        val snapshot: ClientStateCrossCheck.Snapshot,
        val monoUs: Long?,
    )

    /**
     * Persistent domains come from the LAST `kind == "state_snapshot"` line. Varcs are captured from
     * the client's transient active update tree and may drain before the final line, so accumulate
     * varc/varcstring entries across ALL snapshots in file order, with later snapshots overwriting
     * earlier values for the same `(recordKind,varId)`. The packet fold clears expected varcs on op5
     * RESET_CLIENT_VARCACHE, so the oracle union also clears when snapshot time crosses a captured
     * reset packet's `mono_us`.
     */
    private fun readSnapshot(session: File, resetTimes: List<Long>): SnapshotRead? {
        val file = File(session, SNAPSHOT_FILE)
        if (!file.exists()) return null
        var last: JsonObject? = null
        val varcs = LinkedHashMap<ClientStateCrossCheck.VarcKey, ClientStateCrossCheck.VarcNumberState>()
        val varcStrings = LinkedHashMap<ClientStateCrossCheck.VarcKey, ClientStateCrossCheck.VarcStringState>()
        var varcsAvailable = false
        var varcStringsAvailable = false
        var nextReset = 0
        file.forEachLine { rawLine ->
            val line = rawLine.trim()
            if (line.isEmpty()) return@forEachLine
            val obj = runCatching { json.parseToJsonElement(line) as? JsonObject }.getOrNull() ?: return@forEachLine
            if (obj["kind"]?.jsonPrimitive?.contentOrNull != "state_snapshot") return@forEachLine
            last = obj
            val snapshotTime = obj["mono_us"]?.jsonPrimitive?.longOrNull
            if (snapshotTime != null) {
                while (nextReset < resetTimes.size && resetTimes[nextReset] <= snapshotTime) {
                    varcs.clear()
                    varcStrings.clear()
                    nextReset++
                }
            }
            (obj["varcs"] as? JsonArray)?.let { array ->
                varcsAvailable = true
                parseVarcs(array).forEach { state -> varcs[state.key] = state }
            }
            (obj["varcstrings"] as? JsonArray)?.let { array ->
                varcStringsAvailable = true
                parseVarcStrings(array).forEach { state -> varcStrings[state.key] = state }
            }
        }
        val obj = last ?: return null
        return SnapshotRead(
            snapshot = parseSnapshot(
                obj = obj,
                accumulatedVarcs = varcs,
                accumulatedVarcStrings = varcStrings,
                varcsAvailable = varcsAvailable,
                varcStringsAvailable = varcStringsAvailable,
            ),
            monoUs = obj["mono_us"]?.jsonPrimitive?.longOrNull,
        )
    }

    private fun readVarcResetTimes(session: File): List<Long> {
        val file = File(session, "framed-s2c.jsonl")
        if (!file.exists()) return emptyList()
        val out = ArrayList<Long>()
        file.forEachLine { rawLine ->
            val line = rawLine.trim()
            if (line.isEmpty()) return@forEachLine
            val obj = runCatching { json.parseToJsonElement(line) as? JsonObject }.getOrNull() ?: return@forEachLine
            val op = obj["op"]?.jsonPrimitive?.intOrNull ?: return@forEachLine
            if (op != ClientStateCrossCheck.OP_RESET_CLIENT_VARCACHE) return@forEachLine
            obj["mono_us"]?.jsonPrimitive?.longOrNull?.let { out += it }
        }
        return out.sorted()
    }

    private data class VarpWrite(
        val id: Int,
        val value: Int,
        val opcode: Int,
        val monoUs: Long?,
    )

    private data class VarpSnapshotPoint(
        val monoUs: Long,
        val tick: Int?,
        val value: Int,
    )

    private fun readDynamicVarpEvidence(
        session: File,
        packets: List<ClientStateCrossCheck.S2cPacket>,
        expected: ClientStateCrossCheck.ExpectedState,
        snapshot: ClientStateCrossCheck.Snapshot,
    ): Map<Int, String> {
        val dynamicIds = ClientStateCrossCheck.CLIENT_DYNAMIC_VARPS
        val lastWrites = LinkedHashMap<Int, VarpWrite>()
        packets.mapNotNull { decodeVarpWrite(it) }
            .filter { it.id in dynamicIds }
            .forEach { lastWrites[it.id] = it }
        if (lastWrites.isEmpty()) return emptyMap()

        val history = readVarpHistory(session, dynamicIds)
        val evidence = LinkedHashMap<Int, String>()
        for (id in dynamicIds) {
            val expectedValue = expected.varps[id] ?: continue
            val clientValue = snapshot.varps[id] ?: continue
            if (expectedValue == clientValue) continue
            val write = lastWrites[id] ?: continue
            if (write.value != expectedValue) continue
            val points = history[id].orEmpty()
                .filter { write.monoUs == null || it.monoUs >= write.monoUs }
                .sortedBy { it.monoUs }
            val matchedIndex = points.indexOfFirst { it.value == expectedValue }
            if (matchedIndex < 0) continue
            val drift = points.drop(matchedIndex + 1).firstOrNull { it.value != expectedValue } ?: continue
            val final = points.lastOrNull() ?: continue
            if (final.value != clientValue) continue
            val match = points[matchedIndex]
            evidence[id] = "op${write.opcode} wrote $expectedValue at mono_us=${write.monoUs ?: "unknown"}; " +
                "snapshot ${formatTick(match)} matched; snapshot ${formatTick(drift)} drifted to ${drift.value} " +
                "without another varp write; final ${formatTick(final)}=$clientValue"
        }
        return evidence
    }

    private fun readVarpHistory(
        session: File,
        ids: Set<Int>,
    ): Map<Int, List<VarpSnapshotPoint>> {
        val file = File(session, SNAPSHOT_FILE)
        if (!file.exists()) return emptyMap()
        val out = ids.associateWith { ArrayList<VarpSnapshotPoint>() }
        file.forEachLine { rawLine ->
            val line = rawLine.trim()
            if (line.isEmpty()) return@forEachLine
            val obj = runCatching { json.parseToJsonElement(line) as? JsonObject }.getOrNull() ?: return@forEachLine
            if (obj["kind"]?.jsonPrimitive?.contentOrNull != "state_snapshot") return@forEachLine
            val monoUs = obj["mono_us"]?.jsonPrimitive?.longOrNull ?: return@forEachLine
            val tick = obj["tick"]?.jsonPrimitive?.intOrNull
            val varps = obj["varps"] as? JsonObject ?: return@forEachLine
            for (id in ids) {
                val value = varps[id.toString()]?.jsonPrimitive?.intOrNull ?: continue
                out.getValue(id) += VarpSnapshotPoint(monoUs, tick, value)
            }
        }
        return out
    }

    private fun decodeVarpWrite(packet: ClientStateCrossCheck.S2cPacket): VarpWrite? {
        val body = packet.body
        return when (packet.opcode) {
            ClientStateCrossCheck.OP_VARP_SMALL -> {
                if (body.size < 3) return null
                val id = body.u16(0)
                val raw = (-128 - body.u8(2)) and 0xff
                VarpWrite(id, raw.toByte().toInt(), packet.opcode, packet.monoUs)
            }
            ClientStateCrossCheck.OP_VARP_LARGE -> {
                if (body.size < 6) return null
                VarpWrite(body.u16(4), body.i32(0), packet.opcode, packet.monoUs)
            }
            ClientStateCrossCheck.OP_VARP_LONG -> {
                if (body.size < 10) return null
                val id = (body.u8(0) shl 8) or ((body.u8(1) + 128) and 0xff)
                VarpWrite(id, body.g4Alt3(6), packet.opcode, packet.monoUs)
            }
            else -> null
        }
    }

    private fun formatTick(point: VarpSnapshotPoint): String =
        point.tick?.let { "tick$it" } ?: "mono_us=${point.monoUs}"

    private fun ByteArray.u8(offset: Int): Int = this[offset].toInt() and 0xff

    private fun ByteArray.u16(offset: Int): Int = (u8(offset) shl 8) or u8(offset + 1)

    private fun ByteArray.i32(offset: Int): Int =
        (u8(offset) shl 24) or (u8(offset + 1) shl 16) or (u8(offset + 2) shl 8) or u8(offset + 3)

    private fun ByteArray.g4Alt3(offset: Int): Int =
        (u8(offset + 1) shl 24) or (u8(offset) shl 16) or (u8(offset + 3) shl 8) or u8(offset + 2)

    private fun parseSnapshot(
        obj: JsonObject,
        accumulatedVarcs: Map<ClientStateCrossCheck.VarcKey, ClientStateCrossCheck.VarcNumberState>,
        accumulatedVarcStrings: Map<ClientStateCrossCheck.VarcKey, ClientStateCrossCheck.VarcStringState>,
        varcsAvailable: Boolean,
        varcStringsAvailable: Boolean,
    ): ClientStateCrossCheck.Snapshot {
        val mainState = obj["main_state"]?.jsonPrimitive?.intOrNull

        val varps = LinkedHashMap<Int, Int>()
        (obj["varps"] as? JsonObject)?.forEach { (k, v) ->
            val id = k.toIntOrNull() ?: return@forEach
            val value = v.jsonPrimitive.intOrNull ?: return@forEach
            varps[id] = value
        }

        val player = parseLocalPlayerTile(obj)

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
        return ClientStateCrossCheck.Snapshot(
            mainState = mainState,
            varps = varps,
            varcs = accumulatedVarcs,
            varcStrings = accumulatedVarcStrings,
            varcsAvailable = varcsAvailable,
            varcStringsAvailable = varcStringsAvailable,
            inventories = parseInventories(obj["inventories"] as? JsonArray),
            players = parseScenePlayers(obj["players"] as? JsonArray),
            npcs = parseSceneNpcs(obj["npcs"] as? JsonArray),
            appearance = parseAppearance(obj["appearance"] as? JsonArray),
            appearanceAvailable = obj["appearance"] is JsonArray,
            player = player,
            skills = skills,
            runEnergy = runEnergy,
            runWeight = runWeight,
        )
    }

    private fun parseLocalPlayerTile(obj: JsonObject): ClientStateCrossCheck.Tile? {
        (obj["player"] as? JsonObject)?.let { p ->
            val x = p["x"]?.jsonPrimitive?.intOrNull
            val y = p["y"]?.jsonPrimitive?.intOrNull
            val plane = p["plane"]?.jsonPrimitive?.intOrNull ?: 0
            if (x != null && y != null) return ClientStateCrossCheck.Tile(x, y, plane)
        }
        val renderTile = ((obj["local_player"] as? JsonObject)?.get("render_tile") as? JsonObject) ?: return null
        val x = renderTile["x"]?.jsonPrimitive?.intOrNull
        val y = renderTile["y"]?.jsonPrimitive?.intOrNull
        val plane = renderTile["plane"]?.jsonPrimitive?.intOrNull ?: 0
        return if (x != null && y != null) ClientStateCrossCheck.Tile(x, y, plane) else null
    }

    private fun parseLocalPlayerIndex(obj: JsonObject): Int? =
        (obj["local_player"] as? JsonObject)
            ?.get("server_index")
            ?.jsonPrimitive
            ?.intOrNull
            ?.takeIf { it in 1 until 2048 }

    private fun readAvatarTrajectory(session: File): List<AvatarTrajectoryPoint> {
        val file = File(session, SNAPSHOT_FILE)
        if (!file.exists()) return emptyList()
        val ticked = LinkedHashMap<Int, AvatarTrajectoryPoint>()
        val unticked = ArrayList<AvatarTrajectoryPoint>()
        file.forEachLine { rawLine ->
            val line = rawLine.trim()
            if (line.isEmpty()) return@forEachLine
            val obj = runCatching { json.parseToJsonElement(line) as? JsonObject }.getOrNull() ?: return@forEachLine
            if (obj["kind"]?.jsonPrimitive?.contentOrNull != "state_snapshot") return@forEachLine
            val tile = parseLocalPlayerTile(obj) ?: return@forEachLine
            val tick = obj["tick"]?.jsonPrimitive?.intOrNull
            val monoUs = obj["mono_us"]?.jsonPrimitive?.longOrNull
            val point = AvatarTrajectoryPoint(tick, monoUs, tile)
            if (tick != null) {
                ticked[tick] = point
            } else {
                unticked += point
            }
        }
        return if (ticked.isNotEmpty()) ticked.values.toList() else unticked
    }

    private fun parseScenePlayers(array: JsonArray?): Map<Int, ClientStateCrossCheck.ScenePlayerState> {
        if (array == null) return emptyMap()
        val out = LinkedHashMap<Int, ClientStateCrossCheck.ScenePlayerState>()
        array.forEach { el ->
            val p = el as? JsonObject ?: return@forEach
            val idx = p["idx"]?.jsonPrimitive?.intOrNull ?: return@forEach
            val x = p["x"]?.jsonPrimitive?.intOrNull
            val y = p["y"]?.jsonPrimitive?.intOrNull
            val plane = p["plane"]?.jsonPrimitive?.intOrNull ?: 0
            out[idx] = ClientStateCrossCheck.ScenePlayerState(
                idx = idx,
                tile = if (x != null && y != null) ClientStateCrossCheck.Tile(x, y, plane) else null,
            )
        }
        return out
    }

    private fun parseSceneNpcs(array: JsonArray?): Map<Int, ClientStateCrossCheck.SceneNpcState> {
        if (array == null) return emptyMap()
        val out = LinkedHashMap<Int, ClientStateCrossCheck.SceneNpcState>()
        array.forEach { el ->
            val n = el as? JsonObject ?: return@forEach
            val idx = n["idx"]?.jsonPrimitive?.intOrNull ?: return@forEach
            val type = n["type"]?.jsonPrimitive?.intOrNull
            val x = n["x"]?.jsonPrimitive?.intOrNull
            val y = n["y"]?.jsonPrimitive?.intOrNull
            val plane = n["plane"]?.jsonPrimitive?.intOrNull ?: 0
            out[idx] = ClientStateCrossCheck.SceneNpcState(
                idx = idx,
                typeId = type,
                tile = if (x != null && y != null) ClientStateCrossCheck.Tile(x, y, plane) else null,
            )
        }
        return out
    }

    private fun parseInventories(array: JsonArray?): Map<Int, ClientStateCrossCheck.InventoryState> {
        if (array == null) return emptyMap()
        val out = LinkedHashMap<Int, ClientStateCrossCheck.InventoryState>()
        array.forEach { el ->
            val inv = el as? JsonObject ?: return@forEach
            val invId = inv["invId"]?.jsonPrimitive?.intOrNull ?: return@forEach
            val slots = LinkedHashMap<Int, ClientStateCrossCheck.InventorySlotState>()
            (inv["slots"] as? JsonArray)?.forEach { slotEl ->
                val slotObj = slotEl as? JsonObject ?: return@forEach
                val slot = slotObj["slot"]?.jsonPrimitive?.intOrNull ?: return@forEach
                val item = slotObj["item"]?.jsonPrimitive?.intOrNull ?: return@forEach
                val count = slotObj["count"]?.jsonPrimitive?.longOrNull ?: return@forEach
                if (item != -1) {
                    slots[slot] = ClientStateCrossCheck.InventorySlotState(invId, slot, item, count)
                }
            }
            out[invId] = ClientStateCrossCheck.InventoryState(invId, slots)
        }
        return out
    }

    private fun parseAppearance(array: JsonArray?): Map<Int, ClientStateCrossCheck.AppearanceSlotState> {
        if (array == null) return emptyMap()
        val out = LinkedHashMap<Int, ClientStateCrossCheck.AppearanceSlotState>()
        array.forEachIndexed { index, el ->
            val slotObj = el as? JsonObject ?: return@forEachIndexed
            val slot = slotObj["slot"]?.jsonPrimitive?.intOrNull ?: index
            val kitId = slotObj["kitId"]?.jsonPrimitive?.intOrNull ?: -1
            val itemId = slotObj["itemId"]?.jsonPrimitive?.intOrNull ?: -1
            out[slot] = ClientStateCrossCheck.AppearanceSlotState(slot, kitId, itemId)
        }
        return out
    }

    private fun parseVarcs(array: JsonArray): List<ClientStateCrossCheck.VarcNumberState> {
        val out = ArrayList<ClientStateCrossCheck.VarcNumberState>()
        array.forEach { el ->
            val v = el as? JsonObject ?: return@forEach
            val kind = v["kind"]?.jsonPrimitive?.intOrNull ?: return@forEach
            val id = v["id"]?.jsonPrimitive?.intOrNull ?: return@forEach
            val valueKind = v["value_kind"]?.jsonPrimitive?.intOrNull ?: ClientStateCrossCheck.VARC_VALUE_KIND_INT32
            val value = v["val"]?.jsonPrimitive?.longOrNull ?: return@forEach
            val key = ClientStateCrossCheck.VarcKey(kind, id)
            out += ClientStateCrossCheck.VarcNumberState(key, valueKind, value)
        }
        return out
    }

    private fun parseVarcStrings(array: JsonArray): List<ClientStateCrossCheck.VarcStringState> {
        val out = ArrayList<ClientStateCrossCheck.VarcStringState>()
        array.forEach { el ->
            val v = el as? JsonObject ?: return@forEach
            val kind = v["kind"]?.jsonPrimitive?.intOrNull ?: return@forEach
            val id = v["id"]?.jsonPrimitive?.intOrNull ?: return@forEach
            val valueKind = v["value_kind"]?.jsonPrimitive?.intOrNull ?: ClientStateCrossCheck.VARC_VALUE_KIND_STRING
            val value = v["str"]?.jsonPrimitive?.contentOrNull ?: return@forEach
            val key = ClientStateCrossCheck.VarcKey(kind, id)
            out += ClientStateCrossCheck.VarcStringState(key, valueKind, value)
        }
        return out
    }

    // ---- read the state-bearing s2c packets from the framed plane --------------------------------

    /**
     * Pull the state-bearing s2c packets (in file order) from `framed-s2c.jsonl`. We read the framed
     * plane (the opcode/body the CLIENT itself resolved) so the comparison reflects exactly what the
     * client decoded, and we only keep the families [ClientStateCrossCheck] models.
     */
    private data class TimedLocal(
        val monoUs: Long,
        val tile: ClientStateCrossCheck.Tile?,
        val playerIndex: Int?,
    )

    private fun readLocalTimeline(session: File): List<TimedLocal> {
        val file = File(session, SNAPSHOT_FILE)
        if (!file.exists()) return emptyList()
        val out = ArrayList<TimedLocal>()
        file.forEachLine { rawLine ->
            val line = rawLine.trim()
            if (line.isEmpty()) return@forEachLine
            val obj = runCatching { json.parseToJsonElement(line) as? JsonObject }.getOrNull() ?: return@forEachLine
            if (obj["kind"]?.jsonPrimitive?.contentOrNull != "state_snapshot") return@forEachLine
            val monoUs = obj["mono_us"]?.jsonPrimitive?.longOrNull ?: return@forEachLine
            val tile = parseLocalPlayerTile(obj)
            val playerIndex = parseLocalPlayerIndex(obj)
            if (tile != null || playerIndex != null) out += TimedLocal(monoUs, tile, playerIndex)
        }
        return out.sortedBy { it.monoUs }
    }

    private fun localAtOrBefore(timeline: List<TimedLocal>, monoUs: Long?): TimedLocal? {
        if (monoUs == null || timeline.isEmpty()) return null
        var lo = 0
        var hi = timeline.size
        while (lo < hi) {
            val mid = (lo + hi) ushr 1
            if (timeline[mid].monoUs <= monoUs) lo = mid + 1 else hi = mid
        }
        return if (lo == 0) null else timeline[lo - 1]
    }

    private fun localTileAtOrBefore(timeline: List<TimedLocal>, monoUs: Long?): ClientStateCrossCheck.Tile? =
        localAtOrBefore(timeline, monoUs)?.tile

    private fun localPlayerIndexFor(timeline: List<TimedLocal>, monoUs: Long?): Int? {
        localAtOrBefore(timeline, monoUs)?.playerIndex?.let { return it }
        return timeline.firstOrNull { it.playerIndex != null }?.playerIndex
    }

    private fun readStateBearingS2c(
        session: File,
        localTimeline: List<TimedLocal>,
        untilMonoUs: Long?,
    ): List<ClientStateCrossCheck.S2cPacket> {
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
            val monoUs = obj["mono_us"]?.jsonPrimitive?.longOrNull
            if (untilMonoUs != null && monoUs != null && monoUs > untilMonoUs) return@forEachLine
            out += ClientStateCrossCheck.S2cPacket(
                opcode = op,
                body = body,
                monoUs = monoUs,
                localTile = localTileAtOrBefore(localTimeline, monoUs),
                localPlayerIndex = localPlayerIndexFor(localTimeline, monoUs),
            )
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
            sb.append(check.varpMismatches.take(MAX_LIST).joinToString { "${gamevals.varpDisplay(it.varId)}: ours=${it.ourValue} client=${it.clientValue}" })
            if (check.varpMismatches.size > MAX_LIST) sb.append(", …")
            sb.append("]")
        }
        if (check.varpAdvisory.isNotEmpty()) {
            sb.append(", ${check.varpAdvisory.size} advisory client-dynamic [")
            sb.append(check.varpAdvisory.take(MAX_LIST).joinToString {
                "${gamevals.varpDisplay(it.varId)}: ours=${it.ourValue} client=${it.clientValue} (${it.evidence})"
            })
            if (check.varpAdvisory.size > MAX_LIST) sb.append(", …")
            sb.append("]")
        }
        if (check.varpMissed.isNotEmpty()) {
            sb.append(", ${check.varpMissed.size} MISSED (client holds, we never set) [")
            sb.append(check.varpMissed.take(MAX_LIST).joinToString { gamevals.varpDisplay(it) })
            if (check.varpMissed.size > MAX_LIST) sb.append(", …")
            sb.append("]")
        }
        if (check.varpExtra.isNotEmpty()) sb.append(", ${check.varpExtra.size} extra (advisory)")
        sb.append(" — we decoded ${expected.varps.size} varps vs ${snapshot.varps.size} in the snapshot\n")

        sb.append("  - VARCS: ${check.varcMatches} match")
        if (check.varcMismatches.isNotEmpty()) {
            sb.append(", ${check.varcMismatches.size} MISMATCH [")
            sb.append(check.varcMismatches.take(MAX_LIST).joinToString {
                "ours=${gamevals.display(it.key, it.ourValueKind, it.ourNumberValue, it.ourStringValue)} " +
                    "client=${gamevals.display(it.key, it.clientValueKind, it.clientNumberValue, it.clientStringValue)}"
            })
            if (check.varcMismatches.size > MAX_LIST) sb.append(", …")
            sb.append("]")
        }
        if (check.varcMissed.isNotEmpty()) {
            sb.append(", ${check.varcMissed.size} MISSED [")
            sb.append(check.varcMissed.take(MAX_LIST).joinToString { gamevals.display(it) })
            if (check.varcMissed.size > MAX_LIST) sb.append(", …")
            sb.append("]")
        }
        if (check.varcUnverified.isNotEmpty()) {
            sb.append(", ${check.varcUnverified.size} UNVERIFIED (folded, no oracle ground truth) [")
            sb.append(check.varcUnverified.take(MAX_LIST).joinToString { gamevals.display(it) })
            if (check.varcUnverified.size > MAX_LIST) sb.append(", …")
            sb.append("]")
        }
        if (check.varcExtra.isNotEmpty()) sb.append(", ${check.varcExtra.size} extra (advisory)")
        sb.append(" — we decoded ${expected.varcs.size} numeric varcs vs ${snapshot.varcs.size} in the snapshot\n")

        sb.append("  - VARCSTRINGS: ${check.varcStringMatches} match")
        if (check.varcStringMismatches.isNotEmpty()) {
            sb.append(", ${check.varcStringMismatches.size} MISMATCH [")
            sb.append(check.varcStringMismatches.take(MAX_LIST).joinToString {
                "ours=${gamevals.display(it.key, it.ourValueKind, it.ourNumberValue, it.ourStringValue)} " +
                    "client=${gamevals.display(it.key, it.clientValueKind, it.clientNumberValue, it.clientStringValue)}"
            })
            if (check.varcStringMismatches.size > MAX_LIST) sb.append(", …")
            sb.append("]")
        }
        if (check.varcStringMissed.isNotEmpty()) {
            sb.append(", ${check.varcStringMissed.size} MISSED [")
            sb.append(check.varcStringMissed.take(MAX_LIST).joinToString { gamevals.display(it) })
            if (check.varcStringMissed.size > MAX_LIST) sb.append(", …")
            sb.append("]")
        }
        if (check.varcStringUnverified.isNotEmpty()) {
            sb.append(", ${check.varcStringUnverified.size} UNVERIFIED (folded, no oracle ground truth) [")
            sb.append(check.varcStringUnverified.take(MAX_LIST).joinToString { gamevals.display(it) })
            if (check.varcStringUnverified.size > MAX_LIST) sb.append(", …")
            sb.append("]")
        }
        if (check.varcStringExtra.isNotEmpty()) sb.append(", ${check.varcStringExtra.size} extra (advisory)")
        sb.append(" — we decoded ${expected.varcStrings.size} string varcs vs ${snapshot.varcStrings.size} in the snapshot\n")

        sb.append("  - SKILLS: ${check.skillMatches} match")
        if (check.skillMismatches.isNotEmpty()) {
            sb.append(", ${check.skillMismatches.size} MISMATCH [")
            sb.append(check.skillMismatches.take(MAX_LIST).joinToString { "skill${it.skillId}.${it.field}: ours=${it.ourValue} client=${it.clientValue}" })
            sb.append("]")
        }
        if (check.skillMissed.isNotEmpty()) sb.append(", ${check.skillMissed.size} MISSED")
        sb.append("\n")

        sb.append("  - INVENTORIES: ${check.inventoryMatches} match")
        if (check.inventoryMismatches.isNotEmpty()) {
            sb.append(", ${check.inventoryMismatches.size} MISMATCH [")
            sb.append(check.inventoryMismatches.take(MAX_LIST).joinToString {
                "inv${it.invId}[${it.slot}]: ours=${inventoryItemDisplay(it.ourItemId, it.ourCount)} " +
                    "client=${inventoryItemDisplay(it.clientItemId, it.clientCount)}"
            })
            if (check.inventoryMismatches.size > MAX_LIST) sb.append(", …")
            sb.append("]")
        }
        if (check.inventoryMissed.isNotEmpty()) {
            sb.append(", ${check.inventoryMissed.size} MISSED [")
            sb.append(check.inventoryMissed.take(MAX_LIST).joinToString { inventorySlotDisplay(it) })
            if (check.inventoryMissed.size > MAX_LIST) sb.append(", …")
            sb.append("]")
        }
        sb.append(" — we decoded ${expected.inventories.size} inventories/${expected.inventories.values.sumOf { it.slots.size }} occupied slots")
        sb.append(" vs ${snapshot.inventories.size} inventories/${snapshot.inventories.values.sumOf { it.slots.size }} occupied slots in the snapshot\n")

        sb.append("  - APPEARANCE: ${check.appearanceMatches} match")
        if (check.appearanceMismatches.isNotEmpty()) {
            sb.append(", ${check.appearanceMismatches.size} MISMATCH [")
            sb.append(check.appearanceMismatches.take(MAX_LIST).joinToString { appearanceMismatchDisplay(it) })
            if (check.appearanceMismatches.size > MAX_LIST) sb.append(", …")
            sb.append("]")
        }
        if (check.appearanceMissed.isNotEmpty()) {
            sb.append(", ${check.appearanceMissed.size} MISSED [")
            sb.append(check.appearanceMissed.take(MAX_LIST).joinToString { appearanceSlotDisplay(it) })
            if (check.appearanceMissed.size > MAX_LIST) sb.append(", …")
            sb.append("]")
        }
        if (check.appearanceUnverified.isNotEmpty()) sb.append(", ${check.appearanceUnverified.size} unverified")
        sb.append(" — decoded ${expected.appearance.size} slots vs ${snapshot.appearance.size} in the snapshot\n")

        sb.append("  - SCENE PLAYERS: ${check.scenePlayerPresenceMatches} presence match")
        if (check.scenePlayerPresenceMismatches.isNotEmpty()) {
            sb.append(", ${check.scenePlayerPresenceMismatches.size} PRESENCE MISMATCH [")
            sb.append(check.scenePlayerPresenceMismatches.take(MAX_LIST).joinToString { scenePresenceDisplay(it) })
            if (check.scenePlayerPresenceMismatches.size > MAX_LIST) sb.append(", …")
            sb.append("]")
        }
        if (check.scenePlayerPositionMismatches.isNotEmpty()) {
            sb.append(", ${check.scenePlayerPositionMismatches.size} POSITION MISMATCH [")
            sb.append(check.scenePlayerPositionMismatches.take(MAX_LIST).joinToString { scenePositionDisplay(it) })
            if (check.scenePlayerPositionMismatches.size > MAX_LIST) sb.append(", …")
            sb.append("]")
        }
        if (expected.scenePlayers.isEmpty() && snapshot.players.isNotEmpty()) sb.append(", op22 GPI external-player fold not yet proven")
        sb.append(" — folded ${expected.scenePlayers.size} players vs ${snapshot.players.size} in the snapshot\n")

        sb.append("  - SCENE NPCS: ${check.sceneNpcPresenceMatches} presence match, ${check.sceneNpcTypeMatches} type match")
        if (check.sceneNpcPresenceMismatches.isNotEmpty()) {
            sb.append(", ${check.sceneNpcPresenceMismatches.size} PRESENCE MISMATCH [")
            sb.append(check.sceneNpcPresenceMismatches.take(MAX_LIST).joinToString { scenePresenceDisplay(it) })
            if (check.sceneNpcPresenceMismatches.size > MAX_LIST) sb.append(", …")
            sb.append("]")
        }
        if (check.sceneNpcTypeMismatches.isNotEmpty()) {
            sb.append(", ${check.sceneNpcTypeMismatches.size} TYPE MISMATCH [")
            sb.append(check.sceneNpcTypeMismatches.take(MAX_LIST).joinToString {
                "npc${it.idx}: ours=${gamevals.npcDisplay(it.expectedTypeId)} client=${gamevals.npcDisplay(it.clientTypeId)}"
            })
            if (check.sceneNpcTypeMismatches.size > MAX_LIST) sb.append(", …")
            sb.append("]")
        }
        if (check.sceneNpcPositionMismatches.isNotEmpty()) {
            sb.append(", ${check.sceneNpcPositionMismatches.size} POSITION MISMATCH [")
            sb.append(check.sceneNpcPositionMismatches.take(MAX_LIST).joinToString { scenePositionDisplay(it) })
            if (check.sceneNpcPositionMismatches.size > MAX_LIST) sb.append(", …")
            sb.append("]")
        }
        sb.append(" — folded ${expected.sceneNpcs.size} NPCs vs ${snapshot.npcs.size} in the snapshot\n")

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

    private fun inventorySlotDisplay(slot: ClientStateCrossCheck.InventorySlotState): String =
        "inv${slot.invId}[${slot.slot}]=${inventoryItemDisplay(slot.itemId, slot.count)}"

    private fun inventoryItemDisplay(itemId: Int, count: Long): String =
        if (itemId == -1) "empty" else "${gamevals.objDisplay(itemId)}×$count"

    private fun appearanceSlotDisplay(slot: ClientStateCrossCheck.AppearanceSlotState): String =
        "slot${slot.slot}=${appearanceValueDisplay(slot.kitId, slot.itemId)}"

    private fun appearanceMismatchDisplay(m: ClientStateCrossCheck.AppearanceMismatch): String =
        "slot${m.slot}: ours=${appearanceValueDisplay(m.ourKitId, m.ourItemId)} " +
            "client=${appearanceValueDisplay(m.clientKitId, m.clientItemId)}"

    private fun appearanceValueDisplay(kitId: Int, itemId: Int): String =
        when {
            itemId >= 0 -> gamevals.objDisplay(itemId)
            kitId >= 0 -> "kit$kitId"
            else -> "empty"
        }

    private fun scenePresenceDisplay(m: ClientStateCrossCheck.ScenePresenceMismatch): String =
        if (m.family == "npc") {
            val type = m.expectedTypeId ?: m.clientTypeId
            "npc${m.idx}${type?.let { ":" + gamevals.npcDisplay(it) } ?: ""} expected=${m.expectedPresent} client=${m.clientPresent}"
        } else {
            "player${m.idx} expected=${m.expectedPresent} client=${m.clientPresent}"
        }

    private fun scenePositionDisplay(m: ClientStateCrossCheck.ScenePositionMismatch): String {
        val type = m.typeId?.let { ":" + gamevals.npcDisplay(it) } ?: ""
        return "${m.family}${m.idx}$type ours=(${m.expectedTile.x},${m.expectedTile.y},${m.expectedTile.plane}) " +
            "client=(${m.clientTile.x},${m.clientTile.y},${m.clientTile.plane})"
    }

    companion object {
        const val SNAPSHOT_FILE = "state-snapshots.jsonl"
        const val SNAPSHOT_ABSENT = "no state-snapshots.jsonl"
        private const val MAX_LIST = 12
    }
}
