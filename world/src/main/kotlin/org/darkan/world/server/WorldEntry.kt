package org.darkan.world.server

import org.darkan.core.EnvVars
import org.darkan.core.Logger.logInfo
import org.darkan.core.Logger.logTrace
import org.darkan.core.Logger.logWarn
import org.darkan.core.net.prot.*
import org.darkan.core.net.session.GameSession
import org.darkan.world.entity.Player
import org.darkan.world.entity.Rev948FirstLightVarpDefaults
import org.darkan.world.net.GameHud
import org.darkan.world.net.NpcInfoEncoder
import org.darkan.world.net.Op81GpiPrefix
import org.darkan.world.net.PlayerInfoEncoder
import org.darkan.world.net.SceneMapCacheDiagnostics
import org.darkan.world.net.SceneMapRegionPlanner
import org.darkan.world.net.ZoneStreamer
import world.gregs.voidps.cache.Cache
import java.security.SecureRandom
import java.util.Base64

/**
 * The world-entry scene/UI build sequence — everything the client needs, in order, to leave the
 * login state machine and stand in the world.
 *
 * SINGLE RESPONSIBILITY: emit the post-login-response world-entry burst (op81 scene rebuild + UI
 * baseline + GPI/NPC sync + op78 zone stream + inventories + HUD transition + the first-light tail)
 * for one [Player]. It owns NOTHING about the login handshake, the auth proof, the session loop, or
 * the player-pool lifecycle — those stay in [WorldServer.initWorldLogin], which calls [enter] once,
 * after the pre-ISAAC world-login response has been written and flushed.
 *
 * This was extracted verbatim from the inline `initWorldLogin` send-script (NETWORKING_AUDIT.md
 * §4.3, Phase 1.3) so it can be re-driven on teleport / respawn / re-entry without copy-pasting the
 * block. The emitted ServerProt sequence — opcodes, order, and bytes — is BEHAVIOUR-IDENTICAL to
 * that inline block; do not add, remove, reorder, or re-field any send here.
 *
 * Several pieces are FROZEN single-player "first light" dev-scaffolding — captured from one Lumbridge
 * recording, not derived from world state — gated behind [EnvVars.firstLightScaffolding] (default
 * true, so current behaviour is preserved). They are individually marked below and are slated for
 * replacement by real inventory / stat / zone-spawn services in a later phase.
 */
object WorldEntry {

    /**
     * Emit the full world-entry burst for [player] over [session], then flush.
     *
     * Precondition: the pre-ISAAC world-login response (Parts A+B+C) has already been written and
     * flushed by the caller; [player] is allocated, its viewport reset after the GPI prefix, and its
     * spawn appearance cached. This is the EXACT inline sequence from `initWorldLogin` (Step 14
     * onward), in the EXACT order — the order is the wire contract.
     */
    suspend fun enter(session: GameSession, player: Player) {
        // Step 14: Send the world-init burst (op81 scene build + UI ops + op5 + varp
        // baseline). The spawn tile is the SINGLE source of truth: it drives the op81 coord-header centre
        // zone here AND the op22 GPI local 30-bit tile below (PlayerInfoEncoder reads
        // player.tile). They are now coherent — the 400-vs-404 split + captured 404/404
        // prefix that quit the client (docs/protocol/world-bootstrap-948.md §4) is gone.
        sendWorldInitPackets(session, player)

        session.send(DestroyZoneData())
        session.send(SetNpcOp())
        session.send(PlayerInfoEncoder.buildWorldEntrySync(player))
        session.send(CamUpdate.firstLight())
        session.send(UpdateIgnoreListRaw())

        // Step 15-zones: stream the op78 scene (13×13 zones × planes 0–3) that POPULATES the
        // client's scene graph and so advances the scene-build phase (ClientSceneManager+0x1c,
        // client+0x19578) — the foundational world-entry render gate. Without it the graph
        // stays empty, the phase never leaves 0 ("Running Auto Configuration"), and the client
        // black-screens while emitting zero C2S (latest prod streams 606 op78 before HUD).
        // MUST be
        // after op55 DestroyZoneData (zone reset) and before the op3 HUD commit + op75 (below).
        ZoneStreamer.streamScene(session, player.viewport)

        // buildWorldEntrySync clears firstTick and marks appearance delivered.
        // FROZEN DEV-SCAFFOLDING (default-on): captured single-player inventory/container block.
        if (EnvVars.firstLightScaffolding) sendInitialInventories(session)

        // Step 15b: THE IN-GAME TRANSITION (§8 task #1+#2) — swap the client's top-level
        // interface from the lobby/worldlist UI to the in-game HUD, then build the HUD, then
        // (Step 15c) flip the render-ready gate LAST. Without this the client stays on the
        // lobby interface (polls op54 worldlist-fetch) and never commits to in-game.
        sendInGameHud(session)

        session.send(SceneFlag(0))
        sendFirstLightTail(session, player)
        // op80 = SETFILTER_PRIVATE (private-chat filter = 1/Friends), NOT run energy.
        // Run energy is delivered via op13 (UpdateRunenergy) in sendFirstLightTail above.
        // (Was UpdateRunenergy(1) on op80, which actually set the chat filter — §10.4.)
        session.send(SetFilterPrivate(1))
        session.send(SetReadyFlag())
        session.flush()
    }

