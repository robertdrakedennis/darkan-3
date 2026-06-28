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
    fun `inventory full param flag adds count and param block per slot`() {
        assertContentEquals(
            byteArrayOf(
                0x00, 0x5D, 0x02, 0x00, 0x01,
                0x01, 0x3C, 0x01, 0x01,
                0x00, 0x2A, 0x11, 0x22, 0x33, 0x44,
            ),
            encodeBody(
                UpdateInvFull(
                    inventoryId = 0x005D,
                    flags = 0x2,
                    entries = listOf(
                        InventoryEntry(
                            itemId = 0x013B,
                            quantity = 1,
                            params = listOf(InventoryItemParam(key = 0x002A, value = 0x11223344)),
                        )
                    ),
                )
            ),
        )
    }

    @Test
    fun `inventory partial packet matches captured premium-currency update`() {
        val entry = codec.serverProts[UpdateInvPartial::class] ?: error("missing UpdateInvPartial")

        assertEquals(121, entry.opcode)
        assertEquals(ProtSize.VarShort, entry.size)
        assertContentEquals(
            byteArrayOf(
                0x03, 0x1B, 0x00,
                0x00, 0xCD.toByte(), 0x4C,
                0xFF.toByte(), 0x00, 0x00, 0x03, 0xE8.toByte(),
            ),
            encodeBody(
                UpdateInvPartial(
                    inventoryId = 0x031B,
                    entries = listOf(InventoryPartialEntry(slot = 0, itemId = 0xCD4B, quantity = 1000)),
                )
            ),
        )
    }

    @Test
    fun `inventory partial empty slot omits quantity and params`() {
        assertContentEquals(
            byteArrayOf(
                0x00, 0x5E, 0x00,
                0x83.toByte(), 0xE8.toByte(), 0x00, 0x00,
            ),
            encodeBody(
                UpdateInvPartial(
                    inventoryId = 0x005E,
                    entries = listOf(InventoryPartialEntry(slot = 1000, itemId = -1, quantity = 0)),
                )
            ),
        )
    }

    @Test
    fun `inventory partial param flag adds count and param block`() {
        assertContentEquals(
            byteArrayOf(
                0x00, 0x5E, 0x02,
                0x05, 0x04, 0xB6.toByte(), 0x01, 0x01,
                0x01, 0x23, 0x55, 0x66, 0x77, 0x88.toByte(),
            ),
            encodeBody(
                UpdateInvPartial(
                    inventoryId = 0x005E,
                    flags = 0x2,
                    entries = listOf(
                        InventoryPartialEntry(
                            slot = 5,
                            itemId = 0x04B5,
                            quantity = 1,
                            params = listOf(InventoryItemParam(key = 0x0123, value = 0x55667788)),
                        )
                    ),
                )
            ),
        )
    }
}
