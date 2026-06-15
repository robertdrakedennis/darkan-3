package org.darkan.world.server

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import io.ktor.utils.io.CancellationException
import io.ktor.utils.io.core.*
import kotlinx.coroutines.*
import kotlinx.io.readByteArray
import kotlinx.io.readUByte
import kotlinx.io.readUShort
import org.darkan.core.EnvVars
import org.darkan.core.Logger.logError
import org.darkan.core.Logger.logInfo
import org.darkan.core.Logger.logTrace
import org.darkan.core.Logger.logWarn
import org.darkan.core.formatForProtocol
import org.darkan.core.net.*
import org.darkan.core.net.login.LoginToken
import org.darkan.core.net.prot.*
import org.darkan.core.net.prot.handler.PacketHandlers
import org.darkan.core.net.session.GameSession
import org.darkan.core.mongo.Accounts
import org.darkan.core.social.gateway.*
import org.darkan.world.entity.Player
import org.darkan.world.net.NpcInfoBuilder
import org.darkan.world.net.PlayerInfoBuilder
import org.darkan.world.social.SocialClient
import org.darkan.world.world.Players
import world.gregs.voidps.buffer.*
import world.gregs.voidps.cache.Cache
import world.gregs.voidps.cache.secure.RSA
import world.gregs.voidps.cache.secure.decryptXtea
import java.math.BigInteger
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

/**
 * Minimal world server that accepts lobby-to-world transfer connections.
 *
 * Handles the NXT world login handshake:
 * 1. Connection type CONNECT_LOGIN (14) already read by the accept loop
 * 2. Sends JS5_SYNC response byte (0)
 * 3. Client sends login opcode (16=RECONNECT or 18=LOGIN)
 * 4. RSA decrypt -> ISAAC keys + LoginToken
 * 5. XTEA decrypt -> username, display mode, screen size, machine info
 * 6. Verifies the LoginToken issued by the lobby
 * 7. Loads account from MongoDB
 * 8. Creates GameSession with ISAAC ciphers
 * 9. Sends WorldLoginDetails (pre-ISAAC)
 * 10. Notifies lobby of player online via SocialClient
 * 11. Enters session loop
 */
object WorldServer {
    private lateinit var job: Job
    private lateinit var dispatcher: ExecutorCoroutineDispatcher
    private lateinit var serverSocket: ServerSocket
    private lateinit var selectorManager: SelectorManager

    lateinit var socialClient: SocialClient
        private set

    private val worldRsaMod = BigInteger(EnvVars.worldRsaModulus)
    private val worldRsaExp = BigInteger(EnvVars.worldRsaExponent)

    private val pendingLogins = ConcurrentHashMap.newKeySet<String>()
    private val playersByUsername = ConcurrentHashMap<String, GameSession>()

    fun start(port: Int): Job {
        val executor = Executors.newCachedThreadPool()
        dispatcher = executor.asCoroutineDispatcher()
        selectorManager = ActorSelectorManager(dispatcher)
        val scope = CoroutineScope(dispatcher)

        socialClient = SocialClient(
            scope,
            onConnected = { resendPresenceSnapshot() },
        ) { msg -> handleSocialMessage(msg) }
        socialClient.start()

        // Start the per-tick PLAYER_INFO / NPC_INFO / zone-bundle dispatcher (B7).
        // Must come AFTER socialClient.start() so that any player tick-driven social
        // packets have a live social client to forward through.
        WorldTick.start()

        logInfo("Starting world server...")
        serverSocket = runBlocking {
            aSocket(selectorManager).tcp().bind("0.0.0.0", port) { reuseAddress = true }
        }
        job = scope.launch {
            try {
                supervisorScope {
                    logInfo("World server started on port $port")
                    while (isActive) {
                        val socket = serverSocket.accept()
                        val ip = socket.remoteAddress.toJavaAddress().toString()
                            .substringAfter("/").substringBefore(":")
                        logTrace("World client connected: $ip")
                        connectClient(socket, ip)
                    }
                }
            } catch (_: CancellationException) {
                logInfo("World server stopping...")
            } catch (e: Exception) {
                logError("Error in World server", e)
            }
        }
        return job
    }

