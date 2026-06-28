package org.darkan.core.net.prot

// === Zone update packets (per A3) ===

/** UPDATE_ZONE_FULL_FOLLOWS — rev948 op 78, 3B. Sets zone globals + scene-clear. */
data class UpdateZoneFullFollowsV2(val level: Int, val zoneX: Int, val zoneY: Int) : ServerProt

/** UPDATE_ZONE_PARTIAL_FOLLOWS — rev948 op 41, 3B. Sets zone globals only. */
data class UpdateZonePartialFollows(val level: Int, val zoneX: Int, val zoneY: Int) : ServerProt

/**
 * UPDATE_ZONE_PARTIAL_ENCLOSED — rev948 op 76, varShort. Carries a header plus embedded
 * sub-packets; prefer standalone main-table zone opcodes when possible.
 */
data class UpdateZonePartialEnclosed(
    val level: Int,
    val zoneX: Int,
    val zoneY: Int,
    val subPackets: List<ServerProt>,
) : ServerProt

/** LOC_ADD — rev948 op 90, varByte. Adds a location at a zone-relative tile. */
data class LocAdd(
    val packedCoord: Int,
    val locId: Int,
    val shapeFlags: Int,
    val extra: Int? = null,
) : ServerProt

/** LOC_DEL — rev948 op 16, 2B. Removes a location by shape+rotation at a tile. */
data class LocDel(val shapeFlags: Int, val packedCoord: Int) : ServerProt

/**
 * LOC_CUSTOMISE — rev948 op 50, varByte. Header (4B template + packed coord + shape +
 * flags) + optional (model list, recolor src list, recolor dst list). Opaque payload until B4.
 */
data class LocCustomise(val payload: ByteArray) : ServerProt {
    override fun equals(other: Any?): Boolean = this === other ||
        (other is LocCustomise && payload.contentEquals(other.payload))
    override fun hashCode(): Int = payload.contentHashCode()
}

/** LOC_PREFETCH — rev948 op 6, 7B. Pre-load a future location. */
data class LocPrefetch(
    val visTime: Int,
    val packedCoord: Int,
    val shapeFlags: Int,
    val locId: Int,
) : ServerProt

/** LOC_ANIM_SPECIFIC — rev948 op 21, 10B. Like LOC_ANIM with constant flag. */
data class LocAnimSpecific(
    val packedCoord: Int,
    val animId: Int,
    val shapeFlags: Int,
    val unknown1: Int,
    val delay: Int,
    val speed: Int,
) : ServerProt

/** LOC_ANIM — rev948 enclosed zone sub-op 13, 11B. */
data class LocAnim(
    val packedCoord: Int,
    val animId: Int,
    val shapeFlags: Int,
    val unknown1: Int,
    val delay: Int,
    val speed: Int,
    val mode: Int,
) : ServerProt

/** LOC_MERGE — rev948 op 170, 5B. Merge a location's model with another entity. */
data class LocMerge(val entityServerIndex: Int, val packedCoordAndShape: Int) : ServerProt

/** OBJ_ADD — rev948 op 46, 5B. Add a ground item to a zone tile. */
data class ObjAdd(
    val packedCoord: Int,
    val objId: Int,
    val count: Int,
) : ServerProt

/** OBJ_DEL — rev948 op 107, 3B. Remove a ground item from a zone tile. */
data class ObjDel(val packedCoord: Int, val objIdLo: Int, val objIdHi: Int) : ServerProt

/** OBJ_COUNT — rev948 op 125, 7B. Update a ground item's stack count. */
data class ObjCount(
    val playerIndex: Int,
    val objIdLo: Int,
    val objIdHi: Int,
    val packedCoord: Int,
    val count: Int,
) : ServerProt

/** OBJ_REVEAL — rev948 op 71, 7B. Reveal a ground item's true count to the viewer. */
data class ObjReveal(
    val packedCoord: Int,
    val objId: Int,
    val oldCount: Int,
    val newCount: Int,
) : ServerProt

/** MAP_ANIM — rev948 op 113, 11B. Place/remove a spot animation at a zone tile. */
data class MapAnim(
    val packedCoord: Int,
    val entityIdLow: Int,
    val entityIdHigh: Int,
    val heightOffset: Int,
    val angleHeight: Int,
) : ServerProt

/** MAP_ANIM_SPECIFIC — rev948 op 183, 14B. Like MAP_ANIM with fine sub-tile offsets. */
data class MapAnimSpecific(
    val packedCoord: Int,
    val entityIdLow: Int,
    val entityIdHigh: Int,
    val heightOffset: Int,
    val angleHeight: Int,
    val unknown: Int,
    val fineOffset: Int,
) : ServerProt

/** MAP_PROJANIM — rev948 op 65, 20B. Spawn a projectile animation between zone tiles. */
data class MapProjAnim(val payload: ByteArray) : ServerProt {
    override fun equals(other: Any?): Boolean = this === other ||
        (other is MapProjAnim && payload.contentEquals(other.payload))
    override fun hashCode(): Int = payload.contentHashCode()
}

/** MAP_PROJANIM_HALT — rev948 op 164, 28B. Projectile with fine src/dst offsets. */
data class MapProjAnimHalt(val payload: ByteArray) : ServerProt {
    override fun equals(other: Any?): Boolean = this === other ||
        (other is MapProjAnimHalt && payload.contentEquals(other.payload))
    override fun hashCode(): Int = payload.contentHashCode()
}

/** PROJANIM_SPECIFIC — rev948 op 151, 21B. Projectile with double-resolution coords. */
data class ProjAnimSpecific(val payload: ByteArray) : ServerProt {
    override fun equals(other: Any?): Boolean = this === other ||
        (other is ProjAnimSpecific && payload.contentEquals(other.payload))
    override fun hashCode(): Int = payload.contentHashCode()
}

/** PROJANIM_SPECIFIC_HALT — rev948 op 177, 29B. Combines halt plus double-res. */
data class ProjAnimSpecificHalt(val payload: ByteArray) : ServerProt {
    override fun equals(other: Any?): Boolean = this === other ||
        (other is ProjAnimSpecificHalt && payload.contentEquals(other.payload))
    override fun hashCode(): Int = payload.contentHashCode()
}

/** SOUND_AREA — rev948 op 168, varByte. Play an area sound effect at a zone tile. */
data class SoundArea(
    val packedCoord: Int,
    val soundId: Int,
    val volume: Int,
    val paramA: Int,
    val paramB: Int,
) : ServerProt
