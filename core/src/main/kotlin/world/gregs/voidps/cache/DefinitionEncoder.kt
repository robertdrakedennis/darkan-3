package world.gregs.voidps.cache

import world.gregs.voidps.buffer.write.Writer

interface DefinitionEncoder<T : Definition> {
    /** Encode with separate members data; defaults to ignoring the members variant. */
    fun Writer.encode(definition: T, members: T) {
        encode(definition)
    }

    fun Writer.encode(definition: T)
}