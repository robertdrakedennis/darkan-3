package org.darkan.world.server

import world.gregs.voidps.buffer.write.BufferWriter
import world.gregs.voidps.cache.Cache
import world.gregs.voidps.cache.config.decoder.ClientVariableParameterDecoder

data class ServerClientVarBlock(
    val ack: ServerClientVarAck = ServerClientVarAck.Continue,
    val entries: List<ServerClientVarEntry> = emptyList(),
) {
    fun encode(definitions: ServerClientVarDefinitions = ServerClientVarDefinitions.Unchecked): ByteArray {
        val out = BufferWriter(1 + entries.sumOf { it.encodedSize })
        out.writeByte(ack.wireValue)
        for (entry in entries) {
            require(entry.id in 0..0xFFFF) { "server-client-var id out of range: ${entry.id}" }
            definitions.typeOf(entry.id)?.let { expected ->
                require(expected == entry.value.type) {
                    "server-client-var ${entry.id} is $expected, got ${entry.value.type}"
                }
            }
            out.writeShort(entry.id)
            entry.value.writeTo(out)
        }
        return out.toArray()
    }
}

enum class ServerClientVarAck(val wireValue: Int) {
    Continue(1),
    ReadNextBlock(0),
}

data class ServerClientVarEntry(
    val id: Int,
    val value: ServerClientVarValue,
) {
    val encodedSize: Int get() = 2 + value.encodedSize
}

sealed interface ServerClientVarValue {
    val type: ServerClientVarType
    val encodedSize: Int
    fun writeTo(out: BufferWriter)

    data class IntValue(val value: Int) : ServerClientVarValue {
        override val type = ServerClientVarType.Int
        override val encodedSize = 4
        override fun writeTo(out: BufferWriter) = out.writeInt(value)
    }

    data class LongValue(val value: Long) : ServerClientVarValue {
        override val type = ServerClientVarType.Long
        override val encodedSize = 8
        override fun writeTo(out: BufferWriter) = out.writeLong(value)
    }

    data class StringValue(val value: String) : ServerClientVarValue {
        override val type = ServerClientVarType.String
        override val encodedSize = value.toByteArray(Charsets.ISO_8859_1).size + 1
        override fun writeTo(out: BufferWriter) = out.writeString(value)
    }
}

enum class ServerClientVarType {
    Int,
    Long,
    String,
}

interface ServerClientVarDefinitions {
    fun typeOf(id: Int): ServerClientVarType?

    object Unchecked : ServerClientVarDefinitions {
        override fun typeOf(id: Int): ServerClientVarType? = null
    }

    class CacheBacked(cache: Cache = Cache.get()) : ServerClientVarDefinitions {
        private val definitions = ClientVariableParameterDecoder().load(cache)

        override fun typeOf(id: Int): ServerClientVarType? {
            val definition = definitions.getOrNull(id) ?: return null
            return when (definition.aChar3210) {
                's' -> ServerClientVarType.String
                'l' -> ServerClientVarType.Long
                else -> ServerClientVarType.Int
            }
        }
    }
}

object ServerClientVarBaseline {
    private val firstLightEntries: List<ServerClientVarEntry> by lazy {
        firstLightInts.toList().chunked(2).map { (id, value) ->
            ServerClientVarEntry(id, ServerClientVarValue.IntValue(value))
        }
    }

    fun firstLight(): ServerClientVarBlock = ServerClientVarBlock(entries = firstLightEntries)

