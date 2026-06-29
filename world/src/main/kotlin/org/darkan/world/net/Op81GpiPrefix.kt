package org.darkan.world.net

import world.gregs.voidps.buffer.write.BufferWriter
import world.gregs.voidps.type.Tile

/**
 * Generates the **op81 GPI-prefix bitstream** — the 5119-byte Global-Player-Info initialisation
 * block that the NXT 948-5 client UNCONDITIONALLY parses at the head of every fresh world-login
 * `REBUILD_NORMAL_SIMPLE` (op81) body. This is the keystone of **Shape B**
 * (`docs/protocol/world-bootstrap-948.md` §"⚠️ CORRECTION (2026-06-23)").
 *
 * ## Why this is a SEPARATE encoder (not [PlayerInfoEncoder.buildInit])
 *
 * On world entry the client sets `worldState+0x49 = 1` (GPI-present) at the state→30 (LOGGED_IN)
 * transition. With that flag set, op81's handler (`@0x001daa70`) calls the GPI-prefix parser
 * `FUN_00b254a0` **before** it reads the 18-byte coord header, advancing the packet cursor by the
 * full prefix. That parser is `gBit`-based with **NO bounds check** and reads, MSB-first:
 *   - **Local player FIRST:** `gBit(30)` = the packed absolute tile, read DIRECTLY — there is no
 *     `[hasUpdate][hasExt][movementType]` header. This is the critical structural difference from the
 *     op22 PLAYER_INFO high-res form ([PlayerInfoEncoder]/[PlayerMovementEncoder]), where every local
 *     entry is prefixed `[gBit(1) hasUpdate][gBit(1) hasExt][gBit(2) movementType]…`. Feeding the op81
 *     parser that prefixed form would mis-decode the local tile (the header bits would be swallowed
 *     into the high bits of the "tile") — INCOMPATIBLE. Hence a distinct encoder.
 *   - **Other slots:** a loop `slot = 1..2047` that **skips the local `playerIndex`** ⇒ exactly
 *     **2046** iterations, each `gBit(20)` = a packed region-init word. NOTE (adversarial review
 *     2026-06-23): a zero word is **NOT** the "absent" sentinel — `DecodePackedCoord`'s absent
 *     sentinel is `0xFFFFFFFF` (unreachable from 20 bits), and the parser has no presence bit, so it
 *     `operator_new`s a low-res entry for EVERY non-local slot regardless. Zero decodes to a real
 *     coord {plane 0, region (0,0)}. This is HARMLESS for a solo spawn — the entry is an inert
 *     low-res position baseline (not rendered, not a map-load trigger; map squares come from the
 *     BuildArea grid, not player entries). But it is NOT "absent": when multiplayer GPI is built,
 *     each visible player's packed low-res region MUST be written into its slot from the viewport
 *     low-res cohort, not left zero.
 *
 * Total: `30 + 2046×20 = 40950 bits`, which byte-aligns to **5119 bytes** (the last byte carries
 * `40950 mod 8 = 6` used bits + 2 zero pad bits). The parser byte-aligns the cursor at its tail
 * (`*(packet+0x18) = (bits+7)>>3`), landing the 18-byte coord header at body offset **5119** and
 * its magic byte `0x85` at body offset **5122** — exactly where op81's `CMP R14B,0x85` looks.
 *
 * ## Why this is FATAL to omit (Shape A is a black screen)
 *
 * If op81 ships the bare 18-byte header (Shape A, empty `rebuildPrefix`), the parser still runs
 * (flag is set) and reads 40950 bits = 5119 bytes from an 18-byte buffer — a 5101-byte heap
 * over-read. The local player lands at a garbage tile, the cursor advances to 5119, and the
 * handler reads the coord header 5101 bytes past the buffer → magic ≠ `0x85` → op81 returns the
 * abort sentinel **before** the BuildArea alloc and **before** `ProcessCameraReset`. No scene, no
 * camera → black screen. Shape B is mandatory.
 *
 * ## Coherence (still required, §4)
 *
 * The local 30-bit tile written here MUST equal the op81 coord-header centre-zone tile and sit
 * inside the build area — all are derived from the SAME `player.tile` at the call site. The
 * `localPlayerIndex` passed here MUST be the player's allocated slot
 * (`WorldLoginDetails.playerIndex` = `viewport.highResIndices[0]`), so the skipped slot matches
 * the index the client's prefix parser treats as local (`client[0x19b30]+0x48`).
 *
 * ## Bit order
 *
 * [BufferWriter.writeBits] is **MSB-first** (the high `bitCount` bits of `value` are written into
 * the high bits of the current byte), matching `jag::Packet::Bit::gBit`. So `writeBits(30, id)`
 * round-trips to `id` under the client's `gBit(30)` / [world.gregs.voidps.buffer.read.BufferReader.readBits].
 * No LSB-first compensation is needed.
 */
