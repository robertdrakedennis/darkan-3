package org.darkan.world.server.packet

import org.darkan.core.Logger
import org.darkan.core.net.prot.AntiCheatChallengeResponse
import org.darkan.core.net.prot.ClanChannelKickUser
import org.darkan.core.net.prot.ClientInputEventBatch
import org.darkan.core.net.prot.DisplayMetrics
import org.darkan.core.net.prot.FriendListAdd
import org.darkan.core.net.prot.FriendListDel
import org.darkan.core.net.prot.IfButton
import org.darkan.core.net.prot.IgnoreListAdd
import org.darkan.core.net.prot.MapBuildComplete
import org.darkan.core.net.prot.MessagePrivateSend
import org.darkan.core.net.prot.MoveGameClick
import org.darkan.core.net.prot.Ping
import org.darkan.core.net.prot.SceneGraphReport
import org.darkan.core.net.prot.handler.PacketHandlers

/**
 * Explicit, compile-checked registration of every world C2S packet handler.
 *
 * This replaces the old ClassGraph package scan + `genericInterfaces[0]` reflection: each line below
 * is verified by the compiler (the handler's packet type must match the [kotlin.reflect.KClass] it is
 * registered against), and a missing handler is visible here rather than failing silently at runtime.
 *
 * The list must mirror exactly the set of `PacketHandler` implementations the scan previously found in
 * `org.darkan.world.server.packet` — no more, no fewer. `SocialHandlers.kt` declares five handler
 * classes (FriendListAdd / FriendListDel / IgnoreListAdd / ClanChannelKickUser / MessagePrivateSend),
 * so every class is enumerated individually.
 *
 * Lobby and world run in separate JVMs, so the same-named handlers in each module never collide.
 */
fun registerWorldPacketHandlers() {
    Logger.log("WorldPacketHandlers", "Registering world packet handlers...")

    PacketHandlers.register(Ping::class, PingHandler())
    PacketHandlers.register(DisplayMetrics::class, DisplayMetricsHandler())
    PacketHandlers.register(ClientInputEventBatch::class, ClientInputEventBatchHandler())
    PacketHandlers.register(SceneGraphReport::class, SceneGraphReportHandler())
    PacketHandlers.register(MapBuildComplete::class, MapBuildCompleteHandler())
    PacketHandlers.register(AntiCheatChallengeResponse::class, AntiCheatChallengeResponseHandler())
    PacketHandlers.register(FriendListAdd::class, FriendListAddHandler())
    PacketHandlers.register(FriendListDel::class, FriendListDelHandler())
    PacketHandlers.register(IgnoreListAdd::class, IgnoreListAddHandler())
    PacketHandlers.register(ClanChannelKickUser::class, ClanChannelKickUserHandler())
    PacketHandlers.register(MessagePrivateSend::class, MessagePrivateSendHandler())
    PacketHandlers.register(MoveGameClick::class, MoveGameClickHandler())
    PacketHandlers.register(IfButton::class, IfButtonHandler())

    Logger.log("WorldPacketHandlers", "World packet handlers registered (13 handlers)")
}
