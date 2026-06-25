package com.undercut.game.hooks.impl

import com.undercut.game.bootstrap.Bootstrap
import com.undercut.game.hooks.Hook
import com.undercut.game.hooks.HookManager
import com.undercut.game.memory.NativeAccess.deref
import com.undercut.game.memory.NativeAccess.getInt
import com.undercut.game.net.PacketLogger
import com.undercut.game.nxt.OClient
import com.undercut.game.nxt.OConnectionManager
import com.undercut.game.nxt.OFunctions
import com.undercut.game.nxt.OServerConnection
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout.JAVA_BYTE

/**
 * Hooks `TcpConnectionMessage::InitIncoming`, called from `ConnectionManager::TcpIn` after the
 * opcode is ISAAC-deciphered, the size resolved, and the payload read into the owning
 * ServerConnection's receive buffer (BUF_DATA, filled at offset 0 with BUF_POS reset to 0), but
 * before handler dispatch.
 *
 * Params (System V AMD64): RDI messagePtr, RSI protEntry (opcode@+0, registered size-class@+4),
 * RDX resolvedSize, RCX isaac (NULL on the incoming path).
 *
 * The payload is NOT in messagePtr — it lives in the ServerConnection buffer. We must read it from
 * the connection whose in-flight packet actually matches this hook's (opcode, resolvedSize). Reading
 * a buffer that doesn't correspond to the opcode staples a mismatched body onto it — the old
 * `mainState`-guess logged a construct-region grid as REBUILD_NORMAL/op81. When no connection agrees
 * the body is withheld and a DESYNC marker logged instead of fabricating one.
 */
object ServerPacketCapture {

    private const val MAX_PAYLOAD = 0x20000

    private val CONNECTION_SLOTS = longArrayOf(
        OConnectionManager.GAME_CONNECTION,
        OConnectionManager.LOGIN_CONNECTION,
    )

    @JvmStatic
    @Hook(OFunctions.TCPCONNECTIONMESSAGE_INIT_INCOMING)
    fun onIncomingPacket(
        messagePtr: MemorySegment,
        protEntry: MemorySegment,
        resolvedSize: MemorySegment,
        isaacPtr: MemorySegment
    ) {
        synchronized(Bootstrap.lock) {
            try {
                val opcode = protEntry.reinterpret(8).getInt()
                val size = resolvedSize.address().toInt()
                when {
                    size <= 0 -> PacketLogger.logServerPacket(opcode, size, ByteArray(0))
                    size > MAX_PAYLOAD -> PacketLogger.logServerPacketDesync(opcode, size, "implausible size")
                    else -> {
                        val conn = findProcessingConnection(opcode, size)
                        if (conn == null) {
                            PacketLogger.logServerPacketDesync(opcode, size, connStateSummary())
                        } else {
                            val bufData = conn.deref(OServerConnection.BUF_DATA, size.toLong())
                            val payload = ByteArray(size)
                            MemorySegment.copy(bufData, JAVA_BYTE, 0, payload, 0, size)
                            PacketLogger.logServerPacket(opcode, size, payload)
                        }
                    }
                }
            } catch (e: Throwable) {
                System.err.println("[ServerPacketCapture] Error: ${e.message}")
            }
            HookManager.trampoline(::onIncomingPacket.name)
                .invoke(messagePtr, protEntry, resolvedSize, isaacPtr)
        }
    }

    private fun findProcessingConnection(opcode: Int, size: Int): MemorySegment? {
        for (off in CONNECTION_SLOTS) {
            val conn = connOrNull(off) ?: continue
            val coherent = runCatching {
                conn.getInt(OServerConnection.CURRENT_OPCODE) == opcode &&
                    conn.getInt(OServerConnection.RESOLVED_SIZE) == size
            }.getOrDefault(false)
            if (coherent) return conn
        }
        return null
    }

    private fun connStateSummary(): String =
        CONNECTION_SLOTS.joinToString(" ") { off ->
            val label = if (off == OConnectionManager.GAME_CONNECTION) "game" else "login"
            val conn = connOrNull(off)
            when {
                conn == null -> "$label=<null>"
                else -> runCatching {
                    "$label(op=${conn.getInt(OServerConnection.CURRENT_OPCODE)},sz=${conn.getInt(OServerConnection.RESOLVED_SIZE)})"
                }.getOrDefault("$label=<unreadable>")
            }
        }

    private fun connOrNull(off: Long): MemorySegment? =
        runCatching {
            val conn = Bootstrap.client.ptr.deref(OClient.CONNECTION_MANAGER, 0x300L).deref(off, 0x300L)
            if (conn.address() == 0L) null else conn
        }.getOrNull()
}
