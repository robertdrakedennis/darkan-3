package com.undercut.script.impl.qb.Quest

import world.gregs.voidps.type.Tile
import world.gregs.voidps.path.PathFinder
import com.undercut.pathfinder.WorldCollision
import world.gregs.voidps.collision.CollisionStrategies
import world.gregs.voidps.path.toTiles
import com.undercut.script.*
import com.undercut.script.api.Lodestone
import com.undercut.script.api.localPlayer
import com.undercut.script.api.useLodestone
import com.undercut.script.api.walkTo
import com.undercut.script.event.Event
import com.undercut.script.event.impl.Chat
import com.undercut.script.impl.qb.Quest.DebugScript.Companion.running
import com.undercut.util.random
import com.google.gson.JsonObject
import com.undercut.script.scheduler.SchedulableScript
import com.undercut.script.scheduler.SchedulerConfigurable

@ScriptDescription(
    name = "Quest Helper",
    version = "1.1",
    author = "Billythebob, Query",
    description = "Comprehensive quest helper with dialog management and automated quest execution."
)
class DebugScript : StateMachineScript<DebugScript>(), ConfigurableScript, SchedulableScript, SchedulerConfigurable {

    // Config items for the GUI
    val selectedQuest = EnumConfigItem(
        name = "Quest",
        description = "Select which quest to run",
        enumValues = Quest.entries.toTypedArray(),
        initialValue = Quest.TEST_DONTSELECT
    )

    val enableDebug = BooleanConfigItem(
        name = "Debug Mode",
        description = "Enable debug output",
        initialValue = true
    )

    val autoCloseDialogs = BooleanConfigItem(
        name = "Auto Close Dialogs",
        description = "Automatically close dialogs that match known patterns",
        initialValue = true
    )

    // This getter ensures the config values are used
    val currentQuest: Quest
        get() = selectedQuest.value

    val debug: Boolean
        get() = enableDebug.value

    val autoClose: Boolean
        get() = autoCloseDialogs.value

    // Statistics tracking
    var questsCompleted = 0
    var dialogsProcessed = 0

    // Static access for other classes
    companion object {
        var running = false
        var message = ""
        lateinit var instance: DebugScript
    }

    // Initialize the instance when the script is created
    init {
        instance = this
    }

    suspend fun moveTo(location: Tile): Boolean {


        val route = PathFinder(
            flags = WorldCollision.allFlags,
            searchMapSize = 1024,
            useRouteBlockerFlags = true,
            moveNear = false
        ).findPath(
            localPlayer.tile.x.toInt(),
            localPlayer.tile.y.toInt(),
            location.x.toInt(),
            location.y.toInt(),
            localPlayer.tile.plane.toInt(),
            collision = CollisionStrategies.NORMAL,
            srcSize = 2,
            destWidth = 1,
            destHeight = 1
        )

        if (route.failed) {

            useLodestone(Lodestone.AL_KHARID)
            delay(600, 1800)

            return false
        }

        route.toTiles().forEach {
            if (it.getDistance(localPlayer.tile) > 7) {

                walkTo(it, true)
                delayUntil { it.getDistance(localPlayer.tile) < 4 }

            }
        }
        return true
    }

    override fun getStartState() = QuestMainState()

    override fun onEvent(event: Event) {
        super.onEvent(event)
        if (event is Chat && debug) {
            println(event.message)

//            if (currentQuest == Quest.VIOLET_IS_BLUE_TOO) {
//                println("TRACKING CHAT FOR VIOLET IS BLUE TOO")
//                println(event.message)
//
//                if (event.message.contains("Objective: Find an area high enough in the town to launch a snow impling from to decorate the tree.")) {
//                    println("Found go uphill objective")
//                }
//                return
//            }

            when {
                event.message.contains("Delivered 0/5 presents to citizens of Gielinor") -> {
                    message = event.message
                }

                event.message.contains("Delivered 1/5 presents to citizens of Gielinor") -> {
                    message = event.message
                }

                event.message.contains("Delivered 2/5 presents to citizens of Gielinor") -> {
                    message = event.message
                }

                event.message.contains("Delivered 3/5 presents to citizens of Gielinor") -> {
                    message = event.message
                }

                event.message.contains("Delivered 4/5 presents to citizens of Gielinor") -> {
                    message = event.message
                }
            }
        }
    }


