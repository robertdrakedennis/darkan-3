package org.darkan.core.model

import kotlinx.serialization.Serializable

/**
 * Player account stored in MongoDB `accounts` collection.
 * Schema based on ~/darkan/server reference (logic only, not protocol).
 */
@Serializable
data class Account(
    val username: String,              // protocol-formatted (lowercase, no spaces)
    var email: String = "",
    var displayName: String = "",
    var prevDisplayName: String = "",
    var passwordHash: String = "",     // Argon2
    var rights: Int = 0,               // 0=player, 1=mod, 2=admin
    var banned: Long = 0,              // timestamp when ban expires (0 = not banned)
    var muted: Long = 0,               // timestamp when mute expires (0 = not muted)
    var lastIp: String? = null,
    var social: Social = Social(),
) {
    fun isBanned() = banned > 0 && System.currentTimeMillis() < banned
    fun isMuted() = muted > 0 && System.currentTimeMillis() < muted

    /** Whether [other] can see this account's online status. */
    fun onlineTo(other: Account): Boolean {
        if (social.status == 2) return false                              // hidden from all
        if (social.status == 1 && other.username !in social.friends) return false // friends only
        return true
    }
}

@Serializable
data class Social(
    var friends: MutableMap<String, FriendData> = mutableMapOf(), // protocol username → per-friend data
    var ignores: MutableSet<String> = mutableSetOf(),              // protocol usernames
    var status: Int = 0,                                           // 0=all, 1=friends only, 2=off
    var currentFriendsChat: String? = null,                        // FC owner username currently joined
    var clanName: String? = null,                                  // clan membership
    var friendsChat: FriendsChat = FriendsChat(),                  // owned FC settings
)

@Serializable
data class FriendData(
    var notes: String = "",            // player-set notes for this friend
    var addedAt: Long = System.currentTimeMillis(),
)

@Serializable
data class FriendsChat(
    var name: String? = null,                                    // channel name (null = disabled)
    var ranks: MutableMap<String, Int> = mutableMapOf(),          // username → rank
    var rankToEnter: Int = -1,                                   // -1=anyone
    var rankToSpeak: Int = -1,
    var rankToKick: Int = 7,                                     // 7=owner only
    var rankToLS: Int = -1,
    var coinshare: Boolean = false,
)
