package com.undercut.cache.type.sequences

import com.undercut.cache.Index
import com.undercut.cache.getSmallSmartInt
import com.undercut.cache.getSmartInt
import com.undercut.cache.getTriByte
import com.undercut.cache.skip
import com.undercut.cache.type.TypeParser
import java.nio.ByteBuffer

class SeqTypeParser : TypeParser<SeqType>() {

    override fun getIndex(): Index = Index.SEQ_DEF

    override fun getArchiveBitSize(): Int = 7

    override fun decode(id: Int, buffer: ByteBuffer): SeqType {
        val def = SeqType(id = id)
        while (buffer.hasRemaining()) {
            when (val opcode = buffer.get().toInt() and 0xFF) {
                0 -> break
                1 -> {
                    val count = buffer.short.toInt() and 0xFFFF
                    def.frameIds = IntArray(count)
                    def.frameLengths = IntArray(count)
                    for (i in 0 until count) {
                        def.frameIds!![i] = buffer.short.toInt() and 0xFFFF
                    }
                    for (i in 0 until count) {
                        def.frameLengths!![i] = buffer.short.toInt() and 0xFFFF
                    }
                    for (i in 0 until count) {
                        def.frameLengths!![i] = def.frameLengths!![i] or
                                ((buffer.short.toInt() and 0xFFFF) shl 16)
                    }
                }
                2 -> def.loopDelay = buffer.short.toInt() and 0xFFFF
                3 -> {
                    val count = buffer.getSmallSmartInt()
                    repeat(count) { buffer.getSmallSmartInt() }
                }
                5 -> def.priority = buffer.get().toInt() and 0xFF
                6 -> def.leftHandItem = buffer.short.toInt() and 0xFFFF
                7 -> def.rightHandItem = buffer.short.toInt() and 0xFFFF
                8 -> def.replayMode = buffer.get().toInt() and 0xFF
                9 -> def.walkMerge = buffer.get().toInt() and 0xFF
                10 -> def.priority2 = buffer.get().toInt() and 0xFF
                11 -> def.stretches = buffer.get().toInt() and 0xFF
                12, 112 -> {
                    val count = if (opcode == 12) buffer.get().toInt() and 0xFF
                               else buffer.short.toInt() and 0xFFFF
                    def.soundEffects = IntArray(count)
                    for (i in 0 until count) {
                        def.soundEffects!![i] = buffer.short.toInt() and 0xFFFF
                    }
                    // Read upper 16 bits
                    for (i in 0 until count) {
                        def.soundEffects!![i] = def.soundEffects!![i] or
                                ((buffer.short.toInt() and 0xFFFF) shl 16)
                    }
                }
                13 -> {
                    val count = buffer.short.toInt() and 0xFFFF
                    repeat(count) {
                        val subCount = buffer.get().toInt() and 0xFF
                        if (subCount != 0) {
                            buffer.getTriByte() // first sound ID
                            if (subCount > 1) {
                                repeat(subCount - 1) {
                                    buffer.short // additional sound IDs
                                }
                            }
                        }
                    }
                }
                19, 119 -> {
                    val index = if (opcode == 19) buffer.get().toInt() and 0xFF
                               else buffer.short.toInt() and 0xFFFF
                    buffer.get() // merge value
                }
                20, 120 -> {
                    val index = if (opcode == 20) buffer.get().toInt() and 0xFF
                               else buffer.short.toInt() and 0xFFFF
                    buffer.short // sound start
                    buffer.short // sound end
                }
                22 -> buffer.skip(1)
                23 -> buffer.skip(2)
                24 -> buffer.skip(2) // animMaya ID
                25 -> def.field25 = buffer.short.toInt() and 0xFFFF
                26 -> {
                    def.field26a = buffer.short.toInt() and 0xFFFF
                    def.field26b = buffer.short.toInt() and 0xFFFF
                }
                27 -> def.field27 = buffer.get().toInt() // signed byte
                249 -> def.params.parse(buffer)
                else -> throw IllegalArgumentException("Invalid SeqType opcode $opcode")
            }
        }
        return def
    }
}
