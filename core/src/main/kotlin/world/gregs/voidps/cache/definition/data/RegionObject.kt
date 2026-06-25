package world.gregs.voidps.cache.definition.data

/**
 * A single decoded map object (location) from a MAPSV2 region, in region-local
 * coordinates (0..63). [plane] is the raw decoded source plane (`location shr 12`)
 * with no bridge adjustment applied - resolving the bridge flag is a consumer
 * concern (see [world.gregs.voidps.cache.definition.decoder.MapDecoder]). [shape] and
 * [rotation] are the raw values from the client's object-data byte; mapping shape ids
 * to wall/scenery/decoration slots is left to the consumer.
 */
data class RegionObject(
    val id: Int,
    val localX: Int,
    val localY: Int,
    val plane: Int,
    val shape: Int,
    val rotation: Int
)
