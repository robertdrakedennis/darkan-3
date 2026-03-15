package org.darkan.lobby.server

import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.io.readByteArray
import kotlinx.io.readUByte
import kotlinx.io.readUShort
import org.darkan.core.EnvVars
import org.darkan.core.Logger.logError
import org.darkan.core.Logger.logInfo
import org.darkan.core.Logger.logTrace
import org.darkan.core.net.Isaac
import org.darkan.core.net.RequestOpcode
import org.darkan.core.net.ResponseOpcode
import world.gregs.voidps.buffer.*
import world.gregs.voidps.cache.secure.RSA
import world.gregs.voidps.cache.secure.Xtea
import java.math.BigInteger
import java.security.SecureRandom

/**
 * Handles the NXT client login protocol.
 *
 * Flow (SSO path, which is what the NXT client uses):
 * 1. Connection type byte (14/CONNECT_LOGIN) already read by LobbyServer
 * 2. Server sends 9 bytes: response(0) + 8-byte session key
 * 3. Client sends login opcode (19/LOBBY) + short size + login packet data
 * 4. Server parses RSA block (XTEA keys) + XTEA-encrypted credentials
 * 5. Server sends login result (2/SUCCESS) + lobby data
 * 6. Enter lobby session loop (ISAAC-encrypted packets)
 */
class LoginServer {
    private val random = SecureRandom()
    private val rsaMod = BigInteger(EnvVars.loginRsaModulus)
    private val rsaExp = BigInteger(EnvVars.loginRsaExponent)

