package org.darkan.core.mongo

import com.mongodb.client.model.CountOptions
import com.mongodb.client.model.Filters
import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.Indexes
import com.mongodb.client.model.ReplaceOptions
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.darkan.core.Logger
import org.darkan.core.formatForDisplay
import org.darkan.core.formatForProtocol
import org.darkan.core.model.Account
import org.darkan.core.security.PasswordHash

/**
 * MongoDB `accounts` collection operations.
 * Schema based on ~/darkan/server reference.
 */
object Accounts {
    private val collection get() = MongoManager.database.getCollection<Account>("accounts")

    suspend fun ensureIndexes() {
        collection.createIndex(Indexes.ascending("username"), IndexOptions().unique(true))
        collection.createIndex(Indexes.ascending("email"), IndexOptions().unique(true).sparse(true))
        collection.createIndex(Indexes.ascending("displayName"))
        Logger.log("Accounts", "Indexes ensured on accounts collection")
    }

    suspend fun findByUsername(username: String): Account? {
        return collection.find(Filters.eq("username", username.lowercase())).firstOrNull()
    }

    suspend fun findByEmail(email: String): Account? {
        return collection.find(Filters.eq("email", email.lowercase())).firstOrNull()
    }

    suspend fun findByDisplayName(displayName: String): Account? {
        val usernameKey = displayName.lowercase().replace(" ", "_")
        // Single round-trip: match either field, then prefer the display-name hit.
        val matches = collection.find(
            Filters.or(
                Filters.eq("displayName", displayName),
                Filters.eq("username", usernameKey),
            )
        ).toList()
        return matches.firstOrNull { it.displayName == displayName } ?: matches.firstOrNull()
    }

    /** Batch lookup for friend list initialization — avoids N+1 queries. */
    suspend fun findByUsernames(usernames: Collection<String>): List<Account> {
        if (usernames.isEmpty()) return emptyList()
        return collection.find(Filters.`in`("username", usernames.toList())).toList()
    }

    suspend fun exists(username: String): Boolean {
        // countDocuments with limit 1 avoids deserializing a full Account just to null-check.
        return collection.countDocuments(
            Filters.eq("username", username.lowercase()),
            CountOptions().limit(1)
        ) > 0
    }

    /** Create a new account. Returns the created account. */
    suspend fun create(username: String, email: String, password: String, displayName: String? = null): Account {
        val proto = username.formatForProtocol()
        val account = Account(
            username = proto,
            email = email.lowercase(),
            displayName = displayName ?: proto.formatForDisplay(),
            passwordHash = PasswordHash.hashSuspend(password),
        )
        collection.insertOne(account)
        Logger.log("Accounts", "Created account: ${account.username} (${account.displayName})")
        return account
    }

    /** Auto-create for SSO login — creates with minimal data if account doesn't exist. */
    suspend fun getOrCreate(username: String): Account {
        val proto = username.formatForProtocol()
        return findByUsername(proto) ?: create(
            username = proto,
            email = "$proto@darkan.local",
            password = "sso_${System.currentTimeMillis()}",
        )
    }

    /** Persist account changes. */
    suspend fun save(account: Account) {
        collection.replaceOne(
            Filters.eq("username", account.username),
            account,
            ReplaceOptions().upsert(true)
        )
    }
}
