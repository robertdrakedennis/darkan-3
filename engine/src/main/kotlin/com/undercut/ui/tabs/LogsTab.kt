package com.undercut.ui.tabs

import com.undercut.ui.UIState
import com.undercut.ui.backend.dsl.scopes.*
import com.undercut.ui.backend.dsl.utils.ImGuiChildFlags
import java.io.File
import java.io.RandomAccessFile

object LogsTab {
    fun ChildScope.render() {
        text("Log Output (Last ${UIState.maxLogLines} lines):")

        child(id = "LogScrollRegion", height = -40f, childFlags = ImGuiChildFlags.Borders) {
            val linesSnapshot = UIState.logLines.toList()
            linesSnapshot.forEach { line ->
                textWrapped(line)
            }
            if (getScrollY() >= getScrollMaxY()) {
                setScrollHereY(1.0f)
            }
        }

        button("Clear Logs") { UIState.logLines.clear() }
        sameLine()
        button("Refresh") { updateLogLines() }
    }

    fun updateLogLines() {
        if (UIState.logFile == null || !UIState.logFile!!.exists()) return

        val modifiedTime = UIState.logFile!!.lastModified()
        if (modifiedTime > UIState.lastLogModifiedTime) {
            UIState.lastLogModifiedTime = modifiedTime
            val lines = readLastLines(UIState.logFile!!, UIState.maxLogLines)
            UIState.logLines.clear()
            UIState.logLines.addAll(lines)
        }
    }

    private fun readLastLines(file: File, count: Int): List<String> {
        val result = mutableListOf<String>()
        RandomAccessFile(file, "r").use { raf ->
            var filePointer = raf.length() - 1
            var line = ""
            while (filePointer >= 0 && result.size < count) {
                raf.seek(filePointer)
                val readByte = raf.readByte()
                if (readByte.toInt().toChar() == '\n' && line.isNotEmpty()) {
                    result.add(line.reversed())
                    line = ""
                } else {
                    line += readByte.toInt().toChar()
                }
                filePointer--
            }
            if (line.isNotEmpty() && result.size < count) {
                result.add(line.reversed())
            }
        }
        return result.reversed()
    }
}