    /**
     * Handle a login connection. Called after the connection type byte (14) has been read.
     */
    suspend fun handleLogin(input: ByteReadChannel, output: ByteWriteChannel, ip: String, connectionType: Int) {
        logInfo("Login attempt from $ip (connection type: $connectionType)")

        // Step 1: Send exchange data response
        // NXT client expects 9 bytes: 1 response + 8-byte session key
        val sessionKey = ByteArray(8).also { random.nextBytes(it) }
        output.writeByte(ResponseOpcode.JS5_SYNC) // 0 = OK
        output.writeFully(sessionKey)
        output.flush()
        logTrace("Sent exchange data (9 bytes) to $ip")

        // Step 2: Read the login packet
        // NXT SSO path: client jumps to SEND_LOGIN_PACKET after reading our 9-byte response
        // Expects: opcode byte + short size + packet data
        val loginOpcode = input.readByte().toInt() and 0xFF
        logInfo("Login opcode from $ip: $loginOpcode (0x${"%02x".format(loginOpcode)})")

        if (loginOpcode != RequestOpcode.LOBBY && loginOpcode != RequestOpcode.LOGIN) {
            logError("Unexpected login opcode: $loginOpcode from $ip")
            output.finish(ResponseOpcode.INVALID_LOGIN_SERVER)
            return
        }

        val size = input.readShort().toInt() and 0xFFFF
        logTrace("Login packet size: $size from $ip")

        if (size <= 0 || size > 5000) {
            logError("Invalid login packet size: $size from $ip")
            output.finish(ResponseOpcode.COULD_NOT_COMPLETE_LOGIN)
            return
        }

        val packetData = ByteArray(size)
        input.readFully(packetData, 0, size)

        logTrace("Read $size bytes of login data from $ip")
        logTrace("First 32 bytes: ${packetData.take(32).joinToString(" ") { "%02x".format(it) }}")

        // Step 3: Parse the login packet
        val packet = ByteReadPacket(packetData)

        // Version info
        val major = packet.readInt()
        val minor = packet.readInt()
        logInfo("Client version: $major.$minor from $ip")

        if (major != EnvVars.majorVersion) {
            logError("Version mismatch: expected ${EnvVars.majorVersion}, got $major from $ip")
            output.finish(ResponseOpcode.GAME_UPDATE)
            return
        }

        // RSA block
        val rsaSize = packet.readUShort().toInt()
        logTrace("RSA block size: $rsaSize from $ip")

        if (rsaSize <= 0 || rsaSize > 512) {
            logError("Invalid RSA block size: $rsaSize from $ip")
            output.finish(ResponseOpcode.COULD_NOT_COMPLETE_LOGIN)
            return
        }

        val rsaBytes = packet.readByteArray(rsaSize)
        val decryptedRsa = ByteReadPacket(RSA.crypt(rsaBytes, rsaMod, rsaExp))

        // RSA block contents: magic(1) + xtea keys(16) + session data
        val magic = decryptedRsa.readUByte().toInt()
        if (magic != 10) {
            logError("RSA magic mismatch: expected 10, got $magic from $ip (bad RSA key?)")
            output.finish(ResponseOpcode.BAD_SESSION_ID)
            return
        }

        val isaacKeys = IntArray(4) { decryptedRsa.readInt() }
        logTrace("ISAAC keys: ${isaacKeys.joinToString(", ") { "0x${"%08x".format(it)}" }} from $ip")

        // Read remaining RSA data (session ID check, password, etc.)
        val sessionCheck = decryptedRsa.readLong()
        logTrace("RSA session check: $sessionCheck from $ip")

        // Try to read password if available (legacy path)
        var password = ""
        if (decryptedRsa.remaining > 0) {
            password = decryptedRsa.readRSString()
            logTrace("Password field present (${password.length} chars) from $ip")
        }

        // Read remaining RSA fields
        while (decryptedRsa.remaining > 0) {
            val remaining = decryptedRsa.readByteArray(decryptedRsa.remaining.toInt())
            logTrace("RSA remaining ${remaining.size} bytes: ${remaining.joinToString(" ") { "%02x".format(it) }}")
        }

        // Step 4: XTEA-decrypted section
        val xteaData = packet.readByteArray(packet.remaining.toInt())
        Xtea.decipher(xteaData, isaacKeys)
        val xtea = ByteReadPacket(xteaData)

        logTrace("XTEA decrypted ${xteaData.size} bytes from $ip")

        try {
            val stringUsername = xtea.readUByte().toInt() == 1
            val username = if (stringUsername) {
                xtea.readRSString()
            } else {
                xtea.readLong().toRSString()
            }
            logInfo("Login username: '$username' (string=$stringUsername) from $ip")

            val remaining = if (xtea.remaining > 0) xtea.readByteArray(xtea.remaining.toInt()) else ByteArray(0)
            logTrace("XTEA remaining ${remaining.size} bytes: ${remaining.take(64).joinToString(" ") { "%02x".format(it) }}")
        } catch (e: Exception) {
            logTrace("XTEA parsing stopped: ${e::class.simpleName}: ${e.message}")
        }

        // Step 5: Send login success
        logInfo("Sending login success to $ip")

        // Initialize ISAAC ciphers
        // Server recv cipher = raw XTEA keys (matches client's send cipher)
        // Server send cipher = XTEA keys + delta (matches client's recv cipher)
        // VERIFIED from crypto.md — client creates out=raw, in=key+delta
        val inCipher = Isaac(isaacKeys.copyOf())
        val outKeys = isaacKeys.copyOf()
        for (i in outKeys.indices) outKeys[i] += EnvVars.ISAAC_DELTA
        val outCipher = Isaac(outKeys)

        // Send login result: byte 2 (SUCCESS)
        output.writeByte(ResponseOpcode.SUCCESS)

        // Send lobby data length + lobby data
        val lobbyData = buildLobbyData()
        output.writeByte(lobbyData.size.toByte()) // varByte length
        output.writeFully(lobbyData)
        output.flush()

        logInfo("Login complete for $ip (lobby data: ${lobbyData.size} bytes)")

        // Step 6: Send initial lobby packets
        sendLobbyInitPackets(output, outCipher, ip)

        // Step 7: Lobby session loop - read ISAAC-encrypted packets from client
        lobbySessionLoop(input, output, ip, inCipher, outCipher)
    }

