package org.darkan.lobby.server

import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
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
import org.darkan.core.model.IFEvents
import org.darkan.core.net.prot.*
import org.darkan.core.net.prot.handler.PacketHandlers
import org.darkan.core.net.session.GameSession
import world.gregs.voidps.buffer.*
import world.gregs.voidps.buffer.read.BufferReader
import world.gregs.voidps.buffer.write.BufferWriter
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
        val sessionKey = ByteArray(8).also { random.nextBytes(it) }
        output.writeByte(ResponseOpcode.JS5_SYNC) // 0 = OK
        output.writeFully(sessionKey)
        output.flush()
        logTrace("Sent exchange data (9 bytes) to $ip")

        // Step 2: Read the login packet
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

        var password = ""
        if (decryptedRsa.remaining > 0) {
            password = decryptedRsa.readRSString()
            logTrace("Password field present (${password.length} chars) from $ip")
        }

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
        val inCipher = Isaac(isaacKeys.copyOf())
        val outKeys = isaacKeys.copyOf()
        for (i in outKeys.indices) outKeys[i] += EnvVars.ISAAC_DELTA
        val outCipher = Isaac(outKeys)

        // Send login result: byte 2 (SUCCESS)
        output.writeByte(ResponseOpcode.SUCCESS)

        // Send lobby data length + lobby data
        val lobbyData = buildLobbyData()
        output.writeByte(lobbyData.size.toByte())
        output.writeFully(lobbyData)
        output.flush()

        logInfo("Login complete for $ip (lobby data: ${lobbyData.size} bytes)")

        // Create session with codec
        val codec = Codec.get(947) ?: error("Rev947 codec not registered!")
        val session = GameSession(output, inCipher, outCipher, ip, codec)

        // Step 6: Send initial lobby packets
        sendLobbyInitPackets(session)

        // Step 7: Lobby session loop — reader coroutine + handler dispatch
        coroutineScope {
            launch { session.readPackets(input) }
            lobbySessionLoop(session)
        }
    }

    /**
     * Build the lobby login data blob.
     */
    private fun buildLobbyData(): ByteArray {
        val buf = BufferWriter(128)
        val nowMs = System.currentTimeMillis()

        buf.writeByte(0)                          // #1 hasTotpUpdate: no
        buf.writeByte(1)                          // #2 membershipType: 1 = member
        buf.writeByte(30)                         // #3 membershipDays: 30
        buf.writeByte(1)                          // #4 emailValidated: yes (bool)
        buf.writeMedium(0)                        // #5 recoveryDelay: 0 (signed medium)
        buf.writeByte(0)                          // #6 staffModLevel: 0 = none
        buf.writeByte(0)                          // #7 unknownFlag1 (bool)
        buf.writeByte(0)                          // #8 unknownFlag2 (bool)
        buf.writeLong(nowMs)                      // #9 membershipTimestamp: Unix millis
        buf.writeByte(0)                          // #10 timeDaysByte
        buf.writeInt((nowMs / 1000).toInt())      // #11 timeMillisInt (seconds since epoch)
        buf.writeByte(0)                          // #12 flagsByte
        buf.writeInt(0)                           // #13 lastLoginIP
        buf.writeInt(5000)                        // #14 lastLoginDays (5000 = "long ago")
        buf.writeShort(1)                         // #15 playerIndex
        buf.writeShort(0)                         // #16 unknown3
        buf.writeShort(0)                         // #17 unknown4
        buf.writeInt(0)                           // #18 unknown5
        buf.writeByte(0)                          // #19 unknown6
        buf.writeShort(0)                         // #20 unknown7
        buf.writeShort(0)                         // #21 unknown8
        buf.writeByte(1)                          // #22 isMembersWorld (bool)
        buf.writePrefixedString("Player")         // #23 displayName (gjStr2)
        buf.writeByte(0)                          // #24 unknown9
        buf.writeInt(0)                           // #25 unknown10
        buf.writeShort(1)                         // #26 worldId
        buf.writePrefixedString("localhost")      // #27 serverHostname (gjStr2)
        buf.writeShort(43594)                     // #28 gamePort
        buf.writeShort(443)                       // #29 httpsPort
        buf.writeLong(0x4461726B616E3333L)        // #30 sessionToken1 = "Darkan33"
        buf.writeLong(0x5365727665723033L)        // #31 sessionToken2 = "Server03"

        return buf.toArray()
    }

    /**
     * Send initial lobby packets to get the client to render the lobby UI.
     */
    private suspend fun sendLobbyInitPackets(session: GameSession) {
        // 1. UPDATE_STAT x29
        for ((statId, xp, level) in DEFAULT_STATS) {
            session.send(UpdateStat(statId, xp, level))
        }
        logTrace("Sent ${DEFAULT_STATS.size}x UPDATE_STAT to ${session.ip}")

        // 2. RESET_ALL_VARPS
        session.send(ResetClientVarcache())

        // 3. SET_VARP — all varps from Jagex live capture
        for ((id, value) in lobbyVarps) {
            if (value in -128..127) {
                session.send(VarpSmall(id, value))
            } else {
                session.send(VarpLarge(id, value))
            }
        }
        logTrace("Sent ${lobbyVarps.size}x SET_VARP to ${session.ip}")

        // 4. Pre-interface varcs
        for ((id, value) in preInterfaceVarcs) {
            sendVarc(session, id, value)
        }

        // 5. IF_OPENTOP + IF_OPENSUB
        session.send(IfOpenTopLobby(LOBBY_INTERFACE_ID))
        for ((parentComponent, subIfId) in LOBBY_SUB_INTERFACES) {
            session.send(IfOpenSubLobby(LOBBY_INTERFACE_ID, parentComponent, subIfId))
        }
        logInfo("Sent IF_OPENTOP($LOBBY_INTERFACE_ID) + ${LOBBY_SUB_INTERFACES.size}x IF_OPENSUB to ${session.ip}")

        // 6. Post-interface varcs
        for ((id, value) in postInterfaceVarcs) {
            sendVarc(session, id, value)
        }
        logTrace("Sent ${preInterfaceVarcs.size + postInterfaceVarcs.size}x SET_VARC to ${session.ip}")

        // 7. IF_SETEVENTS — from 947-1 capture: settings=0, comp varies, ifId=907, fromSlot=1, settings=2
        for (comp in LOBBY_SETEVENTS_COMPONENTS) {
            session.send(IfSetEvents(IFEvents(LOBBY_SETEVENTS_INTERFACE, comp, 1, 0, LOBBY_SETEVENTS_SETTINGS)))
        }

        // 8. SET_RUN_ENERGY → SET_READY_FLAG → UPDATE_IGNORELIST → UPDATE_FRIENDLIST
        session.send(UpdateRunenergy(1))
        session.send(SetReadyFlag())
        session.send(UpdateIgnoreList())
        session.send(UpdateFriendList(TEST_FRIENDS))

        session.flush()
        logInfo("Sent lobby init packets to ${session.ip}")
    }

    private suspend fun sendVarc(session: GameSession, id: Int, value: Int) {
        if (value in -128..127) {
            session.send(ClientSetVarcSmall(id, value))
        } else {
            session.send(ClientSetVarcLarge(id, value))
        }
    }

    /**
     * Dispatch loop for the lobby session.
     * Reads decoded packets from [session.readChannel] and dispatches to handlers.
     * Sends periodic keepalives.
     */
    private suspend fun lobbySessionLoop(session: GameSession) {
        var packetCount = 0
        var lastKeepaliveSent = System.currentTimeMillis()

        try {
            // Send initial keepalive
            session.send(NoTimeout())
            session.flush()
            logTrace("Sent initial NOOP to ${session.ip}")

            while (!session.disconnected) {
                val packet = withTimeoutOrNull(KEEPALIVE_INTERVAL_MS) {
                    session.readChannel.receive()
                }

                if (packet != null) {
                    packetCount++
                    PacketHandlers.handleBlocking<GameSession>(session, packet)
                }

                // Send periodic keepalives
                val now = System.currentTimeMillis()
                if (now - lastKeepaliveSent > KEEPALIVE_INTERVAL_MS) {
                    session.send(NoTimeout())
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

        private const val LOBBY_INTERFACE_ID = 906

        // Sub-interface IDs from live Jagex 947-1 capture (2026-03-23)
        private val LOBBY_SUB_INTERFACES = listOf(
            44 to 907,
            45 to 910,
            46 to 909,
            48 to 912,
            47 to 589,
            49 to 911,
            144 to 914,
            145 to 915,
            146 to 913,
            154 to 815,
            148 to 803,
            149 to 822,
            100 to 825,
            101 to 821,
            99 to 808,
            151 to 820,
            147 to 811,
            51 to 826,
            139 to 801,
            171 to 1322,
            140 to 814,
        )

        private val DEFAULT_STATS: List<Triple<Int, Int, Int>> = buildList {
            for (i in 0..28) {
                if (i == 3) add(Triple(i, 1154, 10))  // Hitpoints
                else add(Triple(i, 0, 1))
            }
        }

        /** Components on interface 907 that receive IF_SETEVENTS during lobby init (from 947-1 capture). */
        private const val LOBBY_SETEVENTS_INTERFACE = 907
        private val LOBBY_SETEVENTS_COMPONENTS = intArrayOf(39, 75, 46, 101)
        private const val LOBBY_SETEVENTS_SETTINGS = 0x0002  // from capture: last 2 bytes = 02 00 LE = 2

        /** Hardcoded test friends for lobby development. */
        private val TEST_FRIENDS = listOf(
            UpdateFriendList.FriendEntry(displayName = "Zezima", worldId = 1, worldName = "Darkan", worldFlags = 1),
            UpdateFriendList.FriendEntry(displayName = "Woox", worldId = 0),
            UpdateFriendList.FriendEntry(displayName = "Suomi"),
        )

        private val lobbyVarps: List<Pair<Int, Int>> = loadIntPairs("/capture/lobby-varps.txt")

        private val preInterfaceVarcs: List<Pair<Int, Int>>
        private val postInterfaceVarcs: List<Pair<Int, Int>>

        init {
            val (pre, post) = loadVarcs("/capture/lobby-varcs.txt")
            preInterfaceVarcs = pre
            postInterfaceVarcs = post
        }

        private fun loadIntPairs(resource: String): List<Pair<Int, Int>> {
            val stream = LoginServer::class.java.getResourceAsStream(resource)
                ?: error("Missing resource: $resource")
            return stream.bufferedReader().useLines { lines ->
                lines.filter { it.isNotBlank() && !it.startsWith("#") }
                    .map { line ->
                        val (id, value) = line.trim().split(" ", limit = 2)
                        id.toInt() to value.toInt()
                    }.toList()
            }
        }

        private fun loadVarcs(resource: String): Pair<List<Pair<Int, Int>>, List<Pair<Int, Int>>> {
            val stream = LoginServer::class.java.getResourceAsStream(resource)
                ?: error("Missing resource: $resource")
            val pre = mutableListOf<Pair<Int, Int>>()
            val post = mutableListOf<Pair<Int, Int>>()
            var target = pre
            stream.bufferedReader().useLines { lines ->
                for (line in lines) {
                    val trimmed = line.trim()
                    when {
                        trimmed.isEmpty() || trimmed.startsWith("#") -> {}
                        trimmed == "---" -> target = post
                        else -> {
                            val (id, value) = trimmed.split(" ", limit = 2)
                            target.add(id.toInt() to value.toInt())
                        }
                    }
                }
            }
            return pre to post
        }
    }
}
