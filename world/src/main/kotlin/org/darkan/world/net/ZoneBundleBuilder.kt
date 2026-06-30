package org.darkan.world.net

import org.darkan.core.net.prot.LocAnim
import org.darkan.core.net.prot.ServerProt
import org.darkan.core.net.prot.UpdateZonePartialEnclosed
import org.darkan.core.net.prot.UpdateZonePartialFollows
import org.darkan.world.entity.Player
import org.darkan.world.world.Zones
import world.gregs.voidps.type.Zone

/**
 * Per-tick builder that turns the global [Zones.pending] queue into per-player zone packets.
 * Rev948 op76 has a real enclosed sub-protocol for fixed loc animations; other zone updates still
 * use a partial-follows header plus standalone main-table packets until their enclosed formats are
 * modelled.
 */
object ZoneBundleBuilder {

    /**
     * Build the per-tick zone packet list for [player].
     */
    fun build(player: Player): List<ServerProt> {
        val pendingMap = Zones.pending
        if (pendingMap.isEmpty()) return emptyList()

        val viewport = player.viewport
        val plan = viewport.sceneBuildPlan
        val buildArea = plan.buildArea
        val minBuildChunkX = buildArea.minRegion.x shl 3
        val maxBuildChunkX = (buildArea.maxRegion.x shl 3) + 7
        val minBuildChunkY = buildArea.minRegion.y shl 3
        val maxBuildChunkY = (buildArea.maxRegion.y shl 3) + 7

        val out = ArrayList<ServerProt>()
        for ((zoneId, packets) in pendingMap) {
            val zone = Zone(zoneId)
            if (zone.x !in minBuildChunkX..maxBuildChunkX) continue
            if (zone.y !in minBuildChunkY..maxBuildChunkY) continue
            val localSceneZone = plan.localSceneZone(zone) ?: continue
            if (packets.isEmpty()) continue

            val enclosed = packets.filterIsInstance<LocAnim>()
            val standalone = packets.filterNot(::isEnclosedOnly)

            if (enclosed.isNotEmpty()) {
                out.add(
                    UpdateZonePartialEnclosed(
                        level = zone.level,
                        zoneX = localSceneZone.zoneX,
                        zoneY = localSceneZone.zoneY,
                        subPackets = enclosed,
                    )
                )
            }
            if (standalone.isNotEmpty()) {
                out.add(
                    UpdateZonePartialFollows(
                        level = zone.level,
                        zoneX = localSceneZone.zoneX,
                        zoneY = localSceneZone.zoneY,
                    )
                )
                out.addAll(standalone)
            }
        }
        return out
    }

    private fun isEnclosedOnly(packet: ServerProt): Boolean = packet is LocAnim
}
