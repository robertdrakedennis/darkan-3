package org.darkan.core.net

import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import kotlinx.io.Buffer
import org.darkan.core.model.IFEvents
import org.darkan.core.net.prot.*
import org.darkan.core.net.prot.revision.rev948.register948
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ServerDecoderRoundTripTest {

    private val codec = register948()

    private fun roundTrip(prot: ServerProt): Pair<Int, String> = runBlocking {
        val entry = codec.serverProts[prot::class] ?: error("no encoder for ${prot::class.simpleName}")
        val encoder = entry.encoder ?: error("encoder null for ${prot::class.simpleName}")
        val buffer = Buffer()
        val channel = buffer.asByteWriteChannel()
        encoder.invoke(prot, channel)
        channel.flush()
        val size = buffer.size.toInt()
        val decoder = codec.serverDecodersByOpcode[entry.opcode] ?: error("no decoder for op ${entry.opcode}")
        entry.opcode to decoder.invoke(buffer, size)
    }

    private fun check(prot: ServerProt, vararg expectedSubstrings: String) {
        val (op, decoded) = roundTrip(prot)
        for (s in expectedSubstrings) {
            assertTrue(decoded.contains(s), "op$op decode '$decoded' missing '$s' for ${prot::class.simpleName}")
        }
        println("op$op ${prot::class.simpleName}: $decoded")
    }

    @Test fun varpSmall() = check(VarpSmall(1749, 5), "(1749)", "value=5")
    @Test fun varpSmallNegative() = check(VarpSmall(100, -1), "(100)", "value=-1")
    @Test fun varpLarge() = check(VarpLarge(431, 1_000_000), "(431)", "value=1000000")
    @Test fun varpLong() = check(VarpLong(200, 5_000_000_000L), "(200)", "value=5000000000")
    @Test fun varpBitSmall() = check(VarpBitSmall(1000, 3), "varbit=", "(1000)", "value=3")
    @Test fun varpBitLarge() = check(VarpBitLarge(2000, 70_000), "varbit=", "(2000)", "value=70000")
    @Test fun varcSmall() = check(ClientSetVarcSmall(50, 12), "varc=", "(50)", "value=12")
    @Test fun varcLarge() = check(ClientSetVarcLarge(60, 300_000), "varc=", "(60)", "value=300000")
    @Test fun varcBitSmall() = check(ClientSetVarcBitSmall(70, 7), "varcbit=", "(70)", "value=7")
    @Test fun varcBitLarge() = check(ClientSetVarcBitLarge(80, 123_456), "varcbit=", "(80)", "value=123456")
    @Test fun varcStr() = check(ClientSetVarcStr(90, "hello"), "varc=", "(90)", "value=\"hello\"")
    @Test fun updateStat() = check(UpdateStat(skillId = 6, xp = 13_034_431, level = 99), "magic(6)", "level=99", "xp=13034431")

    @Test fun ifSetTopLevel() = check(IfSetTopLevelInterface(906), "topLevelInterface=", "(906)")
    @Test fun ifOpenTop() = check(IfOpenTop(1477, 0), "topLevelInterface=", "(1477)")
    @Test fun ifOpenSub() = check(IfOpenSub(subId = 1473, walkable = 0, parentHash = (906 shl 16) or 44), "sub=", "(1473)", "906:44")
    @Test fun ifSetPosition() = check(IfSetPosition(componentId = 907, layer = 1, position = (906 shl 16) or 44), "906:44", "child=", "(907)", "layer=1")
    @Test fun ifSetEvents() = check(IfSetEvents(IFEvents(906, 44, 0, 5).enableContinueButton()), "906:44", "fromSlot=0", "toSlot=5")
    @Test fun ifSetEvents1() = check(IfSetEvents1(componentHash = (1473 shl 16) or 10, eventsMask = 0x3F, endSlot = 5, startSlot = 0), "1473:10", "mask=63", "startSlot=0", "endSlot=5")
    @Test fun ifSetHide() = check(IfSetHide((906 shl 16) or 44, hide = true), "906:44", "hide=true")
    @Test fun ifCloseSub() = check(IfCloseSub((906 shl 16) or 44), "906:44")
    @Test fun ifSetGraphic() = check(IfSetGraphic(graphicId = 1234, componentHash = (906 shl 16) or 44), "906:44", "graphic=1234")
    @Test fun ifSetAnim() = check(IfSetAnim(animationId = 885, componentHash = (906 shl 16) or 44), "906:44", "(885)")
    @Test fun ifSetText() = check(IfSetText((906 shl 16) or 44, "Hello World"), "906:44", "text=\"Hello World\"")

    // op13 is the real rev948 run-energy opcode (g1, 0..100 RAW); §10.4.
    @Test fun runEnergy() = check(UpdateRunenergy(75), "runEnergy=75")
    // op80 is the private-chat filter (NOT run energy); §10.4. 1=Friends.
    @Test fun setFilterPrivate() = check(SetFilterPrivate(1), "filterPrivate=1")
    @Test fun jcoins() = check(JcoinsUpdate(50_000), "jcoins=50000")
    @Test fun sceneTimingBase() = check(SceneTimingBase(0x12345678), "sceneTimingBase=305419896")
    @Test fun setPlayerOp() = check(SetPlayerOp(slot = 3, text = "Trade with", priority = false), "slot=3", "text=\"Trade with\"")
    @Test fun chatFilter() = check(ChatFilterSettingsPrivateChat(2), "chatFilter=2")

    @Test fun runClientScript() {
        val (op, decoded) = roundTrip(RunClientScript.of(8178, 1, "test", 42))
        assertEquals(110, op)
        assertTrue(decoded.contains("script=8178"), decoded)
        assertTrue(decoded.contains("types=\"isi\""), decoded)
        // logical order restored: [1, "test", 42]
        assertTrue(decoded.contains("[1, \"test\", 42]"), decoded)
        println("op$op RunClientScript: $decoded")
    }
}
