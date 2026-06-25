package com.undercut.ui.backend.native

import com.undercut.cache.type.sprites.Sprite
import com.undercut.cache.type.sprites.toRGBABytes
import java.awt.image.BufferedImage
import java.lang.ref.Cleaner
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Managed ImGui texture that auto-destroys via Cleaner when unreachable.
 * Thread-safe single-shot destruction is ensured by an AtomicBoolean gate.
 */
class ImGuiTexture internal constructor(
    val id: Long,
    val width: Int,
    val height: Int
) : AutoCloseable {

    private val disposed = AtomicBoolean(false)
    private val cleanable: Cleaner.Cleanable = cleaner.register(this, Cleanup(id, disposed))

    fun destroy() {
        cleanable.clean()
    }

    override fun close() = destroy()

    private class Cleanup(
        private val textureId: Long,
        private val disposed: AtomicBoolean
    ) : Runnable {
        override fun run() {
            if (textureId == 0L) return
            if (disposed.compareAndSet(false, true)) {
                try {
                    NativeBridge.destroyTextureById(textureId)
                } catch (_: Throwable) {
                    // Ignore cleanup failures
                }
            }
        }
    }

    companion object {
        private val cleaner: Cleaner = Cleaner.create()

        fun fromRGBA(pixels: ByteArray, width: Int, height: Int): ImGuiTexture? =
            NativeBridge.createTextureFromRGBA(pixels, width, height)

        fun fromPath(pathOrResource: String): ImGuiTexture? =
            ImageHelper.loadTexture(pathOrResource)
    }
}

private data class SpriteKey(val id: Int, val turns: Int)
private val spriteTextureCache = mutableMapOf<SpriteKey, ImGuiTexture>()

enum class SpriteRotation(val turns: Int) {
    R0(0), R90(1), R180(2), R270(3);
    val degrees: Int get() = turns * 90
}

fun Sprite.getTexture(): ImGuiTexture = getTexture(SpriteRotation.R0)

fun Sprite.getTexture(rotation: SpriteRotation): ImGuiTexture =
    spriteTextureCache.getOrPut(SpriteKey(archiveId, rotation.turns)) {
        createTexture(rotation) ?: error("Sprite $archiveId doesn't exist.")
    }

fun Sprite.createTexture(rotation: SpriteRotation): ImGuiTexture? {
    if (subImages.isEmpty()) return null

    val fullImage = BufferedImage(maxWidth, maxHeight, BufferedImage.TYPE_INT_ARGB)
    val graphics = fullImage.createGraphics()

    // Draw all sub-images onto the full canvas
    for (subImg in subImages) {
        graphics.drawImage(subImg.img, subImg.x, subImg.y, null)
    }
    graphics.dispose()

    val (finalImage, w, h) = when (rotation) {
        SpriteRotation.R0 -> Triple(fullImage, fullImage.width, fullImage.height)
        SpriteRotation.R90 -> rotate90(fullImage)
        SpriteRotation.R180 -> rotate180(fullImage)
        SpriteRotation.R270 -> rotate270(fullImage)
    }

    val rgbaBytes = finalImage.toRGBABytes()
    return ImGuiTexture.fromRGBA(rgbaBytes, w, h)
}

fun Sprite.clearTextureCache() {
    val keys = spriteTextureCache.keys.filter { it.id == archiveId }
    keys.forEach { key -> spriteTextureCache.remove(key)?.destroy() }
}

fun clearAllSpriteTextures() {
    spriteTextureCache.values.forEach { it.destroy() }
    spriteTextureCache.clear()
}

private fun rotate90(src: BufferedImage): Triple<BufferedImage, Int, Int> {
    val w = src.width
    val h = src.height
    val dst = BufferedImage(h, w, BufferedImage.TYPE_INT_ARGB)
    for (y in 0 until h) {
        for (x in 0 until w) {
            val argb = src.getRGB(x, y)
            dst.setRGB(h - 1 - y, x, argb)
        }
    }
    return Triple(dst, h, w)
}

private fun rotate180(src: BufferedImage): Triple<BufferedImage, Int, Int> {
    val w = src.width
    val h = src.height
    val dst = BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)
    for (y in 0 until h) {
        for (x in 0 until w) {
            val argb = src.getRGB(x, y)
            dst.setRGB(w - 1 - x, h - 1 - y, argb)
        }
    }
    return Triple(dst, w, h)
}

private fun rotate270(src: BufferedImage): Triple<BufferedImage, Int, Int> {
    val w = src.width
    val h = src.height
    val dst = BufferedImage(h, w, BufferedImage.TYPE_INT_ARGB)
    for (y in 0 until h) {
        for (x in 0 until w) {
            val argb = src.getRGB(x, y)
            dst.setRGB(y, w - 1 - x, argb)
        }
    }
    return Triple(dst, h, w)
}
