package world.gregs.voidps.cache.definition.decoder

import world.gregs.voidps.buffer.read.Reader
import world.gregs.voidps.cache.Config.VAR_BIT
import world.gregs.voidps.cache.config.ConfigDecoder
import world.gregs.voidps.cache.definition.data.VarBitDefinition
import world.gregs.voidps.cache.definition.data.VarDomain

/**
 * Decodes varbit types from the CONFIG index (2), archive 69, one file per id.
 *
 * (The legacy core decoder read from index 22 — the structs index — via the
 * deprecated `Index.VAR_BIT` alias, which produced garbage. The NXT client reads
 * varbits from CONFIG/69; this matches `jag::game::VarBitType::DecodeType`.)
 */
class VarBitDecoder : ConfigDecoder<VarBitDefinition>(VAR_BIT) {

    override fun create(size: Int) = Array(size) { VarBitDefinition(it) }

    override fun VarBitDefinition.read(opcode: Int, buffer: Reader) {
        when (opcode) {
            1 -> {
                domainId = buffer.readUnsignedByte().toByte()
                domain = VarDomain.forId(domainId.toInt())
                index = buffer.readUnsignedShort()
            }
            2 -> {
                startBit = buffer.readUnsignedByte()
                endBit = buffer.readUnsignedByte()
            }
            16 -> flags = buffer.readUnsignedByte()
        }
    }
}
