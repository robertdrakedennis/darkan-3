package org.darkan.world.net

import org.darkan.core.net.prot.UpdateZoneFullFollowsV2
import org.darkan.core.net.session.GameSession
import org.darkan.world.world.Viewport

/**
 * Streams the world-entry **zone scene** — the op78 `UpdateZoneFullFollowsV2` packets that populate
 * the NXT 948-5 client's scene graph and thereby advance the scene-build phase
 * (`ClientSceneManager+0x1c`, `client+0x19578`).
 *
 * ## Why this is the foundational render fix
 *
 * Per the binary RE in `docs/protocol/world-login-camera-render-948.md` §12: the client's per-frame
 * world tick runs, but op5/op51 emission, the in-world per-frame branch, and the render are **all
 * gated on `ClientSceneManager+0x1c != 0`**. That field boots at 0 ("Running Auto Configuration")
 * and only advances once the **scene graph is populated** — which happens as the server streams
 * zone packets. With **no** zone stream the graph stays empty, the phase never advances, and the
 * client black-screens after world login while emitting **zero** C2S. (op81 builds the world +
 * positions the camera and JS5/cache are healthy — both settled; the scene stream was the missing
 * piece.) Latest production streams **606 op78** before the HUD root commit.
 *
 * ## Scene window + base origin
 *
 * The rendered scene is the classic RS **13×13 zone block (104×104 tiles) centred on the spawn**.
 * Each op78 carries a zone coordinate relative to the scene-state base at local zone 16, so the
 * southwest streamed zone `(centre - 6)` is sent as local `10`. This matches production's first
 * op78 byte sample `80 76 0a` without tying the op78 local space to op81's packed build-area SW.
 *
 * The packed op81 build-area bounds are still needed for map-square allocation/JS5, but they are
 * not the coordinate origin for op78. Production rev948 uses much larger packed bounds while op78
 * remains in the same `[10..22]` scene-local range.
 *
 * ## Ordering (critical)
 *
 * MUST run **after** op55 `DestroyZoneData` (which clears the client's zones) and **before** the op3
 * HUD commit + op75 ready flag — matching production order: `op55 → zone stream → op3 → … → op75
 * (last)`. Streaming before op55 would have the reset wipe the freshly-registered zones.
 */
object ZoneStreamer {

    /** Half-extent of the render scene in zones: a `2*RADIUS+1` = 13-zone (104-tile) square. */
    const val SCENE_RADIUS_ZONES: Int = 6

    /** Local scene zone assigned to the centre zone by the client scene state. */
    const val SCENE_CENTER_LOCAL_ZONE: Int = 16

    /** Scene planes streamed (0–3). */
    const val SCENE_PLANES: Int = 4

    /**
     * Emits op78 for every zone of the 13×13×[SCENE_PLANES] render scene centred on the viewport's
     * build-area centre zone, in build-area-local coordinates. Iteration order matches production
     * (outer `zoneX`, inner `zoneZ`). Call after op55 `DestroyZoneData` and before the op3 HUD commit.
     *
     * The scene (13 zones per axis) always sits inside the MEDIUM+ build-area grid (≥40 zones per
     * axis), so every emitted local coord is non-negative and inside the client's allocated grid.
     */
    suspend fun streamScene(session: GameSession, viewport: Viewport) {
        val centreZoneX = viewport.buildAreaChunkX
        val centreZoneZ = viewport.buildAreaChunkY
        val baseZoneX = sceneBaseZone(centreZoneX)
        val baseZoneZ = sceneBaseZone(centreZoneZ)
        for (level in 0 until SCENE_PLANES) {
            for (zoneX in (centreZoneX - SCENE_RADIUS_ZONES)..(centreZoneX + SCENE_RADIUS_ZONES)) {
                for (zoneZ in (centreZoneZ - SCENE_RADIUS_ZONES)..(centreZoneZ + SCENE_RADIUS_ZONES)) {
                    val localX = zoneX - baseZoneX
                    val localY = zoneZ - baseZoneZ
                    if (shouldStream(level, localX - 16, localY - 16)) {
                        session.send(
                            UpdateZoneFullFollowsV2(
                                level = level,
                                zoneX = localX,
                                zoneY = localY,
                            )
                        )
                        for (packet in FirstLightSceneBootstrap.packets(level, localX, localY)) {
                            session.send(packet)
                        }
                    }
                }
            }
        }
    }

    internal fun shouldStream(level: Int, relativeX: Int, relativeY: Int): Boolean = when (level) {
        0, 1 -> true
        2 -> (relativeX to relativeY) !in LEVEL_2_OMIT
        3 -> (relativeX to relativeY) !in LEVEL_3_OMIT
        else -> false
    }

    internal fun sceneBaseZone(centreZone: Int): Int = centreZone - SCENE_CENTER_LOCAL_ZONE

    private val LEVEL_2_OMIT = setOf(
        -6 to -2,
        -5 to -2, -5 to -1, -5 to 0, -5 to 1, -5 to 3, -5 to 4,
        -4 to -3, -4 to -2, -4 to -1, -4 to 0, -4 to 1, -4 to 6,
        3 to 5, 3 to 6,
        4 to 5, 4 to 6,
        5 to 5, 5 to 6,
    )

    private val LEVEL_3_OMIT = setOf(
        -6 to -2, -6 to 1, -6 to 2, -6 to 3, -6 to 4,
        -5 to -2, -5 to -1, -5 to 0, -5 to 1, -5 to 2, -5 to 3, -5 to 4,
        -4 to -3, -4 to -2, -4 to -1, -4 to 0, -4 to 1, -4 to 6,
        -2 to 1, -2 to 4,
        -1 to 1, -1 to 4,
        0 to -3, 0 to 1, 0 to 3, 0 to 4,
        1 to 3, 1 to 4,
        2 to 1, 2 to 3, 2 to 4,
        3 to 0, 3 to 1, 3 to 3, 3 to 4, 3 to 5, 3 to 6,
        4 to 0, 4 to 1, 4 to 3, 4 to 4, 4 to 5, 4 to 6,
        5 to -2, 5 to -1, 5 to 0, 5 to 1, 5 to 2, 5 to 3, 5 to 4, 5 to 6,
    )
}
