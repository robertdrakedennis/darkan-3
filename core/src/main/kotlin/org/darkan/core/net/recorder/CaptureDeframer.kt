package org.darkan.core.net.recorder

import org.darkan.core.net.Isaac
import org.darkan.core.net.prot.Codec

/**
 * Independent, offline deframer for a raw recorder capture (`raw-<role>-<dir>.bin` + ISAAC seeds).
 *
 * This is the validated logic promoted out of the `DecodeCapture` tool: ISAAC-decipher the opcode
 * (1 or 2 bytes), resolve its size class from the active [Codec], read the body, repeat. It runs
 * the SAME deframe the in-process recorder dylib runs, but from an independent code path — diffing
 * the two catches opcode mislabels and size-class drift.
 *
 * Direction conventions match the capture contract:
 *  - **c2s** ISAAC is seeded with the raw send seeds.
 *  - **s2c** ISAAC is seeded with `rawSeed + 50` per element (the `isaac_delta 50` rule).
 *
 * That `+50` is applied for [Direction.S2C] only in the legacy single-seed path. When a per-plane
 * seed from `isaac-keys.txt` (one line per `(conn, dir)`) is supplied — where a `-s2c` line ALREADY
 * encodes the `+50` — construct with [seedIsExactCipherSeed] = true so the seed is used VERBATIM and
 * the delta is NOT re-applied.
 *
 * The underlying [Isaac] reproduces the client's `jag::Isaac::Init` key schedule **byte-for-byte**
 * (verified against the binary: seed in `randrsl[0..3]`, the 4-round golden-ratio pre-scramble, the
 * two `randmem` passes, first-pool generate, `randcnt=256`, and the top-down `randrsl[--randcnt]`
 * read order). So a byte-0 desync is NEVER the cipher math — it means either the captured plane is
 * not ISAAC ciphertext (see [plaintext]), the capture started mid-keystream (see [keystreamSkip]),
 * or the supplied seed does not belong to the connection that enciphered these bytes.
 *
 * Size class (from [Codec.serverProtSize] / [Codec.clientProtSize]):
 *  - `>= 0` fixed body length
 *  - `-1` varByte (1-byte g1 length prefix)
 *  - `-2` varShort (2-byte g2 length prefix)
 */
