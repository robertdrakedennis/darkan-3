package org.darkan.core.net.prot.handler

import org.darkan.core.Logger.logInfo
import org.darkan.core.Logger.logWarn
import org.darkan.core.net.prot.ClientProt
import org.darkan.core.net.prot.UnhandledClientProt
import kotlin.reflect.KClass

interface PacketHandler<T, K : ClientProt> {
    suspend fun handle(player: T, packet: K)
}

object PacketHandlers {
    private val PACKET_HANDLERS = mutableMapOf<Class<out ClientProt>, PacketHandler<*, out ClientProt>>()

    /**
     * Bind [handler] to the [packetClass] it decodes, compile-checked end-to-end.
     *
     * Because the handler's packet-type parameter `K` is constrained to equal [packetClass]'s type,
     * a miswired pair (e.g. `register(IfButton::class, PingHandler())`) is a COMPILE error — the
     * compiler enforces the handler↔packet edge that the old `genericInterfaces[0]` reflection only
     * discovered (and silently mis-bound) at runtime.
     *
     * Registering the same [packetClass] twice fails fast with an [error]: the previous classpath
     * scan silently let a later handler overwrite an earlier one (last-wins), so a duplicate is now
     * a loud boot failure instead of a quietly-dropped handler.
     */
    fun <K : ClientProt> register(packetClass: KClass<K>, handler: PacketHandler<*, K>) {
        val existing = PACKET_HANDLERS.put(packetClass.java, handler)
        if (existing != null) {
            error(
                "Duplicate packet handler registration for ${packetClass.java.name}: " +
                    "${existing.javaClass.simpleName} already registered, refused ${handler.javaClass.simpleName}"
            )
        }
    }

    /**
     * Suspend dispatch for coroutine callers (the lobby/world session loops).
     */
    @Suppress("UNCHECKED_CAST")
    suspend fun <T> handle(player: T, packet: ClientProt) {
        if (packet is UnhandledClientProt) {
            logWarn("Unhandled ClientProt: opcode=${packet.opcode} name=${packet.name} size=${packet.size}")
            return
        }
        val handler = PACKET_HANDLERS[packet::class.java] as? PacketHandler<T, ClientProt>
        if (handler == null) {
            logWarn("No handler for ${packet::class.java.simpleName}")
            return
        }
        handler.handle(player, packet)
    }
}
