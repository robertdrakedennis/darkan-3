package world.gregs.voidps.cache.config.decoder

import world.gregs.voidps.buffer.read.Reader
import world.gregs.voidps.cache.Config.IDENTITY_KIT
import world.gregs.voidps.cache.config.ConfigDecoder
import world.gregs.voidps.cache.config.data.IdentityKitDefinition

class IdentityKitDecoder : ConfigDecoder<IdentityKitDefinition>(IDENTITY_KIT) {

    override fun create(size: Int) = Array(size) { IdentityKitDefinition(it) }

    override fun IdentityKitDefinition.read(opcode: Int, buffer: Reader) {
        when (opcode) {
            1 -> bodyPartId = buffer.readUnsignedByte()
            2 -> {
                val length = buffer.readUnsignedByte()
                modelIds = IntArray(length) { buffer.readBigSmart() }
            }
            3 -> nonSelectable = true
            in 4..39, in 42..43, in 46..59 -> { } // no-op ranges
            40 -> readColours(buffer)
            41 -> readTextures(buffer)
            44, 45 -> buffer.readShort() // recolor/retexture bitmask
            in 60..64 -> headModels[opcode - 60] = buffer.readBigSmart()
        }
    }
}