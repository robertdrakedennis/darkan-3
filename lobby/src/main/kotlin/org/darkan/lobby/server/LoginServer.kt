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
import org.darkan.core.net.prot.ClientProt
import org.darkan.core.net.prot.ServerProt
import org.darkan.core.net.session.GameSession
import org.darkan.lobby.server.packet.LobbyPacketRegistry
import org.darkan.lobby.server.packet.encoders.*
import world.gregs.voidps.buffer.*
import world.gregs.voidps.buffer.read.BufferReader
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
        // VERIFIED from crypto.md -- client creates out=raw, in=key+delta
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

        // Create session and enter lobby
        val session = GameSession(input, output, inCipher, outCipher, ip)

        // Step 6: Send initial lobby packets
        sendLobbyInitPackets(session)

        // Step 7: Lobby session loop - read ISAAC-encrypted packets from client
        lobbySessionLoop(session)
    }

    /**
     * Build the lobby login data blob.
     *
     * Format from LoginStepHandleLoginData (step 150) in lobby-login-data.md,
     * VERIFIED from rs2client rev 946 decompilation.
     *
     * Strings use gjStr2 encoding: [1B ver=0x00][string][NUL]
     */
    private fun buildLobbyData(): ByteArray {
        val buf = java.io.ByteArrayOutputStream(128)
        fun p1(v: Int) = buf.write(v and 0xFF)
        fun p2(v: Int) { buf.write((v shr 8) and 0xFF); buf.write(v and 0xFF) }
        fun p3(v: Int) { buf.write((v shr 16) and 0xFF); buf.write((v shr 8) and 0xFF); buf.write(v and 0xFF) }
        fun p4(v: Int) { buf.write((v shr 24) and 0xFF); buf.write((v shr 16) and 0xFF); buf.write((v shr 8) and 0xFF); buf.write(v and 0xFF) }
        fun p8(v: Long) { p4((v shr 32).toInt()); p4(v.toInt()) }
        fun pStr(s: String) { buf.write(s.toByteArray(Charsets.ISO_8859_1)); buf.write(0) }
        // gjStr2: versioned string = version byte (0x00) + null-terminated CP1252 string
        fun pjStr2(s: String) { p1(0x00); pStr(s) }

        val nowMs = System.currentTimeMillis()

        p1(0)                      // #1 hasTotpUpdate: no
        p1(1)                      // #2 membershipType: 1 = member
        p1(30)                     // #3 membershipDays: 30
        p1(1)                      // #4 emailValidated: yes (bool)
        p3(0)                      // #5 recoveryDelay: 0 (signed medium)
        p1(0)                      // #6 staffModLevel: 0 = none
        p1(0)                      // #7 unknownFlag1 (bool)
        p1(0)                      // #8 unknownFlag2 (bool)
        p8(nowMs)                  // #9 membershipTimestamp: Unix millis (NOT micros)
        p1(0)                      // #10 timeDaysByte
        p4((nowMs / 1000).toInt()) // #11 timeMillisInt (seconds since epoch, low 32 bits)
        p1(0)                      // #12 flagsByte
        p4(0)                      // #13 lastLoginIP
        p4(5000)                   // #14 lastLoginDays (5000 = "long ago")
        p2(1)                      // #15 playerIndex
        p2(0)                      // #16 unknown3
        p2(0)                      // #17 unknown4
        p4(0)                      // #18 unknown5
        p1(0)                      // #19 unknown6
        p2(0)                      // #20 unknown7
        p2(0)                      // #21 unknown8
        p1(1)                      // #22 isMembersWorld (bool)
        pjStr2("Player")           // #23 displayName (gjStr2: version + null-terminated)
        p1(0)                      // #24 unknown9
        p4(0)                      // #25 unknown10
        p2(1)                      // #26 worldId (1 = world 1, from jav_config param=39)
        pjStr2("localhost")        // #27 serverHostname (gjStr2: version + null-terminated)
        p2(43594)                  // #28 gamePort
        p2(443)                    // #29 httpsPort
        // CRITICAL: Session tokens MUST be stable across logins!
        // If tokens change, the client clears its entire JS5 disk cache (LoginStepHandleLoginData).
        // Using fixed tokens prevents cache wipe on every login.
        p8(0x4461726B616E3333L)    // #30 sessionToken1 = "Darkan33" as ASCII
        p8(0x5365727665723033L)    // #31 sessionToken2 = "Server03" as ASCII

        return buf.toByteArray()
    }

    /**
     * Send initial lobby packets to get the client to render the lobby UI.
     *
     * Sequence matches the Jagex live server capture (from variables-re.md):
     * 1. RESET_ALL_VARPS -- clear all varps/varcs
     * 2. IF_SETGRAPHIC(906) -- open lobby root interface (opcode 126, 19B)
     * 3. IF_SETPOSITION x21 -- open sub-interfaces on 906's components (opcode 38, 23B)
     * 4. SET_READY_FLAG -- signal server ready
     * 5. UPDATE_IGNORELIST -- empty ignore list
     * 6. SET_RUN_ENERGY -- energy=1
     * 7. UPDATE_SITESETTINGS -- empty site settings
     */
    private suspend fun sendLobbyInitPackets(session: GameSession) {
        // 1. RESET_ALL_VARPS
        session.writeEmpty(ServerProt.RESET_ALL_VARPS)
        logTrace("Sent RESET_ALL_VARPS to ${session.ip}")

        // 2. Open lobby root interface
        session.write(IfOpenTopLobbyEncoder(LOBBY_INTERFACE_ID))
        logInfo("Sent IF_OPENTOP_LOBBY interface=$LOBBY_INTERFACE_ID to ${session.ip}")

        // 3. Open all sub-interfaces
        for ((parentComponent, subIfId) in LOBBY_SUB_INTERFACES) {
            session.write(IfOpenSubEncoder(LOBBY_INTERFACE_ID, parentComponent, subIfId))
        }
        logInfo("Sent ${LOBBY_SUB_INTERFACES.size}x IF_OPENSUB to ${session.ip}")

        // 4. SET_READY_FLAG
        session.writeEmpty(ServerProt.SET_READY_FLAG)
        logTrace("Sent SET_READY_FLAG to ${session.ip}")

        // 5. UPDATE_IGNORELIST (empty)
        session.write(UpdateIgnorelistEncoder())
        logTrace("Sent UPDATE_IGNORELIST (empty) to ${session.ip}")

        // 6. SET_RUN_ENERGY
        session.write(SetRunEnergyEncoder(1))
        logTrace("Sent SET_RUN_ENERGY to ${session.ip}")

        // 7. UPDATE_SITESETTINGS (empty)
        session.write(UpdateSiteSettingsEncoder())
        logTrace("Sent UPDATE_SITESETTINGS (empty) to ${session.ip}")

        session.flush()
        logInfo("Sent lobby init packets to ${session.ip}")
    }

    /**
     * Read ISAAC-encrypted packets from the client in lobby state.
     *
     * Uses [LobbyPacketRegistry] to dispatch packets to their decoders.
     * Unregistered opcodes are logged but not fatal.
     */
    private suspend fun lobbySessionLoop(session: GameSession) {
        var packetCount = 0
        var lastKeepaliveSent = System.currentTimeMillis()

        try {
            // Send initial NOOP to let client know we're alive
            session.writeEmpty(ServerProt.NOOP)
            session.flush()
            logTrace("Sent initial NOOP to ${session.ip}")

            while (true) {
                val opcode = session.readOpcode()
                val prot = ClientProt.forOpcode(opcode)

                if (prot == null) {
                    logError("Client opcode $opcode out of range from ${session.ip} -- desync likely")
                    break
                }

                val payload = session.readPayload(prot)
                packetCount++

                val decoder = LobbyPacketRegistry.get(opcode)
                if (decoder != null) {
                    decoder.decode(BufferReader(payload), session)
                    if (packetCount % 50 == 0 && (opcode == ClientProt.NO_TIMEOUT.opcode || opcode == ClientProt.NO_TIMEOUT_2.opcode)) {
                        logTrace("Keepalive #$packetCount from ${session.ip}")
                    }
                } else {
                    logInfo("Client packet from ${session.ip}: opcode=$opcode (0x${"%02x".format(opcode)}) size=${payload.size} payload=${payload.take(32).joinToString(" ") { "%02x".format(it) }}")
                }

                // Send periodic keepalives back to the client
                val now = System.currentTimeMillis()
                if (now - lastKeepaliveSent > KEEPALIVE_INTERVAL_MS) {
                    session.writeEmpty(ServerProt.NOOP)
                    session.flush()
                    lastKeepaliveSent = now
                }

                if (packetCount > 100000) {
                    logError("Too many packets from ${session.ip}, disconnecting")
                    break
                }
            }
        } catch (e: Exception) {
            logTrace("Lobby session ended for ${session.ip}: ${e::class.simpleName}: ${e.message}")
        }
        logInfo("Lobby session closed for ${session.ip} after $packetCount packets")
    }

    companion object {
        private const val KEEPALIVE_INTERVAL_MS = 15_000L

        /** Lobby root interface ID. 906 is used by Jagex live (from capture). */
        private const val LOBBY_INTERFACE_ID = 906

        /**
         * Sub-interfaces opened on lobby interface 906 components.
         * From Jagex live capture. Each pair is (parentComponent, subInterfaceId).
         */
        private val LOBBY_SUB_INTERFACES = listOf(
            44 to 779,
            45 to 782,
            46 to 781,
            48 to 784,
            47 to 717,
            49 to 783,
            144 to 786,
            145 to 787,
            146 to 785,
            154 to 943,
            148 to 929,
            149 to 954,
            100 to 955,
            101 to 953,
            99 to 941,
            151 to 952,
            147 to 939,
            51 to 957,
            139 to 928,
            171 to 1450,
            140 to 945,
        )
    }
}
