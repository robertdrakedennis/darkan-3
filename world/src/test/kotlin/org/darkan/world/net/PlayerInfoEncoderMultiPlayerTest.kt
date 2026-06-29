package org.darkan.world.net

import io.ktor.utils.io.ByteChannel
import org.darkan.core.model.Account
import org.darkan.core.net.Isaac
import org.darkan.core.net.prot.revision.rev948.register948
import org.darkan.core.net.session.GameSession
import org.darkan.world.entity.Player
import org.darkan.world.world.Players
import world.gregs.voidps.buffer.read.BufferReader
import world.gregs.voidps.type.Tile
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

/**
 * Byte-level regression for the multi-player op22 PLAYER_INFO four-pass structure produced by
 * [PlayerInfoEncoder], hand-derived FROM THE VERIFIED DECODE
 * (`core/.../recorder/ClientStateCrossCheck.kt`). These prove the three things the FOUNDATION
 * increment fixes beyond the solo `c0 7f f4` pin (covered by [PlayerInfoBuilderInitTest]):
 *
 *  1. the FOUR passes run in the verified order — known-inactive, known-active, external-active,
 *     external-inactive (`decodePlayerInfo`, `ClientStateCrossCheck.kt:1085-1098`);
 *  2. each pass is BYTE-ALIGNED independently (`alignToByte()` after each,
 *     `ClientStateCrossCheck.kt:1087/1090/1093/1096`);
 *  3. a no-update run is a skip-run RLE whose count is the FOLLOWING same-cohort no-update slots
 *     (`runKnownPass`/`runExternalPass` + `readPlayerSkipCount`,
 *     `ClientStateCrossCheck.kt:1110/1136/1290`), and the per-slot `active` flag — NOT physical list
 *     position — selects the pass (`if (slot.active != activeFlag) continue`,
 *     `ClientStateCrossCheck.kt:1119/1145`).
 *
 * Scene shaping: each test seeds the per-viewer slot model from the op81 prefix (via
 * `resetAfterGpiPrefix`) — local slot `active=false` in the render cohort, every other slot
 * `active=true` in the pending cohort — then performs decode-faithful surgery on the public cohort
 * lists / per-slot flags to construct the target scene. The local player's appearance is marked
 * already-delivered so its pass-1 entry is the clean `hasUpdate=0` stationary hold, isolating the
 * structure under test. A slot with no registered [Player] is treated as a no-update slot (its bits
 * fold into the skip-run), so we don't fabricate appearances for the absent cohort.
 *
 * A full encode→decode round-trip (feeding these bytes back through the recorder oracle's decode to
 * assert the SAME scene) is the increment-2 acceptance path — it needs the recorder decode exposed.
 * Here the hand-derived byte assertions plus the in-test skip-count bit-walk are the guardrail.
 */
class PlayerInfoEncoderMultiPlayerTest {

    /** Total GPI slots — matches [org.darkan.world.world.PlayerInfoSlots.SLOT_COUNT] / the decode's PLAYER_SLOT_COUNT. */
    private val slotCount = 2048

    private val allocated = ArrayList<Int>()

    @BeforeTest
    fun setUp() {
        register948() // publishes ActiveMaskKeys.playerAppearance (read by the appearance synth).
    }

    @AfterTest
    fun tearDown() {
        allocated.forEach { Players.release(it) }
        allocated.clear()
    }

    private fun newPlayer(spawn: Tile): Player {
        val account = Account(username = "tester", displayName = "Tester")
        val session = GameSession(
            write = ByteChannel(),
            isaacIn = Isaac(IntArray(4)),
            isaacOut = Isaac(IntArray(4)),
            ip = "127.0.0.1",
            codec = register948(),
            username = "tester",
        )
        val player = Player(index = 0, account = account, session = session)
        val idx = Players.allocate(player) { i -> player.index = i }
        allocated += idx
        player.viewport.resetAfterGpiPrefix(idx)
        player.tile = spawn
        return player
    }

    /**
     * Drive the local player past world entry so `firstTick` is cleared AND its appearance is recorded
     * as delivered to itself — leaving its render-cohort entry as the clean `hasUpdate=0` stationary
     * hold for the structure tests.
     */
    private fun primeDelivered(player: Player) {
        PlayerInfoEncoder.buildWorldEntrySync(player) // sends appearance, clears firstTick.
        // After world entry the slot model rebuilt with the local active flag flipped by its pass-1
        // skip; re-seed so each scenario starts from the canonical post-prefix cohorts.
        player.viewport.resetAfterGpiPrefix(player.index)
        // Mark this viewer's own appearance as already seen so pass 1 is a no-update stationary hold.
        player.viewport.cachedApprHashes[player.index] = player.appearance.cachedBytes
    }

