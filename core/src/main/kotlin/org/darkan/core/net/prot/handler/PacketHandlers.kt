package org.darkan.core.net.prot.handler

import kotlinx.coroutines.runBlocking
import org.darkan.core.Logger.logError
import org.darkan.core.Logger.logInfo
import org.darkan.core.Logger.logWarn
import org.darkan.core.getClasses
import org.darkan.core.net.prot.ClientProt
import org.darkan.core.net.prot.UnhandledClientProt
import java.lang.reflect.ParameterizedType

interface PacketHandler<T, K : ClientProt> {
    suspend fun handle(player: T, packet: K)
}

object PacketHandlers {
    private val PACKET_HANDLERS = mutableMapOf<Class<out ClientProt>, PacketHandler<*, out ClientProt>>()

    fun loadHandlersFromPackage(pack: String) {
        try {
            logInfo("Initializing packet handlers ($pack)...")
            val classes = getClasses(pack)

            classes
                .filter { PacketHandler::class.java.isAssignableFrom(it) }
                .forEach { clazz ->
                    @Suppress("UNCHECKED_CAST")
                    mapHandler(clazz.getConstructor().newInstance() as PacketHandler<*, out ClientProt>)
                }
            logInfo("Packet handlers loaded for ${PACKET_HANDLERS.size} packets...")
        } catch (e: Exception) {
            // A classpath/scanning failure here would otherwise yield a silently
            // handler-less server — surface it and fail fast.
            logError("Failed to load packet handlers from package $pack", e)
            throw e
        }
    }

    fun mapHandler(handler: PacketHandler<*, out ClientProt>) {
        val type = handler.javaClass.genericInterfaces[0] as ParameterizedType
        @Suppress("UNCHECKED_CAST")
        val clazz = type.actualTypeArguments[1] as Class<ClientProt>
        logInfo("  Mapped handler: ${handler.javaClass.simpleName} -> ${clazz.simpleName} (${clazz.name})")
        PACKET_HANDLERS[clazz] = handler
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getHandler(packet: Class<out ClientProt>) = PACKET_HANDLERS[packet] as? PacketHandler<T, ClientProt>

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

    /**
     * Blocking dispatch for Java callers (e.g. Player.processPackets on the world thread).
     * Handlers that call only non-suspend legacy Java code complete synchronously.
     */
    fun <T> handleBlocking(player: T, packet: ClientProt) {
        runBlocking { handle(player, packet) }
    }
}
