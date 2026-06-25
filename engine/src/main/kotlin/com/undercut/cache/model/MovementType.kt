package com.undercut.cache.model

enum class MovementType(val id: Int) {
    STATIONARY(-1),
    HALF_WALK(0),
    WALKING(1),
    RUNNING(2);

    companion object {
        fun forId(id: Int): MovementType? {
            return entries.find { it.id == id }
        }
    }
}