    fun stop() {
        try {
            // Stop the tick driver first so no half-built packets land on closing sockets.
            WorldTick.stop()
            job.cancel()
            dispatcher.close()
            if (::serverSocket.isInitialized) serverSocket.close()
            if (::selectorManager.isInitialized) selectorManager.close()
        } catch (e: Exception) {
            logError("Error stopping World server", e)
        }
    }

    private fun CoroutineScope.connectClient(socket: Socket, ip: String) = launch(
        dispatcher + CoroutineExceptionHandler { _, throwable ->
            logTrace("Error connecting world client: ${throwable.message}")
        }
    ) {
        try {
            val input = socket.openReadChannel()
            val output = socket.openWriteChannel(autoFlush = false)

            try {
                when (val reqOpcode = input.readByte().toInt()) {
                    RequestOpcode.CONNECT_LOGIN -> initWorldLogin(input, output, ip)
                    else -> {
                        logTrace("Invalid request opcode for world: $reqOpcode")
                        output.finish(ResponseOpcode.INVALID_LOGIN_SERVER)
                    }
                }
            } finally {
                socket.close()
            }
        } catch (e: Exception) {
            when {
                e is CancellationException -> {}
                Session.isExpectedDisconnect(e) -> logTrace("World client disconnected: ${e::class.simpleName}")
                else -> logError("Error handling world client connection", e)
            }
        } finally {
            socket.close()
        }
    }

