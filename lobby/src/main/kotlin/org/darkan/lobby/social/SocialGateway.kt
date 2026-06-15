package org.darkan.lobby.social

import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.darkan.core.EnvVars
import org.darkan.core.Logger
import org.darkan.core.Logger.logInfo
import org.darkan.core.Logger.logTrace
import org.darkan.core.Logger.logWarn
import org.darkan.core.formatForDisplay
import org.darkan.core.formatForProtocol
import org.darkan.core.model.*
import org.darkan.core.mongo.Accounts
import org.darkan.core.mongo.Clans
import org.darkan.core.net.Session
import org.darkan.core.net.prot.*
import org.darkan.core.net.session.GameSession
import org.darkan.core.social.gateway.*
import org.darkan.lobby.LobbyState
import java.util.concurrent.ConcurrentHashMap

/**
 * Central social authority for the darkan-3 server.
 *
 * Manages player presence (both lobby and world), routes packets between
 * lobby sessions and world servers, and handles all social operations:
 * friends, ignore, friends chat (FC), clan chat (CC), and private messages.
 *
 * World servers connect via WebSocket and exchange [SocialGatewayWireMessage]s.
 * Lobby players are tracked directly via their [GameSession].
 */
object SocialGateway {

    // --- Internal data types ---

    private data class Presence(
        val username: String,
        val displayName: String,
        val rightsCrown: Int,
        val privateStatus: Int,
        val worldId: Int,
        val worldName: String,
        val lastSeenMs: Long,
    )

    private data class LobbyUser(
        val username: String,
        val displayName: String,
        val rightsCrown: Int,
        val privateStatus: Int,
        val session: GameSession,
        val lastSeenMs: Long,
    )

    private data class FcRoom(
        val ownerUsername: String,
        val members: MutableSet<String>,
        val tempBans: ConcurrentHashMap<String, Long> = ConcurrentHashMap(),
    )

    private data class CcRoom(
        val clanName: String,
        val members: MutableSet<String>,
        val tempBans: ConcurrentHashMap<String, Long> = ConcurrentHashMap(),
        var updateNum: Long = 0,
    )

    // --- State ---

    private val worldsById = ConcurrentHashMap<Int, WorldSession>()

    private val presenceByUsername = ConcurrentHashMap<String, Presence>()
    private val lobbyByUsername = ConcurrentHashMap<String, LobbyUser>()
    private val usernameByDisplayLower = ConcurrentHashMap<String, String>()

    private val fcRoomsByOwner = ConcurrentHashMap<String, FcRoom>()
    private val fcOwnerByMember = ConcurrentHashMap<String, String>()

    private val ccRoomsByClanName = ConcurrentHashMap<String, CcRoom>()
    private val ccClanByMember = ConcurrentHashMap<String, String>()

    // =====================================================================
    //  Lobby Player Lifecycle
    // =====================================================================

    /**
     * Register a lobby player after successful login (presence only).
     * The player's own social state (friend list, ignore list, chat filter) is NOT sent here —
     * it must be sent AFTER the lobby interface is built, via [initializeSocial], which the login
     * flow calls from sendLobbyInitPackets(). Sending UPDATE_FRIENDLIST (op26) before the
     * friends-tab components exist makes the client drop it (empty/broken friends list).
     */
    suspend fun registerLobbyPlayer(account: Account, session: GameSession) {
        lobbyByUsername[account.username] = LobbyUser(
            username = account.username,
            displayName = account.displayName,
            rightsCrown = account.rights,
            privateStatus = account.social.status,
            session = session,
            lastSeenMs = System.currentTimeMillis(),
        )
        usernameByDisplayLower[account.displayName.lowercase()] = account.username
        logInfo("SocialGateway: ${account.displayName} registered in lobby (${lobbyByUsername.size} online)")
    }

    /**
     * Unregister a lobby player on disconnect.
     * Notifies mutual friends, leaves FC/CC.
     */
    suspend fun unregisterLobbyPlayer(username: String) {
        val prior = lobbyByUsername.remove(username) ?: return
        usernameByDisplayLower.remove(prior.displayName.lowercase(), username)
        logInfo("SocialGateway: ${prior.displayName} unregistered from lobby (${lobbyByUsername.size} online)")
        val acc = Accounts.findByUsername(username)
        if (acc != null) notifyMutualFriendsAbout(acc)
        leaveFriendsChat(username, notify = false)
        leaveClanChat(username, notify = false)
    }

    fun isOnline(username: String): Boolean =
        lobbyByUsername.containsKey(username) || presenceByUsername.containsKey(username)

    // =====================================================================
    //  World WebSocket Handling
    // =====================================================================

