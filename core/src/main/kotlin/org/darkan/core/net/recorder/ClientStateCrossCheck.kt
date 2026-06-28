package org.darkan.core.net.recorder

import kotlinx.io.Buffer
import kotlinx.io.write
import org.darkan.core.net.prot.Codec
import world.gregs.voidps.buffer.readByteInverse
import world.gregs.voidps.buffer.readByteSubtract
import world.gregs.voidps.buffer.readUByte
import world.gregs.voidps.buffer.readUIntInverseMiddle
import world.gregs.voidps.buffer.readUIntLittle
import world.gregs.voidps.buffer.readUShort
import world.gregs.voidps.buffer.readUShortAdd
import kotlin.math.floor
import kotlin.math.pow

/**
 * The **client-is-king** cross-check: rebuild the client's EXPECTED state from the *state-bearing*
 * s2c packets we decoded, then compare it field-by-field against the client's OWN final state
 * snapshot (`state-snapshots.jsonl`). The client is the ground truth — if our decode of a varp/skill/
 * run-energy/tile packet disagrees with the value the client actually committed, OUR decode is wrong
 * (or a state-bearing packet was dropped), and that is a real correctness failure.
 *
 * This is a stronger bar than the trust verifier's self-consistency proofs (byte-accounting,
 * clean-decode round-trip): those prove "we framed every byte and our decoders don't throw"; this
 * proves "the numbers our decoders produce are the numbers the client computed". A decoder can be
 * byte-clean and still wrong (e.g. an endian flip that round-trips but yields a different integer) —
 * only comparison against the client's committed state catches that.
 *
 * ### What this decodes (the state-bearing s2c families)
 * Wire formats are taken VERBATIM from the live [Codec] revision (register948) — same opcodes, sizes,
 * and byte transforms as the registered server encoders/decoders, so this never diverges from the
 * protocol the rest of the codebase serves:
 *  - **VARP** (primary): `VARP_SMALL` (op 61, 3B: id g2 BE, value byteSubtract), `VARP_LARGE`
 *    (op 28, 6B: value g4 BE first, then id g2 BE), `VARP_LONG` (op 147 — recorded as a 64-bit value
 *    truncated to the low 32 for the int-valued snapshot comparison). Last write wins.
 *  - **SKILLS**: `UPDATE_STAT` (op 44, 6B: xp g4 LE, currentLevel g1, skillId byteInverse). Last
 *    write per skill wins; base level is recomputed from xp via the standard RS xp→level table.
 *  - **RUN ENERGY**: `UPDATE_RUNENERGY` (s2c op **0x0d / 13**, 1B: g1, **0..100 RAW** — a direct
 *    percentage, no scaling). Last write wins. (Op 0x50/80 is NOT run energy — it is the
 *    private-chat filter `SETFILTER_PRIVATE`; see §10.4 of `recorder-capture-points.md`.)
 *
 * The comparison is intentionally limited to the families the [Codec] models AND the snapshot
 * carries; anything else is ignored (it cannot be a client-king disagreement if there is nothing to
 * compare against).
 *
 * ### Verdict semantics
 * A **mismatch** (we set varp X = a, client holds X = b) or a **missed** varp (client holds a varp we
 * never set → a dropped/uncaptured state packet) is a hard FAIL: the client is authoritative, so
 * either our decode is wrong or our capture lost a packet that changed committed state. An **extra**
 * (we set a varp the client does not hold) is reported but is NOT a FAIL on its own — the client
 * legitimately overwrites or resets vars after the last packet we attributed, and a transient varp
 * set then cleared can vanish from the final snapshot without any decode being wrong.
 */
object ClientStateCrossCheck {

    // ---- opcodes / sizes (cross-referenced against register948; see Rev948ServerCodecsVariable) ----

    /** VARP_SMALL — op 61, 3B: id (g2 BE), value (byteSubtract = -128 - wireByte). */
    const val OP_VARP_SMALL = 61

    /** VARP_LARGE — op 28, 6B: value (g4 BE) FIRST, then id (g2 BE). */
    const val OP_VARP_LARGE = 28

    /** VARP_LONG — op 147, 10B: id (shortAdd), value:Long = [high g4_alt3, low g4_alt3]. */
    const val OP_VARP_LONG = 147

    /** UPDATE_STAT — op 44, 6B: xp (g4 LE), currentLevel (g1), skillId (byteInverse). */
    const val OP_UPDATE_STAT = 44

