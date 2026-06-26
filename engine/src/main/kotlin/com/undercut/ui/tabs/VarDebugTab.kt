package com.undercut.ui.tabs

import com.undercut.script.api.varcs
import com.undercut.script.api.varps
import com.undercut.ui.UIState
import com.undercut.ui.backend.dsl.scopes.*
import com.undercut.ui.backend.dsl.utils.ImGuiTableFlags
import world.gregs.voidps.gameval.Gameval

object VarDebugTab {
    fun ChildScope.render() {
        text("Var Debug")
        separator()

        text("New Watch")

        combo(
            label = "Domain",
            currentItem = UIState.varDebugDomainIndex,
            items = listOf("Varp", "Varc"),
            maxItemsShown = 2
        )

        combo(
            label = "Read",
            currentItem = UIState.varDebugReadModeIndex,
            items = listOf("getVar", "getVarbit"),
            maxItemsShown = 2
        )

        inputInt("ID", UIState.varDebugId)

        checkbox("Live", UIState.varDebugLive.value) { newValue ->
            UIState.varDebugLive.value = newValue
            if (!newValue) {
                UIState.varDebugCachedValue.value = readValue(
                    domainIndex = UIState.varDebugDomainIndex.value,
                    readModeIndex = UIState.varDebugReadModeIndex.value,
                    id = UIState.varDebugId.value
                )
            }
        }
        if (!UIState.varDebugLive.value) {
            sameLine()
            button("Refresh") {
                UIState.varDebugCachedValue.value = readValue(
                    domainIndex = UIState.varDebugDomainIndex.value,
                    readModeIndex = UIState.varDebugReadModeIndex.value,
                    id = UIState.varDebugId.value
                )
            }
        }

        val value = if (UIState.varDebugLive.value) {
            readValue(
                domainIndex = UIState.varDebugDomainIndex.value,
                readModeIndex = UIState.varDebugReadModeIndex.value,
                id = UIState.varDebugId.value
            )
        } else {
            UIState.varDebugCachedValue.value
        }

        separator()
        text("Value: $value")

        sameLine()
        button("Add") {
            val live = UIState.varDebugLive.value
            val cachedValue = if (live) {
                0
            } else {
                readValue(
                    domainIndex = UIState.varDebugDomainIndex.value,
                    readModeIndex = UIState.varDebugReadModeIndex.value,
                    id = UIState.varDebugId.value
                )
            }

            UIState.varDebugWatches.add(
                UIState.VarDebugWatch(
                    domainIndex = UIState.varDebugDomainIndex.value,
                    readModeIndex = UIState.varDebugReadModeIndex.value,
                    id = UIState.varDebugId.value,
                    live = live,
                    cachedValue = cachedValue
                )
            )
        }

        separator()
        text("Watches")
        if (UIState.varDebugWatches.isEmpty()) {
            text("No watches added")
        } else {
            button("Clear Watches") {
                UIState.varDebugWatches.clear()
            }

            table(
                id = "VarWatchTable",
                columns = 6,
                flags = ImGuiTableFlags.SizingStretchSame or ImGuiTableFlags.BordersInnerH
            ) {
                setupColumn("Domain")
                setupColumn("Read")
                setupColumn("ID")
                setupColumn("Live")
                setupColumn("Value")
                setupColumn("Actions")
                headersRow()

                UIState.varDebugWatches.forEachIndexed { index, watch ->
                    nextRow()

                    nextColumn()
                    text(if (watch.domainIndex == 0) "Varp" else "Varc")

                    nextColumn()
                    text(if (watch.readModeIndex == 0) "getVar" else "getVarbit")

                    nextColumn()
                    text(varLabel(watch.domainIndex, watch.readModeIndex, watch.id))

                    nextColumn()
                    text(watch.live.toString())

                    nextColumn()
                    val watchValue = if (watch.live) {
                        readValue(
                            domainIndex = watch.domainIndex,
                            readModeIndex = watch.readModeIndex,
                            id = watch.id
                        )
                    } else {
                        watch.cachedValue
                    }
                    text(watchValue.toString())

                    nextColumn()
                    if (!watch.live) {
                        button("Refresh##watch_$index") {
                            watch.cachedValue = readValue(
                                domainIndex = watch.domainIndex,
                                readModeIndex = watch.readModeIndex,
                                id = watch.id
                            )
                        }
                        sameLine()
                    }
                    button("Remove##watch_$index") {
                        UIState.varDebugWatches.remove(watch)
                    }
                }
            }
        }

        separator()
        text("Change Tracking")
        checkbox("Track changes", UIState.varChangeTrackingEnabled)

        if (UIState.varChangeTrackingEnabled.value) {
            checkbox("varp", UIState.varChangeTrackVarp)
            sameLine()
            checkbox("varpbit", UIState.varChangeTrackVarpbit)
            sameLine()
            checkbox("varc", UIState.varChangeTrackVarc)
            sameLine()
            checkbox("varcbit", UIState.varChangeTrackVarcbit)

            inputText("Search", UIState.varChangeSearchText)
            sameLine()
            button("Clear") {
                UIState.varTableData.clear()
            }

            table(
                id = "VarChangeTable",
                columns = 4,
                flags = ImGuiTableFlags.SizingStretchSame or ImGuiTableFlags.BordersInnerH
            ) {
                setupColumn("Type")
                setupColumn("ID")
                setupColumn("Previous")
                setupColumn("New")
                headersRow()

                val searchQuery = UIState.varChangeSearchText.value
                val hasSearch = searchQuery.isNotEmpty()
                val trackVarp = UIState.varChangeTrackVarp.value
                val trackVarpbit = UIState.varChangeTrackVarpbit.value
                val trackVarc = UIState.varChangeTrackVarc.value
                val trackVarcbit = UIState.varChangeTrackVarcbit.value

                UIState.varTableData.forEach { entry ->
                    val typePass = when (entry.type) {
                        "varp" -> trackVarp
                        "varpbit" -> trackVarpbit
                        "varc" -> trackVarc
                        "varcbit" -> trackVarcbit
                        else -> true
                    }
                    if (!typePass) return@forEach
                    if (hasSearch && !entry.type.contains(searchQuery, ignoreCase = true) &&
                        !entry.id.toString().contains(searchQuery)) return@forEach
                    nextRow()
                    nextColumn()
                    text(entry.type)
                    nextColumn()
                    text(changeVarLabel(entry.type, entry.id))
                    nextColumn()
                    text(entry.prevValue.toString())
                    nextColumn()
                    text(entry.newValue.toString())
                }
            }
        }
    }

    private fun varLabel(domainIndex: Int, readModeIndex: Int, id: Int): String = when {
        domainIndex == 0 && readModeIndex == 1 -> Gameval.varbitLabel(id)
        domainIndex == 0 -> Gameval.varpLabel(id)
        readModeIndex == 1 -> id.toString() // client varbits have no gameval dictionary
        else -> Gameval.varcLabel(id)
    }

    private fun changeVarLabel(type: String, id: Int): String = when (type) {
        "varp" -> Gameval.varpLabel(id)
        "varpbit" -> Gameval.varbitLabel(id)
        "varc" -> Gameval.varcLabel(id)
        else -> id.toString() // varcbit has no gameval dictionary
    }

    private fun readValue(domainIndex: Int, readModeIndex: Int, id: Int): Int {
        if (id < 0) return 0

        return try {
            val useVarps = domainIndex == 0
            val useGetVar = readModeIndex == 0

            if (useVarps) {
                if (useGetVar) varps.getVar(id) else varps.getVarBit(id)
            } else {
                if (useGetVar) varcs.getVar(id) else varcs.getVarBit(id)
            }
        } catch (_: Throwable) {
            0
        }
    }
}
