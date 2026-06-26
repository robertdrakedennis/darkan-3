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
                index = buffer.readBigSmart() // jag gSmart2or4 (2-or-4 byte var id)
            }
            2 -> {
                startBit = buffer.readUnsignedByte()
                endBit = buffer.readUnsignedByte()
            }
            // 0x10: presence flag, NO operand. VarBitType::DecodeType (948-5) just sets this+0x50 = 1.
            // Newer schema than the old refs (opcodes 1/2 only); reading an operand here ate the
            // terminator byte and threw BufferUnderflowException on the last record in the blob.
            16 -> flags = 1
        }
    }
}
