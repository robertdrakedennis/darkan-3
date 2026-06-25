package com.undercut.profiling

import com.undercut.game.bootstrap.Bootstrap
import com.undercut.game.hooks.impl.SDLKeycode
import com.undercut.game.input.ShadowClickMode
import com.undercut.util.JsonFileManager
import com.undercut.util.Logger
import com.undercut.util.random
import java.io.File
import kotlin.system.exitProcess

class PlayerProfiles {
    companion object {
        private val loadedProfiles = mutableMapOf<String, PlayerProfile>()
        val DEFAULTS = PlayerProfile()

        fun get(): PlayerProfile {
            val name = Bootstrap.client.loggedInPlayer.getPlayerName()
            return if (name == null) DEFAULTS else getByName(name)
        }

        fun getByName(name: String): PlayerProfile {
            loadedProfiles[name]?.let { return it }
            val profile = loadProfile(name)
            profile.applyDefaults()
            loadedProfiles[name] = profile
            saveProfile(name, profile)

            return profile
        }

        private fun saveProfile(username: String, profile: PlayerProfile) {
            try {
                val sanitizedUsername = sanitizeFilename(username)
                val profilesDir = File("${System.getProperty("user.home")}/.undercut/profiles")
                if (!profilesDir.exists())
                    profilesDir.mkdirs()
                val configFile = File(profilesDir, "$sanitizedUsername.json")
                JsonFileManager.saveJsonFile(profile, configFile)
            } catch (e: Exception) {
                Logger.handle(e)
                exitProcess(5)
            }
        }

        private fun loadProfile(username: String): PlayerProfile {
            try {
                val sanitizedUsername = sanitizeFilename(username)
                val profilesDir = File("${System.getProperty("user.home")}/.undercut/profiles")
                val configFile = File(profilesDir, "$sanitizedUsername.json")

                return if (configFile.exists() && configFile.length() > 0) {
                    JsonFileManager.loadJsonFile(configFile, PlayerProfile::class.java)
                } else {
                    PlayerProfile()
                }
            } catch (e: Exception) {
                Logger.handle(e)
                return PlayerProfile()
            }
        }

        private fun sanitizeFilename(filename: String): String {
            return filename
                .replace(Regex("[<>:\"/\\\\|?*]"), "_")
                .replace(Regex("\\s+"), "_")
                .take(100)
                .lowercase()
        }
    }
}

class PlayerProfile {
    var afkLogoutRefreshSeconds = random(400, 425)
    var afkLogoutRefreshVariance = random(15, 23)
    var afkLogoutRefreshKey = setOf(SDLKeycode.LALT, SDLKeycode.LCTRL, SDLKeycode.PAGEDOWN, SDLKeycode.PAGEUP).random()
    var gaussVariance = 0.4
    var walkPathClickTime = random(2200, 4100)
    var futurePathStepMin = random(10, 13)
    var futurePathStepMax = random(futurePathStepMin+3, 20)
    var walkPathDeviation = random(1, 3)
    var minimapWalkPerc = random(10, 60)
    var interactDistanceRange = random(16, 20)

    var synthInputEnabled: Boolean = false
    var synthShadowDoActions: Boolean = true
    var synthIdleFiller: Boolean = false
    var synthShadowClickMode: ShadowClickMode = ShadowClickMode.MOVE_ONLY
    var synthModelPlayer: String = ""
    var synthVisualizerEnabled: Boolean = true

    // Per-profile randomization for the synthetic input system. Persisted to JSON so
    // each account has its own fingerprint instead of a shared constant signature.
    var synthSendToServer: Boolean = true
    var synthIdlePauseMinMs: Long = random(700, 2200).toLong()
    var synthIdlePauseMaxMs: Long = (synthIdlePauseMinMs + random(1800, 6000)).coerceAtMost(12_000L)
    var synthPeakPxPerSecond: Float = random(1050, 1750).toFloat()
    var synthTremorPx: Float = (random(15, 38) / 10f)            // 1.5–3.8 px
    var synthOffsetAmplification: Float = (random(160, 290) / 10f) // 16–29
    var synthMomentumBlend: Float = (random(40, 70) / 100f)       // 0.40–0.70
    var synthMinTrajectoryMs: Long = random(90, 180).toLong()
    var synthMinTravelPx: Float = random(20, 40).toFloat()

    fun applyDefaults() {
        for (field in this::class.java.declaredFields) {
            field.isAccessible = true
            if (field.get(this) == null) {
                field.set(this, field.get(PlayerProfiles.DEFAULTS))
            }
        }
    }

    val afkLogoutMinMillis get() = (PlayerProfiles.get().afkLogoutRefreshSeconds * 1000L) - PlayerProfiles.get().afkLogoutRefreshVariance * 1000L
    val afkLogoutMaxMillis get() = (PlayerProfiles.get().afkLogoutRefreshSeconds * 1000L) + PlayerProfiles.get().afkLogoutRefreshVariance * 1000L
}