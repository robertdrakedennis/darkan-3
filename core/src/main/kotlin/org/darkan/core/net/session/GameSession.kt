package org.darkan.core.net.session

import io.ktor.utils.io.*
import org.darkan.core.net.Isaac
import org.darkan.core.net.Session
import org.darkan.core.net.prot.AntiCheatChallenge
import org.darkan.core.net.prot.Codec

class GameSession(
    write: ByteWriteChannel,
    isaacIn: Isaac,
    isaacOut: Isaac,
    ip: String,
    codec: Codec,
    /** Protocol username of the logged-in player. Set after login. */
    var username: String = "",
) : Session(write, isaacIn, isaacOut, ip, codec) {
    var pendingAntiCheatChallenge: AntiCheatChallenge? = null
        private set

    private var pendingAntiCheatChallengeSentAtMs: Long = 0L

    fun markAntiCheatChallengePending(challenge: AntiCheatChallenge, sentAtMs: Long) {
        pendingAntiCheatChallenge = challenge
        pendingAntiCheatChallengeSentAtMs = sentAtMs
    }

    fun clearAntiCheatChallenge() {
        pendingAntiCheatChallenge = null
        pendingAntiCheatChallengeSentAtMs = 0L
    }

    fun isAntiCheatChallengeTimedOut(nowMs: Long, timeoutMs: Long): Boolean =
        pendingAntiCheatChallenge != null && nowMs - pendingAntiCheatChallengeSentAtMs > timeoutMs
}
