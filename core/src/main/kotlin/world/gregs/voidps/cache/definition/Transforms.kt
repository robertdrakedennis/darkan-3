package world.gregs.voidps.cache.definition

import world.gregs.voidps.buffer.read.Reader
import world.gregs.voidps.buffer.write.Writer

interface Transforms {
    var varbit: Int
    var varp: Int
    var transforms: IntArray?

    fun readTransforms(buffer: Reader, isLast: Boolean) {
        varbit = buffer.readUnsignedShort()
        if (varbit == 65535) {
            varbit = -1
        }
        varp = buffer.readUnsignedShort()
        if (varp == 65535) {
            varp = -1
        }
        var last = -1
        if (isLast) {
            // Legacy uses readBigSmart() for the "last" object ID
            last = buffer.readBigSmart()
        }
        // RS3 modern: uses readSmart() for transform count
        val length = buffer.readSmart()
        transforms = IntArray(length + 2)
        for (count in 0..length) {
            // Legacy uses readBigSmart() for transform IDs
            transforms!![count] = buffer.readBigSmart()
        }
        transforms!![length + 1] = last
    }


    fun writeTransforms(writer: Writer, smaller: Int, larger: Int) {
        val configIds = transforms
        if (configIds != null && (varbit != -1 || varp != -1)) {
            val last = configIds.last()
            val extended = last != -1
            writer.writeByte(if (extended) larger else smaller)
            writer.writeShort(varbit)
            writer.writeShort(varp)

            if (extended) {
                writer.writeShort(last)
            }
            writer.writeByte(configIds.size - 2)
            for (i in 0 until configIds.size - 1) {
                writer.writeShort(configIds[i])
            }
        }
    }
}