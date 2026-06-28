package org.darkan.tools.recorder

import java.io.File

/**
 * The `recorder-known-opcodes.txt` filter: which fully-RE'd (dir, conn, opcode) packets get routed
 * out of the main enriched stream into `filtered/`. See the file header for the line format.
 */
class KnownOpcodes private constructor(private val rules: Set<Rule>) {

    private data class Rule(val dir: String, val conn: String, val opcode: Int)

    /**
     * A record is "known" when some rule matches its dir+opcode AND the rule's conn is `any` or
     * equals the record's conn. A record with an unknown/blank conn only matches `any` rules.
     */
    fun isKnown(dir: String, conn: String?, opcode: Int): Boolean {
        val d = dir.lowercase()
        val c = conn?.lowercase()
        return rules.any { it.dir == d && it.opcode == opcode && (it.conn == ANY || it.conn == c) }
    }

    val size: Int get() = rules.size

    companion object {
        private const val ANY = "any"
        private val VALID_DIRS = setOf("s2c", "c2s")
        private val VALID_CONNS = setOf("lobby", "world", ANY)

        fun empty() = KnownOpcodes(emptySet())

        fun load(file: File): KnownOpcodes {
            if (!file.exists()) {
                System.err.println("[EnrichSession] known-opcodes file not found, filtering disabled: ${file.absolutePath}")
                return empty()
            }
            val rules = LinkedHashSet<Rule>()
            file.forEachLine { rawLine ->
                val line = rawLine.substringBefore('#').trim()
                if (line.isEmpty()) return@forEachLine
                val parts = line.split(Regex("\\s+"))
                if (parts.size != 3) {
                    System.err.println("[EnrichSession] skipping malformed known-opcodes line: '$rawLine'")
                    return@forEachLine
                }
                val (dir, conn, opStr) = parts
                val opcode = opStr.toIntOrNull()
                if (dir.lowercase() !in VALID_DIRS || conn.lowercase() !in VALID_CONNS || opcode == null) {
                    System.err.println("[EnrichSession] skipping invalid known-opcodes line: '$rawLine'")
                    return@forEachLine
                }
                rules += Rule(dir.lowercase(), conn.lowercase(), opcode)
            }
            return KnownOpcodes(rules)
        }
    }
}
