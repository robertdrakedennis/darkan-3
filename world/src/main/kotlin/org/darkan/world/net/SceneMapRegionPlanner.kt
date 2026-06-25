package org.darkan.world.net

import world.gregs.voidps.type.Region

object SceneMapRegionPlanner {

    fun regionsForScene(centreZoneX: Int, centreZoneY: Int): Set<Region> {
        val regions = LinkedHashSet<Region>()
        for (zoneX in (centreZoneX - ZoneStreamer.SCENE_RADIUS_ZONES)..(centreZoneX + ZoneStreamer.SCENE_RADIUS_ZONES)) {
            for (zoneY in (centreZoneY - ZoneStreamer.SCENE_RADIUS_ZONES)..(centreZoneY + ZoneStreamer.SCENE_RADIUS_ZONES)) {
                regions.add(Region(zoneX shr 3, zoneY shr 3))
            }
        }
        return regions
    }
}
