package org.darkan.core.net.prot

import io.ktor.utils.io.ByteChannel
import io.ktor.utils.io.availableForRead
import io.ktor.utils.io.readFully
import kotlinx.coroutines.runBlocking
import org.darkan.core.net.prot.revision.rev948.register948
import org.darkan.core.worldlist.Country
import org.darkan.core.worldlist.World
import org.darkan.core.worldlist.WorldList
import world.gregs.voidps.buffer.read.BufferReader
import kotlin.test.Test
import kotlin.test.assertEquals

class Rev948WorldListCodecTest {
    private val codec = register948()

    private fun encode(packet: WorldListPacket): ByteArray = runBlocking {
        val entry = codec.serverProts[WorldListPacket::class] ?: error("missing worldlist codec")
        val encoder = entry.encoder ?: error("missing worldlist encoder")
        val channel = ByteChannel()
        encoder.invoke(packet, channel)
        channel.flush()
        val out = ByteArray(channel.availableForRead)
        channel.readFully(out)
        channel.close()
        out
    }

    private fun readJagString(reader: BufferReader): String {
        assertEquals(0, reader.readUnsignedByte())
        return reader.readString()
    }

    private fun worldList(): WorldList {
        val worldList = WorldList(300)
        val world = World(
            number = 300,
            hostname = "localhost",
            activity = "Darkan",
            country = Country.USA,
            members = true,
        )
        world.playersOnline = 42
        worldList.put(world)
        return worldList
    }

    @Test
    fun `full worldlist writes 948 activity gate and host fields`() {
        val bytes = encode(WorldListPacket(worldList(), fullRefresh = true))
        val reader = BufferReader(bytes)

        assertEquals(1, reader.readUnsignedByte())
        assertEquals(2, reader.readUnsignedByte())
        assertEquals(1, reader.readUnsignedByte())
        assertEquals(1, reader.readSmart())
        assertEquals(Country.USA.id, reader.readSmart())
        assertEquals("Usa", readJagString(reader))
        assertEquals(300, reader.readSmart())
        assertEquals(300, reader.readSmart())
        assertEquals(1, reader.readSmart())
        assertEquals(0, reader.readSmart())
        assertEquals(0, reader.readUnsignedByte())
        assertEquals(1, reader.readInt())
        assertEquals(1, reader.readSmart())
        assertEquals("Darkan", readJagString(reader))
        assertEquals("localhost", readJagString(reader))
        assertEquals("localhost", readJagString(reader))
        assertEquals(11, reader.readInt())
        assertEquals(0, reader.readSmart())
        assertEquals(42, reader.readUnsignedShort())
        assertEquals(0, reader.remaining)
    }

    @Test
    fun `count-only worldlist keeps 948 revision before player counts`() {
        val bytes = encode(WorldListPacket(worldList(), fullRefresh = false))
        val reader = BufferReader(bytes)

        assertEquals(1, reader.readUnsignedByte())
        assertEquals(2, reader.readUnsignedByte())
        assertEquals(0, reader.readUnsignedByte())
        assertEquals(11, reader.readInt())
        assertEquals(0, reader.readSmart())
        assertEquals(42, reader.readUnsignedShort())
        assertEquals(0, reader.remaining)
    }
}
