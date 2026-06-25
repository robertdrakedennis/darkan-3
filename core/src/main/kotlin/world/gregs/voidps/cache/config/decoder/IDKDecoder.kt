package world.gregs.voidps.cache.config.decoder

import world.gregs.voidps.buffer.read.Reader
import world.gregs.voidps.cache.Config.IDENTITY_KIT
import world.gregs.voidps.cache.config.ConfigDecoder
import world.gregs.voidps.cache.config.data.IDKDefinition

/**
 * Decodes identity-kit (IDK) types from the CONFIG index (2), archive 3.
 *
 * Opcode layout matches `jag::game::IdkType::DecodeType` for 948-5.
 */
class IDKDecoder : ConfigDecoder<IDKDefinition>(IDENTITY_KIT) {

    override fun create(size: Int) = Array(size) { IDKDefinition(it) }

    override fun IDKDefinition.read(opcode: Int, buffer: Reader) {
        when (opcode) {
            1 -> bodyPart = buffer.readUnsignedByte()
            2 -> {
                val count = buffer.readUnsignedByte()
                modelIds = IntArray(count) { buffer.readBigSmart() }
            }
            3 -> nonSelectable = true
            40 -> {
                val count = buffer.readUnsignedByte()
                recolorSrc = IntArray(count)
                recolorDst = IntArray(count)
                for (i in 0 until count) {
                    recolorSrc!![i] = buffer.readUnsignedShort()
                    recolorDst!![i] = buffer.readUnsignedShort()
                }
            }
            41 -> {
                val count = buffer.readUnsignedByte()
                retextureSrc = IntArray(count)
                retextureDst = IntArray(count)
                for (i in 0 until count) {
                    retextureSrc!![i] = buffer.readUnsignedShort()
                    retextureDst!![i] = buffer.readUnsignedShort()
                }
            }
            44, 45 -> buffer.readShort() // recolor/retexture bitmask
            in 60..64 -> headModelIds[opcode - 60] = buffer.readBigSmart()
        }
    }
}
