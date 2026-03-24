package org.darkan.tools.loginproxy

import org.darkan.core.model.IFEvents
import org.darkan.core.EnvVars
import org.darkan.core.net.Isaac
import org.darkan.core.net.prot.Codec
import org.darkan.core.net.prot.revision.rev947.register947
import world.gregs.voidps.cache.secure.RSA
import com.sun.net.httpserver.HttpServer
import java.io.*
import java.math.BigInteger
import java.net.HttpURLConnection
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.URI
import java.nio.ByteBuffer
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicInteger

/**
 * TCP MITM proxy that intercepts NXT client login traffic to/from Jagex live servers.
 *
 * Architecture:
 *   [Patched NXT Client] -> TCP:43594 -> [LoginProxy] -> TCP -> [Jagex Live Server]
 *                                              |
 *                                         capture/ directory + console
 *
 * MITM capabilities:
 * - Decrypts the RSA block from the patched client (using our private key)
 * - Extracts ISAAC seeds from the RSA plaintext
 * - Re-encrypts the RSA block with Jagex's real public key
 * - Forwards the modified login packet to Jagex
 * - After login success, decodes all ISAAC-encrypted packets in both directions
 *
 * Phase tracking follows the login wire format documented in docs/net/login-wire-format.md:
 *   Phase 0 (CONNECTION_TYPE): First byte from client (14=login, 15=JS5, 19=lobby)
 *   Phase 1 (FIRST_RESPONSE): Server sends [1B response] [8B session_key] = 9 bytes
 *   Phase 2 (LOGIN_PACKET): Client sends [1B opcode] [2B size] [NB login_data]
 *   Phase 3 (LOGIN_RESULT): Server sends [1B result_code]
 *   Phase 4 (LOGIN_DATA_LEN): Server sends [1B data_length] (on success)
 *   Phase 5 (LOGIN_DATA): Server sends [NB login_data]
 *   Phase 6 (POST_LOGIN): ISAAC-encrypted game/lobby packets
 *
 * Run: ./gradlew :tools:run -PmainClass=org.darkan.tools.loginproxy.LoginProxyKt
 */

// ---- Configuration ----

private const val JAV_CONFIG_URL = "https://www.runescape.com/k=5/l=0/jav_config.ws?binaryType=4"
private const val DEFAULT_LISTEN_PORT = 43594
private const val DEFAULT_HTTP_PORT = 8081
private const val DEFAULT_CAPTURE_DIR = "capture"
private const val FALLBACK_LOBBY_HOST = "lobby1.runescape.com"
private const val FALLBACK_WORLD_HOST = "world1.runescape.com"
private const val FALLBACK_PORT = 43594

/** World hostnames captured from WORLDLIST_FETCH_REPLY. Shared across sessions. */
private val worldHostnames = java.util.concurrent.ConcurrentHashMap<Int, String>()

// No hex dump limit for POST_LOGIN -- we want full packet captures
private const val MAX_HEX_DUMP_BYTES_DEFAULT = 256
private const val MAX_HEX_DUMP_BYTES_POSTLOGIN = Int.MAX_VALUE

/** Server opcodes to suppress from logs (keepalives, ticks, etc.) */
private val SUPPRESS_S2C = setOf(146) // NO_TIMEOUT

/** Client opcodes to suppress from logs (keepalives, pings, mouse, camera, etc.) */
private val SUPPRESS_C2S = setOf(15, 80, 1, 2, 17, 85, 31, 51) // NO_TIMEOUT, NO_TIMEOUT_2, EVENT_CAMERA_POSITION, CAMERA_DIRECTION, EVENT_MOUSE_CLICK, EVENT_MOUSE_MOVE, EVENT_CAMERA_POSITION_2, CAMERA_ANGLE

// ---- Connection type opcodes ----

private val CONNECTION_TYPE_NAMES = mapOf(
    10 to "SESSION",
    14 to "CONNECT_LOGIN",
    15 to "JS5_INIT",
    16 to "LOGIN",
    18 to "RECONNECT",
    19 to "LOBBY",
    28 to "ACCOUNT_CREATION",
)

// ---- Login result codes (from RE docs) ----

private val LOGIN_RESULT_NAMES = mapOf(
    0 to "VIDEO_AD",
    1 to "WAIT_FOR_TIMEOUT",
    2 to "SUCCESS",
    3 to "INVALID_CREDENTIALS",
    4 to "ACCOUNT_DISABLED",
    5 to "ALREADY_LOGGED_IN",
    6 to "GAME_UPDATED",
    7 to "WORLD_FULL",
    8 to "LOGIN_SERVER_OFFLINE",
    9 to "LOGIN_LIMIT_EXCEEDED",
    10 to "BAD_SESSION_ID",
    11 to "WEAK_PASSWORD",
    12 to "MEMBERS_WORLD",
    13 to "COULD_NOT_COMPLETE_LOGIN",
    14 to "SERVER_BEING_UPDATED",
    15 to "RECONNECTING",
    16 to "LOGIN_ATTEMPTS_EXCEEDED",
    17 to "MEMBERS_AREA",
    18 to "INVALID_LOGIN_SERVER",
    19 to "TRANSFERRING_PROFILE",
    20 to "ACCOUNT_LOCKED",
    21 to "CLOSED_BETA",
    22 to "CONNECTION_FAILED",
    23 to "RECONNECT_TRY_AGAIN",
    24 to "TOO_MANY_CONNECTIONS",
    25 to "IN_QUEUE",
    26 to "TOO_MANY_WORLD_LOGINS",
    29 to "PLEASE_TRY_A_DIFFERENT_WORLD",
    30 to "NEED_SKILL_TOTAL_OF_CURRENT_WORLD",
    35 to "TEMPORARY_BAN",
    37 to "AUTHENTICATOR_CODE_REQUIRED",
    38 to "URL_REDIRECT",
    55 to "PING_STATUS",
    56 to "HOP_BLOCKED",
)

// ---- Phases ----

private enum class Phase {
    CONNECTION_TYPE,      // Waiting for first client byte
    FIRST_RESPONSE,       // Server: [1B response] [8B session_key]
    SECOND_RESPONSE,      // Server: [2B payload_length]
    XTEA_CHALLENGE,       // Server: [NB xtea_encrypted_data]
    GO_AHEAD,             // Server: [1B go_ahead]
    LOGIN_TOKEN,          // Server: [16B xtea_encrypted(token+nonce)]
    LOGIN_PACKET,         // Client: [1B opcode] [2B size] [NB data]
    LOGIN_RESULT,         // Server: [1B result_code]
    LOGIN_DATA_LEN,       // Server: [1B data_length] or [2B data_length]
    LOGIN_DATA,           // Server: [NB login_data]
    SERVER_CLIENT_VARS,   // Server: server client var exchange (steps 250-270)
    POST_LOGIN,           // ISAAC-encrypted traffic
    JS5,                  // JS5 file service (not login)
    CLOSED,               // Connection ended
}


// Prot names and sizes come from the Codec (Rev946ServerProtStubs / Rev946ClientProtStubs).

// ---- Entry Point ----

/** Protocol codec — initialized once at startup, used for opcode names/sizes. */
private val codec: Codec = register947()

fun main(args: Array<String>) {
    val listenPort = findArg(args, "--listen-port")?.toIntOrNull() ?: DEFAULT_LISTEN_PORT
    val httpPort = findArg(args, "--http-port")?.toIntOrNull() ?: DEFAULT_HTTP_PORT
    val captureDir = File(findArg(args, "--capture-dir") ?: DEFAULT_CAPTURE_DIR)

    println("[proxy] Fetching jav_config from Jagex...")
    val (targetHost, targetPort, rawConfig) = resolveJagexServer()
    println("[proxy] Jagex lobby server: $targetHost:$targetPort")

    // Start the HTTP config proxy server
    startConfigHttpServer(httpPort, rawConfig, listenPort)

    // Load RSA keys
    val ourMod = BigInteger(EnvVars.loginRsaModulus)
    val ourExp = BigInteger(EnvVars.loginRsaExponent)
    val jagexMod = BigInteger(EnvVars.JAGEX_LOGIN_RSA_MODULUS_HEX, 16)
    val jagexExp = BigInteger.valueOf(EnvVars.JAGEX_LOGIN_RSA_EXPONENT.toLong())
    println("[proxy] Our RSA modulus (first 32 hex): ${ourMod.toString(16).take(32)}...")
    println("[proxy] Jagex RSA modulus (first 32 hex): ${jagexMod.toString(16).take(32)}...")

    captureDir.mkdirs()
    println("[proxy] Capture directory: ${captureDir.absolutePath}")
    println("[proxy] TCP proxy listening on port $listenPort")
    println("[proxy] All connections will be relayed to $targetHost:$targetPort")
    println("[proxy] RSA MITM: ENABLED")
    println()

    val sessionCounter = AtomicInteger(0)
    val server = ServerSocket(listenPort)

    while (true) {
        val clientSocket = server.accept()
        val sessionId = sessionCounter.incrementAndGet()
        println("[proxy] === New connection #$sessionId from ${clientSocket.remoteSocketAddress} ===")

        val session = ProxySession(
            sessionId, clientSocket, targetHost, targetPort, captureDir,
            ourMod, ourExp, jagexMod, jagexExp
        )
        Thread({ session.run() }, "proxy-session-$sessionId").start()
    }
}

// ---- Proxy Session ----