    /**
     * Handle a world server WebSocket connection.
     * Receives [WorldHello] to register, then processes presence and packet messages.
     */
    suspend fun handleWorldSocket(socket: DefaultWebSocketServerSession) = coroutineScope {
        var worldSession: WorldSession? = null

        try {
            for (frame in socket.incoming) {
                val text = (frame as? Frame.Text)?.readText() ?: continue
                val msg = try {
                    SocialGatewayWireJson.json.decodeFromString(SocialGatewayWireMessage.serializer(), text)
                } catch (e: Exception) {
                    logWarn("SocialGateway: invalid message JSON", e)
                    continue
                }

                when (msg) {
                    is WorldHello -> {
                        if (msg.token != EnvVars.socialGatewayToken) {
                            val resp = WorldHelloAck(ok = false, message = "Unauthorized")
                            socket.send(Frame.Text(SocialGatewayWireJson.json.encodeToString(SocialGatewayWireMessage.serializer(), resp)))
                            socket.close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Unauthorized"))
                            return@coroutineScope
                        }

                        val worldId = msg.world.worldId
                        val session = WorldSession(worldId, msg.world.worldName, socket)
                        worldsById[worldId]?.markClosed()
                        worldsById[worldId] = session
                        worldSession = session
                        LobbyState.worldList.put(msg.world.toWorld())

                        logInfo("SocialGateway: world connected worldId=$worldId name=${msg.world.worldName} host=${msg.world.publicHost}:${msg.world.port}")
                        val resp = WorldHelloAck(ok = true, message = null)
                        socket.send(Frame.Text(SocialGatewayWireJson.json.encodeToString(SocialGatewayWireMessage.serializer(), resp)))
                    }

                    is PlayerOnline -> {
                        val session = worldSession ?: continue
                        val username = msg.username.formatForProtocol()
                        // Player moved to a world -- evict stale lobby entry
                        lobbyByUsername.remove(username)
                        val presence = Presence(
                            username = username,
                            displayName = msg.displayName,
                            rightsCrown = msg.rightsCrown,
                            privateStatus = msg.privateStatus,
                            worldId = session.worldId,
                            worldName = session.worldName,
                            lastSeenMs = System.currentTimeMillis(),
                        )
                        presenceByUsername[username] = presence
                        usernameByDisplayLower[msg.displayName.lowercase()] = username
                        LobbyState.worldList.get(session.worldId)?.let { it.playersOnline++ }
                        launch {
                            try {
                                val acc = Accounts.findByUsername(username) ?: return@launch
                                initializeSocial(acc)
                                joinClanChat(username)
                                // Auto-rejoin friends chat from saved state
                                val savedFc = acc.social.currentFriendsChat
                                if (!savedFc.isNullOrBlank()) {
                                    joinFriendsChat(username, savedFc)
                                }
                            } catch (e: Exception) {
                                logWarn("SocialGateway: failed initial snapshots user=$username", e)
                            }
                        }
                    }

                    is PlayerOffline -> {
                        val username = msg.username.formatForProtocol()
                        val prior = presenceByUsername.remove(username)
                        if (prior != null) {
                            usernameByDisplayLower.remove(prior.displayName.lowercase(), username)
                            LobbyState.worldList.get(prior.worldId)?.let { if (it.playersOnline > 0) it.playersOnline-- }
                        }
                        launch {
                            val acc = Accounts.findByUsername(username)
                            if (acc != null) notifyMutualFriendsAbout(acc)
                        }
                        launch { leaveFriendsChat(username, notify = false) }
                        launch { leaveClanChat(username, notify = false) }
                    }

                    is ClientPacketEnvelope -> {
                        handleClientPacket(msg.username.formatForProtocol(), msg.packet)
                    }

                    is AnnouncementBroadcast -> {
                        broadcastAnnouncement(msg)
                    }

                    is FcSettingsUpdate -> {
                        launch { handleFcSettingsUpdate(msg) }
                    }

                    is ClanCreateRequest -> {
                        launch {
                            val success = createClan(msg.username, msg.clanName)
                            val response = ClanCreateResponse(
                                username = msg.username,
                                success = success,
                                message = if (success) null else "Unable to create clan.",
                            )
                            sendToWorld(worldSession?.worldId ?: return@launch, response)
                        }
                    }

                    is ClanLeaveRequest -> {
                        launch { leaveClan(msg.username) }
                    }

                    is ClanAddMemberRequest -> {
                        launch {
                            val account = Accounts.findByUsername(msg.inviterUsername) ?: return@launch
                            val clanName = account.social.clanName ?: return@launch
                            addClanMember(clanName, msg.targetUsername, msg.inviterUsername)
                        }
                    }

                    is ClanKickMemberRequest -> {
                        launch {
                            val account = Accounts.findByUsername(msg.kickerUsername) ?: return@launch
                            val clanName = account.social.clanName ?: return@launch
                            removeClanMember(clanName, msg.targetUsername, msg.kickerUsername)
                        }
                    }

                    is ClanSetRankRequest -> {
                        launch {
                            val account = Accounts.findByUsername(msg.username) ?: return@launch
                            val clanName = account.social.clanName ?: return@launch
                            setClanMemberRank(clanName, msg.targetUsername, msg.rank, msg.username)
                        }
                    }

                    is ClanBanMemberRequest -> {
                        launch {
                            val account = Accounts.findByUsername(msg.username) ?: return@launch
                            val clanName = account.social.clanName ?: return@launch
                            banClanMember(clanName, msg.targetUsername, msg.username)
                        }
                    }

                    is ClanUnbanMemberRequest -> {
                        launch {
                            val account = Accounts.findByUsername(msg.username) ?: return@launch
                            val clanName = account.social.clanName ?: return@launch
                            unbanClanMember(clanName, msg.targetUsername, msg.username)
                        }
                    }

                    else -> {
                        logTrace("SocialGateway ignoring message type=${msg::class.simpleName}")
                    }
                }
            }
        } catch (_: CancellationException) {
            // normal shutdown
        } catch (e: Exception) {
            Logger.logError("SocialGateway socket error", e)
        } finally {
            val session = worldSession
            if (session != null) {
                session.markClosed()
                LobbyState.worldList.remove(session.worldId)
                worldsById.remove(session.worldId, session)

                // Evict all presence entries for this world
                val toRemove = presenceByUsername
                    .filterValues { it.worldId == session.worldId }
                    .keys
                for (username in toRemove) {
                    val prior = presenceByUsername.remove(username)
                    if (prior != null) {
                        usernameByDisplayLower.remove(prior.displayName.lowercase(), username)
                    }
                    leaveFriendsChat(username, notify = false)
                    leaveClanChat(username, notify = false)
                }

                logInfo("SocialGateway: world disconnected worldId=${session.worldId}")
            }
        }
    }

    // =====================================================================
    //  Message Routing
    // =====================================================================

    /**
     * Send a packet to a user. Checks lobby first (direct session send),
     * then world presence (via ServerPacketEnvelope over WebSocket).
     */
    private suspend fun sendToUser(username: String, packet: ServerProt) {
        val lobby = lobbyByUsername[username]
        if (lobby != null) {
            try {
                lobby.session.send(packet)
                lobby.session.flush()
            } catch (e: Exception) {
                logWarn("Failed sending packet to lobby user=$username type=${packet::class.simpleName}", e)
            }
            return
        }
        val presence = presenceByUsername[username] ?: return
        sendToWorld(presence.worldId, ServerPacketEnvelope(username = username, packet = packet))
    }

