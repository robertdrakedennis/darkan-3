package org.darkan.world.net

import org.darkan.core.net.prot.PlayerInfo
import org.darkan.core.net.prot.update.ActiveMaskKeys
import org.darkan.core.net.prot.update.PlayerUpdateMaskEncoder
import org.darkan.core.net.prot.update.PlayerUpdateMaskKey
import org.darkan.core.net.prot.update.UpdateMask
import org.darkan.world.entity.Player
import org.darkan.world.world.Players
import world.gregs.voidps.buffer.write.BufferWriter

/**
 * Per-tick builder that produces a [PlayerInfo] (ServerProt op 22 / GPI) DTO from world state.
 *
 * The wire format is binary-confirmed against the 948-5 client handler
 * `jag::packethandlers::PlayerInfo::PLAYER_INFO @ 0x001618a0` (varShort). The body is a single
 * MSB-first bit block of FOUR cohort passes followed by a byte-aligned extended-info section:
 *  1. HIGH-RES, flag27 == 0   (active — was NOT skipped last cycle)
 *  2. HIGH-RES, flag27 != 0   (stationary)
 *  3. LOW-RES,  flag27 != 0
 *  4. LOW-RES,  flag27 == 0
 *
 * `Player.active` is the modern-named mirror of the client slot `+0x27` flag, so `active == true`
 * maps to the flag27 == 0 cohort (passes 1 / 4) and `active == false` to flag27 != 0 (passes 2 / 3).
 *
 * There is exactly ONE entry point ([build]); there is **no** init/bulk-seed form. The 948 handler
 * has no bulk region-hash seed path — it always parses the 4-pass bit structure — so the previous
 * `buildInit()` (a 30-bit local tile + 2047 × 18-bit region hashes, an OSRS/919-era guess) has been
 * removed. The local player is placed in-world by encoding an absolute TELEPORT in its high-res entry
 * on the first build after login, driven by [Player.teleporting].
 *
 * Encoder registration check: ext-info bytes go through [PlayerUpdateMaskEncoder]. Only mask bits
 * with a registered encoder are flagged in the bitset — flagging without an encoder would leave the
 * client trying to consume bytes the server never emitted. Unregistered keys are dropped with a
 * WARN-once log.
 */
object PlayerInfoBuilder {

    /** Initial per-tick bit-packed body capacity — generous default; grows naturally via ByteBuffer reallocation if needed. */
    private const val TICK_BUFFER_CAPACITY = 8192

    /**
     * Master gate for the local-player APPEARANCE ext-info block.
     *
     * The appearance sub-format (equipment / body region, combat-level / size placement) is still
     * UNCONFIRMED — see `extended_info_section.appearance_block.confidence` in the op22 wire spec and
     * the `jag::PlayerEntity::SetAppearanceAsPlayer @ 0x0014d5b0` / lazy model builder
     * (vtable 0x01363708) RE TODO. Emitting an appearance block we can't yet prove byte-correct risks
     * a malformed-appearance parse crash in the client.
     *
     * While `false` the builder emits `hasExtendedInfo = 0` and NO ext-info section, so the packet is
     * guaranteed well-formed and the client can position the avatar without any appearance parse. The
     * appearance-synth path below is left fully intact behind this flag; flip it to `true` once the
     * appearance RE confirms the byte layout.
     */
    private const val INCLUDE_APPEARANCE = false

    /**
     * Teleport movement-descriptor index (the 3-bit `jumpType`). Value 4 selects the INSTANT WARP
     * descriptor (&DAT_015bf220) — no interpolation — which is what the client uses for a login spawn
     * (confirmed: high_res_position_decoder.teleport_big.jumpType in the op22 wire spec).
     */
    private const val JUMP_TYPE_INSTANT = 4

    /** Reduced log-spam: report each missing mask-encoder key only once per JVM lifetime. */
    private val warnedMissingPlayerEncoders: MutableSet<PlayerUpdateMaskKey> =
        java.util.Collections.newSetFromMap(java.util.concurrent.ConcurrentHashMap())

