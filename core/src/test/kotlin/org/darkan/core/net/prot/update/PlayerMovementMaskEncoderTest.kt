package org.darkan.core.net.prot.update

import org.darkan.core.net.prot.revision.rev948.Rev948PlayerUpdateMaskKey
import org.darkan.core.net.prot.revision.rev948.register948
import world.gregs.voidps.buffer.read.BufferReader
import world.gregs.voidps.buffer.write.BufferWriter
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Byte-precise round-trip tests for the two 948 PLAYER_INFO movement ext-info blocks added for the local
 * avatar walk-animation + glide fix, per the binary-verified spec
 * `re-resources/docs/net/serverprot/player-appearance-948.md`:
 *
 *  - **MOVEMENT_ANIM** (bit 5 / 0x20): `4× gSmart2or4s` (movement-anim seq ids) + `1× g1_sub` (priority),
 *    the walk/run leg-animation block consumed by `SetMovementAnimSet @0x1003a69f0`.
 *  - **FORCED_MOVEMENT** (bit 7 / 0x80): 6× transformed byte + 2× g2 (start/end tick) + a 14-bit yaw
 *    (p1 + 6-bit), the glide block consumed by `SetRenderWaypoint @0x10039e220`.
 *
 * Each test encodes via the registered [PlayerUpdateMaskEncoder] (the exact production path) then decodes
 * with the EXACT inverse of the client's per-field reads — the same `DecodePlayerExtendedInfo` transform
 * pairs the recorder's `PlayerInfoDecoder` consumes — and asserts the values round-trip and the byte count
 * matches the wire spec.
 */
class PlayerMovementMaskEncoderTest {

    @BeforeTest
    fun setUp() {
        register948() // registers the player mask encoders into PlayerUpdateMaskEncoder.
    }

    private fun encode(key: PlayerUpdateMaskKey, mask: UpdateMask): ByteArray {
        val out = BufferWriter(64)
        PlayerUpdateMaskEncoder.encode(out, key, mask)
        return out.toArray()
    }

    // -------------------------------------------------------------------------------------------------
    // MOVEMENT_ANIM (bit 0x20): 4× gSmart2or4s + 1× g1_sub
    // -------------------------------------------------------------------------------------------------

    @Test
    fun `movement anim encodes 4 bigSmart seq ids plus a sub-byte priority`() {
        val mask = UpdateMask.MovementAnim(seq0 = 1422, seq1 = 1427, seq2 = 1426, seq3 = 1426, priority = 5)
        val bytes = encode(Rev948PlayerUpdateMaskKey.MOVEMENT_ANIM, mask)

        // All four ids are in [0,32766] → 2-byte gSmart2or4s each; priority is one g1_sub byte. 9 bytes.
        assertEquals(2 * 4 + 1, bytes.size, "4× 2-byte bigSmart + 1 sub-byte = 9 bytes")

        val r = BufferReader(bytes)
        assertEquals(1422, r.readBigSmart(), "seq0")
        assertEquals(1427, r.readBigSmart(), "seq1")
        assertEquals(1426, r.readBigSmart(), "seq2")
        assertEquals(1426, r.readBigSmart(), "seq3")
        assertEquals(5, r.readByteSubtract(), "priority (g1_sub)")
        assertEquals(0, r.remaining, "no trailing bytes")
    }

    @Test
    fun `movement anim encodes -1 seq ids as the bigSmart null sentinel (hard-stop form)`() {
        // 4× -1 is the bigSmart null-sentinel form the client special-cases to ResetMovementSeqs
        // @0x100589ea0 — an IMMEDIATE hard stop (empties the route-anim queue, NO crossfade). Ordinary
        // walking does NOT send any MOVEMENT_ANIM block at all (faithful: prod sends no per-step movement
        // ext-info — see PlayerExtInfoEncoder). This test only guards the ENCODER's -1 handling for any
        // future non-walk caller that needs the hard-stop form. Each -1 MUST hit the wire as
        // the 2-byte bigSmart null sentinel 0x7FFF (high bit clear → 2-byte form; 0x7FFF reads back as -1),
        // so the block is 4×2 + 1 sub-byte = 9 bytes.
        val mask = UpdateMask.MovementAnim(seq0 = -1, seq1 = -1, seq2 = -1, seq3 = -1, priority = 0)
        val bytes = encode(Rev948PlayerUpdateMaskKey.MOVEMENT_ANIM, mask)
        assertEquals(2 * 4 + 1, bytes.size, "4× 2-byte 0x7FFF sentinel + 1 sub-byte = 9 bytes")

        // Raw wire bytes: each of the four seqs is 0x7F 0xFF (big-endian 0x7FFF), proving the sentinel
        // form rather than incidentally decoding to -1.
        for (i in 0 until 4) {
            assertEquals(0x7F, bytes[i * 2].toInt() and 0xFF, "seq $i high byte = 0x7F (bigSmart -1 sentinel)")
            assertEquals(0xFF, bytes[i * 2 + 1].toInt() and 0xFF, "seq $i low byte = 0xFF (bigSmart -1 sentinel)")
        }

        val r = BufferReader(bytes)
        repeat(4) { assertEquals(-1, r.readBigSmart(), "seq $it round-trips as -1") }
        assertEquals(0, r.readByteSubtract(), "priority")
        assertEquals(0, r.remaining, "no trailing bytes")
    }

