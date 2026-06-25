package com.undercut.game.hooks.impl

import com.undercut.game.bootstrap.Bootstrap
import com.undercut.game.hooks.Hook
import com.undercut.game.hooks.HookManager
import com.undercut.game.input.InputRecorder
import com.undercut.game.input.MouseButton
import com.undercut.game.input.MouseButtonEvent
import com.undercut.game.input.MouseEventBuffer
import com.undercut.game.input.MouseMotionEvent
import com.undercut.game.input.SyntheticInputShadow
import com.undercut.game.nxt.OFunctions
import java.lang.foreign.MemorySegment

/**
 * Hook on `jag::ClientProt::SendEventMouseClick` — fires once per drained entry
 * (one packet per call). Reads the entry the game is about to send and feeds it
 * into the recorder (for training) and the synth window (so the model sees
 * server-bound history). Does NOT modify the entry or skip the trampoline.
 */
object SendEventMouseClickCapture {
    /** Last-seen ClientProt pointer — captured here so MouseClickPacketSender can drive sends from outside a real hook fire. Zero until the hook fires at least once. */
    @JvmStatic
    @Volatile
    var lastClientProtAddr: Long = 0L

    @JvmStatic
    @Hook(OFunctions.CLIENTPROT_SENDEVENTMOUSECLICK)
    fun sendEventMouseClickHook(clientProt: MemorySegment) {
        if (clientProt.address() != 0L) lastClientProtAddr = clientProt.address()
        try {
            val tail = MouseEventBuffer.tail()
            if (tail >= 0) {
                val entry = MouseEventBuffer.readEntry(tail)
                if (entry != null) {
                    val gameTick = Bootstrap.client.clientCycle
                    val ts = System.nanoTime()
                    val event = if (entry.buttonFlag != 0) {
                        MouseButtonEvent(
                            timestampNanos = ts,
                            gameTick = gameTick,
                            x = entry.x.toInt(),
                            y = entry.y.toInt(),
                            button = MouseButton.LEFT,
                            pressed = true
                        )
                    } else {
                        MouseMotionEvent(
                            timestampNanos = ts,
                            gameTick = gameTick,
                            x = entry.x.toInt(),
                            y = entry.y.toInt()
                        )
                    }
                    if (InputRecorder.isRecording) InputRecorder.record(event)
                    SyntheticInputShadow.observeEvent(event)
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        HookManager.trampoline(::sendEventMouseClickHook.name).invokeExact(clientProt)
    }
}
