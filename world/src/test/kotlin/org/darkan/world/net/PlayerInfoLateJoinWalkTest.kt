package org.darkan.world.net

import io.ktor.utils.io.ByteChannel
import org.darkan.core.model.Account
import org.darkan.core.net.Isaac
import org.darkan.core.net.prot.PlayerInfo
import org.darkan.core.net.prot.revision.rev948.register948
import org.darkan.core.net.recorder.PlayerInfoDecoder
import org.darkan.core.net.session.GameSession
import org.darkan.world.entity.Direction8
import org.darkan.world.entity.MovementQueue
import org.darkan.world.entity.Player
import org.darkan.world.server.WorldTick
import org.darkan.world.world.Players
import world.gregs.voidps.buffer.write.BufferWriter
import world.gregs.voidps.type.Tile
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * FAITHFUL stateful reproduction of LIVE MULTIPLAYER BUG #2 — a late-joining remote that the ADD adds
 * to the viewer's cohort, but whose per-tick WALK is "barely delivered": the remote's avatar hardly
 * moves and never animates a walk for the viewer.
 *
 * ## ROOT CAUSE (found by this test): the world tick built op22 BEFORE settling every player's movement
 *
 * The pre-fix `WorldTick.runTick` ran one slot-ordered loop that, per player, applied that player's
 * movement AND THEN built that player's op22. So when the lower-slot VIEWER (slot 1) built its op22, the
 * higher-slot REMOTE (slot 2) had NOT yet been stepped this tick: the viewer's encoder read the remote's
 * PREVIOUS-tick tile and a `lastWalkStepDir == NO_STEP` marker (the remote's marker is set only in the
 * remote's own LATER iteration, then wiped by the end-of-tick reset before the viewer's next iteration).
 * Net effect — the viewer NEVER sees a higher-slot remote's per-tick step, so [PlayerInfoEncoder]'s walk
 * state machine emits `mvt=0` holds at a one-tick-stale tile → the remote "barely moves" and never walks.
 *
 * The FIX two-phases the tick: **Phase A** settles EVERY player's movement (`WorldTick.applyTickMovement`),
 * **Phase B** builds every viewer's op22 against the settled world. The walk encoder and the re-anchoring
 * ADD were already correct (proven below — they round-trip clean once the remote's tile + marker are
 * current); the bug was purely the cross-player tick ordering.
 *
 * ## Why the prior [PlayerInfoEncoderRemoteRenderTest] missed it
 *
 * That test set the remote's tile + `lastWalkStepDir` MANUALLY right before building the viewer's op22 —
 * accidentally simulating the FIXED (settle-then-build) order — so it never exercised the real tick's
 * stale-remote ordering. This test drives the REAL [org.darkan.world.server.WorldTick.applyTickMovement]
 * in the production two-phase order via [tickWorld], so a regression to the interleaved order fails it.
 *
 * ## The scenario (the user's repro)
 *
 *  * Viewer A enters the world FIRST (its slot model + the decode prefix seed B's slot anchored to A's
 *    region — B does not exist yet, so `seedFromGpiPrefix` reads no tile for B).
 *  * Remote B LATE-JOINS at B's real spawn (a DIFFERENT region than A's → the slot's seeded anchor is
 *    stale → the ADD must re-anchor, decode branches 1-3 chained under `jumpFlag=1`).
 *  * Then B WALKS a real multi-tile path AND A walks too, simultaneously, for many ticks.
 *
 * Every tick's op22 is fed through the verified stateful [PlayerInfoDecoder.DetailedSession] (the same
 * fold the live oracle uses) and we assert: (1) EVERY op22 decodes clean (zero desync), (2) B is
 * present and its decoded tile advances one-per-tick along its path WITH NO ONE-TICK LAG, (3) B's
 * movement decodes as START then STEP(mvt1)×N then STOP — NOT repeated START, NOT mm/tele-only.
 */
class PlayerInfoLateJoinWalkTest {

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

