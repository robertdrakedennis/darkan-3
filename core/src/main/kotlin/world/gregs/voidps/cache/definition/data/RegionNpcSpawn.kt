package world.gregs.voidps.cache.definition.data

/**
 * An NPC spawn decoded from a MAPSV2 region NPCS file.
 *
 * Only [typeId] is currently recovered. The per-spawn position layout that follows
 * each type id in the client's NPCS stream is an unresolved reverse-engineering
 * follow-up, so [localX]/[localY]/[plane] are left at 0. Do not treat them as real
 * positions until the handler format is documented.
 */
data class RegionNpcSpawn(
    val typeId: Int,
    val localX: Int = 0,
    val localY: Int = 0,
    val plane: Int = 0
)
