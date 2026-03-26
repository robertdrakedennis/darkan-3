package org.darkan.tools

import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import org.darkan.core.net.prot.WorldListPacket
import org.darkan.core.net.prot.revision.rev947.register947
import org.darkan.core.worldlist.Country
import org.darkan.core.worldlist.World
import org.darkan.core.worldlist.WorldList

fun main() = runBlocking {
    val codec = register947()
    
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
    println("Hex: ${bytes.joinToString(" ") { "%02x".format(it.toInt() and 0xFF) }}")
    
    // Decode it manually
    var pos = 0
    fun g1() = bytes[pos++].toInt() and 0xFF
    fun g2() : Int { val v = ((bytes[pos].toInt() and 0xFF) shl 8) or (bytes[pos+1].toInt() and 0xFF); pos += 2; return v }
    fun g4() : Int { val v = ((bytes[pos].toInt() and 0xFF) shl 24) or ((bytes[pos+1].toInt() and 0xFF) shl 16) or ((bytes[pos+2].toInt() and 0xFF) shl 8) or (bytes[pos+3].toInt() and 0xFF); pos += 4; return v }
    fun smart(): Int {
        return if ((bytes[pos].toInt() and 0xFF) < 0x80) g1()
        else g2() - 0x8000
    }
    fun gjstr2(): String {
        val ver = g1()
        if (ver != 0) return "<bad version $ver>"
        val sb = StringBuilder()
        while (bytes[pos].toInt() != 0) { sb.append(bytes[pos].toInt().toChar()); pos++ }
        pos++ // null
        return sb.toString()
    }
    
    println("\n=== Decoding ===")
    val frame = g1(); println("frame=$frame")
    val refresh = g1(); println("refresh=$refresh")
    if (refresh == 2) {
        val sep = g1(); println("separator=$sep")
        if (sep == 1) {
            val countryCount = smart(); println("countryCount=$countryCount")
            repeat(countryCount) { i ->
                val cid = smart(); val cname = gjstr2()
                println("  country[$i]: id=$cid name='$cname'")
            }
            val minWorld = smart(); println("minWorldId=$minWorld")
            val maxWorld = smart(); println("maxWorldId=$maxWorld")
            val worldCount = smart(); println("worldCount=$worldCount")
            repeat(worldCount) { i ->
                val offset = smart()
                val idx = g1()
                val flags = g4()
                val actPres = smart()
                val act = if (actPres != 0) gjstr2() else ""
                val host = gjstr2()
                val addr = gjstr2()
                println("  world[$i]: num=${minWorld+offset} idx=$idx flags=0x${"%X".format(flags)} actPres=$actPres act='$act' host='$host' addr='$addr'")
            }
            val rev = g4(); println("revision=$rev")
        }
    }
    // Player counts
    println("=== Player counts (remaining ${bytes.size - pos} bytes) ===")
    while (pos < bytes.size) {
        val wnum = smart()
        val count = g2()
        println("  world $wnum: players=$count")
    }
}
