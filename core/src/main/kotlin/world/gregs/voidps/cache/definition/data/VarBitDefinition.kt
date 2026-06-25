package world.gregs.voidps.cache.definition.data

import world.gregs.voidps.cache.Cache
import world.gregs.voidps.cache.Definition

data class VarBitDefinition(
    override var id: Int = -1,
    var index: Int = 0,
    var startBit: Int = 0,
    var endBit: Int = 0,
    var flags: Int = 0,
    var domainId: Byte = 0,
    var domain: VarDomain? = null,
) : Definition {

    /** Engine alias for [index] — the backing var id this varbit packs into. */
    var baseVar: Int
        get() = index
        set(value) {
            index = value
        }

    fun getValue(varValue: Int): Int = (varValue shr startBit) and BIT_MASKS[endBit - startBit]

    companion object {
        val EMPTY = VarBitDefinition()

        val BIT_MASKS = IntArray(32).apply {
            var current = 2
            for (i in 0..31) {
                this[i] = current - 1
                current += current
            }
        }

        /** domain -> (baseVar -> varbits packed into it). Populated by [loadBaseVarMap]. */
        val baseVarMap = mutableMapOf<VarDomain, MutableMap<Int, MutableSet<VarBitDefinition>>>()

        fun loadBaseVarMap() {
            baseVarMap.clear()
            for (def in Cache.varbits) {
                val domain = def.domain ?: continue
                baseVarMap.getOrPut(domain) { mutableMapOf() }
                    .getOrPut(def.baseVar) { mutableSetOf() }
                    .add(def)
            }
        }
    }
}
