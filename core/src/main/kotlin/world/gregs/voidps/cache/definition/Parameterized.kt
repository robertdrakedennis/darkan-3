package world.gregs.voidps.cache.definition

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap
import world.gregs.voidps.buffer.read.Reader
import world.gregs.voidps.buffer.write.Writer

interface Parameterized {

    var params: Map<Int, Any>?

    /**
     * Reads a typed int [params] entry by [id], returning [default] when the
     * entry is absent or not an int. Public, uniform accessor shared by every
     * [Parameterized] definition (item/obj/struct/quest/...).
     */
    fun getParamInt(id: Int, default: Int = 0): Int = params?.get(id) as? Int ?: default

    /**
     * Reads a typed string [params] entry by [id], returning [default] when the
     * entry is absent or not a string. Public, uniform accessor shared by every
     * [Parameterized] definition (item/obj/struct/quest/...).
     */
    fun getParamString(id: Int, default: String? = null): String? = params?.get(id) as? String ?: default

    fun readParameters(buffer: Reader) {
        val length = buffer.readUnsignedByte()
        if (length == 0) {
            return
        }
        val params = Int2ObjectArrayMap<Any>()
        for (i in 0 until length) {
            val string = buffer.readUnsignedBoolean()
            val id = buffer.readUnsignedMedium()
            params[id] = if (string) buffer.readString() else buffer.readInt()
        }
        this.params = params
    }

    fun writeParameters(writer: Writer) {
        params?.let { params ->
            writer.writeByte(249)
            writer.writeByte(params.size)
            params.forEach { (id, value) ->
                writer.writeByte(value is String)
                writer.writeMedium(id)
                if (value is String) {
                    writer.writeString(value)
                } else if (value is Int) {
                    writer.writeInt(value)
                }
            }
        }
    }
}