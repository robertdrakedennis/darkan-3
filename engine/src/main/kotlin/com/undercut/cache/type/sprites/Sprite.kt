package com.undercut.cache.type.sprites

import com.undercut.cache.Cache
import com.undercut.cache.Index
import java.awt.image.BufferedImage
import java.nio.ByteBuffer

data class SubImageData(
    val x: Int,
    val y: Int,
    val fullWidth: Int,
    val fullHeight: Int,
    val img: BufferedImage
)

class Sprite(val archiveId: Int, load: Boolean = true) {
    var subImages: List<SubImageData> = emptyList()
    var maxWidth: Int = 0
    var maxHeight: Int = 0
    private var loaded = false

    companion object {
        private val sprites = mutableMapOf<Int, Sprite>()

        fun get(archiveId: Int, load: Boolean = true): Sprite {
            return sprites.getOrPut(archiveId) { Sprite(archiveId, load) }.apply {
                if (load && !loaded) load()
            }
        }

        const val ATTACK = 13193
        const val STRENGTH = 13194
        const val DEFENSE = 13195
        const val RANGED = 13196
        const val PRAYER = 13197
        const val MINING = 13198
        const val HITPOINTS = 13199
        const val FIREMAKING = 13200
        const val THIEVING = 13201
        const val HUNTER = 13202
        const val CRAFTING = 13203
        const val FLETCHING = 13204
        const val MAGIC = 13205
        const val RUNECRAFTING = 13206
        const val WOODCUTTING = 13207
        const val SMITHING = 13208
        const val CONSTRUCTION = 13209
        const val HERBLORE = 13210
        const val FISHING = 13211
        const val COOKING = 13212
        const val FARMING = 13213
        const val SLAYER = 13214
        const val DUNGEONEERING = 13215
        const val SUMMONING = 13216
        const val AGILITY = 13217
        const val DIVINATION = 13218
        const val ARCHAEOLOGY = 13220
        const val INVENTION = 13221
        const val NECROMANCY = 30510
        const val NECROMANCY_BLUE = 31336

        const val DIVINATION_HIRES = 20342

        const val HEADBARS = 15227

        const val RS_INTER_BACKGROUND = 18035
        const val RS_INTER_BORDER = 18040
        const val RS_INTER_CORNER_TOP_LEFT = 18039
        const val RS_INTER_CLOSE_BUTTON = 18444
        const val RS_INTER_CLOSE_BUTTON_HOVERED = 18445

    }

    init {
        if (load) load()
    }

