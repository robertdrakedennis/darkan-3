package com.undercut.game.hooks.impl

import com.undercut.game.bootstrap.Bootstrap
import com.undercut.game.hooks.HookManager
import com.undercut.game.hooks.SymbolHook
import com.undercut.game.memory.NativeAccess
import com.undercut.ui.backend.dsl.utils.ModernTheme.withModernTheme
import com.undercut.ui.backend.native.NativeBridge
import com.undercut.ui.backend.rendering.CommandRenderer
import com.undercut.ui.backend.rendering.ImGuiRenderManager
import com.undercut.ui.backend.rendering.UiFrameProducer
import java.lang.foreign.FunctionDescriptor
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout.ADDRESS


object EGLSwapBuffers {
    private var initialized = false

    private val sdlGetCurrentWindow by lazy {
        NativeAccess.getLibraryFunction(
            "/usr/lib/libSDL2-2.0.so.0",
            "SDL_GL_GetCurrentWindow"
        ) { FunctionDescriptor.of(ADDRESS) }
    }

    private val sdlGetCurrentContext by lazy {
        NativeAccess.getLibraryFunction(
            "/usr/lib/libSDL2-2.0.so.0",
            "SDL_GL_GetCurrentContext"
        ) { FunctionDescriptor.of(ADDRESS) }
    }

    @JvmStatic
    @SymbolHook(
        library = "/usr/lib/libEGL.so.1",
        symbol = "eglSwapBuffers"
    )
    fun eglSwapBuffersHook(display: MemorySegment, surface: MemorySegment): MemorySegment {
        var frameStarted = false
        try {
            if (!initialized) {
                initImGui()
                initialized = true
            }

            NativeBridge.newFrame()
            frameStarted = true

            // Replay the latest buffer built on the main-logic thread (UiFrameProducer.build). The
            // render thread NEVER reads live game state — it only replays already-captured commands, so
            // there is no thread to race scene/interface mutation. (No direct-render fallback: that path
            // ran render() on this thread and was the source of the recurring SIGSEGVs.)
            val commands = UiFrameProducer.frames.promoteIfReadyOrKeepFront()
            if (commands.isNotEmpty()) {
                UiFrameProducer.frames.beginConsume(commands)
                try {
                    withModernTheme {
                        CommandRenderer.executePrecomputed(commands)
                    }
                } finally {
                    UiFrameProducer.frames.endConsume(commands)
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        } finally {
            // Always render to complete the frame, even if exceptions occurred
            if (frameStarted) {
                try {
                    NativeBridge.render()
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
        
        return HookManager.trampoline(::eglSwapBuffersHook.name).invokeExact(display, surface) as MemorySegment
    }

    private fun initImGui() {
        try {
            val sdlWindow = sdlGetCurrentWindow.invokeExact() as MemorySegment
            val glContext = sdlGetCurrentContext.invokeExact() as MemorySegment
            
            if (sdlWindow.address() == 0L || glContext.address() == 0L) {
                println("[EGLSwapBuffers] Warning: SDL window or GL context is null, retrying with Bootstrap")
                // Fallback to Bootstrap's SDL window if available
                val fallbackWindow = Bootstrap.client.sdlManager.sdlWindow.ptr
                NativeBridge.init(fallbackWindow, glContext)
            } else {
                NativeBridge.init(sdlWindow, glContext)
            }
            
            // Discover render methods; the actual per-frame build runs on the main-logic thread
            // (UiFrameProducer.build, driven by ClientMainLogic), not a background worker.
            ImGuiRenderManager.initialize()
            UiFrameProducer.enabled = true

            println("[EGLSwapBuffers] ImGui initialized successfully")
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}
