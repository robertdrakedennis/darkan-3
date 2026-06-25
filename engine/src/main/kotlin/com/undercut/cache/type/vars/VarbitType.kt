package com.undercut.cache.type.vars

import com.undercut.cache.Cache

class VarbitType {
    var id: Int = 0
    var domainId: Byte = 0
    var baseVar: Int = 0
    var startBit: Int = 0
    var endBit: Int = 0
    var domain: VarDomain? = null
    var flags: Int = 0

    override fun toString(): String {
        return "[$id base: $baseVar ($startBit -> $endBit) domain: ($domain)]"
    }

    fun getValue(varValue: Int) = varValue shr startBit and BIT_MASKS[endBit - startBit]

    companion object {
        private val parser = VarbitDefParser()
        fun get(id: Int): VarbitType = parser.get(Cache.get(), id)
        fun getParser(): VarbitDefParser = parser

        val BIT_MASKS = IntArray(32).apply {
            var curr = 2
            for (i in 0..31) {
                this[i] = curr - 1
                curr += curr
            }
        }

        val baseVarMap = mutableMapOf<VarDomain, MutableMap<Int, MutableSet<VarbitType>>>()
        fun loadBaseVarMap() {
            for (i in 0..parser.getMaxId()) {
                try {
                    val def = get(i)
                    if (def.domain == null) continue
                    val vars = baseVarMap[def.domain] ?: mutableMapOf()
                    val varbits = vars[def.baseVar] ?: mutableSetOf()
                    varbits.add(def)
                    vars[def.baseVar] = varbits
                    baseVarMap[def.domain!!] = vars
                } catch (e: Throwable) {

                }
            }
        }
    }
}