private class ProxySession(
    private val sessionId: Int,
    private val clientSocket: Socket,
    private val targetHost: String,
    private val targetPort: Int,
    captureBaseDir: File,
    private val ourRsaMod: BigInteger,
    private val ourRsaExp: BigInteger,
    private val jagexRsaMod: BigInteger,
    private val jagexRsaExp: BigInteger,
) {
    @Volatile private var phase = Phase.CONNECTION_TYPE
    @Volatile private var connectionType = -1
    @Volatile private var xteaChallengeLen = 0
    @Volatile private var loginDataLen = 0

    // ISAAC ciphers -- initialized after RSA MITM extracts keys
    @Volatile private var isaacKeys: IntArray? = null
    @Volatile private var c2sIsaac: Isaac? = null  // Decodes client->server opcodes (raw keys)
    @Volatile private var s2cIsaac: Isaac? = null  // Decodes server->client opcodes (keys + 50)

    // Accumulation buffers for multi-read parsing
    private val s2cAccum = ByteArrayOutputStream()
    private val c2sAccum = ByteArrayOutputStream()

    // File logging
    private val sessionDir: File
    private val c2sRaw: FileOutputStream
    private val s2cRaw: FileOutputStream
    private val logWriter: PrintWriter
    private val startNanos = System.nanoTime()

    // Output stream to Jagex -- needed by c2s thread to write modified login packet
    @Volatile private var targetOutput: OutputStream? = null

    // Output stream to client -- needed by s2c thread for login data rewriting
    @Volatile private var clientOutput: OutputStream? = null

    // Active target socket — swapped when switching from lobby to world
    @Volatile private var activeTargetSocket: Socket? = null

    // Buffer for login response rewriting (LOGIN_RESULT through LOGIN_DATA)
    private val loginResponseBuffer = ByteArrayOutputStream()
    @Volatile private var bufferingLoginResponse = false

    // Session key from the server's FIRST_RESPONSE — needed for game login session key swap
    @Volatile private var serverSessionKey: Long = 0

    // Partial 2-byte S2C opcode state: when we decode the first byte of a 2-byte
    // opcode but the second byte hasn't arrived yet, we store the decoded first byte
    // here. -1 means no partial opcode pending.
    @Volatile private var s2cPartialOpcodeFirstByte = -1

    // Track the raw byte position of the varShort size field for the WORLDLIST_FETCH_REPLY packet.
    // Set by parseServerPostLogin when it encounters opcode 150, used by processAndForwardPostLogin.
    @Volatile private var worldlistSizeFieldPos = -1  // position in the s2cAccum byte array
    @Volatile private var worldlistPayloadStart = -1  // first payload byte
    @Volatile private var worldlistPayloadEnd = -1    // byte after last payload byte

    // S2C pending packet state: when we've ISAAC-decoded an opcode (and possibly
    // read the size) but the payload hasn't fully arrived, we save the decoded
    // state here to avoid consuming another ISAAC value on the next TCP read.
    @Volatile private var s2cPendingOpcode = -1   // decoded opcode, or -1 if none
    @Volatile private var s2cPendingSize = -1     // decoded size, or -1 if size not yet read
    private val s2cRecentPackets = ArrayDeque<String>(12) // ring buffer for desync debug
    private var s2cLastIsaacDebug = "" // ISAAC state at last opcode decode

    // C2S pending packet state (same pattern)
    @Volatile private var c2sPendingOpcode = -1
    @Volatile private var c2sPendingSize = -1

    init {
        val ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"))
        sessionDir = File(captureBaseDir, "login-${ts}_s${sessionId}")
        sessionDir.mkdirs()
        c2sRaw = FileOutputStream(File(sessionDir, "raw-c2s.bin"))
        s2cRaw = FileOutputStream(File(sessionDir, "raw-s2c.bin"))
        logWriter = PrintWriter(File(sessionDir, "decoded.log").bufferedWriter(), true)
        logWriter.println("# Login Proxy Capture (MITM) -- Session $sessionId")
        logWriter.println("# Started: ${LocalDateTime.now()}")
        logWriter.println("# Target: $targetHost:$targetPort")
        logWriter.println("# RSA MITM: ENABLED")
        logWriter.println()
    }

    fun run() {
        val clientIn = clientSocket.getInputStream()
        val clientOut = clientSocket.getOutputStream()

        // Read the first byte (connection type) BEFORE connecting to the target.
        // This tells us whether to route to the lobby or a game world.
        val firstByte = try { clientIn.read() } catch (e: Exception) { -1 }
        if (firstByte == -1) {
            log("CTRL", "Client disconnected before sending connection type")
            clientSocket.close()
            closeLog()
            return
        }

        connectionType = firstByte
        val typeName = CONNECTION_TYPE_NAMES[connectionType] ?: "UNKNOWN"
        log("C->S", "[1B] CONNECTION_TYPE = $connectionType ($typeName)")

        // All connections initially go to the lobby server.
        // If this turns out to be a game login (opcode 16), handleGameLogin() will
        // close the lobby connection and switch to the world server.
        val actualHost = targetHost
        val actualPort = targetPort
        if (connectionType == 15) {
            phase = Phase.JS5
        } else {
            phase = Phase.FIRST_RESPONSE
        }

        val targetSocket: Socket
        try {
            targetSocket = Socket(actualHost, actualPort)
            log("CTRL", "Connected to Jagex at $actualHost:$actualPort")
        } catch (e: Exception) {
            log("CTRL", "FAILED to connect to Jagex at $actualHost:$actualPort: ${e.message}")
            clientSocket.close()
            closeLog()
            return
        }

        activeTargetSocket = targetSocket
        val targetIn = targetSocket.getInputStream()
        val targetOut = targetSocket.getOutputStream()
        targetOutput = targetOut
        clientOutput = clientOut

        // Forward the connection type byte we already read
        targetOut.write(firstByte)
        targetOut.flush()
        synchronized(c2sRaw) { c2sRaw.write(firstByte); c2sRaw.flush() }

        val c2sThread = Thread({
            relayClientToServer(clientIn, targetOut)
            try { targetSocket.shutdownOutput() } catch (_: Exception) {}
        }, "s$sessionId-c2s")

        val s2cThread = Thread({
            relayServerToClient(targetIn, clientOut)
            try { clientSocket.shutdownOutput() } catch (_: Exception) {}
        }, "s$sessionId-s2c")

        c2sThread.start()
        s2cThread.start()
        c2sThread.join()
        s2cThread.join()

        clientSocket.close()
        targetSocket.close()
        log("CTRL", "Session ended")
        closeLog()
    }

    /**
     * Relay client -> Jagex server. In LOGIN_PACKET phase, buffers the full
     * login packet, performs RSA MITM (decrypt with our key, re-encrypt with
     * Jagex key), then forwards the modified packet.
     */
    private fun relayClientToServer(input: InputStream, output: OutputStream) {
        val buf = ByteArray(65536)
        var totalBytes = 0L

        try {
            while (true) {
                val n = input.read(buf)
                if (n == -1) break

                totalBytes += n

                // Save raw bytes (always, regardless of phase)
                synchronized(c2sRaw) {
                    c2sRaw.write(buf, 0, n)
                    c2sRaw.flush()
                }

                when (phase) {
                    Phase.LOGIN_PACKET -> {
                        // Buffer the login packet for MITM
                        c2sAccum.write(buf, 0, n)
                        val accum = c2sAccum.toByteArray()

                        if (accum.size >= 3) {
                            val loginOpcode = accum[0].toInt() and 0xFF
                            val varShortSize = ((accum[1].toInt() and 0xFF) shl 8) or (accum[2].toInt() and 0xFF)
                            val totalExpected = 3 + varShortSize

                            if (accum.size >= totalExpected) {
                                c2sAccum.reset()

                                val opName = when (loginOpcode) {
                                    16 -> "GAME_LOGIN"
                                    19 -> "LOBBY_LOGIN"
                                    30 -> "RECONNECT_LOGIN"
                                    26 -> "CONTINUE_ACK"
                                    else -> "UNKNOWN_OP"
                                }
                                log("C->S", "[${accum.size}B] LOGIN_PACKET: opcode=$loginOpcode ($opName), varShort size=$varShortSize")
                                logHex("C->S", accum, 0, accum.size)

                                if (loginOpcode == 16) {
                                    // GAME LOGIN — switch to world server
                                    handleGameLogin(accum, loginOpcode, varShortSize)
                                } else {
                                    // LOBBY LOGIN or other — forward to current target
                                    val modifiedPacket = performRsaMitm(accum, loginOpcode, varShortSize)
                                    if (modifiedPacket != null) {
                                        output.write(modifiedPacket)
                                        output.flush()
                                        log("C->S", "MITM: Forwarded modified login packet (${modifiedPacket.size}B)")
                                    } else {
                                        output.write(accum, 0, totalExpected)
                                        output.flush()
                                        log("C->S", "MITM: FAILED -- forwarded original packet")
                                    }

                                    if (accum.size > totalExpected) {
                                        val excess = accum.copyOfRange(totalExpected, accum.size)
                                        output.write(excess)
                                        output.flush()
                                        log("C->S", "Forwarded ${excess.size}B excess after login packet")
                                    }
                                }

                                phase = Phase.LOGIN_RESULT
                            } else {
                                log("C->S", "[${n}B] LOGIN_PACKET partial (${accum.size}/$totalExpected bytes accumulated)")
                            }
                        } else {
                            log("C->S", "[${n}B] LOGIN_PACKET partial (${accum.size} bytes, need header)")
                        }
                    }

                    Phase.POST_LOGIN -> {
                        // Decode ISAAC-encrypted packets
                        parseClientPostLogin(buf, n)
                        // Forward to current target (may have switched from lobby to world)
                        val out = targetOutput ?: output
                        out.write(buf, 0, n)
                        out.flush()
                    }

                    else -> {
                        // Passthrough with logging
                        val out = targetOutput ?: output
                        out.write(buf, 0, n)
                        out.flush()
                        parseClientData(buf, n)
                    }
                }
            }
        } catch (e: Exception) {
            log("C->S", "Stream ended: ${e::class.simpleName}: ${e.message}")
        }
        log("C->S", "Total: $totalBytes bytes")
    }

    /**
     * Relay Jagex server -> client. Tracks login handshake phases based on
     * expected byte counts at each step.
     */
    private fun relayServerToClient(input: InputStream, output: OutputStream) {
        val buf = ByteArray(65536)
        var totalBytes = 0L

        try {
            while (true) {
                val n = input.read(buf)
                if (n == -1) break

                // Save raw bytes
                synchronized(s2cRaw) {
                    s2cRaw.write(buf, 0, n)
                    s2cRaw.flush()
                }

                totalBytes += n

                if (bufferingLoginResponse ||
                    phase == Phase.LOGIN_RESULT ||
                    phase == Phase.LOGIN_DATA_LEN ||
                    phase == Phase.LOGIN_DATA
                ) {
                    // Buffer the entire login response (result + data) for hostname rewriting.
                    // Must catch ALL login response phases because the flag gets set too late
                    // if we only check bufferingLoginResponse (data is forwarded before flag is set).
                    bufferingLoginResponse = true
                    loginResponseBuffer.write(buf, 0, n)
                    parseServerData(buf, n)
                    if (phase == Phase.POST_LOGIN) {
                        val buffered = loginResponseBuffer.toByteArray()
                        loginResponseBuffer.reset()
                        bufferingLoginResponse = false

                        // The buffer may contain POST_LOGIN bytes AFTER the login data.
                        // Split: rewrite the login response portion, then process POST_LOGIN portion separately.
                        // loginDataLen was set during LOGIN_DATA_LEN phase.
                        // Buffer layout: [1B result][1B dataLen][dataLen bytes login data][POST_LOGIN bytes...]
                        val loginResponseEnd = 2 + loginDataLen
                        val loginPart = buffered.copyOfRange(0, minOf(loginResponseEnd, buffered.size))
                        val modified = rewriteLoginResponseHostname(loginPart)
                        output.write(modified)
                        output.flush()
                        log("CTRL", "Forwarded rewritten login response (${modified.size}B)")

                        // Forward any remaining POST_LOGIN bytes through the decoder
                        if (buffered.size > loginResponseEnd) {
                            val postLoginPart = buffered.copyOfRange(loginResponseEnd, buffered.size)
                            val rewritten = decodeAndRewritePostLogin(postLoginPart, postLoginPart.size)
                            output.write(rewritten)
                            output.flush()
                        }
                    }
                } else if (phase == Phase.POST_LOGIN) {
                    // Decode packets and rewrite worldlist hostnames
                    val modified = decodeAndRewritePostLogin(buf, n)
                    output.write(modified)
                    output.flush()
                } else {
                    // Forward immediately
                    output.write(buf, 0, n)
                    output.flush()
                    parseServerData(buf, n)
                }
            }
        } catch (e: Exception) {
            log("S->C", "Stream ended: ${e::class.simpleName}: ${e.message}")
        }
        log("S->C", "Total: $totalBytes bytes")
    }

    // ---- Game Login (World Switch) ----

    /**
     * Handle a GAME_LOGIN (opcode 16) by switching from the lobby to the world server.
     * 1. Close the lobby connection
     * 2. Connect to the world server
     * 3. Perform the handshake (send type 14, read session key)
     * 4. Decrypt the client's RSA block, swap the session key, re-encrypt
     * 5. Forward the modified login packet to the world
     * 6. Relay the world's response back to the client
     * 7. Continue relaying on the world connection
     */
    private fun handleGameLogin(packet: ByteArray, loginOpcode: Int, varShortSize: Int) {
        val worldHost = worldHostnames.values.firstOrNull() ?: FALLBACK_WORLD_HOST
        log("CTRL", "GAME_LOGIN detected — switching to world server $worldHost:$targetPort")

        // 1. Close the lobby connection (this will cause the s2c thread to exit)
        try { activeTargetSocket?.close() } catch (_: Exception) {}

        // 2. Connect to the world server
        val worldSocket: Socket
        try {
            worldSocket = Socket(worldHost, targetPort)
            log("CTRL", "Connected to world server at $worldHost:$targetPort")
        } catch (e: Exception) {
            log("CTRL", "FAILED to connect to world server: ${e.message}")
            return
        }

        activeTargetSocket = worldSocket
        val worldOut = worldSocket.getOutputStream()
        val worldIn = worldSocket.getInputStream()
        targetOutput = worldOut

        // 3. Handshake: send connection type 14, read 9-byte response
        worldOut.write(14)
        worldOut.flush()
        log("C->S", "Sent CONNECTION_TYPE=14 to world server")

        val resp = ByteArray(9)
        var read = 0
        while (read < 9) {
            val n = worldIn.read(resp, read, 9 - read)
            if (n == -1) {
                log("CTRL", "World server closed during handshake")
                return
            }
            read += n
        }
        val responseCode = resp[0].toInt() and 0xFF
        val worldSessionKey = ByteBuffer.wrap(resp, 1, 8).long
        log("S->C", "World FIRST_RESPONSE: code=$responseCode session_key=0x${"%016X".format(worldSessionKey)}")

        if (responseCode != 0) {
            log("CTRL", "World handshake failed with code $responseCode")
            return
        }

        // 4. RSA MITM with session key swap
        val modifiedPacket = performRsaMitmWithKeySwap(packet, loginOpcode, varShortSize, worldSessionKey)

        if (modifiedPacket != null) {
            worldOut.write(modifiedPacket)
            worldOut.flush()
            log("C->S", "MITM: Forwarded game login to world (${modifiedPacket.size}B, session key swapped)")
        } else {
            log("C->S", "MITM: FAILED — cannot forward game login")
            return
        }

        // 5. Read world's login result and relay to client
        val clientOut = clientOutput ?: return
        try {
            // Relay everything from world to client (login result + login data + post-login)
            val buf = ByteArray(65536)
            while (true) {
                val n = worldIn.read(buf)
                if (n == -1) break

                synchronized(s2cRaw) { s2cRaw.write(buf, 0, n); s2cRaw.flush() }

                if (bufferingLoginResponse) {
                    loginResponseBuffer.write(buf, 0, n)
                    parseServerData(buf, n)
                    if (phase == Phase.POST_LOGIN) {
                        val modified = rewriteLoginResponseHostname(loginResponseBuffer.toByteArray())
                        clientOut.write(modified)
                        clientOut.flush()
                        loginResponseBuffer.reset()
                        bufferingLoginResponse = false
                    }
                } else {
                    clientOut.write(buf, 0, n)
                    clientOut.flush()
                    parseServerData(buf, n)
                }
            }
        } catch (e: Exception) {
            log("S->C", "World relay ended: ${e::class.simpleName}: ${e.message}")
        }

        // Also relay any further client data to the world
        // (the main c2s loop will continue reading from clientIn and forwarding)
        // But the output reference needs to point to the world
        targetOutput = worldOut
    }

    /**
     * RSA MITM with session key swap: decrypt, replace session key, re-encrypt.
     */
    private fun performRsaMitmWithKeySwap(packet: ByteArray, loginOpcode: Int, varShortSize: Int, newSessionKey: Long): ByteArray? {
        try {
            val payload = packet.copyOfRange(3, 3 + varShortSize)
            var pos = 0
            pos += 8 // skip version (2x g4)
            if (loginOpcode == 16) pos++ // skip disconnect flag

            val rsaSize = ((payload[pos].toInt() and 0xFF) shl 8) or (payload[pos + 1].toInt() and 0xFF)
            pos += 2
            val rsaBlockStart = pos
            val rsaCiphertext = payload.copyOfRange(pos, pos + rsaSize)
            pos += rsaSize
            val postRsaData = payload.copyOfRange(pos, payload.size)
            val preRsaData = payload.copyOfRange(0, rsaBlockStart - 2)

            // Decrypt
            val decrypted = RSA.crypt(rsaCiphertext, ourRsaMod, ourRsaExp)
            var dpos = 0
            if (decrypted.isNotEmpty() && decrypted[0] != 10.toByte() && decrypted.size > 1 && decrypted[1] == 10.toByte()) {
                dpos = 1
            }
            val magicPos = dpos
            if ((decrypted[dpos].toInt() and 0xFF) != 10) {
                log("MITM", "ERROR: RSA magic mismatch in game login!")
                return null
            }
            dpos++ // magic
            dpos += 16 // 4 ISAAC keys

            // SWAP the session key (8 bytes at dpos)
            val oldSessionKey = ByteBuffer.wrap(decrypted, dpos, 8).long
            log("MITM", "Session key swap: lobby=0x${"%016X".format(oldSessionKey)} → world=0x${"%016X".format(newSessionKey)}")
            val keyBytes = ByteBuffer.allocate(8).putLong(newSessionKey).array()
            System.arraycopy(keyBytes, 0, decrypted, dpos, 8)

            // Extract ISAAC keys (at magicPos+1)
            var kpos = magicPos + 1
            val keys = IntArray(4)
            for (i in 0..3) {
                keys[i] = ((decrypted[kpos].toInt() and 0xFF) shl 24) or
                        ((decrypted[kpos + 1].toInt() and 0xFF) shl 16) or
                        ((decrypted[kpos + 2].toInt() and 0xFF) shl 8) or
                        (decrypted[kpos + 3].toInt() and 0xFF)
                kpos += 4
            }
            isaacKeys = keys.copyOf()
            initializeIsaacCiphers(keys)

            // Re-encrypt
            val rsaPlaintext = decrypted.copyOfRange(magicPos, decrypted.size)
            val reEncrypted = RSA.crypt(rsaPlaintext, jagexRsaMod, jagexRsaExp)

            // Build the re-encrypted block with leading zero
            val reEncBlock: ByteArray
            if (reEncrypted.isNotEmpty() && (reEncrypted[0].toInt() and 0x80) != 0) {
                reEncBlock = ByteArray(reEncrypted.size + 1)
                reEncBlock[0] = 0
                System.arraycopy(reEncrypted, 0, reEncBlock, 1, reEncrypted.size)
            } else {
                reEncBlock = reEncrypted
            }

            val newRsaSize = reEncBlock.size
            val newPayload = ByteArrayOutputStream()
            newPayload.write(preRsaData)
            newPayload.write((newRsaSize shr 8) and 0xFF)
            newPayload.write(newRsaSize and 0xFF)
            newPayload.write(reEncBlock)
            newPayload.write(postRsaData)

            val newPayloadBytes = newPayload.toByteArray()
            val result = ByteArray(3 + newPayloadBytes.size)
            result[0] = loginOpcode.toByte()
            result[1] = ((newPayloadBytes.size shr 8) and 0xFF).toByte()
            result[2] = (newPayloadBytes.size and 0xFF).toByte()
            System.arraycopy(newPayloadBytes, 0, result, 3, newPayloadBytes.size)

            return result
        } catch (e: Exception) {
            log("MITM", "ERROR during game login RSA MITM: ${e::class.simpleName}: ${e.message}")
            e.printStackTrace()
            return null
        }
    }

    // ---- RSA MITM ----

    /**
     * Perform RSA MITM on a login packet:
     * 1. Parse the pre-RSA fields to locate the RSA block
     * 2. Decrypt with our private key
     * 3. Extract ISAAC seeds
     * 4. Re-encrypt with Jagex's public key
     * 5. Reconstruct the packet with the re-encrypted RSA block
     *
     * Returns the full modified packet (opcode + varShort size + payload) or null on failure.
     */
    private fun performRsaMitm(packet: ByteArray, loginOpcode: Int, originalVarShortSize: Int): ByteArray? {
        try {
            // Payload starts after [1B opcode][2B varShort]
            val payload = packet.copyOfRange(3, 3 + originalVarShortSize)
            var pos = 0

            fun g4(): Int {
                val v = ((payload[pos].toInt() and 0xFF) shl 24) or
                        ((payload[pos + 1].toInt() and 0xFF) shl 16) or
                        ((payload[pos + 2].toInt() and 0xFF) shl 8) or
                        (payload[pos + 3].toInt() and 0xFF)
                pos += 4
                return v
            }

            // Read version info
            val majorVersion = g4()
            val minorVersion = g4()
            log("MITM", "Version: $majorVersion.$minorVersion")

            // Game login (opcode 16) has an extra disconnect_flag byte before RSA
            if (loginOpcode == 16) {
                val disconnectFlag = payload[pos].toInt() and 0xFF
                pos++
                log("MITM", "Disconnect flag: $disconnectFlag")
            }

            // RSA block: [2B rsaSize] [rsaSize bytes]
            // The client writes: pT_ushort(length+1), byte(0), bytes[...]
            // So the server reads: rsaSize = readUShort(), which includes the leading 0 byte
            val rsaSize = ((payload[pos].toInt() and 0xFF) shl 8) or (payload[pos + 1].toInt() and 0xFF)
            pos += 2
            log("MITM", "RSA block size: $rsaSize bytes (at payload offset ${pos - 2})")

            if (rsaSize <= 0 || rsaSize > 512 || pos + rsaSize > payload.size) {
                log("MITM", "ERROR: Invalid RSA block size $rsaSize (payload remaining: ${payload.size - pos})")
                return null
            }

            val rsaBlockStart = pos
            val rsaCiphertext = payload.copyOfRange(pos, pos + rsaSize)
            pos += rsaSize

            // The rest of the payload after the RSA block (XTEA-encrypted section + misc)
            val postRsaData = payload.copyOfRange(pos, payload.size)
            val preRsaData = payload.copyOfRange(0, rsaBlockStart - 2) // Everything before the rsaSize field

            // Decrypt RSA block with our private key
            log("MITM", "Decrypting RSA block (${rsaCiphertext.size}B) with our private key...")
            val decrypted = RSA.crypt(rsaCiphertext, ourRsaMod, ourRsaExp)
            log("MITM", "Decrypted RSA plaintext: ${decrypted.size}B")
            logHex("MITM", decrypted, 0, decrypted.size)

            // Parse decrypted RSA block
            var dpos = 0
            // Handle potential leading zero byte from BigInteger
            if (decrypted.isNotEmpty() && decrypted[0] == 0.toByte() && decrypted.size > 1) {
                // BigInteger may add a leading zero for positive numbers -- skip it
                // But only if the magic byte isn't at index 0
                if (decrypted[0] != 10.toByte() && decrypted.size > 1 && decrypted[1] == 10.toByte()) {
                    dpos = 1
                }
            }

            val magicPos = dpos // remember where the magic byte (and real plaintext) starts
            val magic = decrypted[dpos].toInt() and 0xFF
            dpos++
            if (magic != 10) {
                log("MITM", "ERROR: RSA magic mismatch! Expected 10, got $magic. Wrong RSA key?")
                logHex("MITM", decrypted, 0, decrypted.size)
                return null
            }

            // ISAAC/XTEA keys: 4 x int32
            val keys = IntArray(4)
            for (i in 0..3) {
                keys[i] = ((decrypted[dpos].toInt() and 0xFF) shl 24) or
                        ((decrypted[dpos + 1].toInt() and 0xFF) shl 16) or
                        ((decrypted[dpos + 2].toInt() and 0xFF) shl 8) or
                        (decrypted[dpos + 3].toInt() and 0xFF)
                dpos += 4
            }
            log("MITM", "ISAAC keys extracted: [${keys.joinToString(", ") { "0x${"%08X".format(it)}" }}]")

            // Session key: 8 bytes (long)
            if (dpos + 8 <= decrypted.size) {
                val sessionKey = ((decrypted[dpos].toLong() and 0xFF) shl 56) or
                        ((decrypted[dpos + 1].toLong() and 0xFF) shl 48) or
                        ((decrypted[dpos + 2].toLong() and 0xFF) shl 40) or
                        ((decrypted[dpos + 3].toLong() and 0xFF) shl 32) or
                        ((decrypted[dpos + 4].toLong() and 0xFF) shl 24) or
                        ((decrypted[dpos + 5].toLong() and 0xFF) shl 16) or
                        ((decrypted[dpos + 6].toLong() and 0xFF) shl 8) or
                        (decrypted[dpos + 7].toLong() and 0xFF)
                log("MITM", "Session key: 0x${"%016X".format(sessionKey)}")
            }

            // Log remaining RSA plaintext
            if (dpos + 8 < decrypted.size) {
                log("MITM", "RSA plaintext remaining ${decrypted.size - dpos - 8}B after session key:")
                logHex("MITM", decrypted, dpos + 8, decrypted.size - dpos - 8)
            }

            // Store ISAAC keys and initialize ciphers
            isaacKeys = keys.copyOf()
            initializeIsaacCiphers(keys)

            // Re-encrypt the decrypted RSA plaintext with Jagex's public key.
            // The plaintext must include the 0x0A magic byte — Jagex's server
            // validates it after decryption. magicPos accounts for any leading
            // 0x00 sign byte that BigInteger.toByteArray() may have prepended.
            val rsaPlaintext = decrypted.copyOfRange(magicPos, decrypted.size)
            log("MITM", "Re-encrypting RSA plaintext (${rsaPlaintext.size}B) with Jagex public key...")
            log("MITM", "  Plaintext starts with: ${rsaPlaintext.take(5).joinToString(" ") { "%02X".format(it) }}")

            // RSA.crypt uses BigInteger(data).modPow(exp, mod).toByteArray()
            // The result's toByteArray() may include a leading 0x00 sign byte.
            val reEncrypted = RSA.crypt(rsaPlaintext, jagexRsaMod, jagexRsaExp)

            // The on-wire format written by the client is:
            //   [2B rsaSize] [rsaSize bytes]
            // where rsaSize bytes = [0x00 padding] [ciphertext]
            // The leading 0x00 ensures BigInteger treats it as positive.
            //
            // BigInteger.toByteArray() already includes a leading 0x00 if the high
            // bit of the result is set. We need to ensure the wire format matches
            // what Jagex's server expects: the rsaSize field counts ALL bytes
            // including the leading zero.
            //
            // If toByteArray() already has a leading 0x00, use it as-is.
            // If not, prepend 0x00 for the wire format.
            val reEncBlock = if (reEncrypted.isNotEmpty() && (reEncrypted[0].toInt() and 0x80) != 0) {
                // High bit set -- prepend zero for positive BigInteger interpretation
                ByteArray(1 + reEncrypted.size).also {
                    it[0] = 0
                    System.arraycopy(reEncrypted, 0, it, 1, reEncrypted.size)
                }
            } else {
                // Already has leading zero or is positive without one
                reEncrypted
            }

            val newRsaSize = reEncBlock.size
            log("MITM", "Re-encrypted RSA block: ${newRsaSize}B (was $rsaSize)")

            // Reconstruct payload
            val newPayload = ByteArrayOutputStream()
            newPayload.write(preRsaData) // Everything before RSA size field
            // Write new RSA size (big-endian ushort)
            newPayload.write((newRsaSize shr 8) and 0xFF)
            newPayload.write(newRsaSize and 0xFF)
            // Write RSA block (includes leading zero + ciphertext)
            newPayload.write(reEncBlock)
            // Write post-RSA data
            newPayload.write(postRsaData)

            val newPayloadBytes = newPayload.toByteArray()
            val newVarShortSize = newPayloadBytes.size

            // Reconstruct full packet
            val result = ByteArray(3 + newVarShortSize)
            result[0] = loginOpcode.toByte()
            result[1] = ((newVarShortSize shr 8) and 0xFF).toByte()
            result[2] = (newVarShortSize and 0xFF).toByte()
            System.arraycopy(newPayloadBytes, 0, result, 3, newVarShortSize)

            log("MITM", "Packet reconstructed: ${result.size}B (was ${packet.size}B, delta=${result.size - packet.size})")

            // Save the decrypted RSA block to a separate file for analysis
            try {
                File(sessionDir, "rsa-plaintext.bin").writeBytes(decrypted)
                File(sessionDir, "isaac-keys.txt").writeText(
                    "ISAAC keys (decimal): ${keys.joinToString(", ")}\n" +
                    "ISAAC keys (hex): ${keys.joinToString(", ") { "0x${"%08X".format(it)}" }}\n"
                )
                log("MITM", "Saved rsa-plaintext.bin and isaac-keys.txt")
            } catch (e: Exception) {
                log("MITM", "Warning: could not save MITM artifacts: ${e.message}")
            }

            return result
        } catch (e: Exception) {
            log("MITM", "ERROR during RSA MITM: ${e::class.simpleName}: ${e.message}")
            e.printStackTrace()
            return null
        }
    }

    /**
     * Initialize ISAAC ciphers from extracted keys.
     *
     * Per crypto.md:
     * - Client creates out cipher (C->S) with raw keys
     * - Client creates in cipher (S->C) with keys + delta (50)
     *
     * For the proxy to decode:
     * - C->S packets: we need the same cipher as the server recv = Isaac(raw keys)
     * - S->C packets: we need the same cipher as the client recv = Isaac(keys + 50)
     */
    private fun initializeIsaacCiphers(keys: IntArray) {
        c2sIsaac = Isaac(keys.copyOf())
        val s2cKeys = keys.copyOf()
        for (i in s2cKeys.indices) s2cKeys[i] += EnvVars.ISAAC_DELTA
        s2cIsaac = Isaac(s2cKeys)
        log("MITM", "ISAAC ciphers initialized (C2S=raw keys, S2C=keys+${EnvVars.ISAAC_DELTA})")
    }

    // ---- Client data parsing (non-LOGIN_PACKET phases) ----

    private fun parseClientData(buf: ByteArray, len: Int) {
        when (phase) {
            Phase.CONNECTION_TYPE -> {
                // Should not happen — connection type is handled in run() before relay starts
                log("C->S", "[${len}B] CONNECTION_TYPE (unexpected in relay)")
                logHex("C->S", buf, 0, len)
            }

            Phase.JS5 -> {
                // JS5 data — silent passthrough, no logging
            }

            else -> {
                log("C->S", "[${len}B] phase=${phase.name}")
                logHex("C->S", buf, 0, len)
            }
        }
    }

    // ---- Post-login client packet decoding ----

    /**
     * Decode ISAAC-encrypted client->server packets.
     *
     * Client opcodes are single-byte (0-129). Per the TcpIn analysis:
     *   raw_byte = read_byte()
     *   opcode = (raw_byte - isaac_next()) & 0xFF
     *
     * Then read size based on codec, then payload.
     */
    private fun parseClientPostLogin(buf: ByteArray, len: Int) {
        val cipher = c2sIsaac
        if (cipher == null) {
            log("C->S", "[${len}B] POST_LOGIN (no ISAAC -- passthrough)")
            logHex("C->S", buf, 0, len, MAX_HEX_DUMP_BYTES_POSTLOGIN)
            return
        }

        // Accumulate for multi-packet reads
        c2sAccum.write(buf, 0, len)
        val data = c2sAccum.toByteArray()
        var pos = 0

        while (pos < data.size) {
            val opcode: Int
            var size = -1

            // Resume from saved state if we have a pending opcode
            if (c2sPendingOpcode >= 0) {
                opcode = c2sPendingOpcode
                size = c2sPendingSize
                c2sPendingOpcode = -1
                c2sPendingSize = -1

                if (size < 0) {
                    val sizeInfo = codec.clientProtSize(opcode)
                    when (sizeInfo) {
                        0 -> size = 0
                        -1 -> {
                            if (pos >= data.size) { c2sPendingOpcode = opcode; c2sPendingSize = -1; c2sAccum.reset(); return }
                            size = data[pos].toInt() and 0xFF; pos++
                        }
                        -2 -> {
                            if (pos + 1 >= data.size) { c2sPendingOpcode = opcode; c2sPendingSize = -1; c2sAccum.reset(); if (pos < data.size) c2sAccum.write(data, pos, data.size - pos); return }
                            size = ((data[pos].toInt() and 0xFF) shl 8) or (data[pos + 1].toInt() and 0xFF); pos += 2
                        }
                        else -> size = sizeInfo
                    }
                }
            } else {
                // Normal: decode opcode from ISAAC
                val rawByte = data[pos].toInt() and 0xFF
                pos++
                opcode = (rawByte - cipher.nextInt()) and 0xFF

                if (opcode < 0 || opcode >= 130) {
                    log("C->S", "POST_LOGIN DESYNC: decoded opcode $opcode out of range at byte ${pos - 1}")
                    logHex("C->S", data, maxOf(0, pos - 3), minOf(32, data.size - maxOf(0, pos - 3)))
                    c2sAccum.reset()
                    return
                }
            }

            if (size < 0) {
                val sizeInfo = codec.clientProtSize(opcode)
                when (sizeInfo) {
                    0 -> size = 0
                    -1 -> {
                        if (pos >= data.size) { c2sPendingOpcode = opcode; c2sPendingSize = -1; c2sAccum.reset(); return }
                        size = data[pos].toInt() and 0xFF; pos++
                    }
                    -2 -> {
                        if (pos + 1 >= data.size) { c2sPendingOpcode = opcode; c2sPendingSize = -1; c2sAccum.reset(); if (pos < data.size) c2sAccum.write(data, pos, data.size - pos); return }
                        size = ((data[pos].toInt() and 0xFF) shl 8) or (data[pos + 1].toInt() and 0xFF); pos += 2
                    }
                    else -> size = sizeInfo
                }
            }

            if (pos + size > data.size) {
                c2sPendingOpcode = opcode
                c2sPendingSize = size
                c2sAccum.reset()
                c2sAccum.write(data, pos, data.size - pos)
                return
            }

            val payload = data.copyOfRange(pos, pos + size)
            pos += size

            if (opcode !in SUPPRESS_C2S) {
                val name = codec.clientProtName(opcode)
                log("C->S", "PKT opcode=$opcode (0x${"%02X".format(opcode)}) $name size=$size")
                if (!prettyPrintClientPkt(opcode, payload, size)) {
                    if (size > 0) logHex("C->S", payload, 0, size, MAX_HEX_DUMP_BYTES_POSTLOGIN)
                }
            }
        }

        // All data consumed
        c2sAccum.reset()
    }

    // ---- Server data parsing ----

    /**
     * Parse server data with accumulation. TCP may deliver partial reads,
     * so we accumulate bytes until we have enough for the current phase.
     */
    private fun parseServerData(buf: ByteArray, len: Int) {
        when (phase) {
            Phase.FIRST_RESPONSE -> {
                s2cAccum.write(buf, 0, len)
                val accum = s2cAccum.toByteArray()

                if (accum.size >= 9) {
                    val responseCode = accum[0].toInt() and 0xFF
                    val sessionKey = ByteBuffer.wrap(accum, 1, 8).long
                    serverSessionKey = sessionKey
                    log("S->C", "[${accum.size}B] FIRST_RESPONSE: response_code=$responseCode, session_key=0x${"%016X".format(sessionKey)}")
                    logHex("S->C", accum, 0, accum.size)

                    if (responseCode == 0) {
                        log("S->C", "  -> OK. Client will send login packet next (savedStep=0x50 for lobby)")
                        phase = Phase.LOGIN_PACKET
                    } else {
                        log("S->C", "  -> Error: code $responseCode -- connection will likely close")
                        phase = Phase.CLOSED
                    }

                    if (accum.size > 9) {
                        s2cAccum.reset()
                        s2cAccum.write(accum, 9, accum.size - 9)
                        val remaining = s2cAccum.toByteArray()
                        s2cAccum.reset()
                        parseServerData(remaining, remaining.size)
                        return
                    }
                    s2cAccum.reset()
                } else {
                    log("S->C", "[${len}B] FIRST_RESPONSE partial (${accum.size}/9 bytes so far)")
                }
            }

            Phase.SECOND_RESPONSE -> {
                s2cAccum.write(buf, 0, len)
                val accum = s2cAccum.toByteArray()

                if (accum.size >= 2) {
                    xteaChallengeLen = ((accum[0].toInt() and 0xFF) shl 8) or (accum[1].toInt() and 0xFF)
                    log("S->C", "[${accum.size}B] SECOND_RESPONSE: xtea_challenge_length=$xteaChallengeLen")
                    logHex("S->C", accum, 0, accum.size)
                    phase = Phase.XTEA_CHALLENGE

                    if (accum.size > 2) {
                        s2cAccum.reset()
                        s2cAccum.write(accum, 2, accum.size - 2)
                        val remaining = s2cAccum.toByteArray()
                        s2cAccum.reset()
                        parseServerData(remaining, remaining.size)
                        return
                    }
                    s2cAccum.reset()
                } else {
                    log("S->C", "[${len}B] SECOND_RESPONSE partial (${accum.size}/2 bytes)")
                }
            }

            Phase.XTEA_CHALLENGE -> {
                s2cAccum.write(buf, 0, len)
                val accum = s2cAccum.toByteArray()

                if (accum.size >= xteaChallengeLen) {
                    log("S->C", "[${accum.size}B] XTEA_CHALLENGE: $xteaChallengeLen bytes of XTEA-encrypted data")
                    logHex("S->C", accum, 0, minOf(accum.size, xteaChallengeLen))
                    phase = Phase.GO_AHEAD

                    if (accum.size > xteaChallengeLen) {
                        s2cAccum.reset()
                        s2cAccum.write(accum, xteaChallengeLen, accum.size - xteaChallengeLen)
                        val remaining = s2cAccum.toByteArray()
                        s2cAccum.reset()
                        parseServerData(remaining, remaining.size)
                        return
                    }
                    s2cAccum.reset()
                } else {
                    log("S->C", "[${len}B] XTEA_CHALLENGE partial (${accum.size}/$xteaChallengeLen bytes)")
                }
            }

            Phase.GO_AHEAD -> {
                val goAhead = buf[0].toInt() and 0xFF
                log("S->C", "[${len}B] GO_AHEAD: value=$goAhead ${if (goAhead == 1) "(OK)" else "(UNEXPECTED)"}")
                logHex("S->C", buf, 0, len)

                if (goAhead == 1) {
                    phase = Phase.LOGIN_TOKEN
                    if (len > 1) {
                        parseServerData(buf.copyOfRange(1, len), len - 1)
                        return
                    }
                } else {
                    phase = Phase.CLOSED
                }
            }

            Phase.LOGIN_TOKEN -> {
                s2cAccum.write(buf, 0, len)
                val accum = s2cAccum.toByteArray()

                if (accum.size >= 16) {
                    log("S->C", "[${accum.size}B] LOGIN_TOKEN: 16 bytes XTEA-encrypted (token + nonce)")
                    logHex("S->C", accum, 0, minOf(accum.size, 16))
                    phase = Phase.LOGIN_PACKET
                    log("S->C", "  -> Server handshake complete. Waiting for client login packet...")

                    if (accum.size > 16) {
                        s2cAccum.reset()
                        s2cAccum.write(accum, 16, accum.size - 16)
                        val remaining = s2cAccum.toByteArray()
                        s2cAccum.reset()
                        parseServerData(remaining, remaining.size)
                        return
                    }
                    s2cAccum.reset()
                } else {
                    log("S->C", "[${len}B] LOGIN_TOKEN partial (${accum.size}/16 bytes)")
                }
            }

            Phase.LOGIN_RESULT -> {
                val resultCode = buf[0].toInt() and 0xFF
                val resultName = LOGIN_RESULT_NAMES[resultCode] ?: "UNKNOWN"
                log("S->C", "[${len}B] LOGIN_RESULT: code=$resultCode ($resultName)")
                logHex("S->C", buf, 0, len)

                when (resultCode) {
                    2 -> {
                        log("S->C", "  -> Login SUCCESS!")
                        phase = Phase.LOGIN_DATA_LEN
                        if (len > 1) {
                            parseServerData(buf.copyOfRange(1, len), len - 1)
                            return
                        }
                    }
                    25 -> {
                        log("S->C", "  -> In queue, waiting for updates...")
                        // Stay in LOGIN_RESULT to read next result
                    }
                    37 -> {
                        log("S->C", "  -> TOTP authenticator code required")
                        phase = Phase.POST_LOGIN
                    }
                    else -> {
                        log("S->C", "  -> Login failed/special: $resultName")
                        phase = Phase.POST_LOGIN
                    }
                }
            }

            Phase.LOGIN_DATA_LEN -> {
                val dataLen = buf[0].toInt() and 0xFF
                loginDataLen = dataLen
                log("S->C", "[${len}B] LOGIN_DATA_LEN: $loginDataLen bytes of login data to follow")
                logHex("S->C", buf, 0, len)
                phase = Phase.LOGIN_DATA

                if (len > 1) {
                    parseServerData(buf.copyOfRange(1, len), len - 1)
                    return
                }
            }

            Phase.LOGIN_DATA -> {
                s2cAccum.write(buf, 0, len)
                val accum = s2cAccum.toByteArray()

                if (accum.size >= loginDataLen) {
                    log("S->C", "[${accum.size}B] LOGIN_DATA: $loginDataLen bytes of lobby/game login state")
                    logHex("S->C", accum, 0, loginDataLen, MAX_HEX_DUMP_BYTES_POSTLOGIN)
                    parseLobbyLoginData(accum, loginDataLen)

                    phase = Phase.POST_LOGIN
                    log("S->C", "  -> Login data received. Switching to POST_LOGIN mode (ISAAC decoding active: ${s2cIsaac != null}).")

                    if (accum.size > loginDataLen) {
                        s2cAccum.reset()
                        val remaining = accum.copyOfRange(loginDataLen, accum.size)
                        parseServerData(remaining, remaining.size)
                        return
                    }
                    s2cAccum.reset()
                } else {
                    log("S->C", "[${len}B] LOGIN_DATA partial (${accum.size}/$loginDataLen bytes)")
                }
            }

            Phase.JS5 -> {
                log("S->C", "[${len}B] JS5 data")
                logHex("S->C", buf, 0, len, MAX_HEX_DUMP_BYTES_DEFAULT)
            }

            Phase.POST_LOGIN -> {
                if (bufferingLoginResponse) {
                    // Don't process POST_LOGIN data during login response buffering —
                    // the bytes will be handled later by decodeAndRewritePostLogin.
                    // Also clear s2cAccum so stale data doesn't corrupt the decoder.
                    s2cAccum.reset()
                } else {
                    parseServerPostLogin(buf, len)
                }
            }

            else -> {
                log("S->C", "[${len}B] phase=${phase.name}")
                logHex("S->C", buf, 0, len)
            }
        }
    }

    // ---- Post-login server packet decoding ----

    /**
     * Decode ISAAC-encrypted server->client packets.
     *
     * Server opcodes use 1 or 2 bytes. BOTH bytes are ISAAC-decoded:
     *   decoded1 = (raw_byte1 - isaac_next()) & 0xFF
     *   if decoded1 < 128:
     *       opcode = decoded1                          (1 ISAAC value consumed)
     *   else:
     *       decoded2 = (raw_byte2 - isaac_next()) & 0xFF
     *       opcode = (decoded1 - 128) * 256 + decoded2 (2 ISAAC values consumed)
     *
     * Then look up size from codec.
     */
    /**
     * Process POST_LOGIN S2C data: rewrite worldlist hostnames to "localhost", then forward.
     *
     * ISAAC only encrypts opcode bytes. Size prefixes and payloads are cleartext.
     * We replace ".runescape.com" hostnames with "localhost" and adjust the containing
     * packet's varShort size field.
     */
    private fun processAndForwardPostLogin(buf: ByteArray, len: Int): ByteArray {
        parseServerPostLogin(buf, len)

        val data = buf.copyOfRange(0, len)
        val marker = ".runescape.com".toByteArray(Charsets.ISO_8859_1)

        // Quick check: does this chunk contain any worldlist hostnames?
        var hasMarker = false
        for (i in 0..data.size - marker.size) {
            var m = true
            for (j in marker.indices) { if (data[i + j] != marker[j]) { m = false; break } }
            if (m) { hasMarker = true; break }
        }
        if (!hasMarker) return data

        // Replace hostnames and track total byte delta
        val localhost = "localhost".toByteArray(Charsets.ISO_8859_1)
        val result = ByteArrayOutputStream(data.size)
        var readPos = 0
        var totalDelta = 0
        // Track which output positions correspond to which input positions for size field fixup
        // sizeFieldInputPos = position of the varShort size field in the ORIGINAL data
        var sizeFieldInputPos = -1

        // First: find the varShort size field position.
        // The worldlist packet (opcode 150, >127 so 2-byte opcode) is:
        //   [2B encrypted opcode][2B BE size][payload...]
        // The first hostname is somewhere inside the payload. Walk backwards from the first
        // hostname occurrence to find the size field: it's the 2-byte BE value immediately
        // before the payload starts. The payload starts right after the size field.
        // We identify the size field by reading the 2-byte value and checking if
        // sizeFieldPos + 2 + size == end_of_data_or_next_packet.
        // Simpler heuristic: search backwards from the first hostname for the size field.
        for (i in 0..data.size - marker.size) {
            var m = true
            for (j in marker.indices) { if (data[i + j] != marker[j]) { m = false; break } }
            if (m) {
                // Found first hostname at position i. Walk backwards to find the packet start.
                // The worldlist payload starts with a frame byte (0x00 or 0x01), preceded by
                // the 2-byte size field, preceded by the 2-byte encrypted opcode.
                // The frame byte is the first byte of the payload.
                // Let's scan backwards for a position where [pos] and [pos+1] form a valid
                // varShort size that spans to the end of known data.
                var searchPos = i - 1
                while (searchPos >= 4) { // need at least 4 bytes before (2 opcode + 2 size)
                    val candidateSize = ((data[searchPos - 1].toInt() and 0xFF) shl 8) or
                            (data[searchPos].toInt() and 0xFF)
                    val payloadStart = searchPos + 1
                    // Check if this size is reasonable (positive and within buffer range)
                    if (candidateSize in 100..60000 && payloadStart + candidateSize <= data.size + 1000) {
                        sizeFieldInputPos = searchPos - 1
                        break
                    }
                    searchPos--
                }
                break
            }
        }

        // Build the output with hostnames replaced
        while (readPos < data.size) {
            var isMarker = false
            if (readPos + marker.size <= data.size) {
                isMarker = true
                for (j in marker.indices) { if (data[readPos + j] != marker[j]) { isMarker = false; break } }
            }

            if (isMarker) {
                var hostStart = readPos
                while (hostStart > 0 && data[hostStart - 1] != 0.toByte()) hostStart--
                var nullPos = readPos + marker.size
                while (nullPos < data.size && data[nullPos] != 0.toByte()) nullPos++

                val oldHostLen = nullPos - hostStart
                totalDelta += oldHostLen - localhost.size

                // Rewind: we already wrote bytes up to readPos. We need to erase from hostStart.
                // This is tricky with a stream. Let me just rebuild more carefully.
                // Actually since we're writing byte-by-byte, hostStart < readPos means we already
                // wrote the hostname prefix bytes. We need a different approach.
                break // bail out and use the two-pass approach below
            } else {
                readPos++
            }
        }

        // TWO-PASS approach: first compute delta, then rebuild
        result.reset()
        readPos = 0
        totalDelta = 0

        // Pass 1: compute total delta
        var tmpPos = 0
        while (tmpPos < data.size) {
            var isMarker = false
            if (tmpPos + marker.size <= data.size) {
                isMarker = true
                for (j in marker.indices) { if (data[tmpPos + j] != marker[j]) { isMarker = false; break } }
            }
            if (isMarker) {
                var hostStart = tmpPos
                while (hostStart > 0 && data[hostStart - 1] != 0.toByte()) hostStart--
                var nullPos = tmpPos + marker.size
                while (nullPos < data.size && data[nullPos] != 0.toByte()) nullPos++
                totalDelta += (nullPos - hostStart) - localhost.size
                tmpPos = nullPos + 1
            } else {
                tmpPos++
            }
        }

        if (totalDelta == 0) return data // hostnames are already "localhost" length? Just return

        // Pass 2: rebuild with replacements
        readPos = 0
        var bytesBeforeFirstHost = 0
        var foundFirst = false
        while (readPos < data.size) {
            var isMarker = false
            if (readPos + marker.size <= data.size) {
                isMarker = true
                for (j in marker.indices) { if (data[readPos + j] != marker[j]) { isMarker = false; break } }
            }

            if (isMarker) {
                // Erase back to hostname start
                var hostStart = readPos
                while (hostStart > 0 && data[hostStart - 1] != 0.toByte()) hostStart--
                var nullPos = readPos + marker.size
                while (nullPos < data.size && data[nullPos] != 0.toByte()) nullPos++

                if (!foundFirst) {
                    bytesBeforeFirstHost = hostStart
                    foundFirst = true
                    // Rewind the result to hostStart
                    val current = result.toByteArray()
                    result.reset()
                    result.write(current, 0, hostStart)
                }

                result.write(localhost)
                result.write(0)
                readPos = nullPos + 1
            } else {
                result.write(data[readPos].toInt())
                readPos++
            }
        }

        val output = result.toByteArray()

        // Fix the varShort size field
        if (sizeFieldInputPos >= 0 && sizeFieldInputPos + 1 < output.size) {
            val oldSize = ((output[sizeFieldInputPos].toInt() and 0xFF) shl 8) or
                    (output[sizeFieldInputPos + 1].toInt() and 0xFF)
            val newSize = oldSize - totalDelta
            output[sizeFieldInputPos] = ((newSize shr 8) and 0xFF).toByte()
            output[sizeFieldInputPos + 1] = (newSize and 0xFF).toByte()
            log("CTRL", "Worldlist rewrite: ${data.size}→${output.size}B, varShort size $oldSize→$newSize (delta=$totalDelta)")
        } else {
            log("CTRL", "WARNING: Could not find worldlist size field (delta=$totalDelta)")
        }

        return output
    }

    /**
     * Decode POST_LOGIN S2C packets, rewrite WORLDLIST_FETCH_REPLY hostnames, return bytes to forward.
     *
     * For each complete packet:
     * - If WORLDLIST_FETCH_REPLY (opcode 150): replace ".runescape.com" hostnames with "localhost"
     *   in the cleartext payload, recompute the varShort size, keep original encrypted opcode bytes.
     * - All other packets: forward the original raw bytes unchanged.
     *
     * This works because ISAAC only encrypts opcode bytes. Size and payload are cleartext.
     */
    private fun decodeAndRewritePostLogin(buf: ByteArray, len: Int): ByteArray {
        val cipher = s2cIsaac
        if (cipher == null) {
            return buf.copyOfRange(0, len)
        }

        // NOTE: do NOT call parseServerPostLogin here — this function does its own
        // ISAAC decoding. Calling both would consume ISAAC values twice → desync.
        s2cAccum.write(buf, 0, len)
        val data = s2cAccum.toByteArray()
        var pos = 0
        val output = ByteArrayOutputStream(data.size + 64)

        while (pos < data.size) {
            val packetStart = pos // raw byte position of this packet's start
            val opcode: Int
            var size = -1

            // --- Decode opcode (same logic as parseServerPostLogin) ---
            if (s2cPendingOpcode >= 0) {
                opcode = s2cPendingOpcode
                size = s2cPendingSize
                s2cPendingOpcode = -1
                s2cPendingSize = -1
                if (size < 0) {
                    val sizeInfo = codec.serverProtSize(opcode)
                    when (sizeInfo) {
                        0 -> size = 0
                        -1 -> { if (pos >= data.size) { s2cPendingOpcode = opcode; s2cPendingSize = -1; break }; size = data[pos].toInt() and 0xFF; pos++ }
                        -2 -> { if (pos + 1 >= data.size) { s2cPendingOpcode = opcode; s2cPendingSize = -1; break }; size = ((data[pos].toInt() and 0xFF) shl 8) or (data[pos + 1].toInt() and 0xFF); pos += 2 }
                        else -> size = sizeInfo
                    }
                }
            } else if (s2cPartialOpcodeFirstByte >= 0) {
                val decoded = s2cPartialOpcodeFirstByte
                s2cPartialOpcodeFirstByte = -1
                if (pos >= data.size) { s2cPartialOpcodeFirstByte = decoded; break }
                val rawSecondByte = data[pos].toInt() and 0xFF; pos++
                val decodedSecond = (rawSecondByte - cipher.nextInt()) and 0xFF
                opcode = (decoded - 128) * 256 + decodedSecond
            } else {
                if (pos >= data.size) break
                val rawByte = data[pos].toInt() and 0xFF; pos++
                val isaacVal = cipher.nextInt()
                val decoded = (rawByte - isaacVal) and 0xFF
                if (decoded < 128) {
                    opcode = decoded
                } else {
                    if (pos >= data.size) { s2cPartialOpcodeFirstByte = decoded; break }
                    val rawSecondByte = data[pos].toInt() and 0xFF; pos++
                    val isaacVal2 = cipher.nextInt()
                    val decodedSecond = (rawSecondByte - isaacVal2) and 0xFF
                    opcode = (decoded - 128) * 256 + decodedSecond
                }
                // Log last 3 opcodes decoded for desync debugging
                if (s2cRecentPackets.size >= 8) {
                    s2cLastIsaacDebug = "raw=0x${"%02X".format(rawByte)} isaac=${isaacVal} decoded=$decoded pos=${pos-1}"
                }
            }

            if (opcode < 0 || opcode >= 218) {
                log("S->C", "POST_LOGIN DESYNC: opcode $opcode out of range at byte ${pos - 1}")
                log("S->C", "  ISAAC debug: $s2cLastIsaacDebug")
                log("S->C", "  Last 10 packets before desync:")
                for (entry in s2cRecentPackets) log("S->C", "    $entry")
                logHex("S->C", data, maxOf(0, pos - 16), minOf(32, data.size - maxOf(0, pos - 16)))
                s2cAccum.reset()
                s2cIsaac = null
                // Flush remaining raw data as-is
                output.write(data, packetStart, data.size - packetStart)
                return output.toByteArray()
            }

            // --- Read size ---
            val sizeFieldStart = pos
            if (size < 0) {
                val sizeInfo = codec.serverProtSize(opcode)
                when (sizeInfo) {
                    0 -> size = 0
                    -1 -> { if (pos >= data.size) { s2cPendingOpcode = opcode; s2cPendingSize = -1; break }; size = data[pos].toInt() and 0xFF; pos++ }
                    -2 -> { if (pos + 1 >= data.size) { s2cPendingOpcode = opcode; s2cPendingSize = -1; break }; size = ((data[pos].toInt() and 0xFF) shl 8) or (data[pos + 1].toInt() and 0xFF); pos += 2 }
                    else -> size = sizeInfo
                }
            }

            val payloadStart = pos

            if (pos + size > data.size) {
                // Partial payload — save state, buffer only the unread payload bytes.
                // The opcode and size have already been consumed (ISAAC advanced, size parsed).
                // Don't include them in the accumulator — they're saved in pending state.
                s2cPendingOpcode = opcode
                s2cPendingSize = size
                s2cAccum.reset()
                if (pos < data.size) {
                    s2cAccum.write(data, pos, data.size - pos)  // only unconsumed bytes
                }
                // Write the original raw opcode+size bytes to output (passthrough)
                output.write(data, packetStart, pos - packetStart)
                return output.toByteArray()
            }

            val payload = data.copyOfRange(pos, pos + size)
            pos += size

            // Track recent packets for desync debugging
            val sizeMode = codec.serverProtSize(opcode)
            val sizeModeStr = when (sizeMode) { -1 -> "varByte"; -2 -> "varShort"; else -> "fixed($sizeMode)" }
            val entry = "op=$opcode (${codec.serverProtName(opcode)}) $sizeModeStr data=$size bytes @byte${packetStart}"
            if (s2cRecentPackets.size >= 10) s2cRecentPackets.removeFirst()
            s2cRecentPackets.addLast(entry)

            // --- Log (suppress keepalives) ---
            if (opcode !in SUPPRESS_S2C) {
                val name = codec.serverProtName(opcode)
                log("S->C", "PKT opcode=$opcode (0x${"%02X".format(opcode)}) $name size=$size")
                if (!prettyPrintServerPkt(opcode, payload, size)) {
                    if (size > 0) logHex("S->C", payload, 0, size, MAX_HEX_DUMP_BYTES_POSTLOGIN)
                }
            }

            // --- Rewrite or passthrough ---
            if (opcode == 159 && size > 0) {  // WORLDLIST_FETCH_REPLY (947: 159)
                // WORLDLIST_FETCH_REPLY — rewrite hostnames
                val newPayload = rewriteWorldlistPayload(payload)
                if (newPayload !== payload) {
                    // Write original encrypted opcode bytes
                    output.write(data, packetStart, sizeFieldStart - packetStart)
                    // Write new varShort size
                    output.write((newPayload.size shr 8) and 0xFF)
                    output.write(newPayload.size and 0xFF)
                    // Write modified payload
                    output.write(newPayload)
                    log("CTRL", "Worldlist rewrite: payload $size → ${newPayload.size}")
                } else {
                    // No changes — write original bytes
                    output.write(data, packetStart, pos - packetStart)
                }
            } else {
                // Forward original raw bytes for this packet
                output.write(data, packetStart, pos - packetStart)
            }
        }

        // All data consumed
        s2cAccum.reset()
        return output.toByteArray()
    }

    /**
     * Rewrite hostnames in a WORLDLIST_FETCH_REPLY payload.
     * Replaces all null-terminated strings containing ".runescape.com" with "localhost".
     * Returns a new byte array if any changes were made, or the same array if not.
     */
    private fun rewriteWorldlistPayload(payload: ByteArray): ByteArray {
        val marker = ".runescape.com".toByteArray(Charsets.ISO_8859_1)
        val localhost = "localhost".toByteArray(Charsets.ISO_8859_1)

        // Check if there are any hostnames to rewrite
        var hasMatch = false
        for (i in 0..payload.size - marker.size) {
            var m = true
            for (j in marker.indices) { if (payload[i + j] != marker[j]) { m = false; break } }
            if (m) { hasMatch = true; break }
        }
        if (!hasMatch) return payload

        // Rebuild payload with hostname replacements
        val out = ByteArrayOutputStream(payload.size)
        var i = 0
        while (i < payload.size) {
            // Check for ".runescape.com" at current position
            var isMarker = false
            if (i + marker.size <= payload.size) {
                isMarker = true
                for (j in marker.indices) { if (payload[i + j] != marker[j]) { isMarker = false; break } }
            }

            if (isMarker) {
                // Walk backwards to hostname start (after preceding null byte)
                var hostStart = i
                while (hostStart > 0 && payload[hostStart - 1] != 0.toByte()) hostStart--
                // Walk forward past ".runescape.com" to find null terminator
                var hostEnd = i + marker.size
                while (hostEnd < payload.size && payload[hostEnd] != 0.toByte()) hostEnd++

                // Erase the hostname bytes we already wrote to output (rewind)
                val written = out.toByteArray()
                out.reset()
                out.write(written, 0, written.size - (i - hostStart))

                // Write replacement
                out.write(localhost)
                out.write(0) // null terminator

                // Also store in worldHostnames map for routing
                val oldHost = String(payload, hostStart, hostEnd - hostStart, Charsets.ISO_8859_1)
                // Extract world ID from "worldNN.runescape.com" pattern
                val worldMatch = Regex("world(\\d+)").find(oldHost)
                if (worldMatch != null) {
                    worldHostnames[worldMatch.groupValues[1].toInt()] = oldHost
                }

                i = hostEnd + 1 // skip past null terminator
            } else {
                out.write(payload[i].toInt())
                i++
            }
        }

        return out.toByteArray()
    }

    @Suppress("unused") // kept for reference — the new decodeAndRewritePostLogin replaces this
    private fun parseServerPostLogin(buf: ByteArray, len: Int) {
        val cipher = s2cIsaac
        if (cipher == null) {
            log("S->C", "[${len}B] POST_LOGIN (no ISAAC -- raw dump)")
            logHex("S->C", buf, 0, len, MAX_HEX_DUMP_BYTES_POSTLOGIN)
            return
        }

        s2cAccum.write(buf, 0, len)
        val data = s2cAccum.toByteArray()
        var pos = 0

        while (pos < data.size) {
            val opcode: Int
            var size = -1

            // Resume from saved state if we have a pending opcode from a previous
            // partial read (ISAAC already consumed for this opcode).
            if (s2cPendingOpcode >= 0) {
                opcode = s2cPendingOpcode
                size = s2cPendingSize  // may be -1 if size wasn't read yet
                s2cPendingOpcode = -1
                s2cPendingSize = -1

                // If size was already decoded, skip straight to payload
                if (size >= 0) {
                    // just need payload bytes
                } else {
                    // We have the opcode but still need the size byte(s)
                    val sizeInfo = codec.serverProtSize(opcode)
                    when (sizeInfo) {
                        0 -> size = 0
                        -1 -> {
                            if (pos >= data.size) {
                                s2cPendingOpcode = opcode
                                s2cPendingSize = -1
                                s2cAccum.reset()
                                s2cAccum.write(data, pos, data.size - pos)
                                return
                            }
                            size = data[pos].toInt() and 0xFF
                            pos++
                        }
                        -2 -> {
                            if (pos + 1 >= data.size) {
                                s2cPendingOpcode = opcode
                                s2cPendingSize = -1
                                s2cAccum.reset()
                                if (pos < data.size) s2cAccum.write(data, pos, data.size - pos)
                                return
                            }
                            size = ((data[pos].toInt() and 0xFF) shl 8) or (data[pos + 1].toInt() and 0xFF)
                            pos += 2
                        }
                        else -> size = sizeInfo
                    }
                }
            } else if (s2cPartialOpcodeFirstByte >= 0) {
                // Resuming a partial 2-byte opcode (first byte ISAAC-decoded, need 2nd)
                val decoded = s2cPartialOpcodeFirstByte
                s2cPartialOpcodeFirstByte = -1
                val rawSecondByte = data[pos].toInt() and 0xFF
                pos++
                val decodedSecond = (rawSecondByte - cipher.nextInt()) and 0xFF
                opcode = (decoded - 128) * 256 + decodedSecond
            } else {
                // Normal case: decode opcode from ISAAC
                val rawByte = data[pos].toInt() and 0xFF
                pos++
                val decoded = (rawByte - cipher.nextInt()) and 0xFF

                if (decoded < 128) {
                    opcode = decoded
                } else {
                    // 2-byte opcode: BOTH bytes are ISAAC-decoded (each consumes 1 value)
                    if (pos >= data.size) {
                        s2cPartialOpcodeFirstByte = decoded
                        s2cAccum.reset()
                        return
                    }
                    val rawSecondByte = data[pos].toInt() and 0xFF
                    pos++
                    val decodedSecond = (rawSecondByte - cipher.nextInt()) and 0xFF
                    opcode = (decoded - 128) * 256 + decodedSecond
                }
            }

            if (opcode < 0 || opcode >= 218) {
                log("S->C", "POST_LOGIN DESYNC: decoded opcode $opcode out of range [0,218) at byte ${pos - 1}")
                logHex("S->C", data, maxOf(0, pos - 3), minOf(64, data.size - maxOf(0, pos - 3)))
                s2cAccum.reset()
                log("S->C", "POST_LOGIN: ISAAC desync -- disabling ISAAC decoding for remainder")
                s2cIsaac = null
                return
            }

            // If size not yet determined (normal path, not resumed from pending)
            if (size < 0) {
                val sizeInfo = codec.serverProtSize(opcode)
                when (sizeInfo) {
                    0 -> size = 0
                    -1 -> {
                        if (pos >= data.size) {
                            s2cPendingOpcode = opcode
                            s2cPendingSize = -1
                            s2cAccum.reset()
                            return
                        }
                        size = data[pos].toInt() and 0xFF
                        pos++
                    }
                    -2 -> {
                        if (pos + 1 >= data.size) {
                            s2cPendingOpcode = opcode
                            s2cPendingSize = -1
                            s2cAccum.reset()
                            if (pos < data.size) s2cAccum.write(data, pos, data.size - pos)
                            return
                        }
                        size = ((data[pos].toInt() and 0xFF) shl 8) or (data[pos + 1].toInt() and 0xFF)
                        pos += 2
                    }
                    else -> size = sizeInfo
                }
            }

            if (pos + size > data.size) {
                // Partial payload -- save decoded state and buffer remaining raw bytes
                s2cPendingOpcode = opcode
                s2cPendingSize = size
                s2cAccum.reset()
                s2cAccum.write(data, pos, data.size - pos)
                log("S->C", "POST_LOGIN: partial payload for opcode $opcode ($size bytes needed, ${data.size - pos} available)")
                return
            }

            val payload = data.copyOfRange(pos, pos + size)
            pos += size

            if (opcode !in SUPPRESS_S2C) {
                val name = codec.serverProtName(opcode)
                log("S->C", "PKT opcode=$opcode (0x${"%02X".format(opcode)}) $name size=$size")
                if (!prettyPrintServerPkt(opcode, payload, size)) {
                    if (size > 0) logHex("S->C", payload, 0, size, MAX_HEX_DUMP_BYTES_POSTLOGIN)
                }
            }
        }

        // All data consumed
        s2cAccum.reset()
    }

    // ---- Server packet pretty-printers ----

    /** Pretty-print a known server packet. Returns true if handled, false for generic hex dump. */
    private fun prettyPrintServerPkt(opcode: Int, d: ByteArray, size: Int): Boolean {
        try {
            when (opcode) {
                // ---- Variable setters (RE-verified from rs2client rev 947) ----
                14 -> { // SET_VARP_SMALL: g2(lo-128) varpId, g1(-128) value
                    val id = r2sub128(d, 0); val v = (r1(d, 2) - 128).toByte().toInt()
                    log("S->C", "         varp[$id] = $v")
                }
                124 -> { // SET_VARP_INT: g2LE varpId, g4_alt1 value
                    val id = r2le(d, 0); val v = r4alt1(d, 2)
                    log("S->C", "         varp[$id] = $v (0x${"%08X".format(v)})")
                }
                138 -> { // SET_VARP_LONG: g8BE value FIRST, g2(lo-128) varpId
                    val v = r8(d, 0); val id = r2sub128(d, 8)
                    log("S->C", "         varp[$id] = $v (0x${"%016X".format(v)})")
                }
                12 -> { // SET_VARC_INT: g2BE varcId, g4_alt2(LE) value
                    val k = r2(d, 0); val v = r4alt2(d, 2)
                    log("S->C", "         varc[$k] = $v (0x${"%08X".format(v)})")
                }
                19 -> { // SET_VARC_SMALL: g1(0x80-raw) value FIRST, g2BE varcId
                    val v = (0x80 - r1(d, 0)).toByte().toInt(); val k = r2(d, 1)
                    log("S->C", "         varc[$k] = $v")
                }
                114 -> { // UPDATE_STAT: g4_alt1 xp, g1(-128) boostedLevel, g1(negate) statId
                    val xp = r4alt1(d, 0); val boosted = r1(d, 4) - 128; val statId = (-(d[5].toInt())) and 0xFF
                    log("S->C", "         stat[$statId] xp=$xp boosted=$boosted")
                }
                115 -> { // RESET_VARC_INT: g2BE varcId, g4_alt3 value
                    val k = r2(d, 0); val v = r4alt3(d, 2)
                    log("S->C", "         reset_varc[$k] = $v (0x${"%08X".format(v)})")
                }
                60 -> { // RESET_VARC_SMALL: g1(negate) value, g2BE varcId
                    val v = (-(d[0].toInt())).toByte().toInt(); val k = r2(d, 1)
                    log("S->C", "         reset_varc[$k] = $v")
                }
                112 -> { // RESET_ALL_VARPS: no payload
                    log("S->C", "         (reset all varps + varcs)")
                }
                // ---- Interface packets ----
                // Opcode 38 is IF_OPENSUB (23B): opens sub-interfaces on parent components
                // [4B unk] [4B g4_alt2 parentHash] [8B unk] [1B flag(0x7F)] [2B g2LE subInterfaceId] [1B unk] [3B suffix]
                38 -> {
                    if (size >= 20) {
                        val parentHash = r4alt2(d, 4)
                        val parentIf = (parentHash ushr 16) and 0xFFFF; val parentComp = parentHash and 0xFFFF
                        val subIfId = r2le(d, 17)
                        log("S->C", "         parent=$parentIf:$parentComp sub=$subIfId")
                    }
                }
                // Opcode 126 is IF_OPENTOP (19B): opens top-level interface
                // Empirically: interface ID 906 at offset 12 as LE u16
                126 -> {
                    if (size >= 14) {
                        val ifId = d[12].toInt() and 0xFF or ((d[13].toInt() and 0xFF) shl 8)
                        log("S->C", "         topInterface=$ifId")
                    } else {
                        logHex("S->C", d, 0, size, MAX_HEX_DUMP_BYTES_POSTLOGIN)
                    }
                }
                // IF_SETEVENTS (59, 12B): decode and print copy-pasteable builder
                59 -> {
                    if (size >= 12) {
                        log("S->C", "         ${IFEvents.fromWire(d)}")
                    }
                }
                // Remaining interface packets — show raw hex
                4, 202, 207 -> {
                    logHex("S->C", d, 0, size, MAX_HEX_DUMP_BYTES_POSTLOGIN)
                }
                // IF_OPENSUB/CLOSESUB use DBFilter dispatch — complex routing, show raw hex
                182, 180 -> {
                    logHex("S->C", d, 0, size, MAX_HEX_DUMP_BYTES_POSTLOGIN)
                }
                36 -> { // CHANGE_LOBBY: varShort — lobby news/banner entries
                    prettyPrintChangeLobby(d, size)
                }
                150 -> { // WORLDLIST_FETCH_REPLY: varShort
                    prettyPrintWorldList(d, size)
                }
                0 -> { // SET_UID: 24B identity + 4B CRC32
                    if (size >= 28) {
                        val uid = d.copyOfRange(0, 24).joinToString("") { "%02X".format(it) }
                        val crc = r4(d, 24)
                        log("S->C", "         uid=$uid crc=0x${"%08X".format(crc)}")
                    }
                }
                27 -> { // SET_RUN_ENERGY: g1 unsigned byte
                    val energy = r1(d, 0)
                    log("S->C", "         energy=$energy")
                }
                35 -> { // SET_READY_FLAG: no payload
                    log("S->C", "         (ready)")
                }
                146 -> { // NOOP: no payload
                    log("S->C", "         (keepalive)")
                }
                17 -> { // UPDATE_IGNORELIST: var_short, complex bitmask format
                    if (size == 0) {
                        log("S->C", "         (empty)")
                    } else {
                        logHex("S->C", d, 0, size, MAX_HEX_DUMP_BYTES_POSTLOGIN)
                    }
                }
                18 -> { // UPDATE_FRIENDLIST: var_short, loop of entries
                    if (size == 0) {
                        log("S->C", "         (empty)")
                    } else {
                        logHex("S->C", d, 0, size, MAX_HEX_DUMP_BYTES_POSTLOGIN)
                    }
                }
                // Interface packets — awaiting RE from Ghidra agent
                207, 182, 180, 202, 4 -> {
                    logHex("S->C", d, 0, size, MAX_HEX_DUMP_BYTES_POSTLOGIN)
                }
                else -> return false
            }
            return true
        } catch (e: Exception) {
            log("S->C", "         [parse error: ${e.message}]")
            logHex("S->C", d, 0, size, MAX_HEX_DUMP_BYTES_POSTLOGIN)
            return true
        }
    }

    /** Parse CHANGE_LOBBY (opcode 36) — lobby news entries */
    /**
     * Parse CHANGE_LOBBY (opcode 36) — RE-verified from rs2client.
     * Format: gStringCP1252 formatString, then fields in REVERSE order per format chars,
     * then gSmart scriptId at the end.
     * Chars: 'i'=g4BE int, 's'=gStringCP1252, 'l'=g8BE long
     */
    private fun prettyPrintChangeLobby(d: ByteArray, size: Int) {
        if (size < 2) { logHex("S->C", d, 0, size, MAX_HEX_DUMP_BYTES_POSTLOGIN); return }
        var p = 0
        fun gStr(): String {
            val start = p; while (p < size && d[p] != 0.toByte()) p++
            val s = String(d, start, p - start, Charsets.ISO_8859_1); if (p < size) p++; return s
        }
        fun g4(): Int { val v = r4(d, p); p += 4; return v }
        fun g8(): Long { val v = r8(d, p); p += 8; return v }

        try {
            val fmt = gStr()
            if (fmt.isEmpty() || !fmt.all { it == 'i' || it == 's' || it == 'l' }) {
                logHex("S->C", d, 0, size, MAX_HEX_DUMP_BYTES_POSTLOGIN); return
            }
            // Read fields in REVERSE order of format string
            val fields = arrayOfNulls<Any>(fmt.length)
            for (i in fmt.lastIndex downTo 0) {
                if (p >= size) break
                fields[i] = when (fmt[i]) {
                    'i' -> if (p + 4 <= size) g4() else break
                    's' -> gStr()
                    'l' -> if (p + 8 <= size) g8() else break
                    else -> break
                }
            }
            // Identify lobby news: format "iiiisssssi" → ints at 0-3, strings at 4-7, int at 8
            val strings = fields.filterIsInstance<String>()
            val ints = fields.filterIsInstance<Int>()
            if (strings.size >= 4) {
                log("S->C", "         [lobby-news] date=${strings[0]} slug=${strings[1]}")
                log("S->C", "           title=\"${strings[3]}\"")
                log("S->C", "           desc=\"${strings[2].take(80)}${if (strings[2].length > 80) "..." else ""}\"")
                if (ints.isNotEmpty()) log("S->C", "           ints=${ints.joinToString(",")}")
            } else {
                log("S->C", "         [lobby-entry] fmt=$fmt fields=${fields.filterNotNull().joinToString(", ")}")
            }
        } catch (e: Exception) {
            logHex("S->C", d, 0, size, MAX_HEX_DUMP_BYTES_POSTLOGIN)
        }
    }

    /**
     * Parse WORLDLIST_FETCH_REPLY (opcode 150) — RE-verified from rs2client.
     *
     * Uses a buffering protocol:
     *   updateType=1: append remaining bytes to internal buffer (continuation)
     *   updateType=2: buffer complete, sub-dispatch byte 0x01 = full world list decode
     *   Other: player count delta only
     *
     * Since we see the full TCP stream, we just concatenate all continuation
     * packets and parse the combined buffer when updateType=2.
     */
    private val worldlistBuffer = java.io.ByteArrayOutputStream()

    private fun prettyPrintWorldList(d: ByteArray, size: Int) {
        if (size < 1) return
        val frameType = d[0].toInt() and 0xFF

        when (frameType) {
            0x00 -> {
                // Buffer continuation — append remaining bytes
                worldlistBuffer.write(d, 1, size - 1)
                log("S->C", "         [worldlist] buffer continuation +${size - 1}B (total ${worldlistBuffer.size()}B)")
            }
            0x01 -> {
                if (worldlistBuffer.size() > 0) {
                    // Last segment — append and parse the complete buffer
                    worldlistBuffer.write(d, 1, size - 1)
                    val buf = worldlistBuffer.toByteArray()
                    worldlistBuffer.reset()
                    log("S->C", "         [worldlist] buffer complete ${buf.size}B, decoding...")
                    parseWorldListBuffer(buf)
                } else {
                    // Standalone delta: [1B subType] [4B CRC] [smart+g2 player count pairs]
                    if (size >= 6) {
                        val subType = r1(d, 1)
                        val crc = r4(d, 2)
                        log("S->C", "         [worldlist] player count delta subType=$subType crc=0x${"%08X".format(crc)}")
                        parseWorldListPlayerCounts(d, 6, size)
                    }
                }
            }
            else -> {
                log("S->C", "         [worldlist] unknown frameType=$frameType")
                logHex("S->C", d, 0, size, MAX_HEX_DUMP_BYTES_POSTLOGIN)
            }
        }
    }

    /**
     * Parse the combined worldlist buffer. RE-verified format from rs2client handler at 0x0022f710.
     *
     * updateType bitmask: bit0=has player counts, bit1=has full world list.
     * World entries: smart worldIdDelta, g1 countryIndex, g4BE flags,
     *   smart countryOverrideFlag (if nonzero: gjStr2 countryNameOverride),
     *   gjStr2 activity, gjStr2 hostname.
     * World IDs are deltas from minWorldId.
     */
    private fun parseWorldListBuffer(d: ByteArray) {
        val size = d.size
        if (size < 4) return
        var p = 0
        fun g1() = d[p++].toInt() and 0xFF
        fun g2(): Int { val v = ((d[p].toInt() and 0xFF) shl 8) or (d[p+1].toInt() and 0xFF); p += 2; return v }
        fun g4(): Int { val v = ((d[p].toInt() and 0xFF) shl 24) or ((d[p+1].toInt() and 0xFF) shl 16) or ((d[p+2].toInt() and 0xFF) shl 8) or (d[p+3].toInt() and 0xFF); p += 4; return v }
        fun gSmart(): Int { val b = d[p].toInt() and 0xFF; return if (b < 128) { p++; b } else { g2() - 0x8000 } }
        fun gStr(): String { val s = p; while (p < size && d[p] != 0.toByte()) p++; val r = String(d, s, p - s, Charsets.ISO_8859_1); if (p < size) p++; return r }
        fun gjStr2(): String { val ver = g1(); if (ver != 0) return ""; return gStr() }

        try {
            val updateType = g1()
            val hasWorldList = (updateType and 2) != 0
            val hasPlayerCounts = (updateType and 1) != 0
            log("S->C", "         [worldlist] updateType=$updateType (worlds=$hasWorldList, counts=$hasPlayerCounts)")

            if (hasWorldList) {
                val hasCountries = g1()
                if (hasCountries != 0) {
                    val countryCount = gSmart()
                    log("S->C", "         [worldlist] $countryCount countries:")
                    for (i in 0 until countryCount) {
                        val flag = gSmart()
                        val name = gjStr2()
                        log("S->C", "           country[$i] flag=$flag name=\"$name\"")
                    }
                }

                val worldMin = gSmart()
                val worldMax = gSmart()
                val worldCount = gSmart()
                log("S->C", "         [worldlist] $worldCount worlds (range $worldMin-$worldMax):")
                for (i in 0 until worldCount) {
                    if (p + 6 > size) break
                    val worldIdDelta = gSmart()
                    val absWorldId = worldMin + worldIdDelta
                    val countryIdx = g1()
                    val flags = g4()
                    val countryOverride = gSmart()
                    val countryName = if (countryOverride != 0) gjStr2() else ""
                    val activity = gjStr2()
                    val hostname = gjStr2()
                    worldHostnames[absWorldId] = hostname
                    val extra = if (countryName.isNotEmpty()) " country=\"$countryName\"" else ""
                    log("S->C", "           world[$absWorldId] loc=$countryIdx flags=0x${"%08X".format(flags)} addr=\"$hostname\" activity=\"$activity\"$extra")
                }
            }

            if (hasPlayerCounts) {
                if (p + 4 <= size) {
                    val crc = g4()
                    log("S->C", "         [worldlist] crc=0x${"%08X".format(crc)}")
                }
                parseWorldListPlayerCounts(d, p, size)
            }
        } catch (e: Exception) {
            log("S->C", "         [worldlist parse error at byte $p/${size}: ${e.message}]")
        }
    }

    private fun parseWorldListPlayerCounts(d: ByteArray, startPos: Int, size: Int) {
        var p = startPos
        fun g2(): Int { val v = ((d[p].toInt() and 0xFF) shl 8) or (d[p+1].toInt() and 0xFF); p += 2; return v }
        fun gSmart(): Int { val b = d[p].toInt() and 0xFF; return if (b < 128) { p++; b } else { g2() - 0x8000 } }
        try {
            var count = 0
            while (p + 3 <= size) {
                val wId = gSmart()
                val players = g2()
                if (count < 10) log("S->C", "           world[$wId] players=$players")
                count++
            }
            if (count > 10) log("S->C", "           ... and ${count - 10} more worlds")
        } catch (_: Exception) {}
    }

    /** Pretty-print a known client packet. Returns true if handled. */
    private fun prettyPrintClientPkt(opcode: Int, d: ByteArray, size: Int): Boolean {
        try {
            when (opcode) {
                110 -> { // WORLDLIST_FETCH: 4B crc
                    if (size >= 4) {
                        val crc = r4(d, 0)
                        log("C->S", "         crc=0x${"%08X".format(crc)}${if (crc == -1) " (full request)" else " (delta)"}")
                    }
                }
                97 -> { // IF_BUTTON1: 8B — [2B slot][2B itemId][4B hash_alt1]
                    if (size >= 8) {
                        val slot = r2(d, 0)
                        val itemId = r2(d, 2)
                        val hash = r4alt1(d, 4)
                        val ifId = (hash ushr 16) and 0xFFFF
                        val comp = hash and 0xFFFF
                        log("C->S", "         if=$ifId comp=$comp slot=${if (slot == 0xFFFF) -1 else slot} itemId=${if (itemId == 0xFFFF) -1 else itemId}")
                    }
                }
                106 -> { // DISPLAY_INFO: 6B
                    if (size >= 6) {
                        val type = r1(d, 0); val platform = r1(d, 1)
                        val width = r2(d, 2); val height = r2(d, 4)
                        log("C->S", "         type=$type platform=$platform size=${width}x${height}")
                    }
                }
                80 -> { // NO_TIMEOUT_2: 0B
                    log("C->S", "         (keepalive)")
                }
                50 -> { // SCENE_GRAPH_REPORT: 4B
                    if (size >= 4) {
                        val value = r4(d, 0)
                        log("C->S", "         value=0x${"%08X".format(value)}")
                    }
                }
                else -> return false
            }
            return true
        } catch (e: Exception) {
            log("C->S", "         [parse error: ${e.message}]")
            return true
        }
    }

    // ---- Packet read helpers (RE-verified byte transforms from rs2client) ----
    private fun r1(d: ByteArray, off: Int) = d[off].toInt() and 0xFF
    // Standard big-endian u16
    private fun r2(d: ByteArray, off: Int) = ((d[off].toInt() and 0xFF) shl 8) or (d[off+1].toInt() and 0xFF)
    // Little-endian u16
    private fun r2le(d: ByteArray, off: Int) = ((d[off+1].toInt() and 0xFF) shl 8) or (d[off].toInt() and 0xFF)
    // Big-endian u16 with low byte subtract-128 transform
    private fun r2sub128(d: ByteArray, off: Int) = ((d[off].toInt() and 0xFF) shl 8) or ((d[off+1].toInt() - 128) and 0xFF)
    // Standard big-endian i32
    private fun r4(d: ByteArray, off: Int) = ((d[off].toInt() and 0xFF) shl 24) or ((d[off+1].toInt() and 0xFF) shl 16) or ((d[off+2].toInt() and 0xFF) shl 8) or (d[off+3].toInt() and 0xFF)
    // Alt1: b[0]<<8 + b[1] + b[2]<<24 + b[3]<<16 (swap 16-bit halves)
    private fun r4alt1(d: ByteArray, off: Int) = ((d[off].toInt() and 0xFF) shl 8) or (d[off+1].toInt() and 0xFF) or ((d[off+2].toInt() and 0xFF) shl 24) or ((d[off+3].toInt() and 0xFF) shl 16)
    // Alt2: little-endian i32
    private fun r4alt2(d: ByteArray, off: Int) = (d[off].toInt() and 0xFF) or ((d[off+1].toInt() and 0xFF) shl 8) or ((d[off+2].toInt() and 0xFF) shl 16) or ((d[off+3].toInt() and 0xFF) shl 24)
    // Alt3: b[1]<<24 + b[0]<<16 + b[3]<<8 + b[2]
    private fun r4alt3(d: ByteArray, off: Int) = ((d[off+1].toInt() and 0xFF) shl 24) or ((d[off].toInt() and 0xFF) shl 16) or ((d[off+3].toInt() and 0xFF) shl 8) or (d[off+2].toInt() and 0xFF)
    // Standard big-endian i64
    private fun r8(d: ByteArray, off: Int): Long { val hi = r4(d, off).toLong() and 0xFFFFFFFFL; val lo = r4(d, off+4).toLong() and 0xFFFFFFFFL; return (hi shl 32) or lo }

    // ---- Lobby login data field-level parsing (best-effort, no crypto) ----

    /**
     * Parse lobby login data — exact format from jag::LoginManager::LoginStepHandleLoginData
     * (verified from rs2client rev 947 via Ghidra RE).
     */
    private fun parseLobbyLoginData(data: ByteArray, len: Int) {
        if (len < 10) {
            log("S->C", "  [login-data] Too short ($len bytes)")
            return
        }
        try {
            var pos = 0
            fun g1(): Int { val v = data[pos].toInt() and 0xFF; pos++; return v }
            fun g1s(): Int { val v = data[pos].toInt(); pos++; return v }
            fun g2(): Int { val v = ((data[pos].toInt() and 0xFF) shl 8) or (data[pos+1].toInt() and 0xFF); pos += 2; return v }
            fun g3s(): Int { val v = ((data[pos].toInt() and 0xFF) shl 16) or ((data[pos+1].toInt() and 0xFF) shl 8) or (data[pos+2].toInt() and 0xFF); pos += 3; return if (v > 0x7FFFFF) v - 0x1000000 else v }
            fun g4(): Int { val v = ((data[pos].toInt() and 0xFF) shl 24) or ((data[pos+1].toInt() and 0xFF) shl 16) or ((data[pos+2].toInt() and 0xFF) shl 8) or (data[pos+3].toInt() and 0xFF); pos += 4; return v }
            fun g8(): Long { val hi = g4().toLong() and 0xFFFFFFFFL; val lo = g4().toLong() and 0xFFFFFFFFL; return (hi shl 32) or lo }
            fun gStr(): String {
                val start = pos
                while (pos < len && data[pos] != 0.toByte()) pos++
                val s = String(data, start, pos - start, Charsets.ISO_8859_1)
                if (pos < len) pos++
                return s
            }
            fun gjStr(): String { val ver = g1(); if (ver != 0) return ""; return gStr() }

            val hasTotpUpdate = g1()
            log("S->C", "  [login-data] hasTotpUpdate=$hasTotpUpdate")
            if (hasTotpUpdate == 1) {
                log("S->C", "  [login-data] TOTP/ISAAC re-seed present — skipping rest")
                return
            }

            val membershipType = g1()
            val membershipDays = g1()
            val emailValidated = g1()
            val recoveryDelay = g3s()
            log("S->C", "  [login-data] membershipType=$membershipType membershipDays=$membershipDays emailValidated=$emailValidated recoveryDelay=$recoveryDelay")

            val staffModLevel = g1s()
            val unknownFlag1 = g1()
            val unknownFlag2 = g1()
            log("S->C", "  [login-data] staffModLevel=$staffModLevel flag1=$unknownFlag1 flag2=$unknownFlag2")

            val membershipTs = g8()
            log("S->C", "  [login-data] membershipTimestamp=$membershipTs (${java.time.Instant.ofEpochMilli(membershipTs)})")

            val timeDaysByte = g1()
            val timeMillisInt = g4()
            log("S->C", "  [login-data] timeDaysByte=$timeDaysByte timeMillisInt=$timeMillisInt")

            val flags = g1()
            log("S->C", "  [login-data] flags=0x${"%02X".format(flags)} (quickChatOnly=${flags and 1}, bit1=${(flags shr 1) and 1})")

            val lastLoginIP = g4()
            val lastLoginDays = g4()
            log("S->C", "  [login-data] lastLoginIP=0x${"%08X".format(lastLoginIP)} lastLoginDays=$lastLoginDays")

            val playerIndex = g2()
            log("S->C", "  [login-data] playerIndex=$playerIndex")

            val unk3 = g2()
            val unk4 = g2()
            val unk5 = g4()
            val unk6 = g1()
            val unk7 = g2()
            val unk8 = g2()
            log("S->C", "  [login-data] unk3=$unk3 unk4=$unk4 unk5=0x${"%08X".format(unk5)} unk6=$unk6 unk7=$unk7 unk8=$unk8")

            val isMembersWorld = g1()
            log("S->C", "  [login-data] isMembersWorld=$isMembersWorld")

            val displayName = gjStr()
            log("S->C", "  [login-data] displayName=\"$displayName\"")

            val unk9 = g1()
            val unk10 = g4()
            val worldId = g2()
            log("S->C", "  [login-data] unk9=$unk9 unk10=0x${"%08X".format(unk10)} worldId=${if (worldId == 0xFFFF) -1 else worldId}")

            val serverHostname = gjStr()
            log("S->C", "  [login-data] serverHostname=\"$serverHostname\"")

            val gamePort = g2()
            val httpsPort = g2()
            log("S->C", "  [login-data] gamePort=$gamePort httpsPort=$httpsPort")

            val sessionToken1 = g8()
            val sessionToken2 = g8()
            log("S->C", "  [login-data] sessionToken1=0x${"%016X".format(sessionToken1)}")
            log("S->C", "  [login-data] sessionToken2=0x${"%016X".format(sessionToken2)}")

            if (pos < len) {
                log("S->C", "  [login-data] ${len - pos} unparsed bytes at offset $pos")
                logHex("S->C", data, pos, len - pos)
            }
        } catch (e: Exception) {
            log("S->C", "  [login-data] Parse error: ${e::class.simpleName}: ${e.message}")
        }
    }

    // ---- Logging ----

    private fun log(direction: String, message: String) {
        val elapsed = (System.nanoTime() - startNanos) / 1_000_000_000.0
        val ts = "%07.3f".format(elapsed)
        val line = "[$ts] [$direction] $message"

        synchronized(logWriter) {
            logWriter.println(line)
        }
        println("[s$sessionId] $line")
    }

    private fun logHex(direction: String, data: ByteArray, offset: Int, length: Int, maxBytes: Int = MAX_HEX_DUMP_BYTES_DEFAULT) {
        val end = minOf(offset + length, offset + maxBytes, data.size)
        val sb = StringBuilder()
        for (i in offset until end) {
            if (i > offset) sb.append(' ')
            sb.append("%02X".format(data[i]))
        }
        if (length > maxBytes) {
            sb.append(" ... (${length} bytes total)")
        }
        val hexLine = "         hex: $sb"
        synchronized(logWriter) {
            logWriter.println(hexLine)
        }
        println("[s$sessionId] $hexLine")
    }

    /**
     * Rewrite the buffered login response to replace the world hostname with "localhost".
     * The buffer contains everything from the LOGIN_RESULT byte onwards:
     *   [1B result=2][1B dataLen][NB loginData]...
     * The loginData contains a null-terminated hostname string. We find it by searching
     * for ".runescape.com\0" and replace the entire hostname with "localhost\0".
     */
    private fun rewriteLoginResponseHostname(data: ByteArray): ByteArray {
        // Find ".runescape.com\0" pattern
        val marker = ".runescape.com\u0000".toByteArray(Charsets.ISO_8859_1)
        var markerPos = -1
        outer@ for (i in 0 until data.size - marker.size) {
            for (j in marker.indices) {
                if (data[i + j] != marker[j]) continue@outer
            }
            markerPos = i
            break
        }

        if (markerPos < 0) {
            log("CTRL", "WARNING: Could not find .runescape.com in login response, forwarding unmodified")
            return data
        }

        // Walk backwards from markerPos to find the start of the hostname string
        var hostnameStart = markerPos
        while (hostnameStart > 0 && data[hostnameStart - 1] != 0.toByte()) {
            hostnameStart--
        }

        val hostnameEnd = markerPos + marker.size // includes the null terminator
        val oldHostname = String(data, hostnameStart, hostnameEnd - hostnameStart - 1, Charsets.ISO_8859_1)
        log("CTRL", "Rewriting hostname: \"$oldHostname\" → \"localhost\"")

        val replacement = "localhost\u0000".toByteArray(Charsets.ISO_8859_1)
        val out = ByteArrayOutputStream(data.size)
        out.write(data, 0, hostnameStart)
        out.write(replacement)
        out.write(data, hostnameEnd, data.size - hostnameEnd)

        val result = out.toByteArray()

        // Adjust the login data length byte (second byte in the buffer = result[1])
        // The length changed by (replacement.size - (hostnameEnd - hostnameStart))
        val delta = replacement.size - (hostnameEnd - hostnameStart)
        if (result.size >= 2) {
            val oldLen = result[1].toInt() and 0xFF
            val newLen = oldLen + delta
            result[1] = (newLen and 0xFF).toByte()
            log("CTRL", "Adjusted login data length: $oldLen → $newLen (delta=$delta)")
        }

        return result
    }

    private fun closeLog() {
        logWriter.println()
        logWriter.println("# Session ended: ${LocalDateTime.now()}")
        logWriter.close()
        c2sRaw.close()
        s2cRaw.close()
    }
}

