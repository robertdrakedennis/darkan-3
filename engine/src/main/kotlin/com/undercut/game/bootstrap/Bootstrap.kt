package com.undercut.game.bootstrap

import com.undercut.BuildInfo
import com.undercut.game.hooks.HookManager
import com.undercut.game.memory.Funchook
import com.undercut.game.memory.NativeAccess
import com.undercut.game.net.PacketLogger
import com.undercut.game.nxt.Client
import com.undercut.markers.TileMarkerStore
import com.undercut.mcp.McpServer
import com.undercut.quest.solver.registerExampleSolvers
import com.undercut.script.ScriptExecutor
import com.undercut.ui.backend.native.StringAllocator
import world.gregs.voidps.cache.Cache
import world.gregs.voidps.cache.definition.data.VarBitDefinition
import java.lang.foreign.MemorySegment
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

object Bootstrap {
    val lock = Any()
    lateinit var client: Client
    private var clientAttached = false

    /** Set during [shutdown] so the hot hooks become pure passthroughs before the hooks are removed. */
    @Volatile
    var stopping = false

    /**
     * Resolve the live NXT client cache dir. `RS_CACHE_DIR` (exported by the darkan launcher per
     * server mode) is authoritative; otherwise probe the known launcher locations and pick the first
     * that actually holds a cache. The injected engine runs inside rs2client whose HOME the launcher
     * redirects to its data dir, so the cache lands at `$HOME/Jagex/RuneScape`.
     */
    private fun resolveCacheDir(): Path {
        System.getenv("RS_CACHE_DIR")?.let {
            println("[Cache] cache dir = $it (RS_CACHE_DIR)")
            return Paths.get(it)
        }
        val home = System.getProperty("user.home")
        val candidates = listOf(
            "$home/Jagex/RuneScape",
            "$home/.local/share/darkan-launcher/Jagex/RuneScape",
        )
        val chosen = candidates.firstOrNull { dir ->
            Files.isDirectory(Paths.get(dir)) && (0..255).any { Files.exists(Paths.get(dir, "js5-$it.jcache")) }
        } ?: candidates.first()
        println("[Cache] cache dir = $chosen (probed; RS_CACHE_DIR unset)")
        return Paths.get(chosen)
    }

    @JvmStatic
    fun initialize(baseAddr: Long) {
        synchronized(lock) {
            ScriptExecutor.loadScripts()
            println("✅ loadScripts() completed. Found: ${ScriptExecutor.scripts.size} scripts")

            Cache.init(resolveCacheDir())
            VarBitDefinition.loadBaseVarMap()
            println("Initializing native access at base address 0x${baseAddr.toString(16)}")
            NativeAccess.init(MemorySegment.ofAddress(baseAddr).reinterpret(0x2000000L))

            client = Client.getClient(NativeAccess.BASE_ADDR.reinterpret(0x2000000L))
            println("Attached to base client address: 0x${client.ptr.address().toString(16)}")
            println("Game state: ${client.mainState}")
            println("Logged In Player: 0x${client.loggedInPlayer.ptr.address().toString(16)}")
            println("Player Var Domain: 0x${client.playerVarDomain.ptr.address().toString(16)}")

            PacketLogger.init()

            println("Parsing and applying hooks...")
            HookManager.parseAndApplyHooks(NativeAccess.BASE_ADDR)

            registerExampleSolvers()

            println("Project Undercut successfully initialized — build ${BuildInfo.VERSION}. (MCP server disabled — enable from Settings tab)")
        }
    }

    /**
     * Full engine teardown so this classloader can be dropped and a rebuilt jar reloaded. ORDERING
     * IS CRASH-SENSITIVE: stop the game from calling engine code FIRST (hooks), quiesce, THEN tear
     * down native ImGui and stop threads. Doing ImGui/thread teardown while hooks are still live
     * lets the render/main-logic thread execute dead upcall stubs → SIGSEGV.
     */
    @JvmStatic
    fun shutdown() {
        stopping = true
        // 1. Let the hot hooks (main-logic + render) become passthrough across a few frames.
        runCatching { Thread.sleep(120) }
        // 2. Uninstall every funchook hook — the game's functions run unhooked from here on.
        runCatching { Funchook.uninstall() }
        // 3. Barrier on the lock (drain an in-flight main-logic hook) + let the patch settle.
        synchronized(lock) {}
        runCatching { Thread.sleep(50) }
        // 4. Free the funchook instance. (Native ImGui + GL textures are deliberately LEFT resident:
        //    the game's GL context is unchanged across a reload, and a fresh engine reuses the
        //    existing ImGui context — Undercut_ImGui_Init is idempotent. Destroying + recreating the
        //    GL backend here broke texture (re)creation on the reloaded engine, so we don't.)
        runCatching { Funchook.destroy(MemorySegment.NULL) }
        // 5. Stop engine-owned threads / resources (order not crash-sensitive once hooks are off).
        runCatching { McpServer.stop() }
        runCatching { StringAllocator.shutdown() }
        runCatching { TileMarkerStore.stopFlusher() }
        runCatching { ScriptExecutor.stopAll() }
        runCatching { ScriptExecutor.stopInternalTasks() }
        runCatching { PacketLogger.close() }
        // 6. LAST: free every per-engine-load native allocation (hook upcall stubs, funchook slots,
        //    UI/DoAction buffers). Done only after hooks are off AND every engine thread/script is
        //    stopped, so nothing can read/write or allocate from the arena while it's being closed.
        //    Releasing the upcall stubs drops the MethodHandles bound to the hook methods, letting
        //    this classloader's metaspace be reclaimed (verified by the supervisor's WeakReference).
        runCatching { NativeAccess.teardown() }
        println("Project Undercut engine torn down (was build ${runCatching { BuildInfo.VERSION }.getOrDefault("?")})")
    }
}