    /**
     * Build one PLAYER_INFO (op 22) for [player]'s viewport. Walks the four cohorts and emits the
     * MSB-first bit-packed update / skip-run structure, then the byte-aligned per-player ext-info
     * blocks for any player flagged `hasExtendedInfo`.
     *
     * On the first build after the player enters the world (and after any teleport), the local
     * player's [Player.teleporting] flag is set; its high-res entry then encodes an absolute
     * TELEPORT to [Player.tile] (see [encodeHighResPosition]) and the flag is cleared.
     *
     * MVP login state: the high-res list contains only the local player and the low-res list is
     * empty, so passes 2 / 3 / 4 iterate nothing and emit zero bits. The whole bit block is wrapped
     * in a single start/stop (matching the confirmed recipe: 38 bits -> 5 bytes after byte-align).
     */
    fun build(player: Player): PlayerInfo {
        val viewport = player.viewport
        val bitOut = BufferWriter(TICK_BUFFER_CAPACITY)
        bitOut.startBitAccess()

        // Tracks player indices flagged hasExtendedInfo this tick, in cohort processing order —
        // the ext-info blocks are emitted in the order the hasExtendedInfo bits fired.
        val flaggedForExtInfo = ArrayList<Int>(8)

        // Pass 1: HIGH-RES, flag27 == 0 (active).
        encodeHighResPass(bitOut, player, viewport.highResIndices, activeFilter = true, flaggedForExtInfo)
        // Pass 2: HIGH-RES, flag27 != 0 (stationary).
        encodeHighResPass(bitOut, player, viewport.highResIndices, activeFilter = false, flaggedForExtInfo)
        // Pass 3: LOW-RES, flag27 != 0 (stationary).
        encodeLowResPass(bitOut, player, viewport.lowResIndices, activeFilter = false, flaggedForExtInfo)
        // Pass 4: LOW-RES, flag27 == 0 (active).
        encodeLowResPass(bitOut, player, viewport.lowResIndices, activeFilter = true, flaggedForExtInfo)

        // NOTE: the handler byte-realigns after EACH list (after HIGH-RES, after LOW-RES). For the
        // local-player-only MVP that produces the SAME byte count as a single trailing realign
        // (passes 2/3/4 emit nothing, so all realigns land on the pass-1 boundary). When the low-res
        // list is populated, insert a stop/start byte-realign between passes 2 and 3 to stay
        // byte-perfect (op22 wire spec: section_order.after_each_list).
        bitOut.stopBitAccess()

        // Build ext-info blocks per flagged player (empty while INCLUDE_APPEARANCE is false and no
        // registered-encoder masks are pending). The op22 codec prefixes each with a u16 BE length.
        val extendedInfo = ArrayList<ByteArray>(flaggedForExtInfo.size)
        for (slot in flaggedForExtInfo) {
            val target = Players.get(slot) ?: continue
            extendedInfo.add(encodeExtendedInfoBlock(target))
        }

        return PlayerInfo(
            bitBlock = bitOut.toArray(),
            extendedInfo = extendedInfo,
            firstTick = false,
        )
    }

    /**
     * Encode one high-res pass (active or stationary filter). For each player in [indices] matching
     * the filter:
     *  * 1-bit `hasUpdate`.
     *  * If `hasUpdate == 1`: high-resolution position bits (see [encodeHighResPosition]).
     *  * Else: 2-bit skip-mode + 0..11-bit skip-count run-length. We emit `skipMode = 0` (cover only
     *    the current slot) so the next iteration reads its own `hasUpdate` bit — correctness over
     *    compactness for MVP.
     */
    private fun encodeHighResPass(
        out: BufferWriter,
        viewer: Player,
        indices: List<Int>,
        activeFilter: Boolean,
        flaggedForExtInfo: MutableList<Int>,
    ) {
        for (slot in indices) {
            val target = Players.get(slot) ?: continue
            val matches = if (activeFilter) target.active else !target.active
            if (!matches) continue

            if (needsAnyUpdate(viewer, target)) {
                out.writeBits(1, 1)
                encodeHighResPosition(out, target, flaggedForExtInfo)
            } else {
                out.writeBits(1, 0)
                out.writeBits(2, 0)  // skipMode = 0: cover only this slot, continue with next.
            }
        }
    }

    /**
     * Encode one low-res pass. Mirrors [encodeHighResPass] but calls [encodeLowResPosition] on update.
     * For MVP the low-res list is empty (no other players in viewport), so this is a no-op loop; the
     * structure is in place for when other players appear.
     */
    private fun encodeLowResPass(
        out: BufferWriter,
        viewer: Player,
        indices: List<Int>,
        activeFilter: Boolean,
        flaggedForExtInfo: MutableList<Int>,
    ) {
        for (slot in indices) {
            val target = Players.get(slot) ?: continue
            val matches = if (activeFilter) target.active else !target.active
            if (!matches) continue

            if (needsAnyUpdate(viewer, target)) {
                out.writeBits(1, 1)
                encodeLowResPosition(out, target, flaggedForExtInfo)
            } else {
                out.writeBits(1, 0)
                out.writeBits(2, 0)
            }
        }
    }

