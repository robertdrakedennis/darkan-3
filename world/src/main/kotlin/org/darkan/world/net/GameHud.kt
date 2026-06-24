package org.darkan.world.net

import org.darkan.core.model.IFEvents
import org.darkan.core.net.prot.IfSetEvents
import org.darkan.core.net.prot.IfSetPosition
import org.darkan.core.net.prot.IfSetTopLevelInterface
import org.darkan.core.net.prot.RunClientScript
import org.darkan.core.net.session.GameSession

/**
 * The real world-entry HUD open (root gameframe 1477), per `docs/protocol/world-entry-render-948.md`
 * §7. Built as a real component map + script/event system, NOT a captured byte replay.
 *
 * **Sequence (§2.3 / §7):**
 *   1. `op3  IF_SETTOPLEVELINTERFACE` — open root **1477** (the modern RS3 resizable gameframe).
 *   2. `op82 IF_SETPOSITION` ×56 — mount each child sub-interface into a 1477 component slot
 *      (parent hash `(1477 << 16) | slot`, child = sub-interface id). [COMPONENT_MAP] is the
 *      definitive §7.2 slot→child table.
 *   3. `op110 RUN_CLIENT_SCRIPT` — `16300` once + `671` once (gameframe master setup), then `8862`
 *      ×22 with the exact `[arg1, tab]` arg lists (§7.3) to build each HUD tab/panel.
 *   4. `op35 IF_SETEVENTS` — per-component event masks enabling interaction (§7.4).
 *
 * **NONE of this is render-gated** — the scene renders behind the HUD via op75. This is required for
 * an *interactive* HUD and so the world-entry CS2 (8862/16300/671) runs.
 *
 * **WIRED (docs/protocol/world-ingame-transition-948.md §8).** Called by
 * `WorldServer.sendInGameHud` for established accounts as the in-game transition: after the
 * zone/npc reset (op55/op1/op130) and BEFORE the final `op75 SetReadyFlag`. This is the HUD-root
 * swap that makes the client commit to in-game instead of polling op54 worldlist-fetch.
 */
object GameHud {

    /** The modern RS3 resizable gameframe root interface id (§7.1/§7.2). */
    const val ROOT_INTERFACE = 1477

    /** op82 layer byte (§7.2: wire byte 0x7F via writeByteSubtract; encoder takes the logical value). */
    private const val LAYER = 1

    /**
     * The definitive 1477 child component map (§7.2): each pair is `slot → childInterface`. The §7.2
     * table has **54** rows (18 rows × 3 columns); the doc PROSE says "56 placements" — a
     * doc-internal mismatch (table vs prose), table taken as authoritative. FLAGGED for the
     * ghidra-reverse-engineer agent to reconcile 54-vs-56. Modeled as a real (slot, child) component
     * map, not raw bytes — the encoder packs each into op82's parent hash `(1477 << 16) | slot` with
     * `componentId = child`.
     */
    val COMPONENT_MAP: List<HudComponent> = listOf(
        HudComponent(31, 1482), HudComponent(300, 1466), HudComponent(103, 1473),
        HudComponent(114, 1464), HudComponent(136, 1458), HudComponent(169, 1461),
        HudComponent(147, 1460), HudComponent(180, 1884), HudComponent(191, 1885),
        HudComponent(202, 1887), HudComponent(213, 1886), HudComponent(257, 1883),
        HudComponent(268, 1449), HudComponent(279, 1882), HudComponent(64, 1431),
        HudComponent(70, 1430), HudComponent(43, 994), HudComponent(158, 1452),
        HudComponent(224, 1219), HudComponent(235, 1220), HudComponent(246, 1221),
        HudComponent(311, 1416), HudComponent(343, 1588), HudComponent(354, 1678),
        HudComponent(376, 190), HudComponent(387, 1854), HudComponent(398, 1894),
        HudComponent(409, 590), HudComponent(420, 137), HudComponent(431, 1467),
        HudComponent(441, 1472), HudComponent(451, 1471), HudComponent(95, 1465),
        HudComponent(96, 1919), HudComponent(461, 1470), HudComponent(471, 464),
        HudComponent(481, 1529), HudComponent(501, 550), HudComponent(512, 1110),
        HudComponent(523, 1519), HudComponent(545, 1417), HudComponent(556, 1427),
        HudComponent(613, 291), HudComponent(617, 284), HudComponent(621, 1483),
        HudComponent(634, 745), HudComponent(668, 1213), HudComponent(691, 568),
        HudComponent(715, 1448), HudComponent(726, 1281), HudComponent(797, 653),
        HudComponent(805, 1433), HudComponent(814, 1488), HudComponent(911, 1847),
    )

