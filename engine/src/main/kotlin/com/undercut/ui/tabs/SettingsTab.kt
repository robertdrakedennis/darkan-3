package com.undercut.ui.tabs

import com.undercut.game.hooks.impl.SDLKeycode
import com.undercut.mcp.McpServer
import com.undercut.profiling.JfrManager
import com.undercut.ui.UI
import com.undercut.ui.UIState
import com.undercut.ui.backend.dsl.scopes.*
import com.undercut.ui.backend.dsl.utils.ImGuiTableFlags
import com.undercut.util.Configuration
import com.undercut.util.DiscordWebhook

object SettingsTab {
    fun ChildScope.render() {
        text("Settings")
        separator()

        text("UI:")
        // Build list of SDL keys to choose from
        val keyItems = SDLKeycode.entries.map { it.name }
        val currentKeyIndex = SDLKeycode.entries.indexOfFirst { it.value == UIState.uiToggleKey.value }.let { if (it >= 0) it else 0 }
        combo(
            label = "Main UI Toggle Key",
            currentIndex = currentKeyIndex,
            items = keyItems,
            maxItemsShown = 20
        ) { newIndex ->
            val selected = SDLKeycode.entries[newIndex]
            UIState.uiToggleKey.value = selected.value
            // Persist to ~/.undercut/config
            val newConfig = Configuration.config.copy(uiToggleKey = selected.value)
            Configuration.updateConfig(newConfig)
        }

        separator()

        text("MCP Server (LLM agent access):")
        reconcileMcpServerState()
        checkbox("Enable MCP server", UIState.mcpEnabled)
        text("Status: ${McpServer.state}")
        val s = McpServer.state
        if (s is McpServer.State.Listening) {
            text("Listening at: 127.0.0.1:${s.port} (localhost only)")
        }

        separator()

        text("Debug Options:")
        checkbox("Varp Debug", UIState.varpDebugEnabled)
        checkbox("Varc Debug", UIState.varcDebugEnabled)

        separator()

        text("Packet Dump:")
        checkbox("Raw login/RSA dump", UIState.rawLoginDumpEnabled)
        text("Captures the raw handshake bytes (RSA block + server preamble) during login,")
        text("which the decoded packet hooks can't see. Enable before logging in.")
        text("Decoded packets: Packets tab. File: ~/.undercut/logs/packets-<date>.log")

        separator()

        text("Discord Notifications:")
        checkbox("Enable Discord Notifications", UIState.discordEnabled)

        if (UIState.discordEnabled.value) {
            text("Webhook URL:")
            inputText("##webhook", UIState.discordWebhookUrl)
            button("Save Webhook") {
                val newConfig = Configuration.config.copy(
                    discordWebhookUrl = UIState.discordWebhookUrl.value,
                    discordEnabled = UIState.discordEnabled.value
                )
                Configuration.updateConfig(newConfig)
            }

            text("Bot Username:")
            inputText("##username", UIState.discordUsername)
            button("Save Username") {
                val newConfig = Configuration.config.copy(
                    discordUsername = UIState.discordUsername.value
                )
                Configuration.updateConfig(newConfig)
            }

            button("Test Discord") {
                DiscordWebhook.sendDiscordNotification(
                    "Test Notification",
                    "This is a test notification from Undercut ImGui!"
                )
            }
        }

        separator()

        sameLine()
        button("Close All Config Windows") {
            UIState.openConfigWindows.values.forEach { windowState ->
                windowState.value = false
            }
        }

        separator()

        // JFR Controls
        text("Java Flight Recorder:")
        val running = JfrManager.isRunning()
        if (!running) {
            button("Start JFR") {
                val ok = JfrManager.start()
                if (!ok) {
                    text("Failed to start JFR (already running or unsupported)")
                }
            }
        } else {
            sameLine()
            button("Stop && Dump") {
                val path = JfrManager.stopAndDump()
                if (path != null) {
                    text("Dumped to: $path")
                } else {
                    text("Failed to stop/dump JFR")
                }
            }
        }
        val last = JfrManager.getLastDumpPath()
        if (last != null) {
            text("Last JFR: $last")
        }

        if (UIState.varcDebugEnabled.value || UIState.varpDebugEnabled.value) {
            text("Variable Changes:")
            inputText("Search", UIState.varcSearchText)
            sameLine()
            button("Clear Table") {
                UIState.varTableData.clear()
            }

            table(
                id = "VarcTable",
                columns = 4,
                flags = ImGuiTableFlags.SizingStretchSame or ImGuiTableFlags.BordersInnerH
            ) {
                setupColumn("Type")
                setupColumn("ID")
                setupColumn("Previous")
                setupColumn("New")
                headersRow()

                val filteredData = if (UIState.varcSearchText.value.isEmpty()) {
                    UIState.varTableData
                } else {
                    UIState.varTableData.filter { entry ->
                        entry.type.contains(UIState.varcSearchText.value, ignoreCase = true) ||
                                entry.id.toString().contains(UIState.varcSearchText.value)
                    }
                }

                filteredData.forEach { entry ->
                    nextRow()
                    nextColumn()
                    text(entry.type)
                    nextColumn()
                    text(entry.id.toString())
                    nextColumn()
                    text(entry.prevValue.toString())
                    nextColumn()
                    text(entry.newValue.toString())
                }
            }
        }
    }

    fun addVarTableEntry(type: String, id: Int, prevValue: Int, newValue: Int) {
        if (prevValue == newValue) return

        val entry = UI.VarcEntry(type, id, prevValue, newValue)

        val existingIndex = UIState.varTableData.indexOfFirst { it.type == type && it.id == id }
        if (existingIndex != -1) {
            UIState.varTableData[existingIndex] = entry
        } else {
            UIState.varTableData.add(0, entry)
            if (UIState.varTableData.size > UIState.maxVarcEntries) {
                UIState.varTableData.removeAt(UIState.varTableData.size - 1)
            }
        }
    }

    fun wantsVarcDebug(): Boolean = UIState.varcDebugEnabled.value
    fun wantsVarpDebug(): Boolean = UIState.varpDebugEnabled.value

    private fun reconcileMcpServerState() {
        val desired = UIState.mcpEnabled.value
        when (val s = McpServer.state) {
            is McpServer.State.Listening -> if (!desired) McpServer.stop()
            McpServer.State.Disabled -> if (desired) {
                McpServer.start().onFailure {
                    UIState.mcpEnabled.value = false
                }
            }
            is McpServer.State.Error -> if (!desired) {
                // user unchecked after a failed start; clear the error by stopping (no-op) and resetting
            } else {
                // try again on next user interaction; auto-retry would spam
            }
            McpServer.State.Starting, McpServer.State.Stopping -> {
                // wait for transition to settle
            }
        }
    }
}