    /**
     * High-res position bits per `GetHighResolutionPlayerPosition @ 0x00154d30` (op22 wire spec):
     * ```
     * 1 bit : hasExtendedInfo
     * 2 bits: movementType  (0 = none, 1 = walk, 2 = run, 3 = teleport)
     * ```
     * Local-player login spawn ([Player.teleporting] set) — movementType = 3, BIG/absolute teleport:
     * ```
     * 1 bit : teleportType = 1 (BIG/region)
     * 3 bits: jumpType     = 4 (instant warp)
     * 30 bits: coord = (plane << 28) | (xTile << 14) | yTile  (== Tile.id)
     * ```
     * Because the freshly-created client entity is at (0,0,0) pre-GPI (REBUILD_NORMAL sets only the
     * scene/camera, not the player tile), the 30-bit field is the ABSOLUTE spawn tile. We clear
     * [Player.teleporting] once emitted.
     *
     * When not teleporting, movementType = 0 (no movement). With hasExtendedInfo = 1 this is an
     * appearance-only update. With hasExtendedInfo = 0 the protocol expects a 1-bit demote-to-low-res
     * flag — unreachable in MVP (a no-update slot takes the `hasUpdate = 0` skip path instead), and
     * the local player must never be demoted, so we always emit demote = 0 here defensively.
     */
    private fun encodeHighResPosition(
        out: BufferWriter,
        target: Player,
        flaggedForExtInfo: MutableList<Int>,
    ) {
        val hasExtInfo = hasFlaggableExtendedInfo(target)
        out.writeBits(1, if (hasExtInfo) 1 else 0)  // hasExtendedInfo

        if (target.teleporting) {
            out.writeBits(2, 3)                 // movementType = 3 (TELEPORT)
            out.writeBits(1, 1)                 // teleportType = 1 (BIG / absolute)
            out.writeBits(3, JUMP_TYPE_INSTANT) // jumpType = 4 (instant warp)
            out.writeBits(30, target.tile.id)   // coord = (plane<<28)|(x<<14)|y == Tile.id (absolute)
            target.teleporting = false
        } else {
            out.writeBits(2, 0)                 // movementType = 0 (none)
            if (!hasExtInfo) {
                out.writeBits(1, 0)             // demote-to-low-res = 0 (stay high-res; defensive/dead in MVP)
            }
        }

        if (hasExtInfo) flaggedForExtInfo.add(target.index)
    }

    /**
     * Low-res position bits per `GetLowResolutionPlayerPosition @ 0x001538e0`:
     * ```
     * 2 bits: updateType (0 = add/promote, 1 = facing-only, 2 = small move, 3 = region jump)
     * ```
     * Not exercised in MVP (low-res list empty). Emits a safe facing-only no-op; wired for forward
     * compatibility only.
     */
    private fun encodeLowResPosition(
        out: BufferWriter,
        @Suppress("UNUSED_PARAMETER") target: Player,
        @Suppress("UNUSED_PARAMETER") flaggedForExtInfo: MutableList<Int>,
    ) {
        out.writeBits(2, 1)   // updateType = 1 (facing-only)
        out.writeBits(2, 0)   // delta = 0
    }

