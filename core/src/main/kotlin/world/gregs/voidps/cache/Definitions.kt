package world.gregs.voidps.cache

import world.gregs.voidps.buffer.read.BufferReader

/**
 * Lazy, memoized per-id view over a [DefinitionDecoder].
 *
 * [DefinitionDecoder.load] eagerly decodes every definition (tens of thousands of
 * items/npcs/objects) which is far too expensive for the injected engine, where
 * only a handful of ids are ever touched. This holder allocates the backing array
 * once (default, undecoded definitions) but decodes each id from the cache blob on
 * first access and memoizes the result.
 *
 * Cross-references between definitions (e.g. the note/lend/bind templates resolved
 * by [DefinitionDecoder.changeValues] via `definitions.getOrNull(templateId)`) are
 * handled by [dependencies]: every template id it reports for a freshly decoded
 * definition is decoded *before* [DefinitionDecoder.changeValues] runs, mirroring
 * the eager path where lower-id templates are already populated.
 */
class Definitions<T : Definition>(
    private val decoder: DefinitionDecoder<T>,
    private val cache: Cache,
    private val dependencies: ((T) -> IntArray)? = null,
) {
    private val size = decoder.size(cache) + 1
    private val definitions: Array<T> = decoder.create(if (size > 0) size else 0)
    private val decoded = BooleanArray(definitions.size)

    fun getOrNull(id: Int): T? {
        if (id < 0 || id >= definitions.size) {
            return null
        }
        if (!decoded[id]) {
            synchronized(this) {
                if (!decoded[id]) {
                    decode(id)
                }
            }
        }
        return definitions[id]
    }

    private fun decode(id: Int) {
        // Mark first so a self/cyclic dependency resolves to the in-progress slot
        // instead of recursing forever.
        decoded[id] = true
        val dependencies = dependencies
        if (dependencies == null) {
            decoder.load(definitions, cache, id)
            return
        }
        val data = cache.data(decoder.index, decoder.getArchive(id), decoder.getFile(id)) ?: return
        val definition = definitions[id]
        decoder.readLoop(definition, BufferReader(data))
        for (dependency in dependencies(definition)) {
            if (dependency in 0 until definitions.size && !decoded[dependency]) {
                decode(dependency)
            }
        }
        decoder.changeValues(definitions, definition)
    }
}
