package com.undercut.game.net.intercept

import com.undercut.game.net.ClientProt
import com.undercut.game.net.ServerProt

/**
 * Intercepts packets flowing through the network layer.
 *
 * Server interceptors run after the payload has been read but before handler dispatch.
 * Client interceptors run before the message is enqueued for transmission.
 *
 * Return [Action.PASS] to let the packet continue, [Action.DROP] to suppress it.
 * Modify [payload] in-place to alter the packet content before it reaches its destination.
 */
interface PacketInterceptor {

    enum class Action { PASS, DROP }

    /**
     * Called for incoming server packets.
     * @param prot the server protocol opcode enum
     * @param opcode raw opcode number
     * @param payload mutable payload bytes (modify in-place to alter)
     * @return [Action.PASS] to forward, [Action.DROP] to suppress
     */
    fun onServerPacket(prot: ServerProt?, opcode: Int, payload: ByteArray): Action = Action.PASS

    /**
     * Called for outgoing client packets.
     * @param prot the client protocol opcode enum
     * @param opcode raw opcode number
     * @param payload mutable payload bytes (modify in-place to alter)
     * @return [Action.PASS] to forward, [Action.DROP] to suppress
     */
    fun onClientPacket(prot: ClientProt?, opcode: Int, payload: ByteArray): Action = Action.PASS
}