    /**
     * Build the lobby login data blob.
     *
     * Format from LoginStepHandleLoginData (step 150) in login-wire-format.md,
     * VERIFIED from rs2client rev 946 decompilation.
     */
    private fun buildLobbyData(): ByteArray {
        val buf = java.io.ByteArrayOutputStream(128)
        fun p1(v: Int) = buf.write(v and 0xFF)
        fun p2(v: Int) { buf.write((v shr 8) and 0xFF); buf.write(v and 0xFF) }
        fun p3(v: Int) { buf.write((v shr 16) and 0xFF); buf.write((v shr 8) and 0xFF); buf.write(v and 0xFF) }
        fun p4(v: Int) { buf.write((v shr 24) and 0xFF); buf.write((v shr 16) and 0xFF); buf.write((v shr 8) and 0xFF); buf.write(v and 0xFF) }
        fun p8(v: Long) { p4((v shr 32).toInt()); p4(v.toInt()) }
        fun pStr(s: String) { buf.write(s.toByteArray(Charsets.ISO_8859_1)); buf.write(0) }

        val nowMs = System.currentTimeMillis()

        p1(0)                     // has_totp_update: no
        p1(1)                     // membership_type: 1 = member
        p1(30)                    // membership_days: 30
        p1(1)                     // email_validated: yes (bool)
        p3(0)                     // recovery_delay: 0 (medium, signed 24-bit)
        p1(0)                     // staff_mod_flag: 0 = none
        p1(0)                     // unknown_flag_1 (bool)
        p1(0)                     // unknown_flag_2 (bool)
        p8(nowMs * 1000)          // membership_timestamp (microseconds)
        p1(0)                     // time_byte (high bits of time offset)
        p4((nowMs / 1000).toInt()) // time_int (seconds since epoch, low 32 bits)
        p1(0)                     // flags byte (bit 0 -> +0x29, bit 1 -> +0x28)
        p4(0)                     // unknown_1
        p4(0)                     // unknown_2
        p2(1)                     // player_index
        p2(0)                     // unknown_3
        p2(0)                     // unknown_4
        p4(0)                     // unknown_5
        p1(0)                     // unknown_6
        p2(0)                     // unknown_7
        p2(0)                     // unknown_8
        p1(1)                     // is_members_world (bool)
        pStr("Player")            // display_name (null-terminated)
        p1(0)                     // unknown_9
        p4(0)                     // unknown_10
        p2(0xFFFF)                // world_id (0xFFFF = -1 = no default world)
        pStr("127.0.0.1")         // server_info string (null-terminated)
        p2(1920)                  // screen_width
        p2(1080)                  // screen_height
        // CRITICAL: Session tokens MUST be stable across logins!
        // If tokens change, the client clears its entire JS5 disk cache (LoginStepHandleLoginData).
        // Using fixed tokens prevents cache wipe on every login.
        p8(0x4461726B616E3333L)   // session_token_1 = "Darkan33" as ASCII
        p8(0x5365727665723033L)   // session_token_2 = "Server03" as ASCII

        return buf.toByteArray()
    }

