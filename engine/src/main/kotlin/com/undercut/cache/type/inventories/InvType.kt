package com.undercut.cache.type.inventories

import com.undercut.cache.Cache

class InvType(
    var id: Int = 0,
    var size: Int = 0,
    var items: Map<Int, Int> = emptyMap()
) {
    companion object {
        private val PARSER = InvTypeParser()

        fun getParser(): InvTypeParser = PARSER

        fun get(id: Int): InvType = PARSER.get(Cache.get(), id)
    }
}
