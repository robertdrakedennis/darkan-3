package world.gregs.voidps.cache.definition.decoder

import world.gregs.voidps.buffer.read.BufferReader
import world.gregs.voidps.buffer.read.Reader
import world.gregs.voidps.cache.Cache
import world.gregs.voidps.cache.Index
import world.gregs.voidps.cache.definition.data.RegionDefinition
import world.gregs.voidps.cache.definition.data.RegionNpcSpawn
import world.gregs.voidps.cache.definition.data.RegionObject
import world.gregs.voidps.cache.definition.data.RegionWaterPatch

/**
 * Decodes a 948-5 MAPSV2 region (index [Index.MAPS]) into a [RegionDefinition] of
 * plain values. A region is a single archive `regionX or (regionY shl 7)` holding up
 * to five files: [TILES], [OBJECTS], [UNDERWATER], [NPCS] and [WATER_TILES].
 *
 * Each [RegionObject.plane] is the raw decoded source plane (`location shr 12`); no
 * bridge-plane adjustment is performed here and no objects are dropped. Applying the
 * bridge flag (`tileFlags[1][x][y] & 0x2`, which lowers an object/clip by one plane)
 * is a CONSUMER concern - [RegionDefinition.tileFlags] is exposed so each consumer can
 * resolve it under its own per-zone semantics (e.g. a dynamic-region builder filters
 * objects by their raw source plane before adjusting).
 */
class MapDecoder {

    fun decode(cache: Cache, regionId: Int): RegionDefinition? {
        val regionX = regionId shr 8
        val regionY = regionId and 0xff
        val archive = regionX or (regionY shl 7)
        if (!cache.exists(Index.MAPS, archive)) {
            return null
        }
        val definition = RegionDefinition(regionId)
        try {
            cache.data(Index.MAPS, archive, TILES)?.let { decodeTiles(BufferReader(it), definition) }
            cache.data(Index.MAPS, archive, OBJECTS)?.let { decodeObjects(BufferReader(it), definition) }
            cache.data(Index.MAPS, archive, UNDERWATER)?.let { decodeObjects(BufferReader(it), definition) }
            cache.data(Index.MAPS, archive, NPCS)?.let { decodeNpcs(BufferReader(it), definition) }
            cache.data(Index.MAPS, archive, WATER_TILES)?.let { decodeWater(BufferReader(it), definition) }
        } catch (_: Throwable) {
            // Tolerate a truncated/malformed file and return whatever decoded cleanly.
        }
        return definition
    }

    private fun decodeTiles(buffer: Reader, definition: RegionDefinition) {
        buffer.skip(5)
        for (plane in 0 until 4) {
            for (x in 0 until 64) {
                for (y in 0 until 64) {
                    val flags = buffer.readUnsignedByte()
                    if (flags and 0x1 != 0) {
                        val shapeHash = buffer.readUnsignedByte()
                        definition.overlayIds[plane][x][y] = buffer.readUnsignedSmart()
                        definition.overlayPathShapes[plane][x][y] = shapeHash shr 2
                        definition.overlayRotations[plane][x][y] = shapeHash and 0x3
                    }
                    if (flags and 0x2 != 0) {
                        definition.tileFlags[plane][x][y] = buffer.readByte()
                    }
                    if (flags and 0x4 != 0) {
                        definition.underlayIds[plane][x][y] = buffer.readUnsignedSmart()
                    }
                    if (flags and 0x8 != 0) {
                        definition.underlayIds[plane][x][y] = buffer.readUnsignedShort()
                    }
                }
            }
        }
    }

    private fun decodeObjects(buffer: Reader, definition: RegionDefinition) {
        var objectId = -1
        while (true) {
            val idIncrement = buffer.readSmartSizeVar()
            if (idIncrement == 0) {
                break
            }
            objectId += idIncrement
            var location = 0
            while (true) {
                val locationIncrement = buffer.readUnsignedSmart()
                if (locationIncrement == 0) {
                    break
                }
                location += locationIncrement - 1
                val localX = (location shr 6) and 0x3f
                val localY = location and 0x3f
                val plane = location shr 12
                val objectData = buffer.readUnsignedByte()
                if (objectData and 0x80 != 0) {
                    readExtendedObjectData(buffer)
                }
                val shape = (objectData shr 2) and 0x1f
                val rotation = objectData and 0x3
                definition.objects.add(RegionObject(objectId, localX, localY, plane, shape, rotation))
            }
        }
    }

    /**
     * Consumes the optional per-object transform block flagged by `0x80`. Values are
     * discarded - this only advances the cursor by the exact number of bytes the
     * client reads (a flag byte followed by 0..7 shorts).
     */
    private fun readExtendedObjectData(buffer: Reader) {
        val flags = buffer.readByte()
        if (flags and 0x1 != 0) buffer.skip(8)
        if (flags and 0x2 != 0) buffer.skip(2)
        if (flags and 0x4 != 0) buffer.skip(2)
        if (flags and 0x8 != 0) buffer.skip(2)
        if (flags and 0x10 != 0) {
            buffer.skip(2)
        } else {
            if (flags and 0x20 != 0) buffer.skip(2)
            if (flags and 0x40 != 0) buffer.skip(2)
            if (flags and 0x80 != 0) buffer.skip(2)
        }
    }

    private fun decodeNpcs(buffer: Reader, definition: RegionDefinition) {
        val count = buffer.readUnsignedSmart()
        for (i in 0 until count) {
            val first = buffer.readUnsignedByte()
            val typeId = if (first < 0x80) {
                first
            } else {
                ((first shl 8) or buffer.readUnsignedByte()) + 0x8000
            }
            // Per-spawn position data follows the type id but its layout varies by NPC
            // handler and is an unresolved RE follow-up; only the type id is recovered.
            definition.npcSpawns.add(RegionNpcSpawn(typeId))
        }
    }

    private fun decodeWater(buffer: Reader, definition: RegionDefinition) {
        val count = buffer.readUnsignedByte()
        for (i in 0 until count) {
            val posX = buffer.readByte()
            val posZ = buffer.readByte()
            val posY = buffer.readUnsignedShort()
            val extentX = buffer.readByte()
            val extentZ = buffer.readByte()
            val qx = buffer.readFloat()
            val qy = buffer.readFloat()
            val qz = buffer.readFloat()
            val qw = buffer.readFloat()
            val waterTypeId = buffer.readUnsignedShort()
            val scale1 = buffer.readByte()
            val scale2 = buffer.readByte()
            val waterMeshId = buffer.readUnsignedShort()
            definition.waterPatches.add(
                RegionWaterPatch(posX, posZ, posY, extentX, extentZ, qx, qy, qz, qw, waterTypeId, scale1, scale2, waterMeshId)
            )
        }
    }

    companion object {
        const val OBJECTS = 0
        const val UNDERWATER = 1
        const val NPCS = 2
        const val TILES = 3
        const val WATER_TILES = 4
    }
}
