package com.undercut.cache.type.idk

import com.undercut.cache.Cache

class IDKType(
    var id: Int = 0,
    var bodyPart: Int = -1,
    var modelIds: IntArray? = null,
    var nonSelectable: Boolean = false,
    var recolorSrc: IntArray? = null,
    var recolorDst: IntArray? = null,
    var retextureSrc: IntArray? = null,
    var retextureDst: IntArray? = null,
    var headModelIds: IntArray = IntArray(5) { -1 }
) {
    companion object {
        private val PARSER = IDKTypeParser()

        fun getParser(): IDKTypeParser = PARSER

        fun get(id: Int): IDKType = PARSER.get(Cache.get(), id)
    }
}
