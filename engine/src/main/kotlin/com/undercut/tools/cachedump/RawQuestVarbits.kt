package com.undercut.tools.cachedump

import world.gregs.voidps.cache.Cache
import world.gregs.voidps.cache.Index
import java.nio.ByteBuffer

// JagString: one version byte (always 0 in current caches), then a null-terminated CP1252
// string. The cache's getString() doesn't skip the leading version byte.
internal fun ByteBuffer.getJagString(): String {
    val ver = get().toInt() and 0xFF
    require(ver == 0) { "Unsupported JagString version $ver at position ${position() - 1}" }
    val start = position()
    while (hasRemaining() && get() != 0.toByte()) Unit
    val end = position() - 1
    val len = end - start
    if (len <= 0) return ""
    val bytes = ByteArray(len)
    val saved = position()
    position(start)
    get(bytes)
    position(saved)
    return String(bytes)
}

data class CacheQuestRecord(
    val id: Int,
    val name: String?,
    val description: String?,
    val varEntries: List<QuestVarEntry>,
    val varEntriesAlt: List<QuestVarEntry>,
)

data class QuestVarEntry(val operator: Int, val varId: Int, val value: Int)

object RawQuestVarbits {
    private const val ARCHIVE_ID = 35

    fun loadAll(): List<CacheQuestRecord> {
        val cache = Cache.get()
        val fileIds = runCatching { cache.files(Index.CONFIGS, ARCHIVE_ID) }.getOrNull() ?: return emptyList()
        val out = mutableListOf<CacheQuestRecord>()
        for (fileId in fileIds) {
            val data = runCatching { cache.data(Index.CONFIGS, ARCHIVE_ID, fileId) }.getOrNull() ?: continue
            val record = runCatching { decode(fileId, ByteBuffer.wrap(data)) }.getOrNull() ?: continue
            out += record
        }
        return out
    }

    fun decode(id: Int, buf: ByteBuffer): CacheQuestRecord {
        var name: String? = null
        var description: String? = null
        val v3 = mutableListOf<QuestVarEntry>()
        val v4 = mutableListOf<QuestVarEntry>()

        try {
            while (buf.hasRemaining()) {
                val opcode = buf.get().toInt() and 0xFF
                when (opcode) {
                    0 -> break
                    1 -> name = buf.getJagString()
                    2 -> description = buf.getJagString()
                    3 -> readVarEntries(buf, v3)
                    4 -> readVarEntries(buf, v4)
                    5 -> buf.short
                    6 -> buf.get()
                    7 -> buf.get()
                    8 -> Unit
                    9 -> buf.get()
                    10 -> repeat(buf.get().toInt() and 0xFF) { buf.int }
                    12 -> buf.int
                    13 -> repeat(buf.get().toInt() and 0xFF) { buf.short }
                    14 -> repeat(buf.get().toInt() and 0xFF) { buf.short }
                    15 -> buf.short
                    17 -> getSmartInt(buf)
                    18, 19 -> {
                        val n = buf.get().toInt() and 0xFF
                        repeat(n) { buf.int; buf.int; buf.int; buf.getJagString() }
                    }
                    249 -> {
                        val count = buf.get().toInt() and 0xFF
                        repeat(count) {
                            val isString = buf.get().toInt() == 1
                            buf.get(); buf.short
                            if (isString) {
                                while (buf.hasRemaining() && buf.get() != 0.toByte()) Unit
                            } else buf.int
                        }
                    }
                    else -> return CacheQuestRecord(id, name, description, v3, v4)
                }
            }
        } catch (_: Throwable) {
            // Decode misaligned mid-record; return whatever we captured (opcodes 1..4 intact).
        }
        return CacheQuestRecord(id, name, description, v3, v4)
    }

    private fun readVarEntries(buf: ByteBuffer, sink: MutableList<QuestVarEntry>) {
        val count = buf.get().toInt() and 0xFF
        repeat(count) {
            val operator = buf.short.toInt() and 0xFFFF
            val varId = buf.int
            val value = buf.int
            sink += QuestVarEntry(operator, varId, value)
        }
    }

    private fun getSmartInt(buf: ByteBuffer): Int =
        if (buf.get(buf.position()) < 0) buf.int and 0x7FFFFFFF
        else buf.short.toInt() and 0xFFFF
}
