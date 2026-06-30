package org.darkan.world.world

import org.darkan.core.net.prot.ServerProt
import world.gregs.voidps.type.Zone

interface ZoneStateProvider {
    fun packetsFor(zone: Zone, localSceneZone: LocalSceneZone): List<ServerProt>
}

object EmptyZoneStateProvider : ZoneStateProvider {
    override fun packetsFor(zone: Zone, localSceneZone: LocalSceneZone): List<ServerProt> = emptyList()
}
