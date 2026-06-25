package com.undercut.cache.type.headbars

import com.undercut.cache.Cache

class HeadbarType(
    var id: Int = 0,
    var spriteId: Int = -1,
    var hasColor: Boolean = false,
    var color: Int = 0,
    var spriteId2: Int = -1,
    var spriteId3: Int = -1,
    var spriteId4: Int = -1,
    var spriteId5: Int = -1,
    var field7: Int = 0,
    var height: Int = 0,
    var field10: Int = 0,
    var field11: Int = 0,
    var field12: Int = 0,
    var field13: Int = 0,
    var field14: Int = 0,
    var field16a: Int = 0,
    var field16b: Int = 0,
    var field19: Int = 0,
    var field20: Int = 0
) {
    companion object {
        private val PARSER = HeadbarTypeParser()

        fun getParser(): HeadbarTypeParser = PARSER

        fun get(id: Int): HeadbarType = PARSER.get(Cache.get(), id)
    }
}
