package org.darkan.lobby.server

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.*
import org.darkan.core.EnvVars
import org.darkan.core.Logger.logError
import org.darkan.core.Logger.logInfo
import org.darkan.core.Logger.logTrace
import org.darkan.core.net.*
import world.gregs.voidps.buffer.*
import java.util.concurrent.Executors

class LobbyServer(val js5: JS5Server, val loginServer: LoginServer = LoginServer()) {
    private lateinit var job: Job
    private lateinit var dispatcher: ExecutorCoroutineDispatcher
    private lateinit var serverSocket: ServerSocket
    private lateinit var selectorManager: SelectorManager

    /** Per-IP connection limiter for login connections (JS5 has its own inside [JS5Server]). */
    private val loginLimiter = MultilogLimiter()

    suspend fun start(): Job {
        val executor = Executors.newCachedThreadPool()
        dispatcher = executor.asCoroutineDispatcher()
        selectorManager = ActorSelectorManager(dispatcher)
        val scope = CoroutineScope(dispatcher)
        logInfo("Starting lobby server...")
        serverSocket = aSocket(selectorManager).tcp().bind("0.0.0.0", EnvVars.lobbyPort) { reuseAddress = true }
        job = scope.launch {
            try {
                supervisorScope {
                    logInfo("Lobby server started on port ${EnvVars.lobbyPort}")
                    while (isActive) {
                        val socket = serverSocket.accept()
                        val ip = socket.remoteAddress.toJavaAddress().toString()
                            .substringAfter("/").substringBefore(":")
                        logTrace("Client connected: $ip")
                        connectClient(socket, ip)
                    }
                }
            } catch (_: CancellationException) {
                logInfo("Lobby server stopping...")
            } catch (e: Exception) {
                logError("Error in Lobby server", e)
            }
        }
        return job
    }

    fun stop() {
        try {
            job.cancel()
            dispatcher.close()
            if (::serverSocket.isInitialized) serverSocket.close()
            if (::selectorManager.isInitialized) selectorManager.close()
        } catch (e: Exception) {
            logError("Error stopping Lobby server", e)
        }
    }

    private fun CoroutineScope.connectClient(socket: Socket, ip: String) = launch(
        dispatcher + CoroutineExceptionHandler { _, throwable ->
            logTrace("Error connecting client: ${throwable.message}")
        }
    ) {
        try {
            val input = socket.openReadChannel()
            val output = socket.openWriteChannel(autoFlush = false)

            try {
                // Pre-auth read from an unauthenticated peer — bound it so a silent
                // connection can't hold the coroutine (and a socket) open forever.
                val reqOpcode = withTimeoutOrNull(PRE_AUTH_READ_TIMEOUT_MS) { input.readByte().toInt() }
                when (reqOpcode) {
                    null -> logTrace("Pre-auth handshake read timed out from $ip — disconnecting")
                    RequestOpcode.JS5_INIT -> js5.init(input, output, ip)
                    RequestOpcode.CONNECT_LOGIN,
                    RequestOpcode.LOGIN,
                    RequestOpcode.LOBBY -> {
                        if (!loginLimiter.add(ip)) {
                            logInfo("Login connection rejected (multilog limit): $ip")
                            output.finish(ResponseOpcode.LOGIN_LIMIT_EXCEEDED)
                        } else {
                            try {
                                loginServer.handleLogin(input, output, ip, reqOpcode)
                            } finally {
                                loginLimiter.remove(ip)
                            }
                        }
                    }
                    else -> {
                        logInfo("Connection from $ip with unhandled opcode: $reqOpcode (0x${"%02x".format(reqOpcode)})")
                        output.finish(ResponseOpcode.INVALID_LOGIN_SERVER)
                    }
                }
            } finally {
                socket.close()
            }
        } catch (e: Exception) {
            when {
                e is CancellationException -> {}
                else -> logTrace("Client disconnected: ${e::class.simpleName}")
            }
        } finally {
            socket.close()
        }
    }

    companion object {
        /** Max time an unauthenticated peer may take to send the connection-type byte. */
        private const val PRE_AUTH_READ_TIMEOUT_MS = 10_000L
    }
}
