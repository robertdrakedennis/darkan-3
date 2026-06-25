package org.darkan.core.net.prot

import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import org.darkan.core.net.prot.revision.rev948.register948
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class Rev948InventoryCodecTest {
    private val codec = register948()

    private fun encodeBody(prot: ServerProt): ByteArray = runBlocking {
        val entry = codec.serverProts[prot::class] ?: error("No encoder registered for ${prot::class.simpleName}")
        val encoder = entry.encoder ?: return@runBlocking byteArrayOf()
        val channel = ByteChannel()
        encoder.invoke(prot, channel)
        channel.flush()
        val out = ByteArray(channel.availableForRead)
        channel.readFully(out)
        channel.close(null)
        out
    }

    @Test
    fun `empty inventory full packet encodes production header shape`() {
        val entry = codec.serverProts[UpdateInvFull::class] ?: error("missing UpdateInvFull")

        assertEquals(85, entry.opcode)
        assertEquals(ProtSize.VarShort, entry.size)
        assertContentEquals(
            byteArrayOf(0x03, 0x13, 0x00, 0x00, 0x00),
            encodeBody(UpdateInvFull(inventoryId = 0x0313)),
        )
    }

    @Test
    fun `inventory full item entry encodes id plus one and extended quantity`() {
        assertContentEquals(
            byteArrayOf(
                0x03, 0x1B, 0x00, 0x00, 0x01,
                0x00, 0x65,
                0xFF.toByte(), 0x00, 0x00, 0x03, 0xE8.toByte(),
            ),
            encodeBody(
                UpdateInvFull(
                    inventoryId = 0x031B,
                    entries = listOf(InventoryEntry(itemId = 100, quantity = 1000)),
                )
            ),
        )
    }

    @Test
    fun `inventory full metadata flag adds one byte per slot`() {
        assertContentEquals(
            byteArrayOf(
                0x00, 0x5D, 0x02, 0x00, 0x01,
                0x01, 0x3C, 0x01, 0x00,
            ),
            encodeBody(
                UpdateInvFull(
                    inventoryId = 0x005D,
                    flags = 0x2,
                    entries = listOf(InventoryEntry(itemId = 0x013B, quantity = 1)),
                )
            ),
        )
    }
}
