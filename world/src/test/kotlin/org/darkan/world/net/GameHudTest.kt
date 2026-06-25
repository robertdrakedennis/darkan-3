package org.darkan.world.net

import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import org.darkan.core.model.IFEvents
import org.darkan.core.net.prot.IfSetEvents
import org.darkan.core.net.prot.IfSetHide
import org.darkan.core.net.prot.IfSetPosition
import org.darkan.core.net.prot.IfSetTopLevelInterface
import org.darkan.core.net.prot.RunClientScript
import org.darkan.core.net.prot.ServerProt
import org.darkan.core.net.prot.revision.rev948.register948
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Wire-format tests for the world-entry HUD open (`GameHud`, root 1477) per
 * `docs/protocol/world-entry-render-948.md` §7. Exercises the op3/op82/op110/op35 encoders the HUD
 * uses and pins the §7.2 component map / §7.3 script arg lists. `GameHud` itself stays UNWIRED.
 */
class GameHudTest {

    private val codec = register948()

    /** Encode a ServerProt through its registered 948 encoder and return the raw body bytes. */
    private fun encodeBody(prot: ServerProt): ByteArray = runBlocking {
        val entry = codec.serverProts[prot::class]
            ?: error("No encoder registered for ${prot::class.simpleName}")
        val ch = ByteChannel()
        (entry.encoder ?: error("Null encoder for ${prot::class.simpleName}")).invoke(prot, ch)
        ch.flush()
        val out = ByteArray(ch.availableForRead)
        ch.readFully(out)
        ch.close(null)
        out
    }

    @Test
    fun `component map is the root-1477 production placement sequence`() {
        assertEquals(55, GameHud.COMPONENT_MAP.size, "prod 1477 child placement sequence has 55 rows")
        // Spot-check the documented anchors.
        assertEquals(1482, GameHud.COMPONENT_MAP.first { it.slot == 31 }.child, "slot 31 ← 1482")
        assertEquals(1473, GameHud.COMPONENT_MAP.first { it.slot == 103 }.child, "slot 103 ← 1473 (inventory)")
        assertEquals(1847, GameHud.COMPONENT_MAP.first { it.slot == 911 }.child, "slot 911 ← 1847 (last row)")
        assertEquals(464, GameHud.COMPONENT_MAP.first { it.slot == 471 }.child, "slot 471 ← 464")
        assertEquals(53, GameHud.COMPONENT_MAP.map { it.slot }.toSet().size, "prod repeats active tab slots 95 and 96")
        assertEquals(2, GameHud.COMPONENT_MAP.count { it == HudComponent(95, 1465) })
        assertEquals(2, GameHud.COMPONENT_MAP.count { it == HudComponent(96, 1919) })
    }

    @Test
    fun `op3 IF_SETTOPLEVELINTERFACE carries root 1477 at offset 9-10`() {
        val body = encodeBody(IfSetTopLevelInterface(topLevelId = GameHud.ROOT_INTERFACE))
        assertEquals(19, body.size, "op3 body is 19 bytes")
        // §7.1 / codec: id at offset 9 (low byte, writeByteAdd → +0x80) and offset 10 (high byte).
        // Client recovers id = byte10*0x100 + ((byte9 + 0x80) wait: byte9 is ALREADY +0x80; client does
        // (byte9 + 0x80) again? No — server wrote (id&0xFF)+0x80; client reads ((wire+0x80)&0xFF)?? The
        // codec is the source of truth: byte9 = writeByteAdd(id&0xFF) = (id + 0x80) & 0xFF; byte10 = id>>8.
        val low = body[9].toInt() and 0xFF
        val high = body[10].toInt() and 0xFF
        val recovered = (high shl 8) or ((low - 0x80) and 0xFF)
        assertEquals(GameHud.ROOT_INTERFACE, recovered, "op3 id round-trips to 1477")
    }

    @Test
    fun `production op3 in-game payload decodes to the GAME_HUD_INTERFACE 1477`() {
        // The in-game transition (docs/protocol/world-ingame-transition-948.md §8 task #1). Production
        // world S2C idx 2295 op3 IF_SETTOPLEVELINTERFACE, size 19, payload below. Decoding the id from
        // the payload through OUR op3 codec must yield 1477 — the established-account HUD root — NOT
        // the character-creation interface 1349. This pins which id the payload encodes and that our
        // GameHud root matches it.
        val payload = "00000000000000000045050000000000000000"
            .chunked(2).map { it.toInt(16).toByte() }.toByteArray()
        assertEquals(19, payload.size, "captured op3 payload is 19 bytes")

        // Same recovery the client/codec performs: byte9 = writeByteAdd(id&0xFF), byte10 = id>>8.
        val low = payload[9].toInt() and 0xFF      // 0x45
        val high = payload[10].toInt() and 0xFF    // 0x05
        val decodedId = (high shl 8) or ((low - 0x80) and 0xFF)

        assertEquals(1477, decodedId, "production op3 in-game payload encodes interface 1477")
        assertEquals(GameHud.ROOT_INTERFACE, decodedId, "decoded id == GameHud HUD root")
        assertEquals(
            decodedId,
            encodeBody(IfSetTopLevelInterface(topLevelId = GameHud.ROOT_INTERFACE)).let { b ->
                ((b[10].toInt() and 0xFF) shl 8) or (((b[9].toInt() and 0xFF) - 0x80) and 0xFF)
            },
            "encoding GameHud.ROOT_INTERFACE reproduces the captured op3 id bytes",
        )
    }

