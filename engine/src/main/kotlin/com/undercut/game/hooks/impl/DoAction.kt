package com.undercut.game.hooks.impl
import com.undercut.game.localizeScene
import com.undercut.game.tileOfSceneLocal
import com.undercut.game.tileOfLocal

import world.gregs.voidps.type.Tile
import world.gregs.voidps.gameval.Gameval
import com.undercut.game.bootstrap.Bootstrap
import com.undercut.game.hooks.Hook
import com.undercut.game.hooks.HookManager
import com.undercut.game.interfaces.IFSlot
import com.undercut.game.memory.NativeAccess
import com.undercut.game.memory.NativeAccess.toShared
import com.undercut.game.nxt.DoActionOpcode
import com.undercut.game.nxt.EntityType
import com.undercut.game.nxt.OFunctions
import com.undercut.game.nxt.entity.EntityTypeContainer
import com.undercut.game.nxt.entity.location.CombinedLocationSection
import com.undercut.game.nxt.entity.location.Location
import com.undercut.game.nxt.entity.location.SceneObject
import com.undercut.game.nxt.entity.npc.NPC
import com.undercut.game.nxt.minimenu.MiniMenuEntry
import com.undercut.script.ScriptExecutor
import com.undercut.script.api.localPlayer
import com.undercut.script.event.impl.ManualDoAction
import com.undercut.script.event.impl.ManualGroundItem
import com.undercut.script.event.impl.ManualItemTarget
import com.undercut.util.componentIdFromHash
import com.undercut.util.interfaceIdFromHash
import java.lang.foreign.MemorySegment

object DoAction {
    private val GROUND_ITEM_OPS = setOf(
        DoActionOpcode.GROUND_ITEM_1, DoActionOpcode.GROUND_ITEM_2, DoActionOpcode.GROUND_ITEM_3,
        DoActionOpcode.GROUND_ITEM_4, DoActionOpcode.GROUND_ITEM_5, DoActionOpcode.GROUND_ITEM_6,
        DoActionOpcode.SELECT_GROUND_ITEM,
    )

