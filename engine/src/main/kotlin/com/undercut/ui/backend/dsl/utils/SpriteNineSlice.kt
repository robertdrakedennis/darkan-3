package com.undercut.ui.backend.dsl.utils

import com.undercut.ui.backend.native.ImGuiTexture
import com.undercut.ui.backend.native.getTexture
import world.gregs.voidps.cache.Cache

/**
 * Minimal nine-slice descriptor backed by cache sprites.
 * Width/height come from the decoded sprite (not the texture), which we then draw via ImGui.
 */
data class SpritePiece(val id: Int, val texture: ImGuiTexture, val width: Int, val height: Int)

data class SpriteNineSlice(
    val topLeft: SpritePiece,
    val top: SpritePiece,
    val topRight: SpritePiece,
    val left: SpritePiece,
    val center: SpritePiece,
    val right: SpritePiece,
    val bottomLeft: SpritePiece,
    val bottom: SpritePiece,
    val bottomRight: SpritePiece
) {
    companion object {
        fun fromSpriteIds(
            topLeft: Int,
            top: Int,
            topRight: Int,
            left: Int,
            center: Int,
            right: Int,
            bottomLeft: Int,
            bottom: Int,
            bottomRight: Int
        ): SpriteNineSlice {
            fun load(id: Int): SpritePiece {
                val s = Cache.sprite(id) ?: error("Sprite $id doesn't exist.")
                val tex = s.getTexture()
                return SpritePiece(id, tex, s.maxWidth, s.maxHeight)
            }

            return SpriteNineSlice(
                topLeft = load(topLeft),
                top = load(top),
                topRight = load(topRight),
                left = load(left),
                center = load(center),
                right = load(right),
                bottomLeft = load(bottomLeft),
                bottom = load(bottom),
                bottomRight = load(bottomRight)
            )
        }
    }
}
