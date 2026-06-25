package world.gregs.voidps.cache.definition.data

import world.gregs.voidps.cache.Definition
import java.awt.image.BufferedImage

data class SpriteDefinition(
    override var id: Int = -1,
    var sprites: Array<IndexedSprite>? = null,
    var maxWidth: Int = 0,
    var maxHeight: Int = 0,
) : Definition {

    /** Engine (Sprite) alias for [sprites] (the decoded sub-images). */
    val subImages: Array<IndexedSprite>? get() = sprites

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SpriteDefinition

        if (id != other.id) return false
        if (sprites != null) {
            if (other.sprites == null) return false
            if (!sprites.contentEquals(other.sprites)) return false
        } else if (other.sprites != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + (sprites?.contentHashCode() ?: 0)
        return result
    }
}

/** Engine (Sprite) helper: flattens an image to tightly packed RGBA bytes. */
fun BufferedImage.toRGBABytes(): ByteArray {
    val pixels = IntArray(width * height)
    getRGB(0, 0, width, height, pixels, 0, width)
    val rgba = ByteArray(width * height * 4)
    var i = 0
    for (argb in pixels) {
        rgba[i++] = ((argb shr 16) and 0xFF).toByte()
        rgba[i++] = ((argb shr 8) and 0xFF).toByte()
        rgba[i++] = (argb and 0xFF).toByte()
        rgba[i++] = ((argb shr 24) and 0xFF).toByte()
    }
    return rgba
}