    @Test
    fun `movement anim emits a 4-byte bigSmart for a large seq id`() {
        // id > 32766 → 4-byte form with the high bit set. 4 + 3*2 + 1 = 11 bytes.
        val mask = UpdateMask.MovementAnim(seq0 = 70000, seq1 = 1, seq2 = 2, seq3 = 3, priority = 0)
        val bytes = encode(Rev948PlayerUpdateMaskKey.MOVEMENT_ANIM, mask)
        assertEquals(4 + 3 * 2 + 1, bytes.size, "one 4-byte bigSmart + three 2-byte + 1 sub-byte")

        val r = BufferReader(bytes)
        assertEquals(70000, r.readBigSmart(), "large seq0 round-trips")
        assertEquals(1, r.readBigSmart())
        assertEquals(2, r.readBigSmart())
        assertEquals(3, r.readBigSmart())
        assertEquals(0, r.readByteSubtract())
    }

    // -------------------------------------------------------------------------------------------------
    // FORCED_MOVEMENT (bit 0x80): glide — 6× transformed tile-delta byte + 2× g2 + 14-bit yaw
    // -------------------------------------------------------------------------------------------------

    @Test
    fun `forced movement glide encodes 12 bytes with the exact per-field transforms`() {
        // Two-segment glide deltas: start (segment-1 end) < end (segment-2 end) so the client's forward
        // prev->target interpolation runs (player-appearance-948.md §Q2). 1 = minimal ease toward prev
        // (never 0); 36 = one game tick in Client+0x518 cycles. (Encoder capability test only — ordinary
        // walking emits no FORCED_MOVEMENT block; this guards the block for future forced-move callers.)
        val mask = UpdateMask.ForcedMovement(
            srcDx = -1, srcDz = 1, dstDx = 0, dstDz = 0,
            delta3 = 0, delta4 = 0,
            startTime = 1, endTime = 36,
            yaw = 0x1234, // 14 bits → 0x1234 & 0x3FFF
        )
        val bytes = encode(Rev948PlayerUpdateMaskKey.FORCED_MOVEMENT, mask)
        assertEquals(12, bytes.size, "6× byte + 2× short + 2× yaw byte = 12 bytes")

        val r = BufferReader(bytes)
        // The client reads each field with the transform listed in the doc; the encoder writes the inverse.
        assertEquals(-1, r.readByteAdd(), "srcDx tile delta (g1_add)")
        assertEquals(1, r.readByteInverse(), "srcDz tile delta (g1_neg)")
        assertEquals(0, r.readByte(), "dstDx (g1)")
        assertEquals(0, r.readByteSubtract(), "dstDz (g1_sub)")
        assertEquals(0, r.readByteAdd(), "delta3 (g1_add)")
        assertEquals(0, r.readByteInverse(), "delta4 (g1_neg)")
        assertEquals(1, r.readShort(), "startTick = segment-1 end delta (g2 BE)")
        assertEquals(36, r.readShort(), "endTick = segment-2 end delta (g2 BE), one game tick of glide")
        // 14-bit yaw: low byte g1_add, high 6 bits (wire & 0x3f) << 8.
        val yawLow = r.readByteAdd() and 0xFF
        val yawHigh = (r.readByte() and 0x3F) shl 8
        assertEquals(0x1234 and 0x3FFF, yawHigh or yawLow, "14-bit yaw round-trips")
        assertEquals(0, r.remaining, "no trailing bytes")
    }

    @Test
    fun `forced movement yaw masks to 14 bits`() {
        // A yaw with bits above 0x3FFF must not leak into the byte fields.
        val mask = UpdateMask.ForcedMovement(
            srcDx = 0, srcDz = 0, dstDx = 0, dstDz = 0, delta3 = 0, delta4 = 0,
            startTime = 30, endTime = 60, yaw = 0x3FFF,
        )
        val bytes = encode(Rev948PlayerUpdateMaskKey.FORCED_MOVEMENT, mask)
        val r = BufferReader(bytes)
        repeat(6) { r.readByte() }
        assertEquals(30, r.readShort(), "startTick")
        assertEquals(60, r.readShort(), "endTick")
        val yawLow = r.readByteAdd() and 0xFF
        val yawHigh = (r.readByte() and 0x3F) shl 8
        assertEquals(0x3FFF, yawHigh or yawLow, "max 14-bit yaw")
    }
}
