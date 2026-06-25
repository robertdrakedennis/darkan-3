package com.undercut.traversal.nodes

import com.undercut.game.interfaces.IFSlot
import com.undercut.profiling.PlayerProfiles
import com.undercut.script.Script
import com.undercut.script.api.localPlayer
import com.undercut.traversal.TraversalNode
import com.undercut.util.Area
import com.undercut.util.gaussian

class IFSlotNode(
    private val ifSlot: IFSlot,
    private val optionNum: Int = 1,
    private var destination: Area? = null,
    private var customReached: (() -> Boolean)? = null
) : TraversalNode() {
    private var nextClick: Long = 0

    override suspend fun process(script: Script): Boolean {
        if (System.currentTimeMillis() < nextClick) return true
        val success = if(optionNum!=0)ifSlot.click(optionNum) else ifSlot.dialogueContinue()
        if (success) {
            script.delayUntil(15000) { !localPlayer.isMoving }
            nextClick = System.currentTimeMillis() + gaussian(PlayerProfiles.get().walkPathClickTime, PlayerProfiles.get().walkPathClickTime / 2)
            return true
        }
        return false
    }

    override fun reached(script: Script) = (customReached?.invoke() ?: destination?.inside(localPlayer.tile)) == true

    override fun copy(): TraversalNode = IFSlotNode(this.ifSlot, this.optionNum, this.destination, this.customReached)

    override fun toString() = "[${this.ifSlot}]"
}