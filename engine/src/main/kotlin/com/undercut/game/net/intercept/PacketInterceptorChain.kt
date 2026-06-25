package com.undercut.game.net.intercept

import com.undercut.game.net.ClientProt
import com.undercut.game.net.ServerProt

/**
 * Manages an ordered chain of [PacketInterceptor]s.
 *
 * Interceptors run in registration order. If any interceptor returns [PacketInterceptor.Action.DROP],
 * the chain short-circuits and the packet is suppressed.
 */
object PacketInterceptorChain {

    private val interceptors = mutableListOf<PacketInterceptor>()

    fun register(interceptor: PacketInterceptor) {
        synchronized(interceptors) {
            interceptors.add(interceptor)
        }
    }

    fun unregister(interceptor: PacketInterceptor) {
        synchronized(interceptors) {
            interceptors.remove(interceptor)
        }
    }

    fun clear() {
        synchronized(interceptors) {
            interceptors.clear()
        }
    }

    /**
     * Runs all interceptors for an incoming server packet.
     * @return true if the packet should continue to the handler, false to drop
     */
    fun processServerPacket(prot: ServerProt?, opcode: Int, payload: ByteArray): Boolean {
        val snapshot = synchronized(interceptors) { interceptors.toList() }
        for (interceptor in snapshot) {
            try {
                if (interceptor.onServerPacket(prot, opcode, payload) == PacketInterceptor.Action.DROP) {
                    return false
                }
            } catch (_: Throwable) {
                // Never let an interceptor crash the game
            }
        }
        return true
    }

    /**
     * Runs all interceptors for an outgoing client packet.
     * @return true if the packet should be sent, false to drop
     */
    fun processClientPacket(prot: ClientProt?, opcode: Int, payload: ByteArray): Boolean {
        val snapshot = synchronized(interceptors) { interceptors.toList() }
        for (interceptor in snapshot) {
            try {
                if (interceptor.onClientPacket(prot, opcode, payload) == PacketInterceptor.Action.DROP) {
                    return false
                }
            } catch (_: Throwable) {
                // Never let an interceptor crash the game
            }
        }
        return true
    }
}
