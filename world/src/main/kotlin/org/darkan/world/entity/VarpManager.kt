package org.darkan.world.entity

import org.darkan.core.net.prot.ServerProt
import org.darkan.core.net.prot.VarpLarge
import org.darkan.core.net.prot.VarpLong
import org.darkan.core.net.prot.VarpSmall
import org.darkan.core.net.session.GameSession

/**
 * Per-player varp (player-variable) state + dirty-flush emitter, per
 * `docs/protocol/world-entry-render-948.md` §2.4/§7.5 and `docs/protocol/world-bootstrap-948.md`
 * §2/§3.
 *
 * Tracks each varp's current value by id and, on [flush], emits a packet for every id changed since
 * the last flush, choosing the opcode by value magnitude:
 *  - **op61 VarpSmall** (1-byte) when the value fits a signed byte `[-128, 127]` — the client
 *    recovers it as `(-128 - wireByte)`, so the wire byte is `(-128 - value)` (already correct in
 *    the registered `VarpSmall` op61 encoder).
 *  - **op28 VarpLarge** (4-byte BE) for any other 32-bit value.
 *  - **op147 VarpLong** (8-byte) for values outside the 32-bit signed range.
 *
 * **Seeding.** Fresh local accounts do not have persisted saved vars yet. The world bootstrap seeds
 * cache-valid first-light defaults through the [VarpDefaults] seam, then emits them between
 * `ResetClientVarcache` and the first GPI.
 *
 * **⚠ CRITICAL (§2.4 HARD HAZARD).** Only emit ids the client's cache defines as a `VarpType` — an
 * unknown id NULL-derefs `GetVarType` → SIGSEGV. [set] does NOT validate ids against the cache
 * (no cache access here); callers must only set cache-valid ids. The [VarpDefaults] seam, once
 * cache-backed, is validated-by-construction (it enumerates real `VarpType` ids).
 *
 */
class VarpManager {

    /** Current value of each set varp, by id. */
    private val values = HashMap<Int, Long>()

    /** Ids whose value changed since the last [flush]. */
    private val dirty = LinkedHashSet<Int>()

    /** Signed-byte range the op61 VarpSmall 1-byte value covers (client recovers `(-128 - wire)`). */
    private val smallRange = -128..127

    /** Current value of varp [id], or `null` if unset. */
    operator fun get(id: Int): Long? = values[id]

    /**
     * Set varp [id] to [value]. Marks the id dirty (to be emitted on the next [flush]) only if the
     * value actually changed — re-setting the same value is a no-op, so [flush] won't re-emit it.
     */
    fun set(id: Int, value: Long) {
        val previous = values.put(id, value)
        if (previous == null || previous != value) {
            dirty.add(id)
        }
    }

    /** Convenience overload for 32-bit values. */
    fun set(id: Int, value: Int) = set(id, value.toLong())

    /** Number of varps pending emission on the next [flush] (for tests/diagnostics). */
    fun dirtyCount(): Int = dirty.size

    /**
     * Seed defaults for cache-valid varp ids via the [defaults] seam. Each seeded id is `set` only
     * if not already present, so persisted account vars can override these later.
     */
    fun seedDefaults(defaults: VarpDefaults = VarpDefaults.None) {
        for ((id, value) in defaults.hudDefaults()) {
            if (id !in values) set(id, value)
        }
    }

    /**
     * Emit a packet for every dirty varp, then clear the dirty set. Chooses op61/op28/op147 by
     * value magnitude (see class doc). Returns the packets emitted (also useful for tests).
     *
     * `WorldServer.sendVarpBaseline` calls this after `ResetClientVarcache`, before the first GPI.
     */
    suspend fun flush(session: GameSession): List<ServerProt> {
        if (dirty.isEmpty()) return emptyList()
        val emitted = ArrayList<ServerProt>(dirty.size)
        for (id in dirty) {
            val value = values[id] ?: continue
            val packet = encode(id, value)
            session.send(packet)
            emitted.add(packet)
        }
        dirty.clear()
        return emitted
    }

    /**
     * Choose the varp packet for `(id, value)` by magnitude. Exposed `internal` so the unit test can
     * assert opcode selection without a live session.
     */
    internal fun encode(id: Int, value: Long): ServerProt = when {
        value in Int.MIN_VALUE.toLong()..Int.MAX_VALUE.toLong() && value.toInt() in smallRange ->
            VarpSmall(id, value.toInt())
        value in Int.MIN_VALUE.toLong()..Int.MAX_VALUE.toLong() ->
            VarpLarge(id, value.toInt())
        else ->
            VarpLong(id, value)
    }
}

/**
 * Seam for first-light varp seeding. Implementations must only return cache-valid ids.
 */
interface VarpDefaults {
    /** `(varpId, defaultValue)` for cache-valid varp ids. */
    fun hudDefaults(): Map<Int, Long>

    object None : VarpDefaults {
        override fun hudDefaults(): Map<Int, Long> = emptyMap()
    }
}
