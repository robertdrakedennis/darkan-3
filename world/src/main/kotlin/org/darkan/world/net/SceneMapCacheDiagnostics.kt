package org.darkan.world.net

import world.gregs.voidps.cache.Cache
import world.gregs.voidps.cache.Index
import world.gregs.voidps.type.Region

object SceneMapCacheDiagnostics {

    fun describe(cache: Cache, regions: Set<Region>): String =
        regions.joinToString(prefix = "[", postfix = "]") { region ->
            "${region.x},${region.y}{${archive(cache, region)}}"
        }

    private fun archive(cache: Cache, region: Region): String {
        val archive = region.id
        val size = cache.sectorSize(Index.MAPS, archive)
        if (size < 0) return "$archive:missing-data"
        return "$archive:${size}B"
    }
}
