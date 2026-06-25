package com.undercut.traversal.nodes

import com.undercut.profiling.PlayerProfiles
import com.undercut.script.Script
import com.undercut.script.api.Lodestone
import com.undercut.script.api.localPlayer
import com.undercut.script.api.useLodestone
import com.undercut.traversal.TraversalNode
import com.undercut.util.gaussian

class LodestoneNode(
    private val lodestone: Lodestone,
    private val customReached: (() -> Boolean)? = null
) : TraversalNode() {
    
    private var nextClick: Long = 0
    private var teleportAttempted: Boolean = false

    override suspend fun process(script: Script): Boolean {
        if (System.currentTimeMillis() < nextClick) return true
        
        // Check if we're already at the target location
        if (reached(script)) return false
        
        script.useLodestone(lodestone)
        nextClick = System.currentTimeMillis() + gaussian(PlayerProfiles.get().walkPathClickTime, PlayerProfiles.get().walkPathClickTime / 2)

        // Wait for teleportation to complete
        script.delay(100, 200)
        return true
    }

    override fun reached(script: Script): Boolean {
        return customReached?.invoke() ?: 
            (localPlayer.tile.getDistance(lodestone.tile) <= 10 && !localPlayer.isAnimating)
    }

    override fun copy(): TraversalNode = LodestoneNode(lodestone, customReached)

    override fun toString(): String = "[Lodestone: ${lodestone.name}]"
} 