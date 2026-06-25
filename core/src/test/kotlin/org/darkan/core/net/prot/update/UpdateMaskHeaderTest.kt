package org.darkan.core.net.prot.update

import kotlin.test.Test
import kotlin.test.assertContentEquals
import org.darkan.core.net.prot.revision.rev948.Rev948NpcUpdateMaskKey
import org.darkan.core.net.prot.revision.rev948.Rev948PlayerUpdateMaskKey

class UpdateMaskHeaderTest {
    @Test
    fun `player header uses 948 expansion bits`() {
        assertContentEquals(
            bytes(0x01, 0x20, 0x20),
            UpdateMaskHeader.player(Rev948PlayerUpdateMaskKey.POSITION_COLOR.flag, Rev948PlayerUpdateMaskKey.EXPANSION_BITS),
        )
        assertContentEquals(
            bytes(0x01, 0x20, 0x40, 0x04),
            UpdateMaskHeader.player(Rev948PlayerUpdateMaskKey.SPOT_ANIM_REMOVAL.flag, Rev948PlayerUpdateMaskKey.EXPANSION_BITS),
        )
    }

    @Test
    fun `npc header uses 948 expansion bits`() {
        assertContentEquals(
            bytes(0x40, 0x01, 0x80),
            UpdateMaskHeader.npc(Rev948NpcUpdateMaskKey.NAME_OVERRIDE.flag, Rev948NpcUpdateMaskKey.EXPANSION_BITS),
        )
        assertContentEquals(
            bytes(0x40, 0x01, 0x08, 0x02, 0x02),
            UpdateMaskHeader.npc(Rev948NpcUpdateMaskKey.UNK_BIT33.flag, Rev948NpcUpdateMaskKey.EXPANSION_BITS),
        )
    }

    private fun bytes(vararg values: Int): ByteArray =
        ByteArray(values.size) { values[it].toByte() }
}
