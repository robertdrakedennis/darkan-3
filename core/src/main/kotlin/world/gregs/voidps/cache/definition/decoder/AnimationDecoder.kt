package world.gregs.voidps.cache.definition.decoder

import world.gregs.voidps.buffer.read.Reader
import world.gregs.voidps.cache.Cache
import world.gregs.voidps.cache.DefinitionDecoder
import world.gregs.voidps.cache.Index.ANIMATIONS
import world.gregs.voidps.cache.definition.data.AnimationDefinition

class AnimationDecoder : DefinitionDecoder<AnimationDefinition>(ANIMATIONS) {

    override fun create(size: Int) = Array(size) { AnimationDefinition(it) }

    override fun getFile(id: Int) = id and 0x7f

    override fun getArchive(id: Int) = id ushr 7

    override fun size(cache: Cache): Int {
        return cache.lastArchiveId(index) * 128 + (cache.fileCount(index, cache.lastArchiveId(index)))
    }

    override fun AnimationDefinition.read(opcode: Int, buffer: Reader) {
        when (opcode) {
            1 -> {
                val length = buffer.readUnsignedShort()
                durations = IntArray(length) { buffer.readUnsignedShort() }
                frames = IntArray(length) { buffer.readUnsignedShort() }
                for (count in 0 until length) {
                    frames!![count] = (buffer.readUnsignedShort() shl 16) + frames!![count]
                }
            }
            2 -> loopOffset = buffer.readUnsignedShort()
            3 -> {
                // Modern RS3: uses smart encoding for count and values
                val length = buffer.readSmart()
                repeat(length) { buffer.readSmart() }
            }
            5 -> priority = buffer.readUnsignedByte()
            6 -> leftHandItem = buffer.readShort()
            7 -> rightHandItem = buffer.readShort()
            8 -> maxLoops = buffer.readUnsignedByte()
            9 -> animatingPrecedence = buffer.readUnsignedByte()
            10 -> walkingPrecedence = buffer.readUnsignedByte()
            11 -> replayMode = buffer.readUnsignedByte()
            12 -> {
                // Opcode 12: ubyte count variant of sound effects
                val length = buffer.readUnsignedByte()
                for (count in 0 until length) {
                    buffer.readUnsignedShort() // lower 16 bits
                }
                for (count in 0 until length) {
                    buffer.readUnsignedShort() // upper 16 bits
                }
            }
            13 -> {
                val length = buffer.readUnsignedShort()
                for (count in 0 until length) {
                    val size = buffer.readUnsignedByte()
                    if (size > 0) {
                        buffer.readUnsignedMedium() // first sound ID
                        for (index in 1 until size) {
                            buffer.readShort() // additional sound IDs
                        }
                    }
                }
            }
            14 -> aBoolean691 = true
            15 -> tweened = true
            18 -> useSounds = true
            19 -> {
                if (volumes == null) {
                    volumes = IntArray(sounds!!.size)
                    for (index in sounds!!.indices) {
                        volumes!![index] = 255
                    }
                }
                volumes!![buffer.readUnsignedByte()] = buffer.readUnsignedByte()
            }
            20 -> {
                if (primarySpeeds == null || secondarySpeeds == null) {
                    primarySpeeds = IntArray(sounds!!.size)
                    secondarySpeeds = IntArray(sounds!!.size)
                    for (index in sounds!!.indices) {
                        primarySpeeds!![index] = 256
                        secondarySpeeds!![index] = 256
                    }
                }
                val length = buffer.readUnsignedByte()
                primarySpeeds!![length] = buffer.readShort()
                secondarySpeeds!![length] = buffer.readShort()
            }
            22 -> buffer.skip(1) // unknown byte
            23 -> buffer.skip(2) // unknown short
            24 -> buffer.skip(2) // animMaya ID (ushort)
            25 -> buffer.readUnsignedShort() // field25
            26 -> {
                buffer.readUnsignedShort() // field26a
                buffer.readUnsignedShort() // field26b
            }
            27 -> buffer.readByte() // signed byte
            112 -> {
                // Extended sound effects (ushort count)
                val length = buffer.readUnsignedShort()
                for (count in 0 until length) {
                    buffer.readUnsignedShort() // lower 16 bits
                }
                for (count in 0 until length) {
                    buffer.readUnsignedShort() // upper 16 bits
                }
            }
            119 -> {
                // Extended version of opcode 19 (ushort index)
                buffer.readUnsignedShort() // index
                buffer.readByte() // merge value
            }
            120 -> {
                // Extended version of opcode 20 (ushort index)
                buffer.readUnsignedShort() // index
                buffer.readShort() // sound start
                buffer.readShort() // sound end
            }
            249 -> { } // parameters (if present in modern)
            else -> { }
        }
    }

    override fun changeValues(definitions: Array<AnimationDefinition>, definition: AnimationDefinition) {
        if (definition.walkingPrecedence == -1) {
            definition.walkingPrecedence = if (definition.interleaveOrder == null) 0 else 2
        }
        if (definition.animatingPrecedence == -1) {
            definition.animatingPrecedence = if (definition.interleaveOrder == null) 0 else 2
        }
    }
}