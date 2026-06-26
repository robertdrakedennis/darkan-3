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
import org.darkan.core.net.Session
import org.darkan.core.model.Account
import org.darkan.core.model.IFEvents
import org.darkan.core.model.Vars
import org.darkan.core.mongo.Accounts
import org.darkan.core.net.login.LoginToken
import org.darkan.core.net.login.RsaCredentialTailParser
import org.darkan.core.security.PasswordHash
import org.darkan.core.net.prot.*
import org.darkan.core.net.prot.handler.PacketHandlers
import org.darkan.core.net.session.GameSession
import org.darkan.lobby.LobbyState
import org.darkan.lobby.social.SocialGateway
import world.gregs.voidps.buffer.Cp1252
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
        output.writeByte(ResponseOpcode.JS5_SYNC.toByte()) // 0 = OK
        output.writeFully(sessionKey)
        output.flush()
        logTrace("Sent exchange data (9 bytes) to $ip")

        // Step 2: Read the login packet. These are pre-auth bytes from an unauthenticated
        // peer — bound the reads so a silent connection can't hold the coroutine forever.
        val loginRequest = withTimeoutOrNull(PRE_AUTH_READ_TIMEOUT_MS) {
            val loginOpcode = input.readByte().toInt() and 0xFF
            val size = input.readShort().toInt() and 0xFFFF
            loginOpcode to size
        }
        if (loginRequest == null) {
            logInfo("Pre-auth login read timed out from $ip — disconnecting")
            return
        }
        val (loginOpcode, size) = loginRequest
        logInfo("Login opcode from $ip: $loginOpcode (0x${"%02x".format(loginOpcode)})")

        if (loginOpcode != RequestOpcode.LOBBY && loginOpcode != RequestOpcode.LOGIN) {
            logError("Unexpected login opcode: $loginOpcode from $ip")
            output.finishLogin(ResponseOpcode.INVALID_LOGIN_SERVER)
            return
        }

        logTrace("Login packet size: $size from $ip")

        if (size <= 0 || size > 5000) {
            logError("Invalid login packet size: $size from $ip")
            output.finishLogin(ResponseOpcode.COULD_NOT_COMPLETE_LOGIN)
            return
        }

        val packetData = ByteArray(size)
        val bodyRead = withTimeoutOrNull(PRE_AUTH_READ_TIMEOUT_MS) {
            input.readFully(packetData, 0, size)
        }
        if (bodyRead == null) {
            logInfo("Pre-auth login body read timed out from $ip — disconnecting")
            return
        }

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
            output.finishLogin(ResponseOpcode.GAME_UPDATE)
            return
        }

        // RSA block
        val rsaSize = packet.readUShort().toInt()
        logTrace("RSA block size: $rsaSize from $ip")

        if (rsaSize <= 0 || rsaSize > 512) {
            logError("Invalid RSA block size: $rsaSize from $ip")
            output.finishLogin(ResponseOpcode.COULD_NOT_COMPLETE_LOGIN)
            return
        }

        val rsaBytes = packet.readByteArray(rsaSize)
        val decryptedRsa = ByteReadPacket(RSA.crypt(rsaBytes, rsaMod, rsaExp))

        // RSA block contents: magic(1) + xtea keys(16) + session data
        val magic = decryptedRsa.readUByte().toInt()
        if (magic != 10) {
            logError("RSA magic mismatch: expected 10, got $magic from $ip (bad RSA key?)")
            output.finishLogin(ResponseOpcode.BAD_SESSION_ID)
            return
        }

        val isaacKeys = IntArray(4) { decryptedRsa.readInt() }
        logTrace("ISAAC keys: ${isaacKeys.joinToString(", ") { "0x${"%08x".format(it)}" }} from $ip")

        // Read remaining RSA data (session ID check, password, etc.)
        val sessionCheck = decryptedRsa.readLong()
        logTrace("RSA session check: $sessionCheck from $ip")

        val credentialTail = if (decryptedRsa.remaining > 0) {
            RsaCredentialTailParser.parse(decryptedRsa.readByteArray(decryptedRsa.remaining.toInt()))
        } else {
            RsaCredentialTailParser.parse(ByteArray(0))
        }
        logTrace("RSA credential type: ${credentialTail.credentialType ?: "legacy"} from $ip")
        logTrace("RSA token string: ${credentialTail.tokenString.length} bytes from $ip")
        val password = credentialTail.password
        logTrace("RSA password: ${password.length} chars from $ip")

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
                xtea.readCp1252String()
            } else {
                xtea.readLong().toBase37Name()
            }
            logInfo("Login username: '$username' (string=$stringUsername) from $ip")

            val remaining = if (xtea.remaining > 0) xtea.readByteArray(xtea.remaining.toInt()) else ByteArray(0)
            logTrace("XTEA remaining ${remaining.size} bytes: ${remaining.take(64).joinToString(" ") { "%02x".format(it) }}")
        } catch (e: Exception) {
            logTrace("XTEA parsing stopped: ${e::class.simpleName}: ${e.message}")
            output.finishLogin(ResponseOpcode.COULD_NOT_COMPLETE_LOGIN)
            return
        }

        // Step 5: Look up or create account in MongoDB
        val existing = Accounts.findByUsername(username)
        val account = if (existing != null) {
            if (password.isNotEmpty() && !PasswordHash.verifySuspend(password, existing.passwordHash)) {
                logInfo("Invalid password for ${existing.username} from $ip (password ${password.length} chars, hash=${existing.passwordHash.take(20)}...)")
                output.finishLogin(ResponseOpcode.INVALID_CREDENTIALS)
                return
            }
            existing
        } else {
            if (password.isEmpty()) {
                logInfo("New account '$username' with no password from $ip — rejecting")
                output.finishLogin(ResponseOpcode.INVALID_CREDENTIALS)
                return
            }
            logInfo("Creating new account '$username' with password (${password.length} chars) from $ip")
            Accounts.create(
                username = username.toProtocolName(),
                email = "${username.toProtocolName()}@darkan.local",
                password = password,
            )
        }
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

        // Send lobby data length + lobby data
        val lobbyData = buildLobbyData(account)
        // The length is a single unsigned byte (0..255). If buildLobbyData ever exceeds 255 bytes
        // the cast would write a wrapped/negative length and silently desync the frame — fail loudly.
        if (lobbyData.size !in 0..255) {
            logError("Lobby data too large for single-byte length: ${lobbyData.size} bytes from $ip — aborting login")
            output.finishLogin(ResponseOpcode.COULD_NOT_COMPLETE_LOGIN)
            return
        }

        // Send login result: byte 2 (SUCCESS)
        output.writeByte(ResponseOpcode.SUCCESS.toByte())

        output.writeByte(lobbyData.size.toByte())
        output.writeFully(lobbyData)
        output.flush()

        logInfo("Login complete for $ip (lobby data: ${lobbyData.size} bytes)")

        // Create session with codec
        val codec = Codec.get(948) ?: error("Rev948 codec not registered!")
        val session = GameSession(output, inCipher, outCipher, ip, codec, username = account.username)

        // Register in SocialGateway for presence tracking
        SocialGateway.registerLobbyPlayer(account, session)

        // Step 7: Send initial lobby packets
        sendLobbyInitPackets(session, account)

        // Step 8: Lobby session loop — reader coroutine + handler dispatch
        try {
            coroutineScope {
                launch { session.readPackets(input) }
                lobbySessionLoop(session)
            }
        } finally {
            SocialGateway.unregisterLobbyPlayer(account.username)
        }
    }

    /**
     * Build the lobby login data blob using real account data.
     *
     * `internal` (not `private`) so [LobbyLoginResponseTest] can pin the world-connect target tail
     * (worldId/host/portA/portB, #26–#29) byte-for-byte — it is the only thing that gets a cold
     * lobby into a world (§10), so a regression there is silent-but-fatal.
     */
    internal fun buildLobbyData(account: Account): ByteArray {
        val buf = BufferWriter(128)
        val nowMs = System.currentTimeMillis()

        // 948 prod-shaped lobby player/status prefix through the byte before displayName.
        buf.writeByte(0)
        buf.writeByte(0)
        buf.writeByte(0)
        buf.writeByte(0)
        buf.writeMedium(0xFFFAFA)
        buf.writeByte(account.rights)
        buf.writeByte(0)
        buf.writeByte(1)
        buf.writeLong(0L)
        buf.writeByte(0x61)
        buf.writeInt(0x0417A208)
        buf.writeByte(0)
        buf.writeInt(0)
        buf.writeInt(0)
        buf.writeShort(0)
        buf.writeShort(0)
        buf.writeShort(0x22B3)
        buf.writeInt(0xADA90A4A.toInt())
        buf.writeByte(3)
        buf.writeShort(0xD21F)
        buf.writeShort(0xD21F)
        buf.writeByte(0)
        buf.writeByte(0)
        // FUN_00126e90 is called with param_3=1 here: first byte is a version byte that must be 0.
        buf.writePrefixedString(account.displayName)

        // ── World-connect target tail (docs/protocol/lobby-world-switch-948.md §10.3) ──
        // VERIFIED against rs2client.948-5 LoginStepHandleLoginData @0x001cd360 (LOBBY branch).
        // This is how a cold-lobby "Play Now" learns the world host:port — NOT op212/op213.
        // The client parses worldId(g2) → host(gStr) → portA(g2) → portB(g2) → sid1(u64) →
        // sid2(u64) at the TAIL of this block, builds a WorldTarget, stages it PENDING at
        // WorldSwitcher+0xa8, and CommitWorldTargetFromLogin promotes it to CURRENT (+0x20) —
        // the slot LoginStepWaitingConnectionOpened reads for the (type-2) world connect.
        buf.writeByte(0)
        buf.writeInt(0x000013D3)
        // #26 worldId — g2 (BE u16). MUST be the real world number, NOT 0xffff: the client maps
        // 0xffff→-1 ("no world") and CommitWorldTargetFromLogin (@0x001acdf0) early-returns on
        // worldId==-1, leaving CURRENT (+0x20) empty so the world connect has no target.
        buf.writeShort(EnvVars.worldId)
        // #27 serverHostname — same versioned string reader as displayName.
        buf.writePrefixedString(EnvVars.worldHost)
        // #28/#29 portA/portB — g2 (BE u16) each. OpenConnection (@0x00b21d70) uses portA when the
        // target's +0x2c select byte is 0; the login-response builder sets +0x2c=1 → portB is used.
        // Send the same world listen port (EnvVars.worldPort, default 43595) in BOTH so the connect
        // hits the world server regardless of which the select byte picks. There is NO hardcoded 443
        // in the connect path — the prior 443 here (an "httpsPort" guess) would have been the port the
        // client connected to via portB. Port comes from config; do not hardcode.
        buf.writeShort(EnvVars.worldPort)         // #28 portA
        buf.writeShort(EnvVars.worldPort)         // #29 portB
        val worldToken = LoginToken.issueCompact(
            username = account.username,
            nowMs = nowMs,
            ttlMs = EnvVars.worldLoginTokenTtlMs,
            secret = EnvVars.worldLoginTokenSecret,
        )
        buf.writeLong(worldToken.part1)           // #30 sessionId1 (u64 BE) — compact world-login token; → LoginManager+0xf0
        buf.writeLong(worldToken.part2)           // #31 sessionId2 (u64 BE) — compact world-login signature; → LoginManager+0xf8

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

        // No pre-interface world list — sent at the end of init (matching Jagex sequence)

        // 5. IF_SETTOPLEVELINTERFACE + IF_SETPOSITION — the REAL 948 Jagex lobby interface setup.
        // Ground truth: capture/login-20260531-191837_s1/decoded.log (live MITM vs lobby6a).
        // The 948 lobby does NOT use the generic in-game IF_OPENTOP(39)+IF_OPENSUB(94); using those
        // left dangling interface components and the next-frame event flush dereffed a bad component
        // pointer → SIGSEGV (docs/net/serverprot/948-worldlist-crash-diagnosis.md).
        //
        // IF_SETTOPLEVELINTERFACE (op 3, 19B): switches the active top-level interface to the lobby
        // parent (906). Encoder verified byte-identical to the captured payload.
        session.send(IfSetTopLevelInterface(topLevelId = LOBBY_INTERFACE_ID))
        // IF_SETPOSITION (op 82, 23B) ×21: places each child interface into its slot on the parent.
        //   layer       = 1 (capture byte 0x7F = writeByteSubtract(1)).
        //   position    = packed parent component hash = (906 << 16) | slot.
        //   componentId = the child interface id placed at that slot.
        // The (slot → child) pairs and ORDER come straight from the capture (== LOBBY_SUB_INTERFACES).
        for ((slot, childInterfaceId) in LOBBY_SUB_INTERFACES_BEFORE_TIMER) {
            val position = (LOBBY_INTERFACE_ID shl 16) or (slot and 0xFFFF)
            session.send(
                IfSetPosition(
                    componentId = childInterfaceId,
                    layer = 1,
                    position = position,
                )
            )
        }

        // 5b. RUNCLIENTSCRIPT — timer display setup on sub-interface components (from Jagex capture)
        // Script 7486: sets up countdown timers. int0=timer minutes, int1=component hash.
        val timerMinutes = ((System.currentTimeMillis() / 60000) + (24 * 365 * 60)).toInt() // ~1 year future
        session.send(RunClientScript.of(SCRIPT_TIMER_SETUP, timerMinutes,
            RunClientScript.componentHash(801, 5)))
        session.send(RunClientScript.of(SCRIPT_TIMER_SETUP, timerMinutes,
            RunClientScript.componentHash(910, 15)))
        for ((slot, childInterfaceId) in LOBBY_SUB_INTERFACES_AFTER_TIMER) {
            val position = (LOBBY_INTERFACE_ID shl 16) or (slot and 0xFFFF)
            session.send(
                IfSetPosition(
                    componentId = childInterfaceId,
                    layer = 1,
                    position = position,
                )
            )
        }
        logInfo("Sent IF_SETTOPLEVELINTERFACE($LOBBY_INTERFACE_ID) + ${LOBBY_SUB_INTERFACES.size}x IF_SETPOSITION to ${session.ip}")

        // 6. Post-interface varcs
        vars.setVarc(VARC_LOBBY_CTA_STATE_A, 0)
        vars.setVarc(VARC_LOBBY_CTA_STATE_B, 0)
        vars.setVarc(VARC_LOBBY_TIMER_STATE, 0)
        vars.setVarc(VARC_BONDS_TRADEABLE, 0)
        vars.setVarc(VARC_BONDS_UNTRADEABLE, 0)
        vars.setVarc(VARC_BONDS_TRADEABLE, 0)
        vars.setVarc(VARC_BONDS_UNTRADEABLE, 0)

        // 7. IF_SETEVENTS
        for (comp in LOBBY_SETEVENTS_COMPONENTS) {
            session.send(IfSetEvents(IFEvents(LOBBY_SETEVENTS_INTERFACE, comp, 1, 0, LOBBY_SETEVENTS_SETTINGS)))
        }
        vars.setVarc(VARC_RUNECOINS, 0)
        vars.setVarc(VARC_LOBBY_CTA_STATE_B, 0)
        vars.setVarc(VARC_LOBBY_CTA_STATE_A, 0)

        // 8. Lobby news entries — script 10931 with 9 args:
        //   arg0(i)=slot, arg1(i)=spriteId, arg2(i)=category, arg3(i)=-1,
        //   arg4(s)=title, arg5(s)=summary, arg6(s)=urlSlug, arg7(s)=date, arg8(i)=0
        for ((slot, entry) in LOBBY_NEWS.withIndex()) {
            session.send(RunClientScript.of(SCRIPT_LOBBY_NEWS, slot, entry.spriteId,
                entry.category, -1, entry.title, entry.summary, entry.slug, entry.date, 0))
        }
        // Final news script — signals end of news entries
        session.send(RunClientScript.of(SCRIPT_LOBBY_NEWS_END))

        // 9. Lobby refresh before social/worldlist.
        session.send(UpdateRunenergy(1))
        session.send(SetReadyFlag())
        session.send(ChangeLobby())
        session.send(NoopVarA())

        // 10. Social init — friend list (UPDATE_FRIENDLIST op26, sent EMPTY even with no friends
        // so the client marks the tab loaded), ignore list, chat filters, mutual-friend notify.
        // MUST be here: after the interface setup (the friends-tab components must exist before the
        // client can populate them) and before WorldList/SetReadyFlag — matches the live capture,
        // where op26 fires in this slot, not at login time. Presence was registered in
        // registerLobbyPlayer() before sendLobbyInitPackets().
        SocialGateway.initializeSocial(account)

        // 11. World list — sent near the end (matching Jagex sequence)
        session.send(WorldListPacket(LobbyState.worldList, fullRefresh = true))

        session.flush()
        logInfo("Sent lobby init packets to ${session.ip}")
    }

    /**
     * Dispatch loop for the lobby session.
     * Reads decoded packets from [session.readChannel] and dispatches to handlers.
     */
    private suspend fun lobbySessionLoop(session: GameSession) {
        var packetCount = 0L
        var rateWindowStart = System.currentTimeMillis()
        var rateWindowCount = 0

        try {
            while (!session.disconnected) {
                val packet = withTimeoutOrNull(KEEPALIVE_INTERVAL_MS) {
                    session.readChannel.receive()
                }

                if (packet != null) {
                    packetCount++
                    rateWindowCount++
                    PacketHandlers.handle<GameSession>(session, packet)
                }

                // Flush any queued responses after processing packets
                session.flush()

                val now = System.currentTimeMillis()

                // Windowed rate check (NOT a lifetime cap — a healthy client would trip
                // a lifetime cap eventually). Disconnect so the socket actually closes.
                if (now - rateWindowStart >= PACKET_RATE_WINDOW_MS) {
                    rateWindowStart = now
                    rateWindowCount = 0
                } else if (rateWindowCount > MAX_PACKETS_PER_RATE_WINDOW) {
                    logError("Packet flood from ${session.ip}: >$MAX_PACKETS_PER_RATE_WINDOW packets in ${PACKET_RATE_WINDOW_MS}ms, disconnecting")
                    session.disconnect()
                    break
                }
            }
        } catch (e: Exception) {
            if (Session.isExpectedDisconnect(e)) {
                logTrace("Lobby session ended for ${session.ip}: ${e::class.simpleName}: ${e.message}")
            } else {
                logError("Lobby session error for ${session.ip}", e)
            }
        }
        logInfo("Lobby session closed for ${session.ip} after $packetCount packets")
    }

    companion object {
        private const val KEEPALIVE_INTERVAL_MS = 15_000L

        /** Max time an unauthenticated peer may take to deliver each pre-auth handshake read. */
        private const val PRE_AUTH_READ_TIMEOUT_MS = 10_000L

        /** Rolling window for the inbound packet-rate check. */
        private const val PACKET_RATE_WINDOW_MS = 10_000L

        /**
         * Max client packets per [PACKET_RATE_WINDOW_MS] window. A healthy lobby client
         * sends ~1 keepalive/s plus sparse UI events — 2000/10s (200/s sustained) is far
         * above legitimate traffic while still catching floods quickly.
         */
        private const val MAX_PACKETS_PER_RATE_WINDOW = 2_000

        private const val LOBBY_INTERFACE_ID = 906

        // Sub-interface IDs from live Jagex 947-1 capture (2026-03-23)
        private val LOBBY_SUB_INTERFACES_BEFORE_TIMER = listOf(
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
        )
        private val LOBBY_SUB_INTERFACES_AFTER_TIMER = listOf(
            171 to 1322,
            140 to 814,
        )
        private val LOBBY_SUB_INTERFACES = LOBBY_SUB_INTERFACES_BEFORE_TIMER + LOBBY_SUB_INTERFACES_AFTER_TIMER

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
        private const val VARC_LOBBY_TIMER_STATE = 1800    // production resets before lobby events
        private const val VARC_NOTIFICATION_COUNT = 2643   // notification counter (shown in UI if 1-4)
        private const val VARC_RENDER_FLAG = 3496          // boolean flag (script 16901)
        private const val VARC_RUNECOINS = 4266            // RuneCoins balance (script 11164)
        private const val VARC_LOBBY_CTA_STATE_A = 4659    // production resets around lobby CTA setup
        private const val VARC_LOBBY_CTA_STATE_B = 4660    // production resets around lobby CTA setup
        private const val VARC_BONDS_TRADEABLE = 4968      // tradeable bonds count
        private const val VARC_BONDS_UNTRADEABLE = 4969    // untradeable bonds count
        private const val VARC_NOTIFY_7108 = 7108          // boolean notification flag

        // --- Lobby CS2 script IDs ---
        private const val SCRIPT_TIMER_SETUP = 7486        // sets up countdown timer display on a component
        private const val SCRIPT_LOBBY_NEWS = 10931             // lobby news entry (9 args: slot, sprite, category, -1, title, summary, slug, date, 0)
        private const val SCRIPT_LOBBY_NEWS_END = 10936         // signals end of news entries (same as subif visibility)

        // News categories (from Jagex capture analysis)
        private const val NEWS_CATEGORY_GAME_UPDATE = 1
        private const val NEWS_CATEGORY_PATCH_NOTES = 12

        data class NewsEntry(
            val spriteId: Int,
            val category: Int,
            val title: String,
            val summary: String,
            val slug: String,
            val date: String,
        )

        private val LOBBY_NEWS = listOf(
            NewsEntry(
                spriteId = 19261,
                category = NEWS_CATEGORY_GAME_UPDATE,
                title = "Welcome to Darkan 3",
                summary = "Darkan 3 is a RuneScape 3 private server targeting the NXT client. This is an early development build; expect bugs and missing features!",
                slug = "welcome-to-darkan-3",
                date = "26-Mar-2026",
            ),
            NewsEntry(
                spriteId = 19256,
                category = NEWS_CATEGORY_GAME_UPDATE,
                title = "Lobby System Online",
                summary = "The lobby server is now functional with friends list, world selection, and account persistence via MongoDB.",
                slug = "lobby-system-online",
                date = "26-Mar-2026",
            ),
            NewsEntry(
                spriteId = 19260,
                category = NEWS_CATEGORY_PATCH_NOTES,
                title = "947-1 Protocol Support",
                summary = "Full protocol support for NXT client revision 947-1 including all packet encoders, ISAAC cipher, and JS5 cache serving.",
                slug = "947-1-protocol-support",
                date = "23-Mar-2026",
            ),
        )

        private const val LOBBY_SETEVENTS_INTERFACE = 907
        private val LOBBY_SETEVENTS_COMPONENTS = intArrayOf(39, 75, 46, 101)
        private const val LOBBY_SETEVENTS_SETTINGS = 0x0002  // from capture: last 2 bytes = 02 00 LE = 2

        // Dump file loaders removed — lobby only needs the named varps above.
    }
}

private suspend fun ByteWriteChannel.finishLogin(value: Int) {
    writeByte(value.toByte())
    flushAndClose()
}

private fun String.toProtocolName(): String = lowercase().replace(" ", "_")

private fun ByteReadPacket.readCp1252String(): String {
    var bytes = ByteArray(32)
    var length = 0
    while (remaining > 0) {
        val b = readByte()
        if (b.toInt() == 0) break
        if (length == bytes.size) bytes = bytes.copyOf(bytes.size * 2)
        bytes[length++] = b
    }
    return Cp1252.decode(bytes, 0, length)
}

private val LOGIN_NAME_CHARS = charArrayOf(
    '_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
    'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
)

private fun Long.toBase37Name(): String {
    val result = CharArray(12)
    var value = this
    var length = 0
    while (value != 0L) {
        val remainder = value % 37L
        value /= 37L
        result[11 - length++] = LOGIN_NAME_CHARS[remainder.toInt()]
    }
    return String(result, 12 - length, length)
}
