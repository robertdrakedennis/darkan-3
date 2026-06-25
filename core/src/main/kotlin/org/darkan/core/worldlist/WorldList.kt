package org.darkan.core.worldlist

/**
 * Manages the world list with revision tracking for delta updates.
 * Based on ~/darkan/server reference — data structure and logic only.
 */
class WorldList(
    val maxWorlds: Int = 300,
    initialRevision: Int = REV948_PROD_WORLDLIST_REVISION,
    private val dynamicRevision: Boolean = false,
) {
    companion object {
        const val REV948_PROD_WORLDLIST_REVISION: Int = 0x27B8926D
    }

    private val worlds = sortedMapOf<Int, World>()
    private var _revision = initialRevision

    val revision: Int get() = _revision

    @Synchronized
    fun get(number: Int): World? = worlds[number]

    @Synchronized
    fun put(world: World) {
        worlds[world.number] = world
        markChanged()
        updateIndices()
    }

    @Synchronized
    fun remove(number: Int): World? {
        val w = worlds.remove(number)
        if (w != null) {
            markChanged()
            updateIndices()
        }
        return w
    }

    @Synchronized
    fun getWorldArray(): Array<World> = worlds.values.toTypedArray()

    @Synchronized
    fun getDefault(): World? = worlds.values.firstOrNull()

    @Synchronized
    fun size(): Int = worlds.size

    private fun updateIndices() {
        worlds.values.forEachIndexed { i, w -> w.index = i }
    }

    private fun markChanged() {
        if (dynamicRevision) {
            _revision++
        }
    }
}
