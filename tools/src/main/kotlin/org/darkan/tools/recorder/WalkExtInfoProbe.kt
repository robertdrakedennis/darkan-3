package org.darkan.tools.recorder

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import org.darkan.core.net.recorder.PlayerInfoDecoder
import org.darkan.core.net.recorder.PlayerMovement
import org.darkan.core.net.recorder.SlotExtInfo
import java.io.File
import java.util.Base64

/**
 * FAITHFUL-REPLICATION probe: decode what the server actually sends the client for the LOCAL player's
 * walk/stop movement, per tick, from a recorder capture's `framed-s2c.jsonl`.
 *
 * ## How the LOCAL slot index is identified (protocol-grounded, NOT a heuristic)
 *
 * The op81 GPI prefix does **NOT** carry the local player index on the wire: its body is
 * `[30-bit local absolute tile][2046 × 20-bit other-slot words for slot ∈ 1..2047 EXCLUDING the local
 * index]` (`world/.../net/Op81GpiPrefix.kt`). The other-slot loop *skips* the local index, so decoding
 * the prefix at all *requires already knowing* which slot is local — the index is therefore impossible
 * to recover from op81 alone. The client keeps it in memory only (`lip = *(Client+0x19DB8)`,
 * `serverIndex = *(lip+0x48)`), and the recorder dylib already dumps it to `state-snapshots.jsonl` as
 * `local_player.server_index`. **That is the authoritative source** ([readLocalIndexFromSnapshot]).
 *
 * As an independent CROSS-CHECK (and the only fallback when no snapshot exists) we use the op81 local
 * 30-bit tile: the local slot is the one the prefix seeds to that exact absolute tile and which the
 * op22 stream keeps coherent. We confirm the snapshot index against it and warn loudly on mismatch.
 *
 * The per-tick LOCAL series reports, for the local slot each tick: movementType (0=still / 1=walk /
 * 2=run / 3=move-mode-or-teleport), the maintained tile (the decode now TRACKS it through a walk —
 * `tileExact` flags whether the tile is byte-exact), hasExt, and the decoded ext-info bits.
 *
 * Usage: `./gradlew :tools:walkExtInfo -Pargs="<captureDir> [<captureDir2> ...]"`
 */
private val JSON = Json { ignoreUnknownKeys = true }

private const val OP_REBUILD = 81
private const val OP_PLAYER_INFO = 22
private const val OP_NPC_INFO = 52

private const val BIT_APPEARANCE = 0x08
private const val BIT_MOVEMENT_ANIM = 0x20
private const val BIT_FORCED_MOVEMENT = 0x80
private const val BIT_FACE_DIRECTION = 0x02

private const val SNAPSHOT_FILE = "state-snapshots.jsonl"

private data class Frame(val op: Int, val body: ByteArray, val monoUs: Long, val len: Int)

private fun loadFrames(dir: File): List<Frame> {
    val file = File(dir, "framed-s2c.jsonl")
    require(file.exists()) { "missing ${file.path}" }
    val out = ArrayList<Frame>()
    file.forEachLine { line ->
        if (line.isBlank()) return@forEachLine
        val obj = JSON.parseToJsonElement(line) as? JsonObject ?: return@forEachLine
        val op = obj["op"]?.jsonPrimitive?.intOrNull ?: return@forEachLine
        if (op != OP_REBUILD && op != OP_PLAYER_INFO && op != OP_NPC_INFO) return@forEachLine
        val b64 = obj["body"]?.jsonPrimitive?.content ?: return@forEachLine
        val mono = obj["mono_us"]?.jsonPrimitive?.content?.toLongOrNull() ?: 0L
        val len = obj["len"]?.jsonPrimitive?.intOrNull ?: 0
        out += Frame(op, Base64.getDecoder().decode(b64), mono, len)
    }
    return out
}

private val DEBUG = System.getenv("PROBE_DEBUG") != null

/** Decode the op81 GPI-prefix local 30-bit absolute tile (first 30 bits, MSB-first). */
private fun decodeOp81LocalTile(op81Body: ByteArray): Triple<Int, Int, Int> {
    var bit = 0
    var v = 0
    repeat(30) {
        val byte = op81Body[bit ushr 3].toInt() and 0xff
        v = (v shl 1) or ((byte ushr (7 - (bit and 7))) and 1)
        bit++
    }
    val plane = (v ushr 28) and 0x3
    val x = (v ushr 14) and 0x3fff
    val y = v and 0x3fff
    return Triple(x, y, plane)
}