    /**
     * Send a packet to multiple users. Groups world-bound packets by world
     * for batch delivery.
     */
    private suspend fun sendBatchToUsers(usernames: Collection<String>, packet: ServerProt) {
        val worldBuckets = mutableMapOf<Int, MutableList<String>>()
        for (username in usernames) {
            val lobby = lobbyByUsername[username]
            if (lobby != null) {
                try {
                    lobby.session.send(packet)
                    lobby.session.flush()
                } catch (e: Exception) {
                    logWarn("Failed sending packet to lobby user=$username type=${packet::class.simpleName}", e)
                }
                continue
            }
            val presence = presenceByUsername[username] ?: continue
            worldBuckets.getOrPut(presence.worldId) { mutableListOf() }.add(username)
        }
        for ((worldId, users) in worldBuckets) {
            if (users.size == 1) {
                sendToWorld(worldId, ServerPacketEnvelope(username = users[0], packet = packet))
            } else {
                sendToWorld(worldId, ServerPacketBatch(usernames = users, packet = packet))
            }
        }
    }

    /**
     * Capability-gated [sendToUser]: skips the send when the active codec has no encoder for
     * the packet type (e.g. FriendlistLoaded / UpdateIgnoreList, whose 948 opcodes are not
     * RE'd yet). The gap is logged once at INFO by [Codec.supportsServerProt] instead of
     * producing a warn per send — both lobby sessions and world-bound envelopes use the same
     * revision codec, so the check is valid for either delivery path.
     */
    private suspend fun sendToUserIfSupported(username: String, packet: ServerProt) {
        val codec = Codec.get(EnvVars.majorVersion)
        if (codec != null && !codec.supportsServerProt(packet::class)) return
        sendToUser(username, packet)
    }

    private suspend fun sendToWorld(worldId: Int, msg: SocialGatewayWireMessage) {
        val session = worldsById[worldId] ?: return
        val text = SocialGatewayWireJson.json.encodeToString(SocialGatewayWireMessage.serializer(), msg)
        try {
            session.sendText(text)
        } catch (e: Exception) {
            logWarn("Failed sending message to world=$worldId type=${msg::class.simpleName}", e)
        }
    }

    suspend fun broadcastAnnouncement(msg: AnnouncementBroadcast) {
        val text = SocialGatewayWireJson.json.encodeToString(SocialGatewayWireMessage.serializer(), msg)
        for (session in worldsById.values) {
            try {
                session.sendText(text)
            } catch (e: Exception) {
                logWarn("Failed sending announcement to world=${session.worldId}", e)
            }
        }
    }

    // =====================================================================
    //  Social Initialization
    // =====================================================================

    /**
     * Sends the full friend list, ignore list, chat filter, and mutual-friend
     * notifications using a single batch DB query instead of N+1 individual lookups.
     */
    suspend fun initializeSocial(account: Account) {
        val username = account.username

        // ONE batch DB query for all friends + ignores
        val allRelated = account.social.friends.keys + account.social.ignores
        val relatedAccounts = if (allRelated.isNotEmpty()) Accounts.findByUsernames(allRelated) else emptyList()
        val accountsByUsername = relatedAccounts.associateBy { it.username }

        // FriendlistLoaded must come FIRST (sets client LOADED=1, "ready for data"),
        // then FriendStatus populates the list and sets LOADED=2 ("fully loaded").
        val friendUpdates = account.social.friends.keys.mapNotNull { friendUsername ->
            val friendAccount = accountsByUsername[friendUsername] ?: return@mapNotNull null
            friendStatusUpdateFor(account, friendAccount)
        }
        sendToUserIfSupported(username, FriendlistLoaded())
        sendToUser(username, FriendStatus(friendUpdates))

        // Build + send ignore list
        val ignores = account.social.ignores.map { ign ->
            val display = accountsByUsername[ign]?.displayName ?: ign.formatForDisplay()
            UpdateIgnoreList.IgnoreEntry(displayName = display, previousName = "")
        }
        sendToUserIfSupported(username, UpdateIgnoreList(ignores))

        // Send chat filter echo
        sendToUser(username, ChatFilterSettingsPrivateChat(account.social.status))

        // Notify mutual friends -- no additional DB queries needed
        for (friendUsername in account.social.friends.keys) {
            val friendAccount = accountsByUsername[friendUsername] ?: continue
            if (!friendAccount.social.friends.containsKey(username)) continue
            if (presenceByUsername[friendUsername] == null && lobbyByUsername[friendUsername] == null) continue
            val update = friendStatusUpdateFor(friendAccount, account)
            sendToUser(friendUsername, FriendStatus(listOf(update)))
        }
    }

    // =====================================================================
    //  Friend Status Helpers
    // =====================================================================

    private fun friendStatusUpdateFor(viewer: Account, friend: Account): FriendStatus.FriendStatusUpdate {
        val canSeeFriend = friend.onlineTo(viewer)
        val friendPresence = if (canSeeFriend) presenceByUsername[friend.username] else null
        val friendLobby = if (canSeeFriend && friendPresence == null) lobbyByUsername[friend.username] else null
        val worldId = friendPresence?.worldId ?: if (friendLobby != null) 0 else 0
        val worldName = friendPresence?.worldName ?: ""
        val rank = viewer.social.friendsChat.ranks[friend.username] ?: 0
        val isOnline = friendPresence != null || friendLobby != null
        return FriendStatus.FriendStatusUpdate(
            displayName = friend.displayName,
            previousName = friend.prevDisplayName,
            worldId = if (isOnline && canSeeFriend) (friendPresence?.worldId ?: 1) else 0,
            fcRank = rank,
            notes = viewer.social.friends[friend.username]?.notes ?: "",
            worldName = if (isOnline && canSeeFriend) (friendPresence?.worldName ?: "Lobby") else "",
            worldFlags = if (isOnline && canSeeFriend) 1 else 0,
        )
    }

