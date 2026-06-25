package com.undercut.cache.type.items

import com.undercut.cache.Index
import com.undercut.cache.getSmartInt
import com.undercut.cache.getString
import com.undercut.cache.skip
import com.undercut.cache.type.TypeParser
import java.nio.ByteBuffer

class ItemTypeParser : TypeParser<ItemType>() {

    override fun getIndex(): Index = Index.ITEM_DEF

    override fun getArchiveBitSize(): Int = 8

    override fun decode(id: Int, buffer: ByteBuffer): ItemType {
        val def = ItemType()
        def.id = id

        while (buffer.hasRemaining()) {
            when (val opcode = buffer.get().toInt() and 0xFF) {
                0 -> break
                1 -> def.modelId = buffer.getSmartInt()
                2 -> def.name = buffer.getString()
                3 -> def.buffEffect = buffer.getString()
                4 -> def.modelZoom = buffer.short.toInt() and 0xFFFF
                5 -> def.modelRotationX = buffer.short.toInt() and 0xFFFF
                6 -> def.modelRotationY = buffer.short.toInt() and 0xFFFF
                7 -> def.modelOffsetX = buffer.short.toInt().adjustShort()
                8 -> def.modelOffsetY = buffer.short.toInt().adjustShort()
                10 -> buffer.skip(2)
                11 -> def.stackable = 1
                12 -> def.price = buffer.int.toLong()
                13 -> def.equipmentSlot = buffer.get()
                14 -> def.equipmentType = buffer.get()
                15 -> Unit
                16 -> def.members = true
                18 -> buffer.skip(2)
                23 -> def.maleModel1 = buffer.getSmartInt()
                24 -> def.maleModel2 = buffer.getSmartInt()
                25 -> def.femaleModel1 = buffer.getSmartInt()
                26 -> def.femaleModel2 = buffer.getSmartInt()
                27 -> def.equipmentType2 = buffer.get()
                in 30..34 -> def.groundActions[opcode - 30] = buffer.getString()
                in 35..39 -> def.inventoryActions[opcode - 35] = buffer.getString()
                40 -> def.parseColors(buffer)
                41 -> def.parseTextures(buffer)
                42 -> def.parseRecolorPalette(buffer)
                43 -> def.notedId = buffer.int
                44, 45 -> buffer.skip(2) // bitmask recolor/retexture
                65 -> def.stockMarket = true
                69 -> def.geBuyLimit = buffer.int
                78 -> def.maleModel3 = buffer.getSmartInt()
                79 -> def.femaleModel3 = buffer.getSmartInt()
                90 -> def.maleModelTranslateY = buffer.getSmartInt()
                91 -> def.femaleModelTranslateY = buffer.getSmartInt()
                92 -> def.maleHeadModel = buffer.getSmartInt()
                93 -> def.femaleHeadModel = buffer.getSmartInt()
                94 -> def.category = buffer.short.toInt() and 0xFFFF
                95 -> def.modelAngleZ = buffer.short.toInt() and 0xFFFF
                96 -> def.equipCategory = buffer.get().toInt() and 0xFF
                97 -> def.notedItemId = buffer.short.toInt() and 0xFFFF
                98 -> def.notedTemplate = buffer.short.toInt() and 0xFFFF
                in 100..109 -> def.parseStackData(buffer, opcode - 100)
                110 -> {
                    def.resizeX = (buffer.short.toInt() and 0xFFFF) / 128.0f
                    def.hasResize = true
                }
                111 -> {
                    def.resizeY = (buffer.short.toInt() and 0xFFFF) / 128.0f
                    def.hasResize = true
                }
                112 -> {
                    def.resizeZ = (buffer.short.toInt() and 0xFFFF) / 128.0f
                    def.hasResize = true
                }
                113 -> def.ambient = buffer.get()
                114 -> def.contrast = buffer.get() * 5
                115 -> def.teamId = buffer.get()
                121 -> def.lentItemId = buffer.short.toInt() and 0xFFFF
                122 -> def.lendTemplate = buffer.short.toInt() and 0xFFFF
                125 -> def.parseMaleModelOffsets(buffer)
                126 -> def.parseFemaleModelOffsets(buffer)
                in 127..130 -> buffer.skip(2)
                132 -> def.parseQuestIds(buffer)
                134 -> def.pickSizeShift = buffer.get().toInt() and 0xFF
                139 -> def.bindId = buffer.short.toInt() and 0xFFFF
                140 -> def.boundTemplate = buffer.short.toInt() and 0xFFFF
                in 142..146 -> def.parseHeadModels(buffer, opcode - 142)
                in 150..154 -> def.parseGroundCursors(buffer, opcode - 150)
                156 -> def.tradeable = true
                157 -> def.searchable = true
                161 -> def.shardItemId = buffer.short.toInt() and 0xFFFF
                162 -> def.shardTemplateId = buffer.short.toInt() and 0xFFFF
                163 -> def.shardCombineAmount = buffer.short.toInt() and 0xFFFF
                164 -> def.shardName = buffer.getString()
                165 -> def.stackable = 2
                167 -> Unit
                168 -> Unit
                178 -> def.stackable = 0
                181 -> def.price = buffer.long
                in 242..248 -> Unit
                249 -> def.params.parse(buffer)
                else -> throw IllegalArgumentException("Invalid ItemDef opcode $opcode")
            }
        }

        def.applyTemplates()
        def.loadEquippedOps()
        return def
    }

