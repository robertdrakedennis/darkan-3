package org.darkan.core

import java.util.logging.ConsoleHandler
import java.util.logging.Level

object Logger {
    private const val ROOT_KEY = "DarkanRS"
    private val DARKAN_ROOT_LOGGER = java.util.logging.Logger.getLogger(ROOT_KEY);

    init {
        setupFormat()
    }

    @JvmStatic
    fun setupFormat() {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1\$tF %1\$tT %1\$tL] [%4$-7s] %5\$s %n")
        DARKAN_ROOT_LOGGER.handlers.forEach { DARKAN_ROOT_LOGGER.removeHandler(it) }
        DARKAN_ROOT_LOGGER.addHandler(ConsoleHandler())
        DARKAN_ROOT_LOGGER.useParentHandlers = false
    }

    @JvmStatic
    fun setLogLevel(level: Level) {
        DARKAN_ROOT_LOGGER.level = level
        DARKAN_ROOT_LOGGER.handlers.forEach { it.level = level }
    }

    private val stackWalker = StackWalker.getInstance()

    /**
     * Resolves the calling class/method by walking only as many frames as needed.
     * Only call this after an [java.util.logging.Logger.isLoggable] check — walking
     * frames on every suppressed log call is wasted work.
     */
    private fun getCallerInfo(): Pair<String, String> = stackWalker.walk { frames ->
        frames
            .filter { !it.className.contains("Logger") && !it.className.contains("Thread") }
            .findFirst()
            .map { Pair(it.className.substringAfterLast('.'), it.methodName) }
            .orElse(Pair("Unknown", "unknown"))
    }

    private fun formatMessage(className: String, methodName: String, msg: Any): String = "[$className.$methodName] $msg"

    fun logError(message: String, throwable: Throwable? = null) {
        if (DARKAN_ROOT_LOGGER.isLoggable(Level.SEVERE)) {
            val (className, methodName) = getCallerInfo()
            DARKAN_ROOT_LOGGER.log(Level.SEVERE, formatMessage(className, methodName, message), throwable)
        }
        if (EnvVars.debug && throwable != null)
            throwable.printStackTrace()
    }

    fun Any.logWarn(msg: Any, throwable: Throwable? = null) {
        if (DARKAN_ROOT_LOGGER.isLoggable(Level.WARNING)) {
            val (className, methodName) = getCallerInfo()
            DARKAN_ROOT_LOGGER.log(Level.WARNING, formatMessage(className, methodName, msg))
        }
        if (EnvVars.debug && throwable != null)
            throwable.printStackTrace()
    }

    fun Any.logInfo(msg: Any) {
        if (!DARKAN_ROOT_LOGGER.isLoggable(Level.INFO)) return
        val (className, methodName) = getCallerInfo()
        DARKAN_ROOT_LOGGER.log(Level.INFO, formatMessage(className, methodName, msg))
    }

    fun Any.logTrace(msg: Any) {
        if (!DARKAN_ROOT_LOGGER.isLoggable(Level.FINER)) return
        val (className, methodName) = getCallerInfo()
        DARKAN_ROOT_LOGGER.log(Level.FINER, formatMessage(className, methodName, msg))
    }

    fun Any.logFinest(msg: Any) {
        if (!DARKAN_ROOT_LOGGER.isLoggable(Level.FINEST)) return
        val (className, methodName) = getCallerInfo()
        DARKAN_ROOT_LOGGER.log(Level.FINEST, formatMessage(className, methodName, msg))
    }

    @JvmStatic
    fun log(tag: String, message: Any) {
        DARKAN_ROOT_LOGGER.log(Level.INFO, "[$tag] $message")
    }
}
