package org.darkan.world.net

import io.ktor.utils.io.ByteChannel
import org.darkan.core.model.Account
import org.darkan.core.net.Isaac
import org.darkan.core.net.prot.LocAnim
import org.darkan.core.net.prot.LocAdd
import org.darkan.core.net.prot.LocDel
import org.darkan.core.net.prot.ObjAdd
import org.darkan.core.net.prot.ServerProt
import org.darkan.core.net.prot.UpdateZoneFullFollowsV2
import org.darkan.core.net.prot.UpdateZonePartialEnclosed
import org.darkan.core.net.prot.UpdateZonePartialFollows
import org.darkan.core.net.prot.revision.rev948.register948
import org.darkan.core.net.session.GameSession
import org.darkan.world.entity.Player
import org.darkan.world.world.BuildAreaSize
import org.darkan.world.world.LocalSceneZone
import org.darkan.world.world.SceneBuildMode
import org.darkan.world.world.SceneBuildPlanner
import org.darkan.world.world.WorldZoneState
import org.darkan.world.world.ZoneStateProvider
import org.darkan.world.world.Zones
import world.gregs.voidps.type.Region
import world.gregs.voidps.type.Tile
import world.gregs.voidps.type.Zone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ZoneStreamerTest {
    private fun plan(spawn: Tile) =
        SceneBuildPlanner.planFor(
            tile = spawn,
            mode = SceneBuildMode.Rebuild,
            size = BuildAreaSize.DEFAULT,
            resolveWorldAreaType = false,
        )

    private fun newPlayer(spawn: Tile): Player {
        val account = Account(username = "tester", displayName = "Tester")
        val session = GameSession(
            write = ByteChannel(),
            isaacIn = Isaac(IntArray(4)),
            isaacOut = Isaac(IntArray(4)),
            ip = "127.0.0.1",
            codec = register948(),
            username = "tester",
        )
        return Player(index = 1, account = account, session = session).also {
            it.tile = spawn
            it.viewport.loadBuildArea(spawn)
        }
    }

    @Test
    fun `zone streamer emits every render-zone marker from the scene plan`() {
        val packets = ZoneStreamer.buildPackets(plan(Tile(3224, 3224, 0)), object : ZoneStateProvider {
            override fun packetsFor(zone: Zone, localSceneZone: LocalSceneZone): List<ServerProt> = emptyList()
        })

        assertEquals(ZoneStreamer.SCENE_PLANES * 13 * 13, packets.size)
        assertEquals(UpdateZoneFullFollowsV2(level = 0, zoneX = 10, zoneY = 10), packets.first())
        assertEquals(UpdateZoneFullFollowsV2(level = 3, zoneX = 22, zoneY = 22), packets.last())
        assertTrue(packets.all { it is UpdateZoneFullFollowsV2 })
    }

    @Test
    fun `zone streamer appends provider packets for matching render zones`() {
        val plan = plan(Tile(3224, 3224, 0))
        val provider = object : ZoneStateProvider {
            override fun packetsFor(zone: Zone, localSceneZone: LocalSceneZone): List<ServerProt> =
                if (zone.x == plan.centreZoneX && zone.y == plan.centreZoneY && zone.level == 0) {
                    listOf(ObjAdd(packedCoord = 1, objId = 2, count = 3))
                } else {
                    emptyList()
                }
        }

        val packets = ZoneStreamer.buildPackets(plan, provider)

        assertEquals(ZoneStreamer.SCENE_PLANES * 13 * 13 + 1, packets.size)
        assertEquals(listOf(ObjAdd(packedCoord = 1, objId = 2, count = 3)), packets.filterIsInstance<ObjAdd>())
    }

    @Test
    fun `scene base keeps production local range independent of build-area bounds`() {
        val centreZone = 403
        val baseZone = ZoneStreamer.sceneBaseZone(centreZone)

        assertEquals(387, baseZone)
        assertEquals(10, centreZone - ZoneStreamer.SCENE_RADIUS_ZONES - baseZone)
        assertEquals(16, centreZone - baseZone)
        assertEquals(22, centreZone + ZoneStreamer.SCENE_RADIUS_ZONES - baseZone)
    }

    @Test
    fun `Lumbridge scene map planner covers visible region squares`() {
        val plan = plan(Tile(3200, 3200, 0))

        assertEquals(
            setOf(
                Region(49, 49),
                Region(49, 50),
                Region(50, 49),
                Region(50, 50),
            ),
            SceneMapRegionPlanner.regionsForScene(plan),
        )
    }

    @Test
    fun `zone bundle uses scene-local coordinates for partial updates`() {
        val player = newPlayer(Tile(3224, 3224, 0))
        Zones.clear()
        Zones.queue(
            Zone(407, 406, 0),
            LocAnim(
                packedCoord = 0x77,
                animId = 0x812,
                shapeFlags = 0xf1,
                unknown1 = 0,
                delay = 0x9b,
                speed = 0x100,
                mode = 0,
            )
        )
        Zones.queue(Zone(407, 406, 0), ObjAdd(packedCoord = 1, objId = 2, count = 3))

        val packets = try {
            ZoneBundleBuilder.build(player)
        } finally {
            Zones.clear()
        }

        val enclosed = assertIs<UpdateZonePartialEnclosed>(packets[0])
        assertEquals(20, enclosed.zoneX)
        assertEquals(19, enclosed.zoneY)
        val follows = assertIs<UpdateZonePartialFollows>(packets[1])
        assertEquals(20, follows.zoneX)
        assertEquals(19, follows.zoneY)
        assertEquals(ObjAdd(packedCoord = 1, objId = 2, count = 3), assertIs<ObjAdd>(packets[2]))
    }

    @Test
    fun `first light scene bootstrap carries typed prod mutations`() {
        assertEquals(36, FirstLightSceneBootstrap.packetCount())

        val groundItems = FirstLightSceneBootstrap.packets(level = 0, zoneX = 14, zoneY = 14)
        assertEquals(2, groundItems.size)
        assertEquals(ObjAdd(packedCoord = 84, objId = 1, count = 946), assertIs<ObjAdd>(groundItems[0]))
        assertEquals(ObjAdd(packedCoord = 96, objId = 1, count = 558), assertIs<ObjAdd>(groundItems[1]))

        val locs = FirstLightSceneBootstrap.packets(level = 0, zoneX = 20, zoneY = 21)
        assertEquals(2, locs.size)
        assertEquals(LocAdd(packedCoord = 67, locId = 2306, shapeFlags = 130, extra = 0), assertIs<LocAdd>(locs[0]))
        assertEquals(LocAdd(packedCoord = 66, locId = 2320, shapeFlags = 130, extra = 0), assertIs<LocAdd>(locs[1]))

        val deletes = FirstLightSceneBootstrap.packets(level = 0, zoneX = 19, zoneY = 12)
        assertEquals(11, deletes.size)
        assertEquals(LocDel(shapeFlags = 42, packedCoord = 86), assertIs<LocDel>(deletes[0]))
    }

    @Test
    fun `first light fixture is appended only when the provider flag is enabled`() {
        val zone = Zone(0, 0, 0)
        val local = LocalSceneZone(level = 0, zoneX = 14, zoneY = 14)

        assertEquals(emptyList(), WorldZoneState.packetsFor(zone, local, includeFirstLightScaffolding = false))
        assertEquals(
            listOf(
                ObjAdd(packedCoord = 84, objId = 1, count = 946),
                ObjAdd(packedCoord = 96, objId = 1, count = 558),
            ),
            WorldZoneState.packetsFor(zone, local, includeFirstLightScaffolding = true),
        )
    }

    @Test
    fun `world zone state streams persistent ground items by absolute tile zone`() {
        val tile = Tile(3225, 3218, 0)
        try {
            WorldZoneState.addGroundItem(tile, objId = 995, count = 42)

            assertEquals(
                listOf(ObjAdd(packedCoord = tile.chunkLocalHash, objId = 995, count = 42)),
                WorldZoneState.packetsFor(tile.zone, LocalSceneZone(0, 16, 16), includeFirstLightScaffolding = false),
            )
            assertEquals(
                emptyList(),
                WorldZoneState.packetsFor(tile.transform(8, 0, 0).zone, LocalSceneZone(0, 17, 16), includeFirstLightScaffolding = false),
            )
        } finally {
            WorldZoneState.clear()
            Zones.clear()
        }
    }
}
