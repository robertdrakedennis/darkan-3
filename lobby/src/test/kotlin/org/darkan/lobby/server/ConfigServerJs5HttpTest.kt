package org.darkan.lobby.server

import world.gregs.voidps.cache.file.FileProvider
import java.util.zip.CRC32
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ConfigServerJs5HttpTest {

    @Test
    fun `http js5 response appends the cache group version suffix`() {
        val container = byteArrayOf(
            2,
            0, 0, 0, 3,
            0x11, 0x22, 0x33,
        )
        val provider = object : FileProvider {
            override fun data(index: Int, archive: Int): ByteArray? {
                return if (index == 8 && archive == 3) container else null
            }

            override fun version(index: Int, archive: Int): Int? {
                return if (index == 8 && archive == 3) 0x1234 else null
            }
        }

        val data = provider.data(8, 3)
        assertNotNull(data)
        val response = ConfigServer.buildJs5HttpResponse(provider, 8, 3, data)

        assertEquals(0x1234, response.version)
        assertContentEquals(
            container + byteArrayOf(0x12, 0x34),
            response.body,
        )
        assertEquals(crc(container), crc(response.body.copyOf(response.body.size - 2)))
    }

    private fun crc(bytes: ByteArray): Long {
        val crc = CRC32()
        crc.update(bytes)
        return crc.value
    }
}
