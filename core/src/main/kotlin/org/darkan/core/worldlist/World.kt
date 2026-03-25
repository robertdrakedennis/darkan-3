package org.darkan.core.worldlist

/**
 * A game world in the world list.
 * Based on ~/darkan/server reference — data structure only.
 */
data class World(
    val number: Int,               // World ID (e.g., 300)
    val hostname: String,          // Server hostname/IP
    val port: Int = 43595,         // Server port (default 43595)
    val activity: String = "",     // Activity description (e.g., "Normal")
    val country: Country = Country.USA,
    val members: Boolean = true,
    val quickchat: Boolean = false,
    val pvp: Boolean = false,
    val lootShare: Boolean = false,
    val highlighted: Boolean = false,
) {
    /** Position in sorted world array (set by WorldList). */
    var index: Int = 0
        internal set

    /** Current player count. -1 = offline. */
    var playersOnline: Int = 0

    /** Whether this world is currently offline. */
    var offline: Boolean = false

    /** Protocol flags bitmask. */
    fun flags(): Int {
        var f = 0
        if (members) f = f or 0x1
        if (quickchat) f = f or 0x2
        if (pvp) f = f or 0x4
        if (lootShare) f = f or 0x8
        if (highlighted) f = f or 0x10
        if (port != 43595) f = f or 0x40000000
        return f
    }
}
