package com.undercut.game.nxt

import com.undercut.game.memory.NativeAccess.deref
import java.lang.foreign.MemorySegment

class SDLManager(val ptr: MemorySegment) {
    val windowPtr
        get() = ptr.deref(OSDLManager.WINDOW, 0x100L)
    val sdlWindow
        get() = SDLWindow(windowPtr.deref(OSDLManager.SDL_WINDOW, 0x100L))
}

class SDLWindow(val ptr: MemorySegment)