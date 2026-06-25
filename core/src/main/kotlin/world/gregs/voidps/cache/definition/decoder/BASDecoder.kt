package world.gregs.voidps.cache.definition.decoder

import world.gregs.voidps.buffer.read.Reader
import world.gregs.voidps.cache.Config.RENDER_ANIMATIONS
import world.gregs.voidps.cache.config.ConfigDecoder
import world.gregs.voidps.cache.definition.data.BASDefinition

/**
 * Decodes Base Animation Set (BAS / render animations) definitions from the
 * CONFIG index (2), archive 32. Opcode mapping matches the NXT client for 948-5.
 */
class BASDecoder : ConfigDecoder<BASDefinition>(RENDER_ANIMATIONS) {

    override fun create(size: Int) = Array(size) { BASDefinition(it) }

    override fun BASDefinition.read(opcode: Int, buffer: Reader) {
        when (opcode) {
            1 -> {
                standAnim = buffer.readBigSmart()
                standTurnAnim = buffer.readBigSmart()
            }
            2 -> walkAnim = buffer.readBigSmart()
            3 -> runAnim = buffer.readBigSmart()
            4 -> turnAroundAnim = buffer.readBigSmart()
            5 -> turnRightAnim = buffer.readBigSmart()
            6 -> walkBackAnim = buffer.readBigSmart()
            7 -> walkLeftAnim = buffer.readBigSmart()
            8 -> walkRightAnim = buffer.readBigSmart()
            9 -> crawlAnim = buffer.readBigSmart()
            26 -> {
                renderOffsetX = buffer.readUnsignedByte() shl 2
                renderOffsetY = buffer.readUnsignedByte() shl 2
            }
            27 -> {
                val count = buffer.readUnsignedByte()
                buffer.skip(count * 2)
            }
            28 -> {
                val count = buffer.readUnsignedByte()
                buffer.skip(count)
            }
            29, 31, 34, 37 -> buffer.skip(1)
            30, 32, 33, 35, 36 -> buffer.skip(2)
            38 -> anim38 = buffer.readBigSmart()
            39 -> anim39 = buffer.readBigSmart()
            40 -> anim40 = buffer.readBigSmart()
            41 -> anim41 = buffer.readBigSmart()
            42 -> anim42 = buffer.readBigSmart()
            43 -> anim43 = buffer.readBigSmart()
            44 -> anim44 = buffer.readBigSmart()
            45 -> field45 = buffer.readUnsignedShort()
            46 -> anim46 = buffer.readBigSmart()
            47 -> anim47 = buffer.readBigSmart()
            48 -> anim48 = buffer.readBigSmart()
            49 -> anim49 = buffer.readBigSmart()
            50 -> anim50 = buffer.readBigSmart()
            51 -> anim51 = buffer.readBigSmart()
            52 -> {
                val count = buffer.readUnsignedByte()
                repeat(count) {
                    buffer.readBigSmart() // anim id
                    buffer.readUnsignedByte() // flags
                    val subCount = buffer.readUnsignedByte()
                    repeat(subCount) { buffer.readByte() }
                }
            }
            53 -> { } // flag, no bytes
            54 -> {
                renderOffsetX2 = buffer.readUnsignedByte() shl 2
                renderOffsetY2 = buffer.readUnsignedByte() shl 2
            }
            55 -> buffer.skip(3)
            56 -> buffer.skip(7)
        }
    }
}
