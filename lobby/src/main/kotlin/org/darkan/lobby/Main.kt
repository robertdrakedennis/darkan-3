package org.darkan.lobby

import kotlinx.coroutines.runBlocking
import org.darkan.core.EnvVars
import org.darkan.core.Logger
import org.darkan.core.mongo.Accounts
import org.darkan.core.mongo.MongoManager
import org.darkan.core.net.prot.handler.PacketHandlers
import org.darkan.core.net.prot.revision.rev948.register948
import world.gregs.voidps.cache.Cache
import world.gregs.voidps.cache.file.FileProvider
import world.gregs.voidps.cache.file.prefetchKeys
import org.darkan.core.net.JS5Server
import org.darkan.lobby.server.ConfigServer
import org.darkan.lobby.server.LobbyServer

fun main() {
    Logger.setLogLevel(when (EnvVars.logLevel.uppercase()) {
        "ERROR" -> java.util.logging.Level.SEVERE
        "WARN" -> java.util.logging.Level.WARNING
        "INFO" -> java.util.logging.Level.CONFIG
        "TRACE", "DEBUG" -> java.util.logging.Level.FINER
        else -> java.util.logging.Level.FINER
    })
    Logger.log("Main", "Log level: ${EnvVars.logLevel}")

    // Initialize MongoDB
    MongoManager.init()
    runBlocking { Accounts.ensureIndexes() }

    // Initialize lobby state
    LobbyState.init()

    // Register protocol codec and packet handlers
    register948()
    Logger.log("Main", "Registered rev948 codec")
    PacketHandlers.loadHandlersFromPackage("org.darkan.lobby.server.packet")

    Logger.log("Main", "Loading cache...")
    val cache = Cache.get()
    Logger.log("Main", "Cache loaded: ${cache.indexCount()} indices")

    val prefetchKeys = prefetchKeys(cache)
    Logger.log("Main", "Generated ${prefetchKeys.size} prefetch keys")

    val provider = FileProvider.load(cache, inMemory = EnvVars.memCache)
    val js5 = JS5Server(provider, prefetchKeys)

    val configServer = ConfigServer(provider)
    configServer.start()

    val lobby = LobbyServer(js5)
    Logger.log("Main", "Starting server...")
    runBlocking { lobby.start().join() }
}
