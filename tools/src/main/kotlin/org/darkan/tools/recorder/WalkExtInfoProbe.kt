package org.darkan.tools.recorder

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.darkan.core.net.recorder.PlayerInfoDecoder
import org.darkan.core.net.recorder.SlotExtInfo
import java.io.File
import java.util.Base64

/**
 * FAITHFUL-REPLICATION probe: decode what the server actually sends the client for the LOCAL player's
 * walk/stop animation, per tick, from a recorder capture's `framed-s2c.jsonl`.
 *
 * It reads the single op81 (REBUILD_NORMAL_SIMPLE) GPI prefix, DERIVES the local player slot index
 * (brute-force: the candidate whose op81-prefix + first appearance-bearing op22 decode yields a clean
 * local APPEARANCE), then walks the ordered op22 (PLAYER_INFO) stream dumping, for the local slot per
 * tick: movementType, hasExt, and the decoded ext-info mask bits + bit-0x20 MOVEMENT_ANIM (4 seq ids +
 * priority) and bit-0x80 FORCED_MOVEMENT (glide deltas/ticks/yaw). It also summarises remote players
 * and (best-effort) NPCs so the local-vs-remote/NPC ext-info contrast is concrete.
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

private data class Frame(val op: Int, val body: ByteArray, val monoUs: Long, val len: Int)

private fun loadFrames(dir: File): List<Frame> {
    val file = File(dir, "framed-s2c.jsonl")
    require(file.exists()) { "missing ${file.path}" }
    val out = ArrayList<Frame>()
    file.forEachLine { line ->
        if (line.isBlank()) return@forEachLine
        val obj = JSON.parseToJsonElement(line) as? JsonObject ?: return@forEachLine
        val op = obj["op"]?.jsonPrimitive?.content?.toIntOrNull() ?: return@forEachLine
        if (op != OP_REBUILD && op != OP_PLAYER_INFO && op != OP_NPC_INFO) return@forEachLine
        val b64 = obj["body"]?.jsonPrimitive?.content ?: return@forEachLine
        val mono = obj["mono_us"]?.jsonPrimitive?.content?.toLongOrNull() ?: 0L
        val len = obj["len"]?.jsonPrimitive?.content?.toIntOrNull() ?: 0
        out += Frame(op, Base64.getDecoder().decode(b64), mono, len)
    }
    return out
}

private val DEBUG = System.getenv("PROBE_DEBUG") != null

/** Decode the op81 GPI-prefix local 30-bit absolute tile (first 30 bits, MSB-first). */
private fun decodeOp81LocalTile(op81Body: ByteArray): Pair<Int, Int> {
    var bit = 0
    var v = 0
    repeat(30) {
        val byte = op81Body[bit ushr 3].toInt() and 0xff
        v = (v shl 1) or ((byte ushr (7 - (bit and 7))) and 1)
        bit++
    }
    val x = (v ushr 14) and 0x3fff
    val y = v and 0x3fff
    return x to y
}

/**
 * Derive the local index via STATEFUL replay. op22 is stateful, so for each candidate index we seed
 * ONE [PlayerInfoDecoder.DetailedSession] from op81 and replay the whole op22 stream, scoring by
 * (a) consecutive clean decodes from the start (a wrong index desyncs early) and (b) whether a local
 * appearance ever decodes on that slot. Returns (index, bestCleanRunLength) or (-1, best) if NO index
 * decodes the stream (the decoder cannot handle this multi-player stream).
 */
