package org.darkan.world.net

import org.darkan.core.model.IFEvents
import org.darkan.core.net.prot.IfCloseSub
import org.darkan.core.net.prot.IfSet2DAngle
import org.darkan.core.net.prot.IfSetEvents
import org.darkan.core.net.prot.IfSetHide
import org.darkan.core.net.prot.IfSetPosition
import org.darkan.core.net.prot.IfSetText
import org.darkan.core.net.prot.IfSetTopLevelInterface
import org.darkan.core.net.prot.RunClientScript
import org.darkan.core.net.session.GameSession

/**
 * The real world-entry HUD open (root gameframe 1477), per `docs/protocol/world-entry-render-948.md`
 * §7. Built as a real component map + script/event system, NOT a captured byte replay.
 *
 * **Sequence (§2.3 / §7):**
 *   1. `op3  IF_SETTOPLEVELINTERFACE` — open root **1477** (the modern RS3 resizable gameframe).
     *   2. `op82 IF_SETPOSITION` ×55 — mount each child sub-interface into a 1477 component slot
 *      (parent hash `(1477 << 16) | slot`, child = sub-interface id). [COMPONENT_MAP] is the
 *      definitive §7.2 slot→child table.
     *   3. `op110 RUN_CLIENT_SCRIPT` — `16300` once + `671` once + `20611` once, then `8862`
     *      ×22 with the exact `[tab, enabled]` arg lists (§7.3) to build each HUD tab/panel.
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
     * The 1477 child placement sequence: each pair is `slot → childInterface`.
     * Modeled as real component placements, not raw bytes — the encoder packs each into op82's
     * parent hash `(1477 << 16) | slot` with `componentId = child`.
     */
    val COMPONENT_MAP: List<HudComponent> = listOf(
        HudComponent(31, 1482), HudComponent(300, 1466), HudComponent(103, 1473),
        HudComponent(114, 1464), HudComponent(136, 1458), HudComponent(169, 1461),
        HudComponent(180, 1884), HudComponent(191, 1885), HudComponent(202, 1887),
        HudComponent(213, 1886), HudComponent(147, 1460), HudComponent(257, 1883),
        HudComponent(268, 1449), HudComponent(279, 1882), HudComponent(158, 1452),
        HudComponent(224, 1219), HudComponent(235, 1220), HudComponent(246, 1221),
        HudComponent(501, 550), HudComponent(556, 1427), HudComponent(512, 1110),
        HudComponent(409, 590), HudComponent(311, 1416), HudComponent(545, 1417),
        HudComponent(523, 1519), HudComponent(343, 1588), HudComponent(354, 1678),
        HudComponent(376, 190), HudComponent(387, 1854), HudComponent(398, 1894),
        HudComponent(64, 1431), HudComponent(691, 568), HudComponent(95, 1465),
        HudComponent(96, 1919), HudComponent(797, 653), HudComponent(70, 1430),
        HudComponent(805, 1433), HudComponent(420, 137), HudComponent(431, 1467),
        HudComponent(441, 1472), HudComponent(451, 1471), HudComponent(461, 1470),
        HudComponent(471, 464), HudComponent(481, 1529), HudComponent(95, 1465),
        HudComponent(96, 1919), HudComponent(621, 1483), HudComponent(634, 745),
        HudComponent(617, 284), HudComponent(668, 1213), HudComponent(715, 1448),
        HudComponent(613, 291), HudComponent(814, 1488), HudComponent(43, 994),
        HudComponent(911, 1847),
    )

    /** HUD-build script run once before the per-tab 8862 calls (§7.3): the gameframe master setup. */
    const val SCRIPT_GAMEFRAME_SETUP = 16300

    /** Second one-off HUD-build script (§7.3). */
    const val SCRIPT_GAMEFRAME_SETUP_2 = 671

    /** The per-tab HUD-build script (§7.3), run 22 times with `[arg1, tabIndex]`. */
    const val SCRIPT_HUD_TAB = 8862

    /**
     * The exact 22 `(tabIndex, enabled)` arg lists for script 8862. Order is the production order.
     */
    val HUD_TAB_ARGS: List<Pair<Int, Int>> = listOf(
        0 to 1, 2 to 1, 3 to 1, 4 to 1, 5 to 1, 14 to 1, 15 to 1, 16 to 1,
        9 to 1, 10 to 1, 11 to 1, 12 to 0, 27 to 1, 45 to 0, 28 to 1, 29 to 1,
        30 to 0, 31 to 1, 32 to 1, 41 to 1, 46 to 0, 1025 to 0,
    )

    val POST_OPEN_SCRIPTS: List<RunClientScript> = listOf(
        RunClientScript.of(11145, 1067, 600, 0, 0, 96797466),
        RunClientScript.of(8420, -1, 96797468, 96797469, -1, "", 21259, 1007),
        RunClientScript.of(15997),
        RunClientScript.of(139, 96796699),
        RunClientScript.of(14150, 5),
        RunClientScript.of(8778),
        RunClientScript.of(20093, 40),
        RunClientScript.of(4704),
        RunClientScript.of(4308, 18, 0),
        RunClientScript.of(10623, 30522, 0),
        RunClientScript.of(10623, 30758, 0),
        RunClientScript.of(10623, 30759, 0),
        RunClientScript.of(10623, 30821, 0),
        RunClientScript.of(10623, 30828, 0),
        RunClientScript.of(10623, 30964, 0),
        RunClientScript.of(10623, 31386, 0),
        RunClientScript.of(10623, 31562, 0),
        RunClientScript.of(10623, 31918, 0),
        RunClientScript.of(10623, 48878, 0),
        RunClientScript.of(10623, 52080, 0),
        RunClientScript.of(10623, 45167, 0),
        RunClientScript.of(3373, 1014),
        RunClientScript.of(5559, 0L),
        RunClientScript.of(5559, 0L),
        RunClientScript.of(5557, 1),
        RunClientScript.of(10623, 39392, 0),
        RunClientScript.of(9945),
        RunClientScript.of(3543, 1),
        RunClientScript.of(3957),
        RunClientScript.of(18950, 1),
        RunClientScript.of(18952, 0),
        RunClientScript.of(18951, 0, 0, 0, 0, 3, 4),
        RunClientScript.of(18951, 1, 0, 0, 0, 3, 4),
        RunClientScript.of(18951, 2, 0, 0, 0, 3, 4),
        RunClientScript.of(18951, 3, 255, 211, 0, 0, 4),
        RunClientScript.of(18951, 4, 255, 13, 22, 0, 4),
        RunClientScript.of(18951, 5, 26, 235, 255, 0, 4),
        RunClientScript.of(18951, 6, 238, 100, 0, 0, 4),
        RunClientScript.of(18951, 7, 238, 100, 0, 0, 4),
        RunClientScript.of(20392),
        RunClientScript.of(10623, 6196, 0),
        RunClientScript.of(9542),
        RunClientScript.of(10623, 35804, 0),
        RunClientScript.of(10623, 35826, 0),
        RunClientScript.of(10623, 29170, 0),
        RunClientScript.of(10623, 29174, 0),
        RunClientScript.of(10623, 29171, 0),
        RunClientScript.of(10623, 29172, 0),
        RunClientScript.of(10623, 29173, 0),
        RunClientScript.of(10623, 36800, 0),
        RunClientScript.of(6504, 0, 0, 0, 0, 8883),
        RunClientScript.of(10623, 45166, 0),
        RunClientScript.of(4308, 18, 0),
        RunClientScript.of(18954, 4, 0),
    )

    /**
     * Inventory interaction masks decoded from the production world-entry recording.
     */
    val EVENT_COMPONENTS: List<IFEvents> = listOf(
        IFEvents(interfaceId = 1473, componentId = 5, fromSlot = -1, toSlot = -1, settings = 0x00002000),
        IFEvents(interfaceId = 1473, componentId = 5, fromSlot = 0, toSlot = 27, settings = 0x8E7DEB00.toInt()),
        IFEvents(interfaceId = 1473, componentId = 20, fromSlot = 0, toSlot = 17, settings = 0x8E050000.toInt()),
        IFEvents(interfaceId = 1473, componentId = 9, fromSlot = 0, toSlot = 1, settings = 0xFE072000.toInt()),
        IFEvents(interfaceId = 1473, componentId = 9, fromSlot = 4096, toSlot = 4097, settings = 0xFE072000.toInt()),
        IFEvents(interfaceId = 1473, componentId = 9, fromSlot = 4352, toSlot = 4353, settings = 0xFE072000.toInt()),
        IFEvents(interfaceId = 1473, componentId = 9, fromSlot = 4608, toSlot = 4609, settings = 0xFE072000.toInt()),
        IFEvents(interfaceId = 1473, componentId = 9, fromSlot = 4864, toSlot = 4865, settings = 0xFE072000.toInt()),
        IFEvents(interfaceId = 1473, componentId = 9, fromSlot = 5120, toSlot = 5121, settings = 0xFE072000.toInt()),
        IFEvents(interfaceId = 1473, componentId = 9, fromSlot = 5376, toSlot = 5377, settings = 0xFE072000.toInt()),
    )

    /**
     * Open the full 1477 HUD on [session]. Emits op110 setup → op3 → op82×55 → op110(8862×22) → op35.
     *
     * Sent by `WorldServer.sendInGameHud` as the in-game transition (after op55/op1/op130, before the
     * final op75 SetReadyFlag) per docs/protocol/world-ingame-transition-948.md §8. [rootInterface]
     * defaults to [ROOT_INTERFACE] (1477) — the established-account HUD root the production capture's
     * op3 decodes to; the component map / scripts below are all keyed to 1477.
     */
    suspend fun open(session: GameSession, rootInterface: Int = ROOT_INTERFACE) {
        session.send(RunClientScript.of(SCRIPT_GAMEFRAME_SETUP, 0))
        session.send(RunClientScript.of(SCRIPT_GAMEFRAME_SETUP_2, 0))
        session.send(RunClientScript.of(20611))

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

        // 3. op110 — per-tab HUD build.
        for ((tab, enabled) in HUD_TAB_ARGS) {
            session.send(RunClientScript.of(SCRIPT_HUD_TAB, tab, enabled))
        }
        for (script in POST_OPEN_SCRIPTS) {
            session.send(script)
        }
        sendPropertyBootstrap(session)

        // 4. op35 — per-component event masks (interaction).
        for (events in EVENT_COMPONENTS) {
            session.send(IfSetEvents(events))
        }
    }

    /** Parent component hash for op82 IF_SETPOSITION: `(interfaceId << 16) | slot`. */
    fun componentHash(interfaceId: Int, slot: Int): Int = (interfaceId shl 16) or (slot and 0xFFFF)

    private suspend fun sendPropertyBootstrap(session: GameSession) {
        session.send(IfSetText(componentHash(1416, 6), "Adventure"))
        session.send(IfSetText(componentHash(1417, 5), "Loading notes<br>Please wait..."))
        session.send(IfSetHide(componentHash(1417, 5), hide = false))
        session.send(IfSetHide(componentHash(653, 71), hide = true))
        session.send(IfSetHide(componentHash(653, 0), hide = false))
        session.send(IfCloseSub(componentHash(ROOT_INTERFACE, 95)))
        session.send(IfSetHide(componentHash(ROOT_INTERFACE, 638), hide = false))
        session.send(IfSetText(componentHash(187, 7), ""))
        session.send(IfSetText(componentHash(1416, 6), ""))
        session.send(IfSetHide(componentHash(ROOT_INTERFACE, 600), hide = true))
        session.send(IfSetHide(componentHash(745, 7), hide = true))
        session.send(IfSet2DAngle(angle = 2730, componentHash = componentHash(1920, 7)))
        session.send(IfSetText(componentHash(187, 7), "Harmony"))
        session.send(IfSetText(componentHash(1416, 6), "Harmony"))
        session.send(IfSetHide(componentHash(1253, 207), hide = true))
        session.send(IfSetHide(componentHash(1253, 207), hide = true))
    }
}

/** One 1477 HUD placement (§7.2): mount sub-interface [child] into 1477 component [slot]. */
data class HudComponent(val slot: Int, val child: Int)
