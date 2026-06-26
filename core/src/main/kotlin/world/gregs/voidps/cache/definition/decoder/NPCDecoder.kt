package world.gregs.voidps.cache.definition.decoder

import world.gregs.voidps.buffer.read.Reader
import world.gregs.voidps.cache.DefinitionDecoder
import world.gregs.voidps.cache.Index.NPCS
import world.gregs.voidps.cache.definition.data.NPCDefinition

class NPCDecoder(val members: Boolean = true) : DefinitionDecoder<NPCDefinition>(NPCS) {

    override fun create(size: Int) = Array(size) { NPCDefinition(it) }

    override fun getFile(id: Int) = id and 0x7f

    override fun getArchive(id: Int) = id ushr 7

    override  fun NPCDefinition.read(opcode: Int, buffer: Reader) {
        when (opcode) {
            1 -> {
                val length = buffer.readUnsignedByte()
                modelIds = IntArray(length)
                for (count in 0 until length) {
                    modelIds!![count] = buffer.readBigSmart()
                }
            }
            2 -> name = buffer.readString()
            12 -> size = buffer.readUnsignedByte()
            in 30..34 -> options[opcode - 30] = buffer.readString()
            39 -> buffer.readByte() // contrast (unused in modern, byte * 5)
            40 -> readColours(buffer)
            41 -> readTextures(buffer)
            42 -> readColourPalette(buffer)
            44, 45 -> buffer.readShort() // ignored recolor/retexture opcodes
            60 -> dialogueModels = IntArray(buffer.readUnsignedByte()) { buffer.readBigSmart() }
            93 -> drawMinimapDot = false
            95 -> combat = buffer.readUnsignedShort()
            97 -> scaleXY = buffer.readUnsignedShort()
            98 -> scaleZ = buffer.readUnsignedShort()
            99 -> priorityRender = true
            100 -> lightModifier = buffer.readByte()
            101 -> shadowModifier = 5 * buffer.readByte()
            102 -> {
                // Modern RS3: masked bitfield with bigSmart + decrSmart pairs
                var mask = buffer.readUnsignedByte()
                while (mask > 0) {
                    if (mask and 0x1 == 1) {
                        buffer.readBigSmart() // headIcon sprite id
                        buffer.readSmart() // headIcon secondary (decrSmart - same byte consumption as smart)
                    }
                    mask = mask ushr 1
                }
            }
            103 -> rotation = buffer.readUnsignedShort()
            // NPC transforms use unsigned short for IDs (not bigSmart like objects)
            106, 118 -> {
                varbit = buffer.readUnsignedShort()
                if (varbit == 65535) varbit = -1
                varp = buffer.readUnsignedShort()
                if (varp == 65535) varp = -1
                var last = -1
                if (opcode == 118) {
                    last = buffer.readUnsignedShort()
                    if (last == 65535) last = -1
                }
                val length = buffer.readSmart()
                transforms = IntArray(length + 2)
                for (count in 0..length) {
                    transforms!![count] = buffer.readUnsignedShort()
                    if (transforms!![count] == 65535) transforms!![count] = -1
                }
                transforms!![length + 1] = last
            }
            107 -> clickable = false
            109 -> { } // isClickable = false (modern) / slowWalk = false (legacy)
            111 -> animateIdle = false
            113 -> {
                primaryShadowColour = buffer.readUnsignedShort().toShort()
                secondaryShadowColour = buffer.readUnsignedShort().toShort()
            }
            114 -> {
                primaryShadowModifier = buffer.readByte().toByte()
                secondaryShadowModifier = buffer.readByte().toByte()
            }
            119 -> walkMask = buffer.readByte().toByte()
            121 -> {
                // RS3 948: a count of model translations, each 4 raw bytes (NOT the OSRS
                // index+xyz form, which crashed by indexing translations[byte] into modelIds).
                val count = buffer.readUnsignedByte()
                translations = Array<IntArray?>(count) { IntArray(4) { buffer.readByte() } }
            }
            122 -> hitbarSprite = buffer.readUnsignedShort()
            123 -> height = buffer.readUnsignedShort()
            125 -> respawnDirection = buffer.readByte().toByte()
            127 -> renderEmote = buffer.readUnsignedShort()
            128 -> buffer.readUnsignedByte()
            134 -> {
                // Order per client: walk, crawl(teleport), idle, run
                walkSound = buffer.readUnsignedShort()
                if (walkSound == 65535) {
                    walkSound = -1
                }
                crawlSound = buffer.readUnsignedShort()
                if (crawlSound == 65535) {
                    crawlSound = -1
                }
                idleSound = buffer.readUnsignedShort()
                if (idleSound == 65535) {
                    idleSound = -1
                }
                runSound = buffer.readUnsignedShort()
                if (runSound == 65535) {
                    runSound = -1
                }
                soundDistance = buffer.readUnsignedByte()
            }
            135 -> {
                primaryCursorOp = buffer.readUnsignedByte()
                primaryCursor = buffer.readUnsignedShort()
            }
            136 -> {
                secondaryCursorOp = buffer.readUnsignedByte()
                secondaryCursor = buffer.readUnsignedShort()
            }
            137 -> attackCursor = buffer.readUnsignedShort()
            // Opcode 138: overhead sprite (bigSmart for rev 727)
            138 -> armyIcon = buffer.readBigSmart()
            139 -> spriteId = buffer.readBigSmart() // bigSmart (2-or-4 bytes), not a plain short
            140 -> ambientSoundVolume = buffer.readUnsignedByte()
            141 -> visiblePriority = true
            142 -> mapFunction = buffer.readUnsignedShort()
            143 -> invisiblePriority = true
            in 150..154 -> {
                options[opcode - 150] = buffer.readString()
                if (!members) {
                    options[opcode - 150] = null
                }
            }
            155 -> {
                hue = buffer.readByte().toByte()
                saturation = buffer.readByte().toByte()
                lightness = buffer.readByte().toByte()
                opacity = buffer.readByte().toByte()
            }
            158 -> mainOptionIndex = 1.toByte()
            159 -> mainOptionIndex = 0.toByte()
            160 -> {
                val length = buffer.readUnsignedByte()
                campaigns = IntArray(length) { buffer.readUnsignedShort() }
            }
            162 -> aBoolean2883 = true
            163 -> anInt2803 = buffer.readUnsignedByte()
            164 -> {
                anInt2844 = buffer.readUnsignedShort()
                anInt2852 = buffer.readUnsignedShort()
            }
            165 -> anInt2831 = buffer.readUnsignedByte()
            168 -> anInt2862 = buffer.readUnsignedByte()
            // Opcode 169: hasTint = false (boolean flag, no data bytes)
            169 -> { }
            in 170..175 -> buffer.readUnsignedShort() // actionCursors
            176 -> repeat(6) { buffer.readSmart() } // aabbBounds (signedSmart)
            178 -> { } // placeholder flag
            179 -> repeat(6) { buffer.readSmart() } // signedSmart array
            180 -> buffer.skip(1) // unknown byte
            181 -> buffer.skip(3) // 3 bytes
            182, 185 -> { } // boolean flags
            183, 184 -> buffer.skip(1) // unknown bytes
            186 -> {
                // Modern multivar transforms - complex skip
                buffer.readUnsignedShort() // unknown short
                buffer.readUnsignedShort() // varpBit
                buffer.readUnsignedShort() // varp
                val flags = buffer.readByte()
                if (flags and 1 != 0) {
                    val len = buffer.readUnsignedByte()
                    for (i in 0 until len) {
                        buffer.skip(1) // value byte
                        val len2 = buffer.readUnsignedByte()
                        for (j in 0 until len2) {
                            buffer.readUnsignedShort() // short
                            buffer.readUnsignedShort() // short
                            buffer.readBigSmart() // smartInt
                            val n = buffer.readUnsignedByte()
                            if (n >= 1) buffer.skip(1)
                            if (n >= 2) buffer.skip(1)
                            if (n >= 3) buffer.skip(1)
                        }
                    }
                }
                if (flags and 2 != 0) {
                    val len = buffer.readUnsignedByte()
                    for (i in 0 until len) {
                        buffer.skip(1)
                        val len2 = buffer.readUnsignedByte()
                        for (j in 0 until len2) {
                            buffer.readUnsignedShort()
                            buffer.readUnsignedShort()
                            buffer.readBigSmart()
                        }
                    }
                }
                if (flags and 4 != 0) {
                    val len = buffer.readUnsignedByte()
                    for (i in 0 until len) {
                        buffer.skip(1)
                        val len2 = buffer.readUnsignedByte()
                        for (j in 0 until len2) {
                            buffer.skip(8) // 4 shorts
                        }
                    }
                }
                if (flags and 8 != 0) {
                    val len = buffer.readUnsignedByte()
                    for (i in 0 until len) {
                        buffer.skip(1)
                        val len2 = buffer.readUnsignedByte()
                        for (j in 0 until len2) {
                            buffer.skip(8) // 4 shorts
                        }
                    }
                }
                if (flags and 16 != 0) {
                    val len = buffer.readUnsignedByte()
                    for (i in 0 until len) {
                        buffer.skip(1)
                        buffer.skip(8) // 2 shorts + 4 bytes
                    }
                }
                buffer.readUnsignedShort() // default transform id
            }
            249 -> readParameters(buffer)
            252 -> buffer.skip(2) // unknown short
            253 -> buffer.skip(1) // unknown byte
            else -> { }
        }
    }

}