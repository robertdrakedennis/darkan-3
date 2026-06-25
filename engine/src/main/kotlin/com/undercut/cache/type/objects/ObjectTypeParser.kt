package com.undercut.cache.type.objects

import com.undercut.cache.Index
import com.undercut.cache.getSmartInt
import com.undercut.cache.getString
import com.undercut.cache.getSignedSmart
import com.undercut.cache.getUnsignedSmart
import com.undercut.cache.type.TypeParser
import com.undercut.cache.type.maps.ObjectShape
import java.nio.ByteBuffer

class ObjectTypeParser : TypeParser<ObjectType>() {

    override fun getIndex(): Index = Index.OBJ_DEF

    override fun getArchiveBitSize(): Int = 8

    override fun decode(id: Int, buffer: ByteBuffer): ObjectType {
        val def = ObjectType(id = id)
        while (buffer.hasRemaining()) {
            when (val opcode = buffer.get().toInt() and 0xff) {
                0 -> break
                1 -> {
                    val count = buffer.get().toInt() and 0xff
                    def.shapes = Array(count) { ObjectShape.WALL_STRAIGHT }
                    def.modelIds = Array(count) { IntArray(0) }

                    for (i in 0 until count) {
                        def.shapes?.set(i, ObjectShape.forId(buffer.get().toInt()))
                        val numModels = buffer.get().toInt() and 0xff
                        def.modelIds?.set(i, IntArray(numModels))

                        for (i7 in 0 until numModels) {
                            def.modelIds?.get(i)[i7] = buffer.getSmartInt()
                        }
                    }
                }
                2 -> def.name = buffer.getString()
                14 -> def.sizeX = buffer.get().toInt() and 0xff
                15 -> def.sizeY = buffer.get().toInt() and 0xff
                17 -> {
                    def.clipType = 0
                    def.blocks = false
                }
                18 -> def.blocks = false
                19 -> def.interactable = buffer.get().toInt() and 0xff
                21 -> def.groundContoured = 1
                22 -> def.delayShading = true
                23 -> def.occludes = 1
                24 -> buffer.getSmartInt().takeIf { it != -1 }?.let { def.animations = intArrayOf(it) }
                27 -> def.clipType = 1
                28 -> def.decorDisplacement = (buffer.get().toInt() and 0xff) shl 2
                29 -> def.ambient = buffer.get().toInt() + 0x40
                39 -> def.contrast = buffer.get() * 5
                in 30..34 -> def.options[opcode - 30] = buffer.getString()
                40 -> {
                    val colorCount = buffer.get().toInt() and 0xff
                    def.originalColors = ShortArray(colorCount)
                    def.modifiedColors = ShortArray(colorCount)
                    repeat(colorCount) {
                        def.originalColors?.set(it, buffer.short)
                        def.modifiedColors?.set(it, buffer.short)
                    }
                }
                41 -> {
                    val textureCount = buffer.get().toInt() and 0xff
                    def.originalTextures = ShortArray(textureCount)
                    def.modifiedTextures = ShortArray(textureCount)
                    repeat(textureCount) {
                        def.originalTextures?.set(it, buffer.short)
                        def.modifiedTextures?.set(it, buffer.short)
                    }
                }
                42 -> {
                    val byteCount = buffer.get().toInt() and 0xff
                    def.aByteArray5641 = ByteArray(byteCount) { buffer.get() }
                }
                44, 45 -> buffer.short // skip
                62 -> def.inverted = true
                64 -> def.castsShadow = false
                65 -> def.scaleX = buffer.short.toInt() and 0xffff
                66 -> def.scaleY = buffer.short.toInt() and 0xffff
                67 -> def.scaleZ = buffer.short.toInt() and 0xffff
                69 -> def.accessBlockFlag = buffer.get().toInt() and 0xff
                70 -> def.offsetX = buffer.short.toInt() shl 2
                71 -> def.offsetY = buffer.short.toInt() * -4
                72 -> def.offsetZ = buffer.short.toInt() shl 2
                73 -> def.obstructsGround = true
                74 -> def.ignoreAltClip = true
                75 -> def.supportsItems = buffer.get().toInt() and 0xff
                77, 92 -> {
                    def.varpBit = buffer.short.toInt() and 0xffff
                    if (def.varpBit == 65535) def.varpBit = -1
                    def.varp = buffer.short.toInt() and 0xffff
                    if (def.varp == 65535) def.varp = -1

                    val objectId = if (opcode == 92) buffer.getSmartInt() else -1
                    val transforms = buffer.getUnsignedSmart()
                    def.transformTo = IntArray(transforms + 2) {
                        if (it <= transforms) buffer.getSmartInt() else objectId
                    }
                }
                78 -> {
                    def.ambientSoundId = buffer.short.toInt() and 0xffff
                    def.ambientSoundHearDistance = buffer.get().toInt() and 0xff
                }
                79 -> {
                    def.anInt5667 = buffer.short.toInt() and 0xffff
                    def.anInt5698 = buffer.short.toInt() and 0xffff
                    def.ambientSoundHearDistance = buffer.get().toInt() and 0xff
                    val count = buffer.get().toInt() and 0xff
                    def.audioTracks = IntArray(count) { buffer.short.toInt() and 0xffff }
                }
                81 -> {
                    def.groundContoured = 2
                    def.anInt5654 = buffer.get().toInt() * 256
                }
                82 -> def.hidden = true
                88 -> def.aBool5703 = false
                89 -> def.aBool5702 = false
                91 -> def.members = true
                93 -> {
                    def.groundContoured = 3
                    def.anInt5654 = buffer.short.toInt() and 0xffff
                }
                94 -> def.groundContoured = 4
                95 -> {
                    def.groundContoured = 5
                    def.anInt5654 = buffer.short.toInt()
                }
                97 -> def.adjustMapSceneRotation = true
                98 -> def.hasAnimation = true
                99 -> {
                    def.anInt5705 = buffer.get().toInt() and 0xff
                    def.anInt5665 = buffer.short.toInt() and 0xffff
                }
                100 -> {
                    def.anInt5670 = buffer.get().toInt() and 0xff
                    def.anInt5666 = buffer.short.toInt() and 0xffff
                }
                101 -> def.mapSpriteRotation = buffer.get().toInt() and 0xff
                102 -> def.mapSpriteId = buffer.short.toInt() and 0xffff
                103 -> def.occludes = 0
                104 -> def.ambientSoundVolume = buffer.get().toInt() and 0xff
                105 -> def.flipMapSprite = true
                106 -> {
                    val animCount = buffer.get().toInt() and 0xff
                    def.animations = IntArray(animCount) { 0 }
                    def.animProbs = IntArray(animCount) { 0 }
                    var totalProbs = 0
                    for (i in 0 until animCount) {
                        def.animations?.set(i, buffer.getSmartInt())
                        def.animProbs?.set(i, buffer.get().toInt() and 0xff)
                    }
                    totalProbs = def.animProbs?.sum() ?: 0
                    def.animProbs = def.animProbs?.map { it * 65535 / totalProbs }?.toIntArray()
                }
                107 -> def.mapIcon = buffer.short.toInt() and 0xffff
                in 150..154 -> def.options[opcode - 150] = buffer.getString()
                160 -> {
                    val count = buffer.get().toInt() and 0xff
                    def.anIntArray5707 = IntArray(count) { buffer.short.toInt() and 0xffff }
                }
                162 -> {
                    def.groundContoured = 3
                    def.anInt5654 = buffer.int
                }
                163 -> {
                    def.aByte5644 = buffer.get()
                    def.aByte5642 = buffer.get()
                    def.aByte5646 = buffer.get()
                    def.aByte5634 = buffer.get()
                }
                164 -> def.anInt5682 = buffer.short
                165 -> def.anInt5683 = buffer.short
                166 -> def.anInt5710 = buffer.short
                167 -> def.anInt5704 = buffer.short.toInt() and 0xffff
                168 -> def.aBool5696 = true
                169 -> def.aBool5700 = true
                170 -> def.anInt5684 = buffer.getUnsignedSmart()
                171 -> def.anInt5658 = buffer.getUnsignedSmart()
                173 -> {
                    def.anInt5708 = buffer.short.toInt() and 0xffff
                    def.anInt5709 = buffer.short.toInt() and 0xffff
                }
                177 -> def.aBool5699 = true
                178 -> def.anInt5694 = buffer.get().toInt() and 0xff
                186 -> buffer.get()
                188 -> Unit //setting bool to true
                189 -> def.aBool5711 = true
                197 -> buffer.get()
                198 -> Unit //setting bool to true
                199 -> Unit //setting bool to true
                196 -> buffer.get()
                in 190..195 -> {
                    if (def.actionCursors == null) def.actionCursors = IntArray(6) { -1 }
                    def.actionCursors?.set(opcode - 190, buffer.short.toInt() and 0xffff)
                }
                200 -> Unit // flag
                201 -> repeat(6) { buffer.getSignedSmart() }
                202 -> buffer.getUnsignedSmart()
                203 -> Unit //setting bool to true
                204 -> {
                    repeat(buffer.getUnsignedSmart()) {
                        buffer.short.toInt() and 0xFFFF
                        buffer.get().toInt() == 1
                        buffer.int to buffer.int to buffer.int
                        buffer.int to buffer.int to buffer.int
                    }
                }
                205 -> {
                    buffer.short
                    def.varpBit = buffer.short.toInt() and 0xffff
                    if (def.varpBit == 65535) def.varpBit = -1
                    def.varp = buffer.short.toInt() and 0xffff
                    if (def.varp == 65535) def.varp = -1

                    val flags = buffer.get().toInt()
                    if ((flags and 1) != 0) {
                        val length = buffer.get()
                        for (i in 0 until length) {
                            val value = buffer.get()
                            val length2 = buffer.get()
                            for (j in 0 until length2) {
                                var line = "multimodel=$value,${buffer.short.toInt() and 0xFFFF},${buffer.short.toInt() and 0xFFFF},${buffer.getSmartInt()}"
                                val n = buffer.get()
                                if (n >= 1) line += ",${buffer.get()}"
                                if (n >= 2) line += ",${buffer.get()}"
                                if (n >= 3) line += ",${buffer.get()}"
                            }
                        }
                    }
                    if ((flags and 2) != 0) {
                        val length = buffer.get()
                        for (i in 0 until length) {
                            val value = buffer.get()
                            val length2 = buffer.get()
                            for (j in 0 until length2) {
                                val line = "multiheadmodel=$value,${buffer.short.toInt() and 0xFFFF},${buffer.short.toInt() and 0xFFFF},${buffer.getSmartInt()}"
                            }
                        }
                    }
                    if ((flags and 4) != 0) {
                        val length = buffer.get()
                        for (i in 0 until length) {
                            val value = buffer.get()
                            val length2 = buffer.get()
                            for (j in 0 until length2) {
                                val line = "multiretex=$value,${buffer.short.toInt() and 0xFFFF},${buffer.short.toInt() and 0xFFFF},${buffer.short.toInt() and 0xFFFF},${buffer.short.toInt() and 0xFFFF}"
                            }
                        }
                    }
                    if ((flags and 8) != 0) {
                        val length = buffer.get()
                        for (i in 0 until length) {
                            val value = buffer.get()
                            val length2 = buffer.get()
                            for (j in 0 until length2) {
                                val line = "multirecol=$value,${buffer.short.toInt() and 0xFFFF},${buffer.short.toInt() and 0xFFFF},${buffer.short.toInt() and 0xFFFF},${buffer.short.toInt() and 0xFFFF}"
                            }
                        }
                    }
                    if ((flags and 16) != 0) {
                        val length = buffer.get()
                        for (i in 0 until length) {
                            val value = buffer.get()
                            val line = "multitint=$value,${buffer.short.toInt() and 0xFFFF},${buffer.short.toInt() and 0xFFFF},${buffer.get()},${buffer.get()},${buffer.get()},${buffer.get()}"
                        }
                    }
                    val default = buffer.short.toInt() and 0xFFFF
                    def.transformTo = IntArray(1) { default }
                }
                249 -> def.params.parse(buffer)

                250 -> buffer.get() //bgsoundshape
                251 -> buffer.get() //bgsounddistancefiltered
                252 -> {
                    val line = "bgsounddistancefilterparams=" + buffer.short + "," + buffer.short + "," + buffer.short //hzhigh,hzlow,q
                }
                253 -> buffer.get() //randomsoundshape
                254 -> buffer.get() //randomsounddistancefiltered
                255 -> {
                    val line = "randomsounddistancefilterparams=" + buffer.short + "," + buffer.short + "," + buffer.short
                }
                else -> throw Exception("Missing Object opcode: $opcode")
            }
        }
        return def
    }
}