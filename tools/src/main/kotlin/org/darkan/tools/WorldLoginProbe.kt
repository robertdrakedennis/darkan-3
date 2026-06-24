package org.darkan.tools

import org.darkan.core.EnvVars
import org.darkan.core.net.Isaac
import org.darkan.core.net.login.LoginToken
import org.darkan.core.net.prot.Codec
import org.darkan.core.net.prot.revision.rev948.register948
import world.gregs.voidps.buffer.write.BufferWriter
import world.gregs.voidps.cache.secure.RSA
import world.gregs.voidps.cache.secure.Xtea
import java.io.InputStream
import java.math.BigInteger
import java.net.InetSocketAddress
import java.net.Socket
import kotlin.system.exitProcess

/**
 * Protocol-level world-login probe.
 *
 * Simulates exactly what the NXT client sends after `SetMainState(0x25)` (the lobby→world hop):
 * it opens a TCP connection to the world server, runs the 3-step world-login handshake, and
 * asserts each milestone with the raw bytes. This is the headless stand-in for a human "Play Now"
 * click — the macOS client has no auto-login, so the world handshake is verified here at the wire
 * level instead.
 *
 * Verifies, in order:
 *  1. INIT: connection-type byte 14 (CONNECT_LOGIN) → server replies the FULL 9 bytes
 *     (1 status + 8 server seed). A 1-byte reply is the silent-stall bug this asserts is fixed.
 *  2. GAMELOGIN: send login opcode 16 (LOGIN) + size + [version, has-extra byte, RSA block (e=65537),
 *     XTEA tail] in the exact layout WorldServer.initWorldLogin parses.
 *  3. RESULT: server replies status byte 2 (SUCCESS).
 *  3b. THREE-PART RESPONSE (the §9 fix): the world client parses a fixed pre-ISAAC stream after
 *     SUCCESS — Part A server-client-var block `[u16 len][u8 ackFlag][u16 ids...]`, Part B players
 *     byte `02`, Part C `[u8 len][00 leadFlag][WorldLoginDetails body][u16 reserved][u32 time]
 *     [u64 sid1][u64 sid2]`. Asserts Part A is raw length-prefixed data, not a framed server packet,
 *     Part B == 2, and Part C decodes field-for-field with a correct 1-byte length prefix.
 *  4a. VARC (in-game op 5): §9 supersedes §8 — the in-game RESET_ALL_VARPS op 5 is NOT the crash
 *     source (the crash was the response framing, asserted in 3b). Production 948 captures it as an
 *     empty fixed-size packet; walking it here confirms the post-ISAAC burst frames cleanly.
 *  4b. REBUILD: de-ISAAC the world-init burst (S2C seed = isaacKeys + 50), walk it with the exact
 *     opcode/size framing FramingRegression uses, find REBUILD_NORMAL_SIMPLE (op 81), and decode its
 *     two packed build-area coords through the live client's BuildArea::DecodePackedCoord logic
 *     (`word = (plane<<28)|(field1<<14)|field0`, then each field `>> 6` before SceneManager).
 *     Asserts packed A/B form non-inverted build-area bounds that contain the spawn region and leave
 *     enough local-zone room for the 13x13 scene stream.
 *
 * The RSA block is encrypted with (worldRsaModulus, e=65537); the server decrypts with the private
 * exponent. The username is carried in the XTEA tail and the lobby-issued compact LoginToken is
 * carried in the two session-token longs the real client forwards during the world hop.
 *
 * Run: ./gradlew :tools:worldLoginProbe [-PworldHost=localhost -PworldPort=43597 -PprobeUser=probeplayer]
 * Exit 0 on full success; nonzero with a diagnostic on the first failed milestone.
 */

private fun hex(b: ByteArray, n: Int = b.size) =
    b.copyOf(minOf(n, b.size)).joinToString(" ") { "%02x".format(it) }

private fun fail(msg: String): Nothing {
    System.err.println("[PROBE FAIL] $msg")
    exitProcess(1)
}

/** Read exactly [n] bytes or fail (the burst is a continuous stream — never under-read). */
private fun readN(input: InputStream, n: Int, what: String): ByteArray {
    val b = ByteArray(n)
    var r = 0
    while (r < n) {
        val k = input.read(b, r, n - r)
        if (k < 0) fail("$what: stream closed after $r/$n bytes")
        r += k
    }
    return b
}

