package com.undercut.ui.tabs

import com.undercut.game.bootstrap.Bootstrap
import com.undercut.game.nxt.MainState
import com.undercut.script.api.*
import com.undercut.ui.UIState
import com.undercut.ui.backend.dsl.scopes.*
import com.undercut.ui.backend.dsl.utils.ImGuiTableColumnFlags
import com.undercut.ui.backend.dsl.utils.ImGuiTableFlags

object EntitiesTab {
    fun ChildScope.render() {
        text("Entities")
        
        text("Range:")
        sameLine()
        sliderInt("##entityRange", UIState.entityRange, 1, 100)
        
        inputText("Search", UIState.entitySearchText)
        sameLine()
        button("Clear Search") {
            UIState.entitySearchText.value = ""
        }
        
        separator()
        
        child("EntityToggles", width = 0f, height = 120f) {
            checkbox("Scene Objects", UIState.showSceneObjects)
            sameLine()
            checkbox("NPCs", UIState.showNpcs)
            sameLine()
            checkbox("Players", UIState.showPlayers)
            checkbox("Spot Animations", UIState.showSpotAnims)
            sameLine()
            checkbox("Projectiles", UIState.showProjectiles)
            checkbox("Ground Items", UIState.showGroundItems)
            checkbox("Clickboxes", UIState.showClickboxes)
            sameLine()
            checkbox("Filled", UIState.showClickboxesFilled)
        }
        
        text("Overlay Text Color")
        colorEdit4(
            label = "##EntityTextColor",
            r = UIState.entityTextColorR,
            g = UIState.entityTextColorG,
            b = UIState.entityTextColorB,
            a = UIState.entityTextColorA
        )

        text("Clickbox Highlight Color")
        colorEdit3(
            label = "##ClickboxColor",
            r = UIState.clickboxColorR,
            g = UIState.clickboxColorG,
            b = UIState.clickboxColorB,
        )
        combo(
            label = "Outline Mode",
            currentItem = UIState.clickboxOutlineMode,
            items = listOf("Off", "Solid", "Outline", "Glow"),
        )
        text("Intensity")
        sameLine()
        sliderInt("##ClickboxIntensity", UIState.clickboxIntensity, 0, 255)

        separator()
        
        if (Bootstrap.client.mainState != MainState.LOGGED_IN) {
            text("Not logged in")
            return
        }
        
        val searchText = UIState.entitySearchText.value.lowercase()
        val range = UIState.entityRange.value
        val playerTile = localPlayer.tile
        
        table(
            id = "EntitiesTable",
            columns = 5,
            flags = ImGuiTableFlags.SizingStretchSame or ImGuiTableFlags.BordersInner
        ) {
            setupColumn("Type", flags = ImGuiTableColumnFlags.WidthFixed)
            setupColumn("ID", flags = ImGuiTableColumnFlags.WidthFixed)
            setupColumn("Name", flags = ImGuiTableColumnFlags.WidthFixed)
            setupColumn("Location", flags = ImGuiTableColumnFlags.WidthFixed)
            setupColumn("Options")
            headersRow()
            
            if (UIState.showSceneObjects.value) {
                getAllObjectsWithinRange(range).forEach { obj ->
                    if (obj.name().isBlank()) return@forEach
                    if (searchText.isNotEmpty() && 
                        !obj.name().lowercase().contains(searchText) && 
                        !obj.id.toString().contains(searchText)) return@forEach
                    
                    nextRow()
                    nextColumn()
                    text("Object")
                    nextColumn()
                    text(obj.id.toString())
                    nextColumn()
                    text(obj.name())
                    nextColumn()
                    text("${obj.tile.x}, ${obj.tile.y}, ${obj.tile.plane}")
                    nextColumn()
                    text(obj.defs.options.joinToString(", ") { it ?: "None" })

                    if (obj.typeId != -1 && obj.typeId != obj.id) {
                        val def = obj.defs
                        val controlledBy = when {
                            def.varpBit != -1 -> "via varbit ${def.varpBit}"
                            def.varp != -1 -> "via varp ${def.varp}"
                            else -> "via transform"
                        }
                        nextRow()
                        nextColumn()
                        text("  visible")
                        nextColumn()
                        text(obj.typeId.toString())
                        nextColumn()
                        text(controlledBy)
                        nextColumn()
                        text("")
                        nextColumn()
                        text("")
                    }
                }
            }
            
            if (UIState.showNpcs.value) {
                npcs.values.forEach { npc ->
                    val npcTile = npc.tile
                    val distance = playerTile.getDistance(npcTile)
                    if (distance > range) return@forEach
                    if (searchText.isNotEmpty() &&
                        !npc.name().lowercase().contains(searchText) &&
                        !npc.id.toString().contains(searchText)) return@forEach
                    
                    nextRow()
                    nextColumn()
                    text("NPC")
                    nextColumn()
                    text(npc.id.toString())
                    nextColumn()
                    text(npc.name())
                    nextColumn()
                    text("${npcTile.x}, ${npcTile.y}, ${npcTile.plane}")
                    nextColumn()
                    text(npc.getDef().options.filterNotNull().joinToString(", ") { it.ifEmpty { "None" } })

                    val spots = npc.spotAnims
                    if (spots.isNotEmpty()) {
                        nextRow()
                        nextColumn()
                        text("  spotAnims")
                        nextColumn()
                        text(spots.size.toString())
                        nextColumn()
                        text(spots.joinToString(", ") { "${it.id} (${it.timeAliveMillis}ms)" })
                        nextColumn()
                        text("")
                        nextColumn()
                        text("")
                    }
                }
            }

            if (UIState.showPlayers.value) {
                players.forEach { player ->
                    val playerEntityTile = player.tile
                    val distance = playerTile.getDistance(playerEntityTile)
                    if (distance > range) return@forEach
                    if (searchText.isNotEmpty() &&
                        !player.name.lowercase().contains(searchText) &&
                        !player.serverIndex.toString().contains(searchText)) return@forEach

                    nextRow()
                    nextColumn()
                    text("Player")
                    nextColumn()
                    text(player.serverIndex.toString())
                    nextColumn()
                    text(player.name)
                    nextColumn()
                    text("${playerEntityTile.x}, ${playerEntityTile.y}, ${playerEntityTile.plane}")
                    nextColumn()
                    text("N/A")

                    val spots = player.spotAnims
                    if (spots.isNotEmpty()) {
                        nextRow()
                        nextColumn()
                        text("  spotAnims")
                        nextColumn()
                        text(spots.size.toString())
                        nextColumn()
                        text(spots.joinToString(", ") { "${it.id} (${it.timeAliveMillis}ms)" })
                        nextColumn()
                        text("")
                        nextColumn()
                        text("")
                    }
                }
            }
            
            if (UIState.showSpotAnims.value) {
                spotAnims.forEach { spotAnim ->
                    val spotAnimTile = spotAnim.tile
                    val distance = playerTile.getDistance(spotAnimTile)
                    if (distance > range) return@forEach
                    if (searchText.isNotEmpty() &&
                        !spotAnim.id.toString().contains(searchText)) return@forEach
                    
                    nextRow()
                    nextColumn()
                    text("SpotAnim")
                    nextColumn()
                    text(spotAnim.id.toString())
                    nextColumn()
                    text("SpotAnim ${spotAnim.id}")
                    nextColumn()
                    text("${spotAnimTile.x}, ${spotAnimTile.y}, ${spotAnimTile.plane}")
                    nextColumn()
                    text("N/A")
                }
            }
            
            if (UIState.showProjectiles.value) {
                projectiles.forEach { projectile ->
                    val projectileTile = projectile.tile
                    val distance = playerTile.getDistance(projectileTile)
                    if (distance > range) return@forEach
                    if (searchText.isNotEmpty() &&
                        !projectile.id.toString().contains(searchText)) return@forEach
                    
                    nextRow()
                    nextColumn()
                    text("Projectile")
                    nextColumn()
                    text(projectile.id.toString())
                    nextColumn()
                    text("Projectile ${projectile.id}")
                    nextColumn()
                    text("${projectileTile.x}, ${projectileTile.y}, ${projectileTile.plane}")
                    nextColumn()
                    text("Target: ${projectile.lockedToServerIndex}")
                }
            }

            if (UIState.showGroundItems.value) {
                groundItems.forEach { gi ->
                    val giTile = gi.tile
                    if (playerTile.getDistance(giTile) > range) return@forEach
                    val name = gi.name
                    if (searchText.isNotEmpty() &&
                        !name.lowercase().contains(searchText) &&
                        !gi.id.toString().contains(searchText)) return@forEach

                    nextRow()
                    nextColumn()
                    text("Ground Item")
                    nextColumn()
                    text(gi.id.toString())
                    nextColumn()
                    text(if (gi.amount > 1) "$name x${gi.amount}" else name)
                    nextColumn()
                    text("${giTile.x}, ${giTile.y}, ${giTile.plane}")
                    nextColumn()
                    text(gi.groundOps.filterNotNull().joinToString(", ").ifEmpty { "None" })
                }
            }
        }
    }
}