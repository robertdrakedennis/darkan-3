package com.undercut.cache.type.npcs

import com.undercut.cache.Cache
import com.undercut.cache.model.MovementType
import com.undercut.cache.type.params.Params

class NPCType(
    var id: Int = 0,
    var params: Params = Params(),
    var modelIds: IntArray? = null,
    var name: String = "",
    var size: Int = 1,
    var options: Array<String?> = arrayOfNulls(5),
    var originalColors: ShortArray? = null,
    var modifiedColors: ShortArray? = null,
    var originalTextures: ShortArray? = null,
    var modifiedTextures: ShortArray? = null,
    var recolourPalette: ByteArray? = null,
    var headModels: IntArray? = null,
    var drawMapdot: Boolean = false,
    var combatLevel: Int = 0,
    var resizeX: Int = 0,
    var resizeY: Int = 0,
    var aBool4904: Boolean = false,
    var ambient: Int = 0,
    var contrast: Int = 0,
    var headIcons: Map<Int, Int>? = null,
    var rotation: Int = 0,
    var varpBit: Int = 0,
    var varp: Int = 0,
    var transformTo: IntArray? = null,
    var visible: Boolean = true,
    var isClickable: Boolean = true,
    var animateIdle: Boolean = true,
    var aShort4874: Short = 0,
    var aShort4897: Short = 0,
    var aByte4883: Byte = 0,
    var aByte4899: Byte = 0,
    var walkMask: Byte = 0,
    var modelTranslation: Array<IntArray>? = null,
    var height: Int = 0,
    var respawnDirection: Byte = 0,
    var basId: Int = 0,
    var movementType: MovementType? = null,
    var walkingAnimation: Int = 0,
    var rotate180Animation: Int = 0,
    var rotate90RightAnimation: Int = 0,
    var rotate90LeftAnimation: Int = 0,
    var specialByte: Int = 0,
    var anInt4875: Int = 0,
    var anInt4873: Int = 0,
    var anInt4854: Int = 0,
    var anInt4861: Int = 0,
    var headModelId1: Int = 0,
    var standAnim: Int = 0,
    var anInt4909: Int = 0,
    var aBool4884: Boolean = false,
    var mapIcon: Int = 0,
    var aBool4890: Boolean = false,
    var membersOptions: Array<String?> = arrayOfNulls(5),
    var aByte4868: Byte = 0,
    var aByte4869: Byte = 0,
    var aByte4905: Byte = 0,
    var aByte4871: Byte = 0,
    var aByte4916: Int = 0,
    var quests: IntArray? = null,
    var aBool4872: Boolean = false,
    var anInt4917: Int = 0,
    var anInt4911: Int = 0,
    var anInt4919: Int = 0,
    var anInt4913: Int = 0,
    var anInt4908: Int = 0,
    var aBool4920: Boolean = true,
    var actionCursors: IntArray? = null,
    var aabbBounds: IntArray? = null
) {
    companion object {
        private val parser = NPCTypeParser()

        fun getParser(): NPCTypeParser = parser

        fun get(id: Int): NPCType {
            val def = parser.get(Cache.get(), id)
            return def ?: NPCType(id = id)
        }

        //TODO uncomment once var reading works
//        fun get(id: Int, vars: VarManager?): NPCType {
//            var def = get(id)
//            def = def?.let { get(it.getIdForPlayer(vars)) } ?: NPCType(id = id)
//            return def
//        }
    }

    //TODO once var reading works
//    fun getIdForPlayer(vars: VarManager?): Int {
//        if (transformTo.isNullOrEmpty()) return id
//
//        val index = when {
//            varpBit != -1 -> vars?.getVarBit(varpBit) ?: -1
//            varp != -1 -> vars?.getVar(varp) ?: -1
//            else -> -1
//        }
//
//        return if (index in 0 until transformTo!!.size - 1 && transformTo!![index] != -1)
//            transformTo!![index]
//        else
//            transformTo!![transformTo!!.lastIndex]
//    }

    fun getOpIdForName(opName: String): Int {
        return options.indexOfFirst { it.equals(opName, ignoreCase = true) }.takeIf { it >= 0 } ?: -1
    }

    fun getOp(optionId: Int): String {
        return options.getOrNull(optionId) ?: "null"
    }

    fun containsOp(i: Int, option: String): Boolean {
        return options.getOrNull(i)?.equals(option, ignoreCase = true) == true
    }

    fun containsOp(option: String): Boolean {
        return options.any { it?.equals(option, ignoreCase = true) == true }
    }
}