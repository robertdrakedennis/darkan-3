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
import org.darkan.core.model.Account
import org.darkan.core.model.IFEvents
import org.darkan.core.model.Vars
import org.darkan.core.mongo.Accounts
import org.darkan.core.net.prot.*
import org.darkan.core.net.prot.handler.PacketHandlers
import org.darkan.core.net.session.GameSession
import org.darkan.lobby.LobbyState
import org.darkan.lobby.social.SocialManager
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

        // Parse username
        val username: String
        try {
            val stringUsername = xtea.readUByte().toInt() == 1
            username = if (stringUsername) {
                xtea.readRSString()
            } else {
                xtea.readLong().toRSString()
            }
            logInfo("Login username: '$username' (string=$stringUsername) from $ip")

            val remaining = if (xtea.remaining > 0) xtea.readByteArray(xtea.remaining.toInt()) else ByteArray(0)
            logTrace("XTEA remaining ${remaining.size} bytes: ${remaining.take(64).joinToString(" ") { "%02x".format(it) }}")
        } catch (e: Exception) {
            logTrace("XTEA parsing stopped: ${e::class.simpleName}: ${e.message}")
            output.finish(ResponseOpcode.COULD_NOT_COMPLETE_LOGIN)
            return
        }

        // Step 5: Look up or create account in MongoDB
        val account = Accounts.getOrCreate(username)
        account.lastIp = ip
        Accounts.save(account)
        logInfo("Account loaded: ${account.displayName} (${account.username}) from $ip")

        // Step 6: Send login success
        logInfo("Sending login success to $ip")

        // Initialize ISAAC ciphers
        val inCipher = Isaac(isaacKeys.copyOf())
        val outKeys = isaacKeys.copyOf()
        for (i in outKeys.indices) outKeys[i] += EnvVars.ISAAC_DELTA
        val outCipher = Isaac(outKeys)

        // Send login result: byte 2 (SUCCESS)
        output.writeByte(ResponseOpcode.SUCCESS)

        // Send lobby data length + lobby data
        val lobbyData = buildLobbyData(account)
        output.writeByte(lobbyData.size.toByte())
        output.writeFully(lobbyData)
        output.flush()

        logInfo("Login complete for $ip (lobby data: ${lobbyData.size} bytes)")

        // Create session with codec
        val codec = Codec.get(947) ?: error("Rev947 codec not registered!")
        val session = GameSession(output, inCipher, outCipher, ip, codec, username = account.username)

        // Register in SocialManager for presence tracking
        SocialManager.registerPlayer(account, session)

        // Step 7: Send initial lobby packets
        sendLobbyInitPackets(session, account)

        // Step 8: Lobby session loop — reader coroutine + handler dispatch
        try {
            coroutineScope {
                launch { session.readPackets(input) }
                lobbySessionLoop(session)
            }
        } finally {
            SocialManager.unregisterPlayer(account.username)
        }
    }

    /**
     * Build the lobby login data blob using real account data.
     */
    private fun buildLobbyData(account: Account): ByteArray {
        val buf = BufferWriter(128)
        val nowMs = System.currentTimeMillis()

        buf.writeByte(0)                          // #1 hasTotpUpdate: no
        buf.writeByte(1)                          // #2 membershipType: 1 = member
        buf.writeByte(30)                         // #3 membershipDays: 30
        buf.writeByte(1)                          // #4 emailValidated: yes (bool)
        buf.writeMedium(0)                        // #5 recoveryDelay: 0 (signed medium)
        buf.writeByte(account.rights)             // #6 staffModLevel (0=none, 1=mod, 2=admin)
        buf.writeByte(0)                          // #7 unknownFlag1 (bool)
        buf.writeByte(0)                          // #8 unknownFlag2 (bool)
        buf.writeLong(nowMs)                      // #9 membershipTimestamp: Unix millis
        buf.writeByte(0)                          // #10 timeDaysByte
        buf.writeInt((nowMs / 1000).toInt())      // #11 timeMillisInt (seconds since epoch)
        buf.writeByte(0)                          // #12 flagsByte: 0 = NOT quickchat-only
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
        buf.writePrefixedString(account.displayName) // #23 displayName
        buf.writeByte(0)                          // #24 unknown9
        buf.writeInt(0)                           // #25 unknown10
        buf.writeShort(300)                       // #26 worldId
        buf.writePrefixedString("localhost")      // #27 serverHostname
        buf.writeShort(43594)                     // #28 gamePort
        buf.writeShort(443)                       // #29 httpsPort
        buf.writeLong(0x4461726B616E3333L)        // #30 sessionToken1 = "Darkan33"
        buf.writeLong(0x5365727665723033L)        // #31 sessionToken2 = "Server03"

        return buf.toArray()
    }

    /**
     * Send initial lobby packets to get the client to render the lobby UI.
     */
    private suspend fun sendLobbyInitPackets(session: GameSession, account: Account) {
        // 1. UPDATE_STAT x29
        for ((statId, xp, level) in DEFAULT_STATS) {
            session.send(UpdateStat(statId, xp, level))
        }
        logTrace("Sent ${DEFAULT_STATS.size}x UPDATE_STAT to ${session.ip}")

        // 2. RESET_ALL_VARPS + init Vars
        val vars = Vars()
        vars.init(session)
        session.send(ResetClientVarcache())

        // 3. Set lobby-relevant varps (identified from CS2 lobby scripts)
        // Only varps actually referenced by lobby CS2 scripts are sent.
        vars.setVar(VARP_LAST_LOGIN_RUNEDAY, 7079)    // last login date (runedays)
        vars.setVar(VARP_CHAT_STATE, 0)                // 0 = safe default (don't hide chat)
        vars.setVar(VARP_TREASURY_TIMESTAMP, 8792)     // treasury/notification timestamp
        vars.setVar(VARP_MEMBERSHIP_NOTIF_1, 1)        // membership notification flag
        // varp 6681 = membership comparison value (only needed if 6680 is set)
        vars.syncAllToClient()
        logTrace("Sent lobby varps to ${session.ip}")

        // 4. Pre-interface varcs
        vars.setVarc(VARC_NOTIFICATION_COUNT, 0)   // no pending notifications
        vars.setVarc(VARC_RENDER_FLAG, 0)
        vars.setVarc(VARC_NOTIFY_7108, 0)          // false = no notification
        vars.setVarc(VARC_INBOX_STATE, -1)          // -1 = no inbox messages
        vars.setVarc(VARC_INBOX_TYPE, -2)           // -2 = no type
        vars.setVarc(VARC_MUSIC_VOLUME, 150)        // default volume

        // 5. IF_OPENTOP + IF_OPENSUB
        session.send(IfOpenTopLobby(LOBBY_INTERFACE_ID))
        for ((parentComponent, subIfId) in LOBBY_SUB_INTERFACES) {
            session.send(IfOpenSubLobby(LOBBY_INTERFACE_ID, parentComponent, subIfId))
        }
        logInfo("Sent IF_OPENTOP($LOBBY_INTERFACE_ID) + ${LOBBY_SUB_INTERFACES.size}x IF_OPENSUB to ${session.ip}")

        // 6. Post-interface varcs
        vars.setVarc(VARC_MEMBERSHIP_TIER, 294)    // membership tier threshold
        vars.setVarc(VARC_MEMBERSHIP_TIMER, 592000) // membership notification timer
        vars.setVarc(VARC_TIMER_1776, 592000)       // display timer
        vars.setVarc(VARC_BONDS_TRADEABLE, 0)       // 0 tradeable bonds
        vars.setVarc(VARC_BONDS_UNTRADEABLE, 0)     // 0 untradeable bonds
        vars.setVarc(VARC_RUNECOINS, 0)             // 0 RuneCoins

        // 7. IF_SETEVENTS — from 947-1 capture: settings=0, comp varies, ifId=907, fromSlot=1, settings=2
        for (comp in LOBBY_SETEVENTS_COMPONENTS) {
            session.send(IfSetEvents(IFEvents(LOBBY_SETEVENTS_INTERFACE, comp, 1, 0, LOBBY_SETEVENTS_SETTINGS)))
        }

        // 8. SET_RUN_ENERGY → SET_READY_FLAG → CHANGE_LOBBY → UPDATE_FRIENDLIST
        session.send(UpdateRunenergy(1))
        session.send(SetReadyFlag())
        session.send(UpdateIgnoreList())  // CHANGE_LOBBY (empty)

        // Send real friend list from account data
        val friendEntries = SocialManager.buildFriendList(account)
        session.send(UpdateFriendList(friendEntries))

        // Send world list (full refresh on login)
        session.send(WorldListPacket(LobbyState.worldList, fullRefresh = true))

        session.flush()
        logInfo("Sent lobby init packets to ${session.ip}")
    }

    // sendVarc removed — use Vars.setVarc() instead

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

        // --- Lobby-relevant varp IDs (from CS2 lobby script analysis) ---
        private const val VARP_LAST_LOGIN_RUNEDAY = 1749  // DATE_RUNEDAY at last login
        private const val VARP_CHAT_STATE = 3185           // chat state machine (0=default, -4=hidden)
        private const val VARP_TREASURY_TIMESTAMP = 6601   // treasury notification timestamp
        private const val VARP_MEMBERSHIP_NOTIF_1 = 6679   // membership notification flag 1

        // --- Lobby-relevant varc IDs (from CS2 script cross-reference) ---
        private const val VARC_INBOX_STATE = 1027          // inbox/notification state (-1=none)
        private const val VARC_INBOX_TYPE = 1034           // inbox notification type (-2=none)
        private const val VARC_MUSIC_VOLUME = 1928         // audio volume setting (switch in script 3904/6566)
        private const val VARC_TIMER_1776 = 1776           // display timer (script 12083)
        private const val VARC_NOTIFICATION_COUNT = 2643   // notification counter (shown in UI if 1-4)
        private const val VARC_RENDER_FLAG = 3496          // boolean flag (script 16901)
        private const val VARC_RUNECOINS = 4266            // RuneCoins balance (script 11164)
        private const val VARC_MEMBERSHIP_TIER = 4787      // membership/premium tier threshold
        private const val VARC_MEMBERSHIP_TIMER = 4788     // membership notification timer
        private const val VARC_BONDS_TRADEABLE = 4968      // tradeable bonds count
        private const val VARC_BONDS_UNTRADEABLE = 4969    // untradeable bonds count
        private const val VARC_NOTIFY_7108 = 7108          // boolean notification flag

        private const val LOBBY_SETEVENTS_INTERFACE = 907
        private val LOBBY_SETEVENTS_COMPONENTS = intArrayOf(39, 75, 46, 101)
        private const val LOBBY_SETEVENTS_SETTINGS = 0x0002  // from capture: last 2 bytes = 02 00 LE = 2

        // Dump file loaders removed — lobby only needs the named varps above.
    }
}
