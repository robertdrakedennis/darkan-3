package world.gregs.voidps.cache.config.decoder

import world.gregs.voidps.buffer.read.Reader
import world.gregs.voidps.cache.Config.PARAMS
import world.gregs.voidps.cache.config.ConfigDecoder
import world.gregs.voidps.cache.config.data.ParamDefinition

/**
 * Decodes param types from the CONFIG index (2), archive 11.
 *
 * Opcode layout (matches `jag::game::ParamType::DecodeType` for 948-5):
 *  - 1   cp1252 script-var-type char
 *  - 2   default int value
 *  - 4   auto-disable flag (false)
 *  - 5   default string value
 *  - 101 numeric type id (unsigned smart)
 */
class ParamDecoder : ConfigDecoder<ParamDefinition>(PARAMS) {

    override fun create(size: Int) = Array(size) { ParamDefinition(it) }

    override fun ParamDefinition.read(opcode: Int, buffer: Reader) {
        when (opcode) {
            1 -> type = byteToChar(buffer.readByte().toByte())
            2 -> defaultInt = buffer.readInt()
            4 -> autoDisable = false
            5 -> defaultString = buffer.readString()
            101 -> typeId = buffer.readUnsignedSmart()
        }
    }
}
