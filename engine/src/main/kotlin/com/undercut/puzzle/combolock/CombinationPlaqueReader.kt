package com.undercut.puzzle.combolock

import com.undercut.script.api.interfaces

/**
 * Reads the lodestone plaque interface (140) — opened by reading the "Plaque" scenery object — and
 * returns the combination: the first letter of each depicted lodestone, left to right.
 *
 * The four icons are components 5..8. Each is a lodestone map sprite; the plaque shows the highlighted
 * variant, which is the base sprite + [HIGHLIGHT_OFFSET] (base sprites start at [BASE_SPRITE]). The
 * sprite→lodestone table is authoritative (mapping supplied from the live game), not guessed.
 */
object CombinationPlaqueReader {
    const val INTERFACE_ID = 140
    private val ICON_COMPONENTS = intArrayOf(5, 6, 7, 8)

    private const val BASE_SPRITE = 22233
    private const val HIGHLIGHT_OFFSET = 46

    // Lodestone names in base-sprite order from 22233. The plaque shows base+46; both map to the name.
    private val NAMES = listOf(
        "Lumbridge", "Varrock", "Falador", "Ardougne", "Taverley", "Burthorpe", "Catherby",
        "Seers' Village", "Port Sarim", "Draynor Village", "Yanille", "Edgeville", "Al Kharid",
        "Bandit Camp", "Lunar Isle", "Canifis", "Eagles' Peak", "Oo'glog", "Fremennik Province",
        "Karamja", "Tirannwn", "Wilderness", "Ashdale",
    )

    private val LETTER_BY_SPRITE: Map<Int, Char> = buildMap {
        NAMES.forEachIndexed { i, name ->
            val letter = name.first().uppercaseChar()
            put(BASE_SPRITE + i, letter)
            put(BASE_SPRITE + HIGHLIGHT_OFFSET + i, letter)
        }
        // Prifddinas uses its own sprite sheet (base 24249, highlighted 24250).
        put(24249, 'P')
        put(24250, 'P')
    }

    /** First letter of the lodestone for an icon sprite (base or highlighted variant), or null if unknown. */
    fun letterForSprite(spriteId: Int): Char? = LETTER_BY_SPRITE[spriteId]

    fun isOpen(): Boolean = runCatching { interfaces.isOpen(INTERFACE_ID) }.getOrDefault(false)

    fun read(): String? {
        if (!isOpen()) return null
        val letters = CharArray(ICON_COMPONENTS.size)
        for (i in ICON_COMPONENTS.indices) {
            val sprite = runCatching { interfaces.getComponent(INTERFACE_ID, ICON_COMPONENTS[i])?.spriteId }.getOrNull() ?: return null
            letters[i] = letterForSprite(sprite) ?: return null
        }
        return String(letters)
    }
}