    /**
     * Send initial lobby packets to get the client to render the lobby UI.
     *
     * Based on darkan reference pattern (design only, byte encoding from RE docs).
     * All byte formats VERIFIED from variables.md (rev 946 binary).
     */
    private suspend fun sendLobbyInitPackets(output: ByteWriteChannel, cipher: Isaac, ip: String) {
        // IF_OPENTOP — opcode 0x6C (108), size 6
        // Format: g4s_alt2 (LE int, walkType) + g2 (BE ushort, interfaceId)
        // VERIFIED from RE: entry at 0x016fdae0, handler at 0x00233c90
        // NOTE: 0xCF was incorrectly identified as IF_OPENTOP — it's actually IF_SETPLAYERMODEL
        val lobbyInterfaceId = 1477 // Modern RS3 lobby interface (rev 946+)
        writeServerOpcode(output, IF_OPENTOP, cipher)
        // walkType as little-endian int (0 = no walk, appropriate for lobby)
        output.writeByte(0); output.writeByte(0); output.writeByte(0); output.writeByte(0)
        // interfaceId as big-endian ushort
        output.writeByte((lobbyInterfaceId shr 8) and 0xFF)
        output.writeByte(lobbyInterfaceId and 0xFF)
        logInfo("Sent IF_OPENTOP interface=$lobbyInterfaceId to $ip")

        // Vars from darkan reference lobby init pattern
        sendVarpInt(output, cipher, 281, 1000)
        sendVarpSmall(output, cipher, 2528, 1)
        sendVarpSmall(output, cipher, 2567, 1)

        // Varbits
        sendVarbitSmall(output, cipher, 10242, 1)
        sendVarbitSmall(output, cipher, 10243, 12)
        sendVarbitSmall(output, cipher, 11162, 1)

        // Varc 1919 = 1 (email validated flag)
        sendVarcInt(output, cipher, 1919, 1)

        // FRIENDLIST_LOADED — opcode 0x42 (66), var_short
        // Darkan sends this to signal client that friend list is loaded
        // Send with empty payload (no friends)
        writeServerOpcode(output, FRIENDLIST_LOADED, cipher)
        output.writeByte(0) // high byte of length
        output.writeByte(0) // low byte of length (0 = empty)
        logInfo("Sent FRIENDLIST_LOADED to $ip")

        // UPDATE_FRIENDLIST — opcode 0xAE (174), var_short
        // Empty friend list update
        writeServerOpcode(output, UPDATE_FRIENDLIST, cipher)
        output.writeByte(0) // high byte of length
        output.writeByte(0) // low byte of length
        logInfo("Sent UPDATE_FRIENDLIST to $ip")

        // UPDATE_IGNORELIST — opcode 0x11 (17), var_short
        // Empty ignore list
        writeServerOpcode(output, UPDATE_IGNORELIST, cipher)
        output.writeByte(0)
        output.writeByte(0)
        logInfo("Sent UPDATE_IGNORELIST to $ip")

        // CHAT_FILTER_SETTINGS — opcode 0x6B (107), var_byte
        // Send default chat filter (1 byte: filter setting = 0)
        writeServerOpcode(output, CHAT_FILTER_SETTINGS, cipher)
        output.writeByte(1) // payload length
        output.writeByte(0) // filter setting
        logInfo("Sent CHAT_FILTER_SETTINGS to $ip")

        output.flush()
        logInfo("Sent lobby init packets to $ip")
    }

    /**
     * SET_VARBIT_SMALL — ServerProt opcode 72 (0x48), fixed 3 bytes.
     *
     * Handler reads: ushort BE id + byte value
     */
    private suspend fun sendVarbitSmall(output: ByteWriteChannel, cipher: Isaac, varbitId: Int, value: Int) {
        writeServerOpcode(output, SET_VARBIT_SMALL, cipher)
        // ushort BE: varbit id
        output.writeByte((varbitId shr 8) and 0xFF)
        output.writeByte(varbitId and 0xFF)
        // byte: value
        output.writeByte(value and 0xFF)
    }

    /**
     * SET_VARC_INT — ServerProt opcode 2 (0x02), varShort.
     *
     * Handler reads: ushort BE varc_id + int BE value
     */
    private suspend fun sendVarcInt(output: ByteWriteChannel, cipher: Isaac, varcId: Int, value: Int) {
        writeServerOpcode(output, SET_VARC_INT, cipher)
        // Write varShort length header (6 bytes payload)
        output.writeByte(0)  // high byte of length
        output.writeByte(6)  // low byte of length
        // ushort BE: varc id
        output.writeByte((varcId shr 8) and 0xFF)
        output.writeByte(varcId and 0xFF)
        // int BE: value
        output.writeByte((value shr 24) and 0xFF)
        output.writeByte((value shr 16) and 0xFF)
        output.writeByte((value shr 8) and 0xFF)
        output.writeByte(value and 0xFF)
    }

    // --- ServerProt packet senders ---
    // All byte formats from docs/net/serverprot/variables.md, VERIFIED from rs2client rev 946.

