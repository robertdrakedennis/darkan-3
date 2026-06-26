package com.undercut.ui.tabs

import com.undercut.game.bootstrap.Bootstrap
import com.undercut.game.nxt.MainState
import com.undercut.game.nxt.interfaces.InterfaceComponent
import com.undercut.script.api.interfaces
import com.undercut.ui.UIState
import com.undercut.ui.backend.dsl.scopes.*
import world.gregs.voidps.gameval.Gameval
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import kotlin.math.min

object InterfaceDebugTab {

    private fun copyToClipboard(text: String) {
        try {
            val selection = StringSelection(text)
            Toolkit.getDefaultToolkit().systemClipboard.setContents(selection, selection)
        } catch (_: Throwable) {
        }
    }

    private fun isClientReady(): Boolean {
        return try {
            Bootstrap.client.mainState == MainState.LOGGED_IN
        } catch (_: Throwable) {
            false
        }
    }

    fun ChildScope.render() {
        if (!isClientReady()) {
            text("Not logged in - Interface Debug requires login")
            return
        }

        // Row 1: Interface and Component ID
        text("Interface ID:")
        sameLine()
        pushItemWidth(150f)
        inputInt("##ifid", UIState.interfaceDebugInterfaceId)
        popItemWidth()
        sameLine()
        text("Component ID:")
        sameLine()
        pushItemWidth(150f)
        inputInt("##compid", UIState.interfaceDebugComponentId)
        popItemWidth()

        // Row 2: Search filters
        text("Text Search:")
        sameLine()
        pushItemWidth(200f)
        inputText("##textsearch", UIState.interfaceDebugTextFilter)
        popItemWidth()
        sameLine()
        text("Item ID:")
        sameLine()
        pushItemWidth(100f)
        inputInt("##itemfilter", UIState.interfaceDebugItemIdFilter)
        popItemWidth()

        spacing()

        // Action buttons row
        button("Scan Interfaces") { scanOpenInterfaces() }
        sameLine()
        button("Search Text") { searchTextInSelected() }
        sameLine()
        button("Clear All") {
            UIState.interfaceDebugOpenInterfaces.clear()
            UIState.interfaceDebugFoundComponents.clear()
        }

        separator()
        spacing()

        // Main content - three panel layout
        columns(3, id = "MainLayout", border = true)
        setColumnWidth(0, 180f)
        setColumnWidth(1, 320f)

        // Panel 1: Open Interfaces
        child("InterfaceList", height = 550f) {
            text("Interfaces (${UIState.interfaceDebugOpenInterfaces.size})")
            separator()

            UIState.interfaceDebugOpenInterfaces.sorted().forEach { id ->
                val selected = UIState.interfaceDebugInterfaceId.value == id
                val shown = Gameval.interfaceLabel(id)
                val label = if (selected) ">> $shown ##if$id" else "$shown##if$id"
                button(label) {
                    UIState.interfaceDebugInterfaceId.value = id
                    UIState.interfaceDebugComponentId.value = -1
                }
            }
        }

        nextColumn()

        // Panel 2: Component list — the interface's flat component vector, indexed by id. This is
        // the canonical access (InterfaceParent at +0x8, the same path getComponent uses); there is
        // no per-component child vector to recurse.
        child("ComponentList", height = 550f) {
            val ifId = UIState.interfaceDebugInterfaceId.value

            if (UIState.interfaceDebugFoundComponents.isNotEmpty()) {
                text("Found (${UIState.interfaceDebugFoundComponents.size})")
                separator()
                UIState.interfaceDebugFoundComponents.forEach { cid ->
                    componentButton(ifId, cid)
                    sameLine()
                }
                newLine()
                separator()
            }

            if (ifId < 0) {
                textWrapped("Select an interface or enter IF ID")
                return@child
            }

            val parent = try { interfaces[ifId] } catch (_: Throwable) { null }
            if (parent == null) {
                text("Interface not open")
                return@child
            }

            val count = parent.size
            text("Components (${Gameval.interfaceLabel(ifId)}): $count")
            separator()
            for (cid in 0 until min(count, MAX_COMPONENTS_SHOWN)) {
                val comp = try { parent[cid] } catch (_: Throwable) { null } ?: continue
                componentRow(cid, comp)
            }
            if (count > MAX_COMPONENTS_SHOWN) text("... +${count - MAX_COMPONENTS_SHOWN} more")
        }

        nextColumn()

        // Panel 3: Component Details
        child("Details", height = 550f) {
            val ifId = UIState.interfaceDebugInterfaceId.value
            val compId = UIState.interfaceDebugComponentId.value

            if (ifId < 0) {
                text("No interface selected")
                return@child
            }

            val comp = try { interfaces.getComponent(ifId, compId) } catch (_: Throwable) { null }
            if (comp == null) {
                text("Component not found")
                text("IF: $ifId, Comp: $compId")
                return@child
            }

            renderComponentDetails(comp)
        }

        columns(1)
    }

    private fun ChildScope.componentRow(cid: Int, comp: InterfaceComponent) {
        val type = try { comp.type } catch (_: Throwable) { -1 }
        val txt = try { comp.text.take(20) } catch (_: Throwable) { "" }
        val itemId = try { comp.itemId } catch (_: Throwable) { 0 }
        val selected = UIState.interfaceDebugComponentId.value == cid
        val compName = Gameval.component(comp.interfaceId, cid)?.substringAfter(':')
        val label = buildString {
            if (selected) append(">> ")
            if (compName != null) append("$cid \"$compName\"") else append("$cid")
            append(" [${getTypeName(type)}]")
            if (txt.isNotBlank()) append(": \"$txt\"")
            if (itemId > 0) append(" [i:$itemId]")
        }
        button("$label##row$cid") { UIState.interfaceDebugComponentId.value = cid }
    }

