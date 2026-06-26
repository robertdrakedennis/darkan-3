package org.darkan.core.mongo

import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.mongo.transitions.Mongod
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess
import de.flapdoodle.reverse.TransitionWalker
import org.darkan.core.Logger
import java.io.File
import java.io.RandomAccessFile
import java.net.InetSocketAddress
import java.net.Socket

/**
 * Optional in-process MongoDB used for dev (`EMBEDDED_MONGO=true`).
 *
 * SHARED across processes: `:lobby` and `:world` are separate JVMs but must see the same data
 * (accounts, the lobby→world token bridge, …). The first process to start boots a mongod and
 * publishes its URI to [uriFile]; every later process (guarded by [lockFile]) reuses that running
 * instance instead of booting its own empty one — otherwise each process gets a private, empty DB
 * and nothing the lobby writes is visible to the world. Only the owner stops it.
 *
 * On the first ever run Flapdoodle downloads a mongod binary into `~/.embedmongo/`. The data lives
 * in a temp dir the owner's mongod cleans up on stop — fine for dev, NOT for prod.
 */
object EmbeddedMongo {
    private val tmpDir = File(System.getProperty("java.io.tmpdir"))
    private val uriFile = File(tmpDir, "darkan-embedded-mongo.uri")
    private val lockFile = File(tmpDir, "darkan-embedded-mongo.lock")

    private var running: TransitionWalker.ReachedState<RunningMongodProcess>? = null

    /** Connection URI of the embedded mongod we started or connected to, or null if not started. */
    var uri: String? = null
        private set

    fun start(): String {
        running?.let { return uri!! }
        uri?.let { return it }

        // Serialize start-or-reuse across processes so a lobby+world race can't boot two mongods.
        RandomAccessFile(lockFile, "rw").channel.use { channel ->
            channel.lock().use {
                running?.let { return uri!! }
                uri?.let { return it }

                readPublishedUri()?.let { shared ->
                    if (isListening(shared)) {
                        Logger.log("EmbeddedMongo", "Reusing shared embedded mongod at $shared")
                        uri = shared
                        return shared
                    }
                }

                Logger.log("EmbeddedMongo", "Starting in-process mongod (first run downloads the binary)...")
                val state = Mongod.instance().start(Version.Main.V7_0)
                val addr = state.current().serverAddress
                val started = "mongodb://${addr.host}:${addr.port}"
                running = state
                uri = started
                uriFile.writeText(started)
                Logger.log("EmbeddedMongo", "Embedded mongod listening at $started (shared via $uriFile)")
                return started
            }
        }
    }

    fun stop() {
        running?.let {
            Logger.log("EmbeddedMongo", "Stopping embedded mongod")
            it.close()
            uriFile.delete()
        }
        running = null
        uri = null
    }

    private fun readPublishedUri(): String? =
        runCatching { uriFile.takeIf { it.isFile }?.readText()?.trim()?.ifEmpty { null } }.getOrNull()

    private fun isListening(mongoUri: String): Boolean {
        val hostPort = mongoUri.substringAfter("://").substringBefore("/")
        val host = hostPort.substringBefore(":")
        val port = hostPort.substringAfter(":", "27017").toIntOrNull() ?: return false
        return runCatching {
            Socket().use { it.connect(InetSocketAddress(host, port), 500); true }
        }.getOrDefault(false)
    }
}
