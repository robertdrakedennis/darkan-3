package com.undercut.cache

class ArchiveFile(
    var id: Int,
    var data: ByteArray = ByteArray(0),
    var name: Int = 0
) {
    constructor(id: Int, data: ByteArray) : this(id, data, 0)

    constructor(id: Int) : this(id, ByteArray(0))
}