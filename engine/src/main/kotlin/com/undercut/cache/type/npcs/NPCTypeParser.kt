package com.undercut.cache.type.npcs

import com.undercut.cache.*
import com.undercut.cache.model.MovementType
import com.undercut.cache.type.TypeParser
import java.nio.ByteBuffer

class NPCTypeParser : TypeParser<NPCType>() {

    override fun getIndex(): Index = Index.NPC_DEF

    override fun getArchiveBitSize(): Int = 7

    override fun decode(id: Int, buffer: ByteBuffer): NPCType {
        val def = NPCType(id = id)
        while (buffer.hasRemaining()) {
            when (val opcode = buffer.get().toInt() and 0xff) {
                0 -> break
                1 -> {
                    val count = buffer.get().toInt() and 0xff
                    def.modelIds = IntArray(count) { buffer.getSmartInt() }
                }
                2 -> def.name = buffer.getString()
                12 -> def.size = buffer.get().toInt() and 0xff
                in 30..34 -> def.options[opcode - 30] = buffer.getString()
                39 -> def.contrast = buffer.get() * 5
                40 -> {
                    val count = buffer.get().toInt() and 0xff
                    def.originalColors = ShortArray(count) { buffer.short.toInt().toShort() }
                    def.modifiedColors = ShortArray(count) { buffer.short.toInt().toShort() }
                }
                41 -> {
                    val count = buffer.get().toInt() and 0xff
                    def.originalTextures = ShortArray(count) { buffer.short.toInt().toShort() }
                    def.modifiedTextures = ShortArray(count) { buffer.short.toInt().toShort() }
                }
                42 -> {
                    val count = buffer.get().toInt() and 0xff
                    def.recolourPalette = ByteArray(count) { buffer.get() }
                }
                44, 45 -> buffer.short // Ignored opcodes
                60 -> {
                    val count = buffer.get().toInt() and 0xff
                    def.headModels = IntArray(count) { buffer.getSmartInt() }
                }
                93 -> def.drawMapdot = false
                95 -> def.combatLevel = buffer.short.toInt() and 0xffff
                97 -> def.resizeX = buffer.short.toInt() and 0xffff
                98 -> def.resizeY = buffer.short.toInt() and 0xffff
                99 -> def.aBool4904 = true
                100 -> def.ambient = buffer.get().toInt() + 0x40
                101 -> def.contrast = buffer.get() * 5
                102 -> def.headIcons = buffer.readMasked()
                103 -> def.rotation = buffer.short.toInt() and 0xffff
                106, 118 -> {
                    def.varpBit = buffer.short.toInt().let { if (it == 65535) -1 else it }
                    def.varp = buffer.short.toInt().let { if (it == 65535) -1 else it }
                    val defaultId = if (opcode == 118) buffer.short.toInt().let { if (it == 65535) -1 else it } else -1
                    val size = buffer.getUnsignedSmart()
                    def.transformTo = IntArray(size + 2) {
                        if (it == size + 1) defaultId else buffer.short.toInt().let { if (it == 65535) -1 else it }
                    }
                }
                107 -> def.visible = false
                109 -> def.isClickable = false
                111 -> def.animateIdle = false
                113 -> {
                    def.aShort4874 = buffer.short
                    def.aShort4897 = buffer.short
                }
                114 -> {
                    def.aByte4883 = buffer.get()
                    def.aByte4899 = buffer.get()
                }
                119 -> def.walkMask = buffer.get()
                121 -> {
                    val count = buffer.get().toInt() and 0xff
                    def.modelTranslation = Array(count) {
                        IntArray(4) { buffer.get().toInt() }
                    }
                }
                123 -> def.height = buffer.short.toInt() and 0xffff
                125 -> def.respawnDirection = buffer.get()
                127 -> def.basId = buffer.short.toInt() and 0xffff
                128 -> def.movementType = MovementType.forId(buffer.get().toInt() and 0xff)
                134 -> {
                    def.walkingAnimation = buffer.short.toInt().let { if (it == 65535) -1 else it }
                    def.rotate180Animation = buffer.short.toInt().let { if (it == 65535) -1 else it }
                    def.rotate90RightAnimation = buffer.short.toInt().let { if (it == 65535) -1 else it }
                    def.rotate90LeftAnimation = buffer.short.toInt().let { if (it == 65535) -1 else it }
                    def.specialByte = buffer.get().toInt() and 0xff
                }
                135 -> {
                    def.anInt4875 = buffer.get().toInt() and 0xff
                    def.anInt4873 = buffer.short.toInt() and 0xffff
                }
                136 -> {
                    def.anInt4854 = buffer.get().toInt() and 0xff
                    def.anInt4861 = buffer.short.toInt() and 0xffff
                }
                137 -> def.headModelId1 = buffer.short.toInt() and 0xffff
                138 -> def.standAnim = buffer.getSmartInt()
                139 -> buffer.getSmartInt()
                140 -> def.anInt4909 = buffer.get().toInt() and 0xff
                141 -> def.aBool4884 = true
                142 -> def.mapIcon = buffer.short.toInt() and 0xffff
                143 -> def.aBool4890 = true
                in 150..154 -> def.membersOptions[opcode - 150] = buffer.getString()
                155 -> {
                    def.aByte4868 = buffer.get()
                    def.aByte4869 = buffer.get()
                    def.aByte4905 = buffer.get()
                    def.aByte4871 = buffer.get()
                }
                158 -> def.aByte4916 = 1
                159 -> def.aByte4916 = 0
                160 -> {
                    val count = buffer.get().toInt() and 0xff
                    def.quests = IntArray(count) { buffer.short.toInt() and 0xffff }
                }
                162 -> def.aBool4872 = true
                163 -> def.anInt4917 = buffer.get().toInt() and 0xff
                164 -> {
                    def.anInt4911 = buffer.short.toInt() and 0xffff
                    def.anInt4919 = buffer.short.toInt() and 0xffff
                }
                165 -> def.anInt4913 = buffer.get().toInt() and 0xff
                168 -> def.anInt4908 = buffer.get().toInt() and 0xff
                169 -> def.aBool4920 = false
                in 170..175 -> {
                    if (def.actionCursors == null) {
                        def.actionCursors = IntArray(6) { -1 }
                    }
                    def.actionCursors?.set(opcode - 170, buffer.short.toInt() and 0xFFFF)
                }
                176 -> def.aabbBounds = IntArray(6) { buffer.getSignedSmart() }
                178 -> Unit // Placeholder
                179 -> repeat(6) { buffer.getSignedSmart() }
                180 -> buffer.get()
                181 -> repeat(3) { buffer.get() }
                182 -> Unit // Placeholder
                183, 184 -> buffer.skip(1) // Skipped
                185 -> Unit
                186 -> {
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
                252 -> buffer.short
                253 -> buffer.skip(1)
                else -> throw RuntimeException("Missing NPC opcode: $opcode")
            }
        }
        return def
    }
}