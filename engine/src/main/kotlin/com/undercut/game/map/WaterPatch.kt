package com.undercut.game.map

data class WaterPatch(
    val posX: Int,
    val posZ: Int,
    val posY: Int,
    val extentX: Int,
    val extentZ: Int,
    val qx: Float,
    val qy: Float,
    val qz: Float,
    val qw: Float,
    val waterTypeId: Int,
    val scale1: Int,
    val scale2: Int,
    val waterMeshId: Int
)
