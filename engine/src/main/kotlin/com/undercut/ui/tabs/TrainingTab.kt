package com.undercut.ui.tabs

import com.undercut.game.bootstrap.Bootstrap
import com.undercut.game.input.InputRecorder
import com.undercut.game.input.MouseEventBuffer
import com.undercut.game.input.ShadowClickMode
import com.undercut.game.input.ShadowInputBus
import com.undercut.game.input.SyntheticInputShadow
import com.undercut.game.nxt.MainState
import com.undercut.profiling.PlayerProfiles
import com.undercut.ui.backend.dsl.scopes.*
import java.io.File

object TrainingTab {
    private val clickModes = ShadowClickMode.entries.map { it.name }

    fun ChildScope.render() {
        try {
            renderInner()
        } catch (e: Throwable) {
            text("Training tab error: ${e.message}")
        }
    }

    private fun ChildScope.renderInner() {
        val playerName = resolveCurrentPlayer()
        val loggedIn = playerName.isNotBlank()

        text("Player Profile: ${playerName.ifBlank { "(not logged in)" }}")
        separator()

        spacing()

        text("Input Recording")
        separator()

        if (!InputRecorder.isRecording) {
            if (loggedIn) {
                button("Start Recording") {
                    InputRecorder.startRecording()
                }
                spacing()
                text("Training set: ~/.undercut/training/$playerName/$playerName.bin")
            } else {
                text("Log in to record input.")
            }
        } else {
            text("Player: ${InputRecorder.playerName}")
            text("Events: ${InputRecorder.eventCount}")
            text("File Size: ${InputRecorder.fileSizeBytes / 1024} KB")
            val elapsed = InputRecorder.sessionDurationMs / 1000
            text("Duration: ${elapsed / 60}m ${elapsed % 60}s")
            button("Stop Recording") {
                InputRecorder.stopRecording()
            }
        }

        spacing()
        spacing()

        text("Synthetic Input")
        separator()

        if (!loggedIn) {
            text("Log in to configure synthetic input.")
        } else {
            val profile = PlayerProfiles.getByName(playerName)

            checkbox("Enabled", profile.synthInputEnabled) { profile.synthInputEnabled = it }
            checkbox("Shadow doActions", profile.synthShadowDoActions) { profile.synthShadowDoActions = it }
            checkbox("Idle filler", profile.synthIdleFiller) { profile.synthIdleFiller = it }
            checkbox("Show visualizer", profile.synthVisualizerEnabled) { profile.synthVisualizerEnabled = it }

            val selectedClickIdx = ShadowClickMode.entries.indexOf(profile.synthShadowClickMode)
            combo("Click mode", selectedClickIdx, clickModes) { idx ->
                profile.synthShadowClickMode = ShadowClickMode.entries[idx]
            }

            inputText("Model player", profile.synthModelPlayer, maxLength = 64) {
                profile.synthModelPlayer = it.trim()
            }

            spacing()
            val modelKey = profile.synthModelPlayer.ifBlank { playerName }
            val modelFile = File(System.getProperty("user.home"), ".undercut/models/$modelKey/input_predictor.onnx")
            text("Model: ${if (modelFile.exists()) "${modelFile.length() / 1024} KB" else "not trained"}")
            text("Loaded: ${if (SyntheticInputShadow.isModelLoaded()) SyntheticInputShadow.currentModelKey else "(none)"}")
            text("Entries generated: ${SyntheticInputShadow.entriesGenerated}")
            text("Last synth XY: %.0f, %.0f".format(SyntheticInputShadow.lastSynthX, SyntheticInputShadow.lastSynthY))
            text("Queue: ${ShadowInputBus.size()}")
            text("Buffer head/tail: ${MouseEventBuffer.head()} / ${MouseEventBuffer.tail()} (${MouseEventBuffer.pendingCount()} pending)")
            text("Last inference: %.2f ms".format(SyntheticInputShadow.lastInferenceMillis))
            text("Last confidence: %.2f".format(SyntheticInputShadow.lastConfidence))
        }

        spacing()
        spacing()

        if (loggedIn) {
            text("Training Data")
            separator()
            val trainFile = File(System.getProperty("user.home"), ".undercut/training/$playerName/$playerName.bin")
            if (trainFile.exists()) {
                text("Training set: ${trainFile.length() / 1024} KB")
            } else {
                text("No recordings yet.")
            }
        }
    }

    private fun resolveCurrentPlayer(): String {
        return try {
            if (Bootstrap.client.mainState == MainState.LOGGED_IN) {
                Bootstrap.client.loggedInPlayer.getPlayerName() ?: ""
            } else ""
        } catch (_: Throwable) { "" }
    }
}
