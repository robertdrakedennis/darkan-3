package world.gregs.voidps.routefinder

data class Route(
    val tiles: List<RouteCoordinates>,
    val alternative: Boolean,
    val success: Boolean,
) : List<RouteCoordinates> by tiles {
    val failed: Boolean get() = !success

    companion object {
        val FAILED = Route(tiles = emptyList(), alternative = false, success = false)
    }
}
