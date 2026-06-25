package com.undercut.cache

import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.nio.ByteBuffer

fun ByteBuffer.getFormatInt(cacheFormat: Int): Int =
    if (cacheFormat >= 7) getSmartInt() else getShort().toInt() and 0xFFFF

fun ByteBuffer.putSmartInt(value: Int) {
    if (value >= Short.MAX_VALUE) {
        putInt(value - Integer.MAX_VALUE - 1)
    } else {
        putShort(if (value >= 0) value.toShort() else Short.MAX_VALUE)
    }
}

fun ByteBuffer.getUnsignedSmart(): Int {
    val i = get().toInt() and 0xFF
    return if (i >= 0x80) (i - 0x80 shl 8) or (get().toInt() and 0xFF) else i
}

fun ByteBuffer.readMasked(): Map<Int, Int> {
    val result = mutableMapOf<Int, Int>()
    var mask = this.get().toInt() and 0xff
    while (mask > 0) {
        if (mask and 0x1 == 1) {
            result[getSmartInt()] = getDecrSmart()
        } else {
            result[0] = 0
        }
        mask /= 2
    }
    return result
}

fun ByteBuffer.getSmartInt(): Int =
    if (get(position()) < 0) int and 0x7FFFFFFF else short.toInt() and 0xFFFF

fun ByteBuffer.getString(): String {
    val startPos = position()
    while (true) {
        if (get() == 0.toByte()) break
    }
    val length = position() - startPos - 1
    if (length == 0) return ""
    return ByteArray(length).also {
        position(startPos)
        get(it)
        position(position() + 1)
    }.let { String(it) }
}

fun ByteBuffer.skip(bytes: Int) = position(position() + bytes)

fun ByteBuffer.getSmallSmartInt(): Int =
    if ((get(position()).toInt() and 0xFF) < 128) get().toInt() and 0xFF
    else (short.toInt() and 0xFFFF) - 0x8000

fun ByteBuffer.getSignedSmart(): Int {
    val peek = get(position()).toInt() and 0xFF
    return if (peek < 128) {
        (get().toInt() and 0xFF) - 64
    } else {
        val ushort = short.toInt() and 0xFFFF
        (ushort + 0x4000).toShort().toInt()
    }
}

fun ByteBuffer.getTriByte(): Int =
    (get().toInt() and 0xFF shl 16) or (get().toInt() and 0xFF shl 8) or (get().toInt() and 0xFF)

fun ByteBuffer.getDecrSmart(): Int {
    val first = get().toInt() and 0xFF
    return if (first < 128) first - 1
    else (first shl 8 or (get().toInt() and 0xFF)) - 0x8000 - 1
}

fun ByteBuffer.getSmartSizeVar(): Int {
    var total = 0
    var current = getUnsignedSmart()
    while (current == 32767) {
        total += current
        current = getUnsignedSmart()
    }
    return total + current
}

fun ByteBuffer.wrap(): ByteBuffer {
    val buf = ByteBuffer.allocate(5 + remaining())
    buf.put(Compression.NONE.ordinal.toByte())
    buf.putInt(remaining())
    buf.put(this)
    buf.flip()
    return buf
}

fun ByteArray.wrap(): ByteBuffer {
    val buf = ByteBuffer.allocate(5 + size)
    buf.put(Compression.NONE.ordinal.toByte())
    buf.putInt(size)
    buf.put(this)
    buf.flip()
    return buf
}

fun String.toFilesystemHash(): Int {
    var c = 0.toChar()
    for (index in indices) {
        c = ((c.code shl 5) - c.code + get(index).charToCp1252().toInt()).toChar()
    }
    return c.code
}

private val HEX_ARRAY = "0123456789ABCDEF".toCharArray()

fun ByteArray.bytesToHex(): String {
    val hexChars = CharArray(size * 2)
    for (j in indices) {
        val v = get(j).toInt() and 0xFF
        hexChars[j * 2] = HEX_ARRAY[v ushr 4]
        hexChars[j * 2 + 1] = HEX_ARRAY[v and 0x0F]
    }
    return String(hexChars)
}

private val CP1252_VALS = charArrayOf(
    '\u20ac', '\u0000', '\u201a', '\u0192', '\u201e', '\u2026', '\u2020', '\u2021',
    '\u02c6', '\u2030', '\u0160', '\u2039', '\u0152', '\u0000', '\u017d', '\u0000',
    '\u0000', '\u2018', '\u2019', '\u201c', '\u201d', '\u2022', '\u2013', '\u2014',
    '\u02dc', '\u2122', '\u0161', '\u203a', '\u0153', '\u0000', '\u017e', '\u0178'
)

fun Byte.cp1252ToChar(): Char {
    var cp1252 = toInt() and 0xFF
    if (cp1252 == 0) throw IllegalArgumentException("Non cp1252 character provided")
    if (cp1252 in 128..159) {
        var translated = CP1252_VALS[cp1252 - 128].code
        if (translated == 0) {
            translated = 63
        }
        cp1252 = translated
    }
    return cp1252.toChar()
}

fun Char.charToCp1252(): Byte {
    return when (this) {
        in '\u0001'..'\u007f', in '\u00a0'..'\u00ff' -> code.toByte()
        '\u20ac' -> -128
        '\u201a' -> -126
        '\u0192' -> -125
        '\u201e' -> -124
        '\u2026' -> -123
        '\u2020' -> -122
        '\u2021' -> -121
        '\u02c6' -> -120
        '\u2030' -> -119
        '\u0160' -> -118
        '\u2039' -> -117
        '\u0152' -> -116
        '\u017d' -> -114
        '\u2018' -> -111
        '\u2019' -> -110
        '\u201c' -> -109
        '\u201d' -> -108
        '\u2022' -> -107
        '\u2013' -> -106
        '\u2014' -> -105
        '\u02dc' -> -104
        '\u2122' -> -103
        '\u0161' -> -102
        '\u203a' -> -101
        '\u0153' -> -100
        '\u017e' -> -98
        '\u0178' -> -97
        else -> 63
    }
}

fun archiveId(id: Int, size: Int): Int = id shr size
fun fileId(id: Int, size: Int): Int = id and (1 shl size) - 1

fun getFieldValue(`object`: Any, field: Field): Any? {
    field.isAccessible = true
    val type = field.type
    return when {
        type == Array<IntArray>::class.java -> (field.get(`object`) as Array<*>).contentDeepToString()
        type.isArray && Map::class.java.isAssignableFrom(type.componentType) -> {
            (field.get(`object`) as Array<*>).contentToString()
        }
        else -> field.get(`object`)
    }
}

fun deepToString(`object`: Any): String {
    val result = StringBuilder()
    val newLine = System.lineSeparator()

    result.append(`object`::class.java.name)
    result.append(" {")
    result.append(newLine)

    val fields = `object`::class.java.declaredFields

    for (field in fields) {
        if (Modifier.isStatic(field.modifiers)) continue
        result.append("  ")
        try {
            result.append("${field.type.canonicalName} ${field.name}: ")
            result.append(getFieldValue(`object`, field))
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }
        result.append(newLine)
    }
    result.append("}")

    return result.toString()
}