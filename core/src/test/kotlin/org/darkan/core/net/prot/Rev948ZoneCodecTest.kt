package org.darkan.core.net.prot

import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import org.darkan.core.net.prot.revision.rev948.register948
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class Rev948ZoneCodecTest {
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

    private fun hexBytes(hex: String): ByteArray =
        hex.filterNot(Char::isWhitespace).chunked(2).map { it.toInt(16).toByte() }.toByteArray()

    @Test
    fun `zone full follows encodes prod coordinate order`() {
        val entry = codec.serverProts[UpdateZoneFullFollowsV2::class] ?: error("missing UpdateZoneFullFollowsV2")

        assertEquals(78, entry.opcode)
        assertEquals(ProtSize.Fixed(3), entry.size)
        assertContentEquals(
            hexBytes("80 76 0A"),
            encodeBody(UpdateZoneFullFollowsV2(level = 0, zoneX = 10, zoneY = 10)),
        )
    }

    @Test
    fun `obj add encodes 948 prod field order`() {
        val entry = codec.serverProts[ObjAdd::class] ?: error("missing ObjAdd")

        assertEquals(46, entry.opcode)
        assertEquals(ProtSize.Fixed(5), entry.size)
        assertContentEquals(
            hexBytes("54 00 01 03 32"),
            encodeBody(ObjAdd(packedCoord = 0x54, objId = 1, count = 946)),
        )
    }

    @Test
    fun `loc add encodes 948 prod field transforms`() {
        val entry = codec.serverProts[LocAdd::class] ?: error("missing LocAdd")

        assertEquals(90, entry.opcode)
        assertEquals(ProtSize.VarByte, entry.size)
        assertContentEquals(
            hexBytes("60 A8 5E 01 00 A8"),
            encodeBody(LocAdd(packedCoord = 0x60, locId = 89768, shapeFlags = 40)),
        )
        assertContentEquals(
            hexBytes("43 02 09 00 00 02 00"),
            encodeBody(LocAdd(packedCoord = 0x43, locId = 2306, shapeFlags = 130, extra = 0)),
        )
    }

    @Test
    fun `loc del encodes 948 prod field transforms`() {
        val entry = codec.serverProts[LocDel::class] ?: error("missing LocDel")

        assertEquals(16, entry.opcode)
        assertEquals(ProtSize.Fixed(2), entry.size)
        assertContentEquals(
            hexBytes("56 AA"),
            encodeBody(LocDel(shapeFlags = 42, packedCoord = 86)),
        )
    }
}