    private suspend fun notifyMutualFriendsAbout(account: Account) {
        if (account.social.friends.isEmpty()) return
        // ONE batch DB query for all friends instead of a findByUsername per friend.
        val friendAccounts = Accounts.findByUsernames(account.social.friends.keys)
        for (friendAccount in friendAccounts) {
            val friendUsername = friendAccount.username
            if (!friendAccount.social.friends.containsKey(account.username)) continue
            if (presenceByUsername[friendUsername] == null && lobbyByUsername[friendUsername] == null) continue

            val update = friendStatusUpdateFor(friendAccount, account)
            sendToUser(friendUsername, FriendStatus(listOf(update)))
        }
    }

    private suspend fun sendFriendListFull(toUsername: String) {
        val account = Accounts.findByUsername(toUsername) ?: return
        val friendAccounts = Accounts.findByUsernames(account.social.friends.keys)
        val updates = friendAccounts.map { friendStatusUpdateFor(account, it) }
        // FriendlistLoaded must come FIRST (sets client LOADED=1, "ready for data"),
        // then FriendStatus populates the list — same documented order as initializeSocial.
        sendToUserIfSupported(account.username, FriendlistLoaded())
        sendToUser(account.username, FriendStatus(updates))
    }

    private suspend fun sendIgnoreListFull(toUsername: String) {
        val account = Accounts.findByUsername(toUsername) ?: return
        val ignoreAccounts = Accounts.findByUsernames(account.social.ignores)
        val ignoreMap = ignoreAccounts.associateBy { it.username }
        val ignores = account.social.ignores.map { ign ->
            val display = ignoreMap[ign]?.displayName ?: ign.formatForDisplay()
            UpdateIgnoreList.IgnoreEntry(displayName = display, previousName = "")
        }
        sendToUserIfSupported(account.username, UpdateIgnoreList(ignores))
    }

    // =====================================================================
    //  Client Packet Dispatch
    // =====================================================================

    /**
     * Dispatch a decoded client packet to the appropriate social handler.
     * Called for both lobby players (directly) and world players (via WebSocket envelope).
     */
    suspend fun handleClientPacket(username: String, packet: ClientProt) {
        when (packet) {
            is FriendListAdd -> {
                val from = Accounts.findByUsername(username) ?: return
                val target = Accounts.findByDisplayName(packet.displayName)
                if (target == null) {
                    sendToUser(username, GameMessage(ChatMessageType.FRIEND_NOTIFICATION, "Unable to find player '${packet.displayName}'."))
                    return
                }
                if (target.username == from.username) {
                    sendToUser(username, GameMessage(ChatMessageType.FRIEND_NOTIFICATION, "You can't add yourself as a friend."))
                    return
                }
                if (from.social.friends.containsKey(target.username)) {
                    return // already friends
                }
                if (from.social.friends.size >= 400) {
                    sendToUser(username, GameMessage(ChatMessageType.FRIEND_NOTIFICATION, "Your friends list is full."))
                    return
                }
                from.social.friends[target.username] = FriendData()
                Accounts.save(from)
                sendFriendListFull(from.username)
                // Notify target if they have us in their list (mutual)
                if (target.social.friends.containsKey(from.username)) {
                    val update = friendStatusUpdateFor(target, from)
                    sendToUser(target.username, FriendStatus(listOf(update)))
                }
            }

            is FriendListDel -> {
                val from = Accounts.findByUsername(username) ?: return
                val target = Accounts.findByDisplayName(packet.displayName) ?: return
                if (from.social.friends.remove(target.username) != null) {
                    Accounts.save(from)
                }
                sendFriendListFull(from.username)
            }

            is IgnoreListAdd -> {
                val from = Accounts.findByUsername(username) ?: return
                val target = Accounts.findByDisplayName(packet.displayName)
                    ?: Accounts.findByUsername(packet.displayName.formatForProtocol())
                if (target == null) {
                    sendToUser(username, GameMessage(ChatMessageType.IGNORE_NOTIFICATION, "Unable to find player '${packet.displayName}'."))
                    return
                }
                if (target.username == from.username) {
                    sendToUser(username, GameMessage(ChatMessageType.IGNORE_NOTIFICATION, "You can't add yourself to your ignore list."))
                    return
                }
                if (from.social.ignores.contains(target.username)) return
                from.social.ignores.add(target.username)
                Accounts.save(from)
                sendIgnoreListFull(from.username)
            }

            is MessagePrivateSend -> {
                handlePrivateMessage(username, packet.toDisplayName, packet.message)
            }

            is ClanChannelKickUser -> {
                handleCcKick(username, packet)
            }

            else -> {
                logTrace("SocialGateway: unhandled client packet type=${packet::class.simpleName} from $username")
            }
        }
    }

    // =====================================================================
    //  Private Messages
    // =====================================================================

    private suspend fun handlePrivateMessage(fromUsername: String, toDisplayName: String, message: ByteArray) {
        val fromAccount = Accounts.findByUsername(fromUsername) ?: return
        if (fromAccount.isMuted()) {
            sendToUser(fromUsername, GameMessage(ChatMessageType.FRIEND_NOTIFICATION, "You are muted."))
            return
        }

        val toAccount = Accounts.findByDisplayName(toDisplayName)
        if (toAccount == null) {
            sendToUser(fromUsername, GameMessage(ChatMessageType.FRIEND_NOTIFICATION, "Unable to find $toDisplayName."))
            return
        }

        if (presenceByUsername[toAccount.username] == null && lobbyByUsername[toAccount.username] == null) {
            sendToUser(fromUsername, GameMessage(ChatMessageType.FRIEND_NOTIFICATION, "${toAccount.displayName} is offline."))
            return
        }

        if (!toAccount.onlineTo(fromAccount)) return
        if (toAccount.social.ignores.contains(fromAccount.username)) return

        // Decode the compressed message bytes to a string for the PM packets
        val messageText = String(message, Charsets.ISO_8859_1)

        sendToUser(
            toAccount.username,
            MessagePrivate(
                crown = fromAccount.rights,
                displayName = fromAccount.displayName,
                quickResponseName = fromAccount.username,
                message = messageText,
            )
        )
        sendToUser(
            fromAccount.username,
            MessagePrivateEcho(
                senderDisplayName = toAccount.displayName,
                message = messageText,
            )
        )
    }

