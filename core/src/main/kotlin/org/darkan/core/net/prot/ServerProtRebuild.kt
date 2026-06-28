package org.darkan.core.net.prot

// === Rebuild packets (per A2) ===

/**
 * REBUILD_NORMAL_SIMPLE — the simple-form (non-instanced) world-login scene build
 * (`ClientState::REBUILD_NORMAL_SIMPLE`). Allocates + installs the BuildArea; without it the
 * client has no scene and stays on the loading screen. Rev948 op 81, varShort, magic 0x85,
 * with a production prefix plus 18-byte tail. The handler parses this tail after packet position
 * has advanced through the prefix:
 *      +0  u8   ignored filler (production sends 0xFF)
 *      +1  u8   centreZoneZ low
 *      +2  u8   centreZoneZ high            (Z is LE u16: lo then hi; may exceed 255)
 *      +3  u8   magic = 0x85
 *      +4  u16  centreZoneX (BE)
 *      +6  u8   cameraRotation, writeByteAdd (wire = (value + 0x80) & 0xFF)
 *      +7  u8   ignored filler (send 0)
 *      +8  u16  sceneRootId (BE)            scene root selector consumed before BuildArea install
 *      +10 u32  packedCoordA (BE)           build-area SW corner
 *      +14 u32  packedCoordB (BE)           build-area NE corner
 *
 * `packedCoordA/B` use the BuildArea `DecodePackedCoord` packing (`BuildArea::DecodePackedCoord`
 * @ 0x006d4320): `word = (plane << 28) | (hi14 << 14) | lo14`, two 14-bit fields + 2-bit plane.
 * Per `docs/protocol/packed-coord-buildarea-948.md` (948-5-verified), the handler discards plane
 * and passes both 14-bit fields `>> 6` to the scene builder as **map-square (region) corners**:
 *   - `packedCoordA` = the **SW / origin corner** {minRegionX = hi14>>6, minRegionZ = lo14>>6}
 *   - `packedCoordB` = the **NE / far corner**  {maxRegionX = hi14>>6, maxRegionZ = lo14>>6}
 * `hi14` is the X-**tile**, `lo14` the Z-**tile**; the client divides each by 64 (`>>6`) to get
 * the region. The two words are two corners, NOT origin+span, and NOT `zone<<6` (the prior broken
 * model overflowed 14 bits and produced inverted bounds → empty grid → black screen). Build each
 * word from a region corner with [packRegionCoord], or use the world `BuildArea` service.
 */
data class RebuildNormalSimple(
    /** Centre zone X (8-tile units). Rev948: +4 BE u16. */
    val zoneX: Int,
    /** Centre zone Z (8-tile units). Rev948: +1/+2 LE u16. */
    val zoneZ: Int,
    val packedCoordA: Int,
    val packedCoordB: Int,
    /** 948 camera rotation byte (written +0x80). Default 0. */
    val cameraRotation: Int = 0,
    /** 948 scene root selector (+8 BE u16). Production first-light uses 474. */
    val sceneRootId: Int = 0,
    /** Legacy encoder field. Ignored by the rev948 encoder. */
    val forceRefresh: Boolean = true,
    /** Legacy encoder field. Ignored by the rev948 encoder. */
    val regionLow: Int = 0,
    /** Rev948 prefix before the 18-byte parser tail. */
    val rebuildPrefix: ByteArray = ByteArray(0),
) : ServerProt {
    init {
        require(rebuildPrefix.size <= 65517) { "Rebuild prefix is too large for VarShort framing" }
    }

    override fun equals(other: Any?): Boolean = this === other ||
        (other is RebuildNormalSimple &&
            zoneX == other.zoneX &&
            zoneZ == other.zoneZ &&
            packedCoordA == other.packedCoordA &&
            packedCoordB == other.packedCoordB &&
            cameraRotation == other.cameraRotation &&
            sceneRootId == other.sceneRootId &&
            forceRefresh == other.forceRefresh &&
            regionLow == other.regionLow &&
            rebuildPrefix.contentEquals(other.rebuildPrefix))

    override fun hashCode(): Int {
        var result = zoneX
        result = 31 * result + zoneZ
        result = 31 * result + packedCoordA
        result = 31 * result + packedCoordB
        result = 31 * result + cameraRotation
        result = 31 * result + sceneRootId
        result = 31 * result + forceRefresh.hashCode()
        result = 31 * result + regionLow
        result = 31 * result + rebuildPrefix.contentHashCode()
        return result
    }

    companion object {
        /**
         * Builds a `DecodePackedCoord` word from a **map-square (region) corner**, the inverse of
         * `BuildArea::DecodePackedCoord @0x006d4320`.
         *
         * The client recovers a region by `field >> 6`, so each 14-bit field must hold the
         * **tile-aligned** value `region << 6` (`hi14` = X-tile, `lo14` = Z-tile). The low 6 bits
         * are ignored by the build path, so we emit the clean `region << 6` form. plane occupies
         * bits 28-29. Verified against production (`docs/protocol/packed-coord-buildarea-948.md`
         * §5.3): `packRegionCoord(26, 37) = 0x01a00940`.
         *
         * Callers should prefer the world `BuildArea` service (`BuildArea.packedCoordA/B`), which
         * also enforces non-inverted bounds; this helper is the low-level encode used by the
         * service and by tests.
         *
         * @param regionX map-square X — recovered as `((word >> 14) & 0x3FFF) >> 6`.
         * @param regionZ map-square Z — recovered as `(word & 0x3FFF) >> 6`.
         */
        fun packRegionCoord(regionX: Int, regionZ: Int, plane: Int = 0): Int =
            ((plane and 0x3) shl 28) or
                (((regionX shl 6) and 0x3FFF) shl 14) or
                ((regionZ shl 6) and 0x3FFF)
    }
}