    /**
     * SET_VARP_SMALL — ServerProt opcode 14 (0x0E), fixed 3 bytes.
     *
     * Client reads:
     *   varp_id = byte[0] * 256 + (byte[1] + 0x80)
     *   value   = byte[2] - 0x80 (signed byte)
     *
     * Value range: -128..127 (signed byte after transform)
     */
    private suspend fun sendVarpSmall(output: ByteWriteChannel, cipher: Isaac, varpId: Int, value: Int) {
        writeServerOpcode(output, SET_VARP_SMALL, cipher)
        output.writeByte((varpId shr 8) and 0xFF)           // high byte of varpId
        output.writeByte(((varpId and 0xFF) - 0x80) and 0xFF)  // low byte with +0x80 transform
        output.writeByte((value + 0x80) and 0xFF)            // value with +0x80 bias
    }

    /**
     * SET_VARP_INT — ServerProt opcode 124 (0x7C), fixed 6 bytes.
     *
     * Client reads:
     *   varp_id = byte[1]*256 + byte[0] (little-endian ushort)
     *   value   = 4-byte big-endian int
     */
    private suspend fun sendVarpInt(output: ByteWriteChannel, cipher: Isaac, varpId: Int, value: Int) {
        writeServerOpcode(output, SET_VARP_INT, cipher)
        // varpId as little-endian ushort
        output.writeByte(varpId and 0xFF)
        output.writeByte((varpId shr 8) and 0xFF)
        // value as big-endian int
        output.writeByte((value shr 24) and 0xFF)
        output.writeByte((value shr 16) and 0xFF)
        output.writeByte((value shr 8) and 0xFF)
        output.writeByte(value and 0xFF)
    }

    /**
     * Write an ISAAC-encrypted ServerProt opcode to the channel.
     *
     * Encoding (from emulator-guide.md):
     * - Opcodes 0-127:   1 byte:  (opcode + isaac_val) & 0xFF
     * - Opcodes 128-216: 2 bytes: ((opcode >> 8) + 128 + isaac_val) & 0xFF, (opcode + isaac_val) & 0xFF
     *
     * Lobby phase uses the full game ServerProt table (217 entries, opcodes 0-216).
     */
    private suspend fun writeServerOpcode(output: ByteWriteChannel, opcode: Int, cipher: Isaac) {
        if (opcode >= 128) {
            output.writeByte(((opcode shr 8) + 128 + cipher.nextInt()) and 0xFF)
            output.writeByte((opcode + cipher.nextInt()) and 0xFF)
        } else {
            output.writeByte((opcode + cipher.nextInt()) and 0xFF)
        }
    }

    /**
     * Read an ISAAC-decrypted ClientProt opcode from the channel.
     *
     * Decoding:
     * - Read first byte, subtract isaac value, mask to 0xFF
     * - If < 128: that's the opcode
     * - If >= 128: read second byte, subtract another isaac value, combine
     */
    private suspend fun readClientOpcode(input: ByteReadChannel, cipher: Isaac): Int {
        val first = ((input.readByte().toInt() and 0xFF) - cipher.nextInt()) and 0xFF
        return if (first < 128) {
            first
        } else {
            val second = ((input.readByte().toInt() and 0xFF) - cipher.nextInt()) and 0xFF
            ((first - 128) shl 8) or second
        }
    }

