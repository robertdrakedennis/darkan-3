package com.undercut.game.hooks.impl

import com.undercut.game.bootstrap.Bootstrap
import com.undercut.game.hooks.Hook
import com.undercut.game.hooks.HookManager
import com.undercut.game.net.PacketLogger
import com.undercut.game.nxt.OFunctions
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout.ADDRESS
import java.lang.foreign.ValueLayout.JAVA_BYTE
import java.lang.foreign.ValueLayout.JAVA_INT
import java.lang.foreign.ValueLayout.JAVA_LONG

/**
 * Hooks ServerConnection::SendClientMessage (0x00cb9390) to capture outgoing client packets.
 *
 * Called when the game enqueues a fully-written message for transmission.
 *
 * Parameters (System V AMD64):
 *   RDI = serverConnPtr: ServerConnection* (this)
 *   RSI = messageSharedPtr: pointer to shared_ptr pair [controlBlock, objectPtr]
 *        objectPtr points to the Packet sub-struct (TcpConnectionMessage+0x20):
 *          +0x00 = opcode (int)
 *          +0x04 = fixedSize (int: -1/-2/fixed)
 *          +0x10 = bufData (void*)
 *          +0x20 = bufWritePos (long: total bytes written including opcode)
 */
object SendClientMessage {
    @JvmStatic
    @Hook(OFunctions.SENDCLIENTMESSAGE)
    fun sendClientMessageHook(serverConnPtr: MemorySegment, messageSharedPtr: MemorySegment) {
        synchronized(Bootstrap.lock) {
            try {
                val sharedPtr = messageSharedPtr.reinterpret(16)
                val objectPtr = sharedPtr.get(ADDRESS, 8).reinterpret(0x58)

                val opcode = objectPtr.get(JAVA_INT, 0)
                val fixedSize = objectPtr.get(JAVA_INT, 4)
                val bufData = objectPtr.get(ADDRESS, 0x18)    // buffer data pointer
                val bufWritePos = objectPtr.get(JAVA_LONG, 0x20).toInt()

                // Calculate how many bytes to skip (opcode encoding + optional size prefix)
                val opcodeBytes = if (opcode >= 128) 2 else 1
                val sizePrefixBytes = when (fixedSize) {
                    -1 -> 1; -2 -> 2; else -> 0
                }
                val headerBytes = opcodeBytes + sizePrefixBytes
                val payloadSize = maxOf(0, bufWritePos - headerBytes)

                val payload = if (payloadSize > 0 && bufData.address() != 0L) {
                    val buf = bufData.reinterpret(bufWritePos.toLong())
                    ByteArray(payloadSize).also { arr ->
                        MemorySegment.copy(buf, JAVA_BYTE, headerBytes.toLong(), arr, 0, payloadSize)
                    }
                } else {
                    ByteArray(0)
                }

                PacketLogger.logClientPacket(opcode, payloadSize, payload)
            } catch (_: Throwable) {
                // Fail open: send the packet if anything goes wrong
            }
            HookManager.trampoline(::sendClientMessageHook.name)
                .invoke(serverConnPtr, messageSharedPtr)
        }
    }
}
