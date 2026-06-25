package com.undercut.script.impl.trent

import com.undercut.game.Skill
import com.undercut.game.chat.MessageType
import com.undercut.game.nxt.entity.npc.NPC
import com.undercut.script.Script
import com.undercut.script.ScriptDescription
import com.undercut.script.api.*
import com.undercut.script.event.Event
import com.undercut.script.event.impl.Chat
import com.undercut.ui.backend.dsl.ImGuiDsl
import com.undercut.ui.backend.dsl.scopes.image
import com.undercut.ui.backend.dsl.scopes.text
import com.undercut.ui.backend.dsl.scopes.xpProgressBar
import com.undercut.ui.backend.native.SpriteIds
import com.undercut.ui.backend.native.spriteTexture
import com.undercut.util.*

@ScriptDescription(
    name = "AIO Wisp Gatherer",
    version = "1.0.0",
    author = "Trent",
    description = "Gathers wisps at any colony you are near."
)
class AIODivinationWisps : Script() {
    var startTime = 0L
    var startingXp = 0
    var tryGrabFragmentUntil = 0L
    val grabFragment
        get() = tryGrabFragmentUntil > System.currentTimeMillis()
    val energies get() = inventory.count(""".*energy$""".toRegex())
    var startEnergies = 0

    override fun onStart() {
        println("AIO divination wisp gatherer started.")
        startTime = System.currentTimeMillis()
        startingXp = getXp(Skill.DIVINATION)
        startEnergies = energies
    }

    override suspend fun loop() {
        if (prayerPoints < 100 && drinkPrayerPot()) delay(600, 300)
        if (prayerPoints > 1 && onCursesPrayers && getRealLevel(Skill.PRAYER) >= 87) togglePrayer(Prayer.CHRONICLE_ATTRACTION, true)

        if (inventory.isFull) {
            if (interactClosestObject("Convert memories")) {
                println("Converting memories.")
                waitThenDelayUntil(1500, 60000) { !inventory.hasItem(Regex(".* memory$")) }
            } else
                println("Failed to find object with 'Convert memories' option.")
            return
        }

        if (grabFragment && !Prayer.CHRONICLE_ATTRACTION.active) {
            println("Checking for chronicle fragment.")
            delay(1200, 500)
            if (interactClosestNPC("Chronicle fragment", "Capture", range = 8)) {
                delayWhile(gaussian(2500L, 800L)) { grabFragment }
                return
            }
            if (interactClosestNPC("Guthixian butterfly", "Catch", range = 8)) {
                delayWhile(gaussian(2500L, 800L)) { grabFragment }
                return
            }
            return
        }

        println("Finding spring.")
        val target = findEnriched() ?: findNonEnriched() ?: run {
            println("No springs found.")
            delay(500, 850)
            return
        }

        if (target.interact("Harvest")) {
            val wasEnriched = target.isEnriched()
            println("Harvesting ${if (wasEnriched) "enriched" else ""} spring.")
            if (!target.isActive())
                delayUntil(gaussian(9852L, 1000L)) { target.isActive() }
            else
                delay(3729, 1500)
            delayUntil(300000, 600) {
                timeSinceLastXpDrop > 5000L ||
                !target.isActive() ||
                inventory.isFull ||
                grabFragment ||
                (!wasEnriched && findEnriched() != null)
            }
        }
    }

    override fun onEvent(event: Event) {
        if (event !is Chat || event.messageType != MessageType.UNFILTERABLE) return
        if (event.message.contains("A chronicle escapes") || event.message.contains("A Guthixian Butterfly blossoms"))
            tryGrabFragmentUntil = System.currentTimeMillis() + random(10000, 20000)
        else if (event.message.contains("cannot capture another player") || event.message.contains("You capture the chronicle fragment"))
            tryGrabFragmentUntil = 0L
    }

    fun findEnriched() = findClosestNPC(maxRange = 7) { it.isEnriched() }
    fun findNonEnriched() = findClosestNPC(maxRange = 7) { Regex(".*\\s(wisp|spring)$").matches(it.name) && it.hasOption("Harvest") }
    fun NPC.isEnriched() = Regex("^Enriched.*(wisp|spring)$").matches(name) && hasOption("Harvest")
    fun NPC.isActive() = exists() && !headbars.isEmpty()

    override fun render() {
        ImGuiDsl.window("AIO Wisp Gatherer") {
            image(spriteTexture(SpriteIds.DIVINATION_HIRES), 32f, 32f)
            text("Runtime: ${formatElapsedTime(System.currentTimeMillis(), startTime)}")
            text("XP/hr: ${getFormattedXpPerHour(startingXp, getXp(Skill.DIVINATION), startTime)}")
            text("Energies/h: ${getUnitsPerHour(energies-startEnergies, startTime)}")
            xpProgressBar(Skill.DIVINATION)
        }
    }

}