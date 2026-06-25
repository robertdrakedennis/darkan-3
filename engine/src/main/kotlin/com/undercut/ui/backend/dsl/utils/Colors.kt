package com.undercut.ui.backend.dsl.utils

/**
 * ImGui color utilities and constants
 */
object ImGuiColors {
    // Common colors in ImGui IM_COL32 packing (ABGR: 0xAABBGGRR)
    const val TRANSPARENT = 0x00000000
    
    val BACKGROUND_PRIMARY = hex("#1A1410")
    val BACKGROUND_SECONDARY = hex("#221A12")
    val BACKGROUND_TERTIARY = hex("#2A2018")
    val BACKGROUND_HOVER = hex("#3A2E22")
    val BACKGROUND_ACTIVE = hex("#4A3A2A")

    val TEXT_PRIMARY = hex("#D4C4A0")
    val TEXT_SECONDARY = hex("#B8A880")
    val TEXT_DISABLED = hex("#6E6048")
    val TEXT_ACCENT = hex("#E8D090")

    val ACCENT_PRIMARY = hex("#C89830")
    val ACCENT_PRIMARY_HOVER = hex("#DAAC40")
    val ACCENT_PRIMARY_ACTIVE = hex("#B08828")
    val ACCENT_SECONDARY = hex("#E0B840")
    val ACCENT_SUCCESS = hex("#70A840")
    val ACCENT_WARNING = hex("#D4A020")
    val ACCENT_ERROR = hex("#C04030")

    val BORDER_DEFAULT = hex("#4A3828")
    val BORDER_ACTIVE = hex("#C89830")
    val BORDER_FOCUS = hex("#DAAC40")
    
    val WHITE = rgba(255, 255, 255, 255)
    val BLACK = rgba(0, 0, 0, 255)
    val RED = ACCENT_ERROR
    val GREEN = ACCENT_SUCCESS
    val BLUE = hex("#0099FF")
    val YELLOW = ACCENT_WARNING
    val CYAN = hex("#00FFFF")
    val MAGENTA = hex("#FF00FF")
    
    val GRAY_LIGHTEST = hex("#5A4A38")
    val GRAY_LIGHTER = hex("#4A3A28")
    val GRAY_LIGHT = hex("#3A2E22")
    val GRAY = hex("#2A2018")
    val GRAY_DARK = hex("#1A1410")
    val GRAY_DARKER = hex("#120E0A")
    val GRAY_DARKEST = hex("#0A0804")

    val BUTTON_DEFAULT = hex("#3A2C1C")
    val BUTTON_HOVER = hex("#4A3C28")
    val BUTTON_ACTIVE = hex("#5A4830")
    val BUTTON_PRIMARY = hex("#6A5020")
    val BUTTON_PRIMARY_HOVER = hex("#7A6028")
    val BUTTON_PRIMARY_ACTIVE = hex("#5A4418")

    val TAB_ACTIVE = hex("#D4C4A0")
    val TAB_HOVER = hex("#4A3A28")
    val TAB_INACTIVE = hex("#2A2018")

    val SCROLLBAR_BG = hex("#1A1410")
    val SCROLLBAR_GRAB = hex("#5A4830")
    val SCROLLBAR_GRAB_HOVER = hex("#6A5838")
    val SCROLLBAR_GRAB_ACTIVE = hex("#7A6840")
    
    val ORANGE = hex("#D08040")
    val PURPLE = hex("#B070B0")
    val BROWN = hex("#C09060")
    val PINK = hex("#D080A0")
    
    val OVERLAY_LIGHT = withAlpha(WHITE, 20)
    val OVERLAY_DARK = withAlpha(BLACK, 180)
    val ACCENT_PRIMARY_SEMI = withAlpha(ACCENT_PRIMARY, 128)
    val ACCENT_SUCCESS_SEMI = withAlpha(ACCENT_SUCCESS, 128)
    val ACCENT_WARNING_SEMI = withAlpha(ACCENT_WARNING, 128)
    val ACCENT_ERROR_SEMI = withAlpha(ACCENT_ERROR, 128)
    
    val ORANGE_SEMI = withAlpha(ORANGE, 128)
    val WHITE_SEMI = withAlpha(WHITE, 128)
    val BLACK_SEMI = withAlpha(BLACK, 128)
    val RED_SEMI = withAlpha(RED, 128)
    val GREEN_SEMI = withAlpha(GREEN, 128)
    val BLUE_SEMI = withAlpha(BLUE, 128)
    