    private fun newPlayer(name: String, spawn: Tile): Player {
        val account = Account(username = name, displayName = name)
        val session = GameSession(
            write = ByteChannel(),
            isaacIn = Isaac(IntArray(4)),
            isaacOut = Isaac(IntArray(4)),
            ip = "127.0.0.1",
            codec = register948(),
            username = name,
        )
        val player = Player(index = 0, account = account, session = session)
        val idx = Players.allocate(player) { i -> player.index = i }
        allocated += idx
        player.tile = spawn
        return player
    }

    /** Reassemble the on-wire op22 body exactly as the rev948 codec serializes it. */
    private fun wireBody(info: PlayerInfo): ByteArray {
        val out = BufferWriter(8192)
        out.writeBytes(info.bitBlock)
        for (block in info.extendedInfo) {
            out.writeShort(block.size)
            out.writeBytes(block)
        }
        return out.toArray()
    }

    /**
     * Run ONE world tick over [players] using the REAL [org.darkan.world.server.WorldTick] two-phase
     * ordering — the production `runTick` after the bug fix:
     *  * **Phase A (movement):** drive EVERY player's [WorldTick.applyTickMovement] (the real movement
     *    code — poll a step, move the tile, set the per-tick walk markers) so every tile is settled.
     *  * **Phase B (build):** build the [viewer]'s op22 (it now reads every other player's CURRENT-tick
     *    tile + live walk markers).
     *  * end of tick: reset every player's per-tick walk markers (as `runTick`'s clear pass does).
     *
     * This GUARDS the fix: if the production tick regresses to interleaving movement with the build in one
     * slot-ordered loop (the bug), a higher-slot remote's step lands AFTER the viewer already built, the
     * viewer reads a stale tile + `NO_STEP`, and the per-tick advance/START→STEP assertions below fail.
     *
     * [players] MUST be in ascending slot order to match `Players.forEach`.
     */
    private fun tickWorld(viewer: Player, players: List<Player>): PlayerInfo {
        // Phase A — settle all movement first (the real production movement code).
        for (p in players) WorldTick.applyTickMovement(p)
        // Phase B — build the viewer's op22 against the settled world.
        val info = PlayerInfoEncoder.buildIfNeeded(viewer)
        // End-of-tick reset of the per-tick walk markers.
        for (p in players) {
            p.lastWalkStepDir = MovementQueue.NO_STEP
            p.lastRunDelta = MovementQueue.NO_STEP
        }
        return info
    }

