package com.undercut.game.hooks.impl

import com.undercut.game.hooks.Hook
import com.undercut.game.hooks.HookManager
import com.undercut.game.nxt.OFunctions
import com.undercut.pathfinder.DynamicRegionCollision
import java.lang.foreign.MemorySegment

/**
 * Hooks FUN_007d6df0 (MapSquare::Init / BUILD_AREA_INIT).
 *
 * Called via direct CALL from both REBUILD_NORMAL (0x001bff3c) and
 * REBUILD_REGION (0x001bfcd6) handlers. This fires for every build area
 * initialization — including incremental updates like opening dungeoneering doors.
 *
 * Parameters (System V AMD64):
 *   RDI = structPtr: pointer to 0x68-byte build area entry being initialized
 *   ESI = mapSquareId: the build area's map square ID (g4 BE from packet)
 *
 * Returns: void
 */
object BuildAreaHook {

    @Volatile
    var dirty = false
        private set

    fun consumeDirty(): Boolean {
        if (!dirty) return false
        dirty = false
        return true
    }

    @JvmStatic
    @Hook(OFunctions.BUILD_AREA_INIT)
    fun buildAreaInitHook(structPtr: MemorySegment, mapSquareId: Int) {
        println("[BuildAreaHook] BUILD_AREA_INIT fired: mapSquareId=0x${mapSquareId.toString(16)} (${mapSquareId}) struct=0x${structPtr.address().toString(16)}")
        System.out.flush()

        // Call original
        HookManager.trampoline(::buildAreaInitHook.name)
            .invoke(structPtr, mapSquareId)

        // Mark build areas as changed — DynamicRegionCollision will re-read on next tick
        dirty = true
        DynamicRegionCollision.markDirty()
    }
}
