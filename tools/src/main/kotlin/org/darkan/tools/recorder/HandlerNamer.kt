package org.darkan.tools.recorder

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File

/**
 * Handler-fingerprint opcode naming — name a packet by the CODE of its handler, not by its opcode
 * NUMBER, so the per-revision opcode scramble is absorbed automatically.
 *
 * ## Why
 * Across NXT client revisions the opcode→handler MAPPING is re-shuffled (op N points at a different
 * handler each build), but the handler FUNCTION itself is sig-stable: its first ~24 instructions
 * change only in relocated immediates (call/jmp targets, RIP-relative displacements, imm64s), which
 * are wildcarded in the fingerprint. We therefore identify a packet by *which handler the client's
 * own dispatch table points its opcode at*, then look that handler's bytes up in the name authority.
 *
 * ## Inputs
 *  - **`handler-sigs.json`** — the NAME AUTHORITY. Produced by the mac migrator
 *    (`RS3RecorderUpdaterMac.buildHandlerSigs`). 709 entries, each `{name, file_offset, blob_hex,
 *    mask_hex}` where `blob_hex`/`mask_hex` are the masked entry-bytes fingerprint padded/trimmed to
 *    `sig_bytes` (32). A mask byte of `0x00` is a wildcard (a relocated immediate); `0xFF` is
 *    significant. This is keyed by HANDLER, not opcode — it is rev-independent name truth.
 *  - **`prot-table.json`** — the per-CAPTURE dispatch table, emitted by the recorder dylib for the
 *    rev it captured. CONTRACT:
 *    ```
 *    { "build": "...", "server_prot": [ {op, size_class, handler, handler_sig}, ... ],
 *      "client_prot": [ ... ] }
 *    ```
 *    `handler` is `0x<vmaddr>` (informational); `handler_sig` is the hex of the FIRST 32 CONCRETE
 *    handler bytes (no wildcards — read straight from the live image). This is the rev-accurate
 *    `opcode → handler` edge for the captured build.
 *
 * ## Matching
 * For each prot-table opcode we take its concrete 32-byte `handler_sig` and find the handler-sigs.json
 * pattern whose mask-significant bytes equal the corresponding sig bytes (a [maskedHamming] of 0 — the
 * relocation wildcards mean the filled-in relocated immediates are ignored, so the SAME handler matches
 * across revs even though its call targets/displacements moved). The accepted match must be EXACT on
 * every significant byte AND UNIQUE: the 698 `jag::opcode::*` CS2 thunks have near-identical 32-byte
 * prologues (measured: 536/709 non-unique even at distance 0), so a non-unique or non-exact match is
 * refused rather than guessed — handler-naming never invents a wrong name. (A supervised tolerant path
 * exists behind a flag for cross-rev experiments; see [SigAuthority.match] / [SIG_MISMATCH_MAX].) The
 * result is an `opcode → name (+ size_class)` map for THIS capture, derived from the client's own table.
 */
