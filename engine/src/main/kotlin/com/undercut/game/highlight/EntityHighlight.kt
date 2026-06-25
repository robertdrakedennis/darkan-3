package com.undercut.game.highlight

import com.undercut.game.nxt.OEntity
import com.undercut.game.nxt.entity.Entity
import com.undercut.game.nxt.entity.PathingEntity
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout.ADDRESS
import java.lang.foreign.ValueLayout.JAVA_BYTE
import java.lang.foreign.ValueLayout.JAVA_FLOAT

/**
 * Drives the engine's built-in entity highlight system by writing directly to
 * the render-model slot the shader reads — exactly what
 * `jag::game::PathingEntity::UpdateHighlight`'s IMPORTANT (combat-target) path
 * does. We bypass the CATEGORY-path entirely (and its global category table)
 * so our writes never leak to other entities the engine has tagged with a
 * category index.
 *
 * The render-model layout is the same across entity subclasses (per Offsets.kt:
 * "same layout as ORenderModel: transform@+0x30, meshComponents@+0x288"). Only
 * the offset to the render-model pointer from the entity differs:
 *
 *   PathingEntity (NPC/Player)        ptr @ entity + 0xC58
 *   CombinedLocation                  ptr @ entity + 0x958
 *   CombinedLocationSection           parent's @0x958, else direct @ entity + 0x270
 *   Location                          ptr @ entity + 0x60
 *
 * Ordering matters: writes must happen AFTER the main-logic trampoline (so the
 * engine's own per-frame UpdateHighlight is done) — see HighlightTick.
 *
 *     EntityHighlight.apply(someNpc, color = 0x00FFFF, mode = Mode.OUTLINE, scale = 20)
 *     EntityHighlight.clear(someNpc)
 */
object EntityHighlight {

    // Render-model fields written by the IMPORTANT path (verified via disassembly
    // of UpdateHighlight @ 0x001bbb90).
    private const val RM_COLOR_R = 0x100L
    private const val RM_COLOR_G = 0x104L
    private const val RM_COLOR_B = 0x108L
    private const val RM_BASE_ALPHA = 0x10CL
    private const val RM_STRENGTH = 0x110L
    private const val RM_FACTOR = 0x114L
    private const val RM_MODE = 0x134L

    // Real Linux x86-64 user-space allocations are at minimum in the megabyte range
    // (typically 0x55_0000_0000+ or 0x7f_0000_0000+). Anything below 1 MB is a small
    // int garbage value living at a stale offset — refuse to deref.
    private const val MIN_VALID_ADDR = 0x100000L

    enum class Mode(val byte: Byte) {
        OFF(0), SOLID(1), OUTLINE(2), GLOW(3);
        companion object {
            fun from(value: Int): Mode = values().firstOrNull { it.byte.toInt() == value } ?: OUTLINE
        }
    }

    /**
     * Light [entity] up with the given color/mode/scale. Color is a packed
     * 0xRRGGBB int (the high byte is ignored). [scale] is the 0..255 intensity
     * byte the engine's renderer reads; default 20 — anything above ~32 is
     * usually overpowering.
     */
    fun apply(entity: Entity, color: Int, mode: Mode = Mode.OUTLINE, scale: Int = 20) {
        val rm = renderModel(entity) ?: return
        val r = ((color ushr 16) and 0xFF) / 255f
        val g = ((color ushr 8) and 0xFF) / 255f
        val b = (color and 0xFF) / 255f
        rm.set(JAVA_FLOAT, RM_COLOR_R, r)
        rm.set(JAVA_FLOAT, RM_COLOR_G, g)
        rm.set(JAVA_FLOAT, RM_COLOR_B, b)
        rm.set(JAVA_FLOAT, RM_BASE_ALPHA, 1.0f)
        rm.set(JAVA_FLOAT, RM_STRENGTH, scale.coerceIn(0, 255).toFloat())
        rm.set(JAVA_FLOAT, RM_FACTOR, 1.0f)
        rm.set(JAVA_BYTE, RM_MODE, mode.byte)
    }

    /** Float-channel variant for callers that already work with normalized RGB. */
    fun apply(entity: Entity, r: Float, g: Float, b: Float, mode: Mode = Mode.OUTLINE, scale: Int = 20) {
        val rm = renderModel(entity) ?: return
        rm.set(JAVA_FLOAT, RM_COLOR_R, r.coerceIn(0f, 1f))
        rm.set(JAVA_FLOAT, RM_COLOR_G, g.coerceIn(0f, 1f))
        rm.set(JAVA_FLOAT, RM_COLOR_B, b.coerceIn(0f, 1f))
        rm.set(JAVA_FLOAT, RM_BASE_ALPHA, 1.0f)
        rm.set(JAVA_FLOAT, RM_STRENGTH, scale.coerceIn(0, 255).toFloat())
        rm.set(JAVA_FLOAT, RM_FACTOR, 1.0f)
        rm.set(JAVA_BYTE, RM_MODE, mode.byte)
    }

    /** Clear any highlight applied to [entity]. */
    fun clear(entity: Entity) {
        val rm = renderModel(entity) ?: return
        rm.set(JAVA_FLOAT, RM_STRENGTH, 0f)
        rm.set(JAVA_FLOAT, RM_BASE_ALPHA, 0f)
        rm.set(JAVA_BYTE, RM_MODE, 0.toByte())
    }

    /** Low-level: clear by raw render-model address (used when the entity
     *  wrapper is gone but we still know the pointer we wrote to last tick). */
    fun clearByRenderModelAddr(rmAddr: Long) {
        if (rmAddr < MIN_VALID_ADDR) return
        val rm = MemorySegment.ofAddress(rmAddr).reinterpret(0x200L)
        rm.set(JAVA_FLOAT, RM_STRENGTH, 0f)
        rm.set(JAVA_FLOAT, RM_BASE_ALPHA, 0f)
        rm.set(JAVA_BYTE, RM_MODE, 0.toByte())
    }

    /**
     * The render-model address for [entity], or 0 if unknown / unallocated.
     *
     * Only PathingEntity (NPC/Player) has a verified persistent render-model pointer at
     * `OEntity.RENDER_MODEL = 0xC58` with the highlight color/strength/mode slots the renderer
     * reads. Locations use the [setLocationImportantFlag] path instead (the binary's IMPORTANT
     * category mechanism). ItemStacks/CombinedLocation* have no per-instance highlight path
     * in 948-2-2 and stay on the UI tile-overlay fallback.
     */
    fun renderModelAddr(entity: Entity): Long {
        if (entity !is PathingEntity) return 0L
        val addr = entity.ptr.get(ADDRESS, OEntity.RENDER_MODEL).address()
        return if (addr >= MIN_VALID_ADDR) addr else 0L
    }

    private fun renderModel(entity: Entity): MemorySegment? {
        val addr = renderModelAddr(entity)
        if (addr < MIN_VALID_ADDR) return null
        return MemorySegment.ofAddress(addr).reinterpret(0x200L)
    }

}
