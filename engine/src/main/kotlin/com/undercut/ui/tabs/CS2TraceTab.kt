package com.undercut.ui.tabs

import com.undercut.game.cs2.CS2Trace
import com.undercut.ui.UIState
import com.undercut.ui.backend.dsl.scopes.*
import com.undercut.ui.backend.dsl.utils.ImGuiTableFlags

object CS2TraceTab {
    fun ChildScope.render() {
        text("CS2 Script Interceptor")
        separator()

        checkbox("Intercept CS2 calls", UIState.cs2TraceEnabled.value) { on ->
            UIState.cs2TraceEnabled.value = on
            CS2Trace.enabled = on
        }
        sameLine()
        button("Clear") { CS2Trace.clear() }
        sameLine()
        checkbox("Show args", UIState.cs2TraceShowArgs)

        inputInt("Filter script id (-1 = all)", UIState.cs2TraceFilterId)
        inputText("Search (id / value)", UIState.cs2TraceSearch)

        separator()
        text("Blacklisted (hidden, click to un-hide):")
        val blacklisted = CS2Trace.blacklistedIds()
        if (blacklisted.isEmpty()) {
            sameLine(); text("none")
        } else {
            blacklisted.forEach { id ->
                sameLine()
                button("$id##unblk") { CS2Trace.removeBlacklist(id) }
            }
        }
        text("Tip: click a Script id in the table to blacklist it.")
        separator()

        val all = CS2Trace.snapshot()
        val filterId = UIState.cs2TraceFilterId.value
        val search = UIState.cs2TraceSearch.value
        val showArgs = UIState.cs2TraceShowArgs.value
        val entries = all.asReversed()
            .filter { filterId < 0 || it.scriptId == filterId }
            .filter { search.isEmpty() || matches(it, search) }
            .take(500)

        text("Showing ${entries.size} of ${all.size} captured" + if (!CS2Trace.enabled) "  (interception OFF)" else "")

        table(
            id = "CS2TraceTable",
            columns = if (showArgs) 4 else 3,
            flags = ImGuiTableFlags.SizingStretchSame or ImGuiTableFlags.BordersInnerH
        ) {
            setupColumn("#")
            setupColumn("Script")
            if (showArgs) setupColumn("Args")
            setupColumn("Returns")
            headersRow()

            entries.forEach { e ->
                nextRow()
                nextColumn(); text(e.seq.toString())
                nextColumn(); button("${e.scriptId}##blk${e.seq}") { CS2Trace.addBlacklist(e.scriptId) }
                if (showArgs) { nextColumn(); text(stackText(e.argInts, e.argLongs, e.argStrings)) }
                nextColumn(); text(stackText(e.retInts, e.retLongs, e.retStrings))
            }
        }
    }

    private fun matches(e: CS2Trace.Entry, q: String): Boolean =
        e.scriptId.toString().contains(q) ||
            stackText(e.argInts, e.argLongs, e.argStrings).contains(q, ignoreCase = true) ||
            stackText(e.retInts, e.retLongs, e.retStrings).contains(q, ignoreCase = true)

    private fun stackText(ints: IntArray, longs: LongArray, strings: List<String>): String {
        val sb = StringBuilder()
        if (ints.isNotEmpty()) sb.append("i:").append(ints.joinToString(",")).append(' ')
        if (longs.isNotEmpty()) sb.append("l:").append(longs.joinToString(",")).append(' ')
        if (strings.isNotEmpty()) sb.append("s:[").append(strings.joinToString("|")).append(']')
        return sb.toString().trim().ifEmpty { "-" }
    }
}
