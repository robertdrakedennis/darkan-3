package org.darkan.lobby

import kotlinx.coroutines.runBlocking
import org.darkan.core.Logger
import world.gregs.voidps.cache.Cache
import world.gregs.voidps.cache.file.FileProvider
import world.gregs.voidps.cache.file.prefetchKeys
import org.darkan.core.net.JS5Server
import org.darkan.lobby.server.ConfigServer
import org.darkan.lobby.server.LobbyServer

fun main() {
    Logger.log("Main", "Loading cache...")
    val cache = Cache.get()
    Logger.log("Main", "Cache loaded: ${cache.indexCount()} indices")

    val prefetchKeys = prefetchKeys(cache)
    Logger.log("Main", "Generated ${prefetchKeys.size} prefetch keys")

    val provider = FileProvider.load(cache)
    val js5 = JS5Server(provider, prefetchKeys)

    val configServer = ConfigServer()
    configServer.start()

    val lobby = LobbyServer(js5)
    Logger.log("Main", "Starting server...")
    runBlocking { lobby.start().join() }
}