    private val firstLightInts = intArrayOf(
        4109, 536937475, 4110, 134678021, 4111, -2088401878,
        4112, 35464, 5139, -2146664148, 5140, 2048,
        4116, 1, 4120, 620756991, 5155, 82560,
        4646, -2129022716, 4647, 16777216, 5160, 1597647,
        5161, 16777215, 4154, 377032, 4155, 10186752,
        5192, -2147176332, 5193, 2048, 8267, 2992,
        4684, -2147360683, 8268, 1812, 4685, 4853759,
        4705, -2147237488, 4706, 2048, 4723, 1597647,
        4724, 16777215, 6277, 1474767, 3721, 100992003,
        3722, -13304057, 3723, 4198400, 6296, 1344405503,
        4764, -2146090718, 4765, 8392703, 4254, -2147352464,
        6302, -1, 4255, 1075, 6304, -2146041344,
        6305, 8390656, 6323, -2146336488, 6324, 200,
        3769, 385875968, 3770, 436207616, 4794, 369098519,
        4798, -2146377458, 4813, -2146897680, 4814, 1805193,
        5840, 1597647, 5841, 16777215, 3295, -1610612736,
        4322, 1597647, 4323, 16777215, 4324, 1597647,
        4325, 16777215, 6417, -2147098374, 6418, 13493424,
        2852, 319951120, 2853, 387323156, 2854, 454695192,
        6439, -2146254592, 2855, 572588031, 2856, 319951120,
        2857, -236, 2858, 385833471, 2859, -268435456,
        2860, -1010826241, 2862, -1, 2863, 704643071,
        2864, 642008373, 2865, -13162457, 2866, -1,
        2867, 589579832, 2868, 842019105, 6964, -1,
        2869, 33646952, 6457, -2145025248, 6458, 8390656,
        5947, -2147098374, 5948, 16775168, 4939, -2146135748,
        4954, -2130378572, 4955, 16780124, 2912, 32,
        2913, -2130706433, 2914, 8390656, 2915, -2147155691,
        2916, 16777215, 2917, -2146635569, 2918, 4095,
        2919, -2130394606, 2920, 29200384, 2921, -2146520558,
        2922, 352317440, 2923, -2145849067, 2924, 79429631,
        2925, -2144451968, 2926, 2048, 2927, 318767104,
        2928, 352321536, 2929, 335544320, 2930, 369098752,
        2931, 352321536, 2932, 385875968, 6005, 1474767,
        2933, 51806415, 2934, 27865087, 6006, 704643072,
        2935, -2146885110, 2936, 15155700, 2937, -2146041344,
        2938, 8390656, 2939, 1597647, 2940, 16777215,
        2941, 84623567, 2942, 8392703, 2943, 287005,
        2944, 8388608, 2945, 737487, 2946, 142610431,
        2947, 118177999, 2948, 109055999, 2949, 134955215,
        4997, -1, 2950, 159387647, 4998, -1,
        2951, 101400783, 4999, -1, 2952, 92278783,
        5000, -1, 5001, -1, 3465, 8388608,
        5002, -1, 2955, 1102022, 5003, -1,
        2956, 16777215, 5004, -1, 2957, 436207616,
        5005, -1, 5006, -1, 2959, 68743445,
        5007, -1, 2960, 263979007, 5008, -1,
        2961, 151732431, 5009, -1, 2962, 226496511,
        5010, -1, 2963, 369098752, 2964, 402653184,
        2965, 1597647, 5014, -2130550254, 2966, 16777215,
        5015, 26345472, 2967, 1597647, 5016, -2130550254,
        2968, 16777215, 5017, 25513984, 2969, 18411797,
        5018, -2112843700, 2970, 12320767, 5019, 46607404,
        2971, 1597647, 5020, -2112843700, 2972, 16777215,
        5021, 46607100, 2973, -2146868998, 6046, -2147110729,
        2974, 12953600, 4510, -51969, 6047, 4196352,
        4511, 402653184, 2975, -2147348352, 4512, 167772160,
        2976, 5607423, 4513, -2146827988, 4514, 8390656,
        4515, 1597647, 2979, -2146664148, 4516, 16777215,
        2980, 4196352, 2981, -2146303776, 2982, 8392703,
        2983, -2147348326, 2984, 3073, 2985, 1597647,
        2986, 16777215, 2987, 307380, 2988, 1805193,
        2989, -2147299198, 2990, 1340, 2991, -2144820534,
        2992, 8390656, 2993, -2146807458, 2994, 4196352,
        2995, 2360096, 2996, 8390656, 6102, -1,
        5079, -1, 6103, -1, 5080, 268435456,
        5081, 1597647, 5082, 16777215, 6108, 1597647,
        6620, -1, 6621, -2147246022, 6109, 16777215,
        6622, 13276630, 6110, 1597647, 6111, 16777215,
        6112, 1597647, 6113, 16777215, 6114, 1597647,
        6115, 16777215, 6116, 1597647, 6117, 16777215,
        6118, 1597647, 6119, 16777215, 8181, 16777215,
        7159, 255, 8184, 963090, 7160, -1,
        8185, 335540224,
    )
}
