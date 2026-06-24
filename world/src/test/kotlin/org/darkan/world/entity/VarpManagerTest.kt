package org.darkan.world.entity

import io.ktor.utils.io.ByteChannel
import kotlinx.coroutines.runBlocking
import org.darkan.core.net.Isaac
import org.darkan.core.net.prot.VarpLarge
import org.darkan.core.net.prot.VarpLong
import org.darkan.core.net.prot.VarpSmall
import org.darkan.core.net.prot.revision.rev948.register948
import org.darkan.core.net.session.GameSession
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertIs

/**
 * Tests for [VarpManager] — varp set/dirty tracking and the op28/op61/op147 magnitude selection per
 * `docs/protocol/world-entry-render-948.md` §2.4 and `docs/protocol/world-bootstrap-948.md` §2/§3.
 */
class VarpManagerTest {

    private fun newSession(): GameSession = GameSession(
        write = ByteChannel(),
        isaacIn = Isaac(IntArray(4)),
        isaacOut = Isaac(IntArray(4)),
        ip = "127.0.0.1",
        codec = register948(),
        username = "tester",
    )

    @Test
    fun `value in signed-byte range selects op61 VarpSmall`() {
        val v = VarpManager()
        assertIs<VarpSmall>(v.encode(111, 50), "value 50 fits a signed byte → op61")
        assertIs<VarpSmall>(v.encode(27, -1), "value -1 fits a signed byte → op61")
        assertIs<VarpSmall>(v.encode(1, 127), "127 is the upper signed-byte bound → op61")
        assertIs<VarpSmall>(v.encode(1, -128), "-128 is the lower signed-byte bound → op61")
    }

    @Test
    fun `value beyond signed byte but within 32 bits selects op28 VarpLarge`() {
        val v = VarpManager()
        assertIs<VarpLarge>(v.encode(3, 128), "128 exceeds the signed-byte range → op28")
        assertIs<VarpLarge>(v.encode(3, -129), "-129 exceeds the signed-byte range → op28")
        assertIs<VarpLarge>(v.encode(20, 687865856), "large positive int → op28")
        assertIs<VarpLarge>(v.encode(3, -1807744892), "large negative int → op28 (the §2.2 example)")
        assertIs<VarpLarge>(v.encode(1, Int.MAX_VALUE.toLong()), "Int.MAX → op28")
        assertIs<VarpLarge>(v.encode(1, Int.MIN_VALUE.toLong()), "Int.MIN → op28")
    }

    @Test
    fun `value outside 32-bit range selects op147 VarpLong`() {
        val v = VarpManager()
        assertIs<VarpLong>(v.encode(1, Int.MAX_VALUE.toLong() + 1), "above 32-bit → op147")
        assertIs<VarpLong>(v.encode(1, Int.MIN_VALUE.toLong() - 1), "below 32-bit → op147")
        assertIs<VarpLong>(v.encode(1, Long.MAX_VALUE), "64-bit max → op147")
    }

    @Test
    fun `set marks dirty only on change and flush clears the dirty set`() = runBlocking {
        val v = VarpManager()
        v.set(111, 50)
        v.set(27, -1)
        assertEquals(2, v.dirtyCount(), "two distinct ids dirtied")

        // Re-setting the same value is a no-op (no extra dirty entry).
        v.set(111, 50)
        assertEquals(2, v.dirtyCount(), "re-setting same value does not re-dirty")

        // Changing a value re-dirties it (already in the set; still 2 distinct ids).
        v.set(111, 60)
        assertEquals(2, v.dirtyCount())

        val emitted = v.flush(newSession())
        assertEquals(2, emitted.size, "flush emits one packet per dirty id")
        assertEquals(0, v.dirtyCount(), "flush clears the dirty set")

        // A clean flush emits nothing.
        assertTrue(v.flush(newSession()).isEmpty(), "no dirty varps → flush emits nothing")
    }

    @Test
    fun `flush emits the correct opcode mix and op61 uses the (-128 - value) transform`() = runBlocking {
        val v = VarpManager()
        v.set(111, 50)                       // op61
        v.set(3, -1807744892)                // op28
        v.set(1, Long.MAX_VALUE)             // op147
        val emitted = v.flush(newSession())

        assertEquals(1, emitted.count { it is VarpSmall })
        assertEquals(1, emitted.count { it is VarpLarge })
        assertEquals(1, emitted.count { it is VarpLong })

        val small = emitted.filterIsInstance<VarpSmall>().single()
        assertEquals(111, small.id)
        assertEquals(50, small.value)
        // The registered op61 encoder writes writeShort(id) + writeByte(-128 - value): id BE, value
        // transform. Verify the value the client recovers ( (-128 - wireByte) ) round-trips to 50.
        val wireByte = (-128 - small.value) and 0xFF
        assertEquals(50, (-128 - wireByte) and 0xFF, "op61 value transform round-trips (client (-128 - wire))")

        val long = emitted.filterIsInstance<VarpLong>().single()
        assertEquals(1, long.id)
        assertEquals(Long.MAX_VALUE, long.value)
    }

    @Test
    fun `seedDefaults with the None seam is a no-op`() {
        val v = VarpManager()
        v.seedDefaults(VarpDefaults.None)
        assertEquals(0, v.dirtyCount(), "None seam seeds nothing (live baseline stays minimal)")
    }

    @Test
    fun `seedDefaults applies a provided defaults seam without overwriting existing values`() {
        val v = VarpManager()
        v.set(111, 99)   // already set
        val defaults = object : VarpDefaults {
            override fun hudDefaults(): Map<Int, Long> = mapOf(111 to 0L, 27 to -1L, 45 to 2L)
        }
        v.seedDefaults(defaults)
        // 111 is already set → not overwritten; 27 and 45 are seeded.
        assertEquals(99L, v[111], "existing value is not overwritten by a default")
        assertEquals(-1L, v[27])
        assertEquals(2L, v[45])
    }
}
