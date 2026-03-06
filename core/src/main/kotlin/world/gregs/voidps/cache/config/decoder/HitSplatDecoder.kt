package world.gregs.voidps.cache.config.decoder

import world.gregs.voidps.buffer.read.Reader
import world.gregs.voidps.cache.Config.HIT_SPLATS
import world.gregs.voidps.cache.config.ConfigDecoder
import world.gregs.voidps.cache.config.data.HitSplatDefinition

class HitSplatDecoder : ConfigDecoder<HitSplatDefinition>(HIT_SPLATS) {

    override fun create(size: Int) = Array(size) { HitSplatDefinition(it) }

    override fun HitSplatDefinition.read(opcode: Int, buffer: Reader) {
        when (opcode) {
            1 -> font = buffer.readBigSmart()
            2 -> textColour = buffer.readUnsignedMedium()
            3 -> buffer.readBigSmart() // spriteId2
            4 -> buffer.readBigSmart() // spriteId3
            5 -> buffer.readBigSmart() // spriteId4
            6 -> buffer.readBigSmart() // spriteId5
            7 -> offsetX = buffer.readShort()
            8 -> amount = buffer.readString()
            9 -> duration = buffer.readUnsignedShort()
            10 -> offsetY = buffer.readShort()
            11 -> fade = 0
            12 -> comparisonType = buffer.readUnsignedByte()
            13 -> anInt3214 = buffer.readShort()
            14 -> fade = buffer.readUnsignedShort()
            15 -> { } // no-op flag
            16 -> {
                buffer.readShort() // field16a
                buffer.readShort() // field16b
            }
            17, 18 -> {
                // Stacked hitmark sprites
                buffer.readUnsignedShort() // id1
                buffer.readUnsignedShort() // id2
                if (opcode == 18) {
                    buffer.readUnsignedShort() // id3
                }
                val count = buffer.readUnsignedByte()
                repeat(count) { buffer.readShort() }
            }
            19 -> buffer.readShort() // field19
            20 -> buffer.readShort() // field20
            21, 22 -> buffer.readInt() // rotated int
            23 -> {
                buffer.skip(3) // 3 bytes
            }
            24 -> {
                buffer.readShort() // field24a
                buffer.readShort() // field24b
            }
            25 -> buffer.readBigSmart() // field25
            26, 27 -> {
                // Stacked hitmark sprites (same pattern as 17/18)
                buffer.readUnsignedShort()
                buffer.readUnsignedShort()
                if (opcode == 27) {
                    buffer.readUnsignedShort()
                }
                val count = buffer.readUnsignedByte()
                repeat(count) { buffer.readShort() }
            }
            28, 29, 30 -> buffer.skip(1) // single byte fields
            249 -> { } // parameters
        }
    }
}