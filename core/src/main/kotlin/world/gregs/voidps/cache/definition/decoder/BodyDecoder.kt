package world.gregs.voidps.cache.definition.decoder

import world.gregs.voidps.buffer.read.Reader
import world.gregs.voidps.cache.DefinitionDecoder
import world.gregs.voidps.cache.Index.DEFAULTS
import world.gregs.voidps.cache.definition.data.BodyDefinition

/**
 * Decodes the body / wear-pos ("WearposDefaults") definition from DEFAULTS (index 28), archive 6, file 0.
 *
 * Opcode coverage is complete for this cache: all 3074 bodies decode cleanly with only opcodes 1,3,4,5,6
 * present. Because [readLoop] dispatches unknown opcodes as no-ops (the `when` below has no `else`), a
 * missing opcode that consumed payload bytes would desync the stream and corrupt every subsequent def —
 * which does not happen, so {1,3,4,5,6} is the full set the body archive uses.
 *
 * The appearance "K" the client uses for kit-colours/kit-styles is NOT decoded here — it is a hard-coded
 * 10 in the client (see [BodyDefinition]). None of these opcodes carries it.
 */
class BodyDecoder : DefinitionDecoder<BodyDefinition>(DEFAULTS) {
    var definition: BodyDefinition? = null

    override fun create(size: Int) = Array(size) { BodyDefinition(it) }

    override fun getFile(id: Int) = 0

    override fun getArchive(id: Int) = 6

    override fun BodyDefinition.read(opcode: Int, buffer: Reader) {
        when (opcode) {
            // WearposDefaults+0x10 disabled-slot mask; its length is the +0x08 slot count (=19 human).
            1 -> disabledSlots = IntArray(buffer.readUnsignedByte()) { buffer.readUnsignedByte() }
            3 -> anInt4506 = buffer.readUnsignedByte()
            4 -> anInt4504 = buffer.readUnsignedByte()
            5 -> anIntArray4501 = IntArray(buffer.readUnsignedByte()) { buffer.readUnsignedByte() }
            6 -> anIntArray4507 = IntArray(buffer.readUnsignedByte()) { buffer.readUnsignedByte() }
        }
    }
}