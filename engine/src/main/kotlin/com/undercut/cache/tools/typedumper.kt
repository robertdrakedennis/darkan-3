package com.undercut.cache.tools

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.undercut.cache.Cache
import com.undercut.cache.type.TypeParser
import com.undercut.cache.type.bas.BASType
import com.undercut.cache.type.cursors.CursorType
import com.undercut.cache.type.enums.EnumType
import com.undercut.cache.type.headbars.HeadbarType
import com.undercut.cache.type.hitmarks.HitmarkType
import com.undercut.cache.type.idk.IDKType
import com.undercut.cache.type.inventories.InvType
import com.undercut.cache.type.items.ItemType
import com.undercut.cache.type.npcs.NPCType
import com.undercut.cache.type.objects.ObjectType
import com.undercut.cache.type.params.ParamType
import com.undercut.cache.type.quests.QuestType
import com.undercut.cache.type.sequences.SeqType
import com.undercut.cache.type.structs.StructType
import com.undercut.cache.type.vars.VarbitType
import java.io.File

fun main() {
    val dumpDir = File("./dumps/types")
    if (!dumpDir.exists()) dumpDir.mkdirs()

    val gson = GsonBuilder()
        .setPrettyPrinting()
        .disableHtmlEscaping()
        .create()

    println("Cache type dumper — dumping all types to ${dumpDir.absolutePath}")
    println()

    dumpType("items", ItemType.getParser(), dumpDir, gson)
    dumpType("npcs", NPCType.getParser(), dumpDir, gson)
    dumpType("objects", ObjectType.getParser(), dumpDir, gson)
    dumpType("sequences", SeqType.getParser(), dumpDir, gson)
    dumpType("enums", EnumType.getParser(), dumpDir, gson)
    dumpType("structs", StructType.getParser(), dumpDir, gson)
    dumpType("params", ParamType.getParser(), dumpDir, gson)
    dumpType("varbits", VarbitType.getParser(), dumpDir, gson)
    dumpType("bas", BASType.getParser(), dumpDir, gson)
    dumpType("cursors", CursorType.getParser(), dumpDir, gson)
    dumpType("headbars", HeadbarType.getParser(), dumpDir, gson)
    dumpType("hitmarks", HitmarkType.getParser(), dumpDir, gson)
    dumpType("idk", IDKType.getParser(), dumpDir, gson)
    dumpType("inventories", InvType.getParser(), dumpDir, gson)
    dumpType("quests", QuestType.getParser(), dumpDir, gson)

    println("\nAll type dumps complete!")
}

private fun <T> dumpType(name: String, parser: TypeParser<T>, dumpDir: File, gson: Gson) {
    print("Dumping $name... ")
    System.out.flush()

    val cache = Cache.get()
    val entries = linkedMapOf<Int, T>()
    var errors = 0
    val maxId = parser.getMaxId()

    for (id in 0..maxId) {
        try {
            entries[id] = parser.get(cache, id)
        } catch (_: Throwable) {
            errors++
        }
    }

    val file = File(dumpDir, "$name.json")
    file.bufferedWriter().use { writer ->
        gson.toJson(entries, writer)
    }

    println("${entries.size} entries (maxId=$maxId, $errors skipped) -> ${file.name}")
}
