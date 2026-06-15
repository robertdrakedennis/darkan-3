package org.darkan.world.net

import org.darkan.core.net.prot.ServerProt
import org.darkan.core.net.prot.UpdateZonePartialEnclosed
import org.darkan.core.net.prot.UpdateZonePartialFollows
import org.darkan.world.entity.Player
import org.darkan.world.world.Zones
import world.gregs.voidps.type.Zone

/**
 * Per-tick builder that turns the global [Zones.pending] queue into per-player ordered
 * lists of [ServerProt] packets that the world-tick loop will dispatch.
 *
 * Per `docs/net/serverprot/zone-updates-947-3.md` §1 "MAJOR FINDING #2" the
 * UPDATE_ZONE_PARTIAL_ENCLOSED sub-protocol vector only has sub-op 1 (LOC_ANIM) bound in
 * 947-3 — every other sub-op silently skips its payload. The server therefore MUST emit:
 *
 *  * For zones that contain ONLY LOC_ANIM updates: bundle them inside one
 *    [UpdateZonePartialEnclosed] with the zone header and a list of sub-packets.
 *  * For zones that contain any other update (LOC_ADD, OBJ_ADD, etc.): emit a leading
 *    [UpdateZonePartialFollows] to set the zone context, followed by each sub-packet as
 *    its OWN standalone main-table ServerProt.
 *
 * The build-area filtering ensures each player only receives updates for zones inside its
 * 13×13-chunk viewport (per [org.darkan.world.world.Viewport.buildAreaSize]).
 */
object ZoneBundleBuilder {

    /**
     * Build the per-tick zone packet list for [player]. Walks [Zones.pending], filters
     * down to zones inside the player's viewport build area, and serialises in the
     * canonical wire form per A3 §1.
     *
     * MVP: returns an empty list because no zone events are queued during world login.
     */
    fun build(player: Player): List<ServerProt> {
        val pendingMap = Zones.pending
        if (pendingMap.isEmpty()) return emptyList()

        val viewport = player.viewport
        val halfArea = viewport.buildAreaSize / 2
        // Build area is defined in CHUNK coords; clamp the per-player visible window.
        val minChunkX = viewport.buildAreaChunkX - halfArea
        val maxChunkX = viewport.buildAreaChunkX + halfArea
        val minChunkY = viewport.buildAreaChunkY - halfArea
        val maxChunkY = viewport.buildAreaChunkY + halfArea

        val out = ArrayList<ServerProt>()
        for ((zoneId, packets) in pendingMap) {
            val zone = Zone(zoneId)
            if (zone.x !in minChunkX..maxChunkX) continue
            if (zone.y !in minChunkY..maxChunkY) continue
            if (packets.isEmpty()) continue

            // Decide bundling strategy: if ALL packets are LOC_ANIM (sub-op 1), bundle them
            // into one UPDATE_ZONE_PARTIAL_ENCLOSED. Else emit a leading
            // UPDATE_ZONE_PARTIAL_FOLLOWS + standalone packets.
            val allLocAnim = packets.all { isLocAnim(it) }

            // Zone-relative offsets within the build area (per A3 §2.2 PARTIAL_FOLLOWS
            // header decoding: the wire bytes are deltas from the client's build area
            // origin in chunk units).
            val relX = zone.x - (viewport.buildAreaChunkX - halfArea)
            val relY = zone.y - (viewport.buildAreaChunkY - halfArea)

            if (allLocAnim) {
                out.add(
                    UpdateZonePartialEnclosed(
                        level = zone.level,
                        zoneX = relX,
                        zoneY = relY,
                        subPackets = packets.toList(),
                    )
                )
            } else {
                out.add(
                    UpdateZonePartialFollows(
                        level = zone.level,
                        zoneX = relX,
                        zoneY = relY,
                    )
                )
                out.addAll(packets)
            }
        }
        return out
    }

    /**
     * Always false: no ServerProt data class models the enclosed LOC_ANIM sub-packet yet
     * (no standalone main-table opcode exists per A3 §3.5), and the 948 enclosed sub-opcode
     * table has not been RE'd — so nothing is currently bundle-eligible and every zone
     * update takes the FOLLOWS-then-standalone path.
     *
     * TODO: once the 948 UPDATE_ZONE_PARTIAL_ENCLOSED sub-opcode table is documented in
     * docs/net/serverprot/ and a LOC_ANIM ServerProt exists, detect it here so LOC_ANIM-only
     * zones are bundled into one enclosed packet.
     */
    private fun isLocAnim(@Suppress("UNUSED_PARAMETER") packet: ServerProt): Boolean = false
}
