package com.undercut.game.bootstrap

import com.undercut.cache.type.vars.VarbitType
import com.undercut.game.hooks.HookManager
import com.undercut.game.memory.NativeAccess
import com.undercut.game.net.PacketLogger
import com.undercut.game.nxt.Client
import com.undercut.quest.solver.registerExampleSolvers
import com.undercut.script.ScriptExecutor
import java.lang.foreign.MemorySegment

object Bootstrap {
    val lock = Any()
    lateinit var client: Client
    private var clientAttached = false

    @JvmStatic
    fun initialize(baseAddr: Long) {
        synchronized(lock) {
            ScriptExecutor.loadScripts()
            println("✅ loadScripts() completed. Found: ${ScriptExecutor.scripts.size} scripts")

            VarbitType.loadBaseVarMap()
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

            println("Project Undercut successfully initialized. (MCP server disabled — enable from Settings tab)")
        }
    }
}