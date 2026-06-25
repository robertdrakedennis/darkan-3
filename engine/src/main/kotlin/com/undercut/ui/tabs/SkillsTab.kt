package com.undercut.ui.tabs

import com.undercut.game.Skill
import com.undercut.game.invention.InventionXpTracker
import com.undercut.ui.UIState
import com.undercut.ui.backend.dsl.scopes.ChildScope
import com.undercut.ui.backend.dsl.scopes.checkbox
import com.undercut.ui.backend.dsl.scopes.separator
import com.undercut.ui.backend.dsl.scopes.smallButton
import com.undercut.ui.backend.dsl.scopes.spacing
import com.undercut.ui.backend.dsl.scopes.table
import com.undercut.ui.backend.dsl.scopes.text
import com.undercut.ui.backend.dsl.utils.ImGuiTableColumnFlags
import com.undercut.ui.backend.dsl.utils.ImGuiTableFlags
import com.undercut.util.format
import com.undercut.util.getFormattedUnitsPerHour

object SkillsTab {
    fun ChildScope.render() {
        text("XP Tracking:")
        separator()

        if (UIState.xpData.isEmpty()) {
            text("No skills tracked yet.")
        } else {
            table(id = "XPTable", columns = 4, flags = ImGuiTableFlags.SizingFixedFit) {
                setupColumn("Skill", ImGuiTableColumnFlags.WidthFixed, 100f)
                setupColumn("XP Gained", ImGuiTableColumnFlags.WidthFixed, 100f)
                setupColumn("XP/Hour", ImGuiTableColumnFlags.WidthFixed, 100f)
                setupColumn("Actions", ImGuiTableColumnFlags.WidthFixed, 150f)
                headersRow()

                UIState.xpData.toList().forEach { (skill, data) ->
                    nextRow()
                    nextColumn()
                    text(skill.name)
                    nextColumn()
                    text(format(data.second))
                    nextColumn()
                    text(getFormattedUnitsPerHour(data.second, data.first))
                    nextColumn()
                    smallButton("Reset##$skill") {
                        UIState.xpData[skill] = System.currentTimeMillis() to 0
                    }
                    sameLine()
                    smallButton("Remove##$skill") {
                        UIState.xpData.remove(skill)
                    }
                }
            }
        }

        renderInventionCalculator()
    }

    private fun ChildScope.renderInventionCalculator() {
        spacing()
        separator()
        text("Invention — effective XP/hr from augmented item XP")
        checkbox("Track augmented item XP", UIState.inventionXpTrackerEnabled)
        if (!UIState.inventionXpTrackerEnabled.value) {
            text("Enable to periodically scan augmented items and estimate Invention XP/hr.")
            return
        }

        val tracker = InventionXpTracker
        text("Assuming level-${tracker.assumedSiphonLevel} siphons (Invention ${tracker.inventionLevel})")

        val rows = tracker.rows
        if (rows.isEmpty()) {
            text("No augmented items gaining XP detected — train with augmented gear equipped/carried.")
            return
        }

        text(
            "Total: ${format(tracker.totalEffectiveInvXpPerHour)} Inv XP/hr" +
                "  •  item ${format(tracker.totalItemXpPerHour)} XP/hr"
        )

        table(id = "InventionItemXpTable", columns = 6, flags = ImGuiTableFlags.SizingFixedFit) {
            setupColumn("Item", ImGuiTableColumnFlags.WidthFixed, 170f)
            setupColumn("Tier", ImGuiTableColumnFlags.WidthFixed, 45f)
            setupColumn("Lvl", ImGuiTableColumnFlags.WidthFixed, 40f)
            setupColumn("Item XP", ImGuiTableColumnFlags.WidthFixed, 90f)
            setupColumn("Item XP/hr", ImGuiTableColumnFlags.WidthFixed, 90f)
            setupColumn("Eff. Inv XP/hr", ImGuiTableColumnFlags.WidthFixed, 110f)
            headersRow()

            rows.forEach { row ->
                nextRow()
                nextColumn()
                text(row.name)
                nextColumn()
                text(if (row.tierKnown) "${row.tier}" else "${row.tier}?")
                nextColumn()
                text("${row.itemLevel}")
                nextColumn()
                text(format(row.itemXp))
                nextColumn()
                text(format(row.itemXpPerHour))
                nextColumn()
                text(format(row.effectiveInvXpPerHour))
            }
        }

        smallButton("Reset##InventionItemXp") { tracker.reset() }
    }

    fun updateXpTable(skill: Skill, xpGained: Int) {
        val totalXp = UIState.xpData.getOrDefault(skill, System.currentTimeMillis() to 0)
        UIState.xpData[skill] = totalXp.first to totalXp.second + xpGained
    }
}