/**
 * AUTHORITATIVE local index: read `local_player.server_index` from `state-snapshots.jsonl` (the
 * recorder's client-memory dump of `lip+0x48`). Returns the index that appears most often across the
 * resolved snapshots (a stable value once the avatar binds), or null when the file is absent / the
 * field never resolves (older captures).
 */
private fun readLocalIndexFromSnapshot(dir: File): Int? {
    val file = File(dir, SNAPSHOT_FILE)
    if (!file.exists()) return null
    val counts = HashMap<Int, Int>()
    file.forEachLine { raw ->
        val line = raw.trim()
        if (line.isEmpty()) return@forEachLine
        val obj = runCatching { JSON.parseToJsonElement(line) as? JsonObject }.getOrNull() ?: return@forEachLine
        val si = (obj["local_player"] as? JsonObject)?.get("server_index")?.jsonPrimitive?.intOrNull
        if (si != null && si in 1 until 2048) counts[si] = (counts[si] ?: 0) + 1
    }
    return counts.entries.maxByOrNull { it.value }?.key
}

/**
 * Confirm a candidate local index against the op81 local tile by STATEFUL replay: seed a session at
 * that index and check the slot stays decode-clean and its tracked tile sits on / moves away from the
 * op81 local tile (a remote slot, seeded low-res from its own map square, never lands on the local
 * absolute tile). Returns (cleanRun, landedOnOp81Tile).
 */
private fun verifyIndexAgainstWire(
    op81Body: ByteArray,
    op22s: List<Frame>,
    idx: Int,
): Pair<Int, Boolean> {
    val op81Tile = decodeOp81LocalTile(op81Body)
    val prefix = runCatching { PlayerInfoDecoder.GpiPrefix(op81Body, idx) }.getOrNull() ?: return 0 to false
    val session = PlayerInfoDecoder.DetailedSession(prefix)
    var cleanRun = 0
    var stillClean = true
    var landed = false
    for (f in op22s) {
        val d = runCatching { session.next(f.body) }.getOrNull()
        if (d == null) { stillClean = false; continue }
        if (stillClean && d.decodeClean) cleanRun++ else stillClean = false
        // The slot's tracked high-res tile (now maintained through the walk) starting on the op81 tile.
        val t = d.scene.players[idx]?.tile ?: d.scene.movements.firstOrNull { it.index == idx }?.tileAfter
        if (t != null && t.x == op81Tile.first && t.y == op81Tile.second && t.plane == op81Tile.third) landed = true
    }
    return cleanRun to landed
}

/**
 * LEGACY fallback derivation (snapshot absent): pick the full-clean, op81-tile-coherent index with the
 * most local-slot activity. This is the OLD heuristic — explicitly low-confidence; the caller labels
 * it as such. The reliable path is [readLocalIndexFromSnapshot].
 */
private fun deriveLocalIndexStateful(op81Body: ByteArray, op22s: List<Frame>): Pair<Int, Int> {
    val op81Tile = decodeOp81LocalTile(op81Body)
    val fullCleanIdx = ArrayList<Int>()
    var bestCleanRun = 0
    val localActivity = HashMap<Int, Int>()
    val tileCoherent = HashSet<Int>()
    for (idx in 1 until 2048) {
        val prefix = runCatching { PlayerInfoDecoder.GpiPrefix(op81Body, idx) }.getOrNull() ?: continue
        val session = PlayerInfoDecoder.DetailedSession(prefix)
        var cleanRun = 0
        var stillClean = true
        var activity = 0
        var coherent = false
        for (f in op22s) {
            val d = runCatching { session.next(f.body) }.getOrNull()
            if (d == null) { stillClean = false; continue }
            if (stillClean && d.decodeClean) cleanRun++ else stillClean = false
            val hadMove = d.scene.movements.any { it.index == idx }
            val hadExt = d.extInfo.containsKey(idx)
            if (hadMove || hadExt) activity++
            val t = d.scene.players[idx]?.tile ?: d.scene.movements.firstOrNull { it.index == idx }?.tileAfter
            if (t != null && t.x == op81Tile.first && t.y == op81Tile.second) coherent = true
        }
        if (cleanRun > bestCleanRun) bestCleanRun = cleanRun
        if (cleanRun == op22s.size) {
            fullCleanIdx += idx
            localActivity[idx] = activity
            if (coherent) tileCoherent += idx
        }
    }
    if (tileCoherent.isNotEmpty()) {
        localActivity.keys.retainAll(tileCoherent)
        fullCleanIdx.retainAll(tileCoherent)
    }
    val bestIdx = localActivity.entries.filter { it.value > 0 }.maxByOrNull { it.value }?.key
        ?: fullCleanIdx.minOrNull()
        ?: -1
    return bestIdx to bestCleanRun
}

