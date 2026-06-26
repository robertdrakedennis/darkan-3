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
import java.io.File
import java.io.RandomAccessFile
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.Path

/**
 * Optional in-process MongoDB used for dev (`EMBEDDED_MONGO=true`).
 *
 * SHARED across processes: `:lobby` and `:world` are separate JVMs but must see the same data
 * (accounts, the lobby→world token bridge, …). The first process to start boots a mongod on the
 * fixed [EnvVars.embeddedMongoHost]:[EnvVars.embeddedMongoPort] with a persistent data dir; every
 * later process detects the already-listening instance and reuses it instead of booting its own
 * empty one — otherwise each process gets a private DB and nothing the lobby writes is visible to
 * the world. Only the owner stops it.
 *
 * The fixed host/port + persistent [EnvVars.embeddedMongoDataDir] make reuse deterministic across
 * processes; a cross-process file lock serializes the start-or-reuse decision so a lobby+world race
 * can't boot two mongods on the same port. On the first ever run Flapdoodle downloads a mongod
 * binary into `~/.embedmongo/`. The data dir persists between runs — fine for dev, NOT for prod.
 */
object EmbeddedMongo {
    private val tmpDir = File(System.getProperty("java.io.tmpdir"))
    private val lockFile = File(tmpDir, "darkan-embedded-mongo.lock")

    private var running: TransitionWalker.ReachedState<RunningMongodProcess>? = null

    /** Connection URI of the embedded mongod we started or connected to, or null if not started. */
    var uri: String? = null
        private set

    fun start(): String {
        running?.let { return uri!! }
        uri?.let { return it }

        val host = EnvVars.embeddedMongoHost
        val port = EnvVars.embeddedMongoPort
        val sharedUri = "mongodb://$host:$port"

        // Serialize start-or-reuse across processes so a lobby+world race can't boot two mongods.
        RandomAccessFile(lockFile, "rw").channel.use { channel ->
            channel.lock().use {
                running?.let { return uri!! }
                uri?.let { return it }

                if (canConnect(host, port)) {
                    uri = sharedUri
                    Logger.log("EmbeddedMongo", "Reusing shared embedded mongod at $sharedUri")
                    return sharedUri
                }

                val databaseDir = Path(EnvVars.embeddedMongoDataDir).toAbsolutePath()
                Files.createDirectories(databaseDir)
                Logger.log(
                    "EmbeddedMongo",
                    "Starting shared embedded mongod at $sharedUri (dbPath=$databaseDir; first run downloads the binary)..."
                )
                val state = mongod(host, port, databaseDir).start(Version.Main.V7_0)
                val addr = state.current().serverAddress
                val started = "mongodb://${addr.host}:${addr.port}"
                running = state
                uri = started
                Logger.log("EmbeddedMongo", "Embedded mongod listening at $started (shared via $host:$port)")
                return started
            }
        }
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
