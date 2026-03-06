package world.gregs.voidps.cache.definition.decoder

import world.gregs.voidps.buffer.read.Reader
import world.gregs.voidps.cache.Cache
import world.gregs.voidps.cache.Config.STRUCTS
import world.gregs.voidps.cache.DefinitionDecoder
import world.gregs.voidps.cache.Index
import world.gregs.voidps.cache.definition.data.StructDefinition

/**
 * Decodes struct definitions from the CONFIGS index (2), archive 26.
 *
 * Structs are simple key-value containers (opcode 249 params only).
 * Each struct is a single file within archive 26.
 */
class StructDecoder : DefinitionDecoder<StructDefinition>(Index.CONFIGS) {

    override fun create(size: Int) = Array(size) { StructDefinition(it) }

    override fun getArchive(id: Int) = STRUCTS

    override fun size(cache: Cache): Int {
        return cache.lastFileId(Index.CONFIGS, STRUCTS)
    }

    override fun StructDefinition.read(opcode: Int, buffer: Reader) {
        if (opcode == 249) {
            readParameters(buffer)
        }
    }
}
