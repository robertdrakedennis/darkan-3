package com.undercut.game.nxt

import com.undercut.game.Tile
import com.undercut.diag.CrashForensics
import com.undercut.game.bootstrap.Bootstrap
import com.undercut.game.input.DoActionShadow
import com.undercut.game.input.ShadowInputBus
import com.undercut.game.math.WorldToScreen
import com.undercut.game.memory.NativeAccess
import com.undercut.game.memory.NativeAccess.toFunctionHandle
import com.undercut.game.nxt.entity.npc.NPC
import com.undercut.game.nxt.entity.player.Player
import com.undercut.profiling.PlayerProfiles
import com.undercut.script.api.localPlayer
import com.undercut.util.componentIdFromHash
import com.undercut.util.interfaceIdFromHash
import java.lang.foreign.FunctionDescriptor
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout.*
import java.lang.invoke.MethodHandle

enum class DoActionOpcode(val id: Int, val actionName: String, val callback: Long) { //947-3
    SELECT_OBJECT(2, "DoOpSelectLoc", 0x15C910),
    OBJECT_1(3, "DoOpLoc1", 0x15C9D0),
    OBJECT_2(4, "DoOpLoc2", 0x15C9B0),
    OBJECT_3(5, "DoOpLoc3", 0x15C990),
    OBJECT_4(6, "DoOpLoc4", 0x15C970),
    SELECT_NPC(8, "DoOpTargetNpc", 0x15C510),
    PLAYER_SELECT(15, "DoOpSelectPlayer", 0x15BE60),
    COMP_ON_PLAYER(16, "DoOpComponentOnPlayer", 0x15BE40),
    SELECT_GROUND_ITEM(17, "DoOpSelectObjStack", 0x15C1A0),
    GROUND_ITEM_1(18, "DoOpObjStack1", 0x15C260),
    GROUND_ITEM_2(19, "DoOpObjStack2", 0x15C240),
    GROUND_ITEM_3(20, "DoOpObjStack3", 0x15C220),
    GROUND_ITEM_4(21, "DoOpObjStack4", 0x15C200),
    GROUND_ITEM_5(22, "DoOpObjStack5", 0x15C1E0),
    WALK(23, "DoOpWalk", 0x15B950),
    SELECT_COMPONENT(25, "DoOpTargetComponent", 0xF3D90),
    DIALOGUE(30, "DoOpDialog", 0x126790),
    PLAYER_1(2044, "DoOpPlayer1", 0x136E80),
    PLAYER_2(2045, "DoOpPlayer2", 0x136E60),
    PLAYER_3(2046, "DoOpPlayer3", 0x136E40),
    PLAYER_4(2047, "DoOpPlayer4", 0x136E20),
    PLAYER_5(2048, "DoOpPlayer5", 0x136E00),
    PLAYER_6(2049, "DoOpPlayer6", 0x136DE0),
    PLAYER_7(2050, "DoOpPlayer7", 0x136DC0),
    PLAYER_8(2051, "DoOpPlayer8", 0x136DA0),
    PLAYER_9(2052, "DoOpPlayer9", 0x136D80),
    PLAYER_10(2053, "DoOpPlayer10", 0x136D60),
    COMPONENT(57, "DoOpComponent", 0xF3C40),
    SELECT_COMPONENT_ITEM(58, "DoOpTargetComponentItem", 0x14EF70),
    SELECT_TILE(59, "DoOpSelectTile", 0x15B740),
    OBJECT_5(1001, "DoOpLoc5", 0x15C950),
    OBJECT_6(1002, "DoOpLoc6", 0x15C930),
    GROUND_ITEM_6(1004, "DoOpObjStack6", 0x15C1C0),
    UNK_1005(1005, "Unk1005", 0x1460B0),
    COMPONENT_SIXPLUS(1007, "DoOpComponent6Plus", 0xF3C40),
    NPC_1(9, "DoOpNpc1", 0x15C5D0),
    NPC_2(10, "DoOpNpc2", 0x15C5B0),
    NPC_3(11, "DoOpNpc3", 0x15C590),
    NPC_4(12, "DoOpNpc4", 0x15C570),
    NPC_5(13, "DoOpNpc5", 0x15C550),
    NPC_6(1003, "DoOpNpc6", 0x15C530);

    companion object {
        fun getActionById(id: Int): DoActionOpcode? {
            return entries.find { it.id == id } ?: if (id == 3003) NPC_6 else null
        }
    }

    private val action: MethodHandle = NativeAccess.BASE_ADDR.asSlice(callback, 8).toFunctionHandle(FunctionDescriptor.ofVoid(ADDRESS, ADDRESS))
    private val clientFakePtr: MemorySegment = NativeAccess.engineArena.allocate(0x8)
    private val fakeMiniMenuEntrySharedPtr: MemorySegment = NativeAccess.engineArena.allocate(0x10)
    private val fakeMiniMenuEntry: MemorySegment = NativeAccess.engineArena.allocate(0x128)

