package world.gregs.voidps.cache.config.decoder

import world.gregs.voidps.buffer.read.Reader
import world.gregs.voidps.cache.config.ConfigDecoder
import world.gregs.voidps.cache.config.data.HeadbarDefinition

/**
 * Decodes headbar types from the CONFIG index (2), archive 33 (verified for 948-5).
 */
class HeadbarDecoder : ConfigDecoder<HeadbarDefinition>(HEADBARS_ARCHIVE) {

    override fun create(size: Int) = Array(size) { HeadbarDefinition(it) }

    override fun HeadbarDefinition.read(opcode: Int, buffer: Reader) {
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
        }
    }

    private companion object {
        const val HEADBARS_ARCHIVE = 33
    }
}