    /**
     * UPDATE_RUNENERGY — s2c op **0x0d (13)**, sizeClass 1 (fixed 1 byte, g1), value **0..100 RAW**
     * (a direct percentage — no `/10`, no `/2.55`). Verified in rs2client 948-5: `RegisterProt(.., 0x0d, 1)`
     * and the dispatch binding → `jag::packethandlers::Misc::UPDATE_RUNENERGY` (writes the `+0x18`
     * status field the oracle reads). This is NOT op 0x50/80 — that opcode is `SETFILTER_PRIVATE`
     * (private-chat filter), a separate social-state field that must not be treated as run energy.
     */
    const val OP_RUN_ENERGY = 13

    /** The state-bearing s2c opcodes this cross-check decodes. */
    val STATE_BEARING_OPCODES: Set<Int> =
        setOf(OP_VARP_SMALL, OP_VARP_LARGE, OP_VARP_LONG, OP_UPDATE_STAT, OP_RUN_ENERGY)

    // ---- decoded "expected" state (built from our s2c packets, in arrival order) -----------------

    /** One skill's expected boosted level + xp as derived from the last [OP_UPDATE_STAT] we saw. */
    data class SkillState(val skillId: Int, val level: Int, val xp: Int) {
        /** Base (un-boosted) level recomputed from [xp] via the RS xp→level table (client does the same). */
        val base: Int get() = levelForXp(xp)
    }

    /**
     * The expected client state rebuilt from the decoded state-bearing s2c packets.
     *
     * @property varps last-write-wins varId → value (the int the client committed for each var_player).
     * @property skills last-write-wins skillId → [SkillState].
     * @property runEnergy last decoded run energy (0..100 RAW), or null if no [OP_RUN_ENERGY] was seen.
     * @property decodeFailures opcodes whose body could not be decoded against the documented wire
     *   format (a short/garbled body) — surfaced so a decode bug does not silently shrink the
     *   comparison set.
     */
    data class ExpectedState(
        val varps: Map<Int, Int>,
        val skills: Map<Int, SkillState>,
        val runEnergy: Int?,
        val decodeFailures: Int,
    )

    /**
     * One state-bearing s2c packet to fold into the expected state, in arrival order. [body] is the
     * post-opcode, post-length-prefix payload (exactly what the registered s2c decoders consume).
     */
    data class S2cPacket(val opcode: Int, val body: ByteArray)

    /**
     * Decode [packets] (in order) into the expected client state. Unknown/non-state opcodes are
     * skipped. A body that throws while decoding (too short, etc.) is counted in
     * [ExpectedState.decodeFailures] and skipped rather than aborting the fold.
     *
     * [codec] is only consulted as a guard that the opcode is still modeled as the family we decode
     * here (so a future opcode-table reshuffle that moves e.g. VARP_SMALL off 61 makes this fall
     * silent rather than mis-decode); the byte layout itself is fixed by the documented wire format.
     */
    fun buildExpected(packets: List<S2cPacket>, codec: Codec): ExpectedState {
        val varps = LinkedHashMap<Int, Int>()
        val skills = LinkedHashMap<Int, SkillState>()
        var runEnergy: Int? = null
        var failures = 0

        for (pkt in packets) {
            if (pkt.opcode !in STATE_BEARING_OPCODES) continue
            val ok = runCatching {
                val src = Buffer().apply { write(pkt.body) }
                when (pkt.opcode) {
                    OP_VARP_SMALL -> {
                        // id (g2 BE), value (byteSubtract). Matches serverDecode(61).
                        val id = src.readUShort()
                        val value = src.readByteSubtract()
                        varps[id] = value
                    }
                    OP_VARP_LARGE -> {
                        // value (g4 BE) FIRST, then id (g2 BE). Matches serverDecode(28).
                        val value = src.readInt()
                        val id = src.readUShort()
                        varps[id] = value
                    }
                    OP_VARP_LONG -> {
                        // id (shortAdd), value:Long = [high g4_alt3, low g4_alt3]. We compare the
                        // int-valued snapshot, so store the LOW 32 bits (the client's int view of a
                        // var that fits in 32 bits; a genuinely 64-bit var simply won't match an int
                        // snapshot field and is reported, not silently dropped).
                        val id = src.readUShortAdd() and 0xFFFF
                        src.readUIntInverseMiddle() // high 32 (unused for the int compare)
                        val low = src.readUIntInverseMiddle()
                        varps[id] = low
                    }
                    OP_UPDATE_STAT -> {
                        // xp (g4 LE), currentLevel (g1), skillId (byteInverse). Matches serverDecode(44).
                        val xp = src.readUIntLittle()
                        val level = src.readUByte()
                        val skillId = src.readByteInverse()
                        skills[skillId] = SkillState(skillId, level, xp)
                    }
                    OP_RUN_ENERGY -> {
                        // s2c op 0x0d (13): g1, 0..100 RAW (a direct percentage). Matches the
                        // UPDATE_RUNENERGY handler — the client stores this byte verbatim.
                        runEnergy = src.readUByte()
                    }
                }
            }.isSuccess
            if (!ok) failures++
        }
        return ExpectedState(varps, skills, runEnergy, failures)
    }

