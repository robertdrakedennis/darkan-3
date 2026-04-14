package org.darkan.lobby.server

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import org.darkan.core.EnvVars
import org.darkan.core.Logger.logFinest
import org.darkan.core.Logger.logInfo
import org.darkan.core.Logger.logTrace
import org.darkan.core.Logger.logWarn
import org.darkan.lobby.social.SocialGateway
import world.gregs.voidps.cache.file.FileProvider
import world.gregs.voidps.cache.secure.Whirlpool
import java.math.BigInteger
import java.util.zip.CRC32
import kotlin.time.Duration.Companion.seconds

class ConfigServer(private val fileProvider: FileProvider? = null) {
    private lateinit var server: EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration>

    private val binaryCrc: Long = computeBinaryCrc(EnvVars.clientBinaryPath)
    private val binaryHash: String = computeBinaryHash(EnvVars.clientBinaryPath)
    private val binarySize: Long = java.io.File(EnvVars.clientBinaryPath).let { if (it.exists()) it.length() else 0L }

    fun start() {
        logInfo("Client binary CRC32: $binaryCrc (path: ${EnvVars.clientBinaryPath})")
        server = embeddedServer(Netty, port = EnvVars.configHttpPort) {
            install(WebSockets) {
                pingPeriod = 15.seconds
                timeout = 30.seconds
                maxFrameSize = Long.MAX_VALUE
            }
            routing {
                // Social gateway WebSocket endpoint — world servers connect here
                webSocket("/social/ws") {
                    logInfo("World server WebSocket connected from ${call.request.local.remoteHost}")
                    SocialGateway.handleWorldSocket(this)
                }
                get("/ms") {
                    logFinest("JS5 HTTP request: ${call.request.local.uri}")
                    serveJs5Http(call)
                }
                post("/nxtclienterror.ws") {
                    val body = call.receiveText()
                    logWarn("=== NXT CLIENT CRASH REPORT ===")
                    // Parse URL-encoded form data
                    body.split("&").forEach { param ->
                        val parts = param.split("=", limit = 2)
                        if (parts.size == 2) {
                            val key = java.net.URLDecoder.decode(parts[0], "UTF-8")
                            val value = java.net.URLDecoder.decode(parts[1], "UTF-8")
                            logWarn("  $key = $value")
                        }
                    }
                    logWarn("=== END CRASH REPORT ===")
                    call.respondText("OK", ContentType.Text.Plain)
                }
                // Stub OAuth endpoints — the NXT client validates tokens against these
                post("/shield/oauth/check_token") {
                    logInfo("OAuth check_token request from ${call.request.local.remoteHost}")
                    call.respondText(
                        """{"valid":true,"token_type":"bearer","scope":"openid","expires_in":3600,"sub":"darkan-player","session_id":"darkan-session"}""",
                        ContentType.Application.Json
                    )
                }
                post("/game-session/v1/tokens") {
                    logInfo("Game session token request from ${call.request.local.remoteHost}")
                    call.respondText(
                        """{"sessionId":"darkan-session","token":"darkan-game-token","expires":${System.currentTimeMillis() / 1000 + 3600}}""",
                        ContentType.Application.Json
                    )
                }
                // Catch-all for any other OAuth/auth endpoints the client may hit
                post("{...}") {
                    val path = call.request.local.uri
                    logInfo("Unhandled POST: $path")
                    val body = call.receiveText()
                    logTrace("POST body: ${body.take(500)}")
                    call.respondText("{}", ContentType.Application.Json)
                }
                get("{...}") {
                    val path = call.request.local.uri
                    logInfo("HTTP request: ${call.request.local.method.value} $path")
                    if (path.contains("jav_config.ws")) {
                        call.respondText(
                            generateJavConfig(),
                            ContentType.Text.Plain.withCharset(Charsets.ISO_8859_1)
                        )
                    } else if (path.contains("rs2client")) {
                        // Serve the client binary so rs3linux can download it
                        val file = java.io.File(EnvVars.clientBinaryPath)
                        if (file.exists()) {
                            logInfo("Serving client binary: ${file.absolutePath} (${file.length()} bytes)")
                            call.respondFile(file)
                        } else {
                            call.respondText("Client binary not found", ContentType.Text.Plain, HttpStatusCode.NotFound)
                        }
                    } else {
                        call.respondText("Not Found", ContentType.Text.Plain, HttpStatusCode.NotFound)
                    }
                }
            }
        }
        server.start(wait = false)
        logInfo("Config HTTP server started on port ${EnvVars.configHttpPort}")
    }

