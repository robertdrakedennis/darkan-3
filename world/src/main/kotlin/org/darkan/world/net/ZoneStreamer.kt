package org.darkan.world.net

import org.darkan.core.net.prot.ServerProt
import org.darkan.core.net.prot.UpdateZoneFullFollowsV2
import org.darkan.core.net.session.GameSession
import org.darkan.world.world.SceneBuildPlan
import org.darkan.world.world.SceneBuildPlanner
import org.darkan.world.world.Viewport
import org.darkan.world.world.WorldZoneState
import org.darkan.world.world.ZoneStateProvider

/**
 * Streams the op78 `UpdateZoneFullFollowsV2` scene-ready markers and the server-owned dynamic
 * per-zone deltas for the current [SceneBuildPlan].
 *
 * Static terrain and loc geometry are not sent here: after op81 the NXT client loads map/loc groups
 * from JS5. This streamer only marks the render-scene zones ready and appends dynamic objects,
 * dynamic locations, loc animations, and optional first-light debug fixture data from the
 * [ZoneStateProvider].
 */
object ZoneStreamer {

    /** Half-extent of the render scene in zones: a `2*RADIUS+1` = 13-zone (104-tile) square. */
    const val SCENE_RADIUS_ZONES: Int = SceneBuildPlanner.RENDER_RADIUS_ZONES

    /** Local scene zone assigned to the centre zone by the client scene state. */
    const val SCENE_CENTER_LOCAL_ZONE: Int = SceneBuildPlanner.SCENE_CENTER_LOCAL_ZONE

    /** Scene planes streamed (0..3). */
    const val SCENE_PLANES: Int = SceneBuildPlanner.SCENE_PLANES

    suspend fun streamScene(session: GameSession, viewport: Viewport, provider: ZoneStateProvider = WorldZoneState) {
        streamScene(session, viewport.sceneBuildPlan, provider)
    }

    suspend fun streamScene(session: GameSession, plan: SceneBuildPlan, provider: ZoneStateProvider = WorldZoneState) {
        for (packet in buildPackets(plan, provider)) {
            session.send(packet)
        }
    }

    fun buildPackets(plan: SceneBuildPlan, provider: ZoneStateProvider = WorldZoneState): List<ServerProt> =
        buildList {
            for (sceneZone in plan.renderZones) {
                val local = sceneZone.localSceneZone
                add(
                    UpdateZoneFullFollowsV2(
                        level = local.level,
                        zoneX = local.zoneX,
                        zoneY = local.zoneY,
                    )
                )
                addAll(provider.packetsFor(sceneZone.zone, local))
            }
        }

    internal fun sceneBaseZone(centreZone: Int): Int = SceneBuildPlanner.sceneBaseZone(centreZone)
}
