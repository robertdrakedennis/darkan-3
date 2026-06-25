package com.undercut.script.impl.qb.Temporary

import com.undercut.script.Script
import com.undercut.script.ScriptDescription
import com.undercut.script.api.localPlayer
import com.undercut.script.event.Event
import com.undercut.script.event.impl.Chat
import com.undercut.script.event.impl.XPDrop
import com.undercut.util.format
import com.undercut.script.api.interfaces
import com.undercut.script.api.clickKey


@ScriptDescription(
        name = "QB's Test Script",
        version = "1.0.0",
        author = "QB",
        description = "Test debug script for Trent's memory reading development."
)
class TestScriptQB : Script() {
        var startTime = 0L

        override fun onStart() {
                println("Test script start.")
                startTime = System.currentTimeMillis()
        }

        override suspend fun loop() {
//        var comp = interfaces[1519]?.get(3)?.slotChildren[0]?.text


println(localPlayer.tile.regionId)
                
                if (interfaces.isOpen(1469)) {
                        println("Input UI Open")
                        clickKey('1')
                        delay(1000)
                }
                // println("Local player animation ID: ${localPlayer.animationId}")
                // localPlayer.headbars.forEach {
                //     println("ID: ${it.type}")
                //     println("ID: ${it.fromFill}")
                //     println("ID: ${it.toFill}")
                // }
                // var x = interfaces.getComponent(1519, 3)?.slotChildren?.get(0)


                // val component7 = interfaces.getComponent(1587, 9)
                // if (component7 == null) {
                //         println("CompNull")
                //         return
                // }
                // var currentLoopWorld = 0

                // if (component7.slotChildren.isEmpty()) {
                //         println("SlotChildren empty")
                //         return
                // }

                // component7.slotChildren.forEach {
                //         if (it.slotId % 9 == 2) {

                //                 println("World: ${it.text}")
                //         }
                //         if (it.slotId % 9 == 3) {
                //                 println("Pop: ${it.text}")
                //         }

                //         delay(100)
                // }


//        println(localPlayer.animationId)

//        for (i in 0..interfaces.size) {
//            if(interfaces[i.toInt()]==null) continue
//            for (j in 0..45) {
//                if (interfaces[i.toInt()]?.get(j) == null) continue
//                for (k in 0..10) {
//                    try {
//                        val comp = interfaces[i.toInt()]?.get(j)?.slotChildren?.get(k)
//                        if (comp != null && !comp.text.isEmpty()) {
//                            println("[${comp.interfaceId}, ${comp.componentId}, ${comp.slotId}]: ${comp.text}")
//                            delay(2)
//                        }
//                    } catch (ex: Throwable) {
//                        break
//                    }
//                }
//            }
//        }


//        println(comp)
//        for (i in 0..50) {
//            var comp = interfaces.getComponent(1519, i)
//            if (comp != null && !comp.children.isEmpty())
//                comp.children.forEach { comp2 ->
//                    if (!comp2.text.isEmpty())
//                        println("[${comp2.interfaceId}, ${comp2.componentId}, ${comp2.slotId}]: ${comp2.text}")
//                }
//        }


//        println(" Test script loop started.")
//        println(inventory.getItem(54913)?.name)
//        val woodbox1 = inventory.getItem(54913)?.name
//
//        println(woodbox.getItem("Logs")?.amount)
//        println(woodbox.getItem("Oak logs")?.amount)


//        InstanceSystem.debugComponents()

//        val startingCoord: Tile = Tile.of(3201, 3293, 0)
//        if (startingCoord.getDistance(localPlayer.tile) < 10) {
//            return
//        }
//
//        val route = PathFinder(
//            flags = WorldCollision.allFlags,
//            searchMapSize = 1024,
//            useRouteBlockerFlags = true,
//            moveNear = false
//        ).findPath(
//            localPlayer.tile.x.toInt(),
//            localPlayer.tile.y.toInt(),
//            startingCoord.x.toInt(),
//            startingCoord.y.toInt(),
//            localPlayer.tile.plane.toInt(),
//            collision = CollisionStrategyType.NORMAL,
//            srcSize = 2,
//            destWidth = 1,
//            destHeight = 1
//        )
//
//
//        if (route.failed) {
//
//                useLodestone(Lodestone.AL_KHARID)
//                delay(random(600, 1800))
//
//            return
//        }
//
//        route.toTiles().forEach {
//            if (it.getDistance(localPlayer.tile) > 7) {
//
//                    walkTo(it, true)
//                    delayUntil { it.getDistance(localPlayer.tile) < 4 }
//
//                return@forEach
//            }
//        }

//        SafeScriptBuilder.of(1564)
//            .args(Layout.STRING)
//            .invokeSafe("30")
                //SafeScriptBuilder.ofVoid(3741).invokeSafe()

//        println("Test script address: ${CS2ScriptExecutor.CS2HookContext.currentScriptRunner}")
                //   GameInput.setIntInput(40);
                // println("Input set to 40")

//        val BANK_WITHDRAW_X = ScriptBuilder.of(1564).args(Layout.STRING)
//        BANK_WITHDRAW_X.invoke("10")

                //


//        val areapolygon = Area.Polygonal(
//            Tile.of(3350, 3391, 0), Tile.of(3350, 3399, 0), Tile.of(3365, 3399, 0), Tile.of(3380, 3388, 0), Tile.of(3380, 3381, 0), Tile.of(3372, 3382, 0))
//        // val starttile = Tile.of(2800,3444,0)

//        val areapolygon = Area.Polygonal(
//            Tile.of(3350, 3391, 0), Tile.of(3350, 3399, 0), Tile.of(3365, 3399, 0), Tile.of(3380, 3388, 0), Tile.of(3380, 3381, 0), Tile.of(3372, 3382, 0))
                // val starttile = Tile.of(2800,3444,0)

//        println(localPlayer.tile)
//        println(areapolygon.contains(localPlayer.tile.asLocatable()))
                //println(Area.Circular(localPlayer.tile, 10.0).contains(localPlayer.tile.asLocatable()))
//
                //  println("LocalPlayer: ${localPlayer.ptr}")
//        println("Interfaces: ${interfaces.ptr}")
//        println("Inventory interface parent: ${interfaces[1473]?.ptr}")
//        println("Inventory interface component: ${interfaces[1473]?.get(5)?.ptr}")
//        println("--HEADBARS--")
//        println("headbars: ${localPlayer.hitmarksAndHeadbars?.ptr}")
//        localPlayer.headbars.forEach {
//            println("\tHeadbar [0x${it.ptr.address().toString(16)}]")
//            println("\t\t${it.type}, ${it.fromFill}, ${it.toFill}, ${it.timeLeftMillis}/${it.durationMillis}")
//        }
//        println("NPCs")
//        npcs.values.forEach {
//            if (it.name.contains("oed"))
//                println("Apter: ${it.ptr}")
//        }
//        println("ClientCycle: ${Bootstrap.client.clientCycle}")
//        println("Spotanims: ${Bootstrap.client.spotAnimManager.ptr.address().toString(16)}")
//        spotAnims.forEach {
//            println("SpotAnim ${it.id} [0x${it.ptr.address().toString(16)}] ${it.timeAliveMillis}")
//        }
//        println("Npcs lmao")
//        npcs.values.filter { it.hiddenMenuOpFlags != 0 }.forEach { npc ->
//            println("\t${npc.name} - ${npc.realId} - ${npc.hiddenMenuOpFlags} (${npc.hiddenMenuOpFlags.toBitString()})")
//        }
//        if (InstanceSystem.isOpen() && InstanceSystem.instanceDetails != null) {
//            val instance = InstanceSystem.instanceDetails?:return
//            if (instance.minCombat == 30 && instance.maxPlayers == 5) {
//                println("~~~~~~ Settings Matched ~~~~~~~~")
//                println(instance.cost)
//                println(instance.name)
//                println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
//            } else if (instance.minCombat == 25 && instance.maxPlayers == 5) {
//                InstanceSystem.startInstance()
//                delayUntil(5000) { !InstanceSystem.isOpen() }
//            }
//        }
                //Finds inventories
//        for (i in 0..1000) {
//            try {
//                val inv = Bootstrap.client.inventoryManager[i]
//                var itemsStr = ""
//                inv.filter { it.id != -1}.takeIf { it.isNotEmpty() }?.forEach { itemsStr += "${it.name}(${it.id}, ${it.amount}), "}
//                println("[$i] - $itemsStr")
//            } catch (e: Throwable) {
//
//            }
//        }
//        val mapSquare = Bootstrap.client.sceneManager.getMapSquare(localPlayer.tile)
//        println("mapSquare: ${mapSquare?.ptr?.address()?.toString(16)}")
//        val locContainer = Bootstrap.client.sceneManager.getLocationContainer(localPlayer.tile)
//        println("Containers: ${locContainer?.ptr?.address()?.toString(16)}")
//        val container = Bootstrap.client.sceneManager.getMapSquare(localPlayer.tile)?.getLocationContainer(localPlayer.tile.xInRegion, localPlayer.tile.yInRegion)
//        println("Container: ${container?.ptr}")
//        println("Obj:")
//        container?.allSceneObjects?.forEach { it ->
//            println("\t [${it.javaClass.simpleName}]: ${it.name()} - ${it.tile}")
//        }
//        println("Player tile: ${localPlayer.tile}")

//        println("--Tile SpotAnims--")
//        spotAnims.forEach {
//            println("${it.id} - ${it.tile}")
//        }
//
//        val componentIds = setOf(
//            30501, 30502, 30503, 30504, 30505, 30506, 30507, 30508, 30509, 30510, 30511,
//            30512, 30513, 30514, 30515, 30516, 30517, 30518, 30519, 30520, 30521, 30921,
//            30922, 30923, 30924, 30925, 30926, 30927, 30928, 30929, 30930, 30931, 30932,
//            30933, 30934, 30935, 30936, 30937, 30938, 30939, 30940, 30941
//        )
//
//        println("--RitualComponent SpotAnims--")
//        npcs.values.filter { component -> componentIds.contains(component.realId) }
//            .forEach { component ->
//                if (component.graphNode.childEntities.isEmpty()) return@forEach
//                println("Non-empty child component: ${component.name}")
//                component.graphNode.childEntities.forEach {
//                    println("${it.type} -> ${it.graphNode.childEntities.size}")
//                    if (it is SpotAnim)
//                        println("SpotAnimID: ${it.id}")
//                }
//            }
//        println("Raksha emote: ${findClosestNPC("Raksha, the Shadow Colossus")?.animation}")
//        println("Target hits: ${npcs[localPlayer.interactionSid]?.hits}")
//        println("--PROJECTILE-- (${projectiles.ptr})")
//        projectiles.forEach {
//            println("${it.id} - ${it.tile} - ${it.tile.getDistance(localPlayer.tile)}")
//        }
//        println("ground item list: ${Bootstrap.client.itemStackList.ptr.address().toString(16)}")
//        groundItems.forEach {
//            println("${it.name} - ${it.tile}")
//        }
//        groundItems.firstOrNull()?.interact("Take")


//        println("Tileflags: ${ClipFlag.getFlags(WorldCollision.getFlags(localPlayer.tile))}")
//

                //println("clientVarDomain: ${Bootstrap.client.clientVarDomain.ptr}")

//        val options = buildMap {
//            interfaces.getComponent(720, 14)?.text?.let { set(it, IFSlot(720, 1, -1)) }
//            interfaces.getComponent(720, 21)?.text?.let { set(it, IFSlot(720, 20, -1)) }
//            interfaces.getComponent(720, 24)?.text?.let { set(it, IFSlot(720, 23, -1)) }
//            interfaces.getComponent(720, 27)?.text?.let { set(it, IFSlot(720, 26, -1)) }
//            interfaces.getComponent(720, 30)?.text?.let { set(it, IFSlot(720, 29, -1)) }
//            interfaces.getComponent(720, 33)?.text?.let { set(it, IFSlot(720, 32, -1)) }
//            interfaces.getComponent(720, 36)?.text?.let { set(it, IFSlot(720, 35, -1)) }
//            interfaces.getComponent(720, 39)?.text?.let { set(it, IFSlot(720, 38, -1)) }
//            interfaces.getComponent(720, 42)?.text?.let { set(it, IFSlot(720, 41, -1)) }
//            interfaces.getComponent(720, 45)?.text?.let { set(it, IFSlot(720, 44, -1)) }
//        }
//
//        for (i in 0..45) {
//            interfaces.getComponent(720, i)?.let { comp ->
//                if (!comp.text.isEmpty())
//                    println("[${comp.interfaceId}, ${comp.componentId}, ${comp.slotId}]: ${comp.text}")
//            }
//        }

//        val component = interfaces.getComponent(720, 45)
//        if (component != null) {
//            println("Component founded: ${component.ptr.address().toString(16)}")
//            println("${component.componentId}: ${component.text}")
//            component.children.forEach {
//                println("${it.componentId}: ${it.text}")
//                it.children.forEach { child ->
//                    println("${child.componentId}: ${child.text}")
//                }
//                it.slotChildren.forEach { child ->
//                    println("${child.componentId}, ${child.slotId}: ${child.text}")
//                }
//            }
//        }

//        val skip = setOf(Effect.WRAPPING_PAPER_BOOST, Effect.GENOCIDAL, Effect.PERFECT_BUILD, Effect.EREBUS_RIFT_ATTUNEMENT, Effect.MENAPHOS_RELEASE_XP_BOOST)
//        println("Buffs and debuffs:")
//        Effect.entries.forEach {
//            if (skip.contains(it)) return@forEach
//            if (isEffectActive(it)) {
//                val stacks = getEffectStacks(it)
//                val time = getEffectTimeRemaining(it)
//                println("[$it]")
//                if (stacks > 0)
//                    println("\tstack: $stacks")
//                if (time > 0)
//                    println("\ttime: $time")
//            }
//        }

//        println("Actionbar:")
//        println(actionbarAbilities.keys)

                delay(100)
        }

