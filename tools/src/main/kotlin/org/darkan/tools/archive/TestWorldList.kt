// org.darkan.tools.archive: unmaintained one-shot experiments kept for reference only.
// These tools were written against specific captures/revisions, are not part of any
// build task, and may rely on stale capture data or stale opcode identities.
package org.darkan.tools.archive

import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import org.darkan.core.net.prot.WorldListPacket
import org.darkan.core.net.prot.revision.rev948.register948
import org.darkan.core.worldlist.Country
import org.darkan.core.worldlist.World
import org.darkan.core.worldlist.WorldList
import org.darkan.tools.util.toHex
import world.gregs.voidps.buffer.read.BufferReader

fun main() = runBlocking {
    val codec = register948()

    val wl = WorldList(300)
    wl.put(World(number = 300, hostname = "localhost", activity = "Darkan", country = Country.USA, members = true))

    val packet = WorldListPacket(wl, fullRefresh = true)

    // Get the encoder
    val encoder = codec.serverProts[WorldListPacket::class]?.encoder
        ?: error("No encoder for WorldListPacket")

    // Write to a buffer
    val channel = ByteChannel(true)
    encoder.invoke(packet, channel)

    val bytes = ByteArray(channel.availableForRead)
    channel.readFully(bytes, 0, bytes.size)

    println("WorldList packet: ${bytes.size} bytes")
    println("Hex: ${bytes.toHex()}")

    // Decode it manually
    val reader = BufferReader(bytes)
    fun gjstr2(): String {
        val ver = reader.readUnsignedByte()
        if (ver != 0) return "<bad version $ver>"
        return reader.readString()
    }

    println("\n=== Decoding ===")
    val frame = reader.readUnsignedByte(); println("frame=$frame")
    val refresh = reader.readUnsignedByte(); println("refresh=$refresh")
    if (refresh == 2) {
        val sep = reader.readUnsignedByte(); println("separator=$sep")
        if (sep == 1) {
            val countryCount = reader.readSmart(); println("countryCount=$countryCount")
            repeat(countryCount) { i ->
                val cid = reader.readSmart(); val cname = gjstr2()
                println("  country[$i]: id=$cid name='$cname'")
            }
            val minWorld = reader.readSmart(); println("minWorldId=$minWorld")
            val maxWorld = reader.readSmart(); println("maxWorldId=$maxWorld")
            val worldCount = reader.readSmart(); println("worldCount=$worldCount")
            repeat(worldCount) { i ->
                val offset = reader.readSmart()
                val idx = reader.readUnsignedByte()
                val flags = reader.readInt()
                val actPres = reader.readSmart()
                val act = if (actPres != 0) gjstr2() else ""
                val host = gjstr2()
                val addr = gjstr2()
                println("  world[$i]: num=${minWorld+offset} idx=$idx flags=0x${"%X".format(flags)} actPres=$actPres act='$act' host='$host' addr='$addr'")
            }
            val rev = reader.readInt(); println("revision=$rev")
        }
    }
    // Player counts
    println("=== Player counts (remaining ${reader.remaining} bytes) ===")
    while (reader.remaining > 0) {
        val wnum = reader.readSmart()
        val count = reader.readUnsignedShort()
        println("  world $wnum: players=$count")
    }
}
