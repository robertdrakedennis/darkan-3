package org.darkan.core.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.darkan.core.EnvVars
import org.darkan.core.Logger
import org.darkan.core.net.Session
import org.darkan.core.net.prot.*
import world.gregs.voidps.cache.Cache
import world.gregs.voidps.cache.Config
import world.gregs.voidps.cache.Index
import world.gregs.voidps.gameval.Gameval

/**
 * Player variable state machine. Handles varps and varbits with automatic
 * bit packing/unpacking using cache VarBit definitions.
 *
 * Designed for use in both lobby and world servers (lives in core).
 * Based on ~/darkan/server Vars class — logic only, not protocol.
 */
@Serializable
class Vars(val saved: MutableMap<Int, Int> = HashMap()) {
    companion object {
        val BIT_MASKS = IntArray(32).apply {
            var value = 2
            for (i in 0 until 32) {
                this[i] = value - 1
                value *= 2
            }
        }
    }

    @Transient
    private var modified: MutableSet<Int> = HashSet()
    @Transient
    private lateinit var varpValues: IntArray
    @Transient
    private var session: Session? = null

    /** Initialize with a session for network sync. Restores saved vars. */
    fun init(session: Session): Vars {
        this.session = session
        varpValues = IntArray(Cache.get().fileCount(Index.CONFIGS, Config.VAR_PLAYER) + 1)
        modified = HashSet()
        saved.forEach { (varId, value) -> setVar(varId, value) }
        return this
    }

    /** Initialize without a session (offline/tool usage). */
    fun initOffline(): Vars {
        varpValues = IntArray(Cache.get().fileCount(Index.CONFIGS, Config.VAR_PLAYER) + 1)
        modified = HashSet()
        saved.forEach { (varId, value) -> setVar(varId, value) }
        return this
    }

    /** Set a varp by ID. Optionally force-send and/or persist to DB. */
    @JvmOverloads
    fun setVar(id: Int, value: Int, forceSend: Boolean = false, save: Boolean = false) {
        if (forceSend) modified.add(id)
        if (id < 0 || id >= varpValues.size || varpValues[id] == value) return
        varpValues[id] = value
        if (save) saved[id] = value
        modified.add(id)
        if (EnvVars.debug) Logger.log("Vars", "varp ${Gameval.varpLabel(id)} = $value")
    }

    /** Set and persist a varp. */
    fun saveVar(id: Int, value: Int) = setVar(id, value, save = true)

    /** Set a varbit by ID. Automatically packs into the backing varp. */
    @JvmOverloads
    fun setVarBit(id: Int, value: Int, forceSend: Boolean = false, save: Boolean = false) {
        val defs = Cache.varbits[id]
        val bitLength = defs.endBit - defs.startBit
        val mask = BIT_MASKS[bitLength]
        val cappedValue = value.coerceIn(0, mask)
        val shiftedMask = mask shl defs.startBit
        val varpValue = (varpValues[defs.index] and shiftedMask.inv()) or
                ((cappedValue shl defs.startBit) and shiftedMask)
        if (varpValue != varpValues[defs.index]) {
            if (EnvVars.debug) Logger.log("Vars", "varbit ${Gameval.varbitPlayerLabel(id)} = $cappedValue (packs varp ${Gameval.varpLabel(defs.index)})")
            setVar(defs.index, varpValue, forceSend, save)
        }
    }

    /** Set and persist a varbit. */
    fun saveVarBit(id: Int, value: Int) = setVarBit(id, value, save = true)

    /** Read a varp value. */
    fun getVar(id: Int): Int = if (id in varpValues.indices) varpValues[id] else 0

    /** Read a varbit value (auto-unpacks from backing varp). */
    fun getVarBit(id: Int): Int {
        val defs = Cache.varbits[id]
        val bitLength = defs.endBit - defs.startBit
        return (varpValues[defs.index] shr defs.startBit) and BIT_MASKS[bitLength]
    }

    /** Check if a specific bit is set in a varp. */
    fun bitFlagged(varpId: Int, bit: Int) = (varpValues[varpId] and (1 shl bit)) != 0

    /** Send all modified varps to the client. */
    fun syncVarsToClient() {
        val s = session ?: return
        modified.forEach { id ->
            if (id < 0 || id >= varpValues.size) return@forEach
            val value = varpValues[id]
            if (value.toLong() in -128..127) {
                s.queuePacket(VarpSmall(id, value))
            } else {
                s.queuePacket(VarpLarge(id, value))
            }
        }
        modified.clear()
    }

    /** Send ALL non-zero varps to the client (for initial login). */
    fun syncAllToClient() {
        val s = session ?: return
        for (id in varpValues.indices) {
            val value = varpValues[id]
            if (value == 0) continue
            if (value.toLong() in -128..127) {
                s.queuePacket(VarpSmall(id, value))
            } else {
                s.queuePacket(VarpLarge(id, value))
            }
        }
    }

    /** Reset all varps to 0 and send RESET_CLIENT_VARCACHE. */
    suspend fun clearVars() {
        varpValues.fill(0)
        session?.send(ResetClientVarcache())
    }

    /** Send a client varc (int). */
    suspend fun setVarc(id: Int, value: Int) {
        val s = session ?: return
        if (value.toLong() in -128..127) {
            s.send(ClientSetVarcSmall(id, value))
        } else {
            s.send(ClientSetVarcLarge(id, value))
        }
    }

    /** Send a client varc (string). */
    suspend fun setVarcStr(id: Int, value: String) {
        session?.send(ClientSetVarcStr(id, value))
    }
}