        val varbitWhitelist = setOf(
                50782, 50783, 50784, 50785, 50786, 50787, 50788, 50789,
                50790, 50791, 50792, 50793, 50813, 50814, 50815, 50816,
                50817, 50818, 50820, 50821, 50822, 50824, 50804, 50805,
                50806, 50807, 50808, 50809, 50810, 50811, 50812
        )

//    override fun onEvent(event: Event) {
//        when (event) {
//            is Chat -> println("[ChatEvent]: (${event.messageType}) ${event.cleanSenderName}: ${event.message}")
//            is XPDrop -> println("[XPDrop]: ${event.skill} - ${format(event.gainedXp)}")
//            is Varc -> Ability.debugCooldownVarc(event)
//            is Varpbit -> {
//                if (varbitWhitelist.contains(event.id))
//                    println("[Varbit]: ${event.id}: ${event.oldValue} -> ${event.newValue}")
//            }
//        }
//    }


        override fun onEvent(event: Event) {
                when (event) {
                        is Chat -> println("[ChatEvent]: (${event.messageType}) ${event.cleanSenderName}: ${event.message}")
                        is XPDrop -> println("[XPDrop]: ${event.skill} - ${format(event.gainedXp)}")
//            is Varc -> Ability.debugCooldownVarc(event)
//            is Varpbit -> {
//                if (varbitWhitelist.contains(event.id))
//                    println("[Varbit]: ${event.id}: ${event.oldValue} -> ${event.newValue}")
//            }
                }
        }
}
