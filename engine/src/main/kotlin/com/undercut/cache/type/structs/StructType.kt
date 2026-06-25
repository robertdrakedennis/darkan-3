package com.undercut.cache.type.structs

import com.undercut.cache.Cache
import com.undercut.cache.type.params.Params

data class StructType(
    var id: Int = 0,
    var params: Params = Params()
) {
    companion object {
        private val parser = StructTypeParser()

        fun getParser(): StructTypeParser = parser

        fun get(id: Int): StructType = parser.get(Cache.get(), id)
    }
}