    /**
     * Encode the extended-info byte block (mask + per-block payloads, WITHOUT the u16 length prefix —
     * the op22 codec adds that). The flag bitset is little-endian (byte0 = bits 0-7, ...) with
     * revision-specific expansion ("continue") bits driving multi-byte expansion.
     *
     * The local-player APPEARANCE synth is gated behind [INCLUDE_APPEARANCE]; while disabled this is
     * only reached for players carrying pending masks that have a registered encoder.
     */
    private fun encodeExtendedInfoBlock(target: Player): ByteArray {
        val extOut = BufferWriter(256)

        // Filter pending masks down to those with a registered encoder. Unregistered ones MUST NOT be
        // flagged in the bitset — doing so would desync the client (it would read bytes we never sent).
        val entries = target.pendingUpdates.playerMaskEntries().filter { (key, _) ->
            val has = PlayerUpdateMaskEncoder.hasEncoder(key)
            if (!has && warnedMissingPlayerEncoders.add(key)) {
                System.err.println(
                    "[PlayerInfoBuilder] no encoder registered for PlayerUpdateMaskKey.$key — " +
                        "dropping from ext-info bitset to avoid client desync."
                )
            }
            has
        }

        val effective = ArrayList(entries)

        // APPEARANCE synth for the local avatar — GATED. The appearance byte layout is still
        // UNCONFIRMED (see INCLUDE_APPEARANCE / appearance RE TODO); leave intact for when it lands.
        if (INCLUDE_APPEARANCE) {
            val appearanceKey = ActiveMaskKeys.playerAppearance
            if (appearanceKey != null) {
                val hasAppearancePending = effective.any { it.first === appearanceKey }
                if (!hasAppearancePending && PlayerUpdateMaskEncoder.hasEncoder(appearanceKey)) {
                    val cached = target.appearance.cachedBytes
                    if (cached != null) {
                        effective.add(appearanceKey to UpdateMask.Appearance(cached))
                    }
                }
            }
        }

        if (effective.isEmpty()) {
            // Flagged hasExtInfo but produced nothing: emit an empty (single 0x00) bitset. Defensive —
            // hasFlaggableExtendedInfo should prevent reaching here.
            extOut.writeByte(0)
            return extOut.toArray()
        }

        // OR all flags into the bitset (LE).
        var flagBitset = 0
        for ((key, _) in effective) flagBitset = flagBitset or key.flag

        // Byte width from the highest set bit, then set the LOWER-byte expansion ("continue") bits.
        val highestBit = 31 - Integer.numberOfLeadingZeros(flagBitset)
        val byteCount = when {
            highestBit < 8 -> 1
            highestBit < 16 -> 2
            highestBit < 24 -> 3
            else -> 4
        }
        val expansionBits = ActiveMaskKeys.playerExpansionBits
        for (byte in 1 until byteCount) {
            flagBitset = flagBitset or (1 shl expansionBits[byte - 1])
        }

        // Write the bitset LSB-first.
        for (i in 0 until byteCount) {
            extOut.writeByte((flagBitset ushr (i * 8)) and 0xFF)
        }

        // Per-flag blocks in fixed processing order (ascending PlayerUpdateMaskKey.order).
        for ((key, mask) in effective.sortedBy { it.first.order }) {
            PlayerUpdateMaskEncoder.encode(extOut, key, mask)
        }

        return extOut.toArray()
    }

    /**
     * Returns true if [target] has a real update to transmit to [viewer] this tick — a pending
     * teleport (login spawn / explicit teleport), a pending mask with a registered encoder, or (when
     * [INCLUDE_APPEARANCE] is enabled) a not-yet-sent appearance for this viewer. Drives the
     * `hasUpdate` bit. Deliberately does NOT return true for masks without an encoder (nothing would
     * be emitted) so the dangerous movementType=0 + hasExtInfo=0 demote path is never taken.
     */
    private fun needsAnyUpdate(viewer: Player, target: Player): Boolean {
        if (target.teleporting) return true
        if (target.pendingUpdates.playerMaskEntries().any { PlayerUpdateMaskEncoder.hasEncoder(it.first) }) return true

        if (!INCLUDE_APPEARANCE) return false
        // Per-viewer first-appearance tracking: each viewer's client caches appearances independently.
        val appearanceKey = ActiveMaskKeys.playerAppearance ?: return false
        if (!PlayerUpdateMaskEncoder.hasEncoder(appearanceKey)) return false
        val cached = target.appearance.cachedBytes ?: return false
        val viewerHashes = viewer.viewport.cachedApprHashes
        if (target.index !in viewerHashes.indices) return false
        if (viewerHashes[target.index] == null) {
            viewerHashes[target.index] = cached
            return true
        }
        return false
    }

    /**
     * Returns true if [target] would emit a non-empty ext-info block — i.e. it has a pending mask with
     * a registered encoder, or (when [INCLUDE_APPEARANCE] is enabled) a synthesisable APPEARANCE.
     * Gates the `hasExtendedInfo` bit in [encodeHighResPosition].
     */
    private fun hasFlaggableExtendedInfo(target: Player): Boolean {
        val entries = target.pendingUpdates.playerMaskEntries()
        if (entries.any { PlayerUpdateMaskEncoder.hasEncoder(it.first) }) return true

        if (!INCLUDE_APPEARANCE) return false
        val appearanceKey = ActiveMaskKeys.playerAppearance ?: return false
        val cached = target.appearance.cachedBytes
        return cached != null && PlayerUpdateMaskEncoder.hasEncoder(appearanceKey)
    }
}