/** How the local index was resolved, for an honest confidence statement in the output. */
private data class LocalIdResolution(val index: Int, val source: String, val confidence: String)

/**
 * Resolve the local slot index, preferring the authoritative `state-snapshots.jsonl`
 * `local_player.server_index` and cross-checking it against the op81 local tile + a stateful clean
 * decode. Falls back to the legacy heuristic only when no snapshot index exists, and says so.
 */
private fun resolveLocalIndex(dir: File, op81Body: ByteArray, op22s: List<Frame>): LocalIdResolution {
    val op81Tile = decodeOp81LocalTile(op81Body)
    val snapIdx = readLocalIndexFromSnapshot(dir)
    if (snapIdx != null) {
        val (cleanRun, landed) = verifyIndexAgainstWire(op81Body, op22s, snapIdx)
        val cleanAll = cleanRun == op22s.size
        val confidence = when {
            cleanAll && landed ->
                "HIGH — snapshot server_index=$snapIdx, AND it decodes ${cleanRun}/${op22s.size} op22 " +
                    "clean AND its tracked tile lands on the op81 local tile " +
                    "(${op81Tile.first},${op81Tile.second},${op81Tile.third})."
            cleanAll ->
                "MEDIUM — snapshot server_index=$snapIdx decodes ${cleanRun}/${op22s.size} op22 clean, " +
                    "but its tracked tile never equalled the op81 local tile " +
                    "(${op81Tile.first},${op81Tile.second},${op81Tile.third}) in the window (walk may end elsewhere)."
            else ->
                "LOW — snapshot server_index=$snapIdx but it only decodes ${cleanRun}/${op22s.size} op22 " +
                    "clean (a wrong index or a stream desync). Treat the per-tick series with caution."
        }
        return LocalIdResolution(snapIdx, "state-snapshots.jsonl local_player.server_index", confidence)
    }
    // No snapshot index — legacy heuristic, low confidence.
    val (idx, bestClean) = deriveLocalIndexStateful(op81Body, op22s)
    val confidence =
        "LOW — no state-snapshots.jsonl server_index in this capture; fell back to the op81-tile/" +
            "activity HEURISTIC (best clean run $bestClean/${op22s.size}). This is the very heuristic " +
            "that mis-picked a remote slot before; do NOT trust the per-tick attribution. Re-capture " +
            "with the recorder dylib (it dumps local_player.server_index)."
    return LocalIdResolution(idx, "legacy heuristic (no snapshot)", confidence)
}

private fun maskBitsString(ext: SlotExtInfo): String {
    val names = buildList {
        if (ext.hasBit(BIT_APPEARANCE)) add("APPEARANCE(0x08)")
        if (ext.hasBit(BIT_MOVEMENT_ANIM)) add("MOVEMENT_ANIM(0x20)")
        if (ext.hasBit(BIT_FORCED_MOVEMENT)) add("FORCED_MOVEMENT(0x80)")
        if (ext.hasBit(BIT_FACE_DIRECTION)) add("FACE_DIRECTION(0x02)")
        val known = BIT_APPEARANCE or BIT_MOVEMENT_ANIM or BIT_FORCED_MOVEMENT or BIT_FACE_DIRECTION
        val other = ext.maskBits and known.inv()
        if (other != 0) add("OTHER(0x%x)".format(other))
    }
    return if (names.isEmpty()) "(none)" else names.joinToString("+")
}

