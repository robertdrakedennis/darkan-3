package com.undercut.script.impl.bp.misc

import com.undercut.game.hooks.Priority
import com.undercut.game.math.Vector2f
import com.undercut.script.Script
import com.undercut.script.ScriptDescription
import com.undercut.script.ScriptExecutor
import com.undercut.script.api.localPlayer
import com.undercut.ui.backend.dsl.BooleanState
import com.undercut.ui.backend.dsl.ImGuiDsl
import com.undercut.ui.backend.dsl.utils.ImGuiColors
import com.undercut.ui.backend.rendering.ImGUIRender

@ScriptDescription(
    name = "ImGUI Test Script",
    author = "BP",
    version = "1.0.0",
    description = "Test some drawing stuff"
)
class ImGUITest : Script() {

    private val running: BooleanState = BooleanState(false)

    companion object {
        @JvmStatic
        @ImGUIRender(priority = Priority.LOW)
        fun drawBackgroundExample() {
            // Only draw background graphics while the script is running
            if (ScriptExecutor.isScriptRunning(ImGUITest::class.java)) {
                // Test the new background drawlist DSL
                ImGuiDsl.backgroundDrawList {
                    // Draw a grid pattern
                    val displayWidth = ImGuiDsl.displayWidth()
                    val displayHeight = ImGuiDsl.displayHeight()
                    
                    // Vertical lines
                    for (x in 0..displayWidth.toInt() step 50) {
                        line(Vector2f(x.toFloat(), 0f), Vector2f(x.toFloat(), displayHeight), ImGuiColors.GRAY_DARK, 1f)
                    }
                    
                    // Horizontal lines  
                    for (y in 0..displayHeight.toInt() step 50) {
                        line(Vector2f(0f, y.toFloat()), Vector2f(displayWidth, y.toFloat()), ImGuiColors.GRAY_DARK, 1f)
                    }
                    
                    // Draw a colored rectangle
                    rectFilled(Vector2f(100f, 100f), Vector2f(200f, 150f), ImGuiColors.withAlpha(ImGuiColors.BLUE, 128))
                    
                    // Draw circle
                    circleFilled(Vector2f(300f, 300f), 30f, ImGuiColors.GREEN)

//                    val v = WorldToScreen.getEstimatedTileCenter(localPlayer.graphNode.tileFine)
//                    if (v != null)
//                        circle(v, 12f, ImGuiColors.CYAN, 16, 2f)

                    tile(localPlayer.tile, ImGuiColors.CYAN, localPlayer.graphNode.tileFine.z)

                    // Draw some text
                    text(Vector2f(10f, 10f), ImGuiColors.WHITE, "Background DrawList Test!")
                    text(Vector2f(10f, 30f), ImGuiColors.YELLOW, "Grid: 50px spacing")
                    text(Vector2f(10f, 50f), ImGuiColors.ORANGE, "Script Status: RUNNING")


                }
            }
        }
    }

    override suspend fun loop() {
        delay(1000)
    }

    override fun onStop() {
        running.close()
    }
}