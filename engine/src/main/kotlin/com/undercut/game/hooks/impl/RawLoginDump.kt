package com.undercut.game.hooks.impl

import com.undercut.game.bootstrap.Bootstrap
import com.undercut.game.hooks.Hook
import com.undercut.game.hooks.HookManager
import com.undercut.game.memory.NativeAccess.deref
import com.undercut.game.memory.NativeAccess.getInt
import com.undercut.game.memory.NativeAccess.getOrNull
import com.undercut.game.net.PacketLogger
import com.undercut.game.nxt.OClient
import com.undercut.game.nxt.OConnectionManager
import com.undercut.game.nxt.OFunctions
import com.undercut.game.nxt.OServerConnection
import com.undercut.ui.UIState
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout.JAVA_BYTE

/**
 * Dumps the raw on-the-wire login/handshake bytes (the RSA block + server preamble) that the
 * decoded prot hooks never see — during login the bytes are read by the LoginManager state machine,
 * not dispatched through TcpIn as ServerProts. Post-login the decoded hooks
 * ([ServerPacketCapture] / [SendClientMessage]) take over, so dumping stops at LOGGED_IN.
 *
 * Toggled live from the UI (Settings → "Raw login/RSA dump", [UIState.rawLoginDumpEnabled]). Off by
 * default — these are the per-byte socket funnels, so when off the hooks are pure passthroughs (one
 * boolean check) and add no overhead to normal networking.
 */
object RawLoginDump {
    private const val LOGGED_IN_STATE = 30

    /**
     * The login connection's clientStream address while still logging in, else 0 — also 0 when
     * disabled, already LOGGED_IN, or not connecting. Every pointer is null-checked the way the
     * binary reads it (never via /proc/self/maps), so a partly-initialised ConnectionManager or a
     * JS5/other stream is simply ignored.
     */
    private fun loginStream(): Long {
        if (!UIState.rawLoginDumpEnabled.value) return 0L
        val client = Bootstrap.client.ptr.getOrNull ?: return 0L
        if (client.getInt(OClient.MAIN_STATE) == LOGGED_IN_STATE) return 0L
        val connMgr = client.deref(OClient.CONNECTION_MANAGER, 0x300L).getOrNull ?: return 0L
        val conn = connMgr.deref(OConnectionManager.LOGIN_CONNECTION, 0x300L).getOrNull ?: return 0L
        return conn.deref(OServerConnection.CLIENT_STREAM, 0x10L).getOrNull?.address() ?: 0L
    }

    private fun copyOf(buf: MemorySegment, len: Int): ByteArray {
        val src = buf.reinterpret(len.toLong())
        return ByteArray(len).also { MemorySegment.copy(src, JAVA_BYTE, 0L, it, 0, len) }
    }

    @JvmStatic
    @Hook(OFunctions.CLIENTSTREAM_READ)
    fun rawLoginRead(stream: MemorySegment, buf: MemorySegment, len: Long): Long {
        val read = HookManager.trampoline(::rawLoginRead.name).invokeExact(stream, buf, len) as Long
        if (read > 0L && stream.address() == loginStream()) {
            runCatching { PacketLogger.logRaw('S', copyOf(buf, read.toInt())) }
        }
        return read
    }

    @JvmStatic
    @Hook(OFunctions.CLIENTSTREAM_WRITE)
    fun rawLoginWrite(stream: MemorySegment, buf: MemorySegment, len: Long): Long {
        if (len > 0L && stream.address() == loginStream()) {
            runCatching { PacketLogger.logRaw('C', copyOf(buf, len.toInt())) }
        }
        return HookManager.trampoline(::rawLoginWrite.name).invokeExact(stream, buf, len) as Long
    }
}
