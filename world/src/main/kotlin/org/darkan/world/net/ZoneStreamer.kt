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
 * piece.) Production streams **616 op78 + 64 op76 before the client sends a single byte back**, and
 * op5 SceneGraphReport's payload grows as the zones register.
 *
 * ## Scene window + base origin (empirically derived; byte-exact vs the production capture)
 *
 * The rendered scene is the classic RS **13×13 zone block (104×104 tiles) centred on the spawn**,
 * across planes 0–3. Each op78 carries the zone in **build-area-local** coordinates:
 *
 *     localZone = sceneZone − buildAreaSWzone
 *
 * where `buildAreaSWzone` is the build area's SW corner in zones (the client's `base608`/`base60c`,
 * set from op81's `packedCoordA`). For the default MEDIUM build area the spawn sits 2 regions
 * (16 zones) from the SW corner, so the window lands at local **[10..22]** with the spawn at local
 * **16** — identical to production. Verification: the first production op78 `80 76 0a` decodes to
 * `(level 0, local (10,10))` = the scene's SW corner, which this streamer reproduces exactly.
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

    /**
     * Scene planes streamed (0–3). Production streams level 0 as a full 13×13 (169 zones) and the
     * upper levels only for content-bearing zones (167/153/127); we stream all four planes fully —
     * a harmless superset, since op78 is a bare zone registration (no map data; the terrain is
     * client-fetched from the local cache) and the client renders nothing for an empty upper floor.
     */
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
        // Build-area SW corner in ZONE units (region << 3 = region * 8 zones-per-region). This is
        // the client's op78 base608/base60c, established by op81's packedCoordA.
        val baseZoneX = viewport.buildArea.minRegion.x shl 3
        val baseZoneZ = viewport.buildArea.minRegion.y shl 3
        for (level in 0 until SCENE_PLANES) {
            for (zoneX in (centreZoneX - SCENE_RADIUS_ZONES)..(centreZoneX + SCENE_RADIUS_ZONES)) {
                for (zoneZ in (centreZoneZ - SCENE_RADIUS_ZONES)..(centreZoneZ + SCENE_RADIUS_ZONES)) {
                    session.send(
                        UpdateZoneFullFollowsV2(
                            level = level,
                            zoneX = zoneX - baseZoneX,
                            zoneY = zoneZ - baseZoneZ,
                        )
                    )
                }
            }
        }
    }
}
