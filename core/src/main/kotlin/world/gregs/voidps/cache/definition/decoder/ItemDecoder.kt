package world.gregs.voidps.cache.definition.decoder

import world.gregs.voidps.buffer.read.Reader
import world.gregs.voidps.cache.DefinitionDecoder
import world.gregs.voidps.cache.Index.ITEMS
import world.gregs.voidps.cache.definition.data.ItemDefinition

class ItemDecoder : DefinitionDecoder<ItemDefinition>(ITEMS) {

    override fun create(size: Int) = Array(size) { ItemDefinition(it) }

    override fun getFile(id: Int) = id and 0xff

    override fun getArchive(id: Int) = id ushr 8

    override fun ItemDefinition.read(opcode: Int, buffer: Reader) {
        when (opcode) {
            // Opcode 1: model ID (bigSmart for rev 727)
            1 -> modelId = buffer.readBigSmart()
            2 -> name = buffer.readString()
            3 -> buffer.readString() // buffEffect
            4 -> spriteScale = buffer.readUnsignedShort()
            5 -> spritePitch = buffer.readUnsignedShort()
            6 -> spriteCameraRoll = buffer.readUnsignedShort()
            7 -> {
                spriteTranslateX = buffer.readUnsignedShort()
                if (spriteTranslateX > 32767) {
                    spriteTranslateX -= 65536
                }
            }
            8 -> {
                spriteTranslateY = buffer.readUnsignedShort()
                if (spriteTranslateY > 32767) {
                    spriteTranslateY -= 65536
                }
            }
            10 -> buffer.skip(2) // unknown ushort
            11 -> stackable = 1
            12 -> cost = buffer.readInt()
            13 -> wearPos = buffer.readUnsignedByte()
            14 -> wearPos2 = buffer.readUnsignedByte()
            15 -> { } // Unit
            16 -> members = true
            18 -> multiStackSize = buffer.readUnsignedShort()
            // Opcodes 23-26: equipment model IDs (bigSmart for rev 727)
            23 -> primaryMaleModel = buffer.readBigSmart()
            24 -> secondaryMaleModel = buffer.readBigSmart()
            25 -> primaryFemaleModel = buffer.readBigSmart()
            26 -> secondaryFemaleModel = buffer.readBigSmart()
            27 -> wearPos3 = buffer.readUnsignedByte()
            in 30..34 -> floorOptions[opcode - 30] = buffer.readString()
            in 35..39 -> options[opcode - 35] = buffer.readString()
            40 -> readColours(buffer)
            41 -> readTextures(buffer)
            42 -> readColourPalette(buffer)
            // Opcode 43: noteId (int in modern RS3)
            43 -> buffer.readInt()
            44, 45 -> buffer.skip(2) // bitmask recolor/retexture
            65 -> exchangeable = true
            69 -> buffer.readInt() // geBuyLimit
            // Opcodes 78-79: tertiary equipment model IDs (bigSmart for rev 727)
            78 -> tertiaryMaleModel = buffer.readBigSmart()
            79 -> tertiaryFemaleModel = buffer.readBigSmart()
            // Opcodes 90-93: dialogue head model IDs (bigSmart for rev 727)
            90 -> primaryMaleDialogueHead = buffer.readBigSmart()
            91 -> primaryFemaleDialogueHead = buffer.readBigSmart()
            92 -> secondaryMaleDialogueHead = buffer.readBigSmart()
            93 -> secondaryFemaleDialogueHead = buffer.readBigSmart()
            94 -> buffer.readUnsignedShort() // category
            95 -> spriteCameraYaw = buffer.readUnsignedShort()
            96 -> dummyItem = buffer.readUnsignedByte()
            97 -> noteId = buffer.readUnsignedShort()
            98 -> notedTemplateId = buffer.readUnsignedShort()
            in 100..109 -> {
                if (stackIds == null) {
                    stackAmounts = IntArray(10)
                    stackIds = IntArray(10)
                }
                stackIds!![opcode - 100] = buffer.readUnsignedShort()
                stackAmounts!![opcode - 100] = buffer.readUnsignedShort()
            }
            110 -> floorScaleX = buffer.readUnsignedShort()
            111 -> floorScaleZ = buffer.readUnsignedShort()
            112 -> floorScaleY = buffer.readUnsignedShort()
            113 -> ambience = buffer.readByte()
            114 -> diffusion = buffer.readByte() * 5
            115 -> team = buffer.readUnsignedByte()
            121 -> lendId = buffer.readUnsignedShort()
            122 -> lendTemplateId = buffer.readUnsignedShort()
            125 -> {
                maleWieldX = buffer.readByte() shl 2
                maleWieldZ = buffer.readByte() shl 2
                maleWieldY = buffer.readByte() shl 2
            }
            126 -> {
                femaleWieldX = buffer.readByte() shl 2
                femaleWieldZ = buffer.readByte() shl 2
                femaleWieldY = buffer.readByte() shl 2
            }
            in 127..130 -> buffer.skip(2) // cursor opcodes (ushort in modern RS3)
            132 -> {
                val length = buffer.readUnsignedByte()
                campaigns = IntArray(length) { buffer.readUnsignedShort() }
            }
            134 -> pickSizeShift = buffer.readUnsignedByte()
            139 -> singleNoteId = buffer.readUnsignedShort()
            140 -> singleNoteTemplateId = buffer.readUnsignedShort()
            in 142..146 -> buffer.readUnsignedShort() // headModels
            in 150..154 -> buffer.readUnsignedShort() // groundCursors
            156, 157 -> { } // boolean flags (tradeable, searchable)
            161 -> buffer.readUnsignedShort() // shardItemId
            162 -> buffer.readUnsignedShort() // shardTemplateId
            163 -> buffer.readUnsignedShort() // shardCombineAmount
            164 -> buffer.readString() // shardName
            165 -> { } // stackable = 2
            167, 168 -> { } // boolean flags
            178 -> { } // stackable = 0
            181 -> buffer.readLong() // price as long
            in 242..248 -> { } // unknown flags
            249 -> readParameters(buffer)
            else -> { }
        }
    }

    override fun changeValues(definitions: Array<ItemDefinition>, definition: ItemDefinition) {
        if (definition.notedTemplateId != -1) {
            definition.toNote(definitions.getOrNull(definition.notedTemplateId), definitions.getOrNull(definition.noteId))
        }
        if (definition.lendTemplateId != -1) {
            definition.toLend(definitions.getOrNull(definition.lendId), definitions.getOrNull(definition.lendTemplateId))
        }
        if (definition.singleNoteTemplateId != -1) {
            definition.toSingleNote(definitions.getOrNull(definition.singleNoteTemplateId), definitions.getOrNull(definition.singleNoteId))
        }
    }
}