    fun load(): Boolean {
        if (!Cache.get().exists(Index.SPRITES.id, archiveId)) {
            println("Sprite $archiveId doesn't exist.")
            loaded = true
            return false
        }

        try {
            val archive = Cache.get().getArchive(Index.SPRITES, archiveId)
            archive.files[0]?.let { file ->
                val buffer = ByteBuffer.wrap(file.data)
                decode(buffer)
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            loaded = true
            return false
        }
        loaded = true
        return true
    }

    private fun decode(buffer: ByteBuffer) {
        buffer.position(buffer.limit() - 2)
        val data = buffer.getShort().toInt() and 0xFFFF
        val format = data shr 15
        val count = data and 0x7FFF

        val spriteImages = mutableListOf<SubImageData>()

        when (format) {
            0 -> {
                buffer.position(buffer.limit() - 7 - count * 8)

                val bigWidth = buffer.getShort().toInt() and 0xFFFF
                val bigHeight = buffer.getShort().toInt() and 0xFFFF
                val paletteCount = buffer.get().toInt() and 0xFF

                val minXs = IntArray(count) { buffer.getShort().toInt() and 0xFFFF }
                val minYs = IntArray(count) { buffer.getShort().toInt() and 0xFFFF }
                val widths = IntArray(count) { buffer.getShort().toInt() and 0xFFFF }
                val heights = IntArray(count) { buffer.getShort().toInt() and 0xFFFF }

                buffer.position(buffer.limit() - 7 - count * 8 - paletteCount * 3)
                val palette = Array(paletteCount) {
                    intArrayOf(
                        buffer.get().toInt() and 0xFF,
                        buffer.get().toInt() and 0xFF,
                        buffer.get().toInt() and 0xFF
                    )
                }

                buffer.position(0)

                for (index in 0 until count) {
                    val width = widths[index]
                    val height = heights[index]
                    val pixelCount = width * height

                    if (pixelCount != 0) {
                        val flags = buffer.get().toInt() and 0xFF
                        val transposed = (flags and 1) != 0
                        val alpha = (flags and 2) != 0

                        val baseIndices = ByteArray(pixelCount)
                        buffer.get(baseIndices)

                        val alphaMask = if (alpha) ByteArray(pixelCount).also { buffer.get(it) } else ByteArray(pixelCount) { 255.toByte() }
                        var img = if (!transposed) BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB) else BufferedImage(height, width, BufferedImage.TYPE_INT_ARGB)

                        for (i in 0 until pixelCount) {
                            val x = i % img.width
                            val y = i / img.width

                            val idx = baseIndices[i].toInt() and 0xFF
                            val alphaValue = alphaMask[i].toInt() and 0xFF

                            val argb = if (idx == 0) {
                                (0 shl 24) or (255 shl 16) or (0 shl 8) or 255
                            } else {
                                val rgb = palette[idx - 1]
                                (alphaValue shl 24) or (rgb[0] shl 16) or (rgb[1] shl 8) or rgb[2]
                            }

                            img.setRGB(x, y, argb)
                        }

                        if (transposed)
                            img = rotateAndFlip(img)

                        spriteImages.add(SubImageData(
                            x = minXs[index],
                            y = minYs[index],
                            fullWidth = bigWidth,
                            fullHeight = bigHeight,
                            img = img
                        ))
                    }
                }

                this.maxWidth = bigWidth
                this.maxHeight = bigHeight
            }

            1 -> {
                buffer.position(0)

                val type = buffer.get().toInt() and 0xFF
                require(type == 0) { "Unknown image type: $type" }

                val flags = buffer.get().toInt() and 0xFF
                val alpha = (flags and 1) != 0

                val width = buffer.getShort().toInt() and 0xFFFF
                val height = buffer.getShort().toInt() and 0xFFFF
                val pixelCount = width * height

                val rgbData = Array(pixelCount) {
                    intArrayOf(
                        buffer.get().toInt() and 0xFF,
                        buffer.get().toInt() and 0xFF,
                        buffer.get().toInt() and 0xFF
                    )
                }

                val alphaMask = if (alpha) ByteArray(pixelCount).also { buffer.get(it) } else ByteArray(pixelCount) { 255.toByte() }

                val img = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)

                for (i in 0 until pixelCount) {
                    val x = i % width
                    val y = i / width

                    val rgb = rgbData[i]
                    val alphaValue = alphaMask[i].toInt() and 0xFF

                    val argb = (alphaValue shl 24) or (rgb[0] shl 16) or (rgb[1] shl 8) or rgb[2]
                    img.setRGB(x, y, argb)
                }

                spriteImages.add(SubImageData(
                    x = 0,
                    y = 0,
                    fullWidth = width,
                    fullHeight = height,
                    img = img
                ))

                this.maxWidth = width
                this.maxHeight = height
            }

            else -> throw IllegalArgumentException("Unknown sprite format: $format")
        }

        subImages = spriteImages
    }

    private fun rotateAndFlip(img: BufferedImage): BufferedImage {
        val rotated = BufferedImage(img.height, img.width, BufferedImage.TYPE_INT_ARGB)
        for (y in 0 until img.height) {
            for (x in 0 until img.width)
                rotated.setRGB(y, img.width - 1 - x, img.getRGB(x, y))
        }

        val flipped = BufferedImage(rotated.width, rotated.height, BufferedImage.TYPE_INT_ARGB)
        for (y in 0 until rotated.height) {
            for (x in 0 until rotated.width)
                flipped.setRGB(x, rotated.height - 1 - y, rotated.getRGB(x, y))
        }

        return flipped
    }

    fun expandSprite(subImg: SubImageData): BufferedImage {
        if (subImg.x == 0 && subImg.y == 0 &&
            subImg.fullWidth == subImg.img.width &&
            subImg.fullHeight == subImg.img.height) {
            return subImg.img
        }

        val img = BufferedImage(subImg.fullWidth, subImg.fullHeight, BufferedImage.TYPE_INT_ARGB)
        val graphics = img.createGraphics()
        graphics.drawImage(subImg.img, subImg.x, subImg.y, null)
        graphics.dispose()
        return img
    }
}

fun BufferedImage.toRGBABytes(): ByteArray {
    val pixels = IntArray(width * height)
    getRGB(0, 0, width, height, pixels, 0, width)

    val rgba = ByteArray(width * height * 4)
    var i = 0

    for (argb in pixels) {
        rgba[i++] = ((argb shr 16) and 0xFF).toByte() // R
        rgba[i++] = ((argb shr 8) and 0xFF).toByte()  // G
        rgba[i++] = (argb and 0xFF).toByte()          // B
        rgba[i++] = ((argb shr 24) and 0xFF).toByte() // A
    }

    return rgba
}