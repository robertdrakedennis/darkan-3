package org.darkan.world.net

import org.darkan.core.net.prot.LocAdd
import org.darkan.core.net.prot.LocAnimSpecific
import org.darkan.core.net.prot.LocDel
import org.darkan.core.net.prot.LocMerge
import org.darkan.core.net.prot.LocPrefetch
import org.darkan.core.net.prot.MapAnim
import org.darkan.core.net.prot.ObjAdd
import org.darkan.core.net.prot.ObjCount
import org.darkan.core.net.prot.ObjDel
import org.darkan.core.net.prot.ObjReveal
import org.darkan.core.net.prot.ServerProt
import org.darkan.core.net.prot.SoundArea
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
     * LOC_ANIM is the ONLY sub-op with a bound handler inside UPDATE_ZONE_PARTIAL_ENCLOSED
     * per A3 §1 MAJOR FINDING #2. We don't currently have a ServerProt data class for the
     * raw LOC_ANIM (because no standalone main-table opcode exists per A3 §3.5) — so the
     * detection is structural: any packet NOT matching one of the standalone main opcodes
     * we already model is treated as bundle-eligible LOC_ANIM-ish content.
     *
     * For MVP no LOC_ANIM packets are queued; this function returns false for every known
     * standalone-mainstandalone op type so the FOLLOWS-then-standalone path is taken.
     */
    private fun isLocAnim(packet: ServerProt): Boolean {
        // None of the existing ServerProt zone data classes represent LOC_ANIM (no standalone
        // main opcode exists per A3 §3.5). Return false for every standalone-main packet we
        // know about so they fall into the FOLLOWS-then-standalone path. When LOC_ANIM
        // ServerProt is added in B7/B8, update this to test for it.
        return when (packet) {
            is LocAdd, is LocDel, is LocPrefetch, is LocAnimSpecific, is LocMerge,
            is ObjAdd, is ObjDel, is ObjCount, is ObjReveal,
            is MapAnim, is SoundArea -> false
            else -> false
        }
    }
}