    private suspend fun initWorldLogin(input: ByteReadChannel, output: ByteWriteChannel, ip: String) {
        // Step 1: Send exchange data (JS5_SYNC = 0)
        output.respond(ResponseOpcode.JS5_SYNC)

        // Step 2: Read login opcode
        val opcode = input.readByte().toInt()
        if (opcode != RequestOpcode.LOGIN && opcode != RequestOpcode.RECONNECT) {
            logTrace("Unexpected world login opcode: $opcode from $ip")
            return output.finish(ResponseOpcode.LOGIN_SERVER_REJECTED_SESSION)
        }
        if (pendingLogins.size > 20)
            return output.finish(ResponseOpcode.LOGIN_SERVER_REJECTED_SESSION)

        // Step 3: Read packet
        val size = input.readShort().toInt() and 0xFFFF
        if (size <= 0 || size > 5000) {
            logError("Invalid world login packet size: $size from $ip")
            return output.finish(ResponseOpcode.COULD_NOT_COMPLETE_LOGIN)
        }
        val packetData = ByteArray(size)
        input.readFully(packetData, 0, size)
        val packet = ByteReadPacket(packetData)

        // Step 4: Parse version
        val major = packet.readInt()
        val codec = Codec.get(major)
        if (codec == null) {
            logError("Invalid codec version: $major from $ip")
            return output.finish(ResponseOpcode.GAME_UPDATE)
        }
        val minor = packet.readInt()
        logTrace("World login: version=$major.$minor from $ip")

        val gameStateIsUnk10 = packet.readUByte().toInt() == 1

        // Step 5: RSA decrypt
        val rsaSize = packet.readUShort().toInt()
        if (rsaSize <= 0 || rsaSize > 512) {
            logError("Invalid RSA block size: $rsaSize from $ip")
            return output.finish(ResponseOpcode.COULD_NOT_COMPLETE_LOGIN)
        }
        val sensitiveData = ByteReadPacket(RSA.crypt(packet.readByteArray(rsaSize), worldRsaMod, worldRsaExp))

        if (sensitiveData.readUByte().toInt() != 10) {
            logError("RSA magic mismatch from $ip (bad RSA key?)")
            return output.finish(ResponseOpcode.BAD_SESSION_ID)
        }

        val isaacKeys = IntArray(4) { sensitiveData.readInt() }

        if (sensitiveData.readLong().toInt() != 0) {
            logTrace("RSA session check non-zero from $ip")
            return output.finish(ResponseOpcode.BAD_SESSION_ID)
        }

        // The password/token slot contains the HMAC-signed LoginToken from the lobby
        val lobbyAuthToken = sensitiveData.readRSString()
        sensitiveData.readLong() // padding
        sensitiveData.readLong() // padding

        // Step 6: XTEA decrypt
        val xtea = packet.decryptXtea(isaacKeys)

        val stringUsername = xtea.readBoolean()
        val username = (if (stringUsername) xtea.readRSString() else xtea.readLong().toRSString())
            .formatForProtocol()

        val displayMode = xtea.readUByte().toInt()
        val screenWidth = xtea.readUShort().toInt()
        val screenHeight = xtea.readUShort().toInt()
        xtea.readUByte() // unknown2
        xtea.readByteArray(24) // randomDat
        val settings = xtea.readRSString()
        val affid = xtea.readInt()

        val prefSize = xtea.readUByte().toInt()
        for (i in 0 until prefSize) xtea.readUByte() // prefs

        // Machine info (skip — we don't need it for now)
        val success = xtea.readUByte().toInt()
        if (success != 6)
            logTrace("Machine info parse flag != 6 for $username (success=$success)")
        // Skip remaining machine info bytes
        // In a full implementation we would parse MachineInformation here

        // We need to consume the remaining known fields after machine info
        // but for now just skip to CRC validation. The reference parses:
        // machineInfo, varVerifyId, machineUid, static loginToken, supplementaryLoginData,
        // jagtheora, isApplet, signedTerms, loginType, sessionToken, js5AuthToken, unknown, worldId
        // For a minimal server we skip the rest of the XTEA block.

        logInfo("World login from: $username (display=$displayMode, screen=${screenWidth}x${screenHeight}) from $ip")

        // Step 7: Rate limit
        if (!pendingLogins.add(username))
            return output.finish(ResponseOpcode.LOGIN_LIMIT_EXCEEDED)

        try {
            // Step 8: Verify LoginToken
            // TODO: Lobby needs to issue a real LoginToken and embed it in lobby data.
            // For now, skip verification — the XTEA-encrypted username is trusted.
            val verified = LoginToken.verify(lobbyAuthToken, System.currentTimeMillis(), EnvVars.worldLoginTokenSecret)
            if (verified != null && verified.username != username) {
                logError("LoginToken username mismatch: token=${verified.username} login=$username")
                return output.finish(ResponseOpcode.INVALID_CREDENTIALS)
            }
            if (verified == null) {
                logWarn("LoginToken verify failed for $username (token not issued by lobby yet — allowing anyway)")
            }

            // Step 9: Load account
            val account = try {
                Accounts.findByUsername(username)
            } catch (e: Exception) {
                logError("Error loading account for $username", e)
                return output.finish(ResponseOpcode.LOGIN_SERVER_OFFLINE)
            }
            if (account == null) {
                logError("Account not found for $username")
                return output.finish(ResponseOpcode.INVALID_CREDENTIALS)
            }
            if (account.isBanned()) {
                return output.finish(ResponseOpcode.ACCOUNT_DISABLED)
            }
            if (playersByUsername.containsKey(username)) {
                return output.finish(ResponseOpcode.ACCOUNT_ONLINE)
            }

            // Step 10: ISAAC cipher setup
            val inCipher = Isaac(isaacKeys.copyOf())
            val outKeys = isaacKeys.copyOf()
            for (i in outKeys.indices) outKeys[i] += EnvVars.ISAAC_DELTA
            val outCipher = Isaac(outKeys)

            // Step 11: Send login success response (pre-ISAAC)
            output.writeByte(ResponseOpcode.SUCCESS)

            val session = GameSession(output, inCipher, outCipher, ip, codec, username = account.username)

            // Step 12: Allocate world-side Player and register in the global pool.
            // Player.index is mutable so we can construct first (with a placeholder index of
            // 0) then have Players.allocate write back the assigned slot id atomically. The
            // slot id MUST match WorldLoginDetails.playerIndex (sent next) AND the PLAYER_INFO
            // 30-bit local-tile prefix that goes out on the first tick.
            //
            // Note: the Player constructor eagerly builds [Viewport] with `owner.index = 0`
            // in `highResIndices[0]` (the local-player slot). We patch that entry below to
            // match the allocated slot — otherwise the high-res cohort would point at slot
            // 0, which is the protocol "no player" sentinel.
            val player = Player(index = 0, account = account, session = session)
            val playerIndex = Players.allocate(player) { idx -> player.index = idx }
            // From this point the slot MUST be released on any exit path — an exception
            // during init (e.g. a failed flush) would otherwise leak one of the 2048 slots
            // permanently. The single try/finally below guarantees it.
            try {
                // Realign the viewport's high-res-indices[0] with the assigned slot id. This
                // SHOULD be done by Viewport but it captures owner.index at construction time;
                // doing it here avoids a refactor of Viewport's init order.
                player.viewport.highResIndices[0] = playerIndex

                // Step 13: Send WorldLoginDetails (pre-ISAAC, via noIsaac=true)
                session.send(
                    WorldLoginDetails(
                        rights = if (EnvVars.debug) 2 else account.rights,
                        modLevel = 0,
                        quickChat = false,
                        verifiedEmail = false,
                        aBool7322 = false,
                        quickChatOnly = false,
                        playerIndex = playerIndex,
                        members = EnvVars.worldMembers,
                        dob = 0,
                        memberWorld = EnvVars.worldMembers,
                        worldName = EnvVars.worldName,
                    ),
                    noIsaac = true,
                )
                session.flush()

                // Step 14: Send world init packets
                sendWorldInitPackets(session, account)

                // Step 15: Send initial PLAYER_INFO / NPC_INFO so the client renders the
                // local avatar before the tick loop's first per-tick build lands. After this
                // the WorldTick loop will drive subsequent updates at 600ms cadence.
                session.send(PlayerInfoBuilder.buildInit(player))
                session.send(NpcInfoBuilder.buildInit(player))
                session.flush()

                // Step 16: Register player and notify lobby
                playersByUsername[username] = session

                CoroutineScope(dispatcher).launch {
                    try {
                        socialClient.sendPlayerOnline(
                            username = account.username,
                            displayName = account.displayName,
                            rightsCrown = account.rights,
                            privateStatus = account.social.status,
                        )
                    } catch (e: Exception) {
                        logError("Failed to notify lobby of player online: ${account.username}", e)
                    }
                }

                // Step 17: Session loop
                coroutineScope {
                    launch { session.readPackets(input) }
                    worldSessionLoop(session)
                }
            } finally {
                // Release the player slot so subsequent logins can reuse it; do this BEFORE
                // notifying the lobby so a fast reconnect won't observe a stale slot still
                // claimed.
                Players.release(playerIndex)
                if (playersByUsername.remove(username, session)) {
                    CoroutineScope(dispatcher).launch {
                        try {
                            socialClient.sendPlayerOffline(username)
                        } catch (e: Exception) {
                            logError("Failed to notify lobby of player offline: $username", e)
                        }
                    }
                }
            }
        } finally {
            pendingLogins.remove(username)
        }
    }

