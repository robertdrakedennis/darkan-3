package com.undercut.util

object NativeSafety {
    inline fun <reified T> safeInvoke(functionName: String, crossinline block: () -> T): T? {
        return try {
            println("[Native Call] Entering: $functionName")
            val result = block()
            println("[Native Call] Exiting: $functionName")
            result
        } catch (e: Throwable) {
            println("[Native Crash] Function: $functionName")
            println("[Native Crash] Exception: ${e.message}")
            e.printStackTrace()
            logCallContext()
            throw e
        }
    }

    inline fun <reified T> safeInvokeVoid(functionName: String, crossinline block: () -> T) {
        try {
            println("[Native Call] Entering: $functionName")
            block()
            println("[Native Call] Exiting: $functionName")
        } catch (e: Throwable) {
            println("[Native Crash] Function: $functionName")
            println("[Native Crash] Exception: ${e.message}")
            e.printStackTrace()
            logCallContext()
            throw e
        }
    }

    fun logCallContext() {
        val thread = Thread.currentThread()
        println("[Context] Thread: ${thread.name} (${thread})")
        val runtime = Runtime.getRuntime()
        println("[Context] Memory - Free: ${runtime.freeMemory()}, Total: ${runtime.totalMemory()}")
    }
}