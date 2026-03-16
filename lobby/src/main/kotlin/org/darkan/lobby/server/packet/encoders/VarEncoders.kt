package org.darkan.lobby.server.packet.encoders

import org.darkan.core.net.packet.ServerPacketEncoder
import org.darkan.core.net.prot.ServerProt
import world.gregs.voidps.buffer.write.BufferWriter

/** SET_VARP_SMALL (14, 3B): g2(lo-128) id + g1(-128) value */
class SetVarpSmallEncoder(private val id: Int, private val value: Int) :
    ServerPacketEncoder(ServerProt.SET_VARP_SMALL) {
    override fun encodeBody(buf: BufferWriter) {
        buf.writeShortAdd(id)
        buf.writeByteAdd(value)
    }
}

/** SET_VARP_INT (124, 6B): g2LE id + g4_alt1 value */
class SetVarpIntEncoder(private val id: Int, private val value: Int) :
    ServerPacketEncoder(ServerProt.SET_VARP_INT) {
    override fun encodeBody(buf: BufferWriter) {
        buf.writeShortLittle(id)
        buf.writeIntMiddle(value)
    }
}

/** SET_VARP_LONG (138, 10B): g8 value + g2(lo-128) id */
class SetVarpLongEncoder(private val id: Int, private val value: Long) :
    ServerPacketEncoder(ServerProt.SET_VARP_LONG) {
    override fun encodeBody(buf: BufferWriter) {
        buf.writeLong(value)
        buf.writeShortAdd(id)
    }
}

/** SET_VARC_INT_2 (12, 6B): g2BE key + g4_alt2(LE) value */
class SetVarcIntEncoder(private val key: Int, private val value: Int) :
    ServerPacketEncoder(ServerProt.SET_VARC_INT_2) {
    override fun encodeBody(buf: BufferWriter) {
        buf.writeShort(key)
        buf.writeIntLittle(value)
    }
}

/** SET_VARC_SMALL_2 (19, 3B): g1(0x80-raw) value + g2BE key */
class SetVarcSmallEncoder(private val key: Int, private val value: Int) :
    ServerPacketEncoder(ServerProt.SET_VARC_SMALL_2) {
    override fun encodeBody(buf: BufferWriter) {
        buf.writeByteSubtract(value)
        buf.writeShort(key)
    }
}

/** UPDATE_STAT (114, 6B): g4_alt1 xp + g1(-128) boosted + g1(negate) statId */
class UpdateStatEncoder(private val statId: Int, private val xp: Int, private val boostedLevel: Int) :
    ServerPacketEncoder(ServerProt.UPDATE_STAT) {
    override fun encodeBody(buf: BufferWriter) {
        buf.writeIntMiddle(xp)
        buf.writeByteAdd(boostedLevel)
        buf.writeByteInverse(statId)
    }
}
