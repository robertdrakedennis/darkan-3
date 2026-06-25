package com.undercut.cache.type.quests

import com.undercut.cache.Cache
import com.undercut.cache.type.params.Params

/**
 * One entry from the per-quest MasterQuestVar array (cache opcodes 3 and 4).
 * Format inferred from the RS3 cache layout: each tuple binds a varbit/varp to a
 * value comparison that defines a quest milestone (e.g. "varbit 29 == 60 means complete").
 */
data class QuestVarEntry(
    val operator: Int,   // 2-byte header: comparison/operator code (RS3-specific)
    val varId: Int,      // 4-byte varbit (or varp) id this quest tracks
    val value: Int,      // 4-byte threshold/value
)

class QuestType(
    var id: Int = 0,
    var name: String? = null,
    var description: String? = null,
    var sortKey: Int = 0,
    var difficulty: Int = 0,
    var members: Int = 0,
    var questFlags: Boolean = false,
    var questPointReward: Int = 0,
    var spriteId: Int = -1,
    var params: Params = Params(),
    /** MasterQuestVar entries from opcode 3 in the cache record. */
    var varEntries: MutableList<QuestVarEntry> = mutableListOf(),
    /** MasterQuestVar entries from opcode 4 in the cache record. */
    var varEntriesAlt: MutableList<QuestVarEntry> = mutableListOf(),
) {
    companion object {
        private val PARSER = QuestTypeParser()

        fun getParser(): QuestTypeParser = PARSER

        fun get(id: Int): QuestType = PARSER.get(Cache.get(), id)
    }
}