    /**
     * Create ARGB color from individual components
     */
    fun rgba(r: Int, g: Int, b: Int, a: Int = 255): Int {
        val safeR = r.coerceIn(0, 255)
        val safeG = g.coerceIn(0, 255)
        val safeB = b.coerceIn(0, 255)
        val safeA = a.coerceIn(0, 255)
        return (safeA shl 24) or (safeB shl 16) or (safeG shl 8) or safeR
    }
    
    /**
     * Create ARGB color from float components (0.0 - 1.0)
     */
    fun rgba(r: Float, g: Float, b: Float, a: Float = 1f): Int {
        val intR = (r.coerceIn(0f, 1f) * 255).toInt()
        val intG = (g.coerceIn(0f, 1f) * 255).toInt()
        val intB = (b.coerceIn(0f, 1f) * 255).toInt()
        val intA = (a.coerceIn(0f, 1f) * 255).toInt()
        return rgba(intR, intG, intB, intA)
    }
    
    /**
     * Create color from hex string (supports #RGB, #RRGGBB, #AARRGGBB)
     */
    fun hex(hex: String): Int {
        val cleanHex = hex.removePrefix("#")
        return when (cleanHex.length) {
            3 -> {
                val r = cleanHex[0].toString().repeat(2).toInt(16)
                val g = cleanHex[1].toString().repeat(2).toInt(16)
                val b = cleanHex[2].toString().repeat(2).toInt(16)
                rgba(r, g, b, 255)
            }
            6 -> {
                val r = cleanHex.substring(0, 2).toInt(16)
                val g = cleanHex.substring(2, 4).toInt(16)
                val b = cleanHex.substring(4, 6).toInt(16)
                rgba(r, g, b, 255)
            }
            8 -> {
                val r = cleanHex.substring(0, 2).toInt(16)
                val g = cleanHex.substring(2, 4).toInt(16)
                val b = cleanHex.substring(4, 6).toInt(16)
                val a = cleanHex.substring(6, 8).toInt(16)
                rgba(r, g, b, a)
            }
            else -> WHITE
        }
    }
    
    /**
     * Adjust alpha of existing color
     */
    fun withAlpha(color: Int, alpha: Int): Int {
        val safeAlpha = alpha.coerceIn(0, 255)
        return (color and 0x00FFFFFF) or (safeAlpha shl 24)
    }
    
    /**
     * Adjust alpha of existing color (0.0 - 1.0)
     */
    fun withAlpha(color: Int, alpha: Float): Int {
        val intAlpha = (alpha.coerceIn(0f, 1f) * 255).toInt()
        return withAlpha(color, intAlpha)
    }
    
    /**
     * Get a modern success color (green)
     */
    fun success(): Int = ACCENT_SUCCESS
    
    /**
     * Get a modern warning color (amber)
     */
    fun warning(): Int = ACCENT_WARNING
    
    /**
     * Get a modern error color (red)
     */
    fun error(): Int = ACCENT_ERROR
    
    /**
     * Get the primary accent color (sky blue)
     */
    fun primary(): Int = ACCENT_PRIMARY
    
    /**
     * Get modern text color
     */
    fun text(): Int = TEXT_PRIMARY
    
    /**
     * Get modern secondary text color
     */
    fun textSecondary(): Int = TEXT_SECONDARY
    
    /**
     * Get modern disabled text color
     */
    fun textDisabled(): Int = TEXT_DISABLED
    
    /**
     * Get modern background color
     */
    fun background(): Int = BACKGROUND_PRIMARY
    
    /**
     * Get modern surface color (for cards, panels)
     */
    fun surface(): Int = BACKGROUND_TERTIARY
    
    /**
     * Get a color based on a status or condition
     */
    fun statusColor(isSuccess: Boolean = false, isWarning: Boolean = false, isError: Boolean = false): Int {
        return when {
            isError -> error()
            isWarning -> warning()
            isSuccess -> success()
            else -> primary()
        }
    }
    
    /**
     * Get a semi-transparent overlay color
     */
    fun overlay(isDark: Boolean = true): Int {
        return if (isDark) OVERLAY_DARK else OVERLAY_LIGHT
    }
}