package com.undercut.game.hooks

/**
 * Default hook will hook a function located at the offset passed into it.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Hook(val value: Long, val priority: Priority = Priority.NORMAL)

/**
 * Symbolic hook will lookup the symbol passed into its value and hook the function at that location.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class SymbolHook(val library: String, val symbol: String, val priority: Priority = Priority.NORMAL)

enum class Priority { FIRST, HIGH, NORMAL, LOW, LAST }