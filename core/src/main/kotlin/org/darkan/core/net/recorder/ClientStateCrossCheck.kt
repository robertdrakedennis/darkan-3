package org.darkan.core.net.recorder

import kotlinx.io.Buffer
import kotlinx.io.write
import org.darkan.core.net.prot.Codec
import world.gregs.voidps.buffer.readByteInverse
import world.gregs.voidps.buffer.readByteAdd
import world.gregs.voidps.buffer.readByteSubtract
import world.gregs.voidps.buffer.readRSString
import world.gregs.voidps.buffer.readUByte
import world.gregs.voidps.buffer.readUIntInverseMiddle
import world.gregs.voidps.buffer.readUIntLittle
import world.gregs.voidps.buffer.readUIntMiddle
import world.gregs.voidps.buffer.readUShort
import world.gregs.voidps.buffer.readUShortAdd
import world.gregs.voidps.buffer.readUShortAddLittle
import world.gregs.voidps.buffer.readUShortLittle
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.pow

/**
 * Stable public wrapper for recorder-side PLAYER_INFO (op22) bodies.
 *
 * Callers provide the op22 [bytes] plus the preceding op81 GPI prefix as [GpiPrefix]. The prefix is
 * required because op22 is stateful: it mutates the render/pending slot lists seeded by op81. The
 * returned [PlayerScene] exposes the folded scene plus raw high-resolution movement records so tests
 * can assert walk/run direction bits survived an encode -> decode round-trip.
 */
object PlayerInfoDecoder {
    class GpiPrefix(
        bytes: ByteArray,
        val localPlayerIndex: Int,
    ) {
        val bytes: ByteArray = bytes.copyOf()

        init {
            require(localPlayerIndex in 1 until 2048) { "localPlayerIndex must be in 1 until 2048" }
        }
    }

    /** Decode one PLAYER_INFO op22 body against the preceding op81 GPI prefix. */
    fun decode(bytes: ByteArray, gpiPrefix: GpiPrefix): PlayerScene =
        ClientStateCrossCheck.decodePlayerScene(bytes, gpiPrefix)

    /**
     * Decode one PLAYER_INFO op22 body against the preceding op81 GPI prefix, additionally surfacing
     * the per-slot extended-info detail (mask bits + decoded bit-0x20 MOVEMENT_ANIM and bit-0x80
     * FORCED_MOVEMENT payloads). Used by the faithful-replication harness to read exactly what the
     * server sent each slot per tick (esp. the LOCAL slot's walk/glide ext-info). The byte-level mask
     * walk mirrors `Rev948PlayerUpdateMaskKey` block order; the wire formats are from
     * `re-resources/docs/net/serverprot/player-appearance-948.md`.
     */
    fun decodeDetailed(bytes: ByteArray, gpiPrefix: GpiPrefix): PlayerSceneDetailed =
        ClientStateCrossCheck.decodePlayerSceneDetailed(bytes, gpiPrefix)

    /**
     * A STATEFUL detailed-decode session. op22 PLAYER_INFO is stateful — each packet mutates the
     * render/pending slot lists seeded by op81 and evolved by every prior op22. Decoding each op22 from
     * a fresh op81 prefix (as [decodeDetailed] does) is only correct for the FIRST op22; for a multi-
     * player stream every subsequent frame desyncs. Seed this session once from the op81 prefix, then
     * feed op22 bodies in arrival order via [next]; the fold carries slot state across frames.
     */
    class DetailedSession(gpiPrefix: GpiPrefix) {
        val localIndex: Int = gpiPrefix.localPlayerIndex
        // Opaque handle (the fold class is private to ClientStateCrossCheck); the helpers cast it back.
        private val fold: Any = ClientStateCrossCheck.newSeededFold(gpiPrefix)

        /** Decode the next op22 body against the carried-over slot state. */
        fun next(bytes: ByteArray): PlayerSceneDetailed =
            ClientStateCrossCheck.decodeDetailedOnFold(fold, bytes, localIndex)
    }
}

/**
 * Decoded bit-0x20 MOVEMENT_ANIM ext-info payload: 4× gSmart2or4s (bigSmart) movement-anim seq ids +
 * 1× g1_sub priority byte. `-1`/`-1`/`-1`/`-1` is the "stop the seq" / ResetMovementSeqs form.
 */
data class ExtMovementAnim(
    val seq0: Int,
    val seq1: Int,
    val seq2: Int,
    val seq3: Int,
    val priority: Int,
) {
    val isResetForm: Boolean get() = seq0 == -1 && seq1 == -1 && seq2 == -1 && seq3 == -1
    val seqs: List<Int> get() = listOf(seq0, seq1, seq2, seq3)
}

/**
 * Decoded bit-0x80 FORCED_MOVEMENT (glide) ext-info payload: 12 bytes → `SetRenderWaypoint`.
 * Fields are the recovered (post-transform) signed values: src/dst fine tile deltas, two plane biases,
 * the two POSITIVE tick deltas the client adds to its live cycle counter, and the 14-bit render yaw.
 */
data class ExtForcedMovement(
    val srcDx: Int,
    val srcDz: Int,
    val dstDx: Int,
    val dstDz: Int,
    val delta3: Int,
    val delta4: Int,
    val startTick: Int,
    val endTick: Int,
    val yaw: Int,
)

/**
 * The decoded extended-info block for one slot in one op22. [maskBits] is the raw OR of every flag bit
 * present in the LE bitset header; [movementAnim]/[forcedMovement] are decoded when bits 0x20/0x80 are
 * present. [parsedClean] is false when the per-bit walk hit an un-modelled bit and fell back to a
 * mask-only read (so payload fields beyond the failure point are absent, not silently wrong).
 */
data class SlotExtInfo(
    val index: Int,
    val maskBits: Int,
    val hasAppearance: Boolean,
    val movementAnim: ExtMovementAnim?,
    val forcedMovement: ExtForcedMovement?,
    val parsedClean: Boolean,
) {
    fun hasBit(flag: Int): Boolean = maskBits and flag != 0
    val maskBitList: List<Int>
        get() = (0 until 32).filter { maskBits and (1 shl it) != 0 }.map { 1 shl it }
}

/**
 * [PlayerScene] plus per-slot extended-info detail keyed by slot index, in the op22 ext-info dispatch
 * order. [localIndex] echoes the GPI prefix's local slot for caller convenience.
 */
data class PlayerSceneDetailed(
    val scene: PlayerScene,
    val extInfo: Map<Int, SlotExtInfo>,
    val extInfoOrder: List<Int>,
    val localIndex: Int,
    /** True iff the 4-pass GPI bit-decode completed without a skip-run desync fallback. When false the
     *  movement/ext-info detail is unreliable for this frame (the bit cursor did not cleanly reach the
     *  ext-info section, usually because the local index is wrong or the body is truncated/garbled). */
    val decodeClean: Boolean,
)

/**
 * Folded PLAYER_INFO scene. [movements] is ordered by the op22 high-resolution pass walk order.
 */
data class PlayerScene(
    val players: Map<Int, ClientStateCrossCheck.ScenePlayerState>,
    val movements: List<PlayerMovement>,
    val appearance: Map<Int, ClientStateCrossCheck.AppearanceSlotState> = emptyMap(),
)

/**
 * Raw high-resolution movement bits retained from op22.
 *
 * [dir] is the raw direction payload for movement types that carry one: 3 bits for walk
 * (`movementType == 1`) and the raw 4-bit run payload for `movementType == 2`. [followup] is the
 * optional 2-bit walk follow-up payload, present only when the wire's follow-up flag is set.
 *
 * [descriptor] is the move-mode descriptor index for `movementType == 3` (and only that type) — the
 * ENTRY index the client uses to index the move-mode descriptor table at `&DAT_100f13a30 +
 * descriptor*4` in `jag::packethandlers::PlayerInfo::DecodeKnownPlayerUpdate` (rs2client 948-5
 * @0x100025da6 / @0x100026493). **`descriptor == 4` selects the SMOOTH descriptor `&DAT_100f13a40`
 * (`*0x100ed2ab0` points here; byte offset 0x10 from the base)** → `SetScenePosition` →
 * `SetTranslationSmoothed` (continuous GLIDE, self-animating); any other descriptor →
 * `AttachToMapSquare` (instant tile SNAP, idle). For the large form it is the raw 3-bit field; for the
 * small (15-bit) form the client computes the table BYTE offset as `(code >> 10) & 0x1c`
 * (@0x1000263cd), so the entry index is `((code >> 10) & 0x1c) >> 2 == (code >> 12) & 0x7`.
 */
