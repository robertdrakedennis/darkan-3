package com.undercut.puzzle.combolock

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CombinationPlaqueReaderTest {

    @Test
    fun liveplaqueIconsResolveToFCKP() {
        // The four icon sprites read live from interface 140, left to right.
        assertEquals('F', CombinationPlaqueReader.letterForSprite(22281)) // Falador
        assertEquals('C', CombinationPlaqueReader.letterForSprite(22294)) // Canifis
        assertEquals('K', CombinationPlaqueReader.letterForSprite(22298)) // Karamja
        assertEquals('P', CombinationPlaqueReader.letterForSprite(24250)) // Prifddinas
    }

    @Test
    fun baseAndHighlightedSpritesBothResolve() {
        // Base sprite (22233 = Lumbridge) and its highlighted variant (+46) map to the same letter.
        assertEquals('L', CombinationPlaqueReader.letterForSprite(22233))
        assertEquals('L', CombinationPlaqueReader.letterForSprite(22279))
        assertEquals('V', CombinationPlaqueReader.letterForSprite(22234))
        assertEquals('A', CombinationPlaqueReader.letterForSprite(22301)) // Ashdale (last)
    }

    @Test
    fun unknownSpriteIsNull() {
        assertNull(CombinationPlaqueReader.letterForSprite(0))
        assertNull(CombinationPlaqueReader.letterForSprite(12345))
    }
}