    /** HUD-build script run once before the per-tab 8862 calls (§7.3): the gameframe master setup. */
    const val SCRIPT_GAMEFRAME_SETUP = 16300

    /** Second one-off HUD-build script (§7.3). */
    const val SCRIPT_GAMEFRAME_SETUP_2 = 671

    /** The per-tab HUD-build script (§7.3), run 22 times with `[arg1, tabIndex]`. */
    const val SCRIPT_HUD_TAB = 8862

    /**
     * The exact 22 `(arg1, tabIndex)` arg lists for script 8862 (§7.3). arg1 is a visibility/enable
     * flag (0 or 1); arg2 is the tab/component index. Order is the production order.
     */
    val HUD_TAB_ARGS: List<Pair<Int, Int>> = listOf(
        1 to 0, 1 to 2, 1 to 3, 1 to 4, 1 to 5, 1 to 9, 1 to 10, 1 to 11,
        0 to 12, 1 to 14, 1 to 15, 1 to 16, 1 to 27, 1 to 28, 1 to 29, 0 to 30,
        1 to 31, 1 to 32, 1 to 41, 0 to 45, 0 to 46, 0 to 1025,
    )

    /**
     * Per-component event masks (§7.4, op35 IF_SETEVENTS). Production sends ~871; none are
     * render-required, they enable interaction. The MINIMAL interactive set is the inventory (1473)
     * and core action components. We seed a small, safe baseline; the full set is staged.
     *
     * `settings` is the event bitmask; the exact event-bit semantics live in 948-research-B
     * IF_SETEVENTS and are not re-derived here. We use 0 (no events) as a placeholder so the
     * encoder/wire is exercised; populate real masks when the interactive HUD is wired.
     */
    val EVENT_COMPONENTS: List<IFEvents> = listOf(
        IFEvents(interfaceId = 1473, componentId = 0, fromSlot = 0, toSlot = 27, settings = 0),
    )

    /**
     * Open the full 1477 HUD on [session]. Emits op3 → op82×54 → op110(16300,671 + 8862×22) → op35.
     *
     * Sent by `WorldServer.sendInGameHud` as the in-game transition (after op55/op1/op130, before the
     * final op75 SetReadyFlag) per docs/protocol/world-ingame-transition-948.md §8. [rootInterface]
     * defaults to [ROOT_INTERFACE] (1477) — the established-account HUD root the production capture's
     * op3 decodes to; the component map / scripts below are all keyed to 1477.
     */
    suspend fun open(session: GameSession, rootInterface: Int = ROOT_INTERFACE) {
        // 1. op3 — open root 1477.
        session.send(IfSetTopLevelInterface(topLevelId = rootInterface))

        // 2. op82 — mount each child into a 1477 slot.
        for (c in COMPONENT_MAP) {
            session.send(
                IfSetPosition(
                    componentId = c.child,
                    layer = LAYER,
                    position = componentHash(ROOT_INTERFACE, c.slot),
                )
            )
        }

        // 3. op110 — gameframe master setup once, then per-tab HUD build.
        session.send(RunClientScript.of(SCRIPT_GAMEFRAME_SETUP, 0))
        session.send(RunClientScript.of(SCRIPT_GAMEFRAME_SETUP_2, 0))
        for ((arg1, tab) in HUD_TAB_ARGS) {
            session.send(RunClientScript.of(SCRIPT_HUD_TAB, arg1, tab))
        }

        // 4. op35 — per-component event masks (interaction).
        for (events in EVENT_COMPONENTS) {
            session.send(IfSetEvents(events))
        }
    }

    /** Parent component hash for op82 IF_SETPOSITION: `(interfaceId << 16) | slot`. */
    fun componentHash(interfaceId: Int, slot: Int): Int = (interfaceId shl 16) or (slot and 0xFFFF)
}

/** One 1477 HUD placement (§7.2): mount sub-interface [child] into 1477 component [slot]. */
data class HudComponent(val slot: Int, val child: Int)