    @JvmStatic
    @Hook(OFunctions.MINIMENU_DOACTIONENTRY)
    fun doActionHook(miniMenu: MemorySegment, miniMenuEntrySharedPtr: MemorySegment, vec3f: MemorySegment): MemorySegment {
        synchronized(Bootstrap.lock) {
            try {
                val entry = MiniMenuEntry(miniMenuEntrySharedPtr.toShared().value(0x128L))
                val knownAction = DoActionOpcode.getActionById(entry.action.id)
                //println("[DoAction] miniMenuPtr: 0x${miniMenu.address().toString(16)} miniMenuEntryPtr: 0x${miniMenuEntrySharedPtr.toShared().value().address().toString(16)}")
                println("[DoAction] Opcode: ${knownAction?.actionName ?: "${entry.action.id}"} Params: ${entry.param1}, ${entry.param2}, ${entry.param3} Targeted entity: ${entry.target.address().toString(16)}")
                when(knownAction) {
                    DoActionOpcode.WALK -> {
                        val tile = Tile.of(entry.param2, entry.param3, localPlayer.plane)
                        println("\tMinimap: ${entry.param1} Tile.of(${tile.x}, ${tile.y}, ${tile.plane}) local: tileOfLocal(${tile.xInRegion}, ${tile.yInRegion}, ${tile.plane})")
                        val sceneLocal = tile.localizeScene()
                        if (sceneLocal != null)
                            println("\tsceneLocal: tileOfSceneLocal(${sceneLocal.x}, ${sceneLocal.y}, ${tile.plane})")
                        ScriptExecutor.pushEvent(ManualDoAction(knownAction, Tile.of(entry.param2, entry.param3, localPlayer.plane)))
                    }
                    DoActionOpcode.COMPONENT, DoActionOpcode.COMPONENT_SIXPLUS -> {
                        println("\tOpNum: ${entry.param1} IFSlot(${Gameval.interfaceLabel(interfaceIdFromHash(entry.param3))}, ${Gameval.componentLabel(interfaceIdFromHash(entry.param3), componentIdFromHash(entry.param3))}, ${entry.param2})")
                        ScriptExecutor.pushEvent(ManualDoAction(knownAction, IFSlot(interfaceIdFromHash(entry.param3), componentIdFromHash(entry.param3), entry.param2)))
                    }
                    DoActionOpcode.SELECT_COMPONENT -> {
                        println("\tOpNum: ${entry.param1} IFSlot(${Gameval.interfaceLabel(interfaceIdFromHash(entry.param3))}, ${Gameval.componentLabel(interfaceIdFromHash(entry.param3), componentIdFromHash(entry.param3))}, ${entry.param2})")
                        ScriptExecutor.pushEvent(ManualDoAction(knownAction, IFSlot(interfaceIdFromHash(entry.param3), componentIdFromHash(entry.param3), entry.param2)))
                    }
                    DoActionOpcode.DIALOGUE -> {
                        println("\tOpNum: ${entry.param1} IFSlot(${Gameval.interfaceLabel(interfaceIdFromHash(entry.param3))}, ${Gameval.componentLabel(interfaceIdFromHash(entry.param3), componentIdFromHash(entry.param3))}, ${entry.param2})")
                    }
                    DoActionOpcode.OBJECT_1, DoActionOpcode.OBJECT_2, DoActionOpcode.OBJECT_3, DoActionOpcode.OBJECT_4, DoActionOpcode.OBJECT_5, DoActionOpcode.OBJECT_6 -> {
                        val loc: EntityTypeContainer? = if (entry.target.address() != 0L) EntityTypeContainer(entry.target.toShared().value(0x18L)) else null
                        if (loc != null) {
                            val obj: SceneObject? = when (loc.type) {
                                EntityType.LOCATION -> Location(entry.target.toShared().value(0x2000L))
                                EntityType.COMBINED_LOCATION_SECTION -> CombinedLocationSection(entry.target.toShared().value(0x2000L))
                                else -> null
                            }
                            if (obj != null) {
                                ScriptExecutor.pushEvent(ManualDoAction(knownAction, obj))
                                println("\t${obj.javaClass.simpleName}: realId: ${Gameval.locLabel(obj.id)} visibleId: ${Gameval.locLabel(obj.typeId)} name: ${obj.name()} tile: ${obj.tile}")
                            }
                        }
                    }
                    DoActionOpcode.GROUND_ITEM_1, DoActionOpcode.GROUND_ITEM_2, DoActionOpcode.GROUND_ITEM_3,
                    DoActionOpcode.GROUND_ITEM_4, DoActionOpcode.GROUND_ITEM_5, DoActionOpcode.GROUND_ITEM_6,
                    DoActionOpcode.SELECT_GROUND_ITEM -> {
                        val name = entry.targetString.toString().replace(Regex("<[^>]*>"), "").trim()
                        val tile = Tile.of(entry.param2, entry.param3, localPlayer.plane)
                        println("\tGroundItem: id: ${entry.param1} (${Gameval.obj(entry.param1) ?: ""}) name: $name tile: $tile")
                        ScriptExecutor.pushEvent(ManualDoAction(knownAction, ManualGroundItem(entry.param1, name, tile)))
                    }
                    DoActionOpcode.NPC_1, DoActionOpcode.NPC_2, DoActionOpcode.NPC_3, DoActionOpcode.NPC_4, DoActionOpcode.NPC_5, DoActionOpcode.NPC_6 -> {
                        val npc = if (entry.target.address() != 0L) NPC(entry.target.toShared().value(0x2000L)) else null
                        if (npc != null) {
                            println("\tNPC: addr: ${Bootstrap.client.npcManager[npc.serverIndex]?.address()?.toString(16)} sid: ${npc.serverIndex} realId: ${Gameval.npcLabel(npc.id)} visibleId: ${Gameval.npcLabel(npc.typeId)} name: ${npc.name}")
                            ScriptExecutor.pushEvent(ManualDoAction(knownAction, npc))
                        }
                    }
                    else -> Unit
                }
                // Any entry carrying an item id (inventory click, use-item) is also surfaced as an
                // item target so the quest editor's picker can capture it. targetString is the
                // visible menu label, markup stripped. Ground-item ops are skipped here — they're
                // surfaced above as ManualGroundItem (with the world tile).
                if (knownAction != null && knownAction !in GROUND_ITEM_OPS && entry.itemId > 0) {
                    val itemName = entry.targetString.toString().replace(Regex("<[^>]*>"), "").trim()
                    ScriptExecutor.pushEvent(ManualDoAction(knownAction, ManualItemTarget(entry.itemId, itemName)))
                }
                if (knownAction == null) {
                    println("\tUnknown action: ${entry.action.id}")
                    println("\tActionFuncPtr: 0x${entry.action.ptr.address().toString(16)}")
                    println("\tActionFuncPtrOffset: 0x${(entry.action.actionSendFunc.address() - NativeAccess.BASE_ADDR.address()).toString(16)}")
                } else if ((entry.action.actionSendFunc.address() - NativeAccess.BASE_ADDR.address()) != knownAction.callback) {
                    println("\t INVALID ACTION SEND FUNCTION: 0x${(entry.action.actionSendFunc.address() - NativeAccess.BASE_ADDR.address()).toString(16)}")
                    println("\t ${knownAction}(${knownAction.id}, \"${knownAction.actionName}\", 0x${(entry.action.actionSendFunc.address() - NativeAccess.BASE_ADDR.address()).toString(16)}),")
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            return HookManager.trampoline(::doActionHook.name).invokeExact(miniMenu, miniMenuEntrySharedPtr, vec3f) as MemorySegment
        }
    }
}