private fun deriveLocalIndexStateful(op81Body: ByteArray, op22s: List<Frame>): Pair<Int, Int> {
    // The op81 prefix's local 30-bit tile is the local player's absolute spawn tile. The DETERMINISTIC
    // local-slot signal: the slot whose op81-seeded tile is the absolute local tile AND that op22 keeps
    // coherent. We score every index by clean-run length; among full-clean indices we REQUIRE the slot's
    // tracked tile to equal the op81 local tile (a remote player is seeded low-res, never lands exactly
    // on the local absolute tile), then prefer the most-active such index. Appearance is NOT required
    // (prod may not re-send it, and the real non-default appearance can defeat the sub-parser).
    val op81Tile = decodeOp81LocalTile(op81Body)
    val fullCleanIdx = ArrayList<Int>()
    var bestCleanRun = 0
    val localActivity = HashMap<Int, Int>() // idx -> (#frames its slot had a movement record or ext block)
    val tileCoherent = HashSet<Int>()       // idx whose tracked tile ever equals the op81 local tile
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
            val t = d.scene.players[idx]?.tile
            if (t != null && t.x == op81Tile.first && t.y == op81Tile.second) coherent = true
        }
        if (cleanRun > bestCleanRun) bestCleanRun = cleanRun
        if (cleanRun == op22s.size) {
            fullCleanIdx += idx
            localActivity[idx] = activity
            if (coherent) tileCoherent += idx
        }
    }
    // Restrict to tile-coherent full-clean indices when any exist (the deterministic signal).
    if (tileCoherent.isNotEmpty()) {
        localActivity.keys.retainAll(tileCoherent)
        fullCleanIdx.retainAll(tileCoherent)
    }
    if (DEBUG) System.err.println(
        "  [derive-stateful] full-clean indices=${fullCleanIdx.size} bestCleanRun=$bestCleanRun; " +
            "active candidates=" + localActivity.entries.filter { it.value > 0 }.sortedByDescending { it.value }
                .take(8).joinToString { "idx${it.key}:${it.value}" }
    )
    if (DEBUG) {
        // The local player is the SOLE initial render-list member, so the FIRST op22's first high-res
        // entry is the local slot. Report which indices decode a non-empty local appearance on the
        // FIRST op22 alone (single-frame is correct for frame 1) — that pins the local index.
        val first = op22s.first().body
        val apprIdx = (1 until 2048).filter { idx ->
            val p = runCatching { PlayerInfoDecoder.GpiPrefix(op81Body, idx) }.getOrNull() ?: return@filter false
            runCatching { PlayerInfoDecoder.decodeDetailed(first, p) }.getOrNull()
                ?.scene?.appearance?.values?.any { it.kitId >= 0 || it.itemId >= 0 } == true
        }
        System.err.println("  [first-op22 appearance] indices with a decodable local appearance on frame 1: " +
            (if (apprIdx.size <= 12) apprIdx.toString() else "${apprIdx.size} indices ${apprIdx.take(6)}..."))
        // For a representative full-clean index, what is extInfoOrder[0] (== the local slot, processed
        // first) and is its first ext block APPEARANCE? Dump the block bytes so we can see why the
        // appearance parser fails on the real (non-default) prod appearance.
        val repIdx = localActivity.entries.filter { it.value > 0 }.maxByOrNull { it.value }?.key ?: fullCleanIdx.minOrNull()
        if (repIdx != null) {
            val p = PlayerInfoDecoder.GpiPrefix(op81Body, repIdx)
            val d = runCatching { PlayerInfoDecoder.decodeDetailed(first, p) }.getOrNull()
            System.err.println("  [first-op22 extInfoOrder] @idx$repIdx: order[0..4]=${d?.extInfoOrder?.take(5)}  " +
                "ext[order0] mask=0x${d?.extInfo?.get(d.extInfoOrder.firstOrNull())?.maskBits?.toString(16)}")
        }
    }
    // Prefer the (tile-coherent) full-clean index with the MOST local-slot activity (the moving local
    // player); if none shows activity, the local player was stationary across the window — pick the
    // lowest such index; the caller reports "local never moved".
    val bestIdx = localActivity.entries.filter { it.value > 0 }.maxByOrNull { it.value }?.key
        ?: fullCleanIdx.minOrNull()
        ?: -1
    return bestIdx to bestCleanRun
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
    3 -> "3=TELE"
    else -> "$t=?"
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

    // Derive the local index via STATEFUL replay: the right index maximises clean decodes + decodes a
    // local appearance somewhere. op22 is stateful, so each candidate is scored by replaying the whole
    // op22 stream through one DetailedSession (NOT per-frame-from-prefix, which desyncs after frame 1).
    val (localIndex, bestClean) = deriveLocalIndexStateful(op81.body, op22s)
    if (localIndex < 0) {
        println("  could NOT derive a local index: NO candidate index decodes the op22 stream cleanly")
        println("  (best clean-decode run across all 2047 indices was $bestClean / ${op22s.size}).")
        println("  -> the recorder/decoder cannot statefully decode this multi-player op22 stream;")
        println("     cannot attribute the local slot. See report for the implication.\n")
        dumpHistogramOnly(op81.body, op22s)
        return
    }
    println("  derived localIndex = $localIndex (stateful replay: $bestClean/${op22s.size} op22 clean; confirmed by op81-tile coherence)\n")

    val t0 = op22s.first().monoUs
    var prevLocalMvt: Int? = null
    var lastLocalStepTick = -1
    var tickNo = 0

    // Per-tick local-slot table — ONE stateful session replayed in order.
    val op81Tile = decodeOp81LocalTile(op81.body)
    println("  op81 local tile = (${op81Tile.first},${op81Tile.second}) zone (${op81Tile.first shr 3},${op81Tile.second shr 3})")
    val session = PlayerInfoDecoder.DetailedSession(PlayerInfoDecoder.GpiPrefix(op81.body, localIndex))
    println("  PER-TICK LOCAL SLOT (idx=$localIndex):")
    println("  %-5s %-9s %-9s %-7s %-7s %-7s %s".format("tick", "+ms", "mvt", "hasExt", "decodeOK", "extOK", "ext-info"))
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
        val mvtStr = mvtName(mvt?.movementType)
        val hasExt = mvt?.hasExt?.toString() ?: "-"
        val extOk = ext?.parsedClean?.toString() ?: "-"
        val tile = detailed.scene.players[localIndex]?.tile
        val tileStr = if (tile != null) "(${tile.x},${tile.y},${tile.plane})" else "-"
        val sb = StringBuilder()
        sb.append("tile=").append(tileStr).append("  ")
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
            isStop -> " <-- STOP (tick after last step)"
            else -> ""
        }
        if (mvt?.movementType == 1 || mvt?.movementType == 2) lastLocalStepTick = tickNo
        println("  %-5d %-9d %-9s %-7s %-7s %-7s %s%s".format(tickNo, ms, mvtStr, hasExt, decodeOk, extOk, sb.toString(), marker))
        prevLocalMvt = mvt?.movementType ?: 0
    }

    if (lastLocalStepTick < 0) {
        println("\n  *** LOCAL PLAYER NEVER WALKED in this capture (no mvt=1/2 on the local slot). ***")
        println("  *** A deliberate local walk->stop production capture is required to answer Q1-Q4. ***")
    }

    // Global ext-info histogram (all slots) + remote-player vs NPC contrast.
    println()
    dumpHistogramOnly(op81.body, op22s, localIndex)
    dumpMovementHistogram(op81.body, op22s, localIndex)
    dumpMostActiveRemote(op81.body, op22s, localIndex)
    dumpNpcExtSummary(op81.body, op22s, op52s, localIndex)
    println()
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
    println("  MOVEMENT-TYPE HISTOGRAM (mvt: 0=still 1=walk 2=run 3=tele):")
    println("    LOCAL  slot=$localIndex : still=${local[0]} walk=${local[1]} run=${local[2]} tele=${local[3]}")
    println("    REMOTE players       : still=${remote[0]} walk=${remote[1]} run=${remote[2]} tele=${remote[3]}")
    println("    WALK/RUN steps carrying ext-info bits:")
    println("      LOCAL : $localWalkTotal walk/run steps; with bit0x20(anim)=$localWalkWithAnim  with bit0x80(glide)=$localWalkWithGlide")
    println("      REMOTE: $remoteWalkTotal walk/run steps; with bit0x20(anim)=$remoteWalkWithAnim  with bit0x80(glide)=$remoteWalkWithGlide")
    correlateAnimWithMovementStart(op81Body, op22s, localIndex)
}

