package world.gregs.voidps.cache.definition.data

/**
 * A water patch decoded from a MAPSV2 region WATER_TILES file. [qx]/[qy]/[qz]/[qw]
 * form the patch orientation quaternion.
 */
data class RegionWaterPatch(
    val posX: Int,
    val posZ: Int,
    val posY: Int,
    val extentX: Int,
    val extentZ: Int,
    val qx: Float,
    val qy: Float,
    val qz: Float,
    val qw: Float,
    val waterTypeId: Int,
    val scale1: Int,
    val scale2: Int,
    val waterMeshId: Int
)