    fun fire(param1: Int, param2: Int, param3: Int) {
        if (Bootstrap.client.mainState != MainState.LOGGED_IN && this != COMPONENT) return
        clientFakePtr.set(JAVA_LONG, 0x0L, Bootstrap.client.ptr.address())
        fakeMiniMenuEntrySharedPtr.set(JAVA_LONG, 0x0L, fakeMiniMenuEntry.address())
        fakeMiniMenuEntrySharedPtr.set(JAVA_LONG, 0x8L, fakeMiniMenuEntry.address())
        fakeMiniMenuEntry.set(JAVA_INT, OMiniMenuEntry.PARAM_1, param1)
        fakeMiniMenuEntry.set(JAVA_INT, OMiniMenuEntry.PARAM_2, param2)
        fakeMiniMenuEntry.set(JAVA_INT, OMiniMenuEntry.PARAM_3, param3)
        fakeMiniMenuEntry.set(JAVA_LONG, OMiniMenuEntry.TARGETED_ENTITY, 0L)
        // The action handler's tail (e.g. FUN_00204430 for ObjStack ops) only queues local
        // predictive pathfinding when entry.highlightType == 2. Without it the avatar stays
        // still after the take packet, the server never sees the player reach the tile, and
        // the action is silently dropped. Right-click menu entries built by the binary have
        // this set to 2 by default — synthetics need it too.
        fakeMiniMenuEntry.set(JAVA_INT, OMiniMenuEntry.HIGHLIGHT_TYPE, 2)
        println("FIRED SYNTHETIC ActionType.$name($param1, $param2, $param3)")
        CrashForensics.trace("DoAction.$name($param1,$param2,$param3)")
        action.invoke(clientFakePtr, fakeMiniMenuEntrySharedPtr)
        CrashForensics.trace("DoAction.$name returned")
        publishShadowIntent(param1, param2, param3)
    }

    private fun publishShadowIntent(param1: Int, param2: Int, param3: Int) {
        try {
            val profile = PlayerProfiles.get()
            if (!profile.synthInputEnabled || !profile.synthShadowDoActions) return
            val (tx, ty) = resolveTarget(param1, param2, param3)
            ShadowInputBus.publish(
                DoActionShadow(
                    opcode = this,
                    param1 = param1,
                    param2 = param2,
                    param3 = param3,
                    gameTick = Bootstrap.client.clientCycle,
                    timestampNanos = System.nanoTime(),
                    resolvedTargetX = tx,
                    resolvedTargetY = ty,
                )
            )
        } catch (_: Throwable) {
            // Never let shadow plumbing affect the action call.
        }
    }

    /**
     * Resolve the on-screen target for this action while we're still on the
     * game thread under the action call's stack frame. The synth consumer will
     * trust whatever we put in the intent — by the time it runs, the entity
     * pointer may be freed memory (NPC despawned, loc deleted), so we can't
     * defer this. Returns null if resolution isn't supported or fails.
     */
    private fun resolveTarget(param1: Int, param2: Int, param3: Int): Pair<Float?, Float?> {
        return try {
            when (this) {
                WALK, SELECT_TILE -> tileScreen(param2, param3)

                SELECT_NPC, NPC_1, NPC_2, NPC_3, NPC_4, NPC_5, NPC_6 -> npcScreen(param1)

                SELECT_OBJECT,
                OBJECT_1, OBJECT_2, OBJECT_3, OBJECT_4, OBJECT_5, OBJECT_6 ->
                    tileScreen(param2, param3)  // loc actions carry tile coords in param2/3

                SELECT_GROUND_ITEM,
                GROUND_ITEM_1, GROUND_ITEM_2, GROUND_ITEM_3,
                GROUND_ITEM_4, GROUND_ITEM_5, GROUND_ITEM_6 ->
                    tileScreen(param2, param3)

                PLAYER_SELECT, COMP_ON_PLAYER,
                PLAYER_1, PLAYER_2, PLAYER_3, PLAYER_4, PLAYER_5,
                PLAYER_6, PLAYER_7, PLAYER_8, PLAYER_9, PLAYER_10 ->
                    playerScreen(param1)

                COMPONENT, COMPONENT_SIXPLUS, SELECT_COMPONENT, SELECT_COMPONENT_ITEM, DIALOGUE ->
                    componentScreen(param3)

                else -> null to null
            }
        } catch (_: Throwable) {
            null to null
        }
    }

    private fun tileScreen(tileX: Int, tileY: Int): Pair<Float?, Float?> {
        val plane = localPlayer.tile.plane.toInt()
        val v = WorldToScreen.getEstimatedTileCenter(Tile.of(tileX, tileY, plane))
            ?: return null to null
        return v.x to v.y
    }

    private fun npcScreen(serverIndex: Int): Pair<Float?, Float?> {
        val ptr = Bootstrap.client.npcManager[serverIndex] ?: return null to null
        if (ptr.address() == 0L) return null to null
        val npc = NPC(ptr)
        if (!npc.exists()) return null to null
        val cx = npc.screenCenterX
        val cy = npc.screenCenterY
        if (cx <= 0 || cy <= 0) return null to null
        return cx.toFloat() to cy.toFloat()
    }

    private fun playerScreen(serverIndex: Int): Pair<Float?, Float?> {
        val ptr = Bootstrap.client.playerManager[serverIndex]
        if (ptr.address() == 0L) return null to null
        val player = Player(ptr)
        val cx = player.screenCenterX
        val cy = player.screenCenterY
        if (cx <= 0 || cy <= 0) return null to null
        return cx.toFloat() to cy.toFloat()
    }

    private fun componentScreen(componentHash: Int): Pair<Float?, Float?> {
        val list = Bootstrap.client.interfaceList
        val ifaceId = interfaceIdFromHash(componentHash)
        val compId = componentIdFromHash(componentHash)
        val comp = list.getComponent(ifaceId, compId) ?: return null to null
        val rect = comp.screenRect ?: return null to null
        val cx = rect.x + rect.width / 2f
        val cy = rect.y + rect.height / 2f
        if (cx <= 0f || cy <= 0f) return null to null
        return cx to cy
    }
}