object Op81GpiPrefix {

    /** Highest player slot index. The prefix parser loops slots `1..2047` (= [TOTAL_SLOTS] - 1). */
    const val TOTAL_SLOTS: Int = 2048

    /** Bits in the local player's leading absolute-tile word (`gBit(30)`). */
    const val LOCAL_TILE_BITS: Int = 30

    /** Bits per other-slot region-init word (`gBit(20)`). */
    const val OTHER_SLOT_BITS: Int = 20

    /**
     * Exact byte length of the generated prefix: `(30 + 2046×20 + 7) / 8` = **5119**.
     * Pinned as a constant so the call site and tests can assert the op81 body math
     * (`5119 prefix + 18 header = 5137`, magic at offset `5119 + 3 = 5122`).
     */
    const val PREFIX_BYTES: Int = 5119

    /**
     * Builds the 5119-byte GPI-prefix for a solo world-login spawn.
     *
     * @param spawnTile      the local player's absolute spawn tile. Its [Tile.id]
     *                       (`(plane<<28)|(x<<14)|y`) is written as the leading 30-bit word and
     *                       MUST equal the op81 coord-header centre-zone tile.
     * @param localPlayerIndex the local player's allocated slot (1..2047). This slot is SKIPPED in
     *                       the other-slots loop, yielding exactly 2046 written words, and must
     *                       equal `WorldLoginDetails.playerIndex`.
     * @return a byte array of exactly [PREFIX_BYTES] (5119) bytes, ready to pass as
     *         `RebuildNormalSimple.rebuildPrefix`.
     */
    fun build(spawnTile: Tile, localPlayerIndex: Int): ByteArray {
        require(localPlayerIndex in 1 until TOTAL_SLOTS) {
            "localPlayerIndex must be a real slot in 1..${TOTAL_SLOTS - 1}, was $localPlayerIndex"
        }

        // Allocate exactly the final size: bit writes never grow the buffer, and we byte-align to
        // PREFIX_BYTES at the end so the backing array is the wire size with no trailing slack.
        val out = BufferWriter(PREFIX_BYTES)
        out.startBitAccess()

        // Local player FIRST: gBit(30) = absolute tile, read DIRECTLY by the prefix parser (no
        // [hasUpdate][hasExt][moveType] header — that is the op22 per-tick shape, not this).
        out.writeBits(LOCAL_TILE_BITS, spawnTile.id)

        // Other slots: loop 1..2047 skipping the local index ⇒ exactly 2046 × gBit(20)=0 (absent).
        var written = 0
        for (slot in 1 until TOTAL_SLOTS) {
            if (slot == localPlayerIndex) continue
            out.writeBits(OTHER_SLOT_BITS, 0)
            written++
        }
        check(written == TOTAL_SLOTS - 2) {
            "expected ${TOTAL_SLOTS - 2} other-slot words (2046), wrote $written"
        }

        out.stopBitAccess()
        val bytes = out.toArray()
        check(bytes.size == PREFIX_BYTES) {
            "op81 GPI prefix must be exactly $PREFIX_BYTES bytes, was ${bytes.size}"
        }
        return bytes
    }
}
