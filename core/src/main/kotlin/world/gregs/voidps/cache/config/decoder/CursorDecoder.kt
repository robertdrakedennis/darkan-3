package world.gregs.voidps.cache.config.decoder

import world.gregs.voidps.buffer.read.Reader
import world.gregs.voidps.cache.config.ConfigDecoder
import world.gregs.voidps.cache.config.data.CursorDefinition

/**
 * Decodes cursor types from the CONFIG index (2), archive 34 (verified for 948-5).
 */
class CursorDecoder : ConfigDecoder<CursorDefinition>(CURSORS_ARCHIVE) {

    override fun create(size: Int) = Array(size) { CursorDefinition(it) }

    override fun CursorDefinition.read(opcode: Int, buffer: Reader) {
        when (opcode) {
            1 -> spriteId = buffer.readBigSmart()
            2 -> {
                hotspotX = buffer.readUnsignedByte()
                hotspotY = buffer.readUnsignedByte()
            }
        }
    }

    private companion object {
        const val CURSORS_ARCHIVE = 34
    }
}