    /**
     * Send world init packets after login. Based on docs/net/account-creation-sequence.md Phase 1.
     *
     * Routes to either character creation (interface 1349) or the game HUD (1477) based
     * on whether the account has completed character creation. We intentionally do NOT
     * send RUNCLIENTSCRIPT(1246) — the display name prompt — because display names are
     * managed via the web, not in-client.
     */
    private suspend fun sendWorldInitPackets(session: GameSession, account: org.darkan.core.model.Account) {
        sendWorldLoginCore(session)

        if (!account.characterCreated) {
            logInfo("Opening character creation UI for ${session.ip} (${account.displayName})")
            sendCharacterCreationUI(session)
        } else {
            logInfo("Opening game HUD for ${session.ip} (${account.displayName})")
            // Per A1 §1.1: IF_OPENTOP is opcode 68 (6B) — the wire format is
            // (topLevelId BE u32, subId LE u16). For the main game HUD root we open
            // sub-component 0 (the root frame inside interface 1477).
            session.send(IfOpenTop(topLevelId = GAME_HUD_INTERFACE, subId = 0))
        }

        session.flush()
    }

    /** Core world login packets common to both character creation and game HUD paths. */
    private suspend fun sendWorldLoginCore(session: GameSession) {
        // 1. Session token (HASHED_WORLD_TOKEN) — no RE'd 948 opcode yet (948 op 6 is
        // LOC_PREFETCH); sendIfSupported skips it with a single INFO log instead of
        // per-login warn spam until the 948 destination is documented.
        val tokenBytes = ByteArray(32).also { java.security.SecureRandom().nextBytes(it) }
        val token = java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes)
        session.sendIfSupported(HashedWorldToken(token))