data class RebuildRegion(val scenes: List<RebuildRegionScene>) : ServerProt {
    init {
        require(scenes.size in 0..0xFF) { "rebuild region scene count out of range: ${scenes.size}" }
    }

    companion object {
        fun firstLight(): RebuildRegion =
            RebuildRegion(
                listOf(
                    RebuildRegionScene(sceneId = 4, secondaryIds = listOf(1)),
                    RebuildRegionScene(
                        sceneId = 1,
                        primaryIds = listOf(0),
                        secondaryIds = listOf(1, 2, 4),
                        primaryMetadata = listOf(0),
                        matrix = listOf(listOf(0, 0, 0)),
                    ),
                    RebuildRegionScene(sceneId = 2, secondaryIds = listOf(22, 15, 17, 11)),
                    RebuildRegionScene(sceneId = 3, secondaryIds = listOf(36, 31, 33, 35)),
                ),
            )
    }
}

data class RebuildRegionScene(
    val sceneId: Int,
    val primaryIds: List<Int> = emptyList(),
    val secondaryIds: List<Int> = emptyList(),
    val primaryMetadata: List<Int> = emptyList(),
    val matrix: List<List<Int?>> = emptyList(),
) {
    init {
        require(primaryIds.size in 0..0xFF) { "primary id count out of range: ${primaryIds.size}" }
        require(secondaryIds.size in 0..0xFF) { "secondary id count out of range: ${secondaryIds.size}" }
        require(primaryMetadata.size == primaryIds.size) {
            "primary metadata count ${primaryMetadata.size} does not match primary id count ${primaryIds.size}"
        }
        require(matrix.size == primaryIds.size) {
            "matrix row count ${matrix.size} does not match primary id count ${primaryIds.size}"
        }
        require(primaryMetadata.all { it in -128..127 }) { "primary metadata must fit signed byte" }
        require(matrix.all { it.size == secondaryIds.size }) {
            "matrix column count must match secondary id count ${secondaryIds.size}"
        }
    }
}

/**
 * Multi-scene grid rebuild for INSTANCED regions (rev947 op172). Field structure (per-scene
 * seed + primary/secondary descriptor lists + per-cell XTEA grid) is heavy; modelled as an
 * opaque payload until a downstream consumer needs structured access.
 *
 * This is distinct from the canonical [RebuildRegion] (rev948 op199, structured scene list). The
 * multi-revision monorepo keeps both: rev947's op172 codec emits the opaque grid via this class,
 * while rev948's op199 codec emits a structured scene list via [RebuildRegion]. The two were
 * separated to avoid a display-name collision — see ServerProt.kt history (2026-06-25).
 */
data class RebuildNormalMultiScene(val payload: ByteArray) : ServerProt {
    override fun equals(other: Any?): Boolean = this === other ||
        (other is RebuildNormalMultiScene && payload.contentEquals(other.payload))
    override fun hashCode(): Int = payload.contentHashCode()
}

/**
 * REBUILD_WORLDENTITY — rev948 op 186, varShort. Triple-nested (level / regionX /
 * regionY) -1-terminated XTEA stream. Opaque payload until B4 defines a structured API.
 */
data class RebuildWorldEntity(val payload: ByteArray) : ServerProt {
    override fun equals(other: Any?): Boolean = this === other ||
        (other is RebuildWorldEntity && payload.contentEquals(other.payload))
    override fun hashCode(): Int = payload.contentHashCode()
}