/**
 * Mirror of `jag::game::BuildArea::DecodePackedCoord` @ 0x006d4320.
 */
private data class DecodedCoord(val plane: Int, val regionX: Int, val regionZ: Int)

private fun decodePackedCoord(word: Int): DecodedCoord {
    if (word == -1 /* 0xFFFFFFFF */) return DecodedCoord(-1, 0, 0)
    val field0 = word and 0x3FFF
    val field1 = (word ushr 14) and 0x3FFF
    val plane = (word ushr 28) and 0x3
    return DecodedCoord(plane = plane, regionX = field1 ushr 6, regionZ = field0 ushr 6)
}

private val MODULUS = BigInteger(EnvVars.worldRsaModulus)
private val PUBLIC_EXP = BigInteger.valueOf(65537L) // client encrypts with the PUBLIC exponent
private val ISAAC_KEYS = intArrayOf(0x11111111, 0x22222222, 0x33333333, 0x44444444)

/**
 * Lobby-login preamble (opcode 19) — the REAL flow creates the account on first lobby login. This
 * also serves as a regression check that the lobby login still works after the IfButton/encoder
 * changes. Lobby login is byte-identical to world login EXCEPT: inner opcode 19, NO has-extra byte,
 * and the RSA block carries a password (the lobby creates the account with it).
 */
private fun lobbyLoginToCreateAccount(host: String, lobbyPort: Int, username: String, password: String) {
    Socket().use { sock ->
        sock.connect(InetSocketAddress(host, lobbyPort), 5000)
        sock.soTimeout = 8000
        val out = sock.getOutputStream(); val input = sock.getInputStream()

        out.write(14); out.flush() // CONNECT_LOGIN
        val first = ByteArray(9); var r = 0
        while (r < 9) { val n = input.read(first, r, 9 - r); if (n < 0) break; r += n }
        if (r != 9) fail("LOBBY INIT: expected 9 bytes, got $r: ${hex(first, r)}")
        println("[PROBE OK]   Lobby INIT: 9-byte first response: ${hex(first)}")

        val rsaInner = BufferWriter(160).apply {
            writeByte(10)
            for (k in ISAAC_KEYS) writeInt(k)
            writeLong(0L)             // sessionCheck
            writeString("")           // authToken
            writeString(password)     // password (lobby creates account with this)
            writeLong(0L); writeLong(0L)
        }.toArray()
        val rsaEnc = RSA.crypt(rsaInner, MODULUS, PUBLIC_EXP)

        val tailPlain = BufferWriter(64).apply {
            writeByte(1)              // stringUsername
            writeString(username)
        }.toArray()
        val padded = if (tailPlain.size % 8 == 0) tailPlain else tailPlain.copyOf(tailPlain.size + (8 - tailPlain.size % 8))
        Xtea.encipher(padded, 0, padded.size - (padded.size % 8), ISAAC_KEYS)

        val body = BufferWriter(512).apply {
            writeInt(EnvVars.majorVersion); writeInt(EnvVars.minorVersion)
            writeShort(rsaEnc.size); writeBytes(rsaEnc); writeBytes(padded)
        }.toArray()
        out.write(19) // LOBBY
        out.write((body.size ushr 8) and 0xFF); out.write(body.size and 0xFF)
        out.write(body); out.flush()

        val result = input.read()
        if (result != 2) fail("LOBBY RESULT: expected SUCCESS(2), got $result — lobby login/account-create failed")
        println("[PROBE OK]   Lobby login SUCCESS(2) — account '$username' now exists")
        // Don't bother reading the full lobby init burst; we have what we need (the account).
    }
}