    @Test
    fun `op82 IF_SETPOSITION packs parent hash (1477 lt lt 16 or slot) and child id`() {
        val slot = 103
        val child = 1473
        val pos = GameHud.componentHash(GameHud.ROOT_INTERFACE, slot)
        assertEquals((1477 shl 16) or 103, pos, "parent hash = (1477<<16)|slot")

        val body = encodeBody(IfSetPosition(componentId = child, layer = 1, position = pos))
        assertEquals(23, body.size, "op82 body is 23 bytes")

        // [5..8] position via g4_alt3 (writeIntInverseMiddle → wire [B1,B0,B3,B2]); decode it back.
        val b = IntArray(4) { body[5 + it].toInt() and 0xFF }
        val decodedPos = (b[0] shl 24) or (b[1] shl 16) or (b[2] shl 8) or b[3]   // inverse-middle reader: B1<<24|B0<<16|B3<<8|B2
        val recoveredPos = (b[1] shl 24) or (b[0] shl 16) or (b[3] shl 8) or b[2]
        assertEquals(pos, recoveredPos, "op82 position round-trips to the parent hash")
        assertTrue(decodedPos != 0)   // sanity: bytes are populated

        // [21..22] componentId via writeShortLittle (low first).
        val cid = (body[21].toInt() and 0xFF) or ((body[22].toInt() and 0xFF) shl 8)
        assertEquals(child, cid, "op82 componentId is the child interface (LE)")
    }

    @Test
    fun `op110 HUD scripts use production tab enabled arg order`() {
        assertEquals(22, GameHud.HUD_TAB_ARGS.size, "8862 runs 22 times")
        assertEquals(54, GameHud.POST_OPEN_SCRIPTS.size, "post-open script bootstrap fills out prod's 79 script calls")
        assertEquals(0 to 1, GameHud.HUD_TAB_ARGS.first(), "first tab arg = (0, 1)")
        assertEquals(1025 to 0, GameHud.HUD_TAB_ARGS.last(), "last tab arg = (1025, 0)")
        assertEquals(11145, GameHud.POST_OPEN_SCRIPTS.first().scriptId)
        assertEquals(18954, GameHud.POST_OPEN_SCRIPTS.last().scriptId)
        assertTrue(12 to 0 in GameHud.HUD_TAB_ARGS)
        assertTrue(30 to 0 in GameHud.HUD_TAB_ARGS)
        assertTrue(45 to 0 in GameHud.HUD_TAB_ARGS)
        assertTrue(46 to 0 in GameHud.HUD_TAB_ARGS)

        val script = RunClientScript.of(GameHud.SCRIPT_HUD_TAB, 0, 1)
        val body = encodeBody(script)
        assertEquals(0, body[3].toInt())
        assertEquals(1, body[6].toInt())
        // Trailing 4 bytes = scriptId (BE u32) in the existing op110 codec.
        val sid = ((body[body.size - 4].toInt() and 0xFF) shl 24) or
            ((body[body.size - 3].toInt() and 0xFF) shl 16) or
            ((body[body.size - 2].toInt() and 0xFF) shl 8) or
            (body[body.size - 1].toInt() and 0xFF)
        assertEquals(GameHud.SCRIPT_HUD_TAB, sid, "op110 trailing scriptId is 8862")
    }

    @Test
    fun `op35 IF_SETEVENTS packs the component hash and slot range`() {
        val events = IFEvents(interfaceId = 1473, componentId = 0, fromSlot = 0, toSlot = 27, settings = 0)
        val body = encodeBody(IfSetEvents(events))
        assertEquals(12, body.size, "op35 body is 12 bytes")
        // [8..11] componentHash BE = (1473<<16)|0.
        val hash = ((body[8].toInt() and 0xFF) shl 24) or ((body[9].toInt() and 0xFF) shl 16) or
            ((body[10].toInt() and 0xFF) shl 8) or (body[11].toInt() and 0xFF)
        assertEquals((1473 shl 16) or 0, hash, "op35 componentHash = (interfaceId<<16)|componentId")
        // [4..5] fromSlot LE, [6..7] toSlot LE.
        val fromSlot = (body[4].toInt() and 0xFF) or ((body[5].toInt() and 0xFF) shl 8)
        val toSlot = (body[6].toInt() and 0xFF) or ((body[7].toInt() and 0xFF) shl 8)
        assertEquals(0, fromSlot)
        assertEquals(27, toSlot)
    }

    @Test
    fun `op91 IF_SETHIDE uses production false and true flags`() {
        assertContentEquals(
            byteArrayOf(0x80.toByte(), 0x00, 0x05, 0x05, 0x89.toByte()),
            encodeBody(IfSetHide(componentHash = GameHud.componentHash(1417, 5), hide = false)),
        )
        assertContentEquals(
            byteArrayOf(0x81.toByte(), 0x00, 0x47, 0x02, 0x8D.toByte()),
            encodeBody(IfSetHide(componentHash = GameHud.componentHash(653, 71), hide = true)),
        )
    }
}