    @Test
    fun `a known-active slot is emitted in pass 2, separately byte-aligned from pass 1`() {
        // Scene: local L (render, active=false, appearance delivered) + one OTHER render slot K
        // (active=TRUE, no registered player → no update). Per the decode, L is processed by pass 1
        // (known-inactive) and K by pass 2 (known-active) — even though both are in the render cohort,
        // the `active` flag routes them to different passes (ClientStateCrossCheck.kt:1119).
        val local = newPlayer(Tile(3200, 3200, 0))
        primeDelivered(local)
        val slots = local.viewport.playerSlots
        val k = if (local.index == 1) 2 else 1 // any other slot

        // decode-faithful surgery: move K from the pending cohort into the render cohort, present, but
        // keep active=TRUE so it lands in pass 2.
        slots.pendingList.remove(k)
        slots.renderList.add(k)
        slots.slot(k)!!.present = true
        slots.slot(k)!!.active = true

        val info = PlayerInfoEncoder.buildIfNeeded(local)

        // Hand-derivation (MSB-first, byte-aligned per pass):
        //  Pass 1 (known,active=false): L matches → hasUpdate=0, skip-run count=0 (K is active=true,
        //    not in this cohort) → bits 0 00 = 000 → 0x00.
        //  Pass 2 (known,active=true): K matches → no update → hasUpdate=0, skip count=0 → 000 → 0x00.
        //  Pass 3 (external,active=true): the remaining 2045 pending slots → first hasUpdate=0, then a
        //    skip-run over the following 2044 → 0 | mode3(11) | count=2044 (0b11111111100). 14 bits:
        //    0 11 11111111100 → 0x7F 0xF0.
        //  Pass 4 (external,active=false): none → no bits.
        assertContentEquals(
            byteArrayOf(0x00, 0x00, 0x7F, 0xF0.toByte()),
            info.bitBlock,
            "pass1 0x00 | pass2 0x00 (known-active K) | pass3 0x7F 0xF0 (2044-skip)",
        )
        assertEquals(0, info.extendedInfo.size, "no slot has deliverable ext-info this tick")

        // In-test bit-walk: decode each byte-aligned pass from its own byte offset (each pass starts on
        // a byte boundary, so a fresh reader over the slice re-derives that pass's bits exactly).
        readPass(info.bitBlock, byteOffset = 0) { // pass 1 (L, known-inactive)
            assertEquals(0, readBits(1), "pass1 L hasUpdate=0")
            assertEquals(0, readBits(2), "pass1 skipMode=0 (no following inactive-known slot)")
        }
        readPass(info.bitBlock, byteOffset = 1) { // pass 2 (K, known-active)
            assertEquals(0, readBits(1), "pass2 K hasUpdate=0")
            assertEquals(0, readBits(2), "pass2 skipMode=0")
        }
        readPass(info.bitBlock, byteOffset = 2) { // pass 3 (external-active skip-run)
            assertEquals(0, readBits(1), "pass3 first pending hasUpdate=0")
            assertEquals(3, readBits(2), "pass3 skipMode=3 (11-bit count)")
            assertEquals(2044, readBits(11), "pass3 skip count = following 2044 active-pending slots")
        }
    }

    @Test
    fun `external cohort splits into its own active and inactive skip-runs across pass 3 and pass 4`() {
        // Scene: local L (render, no update). Of the 2046 pending slots, flip the LAST 3 (by index) to
        // active=FALSE so they fall into pass 4; the other 2043 stay active=TRUE in pass 3. Proves both
        // external passes fire with independent, separately byte-aligned skip-runs.
        val local = newPlayer(Tile(3200, 3200, 0))
        primeDelivered(local)
        val slots = local.viewport.playerSlots

        val inactiveThree = slots.pendingList.takeLast(3)
        inactiveThree.forEach { slots.slot(it)!!.active = false }
        val activePendingCount = slots.pendingList.size - 3 // 2043

        val info = PlayerInfoEncoder.buildIfNeeded(local)

        // Hand-derivation:
        //  Pass 1 (known,active=false): L no update → 0 00 = 000 → 0x00.
        //  Pass 2 (known,active=true): renderList has only L (active=false) → empty → no bits.
        //  Pass 3 (external,active=true): 2043 slots → first hasUpdate=0, skip=2042 (0b11111111010).
        //    14 bits: 0 11 11111111010 → 0x7F 0xE8.
        //  Pass 4 (external,active=false): 3 slots → first hasUpdate=0, skip=2. count<32 → mode1(5):
        //    0 01 00010 → 8 bits → 0x22.
        assertContentEquals(
            byteArrayOf(0x00, 0x7F, 0xE8.toByte(), 0x22),
            info.bitBlock,
            "pass1 0x00 | pass3 0x7F 0xE8 (2042-skip) | pass4 0x22 (2-skip)",
        )
        assertEquals(0, info.extendedInfo.size)

        // Bit-walk both external runs from their byte offsets to confirm the cohort split + counts.
        // Pass 1 → byte 0; pass 2 emitted nothing (renderList has only the inactive L); pass 3 → byte 1
        // (2 bytes wide: 0x7F 0xE8); pass 4 → byte 3 (0x22).
        readPass(info.bitBlock, byteOffset = 0) { // pass 1 (L)
            assertEquals(0, readBits(1)); assertEquals(0, readBits(2))
        }
        readPass(info.bitBlock, byteOffset = 1) { // pass 3 (external-active)
            assertEquals(0, readBits(1), "pass3 first active-pending hasUpdate=0")
            assertEquals(3, readBits(2), "pass3 skipMode=3")
            assertEquals(activePendingCount - 1, readBits(11), "pass3 skip = 2043-1 following active slots")
        }
        readPass(info.bitBlock, byteOffset = 3) { // pass 4 (external-inactive)
            assertEquals(0, readBits(1), "pass4 first inactive-pending hasUpdate=0")
            assertEquals(1, readBits(2), "pass4 skipMode=1 (5-bit count)")
            assertEquals(2, readBits(5), "pass4 skip = 3-1 following inactive slots")
        }
    }

    /**
     * Decode one byte-aligned pass starting at [byteOffset] of [block]. Each GPI pass begins on a byte
     * boundary, so a fresh [BufferReader] over the slice from [byteOffset] reproduces that pass's bits
     * exactly — sidestepping the reader's private bit cursor and the per-pass alignment bookkeeping.
     */
    private fun readPass(block: ByteArray, byteOffset: Int, body: BufferReader.() -> Unit) {
        val r = BufferReader(block.copyOfRange(byteOffset, block.size))
        r.startBitAccess()
        r.body()
        r.stopBitAccess()
    }
}
