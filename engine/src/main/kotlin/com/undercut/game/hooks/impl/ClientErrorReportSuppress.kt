package com.undercut.game.hooks.impl

import com.undercut.game.hooks.Hook
import com.undercut.game.hooks.Priority
import com.undercut.game.nxt.OFunctions
import java.lang.foreign.MemorySegment

/**
 * Drops every outbound crash/error report. `jag::game::ClientError::ReportError` is
 * the single funnel that the fatal-signal handler, the C++ fatal path, CS2 script
 * errors and connection errors all converge on; it synchronously POSTs the message
 * plus a dladdr-relocated backtrace — which would expose our injected modules — to
 * Jagex's `nxtclienterror.ws` endpoint via libcurl. Returning without invoking the
 * trampoline drops the report entirely: the function is `void` and every caller
 * ignores the result, so nothing downstream depends on the send.
 *
 * The body deliberately touches nothing. ReportError is frequently reached from
 * inside a fatal signal handler with the process in an undefined state, so reading
 * the message/callstack args (or running libcurl) here is exactly what we must
 * avoid — an empty return is both the suppression and the safe path.
 */
object ClientErrorReportSuppress {
    @JvmStatic
    @Hook(OFunctions.CLIENTERROR_REPORTERROR, priority = Priority.FIRST)
    fun reportErrorHook(reporter: MemorySegment, message: MemorySegment, callstack: MemorySegment, flags: Long) {
    }
}