    /**
     * Read ISAAC-encrypted packets from the client in lobby state.
     *
     * The lobby phase uses the 130-entry game ClientProt table (opcodes 0-129)
     * for client→server packets. Verified from rs2client rev 946 binary.
     */
    private suspend fun lobbySessionLoop(
        input: ByteReadChannel,
        output: ByteWriteChannel,
        ip: String,
        inCipher: Isaac,
        outCipher: Isaac
    ) {
        var packetCount = 0
        var lastKeepaliveSent = System.currentTimeMillis()
        val KEEPALIVE_INTERVAL_MS = 15_000L
        // ServerProt NO_TIMEOUT = opcode 0x92 (146), size 0
        val SERVER_NO_TIMEOUT = 0x92

        try {
            // Send initial NO_TIMEOUT to let client know we're alive
            writeServerOpcode(output, SERVER_NO_TIMEOUT, outCipher)
            output.flush()
            logTrace("Sent initial NO_TIMEOUT to $ip")

            // EXPERIMENT removed — LOGOUT proved ISAAC works but doesn't disconnect in lobby

            while (true) {
                val opcode = readClientOpcode(input, inCipher)

                if (opcode < 0 || opcode >= GAME_CLIENT_PROT_SIZES.size) {
                    logError("Client opcode $opcode out of range from $ip — desync likely")
                    break
                }

                val sizeInfo = GAME_CLIENT_PROT_SIZES[opcode]
                val size = when (sizeInfo) {
                    0 -> 0
                    -1 -> input.readByte().toInt() and 0xFF       // VarByte
                    -2 -> input.readShort().toInt() and 0xFFFF     // VarShort
                    else -> sizeInfo                                // Fixed size
                }

                val payload = if (size > 0) {
                    ByteArray(size).also { input.readFully(it, 0, size) }
                } else {
                    ByteArray(0)
                }

                packetCount++
                when (opcode) {
                    15, 80 -> {
                        // NO_TIMEOUT (15) / NO_TIMEOUT_2 (80) — client keepalive
                        if (packetCount % 50 == 0) {
                            logTrace("Keepalive #$packetCount from $ip")
                        }
                    }
                    110 -> {
                        // WORLDLIST_FETCH — client requests world list update
                        // Payload is 4 bytes: int32 checksum of current world list
                        val checksum = if (payload.size >= 4) {
                            ((payload[0].toInt() and 0xFF) shl 24) or
                            ((payload[1].toInt() and 0xFF) shl 16) or
                            ((payload[2].toInt() and 0xFF) shl 8) or
                            (payload[3].toInt() and 0xFF)
                        } else 0
                        logInfo("WORLDLIST_FETCH from $ip: checksum=0x${"%08x".format(checksum)}")
                        // TODO: Send world list response
                    }
                    else -> {
                        logInfo("Client packet from $ip: opcode=$opcode (0x${"%02x".format(opcode)}) size=$size payload=${payload.take(32).joinToString(" ") { "%02x".format(it) }}")
                    }
                }

                // Send periodic keepalives back to the client
                val now = System.currentTimeMillis()
                if (now - lastKeepaliveSent > KEEPALIVE_INTERVAL_MS) {
                    writeServerOpcode(output, SERVER_NO_TIMEOUT, outCipher)
                    output.flush()
                    lastKeepaliveSent = now
                }

                // No experiment — just observe client behavior

                if (packetCount > 100000) {
                    logError("Too many packets from $ip, disconnecting")
                    break
                }
            }
        } catch (e: Exception) {
            logTrace("Lobby session ended for $ip: ${e::class.simpleName}: ${e.message}")
        }
        logInfo("Lobby session closed for $ip after $packetCount packets")
    }