class HandlerNamer private constructor(
    private val server: Map<Int, Resolved>,
    private val client: Map<Int, Resolved>,
    /** Build string from `prot-table.json` (for the trust/coverage report). */
    val build: String?,
    /** Build/version string from `handler-sigs.json` (the name-authority's source rev). */
    val sigVersion: String?,
    /** Total opcode→handler edges seen in the prot-table (named or not), per direction. */
    val serverOpcodeCount: Int,
    val clientOpcodeCount: Int,
) {
    /** One opcode's handler resolution: the matched handler name + its size class + match quality. */
    data class Resolved(
        val opcode: Int,
        val name: String,
        val sizeClass: Int,
        /** Masked-Hamming distance of the winning handler-sig match (0 == exact on all significant bytes). */
        val mismatch: Int,
        /** `0x<vmaddr>` of the handler from the prot-table (provenance only). */
        val handlerAddr: String?,
    )

    /** Handler-derived name for an s2c opcode, or null when the prot-table had no (matched) handler for it. */
    fun serverName(opcode: Int): String? = server[opcode]?.name

    /** Handler-derived name for a c2s opcode, or null when the prot-table had no (matched) handler for it. */
    fun clientName(opcode: Int): String? = client[opcode]?.name

    /** Handler-derived size class (fixed=N, varByte=-1, varShort=-2) for an s2c opcode, or null. */
    fun serverSizeClass(opcode: Int): Int? = server[opcode]?.sizeClass

    /** Handler-derived size class for a c2s opcode, or null. */
    fun clientSizeClass(opcode: Int): Int? = client[opcode]?.sizeClass

    fun serverResolved(): Map<Int, Resolved> = server
    fun clientResolved(): Map<Int, Resolved> = client

    /** Count of opcodes that resolved to a handler name, per direction. */
    val serverNamedCount: Int get() = server.size
    val clientNamedCount: Int get() = client.size

    companion object {
        /** Fingerprint width — must equal `handler-sigs.json.sig_bytes` and the dylib's `handler_sig` width. */
        const val SIG_BYTES = 32

        /**
         * Masked-Hamming slack for the OPTIONAL tolerant path (default OFF — see [load]'s
         * `allowTolerant`). Mirrors the magnitude of `RS3RecorderUpdaterMac.HANDLER_SIG_MISMATCH_MAX`
         * (4), but note the migrator uses that slack to RELOCATE a function it already knows the name
         * of, whereas NAMING an unknown handler from its bytes is a different, riskier problem.
         *
         * Measured on the 948 authority, the first 32 handler bytes are NOT discriminative for the 698
         * `jag::opcode::*` CS2 thunks — 536/709 entries are non-unique even at distance 0, and 622/709
         * fall within Hamming-4 of some OTHER name. So a tolerant match would routinely manufacture a
         * WRONG name. The packet handlers we actually name, by contrast, ALL self-identify uniquely at
         * distance 0. The naming policy is therefore EXACT-significant-byte + UNIQUE by default; the
         * tolerant path exists only for explicit, supervised cross-rev experiments and still demands a
         * unique winner with a strict margin over the runner-up.
         */
        const val SIG_MISMATCH_MAX = 4

        private val json = Json { ignoreUnknownKeys = true }

        /**
         * Build a [HandlerNamer] for a capture, or null when [protTable] is absent (callers then fall
         * back to the static codec). Throws only on a malformed name authority ([sigs] present but
         * unparseable), since that is a build-config error, not a per-capture condition.
         *
         * [allowTolerant] (default false) enables the supervised near-match path in [SigAuthority.match]
         * — leave it off for production naming; the exact-unique default is false-name-proof.
         */
        fun load(protTable: File, sigs: File, allowTolerant: Boolean = false): HandlerNamer? {
            if (!protTable.exists()) return null
            val authority = SigAuthority.load(sigs)
            val table = json.parseToJsonElement(protTable.readText()).jsonObject

            val build = table["build"]?.jsonPrimitive?.contentOrNull
            val (server, serverTotal) = resolveDirection(table["server_prot"], authority, allowTolerant)
            val (client, clientTotal) = resolveDirection(table["client_prot"], authority, allowTolerant)

            return HandlerNamer(
                server = server,
                client = client,
                build = build,
                sigVersion = authority.version,
                serverOpcodeCount = serverTotal,
                clientOpcodeCount = clientTotal,
            )
        }

        /** Resolve one prot-table direction array to `opcode -> Resolved`; returns (resolved, totalEdges). */
        private fun resolveDirection(arr: kotlinx.serialization.json.JsonElement?, authority: SigAuthority, allowTolerant: Boolean): Pair<Map<Int, Resolved>, Int> {
            if (arr == null) return emptyMap<Int, Resolved>() to 0
            val out = LinkedHashMap<Int, Resolved>()
            var total = 0
            for (el in arr.jsonArray) {
                val obj = el as? JsonObject ?: continue
                val op = obj["op"]?.jsonPrimitive?.intOrNull ?: continue
                total++
                val sizeClass = obj["size_class"]?.jsonPrimitive?.intOrNull ?: 0
                val sigHex = obj["handler_sig"]?.jsonPrimitive?.contentOrNull ?: continue
                val sig = parseHex(sigHex) ?: continue
                val match = authority.match(sig, allowTolerant) ?: continue
                out[op] = Resolved(
                    opcode = op,
                    name = match.name,
                    sizeClass = sizeClass,
                    mismatch = match.mismatch,
                    handlerAddr = obj["handler"]?.jsonPrimitive?.contentOrNull,
                )
            }
            return out to total
        }

        /** Parse an even-length hex string (no `0x`, no spaces) to bytes; null on malformed input. */
        internal fun parseHex(hex: String): ByteArray? {
            val s = hex.removePrefix("0x")
            if (s.isEmpty() || s.length % 2 != 0) return null
            val out = ByteArray(s.length / 2)
            var i = 0
            while (i < s.length) {
                val hi = Character.digit(s[i], 16)
                val lo = Character.digit(s[i + 1], 16)
                if (hi < 0 || lo < 0) return null
                out[i / 2] = ((hi shl 4) or lo).toByte()
                i += 2
            }
            return out
        }

        /**
         * Masked Hamming distance — bit-for-bit identical to the migrator's
         * `RS3RecorderUpdaterMac.maskedHamming`: count positions where the mask is significant and
         * the pattern byte differs from the target, plus any pattern length the target is short by.
         * (Both are padded to [SIG_BYTES] here, so the length term is normally 0.)
         */
        internal fun maskedHamming(pattern: ByteArray, mask: ByteArray, target: ByteArray): Int {
            val len = minOf(pattern.size, mask.size, target.size)
            var mis = 0
            for (i in 0 until len) {
                if (mask[i].toInt() and 0xFF == 0) continue
                if (pattern[i] != target[i]) mis++
            }
            return mis + maxOf(0, pattern.size - len)
        }
    }

    /**
     * The name authority: the handler-sigs.json patterns, indexed for matching. Holds each entry's
     * 32-byte blob + mask and resolves a concrete capture sig to the best-matching handler name.
     */
    class SigAuthority private constructor(
        val version: String?,
        private val entries: List<Entry>,
        /** Anchor-byte bucket: first significant byte value -> entries (most patterns start `55`/`48`). */
        private val byAnchor: Map<Int, List<Entry>>,
    ) {
        class Entry(val name: String, val blob: ByteArray, val mask: ByteArray) {
            /** Index of the first significant (mask != 0) byte; -1 if fully wildcarded (never, in practice). */
            val anchor: Int = mask.indexOfFirst { it.toInt() and 0xFF != 0 }
        }

        data class Match(val name: String, val mismatch: Int)

        /**
         * Best handler match for a concrete capture [sig], or null when there is no SAFE unique match.
         *
         * The dylib writes `handler_sig` straight from the function ENTRY, so byte 0 is the prologue's
         * first opcode (`0x55 push rbp` for the standard frame). We scan the entries whose own anchor
         * byte equals the sig's byte at that anchor index, computing each one's masked-Hamming.
         *
         * Policy (see [SIG_MISMATCH_MAX] for the measurement that motivates it):
         *  - **Default (`allowTolerant=false`)**: accept ONLY a distance-0 match (every significant
         *    byte equal) that is UNIQUE. A non-unique distance-0 is two handlers with an identical
         *    significant skeleton — refuse. This is exact and false-name-proof; all real packet
         *    handlers self-identify uniquely at distance 0.
         *  - **Tolerant (`allowTolerant=true`, supervised only)**: if there is no distance-0 match,
         *    accept the lowest-distance match within [SIG_MISMATCH_MAX] ONLY IF it is the sole entry at
         *    that distance AND the runner-up is strictly farther (a clear margin) — so a near-twin
         *    cluster still refuses rather than guessing.
         */
        fun match(sig: ByteArray, allowTolerant: Boolean = false): Match? {
            val padded = if (sig.size >= SIG_BYTES) sig else sig.copyOf(SIG_BYTES)
            // Tally how many entries sit at each masked-Hamming distance, and remember a winner per
            // distance, so we can reason about uniqueness AND the margin to the runner-up.
            var bestEntry: Entry? = null
            var bestMis = Int.MAX_VALUE
            var bestTies = 0
            var secondMis = Int.MAX_VALUE // lowest distance strictly greater than bestMis

            fun consider(e: Entry) {
                if (e.anchor < 0 || e.anchor >= padded.size) return
                if (e.blob[e.anchor] != padded[e.anchor]) return // anchor byte must agree
                val mis = maskedHamming(e.blob, e.mask, padded)
                when {
                    mis < bestMis -> { secondMis = bestMis; bestMis = mis; bestEntry = e; bestTies = 1 }
                    mis == bestMis -> bestTies++
                    mis < secondMis -> secondMis = mis
                }
            }

            // Anchor bucket (byte-0 prologue) is the overwhelming common case; sweep all only if it
            // produced nothing, so correctness never hinges on the bucketing heuristic.
            byAnchor[padded[0].toInt() and 0xFF]?.let { bucket -> for (e in bucket) consider(e) }
            if (bestEntry == null) for (e in entries) consider(e)

            val winner = bestEntry ?: return null

            // Exact + unique is the only accepted outcome by default.
            if (bestMis == 0) return if (bestTies == 1) Match(winner.name, 0) else null
            if (!allowTolerant) return null

            // Supervised tolerant path: unique at its distance, within tolerance, and clearly ahead.
            if (bestMis <= SIG_MISMATCH_MAX && bestTies == 1 && secondMis > bestMis) return Match(winner.name, bestMis)
            return null
        }

        companion object {
            private val json = Json { ignoreUnknownKeys = true }

            fun load(file: File): SigAuthority {
                require(file.exists()) { "handler-sigs.json not found: ${file.absolutePath}" }
                val root = json.parseToJsonElement(file.readText()).jsonObject
                val version = root["version"]?.jsonPrimitive?.contentOrNull
                val sigBytes = root["sig_bytes"]?.jsonPrimitive?.intOrNull ?: SIG_BYTES
                val arr = root["handlers"]?.jsonArray
                    ?: error("handler-sigs.json has no `handlers` array")
                val entries = ArrayList<Entry>(arr.size)
                for (el in arr) {
                    val obj = el as? JsonObject ?: continue
                    val name = obj["name"]?.jsonPrimitive?.contentOrNull ?: continue
                    val blobHex = obj["blob_hex"]?.jsonPrimitive?.contentOrNull ?: continue
                    val maskHex = obj["mask_hex"]?.jsonPrimitive?.contentOrNull ?: continue
                    val blob = parseHex(blobHex) ?: continue
                    val mask = parseHex(maskHex) ?: continue
                    // Normalize every entry to SIG_BYTES so masking + Hamming are width-stable
                    // regardless of `sig_bytes` drift in the authority file.
                    entries += Entry(name, blob.copyOf(SIG_BYTES), mask.copyOf(SIG_BYTES))
                }
                require(entries.isNotEmpty()) { "handler-sigs.json `handlers` produced no usable entries" }
                if (sigBytes != SIG_BYTES) {
                    System.err.println(
                        "[handler-namer] WARN handler-sigs.json sig_bytes=$sigBytes != $SIG_BYTES — " +
                            "fingerprints normalized to $SIG_BYTES bytes"
                    )
                }
                val byAnchor = entries.groupBy { e ->
                    if (e.anchor in e.blob.indices) e.blob[e.anchor].toInt() and 0xFF else -1
                }
                return SigAuthority(version, entries, byAnchor)
            }

            // Build directly from in-memory entries (for self-tests, so they need no temp authority file).
            fun of(version: String?, entries: List<Triple<String, ByteArray, ByteArray>>): SigAuthority {
                val es = entries.map { (n, b, m) -> Entry(n, b.copyOf(SIG_BYTES), m.copyOf(SIG_BYTES)) }
                val byAnchor = es.groupBy { e ->
                    if (e.anchor in e.blob.indices) e.blob[e.anchor].toInt() and 0xFF else -1
                }
                return SigAuthority(version, es, byAnchor)
            }
        }
    }
}
