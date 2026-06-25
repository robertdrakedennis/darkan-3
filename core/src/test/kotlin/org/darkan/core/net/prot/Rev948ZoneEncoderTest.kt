package org.darkan.core.net.prot

import io.ktor.utils.io.ByteChannel
import io.ktor.utils.io.availableForRead
import io.ktor.utils.io.close
import io.ktor.utils.io.readFully
import kotlinx.coroutines.runBlocking
import org.darkan.core.net.prot.revision.rev948.register948
import kotlin.test.Test
import kotlin.test.assertEquals

class Rev948ZoneEncoderTest {

    private val codec = register948()

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
    fun `UPDATE_ZONE_PARTIAL_ENCLOSED encodes rev948 loc anim subop`() {
        val actual = encodeBody(
            UpdateZonePartialEnclosed(
                level = 0,
                zoneX = 20,
                zoneY = 19,
                subPackets = listOf(
                    LocAnim(
                        packedCoord = 0x77,
                        animId = 0x00000812,
                        shapeFlags = 0xf1,
                        unknown1 = 0x00,
                        delay = 0x9b,
                        speed = 0x0100,
                        mode = 0x00,
                    )
                ),
            )
        )

        val expected = byteArrayOf(
            0x00, 0x13, 0x6c,
            0x0d,
            0x77, 0x00, 0x00, 0x08, 0x12.toByte(), 0xf1.toByte(),
            0x00, 0x9b.toByte(), 0x01, 0x00, 0x00,
        )

        assertEquals(expected.toHex(), actual.toHex())
    }

    private fun ByteArray.toHex(): String = joinToString(" ") { "%02x".format(it) }
}
