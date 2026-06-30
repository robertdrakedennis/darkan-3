package org.darkan.world.world

import org.darkan.core.EnvVars
import org.darkan.core.Logger.logWarn
import world.gregs.voidps.cache.Cache
import world.gregs.voidps.type.Tile
import world.gregs.voidps.type.Zone
import kotlin.math.abs

enum class SceneBuildMode {
    WorldEntry,
    Rebuild,
}

data class LocalSceneZone(
    val level: Int,
    val zoneX: Int,
    val zoneY: Int,
)

data class RenderSceneZone(
    val zone: Zone,
    val localSceneZone: LocalSceneZone,
)

data class SceneBuildPlan(
    val tile: Tile,
    val mode: SceneBuildMode,
    val buildArea: BuildArea,
    val centreZoneX: Int,
    val centreZoneY: Int,
    val sceneBaseZoneX: Int,
    val sceneBaseZoneY: Int,
    val worldAreaTypeId: Int,
    val renderZones: List<RenderSceneZone>,
) {
    private val renderZoneIds: Set<Int> = renderZones.mapTo(LinkedHashSet(renderZones.size)) { it.zone.id }

    fun containsRenderZone(zone: Zone): Boolean = zone.id in renderZoneIds

    fun localSceneZone(zone: Zone): LocalSceneZone? =
        if (containsRenderZone(zone)) {
            LocalSceneZone(
                level = zone.level,
                zoneX = zone.x - sceneBaseZoneX,
                zoneY = zone.y - sceneBaseZoneY,
            )
        } else {
            null
        }

    fun requiresRebuildFor(tile: Tile): Boolean {
        val zoneX = tile.x shr 3
        val zoneY = tile.y shr 3
        return abs(zoneX - centreZoneX) > SceneBuildPlanner.REBUILD_THRESHOLD_ZONES ||
            abs(zoneY - centreZoneY) > SceneBuildPlanner.REBUILD_THRESHOLD_ZONES ||
            tile.level != this.tile.level
    }
}

object SceneBuildPlanner {
    const val RENDER_RADIUS_ZONES: Int = 6
    const val SCENE_CENTER_LOCAL_ZONE: Int = 16
    const val SCENE_PLANES: Int = 4
    const val REBUILD_THRESHOLD_ZONES: Int = RENDER_RADIUS_ZONES - 2

    fun planFor(tile: Tile, mode: SceneBuildMode): SceneBuildPlan = planFor(tile, mode, BuildAreaSize.DEFAULT)

    fun planFor(
        tile: Tile,
        mode: SceneBuildMode,
        size: BuildAreaSize,
        resolveWorldAreaType: Boolean = true,
    ): SceneBuildPlan {
        val buildArea = when (mode) {
            SceneBuildMode.WorldEntry -> BuildArea.firstLight(tile)
            SceneBuildMode.Rebuild -> BuildArea.of(tile, size)
        }
        val centreZoneX = tile.x shr 3
        val centreZoneY = tile.y shr 3
        val sceneBaseZoneX = sceneBaseZone(centreZoneX)
        val sceneBaseZoneY = sceneBaseZone(centreZoneY)
        val renderZones = buildList(RENDER_ZONE_COUNT) {
            for (level in 0 until SCENE_PLANES) {
                for (zoneX in (centreZoneX - RENDER_RADIUS_ZONES)..(centreZoneX + RENDER_RADIUS_ZONES)) {
                    for (zoneY in (centreZoneY - RENDER_RADIUS_ZONES)..(centreZoneY + RENDER_RADIUS_ZONES)) {
                        add(
                            RenderSceneZone(
                                zone = Zone(zoneX, zoneY, level),
                                localSceneZone = LocalSceneZone(
                                    level = level,
                                    zoneX = zoneX - sceneBaseZoneX,
                                    zoneY = zoneY - sceneBaseZoneY,
                                ),
                            )
                        )
                    }
                }
            }
        }
        return SceneBuildPlan(
            tile = tile,
            mode = mode,
            buildArea = buildArea,
            centreZoneX = centreZoneX,
            centreZoneY = centreZoneY,
            sceneBaseZoneX = sceneBaseZoneX,
            sceneBaseZoneY = sceneBaseZoneY,
            worldAreaTypeId = if (resolveWorldAreaType) worldAreaTypeId(tile) else EnvVars.worldSceneRootId,
            renderZones = renderZones,
        )
    }

    fun sceneBaseZone(centreZone: Int): Int = centreZone - SCENE_CENTER_LOCAL_ZONE

    private fun worldAreaTypeId(tile: Tile): Int =
        runCatching { Cache.worldAreaTypeAt(tile.x, tile.y) }.getOrNull() ?: run {
            logWarn(
                "No WorldAreaType covers scene tile (${tile.x},${tile.y}); " +
                    "using WORLD_SCENE_ROOT_ID=${EnvVars.worldSceneRootId}"
            )
            EnvVars.worldSceneRootId
        }

    private const val RENDER_ZONE_COUNT = SCENE_PLANES * (RENDER_RADIUS_ZONES * 2 + 1) * (RENDER_RADIUS_ZONES * 2 + 1)
}
