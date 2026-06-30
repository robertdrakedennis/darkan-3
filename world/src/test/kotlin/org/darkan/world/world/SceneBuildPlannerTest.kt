package org.darkan.world.world

import org.darkan.world.net.ZoneStreamer
import world.gregs.voidps.type.Region
import world.gregs.voidps.type.Tile
import world.gregs.voidps.type.Zone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SceneBuildPlannerTest {

    @Test
    fun `Lumbridge rebuild plan keeps op81 centre build area and render locals coherent`() {
        val spawn = Tile(3224, 3216, 0)
        val plan = plan(spawn, SceneBuildMode.Rebuild)

        assertEquals(spawn.zone.x, plan.centreZoneX)
        assertEquals(spawn.zone.y, plan.centreZoneY)
        val expectedBuildArea = BuildArea.of(spawn)
        assertEquals(expectedBuildArea.minRegion, plan.buildArea.minRegion)
        assertEquals(expectedBuildArea.maxRegion, plan.buildArea.maxRegion)
        assertEquals(expectedBuildArea.size, plan.buildArea.size)
        assertEquals(SceneBuildPlanner.sceneBaseZone(spawn.zone.x), plan.sceneBaseZoneX)
        assertEquals(SceneBuildPlanner.sceneBaseZone(spawn.zone.y), plan.sceneBaseZoneY)
        assertEquals(ZoneStreamer.SCENE_PLANES * 13 * 13, plan.renderZones.size)

        val centre = plan.localSceneZone(Zone(plan.centreZoneX, plan.centreZoneY, 0))
        require(centre != null) { "centre zone must be part of the render scene" }
        assertEquals(16, centre.zoneX)
        assertEquals(16, centre.zoneY)
        assertEquals(0, centre.level)
        assertTrue(plan.containsRenderZone(Zone(plan.centreZoneX - 6, plan.centreZoneY - 6, 3)))
        assertFalse(plan.containsRenderZone(Zone(plan.centreZoneX - 7, plan.centreZoneY, 0)))
    }

    @Test
    fun `Burthorpe rebuild plan uses the same code path as Lumbridge`() {
        val spawn = Tile(2889, 3543, 0)
        val plan = plan(spawn, SceneBuildMode.Rebuild)

        assertEquals(spawn.zone.x, plan.centreZoneX)
        assertEquals(spawn.zone.y, plan.centreZoneY)
        assertEquals(Region(43, 53), plan.buildArea.minRegion)
        assertEquals(Region(47, 57), plan.buildArea.maxRegion)
        assertTrue(plan.buildArea.containsTile(spawn))

        val southwest = plan.renderZones.first()
        assertEquals(Zone(spawn.zone.x - 6, spawn.zone.y - 6, 0), southwest.zone)
        assertEquals(10, southwest.localSceneZone.zoneX)
        assertEquals(10, southwest.localSceneZone.zoneY)
    }

    @Test
    fun `world entry plan keeps the production first-light build area as an explicit mode`() {
        val spawn = Tile(3224, 3216, 0)
        val plan = plan(spawn, SceneBuildMode.WorldEntry)

        assertEquals(BuildArea.FIRST_LIGHT_MIN_REGION, plan.buildArea.minRegion)
        assertEquals(BuildArea.FIRST_LIGHT_MAX_REGION, plan.buildArea.maxRegion)
        assertTrue(plan.buildArea.containsTile(spawn))
        assertEquals(spawn.zone.x, plan.centreZoneX)
        assertEquals(spawn.zone.y, plan.centreZoneY)
    }

    @Test
    fun `rebuild threshold trips before the player walks out of the active render scene`() {
        val spawn = Tile(3224, 3216, 0)
        val plan = plan(spawn, SceneBuildMode.Rebuild)

        assertFalse(plan.requiresRebuildFor(spawn.transform(ZoneStreamer.SCENE_RADIUS_ZONES * 4, 0, 0)))
        assertTrue(plan.requiresRebuildFor(spawn.transform((SceneBuildPlanner.REBUILD_THRESHOLD_ZONES + 1) * 8, 0, 0)))
    }

    private fun plan(tile: Tile, mode: SceneBuildMode): SceneBuildPlan =
        SceneBuildPlanner.planFor(
            tile = tile,
            mode = mode,
            size = BuildAreaSize.DEFAULT,
            resolveWorldAreaType = false,
        )
}
