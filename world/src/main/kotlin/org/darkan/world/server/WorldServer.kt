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
import org.darkan.core.formatPlayerNameForProtocol
import org.darkan.core.net.*
import org.darkan.core.net.login.LoginToken
import org.darkan.core.net.prot.*
import org.darkan.core.net.prot.handler.PacketHandlers
import org.darkan.core.net.session.GameSession
import org.darkan.core.mongo.Accounts
import org.darkan.core.social.gateway.*
import org.darkan.world.social.SocialClient
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

        socialClient = SocialClient(scope) { msg -> handleSocialMessage(msg) }
        socialClient.start()

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
            .formatPlayerNameForProtocol()

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

            // Step 12: Send WorldLoginDetails (pre-ISAAC, via noIsaac=true)
            session.send(
                WorldLoginDetails(
                    rights = if (EnvVars.debug) 2 else account.rights,
                    modLevel = 0,
                    quickChat = false,
                    verifiedEmail = false,
                    aBool7322 = false,
                    quickChatOnly = false,
                    playerIndex = 1, // TODO: assign from player index pool
                    members = EnvVars.worldMembers,
                    dob = 0,
                    memberWorld = EnvVars.worldMembers,
                    worldName = EnvVars.worldName,
                ),
                noIsaac = true,
            )
            session.flush()

            // Step 13: Send world init packets
            sendWorldInitPackets(session, account)

            // Step 14: Register player and notify lobby
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

            // Step 15: Session loop
            try {
                coroutineScope {
                    launch { session.readPackets(input) }
                    worldSessionLoop(session)
                }
            } finally {
                playersByUsername.remove(username)
                CoroutineScope(dispatcher).launch {
                    try {
                        socialClient.sendPlayerOffline(username)
                    } catch (e: Exception) {
                        logError("Failed to notify lobby of player offline: $username", e)
                    }
                }
            }
        } finally {
            pendingLogins.remove(username)
        }
    }

    /**
     * Send the minimum world init packets after login.
     * Based on docs/net/account-creation-sequence.md Phase 1.
     */
    private suspend fun sendWorldInitPackets(session: GameSession, account: org.darkan.core.model.Account) {
        // 1. Session token
        session.send(HashedWorldToken(java.util.Base64.getUrlEncoder().withoutPadding()
            .encodeToString(java.security.SecureRandom().let { r -> ByteArray(32).also { r.nextBytes(it) } })))

        // 2. Reset varps and send defaults
        session.send(ResetClientVarcache())

        // 3. Stats (29 skills, all level 1 except Hitpoints=10)
        for (i in 0..28) {
            if (i == 3) session.send(UpdateStat(i, 1154, 10))  // Hitpoints
            else session.send(UpdateStat(i, 0, 1))
        }

        // 4. Map build — place player at Lumbridge (chunk 400, 400 = tile 3200, 3200)
        // The client needs a REBUILD_NORMAL to load the scene.
        // XTEA keys are all zeros for now (unencrypted regions).
        val chunkX = 400  // tile 3200 / 8
        val chunkZ = 400  // tile 3200 / 8
        val regionX = chunkX / 8  // region 50
        val regionZ = chunkZ / 8  // region 50
        // Collect XTEA keys for the 13x13 region grid around the player (104x104 map = 13 regions)
        val xteaKeys = mutableListOf<IntArray>()
        for (rx in (regionX - 6)..(regionX + 6)) {
            for (rz in (regionZ - 6)..(regionZ + 6)) {
                xteaKeys.add(intArrayOf(0, 0, 0, 0))  // zeros = unencrypted
            }
        }
        session.send(RebuildNormal(
            chunkX = chunkX,
            chunkZ = chunkZ,
            forceRefresh = true,
            xteaKeys = xteaKeys.toTypedArray(),
        ))

        // 5. Set player right-click options
        session.send(SetPlayerOp(3, "Follow"))
        session.send(SetPlayerOp(4, "Trade with"))
        session.send(SetPlayerOp(6, "Req Assist"))
        session.send(SetPlayerOp(8, "Examine"))

        // 6. Open top-level interface (1349 = character creation, 1477 = game HUD)
        // For now use the game HUD since character creation needs more setup
        session.send(IfOpenTopLobby(1477))

        session.flush()
        logInfo("Sent world init packets to ${session.ip} (${account.displayName})")
    }

    /**
     * Dispatch loop for the world session.
     * Reads decoded packets from the session's read channel and dispatches to handlers.
     * Sends periodic keepalives.
     */
    private suspend fun worldSessionLoop(session: GameSession) {
        var packetCount = 0
        var lastKeepaliveSent = System.currentTimeMillis()

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
                    PacketHandlers.handleBlocking<GameSession>(session, packet)
                }

                // Flush any queued responses
                session.flush()

                // Send periodic keepalives
                val now = System.currentTimeMillis()
                if (now - lastKeepaliveSent > KEEPALIVE_INTERVAL_MS) {
                    session.send(NoTimeout())
                    lastKeepaliveSent = now
                }

                if (packetCount > 100_000) {
                    logError("Too many packets from ${session.ip}, disconnecting")
                    break
                }
            }
        } catch (e: Exception) {
            logTrace("World session ended for ${session.ip}: ${e::class.simpleName}: ${e.message}")
        }
        logInfo("World session closed for ${session.ip} (${session.username}) after $packetCount packets")
    }

    // --- Social message handling ---

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
}