private fun mvtName(t: Int?): String = when (t) {
    null -> "absent"
    0 -> "0=still"
    1 -> "1=WALK"
    2 -> "2=RUN"
    3 -> "3=MM" // mvt=3 = move-mode-descriptor form (SMOOTH glide when descriptor==4) or teleport
    else -> "$t=?"
}

/** For an mvt=3 record, render the descriptor index + whether it is the SMOOTH (glide) descriptor 4. */
private fun descTag(m: PlayerMovement?): String {
    if (m == null || m.movementType != 3) return ""
    val d = m.descriptor
    return when {
        d == null -> " desc=?"
        d == 4 -> " desc=4=SMOOTH/glide"
        else -> " desc=$d=SNAP"
    }
}

private fun probe(dir: File) {
    println("=".repeat(100))
    println("CAPTURE: ${dir.name}")
    println("=".repeat(100))

    val frames = loadFrames(dir)
    val op81 = frames.firstOrNull { it.op == OP_REBUILD }
    if (op81 == null) {
        println("  no op81 GPI prefix — cannot decode op22 statefully. SKIP.")
        return
    }
    val op22s = frames.filter { it.op == OP_PLAYER_INFO }
    val op52s = frames.filter { it.op == OP_NPC_INFO }
    println("  op81=1 (${op81.body.size}B)  op22=${op22s.size}  op52=${op52s.size}")
    if (op22s.isEmpty()) {
        println("  no op22 — SKIP.")
        return
    }

    val op81Tile = decodeOp81LocalTile(op81.body)
    println("  op81 local tile = (${op81Tile.first},${op81Tile.second},${op81Tile.third}) " +
        "zone (${op81Tile.first shr 3},${op81Tile.second shr 3})")

    // Identify the local slot PROTOCOL-GROUNDED: snapshot server_index first, op81-tile cross-check.
    val resolution = resolveLocalIndex(dir, op81.body, op22s)
    val localIndex = resolution.index
    println("  LOCAL slot index = $localIndex")
    println("    source     : ${resolution.source}")
    println("    confidence : ${resolution.confidence}")
    if (localIndex < 0) {
        println("  could NOT resolve a local index. Dumping global histogram only.\n")
        dumpHistogramOnly(op81.body, op22s)
        return
    }
    println()

    val t0 = op22s.first().monoUs
    var prevLocalMvt: Int? = null
    var lastLocalStepTick = -1
    var tickNo = 0

    // Per-tick local-slot table — ONE stateful session replayed in order.
    val session = PlayerInfoDecoder.DetailedSession(PlayerInfoDecoder.GpiPrefix(op81.body, localIndex))
    println("  PER-TICK LOCAL SLOT (idx=$localIndex):")
    println("  %-5s %-9s %-13s %-7s %-9s %-7s %-7s %s".format(
        "tick", "+ms", "mvt", "hasExt", "decodeOK", "extOK", "tileEx", "tile  |  ext-info"))
    for (f in op22s) {
        tickNo++
        val detailed = runCatching { session.next(f.body) }.getOrNull()
        if (detailed == null) {
            println("  %-5d %-9d  <op22 decode threw (len=%d)>".format(tickNo, (f.monoUs - t0) / 1000, f.len))
            continue
        }
        val mvt = detailed.scene.movements.firstOrNull { it.index == localIndex }
        val ext = detailed.extInfo[localIndex]
        val decodeOk = detailed.decodeClean.toString()
        val ms = (f.monoUs - t0) / 1000
        val mvtStr = mvtName(mvt?.movementType) + descTag(mvt)
        val hasExt = mvt?.hasExt?.toString() ?: "-"
        val extOk = ext?.parsedClean?.toString() ?: "-"
        // The maintained per-tick tile: from the movement record (now tracked) or the folded slot.
        val tile = mvt?.tileAfter ?: detailed.scene.players[localIndex]?.tile
        val tileExact = mvt?.tileExact
        val tileExStr = when {
            mvt == null -> "-"
            tileExact == true -> "yes"
            else -> "NO"
        }
        val tileStr = if (tile != null) "(${tile.x},${tile.y},${tile.plane})" else "-"
        val sb = StringBuilder()
        sb.append(tileStr).append("  |  ")
        if (ext != null) {
            sb.append(maskBitsString(ext))
            ext.movementAnim?.let { a ->
                sb.append("  anim=").append(if (a.isResetForm) "RESET(-1×4)" else a.seqs.toString())
                sb.append("/prio=").append(a.priority)
            }
            ext.forcedMovement?.let { g ->
                sb.append("  glide{src=(").append(g.srcDx).append(',').append(g.srcDz)
                    .append(") dst=(").append(g.dstDx).append(',').append(g.dstDz)
                    .append(") tick=").append(g.startTick).append("..").append(g.endTick)
                    .append(" yaw=").append(g.yaw).append('}')
            }
        } else {
            sb.append("(no ext block this tick)")
        }
        // Flag the walk->stop transition tick.
        val isStop = (prevLocalMvt == 1 || prevLocalMvt == 2) && (mvt?.movementType ?: 0) !in setOf(1, 2)
        val marker = when {
            mvt?.movementType == 1 || mvt?.movementType == 2 -> " <-- STEP"
            mvt?.movementType == 3 -> " <-- MM/TELE"
            isStop -> " <-- STOP (tick after last step)"
            else -> ""
        }
        if (mvt?.movementType == 1 || mvt?.movementType == 2) lastLocalStepTick = tickNo
        println("  %-5d %-9d %-13s %-7s %-9s %-7s %-7s %s%s".format(
            tickNo, ms, mvtStr, hasExt, decodeOk, extOk, tileExStr, sb.toString(), marker))
        prevLocalMvt = mvt?.movementType ?: 0
    }

    if (lastLocalStepTick < 0) {
        println("\n  *** LOCAL SLOT $localIndex TOOK NO HIGH-RES WALK/RUN STEP (mvt=1/2) in this window. ***")
        println("  *** Its per-tick form was mvt=0/still or absent throughout — see the verdict below. ***")
    }

    // The blunt answer for the walk window.
    summariseLocalWalkVerdict(op81.body, op22s, localIndex)

    // RAW mvt=3 payload dump (local + most-active remote): decode the 30-bit (large) / 15-bit (small)
    // position field as the binary does so we can read the exact SMOOTH (desc=4) on-wire encoding.
    dumpMvt3Payloads(op81.body, op22s, localIndex)

    // Global ext-info histogram (all slots) + remote-player vs NPC contrast.
    println()
    dumpHistogramOnly(op81.body, op22s, localIndex)
    dumpMovementHistogram(op81.body, op22s, localIndex)
    dumpMostActiveRemote(op81.body, op22s, localIndex)
    dumpNpcExtSummary(op81.body, op22s, op52s, localIndex)
    println()
}

