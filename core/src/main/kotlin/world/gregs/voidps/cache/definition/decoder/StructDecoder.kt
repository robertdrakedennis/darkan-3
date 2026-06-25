package world.gregs.voidps.cache.definition.decoder

import world.gregs.voidps.buffer.read.Reader
import world.gregs.voidps.cache.Cache
import world.gregs.voidps.cache.DefinitionDecoder
import world.gregs.voidps.cache.Index
import world.gregs.voidps.cache.definition.data.StructDefinition

/**
 * Decodes struct definitions from the dedicated STRUCTS index (22).
 *
 * Addressing: 5-bit archive (archive = id ushr 5, file = id and 0x1f), matching
 * the NXT client (`jag::game::StructType`) for 948-5. (The legacy core decoder
 * read CONFIG/26 — the pre-dedicated-index location — which is wrong for 948-5.)
 *
 * Structs are simple key-value containers (opcode 249 params only).
 */
class StructDecoder : DefinitionDecoder<StructDefinition>(Index.STRUCTS) {

    override fun create(size: Int) = Array(size) { StructDefinition(it) }

    override fun getArchive(id: Int) = id ushr 5

    override fun getFile(id: Int) = id and 0x1f

    override fun size(cache: Cache): Int {
        val lastArchive = cache.lastArchiveId(index)
        return lastArchive * 32 + cache.fileCount(index, lastArchive)
    }

    override fun StructDefinition.read(opcode: Int, buffer: Reader) {
        if (opcode == 249) {
            readParameters(buffer)
        }
    }
}