    private fun ChildScope.componentButton(ifId: Int, cid: Int) {
        val sel = UIState.interfaceDebugComponentId.value == cid
        val lbl = if (sel) ">> $cid ##f$cid" else "$cid##f$cid"
        button(lbl) { UIState.interfaceDebugComponentId.value = cid }
    }

    private fun ChildScope.renderComponentDetails(comp: InterfaceComponent) {
        try {
            text("Component: ${Gameval.componentLabel(comp.interfaceId, comp.componentId)}")
            button("Copy ID") { copyToClipboard("${comp.interfaceId}:${comp.componentId}") }

            separator()

            columns(2, id = "DetailsTable", border = false)
            setColumnWidth(0, 120f)

            text("Type"); nextColumn(); text("${comp.type} (${getTypeName(comp.type)})"); nextColumn()
            text("Slot ID"); nextColumn(); text("${comp.slotId}"); nextColumn()
            text("Position"); nextColumn(); text("${comp.parentRelX}, ${comp.parentRelY}"); nextColumn()
            text("Size"); nextColumn(); text("${comp.screenWidth} x ${comp.screenHeight}"); nextColumn()
            text("Visible"); nextColumn(); text("${comp.visible}"); nextColumn()

            val spriteId = comp.spriteId
            val itemId = comp.itemId
            val stackSize = comp.stackSize

            if (spriteId != 0) {
                text("Sprite"); nextColumn(); text("$spriteId"); nextColumn()
            }
            if (itemId != 0) {
                text("Item"); nextColumn()
                text("${Gameval.objLabel(itemId)} x$stackSize")
                sameLine()
                button("Copy##cpitem") { copyToClipboard("$itemId") }
                nextColumn()
            }

            columns(1)

            val txt = try { comp.text } catch (_: Throwable) { "" }
            if (txt.isNotBlank()) {
                separator()
                text("Text:")
                textWrapped(txt.take(300))
                if (txt.length > 300) text("... (${txt.length} chars total)")
                button("Copy Text") { copyToClipboard(txt) }
            }

            separator()

            val parentComp = try { comp.parent } catch (_: Throwable) { null }
            if (parentComp != null) {
                button("Go to Parent (${parentComp.componentId})") {
                    UIState.interfaceDebugComponentId.value = parentComp.componentId
                }
            }

            // Dynamic slot children (e.g. inventory/bank item slots) — the one real per-component
            // child vector (SLOT_CHILDREN @ +0x1A8). Static sub-components are siblings in the
            // interface's flat vector (Panel 2), not nested here.
            val slotChildren = try { comp.slotChildren } catch (_: Throwable) { emptyList() }
            text("Slots: ${slotChildren.size}")
            if (slotChildren.isNotEmpty()) {
                slotChildren.take(20).forEach { sc ->
                    val scid = try { sc.componentId } catch (_: Throwable) { return@forEach }
                    button("[S]$scid##slot$scid") { UIState.interfaceDebugComponentId.value = scid }
                    sameLine()
                }
                newLine()
            }

        } catch (e: Throwable) {
            text("Error: ${e.message}")
        }
    }

    private fun getTypeName(type: Int): String {
        return when (type) {
            0 -> "Layer"
            3 -> "Rectangle"
            4 -> "Text"
            5 -> "Sprite"
            6 -> "Model"
            9 -> "Line"
            else -> "Type$type"
        }
    }

    private fun scanOpenInterfaces() {
        UIState.interfaceDebugOpenInterfaces.clear()
        try {
            val max = UIState.interfaceDebugInterfaceScanMax.value
            val size = try { interfaces.size.toInt() } catch (_: Throwable) { 0 }
            for (id in 0..<min(max, size)) {
                try {
                    if (interfaces.isOpen(id)) {
                        UIState.interfaceDebugOpenInterfaces.add(id)
                    }
                } catch (_: Throwable) {}
            }
        } catch (_: Throwable) {}
    }

    private fun searchTextInSelected() {
        UIState.interfaceDebugFoundComponents.clear()
        val ifId = UIState.interfaceDebugInterfaceId.value
        if (ifId < 0) return

        val textFilter = UIState.interfaceDebugTextFilter.value.trim()
        val itemIdFilter = UIState.interfaceDebugItemIdFilter.value
        if (textFilter.isEmpty() && itemIdFilter < 0) return

        val parent = try { interfaces[ifId] } catch (_: Throwable) { null } ?: return
        val count = parent.size

        for (cid in 0 until count) {
            try {
                val comp = parent[cid] ?: continue
                if (matchesFilter(comp, textFilter, itemIdFilter)) {
                    UIState.interfaceDebugFoundComponents.add(cid)
                }
            } catch (_: Throwable) {}
        }
    }

    private fun matchesFilter(comp: InterfaceComponent, text: String, itemId: Int): Boolean {
        if (itemId >= 0 && comp.itemId == itemId) return true
        if (text.isNotEmpty()) {
            val t = try { comp.text } catch (_: Throwable) { "" }
            if (t.contains(text, ignoreCase = true)) return true
        }
        return false
    }

    private const val MAX_COMPONENTS_SHOWN = 1000
}