    // =====================================================================
    //  Friends Chat (FC)
    // =====================================================================

    private fun rankAllowed(rankId: Int, requiredId: Int): Boolean {
        if (requiredId <= FriendsChatRank.UNRANKED.id) return true
        return rankId >= requiredId
    }

    private suspend fun getFcOwnerUsername(fromUsername: String, ownerDisplayNameOrNull: String?): String? {
        if (ownerDisplayNameOrNull.isNullOrBlank()) {
            val from = Accounts.findByUsername(fromUsername) ?: return null
            val raw = from.social.currentFriendsChat ?: return null
            return raw.formatForProtocol()
        }
        val owner = Accounts.findByDisplayName(ownerDisplayNameOrNull) ?: return null
        return owner.username
    }

    private fun getOrCreateRoom(ownerUsername: String): FcRoom {
        return fcRoomsByOwner.computeIfAbsent(ownerUsername) {
            FcRoom(ownerUsername = ownerUsername, members = ConcurrentHashMap.newKeySet())
        }
    }

    private suspend fun buildFriendsChatChannel(owner: Account, room: FcRoom): FriendsChatChannel {
        val minRankKick = owner.social.friendsChat.rankToKick
        val chatName = owner.social.friendsChat.name ?: owner.username

        val memberAccounts = Accounts.findByUsernames(room.members)
        val accByUser = memberAccounts.associateBy { it.username }

        val members = room.members.mapNotNull { memberUsername ->
            val acc = accByUser[memberUsername] ?: return@mapNotNull null
            val presence = presenceByUsername[memberUsername]
            val lobby = lobbyByUsername[memberUsername]
            val worldId = presence?.worldId ?: if (lobby != null) 0 else 0
            val worldName = presence?.worldName ?: ""
            val rank = if (memberUsername == owner.username) {
                FriendsChatRank.OWNER.id
            } else {
                owner.social.friendsChat.ranks[memberUsername] ?: FriendsChatRank.UNRANKED.id
            }
            FriendsChatChannel.FriendsChatPlayer(
                displayName = acc.displayName,
                username = acc.username,
                worldId = worldId,
                rank = rank,
                worldName = worldName,
            )
        }.toTypedArray()

        return FriendsChatChannel(
            clear = false,
            ownerDisplayName = owner.displayName,
            ownerUsername = owner.username,
            chatName = chatName,
            minRankCanKick = minRankKick,
            players = members,
        )
    }

    private suspend fun broadcastFcChannel(owner: Account, room: FcRoom) {
        val channel = buildFriendsChatChannel(owner, room)
        sendBatchToUsers(room.members, channel)
    }

    /**
     * Join a friends chat. If [ownerDisplayName] is null, joins the user's own FC.
     * Called from handleClientPacket or directly for lobby FC join.
     */
    suspend fun joinFriendsChat(username: String, ownerDisplayName: String?) {
        if (ownerDisplayName == null) {
            leaveFriendsChat(username, notify = true)
            return
        }

        val ownerUsername = getFcOwnerUsername(username, ownerDisplayName)
        if (ownerUsername == null) {
            sendToUser(username, GameMessage(ChatMessageType.FC_NOTIFICATION, "Unable to find that friends chat."))
            sendToUser(username, FriendsChatChannel(clear = true))
            return
        }

        val owner = Accounts.findByUsername(ownerUsername)
        if (owner == null) {
            sendToUser(username, GameMessage(ChatMessageType.FC_NOTIFICATION, "Unable to find that friends chat."))
            sendToUser(username, FriendsChatChannel(clear = true))
            return
        }

        // FC not set up check
        if (owner.social.friendsChat.name == null) {
            sendToUser(username, GameMessage(ChatMessageType.FC_NOTIFICATION, "The friends chat you are trying to join is not set up yet."))
            sendToUser(username, FriendsChatChannel(clear = true))
            return
        }

        val from = Accounts.findByUsername(username) ?: return

        // Ignore check: owner has joiner on ignore list
        if (owner.social.ignores.contains(from.username)) {
            sendToUser(username, GameMessage(ChatMessageType.FC_NOTIFICATION, "You are not allowed to join this user's friends chat channel."))
            sendToUser(username, FriendsChatChannel(clear = true))
            return
        }

        val fromRank = if (from.username == owner.username) {
            FriendsChatRank.OWNER.id
        } else {
            owner.social.friendsChat.ranks[from.username] ?: FriendsChatRank.UNRANKED.id
        }
        val required = owner.social.friendsChat.rankToEnter
        if (!rankAllowed(fromRank, required)) {
            sendToUser(username, GameMessage(ChatMessageType.FC_NOTIFICATION, "You are not allowed to join that friends chat."))
            sendToUser(username, FriendsChatChannel(clear = true))
            return
        }

        val room = getOrCreateRoom(owner.username)

        // Capacity check
        if (room.members.size >= 100) {
            sendToUser(username, GameMessage(ChatMessageType.FC_NOTIFICATION, "The channel you tried to join is currently full."))
            sendToUser(username, FriendsChatChannel(clear = true))
            return
        }

        // Temp ban check
        val banExpiry = room.tempBans[username]
        if (banExpiry != null) {
            if (System.currentTimeMillis() < banExpiry) {
                sendToUser(username, GameMessage(ChatMessageType.FC_NOTIFICATION, "You have been temporarily banned from this friends chat."))
                sendToUser(username, FriendsChatChannel(clear = true))
                return
            }
            room.tempBans.remove(username)
        }

        from.social.currentFriendsChat = owner.username
        Accounts.save(from)

        val existingOwner = fcOwnerByMember[username]
        if (existingOwner != null && existingOwner != owner.username) {
            leaveFriendsChat(username, notify = false)
        }

        room.members.add(username)
        fcOwnerByMember[username] = owner.username
        broadcastFcChannel(owner, room)
    }

