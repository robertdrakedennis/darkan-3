package com.undercut.ui.backend.native

import java.lang.ref.WeakReference
import java.nio.file.Files
import java.nio.file.Paths
import java.io.InputStream
import java.util.Random
import java.util.concurrent.ConcurrentHashMap
import javax.imageio.ImageIO

object ImageHelper {
    data class DecodedImage(val pixels: ByteArray, val width: Int, val height: Int)

    private val textureCache = ConcurrentHashMap<String, WeakReference<ImGuiTexture>>()

    fun getNoiseTexture(): ImGuiTexture {
        val key = "__imghelper_noise_128__"
        val cached = textureCache[key]?.get()
        if (cached != null) return cached

        val width = 128
        val height = 128
        val pixels = ByteArray(width * height * 4)

        // Subtle white noise encoded via alpha channel so it tints nicely
        // R=G=B=255, A in [0..64) for gentle grain
        val rnd = Random(1337L)
        var i = 0
        while (i < pixels.size) {
            pixels[i++] = 0xFF.toByte() // R
            pixels[i++] = 0xFF.toByte() // G
            pixels[i++] = 0xFF.toByte() // B
            pixels[i++] = (rnd.nextInt(64)).toByte() // A
        }

        val tex = NativeBridge.createTextureFromRGBA(pixels, width, height)
            ?: run {
                // Fallback to a tiny static texture if creation fails
                val tiny = staticTestImageRGBA()
                NativeBridge.createTextureFromRGBA(tiny.pixels, tiny.width, tiny.height)
            }
            ?: error("Failed to create noise texture")

        textureCache[key] = WeakReference(tex)
        return tex
    }

    /**
     * Returns a tiny 2x2 RGBA test image with distinct quadrant colors:
     * Row 0: [Red][Green]
     * Row 1: [Blue][White]
     */
    fun staticTestImageRGBA(): DecodedImage {
        val w = 2
        val h = 2
        val p = ByteArray(w * h * 4)
        var i = 0
        // (0,0) Red
        p[i++] = 255.toByte(); p[i++] = 0.toByte();   p[i++] = 0.toByte();   p[i++] = 255.toByte()
        // (1,0) Green
        p[i++] = 0.toByte();   p[i++] = 255.toByte(); p[i++] = 0.toByte();   p[i++] = 255.toByte()
        // (0,1) Blue
        p[i++] = 0.toByte();   p[i++] = 0.toByte();   p[i++] = 255.toByte(); p[i++] = 255.toByte()
        // (1,1) White
        p[i++] = 255.toByte(); p[i++] = 255.toByte(); p[i++] = 255.toByte(); p[i++] = 255.toByte()
        return DecodedImage(p, w, h)
    }

    /**
     * Convenience: create an ImGui texture for the tiny static test image.
     * Uses a stable cache key so it only allocates once.
     */
    fun loadStaticTestTexture(): ImGuiTexture? {
        val test = staticTestImageRGBA()
        return loadTextureFromRGBA(test.pixels, test.width, test.height, cacheKey = "__imghelper_static_test__")
    }

    /**
     * Create an ImGui texture from raw RGBA pixel data.
     * Optionally provide a cacheKey to enable reuse via the internal cache (similar to loadTexture).
     * If cacheKey is null, no caching is performed. (Make sure to create outside of render method)
     */
    fun loadTextureFromRGBA(pixels: ByteArray, width: Int, height: Int, cacheKey: String? = null): ImGuiTexture? {
        if (cacheKey != null) {
            val ref = textureCache[cacheKey]
            val cached = ref?.get()
            if (cached != null) return cached
            if (ref != null) {
                textureCache.remove(cacheKey, ref)
            }
        }

        val tex = NativeBridge.createTextureFromRGBA(pixels, width, height)
        if (tex != null && cacheKey != null) {
            textureCache[cacheKey] = WeakReference(tex)
        }
        return tex
    }

    fun loadTexture(pathOrResource: String): ImGuiTexture? {
        val ref = textureCache[pathOrResource]
        val cached = ref?.get()
        if (cached != null) return cached
        if (ref != null) {
            textureCache.remove(pathOrResource, ref)
        }

        val decoded = decodeImageToRGBA(pathOrResource) ?: return null
        val tex = NativeBridge.createTextureFromRGBA(decoded.pixels, decoded.width, decoded.height)
        if (tex != null) {
            textureCache[pathOrResource] = WeakReference(tex)
        }
        return tex
    }

    fun forgetTextureById(textureId: Long) {
        if (textureId == 0L) return
        textureCache.entries.removeIf { entry ->
            val tex = entry.value.get()
            tex == null || tex.id == textureId
        }
    }

    private fun decodeImageToRGBA(original: String): DecodedImage? {
        // Try as filesystem path first
        try {
            val p = Paths.get(original)
            if (Files.exists(p)) {
                Files.newInputStream(p).use { input ->
                    return decodeStream(input)
                }
            }
        } catch (_: Throwable) {}

        // Prepare candidate resource names
        val candidates = ArrayList<String>(4)
        if (original.contains("!")) {
            val afterBang = original.substringAfter('!')
            val jarRes = if (afterBang.startsWith('/')) afterBang else "/$afterBang"
            candidates.add(jarRes)
        }
        candidates.add(if (original.startsWith('/')) original else "/$original")
        candidates.add(original.removePrefix("/"))

        val loader = ImageHelper::class.java.classLoader
        for (cand in candidates) {
            val url = ImageHelper::class.java.getResource(cand) ?: loader?.getResource(cand.removePrefix("/"))
            if (url != null) {
                try {
                    url.openStream().use { input ->
                        return decodeStream(input)
                    }
                } catch (_: Throwable) {}
            }
        }
        return null
    }

    private fun decodeStream(input: InputStream): DecodedImage? {
        return try {
            val image = ImageIO.read(input) ?: return null
            val w = image.width
            val h = image.height
            // Extract into RGBA bytes
            val argb = IntArray(w * h)
            image.getRGB(0, 0, w, h, argb, 0, w)
            val out = ByteArray(w * h * 4)
            var i = 0
            var j = 0
            while (i < argb.size) {
                val v = argb[i]
                // ARGB -> RGBA
                out[j]     = ((v ushr 16) and 0xFF).toByte() // R
                out[j + 1] = ((v ushr 8) and 0xFF).toByte()  // G
                out[j + 2] = (v and 0xFF).toByte()           // B
                out[j + 3] = ((v ushr 24) and 0xFF).toByte() // A
                i++
                j += 4
            }
            DecodedImage(out, w, h)
        } catch (_: Throwable) {
            null
        }
    }

    // Expose decoding for managed texture creation without polluting cache semantics
    fun decode(original: String): DecodedImage? = decodeImageToRGBA(original)
}
