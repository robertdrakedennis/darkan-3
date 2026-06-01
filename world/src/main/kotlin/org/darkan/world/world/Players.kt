package org.darkan.world.world

import org.darkan.world.entity.Player
import java.util.concurrent.atomic.AtomicReferenceArray

/**
 * Global 2048-slot player pool. Slot 0 is reserved (the protocol treats index 0 as
 * "no player" in several PLAYER_INFO bit fields), so allocation begins at index 1.
 *
 * In the current single-player MVP scope, the local player IS the only player and
 * will be allocated to slot 1 on login.
 *
 * Backed by an [AtomicReferenceArray] so concurrent allocate/release calls from
 * network I/O coroutines and the game tick thread don't race. The tick loop owns
 * iteration order (via [forEach]) and is the only mutator that mutates entity state.
 */
object Players {
    private val slots = AtomicReferenceArray<Player?>(2048)

    /**
     * Reserve the first free slot for [player]; returns the assigned index.
     * Then mutates [Player.index] via the supplied setter callback so the assigned slot
     * is reflected on the player itself (PLAYER_INFO encoders read `Player.index` when
     * building bit-packed positions).
     *
     * Because [Player.index] is `var` (settable post-construction), callers must provide
     * the setter rather than us reading the property reference — this keeps the player
     * type free of reflection-based mutation.
     *
     * @throws IllegalStateException if all 2047 usable slots are filled.
     */
    fun allocate(player: Player, setIndex: (Int) -> Unit = {}): Int {
        for (i in 1 until slots.length()) {
            if (slots.compareAndSet(i, null, player)) {
                setIndex(i)
                return i
            }
        }
        error("Player pool exhausted")
    }

    fun release(index: Int) {
        slots.set(index, null)
    }

    fun get(index: Int): Player? = slots.get(index)

    /** Iterate every non-null player slot in index order. */
    fun forEach(block: (Player) -> Unit) {
        for (i in 1 until slots.length()) {
            slots.get(i)?.let(block)
        }
    }

    fun count(): Int {
        var n = 0
        forEach { n++ }
        return n
    }
}
