package org.darkan.core.mongo

import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.mongo.types.DatabaseDir
import de.flapdoodle.embed.mongo.transitions.Mongod
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess
import de.flapdoodle.reverse.TransitionWalker
import de.flapdoodle.reverse.transitions.Start
import org.darkan.core.EnvVars
import org.darkan.core.Logger
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.Path

/**
 * Shared dev mongod for `EMBEDDED_MONGO=true`.
 */
object EmbeddedMongo {
    private var running: TransitionWalker.ReachedState<RunningMongodProcess>? = null

    var uri: String? = null
        private set

    fun start(): String {
        running?.let { return uri!! }

        val host = EnvVars.embeddedMongoHost
        val port = EnvVars.embeddedMongoPort
        val sharedUri = "mongodb://$host:$port"
        if (canConnect(host, port)) {
            uri = sharedUri
            Logger.log("EmbeddedMongo", "Using existing embedded mongod at $sharedUri")
            return sharedUri
        }

        val databaseDir = Path(EnvVars.embeddedMongoDataDir).toAbsolutePath()
        Files.createDirectories(databaseDir)
        Logger.log("EmbeddedMongo", "Starting shared embedded mongod at $sharedUri (dbPath=$databaseDir)")
        val state = mongod(host, port, databaseDir).start(Version.Main.V7_0)
        val addr = state.current().serverAddress
        val u = "mongodb://${addr.host}:${addr.port}"
        running = state
        uri = u
        Logger.log("EmbeddedMongo", "Embedded mongod listening at $u")
        return u
    }

    fun stop() {
        running?.let {
            Logger.log("EmbeddedMongo", "Stopping embedded mongod")
            it.close()
        }
        running = null
        uri = null
    }

    private fun mongod(host: String, port: Int, databaseDir: Path): Mongod {
        return Mongod.builder()
            .net(Start.to(Net::class.java).initializedWith(Net.of(host, port, false)))
            .databaseDir(Start.to(DatabaseDir::class.java).initializedWith(DatabaseDir.of(databaseDir)))
            .build()
    }

    private fun canConnect(host: String, port: Int): Boolean {
        return try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(host, port), 250)
            }
            true
        } catch (_: Exception) {
            false
        }
    }
}
