package com.undercut.script.scheduler

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import com.undercut.game.Skill
import java.io.FileWriter
import java.lang.reflect.Type

/**
 * Small JSON-backed store for schedules.
 * - Directory: $HOME/.undercut/schedules/
 * - Files: {sanitizedName}.json
 * - No UI dependencies; logs via println on errors
 */
object SchedulesStore {

    private const val DIR_NAME = ".undercut"
    private const val SCHEDULES_DIR = "schedules"


    private val listType: Type = object : TypeToken<List<ScheduleItem>>() {}.type

    /**
     * Gson configuration:
     * - ScheduleItem.configuration (JsonObject?) is handled automatically by Gson.
     *   When present it is saved/loaded; when missing in older presets it deserializes as null
     *   to preserve backward compatibility. No custom adapter is required for this field.
     * - StopCondition uses a custom adapter for polymorphic round-trip and remains unchanged.
     */
    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(StopCondition::class.java, StopConditionAdapter())
        .create()

    private fun baseDir(): File {
        val userHome = System.getProperty("user.home")
        val dir = File(userHome).resolve(DIR_NAME).resolve(SCHEDULES_DIR)
        if (!dir.exists()) {
            try {
                dir.mkdirs()
            } catch (t: Throwable) {
                println("SchedulesStore: failed to create directory ${dir.absolutePath}: ${t.message}")
            }
        }
        return dir
    }

    private fun sanitizeName(name: String): String {
        val trimmed = name.trim()
        val sanitized = trimmed.map { ch ->
            when (ch) {
                in 'a'..'z', in 'A'..'Z', in '0'..'9', '-', '_' -> ch
                else -> '_'
            }
        }.joinToString("")
        val noDots = sanitized.replace("..", "_").trim('_')
        return if (noDots.isEmpty()) "default" else noDots.take(128)
    }

    private fun fileFor(name: String): File {
        val safe = sanitizeName(name)
        return baseDir().resolve("$safe.json")
    }

    /**
     * List available schedule preset names (without .json extension).
     *
     * Returns an empty list on error and logs a warning via println; never throws.
     */
    @Synchronized
    fun listPresets(): List<String> = try {
        val dir = baseDir()
        (dir.listFiles { f -> f.isFile && f.name.endsWith(".json", ignoreCase = true) } ?: emptyArray())
            .map { it.name.removeSuffix(".json") }
            .sorted()
    } catch (t: Throwable) {
        println("SchedulesStore: listPresets failed: ${t.message}")
        emptyList()
    }

    /**
     * Save [items] under the given [name]. Overwrites any existing file with the same name.
     *
     * Errors are logged and ignored; this method never throws.
     */
    @Synchronized
    fun save(name: String, items: List<ScheduleItem>) {
        val file = fileFor(name)
        try {
            FileWriter(file, false).use { fw ->
                gson.toJson(items, listType, fw)
            }
        } catch (t: Throwable) {
            println("SchedulesStore: save failed for '${file.absolutePath}': ${t.message}")
        }
    }

    /**
     * Load schedule items for [name]. Returns an empty list when the preset does not
     * exist or on error. Errors are logged and never thrown to callers.
     */
    @Synchronized
    fun load(name: String): List<ScheduleItem> {
        val file = fileFor(name)
        if (!file.exists()) return emptyList()
        return try {
            FileReader(file).use { fr ->
                gson.fromJson<List<ScheduleItem>>(fr, listType) ?: emptyList()
            }
        } catch (t: Throwable) {
            println("SchedulesStore: load failed for '${file.absolutePath}': ${t.message}")
            emptyList()
        }
    }

    /**
     * Adapter for sealed StopCondition to enable round-trip JSON.
     * Injects a 'type' discriminator and serializes subtype fields.
     */
    private class StopConditionAdapter : JsonSerializer<StopCondition>, JsonDeserializer<StopCondition> {
        override fun serialize(src: StopCondition?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
            if (src == null || context == null) return JsonNull.INSTANCE
            val obj = JsonObject()
            when (src) {
                is StopCondition.TimeBased -> {
                    obj.addProperty("type", "TimeBased")
                    obj.addProperty("durationMs", src.durationMs)
                }
                is StopCondition.LevelBased -> {
                    obj.addProperty("type", "LevelBased")
                    obj.addProperty("skill", src.skill.name)
                    obj.addProperty("targetLevel", src.targetLevel)
                }
            }
            return obj
        }

        override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): StopCondition {
            val obj = json?.asJsonObject ?: throw JsonParseException("Invalid StopCondition JSON")
            return when (obj.get("type")?.asString) {
                "TimeBased" -> StopCondition.TimeBased(obj.get("durationMs").asLong)
                "LevelBased" -> {
                    val skillName = obj.get("skill").asString
                    val target = obj.get("targetLevel").asInt
                    val skill = try { Skill.valueOf(skillName) } catch (_: Throwable) {
                        throw JsonParseException("Unknown skill: $skillName")
                    }
                    StopCondition.LevelBased(skill, target)
                }
                else -> throw JsonParseException("Unknown StopCondition type")
            }
        }
    }
}
