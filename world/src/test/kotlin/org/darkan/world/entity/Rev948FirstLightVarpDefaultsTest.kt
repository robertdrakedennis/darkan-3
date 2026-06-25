package org.darkan.world.entity

import kotlin.test.Test
import kotlin.test.assertEquals

class Rev948FirstLightVarpDefaultsTest {
    @Test
    fun `first-light defaults load decoded production baseline`() {
        val defaults = Rev948FirstLightVarpDefaults.hudDefaults()

        assertEquals(1604, defaults.size)
        assertEquals(-1807744892L, defaults[3])
        assertEquals(-1L, defaults[27])
        assertEquals(2L, defaults[45])
        assertEquals(-1L, defaults[11792])
        assertEquals(12792912L, defaults[12863])
    }
}
