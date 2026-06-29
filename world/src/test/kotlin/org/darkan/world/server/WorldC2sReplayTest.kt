package org.darkan.world.server

import io.ktor.utils.io.ByteChannel
import io.ktor.utils.io.close
import io.ktor.utils.io.writeFully
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.darkan.core.model.Account
import org.darkan.core.net.Isaac
import org.darkan.core.net.prot.ClientProt
import org.darkan.core.net.prot.Codec
import org.darkan.core.net.prot.IfButton
import org.darkan.core.net.prot.MoveGameClick
import org.darkan.core.net.prot.UnhandledClientProt
import org.darkan.core.net.prot.revision.rev948.register948
import org.darkan.core.net.session.GameSession
import org.darkan.world.entity.Direction8
import org.darkan.world.entity.Player
import org.darkan.world.server.packet.MoveGameClickHandler
import org.darkan.world.world.Players
import org.junit.jupiter.api.Assumptions.assumeTrue
import world.gregs.voidps.type.Tile
import java.io.File
import java.util.Base64
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * OFFLINE replay of the world server's in-game c2s read pipeline against real recorder captures.
 *
 * This is the regression guard for the **c2s-read desync** that halted ALL client input at the 2nd
 * post-login game packet (`Session.logMissingClientProt: opcode=239 ... C2S reads stopped`). Root
 * cause: the NXT world client sends ONE pre-ISAAC login-confirm opcode (op26, the bare login-stage
 * descriptor 0x100f13380) on c2s after parsing the world-login response, *before* the
 * ISAAC-enciphered game stream. The world login flow did not consume it, so [GameSession.readPackets]
 * (which ISAAC-deciphers every opcode) decoded that plaintext byte as a ciphered opcode — yielding
 * garbage op239 — AND burned the first keystream value, permanently desyncing every later opcode.
 * The fix consumes the confirm byte in `WorldServer.consumeWorldLoginConfirm` before `readPackets`.
 *
 * ## What this test feeds the reader
 *
 * It reconstructs the captured **game** connection's raw c2s byte stream from `socket.jsonl` (the
 * ciphered wire bytes), drops the pre-ISAAC login prologue — the three login-stage sends the server
 * consumes outside `readPackets`: op14 (CONNECT_LOGIN), the op16 world-login auth packet, and the
 * op26 confirm byte — then pushes the REMAINING bytes (the genuinely ISAAC-enciphered in-game stream,
 * starting at op52 DisplayMetrics) through the SAME [GameSession.readPackets] the live world session
 * runs, seeded with the capture's real `game-c2s` ISAAC keys.
 *
 * It then asserts the decoded [ClientProt] opcode sequence equals the recorder's ground-truth
 * `framed-c2s.jsonl` (the recorder's independently-verified deframe) for the game connection — with
 * ZERO desync: no op239, no [UnhandledClientProt], the reader never stops early, and all op74
 * MoveGameClick packets decode. Finally it drives each decoded op74 through [MoveGameClickHandler]
 * and asserts the player's [org.darkan.world.entity.MovementQueue] holds the expected straight-line
 * path to the clicked tile — the full op74 -> handler -> MovementQueue chain, offline.
 *
 * The fixtures live outside the repo (recorder output on the dev machine); the test [assumeTrue]-skips
 * when they are absent so CI without captures stays green.
 */
class WorldC2sReplayTest {

    /** A reconstructed capture: the ordered game-c2s byte chunks, the per-plane ISAAC seed, and the framed truth. */
    private data class Fixture(
        val label: String,
        /** Raw game-c2s wire bytes in send order (login prologue + ISAAC-ciphered in-game stream). */
        val raw: ByteArray,
        /** Byte length of the pre-ISAAC login prologue (op14 + op16 auth + op26 confirm) the server consumes. */
        val prologueLen: Int,
        /** The exact 4-int ISAAC seed the game-c2s plane was constructed with (from isaac-keys.txt). */
        val gameC2sSeed: IntArray,
        /** Ground-truth in-game c2s opcode sequence from framed-c2s.jsonl (login-stage op14/16/26 dropped). */
        val expectedInGameOpcodes: List<Int>,
        /** Ground-truth op74 bodies (5 raw wire bytes each), in order. */
        val op74Bodies: List<ByteArray>,
    )

    @Test
    fun `local capture in-game c2s deframes with zero desync including op74`() {
        val fx = loadFixture(LOCAL_SESSION, "local") ?: return
        replayAndAssert(fx)
    }

    @Test
    fun `production capture in-game c2s deframes with zero desync including op74`() {
        val fx = loadFixture(PROD_SESSION, "production") ?: return
        replayAndAssert(fx)
    }

    /** Feed the in-game stream through the real reader, assert the full opcode sequence + the op74 chain. */
    private fun replayAndAssert(fx: Fixture) = runBlocking {
        val session = GameSession(
            write = ByteChannel(),
            isaacIn = Isaac(fx.gameC2sSeed.copyOf()),
            isaacOut = Isaac(IntArray(4)),
            ip = "127.0.0.1",
            codec = register948(),
            username = "replay",
        )

        // The in-game stream is everything AFTER the login prologue — exactly what the live reader sees
        // once connectClient + initWorldLogin + consumeWorldLoginConfirm have run.
        val inGame = fx.raw.copyOfRange(fx.prologueLen, fx.raw.size)

        val input = ByteChannel(autoFlush = true)
        input.writeFully(inGame)
        input.close(null)

        val decoded = ArrayList<ClientProt>()
        val reader = launch { session.readPackets(input) }
        // Drain everything the reader produces. A clean run delivers one ClientProt per in-game frame
        // and then the channel goes idle (the reader returns at EOF without closing the session).
        while (true) {
            val packet = withTimeoutOrNull(2_000L) { session.readChannel.receive() } ?: break
            decoded += packet
        }
        reader.join()

        // (1) No desync. The bug surfaced as UnhandledClientProt(opcode=239) — an opcode the codec has
        // NO size metadata for (the unframable case that stops c2s reads). A clean run produces no such
        // packet. Note: UnhandledClientProt itself is EXPECTED here — several in-game opcodes (op105
        // SERVER_CLIENT_VAR_DATA, op59, op81, op15, op106, op78 …) have stub size metadata but no
        // server-side decoder yet; the reader frames them from that metadata and stays in sync. The
        // desync invariant is that EVERY UnhandledClientProt opcode is a KNOWN, size-metadata'd opcode
        // (so it was framed, not mis-decoded), and specifically that op239 never appears.
        val unhandled = decoded.filterIsInstance<UnhandledClientProt>()
        assertTrue(
            unhandled.none { it.opcode == 239 },
            "${fx.label}: decoded desync opcode 239 — c2s reader misframed: $unhandled",
        )
        val unframable = unhandled.filter { session.codec.clientProtInfo[it.opcode] == null }
        assertTrue(
            unframable.isEmpty(),
            "${fx.label}: decoded opcodes with no size metadata (true desync, would stop reads): $unframable",
        )

        // (2) The reader consumed the ENTIRE in-game stream and stopped only at EOF — never via the
        // "C2S reads stopped" early-out. If it had stopped early the decoded count would fall short of
        // the recorder's framed truth.
        assertEquals(
            fx.expectedInGameOpcodes.size,
            decoded.size,
            "${fx.label}: decoded ${decoded.size} in-game packets but the recorder framed ${fx.expectedInGameOpcodes.size} (reader desynced/stopped early)",
        )

        // (3) The decoded opcode sequence matches the recorder's ground truth EXACTLY, frame for frame.
        val decodedOpcodes = decoded.map { opcodeOf(session.codec, it) }
        assertEquals(
            fx.expectedInGameOpcodes,
            decodedOpcodes,
            "${fx.label}: decoded opcode sequence diverged from framed-c2s.jsonl",
        )

        // (4) Every op74 MoveGameClick decoded, and matches the recorder's op74 bodies in order.
        val moveClicks = decoded.filterIsInstance<MoveGameClick>()
        assertEquals(
            fx.op74Bodies.size,
            moveClicks.size,
            "${fx.label}: expected ${fx.op74Bodies.size} op74 MoveGameClick packets, decoded ${moveClicks.size}",
        )
        assertTrue(moveClicks.isNotEmpty(), "${fx.label}: capture must contain op74 click-to-walk packets")
        fx.op74Bodies.forEachIndexed { i, body ->
            assertEquals(decodeOp74(body), moveClicks[i], "${fx.label}: op74 #$i decoded body mismatch")
        }

        // The channel is idle and the session is still open (the c2s read side ends at EOF without
        // tearing down the write side) — i.e. the live session would keep serving s2c.
        assertNull(
            withTimeoutOrNull(50L) { session.readChannel.receive() },
            "${fx.label}: extra packets after the framed stream — over-read past the capture",
        )

        // (5) FULL CHAIN: drive each decoded op74 through the real handler and assert the player's
        // MovementQueue holds the expected naive straight-line path to the clicked tile. This proves
        // op74 decode -> MoveGameClickHandler -> MovementQueue end-to-end, offline.
        assertOp74DrivesMovementQueue(moveClicks)
    }

    /** Run the first decoded op74 through [MoveGameClickHandler] and verify the queued path reaches the click. */
    private fun assertOp74DrivesMovementQueue(moveClicks: List<MoveGameClick>) = runBlocking {
        val click = moveClicks.first()
        // Place the player one Chebyshev "row" away from the click so a non-empty, predictable path is
        // generated by the naive straight-line provider (the production StepProvider for STAGE 2.1).
        val spawn = Tile(click.destX - 3, click.destZ - 2, 0)

        val session = GameSession(
            write = ByteChannel(),
            isaacIn = Isaac(IntArray(4)),
            isaacOut = Isaac(IntArray(4)),
            ip = "127.0.0.1",
            codec = register948(),
            username = "walker",
        )
        val player = Player(index = 0, account = Account(username = "walker", displayName = "Walker"), session = session)
        val allocatedIndex = Players.allocate(player) { idx -> player.index = idx }
        try {
            player.tile = spawn
            MoveGameClickHandler().handle(session, click)

            assertTrue(player.movementQueue.hasPendingStep(), "op74 enqueued a walk path")
            // Drain the queue, applying each 3-bit direction to the player's tile; the final tile MUST be
            // the clicked destination, and the path length MUST equal the Chebyshev distance.
            var x = spawn.x
            var y = spawn.y
            var steps = 0
            while (player.movementQueue.hasPendingStep()) {
                val dir = player.movementQueue.pollStep()
                x += Direction8.DX[dir]
                y += Direction8.DY[dir]
                steps++
            }
            val chebyshev = maxOf(kotlin.math.abs(click.destX - spawn.x), kotlin.math.abs(click.destZ - spawn.y))
            assertEquals(chebyshev, steps, "op74 path length == Chebyshev distance to the click")
            assertEquals(click.destX to click.destZ, x to y, "op74 path ends on the clicked destination tile")
        } finally {
            Players.release(allocatedIndex)
        }
    }

    // ---- fixture loading -------------------------------------------------------------------------

    /**
     * Reconstruct a capture into a [Fixture], or return null (and skip the test) if it is not present.
     * The game-c2s byte stream is rebuilt from `socket.jsonl` (the ciphered wire plane); the login
     * prologue length is the sum of the FIRST THREE game-c2s socket chunks (op14 connect + op16
     * world-login auth packet + op26 confirm), which the live server consumes before `readPackets`.
     */
    private fun loadFixture(sessionDir: String, label: String): Fixture? {
        val dir = File(sessionDir)
        val socket = File(dir, "socket.jsonl")
        val framed = File(dir, "framed-c2s.jsonl")
        val keys = File(dir, "isaac-keys.txt")
        val present = dir.isDirectory && socket.isFile && framed.isFile && keys.isFile
        assumeTrue(present, "recorder fixture '$label' not present at $sessionDir — skipping")
        if (!present) return null

        // Ordered game-c2s wire chunks.
        val chunks = ArrayList<ByteArray>()
        socket.forEachLine { line ->
            if (line.isBlank()) return@forEachLine
            if (jsonStr(line, "conn") != "game" || jsonStr(line, "dir") != "c2s") return@forEachLine
            chunks += Base64.getDecoder().decode(jsonStr(line, "body"))
        }
        assumeTrue(chunks.size >= 4, "fixture '$label' has too few game-c2s chunks — skipping")
        if (chunks.size < 4) return null
        val raw = chunks.reduce { a, b -> a + b }
        // Prologue = op14 connect chunk + op16 world-login auth chunk + op26 confirm chunk.
        val prologueLen = chunks[0].size + chunks[1].size + chunks[2].size

        // Ground-truth framed game-c2s opcodes; drop the three login-stage frames (op14/op16/op26).
        val framedGameOpcodes = ArrayList<Int>()
        val framedGameBodiesOp74 = ArrayList<ByteArray>()
        framed.forEachLine { line ->
            if (line.isBlank()) return@forEachLine
            if (jsonStr(line, "conn") != "game") return@forEachLine
            val op = jsonInt(line, "op")
            framedGameOpcodes += op
            if (op == 74) framedGameBodiesOp74 += Base64.getDecoder().decode(jsonStr(line, "body"))
        }
        assumeTrue(framedGameOpcodes.size > 3, "fixture '$label' framed game c2s too short — skipping")
        // The first three framed game frames are the login-stage op14, op16, op26 (consumed by login).
        val inGameExpected = framedGameOpcodes.drop(3)

        val gameC2sSeed = parseSeed(keys.readText(), "game-c2s")
        assumeTrue(gameC2sSeed != null, "fixture '$label' has no game-c2s ISAAC line — skipping")
        if (gameC2sSeed == null) return null

        return Fixture(
            label = label,
            raw = raw,
            prologueLen = prologueLen,
            gameC2sSeed = gameC2sSeed,
            expectedInGameOpcodes = inGameExpected,
            op74Bodies = framedGameBodiesOp74,
        )
    }

    /** Decode an op74 5-byte wire body the same way the rev948 codec does, for assertion. */
    private fun decodeOp74(body: ByteArray): MoveGameClick {
        require(body.size == 5) { "op74 body must be 5 bytes, got ${body.size}" }
        val modifier = body[0].toInt() and 0xFF
        val destZHi = body[1].toInt() and 0xFF
        val destZLo = (body[2].toInt() - 128) and 0xFF
        val destXLo = body[3].toInt() and 0xFF
        val destXHi = body[4].toInt() and 0xFF
        return MoveGameClick(
            destX = (destXHi shl 8) or destXLo,
            destZ = (destZHi shl 8) or destZLo,
            modifier = modifier and 0x1,
        )
    }

    /**
     * Resolve a decoded [ClientProt]'s opcode by reverse-mapping the active codec's opcode table.
     *
     * [UnhandledClientProt] already carries its opcode. Every other decoded prot type in these captures
     * maps to a single opcode EXCEPT [IfButton], which the codec registers under ten click opcodes keyed
     * by its 1-based option index — disambiguated here via [IfButton.buttonId].
     */
    private fun opcodeOf(codec: Codec, prot: ClientProt): Int {
        if (prot is UnhandledClientProt) return prot.opcode
        if (prot is IfButton) {
            return IF_BUTTON_OPCODE_BY_OPTION[prot.buttonId]
                ?: error("IfButton with unmapped option index ${prot.buttonId}")
        }
        val opcodes = codec.clientProtsByOpcode.entries
            .filter { it.value.protClass == prot::class }
            .map { it.key }
        return when (opcodes.size) {
            0 -> error("no opcode registered for decoded prot ${prot::class.simpleName}")
            1 -> opcodes.single()
            else -> error("ambiguous opcode for ${prot::class.simpleName} (registered under $opcodes) — add disambiguation")
        }
    }

    // ---- tiny JSON helpers (avoid a JSON dep for two string/int fields) --------------------------

    private fun jsonStr(line: String, key: String): String {
        val needle = "\"$key\":\""
        val start = line.indexOf(needle)
        if (start < 0) return ""
        val from = start + needle.length
        val end = line.indexOf('"', from)
        return if (end < 0) "" else line.substring(from, end)
    }

    private fun jsonInt(line: String, key: String): Int {
        val needle = "\"$key\":"
        val start = line.indexOf(needle)
        if (start < 0) return -1
        var i = start + needle.length
        while (i < line.length && (line[i] == ' ')) i++
        val sb = StringBuilder()
        if (i < line.length && line[i] == '-') { sb.append('-'); i++ }
        while (i < line.length && line[i].isDigit()) { sb.append(line[i]); i++ }
        return sb.toString().toIntOrNull() ?: -1
    }

    /** Parse the `<role> ISAAC keys (hex): 0x..,0x..,0x..,0x..` line for [role]. */
    private fun parseSeed(text: String, role: String): IntArray? {
        val line = text.lineSequence().firstOrNull { it.trimStart().startsWith(role) && "hex" in it } ?: return null
        val words = Regex("0x([0-9A-Fa-f]+)").findAll(line).map { it.groupValues[1].toLong(16).toInt() }.toList()
        return if (words.size == 4) words.toIntArray() else null
    }

    companion object {
        private val HOME: String = System.getProperty("user.home")
        private val LOCAL_SESSION = "$HOME/darkan-3/macos/recorder/session-20260629-043412-8376-local"
        private const val PROD_SESSION = "/tmp/darkan-scene-oracle-validate/session-20260628-223414-71972-production"

        /**
         * IF_BUTTON click opcode per 1-based option index — the rev948 codec's dense slot order
         * (Rev948ClientCodecs.ifButtonClick): option1->op127 … option10->op23. Used only to reverse the
         * single multi-opcode prot type back to its on-wire opcode for the framed-sequence assertion.
         */
        private val IF_BUTTON_OPCODE_BY_OPTION = mapOf(
            1 to 127, 2 to 103, 3 to 92, 4 to 45, 5 to 30, 6 to 68, 7 to 43, 8 to 21, 9 to 13, 10 to 23,
        )
    }
}
