package org.darkan.world.server.packet

import io.ktor.utils.io.ByteChannel
import kotlinx.coroutines.runBlocking
import org.darkan.core.model.Account
import org.darkan.core.net.Isaac
import org.darkan.core.net.prot.MoveGameClick
import org.darkan.core.net.prot.revision.rev948.register948
import org.darkan.core.net.session.GameSession
import org.darkan.world.entity.Direction8
import org.darkan.world.entity.NaiveStraightLineStepProvider
import org.darkan.world.entity.Player
import org.darkan.world.world.Players
import world.gregs.voidps.type.Tile
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Self-test for the op74 click-to-walk handler (STAGE 2.1): a decoded [MoveGameClick] for the player's
 * absolute destination must enqueue the naive straight-line step sequence onto the player's
 * [org.darkan.world.entity.MovementQueue], which the world tick then drains one tile/tick.
 */
class MoveGameClickHandlerTest {

    private var allocatedIndex: Int = -1

    @AfterTest
    fun tearDown() {
        if (allocatedIndex > 0) Players.release(allocatedIndex)
    }

    private fun newPlayer(spawn: Tile): Player {
        val account = Account(username = "walker", displayName = "Walker")
        val session = GameSession(
            write = ByteChannel(),
            isaacIn = Isaac(IntArray(4)),
            isaacOut = Isaac(IntArray(4)),
            ip = "127.0.0.1",
            codec = register948(),
            username = "walker",
        )
        val player = Player(index = 0, account = account, session = session)
        allocatedIndex = Players.allocate(player) { idx -> player.index = idx }
        player.tile = spawn
        return player
    }

    /** Drain the whole queue into the ordered list of tiles the player visits. */
    private fun drain(player: Player): List<Pair<Int, Int>> {
        val visited = ArrayList<Pair<Int, Int>>()
        var x = player.tile.x
        var y = player.tile.y
        while (player.movementQueue.hasPendingStep()) {
            val dir = player.movementQueue.pollStep()
            x += Direction8.DX[dir]
            y += Direction8.DY[dir]
            visited += x to y
        }
        return visited
    }

    @Test
    fun `op74 enqueues a straight-line path from the player tile to the clicked destination`() {
        val player = newPlayer(Tile(3227, 3219, 0))
        val handler = MoveGameClickHandler(stepProvider = NaiveStraightLineStepProvider)

        // Click 5 east, 2 north of the spawn (the open-courtyard pipeline-proof shape).
        runBlocking { handler.handle(player.session, MoveGameClick(destX = 3232, destZ = 3221, modifier = 0)) }

        assertTrue(player.movementQueue.hasPendingStep(), "click enqueued a path")
        val visited = drain(player)
        assertEquals(5, visited.size, "Chebyshev distance max(5,2) = 5 steps")
        assertEquals(3232 to 3221, visited.last(), "the final queued tile is the clicked destination")
    }

    @Test
    fun `a new op74 click supersedes the in-progress path`() {
        val player = newPlayer(Tile(3200, 3200, 0))
        val handler = MoveGameClickHandler(stepProvider = NaiveStraightLineStepProvider)

        // First click: a long east walk.
        runBlocking { handler.handle(player.session, MoveGameClick(destX = 3220, destZ = 3200, modifier = 0)) }
        // Player has not stepped yet; a fresh click to a different tile must replace, not append.
        runBlocking { handler.handle(player.session, MoveGameClick(destX = 3203, destZ = 3203, modifier = 0)) }

        val visited = drain(player)
        assertEquals(3, visited.size, "only the latest click's 3-step path remains")
        assertEquals(3203 to 3203, visited.last(), "queue redirects to the newest destination")
    }

    @Test
    fun `clicking the player's own tile enqueues nothing`() {
        val player = newPlayer(Tile(3227, 3219, 0))
        val handler = MoveGameClickHandler(stepProvider = NaiveStraightLineStepProvider)

        runBlocking { handler.handle(player.session, MoveGameClick(destX = 3227, destZ = 3219, modifier = 0)) }

        assertFalse(player.movementQueue.hasPendingStep(), "no movement for a click on the current tile")
    }
}