/**
 * Does bit-0x20 (MOVEMENT_ANIM) correlate with the FIRST step of a movement burst (a walk/run step
 * whose previous tick was still/absent) vs a CONTINUATION step? If prod sends bit-0x20 at movement
 * START only (not every step), the faithful encoder should do the same. Tracks per slot across the
 * whole stream.
 */
private fun correlateAnimWithMovementStart(op81Body: ByteArray, op22s: List<Frame>, localIndex: Int) {
    val session = PlayerInfoDecoder.DetailedSession(PlayerInfoDecoder.GpiPrefix(op81Body, localIndex))
    val prevMoving = HashMap<Int, Boolean>() // slot -> was walking/running last tick
    var startSteps = 0; var startWithAnim = 0; var startWithGlide = 0
    var contSteps = 0; var contWithAnim = 0; var contWithGlide = 0
    for (f in op22s) {
        val d = runCatching { session.next(f.body) }.getOrNull() ?: continue
        val movingNow = HashSet<Int>()
        for (m in d.scene.movements) {
            if (m.movementType != 1 && m.movementType != 2) continue
            movingNow += m.index
            val wasMoving = prevMoving[m.index] == true
            val ext = d.extInfo[m.index]
            val anim = ext?.hasBit(BIT_MOVEMENT_ANIM) == true
            val glide = ext?.hasBit(BIT_FORCED_MOVEMENT) == true
            if (wasMoving) {
                contSteps++; if (anim) contWithAnim++; if (glide) contWithGlide++
            } else {
                startSteps++; if (anim) startWithAnim++; if (glide) startWithGlide++
            }
        }
        // Update prevMoving: a slot not in movingNow this tick is no longer moving.
        prevMoving.keys.retainAll(movingNow)
        for (i in movingNow) prevMoving[i] = true
    }
    println("    bit-0x20/0x80 vs movement phase (all players):")
    println("      START steps (prev tick still): $startSteps; with anim=$startWithAnim glide=$startWithGlide")
    println("      CONT  steps (prev tick moving): $contSteps; with anim=$contWithAnim glide=$contWithGlide")

    // Where DO bit-0x20 / bit-0x80 occur? Tally by the slot's movementType THIS tick (across all players).
    val s2 = PlayerInfoDecoder.DetailedSession(PlayerInfoDecoder.GpiPrefix(op81Body, localIndex))
    val animByMvt = HashMap<String, Int>()
    val glideByMvt = HashMap<String, Int>()
    for (f in op22s) {
        val d = runCatching { s2.next(f.body) }.getOrNull() ?: continue
        for ((slot, ext) in d.extInfo) {
            val m = d.scene.movements.firstOrNull { it.index == slot }?.movementType
            val key = mvtName(m).substringAfter('=').ifEmpty { mvtName(m) }
            if (ext.hasBit(BIT_MOVEMENT_ANIM)) animByMvt[key] = (animByMvt[key] ?: 0) + 1
            if (ext.hasBit(BIT_FORCED_MOVEMENT)) glideByMvt[key] = (glideByMvt[key] ?: 0) + 1
        }
    }
    println("      bit-0x20(anim) occurrences by this-tick mvt: ${animByMvt.toSortedMap()}")
    println("      bit-0x80(glide) occurrences by this-tick mvt: ${glideByMvt.toSortedMap()}")
}