    fun stop() {
        if (::server.isInitialized) {
            server.stop(1000, 2000)
        }
    }

    private suspend fun serveJs5Http(call: ApplicationCall) {
        val provider = fileProvider
        if (provider == null) {
            call.respondText("JS5 HTTP not configured", ContentType.Text.Plain, HttpStatusCode.ServiceUnavailable)
            return
        }
        val archive = call.request.queryParameters["a"]?.toIntOrNull()
        val group = call.request.queryParameters["g"]?.toIntOrNull()
        if (archive == null || group == null) {
            call.respondText("Missing parameters", ContentType.Text.Plain, HttpStatusCode.BadRequest)
            return
        }
        val data = provider.data(archive, group)
        if (data == null) {
            call.respondText("Not found", ContentType.Text.Plain, HttpStatusCode.NotFound)
            return
        }
        // HTTP JS5 responses must end with a 2-byte big-endian version suffix.
        // The client computes CRC over (responseBody.length - 2) and verifies against the archive index.
        //
        // Some archives were downloaded via HTTP from Jagex, so the stored blob already
        // contains the 2-byte suffix. Detect this by checking blob size vs container header.
        val version = call.request.queryParameters["v"]?.toIntOrNull() ?: 0
        val alreadyHasSuffix = blobHasVersionSuffix(data)
        val response = if (alreadyHasSuffix) {
            data // serve as-is — suffix already present
        } else {
            val buf = ByteArray(data.size + 2)
            System.arraycopy(data, 0, buf, 0, data.size)
            buf[data.size] = ((version shr 8) and 0xFF).toByte()
            buf[data.size + 1] = (version and 0xFF).toByte()
            buf
        }
        logFinest("JS5 HTTP: a=$archive g=$group v=$version -> ${response.size} bytes (container=${data.size}, suffix=${if (alreadyHasSuffix) "stored" else "appended"})")
        call.respondBytes(response, ContentType.Application.OctetStream)
    }

