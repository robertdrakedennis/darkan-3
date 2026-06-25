package com.undercut.mcp

import com.undercut.mcp.tools.EntityTools
import com.undercut.mcp.tools.ActionTools
import com.undercut.mcp.tools.ContentTools
import com.undercut.mcp.tools.Cs2Tools
import com.undercut.mcp.tools.ExploreTools
import com.undercut.mcp.tools.GameStateTools
import com.undercut.mcp.tools.InterfaceTools
import com.undercut.mcp.tools.InventoryTools
import com.undercut.mcp.tools.MemoryTools
import com.undercut.mcp.tools.MetaTools
import com.undercut.mcp.tools.PlayerTools
import com.undercut.mcp.tools.ProjectionTools
import com.undercut.mcp.tools.VarTools
import com.undercut.mcp.tools.WorldTools
import io.ktor.server.cio.CIO
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.server.mcp
import io.modelcontextprotocol.kotlin.sdk.types.Implementation
import io.modelcontextprotocol.kotlin.sdk.types.ServerCapabilities
import java.net.InetSocketAddress
import java.net.ServerSocket

object McpServer {

    const val PORT = 7882

    sealed class State {
        data object Disabled : State() {
            override fun toString() = "Disabled"
        }

        data object Starting : State() {
            override fun toString() = "Starting…"
        }

        data class Listening(val port: Int, val toolCount: Int) : State() {
            override fun toString() = "Listening on 127.0.0.1:$port ($toolCount tools)"
        }

        data object Stopping : State() {
            override fun toString() = "Stopping…"
        }

        data class Error(val message: String) : State() {
            override fun toString() = "Error: $message"
        }
    }

    @Volatile
    var state: State = State.Disabled
        private set

    private val lock = Any()
    private var ktorEngine: EmbeddedServer<*, *>? = null
    private var serverThread: Thread? = null

    fun start(): Result<Unit> = synchronized(lock) {
        when (val s = state) {
            is State.Listening -> return Result.success(Unit)
            State.Starting, State.Stopping -> return Result.failure(IllegalStateException("transition in progress: $s"))
            else -> {}
        }
        state = State.Starting

        probeBind(PORT)?.let { e ->
            state = State.Error("port $PORT in use by another client")
            return Result.failure(e)
        }

        return try {
            val server = createServer()
            val toolCount = lastToolCount
            val engine = embeddedServer(CIO, host = "127.0.0.1", port = PORT) {
                mcp { server }
            }
            val t = Thread({
                try {
                    engine.start(wait = true)
                } catch (e: Throwable) {
                    println("[MCP] engine error: ${e.message}")
                }
            }, "mcp-debug-server")
            t.isDaemon = true
            t.start()

            ktorEngine = engine
            serverThread = t
            state = State.Listening(PORT, toolCount)
            println("[MCP] started on 127.0.0.1:$PORT")
            Result.success(Unit)
        } catch (e: Throwable) {
            state = State.Error("start failed: ${e.message}")
            Result.failure(e)
        }
    }

    fun stop(): Result<Unit> = synchronized(lock) {
        when (state) {
            State.Disabled -> return Result.success(Unit)
            State.Starting, State.Stopping -> return Result.failure(IllegalStateException("transition in progress"))
            else -> {}
        }
        state = State.Stopping

        return try {
            ktorEngine?.stop(gracePeriodMillis = 1_000L, timeoutMillis = 5_000L)
            ktorEngine = null
            serverThread?.join(6_000L)
            serverThread = null
            state = State.Disabled
            println("[MCP] stopped, port $PORT released")
            Result.success(Unit)
        } catch (e: Throwable) {
            state = State.Error("stop failed: ${e.message}")
            Result.failure(e)
        }
    }

    private fun probeBind(port: Int): Throwable? = try {
        ServerSocket().use { ss ->
            ss.reuseAddress = false
            ss.bind(InetSocketAddress("127.0.0.1", port))
        }
        null
    } catch (e: Throwable) {
        e
    }

    @Volatile
    private var lastToolCount: Int = 0

    private fun createServer(): Server {
        val server = Server(
            serverInfo = Implementation(name = "undercut-debug", version = "1.0.0"),
            options = ServerOptions(
                capabilities = ServerCapabilities(
                    tools = ServerCapabilities.Tools(listChanged = false)
                )
            )
        )

        var count = 0
        count += MemoryTools.register(server)
        count += ProjectionTools.register(server)
        count += EntityTools.register(server)
        count += GameStateTools.register(server)
        count += MetaTools.register(server)
        count += PlayerTools.register(server)
        count += WorldTools.register(server)
        count += InterfaceTools.register(server)
        count += VarTools.register(server)
        count += InventoryTools.register(server)
        count += ContentTools.register(server)
        count += Cs2Tools.register(server)
        count += ActionTools.register(server)
        count += ExploreTools.register(server)

        lastToolCount = count
        return server
    }
}