    private suspend fun sendWorldInitPackets(session: GameSession, player: Player) {
        sendWorldLoginCore(session, player)
        session.flush()
    }

    private suspend fun sendInGameHud(session: GameSession) {
        GameHud.open(session, rootInterface = GAME_HUD_INTERFACE)
    }

    /** Core world login packets before the 1477 HUD transition. */
    private suspend fun sendWorldLoginCore(session: GameSession, player: Player) {
        // op81 REBUILD_NORMAL_SIMPLE — **Shape B**: body = [5119-byte GPI prefix][18-byte coord
        // header] (docs/protocol/world-bootstrap-948.md §"⚠️ CORRECTION (2026-06-23)").
        //
        // On world entry the client sets worldState+0x49=1, so op81's handler UNCONDITIONALLY runs
        // the gBit-based GPI-prefix parser (NO bounds check) BEFORE reading the coord header. The
        // parser consumes 30 + 2046×20 = 40950 bits = 5119 bytes, then the handler reads the header
        // at the advanced cursor (magic 0x85 at body offset 5122). Shipping the bare 18-byte header
        // (Shape A) makes the parser over-read 5101 bytes of heap, place the player at a garbage
        // tile, and read the header out-of-bounds → magic ≠ 0x85 → op81 ABORTS before the BuildArea
        // alloc and before ProcessCameraReset → black screen. So the prefix is mandatory; we
        // generate it from local state via [Op81GpiPrefix].
        //
        // The spawn tile is the SINGLE source of truth for all three coordinate facets that MUST
        // agree (§4 coherence): (a) the op81 centre zone (header +4 X / +1,+2 Z), (b) the op81
        // build-area corners (packedCoordA/B), and (c) the GPI prefix's local 30-bit tile. The
        // centre zone and prefix tile come from `player.tile`; the first-light build-area uses the
        // larger asymmetric map-square grid observed in production rev948 so the scene manager
        // allocates the same map window before the op78 stream arrives.
        val spawn = player.tile
        val buildArea = player.viewport.loadFirstLightBuildArea(spawn)
        val centreZone = spawn.zone                      // render-scene centre (positioned inside the grid)
        val sceneRootId = Cache.worldAreaTypeAt(spawn.x, spawn.y) ?: run {
            logWarn(
                "No WorldAreaType covers spawn (${spawn.x},${spawn.y}) for ${player.account.username}; " +
                    "using WORLD_SCENE_ROOT_ID=${EnvVars.worldSceneRootId}"
            )
            EnvVars.worldSceneRootId
        }
        // GPI prefix: local player's 30-bit tile == this same spawn tile; the skipped slot is the
        // player's allocated index (== WorldLoginDetails.playerIndex == viewport.highResIndices[0]).
        val gpiPrefix = Op81GpiPrefix.build(spawnTile = spawn, localPlayerIndex = player.index)
        session.send(
            RebuildNormalSimple(
                // CENTRE-ZONE AXIS ORDER — verified BYTE-FOR-BYTE against the production op81 capture
                // (session-20260627-044937-74364): prod sends wire+4 = spawn X-zone (east-west, 403) and
                // wire+1 = spawn Z-zone (north-south, 402). The codec maps zoneX→wire+4 and zoneZ→wire+1, so
                // zoneX = centreZone.x and zoneZ = centreZone.y reproduce prod exactly.
                //
                // The earlier "axis-transposition fix" (zoneX=centreZone.y / zoneZ=centreZone.x) was WRONG:
                // it transposed our centre zone vs prod (we shipped +4=402 / +1=403), centring the render
                // scene one zone off-diagonal from the avatar. Per the binary trace, that mismatch leaves the
                // avatar's map-square scene-resource group never-ready, so the per-tick async-load gate in
                // ProcessPendingAppearance @0x10002ba20 (→ SceneLoadRegistry::IsResourceGroupReady @0x1003de570)
                // never fires ComposeAppearanceModel and render_model (avatar+0xC58) stays null (INVISIBLE
                // avatar), while the camera anchors off the avatar (sustained DRIFT). The build-area corners
                // (packedCoordA/B) already matched prod exactly; ONLY this centre-zone order was off.
                zoneX = centreZone.x,                    // +4 = centreZoneX (east-west) — matches prod (403)
                zoneZ = centreZone.y,                    // +1/+2 = centreZoneZ (north-south) — matches prod (402)
                packedCoordA = buildArea.packedCoordA,   // +10 SW corner {minRegionX, minRegionZ}
                packedCoordB = buildArea.packedCoordB,   // +14 NE corner {maxRegionX, maxRegionZ}
                npcInfoCoordBitWidth = NPC_INFO_COORD_BIT_WIDTH,
                sceneRootId = sceneRootId,
                rebuildPrefix = gpiPrefix,               // Shape B: 5119-byte GPI init; body = 5119 + 18 = 5137
            )
        )
        logTrace("World build area for ${player.account.username}: $buildArea centreZone=${centreZone.x},${centreZone.y} gpiPrefix=${gpiPrefix.size}B slot=${player.index}")
        val sceneRegions = SceneMapRegionPlanner.regionsForScene(centreZone.x, centreZone.y)
        logInfo(
            "World scene map groups for ${player.account.username}: " +
                SceneMapCacheDiagnostics.describe(Cache.get(), sceneRegions)
        )

        val tokenBytes = ByteArray(32).also { loginRandom.nextBytes(it) }
        val token = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes)
        session.send(HashedWorldToken(token))
        session.send(MinimapState(0, 0))
        session.send(JcoinsUpdate(INITIAL_DISPLAY_INT))
        session.send(MinimapFlagA(1))
        session.send(MinimapFlagB(1))
        session.send(SetPlayerOp(2, "Follow", priority = true))
        session.send(SetPlayerOp(3, "Trade with", priority = true))
        session.send(SetPlayerOp(5, "Req Assist", priority = true))
        session.send(SetPlayerOp(6, null, priority = true))
        session.send(SetPlayerOp(7, "Examine", priority = true))
        session.send(MidiSong(INITIAL_MIDI_SONG))
        session.send(SetPlayerOp(4, "Duel", priority = true))
        session.send(ResetClientVarcache())

