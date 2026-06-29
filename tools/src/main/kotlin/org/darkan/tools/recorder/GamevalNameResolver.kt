package org.darkan.tools.recorder

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import org.darkan.core.net.recorder.ClientStateCrossCheck
import java.io.File

internal class GamevalNameResolver private constructor(private val gamevalsDir: File?) {

    data class Resolved(
        val gamevalType: String,
        val id: Int,
        val name: String?,
        val display: String,
    )

    data class ResolvedComponent(
        val gamevalType: String,
        val packedId: Int,
        val interfaceId: Int,
        val componentId: Int,
        val name: String?,
        val display: String,
    )

    private val json = Json { ignoreUnknownKeys = true }
    private val dictionaries = LinkedHashMap<String, Map<String, String>>()

    fun nameFor(gamevalType: String, id: Int): String? =
        nameForKey(gamevalType, id.toString())

    fun resolve(gamevalType: String, id: Int): Resolved {
        val name = nameFor(gamevalType, id)
        return Resolved(
            gamevalType = gamevalType,
            id = id,
            name = name,
            display = name?.let { "$gamevalType:$it($id)" } ?: "$gamevalType:${gamevalType}_$id",
        )
    }

    fun display(gamevalType: String, id: Int): String =
        resolve(gamevalType, id).display

    fun resolveComponentPacked(packedId: Int): ResolvedComponent {
        val interfaceId = (packedId ushr 16) and 0xffff
        val componentId = packedId and 0xffff
        val key = "$interfaceId:$componentId"
        val name = nameForKey(TYPE_COMPONENT, key)
        return ResolvedComponent(
            gamevalType = TYPE_COMPONENT,
            packedId = packedId,
            interfaceId = interfaceId,
            componentId = componentId,
            name = name,
            display = name?.let { "$TYPE_COMPONENT:$it($packedId; $key)" }
                ?: "$TYPE_COMPONENT:${TYPE_COMPONENT}_$packedId($key)",
        )
    }

    fun displayComponentPacked(packedId: Int): String =
        resolveComponentPacked(packedId).display

    fun varcNameFor(varId: Int): String? =
        nameFor(TYPE_VAR_CLIENT, varId)

    fun varcKeyDisplay(key: ClientStateCrossCheck.VarcKey): String =
        "kind${key.recordKind}:${varcLabel(key.varId)}"

    fun varpDisplay(varId: Int): String =
        display(TYPE_VAR_PLAYER, varId)

    fun objDisplay(objId: Int): String =
        display(TYPE_OBJ, objId)

    fun npcDisplay(npcId: Int): String =
        display(TYPE_NPC, npcId)

    fun display(state: ClientStateCrossCheck.VarcNumberState): String =
        "${varcKeyDisplay(state.key)}=${state.value}"

    fun display(state: ClientStateCrossCheck.VarcStringState): String =
        "${varcKeyDisplay(state.key)}=${JsonPrimitive(state.value)}"

    fun display(
        key: ClientStateCrossCheck.VarcKey,
        valueKind: Int,
        numberValue: Long?,
        stringValue: String?,
    ): String = when {
        stringValue != null -> "${varcKeyDisplay(key)}=${JsonPrimitive(stringValue)}"
        numberValue != null -> "${varcKeyDisplay(key)}=$numberValue"
        else -> "${varcKeyDisplay(key)}=<kind$valueKind>"
    }

    private fun varcLabel(varId: Int): String =
        varcNameFor(varId)?.let { "$it($varId)" } ?: "varc_$varId"

    private fun nameForKey(gamevalType: String, rawKey: String): String? =
        dictionary(gamevalType)[rawKey]

    private fun dictionary(gamevalType: String): Map<String, String> =
        dictionaries.getOrPut(gamevalType) { loadDictionary(gamevalType) }

    private fun loadDictionary(gamevalType: String): Map<String, String> {
        val rootDir = gamevalsDir ?: return emptyMap()
        val file = File(rootDir, "$gamevalType.json")
        if (!file.isFile) return emptyMap()
        return runCatching {
            val root = json.parseToJsonElement(file.readText()) as? JsonObject
                ?: return@runCatching emptyMap()
            val entries = root["entries"] as? JsonObject ?: return@runCatching emptyMap()
            entries.mapNotNull { (rawId, rawName) ->
                val name = rawName.jsonPrimitive.contentOrNull ?: return@mapNotNull null
                rawId to name
            }.toMap()
        }.getOrDefault(emptyMap())
    }

    companion object {
        const val TYPE_VAR_CLIENT = "var_client"
        const val TYPE_VAR_PLAYER = "var_player"
        const val TYPE_COMPONENT = "component"
        const val TYPE_OBJ = "obj"
        const val TYPE_NPC = "npc"

        private val defaultResolver: GamevalNameResolver by lazy { load() }

        fun default(): GamevalNameResolver = defaultResolver

        fun load(start: File = File(System.getProperty("user.dir"))): GamevalNameResolver =
            GamevalNameResolver(findGamevalsDir(start.absoluteFile))

        private fun findGamevalsDir(start: File): File? {
            generateSequence(start) { it.parentFile }.forEach { dir ->
                if (dir.name == "gamevals" && dir.isDirectory) return dir
                val candidate = File(dir, "re-resources/gamevals")
                if (candidate.isDirectory) return candidate
            }
            return null
        }
    }
}
