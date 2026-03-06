package world.gregs.voidps.cache.config.decoder

import world.gregs.voidps.buffer.read.Reader
import world.gregs.voidps.cache.Config.QUESTS
import world.gregs.voidps.cache.config.ConfigDecoder
import world.gregs.voidps.cache.config.data.QuestDefinition

class QuestDecoder : ConfigDecoder<QuestDefinition>(QUESTS) {

    override fun create(size: Int) = Array(size) { QuestDefinition(it) }

    override fun QuestDefinition.read(opcode: Int, buffer: Reader) {
        when (opcode) {
            1 -> name = buffer.readString()
            2 -> description = buffer.readString()
            3, 4 -> {
                // MasterQuestVar array: count * {ushort, uint, uint} = 10 bytes each
                val count = buffer.readUnsignedByte()
                buffer.skip(count * 10)
            }
            5 -> sortKey = buffer.readUnsignedShort()
            6 -> difficulty = buffer.readUnsignedByte()
            7 -> members = buffer.readUnsignedByte()
            8 -> questFlags = true
            9 -> questPoints = buffer.readUnsignedByte()
            10 -> {
                val count = buffer.readUnsignedByte()
                pathStart = IntArray(count) { buffer.readInt() }
            }
            12 -> otherPathStart = buffer.readInt()
            13 -> {
                val count = buffer.readUnsignedByte()
                questRequirements = IntArray(count) { buffer.readUnsignedShort() }
            }
            14 -> {
                val count = buffer.readUnsignedByte()
                skillRequirements = Array(count) { IntArray(2) { buffer.readUnsignedByte() } }
            }
            15 -> buffer.readShort() // ushort, not stored
            17 -> itemSprite = buffer.readBigSmart()
            18, 19 -> {
                val count = buffer.readUnsignedByte()
                repeat(count) {
                    buffer.readInt()
                    buffer.readInt()
                    buffer.readInt()
                    buffer.readString()
                }
            }
            249 -> readParameters(buffer)
        }
    }

    override fun changeValues(definitions: Array<QuestDefinition>, definition: QuestDefinition) {
        if (definition.listName == null) {
            definition.listName = definition.name
        }
    }
}