/**
 * The headline answer: across the whole op22 window, what movement form did the LOCAL slot take? This
 * is the server-side ground truth for whether the local walk is client-predicted (mvt=0/absent = the
 * server does NOT force-move the local avatar) or server-driven (mvt=1/2/3 forced).
 */
private fun summariseLocalWalkVerdict(op81Body: ByteArray, op22s: List<Frame>, localIndex: Int) {
    val session = PlayerInfoDecoder.DetailedSession(PlayerInfoDecoder.GpiPrefix(op81Body, localIndex))
    val hist = IntArray(4)
    var absent = 0
    var threw = 0
    val forcedTicks = ArrayList<Int>()
    var tick = 0
    for (f in op22s) {
        tick++
        val d = runCatching { session.next(f.body) }.getOrNull()
        if (d == null) { threw++; continue }
        val m = d.scene.movements.firstOrNull { it.index == localIndex }
        if (m == null) { absent++; continue }
        if (m.movementType in 0..3) hist[m.movementType]++
        if (m.movementType in 1..3) forcedTicks += tick
    }
    val forced = hist[1] + hist[2] + hist[3]
    println("\n  ===== LOCAL-SLOT VERDICT (idx=$localIndex, ${op22s.size} op22 ticks) =====")
    println("    mvt=0 (still / no forced movement): ${hist[0]}")
    println("    mvt=1 (WALK forced)               : ${hist[1]}")
    println("    mvt=2 (RUN forced)                : ${hist[2]}")
    println("    mvt=3 (move-mode / teleport)      : ${hist[3]}")
    println("    absent (folded into a skip-run)   : $absent")
    if (threw > 0) println("    (ticks that threw on decode      : $threw)")
    if (forced == 0) {
        println("    => ANSWER: the LOCAL slot is mvt=0 / skip-run for the ENTIRE window — the server sends")
        println("       NO forced movement for the local avatar. This CONFIRMS the client predicts the local")
        println("       walk (op74 click → client pathfinds/walks/glides); op22 is reconciliation only.")
    } else {
        println("    => ANSWER: the LOCAL slot took a FORCED movement (mvt=1/2/3) on ${forced} tick(s) " +
            "(ticks ${forcedTicks.take(20)}${if (forcedTicks.size > 20) "…" else ""}).")
        println("       The fix PREMISE (local walk is client-predicted, server sends mvt=0) is CONTRADICTED")
        println("       on this capture — reconsider before stripping the server-side forced local movement.")
    }
}

