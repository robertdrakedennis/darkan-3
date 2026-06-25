package world.gregs.voidps.cache.definition.data

import world.gregs.voidps.cache.Definition

/**
 * Plain decoded contents of a single MAPSV2 region (index 5), keyed by its packed
 * region id (`regionX shl 8 or regionY`). All tile grids are indexed `[plane][x][y]`
 * with plane in 0..3 and local x/y in 0..63; cells absent from the cache stream stay
 * at their default 0. Holds raw decoded values only - no collision, scene-graph, or
 * game-runtime types.
 */
class RegionDefinition(
    override var id: Int = -1
) : Definition {

    val overlayIds: Array<Array<IntArray>> = grid()
    val underlayIds: Array<Array<IntArray>> = grid()
    val overlayPathShapes: Array<Array<IntArray>> = grid()
    val overlayRotations: Array<Array<IntArray>> = grid()

    /**
     * Raw per-tile render flags (the client's signed flag byte, sign-extended).
     * Only bitwise masks are meaningful (e.g. `& 0x2` marks a bridge tile that
     * lowers objects/clipping by one plane).
     */
    val tileFlags: Array<Array<IntArray>> = grid()

    val objects: MutableList<RegionObject> = mutableListOf()
    val npcSpawns: MutableList<RegionNpcSpawn> = mutableListOf()
    val waterPatches: MutableList<RegionWaterPatch> = mutableListOf()

    private companion object {
        fun grid(): Array<Array<IntArray>> = Array(4) { Array(64) { IntArray(64) } }
    }
}
