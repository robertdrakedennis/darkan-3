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
 * **Seeding (§7.5 fresh-account strategy).** A fresh account has no saved varps. The HUD scripts
 * (8862/16300/671) read varps to populate skills/orbs/settings; a *missing-but-required* var can
 * trip a CS2 error. The safe path is to seed `VarpType.defaultValue` for the cache-valid, HUD-touched
 * ids. Reading `VarpType` defs requires the cache library (`world.gregs.voidps`), which this module
 * must NOT modify; so seeding goes through the [VarpDefaults] seam (default = no-op / empty). Until
 * `cache-library-engineer` provides a real `VarpType` defaults provider, [seedDefaults] is a no-op
 * and the live baseline stays minimal (safe: PlayerInfo has no varp gate — §2.4).
 *
 * **⚠ CRITICAL (§2.4 HARD HAZARD).** Only emit ids the client's cache defines as a `VarpType` — an
 * unknown id NULL-derefs `GetVarType` → SIGSEGV. [set] does NOT validate ids against the cache
 * (no cache access here); callers must only set cache-valid ids. The [VarpDefaults] seam, once
 * cache-backed, is validated-by-construction (it enumerates real `VarpType` ids).
 *
 * **UNWIRED for first light.** The live op75-alone burst (`WorldServer.sendVarpBaseline`) stays empty.
 * Wire this in only after op75 validates — see [flush]'s wiring note.
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
     * Seed defaults for the cache-valid, HUD-touched varp ids via the [defaults] seam (§7.5). Each
     * seeded id is `set` (and thus dirtied) only if not already present. Default seam = empty, so
     * this is a no-op until `cache-library-engineer` provides a real `VarpType` defaults provider.
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
     * Wiring (post-op75 validation), in `WorldServer.sendVarpBaseline` (currently an intentional
     * no-op):
     *
     *     // WIRE: call after op75 validates — emit the seeded/saved varp baseline between op5 and
     *     // the first GPI. player.varps.seedDefaults(cacheBackedDefaults); player.varps.flush(session)
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
 * Seam for `VarpType.defaultValue` seeding (§7.5). The world module cannot read cache defs (that is
 * `world.gregs.voidps`, owned by `cache-library-engineer`); this interface lets the world request
 * "the default values for the HUD-critical, cache-valid varp ids" without depending on the cache.
 *
 * **FLAGGED for `cache-library-engineer`:** provide a cache-backed implementation that enumerates the
 * `VarpType` table (interface config index), returning `(id, defaultValue)` for the low-id varps the
 * world-entry CS2 (8862/16300/671) reads — validated-by-construction so it can never contain a stale
 * id (§2.4). Until then [None] returns nothing and the baseline stays empty.
 */
interface VarpDefaults {
    /** `(varpId, defaultValue)` for the HUD-critical, cache-valid varp ids. */
    fun hudDefaults(): Map<Int, Long>

    /** No-op default: no seeding (safe — PlayerInfo has no varp gate, §2.4). */
    object None : VarpDefaults {
        override fun hudDefaults(): Map<Int, Long> = emptyMap()
    }
}
