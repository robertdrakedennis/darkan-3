package com.undercut.tools.cachedump

import com.undercut.cache.Cache
import com.undercut.cache.Index
import java.io.File

object DumpQuestVarbitsMain {
    @JvmStatic
    fun main(args: Array<String>) {
        val engineRoot = File(System.getProperty("user.dir")).absoluteFile
        val out = File(engineRoot, "developer-info/quest-varbits-dump.txt")
        val records = RawQuestVarbits.loadAll().sortedBy { it.id }
        println("[dump-quest-varbits] decoded ${records.size} quest records from cache")

        val firstOpcodes = mutableMapOf<Int, Int>()
        val cache = Cache.get()
        val archive = cache.getArchive(Index.CONFIG, 35)
        for ((_, file) in archive.files) {
            if (file.data.isEmpty()) continue
            val first = file.data[0].toInt() and 0xFF
            firstOpcodes[first] = (firstOpcodes[first] ?: 0) + 1
        }
        println("[dump-quest-varbits] First-byte (opcode) distribution across ${archive.files.size} files:")
        firstOpcodes.entries.sortedBy { it.key }.forEach { (op, n) ->
            println("  opcode 0x%02x (%3d)  %5d files".format(op, op, n))
        }

        println("\n[dump-quest-varbits] Sample raw bytes (first 96) for selected ids:")
        for (id in listOf(0, 1, 6, 66, 173)) {
            val data = archive.files[id]?.data ?: continue
            val hex = data.take(96).joinToString(" ") { "%02x".format(it.toInt() and 0xFF) }
            println("  id=$id (size=${data.size}): $hex")
        }

        val lines = mutableListOf<String>()
        lines += "# Per-quest MasterQuestVar dump from cache opcode 3 (primary) and opcode 4 (alt)."
        lines += "# Format: <questId>  <name>  | op3=[(operator,varId,value)...]  op4=[...]"
        lines += "#"

        var withEntries = 0
        for (q in records) {
            val op3 = q.varEntries.joinToString(",") { "(${it.operator},${it.varId},${it.value})" }
            val op4 = q.varEntriesAlt.joinToString(",") { "(${it.operator},${it.varId},${it.value})" }
            if (q.varEntries.isNotEmpty() || q.varEntriesAlt.isNotEmpty()) withEntries++
            lines += "%4d  %-50s | op3=[%s]  op4=[%s]".format(q.id, q.name ?: "(no name)", op3, op4)
        }
        out.writeText(lines.joinToString("\n"))
        println("[dump-quest-varbits] wrote ${records.size} entries to ${out.relativeTo(engineRoot)}")
        println("[dump-quest-varbits] $withEntries of ${records.size} quests have varbit entries.")
    }
}