fun main(args: Array<String>) {
    val host = System.getProperty("worldHost") ?: args.getOrNull(0) ?: "localhost"
    val port = (System.getProperty("worldPort") ?: args.getOrNull(1) ?: EnvVars.worldPort.toString()).toInt()
    // Default to a UNIQUE per-run username. The world server keeps a session in `playersByUsername`
    // (and the name in `pendingLogins`) until its async session loop observes the socket close, which
    // lags the probe's exit by several seconds. A fixed username therefore makes a back-to-back re-run
    // false-fail with result byte 9 (LOGIN_LIMIT_EXCEEDED) even though the framing is correct. A unique
    // name avoids that collision; pass -PprobeUser=<name> to pin a specific account.
    val username = System.getProperty("probeUser") ?: args.getOrNull(2) ?: "probe${System.currentTimeMillis()}"
    val lobbyPort = (System.getProperty("lobbyPort") ?: EnvVars.lobbyPort.toString()).toInt()
    val password = System.getProperty("probePass") ?: "probepass123"

    println("WorldLoginProbe → world $host:$port as '$username' (lobby $host:$lobbyPort)")

    // Create the account via the real lobby-login path first (idempotent: a second login just
    // re-authenticates). This is what the live client does before the world hop.
    lobbyLoginToCreateAccount(host, lobbyPort, username, password)

    val modulus = MODULUS
    val publicExp = PUBLIC_EXP
    val isaacKeys = ISAAC_KEYS

    Socket().use { sock ->
        sock.connect(InetSocketAddress(host, port), 5000)
        sock.soTimeout = 8000
        val out = sock.getOutputStream()
        val input = sock.getInputStream()

        // --- Step 1: INIT — connection type 14, expect 9-byte first response ---
        out.write(14) // RequestOpcode.CONNECT_LOGIN
        out.flush()

        val first = ByteArray(9)
        var read = 0
        try {
            while (read < 9) {
                val r = input.read(first, read, 9 - read)
                if (r < 0) break
                read += r
            }
        } catch (e: Exception) {
            fail("INIT: timed out reading the first response after ${read} byte(s) — " +
                "world likely sent a short (1-byte) reply and the client/probe blocks waiting for " +
                "the missing seed bytes. Got: ${hex(first, read)} (${e::class.simpleName})")
        }
        if (read != 9) fail("INIT: expected 9-byte first response, got $read byte(s): ${hex(first, read)}")
        if (first[0].toInt() != 0) fail("INIT: status byte != 0 (got ${first[0].toInt() and 0xFF}) — server rejected the connection")
        // The 8 bytes after the status are the world's server seed (stored client-side at
        // LoginManager+0x40). The real client echoes this back as the RSA session-check value,
        // so capture it here and echo it below — the probe MUST exercise the same non-zero
        // session-check path the real client does (a zero here sailed past the old `!= 0` reject
        // and falsely "passed" while the real client failed).
        val serverSeed = first.copyOfRange(1, 9)
        val sessionCheck = ((serverSeed[0].toLong() and 0xFF) shl 56) or
            ((serverSeed[1].toLong() and 0xFF) shl 48) or
            ((serverSeed[2].toLong() and 0xFF) shl 40) or
            ((serverSeed[3].toLong() and 0xFF) shl 32) or
            ((serverSeed[4].toLong() and 0xFF) shl 24) or
            ((serverSeed[5].toLong() and 0xFF) shl 16) or
            ((serverSeed[6].toLong() and 0xFF) shl 8) or
            (serverSeed[7].toLong() and 0xFF)
        if (sessionCheck == 0L) fail("INIT: server seed decoded to 0 — cannot exercise the non-zero session-check path the real client uses")
        println("[PROBE OK]   Step 1 INIT: 9-byte first response received (status=0 + 8 seed): ${hex(first)} " +
            "(echoing seed as non-zero sessionCheck=0x${"%016x".format(sessionCheck)})")

        // --- Step 2: GAMELOGIN — build the login packet exactly as WorldServer parses it ---
        // RSA inner block: magic(10) + 4 isaac ints + long sessionCheck + credential tail.
        // sessionCheck echoes the server seed from the INIT reply — NON-ZERO, exactly like the real
        // client. WorldServer reads + logs it and does NOT reject (mirrors the lobby), so the probe
        // now walks the same path the real NXT client does instead of the old zero shortcut.
        val loginToken = LoginToken.issueCompact(
            username = username,
            nowMs = System.currentTimeMillis(),
            ttlMs = EnvVars.worldLoginTokenTtlMs,
            secret = EnvVars.worldLoginTokenSecret,
        )
        val rsaInner = BufferWriter(128).apply {
            writeByte(10)                       // magic
            for (k in isaacKeys) writeInt(k)    // 4 ISAAC keys (16 bytes)
            writeLong(sessionCheck)             // sessionCheck (echo of server seed — NON-ZERO, like the real client)
            writeByte(2)                        // credential type: anonymous/no social-token payload
            writeInt(0)                         // credential padding
            writeByte(0)                        // password/session-token branch
            writeString("")                     // password slot unused for the probe
            writeLong(loginToken.part1)         // compact token expiry+nonce
            writeLong(loginToken.part2)         // compact token signature
        }.toArray()
        val rsaEnc = RSA.crypt(rsaInner, modulus, publicExp)

        // XTEA tail (enciphered with the 4 ISAAC keys). Must mirror WorldServer's read order.
        val tailPlain = BufferWriter(160).apply {
            writeByte(1)                        // stringUsername = true
            writeString(username)               // username
            writeByte(2)                        // displayMode
            writeShort(765)                     // screenWidth
            writeShort(503)                     // screenHeight
            writeByte(0)                        // unknown2
            writeBytes(ByteArray(24))           // randomDat (24 bytes)
            writeString("")                     // settings
            writeInt(0)                         // affid
            writeByte(0)                        // prefSize = 0 (no prefs)
            writeByte(6)                        // machine-info flag (WorldServer expects 6)
        }.toArray()
        // Pad to a multiple of 8 so XTEA enciphers the whole tail (decryptXtea processes full quads).
        val padded = if (tailPlain.size % 8 == 0) tailPlain else tailPlain.copyOf(tailPlain.size + (8 - tailPlain.size % 8))
        Xtea.encipher(padded, 0, padded.size - (padded.size % 8), isaacKeys)

        // Outer login packet body: int major, int minor, ubyte has-extra, ushort rsaSize, RSA, XTEA tail.
        val body = BufferWriter(512).apply {
            writeInt(EnvVars.majorVersion)
            writeInt(EnvVars.minorVersion)
            writeByte(1)                        // has-extra byte (world mode, mode==2) -> readUByte()==1
            writeShort(rsaEnc.size)             // RSA block size
            writeBytes(rsaEnc)
            writeBytes(padded)
        }.toArray()

        out.write(16)                           // RequestOpcode.LOGIN
        out.write((body.size ushr 8) and 0xFF)  // size hi (BE u16)
        out.write(body.size and 0xFF)           // size lo
        out.write(body)
        out.flush()
        println("[PROBE OK]   Step 2 GAMELOGIN: sent LOGIN(16) + ${body.size}B body (rsa=${rsaEnc.size}B, xtea=${padded.size}B)")

        // --- Step 3: RESULT — expect status byte 2 (SUCCESS) ---
        val resultByte = input.read()
        if (resultByte < 0) fail("RESULT: connection closed before the login result byte (RSA/XTEA parse likely failed server-side — check world log)")
        if (resultByte != 2) fail("RESULT: expected SUCCESS(2), got result byte $resultByte (0x${"%02x".format(resultByte)}) — see world log for the rejection reason")
        println("[PROBE OK]   Step 3 RESULT: login result byte = 2 (SUCCESS)")

        // --- Step 3b: the THREE-PART pre-ISAAC world-login response (docs §9.5/§9.6) ---
        // In WORLD mode the client routes result byte 2 to its server-client-var state (step 0xfa),
        // NOT to HandleLoginData like the lobby. So after SUCCESS the server sends, pre-ISAAC and
        // UN-framed (no smart opcode, no per-packet length other than the explicit prefixes below):
        //   Part A: [u16 BE varcLen][u8 ackFlag][varc entries…]
        //   Part B: [u8 playersByte == 2]
        //   Part C: [u8 loginDataLen][u8 leadFlag == 0][WorldLoginDetails body][u16 reserved][u32 time][u64 sid1][u64 sid2]
        //
        // PART A. The OLD framing sent `02 14 <body>`; the client read `[02][14]` as a 2-byte
        // BE server-client-var length (=532). Part A is raw pre-ISAAC data, not a server packet.
        val partALenBytes = readN(input, 2, "Part A (server-client-var length)")
        val varcBlockLen = ((partALenBytes[0].toInt() and 0xFF) shl 8) or (partALenBytes[1].toInt() and 0xFF)
        if (partALenBytes[0].toInt() and 0xFF == 0x02) {
            fail("PART A: first byte is 0x02 — this is the OLD op-2 smart-opcode framing. The client " +
                "would read the next byte as the varc-length low byte (→ a ~512-byte misread → NULL " +
                "GetVarcType → SIGSEGV @ 0x40). Expected raw [u16 length][body] data.")
        }
        if (varcBlockLen < 1) {
            fail("PART A: varcBlockLen = 0x${"%04x".format(varcBlockLen)} (expected at least 1 for ackFlag). " +
                "lengthBytes=${hex(partALenBytes)}")
        }
        val partABody = readN(input, varcBlockLen, "Part A (server-client-var body)")
        val ackFlag = partABody[0].toInt() and 0xFF
        if (ackFlag != 0x01) {
            fail("PART A: ackFlag = 0x${"%02x".format(ackFlag)} (expected 0x01). With ackFlag != 1 the client " +
                "loops back to step 0xfa and reads the NEXT bytes as another server-client-var length → desync (§9.2). " +
                "lenBytes=${hex(partALenBytes)} body=${hex(partABody, 64)}")
        }
        if ((varcBlockLen - 1) % 2 != 0) {
            fail("PART A: ${varcBlockLen - 1} bytes remain after ackFlag, expected an even number of u16 ids. " +
                "lenBytes=${hex(partALenBytes)} body=${hex(partABody, 64)}")
        }
        val serverClientVarIds = IntArray((varcBlockLen - 1) / 2) { i ->
            val o = 1 + i * 2
            ((partABody[o].toInt() and 0xFF) shl 8) or (partABody[o + 1].toInt() and 0xFF)
        }
        val firstServerClientVarIds = serverClientVarIds.take(12).joinToString(prefix = "[", postfix = "]")
        println("[PROBE OK]   Step 3b PART A: server-client-var block len=$varcBlockLen ackFlag=0x01 " +
            "ids=${serverClientVarIds.size} firstIds=$firstServerClientVarIds")

        // PART B — players byte; the client requires == 2 (step 0x82) to advance to the login-data block.
        val playersByte = readN(input, 1, "Part B (players byte)")[0].toInt() and 0xFF
        if (playersByte != 0x02) fail("PART B: players byte = 0x${"%02x".format(playersByte)} (expected 0x02 — step 0x82 requires 2 to reach Part C).")
        println("[PROBE OK]   Step 3b PART B: players byte = 0x02 (advances to the login-data block).")

        // PART C — login-data block. 1-byte length, then leadFlag(0) + WorldLoginDetails body + 4 trailing fields.
        val loginDataLen = readN(input, 1, "Part C (login-data length)")[0].toInt() and 0xFF
        val partC = readN(input, loginDataLen, "Part C (login-data body)")
        if (partC[0].toInt() != 0) {
            fail("PART C: leadFlag (byte 0 of the login-data body) = 0x${"%02x".format(partC[0].toInt() and 0xFF)}, expected 0x00. " +
                "A leadFlag of 1 makes the client (FUN_0017a620) try to pull 4 ISAAC-keystream bytes that don't exist yet (§9.4 field 0).")
        }
        // Decode Part C exactly as LoginStepHandleLoginData (world branch) does, §9.4 fields 0–15, to
        // prove the 1-byte length frames the whole body (leadFlag + fields 1–11 + the 4 trailing fields)
        // with NOTHING left over and NOTHING read past it.
        var p = 1                                   // cursor after leadFlag (field 0)
        fun u8(): Int { val v = partC[p].toInt() and 0xFF; p += 1; return v }
        fun u16(): Int { val v = ((partC[p].toInt() and 0xFF) shl 8) or (partC[p + 1].toInt() and 0xFF); p += 2; return v }
        fun s24(): Int { val v = ((partC[p].toInt() and 0xFF) shl 16) or ((partC[p + 1].toInt() and 0xFF) shl 8) or (partC[p + 2].toInt() and 0xFF); p += 3; return v }
        fun u32(): Long { var v = 0L; repeat(4) { v = (v shl 8) or (partC[p++].toLong() and 0xFF) }; return v }
        fun u64(): Long { var v = 0L; repeat(8) { v = (v shl 8) or (partC[p++].toLong() and 0xFF) }; return v }
        fun jstr(): String { val s = p; while (p < partC.size && partC[p].toInt() != 0) p++; val str = String(partC, s, p - s, Charsets.ISO_8859_1); p += 1 /* NUL */; return str }

        val rights = u8(); val modLevel = u8()
        val quickChat = u8(); val verifiedEmail = u8(); val aBool7322 = u8(); val quickChatOnly = u8()
        val playerIndex = u16()
        val members = u8()
        val dob = s24()
        val memberWorld = u8()
        val worldName = jstr()
        val reserved16 = u16()
        val serverTime = u32()
        val sessionId1 = u64()
        val sessionId2 = u64()

        if (p != partC.size) {
            fail("PART C: decoded $p bytes but loginDataLen framed $loginDataLen — the length prefix does NOT match the body " +
                "(client step 0x96 would read past the body into following bytes, corrupting the session). body=${hex(partC)}")
        }
        if (!worldName.contains("Darkan")) {
            fail("PART C: worldName decoded to '$worldName' (expected to contain 'Darkan') — body is misaligned. body=${hex(partC)}")
        }
        println("[PROBE OK]   Step 3b PART C: $loginDataLen-byte login-data block decodes cleanly with a CORRECT 1-byte length prefix: " +
            "leadFlag=0, rights=$rights modLevel=$modLevel quickChat=$quickChat verifiedEmail=$verifiedEmail aBool7322=$aBool7322 " +
            "quickChatOnly=$quickChatOnly playerIndex=$playerIndex members=$members dob=$dob memberWorld=$memberWorld worldName='$worldName' " +
            "reserved16=$reserved16 serverTime=$serverTime sid1=$sessionId1 sid2=$sessionId2 (the 4 trailing fields §9.4 #12–15 are present). " +
            "body=${hex(partC)}")

        // --- Step 4: REBUILD — de-ISAAC the world-init burst and decode op 81's packed coords ---
        // The ISAAC-encrypted burst follows the THREE-PART response (RESET_CLIENT_VARCACHE, 29× stats,
        // REBUILD_NORMAL_SIMPLE, player-ops, SET_READY_FLAG, PLAYER_INFO/NPC_INFO, NO_TIMEOUT).
        // The three-part response (Parts A/B/C above) was written raw/pre-ISAAC so it consumed NO ISAAC
        // ints — the outbound ISAAC stream starts fresh at the first framed opcode here, seeded with
        // isaacKeys + 50. (This also confirms the burst still frames cleanly after the §9.6 fix.)
        val codec: Codec = register948()
        val isaacOut = Isaac(IntArray(4) { isaacKeys[it] + EnvVars.ISAAC_DELTA })

        // Walk packets with the EXACT framing FramingRegression proves: decoded =
        // (rawByte - isaac.nextInt()) & 0xFF; decoded >= 128 ⇒ 2-byte opcode; size from
        // codec.serverProtSize (fixed=N, varByte=-1, varShort=-2, unknown=0).
        val REBUILD_OP = 81
        // In-game RESET_ALL_VARPS / SERVER_CLIENT_VAR (op 5). NOTE: §9 supersedes §8 — this in-game op 5
        // is NOT the crash source (the crash was the response framing, fixed/asserted in Step 3b).
        val VARC_OP = 5
        var rebuildBody: ByteArray? = null
        // varc block: (declared 2-byte-prefix length, body). null until op 5 is seen in the burst.
        var varcLen: Int? = null
        var varcBody: ByteArray? = null
        var packetsWalked = 0
        val opcodesSeen = sortedSetOf<Int>()
        try {
            // Walk until we've seen BOTH the varc block (op 5) and the rebuild (op 81). The varc
            // block precedes op 81 in sendWorldLoginCore, so a successful op-81 find implies the
            // varc block was already crossed — but require it explicitly so an unframed op 5 that
            // desyncs ISAAC is caught here rather than silently masked.
            while ((rebuildBody == null || varcBody == null) && packetsWalked < 200) {
                val rawOp1 = readN(input, 1, "opcode")[0].toInt() and 0xFF
                val dec1 = (rawOp1 - isaacOut.nextInt()) and 0xFF
                val opcode = if (dec1 < 128) dec1 else {
                    val rawOp2 = readN(input, 1, "opcode2")[0].toInt() and 0xFF
                    val dec2 = (rawOp2 - isaacOut.nextInt()) and 0xFF
                    (dec1 - 128) * 256 + dec2
                }
                opcodesSeen.add(opcode)
                val sizeMode = codec.serverProtSize(opcode)
                val len = when {
                    sizeMode >= 0 -> sizeMode
                    sizeMode == -1 -> readN(input, 1, "varByte len")[0].toInt() and 0xFF
                    sizeMode == -2 -> {
                        val h = readN(input, 2, "varShort len")
                        ((h[0].toInt() and 0xFF) shl 8) or (h[1].toInt() and 0xFF)
                    }
                    else -> fail("REBUILD walk: op $opcode (${codec.serverProtName(opcode)}) has unknown size mode $sizeMode — ISAAC desync (the burst stopped decoding to valid opcodes)")
                }
                val pktBody = readN(input, len, "body op $opcode")
                packetsWalked++
                if (opcode == REBUILD_OP) rebuildBody = pktBody
                if (opcode == VARC_OP) {
                    if (sizeMode != 0) {
                        fail("VARC: op 5 decoded with size mode $sizeMode (expected fixed 0). Production 948 captures RESET_ALL_VARPS as an empty fixed packet.")
                    }
                    varcLen = len
                    varcBody = pktBody
                }
            }
        } catch (e: Exception) {
            if (rebuildBody == null || varcBody == null)
                fail("world-init walk: did not reach both op 5 and op 81 (saw $packetsWalked packets, " +
                    "opcodes=$opcodesSeen, varcSeen=${varcBody != null}, rebuildSeen=${rebuildBody != null}): " +
                    "${e::class.simpleName} ${e.message}")
        }

        // --- Step 4a: in-game RESET_ALL_VARPS (op 5) — confirm it still frames cleanly post-fix ---
        // §9 supersedes §8: the varc crash was the RESPONSE framing (Step 3b), not this in-game op 5.
        // It stays empty/fixed-size, and the structural checks below remain valid correctness guards.
        val vBody = varcBody
            ?: fail("VARC: in-game op 5 (RESET_ALL_VARPS) not found within $packetsWalked packets (opcodes seen: $opcodesSeen) — sendWorldLoginCore must include it.")
        val vLen = varcLen!!
        if (vLen != vBody.size)
            fail("VARC: fixed-size metadata ($vLen) != body bytes actually framed (${vBody.size}) — mis-sized in-game op-5 block.")
        if (vBody.size % 2 != 0)
            fail("VARC: in-game op-5 body is ${vBody.size} bytes — NOT an exact multiple of 2 (the in-game varc handler strides 2-byte ids).")
        val varcIds = IntArray(vBody.size / 2) { i ->
            ((vBody[i * 2].toInt() and 0xFF) shl 8) or (vBody[i * 2 + 1].toInt() and 0xFF)  // BE u16
        }
        if (vBody.isEmpty()) {
            println("[PROBE OK]   Step 4a VARC: in-game op 5 framed as fixed-size 0 with an EMPTY body " +
                "(not the crash, which was the response framing fixed in Step 3b).")
        } else {
            // Non-empty is only safe if EVERY id is a real cache-backed VarcType. The probe can't load
            // the client cache, so it cannot prove that — flag it loudly. The chosen fix is the empty
            // block, so reaching here means someone re-added ids and must justify each one.
            println("[PROBE WARN] Step 4a VARC: op 5 carries ${varcIds.size} varc id(s) with a well-formed " +
                "fixed-size body ($vLen, multiple of 2): ${varcIds.joinToString(prefix = "[", postfix = "]")}. " +
                "The length framing is correct, but each id MUST resolve to a VarcType in the client's cache or " +
                "the client SIGSEGVs at 0x40 (§8.4 case (a)). The empty-block fix avoids this risk — re-verify " +
                "every id is cache-backed before relying on a non-empty block.")
        }

        val rebuild = rebuildBody
            ?: fail("REBUILD walk: op 81 not found within $packetsWalked packets (opcodes seen: $opcodesSeen)")

        if (rebuild.size != 5137) {
            fail("REBUILD: op-81 body is ${rebuild.size}B, expected 5137B (5119B GPI prefix + 18B coord header) for fresh world entry.")
        }
        println("[PROBE OK]   Step 4 world-init: de-ISAAC'd $packetsWalked packet(s); found REBUILD_NORMAL_SIMPLE (op 81), ${rebuild.size}-byte body.")

        // Decode the trailing 18 bytes of op-81 per Rev948ServerCodecsRebuild / handler @ 0x001daa70:
        //   +1/+2 centreZoneZ (LE u16)  +3 magic 0x85  +4 centreZoneX (BE u16)
        //   +10 packedCoordA (BE u32)   +14 packedCoordB (BE u32)
        if (rebuild.size < 18) fail("REBUILD: op-81 body is ${rebuild.size}B, expected 18")
        val rebuildTail = rebuild.size - 18
        fun be32(o: Int) = ((rebuild[o].toInt() and 0xFF) shl 24) or ((rebuild[o + 1].toInt() and 0xFF) shl 16) or
            ((rebuild[o + 2].toInt() and 0xFF) shl 8) or (rebuild[o + 3].toInt() and 0xFF)
        val magic = rebuild[rebuildTail + 3].toInt() and 0xFF
        if (magic != 0x85) fail("REBUILD: magic byte at +3 is 0x${"%02x".format(magic)}, expected 0x85")
        val centreZoneZ = (rebuild[rebuildTail + 1].toInt() and 0xFF) or ((rebuild[rebuildTail + 2].toInt() and 0xFF) shl 8)  // LE u16
        val centreZoneX = ((rebuild[rebuildTail + 4].toInt() and 0xFF) shl 8) or (rebuild[rebuildTail + 5].toInt() and 0xFF)   // BE u16
        val packedA = be32(rebuildTail + 10)
        val packedB = be32(rebuildTail + 14)
        val minRegion = decodePackedCoord(packedA)
        val maxRegion = decodePackedCoord(packedB)

        println("[PROBE OK]   Step 4 decode: centreZone=($centreZoneX,$centreZoneZ) magic=0x85 " +
            "packedA=0x${"%08x".format(packedA)} → minRegion=(${minRegion.regionX},${minRegion.regionZ}) plane=${minRegion.plane} ; " +
            "packedB=0x${"%08x".format(packedB)} → maxRegion=(${maxRegion.regionX},${maxRegion.regionZ}) plane=${maxRegion.plane}")

        val spawnRegionX = centreZoneX ushr 3
        val spawnRegionZ = centreZoneZ ushr 3
        if (minRegion.plane != maxRegion.plane) {
            fail("REBUILD: packed A/B planes differ (${minRegion.plane} vs ${maxRegion.plane}).")
        }
        if (minRegion.regionX > maxRegion.regionX || minRegion.regionZ > maxRegion.regionZ) {
            fail("REBUILD: build-area bounds are inverted: X[${minRegion.regionX}..${maxRegion.regionX}] " +
                "Z[${minRegion.regionZ}..${maxRegion.regionZ}].")
        }
        if (spawnRegionX !in minRegion.regionX..maxRegion.regionX ||
            spawnRegionZ !in minRegion.regionZ..maxRegion.regionZ
        ) {
            fail("REBUILD: spawn region ($spawnRegionX,$spawnRegionZ) is outside build-area bounds " +
                "X[${minRegion.regionX}..${maxRegion.regionX}] Z[${minRegion.regionZ}..${maxRegion.regionZ}].")
        }
        val localCentreX = centreZoneX - (minRegion.regionX shl 3)
        val localCentreZ = centreZoneZ - (minRegion.regionZ shl 3)
        val widthZones = (maxRegion.regionX - minRegion.regionX + 1) shl 3
        val heightZones = (maxRegion.regionZ - minRegion.regionZ + 1) shl 3
        if (localCentreX - 6 < 0 || localCentreX + 6 >= widthZones ||
            localCentreZ - 6 < 0 || localCentreZ + 6 >= heightZones
        ) {
            fail("REBUILD: 13x13 scene window around local centre ($localCentreX,$localCentreZ) does not fit " +
                "inside build-area size ${widthZones}x${heightZones} zones.")
        }
        println("[PROBE OK]   Step 4 ASSERT: build-area bounds contain spawn region ($spawnRegionX,$spawnRegionZ), " +
            "localCentre=($localCentreX,$localCentreZ), scene window fits ${widthZones}x${heightZones} zone grid.")
    }

    println("\n[PROBE PASS] World-login handshake completed end-to-end: 9-byte INIT, GAMELOGIN accepted, " +
        "SUCCESS(2), then the THREE-PART pre-ISAAC response decoded cleanly — Part A is a raw server-client-var " +
        "block (not smart-opcode framed), Part B players byte 02, Part C the login-data block with a correct " +
        "1-byte length prefix + the 4 trailing fields. The in-game op 5 " +
        "is empty/well-formed and REBUILD_NORMAL_SIMPLE (op 81) decodes to build-area bounds containing spawn.")
    exitProcess(0)
}
