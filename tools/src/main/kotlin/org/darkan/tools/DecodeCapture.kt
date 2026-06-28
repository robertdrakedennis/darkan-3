package org.darkan.tools

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.darkan.core.net.prot.revision.rev948.register948
import org.darkan.core.net.recorder.CaptureDeframer
import org.darkan.core.net.recorder.CaptureDeframer.Direction
import org.darkan.core.net.recorder.CapturePacketDecode
import java.io.File
import java.util.Base64

/**
 * Standalone capture deframer / cross-validation deframer.
 *
 * Originally an s2c-only console decoder; now it drives the shared [CaptureDeframer] (the same code
 * the recorder dylib and [org.darkan.tools.recorder.EnrichSession] use) for BOTH directions, and can
 * emit the SAME enriched JSONL so a raw-capture deframe is diffable against the dylib's in-process
 * framing.
 *
 * Usage:
 *   ./gradlew :tools:run -PmainClass=org.darkan.tools.DecodeCaptureKt -Pargs="<dir> [s2c|c2s] [--jsonl <out.jsonl>]"
 *
 *   <dir>   capture directory holding raw-s2c.bin / raw-c2s.bin + isaac-keys.txt
 *           (defaults to the newest subdir of ./capture)
 *   s2c|c2s direction to deframe (default s2c)
 *   --jsonl write enriched JSONL (the EnrichSession shape) instead of the console summary
 */
fun main(args: Array<String>) {
    var dir: String? = null
    var direction = Direction.S2C
    var jsonlOut: String? = null

    var i = 0
    while (i < args.size) {
        when (val a = args[i]) {
            "s2c", "S2C" -> direction = Direction.S2C
            "c2s", "C2S" -> direction = Direction.C2S
            "--jsonl" -> jsonlOut = args[++i]
            else -> dir = a
        }
        i++
    }

    val captureDir = dir ?: run {
        val captures = File("capture")
        captures.listFiles()?.filter { it.isDirectory }?.maxByOrNull { it.name }?.absolutePath
            ?: error("No captures found in capture/ — pass a directory explicitly")
    }
    val dirTag = if (direction == Direction.S2C) "s2c" else "c2s"
    println("Decoding $dirTag from: $captureDir")

    val codec = register948()
    val seeds = CaptureDeframer.parseHexKeys(File("$captureDir/isaac-keys.txt").readText())

    val rawFile = File("$captureDir/raw-$dirTag.bin")
    require(rawFile.exists()) { "missing ${rawFile.path}" }
    val raw = rawFile.readBytes()

    val deframer = CaptureDeframer(codec, direction, seeds)

    // s2c stream opens with the pre-prot login prologue (9B first_response + login result + len + data).
    if (direction == Direction.S2C && raw.size >= 11) {
        val loginResult = raw[9].toInt() and 0xFF
        val loginLen = raw[10].toInt() and 0xFF
        deframer.skip(11 + loginLen)
        println("Login result=$loginResult, data=${loginLen}B, post-login at byte ${deframer.position()}")
    }

    val json = Json { encodeDefaults = true }
    val base64 = Base64.getEncoder()
    val writer = jsonlOut?.let { File(it).bufferedWriter() }

    var count = 0
    loop@ while (true) {
        when (val r = deframer.next(raw)) {
            is CaptureDeframer.Result.End -> break@loop
            is CaptureDeframer.Result.Error -> {
                println("\n*** STOP at byte ${r.offset}: ${r.detail}")
                val from = maxOf(0, r.offset - 8)
                val to = minOf(raw.size, r.offset + 24)
                println("    hex: ${raw.slice(from until to).joinToString(" ") { "%02x".format(it.toInt() and 0xFF) }}")
                break@loop
            }
            is CaptureDeframer.Result.Ok -> {
                val f = r.frame
                count++
                if (writer != null) {
                    var decoded: String? = null
                    if (direction == Direction.C2S && !f.xteaBody && CapturePacketDecode.hasClientDecoder(codec, f.opcode)) {
                        decoded = CapturePacketDecode.decodeClient(codec, f.opcode, f.body)
                    }
                    writer.appendLine(frameToJson(json, base64, dirTag, f, decoded))
                } else if (f.opcode !in intArrayOf(10, 111, 28, 61) || count % 100 == 0) {
                    val modeStr = when (f.sizeClass) { -1 -> "vB"; -2 -> "vS"; else -> "f${f.sizeClass}" }
                    println(
                        "[${"%4d".format(count)}] op=${"%3d".format(f.opcode)} " +
                            "(${f.name.take(28).padEnd(28)}) ${"%-4s".format(modeStr)} " +
                            "size=${"%5d".format(f.body.size)} @${"%6d".format(f.offset)}  " +
                            f.body.take(24).joinToString(" ") { "%02x".format(it.toInt() and 0xFF) }
                    )
                }
            }
        }
    }
    writer?.close()

    println("\nTotal: $count packets decoded, ${deframer.position()}/${raw.size} bytes consumed")
    if (jsonlOut != null) println("Wrote enriched JSONL -> $jsonlOut")
}

private fun frameToJson(
    json: Json,
    base64: Base64.Encoder,
    dirTag: String,
    f: CaptureDeframer.Frame,
    decoded: String?,
): String {
    val map = LinkedHashMap<String, JsonElement>()
    map["plane"] = JsonPrimitive("framed")
    map["dir"] = JsonPrimitive(dirTag)
    map["op"] = JsonPrimitive(f.opcode)
    map["len"] = JsonPrimitive(f.body.size)
    map["body"] = JsonPrimitive(base64.encodeToString(f.body))
    if (f.xteaBody) map["xtea_body"] = JsonPrimitive(true)
    map["offset"] = JsonPrimitive(f.offset)
    map["name"] = JsonPrimitive(f.name)
    if (decoded != null) map["decoded"] = JsonPrimitive(decoded)
    return json.encodeToString(JsonObject.serializer(), JsonObject(map))
}
