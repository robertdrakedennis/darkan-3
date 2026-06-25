package com.undercut.cache.type.cursors

import com.undercut.cache.Cache

class CursorType(
    var id: Int = 0,
    var spriteId: Int = -1,
    var hotspotX: Int = 0,
    var hotspotY: Int = 0
) {
    companion object {
        private val PARSER = CursorTypeParser()

        fun getParser(): CursorTypeParser = PARSER

        fun get(id: Int): CursorType = PARSER.get(Cache.get(), id)
    }
}
