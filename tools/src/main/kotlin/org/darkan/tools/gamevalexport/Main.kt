package org.darkan.tools.gamevalexport

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import world.gregs.voidps.cache.gameval.GamevalIndex
import world.gregs.voidps.cache.gameval.GamevalIndexDecoder
import world.gregs.voidps.cache.sqlite.SQLiteCache
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDate
import kotlin.io.path.exists
import kotlin.system.exitProcess

/**
 * Generates our own prettified gameval JSON from a real cache **index 67** (gameval / RSCM), decoded
 * via [GamevalIndexDecoder]. One `<type>.json` per gameval type, sorted by id, with metadata. Reads a
 * cache dir READ-ONLY (default `./data/betacache`, fetched by `betaScanner --download 67`) and never
 * touches the live cache. Only the 35 index-67 types are (re)written; other files (e.g. varbit.json,
 * which index 67 does not contain) are left untouched.
 *
 * Run: `./gradlew :tools:gamevalExport -Pargs="--cache ./data/betacache --out re-resources/gamevals"`
 */
fun main(args: Array<String>) {
    var cacheDir = "./data/betacache"
    var outDir = "re-resources/gamevals"
    var revision = 947
    var source = "content.beta.runescape.com"

    var i = 0
    while (i < args.size) {
        when (val a = args[i]) {
            "--cache" -> cacheDir = args[++i]
            "--out" -> outDir = args[++i]
            "--revision" -> revision = args[++i].toInt()
            "--source" -> source = args[++i]
            "--help", "-h" -> { printUsage(); return }
            else -> { System.err.println("Unknown arg: $a"); printUsage(); exitProcess(2) }
        }
        i++
    }

    val cachePath = Path.of(cacheDir).toAbsolutePath().normalize()
    val indexFile = cachePath.resolve("js5-${GamevalIndex.INDEX}.jcache")
    if (!indexFile.exists()) {
        System.err.println("No $indexFile — fetch index 67 first: ./gradlew :tools:betaScanner -Pargs=\"--host content.beta.runescape.com --major 947 --token <t> --download 67\"")
        exitProcess(1)
    }
    val out = Path.of(outDir).toAbsolutePath().normalize()
    Files.createDirectories(out)

    println("==================== Gameval JSON export ====================")
    println("  cache (read-only) : $cachePath")
    println("  out               : $out")
    println("  revision / source : $revision / $source")
    println("=============================================================")

    val cache = SQLiteCache.load(cachePath)
    val decoder = GamevalIndexDecoder()
    val all = decoder.decode(cache)
    val components = decoder.decodeComponents(cache)
    val date = LocalDate.now().toString()
    val json = Json { prettyPrint = true; prettyPrintIndent = "  " }

    var totalEntries = 0
    for ((archive, type) in GamevalIndex.TYPE_BY_ARCHIVE) {
        val sorted: List<Pair<String, String>> = if (type == GamevalIndex.COMPONENT) {
            components.entries
                .sortedWith(compareBy({ it.key.substringBefore(':').toIntOrNull() ?: 0 }, { it.key.substringAfter(':').toIntOrNull() ?: 0 }))
                .map { it.key to it.value }
        } else {
            (all[type] ?: emptyMap()).entries.sortedBy { it.key }.map { it.key.toString() to it.value }
        }
        val obj = buildJsonObject {
            put("revision", revision)
            put("source", "$source cache index ${GamevalIndex.INDEX} archive $archive")
            put("date", date)
            putJsonObject("entries") { for ((k, v) in sorted) put(k, v) }
        }
        val file = out.resolve("$type.json")
        Files.writeString(file, json.encodeToString(JsonObject.serializer(), obj))
        println("  wrote %-18s %7d entries".format(type, sorted.size))
        totalEntries += sorted.size
    }
    println("Done: ${GamevalIndex.TYPE_BY_ARCHIVE.size} types, $totalEntries entries -> $out")
}

private fun printUsage() {
    println(
        """
        Gameval JSON exporter — decode cache index ${GamevalIndex.INDEX} (gameval/RSCM) into prettified per-type JSON.
          --cache <dir>     cache dir holding js5-${GamevalIndex.INDEX}.jcache (default ./data/betacache; read-only)
          --out <dir>       output dir for <type>.json (default re-resources/gamevals)
          --revision <n>    revision stamped into the json (default 947)
          --source <s>      source stamped into the json (default content.beta.runescape.com)
        Only the ${GamevalIndex.INDEX}-index types are written; other gameval files are left untouched.
        """.trimIndent(),
    )
}
