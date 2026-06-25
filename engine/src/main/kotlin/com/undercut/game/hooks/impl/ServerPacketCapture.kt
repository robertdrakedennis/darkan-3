package com.undercut.game.hooks.impl

import com.undercut.game.bootstrap.Bootstrap
import com.undercut.game.hooks.Hook
import com.undercut.game.hooks.HookManager
import com.undercut.game.memory.NativeAccess.deref
import com.undercut.game.memory.NativeAccess.getInt
import com.undercut.game.net.PacketLogger
import com.undercut.game.nxt.OClient
import com.undercut.game.nxt.OFunctions
import com.undercut.game.nxt.OServerConnection
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout.JAVA_BYTE
import java.lang.foreign.ValueLayout.JAVA_INT

/**
 * Hooks the TcpConnectionMessage<ServerProt> initializer (0x002475f0) to capture
 * incoming server packets.
 *
 * Called from TcpIn after the payload has been read into the ServerConnection
 * receive buffer, but before handler dispatch.
 *
 * Parameters (System V AMD64):
 *   RDI = messagePtr: TcpConnectionMessage* (destination struct in pool slot + 0x20)
 *   RSI = protEntry: ServerProt* (from global lookup table, opcode at +0x00)
 *   RDX = payloadSize: int (resolved payload byte count from ServerConnection+0x30)
 *   RCX = isaacPtr: Isaac* (NULL in TcpIn path — ISAAC consumed during opcode decode)
 */
object ServerPacketCapture {
    @JvmStatic
    @Hook(OFunctions.TCPCONNECTIONMESSAGE_INIT_INCOMING)
    fun onIncomingPacket(
        messagePtr: MemorySegment,
        protEntry: MemorySegment,
        payloadSize: MemorySegment,
        isaacPtr: MemorySegment
    ) {
        synchronized(Bootstrap.lock) {
            try {
                val opcode = protEntry.reinterpret(8).get(JAVA_INT, 0)
                val size = payloadSize.address().toInt()

                val payload = if (size > 0) {
                    val client = Bootstrap.client.ptr
                    val connMgr = client.deref(OClient.CONNECTION_MANAGER, 0x300L)
                    val mainState = client.getInt(OClient.MAIN_STATE)
                    val connOffset = if (mainState == 30) 0x18L else 0x28L
                    val conn = connMgr.deref(connOffset, 0x300L)
                    val bufData = conn.deref(OServerConnection.BUF_DATA, size.toLong())
                    ByteArray(size).also { arr ->
                        MemorySegment.copy(bufData, JAVA_BYTE, 0, arr, 0, size)
                    }
                } else {
                    ByteArray(0)
                }

                PacketLogger.logServerPacket(opcode, size, payload)
            } catch (e: Throwable) {
                System.err.println("[ServerPacketCapture] Error: ${e.message}")
            }
            HookManager.trampoline(::onIncomingPacket.name)
                .invoke(messagePtr, protEntry, payloadSize, isaacPtr)
        }
    }
}
