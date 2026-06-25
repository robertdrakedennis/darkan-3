package org.darkan.core.net.login

import com.mongodb.client.model.Filters
import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.Indexes
import com.mongodb.client.model.ReplaceOptions
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.Serializable
import org.darkan.core.Logger.logTrace
import org.darkan.core.Logger.logWarn
import org.darkan.core.mongo.MongoManager
import java.security.SecureRandom

/**
 * Cross-process lobby -> world session-token bridge.
 *
 * `:lobby` and `:world` are SEPARATE JVMs/ports (a hard requirement), so the authorization the
 * lobby grants at login-success must survive a process boundary. We use the only shared store both
 * servers already connect to — MongoDB — plus a cryptographically-signed [LoginToken] (HMAC over
 * the username with the shared [org.darkan.core.EnvVars.worldLoginTokenSecret]) for integrity.
 *
 * Flow:
 *  1. Lobby login-success: [issue] writes an authorization for the player's username (TTL'd) and
 *     returns the random [IssuedWorldLogin.sessionId1]/[IssuedWorldLogin.sessionId2] longs, which the
 *     lobby puts into the login-data `sessionToken1`/`sessionToken2` fields the client carries.
 *  2. World reconnect (loginType 2): the world reads the username from the (reliable) XTEA section of
 *     the world login block and calls [validate]. No second password/credential exchange occurs.
 *
 * ## Why username-keyed (not connection-bound — yet)
 * The exact byte layout of the RECONNECT RSA block is not yet fully reverse-engineered
 * (world_handoff_wire_spec uncertainty #3), so we deliberately do NOT depend on transporting an
 * opaque token through it. The username is produced identically on both sides (lobby:
 * `account.username`; world: XTEA username -> `formatForProtocol()`), so it is the safe join key.
 * The random [sessionId1]/[sessionId2] are persisted and carried in the login-data session-token
 * fields so connection-level binding can be added with zero schema change once that RSA layout is
 * confirmed (the world would then additionally require the carried session id to match the stored
 * one).
 */
@Serializable
data class WorldLoginAuthorization(
    val username: String,
    val sessionId1: Long,
    val sessionId2: Long,
    /** Signed [LoginToken] (HMAC, shared secret) — integrity check independent of the store. */
    val token: String,
    val issuedAt: Long,
    val expiresAt: Long,
)

/** Returned to the lobby so it can emit the session-token longs in the login-data block. */
data class IssuedWorldLogin(val sessionId1: Long, val sessionId2: Long, val token: String)

object WorldLoginTokens {
    private const val COLLECTION = "world_login_tokens"
    private val random = SecureRandom()
    private val collection get() = MongoManager.database.getCollection<WorldLoginAuthorization>(COLLECTION)

    suspend fun ensureIndexes() {
        collection.createIndex(Indexes.ascending("username"), IndexOptions().unique(true))
    }

    /**
     * Lobby side: authorize [username] to enter a world for [ttlMs]. Idempotent per username
     * (re-login overwrites the prior authorization). Returns the session-token longs to embed in the
     * lobby login-data response.
     */
    suspend fun issue(username: String, ttlMs: Long, secret: String): IssuedWorldLogin {
        val now = System.currentTimeMillis()
        val token = LoginToken.issue(username, now, ttlMs, secret)
        val auth = WorldLoginAuthorization(
            username = username,
            sessionId1 = random.nextLong(),
            sessionId2 = random.nextLong(),
            token = token,
            issuedAt = now,
            expiresAt = now + ttlMs,
        )
        collection.replaceOne(Filters.eq("username", username), auth, ReplaceOptions().upsert(true))
        return IssuedWorldLogin(auth.sessionId1, auth.sessionId2, token)
    }

    /**
     * World side: true if [username] currently holds a valid (unexpired, signature-verified)
     * lobby authorization.
     *
     * Not consumed on success: a client may legitimately reconnect within the TTL (e.g. a quick
     * world-hop back, or a transient reconnect retry — see LoginResult RECONNECT_TRY_AGAIN). The
     * per-username upsert in [issue] plus the TTL bound staleness instead.
     */
    suspend fun validate(username: String, secret: String): Boolean {
        val now = System.currentTimeMillis()
        val auth = collection.find(Filters.eq("username", username)).firstOrNull()
        if (auth == null) {
            logTrace("WorldLoginTokens: no lobby authorization on record for '$username'")
            return false
        }
        if (now >= auth.expiresAt) {
            logTrace("WorldLoginTokens: lobby authorization for '$username' expired")
            return false
        }
        val verified = LoginToken.verify(auth.token, now, secret)
        if (verified == null || verified.username != username) {
            logWarn("WorldLoginTokens: signed-token integrity/username check failed for '$username'")
            return false
        }
        return true
    }
}
