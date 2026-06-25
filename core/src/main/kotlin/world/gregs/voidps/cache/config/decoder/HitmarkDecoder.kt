package world.gregs.voidps.cache.config.decoder

import world.gregs.voidps.buffer.read.Reader
import world.gregs.voidps.cache.config.ConfigDecoder
import world.gregs.voidps.cache.config.data.HitmarkDefinition

/**
 * Decodes hitmark types from the CONFIG index (2), archive 29 (verified for 948-5).
 */
class HitmarkDecoder : ConfigDecoder<HitmarkDefinition>(HITMARKS_ARCHIVE) {

    override fun create(size: Int) = Array(size) { HitmarkDefinition(it) }

    override fun HitmarkDefinition.read(opcode: Int, buffer: Reader) {
        when (opcode) {
            1 -> spriteId = buffer.readBigSmart()
            2 -> {
                hasColor = true
                color = buffer.readUnsignedMedium()
            }
            3 -> spriteId2 = buffer.readBigSmart()
            4 -> spriteId3 = buffer.readBigSmart()
            5 -> spriteId4 = buffer.readBigSmart()
            6 -> spriteId5 = buffer.readBigSmart()
            7 -> field7 = buffer.readShort()
            8 -> buffer.readString() // string, not stored
            9 -> height = buffer.readUnsignedShort()
            10 -> field10 = buffer.readShort()
            11 -> field11 = 0
            12 -> field12 = buffer.readUnsignedByte()
            13 -> field13 = buffer.readShort()
            14 -> field14 = buffer.readUnsignedShort()
            15 -> { } // no-op
            16 -> {
                field16a = buffer.readShort()
                field16b = buffer.readShort()
            }
            17, 18 -> {
                buffer.readUnsignedShort()
                buffer.readUnsignedShort()
                if (opcode == 18) buffer.readUnsignedShort()
                val count = buffer.readUnsignedByte()
                repeat(count) { buffer.readShort() }
            }
            19 -> field19 = buffer.readShort()
            20 -> field20 = buffer.readShort()
            21 -> {
                val v = buffer.readInt()
                field21 = (v shl 8) or (v ushr 24)
            }
            22 -> {
                val v = buffer.readInt()
                field22 = (v shl 8) or (v ushr 24)
            }
            23 -> {
                field23a = buffer.readUnsignedByte()
                field23b = buffer.readUnsignedByte()
                field23c = buffer.readUnsignedByte()
            }
            24 -> {
                field24a = buffer.readShort() shl 9
                field24b = buffer.readShort() shl 9
            }
            25 -> field25 = buffer.readBigSmart()
            26, 27 -> {
                buffer.readUnsignedShort()
                buffer.readUnsignedShort()
                if (opcode == 27) buffer.readUnsignedShort()
                val count = buffer.readUnsignedByte()
                repeat(count) { buffer.readShort() }
            }
            28 -> field28 = buffer.readUnsignedByte()
            29 -> field29 = buffer.readUnsignedByte()
            30 -> field30 = buffer.readUnsignedByte()
            249 -> readParameters(buffer)
        }
    }

    private companion object {
        const val HITMARKS_ARCHIVE = 29
    }
}
