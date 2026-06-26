package com.undercut.script.impl.gibson.farming

import world.gregs.voidps.type.Tile
import com.undercut.script.api.varps

enum class CactusPatch(
    val patchName: String,
    val locationName: String,
    val location: Tile,
    val varbitId: Int,
    val questId: Int // Quest requirement (0 means no quest required)
) {
    AL_KHARID("Cactus patch", "Al Kharid", Tile(3313, 3204, 0), 18416, 0);

    /**
     * Detects the current state of the cactus patch.
     * @return A string representing the current state of the patch.
     */
    fun detectPatchState(): String {
        val varValue = varps.getVarBit(this.varbitId)

        // Patch state mapping
        return when {
            varValue in 0..2 -> "Needs to be raked"
            varValue == 3 -> "Already raked"
            varValue in 102..108 -> "Growing"
            varValue == 125 -> "Ready to be picked"
            varValue == 112 -> "Pick"
            varValue == 109 -> "Clear"
            else -> "Unknown state: $varValue"
        }
    }
}