    companion object {
        /**
         * Game ClientProt sizes (130 entries, opcodes 0-129).
         * VERIFIED from rs2client rev 946 binary (clientprot-table.md).
         *
         * The lobby phase reuses the game ClientProt table for client→server packets.
         * Values: 0+ = fixed size, -1 = varByte, -2 = varShort.
         */
        private val GAME_CLIENT_PROT_SIZES = intArrayOf(
            /* 0 */ -2,  // EVENT_APPLET_FOCUS
            /* 1 */  9,  // EVENT_CAMERA_POSITION
            /* 2 */  6,  // CAMERA_DIRECTION
            /* 3 */ -2,  // VERIFIED_STRING_SEND
            /* 4 */ -1,  // MESSAGE_PUBLIC (with effects)
            /* 5 */ 15,  // OPNPC_T_LONG
            /* 6 */  3,  // OPOBJ5
            /* 7 */ -2,  // RESUME_COUNTDIALOG
            /* 8 */  4,  // STRTOL_SEND
            /* 9 */  3,  // OPNPC_T1
            /* 10 */ -2, // RESUME_PAUSEBUTTON
            /* 11 */  4, // (unknown)
            /* 12 */ 16, // IF_BUTTON_T
            /* 13 */  2, // (unknown)
            /* 14 */  3, // OPOBJ7
            /* 15 */  0, // NO_TIMEOUT
            /* 16 */ 11, // OPLOC_T (long form)
            /* 17 */ -1, // EVENT_MOUSE_CLICK
            /* 18 */  8, // IF_BUTTON5
            /* 19 */  3, // OPNPC_T2
            /* 20 */  3, // OPOBJ1
            /* 21 */  0, // MAP_BUILD_COMPLETE
            /* 22 */  1, // (unknown)
            /* 23 */  7, // OPNPC3
            /* 24 */ -1, // CLIENT_CHEAT
            /* 25 */  7, // OPNPC2
            /* 26 */  7, // OPNPC1
            /* 27 */ 12, // OPLOC_T
            /* 28 */ -1, // DEVICE_INFO
            /* 29 */ -1, // MESSAGE_PUBLIC
            /* 30 */  3, // OPOBJ10
            /* 31 */ -2, // EVENT_CAMERA_POSITION_2
            /* 32 */ -1, // CS2_CALLBACK
            /* 33 */  5, // MOVE_GAME (from minimenu)
            /* 34 */  0, // QUEUED_PACKET
            /* 35 */  3, // OPNPC_T5
            /* 36 */  3, // OPNPC_T6
            /* 37 */ 15, // OPLOC_T (extended form)
            /* 38 */  9, // OPLOC_T1
            /* 39 */ -1, // SOCIAL_REQUEST
            /* 40 */  9, // OPLOC_T3
            /* 41 */  0, // (unknown)
            /* 42 */  3, // UNKNOWN_3BYTE_42
            /* 43 */  9, // OPLOC_T6
            /* 44 */ -2, // (unknown)
            /* 45 */ -2, // EVENT_TELEMETRY
            /* 46 */  3, // OPOBJ2
            /* 47 */  8, // IF_BUTTON10
            /* 48 */ -1, // EVENT_KEYBOARD
            /* 49 */ -2, // DATA_REPORT_VARSHORT
            /* 50 */  4, // SCENE_GRAPH_REPORT
            /* 51 */  4, // CAMERA_ANGLE
            /* 52 */ -2, // MESSAGE_PRIVATE
            /* 53 */  9, // (unknown)
            /* 54 */  8, // IF_BUTTON3
            /* 55 */  1, // (unknown)
            /* 56 */ -1, // RESUME_NAMEDIALOG
            /* 57 */  9, // (unknown)
            /* 58 */ 17, // OPPLAYER_T (extended)
            /* 59 */  3, // OPOBJ8
            /* 60 */  3, // OPOBJ6
            /* 61 */  8, // IF_BUTTON7
            /* 62 */ -1, // ENCODEDSTRING_SEND
            /* 63 */  8, // IF_BUTTON9
            /* 64 */  8, // IF_BUTTON6
            /* 65 */  4, // SCENE_INTERACTION
            /* 66 */  4, // EVENT_APPLET_FOCUS_2
            /* 67 */ -1, // CLAN_JOINCHAT
            /* 68 */  9, // OPLOC2_T
            /* 69 */  3, // OPNPC4_T
            /* 70 */  4, // OPLOC1
            /* 71 */ -1, // ACTIVE_CHAT_PHRASE_SEND
            /* 72 */  1, // (unknown)
            /* 73 */ 18, // (unknown)
            /* 74 */ -2, // ENCRYPTED_STRING_SEND2
            /* 75 */ -2, // IF_BUTTON_TARGETMENU
            /* 76 */  2, // SOUND_SONGEND
            /* 77 */  7, // OPNPC5
            /* 78 */ -1, // FRIENDLIST_ADD
            /* 79 */  1, // (unknown)
            /* 80 */  0, // NO_TIMEOUT_2
            /* 81 */ -1, // ACTIVE_CHAT_PHRASE_SENDPRIVATE
            /* 82 */  3, // WINDOW_STATUS
            /* 83 */  1, // FOCUS_CHANGED
            /* 84 */ -1, // OPOBJ_CS2_2
            /* 85 */  7, // EVENT_MOUSE_MOVE
            /* 86 */ -1, // MESSAGE_CLAN_CHAT
            /* 87 */  0, // CLOSE_MODAL
            /* 88 */  2, // SOUND_SONGSELECT
            /* 89 */  5, // MOVE_SCRIPTED
            /* 90 */  7, // OPNPC4
            /* 91 */  3, // OPOBJ9
            /* 92 */ 18, // MOVE_GAME (extended form)
            /* 93 */ -1, // CLAN_LEAVECHAT
            /* 94 */ -1, // OPLOC_CS2
            /* 95 */  4, // DETECT_MODIFIED_CLIENT
            /* 96 */  3, // OPOBJ4
            /* 97 */  8, // IF_BUTTON1
            /* 98 */ -1, // OPOBJ_CS2
            /* 99 */  2, // AFFINEDTRANSFORM_SET
            /* 100 */ 4, // (unknown)
            /* 101 */ 9, // OPLOC4_T
            /* 102 */-2, // MOVE_GAME
            /* 103 */ 7, // OPNPC6
            /* 104 */-1, // OPNPC_CS2
            /* 105 */11, // OPOBJ_T
            /* 106 */ 6, // DISPLAY_INFO
            /* 107 */-1, // IF_BUTTONT
            /* 108 */-2, // (unknown)
            /* 109 */-1, // IGNORELIST_ADD
            /* 110 */ 4, // WORLDLIST_FETCH
            /* 111 */ 9, // OPLOC5_T
            /* 112 */-1, // (unknown)
            /* 113 */ 4, // RENDER_REPORT
            /* 114 */22, // IF_BUTTON_TARGETMENU_SEND
            /* 115 */ 3, // OPOBJ3
            /* 116 */-1, // ENCRYPTED_STRING_SEND
            /* 117 */ 6, // CLOSE_MODAL_COMPONENT
            /* 118 */ 8, // IF_BUTTON2
            /* 119 */-1, // OPPLAYER_CS2
            /* 120 */11, // OPPLAYER_T
            /* 121 */-1, // FRIENDLIST_DEL
            /* 122 */16, // INTERFACE_INTERACTION
            /* 123 */ 1, // BUG_REPORT
            /* 124 */ 8, // IF_BUTTON8
            /* 125 */ 3, // OPNPC3_T
            /* 126 */-1, // (unknown)
            /* 127 */-1, // ENCODEDSTRING_SEND2
            /* 128 */ 8, // IF_BUTTON4
            /* 129 */ 8, // STRTOLL_SEND
        )

        // --- ServerProt opcodes (from serverprot-table.md, rev 946) ---
        const val NOOP = 0x92             // 146, size 0 — keepalive/no-op
        const val SET_VARP_SMALL = 0x0E   // 14, size 3
        const val SET_VARP_INT = 0x7C     // 124, size 6
        const val SET_VARBIT_SMALL = 0x48 // 72, size 3 (handler reads: ushort BE id + byte value)
        const val SET_VARBIT_INT = 0x33   // 51, size 6 (handler reads: int BE value + ushort BE id)
        const val SET_VARC_INT = 0x02     // 2, var_short (reads: ushort BE id + int BE value)
        const val IF_OPENTOP = 0x6C      // 108, size 6 (LE int walkType + BE ushort interfaceId) — VERIFIED: entry 0x016fdae0, handler 0x00233c90
        const val FRIENDLIST_LOADED = 0x42 // 66, var_short — signals friend list loaded
        const val UPDATE_FRIENDLIST = 0xAE // 174, var_short — friend list data
        const val UPDATE_IGNORELIST = 0x11 // 17, var_short — ignore list data
        const val CHAT_FILTER_SETTINGS = 0x6B // 107, var_byte — chat filter settings
        const val RUN_CLIENTSCRIPT = 0x10 // 16, var_short — execute CS2 script
        const val SERVER_TICK_END = 0xCB  // 203, size 8 — end of tick marker
        const val LOGOUT = 0x86           // 134, size 0 — logout from game
    }
}