        sendVarpBaseline(session, player)
    }

    private suspend fun sendVarpBaseline(session: GameSession, player: Player) {
        player.varps.seedDefaults(Rev948FirstLightVarpDefaults)
        val emitted = player.varps.flush(session)
        logTrace("World varp baseline for ${player.account.username}: ${emitted.size} packet(s)")
    }

    /**
     * FROZEN DEV-SCAFFOLDING — captured single-player stat block, NOT derived from world state.
     * Hardcodes 28 skills at level 1 plus a single non-default (slot 3 = 1154xp / level 10). Replayed
     * from one character recording. Gated by [EnvVars.firstLightScaffolding].
     *
     * TODO(Phase 1.3+): replace with a real per-player stat service (`Stats`/`Skills` on [Player]).
     */
    private suspend fun sendInitialStats(session: GameSession) {
        for (i in 0..28) {
            if (i == 3) session.send(UpdateStat(i, 1154, 10))
            else session.send(UpdateStat(i, 0, 1))
        }
    }

    /**
     * FROZEN DEV-SCAFFOLDING — the fresh-spawn worn loadout, keyed by Body/Wearpos-def slot index
     * (WEAPON = 3). Drives the worn-equipment CONTAINER (94 / 0x005E) shown in the equipment UI.
     * Currently just a bronze dagger in the weapon slot (matches the prod `op85 UpdateInvFull`
     * container 94 spawn loadout). Captured, NOT derived from world state — replayed via
     * [wornContainerEntries] inside the [EnvVars.firstLightScaffolding]-gated [sendInitialInventories].
     *
     * TODO(Phase 1.3+): replace with a real worn-equipment container backed by per-player state.
     *
     * **NOT rendered on the avatar at spawn.** A freshly-created character renders with default
     * identitykits only (empty equipment) — exactly like RS3 character creation. Forcing an equipped
     * item onto the avatar model at spawn is what CRASHED the client: per the binary trace
     * (`DecodeAppearance @0x100031480` + `DecodeAppearanceEquipment @0x100032450`) our appearance BYTE
     * block decoded PERFECTLY — the client cursor landed exactly at the emitted length, NOT an
     * over-read — but resolving an equipped weapon's worn/wield model for the avatar pose null-derefs
     * in the model-attach path (`(**(*def + 0x40))(def, itemId, 0)` + avatar assembly
     * `FUN_10039dee0`/`FUN_100033260`). Equipped-item avatar rendering is deferred until the
     * worn-model attach is validated (a feature separate from this spawn-avatar task). The container
     * UI is unaffected. (The spawn-avatar appearance itself is built in
     * `WorldServer.applySpawnEquipment`, which DELIBERATELY does not push this map onto the avatar.)
     */
    private val spawnWornEquipment: Map<Int, Int> = mapOf(
        Player.EQUIP_SLOT_WEAPON to 1205, // bronze_dagger (UI container only — see above)
    )

    /** Number of worn-container slots (94 / 0x005E) the spawn UpdateInvFull writes. */
    private const val WORN_CONTAINER_SLOTS = 4

    /** Build the worn-equipment container (94) entries from [spawnWornEquipment]. */
    private fun wornContainerEntries(): List<InventoryEntry> =
        (0 until WORN_CONTAINER_SLOTS).map { slot ->
            InventoryEntry(itemId = spawnWornEquipment[slot] ?: -1, quantity = if (spawnWornEquipment.containsKey(slot)) 1 else 0)
        }

    /**
     * FROZEN DEV-SCAFFOLDING — captured single-player inventory/container block, NOT derived from
     * world state. Hardcodes the magic item ids (worn container 94, coins, starter widgets, …) replayed
     * from one Lumbridge recording. Gated by [EnvVars.firstLightScaffolding].
     *
     * TODO(Phase 1.3+): replace with real per-player [Player] inventory/container state.
     */
    private suspend fun sendInitialInventories(session: GameSession) {
        session.send(UpdateInvFull(inventoryId = 0x0313))
        session.send(
            UpdateInvFull(
                inventoryId = 0x031B,
                entries = listOf(InventoryEntry(itemId = 0xCD4B, quantity = 1000)),
            )
        )
        session.send(UpdateInvFull(inventoryId = 0x037B))
        session.send(
            UpdateInvFull(
                inventoryId = 0x005D,
                flags = 0x2,
                entries = listOf(InventoryEntry(itemId = 0x013B, quantity = 1)),
            )
        )
        session.send(
            UpdateInvFull(
                inventoryId = 0x005E, // 94 / worn
                flags = 0x2,
                entries = wornContainerEntries(),
            )
        )
        session.send(UpdateInvFull(inventoryId = 0x026F))
        session.send(
            UpdateInvFull(
                inventoryId = 0x037F,
                entries = listOf(
                    InventoryEntry(itemId = 0x03C0, quantity = 0),
                    InventoryEntry(itemId = 0x224A, quantity = 0),
                    InventoryEntry(itemId = 0x224C, quantity = 0),
                    InventoryEntry(itemId = 0x224E, quantity = 0),
                    InventoryEntry(itemId = 0xD64C, quantity = 0),
                    InventoryEntry(itemId = 0xD64E, quantity = 0),
                    InventoryEntry(itemId = 0xD650, quantity = 0),
                    InventoryEntry(itemId = 0xD652, quantity = 0),
                    InventoryEntry(itemId = 0xD654, quantity = 0),
                    InventoryEntry(itemId = 0xD656, quantity = 0),
                ),
            )
        )
    }

    private suspend fun sendFirstLightTail(session: GameSession, player: Player) {
        session.send(NpcInfoThunk())
        session.send(TriggerOnDialogAbort())
        session.send(ClearPendingUpdates())
        session.send(NpcInfoEncoder.buildInit(player))
        // FROZEN DEV-SCAFFOLDING (default-on): captured single-player 28-skill stat block.
        if (EnvVars.firstLightScaffolding) sendInitialStats(session)
        session.send(UpdateRunWeight(0))
        // Run energy via op13 (UPDATE_RUNENERGY), g1 0..100 — full bar. (op80 is the chat filter; §10.4.)
        session.send(UpdateRunenergy(100))
        session.send(ResetEntityLists())
        session.send(SetMultiwayState(0))
        session.send(ClanChannelFull(main = true))
        for (slot in 0..7) {
            session.send(
                CutsceneData(
                    group = 0,
                    slot = slot,
                    mode = 7,
                    extendedMode = 2,
                    shape = 0,
                    flags = 0,
                    id = 0,
                    primaryLong = 0,
                    primaryInt = 0,
                    secondaryInt = 0,
                    secondaryLong = 0,
                    skipLength = 0,
                )
            )
        }
        for (slot in 0..7) {
            session.send(PlayerInfoDecode(slot = slot, mode = 0))
        }
        session.send(RebuildRegion.firstLight())
        session.send(MinimapFlagB(1))
        session.send(MinimapFlagA(1))
        session.send(TriggerOnDialogAbort())
    }

    private val loginRandom = SecureRandom()

    private const val INITIAL_DISPLAY_INT = -1381430710
    private val INITIAL_MIDI_SONG = byteArrayOf(0x7E, 0x8C.toByte(), 0xE3.toByte(), 0x00, 0x00)
    private const val NPC_INFO_COORD_BIT_WIDTH = 7

    /** Top-level interface ID for the main in-game HUD. */
    private const val GAME_HUD_INTERFACE = 1477
}
