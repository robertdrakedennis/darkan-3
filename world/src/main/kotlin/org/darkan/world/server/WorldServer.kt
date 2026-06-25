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
import org.darkan.core.net.login.RsaCredentialTailParser
import org.darkan.core.net.prot.*
import org.darkan.core.net.prot.handler.PacketHandlers
import org.darkan.core.net.session.GameSession
import org.darkan.core.security.PasswordHash
import org.darkan.core.model.Account
import org.darkan.core.mongo.Accounts
import org.darkan.core.social.gateway.*
import org.darkan.world.net.GameHud
import org.darkan.world.net.NpcInfoBuilder
import org.darkan.world.net.Op81GpiPrefix
import org.darkan.world.net.PlayerInfoBuilder
import org.darkan.world.net.SceneMapCacheDiagnostics
import org.darkan.world.net.SceneMapRegionPlanner
import org.darkan.world.net.ZoneStreamer
import org.darkan.world.entity.Player
import org.darkan.world.entity.Rev948FirstLightVarpDefaults
import org.darkan.world.social.SocialClient
import org.darkan.world.world.Players
import world.gregs.voidps.buffer.*
import world.gregs.voidps.buffer.write.BufferWriter
import world.gregs.voidps.cache.Cache
import world.gregs.voidps.cache.secure.RSA
import world.gregs.voidps.cache.secure.decryptXtea
import java.math.BigInteger
import java.security.SecureRandom
import java.util.Base64
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
    private val loginRandom = SecureRandom()

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
        // Step 1: Send the 9-byte first response: status byte (JS5_SYNC = 0 = OK) + 8-byte server
        // seed. The client re-runs the SAME login state machine for the world hop as for the lobby
        // (LoginStepWaitingFirstResponse expects 1 status + 8 seed = 9 bytes — boot/02 step 0x1e),
        // so the world MUST emit the full 9 bytes exactly like LoginServer.handleLogin does. A bare
        // 1-byte response leaves the client blocked reading the missing 8 seed bytes — the silent
        // world-login stall flagged in docs/protocol/lobby-world-switch-948.md §2.
        val serverSeed = ByteArray(8).also { loginRandom.nextBytes(it) }
        output.writeByte(ResponseOpcode.JS5_SYNC) // 0 = OK
        output.writeFully(serverSeed)
        output.flush()

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
        logTrace("World ISAAC keys: ${isaacKeys.joinToString(", ") { "0x${"%08x".format(it)}" }} from $ip")

        // 8-byte session/seed value the client echoes back here. For the world hop the real NXT
        // client echoes the seed it stored from the world's INIT_GAME_CONNECTION first-response
        // (LoginManager+0x40), so this is LEGITIMATELY NON-ZERO — rejecting on `!= 0` kicked the
        // real client straight back to login (the 03:27:59 "RSA session check non-zero" abort).
        // Mirror the lobby login (LoginServer.handleLogin), which reads + logs this value and
        // never rejects on it: read it to keep the byte cursor aligned for the subsequent
        // lobbyAuthToken/padding reads, but do not validate it.
        // FUTURE HARDENING: echo-verify this against the seed the world sent in its INIT reply.
        val sessionCheck = sensitiveData.readLong()
        logTrace("RSA session check: $sessionCheck from $ip")

        val credentialTail = if (sensitiveData.remaining > 0) {
            RsaCredentialTailParser.parse(sensitiveData.readByteArray(sensitiveData.remaining.toInt()))
        } else {
            RsaCredentialTailParser.parse(ByteArray(0))
        }
        logTrace("World RSA credential type: ${credentialTail.credentialType ?: "legacy"} from $ip")

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

            // Step 8: Verify the world login proof. Launcher/session-token paths carry a compact
            // lobby token; direct username/password login carries the password again in the same
            // credential branch.
            val nowMs = System.currentTimeMillis()
            val verifiedString = LoginToken.verify(credentialTail.tokenString, nowMs, EnvVars.worldLoginTokenSecret)
            if (verifiedString != null && verifiedString.username != username) {
                logError("LoginToken username mismatch: token=${verifiedString.username} login=$username")
                return output.finish(ResponseOpcode.INVALID_CREDENTIALS)
            }
            val compactToken = LoginToken.Compact(credentialTail.sessionNonce1, credentialTail.sessionNonce2)
            val verifiedCompact = LoginToken.verifyCompact(username, compactToken, nowMs, EnvVars.worldLoginTokenSecret)
            val verifiedPassword = credentialTail.password.isNotEmpty() &&
                PasswordHash.verifySuspend(credentialTail.password, account.passwordHash)
            logTrace("World auth proof for $username: tokenString=${verifiedString != null} compact=$verifiedCompact password=$verifiedPassword")
            if (verifiedString == null && !verifiedCompact && !verifiedPassword) {
                logWarn("World auth proof verify failed for $username")
                return output.finish(ResponseOpcode.INVALID_CREDENTIALS)
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
                player.viewport.resetAfterGpiPrefix(playerIndex)
                // Step 13: Send the world-login RESPONSE (pre-ISAAC, raw — NO opcode/length framing).
                //
                // CRITICAL (docs/protocol/lobby-world-switch-948.md §9): in WORLD mode the client's
                // LoginStepDealWithFirstResponse routes result byte 2 to step 0xfa (server-client-var
                // block), NOT to HandleLoginData like the lobby. So after the SUCCESS byte the world
                // client parses a fixed THREE-PART pre-ISAAC stream and `WorldLoginDetails` is the LAST
                // part, not the first:
                //   Part A — server-client-var block: [u16 BE len][u8 ackFlag][ {u16 BE varcId,value}… ]
                //   Part B — players byte: 0x02
                //   Part C — login-data block: [u8 len][u8 leadFlag=0][WorldLoginDetails body][u16 reserved=0]
                //            [u32 serverTime][u64 sid1][u64 sid2]
                //
                // The OLD code sent only Part C, mis-framed as the op-2 smart-opcode/VarByte packet
                // (`02 14 <20-byte body>`). The client read `[02][14]` as a 2-byte BE varc length (=532),
                // swallowed the body + ~514 bytes of the following ISAAC burst, walked it as varc ids,
                // hit 0x6803 → GetVarcType NULL → SIGSEGV @ +0x40 in LoginStepWaitingServerClientVar.
                //
                // FIX (§9.6 Option 1): write Parts A+B+C as one raw pre-ISAAC blob.
                //  - Part A is generated by ServerClientVarBlock. First light uses an empty
                //    continue block until cache-backed varc values are known.
                //  - Part B = 02: players byte; step 0x82 requires ==2 (§9.5).
                //  - Part C: 1-byte length prefix (= bytes after it), leadFlag 0x00 (so the client's
                //    field-0 sub-reader FUN_0017a620 — which would pull 4 ISAAC-keystream bytes that
                //    don't exist yet — is skipped), the EXISTING WorldLoginDetails body fields 1–11
                //    (unchanged — they ARE the correct world HandleLoginData body, §9.4), then the 4
                //    trailing fields the client unconditionally reads (§9.4 fields 12–15): u16 reserved,
                //    u32 serverTime, u64 sid1, u64 sid2.
                //
                // serverTime = server epoch seconds; sid1/sid2 = 0 (§9.5/§9.6: "0 ok for first light").
                // worldName is the only variable-length field, so loginDataLen is computed, not constant.
                val details = WorldLoginDetails(
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
                )
                writeWorldLoginResponse(output, details)
                output.flush()

                // Step 14: Send the world-init burst (op81 scene build + UI ops + op5 + varp
                // baseline). The spawn tile (player.tile, default Lumbridge 3200,3200 = zone
                // 400,400) is the SINGLE source of truth: it drives the op81 coord-header centre
                // zone here AND the op22 GPI local 30-bit tile below (PlayerInfoBuilder reads
                // player.tile). They are now coherent — the 400-vs-404 split + captured 404/404
                // prefix that quit the client (docs/protocol/world-bootstrap-948.md §4) is gone.
                sendWorldInitPackets(session, player)

                session.send(DestroyZoneData())
                session.send(SetNpcOp())
                session.send(PlayerInfoBuilder.buildWorldEntrySync(player))
                session.send(CamUpdate.firstLight())
                session.send(UpdateIgnoreListRaw())

                // Step 15-zones: stream the op78 scene (13×13 zones × planes 0–3) that POPULATES the
                // client's scene graph and so advances the scene-build phase (ClientSceneManager+0x1c,
                // client+0x19578) — the foundational world-entry render gate. Without it the graph
                // stays empty, the phase never leaves 0 ("Running Auto Configuration"), and the client
                // black-screens while emitting zero C2S (latest prod streams 606 op78 before HUD).
                // MUST be
                // after op55 DestroyZoneData (zone reset) and before the op3 HUD commit + op75 (below).
                ZoneStreamer.streamScene(session, player.viewport)

                // buildWorldEntrySync clears firstTick and marks appearance delivered.
                sendInitialInventories(session)

                // Step 15b: THE IN-GAME TRANSITION (§8 task #1+#2) — swap the client's top-level
                // interface from the lobby/worldlist UI to the in-game HUD, then build the HUD, then
                // (Step 15c) flip the render-ready gate LAST. Without this the client stays on the
                // lobby interface (polls op54 worldlist-fetch) and never commits to in-game.
                sendInGameHud(session)

                session.send(SceneFlag(0))
                sendFirstLightTail(session, player)
                session.send(UpdateRunenergy(1))
                session.send(SetReadyFlag())
                session.flush()

                // Step 16: Register player and notify lobby
                player.readyForTick = true
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
     * Writes the full world-login RESPONSE (Parts A + B + C) as a single raw pre-ISAAC blob.
     *
     * This is the §9.6 fix for the world-login segfault. See the call site (Step 13) for the why.
     * Layout (docs/protocol/lobby-world-switch-948.md §9.5), all big-endian, NO opcode/length framing
     * other than the explicit length prefixes below:
     *
     *   PART A (server-client-var block — steps 0xfa→0x104→0x10e):
     *     u16  varcBlockLen            (BE; length of the part-A body that follows)
     *     u8   ackFlag      = 0x01     (==1 advances to Part B; len==1 ⇒ step 0x10e loop guard `1<1` false)
     *   PART B (players byte — step 0x82):
     *     u8   playersByte  = 0x02     (==2 advances to Part C)
     *   PART C (login-data block — steps 0x8c→0x96, = darkan's WorldLoginDetails):
     *     u8   loginDataLen            (1-byte count of every byte AFTER it: leadFlag … sessionId2)
     *     u8   leadFlag     = 0x00     (field 0; 0 ⇒ skip FUN_0017a620's 4-byte ISAAC-keystream peek)
     *     ... WorldLoginDetails body fields 1–11 (identical transforms to the op-2 encoder) ...
     *     u16  reserved16   = 0        (BE; client discards — §9.4 field 12)
     *     u32  serverTime              (BE; server epoch seconds — §9.4 field 13)
     *     u64  sessionId1   = 0        (BE — §9.4 field 14; 0 ok for first light)
     *     u64  sessionId2   = 0        (BE — §9.4 field 15; 0 ok for first light)
     *
     * loginDataLen is computed (not constant) because worldName is variable-length. For
     * worldName="Darkan" it is 43 (≤255, so the 1-byte prefix is sufficient). If a future worldName
     * pushes the body past 255 the client's 1-byte length step (0x8c) cannot represent it — the
     * length is asserted below so that surfaces loudly rather than silently truncating.
     */
    private suspend fun writeWorldLoginResponse(output: ByteWriteChannel, d: WorldLoginDetails) {
        // Part C body: leadFlag(0) + fields 1–11 + 4 trailing fields. Built first to measure its length.
        // Fields 1–11 mirror the op-2 encoder in Rev948ServerCodecsMisc byte-for-byte.
        val body = BufferWriter(96).apply {
            writeByte(0)                  // field 0: leadFlag = 0  (skip the gated ISAAC-prefix reader)
            writeByte(d.rights)           // field 1
            writeByte(d.modLevel)         // field 2
            writeByte(d.quickChat)        // field 3  (Boolean overload → 0/1)
            writeByte(d.verifiedEmail)    // field 4
            writeByte(d.aBool7322)        // field 5
            writeByte(d.quickChatOnly)    // field 6
            writeShort(d.playerIndex)     // field 7  (BE u16 — MUST equal the GPI slot)
            writeByte(d.members)          // field 8
            writeMedium(d.dob)            // field 9  (BE s24)
            writeByte(d.memberWorld)      // field 10
            writeString(d.worldName)      // field 11 (CP1252 + NUL — same bytes as ByteWriteChannel.writeRSString)
            // Trailing fields the client reads unconditionally (§9.4 fields 12–15):
            writeShort(0)                                              // field 12: reserved16 (discarded)
            writeInt((System.currentTimeMillis() / 1000).toInt())      // field 13: serverTime (epoch seconds)
            writeLong(0L)                                              // field 14: sessionId1
            writeLong(0L)                                              // field 15: sessionId2
        }.toArray()

        require(body.size <= 255) {
            "World login-data body is ${body.size} bytes (>255) — the client's step 0x8c reads a " +
                "1-byte length and cannot frame this. Shorten worldName ('${d.worldName}')."
        }

        val serverClientVarBlock = ServerClientVarBaseline.firstLight().encode()
        val response = BufferWriter(body.size + serverClientVarBlock.size + 4).apply {
            // Part A — server-client-var block
            writeShort(serverClientVarBlock.size)
            writeBytes(serverClientVarBlock)
            // Part B — players byte
            writeByte(0x02)               // playersByte = 2
            // Part C — login-data block
            writeByte(body.size)          // loginDataLen (1 byte)
            writeBytes(body)
        }.toArray()

        output.writeFully(response)
    }

    private suspend fun sendWorldInitPackets(session: GameSession, player: Player) {
        sendWorldLoginCore(session, player)
        session.flush()
    }

    private suspend fun sendInGameHud(session: GameSession) {
        GameHud.open(session, rootInterface = GAME_HUD_INTERFACE)
    }

    /** Core world login packets before the 1477 HUD transition. */
    private suspend fun sendWorldLoginCore(session: GameSession, player: Player) {
        // op81 REBUILD_NORMAL_SIMPLE — **Shape B**: body = [5119-byte GPI prefix][18-byte coord
        // header] (docs/protocol/world-bootstrap-948.md §"⚠️ CORRECTION (2026-06-23)").
        //
        // On world entry the client sets worldState+0x49=1, so op81's handler UNCONDITIONALLY runs
        // the gBit-based GPI-prefix parser (NO bounds check) BEFORE reading the coord header. The
        // parser consumes 30 + 2046×20 = 40950 bits = 5119 bytes, then the handler reads the header
        // at the advanced cursor (magic 0x85 at body offset 5122). Shipping the bare 18-byte header
        // (Shape A) makes the parser over-read 5101 bytes of heap, place the player at a garbage
        // tile, and read the header out-of-bounds → magic ≠ 0x85 → op81 ABORTS before the BuildArea
        // alloc and before ProcessCameraReset → black screen. So the prefix is mandatory; we
        // generate it from local state via [Op81GpiPrefix].
        //
        // The spawn tile is the SINGLE source of truth for all three coordinate facets that MUST
        // agree (§4 coherence): (a) the op81 centre zone (header +4 X / +1,+2 Z), (b) the op81
        // build-area corners (packedCoordA/B), and (c) the GPI prefix's local 30-bit tile — all
        // derived from `player.tile` right here. The build area is computed by the [BuildArea]
        // service per docs/protocol/packed-coord-buildarea-948.md: packedCoordA = SW corner,
        // packedCoordB = NE corner, both as TILE coords the client `>>6`s to REGIONS. The grid is
        // a non-inverted window CONTAINING the spawn region, so the client allocates a real grid
        // and JS5-pulls the index-5 map groups (this is the definitive black-screen fix — the old
        // packZoneCoord produced inverted, empty bounds). Default spawn is Lumbridge tile
        // (3200,3200) -> region (50,50), centre zone (400,400).
        val spawn = player.tile
        val buildArea = player.viewport.loadBuildArea(spawn)
        val centreZone = spawn.zone                      // render-scene centre (positioned inside the grid)
        // GPI prefix: local player's 30-bit tile == this same spawn tile; the skipped slot is the
        // player's allocated index (== WorldLoginDetails.playerIndex == viewport.highResIndices[0]).
        val gpiPrefix = Op81GpiPrefix.build(spawnTile = spawn, localPlayerIndex = player.index)
        session.send(
            RebuildNormalSimple(
                zoneX = centreZone.x,                    // +4  centre zone X (absolute, BE u16)
                zoneZ = centreZone.y,                    // +1/+2 centre zone Z (absolute, LE u16)
                packedCoordA = buildArea.packedCoordA,   // +10 SW corner {minRegionX, minRegionZ}
                packedCoordB = buildArea.packedCoordB,   // +14 NE corner {maxRegionX, maxRegionZ}
                cameraRotation = 7,                      // harmless (§5): op81's camera anchor is a map-config flag, not this byte. Production ships 7. Kept so the wire matches; not the render lever.
                targetWorldId = EnvVars.worldId,
                rebuildPrefix = gpiPrefix,               // Shape B: 5119-byte GPI init; body = 5119 + 18 = 5137
            )
        )
        logTrace("World build area for ${player.account.username}: $buildArea centreZone=${centreZone.x},${centreZone.y} gpiPrefix=${gpiPrefix.size}B slot=${player.index}")
        val sceneRegions = SceneMapRegionPlanner.regionsForScene(centreZone.x, centreZone.y)
        logInfo(
            "World scene map groups for ${player.account.username}: " +
                SceneMapCacheDiagnostics.describe(Cache.get(), sceneRegions)
        )

        val tokenBytes = ByteArray(32).also { loginRandom.nextBytes(it) }
        val token = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes)
        session.send(HashedWorldToken(token))
        session.send(MinimapState(0, 0))
        session.send(JcoinsUpdate(INITIAL_DISPLAY_INT))
        session.send(MinimapFlagA(1))
        session.send(MinimapFlagB(1))
        session.send(SetPlayerOp(2, "Follow", priority = true))
        session.send(SetPlayerOp(3, "Trade with", priority = true))
        session.send(SetPlayerOp(5, "Req Assist", priority = true))
        session.send(SetPlayerOp(6, null, priority = true))
        session.send(SetPlayerOp(7, "Examine", priority = true))
        session.send(MidiSong(INITIAL_MIDI_SONG))
        session.send(SetPlayerOp(4, "Duel", priority = true))
        session.send(ResetClientVarcache())

        sendVarpBaseline(session, player)
    }

    private suspend fun sendVarpBaseline(session: GameSession, player: Player) {
        player.varps.seedDefaults(Rev948FirstLightVarpDefaults)
        val emitted = player.varps.flush(session)
        logTrace("World varp baseline for ${player.account.username}: ${emitted.size} packet(s)")
    }

    private suspend fun sendInitialStats(session: GameSession) {
        for (i in 0..28) {
            if (i == 3) session.send(UpdateStat(i, 1154, 10))
            else session.send(UpdateStat(i, 0, 1))
        }
    }

    private suspend fun sendInitialInventories(session: GameSession) {
        session.send(UpdateInvFull(inventoryId = 0x0313))
        session.send(
            UpdateInvFull(
                inventoryId = 0x031B,
                entries = listOf(InventoryEntry(itemId = 0xCD4B, quantity = 1000)),
            )
        )
        session.send(UpdateInvFull(inventoryId = 0x037B))
        session.send(
            UpdateInvFull(
                inventoryId = 0x005D,
                flags = 0x2,
                entries = listOf(InventoryEntry(itemId = 0x013B, quantity = 1, metadata = 0)),
            )
        )
        session.send(
            UpdateInvFull(
                inventoryId = 0x005E,
                flags = 0x2,
                entries = listOf(
                    InventoryEntry(itemId = -1, quantity = 0, metadata = 0),
                    InventoryEntry(itemId = -1, quantity = 0, metadata = 0),
                    InventoryEntry(itemId = -1, quantity = 0, metadata = 0),
                    InventoryEntry(itemId = 0x04B5, quantity = 1, metadata = 0),
                ),
            )
        )
        session.send(UpdateInvFull(inventoryId = 0x026F))
        session.send(
            UpdateInvFull(
                inventoryId = 0x037F,
                entries = listOf(
                    InventoryEntry(itemId = 0x03C0, quantity = 0),
                    InventoryEntry(itemId = 0x224A, quantity = 0),
                    InventoryEntry(itemId = 0x224C, quantity = 0),
                    InventoryEntry(itemId = 0x224E, quantity = 0),
                    InventoryEntry(itemId = 0xD64C, quantity = 0),
                    InventoryEntry(itemId = 0xD64E, quantity = 0),
                    InventoryEntry(itemId = 0xD650, quantity = 0),
                    InventoryEntry(itemId = 0xD652, quantity = 0),
                    InventoryEntry(itemId = 0xD654, quantity = 0),
                    InventoryEntry(itemId = 0xD656, quantity = 0),
                ),
            )
        )
    }

    private suspend fun sendFirstLightTail(session: GameSession, player: Player) {
        session.send(NpcInfoThunk())
        session.send(TriggerOnDialogAbort())
        session.send(ClearPendingUpdates())
        session.send(NpcInfoBuilder.buildInit(player))
        sendInitialStats(session)
        session.send(SetPlayerOp2(0))
        session.send(SetPlayerOp3(100))
        session.send(ResetEntityLists())
        session.send(SetMultiwayState(0))
        session.send(ClanChannelFull(main = true))
        for (slot in 0..7) {
            session.send(
                CutsceneData(
                    group = 0,
                    slot = slot,
                    mode = 7,
                    extendedMode = 2,
                    shape = 0,
                    flags = 0,
                    id = 0,
                    primaryLong = 0,
                    primaryInt = 0,
                    secondaryInt = 0,
                    secondaryLong = 0,
                    skipLength = 0,
                )
            )
        }
        for (slot in 0..7) {
            session.send(PlayerInfoDecode(slot = slot, mode = 0))
        }
        session.send(RebuildRegion.firstLight())
        session.send(MinimapFlagB(1))
        session.send(MinimapFlagA(1))
        session.send(TriggerOnDialogAbort())
    }

    /**
     * Dispatch loop for the world session.
     * Reads decoded packets from the session's read channel and dispatches to handlers.
     */
    private suspend fun worldSessionLoop(session: GameSession) {
        var packetCount = 0L
        var rateWindowStart = System.currentTimeMillis()
        var rateWindowCount = 0

        try {
            while (!session.disconnected) {
                val packet = kotlinx.coroutines.withTimeoutOrNull(SESSION_POLL_INTERVAL_MS) {
                    session.readChannel.receive()
                }

                if (packet != null) {
                    packetCount++
                    rateWindowCount++
                    PacketHandlers.handle<GameSession>(session, packet)
                }

                // Flush any queued responses
                session.flush()

                val now = System.currentTimeMillis()
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

    private const val SESSION_POLL_INTERVAL_MS = 1_000L
    private const val INITIAL_DISPLAY_INT = -1381430710
    private val INITIAL_MIDI_SONG = byteArrayOf(0x7E, 0x8C.toByte(), 0xE3.toByte(), 0x00, 0x00)
    /** Rolling window for the inbound packet-rate check. */
    private const val PACKET_RATE_WINDOW_MS = 10_000L

    /**
     * Max client packets per [PACKET_RATE_WINDOW_MS] window. A healthy client sends ~1
     * keepalive/s plus input events — 2000/10s (200/s sustained) is far above legitimate
     * traffic while still catching floods quickly.
     */
    private const val MAX_PACKETS_PER_RATE_WINDOW = 2_000

    /** Top-level interface ID for the main in-game HUD. */
    private const val GAME_HUD_INTERFACE = 1477
}
