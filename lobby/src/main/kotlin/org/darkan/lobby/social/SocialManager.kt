package org.darkan.lobby.social

import org.darkan.core.Logger.logInfo
import org.darkan.core.Logger.logTrace
import org.darkan.core.formatForDisplay
import org.darkan.core.model.Account
import org.darkan.core.mongo.Accounts
import org.darkan.core.net.prot.UpdateFriendList
import org.darkan.core.net.session.GameSession
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages online lobby presence and social operations.
 * Simplified version of the reference SocialGateway — lobby-only, no multi-world routing.
 */
object SocialManager {

    data class LobbyPlayer(val account: Account, val session: GameSession)

    private val onlinePlayers = ConcurrentHashMap<String, LobbyPlayer>()

    fun registerPlayer(account: Account, session: GameSession) {
        onlinePlayers[account.username] = LobbyPlayer(account, session)
        logInfo("SocialManager: ${account.displayName} registered (${onlinePlayers.size} online)")
    }

    fun unregisterPlayer(username: String) {
        val removed = onlinePlayers.remove(username)
        if (removed != null) {
            logInfo("SocialManager: ${removed.account.displayName} unregistered (${onlinePlayers.size} online)")
            // Notify mutual friends that this player went offline
            notifyFriendsOfStatus(removed.account, online = false)
        }
    }

    fun isOnline(username: String): Boolean = onlinePlayers.containsKey(username)
    fun getPlayer(username: String): LobbyPlayer? = onlinePlayers[username]

    /** Build friend list entries with live online status for sending to client. */
    suspend fun buildFriendList(account: Account): List<UpdateFriendList.FriendEntry> {
        if (account.social.friends.isEmpty()) return emptyList()

        // Batch-lookup all friends from DB
        val friendAccounts = Accounts.findByUsernames(account.social.friends.keys)
        val friendMap = friendAccounts.associateBy { it.username }

        return account.social.friends.map { (username, friendData) ->
            val friendAccount = friendMap[username]
            val online = isOnline(username)
            val worldId = if (online) 1 else 0  // lobby = world 1

            UpdateFriendList.FriendEntry(
                displayName = friendAccount?.displayName ?: username.formatForDisplay(),
                previousName = friendAccount?.prevDisplayName ?: "",
                worldId = worldId,
                fcRank = account.social.friendsChat.ranks[username] ?: 0,
                notes = friendData.notes,
                worldName = if (online) "Darkan" else "",
                worldFlags = if (online) 1 else 0,  // bit0 = members
            )
        }
    }

    /** Notify all mutual friends that [account] came online/offline. */
    fun notifyFriendsOfStatus(account: Account, online: Boolean) {
        for ((friendUsername, friendData) in account.social.friends) {
            val friendPlayer = onlinePlayers[friendUsername] ?: continue
            // Only notify if the friend has us in their friend list too (mutual)
            if (account.username !in friendPlayer.account.social.friends) continue
            // Only notify if we're visible to them
            if (!account.onlineTo(friendPlayer.account)) continue

            val entry = UpdateFriendList.FriendEntry(
                displayName = account.displayName,
                previousName = account.prevDisplayName,
                worldId = if (online) 1 else 0,
                fcRank = friendPlayer.account.social.friendsChat.ranks[account.username] ?: 0,
                notes = friendPlayer.account.social.friends[account.username]?.notes ?: "",
                worldName = if (online) "Darkan" else "",
                worldFlags = if (online) 1 else 0,
            )
            friendPlayer.session.queuePacket(UpdateFriendList(listOf(entry)))
            logTrace("SocialManager: notified ${friendPlayer.account.displayName} of ${account.displayName} ${if (online) "online" else "offline"}")
        }
    }

    /** Add a friend to [account]'s friend list. Returns the new friend entry or null if not found. */
    suspend fun addFriend(account: Account, targetDisplayName: String): UpdateFriendList.FriendEntry? {
        val target = Accounts.findByDisplayName(targetDisplayName) ?: return null
        if (target.username == account.username) return null  // can't friend yourself
        if (target.username in account.social.friends) return null  // already friends

        account.social.friends[target.username] = org.darkan.core.model.FriendData(notes = "")
        Accounts.save(account)

        val online = isOnline(target.username) && target.onlineTo(account)
        return UpdateFriendList.FriendEntry(
            displayName = target.displayName,
            previousName = target.prevDisplayName,
            worldId = if (online) 1 else 0,
            worldName = if (online) "Darkan" else "",
            worldFlags = if (online) 1 else 0,
        )
    }

    /** Remove a friend from [account]'s friend list. */
    suspend fun removeFriend(account: Account, targetDisplayName: String) {
        val target = Accounts.findByDisplayName(targetDisplayName)
        val username = target?.username ?: targetDisplayName.lowercase().replace(" ", "_")
        account.social.friends.remove(username)
        Accounts.save(account)
    }

    /** Add to [account]'s ignore list. */
    suspend fun addIgnore(account: Account, targetDisplayName: String): Boolean {
        val target = Accounts.findByDisplayName(targetDisplayName) ?: return false
        if (target.username == account.username) return false
        if (target.username in account.social.ignores) return false

        account.social.ignores.add(target.username)
        Accounts.save(account)
        return true
    }
}
