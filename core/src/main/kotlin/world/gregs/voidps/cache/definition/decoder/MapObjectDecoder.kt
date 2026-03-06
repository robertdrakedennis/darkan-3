package world.gregs.voidps.cache.definition.decoder

import world.gregs.voidps.cache.definition.data.MapDefinition

/**
 * Decodes all objects in a map.
 *
 * Objects are loaded at their ORIGINAL plane as stored in the map data.
 * The bridge tile flag (0x2 on plane 1) only affects collision plane calculation,
 * NOT the object's stored plane. Rendering transformations like "linkBelow" that
 * shift objects for display purposes happen separately after loading.
 *
 * Reference: darkan-bot SceneGraph.decodeLocMapClient - objects are always added
 * at their original plane, with only the collision map reference adjusted for bridges.
 */
abstract class MapObjectDecoder {

    /**
     * Decodes object information and calls [add] for each object.
     * Objects are stored at their original plane from the map data.
     */
    fun decode(buffer: ByteArray, settings: ByteArray, regionTileX: Int, regionTileY: Int) {
        var position = 0
        var objectId = -1
        while (true) {
            /*
            val skip = readLargeSmart()
            if (skip == 0) {
                break
            }
            */
            // Decomposed for early exit
            var peek = buffer[position++].toInt() and 0xff
            val skip = when {
                peek == 0 -> break
                peek >= 128 -> {
                    var lastValue = (peek shl 8 or (buffer[position++].toInt() and 0xff)) - 32768
                    var baseValue = 0
                    if (lastValue == 32767) {
                        peek = buffer[position++].toInt() and 0xff
                        lastValue = if (peek < 128) {
                            peek
                        } else {
                            (peek shl 8 or (buffer[position++].toInt() and 0xff)) - 32768
                        }
                        baseValue += 32767
                    }
                    baseValue + lastValue
                }
                else -> peek
            }
            objectId += skip
            var tile = 0
            while (true) {
                /*
                val loc = reader.readSmart()
                if (loc == 0) {
                    break
                }
                tile += loc - 1
                */
                // Decomposed for early exit
                val loc = buffer[position++].toInt() and 0xff
                tile += when {
                    loc == 0 -> break
                    loc >= 128 -> (loc shl 8 or (buffer[position++].toInt() and 0xff)) - 32769
                    else -> loc - 1
                }

                // Extract the original plane from the tile data - do NOT adjust for bridges here.
                // Bridge adjustments only affect collision plane, not the object's stored plane.
                val level = tile shr 12

                // Data
                val data = buffer[position++].toInt()
                val shape = data shr 2
                val rotation = data and 0x3
                val localX = MapDefinition.localX(tile)
                val localY = MapDefinition.localY(tile)

                // Add object at its original plane
                add(objectId, localX, localY, level, shape, rotation, regionTileX, regionTileY)
            }
        }
    }

    abstract fun add(objectId: Int, localX: Int, localY: Int, level: Int, shape: Int, rotation: Int, regionTileX: Int, regionTileY: Int)
}
