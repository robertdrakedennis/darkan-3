package org.darkan.world.server

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class ServerClientVarBlockTest {
    @Test
    fun `first light baseline matches production block shape`() {
        val block = ServerClientVarBaseline.firstLight()
        val encoded = block.encode()

        assertEquals(220, block.entries.size)
        assertEquals(1321, encoded.size)
        assertContentEquals(byteArrayOf(0x01, 0x10, 0x0d, 0x20, 0x01, 0x04, 0x03), encoded.take(7).toByteArray())
    }

    @Test
    fun `typed int entry encodes id and value`() {
        val block = ServerClientVarBlock(
            entries = listOf(ServerClientVarEntry(0x1234, ServerClientVarValue.IntValue(0x01020304)))
        )

        assertContentEquals(
            byteArrayOf(0x01, 0x12, 0x34, 0x01, 0x02, 0x03, 0x04),
            block.encode(),
        )
    }

    @Test
    fun `definition provider rejects mismatched value type`() {
        val definitions = object : ServerClientVarDefinitions {
            override fun typeOf(id: Int): ServerClientVarType? = ServerClientVarType.String
        }
        val block = ServerClientVarBlock(
            entries = listOf(ServerClientVarEntry(1, ServerClientVarValue.IntValue(0)))
        )

        val error = runCatching { block.encode(definitions) }.exceptionOrNull()

        assertEquals(IllegalArgumentException::class, error!!::class)
    }
}