/** Movement-type histogram for the local slot vs all remote slots (validates the decode + answers Q5). */
private fun dumpMovementHistogram(op81Body: ByteArray, op22s: List<Frame>, localIndex: Int) {
    val session = PlayerInfoDecoder.DetailedSession(PlayerInfoDecoder.GpiPrefix(op81Body, localIndex))
    val local = IntArray(4)
    val remote = IntArray(4)
    var localWalkWithGlide = 0; var localWalkWithAnim = 0; var localWalkTotal = 0
    var remoteWalkWithGlide = 0; var remoteWalkWithAnim = 0; var remoteWalkTotal = 0
    for (f in op22s) {
        val d = runCatching { session.next(f.body) }.getOrNull() ?: continue
        for (m in d.scene.movements) {
            val arr = if (m.index == localIndex) local else remote
            if (m.movementType in 0..3) arr[m.movementType]++
            if (m.movementType == 1 || m.movementType == 2) {
                val ext = d.extInfo[m.index]
                val glide = ext?.hasBit(BIT_FORCED_MOVEMENT) == true
                val anim = ext?.hasBit(BIT_MOVEMENT_ANIM) == true
                if (m.index == localIndex) {
                    localWalkTotal++; if (glide) localWalkWithGlide++; if (anim) localWalkWithAnim++
                } else {
                    remoteWalkTotal++; if (glide) remoteWalkWithGlide++; if (anim) remoteWalkWithAnim++
                }
            }
        }
    }
    println("  MOVEMENT-TYPE HISTOGRAM (mvt: 0=still 1=walk 2=run 3=move-mode/tele):")
    println("    LOCAL  slot=$localIndex : still=${local[0]} walk=${local[1]} run=${local[2]} mm/tele=${local[3]}")
    println("    REMOTE players       : still=${remote[0]} walk=${remote[1]} run=${remote[2]} mm/tele=${remote[3]}")
    println("    WALK/RUN steps carrying ext-info bits:")
    println("      LOCAL : $localWalkTotal walk/run steps; with bit0x20(anim)=$localWalkWithAnim  with bit0x80(glide)=$localWalkWithGlide")
    println("      REMOTE: $remoteWalkTotal walk/run steps; with bit0x20(anim)=$remoteWalkWithAnim  with bit0x80(glide)=$remoteWalkWithGlide")
}

/**
 * Decode the raw mvt=3 position field the way the binary does (DecodeKnownPlayerUpdate large/small
 * branches): large `payload` = `(planeDelta<<28)|(xFineDelta14<<14)|yFineDelta14`; small `payload`
 * 15-bit = `desc(bits14:10)` + signed-5 X (bits9:5) + signed-5 Y (bits4:0) at 512-unit steps. Lets us
 * read the exact SMOOTH (desc=4) encoding when it appears.
 */
