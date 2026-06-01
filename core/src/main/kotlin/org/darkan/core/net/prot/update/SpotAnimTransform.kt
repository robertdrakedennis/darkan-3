package org.darkan.core.net.prot.update

/**
 * A single 4x4-style transform entry inside a SPOT_ANIMS or SPOTANIM_TRANSFORM_LIST block.
 *
 * Per the player-info SPOT_ANIMS block and the analogous NPC_INFO mask (see
 * `docs/net/serverprot/948-delta-from-947-3.md`), each entry carries a 16-bit `flags` bitmask and a 16-bit `slotId`
 * followed by optional [startTransformId], [endTransformId], translation, rotation, and
 * scale fields whose presence is gated by [flags]:
 *
 * - `0x400` => uint startTransformId
 * - `0x800` => uint endTransformId
 * - `0x004` => uint translationX (axis assignment provisional; see A4 §SpotAnims)
 * - `0x010` / `0x020` => rotationX / rotationY (angle lookups via FUN_00c2b060)
 * - `0x080` / `0x100` / `0x200` => scaleX / scaleY / scaleZ
 * - `0x040` => model-space (compose with existing); else world-space (replace)
 */
data class SpotAnimTransform(
    val flags: Int,
    val slotId: Int,
    val startTransformId: Int = 0,
    val endTransformId: Int = 0,
    val translationX: Int = 0,
    val translationY: Int = 0,
    val translationZ: Int = 0,
    val rotationX: Int = 0,
    val rotationY: Int = 0,
    val rotationZ: Int = 0,
    val scaleX: Int = 0,
    val scaleY: Int = 0,
    val scaleZ: Int = 0,
)