        // 2. Reset client varcache
        session.send(ResetClientVarcache())

        // 3. Stats (29 skills, all level 1 except Hitpoints=10 with 1154 xp)
        for (i in 0..28) {
            if (i == 3) session.send(UpdateStat(i, 1154, 10))
            else session.send(UpdateStat(i, 0, 1))
        }

        // 4. Map build — place player at Lumbridge (tile 3200, 3200 = chunk 400, 400)
        //
        // Per A2 §3 / `docs/net/serverprot/rebuild-947-3.md`, REBUILD_NORMAL is opcode 90
        // (varShort) and its simple form has the byte layout:
        //   chunkX BE u16, forceRefresh u8, regionLow LE u16, magic 0x7B, chunkZ BE u16,
        //   packedCoordA BE u32, packedCoordB BE u32
        // where packedCoord = (plane << 28) | (y << 14) | x.
        //
        // MVP: regionLow defaults to 0 (no config-provider world area), and we pack
        // packedCoordA/B with the player's tile so the BuildArea scene cache lookup
        // resolves to the same chunk we encoded into the bit-packed PLAYER_INFO position.
        val chunkX = 400
        val chunkZ = 400
        val playerTileX = chunkX * 8 + 4    // tile centre within the chunk
        val playerTileY = chunkZ * 8 + 4
        val packedCoord = (0 shl 28) or ((playerTileY and 0x3FFF) shl 14) or (playerTileX and 0x3FFF)
        session.send(
            RebuildNormalSimple(
                chunkX = chunkX,
                chunkZ = chunkZ,
                forceRefresh = true,
                regionLow = 0,
                packedCoordA = packedCoord,
                packedCoordB = packedCoord,
            )
        )

        // 5. RuneCoin balance display
        session.send(JcoinsUpdate(0))

        // 6. Player right-click options (standard 4: Follow, Trade, Req Assist, Examine)
        session.send(SetPlayerOp(3, "Follow"))
        session.send(SetPlayerOp(4, "Trade with"))
        session.send(SetPlayerOp(6, "Req Assist"))
        session.send(SetPlayerOp(8, "Examine"))