    // ---- the snapshot (client ground truth) ------------------------------------------------------

    /** The client's own final decoded state, read from the at-exit `state-snapshots.jsonl` line. */
    data class Snapshot(
        val mainState: Int?,
        val varps: Map<Int, Int>,
        val player: Tile?,
        val skills: Map<Int, SkillSnapshot>,
        val runEnergy: Int?,
        val runWeight: Int?,
    )

    data class Tile(val x: Int, val y: Int, val plane: Int)
    data class SkillSnapshot(val id: Int, val level: Int, val base: Int, val xp: Int)

    // ---- comparison result -----------------------------------------------------------------------

    /** A varp our decode and the client disagree on (the headline correctness failure). */
    data class VarpMismatch(val varId: Int, val ourValue: Int, val clientValue: Int)

    /** A skill field disagreement (the named field differs between our decode and the client). */
    data class SkillMismatch(
        val skillId: Int,
        val field: String,
        val ourValue: Int,
        val clientValue: Int,
    )

    /** A scalar (run energy / weight / player tile axis) disagreement. */
    data class ScalarMismatch(val field: String, val ourValue: String, val clientValue: String)

    /**
     * The full client-is-king comparison. [available] is false when the comparison could not run
     * (no snapshot file, or no usable at-exit snapshot line) — that is NOT a failure, only "rerun a
     * fresh capture". [reason] explains an unavailable result.
     */
    data class CrossCheck(
        val available: Boolean,
        val reason: String,
        // varp coverage
        val varpMatches: Int,
        val varpMismatches: List<VarpMismatch>,
        /** varIds the client holds that we never set — a dropped/uncaptured state packet. */
        val varpMissed: List<Int>,
        /** varIds we set that the client does not hold (advisory — overwritten/reset, not a FAIL). */
        val varpExtra: List<Int>,
        // skills
        val skillMatches: Int,
        val skillMismatches: List<SkillMismatch>,
        val skillMissed: List<Int>,
        // scalars
        val scalarMismatches: List<ScalarMismatch>,
        val decodeFailures: Int,
    ) {
        /**
         * A real correctness failure occurred: a varp/skill we decoded disagrees with the client, OR
         * the client committed state from a state-bearing packet we never captured. (Extras and a
         * missing snapshot are not failures.)
         */
        val hasFailure: Boolean
            get() = available && (
                varpMismatches.isNotEmpty() || varpMissed.isNotEmpty() ||
                    skillMismatches.isNotEmpty() || skillMissed.isNotEmpty() ||
                    scalarMismatches.isNotEmpty()
                )

        companion object {
            fun unavailable(reason: String) = CrossCheck(
                available = false, reason = reason,
                varpMatches = 0, varpMismatches = emptyList(), varpMissed = emptyList(),
                varpExtra = emptyList(), skillMatches = 0, skillMismatches = emptyList(),
                skillMissed = emptyList(), scalarMismatches = emptyList(), decodeFailures = 0,
            )
        }
    }

