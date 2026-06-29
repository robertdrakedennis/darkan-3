package org.darkan.world.interfaces

import org.darkan.core.Logger.logWarn
import org.darkan.world.entity.Player
import world.gregs.voidps.gameval.Gameval

/**
 * Named sub-interfaces the world frame can host.
 *
 * Each entry is a placement: a child interface ([child]) laid into a [slot] of a parent interface
 * ([parent], default `toplevel_v2`). Ids resolve lazily from gamevals so this can be referenced
 * before the cache-backed dictionaries are initialized.
 *
 * The existing world-entry HUD path keeps its capture-verified placement stream in
 * `GameHud.COMPONENT_MAP`; this enum gives later interface/button code a named index for the same
 * family of frame and overlay placements.
 */
enum class GameInterface(
    val child: String,
    val slot: Int,
    val parent: String = Top.TOPLEVEL_V2.gameval,
    val inFrame: Boolean = true,
    val onOpen: (Player.() -> Unit)? = null,
) {
    GAMEWORLD("gameworld", 31),

    STATS("stats_child", 300),
    INVENTORY("toplevel_v2_inventory", 103),
    WORN("toplevel_v2_worn", 114),
    PRAYER("toplevel_v2_prayer", 136),

    ABILITY_BOOK_MAGIC("toplevel_v2_window_ability_book_magic", 169),
    ABILITY_BOOK_MAGIC_ABILITY("toplevel_v2_window_ability_book_magic_ability", 180),
    ABILITY_BOOK_MAGIC_COMBAT("toplevel_v2_window_ability_book_magic_combat", 191),
    ABILITY_BOOK_MAGIC_TELEPORT("toplevel_v2_window_ability_book_magic_teleport", 202),
    ABILITY_BOOK_MAGIC_SKILLING("toplevel_v2_window_ability_book_magic_skilling", 213),
    ABILITY_BOOK_MELEE("toplevel_v2_window_ability_book_melee", 147),
    ABILITY_BOOK_DEFCON("toplevel_v2_window_ability_book_defcon", 257),
    ABILITY_BOOK_DEFENCE("toplevel_v2_window_ability_book_defence", 268),
    ABILITY_BOOK_CONSTITUTION("toplevel_v2_window_ability_book_constitution", 279),
    ABILITY_BOOK_RANGED("toplevel_v2_window_ability_book_ranged", 158),
    ABILITY_BOOK_NECROMANCY("toplevel_v2_window_ability_book_necromancy", 224),
    ABILITY_BOOK_NECROMANCY_ABILITIES("toplevel_v2_window_ability_book_necromancy_abilities", 235),
    ABILITY_BOOK_NECROMANCY_SPELLS("toplevel_v2_window_ability_book_necromancy_spells", 246),

    FRIENDS("friends2", 501),
    FRIENDS_CHAT("friendschat_child", 556),
    CLAN_CHAT("clan_chat", 512),
    EMOTES("emotes2", 409),

    MUSIC("music_v3_child", 311),
    NOTES("notes_child", 545),
    GROUP("group_child", 523),

    TELEMETRY("telemetry", 343),
    DROP_LOG("droplog", 354),
    QUEST_LIST("questlist_v4", 376),
    ACHIEVEMENT_TRACKER("cheevo_tracker", 387),
    ACHIEVEMENT_PATHS("cheevo_paths", 398),

    RIBBON("toplevel_v2_ribbon", 64),
    RIBBON_EXTRA("toplevel_v2_ribbon_extra", 691),
    MINIMAP("toplevel_v2_minimap", 95),
    COMPASS("toplevel_v2_compass", 96),

    EVENT_CRAFTING("event_crafting", 797),
    COMBAT_BAR("toplevel_v2_combat_bar", 70),
    ESCAPE_MENU("escape_menu", 805),

    CHAT_DEFAULT("chatdefault", 420),
    CHAT_DEFAULT_2("chatdefault2", 431),
    CHAT_DEFAULT_3("chatdefault3", 441),
    CHAT_DEFAULT_4("chatdefault4", 451),
    CHAT_DEFAULT_5("chatdefault5", 461),
    CHAT_DEFAULT_6("chatdefault6", 471),
    CHAT_DEFAULT_8("chatdefault8", 481),
    SPLIT_PM("split_pm", 648),

    GRAVESTONE_TIMER("gravestone_timer", 621),
    STATUS_ICONS("statusicons", 634),
    BUFF_BAR("buff_bar", 617),
    XP_POPUP("xp_popup", 668),
    TOPLEVEL_PARENT("toplevel_v2_parent", 715),
    DEBUFF_BAR("debuff_bar", 613),
    TARGETING("toplevel_v2_targeting", 814),
    STATUS_EFFECTS("toplevel_v2_status_effects", 43),
    RAGDOLL_STORE_TOOLTIP("toplevel_v2_ragdoll_store_tooltip", 911),

    ACC_CREATE("acc_create", 815, inFrame = false),
    GAME_MODE_SELECTION("game_mode_selection", 186, parent = "acc_create", inFrame = false),
    ACC_CREATE_NAME("acc_create_name", 147, parent = "acc_create", inFrame = false),
    MINIMENU("minimenu", 207, parent = "acc_create", inFrame = false);

    val childId: Int by lazy { Gameval.requireId(Gameval.INTERFACE, child) }

    private val parentId: Int by lazy { Gameval.requireId(Gameval.INTERFACE, parent) }

    val hash: Int get() = (parentId shl 16) or (slot and 0xFFFF)

    companion object {
        val FRAME: List<GameInterface> = entries.filter { it.inFrame }

        private val byHash: Map<Int, GameInterface> by lazy {
            val map = HashMap<Int, GameInterface>(entries.size)
            for (sub in entries) {
                val prev = map.put(sub.hash, sub)
                if (prev != null) logWarn("GameInterface: duplicate hash ${sub.hash} for $prev / $sub")
            }
            map
        }

        fun forHash(componentHash: Int): GameInterface? = byHash[componentHash]
    }
}
