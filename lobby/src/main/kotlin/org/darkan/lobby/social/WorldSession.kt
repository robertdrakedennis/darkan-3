package org.darkan.lobby.social

import io.ktor.websocket.*
import org.darkan.core.social.gateway.SocialGatewayWireJson
import org.darkan.core.social.gateway.SocialGatewayWireMessage
import java.util.concurrent.atomic.AtomicBoolean

class WorldSession(
    val worldId: Int,
    val worldName: String,
    private val socket: WebSocketSession,
) {
    private val closed = AtomicBoolean(false)

    fun isClosed(): Boolean = closed.get()

    suspend fun sendText(text: String) {
        if (isClosed()) return
        socket.send(Frame.Text(text))
    }

    suspend fun send(msg: SocialGatewayWireMessage) {
        val text = SocialGatewayWireJson.json.encodeToString(SocialGatewayWireMessage.serializer(), msg)
        sendText(text)
    }

    fun markClosed() {
        closed.set(true)
    }
}