    /**
     * Apply and validate a per-entry schedule configuration. Implementations
     * should not perform any long-running work here and must be side-effect
     * safe beyond updating in-memory configuration on the script instance.
     */
    override fun applyScheduleConfiguration(config: JsonObject): SchedulerConfigurable.Validation {
        // Delegate to shared mapper which:
        // - Maps booleans/ints/strings
        // - Maps Options by toString()
        // - Maps Enum by name (case-insensitive)
        // - Aggregates non-fatal warnings into Validation.message
        val validation = ScriptConfigJson.applyTo(this, config)
        // Notify script about updated config (fast, side-effect safe)
        try {
            onConfigUpdated()
        } catch (_: Throwable) {
        }
        return validation
    }

    fun onConfigUpdated() {
        println("Config updated")
        this::class.java.declaredFields.filter { ConfigItem::class.java.isAssignableFrom(it.type) }.forEach { field ->
            field.isAccessible = true
            val configItem = field.get(this) as? ConfigItem<*>
            val name = configItem?.name
            val value = configItem?.value
            println("$name: $value")
        }
    }


    fun initialize() {
        instance = this

    }

    enum class Quest(val questId: Int) {
        F2P_LODESTONES(9999999),
        P2P_LODESTONES(9999999),
        ARCH_TUTORIAL(999999),
        ANACHRONIA_TUT(99999999),
        NEW_FOUNDATION(489),


        //        DEATH_PLATEAU(140),
//        DRUIDIC_RITUAL(111),
//        LET_THEM_EAT_PIE(350),
//        WOLF_WHISTLE(324),
        VIOLET_IS_BLUE(400),
        VIOLET_IS_BLUE_TOO(453),
//        BLOOD_PACT(335),
//        RESTLESS_GHOST(27),
//        COOKS_ASSISTANT(257),
//        MYTHS_OF_THE_WHITE_LANDS(74),
//        ERNEST_THE_CHICKEN(15),
//        SWEPT_AWAY(20),
//        NECROMANCY_INTRO(493),
//        SOUL_SEARCHING(513),
//        IMP_CATCHER(72),
//        RUNE_MYSTERIES(358),
//        TROLL_STRONGHOLD(85),
//        BUYERS_AND_SELLERS(336),
//        PRIEST_IN_PERIL(276),
//        WHAT_LIES_BELOW(144),
//        ICTHLARIN_LITTLE_HELPER(287),
//        THE_KNIGHT_SWORD(261),
//        TEMPLE_OF_IKOV(126),
//        MISSING_MY_MUMMY(77),
//        SHIELD_OF_ARRAV(63),
//        GOBLIN_DIPLOMACY(137),
//        FAMILY_CREST(116),
//        TOMES_OF_WARLOCK(497),
//        STOLEN_HEARTS(355),
//        NATURE_SPIRIT(133),
//        IN_SEARCH_OF_THE_MYREQUE(283),
//        IN_AID_OF_THE_MYREQUE(21),
//        THE_DARKNESS_OF_HALLOWVALE(311),
//        LEGACY_OF_SEERGAZE(327),
//        BRANCHES_OF_DARKMEYER(347),
//        THE_DIG_SITE(273),
//        THE_GOLEM(286),
//        RUNE_MYTHOS(494),
//        GHOSTS_AHOY(82),
//        VESSEL_HARINGER(495),
//        SPIRIT_WAR(496),
//        DIAMOND_ROUGH(356),
//        JACK_OF_SPADES(390),
//        DAUGHTER_OF_CHAOS(483),
//        KILI_ROW(500),

