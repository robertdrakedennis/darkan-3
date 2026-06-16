package org.darkan.core.mongo

import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.mongo.transitions.Mongod
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess
import de.flapdoodle.reverse.TransitionWalker
import org.darkan.core.Logger

/**
 * Optional in-process MongoDB used for dev (`EMBEDDED_MONGO=true`).
 *
 * On first start Flapdoodle downloads a mongod binary for the host platform
 * into `~/.embedmongo/` and runs it on an ephemeral loopback port. Once the
 * process is up [uri] returns the connection string the rest of the app should
 * use instead of `EnvVars.mongoUri`.
 *
 * The data lives in a temp directory that mongod cleans up on stop, so each
 * run starts empty — fine for dev / smoke tests, NOT for prod.
 */
object EmbeddedMongo {
    private var running: TransitionWalker.ReachedState<RunningMongodProcess>? = null

    /** Connection URI of the running embedded mongod, or null if not started. */
    var uri: String? = null
        private set

    fun start(): String {
        running?.let { return uri!! }
        Logger.log("EmbeddedMongo", "Starting in-process mongod (first run will download the binary)...")
        val state = Mongod.instance().start(Version.Main.V7_0)
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
}
