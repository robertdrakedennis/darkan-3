package com.undercut.cache.type.sequences

import com.undercut.cache.Cache
import com.undercut.cache.type.params.Params

class SeqType(
    var id: Int = 0,
    var frameIds: IntArray? = null,
    var frameLengths: IntArray? = null,
    var loopDelay: Int = -1,
    var priority: Int = 5,
    var leftHandItem: Int = -1,
    var rightHandItem: Int = -1,
    var replayMode: Int = 0,
    var walkMerge: Int = 0,
    var priority2: Int = 0,
    var stretches: Int = 0,
    var soundEffects: IntArray? = null,
    var field25: Int = 0,
    var field26a: Int = 0,
    var field26b: Int = 0,
    var field27: Int = 0,
    var params: Params = Params()
) {
    companion object {
        private val PARSER = SeqTypeParser()

        fun getParser(): SeqTypeParser = PARSER

        fun get(id: Int): SeqType = PARSER.get(Cache.get(), id)
    }
}
