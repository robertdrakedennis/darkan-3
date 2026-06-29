package org.darkan.core.net.prot

import io.ktor.utils.io.ByteChannel
import io.ktor.utils.io.availableForRead
import io.ktor.utils.io.close
import io.ktor.utils.io.readFully
import kotlinx.coroutines.runBlocking
import org.darkan.core.net.prot.revision.rev948.register948
import kotlin.test.Test
import kotlin.test.assertContentEquals
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
    fun `UPDATE_ZONE_PARTIAL_FOLLOWS encodes rev948 scene-local header`() {
        val actual = encodeBody(UpdateZonePartialFollows(level = 0, zoneX = 20, zoneY = 19))

        val expected = byteArrayOf(
            0x94.toByte(),
            0x80.toByte(),
            0x13,
        )

        assertEquals(expected.toHex(), actual.toHex())
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

    @Test
    fun `UPDATE_ZONE_PARTIAL_ENCLOSED keeps fixed subpacket boundaries for repeated loc anim`() {
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
                    ),
                    LocAnim(
                        packedCoord = 0x04,
                        animId = 0x00000823,
                        shapeFlags = 0xa1,
                        unknown1 = 0x01,
                        delay = 0x20,
                        speed = 0x0123,
                        mode = 0x02,
                    ),
                ),
            )
        )

        assertEquals(27, actual.size, "op76 body = 3-byte header + 2 * (subop byte + 11-byte LocAnim)")
        assertContentEquals(
            byteArrayOf(
                0x00, 0x13, 0x6c,
                0x0d,
                0x77, 0x00, 0x00, 0x08, 0x12.toByte(), 0xf1.toByte(),
                0x00, 0x9b.toByte(), 0x01, 0x00, 0x00,
                0x0d,
                0x04, 0x00, 0x00, 0x08, 0x23, 0xa1.toByte(),
                0x01, 0x20, 0x01, 0x23, 0x02,
            ),
            actual,
        )
        assertEquals(0x0d, actual[3].toInt() and 0xFF)
        assertEquals(0x0d, actual[15].toInt() and 0xFF)
    }

    private fun ByteArray.toHex(): String = joinToString(" ") { "%02x".format(it) }
}