        //        NEW_FOUNDATION(489),
//        KILI_KNOWLEDGE_I(99999),
//        KILI_KNOWLEDGE_II(99999),
//        KILI_KNOWLEDGE_III(99999),
//        KILI_KNOWLEDGE_IV(99999),
//        MOGRE_ACTIVITY(99999),
//        WATERFALL(93),
//        ENTER_THE_ABYSS(3149),
//        WHATS_MINE_IS_YOURS(357),
//        GERTRUDE_CAT(138),
//        CHRISTMAS_REUNION(516),
//        ITS_SNOW_BOTHER(508),
//        DEAD_AND_BURIED(492),
//        ANCIENT_AWAKENING(502),
//        BATTLE_OF_FORINTHRY(507),
//        REQUIEM_FOR_A_DRAGON(511),
//        MURDER_ON_THE_BORDER(490),
//        UNWELCOME_GUESTS(491),
//        IMPRESSING_THE_LOCALS(387),
//        MAKING_HISTORY(124),
//        MEETING_HISTORY(142),
//        ODE_OF_THE_DEVOURER(514),
        TEST_DONTSELECT(135);

        companion object {
            fun getByQuestId(questId: Int): Quest? {
                return entries.find { it.questId == questId }
            }
        }
    }
}

class QuestMainState : State<DebugScript>() {
    override suspend fun DebugScript.checkNext(): State<DebugScript>? {

        println("Current quest: ${currentQuest.name}")

        
        // Handle dialog processing
        if (QuestDialogs.isDialogOpen()) {
            return QuestDialogState()
        }

        if (currentQuest == DebugScript.Quest.F2P_LODESTONES) {
            println("F2P_LODESTONES")
            return F2PLoadstone()
        }

        if (currentQuest == DebugScript.Quest.P2P_LODESTONES) {
            println("P2P_LODESTONES")
            return P2PLodestone()
        }

        if (currentQuest == DebugScript.Quest.ANACHRONIA_TUT) {
            println("ANACHRONIA_TUT")
            return AnachroniaTut()
        }

        if (currentQuest == DebugScript.Quest.ARCH_TUTORIAL) {
            println("ARCH_TUTORIAL")
            return ArchTut()
        }

        if (currentQuest == DebugScript.Quest.NEW_FOUNDATION) {
            println("NEW_FOUNDATION")
            return NewFoundation()
        }

        if (currentQuest == DebugScript.Quest.VIOLET_IS_BLUE) {
            println("VIOLET_IS_BLUE")
            return VioletIsBlue()
        }
        if(currentQuest == DebugScript.Quest.VIOLET_IS_BLUE_TOO) {
            println("VIOLET_IS_BLUE_TOO")
            return VioletIsBlueToo()
        }

        // Return to main state for reevaluation after a period of time
        return null
    }

    override suspend fun DebugScript.stateLoop() {
        if (debug) {
            println("Quest Helper State: Main controller")
            println("Current quest: ${currentQuest.name}")
            println("Running: $running")
        }

        // Auto-start quest if a valid quest is selected but not running
        if (!running && currentQuest != DebugScript.Quest.TEST_DONTSELECT) {
            println("Starting quest: ${currentQuest.name}")
            running = true
        }

        if (!running) {
            delay(random(1000, 2000))
            return
        }

        delay(random(500, 1000))
    }
}

class QuestDialogState : State<DebugScript>() {
    override suspend fun DebugScript.checkNext(): State<DebugScript>? {
        // Return to main state when dialog is closed
        if (!QuestDialogs.isDialogOpen()) {
            return QuestMainState()
        }
        return null
    }

    override suspend fun DebugScript.stateLoop() {
        if (debug) {
            println("Quest Helper State: Dialog handling")
        }

        // Process dialog using the Dialogs class
        QuestDialogs.pressDialog()
        dialogsProcessed++

        delay(random(400, 600))
    }
}