    private fun generateJavConfig(): String = buildString {
        val host = "localhost"
        val port = EnvVars.lobbyPort

        fun line(s: String) { append(s); append("\n") }

        // General config
        line("title=${EnvVars.serverName}")
        line("adverturl=")
        line("codebase=http://$host:${EnvVars.configHttpPort}/")
        line("binary_name=rs2client")
        line("download_name_0=rs2client")
        line("download_crc_0=$binaryCrc")
        line("download_hash_0=$binaryHash")
        line("binary_count=1")
        line("launcher_version=224")
        line("server_version=${EnvVars.majorVersion}")
        line("launcher_sub_version=1")
        line("cache_variant_suffix=")
        line("termsurl=https://legal.jagex.com/docs/terms")
        line("privacyurl=https://legal.jagex.com/docs/policies")
        line("download=$binarySize")
        line("window_preferredwidth=1024")
        line("window_preferredheight=768")
        line("advert_height=96")
        line("applet_minwidth=765")
        line("applet_minheight=540")
        line("applet_maxwidth=3840")
        line("applet_maxheight=2160")

        // Messages
        line("msg=lang0=English")
        line("msg=lang1=Deutsch")
        line("msg=lang2=Fran${'\u00e7'}ais")
        line("msg=lang3=Portugu${'\u00ea'}s")
        line("msg=progress_downloading_client=Downloading Client...")
        line("msg=nxt_confirm_quit=Are you sure you want to quit?")
        line("msg=nxt_bad_version=Incorrect Launcher version")
        line("msg=nxt_cannot_move_file=Could not move %s")
        line("msg=nxt_cannot_move_file_to_file=Could not move %s to %s")
        line("msg=nxt_retry_move_at_exit=We will try to move it again when you quit")
        line("msg=nxt_moving_files=Moving files to the new location")
        line("msg=nxt_not_enough_space=There is not enough space on the target drive to move the files")
        line("msg=nxt_check_security=Please ensure that access to %s is not blocked by security permissions and is not already open by another instance of RuneScape")
        line("msg=nxt_single_instance_options=You may only change RuneScape Launcher options when a single instance is running")
        line("msg=nxt_graphics_prompt1=The RuneScape client suffered from an error")
        line("msg=nxt_graphics_prompt1_init=The RuneScape client suffered from an error during graphics initialisation")
        line("msg=nxt_graphics_prompt2=This could be due to graphics driver issues and may be improved by switching graphics mode or updating drivers.")
        line("msg=nxt_graphics_prompt3=The graphics drivers on your %s are at version %s, which is %s days old.")
        line("msg=nxt_graphics_prompt4=You are currently using the %s graphics mode. Would you like to switch to %s instead?")
        line("msg=nxt_graphics_prompt5=Alternatively, you may instruct RuneScape to load with default graphics settings the next time it is run.")
        line("msg=nxt_driver_warning_small=Your graphics drivers are %d days old.")
        line("msg=nxt_driver_warning=The graphics drivers on your %s are at version %s, which is %d days old.")
        line("msg=nxt_driver_update=You can update them here.")
        line("msg=nxt_dialog_graphics_mode=Graphics Mode")
        line("msg=nxt_dialog_graphics_auto=Auto (%s)")
        line("msg=nxt_dialog_graphics_normal=Normal Mode")
        line("msg=nxt_dialog_graphics_compat=Compatibility Mode")
        line("msg=nxt_use_default=Use Default Settings")
        line("msg=nxt_never_ask=Do not ask me again")
        line("msg=nxt_button_switch=Switch")
        line("msg=new_version=")
        line("msg=new_version_linktext=")
        line("msg=new_version_link=")
        line("msg=tandc=")
        line("msg=options=Options")
        line("msg=language=Language")
        line("msg=changes_on_restart=Your changes will take effect when you next start this program.")
        line("msg=loading_app_resources=Loading application resources")
        line("msg=loading_app=Loading application")
        line("msg=err_save_file=Error saving file")
        line("msg=err_downloading=Error downloading")
        line("msg=ok=OK")
        line("msg=cancel=Cancel")
        line("msg=message=Message")
        line("msg=information=Information")
        line("msg=err_get_file=Error getting file")

        // Params (numbered parameters passed as CLI args to the client)
        line("param=1=0")
        line("param=2=1101")
        line("param=3=$host")                                    // lobby host
        line("param=4=0")
        line("param=5=0")
        line("param=6=300")                                     // world ID (MAP_WORLD() reads this)
        line("param=7=0")
        line("param=8=false")
        line("param=10=${EnvVars.loginServerToken}")              // login server token
        line("param=11=225")
        line("param=13=false")
        line("param=14=false")
        line("param=15=")
        line("param=16=.$host")
        line("param=17=false")
        line("param=18=-1187320092")
        line("param=19=")
        line("param=20=false")
        line("param=21=halign=true|valign=true|image=rs_logo.gif,0,-43|rotatingimage=rs3_loading_spinner.gif,0,47,9.6|progress=true,Verdana,13,0xFFFFFF,0,51")
        line("param=22=")
        line("param=23=false")
        line("param=24=true")
        line("param=25=0")                                    // ModeWhere=LIVE (HTTP port patched by LD_PRELOAD)
        line("param=26=false")
        line("param=27=3")
        line("param=28=265964763")
        line("param=29=${EnvVars.js5ServerToken}")                // JS5 server token
        line("param=31=19435")
        line("param=32=")
        line("param=33=")
        line("param=34=547933620")
        line("param=35=http://$host:${EnvVars.configHttpPort}")   // world server URL
        line("param=36=")
        line("param=37=$host")                                    // content server host
        line("param=38=1200")
        line("param=39=1829")                                    // lobby worldId (HTTP port = 1829+7000 = 8829)
        line("param=40=http://$host:${EnvVars.configHttpPort}")   // world server URL
        line("param=41=$port")                                    // game port 1
        line("param=42=$port")                                    // game port 2 (was SSL 443)
        line("param=43=$port")                                    // game port 3
        line("param=44=$port")                                    // game port 4 (was SSL 443)
        line("param=45=$port")                                    // game port 5
        line("param=46=$port")                                    // game port 6 (was SSL 443)
        line("param=47=$port")                                    // game port 7
        line("param=48=$port")                                    // game port 8 (was SSL 443)
        line("param=49=$host")                                    // content server host
        line("param=50=0")
        line("param=51=0")
        line("param=52=0")
        line("param=53=http://$host:${EnvVars.configHttpPort}/")   // auth base URL (was auth.jagex.com)
        line("param=54=https://payments.jagex.com/")
        line("param=55=")
        line("param=56=http://$host:${EnvVars.configHttpPort}/")   // social auth URL (was social.auth.jagex.com)
        line("param=57=6124")
        line("param=58=https://account.jagex.com/")
        line("param=59=http://$host:${EnvVars.configHttpPort}/")   // auth RS URL (was auth.runescape.com)
        line("param=60=0")
        line("param=99=${EnvVars.loginRsaModulusHex}")  // hex RSA modulus for client patcher
    }