        // 7. Empty ignore list — 948 op 130 encoder is intentionally disabled (wire format
        // unconfirmed, see Rev948ServerCodecsSocial); skip with a one-time INFO log.
        session.sendIfSupported(UpdateIgnoreList(emptyList()))
    }

    /**
     * Open the character creation UI (interface 1349).
     *
     * Per docs/net/account-creation-sequence.md Phase 2, the full sequence involves ~40
     * IF_SETPOSITION calls and ~800 IF_SETEVENTS2 calls to wire up all the sub-interfaces
     * and enable click events on every option. This is a minimal first-pass — we send just
     * the setup scripts and the top-level interface to see what the client renders.
     *
     * We explicitly do NOT send RUNCLIENTSCRIPT(1246) — that would prompt the client to
     * enter a display name, but display names are managed via the web.
     */
    private suspend fun sendCharacterCreationUI(session: GameSession) {
        // Pre-interface setup scripts (exact args from capture)
        session.send(RunClientScript.of(16300, 0, 0, 0, 0))
        session.send(RunClientScript.of(671, 0, 0, 0, 0))
        session.send(RunClientScript.of(20611))

        // Open the character creation root interface per A1 §1.1 (op 68, 6B).
        session.send(IfOpenTop(topLevelId = CHARACTER_CREATION_INTERFACE, subId = 0))

        // TODO: IF_SETPOSITION ×40 + IF_SETEVENTS2 ×800 for sub-interfaces (see account-creation-sequence.md)
    }

    /**
     * Dispatch loop for the world session.
     * Reads decoded packets from the session's read channel and dispatches to handlers.
     * Sends periodic keepalives.
     */
    private suspend fun worldSessionLoop(session: GameSession) {
        var packetCount = 0L
        var lastKeepaliveSent = System.currentTimeMillis()
        var rateWindowStart = lastKeepaliveSent
        var rateWindowCount = 0

        try {
            // Send initial keepalive
            session.send(NoTimeout())
            session.flush()
            logTrace("Sent initial NO_TIMEOUT to ${session.ip}")

            while (!session.disconnected) {
                val packet = kotlinx.coroutines.withTimeoutOrNull(KEEPALIVE_INTERVAL_MS) {
                    session.readChannel.receive()
                }

                if (packet != null) {
                    packetCount++
                    rateWindowCount++
                    PacketHandlers.handle<GameSession>(session, packet)
                }

                // Flush any queued responses
                session.flush()

                // Send periodic keepalives — flushed immediately so they aren't delayed
                // until the next loop iteration.
                val now = System.currentTimeMillis()
                if (now - lastKeepaliveSent > KEEPALIVE_INTERVAL_MS) {
                    session.send(NoTimeout())
                    session.flush()
                    lastKeepaliveSent = now
                }

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
                logTrace("World session ended for ${session.ip}: ${e::class.simpleName}: ${e.message}")
            } else {
                logError("World session error for ${session.ip} (${session.username})", e)
            }
        }
        logInfo("World session closed for ${session.ip} (${session.username}) after $packetCount packets")
    }

    // --- Social message handling ---

    /**
     * Replays PlayerOnline for every logged-in player after the gateway connection is
     * (re)established. Messages sent while the gateway was down are dropped, so without
     * this a lobby restart would leave presence permanently desynced.
     */
    private suspend fun resendPresenceSnapshot() {
        val snapshot = ArrayList<Player>()
        Players.forEach { snapshot.add(it) }
        for (player in snapshot) {
            try {
                socialClient.sendPlayerOnline(
                    username = player.account.username,
                    displayName = player.account.displayName,
                    rightsCrown = player.account.rights,
                    privateStatus = player.account.social.status,
                )
            } catch (e: Exception) {
                logError("Failed to resend presence for ${player.account.username}", e)
            }
        }
        if (snapshot.isNotEmpty()) {
            logInfo("Resent presence snapshot for ${snapshot.size} player(s) to the social gateway")
        }
    }

    private fun handleSocialMessage(msg: SocialGatewayWireMessage) {
        when (msg) {
            is ServerPacketEnvelope -> {
                logTrace("handleSocialMessage: ServerPacketEnvelope username=${msg.username} packet=${msg.packet::class.simpleName}")
                deliverToPlayer(msg.username, msg.packet)
            }
            is ServerPacketBatch -> {
                logTrace("handleSocialMessage: ServerPacketBatch usernames=${msg.usernames} packet=${msg.packet::class.simpleName}")
                msg.usernames.forEach { deliverToPlayer(it, msg.packet) }
            }
            is AnnouncementBroadcast -> logInfo("Received announcement: ${msg.message}")
            else -> logTrace("Ignoring gateway message: ${msg::class.simpleName}")
        }
    }

    private fun deliverToPlayer(username: String, packet: ServerProt) {
        val session = playersByUsername[username]
        if (session == null) {
            logTrace("deliverToPlayer: player not found username=$username packet=${packet::class.simpleName}")
            return
        }
        if (session.disconnected) {
            logTrace("deliverToPlayer: player disconnected username=$username packet=${packet::class.simpleName}")
            return
        }
        session.queuePacket(packet)
    }

    // --- Social forwarding ---

    suspend fun forwardToLobby(username: String, packet: ClientProt) {
        socialClient.sendClientPacket(username, packet)
    }

    private const val KEEPALIVE_INTERVAL_MS = 15_000L

    /** Rolling window for the inbound packet-rate check. */
    private const val PACKET_RATE_WINDOW_MS = 10_000L

    /**
     * Max client packets per [PACKET_RATE_WINDOW_MS] window. A healthy client sends ~1
     * keepalive/s plus input events — 2000/10s (200/s sustained) is far above legitimate
     * traffic while still catching floods quickly.
     */
    private const val MAX_PACKETS_PER_RATE_WINDOW = 2_000

    /** Top-level interface ID for the character creation / gamemode selection screen. */
    private const val CHARACTER_CREATION_INTERFACE = 1349

    /** Top-level interface ID for the main in-game HUD. */
    private const val GAME_HUD_INTERFACE = 1477
}
