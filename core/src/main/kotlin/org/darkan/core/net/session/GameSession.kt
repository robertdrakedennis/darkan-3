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
    /** Optional owner object for module-specific session state, e.g. the world player. */
    var attachment: Any? = null

    var pendingAntiCheatChallenge: AntiCheatChallenge? = null
        private set
    var lobbyWorldSwitchSent: Boolean = false

    private var pendingAntiCheatChallengeSentAtMs: Long = 0L
    private var lastAntiCheatChallengeSentAtMs: Long = System.currentTimeMillis()

    fun markAntiCheatChallengePending(challenge: AntiCheatChallenge, sentAtMs: Long) {
        pendingAntiCheatChallenge = challenge
        pendingAntiCheatChallengeSentAtMs = sentAtMs
        lastAntiCheatChallengeSentAtMs = sentAtMs
    }

    fun clearAntiCheatChallenge() {
        pendingAntiCheatChallenge = null
        pendingAntiCheatChallengeSentAtMs = 0L
    }

    fun isAntiCheatChallengeTimedOut(nowMs: Long, timeoutMs: Long): Boolean =
        pendingAntiCheatChallenge != null && nowMs - pendingAntiCheatChallengeSentAtMs > timeoutMs

    fun canIssueAntiCheatChallenge(nowMs: Long, intervalMs: Long): Boolean =
        pendingAntiCheatChallenge == null &&
            nowMs - lastAntiCheatChallengeSentAtMs >= intervalMs
}