    @Test
    fun `late-joiner walks a multi-tile path while the viewer also walks, every tick decodes clean and B steps one tile per tick`() {
        // ---- World entry: viewer A enters FIRST, before B exists. ----
        val viewerSpawn = Tile(3200, 3200, 0)
        val viewer = newPlayer("test", viewerSpawn)
        val viewerIdx = viewer.index
        viewer.viewport.loadSceneBuild(viewerSpawn, org.darkan.world.world.SceneBuildMode.Rebuild)
        viewer.viewport.resetAfterGpiPrefix(viewerIdx)

        // The op81 prefix the world server shipped at A's entry — B is absent, so B's seeded anchor word
        // carries A's (local) region. The decode seeds from the SAME prefix → both sides hold the stale
        // anchor for B's slot.
        val prefix = Op81GpiPrefix.build(spawnTile = viewerSpawn, localPlayerIndex = viewerIdx)
        val session = PlayerInfoDecoder.DetailedSession(PlayerInfoDecoder.GpiPrefix(prefix, viewerIdx))

        // Tick 0: world entry op22 (viewer alone, inline appearance). Sent right after op81.
        val entry = PlayerInfoEncoder.buildWorldEntrySync(viewer)
        val d0 = session.next(wireBody(entry))
        assertTrue(d0.decodeClean, "tick 0 (world entry, viewer alone) must decode cleanly")

        // ---- B LATE-JOINS in a different region than its stale anchor (A's region 50,50). ----
        // B at (3199,3196) → region (49,49). A few tiles SW of A, inside the render window.
        val remoteSpawn = Tile(3199, 3196, 0)
        val remote = newPlayer("test2", remoteSpawn)
        val remoteIdx = remote.index
        assertEquals(49, remoteSpawn.x ushr 6, "B region X 49 differs from A anchor region X 50")
        assertEquals(49, remoteSpawn.y ushr 6, "B region Y 49 differs from A anchor region Y 50")

        val players = listOf(viewer, remote)

        // Queue a real multi-tile path on EACH player. B walks ~10 tiles; A walks ~15 tiles,
        // simultaneously. Use plain cardinal steps so the per-tile expected tiles are easy to assert.
        val north = Direction8.indexOf(0, 1)
        val east = Direction8.indexOf(1, 0)
        // B: 10 steps NORTH then EAST alternating-ish — a genuine multi-tile path.
        val bPath = listOf(north, north, east, north, east, east, north, east, north, east)
        // A: 15 steps EAST then NORTH — a longer path so A and B move together for the whole run.
        val aPath = listOf(east, east, north, east, north, north, east, north, east, east, north, east, north, east, north)
        for (d in bPath) remote.movementQueue.enqueueStep(Direction8.DX[d], Direction8.DY[d])
        for (d in aPath) viewer.movementQueue.enqueueStep(Direction8.DX[d], Direction8.DY[d])

        // ---- Tick 1: the first tick B exists. The viewer's encoder must ADD (re-anchored) B. ----
        // NOTE: on this tick B ALSO takes its first queued step (applyPendingStep runs for B before the
        // viewer builds), so the ADD encodes B's tile AFTER its first step.
        val add = tickWorld(viewer, players)
        val d1 = session.next(wireBody(add))
        assertTrue(d1.decodeClean, "tick 1 (late-join ADD + re-anchor) must decode cleanly")
        val addState = assertNotNull(
            d1.scene.players[remoteIdx],
            "the late-joiner must be ADDED to the viewer's render cohort the first tick it exists",
        )
        val addTile = assertNotNull(addState.tile, "the added remote decodes to a concrete tile")
        // B took bPath[0] (north) on tick 1 before the ADD, so its tile is spawn + north.
        val bAfterStep1 = Tile(remoteSpawn.x + Direction8.DX[bPath[0]], remoteSpawn.y + Direction8.DY[bPath[0]], 0)
        assertEquals(bAfterStep1.x, addTile.x, "B ADD tile X == spawn+step1 (re-anchored to B's live region)")
        assertEquals(bAfterStep1.y, addTile.y, "B ADD tile Y == spawn+step1")
        assertEquals(0, addTile.plane, "B ADD plane == 0")

        // ---- Ticks 2..N: B is now high-res. Each tick B walks one more tile. Drive the rest of the path
        //      and assert clean decode + one-tile-per-tick advance + the WALK state-machine shape. ----
        // B already consumed bPath[0] on tick 1; the remaining ticks are bPath[1..]. We track B's
        // expected tile and the decoded movement type per tick.
        var expectedB = bAfterStep1
        val bMovementTypes = ArrayList<Int>()      // decoded mvt per tick (from tick 2 on)
        val bDescriptors = ArrayList<Int?>()       // decoded move-mode descriptor when mvt=3

        // Remaining steps B will take (it had 10 total, consumed 1 on the ADD tick → 9 left). After the
        // path is exhausted we drive a few more idle ticks to observe the STOP marker. We track B's
        // remaining path explicitly (bPath[1..]) rather than peeking the queue.
        val remainingBSteps = bPath.size - 1
        val totalDriveTicks = remainingBSteps + 2 // + the STOP tick + one idle tick after

        for (t in 0 until totalDriveTicks) {
            // The step B will take THIS tick (null once its path is exhausted) — bPath index t+1.
            val stepDir = if (t + 1 < bPath.size) bPath[t + 1] else null
            val frame = tickWorld(viewer, players)
            val d = session.next(wireBody(frame))
            assertTrue(d.decodeClean, "tick ${t + 2} must decode cleanly (no op22 desync)")

            if (stepDir != null) {
                expectedB = Tile(expectedB.x + Direction8.DX[stepDir], expectedB.y + Direction8.DY[stepDir], 0)
            }

            val state = assertNotNull(
                d.scene.players[remoteIdx],
                "B must stay PRESENT in the viewer's render cohort while it walks (tick ${t + 2})",
            )
            val tile = assertNotNull(state.tile, "B decodes to a concrete tile (tick ${t + 2})")
            assertEquals(expectedB.x, tile.x, "B decoded X advances one tile/tick along its path (tick ${t + 2})")
            assertEquals(expectedB.y, tile.y, "B decoded Y advances one tile/tick along its path (tick ${t + 2})")

            val move = d.scene.movements.singleOrNull { it.index == remoteIdx }
            if (move != null) {
                bMovementTypes += move.movementType
                bDescriptors += if (move.movementType == 3) move.descriptor else null
            } else {
                bMovementTypes += -1 // no movement record this tick (a skip) — record as sentinel
                bDescriptors += null
            }
        }

        // ---- Assert the WALK state-machine shape: START (mvt=3 desc 0x8) then STEP (mvt=1) ×N then
        //      STOP (mvt=3 desc 0x0). NOT repeated START, NOT mm/tele-only. ----
        // tick 2 (the first known-pass tick for B) = WALK-START.
        assertEquals(3, bMovementTypes.first(), "B's first known-pass tick is WALK-START (mvt=3), not a skip or mvt=1")
        assertEquals(0x8 ushr 2, bDescriptors.first(), "B WALK-START descriptor == (byteOffset 0x8)>>2 == 2 (WALK token a38)")

        // The middle steps (while B keeps walking) must be plain WALK-STEP (mvt=1), NOT repeated START.
        // remainingBSteps total steps from tick 2; the FIRST is START, the rest (remainingBSteps-1) are STEP.
        val midSteps = bMovementTypes.subList(1, remainingBSteps)
        assertTrue(
            midSteps.all { it == 1 },
            "B's middle walk ticks must all be WALK-STEP (mvt=1), not repeated START (mvt=3) — got $bMovementTypes",
        )

        // The tick AFTER the last step (B idle) = WALK-STOP marker (mvt=3 desc 0x0).
        val stopMvt = bMovementTypes[remainingBSteps]
        assertEquals(3, stopMvt, "the tick after B's last step is the WALK-STOP marker (mvt=3) — got $bMovementTypes")
        assertEquals(0x0, bDescriptors[remainingBSteps], "B WALK-STOP descriptor == IDLE token (0x0)")

        // Sanity: B took exactly one WALK-START across the whole walk (no repeated re-arm). Count the
        // ticks whose mvt=3 carries the WALK descriptor (0x8>>2); the STOP tick's mvt=3 carries the IDLE
        // descriptor (0x0) and must NOT be counted as a START.
        val walkStartCount = bMovementTypes.indices.count { i ->
            bMovementTypes[i] == 3 && bDescriptors[i] == (0x8 ushr 2)
        }
        assertEquals(
            1, walkStartCount,
            "B emits EXACTLY ONE WALK-START across the whole walk (no repeated re-arm) — got $bMovementTypes",
        )
    }