class CaptureDeframer(
    private val codec: Codec,
    private val direction: Direction,
    isaacSeed: IntArray,
    /**
     * Opcodes whose body the client additionally XTEA-encrypts (c2s only; e.g. the
     * `FUN_1000c4060` special senders). The body cannot be decrypted offline without the
     * ConnectionManager tinyKey, so a frame for one of these is emitted with [Frame.xteaBody] set
     * and its raw (still-encrypted) body passed through rather than treated as a failure.
     */
    private val xteaBodyOpcodes: Set<Int> = emptySet(),
    /**
     * When true the opcode bytes are read **verbatim** (identity keystream — no ISAAC subtraction).
     * Use this when the captured plane carries *plaintext* opcodes rather than ISAAC ciphertext
     * (e.g. a recorder hook that taps the buffer before the opcode is enciphered). In this mode
     * [isaacSeed] is ignored and no ISAAC is constructed.
     */
    private val plaintext: Boolean = false,
    /**
     * When true, [isaacSeed] is the **exact 4-int seed the cipher was constructed with** for THIS
     * plane, and ISAAC is built from it **verbatim** in BOTH directions — the internal
     * [S2C_ISAAC_DELTA] (`+50`) is NOT applied.
     *
     * Use this with the per-`(conn, dir)` seeds the recorder now writes (one line per plane in
     * `isaac-keys.txt`): a `-s2c` line already encodes the `recv = send + 50` rule (the dylib stores
     * exactly what `Isaac::Init` was called with), so adding the delta again would desync. A `-c2s`
     * line holds the raw send keys, which the verbatim path also seeds directly — identical to the
     * legacy `Direction.C2S` behaviour.
     *
     * Default `false` preserves the legacy single-seed contract (raw send seed; the deframer adds
     * `+50` for [Direction.S2C]) used by [DecodeCapture] and the self-tests.
     */
    private val seedIsExactCipherSeed: Boolean = false,
    /**
     * Number of keystream values to burn from the (recv/send) ISAAC before the first opcode byte —
     * i.e. the keystream POSITION at which this captured byte stream begins. The game/login ISAAC is
     * positional and continuous across the whole connection, so a socket capture that started
     * mid-stream (after earlier packets already consumed keystream) must skip that many values to
     * realign. 0 means the capture begins at keystream position 0 (the connection's first packet).
     * Ignored when [plaintext] is true.
     */
    keystreamSkip: Int = 0,
) {
    enum class Direction { S2C, C2S }

    private val isaac: Isaac? = if (plaintext) null else Isaac(
        when {
            // Per-plane seed: build verbatim in both directions — the line already encodes the
            // +50 for s2c (recv = send + 50 was applied by the client before Isaac::Init).
            seedIsExactCipherSeed -> isaacSeed.copyOf()
            direction == Direction.S2C -> IntArray(isaacSeed.size) { isaacSeed[it] + S2C_ISAAC_DELTA }
            else -> isaacSeed.copyOf()
        }
    ).also { ks -> repeat(keystreamSkip) { ks.nextInt() } }

    /** Next opcode-cipher value: the ISAAC keystream, or 0 in [plaintext] mode (identity). */
    private fun nextKey(): Int = if (plaintext) 0 else isaac!!.nextInt()

    /**
     * A single deframed packet. [body] is the post-opcode, post-length-prefix payload (the same
     * bytes the codec decoders expect). [xteaBody] is true when the payload is the XTEA-encrypted
     * form (see [xteaBodyOpcodes]) and so was NOT decrypted.
     */
    data class Frame(
        val opcode: Int,
        val name: String,
        val sizeClass: Int,
        val offset: Int,
        val body: ByteArray,
        val xteaBody: Boolean,
    )

    sealed interface Result {
        data class Ok(val frame: Frame) : Result
        /** Stream is exhausted at a clean packet boundary. */
        data object End : Result
        /**
         * The deframe cannot continue: ISAAC desync (opcode out of range), a length prefix or body
         * that runs past the buffer, etc. [offset] is where the bad frame started.
         */
        data class Error(val offset: Int, val detail: String) : Result
    }

    private var pos = 0

    /** Manually advance the cursor (e.g. to skip a pre-prot login prologue before deframing). */
    fun skip(bytes: Int) {
        pos += bytes
    }

    fun position(): Int = pos

    /** Decode the next frame from [raw], advancing the internal cursor. */
    fun next(raw: ByteArray): Result {
        if (pos >= raw.size) return Result.End
        val frameStart = pos

        val rawByte = raw[pos].toInt() and 0xFF; pos++
        val decoded = (rawByte - nextKey()) and 0xFF
        val opcode: Int = if (decoded < 128) {
            decoded
        } else {
            if (pos >= raw.size) return Result.Error(frameStart, "truncated 2-byte opcode")
            val rawByte2 = raw[pos].toInt() and 0xFF; pos++
            val decoded2 = (rawByte2 - nextKey()) and 0xFF
            (decoded - 128) * 256 + decoded2
        }

        val sizeClass = when (direction) {
            Direction.S2C -> codec.serverProtSize(opcode)
            Direction.C2S -> codec.clientProtSize(opcode)
        }
        // An opcode with no metadata entry resolves to size 0; an out-of-range opcode is a desync.
        if (opcode !in 0..MAX_OPCODE) {
            return Result.Error(frameStart, "opcode $opcode out of range (ISAAC desync?)")
        }

        val bodyLen: Int = when (sizeClass) {
            SIZE_VAR_BYTE -> {
                if (pos >= raw.size) return Result.Error(frameStart, "truncated varByte length (op=$opcode)")
                (raw[pos].toInt() and 0xFF).also { pos++ }
            }
            SIZE_VAR_SHORT -> {
                if (pos + 1 >= raw.size) return Result.Error(frameStart, "truncated varShort length (op=$opcode)")
                (((raw[pos].toInt() and 0xFF) shl 8) or (raw[pos + 1].toInt() and 0xFF)).also { pos += 2 }
            }
            else -> sizeClass
        }

        if (bodyLen < 0 || pos + bodyLen > raw.size) {
            return Result.Error(frameStart, "body of $bodyLen runs past buffer (op=$opcode, ${raw.size - pos} left)")
        }

        val body = raw.copyOfRange(pos, pos + bodyLen)
        pos += bodyLen

        val name = when (direction) {
            Direction.S2C -> codec.serverProtName(opcode)
            Direction.C2S -> codec.clientProtName(opcode)
        }
        val xtea = direction == Direction.C2S && opcode in xteaBodyOpcodes
        return Result.Ok(Frame(opcode, name, sizeClass, frameStart, body, xtea))
    }

    companion object {
        const val S2C_ISAAC_DELTA = 50
        const val MAX_OPCODE = 217
        const val SIZE_VAR_BYTE = -1
        const val SIZE_VAR_SHORT = -2

        /** Role tag of a per-plane seed line: `<role>-<dir>`, e.g. `game-s2c`, `login-c2s`. */
        private val ROLE_TAG = Regex("[A-Za-z0-9]+-(s2c|c2s)")

        /**
         * Parse the first `ISAAC keys (hex): 0x..,0x..,0x..,0x..` line from a recorder
         * `isaac-keys.txt`. This is the **legacy single-seed** contract: it returns whichever `hex`
         * line comes first and is used by [DecodeCapture] and the self-tests, which seed one ISAAC
         * (raw send seed; the deframer adds `+50` for [Direction.S2C]).
         *
         * Prefer [parseHexKeysByRole] for the new multi-line format (one verbatim seed per plane).
         */
        fun parseHexKeys(isaacKeysText: String): IntArray {
            val line = isaacKeysText.lineSequence().firstOrNull { "hex" in it }
                ?: error("no 'ISAAC keys (hex)' line in isaac-keys.txt")
            return parseFourKeys(line)
        }

        /**
         * Parse the **per-`(conn, dir)` seeds** the recorder now writes: one `hex` line per plane,
         * tagged by its first whitespace token (e.g. `game-s2c ISAAC keys (hex): 0x..,..`). Returns a
         * map keyed by that `<role>-<dir>` tag (`"game-s2c"`, `"game-c2s"`, `"login-s2c"`,
         * `"login-c2s"`, …); each value is the EXACT 4-int seed that plane's ISAAC was constructed
         * with — a `-s2c` line ALREADY includes the `+50` per int, so it must be seeded VERBATIM
         * (build the deframer with `seedIsExactCipherSeed = true`).
         *
         * A legacy untagged `ISAAC keys (hex): 0x..` line (the self-tests' / [DecodeCapture]'s single
         * line) has no `<role>-<dir>` first token, so it is NOT placed in the map under a bogus role;
         * callers fall back to [parseHexKeys] for that default seed.
         */
        fun parseHexKeysByRole(isaacKeysText: String): Map<String, IntArray> {
            val out = LinkedHashMap<String, IntArray>()
            for (raw in isaacKeysText.lineSequence()) {
                val line = raw.trim()
                if (line.isEmpty() || line.startsWith("#") || "hex" !in line) continue
                val tag = line.split(Regex("\\s+")).firstOrNull() ?: continue
                if (!ROLE_TAG.matches(tag)) continue // legacy untagged line — not a per-plane seed
                out[tag] = parseFourKeys(line)
            }
            return out
        }

        /** Pull the four `0x..` words out of one `hex` line. */
        private fun parseFourKeys(line: String): IntArray =
            Regex("0x([0-9A-Fa-f]+)").findAll(line)
                .map { it.groupValues[1].toLong(16).toInt() }
                .toList()
                .toIntArray()
                .also { require(it.size == 4) { "expected 4 ISAAC keys, got ${it.size}" } }
    }
}
