package org.darkan.core.mongo

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import org.darkan.core.EnvVars
import org.darkan.core.Logger

/**
 * Singleton MongoDB connection manager.
 */
object MongoManager {
    private lateinit var client: MongoClient
    lateinit var database: MongoDatabase
        private set

    fun init() {
        client = MongoClient.create(EnvVars.mongoUri)
        database = client.getDatabase(EnvVars.mongoDatabase)
        Logger.log("MongoManager", "Connected to ${EnvVars.mongoUri}/${EnvVars.mongoDatabase}")
    }

    fun close() {
        if (::client.isInitialized) client.close()
    }
}