private fun dumpMvt3Payloads(op81Body: ByteArray, op22s: List<Frame>, localIndex: Int) {
    val s = PlayerInfoDecoder.DetailedSession(PlayerInfoDecoder.GpiPrefix(op81Body, localIndex))
    val moveCount = HashMap<Int, Int>()
    run {
        val s0 = PlayerInfoDecoder.DetailedSession(PlayerInfoDecoder.GpiPrefix(op81Body, localIndex))
        for (f in op22s) {
            val d = runCatching { s0.next(f.body) }.getOrNull() ?: continue
            for (m in d.scene.movements) if (m.index != localIndex && m.movementType == 3) moveCount[m.index] = (moveCount[m.index] ?: 0) + 1
        }
    }
    val remote = moveCount.entries.maxByOrNull { it.value }?.key ?: -1
    val anyLocalMm = run {
        val s1 = PlayerInfoDecoder.DetailedSession(PlayerInfoDecoder.GpiPrefix(op81Body, localIndex))
        var any = false
        for (f in op22s) {
            val d = runCatching { s1.next(f.body) }.getOrNull() ?: continue
            if (d.scene.movements.any { it.index == localIndex && it.movementType == 3 }) { any = true; break }
        }
        any
    }
    if (!anyLocalMm && remote < 0) return
    println("\n  RAW mvt=3 PAYLOADS (local=$localIndex, remote=$remote) — decoding 30/15-bit field as the binary does:")
    var tickNo = 0
    for (f in op22s) {
        tickNo++
        val d = runCatching { s.next(f.body) }.getOrNull() ?: continue
        for (m in d.scene.movements) {
            if (m.movementType != 3) continue
            if (m.index != localIndex && m.index != remote) continue
            val who = if (m.index == localIndex) "LOCAL " else "remote"
            val p = m.payload ?: continue
            val line = if (m.large == true) {
                val planeD = (p ushr 28) and 0x3
                val xFine14 = (p ushr 14) and 0x3fff
                val yFine14 = p and 0x3fff
                "large desc=${m.descriptor}(byteOff=0x${(m.descriptor!! * 4).toString(16)}) planeD=$planeD xFineDelta=$xFine14 yFineDelta=$yFine14 rawPos30=0x${p.toString(16)}"
            } else {
                fun s5(v: Int) = if (v < 0x10) v else v - 0x20
                val byteOff = (p ushr 10) and 0x1c
                val planeD = (p ushr 10) and 0x3
                val x5 = (p ushr 5) and 0x1f
                val y5 = p and 0x1f
                "small desc=${m.descriptor}(byteOff=0x${byteOff.toString(16)}) planeD=$planeD xS5=${s5(x5)}(${s5(x5) * 512}fine) yS5=${s5(y5)}(${s5(y5) * 512}fine) code15=0x${p.toString(16)}"
            }
            val smooth = if (m.descriptor == 4) " [SMOOTH/glide desc4]" else " [SNAP desc=${m.descriptor}]"
            println("    tick %-4d %s %s%s".format(tickNo, who, line, smooth))
        }
    }
}

/** Dump the per-tick movement+ext of the most-active REMOTE player (validates the decoder + Q5 contrast). */
private fun dumpMostActiveRemote(op81Body: ByteArray, op22s: List<Frame>, localIndex: Int) {
    val moveCount = HashMap<Int, Int>()
    run {
        val s = PlayerInfoDecoder.DetailedSession(PlayerInfoDecoder.GpiPrefix(op81Body, localIndex))
        for (f in op22s) {
            val d = runCatching { s.next(f.body) }.getOrNull() ?: continue
            for (m in d.scene.movements) {
                if (m.index != localIndex && (m.movementType == 1 || m.movementType == 2)) {
                    moveCount[m.index] = (moveCount[m.index] ?: 0) + 1
                }
            }
        }
    }
    val target = moveCount.entries.maxByOrNull { it.value }?.key ?: run {
        println("  (no remote player took a walk/run step in this capture)")
        return
    }
    println("  MOST-ACTIVE REMOTE PLAYER slot=$target (${moveCount[target]} walk/run steps) per-tick:")
    val s = PlayerInfoDecoder.DetailedSession(PlayerInfoDecoder.GpiPrefix(op81Body, localIndex))
    var tickNo = 0
    for (f in op22s) {
        tickNo++
        val d = runCatching { s.next(f.body) }.getOrNull() ?: continue
        val m = d.scene.movements.firstOrNull { it.index == target } ?: continue
        if (m.movementType == 0) continue
        val ext = d.extInfo[target]
        val bits = if (ext != null) maskBitsString(ext) else "(no ext)"
        val anim = ext?.movementAnim?.let { if (it.isResetForm) " anim=RESET" else " anim=${it.seqs}" } ?: ""
        val glide = ext?.forcedMovement?.let { " glide{src=(${it.srcDx},${it.srcDz}) tick=${it.startTick}..${it.endTick}}" } ?: ""
        val tile = m.tileAfter?.let { " tile=(${it.x},${it.y},${it.plane})${if (m.tileExact) "" else "~"}" } ?: ""
        println("    tick %-4d %-22s %s%s%s%s".format(tickNo, mvtName(m.movementType) + descTag(m), tile, bits, anim, glide))
    }
}