    suspend fun leaveFriendsChat(username: String, notify: Boolean) {
        val ownerUsername = fcOwnerByMember.remove(username) ?: return
        val room = fcRoomsByOwner[ownerUsername] ?: return
        room.members.remove(username)
        if (notify) {
            sendToUser(username, FriendsChatChannel(clear = true))
        }
        val owner = Accounts.findByUsername(ownerUsername) ?: return
        broadcastFcChannel(owner, room)
        if (room.members.isEmpty()) {
            fcRoomsByOwner.remove(ownerUsername, room)
        }
    }

    suspend fun handleFcMessage(username: String, message: String) {
        val ownerUsername = fcOwnerByMember[username] ?: return
        val room = fcRoomsByOwner[ownerUsername] ?: return
        if (!room.members.contains(username)) return

        val owner = Accounts.findByUsername(ownerUsername) ?: return
        val fromAccount = Accounts.findByUsername(username) ?: return
        if (fromAccount.isMuted()) return

        val fromRank = if (username == owner.username) {
            FriendsChatRank.OWNER.id
        } else {
            owner.social.friendsChat.ranks[username] ?: FriendsChatRank.UNRANKED.id
        }
        val required = owner.social.friendsChat.rankToSpeak
        if (!rankAllowed(fromRank, required)) {
            sendToUser(username, GameMessage(ChatMessageType.FC_NOTIFICATION, "You are not allowed to speak in that friends chat."))
            return
        }

        val chatName = owner.social.friendsChat.name ?: owner.username
        sendBatchToUsers(
            room.members,
            MessageFriendsChat(
                crown = fromAccount.rights,
                displayName = fromAccount.displayName,
                quickResponseName = fromAccount.username,
                chatName = chatName,
                message = message,
            )
        )
    }

    suspend fun handleFcKick(kickerUsername: String, targetDisplayName: String) {
        val targetUsername = targetDisplayName.formatForProtocol()

        if (targetUsername == kickerUsername) {
            sendToUser(kickerUsername, GameMessage(ChatMessageType.FC_NOTIFICATION, "You can't kick yourself!"))
            return
        }

        val ownerUsername = fcOwnerByMember[kickerUsername] ?: return
        val room = fcRoomsByOwner[ownerUsername] ?: return
        if (!room.members.contains(targetUsername)) return

        val owner = Accounts.findByUsername(ownerUsername) ?: return
        val fromRank = if (kickerUsername == owner.username) {
            FriendsChatRank.OWNER.id
        } else {
            owner.social.friendsChat.ranks[kickerUsername] ?: FriendsChatRank.UNRANKED.id
        }
        val targetRank = if (targetUsername == owner.username) {
            FriendsChatRank.OWNER.id
        } else {
            owner.social.friendsChat.ranks[targetUsername] ?: FriendsChatRank.UNRANKED.id
        }
        val required = owner.social.friendsChat.rankToKick

        // Owner kick check
        if (targetRank >= FriendsChatRank.OWNER.id) {
            sendToUser(kickerUsername, GameMessage(ChatMessageType.FC_NOTIFICATION, "You cannot kick the owner of a channel."))
            return
        }
        if (!rankAllowed(fromRank, required)) {
            sendToUser(kickerUsername, GameMessage(ChatMessageType.FC_NOTIFICATION, "You do not have permission to kick from this friends chat."))
            return
        }
        if (kickerUsername != owner.username && targetRank >= fromRank) {
            sendToUser(kickerUsername, GameMessage(ChatMessageType.FC_NOTIFICATION, "You cannot kick that user."))
            return
        }

        // Apply 1-hour temp ban
        room.tempBans[targetUsername] = System.currentTimeMillis() + 3600000L

        room.members.remove(targetUsername)
        fcOwnerByMember.remove(targetUsername, ownerUsername)
        sendToUser(targetUsername, FriendsChatChannel(clear = true))
        broadcastFcChannel(owner, room)
    }

    private suspend fun handleFcSettingsUpdate(msg: FcSettingsUpdate) {
        val account = Accounts.findByUsername(msg.username) ?: return
        account.social.friendsChat.name = msg.name
        account.social.friendsChat.rankToEnter = msg.rankToEnter
        account.social.friendsChat.rankToSpeak = msg.rankToSpeak
        account.social.friendsChat.rankToKick = msg.rankToKick
        account.social.friendsChat.rankToLS = msg.rankToLS
        Accounts.save(account)

        val room = fcRoomsByOwner[msg.username]
        if (msg.name == null && room != null) {
            // FC dissolved -- kick all members
            for (member in room.members.toList()) {
                fcOwnerByMember.remove(member, msg.username)
                sendToUser(member, FriendsChatChannel(clear = true))
                sendToUser(member, GameMessage(ChatMessageType.FC_NOTIFICATION, "The channel has been disabled."))
            }
            room.members.clear()
            fcRoomsByOwner.remove(msg.username, room)
        } else if (room != null) {
            broadcastFcChannel(account, room)
        }
    }

    // =====================================================================
    //  Clan Chat (CC)
    // =====================================================================

    private fun clanRankAllowed(rankIconId: Int, requiredIconId: Int): Boolean {
        if (requiredIconId <= ClanRank.NONE.iconId) return true
        return rankIconId >= requiredIconId
    }

    private suspend fun resolveDisplayNames(usernames: Collection<String>): Map<String, String> {
        val accounts = Accounts.findByUsernames(usernames)
        return accounts.associate { it.username to it.displayName }
    }

    private suspend fun buildClanChannelFull(clan: Clan, room: CcRoom): ClanChannelFull {
        val displayNames = resolveDisplayNames(room.members)
        val chatters = room.members.mapNotNull { username ->
            val display = displayNames[username] ?: return@mapNotNull null
            val presence = presenceByUsername[username]
            val worldId = presence?.worldId ?: 0
            val rank = clan.getRank(username)
            ClanChannelFull.ClanChannelChatter(displayName = display, rank = rank.iconId, worldId = worldId)
        }.toTypedArray()

        return ClanChannelFull(
            main = true,
            clanName = clan.name,
            updateNum = room.updateNum,
            kickRank = clan.ccKickRank.iconId,
            talkRank = clan.ccChatRank.iconId,
            chatters = chatters,
        )
    }