    private fun Int.adjustShort(): Int = if (this > 32767) this - 65536 else this

    private fun ItemType.parseColors(buffer: ByteBuffer) {
        val count = buffer.get().toInt() and 0xFF
        originalColors = ShortArray(count)
        replacementColors = ShortArray(count)
        for (i in 0 until count) {
            originalColors?.set(i, buffer.short)
            replacementColors?.set(i, buffer.short)
        }
    }

    private fun ItemType.parseTextures(buffer: ByteBuffer) {
        val count = buffer.get().toInt() and 0xFF
        originalTextures = ShortArray(count)
        replacementTextures = ShortArray(count)
        for (i in 0 until count) {
            originalTextures?.set(i, buffer.short)
            replacementTextures?.set(i, buffer.short)
        }
    }

    private fun ItemType.parseRecolorPalette(buffer: ByteBuffer) {
        val count = buffer.get().toInt() and 0xFF
        recolorPalette = ByteArray(count)
        for (i in 0 until count) {
            recolorPalette?.set(i, buffer.get())
        }
    }

    private fun ItemType.parseStackData(buffer: ByteBuffer, index: Int) {
        if (stackIds == null) {
            stackAmounts = IntArray(10)
            stackIds = IntArray(10)
        }
        stackIds!![index] = buffer.short.toInt() and 0xFFFF
        stackAmounts!![index] = buffer.short.toInt() and 0xFFFF
    }

    private fun ItemType.parseMaleModelOffsets(buffer: ByteBuffer) {
        maleModelOffsetX = buffer.get().toInt() shl 2
        maleModelOffsetY = buffer.get().toInt() shl 2
        maleModelOffsetZ = buffer.get().toInt() shl 2
    }

    private fun ItemType.parseFemaleModelOffsets(buffer: ByteBuffer) {
        femaleModelOffsetX = buffer.get().toInt() shl 2
        femaleModelOffsetY = buffer.get().toInt() shl 2
        femaleModelOffsetZ = buffer.get().toInt() shl 2
    }

    private fun ItemType.parseQuestIds(buffer: ByteBuffer) {
        val count = buffer.get().toInt() and 0xFF
        questIds = IntArray(count)
        for (i in 0 until count) {
            questIds!![i] = buffer.short.toInt() and 0xFFFF
        }
    }

    private fun ItemType.parseHeadModels(buffer: ByteBuffer, index: Int) {
        if (headModels == null) {
            headModels = IntArray(6) { -1 }
        }
        headModels!![index] = buffer.short.toInt() and 0xFFFF
    }

    private fun ItemType.parseGroundCursors(buffer: ByteBuffer, index: Int) {
        if (groundCursors == null) {
            groundCursors = IntArray(5) { -1 }
        }
        groundCursors!![index] = buffer.short.toInt() and 0xFFFF
    }

    private fun ItemType.applyTemplates() {
        if (notedTemplate != -1) toNote()
        if (lendTemplate != -1) toLend()
        if (boundTemplate != -1) toBind()
    }
}
