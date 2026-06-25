@file:Suppress("unused")

package com.undercut.pathfinder.collision

object CollisionStrategyType {
    val NORMAL = NormalBlockFlagCollision()
    val WATER = BlockedFlagCollision()
    val FLY = LineOfSightBlockFlagCollision()
    val INDOOR = IndoorsFlagCollision()
    val OUTDOOR = OutdoorsFlagCollision()
    val NOCLIP = NoClipCollision()
}