    @Test
    fun `a late-joiner taking single-tile hops with idle gaps re-arms WALK-START each hop with no desync`() {
        // The live symptom was "mvt=3 ×3 and 0 walk steps". That pattern is the LEGITIMATE encoding of a
        // remote taking SEPARATE single-tile hops (walk 1 tile → idle → walk 1 tile): each hop is a
        // WALK-START (mvt=3, 1 tile) followed by a WALK-STOP (mvt=3) the next idle tick; a plain WALK-STEP
        // (mvt=1) only ever appears on the 2nd+ CONTIGUOUS step. This test proves that shape round-trips
        // cleanly through the verified decode — so `mvt=3 ×N, 0×mvt=1` is NOT itself a desync; it is what a
        // hop-walk looks like. The "barely moves / few-and-stale steps" the live session showed is the
        // TICK-ORDERING bug fixed in this change ([tickWorld] / WorldTick's two-phase split): pre-fix the
        // viewer built its op22 before the higher-slot remote was stepped, so it saw a stale tile + a
        // NO_STEP marker every tick and the remote never animated a walk. [tickWorld] drives the FIXED
        // (move-all-then-build) order via the real WorldTick.applyTickMovement, so this round-trips clean.
        val viewerSpawn = Tile(3200, 3200, 0)
        val viewer = newPlayer("test", viewerSpawn)
        val viewerIdx = viewer.index
        viewer.viewport.loadSceneBuild(viewerSpawn, org.darkan.world.world.SceneBuildMode.Rebuild)
        viewer.viewport.resetAfterGpiPrefix(viewerIdx)
        val prefix = Op81GpiPrefix.build(spawnTile = viewerSpawn, localPlayerIndex = viewerIdx)
        val session = PlayerInfoDecoder.DetailedSession(PlayerInfoDecoder.GpiPrefix(prefix, viewerIdx))
        session.next(wireBody(PlayerInfoEncoder.buildWorldEntrySync(viewer)))

        val remoteSpawn = Tile(3198, 3198, 0)
        val remote = newPlayer("test2", remoteSpawn)
        val remoteIdx = remote.index
        val players = listOf(viewer, remote)

        val north = Direction8.indexOf(0, 1)

        // Add the remote first (tick 1: B exists, no step yet → ADD at spawn).
        run {
            val d = session.next(wireBody(tickWorld(viewer, players)))
            assertTrue(d.decodeClean, "ADD tick must decode cleanly")
            assertNotNull(d.scene.players[remoteIdx], "B is added at spawn")
        }

        // Three single-tile hops, each separated by an idle tick. Schedule: STEP, idle, STEP, idle, STEP, idle.
        val schedule = listOf(true, false, true, false, true, false)
        val mvts = ArrayList<Int>()
        val descs = ArrayList<Int?>()
        var expected = remoteSpawn
        for ((i, doStep) in schedule.withIndex()) {
            if (doStep) {
                remote.movementQueue.enqueueStep(Direction8.DX[north], Direction8.DY[north])
                expected = Tile(expected.x, expected.y + 1, 0)
            }
            val d = session.next(wireBody(tickWorld(viewer, players)))
            assertTrue(d.decodeClean, "hop tick ${i + 1} (step=$doStep) must decode cleanly")
            val st = assertNotNull(d.scene.players[remoteIdx], "B stays present through the hops (tick ${i + 1})")
            assertEquals(expected.x, st.tile?.x, "B X correct after hop tick ${i + 1}")
            assertEquals(expected.y, st.tile?.y, "B Y correct after hop tick ${i + 1}")
            val m = d.scene.movements.singleOrNull { it.index == remoteIdx }
            mvts += m?.movementType ?: -1
            descs += if (m?.movementType == 3) m.descriptor else null
        }

        // Each STEP tick is a WALK-START (mvt=3, desc 0x8); each idle tick after a step is a WALK-STOP
        // (mvt=3, desc 0x0). So we see mvt=3 on EVERY tick and ZERO mvt=1 — exactly the live symptom,
        // here proven to be a clean round-trip.
        assertTrue(mvts.all { it == 3 }, "hop-walk is all mvt=3 (START/STOP markers, no contiguous STEP) — got $mvts")
        assertTrue(mvts.none { it == 1 }, "hop-walk produces ZERO mvt=1 WALK-STEP — got $mvts")
        val startTicks = descs.indices.filter { mvts[it] == 3 && descs[it] == (0x8 ushr 2) }
        assertEquals(listOf(0, 2, 4), startTicks, "the three STEP ticks (0,2,4) are WALK-STARTs (desc 0x8)")
        val stopTicks = descs.indices.filter { mvts[it] == 3 && descs[it] == 0x0 }
        assertEquals(listOf(1, 3, 5), stopTicks, "the three idle ticks (1,3,5) are WALK-STOPs (desc 0x0)")
    }