data class PlayerMovement(
    val index: Int,
    val movementType: Int,
    val dir: Int?,
    val hasExt: Boolean,
    val followup: Int?,
    val descriptor: Int? = null,
    /** mvt=3 only: true = large form (`[1][3-bit desc][30-bit pos]`), false = small 15-bit form. */
    val large: Boolean? = null,
    /** mvt=3 only: the raw payload field — the 30-bit pos (large form) or the 15-bit code (small form). */
    val payload: Int? = null,
    /**
     * The slot's high-resolution tile AFTER applying this movement, as maintained by the decode. For
     * a high-res walk (`movementType==1`) this is `prevTile + DX/DY[dir]` — byte-exact (the dir→delta
     * table is the same `PLAYER_REGION_DX/DY` the encoder inverts). For `movementType==0` it is the
     * UNCHANGED tile (no forced movement). For run/teleport (`movementType==2/3`) it is best-effort
     * (see [tileExact]). `null` only when the slot had no prior tile to advance from (e.g. a remote
     * slot that has not yet promoted to high-res). This is the per-tick LOCAL-slot path the probe reads.
     */
    val tileAfter: ClientStateCrossCheck.Tile? = null,
    /**
     * True iff [tileAfter] is byte-exactly reconstructed from the wire (mvt 0 = unchanged, mvt 1 =
     * verified `DX/DY` walk delta). False for mvt 2 (run — the 4-bit→delta table is NOT binary-verified
     * here, so the tile is advanced best-effort and flagged) and mvt 3 (move-mode/teleport — the
     * decode does not reconstruct the absolute destination). Lets the probe label uncertain tiles
     * honestly instead of asserting a fabricated position.
     */
    val tileExact: Boolean = true,
)

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
 *  - **VARC** (client vars): `CLIENT_SETVARC_SMALL` (op 47), `CLIENT_SETVARC_LARGE` (op 64),
 *    `CLIENT_SETVARC_LONG` (op 196), `CLIENT_SETVARC_STR` (op 92), and
 *    `CLIENT_SETVARC_STR_LARGE` (op 116). Last write wins, keyed by `(recordKind,varId)`.
 *    Varcbit ops 48/69 are deliberately not folded yet; predicting them needs cache varcbit defs.
 *  - **INVENTORIES**: `UPDATE_INV_FULL` (op 85) replaces one container and `UPDATE_INV_PARTIAL`
 *    (op 121) mutates slots. Last write wins per `(inventoryId,slot)`; item id `-1` clears a slot.
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

    /** RESET_CLIENT_VARCACHE — op 5, 0B: invalidates/reset client-var expected state. */
    const val OP_RESET_CLIENT_VARCACHE = 5

    /** CLIENT_SETVARC_SMALL — op 47, 3B: value byteAdd, id u16 LE. */
    const val OP_CLIENT_SETVARC_SMALL = 47

    /** CLIENT_SETVARC_LARGE — op 64, 6B: value intMiddle, id shortAddLittle. */
    const val OP_CLIENT_SETVARC_LARGE = 64

    /** CLIENT_SETVARC_STR — op 92, varByte: id u16 LE, then CP1252/RS string. */
    const val OP_CLIENT_SETVARC_STR = 92

    /** CLIENT_SETVARC_STR_LARGE — op 116, varShort: CP1252/RS string, then shortAdd id. */
    const val OP_CLIENT_SETVARC_STR_LARGE = 116

    /** CLIENT_SETVARC_LONG — op 196, 10B: value g8 BE, then id g2 BE. */
    const val OP_CLIENT_SETVARC_LONG = 196

    /** CLIENT_SETVARCBIT_* are binary-proven but not folded until cache varcbit defs are wired. */
    const val OP_CLIENT_SETVARCBIT_SMALL = 48
    const val OP_CLIENT_SETVARCBIT_LARGE = 69

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

    /** UPDATE_INV_FULL — op 85, varShort: inventoryId, flags, entry count, then full slot array. */
    const val OP_UPDATE_INV_FULL = 85

    /** UPDATE_INV_PARTIAL — op 121, varShort: inventoryId, flags, then slot mutations until EOF. */
    const val OP_UPDATE_INV_PARTIAL = 121

    /** REBUILD_NORMAL_SIMPLE — op 81, varShort: carries the op81 GPI prefix that seeds op22 slots. */
    const val OP_REBUILD_NORMAL_SIMPLE = 81

    /** PLAYER_INFO — op 22, varShort: bit-packed GPI player list + ext-info blocks. */
    const val OP_PLAYER_INFO = 22

    /** NPC_INFO — op 52, varShort: bit-packed NPC list + ext-info blocks. */
    const val OP_NPC_INFO = 52

    /** The state-bearing s2c opcodes this cross-check decodes. */
    val STATE_BEARING_OPCODES: Set<Int> =
        setOf(
            OP_VARP_SMALL, OP_VARP_LARGE, OP_VARP_LONG,
            OP_RESET_CLIENT_VARCACHE, OP_CLIENT_SETVARC_SMALL, OP_CLIENT_SETVARC_LARGE,
            OP_CLIENT_SETVARC_STR, OP_CLIENT_SETVARC_STR_LARGE, OP_CLIENT_SETVARC_LONG,
            OP_UPDATE_STAT, OP_RUN_ENERGY,
            OP_UPDATE_INV_FULL, OP_UPDATE_INV_PARTIAL,
            OP_REBUILD_NORMAL_SIMPLE, OP_PLAYER_INFO, OP_NPC_INFO,
        )

    /**
     * Opcodes promoted to CLIENT_VERIFIED when this cross-check is available and clean. Excludes
     * reset op5 (no value to verify) and varcbit 48/69 (deferred until cache varcbit defs are wired).
     */
    val CLIENT_VERIFIED_OPCODES: Set<Int> =
        setOf(
            OP_VARP_SMALL, OP_VARP_LARGE, OP_VARP_LONG,
            OP_CLIENT_SETVARC_SMALL, OP_CLIENT_SETVARC_LARGE, OP_CLIENT_SETVARC_LONG,
            OP_CLIENT_SETVARC_STR, OP_CLIENT_SETVARC_STR_LARGE,
            OP_UPDATE_STAT, OP_RUN_ENERGY,
            OP_UPDATE_INV_FULL, OP_UPDATE_INV_PARTIAL,
            OP_PLAYER_INFO, OP_NPC_INFO,
        )

    val DEFERRED_VARCBIT_OPCODES: Set<Int> = setOf(OP_CLIENT_SETVARCBIT_SMALL, OP_CLIENT_SETVARCBIT_LARGE)

    // Ghidra-confirmed writer constants:
    // - CLIENT_SETVARC_SMALL/LARGE/LONG call GetOrCreateInterfaceRecord(domain, 1, varId, ...)
    // - SetStringVarRecord calls GetOrCreateInterfaceRecord(domain, 2, varId, ...)
    const val VARC_RECORD_KIND_NUMERIC = 1
    const val VARC_RECORD_KIND_STRING = 2
    const val VARC_VALUE_KIND_INT32 = 0
    const val VARC_VALUE_KIND_INT64 = 1
    const val VARC_VALUE_KIND_STRING = 2
    val CLIENT_DYNAMIC_VARPS: Set<Int> = setOf(3913, 3914, 3915)
    private const val UNKNOWN_SOURCE_OPCODE = -1

    // ---- decoded "expected" state (built from our s2c packets, in arrival order) -----------------

    /** One skill's expected boosted level + xp as derived from the last [OP_UPDATE_STAT] we saw. */
    data class SkillState(
        val skillId: Int,
        val level: Int,
        val xp: Int,
        val sourceOpcode: Int = OP_UPDATE_STAT,
    ) {
        /** Base (un-boosted) level recomputed from [xp] via the RS xp→level table (client does the same). */
        val base: Int get() = levelForXp(xp)
    }

    /** Varc key. The client record tree is keyed by BOTH record kind and var id. */
    data class VarcKey(val recordKind: Int, val varId: Int) : Comparable<VarcKey> {
        override fun compareTo(other: VarcKey): Int =
            compareValuesBy(this, other, VarcKey::recordKind, VarcKey::varId)

        override fun toString(): String = "kind$recordKind/var$varId"
    }

    /** Numeric/long varc state: valueKind 0=int32, 1=int64. */
    data class VarcNumberState(
        val key: VarcKey,
        val valueKind: Int,
        val value: Long,
        val sourceOpcode: Int = UNKNOWN_SOURCE_OPCODE,
    )

    /** String varc state: valueKind 2=string. */
    data class VarcStringState(
        val key: VarcKey,
        val valueKind: Int,
        val value: String,
        val sourceOpcode: Int = UNKNOWN_SOURCE_OPCODE,
    )

    /** One occupied inventory slot. Empty item `-1` slots are not retained in folded state. */
    data class InventorySlotState(
        val invId: Int,
        val slot: Int,
        val itemId: Int,
        val count: Long,
        val sourceOpcode: Int = UNKNOWN_SOURCE_OPCODE,
    )

    /** Occupied slots for one inventory id. Snapshot keys by derived `invId`, not raw oracle key. */
    data class InventoryState(
        val invId: Int,
        val slots: Map<Int, InventorySlotState>,
    )

    /** One local-player appearance slot. `-1` means empty for that identity domain. */
    data class AppearanceSlotState(
        val slot: Int,
        val kitId: Int,
        val itemId: Int,
        val sourceOpcode: Int = UNKNOWN_SOURCE_OPCODE,
    )

    /** Scene player presence/position state folded from op22. */
    data class ScenePlayerState(
        val idx: Int,
        val tile: Tile? = null,
        val sourceOpcode: Int = OP_PLAYER_INFO,
    )

    /** Scene NPC presence/type/position state folded from op52. */
    data class SceneNpcState(
        val idx: Int,
        val typeId: Int? = null,
        val tile: Tile? = null,
        val sourceOpcode: Int = OP_NPC_INFO,
    )

    /**
     * The expected client state rebuilt from the decoded state-bearing s2c packets.
     *
     * @property varps last-write-wins varId → value (the int the client committed for each var_player).
     * @property varcs last-write-wins numeric client var key → value.
     * @property varcStrings last-write-wins string client var key → string.
     * @property skills last-write-wins skillId → [SkillState].
     * @property inventories last-write-wins inventoryId → occupied slot map.
     * @property runEnergy last decoded run energy (0..100 RAW), or null if no [OP_RUN_ENERGY] was seen.
     * @property decodeFailures opcodes whose body could not be decoded against the documented wire
     *   format (a short/garbled body) — surfaced so a decode bug does not silently shrink the
     *   comparison set.
     */
    data class ExpectedState(
        val varps: Map<Int, Int>,
        val varpSources: Map<Int, Int>,
        val varcs: Map<VarcKey, VarcNumberState>,
        val varcStrings: Map<VarcKey, VarcStringState>,
        val skills: Map<Int, SkillState>,
        val inventories: Map<Int, InventoryState>,
        val appearance: Map<Int, AppearanceSlotState>,
        val scenePlayers: Map<Int, ScenePlayerState>,
        val playerMovements: List<PlayerMovement>,
        val sceneNpcs: Map<Int, SceneNpcState>,
        val runEnergy: Int?,
        val runEnergySourceOpcode: Int?,
        val decodeFailures: Int,
    )

    /**
     * One state-bearing s2c packet to fold into the expected state, in arrival order. [body] is the
     * post-opcode, post-length-prefix payload (exactly what the registered s2c decoders consume).
     */
    data class S2cPacket(
        val opcode: Int,
        val body: ByteArray,
        val monoUs: Long? = null,
        val localTile: Tile? = null,
        val localPlayerIndex: Int? = null,
    )

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
        val varpSources = LinkedHashMap<Int, Int>()
        val varcs = LinkedHashMap<VarcKey, VarcNumberState>()
        val varcStrings = LinkedHashMap<VarcKey, VarcStringState>()
        val skills = LinkedHashMap<Int, SkillState>()
        val inventories = LinkedHashMap<Int, InventoryState>()
        val playerFold = PlayerSceneFold()
        val npcFold = NpcSceneFold()
        var runEnergy: Int? = null
        var runEnergySourceOpcode: Int? = null
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
                        varpSources[id] = pkt.opcode
                    }
                    OP_VARP_LARGE -> {
                        // value (g4 BE) FIRST, then id (g2 BE). Matches serverDecode(28).
                        val value = src.readInt()
                        val id = src.readUShort()
                        varps[id] = value
                        varpSources[id] = pkt.opcode
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
                        varpSources[id] = pkt.opcode
                    }
                    OP_RESET_CLIENT_VARCACHE -> {
                        varcs.clear()
                        varcStrings.clear()
                    }
                    OP_CLIENT_SETVARC_SMALL -> {
                        val value = src.readByteAdd()
                        val id = src.readUShortLittle()
                        val key = VarcKey(VARC_RECORD_KIND_NUMERIC, id)
                        varcs[key] = VarcNumberState(key, VARC_VALUE_KIND_INT32, value.toLong(), pkt.opcode)
                    }
                    OP_CLIENT_SETVARC_LARGE -> {
                        val value = src.readUIntMiddle()
                        val id = src.readUShortAddLittle()
                        val key = VarcKey(VARC_RECORD_KIND_NUMERIC, id)
                        varcs[key] = VarcNumberState(key, VARC_VALUE_KIND_INT32, value.toLong(), pkt.opcode)
                    }
                    OP_CLIENT_SETVARC_LONG -> {
                        // Ghidra 0x100049b10: load 8 bytes at packet pos, BSWAP, then id at +8 (g2 BE).
                        val value = src.readLong()
                        val id = src.readUShort()
                        val key = VarcKey(VARC_RECORD_KIND_NUMERIC, id)
                        varcs[key] = VarcNumberState(key, VARC_VALUE_KIND_INT64, value, pkt.opcode)
                    }
                    OP_CLIENT_SETVARC_STR -> {
                        val id = src.readUShortLittle()
                        val value = src.readRSString()
                        val key = VarcKey(VARC_RECORD_KIND_STRING, id)
                        varcStrings[key] = VarcStringState(key, VARC_VALUE_KIND_STRING, value, pkt.opcode)
                    }
                    OP_CLIENT_SETVARC_STR_LARGE -> {
                        val value = src.readRSString()
                        val id = src.readUShortAdd() and 0xFFFF
                        val key = VarcKey(VARC_RECORD_KIND_STRING, id)
                        varcStrings[key] = VarcStringState(key, VARC_VALUE_KIND_STRING, value, pkt.opcode)
                    }
                    OP_UPDATE_STAT -> {
                        // xp (g4 LE), currentLevel (g1), skillId (byteInverse). Matches serverDecode(44).
                        val xp = src.readUIntLittle()
                        val level = src.readUByte()
                        val skillId = src.readByteInverse()
                        skills[skillId] = SkillState(skillId, level, xp, pkt.opcode)
                    }
                    OP_RUN_ENERGY -> {
                        // s2c op 0x0d (13): g1, 0..100 RAW (a direct percentage). Matches the
                        // UPDATE_RUNENERGY handler — the client stores this byte verbatim.
                        runEnergy = src.readUByte()
                        runEnergySourceOpcode = pkt.opcode
                    }
                    OP_UPDATE_INV_FULL -> {
                        val inventory = decodeInventoryFull(pkt.body, pkt.opcode)
                        inventories[inventory.invId] = inventory
                    }
                    OP_UPDATE_INV_PARTIAL -> {
                        val inventory = decodeInventoryPartial(pkt.body, inventories, pkt.opcode)
                        inventories[inventory.invId] = inventory
                    }
                    OP_REBUILD_NORMAL_SIMPLE -> {
                        decodeGpiPrefix(pkt.body, playerFold, pkt.localPlayerIndex)
                    }
                    OP_PLAYER_INFO -> {
                        decodePlayerInfo(pkt.body, playerFold, pkt.opcode)
                    }
                    OP_NPC_INFO -> {
                        decodeNpcInfo(pkt.body, npcFold, pkt.localTile, pkt.opcode)
                    }
                }
            }.isSuccess
            if (!ok) failures++
        }
        return ExpectedState(
            varps = varps,
            varpSources = varpSources,
            varcs = varcs,
            varcStrings = varcStrings,
            skills = skills,
            inventories = inventories,
            appearance = playerFold.appearanceState().withEquipmentAppearance(inventories),
            scenePlayers = playerFold.states(),
            playerMovements = playerFold.movements(),
            sceneNpcs = npcFold.states(),
            runEnergy = runEnergy,
            runEnergySourceOpcode = runEnergySourceOpcode,
            decodeFailures = failures,
        )
    }

    /** Decode one op22 body against one op81 GPI prefix for round-trip/self-test callers. */
    internal fun decodePlayerScene(bytes: ByteArray, gpiPrefix: PlayerInfoDecoder.GpiPrefix): PlayerScene {
        require(gpiPrefix.bytes.size >= GPI_PREFIX_BYTES) {
            "GPI prefix must contain at least $GPI_PREFIX_BYTES bytes"
        }
        val fold = PlayerSceneFold()
        decodeGpiPrefix(gpiPrefix.bytes, fold, gpiPrefix.localPlayerIndex)
        decodePlayerInfo(bytes, fold, OP_PLAYER_INFO)
        return PlayerScene(
            players = fold.states(),
            movements = fold.movements(),
            appearance = fold.appearanceState(),
        )
    }

    /**
     * Like [decodePlayerScene] but also surfaces per-slot ext-info detail. Runs the same 4-pass GPI
     * bit-decode, then re-walks the trailing ext-info `[u16 len][bytes]` blocks (one per slot in
     * [PlayerSceneFold.lastExtInfoOrder]) decoding each block's mask + bit-0x20/0x80 payloads.
     */
    internal fun decodePlayerSceneDetailed(
        bytes: ByteArray,
        gpiPrefix: PlayerInfoDecoder.GpiPrefix,
    ): PlayerSceneDetailed {
        require(gpiPrefix.bytes.size >= GPI_PREFIX_BYTES) {
            "GPI prefix must contain at least $GPI_PREFIX_BYTES bytes"
        }
        val fold = PlayerSceneFold()
        decodeGpiPrefix(gpiPrefix.bytes, fold, gpiPrefix.localPlayerIndex)
        decodePlayerInfo(bytes, fold, OP_PLAYER_INFO)
        val scene = PlayerScene(
            players = fold.states(),
            movements = fold.movements(),
            appearance = fold.appearanceState(),
        )
        val order = fold.lastExtInfoOrder
        val detail = decodeExtInfoDetail(bytes, fold.lastExtInfoOffset, order)
        return PlayerSceneDetailed(
            scene = scene,
            extInfo = detail,
            extInfoOrder = order,
            localIndex = gpiPrefix.localPlayerIndex,
            decodeClean = fold.lastDecodeClean,
        )
    }

    /** Seed a fresh fold from an op81 GPI prefix for a stateful [PlayerInfoDecoder.DetailedSession]. */
    internal fun newSeededFold(gpiPrefix: PlayerInfoDecoder.GpiPrefix): Any {
        require(gpiPrefix.bytes.size >= GPI_PREFIX_BYTES) {
            "GPI prefix must contain at least $GPI_PREFIX_BYTES bytes"
        }
        val fold = PlayerSceneFold()
        decodeGpiPrefix(gpiPrefix.bytes, fold, gpiPrefix.localPlayerIndex)
        return fold
    }

    /** Decode one op22 on a carried-over fold (stateful — mutates the fold's slot lists). The fold's
     *  movement list accumulates across frames, so we snapshot only THIS frame's new movements. */
    internal fun decodeDetailedOnFold(foldHandle: Any, bytes: ByteArray, localIndex: Int): PlayerSceneDetailed {
        val fold = foldHandle as PlayerSceneFold
        val movementsBefore = fold.movementCount
        // A desync throws (and runs the appearance-scan fallback inside); swallow it so one bad frame
        // does not kill the session — the result's decodeClean=false flags the frame as unreliable.
        runCatching { decodePlayerInfo(bytes, fold, OP_PLAYER_INFO) }
        val scene = PlayerScene(
            players = fold.states(),
            movements = fold.movementsSince(movementsBefore),
            appearance = fold.appearanceState(),
        )
        val order = fold.lastExtInfoOrder
        val detail = decodeExtInfoDetail(bytes, fold.lastExtInfoOffset, order)
        return PlayerSceneDetailed(
            scene = scene,
            extInfo = detail,
            extInfoOrder = order,
            localIndex = localIndex,
            decodeClean = fold.lastDecodeClean,
        )
    }

    /**
     * Re-walk the trailing ext-info blocks (`[u16 len][block bytes]` per slot in [order]) decoding
     * each block's LE mask header + the modelled bit-0x08/0x20/0x80 payloads. Each block is parsed in
     * isolation (the length frames it), so a bit we don't model only loses THAT slot's detail past the
     * unknown bit (`parsedClean=false`), never the whole packet.
     */
    private fun decodeExtInfoDetail(
        body: ByteArray,
        offset: Int,
        order: List<Int>,
    ): Map<Int, SlotExtInfo> {
        val out = LinkedHashMap<Int, SlotExtInfo>()
        var cursor = offset
        for (idx in order) {
            if (cursor + 2 > body.size) break
            val length = ((body[cursor].toInt() and 0xff) shl 8) or (body[cursor + 1].toInt() and 0xff)
            cursor += 2
            if (cursor + length > body.size) break
            val block = body.copyOfRange(cursor, cursor + length)
            cursor += length
            out[idx] = parsePlayerExtInfoBlock(idx, block)
        }
        return out
    }

    /**
     * Parse ONE player ext-info block (after the u16 length is stripped): the 1..4 byte LE mask header,
     * then per-flag payloads in ascending [Rev948PlayerUpdateMaskKey] order. We fully decode bit 0x08
     * (appearance → hasAppearance), bit 0x20 (MOVEMENT_ANIM), bit 0x80 (FORCED_MOVEMENT) and skip the
     * other modelled blocks by their documented widths. An un-modelled bit stops the walk early and
     * marks [SlotExtInfo.parsedClean]=false (the mask is still reported).
     */
    private fun parsePlayerExtInfoBlock(idx: Int, block: ByteArray): SlotExtInfo {
        val src = ByteBodyReader(block)
        val mask = runCatching { src.readPlayerMask() }.getOrElse {
            return SlotExtInfo(idx, 0, false, null, null, parsedClean = false)
        }
        var anim: ExtMovementAnim? = null
        var forced: ExtForcedMovement? = null
        var clean = true

        // Walk EVERY set bit in the client's fixed dispatch order (ascending order). For each we either
        // fully decode (0x08/0x20/0x80), skip by a documented fixed width, or — for any bit whose width
        // we do not model — STOP and report mask-only (parsedClean=false) so the cursor is never walked
        // past an unknown block and a later 0x20/0x80 mis-decoded. On the LOCAL slot prod sets only
        // 0x08/0x20/0x80 (+ occasionally 0x02), which are all modelled, so clean stays true there.
        for (e in PLAYER_EXT_ORDER) {
            if (mask and e.flag == 0) continue
            val ok = runCatching {
                when (e.kind) {
                    ExtKind.APPEARANCE -> src.skipAppearanceBlock()
                    ExtKind.MOVEMENT_ANIM -> anim = src.readMovementAnimBlock()
                    ExtKind.FORCED_MOVEMENT -> forced = src.readForcedMovementBlock()
                    ExtKind.FIXED -> src.skipFixed(e.skipBytes)
                    ExtKind.UNMODELLED -> throw IllegalStateException("unmodelled ext-info bit ${e.flag}")
                }
            }.isSuccess
            if (!ok) {
                clean = false
                break
            }
        }
        return SlotExtInfo(
            index = idx,
            maskBits = mask,
            hasAppearance = mask and (1 shl PLAYER_APPEARANCE_MASK_BIT) != 0,
            movementAnim = anim,
            forcedMovement = forced,
            parsedClean = clean,
        )
    }

    // ---- the snapshot (client ground truth) ------------------------------------------------------

    /** The client's own final decoded state, read from the at-exit `state-snapshots.jsonl` line. */
    data class Snapshot(
        val mainState: Int?,
        val varps: Map<Int, Int>,
        val varcs: Map<VarcKey, VarcNumberState>,
        val varcStrings: Map<VarcKey, VarcStringState>,
        val varcsAvailable: Boolean,
        val varcStringsAvailable: Boolean,
        val inventories: Map<Int, InventoryState>,
        val players: Map<Int, ScenePlayerState>,
        val npcs: Map<Int, SceneNpcState>,
        val appearance: Map<Int, AppearanceSlotState>,
        val appearanceAvailable: Boolean,
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

    /** A known client-local varp mutation, proven by snapshot history and reported as advisory. */
    data class VarpAdvisory(
        val varId: Int,
        val ourValue: Int,
        val clientValue: Int,
        val evidence: String,
    )

    /** A varc our decode and the client disagree on. [ourValue]/[clientValue] are legacy detail strings. */
    data class VarcMismatch(
        val key: VarcKey,
        val ourValue: String,
        val clientValue: String,
        val ourValueKind: Int,
        val clientValueKind: Int,
        val ourNumberValue: Long? = null,
        val clientNumberValue: Long? = null,
        val ourStringValue: String? = null,
        val clientStringValue: String? = null,
    )

    /** A skill field disagreement (the named field differs between our decode and the client). */
    data class SkillMismatch(
        val skillId: Int,
        val field: String,
        val ourValue: Int,
        val clientValue: Int,
    )

    /** Inventory slot disagreement. `clientItemId == -1` means snapshot had no occupied slot. */
    data class InventoryMismatch(
        val invId: Int,
        val slot: Int,
        val ourItemId: Int,
        val ourCount: Long,
        val clientItemId: Int,
        val clientCount: Long,
    )

    /** Local-player appearance identity disagreement. */
    data class AppearanceMismatch(
        val slot: Int,
        val ourKitId: Int,
        val ourItemId: Int,
        val clientKitId: Int,
        val clientItemId: Int,
    )

    /** A scalar (run energy / weight / player tile axis) disagreement. */
    data class ScalarMismatch(val field: String, val ourValue: String, val clientValue: String)

    /** Scene presence disagreement: one side believes an entity index is in-scene and the other does not. */
    data class ScenePresenceMismatch(
        val family: String,
        val idx: Int,
        val expectedPresent: Boolean,
        val clientPresent: Boolean,
        val expectedTypeId: Int? = null,
        val clientTypeId: Int? = null,
    )

    /** NPC type id disagreement for an index whose presence matched. */
    data class SceneNpcTypeMismatch(
        val idx: Int,
        val expectedTypeId: Int,
        val clientTypeId: Int,
    )

    /** Best-effort scene tile disagreement for entities with a stable folded tile. */
    data class ScenePositionMismatch(
        val family: String,
        val idx: Int,
        val expectedTile: Tile,
        val clientTile: Tile,
        val typeId: Int? = null,
    )

    /**
     * Per-opcode client-oracle proof. An opcode only earns CLIENT_VERIFIED when its own writes have
     * at least one client-grounded match and zero client-grounded mismatches.
     */
    data class OpcodeWriteCheck(
        val opcode: Int,
        val domain: String,
        val matches: Int,
        val mismatches: Int,
        val unverified: Int,
    ) {
        val clientVerified: Boolean
            get() = matches > 0 && mismatches == 0
    }

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
        /** Known client-local varp mutations (not hard failures once drift is proven). */
        val varpAdvisory: List<VarpAdvisory>,
        /** varIds the client holds that we never set — a dropped/uncaptured state packet. */
        val varpMissed: List<Int>,
        /** varIds we set that the client does not hold (advisory — overwritten/reset, not a FAIL). */
        val varpExtra: List<Int>,
        // numeric/long varcs
        val varcMatches: Int,
        val varcMismatches: List<VarcMismatch>,
        /** varc keys the client holds that we never set — a dropped/uncaptured state packet. */
        val varcMissed: List<VarcNumberState>,
        /** varc writes we folded but the snapshot carried no oracle value for. */
        val varcUnverified: List<VarcNumberState>,
        /** varcs we set that the client does not hold (advisory — overwritten/reset, not a FAIL). */
        val varcExtra: List<VarcNumberState>,
        val varcsAvailable: Boolean,
        // string varcs
        val varcStringMatches: Int,
        val varcStringMismatches: List<VarcMismatch>,
        val varcStringMissed: List<VarcStringState>,
        val varcStringUnverified: List<VarcStringState>,
        val varcStringExtra: List<VarcStringState>,
        val varcStringsAvailable: Boolean,
        // skills
        val skillMatches: Int,
        val skillMismatches: List<SkillMismatch>,
        val skillMissed: List<Int>,
        // inventories
        val inventoryMatches: Int,
        val inventoryMismatches: List<InventoryMismatch>,
        val inventoryMissed: List<InventorySlotState>,
        // local player appearance
        val appearanceMatches: Int,
        val appearanceMismatches: List<AppearanceMismatch>,
        val appearanceMissed: List<AppearanceSlotState>,
        val appearanceUnverified: List<AppearanceSlotState>,
        val appearanceAvailable: Boolean,
        // scene entities
        val scenePlayerPresenceMatches: Int,
        val scenePlayerPresenceMismatches: List<ScenePresenceMismatch>,
        val scenePlayerPositionMatches: Int,
        val scenePlayerPositionMismatches: List<ScenePositionMismatch>,
        val sceneNpcPresenceMatches: Int,
        val sceneNpcPresenceMismatches: List<ScenePresenceMismatch>,
        val sceneNpcTypeMatches: Int,
        val sceneNpcTypeMismatches: List<SceneNpcTypeMismatch>,
        val sceneNpcPositionMatches: Int,
        val sceneNpcPositionMismatches: List<ScenePositionMismatch>,
        // scalars
        val scalarMismatches: List<ScalarMismatch>,
        val opcodeWriteChecks: List<OpcodeWriteCheck>,
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
                    varcMismatches.isNotEmpty() || varcMissed.isNotEmpty() ||
                    varcStringMismatches.isNotEmpty() || varcStringMissed.isNotEmpty() ||
                    skillMismatches.isNotEmpty() || skillMissed.isNotEmpty() ||
                    inventoryMismatches.isNotEmpty() || inventoryMissed.isNotEmpty() ||
                    appearanceMismatches.isNotEmpty() || appearanceMissed.isNotEmpty() ||
                    scenePlayerPresenceMismatches.isNotEmpty() ||
                    scenePlayerPositionMismatches.isNotEmpty() ||
                    sceneNpcPresenceMismatches.isNotEmpty() ||
                    sceneNpcTypeMismatches.isNotEmpty() ||
                    sceneNpcPositionMismatches.isNotEmpty() ||
                    scalarMismatches.isNotEmpty()
                )

        companion object {
            fun unavailable(reason: String) = CrossCheck(
                available = false, reason = reason,
                varpMatches = 0, varpMismatches = emptyList(), varpMissed = emptyList(),
                varpAdvisory = emptyList(), varpExtra = emptyList(), varcMatches = 0, varcMismatches = emptyList(),
                varcMissed = emptyList(), varcUnverified = emptyList(), varcExtra = emptyList(), varcsAvailable = false,
                varcStringMatches = 0, varcStringMismatches = emptyList(), varcStringMissed = emptyList(),
                varcStringUnverified = emptyList(),
                varcStringExtra = emptyList(), varcStringsAvailable = false,
                skillMatches = 0, skillMismatches = emptyList(),
                skillMissed = emptyList(),
                inventoryMatches = 0, inventoryMismatches = emptyList(), inventoryMissed = emptyList(),
                appearanceMatches = 0, appearanceMismatches = emptyList(), appearanceMissed = emptyList(),
                appearanceUnverified = emptyList(), appearanceAvailable = false,
                scenePlayerPresenceMatches = 0, scenePlayerPresenceMismatches = emptyList(),
                scenePlayerPositionMatches = 0, scenePlayerPositionMismatches = emptyList(),
                sceneNpcPresenceMatches = 0, sceneNpcPresenceMismatches = emptyList(),
                sceneNpcTypeMatches = 0, sceneNpcTypeMismatches = emptyList(),
                sceneNpcPositionMatches = 0, sceneNpcPositionMismatches = emptyList(),
                scalarMismatches = emptyList(),
                opcodeWriteChecks = emptyList(), decodeFailures = 0,
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
        dynamicVarpEvidence: Map<Int, String> = emptyMap(),
    ): CrossCheck {
        data class MutableOpcodeWriteCheck(
            val opcode: Int,
            val domain: String,
            var matches: Int = 0,
            var mismatches: Int = 0,
            var unverified: Int = 0,
        ) {
            fun freeze(): OpcodeWriteCheck = OpcodeWriteCheck(opcode, domain, matches, mismatches, unverified)
        }

        val opcodeChecks = LinkedHashMap<Pair<String, Int>, MutableOpcodeWriteCheck>()

        fun opcodeCheck(domain: String, opcode: Int): MutableOpcodeWriteCheck? {
            if (opcode == UNKNOWN_SOURCE_OPCODE) return null
            return opcodeChecks.getOrPut(domain to opcode) { MutableOpcodeWriteCheck(opcode, domain) }
        }

        val varpMatches = ArrayList<Int>()
        val varpMismatches = ArrayList<VarpMismatch>()
        val varpAdvisory = ArrayList<VarpAdvisory>()
        val varpMissed = ArrayList<Int>()
        val varpExtra = ArrayList<Int>()

        // VARPS — the client snapshot is authoritative. Walk the client's vars: each must match what
        // we decoded; a client var we never set is a MISSED (dropped) state packet.
        for ((id, clientValue) in snapshot.varps) {
            val ourValue = expected.varps[id]
            when {
                ourValue == null -> varpMissed += id
                ourValue == clientValue -> {
                    varpMatches += id
                    expected.varpSources[id]?.let { opcodeCheck("varp", it)?.matches++ }
                }
                id in dynamicVarpEvidence -> {
                    varpAdvisory += VarpAdvisory(
                        varId = id,
                        ourValue = ourValue,
                        clientValue = clientValue,
                        evidence = dynamicVarpEvidence.getValue(id),
                    )
                    expected.varpSources[id]?.let { opcodeCheck("varp", it)?.unverified++ }
                }
                else -> {
                    varpMismatches += VarpMismatch(id, ourValue, clientValue)
                    expected.varpSources[id]?.let { opcodeCheck("varp", it)?.mismatches++ }
                }
            }
        }
        // Vars we set that the client does not report (advisory).
        for (id in expected.varps.keys) {
            if (id !in snapshot.varps) {
                varpExtra += id
                expected.varpSources[id]?.let { opcodeCheck("varp", it)?.unverified++ }
            }
        }

        // VARCS — same client-authoritative rule, but keyed by (recordKind,varId). Varcbit writes are
        // not predicted yet; until cache varcbit defs are wired, only plain varc/varcstr opcodes feed
        // [expected].
        val varcMatches = ArrayList<VarcKey>()
        val varcMismatches = ArrayList<VarcMismatch>()
        val varcMissed = ArrayList<VarcNumberState>()
        val varcUnverified = ArrayList<VarcNumberState>()
        val varcExtra = ArrayList<VarcNumberState>()
        if (snapshot.varcsAvailable) {
            for ((key, clientValue) in snapshot.varcs) {
                val ourValue = expected.varcs[key]
                when {
                    ourValue == null -> varcMissed += clientValue
                    ourValue.valueKind == clientValue.valueKind && ourValue.value == clientValue.value -> {
                        varcMatches += key
                        opcodeCheck("varc", ourValue.sourceOpcode)?.matches++
                    }
                    else -> {
                        varcMismatches += VarcMismatch(
                            key = key,
                            ourValue = ourValue.describe(),
                            clientValue = clientValue.describe(),
                            ourValueKind = ourValue.valueKind,
                            clientValueKind = clientValue.valueKind,
                            ourNumberValue = ourValue.value,
                            clientNumberValue = clientValue.value,
                        )
                        opcodeCheck("varc", ourValue.sourceOpcode)?.mismatches++
                    }
                }
            }
            for ((key, value) in expected.varcs) {
                if (key !in snapshot.varcs) {
                    if (snapshot.varcs.isEmpty()) {
                        varcUnverified += value
                        opcodeCheck("varc", value.sourceOpcode)?.unverified++
                    } else {
                        varcExtra += value
                        opcodeCheck("varc", value.sourceOpcode)?.unverified++
                    }
                }
            }
        } else {
            for (value in expected.varcs.values) {
                varcUnverified += value
                opcodeCheck("varc", value.sourceOpcode)?.unverified++
            }
        }

        val varcStringMatches = ArrayList<VarcKey>()
        val varcStringMismatches = ArrayList<VarcMismatch>()
        val varcStringMissed = ArrayList<VarcStringState>()
        val varcStringUnverified = ArrayList<VarcStringState>()
        val varcStringExtra = ArrayList<VarcStringState>()
        if (snapshot.varcStringsAvailable) {
            for ((key, clientValue) in snapshot.varcStrings) {
                val ourValue = expected.varcStrings[key]
                when {
                    ourValue == null -> varcStringMissed += clientValue
                    ourValue.valueKind == clientValue.valueKind && ourValue.value == clientValue.value -> {
                        varcStringMatches += key
                        opcodeCheck("varcstring", ourValue.sourceOpcode)?.matches++
                    }
                    else -> {
                        varcStringMismatches += VarcMismatch(
                            key = key,
                            ourValue = ourValue.describe(),
                            clientValue = clientValue.describe(),
                            ourValueKind = ourValue.valueKind,
                            clientValueKind = clientValue.valueKind,
                            ourStringValue = ourValue.value,
                            clientStringValue = clientValue.value,
                        )
                        opcodeCheck("varcstring", ourValue.sourceOpcode)?.mismatches++
                    }
                }
            }
            for ((key, value) in expected.varcStrings) {
                if (key !in snapshot.varcStrings) {
                    if (snapshot.varcStrings.isEmpty()) {
                        varcStringUnverified += value
                        opcodeCheck("varcstring", value.sourceOpcode)?.unverified++
                    } else {
                        varcStringExtra += value
                        opcodeCheck("varcstring", value.sourceOpcode)?.unverified++
                    }
                }
            }
        } else {
            for (value in expected.varcStrings.values) {
                varcStringUnverified += value
                opcodeCheck("varcstring", value.sourceOpcode)?.unverified++
            }
        }

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
                opcodeCheck("skill", ours.sourceOpcode)?.mismatches++
            } else if (ours.level != clientSkill.level) {
                skillMismatches += SkillMismatch(id, "level", ours.level, clientSkill.level)
                opcodeCheck("skill", ours.sourceOpcode)?.mismatches++
            } else if (ours.base != clientSkill.base) {
                skillMismatches += SkillMismatch(id, "base", ours.base, clientSkill.base)
                opcodeCheck("skill", ours.sourceOpcode)?.mismatches++
            } else {
                skillMatches += id
                opcodeCheck("skill", ours.sourceOpcode)?.matches++
            }
        }
        for ((id, ours) in expected.skills) {
            if (id !in snapshot.skills) opcodeCheck("skill", ours.sourceOpcode)?.unverified++
        }

        // INVENTORIES — persistent store, so compare expected occupied slots against FINAL snapshot.
        // The oracle emits occupied slots only; absence means empty.
        val inventoryMatches = ArrayList<InventorySlotState>()
        val inventoryMismatches = ArrayList<InventoryMismatch>()
        val inventoryMissed = ArrayList<InventorySlotState>()
        for ((invId, clientInventory) in snapshot.inventories) {
            val ourInventory = expected.inventories[invId]
            for ((slot, clientSlot) in clientInventory.slots) {
                val ourSlot = ourInventory?.slots?.get(slot)
                when {
                    ourSlot == null -> inventoryMissed += clientSlot
                    ourSlot.itemId == clientSlot.itemId && ourSlot.count == clientSlot.count -> {
                        inventoryMatches += ourSlot
                        opcodeCheck("inventory", ourSlot.sourceOpcode)?.matches++
                    }
                    else -> {
                        inventoryMismatches += InventoryMismatch(
                            invId = invId,
                            slot = slot,
                            ourItemId = ourSlot.itemId,
                            ourCount = ourSlot.count,
                            clientItemId = clientSlot.itemId,
                            clientCount = clientSlot.count,
                        )
                        opcodeCheck("inventory", ourSlot.sourceOpcode)?.mismatches++
                    }
                }
            }
        }
        for ((invId, ourInventory) in expected.inventories) {
            val clientInventory = snapshot.inventories[invId]
            for ((slot, ourSlot) in ourInventory.slots) {
                if (clientInventory?.slots?.containsKey(slot) == true) continue
                inventoryMismatches += InventoryMismatch(
                    invId = invId,
                    slot = slot,
                    ourItemId = ourSlot.itemId,
                    ourCount = ourSlot.count,
                    clientItemId = -1,
                    clientCount = 0L,
                )
                opcodeCheck("inventory", ourSlot.sourceOpcode)?.mismatches++
            }
        }

        // LOCAL APPEARANCE — op22 APPEARANCE ext-info commits the local avatar's kit/item identity.
        // The oracle emits all 19 body slots, including disabled/empty slots as kit=-1,item=-1.
        val appearanceMatches = ArrayList<AppearanceSlotState>()
        val appearanceMismatches = ArrayList<AppearanceMismatch>()
        val appearanceMissed = ArrayList<AppearanceSlotState>()
        val appearanceUnverified = ArrayList<AppearanceSlotState>()
        if (snapshot.appearanceAvailable) {
            for ((slot, clientSlot) in snapshot.appearance) {
                val ours = expected.appearance[slot]
                when {
                    ours == null -> {
                        appearanceMissed += clientSlot
                        opcodeCheck("appearance", OP_PLAYER_INFO)?.mismatches++
                    }
                    ours.kitId == clientSlot.kitId && ours.itemId == clientSlot.itemId -> {
                        appearanceMatches += ours
                        opcodeCheck("appearance", ours.sourceOpcode)?.matches++
                    }
                    else -> {
                        appearanceMismatches += AppearanceMismatch(
                            slot = slot,
                            ourKitId = ours.kitId,
                            ourItemId = ours.itemId,
                            clientKitId = clientSlot.kitId,
                            clientItemId = clientSlot.itemId,
                        )
                        opcodeCheck("appearance", ours.sourceOpcode)?.mismatches++
                    }
                }
            }
            for ((slot, ours) in expected.appearance) {
                if (slot in snapshot.appearance) continue
                appearanceMismatches += AppearanceMismatch(
                    slot = slot,
                    ourKitId = ours.kitId,
                    ourItemId = ours.itemId,
                    clientKitId = -1,
                    clientItemId = -1,
                )
                opcodeCheck("appearance", ours.sourceOpcode)?.mismatches++
            }
        } else {
            for (slot in expected.appearance.values) {
                appearanceUnverified += slot
                opcodeCheck("appearance", slot.sourceOpcode)?.unverified++
            }
        }

        // SCENE PLAYERS — op22 GPI presence/position. The final scene oracle is authoritative, but
        // the external-player GPI replay is still intentionally conservative: only folded player
        // states participate. If none were folded, op22 stays unverified rather than being promoted
        // because the `players` array was available in a snapshot.
        val scenePlayerPresenceMatches = ArrayList<Int>()
        val scenePlayerPresenceMismatches = ArrayList<ScenePresenceMismatch>()
        val scenePlayerPositionMatches = ArrayList<Int>()
        val scenePlayerPositionMismatches = ArrayList<ScenePositionMismatch>()
        for ((idx, clientPlayer) in snapshot.players) {
            val ours = expected.scenePlayers[idx]
            if (ours == null) {
                if (expected.scenePlayers.isNotEmpty()) {
                    scenePlayerPresenceMismatches += ScenePresenceMismatch("player", idx, expectedPresent = false, clientPresent = true)
                    opcodeCheck("scene-player", OP_PLAYER_INFO)?.mismatches++
                }
                continue
            }
            scenePlayerPresenceMatches += idx
            opcodeCheck("scene-player", ours.sourceOpcode)?.matches++
            val ourTile = ours.tile
            val clientTile = clientPlayer.tile
            if (ourTile != null && clientTile != null) {
                if (tilesNear(ourTile, clientTile)) {
                    scenePlayerPositionMatches += idx
                    opcodeCheck("scene-player-position", ours.sourceOpcode)?.matches++
                } else {
                    opcodeCheck("scene-player-position", ours.sourceOpcode)?.unverified++
                }
            }
        }
        for ((idx, ours) in expected.scenePlayers) {
            if (idx !in snapshot.players) {
                scenePlayerPresenceMismatches += ScenePresenceMismatch("player", idx, expectedPresent = true, clientPresent = false)
                opcodeCheck("scene-player", ours.sourceOpcode)?.mismatches++
            }
        }

        // SCENE NPCS — op52 list management is folded from the bit-packed active-count/update/add
        // loops. Presence is the Stage-1a proof; type id is decoded from add records and checked when
        // both sides have the same index. Position is best-effort and only compares folded tiles.
        val sceneNpcPresenceMatches = ArrayList<Int>()
        val sceneNpcPresenceMismatches = ArrayList<ScenePresenceMismatch>()
        val sceneNpcTypeMatches = ArrayList<Int>()
        val sceneNpcTypeMismatches = ArrayList<SceneNpcTypeMismatch>()
        val sceneNpcPositionMatches = ArrayList<Int>()
        val sceneNpcPositionMismatches = ArrayList<ScenePositionMismatch>()
        for ((idx, clientNpc) in snapshot.npcs) {
            val ours = expected.sceneNpcs[idx]
            if (ours == null) {
                if (expected.sceneNpcs.isNotEmpty()) {
                    sceneNpcPresenceMismatches += ScenePresenceMismatch(
                        family = "npc",
                        idx = idx,
                        expectedPresent = false,
                        clientPresent = true,
                        clientTypeId = clientNpc.typeId,
                    )
                    opcodeCheck("scene-npc", OP_NPC_INFO)?.mismatches++
                }
                continue
            }
            sceneNpcPresenceMatches += idx
            opcodeCheck("scene-npc", ours.sourceOpcode)?.matches++
            val ourType = ours.typeId
            val clientType = clientNpc.typeId
            if (ourType != null && clientType != null) {
                if (ourType == clientType) {
                    sceneNpcTypeMatches += idx
                    opcodeCheck("scene-npc-type", ours.sourceOpcode)?.matches++
                } else {
                    sceneNpcTypeMismatches += SceneNpcTypeMismatch(idx, ourType, clientType)
                    opcodeCheck("scene-npc-type", ours.sourceOpcode)?.mismatches++
                }
            }
            val ourTile = ours.tile
            val clientTile = clientNpc.tile
            if (ourTile != null && clientTile != null && clientTile.x >= 0 && clientTile.y >= 0) {
                if (tilesNear(ourTile, clientTile)) {
                    sceneNpcPositionMatches += idx
                    opcodeCheck("scene-npc-position", ours.sourceOpcode)?.matches++
                } else {
                    opcodeCheck("scene-npc-position", ours.sourceOpcode)?.unverified++
                }
            }
        }
        for ((idx, ours) in expected.sceneNpcs) {
            if (idx !in snapshot.npcs) {
                sceneNpcPresenceMismatches += ScenePresenceMismatch(
                    family = "npc",
                    idx = idx,
                    expectedPresent = true,
                    clientPresent = false,
                    expectedTypeId = ours.typeId,
                )
                opcodeCheck("scene-npc", ours.sourceOpcode)?.mismatches++
            }
        }

        // SCALARS — run energy / run weight / player tile.
        val scalar = ArrayList<ScalarMismatch>()
        if (expected.runEnergy != null) {
            val sourceOpcode = expected.runEnergySourceOpcode ?: UNKNOWN_SOURCE_OPCODE
            when (snapshot.runEnergy) {
                null -> opcodeCheck("runEnergy", sourceOpcode)?.unverified++
                expected.runEnergy -> opcodeCheck("runEnergy", sourceOpcode)?.matches++
                else -> {
                    scalar += ScalarMismatch("runEnergy", expected.runEnergy.toString(), snapshot.runEnergy.toString())
                    opcodeCheck("runEnergy", sourceOpcode)?.mismatches++
                }
            }
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
            varpAdvisory = varpAdvisory,
            varpMissed = varpMissed,
            varpExtra = varpExtra,
            varcMatches = varcMatches.size,
            varcMismatches = varcMismatches,
            varcMissed = varcMissed.sortedBy { it.key },
            varcUnverified = varcUnverified.sortedBy { it.key },
            varcExtra = varcExtra.sortedBy { it.key },
            varcsAvailable = snapshot.varcsAvailable,
            varcStringMatches = varcStringMatches.size,
            varcStringMismatches = varcStringMismatches,
            varcStringMissed = varcStringMissed.sortedBy { it.key },
            varcStringUnverified = varcStringUnverified.sortedBy { it.key },
            varcStringExtra = varcStringExtra.sortedBy { it.key },
            varcStringsAvailable = snapshot.varcStringsAvailable,
            skillMatches = skillMatches.size,
            skillMismatches = skillMismatches,
            skillMissed = skillMissed,
            inventoryMatches = inventoryMatches.size,
            inventoryMismatches = inventoryMismatches
                .sortedWith(compareBy<ClientStateCrossCheck.InventoryMismatch> { it.invId }.thenBy { it.slot }),
            inventoryMissed = inventoryMissed
                .sortedWith(compareBy<ClientStateCrossCheck.InventorySlotState> { it.invId }.thenBy { it.slot }),
            appearanceMatches = appearanceMatches.size,
            appearanceMismatches = appearanceMismatches.sortedBy { it.slot },
            appearanceMissed = appearanceMissed.sortedBy { it.slot },
            appearanceUnverified = appearanceUnverified.sortedBy { it.slot },
            appearanceAvailable = snapshot.appearanceAvailable,
            scenePlayerPresenceMatches = scenePlayerPresenceMatches.size,
            scenePlayerPresenceMismatches = scenePlayerPresenceMismatches.sortedBy { it.idx },
            scenePlayerPositionMatches = scenePlayerPositionMatches.size,
            scenePlayerPositionMismatches = scenePlayerPositionMismatches.sortedBy { it.idx },
            sceneNpcPresenceMatches = sceneNpcPresenceMatches.size,
            sceneNpcPresenceMismatches = sceneNpcPresenceMismatches.sortedBy { it.idx },
            sceneNpcTypeMatches = sceneNpcTypeMatches.size,
            sceneNpcTypeMismatches = sceneNpcTypeMismatches.sortedBy { it.idx },
            sceneNpcPositionMatches = sceneNpcPositionMatches.size,
            sceneNpcPositionMismatches = sceneNpcPositionMismatches.sortedBy { it.idx },
            scalarMismatches = scalar,
            opcodeWriteChecks = opcodeChecks.values
                .map { it.freeze() }
                .sortedWith(compareBy<ClientStateCrossCheck.OpcodeWriteCheck> { it.opcode }.thenBy { it.domain }),
            decodeFailures = expected.decodeFailures,
        )
    }

    // ---- helpers ---------------------------------------------------------------------------------

    private fun Map<Int, AppearanceSlotState>.withEquipmentAppearance(
        inventories: Map<Int, InventoryState>,
    ): Map<Int, AppearanceSlotState> {
        val equipment = inventories[WORN_EQUIPMENT_INVENTORY_ID] ?: return this
        val out = LinkedHashMap(this)
        if (out.isEmpty()) {
            repeat(PLAYER_APPEARANCE_SLOT_COUNT) { slot ->
                out[slot] = AppearanceSlotState(slot, kitId = -1, itemId = -1)
            }
        }
        val weapon = equipment.slots[WORN_WEAPON_EQUIPMENT_SLOT]
        out[PLAYER_APPEARANCE_WEAPON_SLOT] = AppearanceSlotState(
            slot = PLAYER_APPEARANCE_WEAPON_SLOT,
            kitId = -1,
            itemId = weapon?.itemId ?: -1,
            sourceOpcode = weapon?.sourceOpcode ?: UNKNOWN_SOURCE_OPCODE,
        )
        return out
    }

    private fun VarcNumberState.describe(): String = "kind$valueKind:$value"

    private fun VarcStringState.describe(): String = "kind$valueKind:\"$value\""

    private fun tilesNear(a: Tile, b: Tile): Boolean =
        a.plane == b.plane && abs(a.x - b.x) <= 1 && abs(a.y - b.y) <= 1

    private fun decodeGpiPrefix(
        body: ByteArray,
        fold: PlayerSceneFold,
        localPlayerIndex: Int?,
    ) {
        val index = localPlayerIndex ?: return
        if (index !in 1 until PLAYER_SLOT_COUNT) return
        if (body.size < GPI_PREFIX_BYTES) return
        fold.resetFromGpiPrefix(body, index)
    }

    private fun decodePlayerInfo(
        body: ByteArray,
        fold: PlayerSceneFold,
        sourceOpcode: Int,
    ) {
        if (!fold.initialized) return
        val bits = BitBodyReader(body)
        try {
            fold.decodePlayerInfo(body, bits, sourceOpcode)
        } catch (e: RuntimeException) {
            fold.scanAppearance(body, sourceOpcode, overwrite = false)
            throw e
        }
    }

    private class PlayerSceneFold {
        private val slots = arrayOfNulls<PlayerSlot>(PLAYER_SLOT_COUNT)
        private val renderList = ArrayList<Int>(PLAYER_SLOT_COUNT)
        private val pendingList = ArrayList<Int>(PLAYER_SLOT_COUNT)
        private val movements = ArrayList<PlayerMovement>()
        private var appearance = emptyMap<Int, AppearanceSlotState>()
        private var localIndex: Int = -1

        /** The ext-info dispatch order (slot indices) + byte offset captured by the last decode, for
         *  the detailed harness to re-walk the trailing `[u16 len][bytes]` blocks. */
        var lastExtInfoOrder: List<Int> = emptyList()
            private set
        var lastExtInfoOffset: Int = 0
            private set

        /** True iff the last [decodePlayerInfo] completed all 4 GPI passes without a skip-run desync
         *  fallback (i.e. the bit cursor reached the ext-info section cleanly). The harness uses this
         *  to pick the correct local index and to report per-frame clean-vs-desync rates. */
        var lastDecodeClean: Boolean = false
            private set

        val initialized: Boolean
            get() = localIndex in 1 until PLAYER_SLOT_COUNT

        fun resetFromGpiPrefix(body: ByteArray, localPlayerIndex: Int) {
            slots.fill(null)
            renderList.clear()
            pendingList.clear()
            movements.clear()
            appearance = emptyMap()
            localIndex = localPlayerIndex
            val bits = BitBodyReader(body)
            val localPacked = bits.readBits(GPI_LOCAL_TILE_BITS)
            val localTile = Tile(
                x = (localPacked ushr 14) and 0x3fff,
                y = localPacked and 0x3fff,
                plane = (localPacked ushr 28) and 0x3,
            )
            slots[localPlayerIndex] = PlayerSlot(
                active = false,
                present = true,
                coord = LowResCoord(localTile.plane, localTile.x ushr 6, localTile.y ushr 6),
                tile = localTile,
                sourceOpcode = OP_REBUILD_NORMAL_SIMPLE,
            )
            renderList += localPlayerIndex
            for (idx in 1 until PLAYER_SLOT_COUNT) {
                if (idx == localPlayerIndex) continue
                val word = bits.readBits(GPI_OTHER_SLOT_BITS)
                slots[idx] = PlayerSlot(
                    active = ((word ushr GPI_PREFIX_ACTIVE_SHIFT) and 0x3) == 0,
                    present = false,
                    coord = LowResCoord(
                        plane = (word ushr 16) and 0x3,
                        regionX = (word ushr 8) and 0xff,
                        regionY = word and 0xff,
                    ),
                    tile = null,
                    sourceOpcode = OP_REBUILD_NORMAL_SIMPLE,
                )
                pendingList += idx
            }
        }

        fun decodePlayerInfo(body: ByteArray, bits: BitBodyReader, sourceOpcode: Int) {
            lastDecodeClean = false
            lastExtInfoOrder = emptyList()
            val extInfoOrder = ArrayList<Int>()
            fun scanFallback() {
                scanAppearance(body, sourceOpcode, overwrite = false)
            }
            var remainingSkip = runKnownPass(bits, renderList, activeFlag = false, sourceOpcode, extInfoOrder)
            bits.alignToByte()
            if (remainingSkip != 0) {
                scanFallback()
                return
            }
            remainingSkip = runKnownPass(bits, renderList, activeFlag = true, sourceOpcode, extInfoOrder)
            bits.alignToByte()
            if (remainingSkip != 0) {
                scanFallback()
                return
            }
            remainingSkip = runExternalPass(bits, pendingList, activeFlag = true, sourceOpcode, extInfoOrder)
            bits.alignToByte()
            if (remainingSkip != 0) {
                scanFallback()
                return
            }
            remainingSkip = runExternalPass(bits, pendingList, activeFlag = false, sourceOpcode, extInfoOrder)
            bits.alignToByte()
            if (remainingSkip != 0) {
                scanFallback()
                return
            }
            lastDecodeClean = true
            lastExtInfoOffset = bits.byteOffset
            lastExtInfoOrder = extInfoOrder.toList()
            val decodedLocalAppearance = readExtInfoBlocks(body, bits.byteOffset, extInfoOrder, sourceOpcode)
            if (!decodedLocalAppearance && localIndex !in extInfoOrder) scanFallback()
            rebuildActivityFlagsAndLists()
        }

        fun states(): Map<Int, ScenePlayerState> {
            val out = LinkedHashMap<Int, ScenePlayerState>()
            for (idx in 1 until PLAYER_SLOT_COUNT) {
                val slot = slots[idx] ?: continue
                if (slot.present) out[idx] = ScenePlayerState(idx, slot.tile, slot.sourceOpcode)
            }
            return out
        }

        fun movements(): List<PlayerMovement> = movements.toList()

        /** Number of movement records accumulated so far (across all frames decoded on this fold). */
        val movementCount: Int get() = movements.size

        /** Movement records added since index [from] (i.e. during the most recent frame). */
        fun movementsSince(from: Int): List<PlayerMovement> =
            if (from >= movements.size) emptyList() else movements.subList(from, movements.size).toList()

        fun appearanceState(): Map<Int, AppearanceSlotState> = LinkedHashMap(appearance)

        fun scanAppearance(body: ByteArray, sourceOpcode: Int, overwrite: Boolean) {
            if (!overwrite && appearance.isNotEmpty()) return
            scanAppearancePayload(body, sourceOpcode)?.let { appearance = it }
        }

        private fun runKnownPass(
            bits: BitBodyReader,
            order: List<Int>,
            activeFlag: Boolean,
            sourceOpcode: Int,
            extInfoOrder: MutableList<Int>,
        ): Int {
            var skip = 0
            for (idx in order) {
                val slot = slots[idx] ?: continue
                if (slot.active != activeFlag) continue
                if (skip > 0) {
                    skip--
                    slot.nextActive = true
                    continue
                }
                val hasUpdate = bits.readBits(1) != 0
                if (hasUpdate) {
                    decodeKnownPlayerUpdate(bits, idx, slot, sourceOpcode, extInfoOrder)
                } else {
                    skip = readPlayerSkipCount(bits)
                    slot.nextActive = true
                }
            }
            return skip
        }

        private fun runExternalPass(
            bits: BitBodyReader,
            order: List<Int>,
            activeFlag: Boolean,
            sourceOpcode: Int,
            extInfoOrder: MutableList<Int>,
        ): Int {
            var skip = 0
            for (idx in order) {
                val slot = slots[idx] ?: continue
                if (slot.active != activeFlag) continue
                if (skip > 0) {
                    skip--
                    slot.nextActive = true
                    continue
                }
                val hasUpdate = bits.readBits(1) != 0
                if (hasUpdate) {
                    if (decodeExternalPlayerUpdate(bits, idx, slot, sourceOpcode, extInfoOrder)) slot.nextActive = true
                } else {
                    skip = readPlayerSkipCount(bits)
                    slot.nextActive = true
                }
            }
            return skip
        }

        private fun decodeKnownPlayerUpdate(
            bits: BitBodyReader,
            idx: Int,
            slot: PlayerSlot,
            sourceOpcode: Int,
            extInfoOrder: MutableList<Int>,
        ) {
            val hasExt = bits.readBits(1) != 0
            if (hasExt) extInfoOrder += idx
            val movementType = bits.readBits(2)
            // The tile BEFORE this update — what we advance from. For the local slot this is the
            // op81-seeded absolute tile on the first walk and then the running maintained tile.
            val prevTile = slot.tile
            when (movementType) {
                0 -> {
                    // mvt=0 = NO FORCED MOVEMENT. The tile is UNCHANGED — record that, do NOT null it.
                    // This is the local player's predicted-walk reconciliation form (and any stationary
                    // high-res slot). We must NOT early-return: the per-tick local series has to stay
                    // complete (a missing tick would corrupt the walk-window classification).
                    movements += PlayerMovement(
                        idx, movementType, dir = null, hasExt = hasExt, followup = null,
                        tileAfter = prevTile, tileExact = true,
                    )
                    slot.sourceOpcode = sourceOpcode
                    // The "demote to low-res" sub-form only exists for a NON-local slot with no ext-info:
                    // [hasExt=0][mvt=0] then a 1-bit flag → optional low-res move. The local slot's
                    // [hasExt=0][mvt=0] is a bare hold (binary: local mvt=0 early-returns as a no-op), so
                    // it carries NO trailing bit. Mirror that: only the non-local hold reads the bit.
                    if (!hasExt && idx != localIndex) {
                        slot.present = false
                        slot.tile = null
                        if (bits.readBits(1) != 0) {
                            decodeExternalPlayerUpdate(bits, idx, slot, sourceOpcode, extInfoOrder)
                        }
                    }
                }
                1 -> {
                    val dir = bits.readBits(3)
                    val hasFollowup = bits.readBits(1) != 0
                    val followup = if (hasFollowup) bits.readBits(2) else null
                    // WALK: advance the maintained tile by the verified 3-bit dir → DX/DY delta (the SAME
                    // PLAYER_REGION_DX/DY table the server's PlayerMovementEncoder inverts), so a decoded
                    // slot TRACKS the avatar's high-res walk path instead of dropping it.
                    val moved = prevTile?.let {
                        Tile(it.x + PLAYER_REGION_DX[dir], it.y + PLAYER_REGION_DY[dir], it.plane)
                    }
                    movements += PlayerMovement(
                        idx, movementType, dir = dir, hasExt = hasExt, followup = followup,
                        tileAfter = moved, tileExact = moved != null,
                    )
                    slot.tile = moved
                    slot.sourceOpcode = sourceOpcode
                }
                2 -> {
                    val dir = bits.readBits(4)
                    // RUN: best-effort tile maintenance. The 4-bit run code → tile-delta table is NOT
                    // binary-verified in this decode (the encoder never emits mvt=2), so we advance one
                    // tile in the matching 8-dir when the low nibble is a known walk dir, but FLAG the
                    // result inexact (tileExact=false) rather than fabricate a precise 2-tile run delta.
                    val moved = prevTile?.let {
                        if (dir in PLAYER_REGION_DX.indices) {
                            Tile(it.x + PLAYER_REGION_DX[dir], it.y + PLAYER_REGION_DY[dir], it.plane)
                        } else it
                    }
                    movements += PlayerMovement(
                        idx, movementType, dir = dir, hasExt = hasExt, followup = null,
                        tileAfter = moved, tileExact = false,
                    )
                    slot.tile = moved
                    slot.sourceOpcode = sourceOpcode
                }
                3 -> {
                    // mvt=3 selects a move-mode descriptor (binary @0x100025da6 / @0x100026493). The
                    // descriptor index is the raw 3-bit field (large form) or (code>>10)&0x7 (15-bit
                    // small form); index 4 = the SMOOTH glide descriptor &DAT_100f13a40. Capture it so
                    // the probe / round-trip can assert SMOOTH vs SNAP without re-reading the binary.
                    val large = bits.readBits(1) != 0
                    val descriptor: Int
                    val payload: Int
                    val moved: Tile?
                    if (large) {
                        descriptor = bits.readBits(3)
                        payload = bits.readBits(30)
                        // Large form carries the new ABSOLUTE tile as (plane<<28)|(x<<14)|y.
                        moved = Tile(
                            x = (payload ushr 14) and 0x3fff,
                            y = payload and 0x3fff,
                            plane = (payload ushr 28) and 0x3,
                        )
                    } else {
                        payload = bits.readBits(15)
                        // Table BYTE offset = (code>>10)&0x1c (binary @0x1000263cd); entry = offset>>2.
                        descriptor = ((payload ushr 10) and 0x1c) ushr 2
                        // Small form carries signed-5 tile deltas (X bits 9:5, Y bits 4:0; 512 fine = 1
                        // tile) + a plane delta (code>>10)&3. Advance the maintained tile by them.
                        fun s5(v: Int) = if (v < 0x10) v else v - 0x20
                        val planeD = (payload ushr 10) and 0x3
                        moved = prevTile?.let {
                            Tile(
                                x = it.x + s5((payload ushr 5) and 0x1f),
                                y = it.y + s5(payload and 0x1f),
                                plane = (it.plane + planeD) and 0x3,
                            )
                        }
                    }
                    movements += PlayerMovement(
                        idx, movementType, dir = null, hasExt = hasExt, followup = null,
                        descriptor = descriptor, large = large, payload = payload,
                        // Large-form absolute tile IS exact; small-form delta is exact when we had a
                        // prior tile. Flag false when small-form had no anchor to advance from.
                        tileAfter = moved, tileExact = large || moved != null,
                    )
                    slot.tile = moved
                    slot.sourceOpcode = sourceOpcode
                }
            }
        }

        private fun decodeExternalPlayerUpdate(
            bits: BitBodyReader,
            idx: Int,
            slot: PlayerSlot,
            sourceOpcode: Int,
            extInfoOrder: MutableList<Int>,
        ): Boolean {
            return when (bits.readBits(2)) {
                0 -> {
                    if (bits.readBits(1) != 0) decodeExternalPlayerUpdate(bits, idx, slot, sourceOpcode, extInfoOrder)
                    val localX = bits.readBits(6)
                    val localY = bits.readBits(6)
                    val hasExt = bits.readBits(1) != 0
                    if (hasExt) extInfoOrder += idx
                    slot.present = true
                    slot.tile = slot.coord?.let { coord ->
                        Tile(
                            x = (coord.regionX shl 6) + localX,
                            y = (coord.regionY shl 6) + localY,
                            plane = coord.plane,
                        )
                    }
                    slot.sourceOpcode = sourceOpcode
                    true
                }
                1 -> {
                    val delta = bits.readBits(2)
                    val coord = slot.coord
                    if (coord != null) slot.coord = coord.copy(plane = (coord.plane + delta) and 0x3)
                    false
                }
                2 -> {
                    val packed = bits.readBits(5)
                    val coord = slot.coord
                    if (coord != null) {
                        val direction = packed and 0x7
                        slot.coord = coord.copy(
                            plane = (coord.plane + (packed ushr 3)) and 0x3,
                            regionX = (coord.regionX + PLAYER_REGION_DX[direction]) and 0xff,
                            regionY = (coord.regionY + PLAYER_REGION_DY[direction]) and 0xff,
                        )
                    }
                    false
                }
                else -> {
                    val packed = bits.readBits(20)
                    val coord = slot.coord
                    if (coord != null) {
                        slot.coord = coord.copy(
                            plane = (coord.plane + (packed ushr 16)) and 0x3,
                            regionX = (coord.regionX + ((packed ushr 8) and 0xff)) and 0xff,
                            regionY = (coord.regionY + (packed and 0xff)) and 0xff,
                        )
                    }
                    false
                }
            }
        }

        private fun readExtInfoBlocks(
            body: ByteArray,
            offset: Int,
            order: List<Int>,
            sourceOpcode: Int,
        ): Boolean {
            var cursor = offset
            var decodedLocalAppearance = false
            for (idx in order) {
                require(cursor + 2 <= body.size) { "short player ext-info length" }
                val length = ((body[cursor].toInt() and 0xff) shl 8) or (body[cursor + 1].toInt() and 0xff)
                cursor += 2
                require(cursor + length <= body.size) { "short player ext-info block: need $length" }
                if (idx == localIndex) {
                    val block = body.copyOfRange(cursor, cursor + length)
                    runCatching { decodeLocalAppearance(block, sourceOpcode) }.getOrNull()?.let {
                        appearance = it
                        decodedLocalAppearance = true
                    }
                }
                cursor += length
            }
            return decodedLocalAppearance
        }

        private fun decodeLocalAppearance(
            block: ByteArray,
            sourceOpcode: Int,
        ): Map<Int, AppearanceSlotState>? {
            val src = ByteBodyReader(block)
            val mask = src.readPlayerMask()
            if (mask and (1 shl PLAYER_APPEARANCE_MASK_BIT) == 0) return null

            if (mask and (1 shl PLAYER_SPOT_ANIM_REMOVAL_MASK_BIT) != 0) src.skipSpotAnimRemoval()
            if (mask and (1 shl PLAYER_TRANSIENT_BOOL_MASK_BIT) != 0) src.skipScrambledByte()
            require(mask and (1 shl PLAYER_OVERHEAD_CHAT_MASK_BIT) == 0) {
                "unsupported player ext-info bit $PLAYER_OVERHEAD_CHAT_MASK_BIT before appearance"
            }
            return decodeAppearancePayload(src.readAppearancePayload(), sourceOpcode)
        }

        private fun rebuildActivityFlagsAndLists() {
            renderList.clear()
            pendingList.clear()
            for (idx in 1 until PLAYER_SLOT_COUNT) {
                val slot = slots[idx] ?: continue
                slot.active = slot.nextActive
                slot.nextActive = false
                if (slot.present) {
                    renderList += idx
                } else {
                    pendingList += idx
                }
            }
        }
    }

    private data class PlayerSlot(
        var active: Boolean,
        var nextActive: Boolean = false,
        var present: Boolean,
        var coord: LowResCoord?,
        var tile: Tile?,
        var sourceOpcode: Int,
    )

    private data class LowResCoord(
        val plane: Int,
        val regionX: Int,
        val regionY: Int,
    )

    private fun readPlayerSkipCount(bits: BitBodyReader): Int =
        when (bits.readBits(2)) {
            0 -> 0
            1 -> bits.readBits(5)
            2 -> bits.readBits(8)
            else -> bits.readBits(11)
        }

    private fun decodeNpcInfo(
        body: ByteArray,
        fold: NpcSceneFold,
        localTile: Tile?,
        sourceOpcode: Int,
    ) {
        val bits = BitBodyReader(body)
        val retainedCount = bits.readBits(8)
        fold.truncate(retainedCount)

        val retained = ArrayList<Int>(retainedCount)
        for (idx in fold.activePrefix(retainedCount)) {
            val changed = bits.readBits(1)
            if (changed == 0) {
                retained += idx
                continue
            }
            when (bits.readBits(2)) {
                0 -> retained += idx
                1 -> {
                    bits.readBits(4)
                    retained += idx
                    fold.clearTile(idx)
                }
                2 -> {
                    bits.readBits(5)
                    retained += idx
                    fold.clearTile(idx)
                }
                3 -> fold.remove(idx)
            }
        }
        fold.replaceActive(retained)

        while (bits.remainingBits > 15) {
            val idx = bits.readBits(16)
            if (idx == NPC_ADD_SENTINEL) break

            val dx = bits.readSignedBits(NPC_COORD_BITS)
            val plane = bits.readBits(2)
            val dy = bits.readSignedBits(NPC_COORD_BITS)
            val rawType = bits.readBits(16)
            val typeId = rawType ushr 1
            bits.readBits(1) // RE'd unknown flag after the type/id packed word.
            bits.readBits(3) // facing/orientation.
            bits.readBits(1) // has extended-info block.

            val tile = localTile?.let { Tile(it.x + dx, it.y + dy, plane) }
            fold.add(SceneNpcState(idx = idx, typeId = typeId, tile = tile, sourceOpcode = sourceOpcode))
        }
    }

    private class NpcSceneFold {
        private val active = ArrayList<Int>()
        private val states = LinkedHashMap<Int, SceneNpcState>()

        fun activePrefix(count: Int): List<Int> {
            require(count <= active.size) { "npc active count $count exceeds folded list ${active.size}" }
            return active.take(count)
        }

        fun truncate(count: Int) {
            require(count <= active.size) { "npc active count $count exceeds folded list ${active.size}" }
            while (active.size > count) {
                val idx = active.removeAt(active.lastIndex)
                states.remove(idx)
            }
        }

        fun replaceActive(indices: List<Int>) {
            active.clear()
            active.addAll(indices)
            val keep = indices.toHashSet()
            states.keys.removeIf { it !in keep }
        }

        fun add(state: SceneNpcState) {
            if (state.idx !in active) active += state.idx
            states[state.idx] = state
        }

        fun remove(idx: Int) {
            active.remove(idx)
            states.remove(idx)
        }

        fun clearTile(idx: Int) {
            val current = states[idx] ?: return
            states[idx] = current.copy(tile = null)
        }

        fun states(): Map<Int, SceneNpcState> = LinkedHashMap(states)
    }

    private class BitBodyReader(private val bytes: ByteArray) {
        private var bit = 0

        val remainingBits: Int
            get() = bytes.size * 8 - bit

        val byteOffset: Int
            get() = bit ushr 3

        fun readBits(count: Int): Int {
            require(count >= 0) { "negative bit count" }
            require(remainingBits >= count) { "short bit body: need $count, have $remainingBits" }
            var value = 0
            repeat(count) {
                val byte = bytes[bit ushr 3].toInt() and 0xff
                val bitInByte = 7 - (bit and 7)
                value = (value shl 1) or ((byte ushr bitInByte) and 1)
                bit++
            }
            return value
        }

        fun readSignedBits(count: Int): Int {
            val raw = readBits(count)
            val sign = 1 shl (count - 1)
            return if (raw and sign == 0) raw else raw - (1 shl count)
        }

        fun alignToByte() {
            bit = ((bit + 7) ushr 3) shl 3
        }
    }

    private class ByteBodyReader(private val bytes: ByteArray) {
        private var offset = 0

        val remaining: Int
            get() = bytes.size - offset

        fun readUByte(): Int {
            require(remaining >= 1) { "short byte body" }
            return bytes[offset++].toInt() and 0xff
        }

        fun readUShort(): Int =
            (readUByte() shl 8) or readUByte()

        fun readByteArray(size: Int): ByteArray {
            require(remaining >= size) { "short byte body: need $size, have $remaining" }
            val out = bytes.copyOfRange(offset, offset + size)
            offset += size
            return out
        }

        fun readSmart1Or2(): Int {
            val first = readUByte()
            return if (first < 128) first else ((first shl 8) or readUByte()) - 32768
        }

        fun readCString(): String {
            val start = offset
            while (offset < bytes.size && bytes[offset].toInt() != 0) offset++
            require(offset < bytes.size) { "unterminated appearance string" }
            val out = bytes.copyOfRange(start, offset).toString(Charsets.ISO_8859_1)
            offset++
            return out
        }

        fun readPlayerMask(): Int {
            var mask = readUByte()
            if (mask and (1 shl PLAYER_MASK_EXPAND_BIT0) != 0) {
                mask = mask or (readUByte() shl 8)
            }
            if (mask and (1 shl PLAYER_MASK_EXPAND_BIT1) != 0) {
                mask = mask or (readUByte() shl 16)
            }
            if (mask and (1 shl PLAYER_MASK_EXPAND_BIT2) != 0) {
                mask = mask or (readUByte() shl 24)
            }
            return mask
        }

        fun skipSpotAnimRemoval() {
            val count = readUByte()
            if (count == 0xff) return
            repeat(count) { readUShort() }
        }

        fun skipScrambledByte() {
            readUByte() // mode
            readUByte() // value
        }

        fun readAppearancePayload(): ByteArray {
            val lengthByte = readUByte()
            val length = if (lengthByte == PLAYER_APPEARANCE_EMPTY_LENGTH) {
                0
            } else {
                (-0x80 - lengthByte) and 0xff
            }
            val transformed = readByteArray(length)
            return ByteArray(length) { index ->
                ((transformed[index].toInt() and 0xff) - 0x80).toByte()
            }
        }

        fun requireConsumed() {
            require(remaining == 0) { "trailing appearance bytes: $remaining" }
        }

        // ---- detailed ext-info readers (faithful-replication harness) ----------------------------

        fun skipFixed(n: Int) {
            require(remaining >= n) { "short ext-info block: need $n, have $remaining" }
            offset += n
        }

        /** APPEARANCE block (bit 0x08): mode-3 length byte then [length] payload bytes. Skip both. */
        fun skipAppearanceBlock() {
            val lengthByte = readUByte()
            val length = if (lengthByte == PLAYER_APPEARANCE_EMPTY_LENGTH) 0 else (-0x80 - lengthByte) and 0xff
            skipFixed(length)
        }

        /**
         * gSmart2or4s / readBigSmart: high bit of the first byte set → 4-byte BE `& 0x7FFFFFFF`; else
         * 2-byte BE, with `0x7FFF` (raw `0xFF7F`? no — value 32767) the null sentinel → `-1`. Mirrors
         * the client's bit-0x20 seq-id read and the server's `writeBigSmart`.
         */
        fun readBigSmart(): Int {
            val first = readUByte()
            return if (first and 0x80 != 0) {
                ((first and 0x7f) shl 24) or (readUByte() shl 16) or (readUByte() shl 8) or readUByte()
            } else {
                val v = (first shl 8) or readUByte()
                if (v == 0x7fff) -1 else v
            }
        }

        /** g1_sub: the value the client recovers from a `writeByteSubtract` (128 - wireByte) byte. */
        fun readByteSubtractValue(): Int = (128 - readUByte()) and 0xff

        /** MOVEMENT_ANIM block (bit 0x20): 4× gSmart2or4s seq ids + 1× g1_sub priority. */
        fun readMovementAnimBlock(): ExtMovementAnim =
            ExtMovementAnim(
                seq0 = readBigSmart(),
                seq1 = readBigSmart(),
                seq2 = readBigSmart(),
                seq3 = readBigSmart(),
                priority = readByteSubtractValue(),
            )

        /**
         * FORCED_MOVEMENT block (bit 0x80), 12 bytes per player-appearance-948.md §Q3:
         * +0 g1_add(b-128) srcDx, +1 g1_neg(-b) srcDz, +2 g1(signed) dstDx, +3 g1_sub(128-b) dstDz,
         * +4 g1_add delta3, +5 g1_neg delta4, +6..7 g2(BE) startTick, +8..9 g2(BE) endTick,
         * +0xa g1_add yaw-low8, +0xb (&0x3f)<<8 yaw-high6.
         */
        fun readForcedMovementBlock(): ExtForcedMovement {
            val srcDx = (readUByte() - 128).toByte().toInt()
            val srcDz = (-readUByte()).toByte().toInt()
            val dstDx = readUByte().toByte().toInt()
            val dstDz = (128 - readUByte()).toByte().toInt()
            val delta3 = (readUByte() - 128).toByte().toInt()
            val delta4 = (-readUByte()).toByte().toInt()
            val startTick = readUShort()
            val endTick = readUShort()
            val yawLow = readUByte()
            val yawHigh = readUByte() and 0x3f
            val yaw = (yawHigh shl 8) or yawLow
            return ExtForcedMovement(srcDx, srcDz, dstDx, dstDz, delta3, delta4, startTick, endTick, yaw)
        }
    }

    private enum class ExtKind { APPEARANCE, MOVEMENT_ANIM, FORCED_MOVEMENT, FIXED, UNMODELLED }

    /**
     * The full 948 player ext-info bit table in the client's fixed dispatch order (ascending `order`),
     * mirroring `Rev948PlayerUpdateMaskKey`. We FULLY DECODE the three movement/identity bits the
     * faithful-replication question turns on — 0x08 APPEARANCE, 0x20 MOVEMENT_ANIM, 0x80
     * FORCED_MOVEMENT — and decode 0x02 FACE_DIRECTION (fixed g2). EVERY OTHER bit is [ExtKind.UNMODELLED]:
     * the walk STOPS at it and the slot is reported mask-only (`parsedClean=false`), so the cursor is
     * never walked past a variable/uncharacterised block and a later 0x20/0x80 silently mis-decoded.
     *
     * This is deliberate and sufficient: on the LOCAL slot production only ever sets 0x08/0x20/0x80
     * (plus occasionally 0x02), all modelled → the local parse is clean. Remote-player / NPC blocks
     * that set spotanim/chat/hit bits will report `parsedClean=false`, which is honest (we still report
     * which bits were set), not wrong.
     */
    private enum class ExtBit(val flag: Int, val order: Int, val kind: ExtKind, val skipBytes: Int = 0) {
        SPOT_ANIM_REMOVAL(0x4000000, 1, ExtKind.UNMODELLED),
        TRANSIENT_BOOL_BIT18(0x40000, 2, ExtKind.UNMODELLED),
        OVERHEAD_CHAT(0x100000, 3, ExtKind.UNMODELLED),
        APPEARANCE(0x8, 4, ExtKind.APPEARANCE),
        OVERHEAD_TEXT(0x40, 5, ExtKind.UNMODELLED),
        SPOT_ANIM_LIST_BIT24(0x1000000, 6, ExtKind.UNMODELLED),
        UNK_BIT11(0x800, 7, ExtKind.UNMODELLED),
        UNK_BIT15(0x8000, 8, ExtKind.UNMODELLED),
        FORCED_MOVEMENT(0x80, 9, ExtKind.FORCED_MOVEMENT),
        CHAT_TEXT_PRIVATE(0x4000, 10, ExtKind.UNMODELLED),
        UNK_BIT25(0x2000000, 11, ExtKind.UNMODELLED),
        SPOT_ANIM_LIST_BIT16(0x10000, 12, ExtKind.UNMODELLED),
        UNK_BIT9(0x200, 13, ExtKind.UNMODELLED),
        FACE_DIRECTION(0x2, 14, ExtKind.FIXED, skipBytes = 2),
        SPOT_ANIM_LIST_BIT23(0x800000, 15, ExtKind.UNMODELLED),
        MOVEMENT_ANIM(0x20, 16, ExtKind.MOVEMENT_ANIM),
        UNK_BIT19(0x80000, 17, ExtKind.UNMODELLED),
        OVERHEAD_OPACITY(0x1000, 18, ExtKind.FIXED, skipBytes = 1),
        UNK_BIT2(0x4, 19, ExtKind.UNMODELLED),
        CHAT_TEXT(0x400, 20, ExtKind.UNMODELLED),
        POSITION_COLOR(0x200000, 21, ExtKind.UNMODELLED),
        HITMARKS(0x10, 22, ExtKind.UNMODELLED),
        HEAD_ICON_BIT17(0x20000, 23, ExtKind.UNMODELLED),
        ;
        companion object {
            val byOrder: List<ExtBit> = entries.sortedBy { it.order }
        }
    }

    private val PLAYER_EXT_ORDER: List<ExtBit> = ExtBit.byOrder

    private fun scanAppearancePayload(
        body: ByteArray,
        sourceOpcode: Int,
    ): Map<Int, AppearanceSlotState>? {
        var found: Map<Int, AppearanceSlotState>? = null
        for (offset in body.indices) {
            val lengthByte = body[offset].toInt() and 0xff
            val length = if (lengthByte == PLAYER_APPEARANCE_EMPTY_LENGTH) {
                0
            } else {
                (-0x80 - lengthByte) and 0xff
            }
            if (length < PLAYER_APPEARANCE_MIN_PAYLOAD_BYTES) continue
            if (offset + 1 + length > body.size) continue
            val payload = ByteArray(length) { index ->
                (((body[offset + 1 + index].toInt() and 0xff) - 0x80) and 0xff).toByte()
            }
            val decoded = runCatching { decodeAppearancePayload(payload, sourceOpcode) }.getOrNull()
                ?: continue
            if (decoded.values.none { it.kitId >= 0 || it.itemId >= 0 }) continue
            if (found != null) return found
            found = decoded
        }
        return found
    }

    private fun decodeAppearancePayload(
        payload: ByteArray,
        sourceOpcode: Int,
    ): Map<Int, AppearanceSlotState> {
        val src = ByteBodyReader(payload)
        val flags = src.readUByte()
        if (flags and PLAYER_APPEARANCE_FLAG_TITLE != 0) src.readSmart1Or2()
        if (flags and PLAYER_APPEARANCE_FLAG_CUSTOMISATION != 0) {
            val count = src.readUByte()
            repeat(count) {
                src.readUShort()
                src.readUByte()
            }
        }
        src.readUByte() // bodyType/gender

        val slots = LinkedHashMap<Int, AppearanceSlotState>()
        repeat(PLAYER_APPEARANCE_SLOT_COUNT) { slot ->
            slots[slot] = AppearanceSlotState(slot, kitId = -1, itemId = -1, sourceOpcode = sourceOpcode)
        }
        var morphed = false
        for (slot in 0 until PLAYER_APPEARANCE_SLOT_COUNT) {
            if (slot in PLAYER_APPEARANCE_DISABLED_SLOTS) continue
            val first = src.readUByte()
            if (first == 0) continue
            val token = (first shl 8) or src.readUByte()
            when {
                slot == 0 && token == PLAYER_APPEARANCE_MORPH_SENTINEL -> {
                    morphed = true
                    src.skipBigSmart()
                    src.readUByte() // morph render/gender byte
                    break
                }
                token < PLAYER_APPEARANCE_ITEM_BASE -> {
                    slots[slot] = AppearanceSlotState(
                        slot = slot,
                        kitId = token - PLAYER_APPEARANCE_KIT_BASE,
                        itemId = -1,
                        sourceOpcode = sourceOpcode,
                    )
                }
                else -> {
                    slots[slot] = AppearanceSlotState(
                        slot = slot,
                        kitId = -1,
                        itemId = token - PLAYER_APPEARANCE_ITEM_BASE,
                        sourceOpcode = sourceOpcode,
                    )
                }
            }
        }

        if (!morphed) src.readUShort() // colour-channel mask
        repeat(PLAYER_APPEARANCE_KIT_COLOUR_COUNT) { src.readUByte() }
        repeat(PLAYER_APPEARANCE_KIT_STYLE_COUNT) { src.readUByte() }
        src.readUShort() // BAS/render anim
        src.readCString() // display name
        src.readUByte() // combat level
        if (flags and PLAYER_APPEARANCE_FLAG_COMBAT2_IS_SHORT != 0) {
            src.readUShort()
        } else {
            src.readUByte()
            src.readUByte()
        }
        if (src.readUByte() != 0) {
            repeat(4) { src.readUShort() }
            src.readUByte()
        }
        src.requireConsumed()
        return slots
    }

    private fun ByteBodyReader.skipBigSmart() {
        val first = readUByte()
        if (first and 0x80 == 0) {
            readUByte()
        } else {
            readUByte()
            readUByte()
            readUByte()
        }
    }

    private fun decodeInventoryFull(body: ByteArray, sourceOpcode: Int): InventoryState {
        val src = InventoryBodyReader(body)
        val invId = src.readUShort()
        val flags = src.readUByte()
        val count = src.readUShort()
        val slots = LinkedHashMap<Int, InventorySlotState>()
        repeat(count) { slot ->
            val itemId = src.readUShort() - 1
            val quantity = src.readQuantity()
            src.skipInventoryParams(flags)
            if (itemId != -1) {
                slots[slot] = InventorySlotState(invId, slot, itemId, quantity, sourceOpcode)
            }
        }
        src.requireConsumed()
        return InventoryState(invId, slots)
    }

    private fun decodeInventoryPartial(
        body: ByteArray,
        inventories: Map<Int, InventoryState>,
        sourceOpcode: Int,
    ): InventoryState {
        val src = InventoryBodyReader(body)
        val invId = src.readUShort()
        val flags = src.readUByte()
        val slots = LinkedHashMap(inventories[invId]?.slots ?: emptyMap())
        while (src.remaining > 0) {
            val slot = src.readSmart()
            val itemId = src.readUShort() - 1
            if (itemId == -1) {
                slots.remove(slot)
                continue
            }
            val quantity = src.readQuantity()
            src.skipInventoryParams(flags)
            slots[slot] = InventorySlotState(invId, slot, itemId, quantity, sourceOpcode)
        }
        return InventoryState(invId, slots)
    }

    private class InventoryBodyReader(private val bytes: ByteArray) {
        private var offset = 0

        val remaining: Int
            get() = bytes.size - offset

        fun readUByte(): Int {
            require(remaining >= 1) { "short inventory body" }
            return bytes[offset++].toInt() and 0xff
        }

        fun readUShort(): Int =
            (readUByte() shl 8) or readUByte()

        fun readIntUnsigned(): Long =
            (readUByte().toLong() shl 24) or
                (readUByte().toLong() shl 16) or
                (readUByte().toLong() shl 8) or
                readUByte().toLong()

        fun readSmart(): Int {
            val peek = readUByte()
            return if (peek < 128) peek else ((peek shl 8) or readUByte()) - 32768
        }

        fun readQuantity(): Long {
            val first = readUByte()
            return if (first == 0xff) readIntUnsigned() else first.toLong()
        }

        fun skipInventoryParams(flags: Int) {
            if (flags and 0x2 == 0) return
            val count = readUByte()
            repeat(count) {
                readUShort()
                readIntUnsigned()
            }
        }

        fun requireConsumed() {
            require(remaining == 0) { "trailing inventory bytes: $remaining" }
        }
    }

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
    private const val PLAYER_SLOT_COUNT = 2048
    private const val GPI_PREFIX_BYTES = 5119
    private const val GPI_LOCAL_TILE_BITS = 30
    private const val GPI_OTHER_SLOT_BITS = 20
    private const val GPI_PREFIX_ACTIVE_SHIFT = 18
    private const val PLAYER_MASK_EXPAND_BIT0 = 0
    private const val PLAYER_MASK_EXPAND_BIT1 = 13
    private const val PLAYER_MASK_EXPAND_BIT2 = 22
    private const val PLAYER_SPOT_ANIM_REMOVAL_MASK_BIT = 26
    private const val PLAYER_TRANSIENT_BOOL_MASK_BIT = 18
    private const val PLAYER_OVERHEAD_CHAT_MASK_BIT = 20
    private const val PLAYER_APPEARANCE_MASK_BIT = 3
    private const val PLAYER_APPEARANCE_EMPTY_LENGTH = 0x80
    private const val PLAYER_APPEARANCE_FLAG_CUSTOMISATION = 0x02
    private const val PLAYER_APPEARANCE_FLAG_COMBAT2_IS_SHORT = 0x04
    private const val PLAYER_APPEARANCE_FLAG_TITLE = 0x40
    private const val PLAYER_APPEARANCE_SLOT_COUNT = 19
    private const val PLAYER_APPEARANCE_KIT_BASE = 0x100
    private const val PLAYER_APPEARANCE_ITEM_BASE = 0x800
    private const val PLAYER_APPEARANCE_MORPH_SENTINEL = 0xffff
    private const val PLAYER_APPEARANCE_KIT_COLOUR_COUNT = 10
    private const val PLAYER_APPEARANCE_KIT_STYLE_COUNT = 10
    private const val PLAYER_APPEARANCE_MIN_PAYLOAD_BYTES = 40
    private const val WORN_EQUIPMENT_INVENTORY_ID = 94
    private const val WORN_WEAPON_EQUIPMENT_SLOT = 3
    private const val PLAYER_APPEARANCE_WEAPON_SLOT = 15
    private val PLAYER_APPEARANCE_DISABLED_SLOTS = setOf(12, 13, 17)
    private val PLAYER_REGION_DX = intArrayOf(-1, 0, 1, -1, 1, -1, 0, 1)
    private val PLAYER_REGION_DY = intArrayOf(-1, -1, -1, 0, 0, 1, 1, 1)
    private const val NPC_ADD_SENTINEL = 0xFFFF
    private const val NPC_COORD_BITS = 7
}
