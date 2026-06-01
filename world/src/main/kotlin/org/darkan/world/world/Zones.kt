package org.darkan.world.world

import org.darkan.core.net.prot.ServerProt
import world.gregs.voidps.type.Zone
import java.util.concurrent.ConcurrentHashMap

/**
 * Per-tick queue of zone-relative ServerProt sub-packets ([ServerProt] data classes
 * representing UPDATE_ZONE_PARTIAL_FOLLOWS sub-records, etc.).
 *
 * Zones flushed here are bundled by ZoneBundleBuilder (B6) into one
 * UPDATE_ZONE_FULL_FOLLOWS / UPDATE_ZONE_PARTIAL_FOLLOWS per zone per recipient
 * player whose viewport overlaps the zone. The map is cleared by the tick loop
 * (B7) after dispatch.
 *
 * Keyed by [Zone.id] (the packed int form of the zone, including level) so multi-level
 * worlds don't collide their bundles.
 */
object Zones {
    /** zoneId (from Zone.id) -> list of pending ServerProt zone-relative sub-packets emitted this tick. */
    val pending = ConcurrentHashMap<Int, MutableList<ServerProt>>()

    fun queue(zone: Zone, packet: ServerProt) {
        pending.computeIfAbsent(zone.id) { mutableListOf() }.add(packet)
    }

    fun clear() {
        pending.clear()
    }
}
