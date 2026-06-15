package org.darkan.world.social

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import org.darkan.core.EnvVars
import org.darkan.core.Logger.logInfo
import org.darkan.core.Logger.logWarn
import org.darkan.core.net.prot.ClientProt
import org.darkan.core.social.gateway.*
import java.net.ConnectException
import java.util.concurrent.atomic.AtomicReference
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * WebSocket client that connects to the lobby's SocialGateway.
 *
 * On connect, sends a WorldHello with this world's metadata.
 * Receives social responses (ServerPacketEnvelope, ServerPacketBatch, etc.)
 * and forwards them to the WorldServer for delivery to player sessions.
 *
 * Design: ~/darkan/server/world/src/main/kotlin/org/darkan/world/social/SocialClient.kt
 */
class SocialClient(
    private val scope: CoroutineScope,
    /**
     * Invoked after each successful (re)connection + WorldHelloAck. Callers use this to
     * resend a full presence snapshot — messages sent while disconnected are dropped, so a
     * lobby restart would otherwise leave presence permanently desynced.
     */
    private val onConnected: suspend () -> Unit = {},
    private val onMessage: suspend (SocialGatewayWireMessage) -> Unit,
) {
    private val client = HttpClient(CIO) {
        install(WebSockets) {
            pingInterval = 15.seconds
            maxFrameSize = Long.MAX_VALUE
        }
    }

    private val state = AtomicReference<State>(State.Stopped)

    sealed interface State {
        data object Stopped : State
        data class Connecting(val attempt: Int) : State
        data class Connected(val session: DefaultClientWebSocketSession) : State
    }

    fun start() {
        if (!state.compareAndSet(State.Stopped, State.Connecting(attempt = 0))) return
        scope.launch(Dispatchers.IO + SupervisorJob()) { connectLoop() }
    }

    suspend fun send(msg: SocialGatewayWireMessage) {
        val st = state.get()
        val session = (st as? State.Connected)?.session
        if (session == null) {
            // Dropped while disconnected — recovered by the onConnected snapshot resend.
            logWarn("SocialClient dropped message while not connected type=${msg::class.simpleName} (state=${st::class.simpleName})")
            return
        }
        val text = SocialGatewayWireJson.json.encodeToString(SocialGatewayWireMessage.serializer(), msg)
        try {
            session.send(Frame.Text(text))
        } catch (e: Exception) {
            logWarn("SocialClient send failed type=${msg::class.simpleName}", e)
        }
    }

    suspend fun sendClientPacket(username: String, packet: ClientProt) {
        send(ClientPacketEnvelope(username = username, packet = packet))
    }

    suspend fun sendPlayerOnline(username: String, displayName: String, rightsCrown: Int, privateStatus: Int) {
        send(PlayerOnline(username = username, displayName = displayName, rightsCrown = rightsCrown, privateStatus = privateStatus))
    }

    suspend fun sendPlayerOffline(username: String) {
        send(PlayerOffline(username = username))
    }

    suspend fun sendFcSettingsUpdate(username: String, name: String?, rankToEnter: Int, rankToSpeak: Int, rankToKick: Int, rankToLS: Int) {
        send(FcSettingsUpdate(username = username, name = name, rankToEnter = rankToEnter, rankToSpeak = rankToSpeak, rankToKick = rankToKick, rankToLS = rankToLS))
    }

    suspend fun sendClanCreate(username: String, clanName: String) {
        send(ClanCreateRequest(username = username, clanName = clanName))
    }

    suspend fun sendClanLeave(username: String) {
        send(ClanLeaveRequest(username = username))
    }

    private suspend fun connectLoop() {
        try {
            var attempt = 0
            while (scope.isActive) {
                try {
                    state.set(State.Connecting(attempt))
                    val url = EnvVars.socialGatewayUrl
                    logInfo("SocialClient connecting url=$url attempt=$attempt")

                    client.webSocket(urlString = url) {
                        val hello = WorldHello(
                            world = GatewayWorldInfo.fromEnv(),
                            token = EnvVars.socialGatewayToken,
                        )
                        send(Frame.Text(SocialGatewayWireJson.json.encodeToString(SocialGatewayWireMessage.serializer(), hello)))

                        // Wait for ack
                        val first = incoming.receiveCatching().getOrNull()
                        val ack = (first as? Frame.Text)?.readText()?.let { txt ->
                            try {
                                SocialGatewayWireJson.json.decodeFromString(SocialGatewayWireMessage.serializer(), txt) as? WorldHelloAck
                            } catch (_: Exception) {
                                null
                            }
                        }
                        if (ack != null && !ack.ok) {
                            logWarn("SocialClient unauthorized: ${ack.message ?: "no message"}")
                            close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Unauthorized"))
                            return@webSocket
                        }

                        state.set(State.Connected(this))
                        logInfo("SocialClient connected to lobby gateway")

                        // The gateway lost all of this world's presence state while we were
                        // disconnected — let the owner replay a full snapshot.
                        try {
                            onConnected()
                        } catch (e: Exception) {
                            logWarn("SocialClient onConnected callback failed", e)
                        }

                        for (frame in incoming) {
                            val txt = (frame as? Frame.Text)?.readText() ?: continue
                            val msg = try {
                                SocialGatewayWireJson.json.decodeFromString(SocialGatewayWireMessage.serializer(), txt)
                            } catch (e: Exception) {
                                logWarn("SocialClient received invalid JSON: $txt", e)
                                continue
                            }
                            onMessage(msg)
                        }
                    }
                } catch (_: CancellationException) {
                    return
                } catch (e: Exception) {
                    if (e is ConnectException) {
                        logInfo("SocialClient connection refused (attempt $attempt, retrying...)")
                    } else {
                        logWarn("SocialClient connection error: ${e::class.simpleName}: ${e.message}")
                    }
                } finally {
                    // Connection ended — drop back to Connecting for the retry. Stopped is
                    // reserved for terminal exit: setting it per-iteration would re-arm
                    // start()'s compareAndSet and allow a second concurrent loop to spawn.
                    state.set(State.Connecting(attempt))
                }

                attempt++
                val backoff = (250L * attempt).coerceAtMost(5_000L).milliseconds
                delay(backoff)
            }
        } finally {
            state.set(State.Stopped)
        }
    }
}