    private suspend fun broadcastCcChannel(clan: Clan, room: CcRoom) {
        room.updateNum++
        val packet = buildClanChannelFull(clan, room)
        sendBatchToUsers(room.members, packet)
    }

    private suspend fun buildClanSettingsFull(clan: Clan, updateCount: Int): ClanSettingsFull {
        val allUsernames = clan.members.keys + clan.bannedUsers
        val displayNames = resolveDisplayNames(allUsernames)

        val members = clan.members.entries.map { (username, data) ->
            val display = displayNames[username] ?: username
            ClanSettingsFull.ClanSettingsMember(displayName = display, rank = data.rank.iconId)
        }.toTypedArray()

        val banned = clan.bannedUsers.map { username ->
            displayNames[username] ?: username
        }.toTypedArray()

        val settings = clan.settings.map { (key, value) ->
            val intKey = key.toInt()
            when (value) {
                is Int -> ClanSettingsFull.ClanVarSetting(key = intKey, intValue = value)
                is Long -> ClanSettingsFull.ClanVarSetting(key = intKey, longValue = value)
                is String -> ClanSettingsFull.ClanVarSetting(key = intKey, stringValue = value)
                else -> ClanSettingsFull.ClanVarSetting(key = intKey, intValue = 0)
            }
        }.toTypedArray()

        return ClanSettingsFull(
            main = true,
            clanName = clan.name,
            updateCount = updateCount,
            allowGuests = clan.ccChatRank == ClanRank.NONE,
            talkRank = clan.ccChatRank.iconId,
            kickRank = clan.ccKickRank.iconId,
            members = members,
            bannedUsers = banned,
            settings = settings,
        )
    }

    private suspend fun sendCcSettingsFull(clan: Clan, toUsername: String) {
        val room = ccRoomsByClanName[clan.name]
        val updateCount = room?.updateNum?.toInt() ?: 0
        sendToUser(toUsername, buildClanSettingsFull(clan, updateCount))
    }

    private suspend fun broadcastCcSettings(clan: Clan, room: CcRoom) {
        val packet = buildClanSettingsFull(clan, room.updateNum.toInt())
        sendBatchToUsers(room.members, packet)
    }

    suspend fun joinClanChat(username: String) {
        val account = Accounts.findByUsername(username) ?: return
        val clanName = account.social.clanName ?: return

        val clan = Clans.find(clanName) ?: return
        if (!clan.members.containsKey(username)) return

        val existingClan = ccClanByMember[username]
        if (existingClan != null) {
            if (existingClan == clanName) return // already in this CC
            leaveClanChat(username, notify = false)
        }

        val room = ccRoomsByClanName.computeIfAbsent(clanName) {
            CcRoom(clanName = clanName, members = ConcurrentHashMap.newKeySet())
        }
        room.members.add(username)
        ccClanByMember[username] = clanName

        sendCcSettingsFull(clan, username)
        sendToUser(username, VarclanEnable())
        broadcastCcChannel(clan, room)
    }

    suspend fun leaveClanChat(username: String, notify: Boolean) {
        val clanName = ccClanByMember.remove(username) ?: return
        val room = ccRoomsByClanName[clanName] ?: return
        room.members.remove(username)
        if (notify) {
            sendToUser(username, ClanChannelFull(main = true))
            sendToUser(username, VarclanDisable())
        }
        val clan = Clans.find(clanName)
        if (clan != null && room.members.isNotEmpty()) {
            broadcastCcChannel(clan, room)
        }
        if (room.members.isEmpty()) {
            ccRoomsByClanName.remove(clanName, room)
        }
    }

    private suspend fun handleCcKick(kickerUsername: String, packet: ClanChannelKickUser) {
        val clanName = ccClanByMember[kickerUsername] ?: return
        val room = ccRoomsByClanName[clanName] ?: return
        val clan = Clans.find(clanName) ?: return

        val targetUsername = packet.username.formatForProtocol()
        if (!room.members.contains(targetUsername)) return

        val kickerRank = clan.getRank(kickerUsername)
        val targetRank = clan.getRank(targetUsername)

        if (kickerRank.iconId < clan.ccKickRank.iconId) {
            sendToUser(kickerUsername, GameMessage(ChatMessageType.CLAN_NOTIFICATION, "You do not have permission to kick from this clan chat."))
            return
        }
        if (kickerUsername != clan.leaderUsername && targetRank.ordinal >= kickerRank.ordinal) {
            sendToUser(kickerUsername, GameMessage(ChatMessageType.CLAN_NOTIFICATION, "You cannot kick that user."))
            return
        }

        // If target is a guest (not in clan members), add temp ban
        if (!clan.members.containsKey(targetUsername)) {
            room.tempBans[targetUsername] = System.currentTimeMillis() + 3600000L
        }

        ccClanByMember.remove(targetUsername, clanName)
        room.members.remove(targetUsername)
        sendToUser(targetUsername, ClanChannelFull(main = true))
        sendToUser(targetUsername, VarclanDisable())
        broadcastCcChannel(clan, room)
    }

    suspend fun handleClanChatMessage(fromUsername: String, message: String) {
        val clanName = ccClanByMember[fromUsername] ?: return
        val room = ccRoomsByClanName[clanName] ?: return
        if (!room.members.contains(fromUsername)) return

        val clan = Clans.find(clanName) ?: return
        val fromAccount = Accounts.findByUsername(fromUsername) ?: return
        if (fromAccount.isMuted()) return

        val fromRank = clan.getRank(fromUsername)
        if (!clanRankAllowed(fromRank.iconId, clan.ccChatRank.iconId)) {
            sendToUser(fromUsername, GameMessage(ChatMessageType.CLAN_NOTIFICATION, "You are not allowed to speak in that clan chat."))
            return
        }

        val guest = !clan.members.containsKey(fromUsername)
        sendBatchToUsers(
            room.members,
            MessageClanChannel(
                guest = guest,
                crown = fromAccount.rights,
                displayName = fromAccount.displayName,
                message = message,
            )
        )
    }

    // =====================================================================
    //  Clan Management
    // =====================================================================