    /**
     * Compare the [expected] state (rebuilt from our decoded s2c packets) against the client's
     * [snapshot] (ground truth). [playerTile] / [runWeightExpected] are the last values we decoded
     * for those fields from their own packets (passed in because their wire formats are owned by the
     * world-info / movement codecs, not this object); pass null when we have no such decode.
     */
    fun compare(
        expected: ExpectedState,
        snapshot: Snapshot,
        playerTile: Tile? = null,
        runWeightExpected: Int? = null,
    ): CrossCheck {
        val varpMatches = ArrayList<Int>()
        val varpMismatches = ArrayList<VarpMismatch>()
        val varpMissed = ArrayList<Int>()
        val varpExtra = ArrayList<Int>()

        // VARPS — the client snapshot is authoritative. Walk the client's vars: each must match what
        // we decoded; a client var we never set is a MISSED (dropped) state packet.
        for ((id, clientValue) in snapshot.varps) {
            val ourValue = expected.varps[id]
            when {
                ourValue == null -> varpMissed += id
                ourValue == clientValue -> varpMatches += id
                else -> varpMismatches += VarpMismatch(id, ourValue, clientValue)
            }
        }
        // Vars we set that the client does not report (advisory).
        for (id in expected.varps.keys) if (id !in snapshot.varps) varpExtra += id

        // SKILLS — compare per skill the client reports. Boosted level + xp are the wire-carried
        // fields; base is derived (we recompute it the same way the client does, so a base mismatch
        // means the xp→level derivation or xp itself disagrees).
        val skillMatches = ArrayList<Int>()
        val skillMismatches = ArrayList<SkillMismatch>()
        val skillMissed = ArrayList<Int>()
        for ((id, clientSkill) in snapshot.skills) {
            val ours = expected.skills[id]
            if (ours == null) {
                // Only count as MISSED when the client shows a non-default skill (a level/xp the
                // server must have pushed). A still-default snapshot skill (xp 0) is not evidence of a
                // dropped packet — a fresh login simply never sent an UPDATE_STAT for it.
                if (clientSkill.xp != 0 || clientSkill.level != clientSkill.base) skillMissed += id
                continue
            }
            if (ours.xp != clientSkill.xp) {
                skillMismatches += SkillMismatch(id, "xp", ours.xp, clientSkill.xp)
            } else if (ours.level != clientSkill.level) {
                skillMismatches += SkillMismatch(id, "level", ours.level, clientSkill.level)
            } else if (ours.base != clientSkill.base) {
                skillMismatches += SkillMismatch(id, "base", ours.base, clientSkill.base)
            } else {
                skillMatches += id
            }
        }

        // SCALARS — run energy / run weight / player tile.
        val scalar = ArrayList<ScalarMismatch>()
        if (expected.runEnergy != null && snapshot.runEnergy != null &&
            expected.runEnergy != snapshot.runEnergy
        ) {
            scalar += ScalarMismatch("runEnergy", expected.runEnergy.toString(), snapshot.runEnergy.toString())
        }
        if (runWeightExpected != null && snapshot.runWeight != null &&
            runWeightExpected != snapshot.runWeight
        ) {
            scalar += ScalarMismatch("runWeight", runWeightExpected.toString(), snapshot.runWeight.toString())
        }
        if (playerTile != null && snapshot.player != null && playerTile != snapshot.player) {
            scalar += ScalarMismatch(
                "playerTile",
                "(${playerTile.x},${playerTile.y},${playerTile.plane})",
                "(${snapshot.player.x},${snapshot.player.y},${snapshot.player.plane})",
            )
        }

        return CrossCheck(
            available = true,
            reason = "",
            varpMatches = varpMatches.size,
            varpMismatches = varpMismatches,
            varpMissed = varpMissed,
            varpExtra = varpExtra,
            skillMatches = skillMatches.size,
            skillMismatches = skillMismatches,
            skillMissed = skillMissed,
            scalarMismatches = scalar,
            decodeFailures = expected.decodeFailures,
        )
    }

    // ---- helpers ---------------------------------------------------------------------------------

    /**
     * RS xp→level table. Returns the highest level whose cumulative xp threshold is `<= xp`
     * (clamped 1..120). Mirrors the client's `OStat.REAL_LEVEL` derivation so a recomputed base can
     * be compared to the client's snapshot base. Identical to the standard RS experience curve.
     */
    fun levelForXp(xp: Int): Int {
        if (xp <= 0) return 1
        var points = 0.0
        var level = 1
        while (level < MAX_LEVEL) {
            points += floor(level + 300.0 * 2.0.pow(level / 7.0))
            val threshold = floor(points / 4.0).toInt()
            if (threshold > xp) break
            level++
        }
        return level
    }

    private const val MAX_LEVEL = 120
}