    companion object {
        /**
         * Check if a cached JS5 blob already contains a 2-byte version suffix.
         *
         * Container format: [1B type][4B compressedSize BE][compressedData][if compressed: 4B decompressedSize]
         * Expected container size = 5 + compressedSize + (if type != 0: 4 else 0)
         * If the blob is exactly 2 bytes longer than that, it already has a version suffix
         * (from being downloaded via HTTP from Jagex).
         */
        private fun blobHasVersionSuffix(data: ByteArray): Boolean {
            if (data.size < 5) return false
            val compressedSize = ((data[1].toInt() and 0xFF) shl 24) or
                    ((data[2].toInt() and 0xFF) shl 16) or
                    ((data[3].toInt() and 0xFF) shl 8) or
                    (data[4].toInt() and 0xFF)
            val type = data[0].toInt() and 0xFF
            val headerSize = if (type != 0) 9 else 5  // compressed types have 4-byte decompressed size
            val expectedContainerSize = headerSize + compressedSize
            return data.size == expectedContainerSize + 2
        }

        private fun computeBinaryCrc(path: String): Long {
            val file = java.io.File(path)
            if (!file.exists()) return 0L
            val crc = CRC32()
            crc.update(file.readBytes())
            return crc.value
        }

        /**
         * Compute the download_hash for jav_config.ws.
         *
         * rs3linux verifies this as an RSA-signed Whirlpool hash:
         * 1. Base64-decode the hash string (using * for +, - for / as alternates)
         * 2. RSA-decrypt with the launcher's public key (sig^e mod n)
         * 3. Expect exactly 65 bytes: [prefix_byte] + [64-byte Whirlpool hash]
         *
         * We sign with our own RSA private key; the LD_PRELOAD patcher replaces
         * the launcher's embedded public modulus with ours so verification passes.
         */
        private fun computeBinaryHash(path: String): String {
            val file = java.io.File(path)
            if (!file.exists()) return ""

            val data = file.readBytes()

            // Compute Whirlpool hash (64 bytes)
            val wp = Whirlpool()
            wp.add(data)
            val whirlpoolHash = ByteArray(64)
            wp.finalize(whirlpoolHash)

            // Build 65-byte message: [0x01 prefix] + [64-byte Whirlpool hash]
            val message = ByteArray(65)
            message[0] = 0x01
            System.arraycopy(whirlpoolHash, 0, message, 1, 64)

            // RSA sign with the login key — rs3linux's embedded public key is
            // patched by DARKAN_RSA_MODULUS which is the login modulus hex
            val modulus = BigInteger(EnvVars.loginRsaModulus)
            val exponent = BigInteger(EnvVars.loginRsaExponent)
            val messageBigInt = BigInteger(1, message) // positive BigInteger
            val signature = messageBigInt.modPow(exponent, modulus)

            // Convert signature to bytes (unsigned, no leading zero padding needed)
            val sigBytes = signature.toByteArray().let { bytes ->
                // BigInteger.toByteArray() may prepend a 0x00 for sign; strip it
                if (bytes.isNotEmpty() && bytes[0] == 0.toByte() && bytes.size > 1) {
                    bytes.copyOfRange(1, bytes.size)
                } else {
                    bytes
                }
            }

            // Base64 encode with Jagex alternate alphabet (* for +, - for /)
            val base64 = java.util.Base64.getEncoder().withoutPadding().encodeToString(sigBytes)
            return base64.replace('+', '*').replace('/', '-')
        }
    }
}
