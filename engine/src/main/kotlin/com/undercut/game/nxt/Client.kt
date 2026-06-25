package com.undercut.game.nxt

import com.undercut.game.memory.NativeAccess.deref
import com.undercut.game.memory.NativeAccess.getInt
import com.undercut.game.memory.NativeAccess.pointerAtOffset
import com.undercut.game.nxt.entity.HintArrow
import com.undercut.game.nxt.interfaces.InterfaceList
import com.undercut.game.nxt.inventories.Inventories
import com.undercut.game.nxt.mainlogicmanager.ClientVarDomain
import com.undercut.game.nxt.mainlogicmanager.MainLogicManager
import com.undercut.game.nxt.mainlogicmanager.StatTable
import java.lang.foreign.MemorySegment

object MainState {
    const val INITIALIZING = 0
    const val LOGIN_SCREEN = 10
    const val LOBBY_SCREEN = 20
    const val ACCOUNT_CREATION = 23
    const val LOGGED_IN = 30
    const val ATTEMPTING_TO_REESTABLISH_NOTIFICATION = 35
    const val RECONNECTING_TO_SERVER = 37
    const val LOADING_NOTIFICATION = 40
}

class Client(val ptr: MemorySegment) {

    val mainState
        get() = ptr.getInt(OClient.MAIN_STATE)
    val clientCycle
        get() = ptr.getInt(OClient.CLIENT_CYCLE)

    /**
     * This is a getter because logged in player can be nullptr at game state 10.
     */
    val loggedInPlayer: LoggedInPlayer
        get() = LoggedInPlayer(ptr.deref(OClient.LOGGED_IN_PLAYER, 0x20000L), this)

    val playerVarDomain: PlayerVarDomain
        get() = PlayerVarDomain(ptr.pointerAtOffset(OClient.PLAYER_VAR_DOMAIN, 0x20000L))

    val inventoryManager: Inventories
        get() = Inventories(ptr.deref(OClient.INVENTORY_MANAGER, 0x20000L))

    val playerManager: PlayerManager
        get() = PlayerManager(ptr.deref(OClient.PLAYER_MANAGER, 0x20000L))

    val npcManager: NPCManager
        get() = NPCManager(ptr.deref(OClient.NPC_MANAGER, 0x20000L))

    val sceneManager: SceneManager
        get() = SceneManager(ptr.deref(OClient.SCENE_MANAGER, 0x20000L))

    val interfaceList: InterfaceList
        get() = InterfaceList(ptr.deref(OClient.INTERFACE_LIST, 0x38L).pointerAtOffset(OInterfaceManager.INTERFACE_LIST_PTR, 0x20000L))

    val spotAnimManager: SpotAnimManager
        get() = SpotAnimManager(ptr.deref(OClient.SPOTANIM_MANAGER, 0x20000L))

    val projectileList
        get() = ProjectileList(ptr.deref(OClient.PROJECTILE_LIST, 0x20000L))

    val itemStackList
        get() = ItemStackList(ptr.deref(OClient.ITEMSTACK_LIST, 0x20000L))

    val mainLogicManager: MainLogicManager
        get() = MainLogicManager(ptr.deref(OClient.MAINLOGIC_MANAGER, 0x20000L))

    val sdlManager: SDLManager
        get() = SDLManager(ptr.deref(OClient.SDL_MANAGER, 0x100L))

    val clientVarDomain: ClientVarDomain
        get() = mainLogicManager.clientVarDomain

    val skills: StatTable
        get() = mainLogicManager.statTable

    val hintArrow: HintArrow?
        get() = HintArrow.get()

    companion object {
        fun getClient(base: MemorySegment): Client {
            return Client(base.deref(OGlobal.CLIENT, 0x20000L))
        }
    }
}