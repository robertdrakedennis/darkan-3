package world.gregs.voidps.cache.config.decoder

import world.gregs.voidps.buffer.read.Reader
import world.gregs.voidps.cache.Config
import world.gregs.voidps.cache.config.ConfigDecoder
import world.gregs.voidps.cache.config.data.WorldAreaCoord
import world.gregs.voidps.cache.config.data.WorldAreaDefinition

/**
 * Decodes WorldAreaType configs from CONFIG archive 83.
 */
class WorldAreaDecoder : ConfigDecoder<WorldAreaDefinition>(Config.WORLD_AREAS) {

    override fun create(size: Int) = Array(size) { WorldAreaDefinition(it) }

    override fun WorldAreaDefinition.read(opcode: Int, buffer: Reader) {
        when (opcode) {
            2 -> value = buffer.readUnsignedMedium()
            3 -> {
                val corner1 = WorldAreaCoord.decode(buffer.readInt())
                val corner2 = WorldAreaCoord.decode(buffer.readInt())
                val list = rects ?: ArrayList<Pair<WorldAreaCoord, WorldAreaCoord>>().also { rects = it }
                list.add(corner1 to corner2)
            }
            4 -> {
                val coord = WorldAreaCoord.decode(buffer.readInt())
                val value = buffer.readInt()
                val list = points ?: ArrayList<Pair<WorldAreaCoord, Int>>().also { points = it }
                list.add(coord to value)
            }
        }
    }

    override fun changeValues(definitions: Array<WorldAreaDefinition>, definition: WorldAreaDefinition) {
        definition.computeMapSquareBounds()
    }
}
