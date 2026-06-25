package com.undercut.game.input

import com.undercut.game.bootstrap.Bootstrap
import com.undercut.game.memory.NativeAccess
import com.undercut.game.nxt.MainState
import com.undercut.script.api.localPlayer
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.foreign.ValueLayout
import java.util.concurrent.ConcurrentLinkedDeque

/**
 * Records input events and per-tick game context to a compact binary file.
 *
 * File format:
 * - Header: magic("UCUT"), version(u16), epochMs(i64), playerName(len-prefixed UTF-8)
 * - Per-tick frame: marker(0xFF), gameTick(i32), timestampNanos(i64), playerX(i16), playerY(i16), plane(i8), mainState(i8)
 * - Per-event: type(u8), deltaMicros(i32), type-specific payload
 *
 * Thread-safe: hooks call [record] from any thread; [tickFlush] is called from the game tick.
 *
 * Per-player profiles: recordings are saved to ~/.undercut/training/{username}/.
 */
object InputRecorder {
    private val eventBuffer = ConcurrentLinkedDeque<InputEvent>()
    private var outputStream: BufferedOutputStream? = null
    private var sessionStartEpochMs: Long = 0
    private var lastFlushTick: Int = -1
    private var lastRightButtonState: Int = -1

    // Static-data globals — verified unchanged in 948-5 (the .data/.bss segment is fixed across
    // the 948-2-2 -> 948-5 sub-patch; only .text shifted, so OFunctions moved but these did not).
    /** Address of g_rightButtonState global (0x016e7bbe from base). */
    private const val G_RIGHT_BUTTON_STATE_OFFSET = 0x016e7bbeL
    /** Address of g_mouseX global. */
    private const val G_MOUSE_X_OFFSET = 0x016e7bb4L
    /** Address of g_mouseY global. */
    private const val G_MOUSE_Y_OFFSET = 0x016e7bb8L

    @Volatile
    var isRecording: Boolean = false
        private set

    /**
     * When true, events from hooks are suppressed (used during injection to avoid feedback loops).
     */
    @Volatile
    var suppressRecording: Boolean = false

    var playerName: String = ""
        private set

    var eventCount: Long = 0
        private set

    var fileSizeBytes: Long = 0
        private set

    var sessionFile: File? = null
        private set

    var sessionStartMs: Long = 0
        private set

    val sessionDurationMs: Long
        get() = if (isRecording) System.currentTimeMillis() - sessionStartMs else 0

    /**
     * Start recording. Requires the player to be logged in.
     * Appends to (or creates) a training set file named after the player's display name.
     */
    fun startRecording() {
        if (isRecording) return

        playerName = resolvePlayerName()
        if (playerName.isBlank()) {
            println("[InputRecorder] Cannot start recording — not logged in.")
            return
        }

        val dir = File(System.getProperty("user.home"), ".undercut/training/$playerName")
        dir.mkdirs()
        val file = File(dir, "${playerName}.bin")
        sessionFile = file

        val append = file.exists() && file.length() > 0
        outputStream = BufferedOutputStream(FileOutputStream(file, true))
        sessionStartEpochMs = System.currentTimeMillis()
        sessionStartMs = sessionStartEpochMs
        eventCount = 0
        fileSizeBytes = file.length()
        lastFlushTick = -1
        lastRightButtonState = -1
        eventBuffer.clear() // Ensure no stale events from a previous session
        writeHeader() // Always write a session header (reader uses magic to find session boundaries)
        isRecording = true
        println("[InputRecorder] ${if (append) "Appending to" else "Created"} training set: ${file.absolutePath}")
    }

