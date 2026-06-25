package org.darkan.world.entity

import world.gregs.voidps.cache.Cache
import world.gregs.voidps.cache.Config
import world.gregs.voidps.cache.Index

object Rev948FirstLightVarpDefaults : VarpDefaults {
    private const val RESOURCE = "/world/rev948-first-light-varps.tsv"

    private val defaults: Map<Int, Long> by lazy {
        validate(load())
    }

    override fun hudDefaults(): Map<Int, Long> = defaults

    fun size(): Int = defaults.size

    private fun load(): LinkedHashMap<Int, Long> {
        val stream = requireNotNull(Rev948FirstLightVarpDefaults::class.java.getResourceAsStream(RESOURCE)) {
            "Missing $RESOURCE"
        }
        val values = LinkedHashMap<Int, Long>()
        stream.bufferedReader().useLines { lines ->
            lines.forEachIndexed { index, line ->
                if (line.isBlank()) return@forEachIndexed
                val parts = line.split('\t')
                require(parts.size == 2) { "Invalid first-light varp line ${index + 1}: $line" }
                val id = parts[0].toInt()
                val value = parts[1].toLong()
                require(values.put(id, value) == null) { "Duplicate first-light varp id $id" }
            }
        }
        return values
    }

    private fun validate(values: LinkedHashMap<Int, Long>): Map<Int, Long> {
        val maxVarpId = Cache.get().lastFileId(Index.CONFIGS, Config.VAR_PLAYER)
        val invalid = values.keys.filter { it !in 0..maxVarpId }
        require(invalid.isEmpty()) {
            "First-light varp baseline contains cache-missing ids: ${invalid.take(16)}"
        }
        return values
    }
}
