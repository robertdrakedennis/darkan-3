package com.undercut.cache.type.vars

enum class BaseVarType(val clazz: Class<*>) {
    INTEGER(Int::class.java),
    LONG(Long::class.java),
    STRING(String::class.java),
    COORDFINE(Int::class.java); // TODO

    companion object {
        fun forId(id: Int): BaseVarType? = entries.getOrNull(id)
    }
}