/** Histogram of which ext-info bits appear across ALL op22 ticks, split local vs remote. */
private fun dumpHistogramOnly(op81Body: ByteArray, op22s: List<Frame>, localIndex: Int = -1) {
    var localAnim = 0; var localForced = 0; var localAppr = 0; var localBlocks = 0
    var remoteAnim = 0; var remoteForced = 0; var remoteAppr = 0; var remoteBlocks = 0
    val remoteMaskHist = HashMap<Int, Int>()
    val idx = if (localIndex > 0) localIndex else 1
    val gpi = runCatching { PlayerInfoDecoder.GpiPrefix(op81Body, idx) }.getOrNull() ?: return
    val session = PlayerInfoDecoder.DetailedSession(gpi)
    for (f in op22s) {
        val d = runCatching { session.next(f.body) }.getOrNull() ?: continue
        for ((slot, ext) in d.extInfo) {
            val isLocal = slot == localIndex
            if (isLocal) {
                localBlocks++
                if (ext.hasBit(BIT_MOVEMENT_ANIM)) localAnim++
                if (ext.hasBit(BIT_FORCED_MOVEMENT)) localForced++
                if (ext.hasBit(BIT_APPEARANCE)) localAppr++
            } else {
                remoteBlocks++
                if (ext.hasBit(BIT_MOVEMENT_ANIM)) remoteAnim++
                if (ext.hasBit(BIT_FORCED_MOVEMENT)) remoteForced++
                if (ext.hasBit(BIT_APPEARANCE)) remoteAppr++
                remoteMaskHist[ext.maskBits] = (remoteMaskHist[ext.maskBits] ?: 0) + 1
            }
        }
    }
    println("  EXT-INFO BIT HISTOGRAM across ${op22s.size} op22 ticks:")
    if (localIndex > 0) {
        println("    LOCAL  slot=$localIndex : blocks=$localBlocks  MOVEMENT_ANIM(0x20)=$localAnim  FORCED_MOVEMENT(0x80)=$localForced  APPEARANCE(0x08)=$localAppr")
    }
    println("    REMOTE players       : blocks=$remoteBlocks  MOVEMENT_ANIM(0x20)=$remoteAnim  FORCED_MOVEMENT(0x80)=$remoteForced  APPEARANCE(0x08)=$remoteAppr")
    if (remoteMaskHist.isNotEmpty()) {
        val top = remoteMaskHist.entries.sortedByDescending { it.value }.take(8)
        println("    remote raw-mask histogram (top): " + top.joinToString(", ") { "0x%x×%d".format(it.key, it.value) })
    }
}

/** Best-effort NPC ext-info presence: does op52 carry per-NPC ext blocks (and how often)? */
private fun dumpNpcExtSummary(op81Body: ByteArray, op22s: List<Frame>, op52s: List<Frame>, localIndex: Int) {
    val sizes = op52s.map { it.len }
    println("  NPC (op52): ${op52s.size} frames; body sizes min=${sizes.minOrNull() ?: 0} max=${sizes.maxOrNull() ?: 0} " +
        "(NPC temp-move glide uses the SetRenderWaypoint path; see report).")
}

fun main(args: Array<String>) {
    val dirs = if (args.isNotEmpty()) {
        args.map { File(it) }
    } else {
        error("usage: walkExtInfo <captureDir> [<captureDir2> ...]")
    }
    for (d in dirs) {
        require(d.isDirectory) { "not a directory: ${d.path}" }
        probe(d)
    }
}
