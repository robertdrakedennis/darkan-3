package org.darkan.lobby.server.packet

import org.darkan.core.Logger
import org.darkan.core.net.prot.AntiCheatChallengeResponse
import org.darkan.core.net.prot.FriendListAdd
import org.darkan.core.net.prot.FriendListDel
import org.darkan.core.net.prot.IfButton
import org.darkan.core.net.prot.IgnoreListAdd
import org.darkan.core.net.prot.MacOsLobbyHandoff
import org.darkan.core.net.prot.MapBuildComplete
import org.darkan.core.net.prot.Ping
import org.darkan.core.net.prot.RequestWorldList
import org.darkan.core.net.prot.SceneGraphReport
import org.darkan.core.net.prot.handler.PacketHandlers

/**
 * Explicit, compile-checked registration of every lobby C2S packet handler.
 *
 * This replaces the old ClassGraph package scan + `genericInterfaces[0]` reflection: each line below
 * is verified by the compiler (the handler's packet type must match the [kotlin.reflect.KClass] it is
 * registered against), and a missing handler is visible here rather than failing silently at runtime.
 *
 * The list must mirror exactly the set of `PacketHandler` implementations the scan previously found in
 * `org.darkan.lobby.server.packet` — no more, no fewer. Several source files declare multiple handler
 * classes (`ClientLifecycleHandlers.kt` → MapBuildComplete + AntiCheatChallengeResponse;
 * `IfButtonHandler.kt` → IfButton + MacOsLobbyHandoff), so every class is enumerated individually.
 *
 * Lobby and world run in separate JVMs, so the same-named handlers in each module never collide.
 */
fun registerLobbyPacketHandlers() {
    Logger.log("LobbyPacketHandlers", "Registering lobby packet handlers...")

    PacketHandlers.register(Ping::class, PingHandler())
    PacketHandlers.register(SceneGraphReport::class, SceneGraphReportHandler())
    PacketHandlers.register(MapBuildComplete::class, MapBuildCompleteHandler())
    PacketHandlers.register(AntiCheatChallengeResponse::class, AntiCheatChallengeResponseHandler())
    PacketHandlers.register(IfButton::class, IfButtonHandler())
    PacketHandlers.register(MacOsLobbyHandoff::class, MacOsLobbyHandoffHandler())
    PacketHandlers.register(RequestWorldList::class, WorldlistFetchHandler())
    PacketHandlers.register(FriendListAdd::class, FriendListAddHandler())
    PacketHandlers.register(FriendListDel::class, FriendListDelHandler())
    PacketHandlers.register(IgnoreListAdd::class, IgnoreListAddHandler())

    Logger.log("LobbyPacketHandlers", "Lobby packet handlers registered (10 handlers)")
}
