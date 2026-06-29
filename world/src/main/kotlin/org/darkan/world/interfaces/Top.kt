package org.darkan.world.interfaces

import world.gregs.voidps.gameval.Gameval

/**
 * Named top-level root windows. Gameval resolution is lazy, with the known rev948 ids retained as a
 * fallback so interface validation does not depend on resource availability for the common roots.
 */
enum class Top(val gameval: String, private val fallbackId: Int) {
    TOPLEVEL_V2("toplevel_v2", 1477),
    LOBBY_SCREEN("lobbyscreen", 906);

    val id: Int by lazy { Gameval.id(Gameval.INTERFACE, gameval) ?: fallbackId }
}
