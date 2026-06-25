package com.undercut.cache.type.objects

import com.undercut.cache.Cache
import com.undercut.cache.type.maps.ObjectShape
import com.undercut.cache.type.params.Params

class ObjectType(
    var id: Int = -1,
    var shapes: Array<ObjectShape>? = null,
    var modelIds: Array<IntArray>? = null,
    var name: String = "",
    var sizeX: Int = 1,
    var sizeY: Int = 1,
    var clipType: Int = 2,
    var blocks: Boolean = true,
    var interactable: Int = 0,
    var groundContoured: Byte = 0,
    var delayShading: Boolean = false,
    var occludes: Int = 0,
    var animations: IntArray? = null,
    var decorDisplacement: Int = 0,
    var ambient: Int = 0,
    var contrast: Int = 0,
    var options: Array<String?> = arrayOfNulls(5),
    var originalColors: ShortArray? = null,
    var modifiedColors: ShortArray? = null,
    var originalTextures: ShortArray? = null,
    var modifiedTextures: ShortArray? = null,
    var aByteArray5641: ByteArray? = null,
    var inverted: Boolean = false,
    var castsShadow: Boolean = false,
    var scaleX: Int = 0,
    var scaleY: Int = 0,
    var scaleZ: Int = 0,
    var accessBlockFlag: Int = 0,
    var offsetX: Int = 0,
    var offsetY: Int = 0,
    var offsetZ: Int = 0,
    var obstructsGround: Boolean = false,
    var supportsItems: Int = 0,
    var ignoreAltClip: Boolean = false,
    var varpBit: Int = -1,
    var varp: Int = -1,
    var transformTo: IntArray? = null,
    var ambientSoundId: Int = 0,
    var ambientSoundHearDistance: Int = 0,
    var anInt5667: Int = 0,
    var anInt5698: Int = 0,
    var audioTracks: IntArray? = null,
    var anInt5654: Int = 0,
    var hidden: Boolean = false,
    var aBool5703: Boolean = true,
    var aBool5702: Boolean = true,
    var members: Boolean = false,
    var adjustMapSceneRotation: Boolean = false,
    var hasAnimation: Boolean = false,
    var anInt5705: Int = 0,
    var anInt5665: Int = 0,
    var anInt5670: Int = 0,
    var anInt5666: Int = 0,
    var mapSpriteRotation: Int = 0,
    var mapSpriteId: Int = 0,
    var ambientSoundVolume: Int = 0,
    var flipMapSprite: Boolean = false,
    var animProbs: IntArray? = null,
    var mapIcon: Int = 0,
    var anIntArray5707: IntArray? = null,
    var aByte5644: Byte = 0,
    var aByte5642: Byte = 0,
    var aByte5646: Byte = 0,
    var aByte5634: Byte = 0,
    var anInt5682: Short = 0,
    var anInt5683: Short = 0,
    var anInt5710: Short = 0,
    var anInt5704: Int = 0,
    var aBool5696: Boolean = false,
    var aBool5700: Boolean = false,
    var anInt5684: Int = 0,
    var anInt5658: Int = 0,
    var anInt5708: Int = 0,
    var anInt5709: Int = 0,
    var aBool5699: Boolean = false,
    var anInt5694: Int = 0,
    var aBool5711: Boolean = false,
    var params: Params = Params(),
    var actionCursors: IntArray? = null
) {

    companion object {
        private val PARSER = ObjectTypeParser()

        fun get(id: Int): ObjectType {
            return PARSER.get(Cache.get(), id)
        }

        fun getParser(): ObjectTypeParser = PARSER

        //TODO enable once we have vars
//        fun get(id: Int, vars: VarManager?): ObjectDef {
//            var def = get(id)
//            def = def.transformForPlayer(vars)
//            return def ?: ObjectDef(id = id)
//        }
    }

    fun getOpIdForName(opName: String): Int {
        return options.indexOfFirst { it?.equals(opName, ignoreCase = true) == true }
    }

    fun getOp(optionId: Int): String {
        return options.getOrNull(optionId) ?: "null"
    }

    fun containsOp(option: String): Boolean {
        return options.any { it?.equals(option, ignoreCase = true) == true }
    }

    //TODO enable once we have vars
//    fun transformForPlayer(vars: VarManager?): ObjectDef {
//        transformTo?.let {
//            val index = when {
//                varpBit != -1 -> vars?.getVarBit(varpBit) ?: -1
//                varp != -1 -> vars?.getVar(varp) ?: -1
//                else -> -1
//            }
//            return if (index in 0 until it.size && it[index] != -1) {
//                get(it[index])
//            } else {
//                get(it.last())
//            }
//        }
//        return this
//    }
}