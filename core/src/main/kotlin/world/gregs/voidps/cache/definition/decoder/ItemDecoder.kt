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
            12 -> cost = buffer.readInt().toLong()
            13 -> wearPos = buffer.readUnsignedByte()
            14 -> wearPos2 = buffer.readUnsignedByte()
            15 -> { } // Unit
            16 -> members = true
            18 -> multiStackSize = buffer.readUnsignedShort()
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
            43 -> buffer.readInt() // notedId
            44, 45 -> buffer.skip(2) // bitmask recolor/retexture
            65 -> exchangeable = true
            69 -> geBuyLimit = buffer.readInt()
            78 -> tertiaryMaleModel = buffer.readBigSmart()
            79 -> tertiaryFemaleModel = buffer.readBigSmart()
            90 -> primaryMaleDialogueHead = buffer.readBigSmart()
            91 -> primaryFemaleDialogueHead = buffer.readBigSmart()
            92 -> secondaryMaleDialogueHead = buffer.readBigSmart()
            93 -> secondaryFemaleDialogueHead = buffer.readBigSmart()
            94 -> category = buffer.readUnsignedShort()
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
            in 127..130 -> buffer.skip(2) // cursor opcodes
            132 -> {
                val length = buffer.readUnsignedByte()
                campaigns = IntArray(length) { buffer.readUnsignedShort() }
            }
            134 -> pickSizeShift = buffer.readUnsignedByte()
            139 -> bindId = buffer.readUnsignedShort()
            140 -> boundTemplateId = buffer.readUnsignedShort()
            in 142..146 -> {
                if (headModels == null) {
                    headModels = IntArray(6) { -1 }
                }
                headModels!![opcode - 142] = buffer.readUnsignedShort()
            }
            in 150..154 -> {
                if (groundCursors == null) {
                    groundCursors = IntArray(5) { -1 }
                }
                groundCursors!![opcode - 150] = buffer.readUnsignedShort()
            }
            156 -> tradeable = true
            157 -> searchable = true
            161 -> shardItemId = buffer.readUnsignedShort()
            162 -> shardTemplateId = buffer.readUnsignedShort()
            163 -> shardCombineAmount = buffer.readUnsignedShort()
            164 -> shardName = buffer.readString()
            165 -> stackable = 2
            167, 168 -> { } // boolean flags
            178 -> stackable = 0
            181 -> cost = buffer.readLong()
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
        if (definition.boundTemplateId != -1) {
            definition.toSingleNote(definitions.getOrNull(definition.boundTemplateId), definitions.getOrNull(definition.bindId))
        }
    }
}