    fun stopRecording() {
        if (!isRecording) return
        isRecording = false
        // Drain any remaining buffered events before closing
        val out = outputStream
        if (out != null) {
            try {
                var event = eventBuffer.poll()
                while (event != null) {
                    writeEvent(out, event)
                    eventCount++
                    event = eventBuffer.poll()
                }
                out.flush()
                out.close()
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
        outputStream = null
        eventBuffer.clear() // Ensure no stale events leak into next session
        fileSizeBytes = sessionFile?.length() ?: 0
        println("[InputRecorder] Stopped recording. Events: $eventCount, Size: ${fileSizeBytes / 1024} KB")
    }

    fun record(event: InputEvent) {
        if (!isRecording || suppressRecording) return
        eventBuffer.add(event)
    }

    /**
     * Called once per game tick from ClientMainLogic.
     * Writes a tick frame header, polls right-click state, then flushes all buffered events.
     */
    fun tickFlush() {
        if (!isRecording) return
        val out = outputStream ?: return

        try {
            val client = Bootstrap.client
            val gameTick = client.clientCycle
            if (gameTick == lastFlushTick) return
            lastFlushTick = gameTick

            // Write tick frame header
            out.write(0xFF) // marker
            writeInt(out, gameTick)
            writeLong(out, System.nanoTime())

            // Player position (best effort)
            val mainState = client.mainState
            if (mainState == MainState.LOGGED_IN) {
                try {
                    val tile = localPlayer.tile
                    writeShort(out, tile.x.toInt())
                    writeShort(out, tile.y.toInt())
                    out.write(tile.plane.toInt())
                } catch (_: Throwable) {
                    writeShort(out, 0)
                    writeShort(out, 0)
                    out.write(0)
                }
            } else {
                writeShort(out, 0)
                writeShort(out, 0)
                out.write(0)
            }
            out.write(mainState)

            // Poll right-click button state from game globals
            pollRightButton(gameTick)

            // Flush buffered events
            var event = eventBuffer.poll()
            while (event != null) {
                writeEvent(out, event)
                eventCount++
                event = eventBuffer.poll()
            }

            out.flush()
            fileSizeBytes = sessionFile?.length() ?: 0
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    /**
     * Poll g_rightButtonState each tick. When it transitions 0→1 or 1→0, synthesize
     * a MouseButtonEvent for the right button using the current g_mouseX/g_mouseY.
     */
    private fun pollRightButton(gameTick: Int) {
        try {
            val base = NativeAccess.BASE_ADDR
            val currentState = base.get(ValueLayout.JAVA_BYTE, G_RIGHT_BUTTON_STATE_OFFSET).toInt() and 0xFF
            if (lastRightButtonState >= 0 && currentState != lastRightButtonState) {
                val mouseX = base.get(ValueLayout.JAVA_INT, G_MOUSE_X_OFFSET)
                val mouseY = base.get(ValueLayout.JAVA_INT, G_MOUSE_Y_OFFSET)
                eventBuffer.add(
                    MouseButtonEvent(
                        timestampNanos = System.nanoTime(),
                        gameTick = gameTick,
                        x = mouseX,
                        y = mouseY,
                        button = MouseButton.RIGHT,
                        pressed = currentState != 0
                    )
                )
            }
            lastRightButtonState = currentState
        } catch (_: Throwable) {
            // Best effort — don't crash recording if globals are unreadable
        }
    }

    private fun resolvePlayerName(): String {
        return try {
            Bootstrap.client.loggedInPlayer.getPlayerName() ?: ""
        } catch (_: Throwable) {
            ""
        }
    }

    private fun writeHeader() {
        val out = outputStream ?: return
        // Magic: "UCUT"
        out.write('U'.code)
        out.write('C'.code)
        out.write('U'.code)
        out.write('T'.code)
        // Version
        writeShort(out, 2)
        // Epoch ms
        writeLong(out, sessionStartEpochMs)
        // Player name (length-prefixed UTF-8)
        val nameBytes = playerName.toByteArray(Charsets.UTF_8)
        writeShort(out, nameBytes.size)
        out.write(nameBytes)
        out.flush()
    }

    private fun writeEvent(out: BufferedOutputStream, event: InputEvent) {
        when (event) {
            is MouseMotionEvent -> {
                out.write(0x01) // type
                writeInt(out, ((event.timestampNanos / 1000) and 0xFFFFFFFFL).toInt())
                writeShort(out, event.x)
                writeShort(out, event.y)
            }
            is MouseButtonEvent -> {
                out.write(0x02)
                writeInt(out, ((event.timestampNanos / 1000) and 0xFFFFFFFFL).toInt())
                writeShort(out, event.x)
                writeShort(out, event.y)
                out.write(event.button.id)
                out.write(if (event.pressed) 1 else 0)
            }
            is MouseScrollEvent -> {
                out.write(0x03)
                writeInt(out, ((event.timestampNanos / 1000) and 0xFFFFFFFFL).toInt())
                writeShort(out, event.x)
                writeShort(out, event.y)
                writeInt(out, event.scrollDelta)
            }
            is KeyboardEvent -> {
                out.write(0x04)
                writeInt(out, ((event.timestampNanos / 1000) and 0xFFFFFFFFL).toInt())
                writeInt(out, event.keyCode)
                out.write(if (event.pressed) 1 else 0)
            }
        }
    }

    // Little-endian write helpers
    private fun writeShort(out: BufferedOutputStream, value: Int) {
        out.write(value and 0xFF)
        out.write((value shr 8) and 0xFF)
    }

    private fun writeInt(out: BufferedOutputStream, value: Int) {
        out.write(value and 0xFF)
        out.write((value shr 8) and 0xFF)
        out.write((value shr 16) and 0xFF)
        out.write((value shr 24) and 0xFF)
    }

    private fun writeLong(out: BufferedOutputStream, value: Long) {
        for (i in 0 until 8) {
            out.write(((value shr (i * 8)) and 0xFF).toInt())
        }
    }
}
