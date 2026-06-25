package com.undercut.cache.type.hitmarks

import com.undercut.cache.Cache
import com.undercut.cache.type.params.Params

class HitmarkType(
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
    var field20: Int = 0,
    var field21: Int = 0, // uint rotated at +0x90
    var field22: Int = 0, // uint rotated at +0x8c
    var field23a: Int = 0, // byte, structure sub-field at +0x2c
    var field23b: Int = 0, // byte, structure sub-field at +0x30
    var field23c: Int = 0, // byte, structure sub-field at +0x34
    var field24a: Int = 0, // signed short << 9 at +0x138
    var field24b: Int = 0, // signed short << 9 at +0x13c
    var field25: Int = 0, // smartInt at +0x148
    var field28: Int = 0, // byte at +0x14c
    var field29: Int = 0, // byte at +0x150
    var field30: Int = 0, // byte at +0x154
    var params: Params = Params()
) {
    companion object {
        private val PARSER = HitmarkTypeParser()

        fun getParser(): HitmarkTypeParser = PARSER

        fun get(id: Int): HitmarkType = PARSER.get(Cache.get(), id)
    }
}