/** Dump the per-tick movement+ext of the most-active REMOTE player (validates the decoder + Q5 contrast). */
private fun dumpMostActiveRemote(op81Body: ByteArray, op22s: List<Frame>, localIndex: Int) {
    // Find the remote slot with the most walk/run movements.
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
        println("    tick %-4d %-7s %s%s%s".format(tickNo, mvtName(m.movementType), bits, anim, glide))
    }
}

/** Histogram of which ext-info bits appear across ALL op22 ticks, split local vs remote. */
private fun dumpHistogramOnly(op81Body: ByteArray, op22s: List<Frame>, localIndex: Int = -1) {
    var localAnim = 0; var localForced = 0; var localAppr = 0; var localBlocks = 0
    var remoteAnim = 0; var remoteForced = 0; var remoteAppr = 0; var remoteBlocks = 0
    val remoteMaskHist = HashMap<Int, Int>()
    val idx = if (localIndex > 0) localIndex else 1
    val gpi = runCatching { PlayerInfoDecoder.GpiPrefix(op81Body, idx) }.getOrNull() ?: return
    val session = PlayerInfoDecoder.DetailedSession(gpi) // STATEFUL replay
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
    // We do not fully re-decode op52 ext-info here (the NPC decoder folds list mgmt only); instead we
    // report op52 frame count + sizes so the report can state whether NPCs were even present/moving.
    val sizes = op52s.map { it.len }
    println("  NPC (op52): ${op52s.size} frames; body sizes min=${sizes.minOrNull() ?: 0} max=${sizes.maxOrNull() ?: 0} " +
        "(NPC temp-move glide uses the same SetRenderWaypoint path; see report).")
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
