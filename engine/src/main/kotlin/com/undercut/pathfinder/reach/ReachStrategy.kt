package com.undercut.pathfinder.reach

interface ReachStrategy {

    fun reached(
        flags: Array<IntArray?>,
        x: Int,
        y: Int,
        z: Int,
        destX: Int,
        destY: Int,
        destWidth: Int,
        destHeight: Int,
        srcSize: Int,
        rotation: Int,
        shape: Int,
        accessBitMask: Int,
    ): Boolean
}
