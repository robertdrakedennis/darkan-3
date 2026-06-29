package world.gregs.voidps.routefinder

@JvmInline
value class RouteCoordinates(val packed: Int) {
    val x: Int get() = (packed shr 14) and 0x3fff
    val z: Int get() = packed and 0x3fff
    val level: Int get() = (packed shr 28) and 0x3

    constructor(x: Int, z: Int, level: Int = 0) :
        this((z and 0x3fff) or ((x and 0x3fff) shl 14) or ((level and 0x3) shl 28))
}
