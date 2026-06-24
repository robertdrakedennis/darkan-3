package org.darkan.tools.recorder

/**
 * Minimal JSON-object emitter for one-line JSONL records. No external dep — the
 * transcript is flat key→scalar maps, so a tiny hand-rolled serializer keeps the
 * tool self-contained. Strings are escaped; numbers/booleans emitted bare.
 */
object Json {

    /** Build a single-line JSON object from ordered key→value pairs. */
    fun obj(vararg pairs: Pair<String, Any?>): String {
        val sb = StringBuilder(64)
        sb.append('{')
        var first = true
        for ((k, v) in pairs) {
            if (!first) sb.append(',')
            first = false
            sb.append('"').append(escape(k)).append("\":")
            appendValue(sb, v)
        }
        sb.append('}')
        return sb.toString()
    }

    private fun appendValue(sb: StringBuilder, v: Any?) {
        when (v) {
            null -> sb.append("null")
            is Boolean -> sb.append(v)
            is Int, is Long, is Short, is Byte -> sb.append(v.toString())
            else -> sb.append('"').append(escape(v.toString())).append('"')
        }
    }

    /** Lowercase hex of [bytes], truncated to [max] bytes (the rest summarized). */
    fun hex(bytes: ByteArray, max: Int): String {
        if (bytes.isEmpty()) return ""
        val n = minOf(bytes.size, max)
        val sb = StringBuilder(n * 2 + 16)
        for (i in 0 until n) sb.append("%02x".format(bytes[i].toInt() and 0xFF))
        if (bytes.size > n) sb.append("...+").append(bytes.size - n).append("B")
        return sb.toString()
    }

    private fun escape(s: String): String {
        val sb = StringBuilder(s.length + 8)
        for (c in s) {
            when (c) {
                '"' -> sb.append("\\\"")
                '\\' -> sb.append("\\\\")
                '\n' -> sb.append("\\n")
                '\r' -> sb.append("\\r")
                '\t' -> sb.append("\\t")
                else -> if (c < ' ') sb.append("\\u%04x".format(c.code)) else sb.append(c)
            }
        }
        return sb.toString()
    }
}