// ---- jav_config.ws fetcher ----

/**
 * Start a lightweight HTTP server that serves the patched jav_config.ws.
 * Patches param=3 (lobby host) to localhost and param=41..48 (ports) to [proxyPort].
 * The launcher uses --configURI http://localhost:[httpPort]/jav_config.ws to pick this up.
 */
private fun startConfigHttpServer(httpPort: Int, rawConfig: String, proxyPort: Int) {
    if (rawConfig.isEmpty()) {
        println("[proxy] WARNING: No jav_config fetched, HTTP config server not started")
        return
    }

    // Build the patched config once (cached for all requests)
    val patchedConfig = buildPatchedConfig(rawConfig, proxyPort)

    val httpServer = HttpServer.create(InetSocketAddress(httpPort), 0)
    httpServer.createContext("/") { exchange ->
        // Accept any path containing jav_config.ws, or just serve on any GET
        if (exchange.requestMethod == "GET") {
            val responseBytes = patchedConfig.toByteArray(Charsets.ISO_8859_1)
            exchange.responseHeaders.add("Content-Type", "text/plain; charset=ISO-8859-1")
            exchange.sendResponseHeaders(200, responseBytes.size.toLong())
            exchange.responseBody.use { it.write(responseBytes) }
        } else {
            exchange.sendResponseHeaders(405, -1)
        }
    }
    httpServer.executor = null // use default executor
    httpServer.start()

    println("[proxy] Config proxy HTTP: http://localhost:$httpPort/jav_config.ws")
    println("[proxy] Use --configURI http://localhost:$httpPort/jav_config.ws")
}

