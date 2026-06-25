package com.undercut.cache.type.bas

import com.undercut.cache.Cache
import com.undercut.cache.Index
import com.undercut.cache.getSmartInt
import com.undercut.cache.skip
import com.undercut.cache.type.TypeParser
import java.nio.ByteBuffer

class BASTypeParser : TypeParser<BASType>() {

    override fun getIndex(): Index = Index.CONFIG

    override fun getArchiveId(id: Int): Int = 32

    override fun getFileId(id: Int): Int = id

    override fun getArchiveBitSize(): Int = 1

    override fun getMaxId(): Int {
        return Cache.get()
            .getArchive(getIndex(), getArchiveId(0))
            .files.keys.maxOrNull() ?: 0
    }

    override fun decode(id: Int, buffer: ByteBuffer): BASType {
        val def = BASType(id = id)
        while (buffer.hasRemaining()) {
            when (val opcode = buffer.get().toInt() and 0xFF) {
                0 -> break
                1 -> {
                    def.standAnim = buffer.getSmartInt()
                    def.standTurnAnim = buffer.getSmartInt()
                }
                2 -> def.walkAnim = buffer.getSmartInt()
                3 -> def.runAnim = buffer.getSmartInt()
                4 -> def.turnAroundAnim = buffer.getSmartInt()
                5 -> def.turnRightAnim = buffer.getSmartInt()
                6 -> def.walkBackAnim = buffer.getSmartInt()
                7 -> def.walkLeftAnim = buffer.getSmartInt()
                8 -> def.walkRightAnim = buffer.getSmartInt()
                9 -> def.crawlAnim = buffer.getSmartInt()
                26 -> {
                    def.renderOffsetX = (buffer.get().toInt() and 0xFF) shl 2
                    def.renderOffsetY = (buffer.get().toInt() and 0xFF) shl 2
                }
                27 -> {
                    val count = buffer.get().toInt() and 0xFF
                    buffer.skip(count * 2)
                }
                28 -> {
                    val count = buffer.get().toInt() and 0xFF
                    buffer.skip(count)
                }
                29, 31, 34, 37 -> buffer.skip(1)
                30, 32, 33, 35, 36 -> buffer.skip(2)
                38 -> def.anim38 = buffer.getSmartInt()
                39 -> def.anim39 = buffer.getSmartInt()
                40 -> def.anim40 = buffer.getSmartInt()
                41 -> def.anim41 = buffer.getSmartInt()
                42 -> def.anim42 = buffer.getSmartInt()
                43 -> def.anim43 = buffer.getSmartInt()
                44 -> def.anim44 = buffer.getSmartInt()
                45 -> def.field45 = buffer.short.toInt() and 0xFFFF
                46 -> def.anim46 = buffer.getSmartInt()
                47 -> def.anim47 = buffer.getSmartInt()
                48 -> def.anim48 = buffer.getSmartInt()
                49 -> def.anim49 = buffer.getSmartInt()
                50 -> def.anim50 = buffer.getSmartInt()
                51 -> def.anim51 = buffer.getSmartInt()
                52 -> {
                    val count = buffer.get().toInt() and 0xFF
                    repeat(count) {
                        buffer.getSmartInt() // anim ID
                        val flags = buffer.get().toInt() and 0xFF
                        val subCount = buffer.get().toInt() and 0xFF
                        repeat(subCount) {
                            buffer.get() // sub value
                        }
                    }
                }
                53 -> Unit // flag = false
                54 -> {
                    def.renderOffsetX2 = (buffer.get().toInt() and 0xFF) shl 2
                    def.renderOffsetY2 = (buffer.get().toInt() and 0xFF) shl 2
                }
                55 -> buffer.skip(3)
                56 -> buffer.skip(7)
                else -> throw IllegalArgumentException("Invalid BASType opcode $opcode")
            }
        }
        return def
    }
}
