package org.darkan.world.entity

/**
 * The verified 8-direction walk table and its inverse — the SINGLE source of truth shared by the
 * [MovementQueue] (which resolves `(dx,dy)` → index on enqueue) and the op22 high-res WALK encoder
 * ([org.darkan.world.net.PlayerMovementEncoder.encodeHighResPosition], which emits the index as 3
 * bits).
 *
 * ## Why this table
 *
 * It is a verbatim copy of the recorder oracle's `PLAYER_REGION_DX` / `PLAYER_REGION_DY`
 * (`core/.../recorder/ClientStateCrossCheck.kt:1614-1615`) — the table the client-verified decode uses
 * to turn a 3-bit direction index into a coordinate delta (the decode applies it in the low-res
 * region-move branch, `ClientStateCrossCheck.kt:1318-1322`). The op22 high-res WALK form
 * (`decodeKnownPlayerUpdate` mvt=1, `ClientStateCrossCheck.kt:1260-1266`) reads the SAME 3-bit index,
 * so to encode a one-tile step `(dx,dy)` we emit the index `d` where `DX[d]==dx && DY[d]==dy`. RS y
 * increases NORTH, so the table reads (lowest index first): SW, S, SE, W, E, NW, N, NE.
 *
 *  | index | (dx, dy) | compass |
 *  |------:|:--------:|:-------:|
 *  |   0   | (-1, -1) |   SW    |
 *  |   1   | ( 0, -1) |   S     |
 *  |   2   | ( 1, -1) |   SE    |
 *  |   3   | (-1,  0) |   W     |
 *  |   4   | ( 1,  0) |   E     |
 *  |   5   | (-1,  1) |   NW    |
 *  |   6   | ( 0,  1) |   N     |
 *  |   7   | ( 1,  1) |   NE    |
 */
object Direction8 {

    /** X delta per direction index — verbatim from `ClientStateCrossCheck.kt:1614` (`PLAYER_REGION_DX`). */
    val DX: IntArray = intArrayOf(-1, 0, 1, -1, 1, -1, 0, 1)

    /** Y delta per direction index — verbatim from `ClientStateCrossCheck.kt:1615` (`PLAYER_REGION_DY`). */
    val DY: IntArray = intArrayOf(-1, -1, -1, 0, 0, 1, 1, 1)

    /**
     * The 3-bit direction index `d` such that `DX[d]==dx && DY[d]==dy` — the inverse of the verified
     * table. `dx, dy` must each be in `{-1, 0, 1}` and not both `0` (a real one-tile step); otherwise
     * this throws, because there is no direction index for a no-move / multi-tile delta.
     */
    fun indexOf(dx: Int, dy: Int): Int {
        for (d in DX.indices) {
            if (DX[d] == dx && DY[d] == dy) return d
        }
        throw IllegalArgumentException(
            "no 8-direction index for step (dx=$dx, dy=$dy); dx,dy must be in {-1,0,1} and not both 0"
        )
    }
}
