package org.darkan.world

import kotlinx.coroutines.runBlocking
import org.darkan.core.EnvVars
import org.darkan.core.Logger
import org.darkan.core.mongo.Accounts
import org.darkan.core.mongo.MongoManager
import org.darkan.core.net.prot.handler.PacketHandlers
import org.darkan.core.net.prot.revision.rev947.register947
import org.darkan.world.server.WorldServer

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

    // Register protocol codec
    register947()
    Logger.log("Main", "Registered rev947 codec")

    // Load packet handlers from the world server packet package
    PacketHandlers.loadHandlersFromPackage("org.darkan.world.server.packet")

    // Start the world server
    Logger.log("Main", "Starting world server on port ${EnvVars.worldPort}...")
    runBlocking { WorldServer.start(EnvVars.worldPort).join() }
}
