package org.darkan.core.mongo

import com.mongodb.client.model.Filters
import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.Indexes
import com.mongodb.client.model.ReplaceOptions
import kotlinx.coroutines.flow.firstOrNull
import org.darkan.core.Logger
import org.darkan.core.model.Clan

/**
 * MongoDB `clans` collection operations.
 */
object Clans {
    private val collection get() = MongoManager.database.getCollection<Clan>("clans")

    suspend fun ensureIndexes() {
        collection.createIndex(Indexes.ascending("name"), IndexOptions().unique(true))
        collection.createIndex(Indexes.ascending("leaderUsername"))
        Logger.log("Clans", "Indexes ensured on clans collection")
    }

    suspend fun find(name: String): Clan? {
        return collection.find(Filters.eq("name", name)).firstOrNull()
    }

    suspend fun findByLeader(leaderUsername: String): Clan? {
        return collection.find(Filters.eq("leaderUsername", leaderUsername)).firstOrNull()
    }

    suspend fun save(clan: Clan) {
        collection.replaceOne(
            Filters.eq("name", clan.name),
            clan,
            ReplaceOptions().upsert(true)
        )
    }

    suspend fun delete(name: String) {
        collection.deleteOne(Filters.eq("name", name))
    }
}
