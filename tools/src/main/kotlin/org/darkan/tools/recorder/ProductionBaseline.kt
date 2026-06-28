package org.darkan.tools.recorder

/**
 * Reference per-(conn, dir, opcode) counts and representative body sizes from the *documented*
 * 948-5 production world-entry session, transcribed verbatim from
 * `claude-re/findings/12-opcode-map.md` (which was itself proven 100%-byte-complete in
 * `claude-re/findings/00-SESSION-SUMMARY.md`, iter29).
 *
 * This is NOT a protocol table — opcode *names* and *size classes* always come from the live core
 * [org.darkan.core.net.prot.Codec] (`register948()`), never from here. This object holds only the
 * *expected shape of a real session* (how many of each opcode a genuine world-entry produces, and a
 * representative body size) so the trust verifier can flag a capture whose population deviates wildly
 * from a known-good run — the signal that surfaced claude-re's own op78/op216/op22/op98 mislabels.
 *
 * A deviation here is a WARN, never a hard FAIL: a different account / a longer or shorter play
 * session legitimately changes counts. The check exists to answer "does this capture *look like* a
 * real RS3 world-entry?", not to assert an exact replay.
 */
object ProductionBaseline {

    /** One documented opcode population for a (conn, dir) plane. */
    data class Expected(val opcode: Int, val count: Int, val size: Int)

    /** `conn` + `dir` key, e.g. (`world`, `s2c`). */
    data class Plane(val conn: String, val dir: String)

    /**
     * The reference populations, keyed by plane. Lobby C2S / world C2S `size` columns are the
     * representative on-wire body sizes claude-re recorded; counts are exact for that session.
     */
    val planes: Map<Plane, List<Expected>> = mapOf(
        Plane("lobby", "s2c") to listOf(
            Expected(3, 1, 19), Expected(5, 1, 0), Expected(26, 1, 0), Expected(28, 438, 6),
            Expected(35, 4, 12), Expected(44, 29, 6), Expected(47, 12, 3), Expected(49, 1, 0),
            Expected(61, 1132, 3), Expected(64, 2, 6), Expected(75, 1, 0), Expected(80, 1, 1),
            Expected(82, 21, 23), Expected(110, 15, 5), Expected(128, 2, 0), Expected(147, 5, 10),
            Expected(216, 4, 430),
        ),
        Plane("lobby", "c2s") to listOf(
            Expected(5, 2, 4), Expected(52, 9, 6), Expected(54, 1, 4), Expected(218, 1, 70),
        ),
        Plane("world", "s2c") to listOf(
            Expected(1, 1, 0), Expected(3, 1, 19), Expected(5, 1, 0), Expected(7, 1, 0),
            Expected(12, 1, 2), Expected(13, 1, 1), Expected(16, 24, 2), Expected(17, 6, 9),
            Expected(22, 54, 68), Expected(26, 1, 0), Expected(28, 491, 6), Expected(30, 2, 8),
            Expected(35, 871, 12), Expected(44, 29, 6), Expected(45, 1, 1), Expected(46, 21, 5),
            Expected(47, 61, 3), Expected(49, 1, 0), Expected(52, 53, 76), Expected(54, 1, 44),
            Expected(55, 1, 0), Expected(61, 1176, 3), Expected(62, 2, 4), Expected(64, 2, 6),
            Expected(67, 1, 0), Expected(73, 1, 2), Expected(74, 1, 4), Expected(75, 1, 0),
            Expected(76, 64, 10), Expected(77, 1, 121), Expected(78, 616, 3), Expected(80, 1, 1),
            Expected(81, 1, 5137), Expected(82, 56, 23), Expected(85, 8, 17), Expected(90, 2, 6),
            Expected(91, 11, 5), Expected(92, 35, 3), Expected(93, 1, 73), Expected(95, 1, 5),
            Expected(104, 8, 14), Expected(110, 86, 15), Expected(119, 8, 35), Expected(120, 1, 0),
            Expected(121, 1, 11), Expected(122, 7, 205), Expected(130, 1, 10), Expected(147, 5, 10),
            Expected(154, 1, 5), Expected(157, 1, 1), Expected(162, 54, 0), Expected(172, 2, 1),
            Expected(174, 5, 8), Expected(190, 1, 0), Expected(199, 1, 93), Expected(204, 2, 1),
            Expected(209, 1, 0), Expected(216, 1, 430),
        ),
        Plane("world", "c2s") to listOf(
            Expected(3, 5, 9), Expected(5, 18, 4), Expected(8, 1, 4), Expected(12, 3, 58),
            Expected(51, 31, 0), Expected(52, 10, 6), Expected(54, 1, 4), Expected(76, 1, 4),
            Expected(94, 1, 3), Expected(98, 2, 91), Expected(105, 1, 1321), Expected(106, 4, 1),
            Expected(127, 1, 8), Expected(240, 1, 7),
        ),
    )

    /**
     * The mac recorder dylib tags connections `game` / `login` (by comparing the live
     * `ServerConnection*` against `ConnectionManager+0x18` / `+0x28`); claude-re's documented session
     * — and therefore this baseline — names the same two planes `world` / `lobby`. Normalize the
     * dylib's vocabulary to the baseline's so a real capture lines up.
     */
    private fun normalizeConn(conn: String): String = when (conn.lowercase()) {
        "game" -> "world"
        "login" -> "lobby"
        else -> conn.lowercase()
    }

    fun forPlane(conn: String, dir: String): List<Expected>? =
        planes[Plane(normalizeConn(conn), dir.lowercase())]
}
