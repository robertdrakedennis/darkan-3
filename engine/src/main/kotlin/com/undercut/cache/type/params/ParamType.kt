package com.undercut.cache.type.params

import com.undercut.cache.Cache
import com.undercut.cache.type.vars.CS2VarType

data class ParamType(
    val id: Int,
    var defaultInt: Int = 0,
    var autoDisable: Boolean = true,
    var typeId: Int = 0,
    var defaultString: String? = null,
    var type: CS2VarType? = null
) {
    companion object {
        private val parser = ParamTypeParser()

        fun getParser(): ParamTypeParser = parser

        fun get(id: Int): ParamType = parser.get(Cache.get(), id)
    }
}