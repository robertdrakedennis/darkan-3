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
        // The build area is the authoritative spatial gate (spec §4 / alerion §6): a zone update is
        // visible only if its zone falls inside the build-area map-square grid. The grid bounds are
        // in REGIONS (64-tile / 8-zone map-squares); convert to inclusive chunk (zone) bounds by
        // `region*8 .. region*8+7`. The origin chunk (SW corner) is `minRegion*8` — relative zone
        // offsets below are measured from it.
        val buildArea = viewport.buildArea
        val originChunkX = buildArea.minRegion.x shl 3
        val originChunkY = buildArea.minRegion.y shl 3
        val minChunkX = originChunkX
        val maxChunkX = (buildArea.maxRegion.x shl 3) + 7
        val minChunkY = originChunkY
        val maxChunkY = (buildArea.maxRegion.y shl 3) + 7

        val out = ArrayList<ServerProt>()
        for ((zoneId, packets) in pendingMap) {
            val zone = Zone(zoneId)
            if (zone.x !in minChunkX..maxChunkX) continue
            if (zone.y !in minChunkY..maxChunkY) continue
            if (packets.isEmpty()) continue

            val relX = zone.x - originChunkX
            val relY = zone.y - originChunkY
            val enclosed = packets.filterIsInstance<LocAnim>()
            val standalone = packets.filterNot(::isEnclosedOnly)

            if (enclosed.isNotEmpty()) {
                out.add(
                    UpdateZonePartialEnclosed(
                        level = zone.level,
                        zoneX = relX,
                        zoneY = relY,
                        subPackets = enclosed,
                    )
                )
            }
            if (standalone.isNotEmpty()) {
                out.add(
                    UpdateZonePartialFollows(
                        level = zone.level,
                        zoneX = relX,
                        zoneY = relY,
                    )
                )
                out.addAll(standalone)
            }
        }
        return out
    }

    private fun isEnclosedOnly(packet: ServerProt): Boolean = packet is LocAnim
}
