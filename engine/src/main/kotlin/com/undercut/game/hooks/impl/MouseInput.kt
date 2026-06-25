package com.undercut.game.hooks.impl

import com.undercut.game.bootstrap.Bootstrap
import com.undercut.game.hooks.Hook
import com.undercut.game.hooks.HookManager
import com.undercut.game.input.*
import com.undercut.game.memory.NativeAccess
import com.undercut.game.memory.NativeAccess.toFunctionHandle
import com.undercut.game.nxt.OFunctions
import java.lang.foreign.FunctionDescriptor
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout.*
import java.lang.invoke.MethodHandle

/**
 * Mouse input hooks — captures mouse motion, button presses, and scroll events.
 *
 * Native calling convention (x86_64 System V ABI):
 *   RDI  = input manager pointer (long)
 *   XMM0 = x coordinate (float)
 *   XMM1 = y coordinate (float)
 *
 * Panama maps Java Long -> integer registers (RDI), Float -> XMM registers.
 * So the hook signature (Long, Float, Float) correctly captures all three params.
 */

object OnMouseMotion {
    @JvmStatic
    @Hook(OFunctions.INPUT_INPUT_ONMOUSEMOTION)
    fun onMouseMotionHook(inputMgr: Long, x: Float, y: Float) {
        try {
            if (mouseInputMgrPtr != inputMgr) {
                println("[MouseInput] Input manager pointer found: 0x${inputMgr.toString(16)}")
                mouseInputMgrPtr = inputMgr
            }
            val event = MouseMotionEvent(
                timestampNanos = System.nanoTime(),
                gameTick = Bootstrap.client.clientCycle,
                x = x.toInt(),
                y = y.toInt()
            )
            if (InputRecorder.isRecording) InputRecorder.record(event)
            SyntheticInputShadow.observeEvent(event)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        HookManager.trampoline(::onMouseMotionHook.name).invokeExact(inputMgr, x, y)
    }
}

object OnLeftButtonDown {
    @JvmStatic
    @Hook(OFunctions.INPUT_INPUT_ONLEFTBUTTONDOWN)
    fun onLeftButtonDownHook(inputMgr: Long, x: Float, y: Float) {
        try {
            mouseInputMgrPtr = inputMgr
            println("[MouseInput] Left button DOWN at (${x.toInt()}, ${y.toInt()})")
            val event = MouseButtonEvent(
                timestampNanos = System.nanoTime(),
                gameTick = Bootstrap.client.clientCycle,
                x = x.toInt(),
                y = y.toInt(),
                button = MouseButton.LEFT,
                pressed = true
            )
            if (InputRecorder.isRecording) InputRecorder.record(event)
            SyntheticInputShadow.observeEvent(event)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        HookManager.trampoline(::onLeftButtonDownHook.name).invokeExact(inputMgr, x, y)
    }
}

object OnLeftButtonUp {
    @JvmStatic
    @Hook(OFunctions.INPUT_INPUT_ONLEFTBUTTONUP)
    fun onLeftButtonUpHook(inputMgr: Long, x: Float, y: Float) {
        try {
            mouseInputMgrPtr = inputMgr
            val event = MouseButtonEvent(
                timestampNanos = System.nanoTime(),
                gameTick = Bootstrap.client.clientCycle,
                x = x.toInt(),
                y = y.toInt(),
                button = MouseButton.LEFT,
                pressed = false
            )
            if (InputRecorder.isRecording) InputRecorder.record(event)
            SyntheticInputShadow.observeEvent(event)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        HookManager.trampoline(::onLeftButtonUpHook.name).invokeExact(inputMgr, x, y)
    }
}

object OnScrollWheel {
    @JvmStatic
    @Hook(OFunctions.INPUT_INPUT_ONSCROLLWHEEL)
    fun onScrollWheelHook(inputMgr: Long, scrollX: Float, scrollY: Float) {
        try {
            mouseInputMgrPtr = inputMgr
            val event = MouseScrollEvent(
                timestampNanos = System.nanoTime(),
                gameTick = Bootstrap.client.clientCycle,
                x = scrollX.toInt(),
                y = scrollY.toInt(),
                scrollDelta = scrollY.toInt()
            )
            if (InputRecorder.isRecording) InputRecorder.record(event)
            SyntheticInputShadow.observeEvent(event)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        HookManager.trampoline(::onScrollWheelHook.name).invokeExact(inputMgr, scrollX, scrollY)
    }
}

// Shared state — the input manager pointer captured from any mouse hook
@Volatile
private var mouseInputMgrPtr: Long = 0

// Direct function handle for InjectSyntheticRightClick (not hooked, call directly)
// Signature: long InjectSyntheticRightClick(long inputMgr)
// Uses the game's own pattern: writes globals, calls DispatchObservers with right-down then right-up observer lists
private val syntheticRightClickFn: MethodHandle by lazy {
    NativeAccess.BASE_ADDR.asSlice(OFunctions.INPUT_INPUT_INJECTSYNTHETICRIGHTCLICK, 8)
        .toFunctionHandle(FunctionDescriptor.of(JAVA_LONG, JAVA_LONG))
}

/**
 * Inject a mouse motion event into the game's native input pipeline.
 * Calls the trampoline (original function) to avoid re-triggering our hook.
 */
fun moveMouse(x: Int, y: Int) {
    if (mouseInputMgrPtr == 0L) return
    HookManager.trampoline(OnMouseMotion::onMouseMotionHook.name)
        .invokeExact(mouseInputMgrPtr, x.toFloat(), y.toFloat())
}

fun leftClickDown(x: Int, y: Int) {
    if (mouseInputMgrPtr == 0L) return
    HookManager.trampoline(OnLeftButtonDown::onLeftButtonDownHook.name)
        .invokeExact(mouseInputMgrPtr, x.toFloat(), y.toFloat())
}

fun leftClickUp(x: Int, y: Int) {
    if (mouseInputMgrPtr == 0L) return
    HookManager.trampoline(OnLeftButtonUp::onLeftButtonUpHook.name)
        .invokeExact(mouseInputMgrPtr, x.toFloat(), y.toFloat())
}

fun leftClick(x: Int, y: Int) {
    leftClickDown(x, y)
    leftClickUp(x, y)
}

fun scroll(x: Int, y: Int, delta: Int) {
    if (mouseInputMgrPtr == 0L) return
    HookManager.trampoline(OnScrollWheel::onScrollWheelHook.name)
        .invokeExact(mouseInputMgrPtr, x.toFloat(), delta.toFloat())
}

/**
 * Inject a right-click at the given coordinates using the game's own InjectSyntheticRightClick.
 * First moves the mouse to (x, y) so the globals are set, then calls the synthetic function.
 */
fun rightClick(x: Int, y: Int) {
    if (mouseInputMgrPtr == 0L) return
    // Move mouse to target first so g_mouseX/g_mouseY are set
    moveMouse(x, y)
    // Call the game's own synthetic right-click function
    syntheticRightClickFn.invoke(mouseInputMgrPtr)
}

/** Returns true if the input manager pointer has been captured (any mouse event received). */
fun isMouseReady(): Boolean = mouseInputMgrPtr != 0L