/**
 * Patch the raw jav_config text:
 * - param=3 (lobby host) -> localhost
 * - param=41..48 (ports) -> [proxyPort]
 */
private fun buildPatchedConfig(rawConfig: String, proxyPort: Int): String {
    val lines = rawConfig.lines().map { line ->
        if (!line.startsWith("param=")) return@map line
        val rest = line.removePrefix("param=")
        val eqIdx = rest.indexOf('=')
        if (eqIdx < 0) return@map line
        val key = rest.substring(0, eqIdx)
        when (key) {
            "3" -> "param=3=localhost"
            "41", "42", "43", "44", "45", "46", "47", "48" -> "param=$key=$proxyPort"
            else -> line
        }
    }
    // Append param=99 with our RSA modulus hex so the launcher can extract it
    val result = lines.toMutableList()
    result.add("param=99=${org.darkan.core.EnvVars.loginRsaModulusHex}")
    return result.joinToString("\n")
}

private data class JagexServerInfo(val host: String, val port: Int, val rawConfig: String)

private fun resolveJagexServer(): JagexServerInfo {
    try {
        val url = URI(JAV_CONFIG_URL).toURL()
        val conn = url.openConnection() as HttpURLConnection
        conn.connectTimeout = 10_000
        conn.readTimeout = 10_000
        conn.setRequestProperty("User-Agent", "Mozilla/5.0")

        val rawConfig = conn.inputStream.bufferedReader(Charsets.ISO_8859_1).readText()
        conn.disconnect()

        val lines = rawConfig.lines()
        var lobbyHost: String? = null
        var port: Int? = null

        for (line in lines) {
            when {
                line.startsWith("param=3=") -> lobbyHost = line.removePrefix("param=3=").trim()
                line.startsWith("param=41=") -> port = line.removePrefix("param=41=").trim().toIntOrNull()
            }
        }

        val host = lobbyHost ?: FALLBACK_LOBBY_HOST
        val p = port ?: FALLBACK_PORT

        println("[proxy] Parsed jav_config: lobby=$host, port=$p")
        println("[proxy] Relevant jav_config params:")
        for (l in lines) {
            if (l.startsWith("param=3=") || l.startsWith("param=41=") || l.startsWith("param=42=")
                || l.startsWith("param=43=") || l.startsWith("param=44=") || l.startsWith("param=45=")
                || l.startsWith("param=46=") || l.startsWith("param=47=") || l.startsWith("param=48=")
                || l.startsWith("server_version=") || l.startsWith("param=29=") || l.startsWith("param=10=")
            ) {
                println("[proxy]   $l")
            }
        }

        return JagexServerInfo(host, p, rawConfig)
    } catch (e: Exception) {
        println("[proxy] WARNING: Failed to fetch jav_config: ${e.message}")
        println("[proxy] Using fallback: $FALLBACK_LOBBY_HOST:$FALLBACK_PORT")
        return JagexServerInfo(FALLBACK_LOBBY_HOST, FALLBACK_PORT, "")
    }
}

private fun findArg(args: Array<String>, name: String): String? {
    val idx = args.indexOf(name)
    return if (idx >= 0 && idx + 1 < args.size) args[idx + 1] else null
}
