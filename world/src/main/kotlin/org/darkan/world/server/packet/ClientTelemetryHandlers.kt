package org.darkan.world.server.packet

import org.darkan.core.net.prot.ClientInputEventBatch
import org.darkan.core.net.prot.DisplayMetrics
import org.darkan.core.net.prot.handler.PacketHandler
import org.darkan.core.net.session.GameSession

class DisplayMetricsHandler : PacketHandler<GameSession, DisplayMetrics> {
    override suspend fun handle(player: GameSession, packet: DisplayMetrics) {
        // Informational client viewport state; no world-side action yet.
    }
}

class ClientInputEventBatchHandler : PacketHandler<GameSession, ClientInputEventBatch> {
    override suspend fun handle(player: GameSession, packet: ClientInputEventBatch) {
        // High-frequency input telemetry. Decoded for captures, intentionally dropped by the world.
    }
}
