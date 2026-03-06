package world.gregs.voidps.cache.definition.decoder

import world.gregs.voidps.buffer.read.Reader
import world.gregs.voidps.cache.Cache
import world.gregs.voidps.cache.Config.RENDER_ANIMATIONS
import world.gregs.voidps.cache.DefinitionDecoder
import world.gregs.voidps.cache.Index
import world.gregs.voidps.cache.definition.data.BASDefinition

/**
 * Decodes Base Animation Set (BAS / Render Animations) definitions from the
 * CONFIGS index (2), archive 32.
 *
 * BAS definitions store movement animation sets for players and NPCs,
 * including stand, walk, run, and teleport animations with directional
 * variants and turning animations.
 */
class BASDecoder : DefinitionDecoder<BASDefinition>(Index.CONFIGS) {

    override fun create(size: Int) = Array(size) { BASDefinition(it) }

    override fun getArchive(id: Int) = RENDER_ANIMATIONS

    override fun size(cache: Cache): Int {
        return cache.lastFileId(Index.CONFIGS, RENDER_ANIMATIONS)
    }

    override fun BASDefinition.read(opcode: Int, buffer: Reader) {
        when (opcode) {
            // Opcode 1: stand + walk animations (two bigSmarts)
            1 -> {
                standAnimation = buffer.readBigSmart()
                walkAnimation = buffer.readBigSmart()
            }
            // Opcode 2-9: teleport and run animations
            2 -> teleportAnimation = buffer.readBigSmart()
            3 -> teleDir3 = buffer.readBigSmart()
            4 -> teleDir2 = buffer.readBigSmart()
            5 -> teleDir1 = buffer.readBigSmart()
            6 -> runAnimation = buffer.readBigSmart()
            7 -> runDir3 = buffer.readBigSmart()
            8 -> runDir2 = buffer.readBigSmart()
            9 -> runDir1 = buffer.readBigSmart()
            // Opcode 26: modelWidth and modelLength (2 unsigned bytes, each * 4)
            26 -> {
                buffer.readUnsignedByte() // modelWidth * 4
                buffer.readUnsignedByte() // modelLength * 4
            }
            // Opcode 27: skip count * 2 bytes
            27 -> {
                val count = buffer.readUnsignedByte()
                buffer.skip(count * 2)
            }
            // Opcode 28: per-slot obj visibility array
            28 -> {
                val count = buffer.readUnsignedByte()
                repeat(count) {
                    buffer.readUnsignedByte() // visibility value
                }
            }
            29, 31, 34, 37 -> buffer.skip(1) // single byte fields
            30, 32, 33, 35, 36 -> buffer.skip(2) // short fields
            // Opcodes 38-42: stand turn and walk directional animations
            38 -> standTurn1 = buffer.readBigSmart()
            39 -> standTurn2 = buffer.readBigSmart()
            40 -> walkDir3 = buffer.readBigSmart()
            41 -> walkDir2 = buffer.readBigSmart()
            42 -> walkDir1 = buffer.readBigSmart()
            43 -> buffer.readBigSmart() // anim43
            44 -> buffer.readBigSmart() // anim44
            45 -> buffer.readUnsignedShort() // field45
            // Opcodes 46-51: turn animations for teleport, run, and walk
            46 -> teleTurn1 = buffer.readBigSmart()
            47 -> teleTurn2 = buffer.readBigSmart()
            48 -> runTurn1 = buffer.readBigSmart()
            49 -> runTurn2 = buffer.readBigSmart()
            50 -> walkTurn1 = buffer.readBigSmart()
            51 -> walkTurn2 = buffer.readBigSmart()
            // Opcode 52: random stand sequences with flags and sub-values
            52 -> {
                val count = buffer.readUnsignedByte()
                repeat(count) {
                    buffer.readBigSmart() // animation id
                    val flags = buffer.readUnsignedByte()
                    val subCount = buffer.readUnsignedByte()
                    repeat(subCount) {
                        buffer.readByte() // sub value
                    }
                }
            }
            // Opcode 53: rendersShadow = false (boolean flag, no data bytes)
            53 -> { }
            // Opcode 54: hillRotateX and hillRotateZ (2 unsigned bytes, each << 6)
            54 -> {
                buffer.readUnsignedByte() // hillRotateX
                buffer.readUnsignedByte() // hillRotateZ
            }
            55 -> buffer.skip(3) // 3 bytes
            56 -> buffer.skip(7) // 7 bytes
            else -> { }
        }
    }
}
