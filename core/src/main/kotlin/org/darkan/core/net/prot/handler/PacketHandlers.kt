package org.darkan.core.net.prot.handler

import kotlinx.coroutines.runBlocking
import org.darkan.core.Logger.logInfo
import org.darkan.core.Logger.logWarn
import org.darkan.core.getClasses
import org.darkan.core.net.prot.ClientProt
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
            e.printStackTrace()
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
     * Blocking dispatch for Java callers (e.g. Player.processPackets on the world thread).
     * Handlers that call only non-suspend legacy Java code complete synchronously.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> handleBlocking(player: T, packet: ClientProt) {
        val handler = PACKET_HANDLERS[packet::class.java] as? PacketHandler<T, ClientProt>
        if (handler == null) {
            logWarn("No handler found for packet: ${packet::class.java.simpleName} (${packet::class.java.name}), registered keys: ${PACKET_HANDLERS.keys.map { it.simpleName }}")
            return
        }
        runBlocking { handler.handle(player, packet) }
    }
}