    suspend fun createClan(leaderUsername: String, clanName: String): Boolean {
        if (clanName.isBlank() || clanName.length > 12) return false
        if (!clanName.matches(Regex("^[a-zA-Z0-9 ]+$"))) return false

        val existing = Clans.find(clanName)
        if (existing != null) return false

        val account = Accounts.findByUsername(leaderUsername) ?: return false
        if (account.social.clanName != null) return false

        val clan = Clan(
            name = clanName,
            leaderUsername = leaderUsername,
        )
        clan.members[leaderUsername] = ClanMemberData(rank = ClanRank.OWNER)
        Clans.save(clan)

        account.social.clanName = clanName
        Accounts.save(account)

        joinClanChat(leaderUsername)
        return true
    }

    suspend fun addClanMember(clanName: String, targetUsername: String, inviterUsername: String): Boolean {
        val clan = Clans.find(clanName) ?: return false
        if (clan.members.size >= Clan.MAX_MEMBERS) return false

        val targetAccount = Accounts.findByUsername(targetUsername) ?: return false
        if (targetAccount.social.clanName != null) return false

        clan.members[targetUsername] = ClanMemberData(rank = ClanRank.RECRUIT)
        Clans.save(clan)

        targetAccount.social.clanName = clanName
        Accounts.save(targetAccount)

        // If target is online, join them to CC
        if (presenceByUsername.containsKey(targetUsername) || lobbyByUsername.containsKey(targetUsername)) {
            joinClanChat(targetUsername)
        }

        // Broadcast updated settings to current CC members
        val room = ccRoomsByClanName[clanName]
        if (room != null) {
            broadcastCcSettings(clan, room)
        }
        return true
    }

    suspend fun removeClanMember(clanName: String, targetUsername: String, kickerUsername: String? = null): Boolean {
        val clan = Clans.find(clanName) ?: return false

        if (kickerUsername != null) {
            val kickerRank = clan.getRank(kickerUsername)
            val targetRank = clan.getRank(targetUsername)
            if (kickerRank.iconId < clan.ccKickRank.iconId) return false
            if (kickerUsername != clan.leaderUsername && targetRank.ordinal >= kickerRank.ordinal) return false
        }

        // Owner leaving requires transferring ownership
        if (targetUsername == clan.leaderUsername) {
            val deputy = clan.members.entries
                .filter { it.key != targetUsername }
                .maxByOrNull { it.value.rank.ordinal }
            if (deputy == null) {
                // Last member, delete clan
                leaveClanChat(targetUsername, notify = true)
                Clans.delete(clanName)
                val account = Accounts.findByUsername(targetUsername)
                if (account != null) {
                    account.social.clanName = null
                    Accounts.save(account)
                }
                return true
            }
            clan.leaderUsername = deputy.key
            deputy.value.rank = ClanRank.OWNER
        }

        clan.members.remove(targetUsername)
        Clans.save(clan)

        val account = Accounts.findByUsername(targetUsername)
        if (account != null) {
            account.social.clanName = null
            Accounts.save(account)
        }

        leaveClanChat(targetUsername, notify = true)

        val room = ccRoomsByClanName[clanName]
        if (room != null) {
            broadcastCcSettings(clan, room)
        }
        return true
    }

    suspend fun leaveClan(username: String): Boolean {
        val account = Accounts.findByUsername(username) ?: return false
        val clanName = account.social.clanName ?: return false
        return removeClanMember(clanName, username)
    }

    suspend fun updateClanSettings(clanName: String, updates: Map<Int, Any>) {
        val clan = Clans.find(clanName) ?: return
        clan.settings.putAll(updates.mapKeys { it.key.toString() })
        Clans.save(clan)
        val room = ccRoomsByClanName[clanName]
        if (room != null) {
            broadcastCcSettings(clan, room)
        }
    }

    private suspend fun setClanMemberRank(clanName: String, targetUsername: String, rank: Int, setterUsername: String) {
        val clan = Clans.find(clanName) ?: return
        val setterRank = clan.getRank(setterUsername)
        if (setterRank.iconId < ClanRank.ADMIN.iconId && setterUsername != clan.leaderUsername) {
            sendToUser(setterUsername, GameMessage(ChatMessageType.CLAN_NOTIFICATION, "You do not have permission to change ranks."))
            return
        }
        val memberData = clan.members[targetUsername]
        if (memberData == null) {
            sendToUser(setterUsername, GameMessage(ChatMessageType.CLAN_NOTIFICATION, "That player is not a clan member."))
            return
        }
        val newRank = ClanRank.fromIconId(rank)
        memberData.rank = newRank
        Clans.save(clan)
        val room = ccRoomsByClanName[clanName]
        if (room != null) {
            broadcastCcSettings(clan, room)
            broadcastCcChannel(clan, room)
        }
    }

    private suspend fun banClanMember(clanName: String, targetUsername: String, bannerUsername: String) {
        val clan = Clans.find(clanName) ?: return
        val bannerRank = clan.getRank(bannerUsername)
        if (bannerRank.iconId < clan.ccKickRank.iconId) {
            sendToUser(bannerUsername, GameMessage(ChatMessageType.CLAN_NOTIFICATION, "You do not have permission to ban users."))
            return
        }
        if (clan.bannedUsers.contains(targetUsername)) return
        clan.bannedUsers.add(targetUsername)
        Clans.save(clan)
        // Also kick from CC if online
        leaveClanChat(targetUsername, notify = true)
        val room = ccRoomsByClanName[clanName]
        if (room != null) broadcastCcSettings(clan, room)
    }

    private suspend fun unbanClanMember(clanName: String, targetUsername: String, unbannerUsername: String) {
        val clan = Clans.find(clanName) ?: return
        val unbannerRank = clan.getRank(unbannerUsername)
        if (unbannerRank.iconId < clan.ccKickRank.iconId) {
            sendToUser(unbannerUsername, GameMessage(ChatMessageType.CLAN_NOTIFICATION, "You do not have permission to unban users."))
            return
        }
        if (!clan.bannedUsers.remove(targetUsername)) return
        Clans.save(clan)
        val room = ccRoomsByClanName[clanName]
        if (room != null) broadcastCcSettings(clan, room)
    }
}