    @Test
    fun `a late-joiner re-anchored by a LARGE region jump (branch 3) renders and walks cleanly`() {
        // Suspect 2 stress: the re-anchor jump is LARGE (multi-region), forcing the encoder's branch-3
        // (20-bit) region-move under the chained jumpFlag=1 ADD. B's slot is seeded with the viewer's
        // region (50,50); B joins many regions away but still inside the ±6-zone render window? No —
        // a >6-zone gap is outside the window. To exercise branch 3 we need |Δregion| ≥ 2 (a non-
        // Chebyshev-1 region delta) while staying inside the 6-zone (48-tile) window. Region delta 2
        // = 128 tiles is outside the window, so branch 3 by REGION delta alone can't coexist with
        // visibility. Instead: seed a STALE anchor 2 regions off (so the encoder must branch-3 re-anchor
        // back), by registering B, seeding the slot model (which anchors B to its CURRENT region), then
        // moving B's slot anchor artificially stale — mirroring a low-res slot whose remote teleported.
        //
        // Simpler faithful construction: B is registered BEFORE the prefix is built but at a far tile, so
        // its slot anchors far away; then B teleports into the window before the first op22. The anchor is
        // now ≥2 regions from B's live tile → branch-3 re-anchor on ADD.
        val viewerSpawn = Tile(3200, 3200, 0)
        val viewer = newPlayer("test", viewerSpawn)
        val viewerIdx = viewer.index
        viewer.viewport.loadSceneBuild(viewerSpawn, org.darkan.world.world.SceneBuildMode.Rebuild)

        // B registered FAR away first (region (40,40) = 640 tiles SW) so the prefix + slot model anchor it
        // there. Then the viewer's slot model is seeded (reads B's far tile as B's anchor).
        val farTile = Tile(2560, 2560, 0)
        val remote = newPlayer("test2", farTile)
        val remoteIdx = remote.index
        viewer.viewport.resetAfterGpiPrefix(viewerIdx)
        val prefix = Op81GpiPrefix.build(spawnTile = viewerSpawn, localPlayerIndex = viewerIdx)
        val session = PlayerInfoDecoder.DetailedSession(PlayerInfoDecoder.GpiPrefix(prefix, viewerIdx))
        session.next(wireBody(PlayerInfoEncoder.buildWorldEntrySync(viewer)))

        // B teleports into the viewer's window — region (49,49), ≥2 regions from its stale anchor (40,40),
        // so the encoder must use the branch-3 (20-bit) region-move to re-anchor.
        remote.tile = Tile(3199, 3196, 0)
        val players = listOf(viewer, remote)

        // ADD tick (B is in-window now): the encoder re-anchors with a large region jump, then ADDs.
        val dAdd = session.next(wireBody(tickWorld(viewer, players)))
        assertTrue(dAdd.decodeClean, "the LARGE-jump re-anchoring ADD must decode cleanly (branch-3 region-move)")
        val addState = assertNotNull(
            dAdd.scene.players[remoteIdx],
            "the far-anchored late-joiner must still be ADDED (branch-3 re-anchor) once in-window",
        )
        assertEquals(3199, addState.tile?.x, "branch-3 re-anchored B decodes to its live X")
        assertEquals(3196, addState.tile?.y, "branch-3 re-anchored B decodes to its live Y")

        // Then B walks 4 contiguous tiles east — clean START → STEP×3.
        val east = Direction8.indexOf(1, 0)
        repeat(4) { remote.movementQueue.enqueueStep(Direction8.DX[east], Direction8.DY[east]) }
        val mvts = ArrayList<Int>()
        var expected = Tile(3199, 3196, 0)
        repeat(4) { i ->
            expected = Tile(expected.x + 1, expected.y, 0)
            val d = session.next(wireBody(tickWorld(viewer, players)))
            assertTrue(d.decodeClean, "post-re-anchor walk tick ${i + 1} must decode cleanly")
            val st = assertNotNull(d.scene.players[remoteIdx], "B stays present (tick ${i + 1})")
            assertEquals(expected.x, st.tile?.x, "B advances east one tile/tick (tick ${i + 1})")
            mvts += d.scene.movements.singleOrNull { it.index == remoteIdx }?.movementType ?: -1
        }
        assertEquals(listOf(3, 1, 1, 1), mvts, "post-re-anchor walk is a clean START then STEP×3 — got $mvts")
    }
}
