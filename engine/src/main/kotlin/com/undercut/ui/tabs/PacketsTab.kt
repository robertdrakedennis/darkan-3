package com.undercut.ui.tabs

import com.undercut.game.net.PacketLogger
import com.undercut.ui.UIState
import com.undercut.ui.backend.dsl.scopes.*
import com.undercut.ui.backend.dsl.utils.ImGuiChildFlags
import com.undercut.ui.backend.dsl.utils.ImGuiTableColumnFlags
import com.undercut.ui.backend.dsl.utils.ImGuiTableFlags

object PacketsTab {
    private const val MAX_ENTRIES = 2000
    private val entries = mutableListOf<PacketLogger.PacketEntry>()

    fun registerCallback() {
        PacketLogger.onPacketLogged = { entry ->
            if (UIState.packetCaptureEnabled.value) {
                addEntry(entry)
            }
        }
    }

    private fun addEntry(entry: PacketLogger.PacketEntry) {
        synchronized(entries) {
            entries.add(entry)
            while (entries.size > MAX_ENTRIES) entries.removeAt(0)
        }
    }

    fun ChildScope.render() {
        checkbox("Capture", UIState.packetCaptureEnabled)
        sameLine()
        checkbox("Auto-scroll", UIState.packetAutoScroll)
        sameLine()
        button("Clear") {
            synchronized(entries) { entries.clear() }
        }

        combo("Direction", UIState.packetDirFilter, listOf("All", "Server", "Client"))
        inputText("Search", UIState.packetSearchText)

        separator()

        val snapshot = synchronized(entries) { entries.toList() }
        val dirFilter = UIState.packetDirFilter.value
        val searchText = UIState.packetSearchText.value.lowercase()

        val filtered = snapshot.filter { entry ->
            when (dirFilter) {
                1 -> entry.direction == 'S'
                2 -> entry.direction == 'C'
                else -> true
            } && (searchText.isEmpty() ||
                entry.name.lowercase().contains(searchText) ||
                entry.opcode.toString().contains(searchText) ||
                entry.decoded?.lowercase()?.contains(searchText) == true ||
                entry.category?.lowercase()?.contains(searchText) == true)
        }

        text("${filtered.size} packets (${snapshot.size} total)")

        child(id = "PacketScrollRegion", height = -1f, childFlags = ImGuiChildFlags.Borders) {
            table(
                id = "PacketsTable",
                columns = 6,
                flags = ImGuiTableFlags.SizingStretchSame or ImGuiTableFlags.BordersInner
            ) {
                setupColumn("Time", flags = ImGuiTableColumnFlags.WidthFixed)
                setupColumn("Dir", flags = ImGuiTableColumnFlags.WidthFixed)
                setupColumn("Name", flags = ImGuiTableColumnFlags.WidthFixed)
                setupColumn("Op", flags = ImGuiTableColumnFlags.WidthFixed)
                setupColumn("Size", flags = ImGuiTableColumnFlags.WidthFixed)
                setupColumn("Decoded")
                headersRow()

                filtered.forEach { entry ->
                    nextRow()
                    nextColumn(); text(entry.time)
                    nextColumn(); text("[${entry.direction}]")
                    nextColumn(); text(entry.name)
                    nextColumn(); text(entry.opcode.toString())
                    nextColumn(); text(entry.size.toString())
                    nextColumn(); text(entry.decoded ?: entry.hex)
                }
            }

            if (UIState.packetAutoScroll.value) {
                if (getScrollY() >= getScrollMaxY()) {
                    setScrollHereY(1.0f)
                }
            }
        }
    }
}
