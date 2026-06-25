package com.undercut.game.hooks.impl

import com.undercut.game.bootstrap.Bootstrap
import com.undercut.game.hooks.Hook
import com.undercut.game.hooks.HookManager
import com.undercut.game.hooks.SymbolHook
import com.undercut.game.input.InputRecorder
import com.undercut.game.input.KeyboardEvent
import com.undercut.game.input.SyntheticInputShadow
import com.undercut.game.memory.NativeAccess
import com.undercut.game.memory.NativeAccess.toFunctionHandle
import com.undercut.game.nxt.OFunctions
import com.undercut.ui.UIState
import java.lang.foreign.FunctionDescriptor
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout.*
import java.lang.invoke.MethodHandle

object OnKeyDown {
    @JvmStatic
    @SymbolHook(symbol = "SDL_GetKeyboardFocus", library = "/usr/lib/libSDL2-2.0.so.0")
    fun getKeyboardFocusHook(): MemorySegment {
        return Bootstrap.client.sdlManager.sdlWindow.ptr
    }

    @JvmStatic
    @Hook(OFunctions.INPUT_INPUT_ONKEYDOWNINNER)
    fun onKeyDownHook(param1: MemorySegment, param2: Int, param3: Long, param4: MemorySegment): Int {
        try {
            if (inputPtr?.address() != param1.address()) {
                println("[KeyInput]: Input pointer found. Setting from ${inputPtr?.address()} to ${param1.address()}")
                inputPtr = param1
            }
            if (idkPtr?.address() != param4.address()) {
                println("[KeyInput]: Unknown input parameter found. Setting from ${idkPtr?.address()} to ${param4.address()}")
                idkPtr = param4
            }
//            val offset = (param1.address() - Bootstrap.client.ptr.address()).toString(0x16)
//            println("Input offset: $offset")
            val event = KeyboardEvent(
                timestampNanos = System.nanoTime(),
                gameTick = Bootstrap.client.clientCycle,
                keyCode = param2,
                pressed = true
            )
            if (InputRecorder.isRecording) InputRecorder.record(event)
            SyntheticInputShadow.observeEvent(event)

            val keyOutput = when {
                param2 in 32..126 -> "'${param2.toChar()}'"
                SDLKeycode.fromValue(param2) != null -> "SDLKeycode.${SDLKeycode.fromValue(param2)}"
                else -> "0x${param2.toString(16).uppercase()}"
            }
            println("[KeyboardDown]: $keyOutput")

            // Global toggle for main UI: configurable SDL key (swallowed)
            if (param2 == UIState.uiToggleKey.value) {
                return try {
                    UIState.showMainWindow.value = !UIState.showMainWindow.value
                    // Swallow: do not forward to game
                    0
                } catch (e: Throwable) {
                    e.printStackTrace()
                    // On error, still forward to game
                    HookManager.trampoline(::onKeyDownHook.name).invokeExact(param1, param2, param3, param4) as Int
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return HookManager.trampoline(::onKeyDownHook.name).invokeExact(param1, param2, param3, param4) as Int
    }
}

object OnKeyUp {
    @JvmStatic
    @Hook(OFunctions.INPUT_INPUT_ONKEYUPINNER)
    fun onKeyUpHook(param1: MemorySegment, param2: Int, param3: Long, param4: MemorySegment): Int {
        try {
            inputPtr = param1
            idkPtr = param4
            val event = KeyboardEvent(
                timestampNanos = System.nanoTime(),
                gameTick = Bootstrap.client.clientCycle,
                keyCode = param2,
                pressed = false
            )
            if (InputRecorder.isRecording) InputRecorder.record(event)
            SyntheticInputShadow.observeEvent(event)
        } catch(e: Throwable) {
            e.printStackTrace()
        }
        return HookManager.trampoline(::onKeyUpHook.name).invokeExact(param1, param2, param3, param4) as Int
    }
}

private var inputPtr: MemorySegment? = null
private var idkPtr: MemorySegment? = null

// Lazy MethodHandle resolution so a transient failure (BASE_ADDR not initialized,
// an offset that's not valid yet) doesn't blow up KeyInputKt's <clinit> and leave
// the file class in permanent ERROR state — once a class fails to initialize the
// JVM caches that for the rest of the process lifetime and every reference to a
// top-level function in the same file throws NoClassDefFoundError forever.
private val keydown: MethodHandle by lazy {
    NativeAccess.BASE_ADDR.asSlice(OFunctions.INPUT_INPUT_ONKEYDOWNINNER, 8)
        .toFunctionHandle(FunctionDescriptor.of(JAVA_INT, ADDRESS, JAVA_INT, JAVA_LONG, ADDRESS))
}
private val keyup: MethodHandle by lazy {
    NativeAccess.BASE_ADDR.asSlice(OFunctions.INPUT_INPUT_ONKEYUPINNER, 8)
        .toFunctionHandle(FunctionDescriptor.of(JAVA_INT, ADDRESS, JAVA_INT, JAVA_LONG, ADDRESS))
}

fun keyDown(key: Int): Int {
    if (inputPtr == null || idkPtr == null) return -1
    return runCatching { keydown.invoke(inputPtr, key, 0, idkPtr) as Int }.getOrDefault(-1)
}

fun keyUp(key: Int): Int {
    if (inputPtr == null || idkPtr == null) return -1
    return runCatching { keyup.invoke(inputPtr, key, 0, idkPtr) as Int }.getOrDefault(-1)
}

enum class SDLKeycode(val value: Int) {
    CAPSLOCK(0x40000039),
    F1(0x4000003A),
    F2(0x4000003B),
    F3(0x4000003C),
    F4(0x4000003D),
    F5(0x4000003E),
    F6(0x4000003F),
    F7(0x40000040),
    F8(0x40000041),
    F9(0x40000042),
    F10(0x40000043),
    F11(0x40000044),
    F12(0x40000045),
    PRINTSCREEN(0x40000046),
    SCROLLLOCK(0x40000047),
    PAUSE(0x40000048),
    INSERT(0x40000049),
    HOME(0x4000004A),
    PAGEUP(0x4000004B),
    END(0x4000004D),
    PAGEDOWN(0x4000004E),
    RIGHT(0x4000004F),
    LEFT(0x40000050),
    DOWN(0x40000051),
    UP(0x40000052),
    NUMLOCKCLEAR(0x40000053),
    KP_DIVIDE(0x40000054),
    KP_MULTIPLY(0x40000055),
    KP_MINUS(0x40000056),
    KP_PLUS(0x40000057),
    KP_ENTER(0x40000058),
    KP_1(0x40000059),
    KP_2(0x4000005A),
    KP_3(0x4000005B),
    KP_4(0x4000005C),
    KP_5(0x4000005D),
    KP_6(0x4000005E),
    KP_7(0x4000005F),
    KP_8(0x40000060),
    KP_9(0x40000061),
    KP_0(0x40000062),
    KP_PERIOD(0x40000063),
    APPLICATION(0x40000065),
    POWER(0x40000066),
    KP_EQUALS(0x40000067),
    F13(0x40000068),
    F14(0x40000069),
    F15(0x4000006A),
    F16(0x4000006B),
    F17(0x4000006C),
    F18(0x4000006D),
    F19(0x4000006E),
    F20(0x4000006F),
    F21(0x40000070),
    F22(0x40000071),
    F23(0x40000072),
    F24(0x40000073),
    EXECUTE(0x40000074),
    HELP(0x40000075),
    MENU(0x40000076),
    SELECT(0x40000077),
    STOP(0x40000078),
    AGAIN(0x40000079),
    UNDO(0x4000007A),
    CUT(0x4000007B),
    COPY(0x4000007C),
    PASTE(0x4000007D),
    FIND(0x4000007E),
    MUTE(0x4000007F),
    VOLUMEUP(0x40000080),
    VOLUMEDOWN(0x40000081),
    KP_COMMA(0x40000085),
    KP_EQUALSAS400(0x40000086),
    ALTERASE(0x40000099),
    SYSREQ(0x4000009A),
    CANCEL(0x4000009B),
    CLEAR(0x4000009C),
    PRIOR(0x4000009D),
    RETURN2(0x4000009E),
    SEPARATOR(0x4000009F),
    OUT(0x400000A0),
    OPER(0x400000A1),
    CLEARAGAIN(0x400000A2),
    CRSEL(0x400000A3),
    EXSEL(0x400000A4),
    KP_00(0x400000B0),
    KP_000(0x400000B1),
    THOUSANDSSEPARATOR(0x400000B2),
    DECIMALSEPARATOR(0x400000B3),
    CURRENCYUNIT(0x400000B4),
    CURRENCYSUBUNIT(0x400000B5),
    KP_LEFTPAREN(0x400000B6),
    KP_RIGHTPAREN(0x400000B7),
    KP_LEFTBRACE(0x400000B8),
    KP_RIGHTBRACE(0x400000B9),
    KP_TAB(0x400000BA),
    KP_BACKSPACE(0x400000BB),
    KP_A(0x400000BC),
    KP_B(0x400000BD),
    KP_C(0x400000BE),
    KP_D(0x400000BF),
    KP_E(0x400000C0),
    KP_F(0x400000C1),
    KP_XOR(0x400000C2),
    KP_POWER(0x400000C3),
    KP_PERCENT(0x400000C4),
    KP_LESS(0x400000C5),
    KP_GREATER(0x400000C6),
    KP_AMPERSAND(0x400000C7),
    KP_DBLAMPERSAND(0x400000C8),
    KP_VERTICALBAR(0x400000C9),
    KP_DBLVERTICALBAR(0x400000CA),
    KP_COLON(0x400000CB),
    KP_HASH(0x400000CC),
    KP_SPACE(0x400000CD),
    KP_AT(0x400000CE),
    KP_EXCLAM(0x400000CF),
    KP_MEMSTORE(0x400000D0),
    KP_MEMRECALL(0x400000D1),
    KP_MEMCLEAR(0x400000D2),
    KP_MEMADD(0x400000D3),
    KP_MEMSUBTRACT(0x400000D4),
    KP_MEMMULTIPLY(0x400000D5),
    KP_MEMDIVIDE(0x400000D6),
    KP_PLUSMINUS(0x400000D7),
    KP_CLEAR(0x400000D8),
    KP_CLEARENTRY(0x400000D9),
    KP_BINARY(0x400000DA),
    KP_OCTAL(0x400000DB),
    KP_DECIMAL(0x400000DC),
    KP_HEXADECIMAL(0x400000DD),
    LCTRL(0x400000E0),
    LSHIFT(0x400000E1),
    LALT(0x400000E2),
    LGUI(0x400000E3),
    RCTRL(0x400000E4),
    RSHIFT(0x400000E5),
    RALT(0x400000E6),
    RGUI(0x400000E7),
    MODE(0x40000101),
    AUDIONEXT(0x40000102),
    AUDIOPREV(0x40000103),
    AUDIOSTOP(0x40000104),
    AUDIOPLAY(0x40000105),
    AUDIOMUTE(0x40000106),
    MEDIASELECT(0x40000107),
    WWW(0x40000108),
    MAIL(0x40000109),
    CALCULATOR(0x4000010A),
    COMPUTER(0x4000010B),
    AC_SEARCH(0x4000010C),
    AC_HOME(0x4000010D),
    AC_BACK(0x4000010E),
    AC_FORWARD(0x4000010F),
    AC_STOP(0x40000110),
    AC_REFRESH(0x40000111),
    AC_BOOKMARKS(0x40000112),
    BRIGHTNESSDOWN(0x40000113),
    BRIGHTNESSUP(0x40000114),
    DISPLAYSWITCH(0x40000115),
    KBDILLUMTOGGLE(0x40000116),
    KBDILLUMDOWN(0x40000117),
    KBDILLUMUP(0x40000118),
    EJECT(0x40000119),
    SLEEP(0x4000011A);

    companion object {
        fun fromValue(value: Int): SDLKeycode? = entries.find { it.value == value }
    }
}
