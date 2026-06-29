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
import lzma.sdk.lzma.Encoder
import org.darkan.core.EnvVars
import org.darkan.core.Logger.logInfo
import org.darkan.core.Logger.logTrace
import org.darkan.core.Logger.logWarn
import org.darkan.lobby.social.SocialGateway
import world.gregs.voidps.cache.file.FileProvider
import world.gregs.voidps.cache.secure.Whirlpool
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.net.URLDecoder
import java.util.Base64
import java.util.zip.CRC32
import kotlin.time.Duration.Companion.seconds

class ConfigServer(private val fileProvider: FileProvider? = null) {
    private lateinit var server: EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration>

    /**
     * Precomputed per-OS client binary metadata for jav_config + binary serving.
     *
     * Two serve modes, selected per-OS by [lzmaEncoded]:
     *  - RAW (linux/windows): the rs3linux LD_PRELOAD patcher disables the launcher's
     *    LZMA decompression flag (patcher/src/lib.rs §6 "Patch 4"), so the launcher saves
     *    whatever bytes we send verbatim — [downloadBytes] is the raw binary.
     *  - LZMA-alone (macos): the RuneScape.app wrapper keeps LZMA decompression ENABLED
     *    (the mac dylib only patches RSA + codebase regex — the LZMA-flag patch is
     *    "rs3linux-launcher-only"), so we must serve a single-stream LZMA-alone (.lzma)
     *    payload that the wrapper decompresses back to the raw binary. [downloadBytes] is
     *    the LZMA-encoded payload.
     *
     * In both modes, [crc] and [hash] are computed over the RAW (decompressed) binary —
     * the launcher verifies the result AFTER decompression — while [size] (download) is the
     * length of [downloadBytes] (what the launcher actually downloads off the wire).
     *
     * @param path           filesystem path to the (raw, decompressed) binary
     * @param downloadName    download_name_0 value the launcher expects ("rs2client" / "rs2client.exe")
     * @param downloadBytes   the bytes served on the wire (raw, or LZMA-alone for macos)
     * @param crc             CRC32 of the RAW binary (download_crc_0)
     * @param hash            RSA-signed Whirlpool hash of the RAW binary (download_hash_0)
     * @param size            length of [downloadBytes] in bytes (download)
     * @param lzmaEncoded     true if [downloadBytes] is LZMA-alone encoded (macos slot)
     */
    private data class BinaryInfo(
        val path: String,
        val downloadName: String,
        val downloadBytes: ByteArray,
        val crc: Long,
        val hash: String,
        val size: Long,
        val lzmaEncoded: Boolean,
    )

    // Per-OS registry, precomputed eagerly at construction. The "linux" entry is the
    // fallback default and preserves today's single-binary behavior exactly.
    private val binaries: Map<String, BinaryInfo> = buildBinaryRegistry()
    private val defaultBinary: BinaryInfo = binaries.getValue("linux")

    private fun buildBinaryRegistry(): Map<String, BinaryInfo> {
        // clientBinaryPath defaults to ./data/client/linux/rs2client → clientRoot = ./data/client
        val clientRoot = java.io.File(EnvVars.clientBinaryPath).parentFile?.parentFile

        // Candidate (os → path, downloadName). Always include "linux" so the default
        // entry exists even when clientRoot can't be derived.
        val candidates = linkedMapOf(
            "linux" to (EnvVars.clientBinaryPath to "rs2client"),
        )
        if (clientRoot != null) {
            candidates["windows"] =
                java.io.File(java.io.File(clientRoot, "windows"), "rs2client.exe").path to "rs2client.exe"
            candidates["macos"] =
                java.io.File(java.io.File(clientRoot, "macos"), "rs2client").path to "rs2client"
        }

        val registry = linkedMapOf<String, BinaryInfo>()
        for ((os, spec) in candidates) {
            val (path, downloadName) = spec
            val file = java.io.File(path)
            if (file.exists()) {
                registry[os] = buildBinaryInfo(os, path, downloadName)
            } else {
                logWarn("Client binary for OS '$os' not found at $path — it will not be served")
            }
        }

        // Guarantee a "linux" fallback entry even if the linux file is missing, so
        // defaultBinary is always resolvable (size/crc 0 → matches prior missing-file behavior).
        if (!registry.containsKey("linux")) {
            registry["linux"] = buildBinaryInfo("linux", EnvVars.clientBinaryPath, "rs2client")
        }
        // Guarantee a "macos" entry. The RuneScape.app wrapper keeps LZMA decompression ON
        // (neither mac patcher disables it), so it MUST receive an LZMA-alone payload. When no
        // macos-specific binary is staged, fall back to the default client binary but LZMA-encode
        // it (os="macos" → buildBinaryInfo sets lzma=true). Without this, a binaryType=3 request
        // falls back to the RAW linux default and the wrapper fails with "Error saving file (14)".
        if (!registry.containsKey("macos")) {
            registry["macos"] = buildBinaryInfo("macos", EnvVars.clientBinaryPath, "rs2client")
        }
        return registry
    }

    /**
     * Read the raw binary at [path] and build its serve metadata. The macos slot is served
     * LZMA-alone-encoded (the wrapper decompresses it); every other OS is served raw (the
     * rs3linux patcher disables the launcher's decompression). CRC + hash always cover the
     * RAW bytes; [BinaryInfo.size] is the served (possibly compressed) length.
     */
    private fun buildBinaryInfo(os: String, path: String, downloadName: String): BinaryInfo {
        val file = java.io.File(path)
        if (!file.exists()) {
            return BinaryInfo(path, downloadName, ByteArray(0), 0L, "", 0L, lzmaEncoded = false)
        }
        val rawBytes = file.readBytes()
        val lzma = os == "macos"
        val downloadBytes = if (lzma) {
            val encoded = encodeLauncherLzmaAlone(rawBytes)
            logInfo("LZMA-alone encoded macos binary: ${rawBytes.size} raw -> ${encoded.size} download bytes")
            encoded
        } else {
            rawBytes
        }
        return BinaryInfo(
            path = path,
            downloadName = downloadName,
            downloadBytes = downloadBytes,
            crc = computeBinaryCrc(rawBytes),
            hash = computeBinaryHash(rawBytes, os),
            size = downloadBytes.size.toLong(),
            lzmaEncoded = lzma,
        )
    }

    /** Resolve a binaryType query param to one of "linux"/"windows"/"macos". null/4/unknown → linux. */
    private fun binaryTypeToOs(bt: Int?): String = when (bt) {
        3 -> "macos"
        1, 2, 5, 6 -> "windows"
        else -> "linux" // includes null and 4 (linux)
    }

    /** Look up a BinaryInfo for an OS, falling back to the linux default with a warning. */
    private fun binaryForOs(os: String): BinaryInfo {
        val info = binaries[os]
        if (info == null) {
            logWarn("No client binary registered for OS '$os' — falling back to linux default")
            return defaultBinary
        }
        return info
    }

    fun start() {
        logInfo("Client binary registry:")
        for (os in listOf("linux", "windows", "macos")) {
            val info = binaries[os]
            if (info != null) {
                val mode = if (info.lzmaEncoded) "lzma" else "raw"
                logInfo("  $os -> ${info.path} (crc=${info.crc}, download=${info.size}, $mode, present)")
            } else {
                val expected = when (os) {
                    "linux" -> EnvVars.clientBinaryPath
                    "windows" -> java.io.File(EnvVars.clientBinaryPath).parentFile?.parentFile?.let {
                        java.io.File(java.io.File(it, "windows"), "rs2client.exe").path
                    } ?: "(unknown)"
                    else -> java.io.File(EnvVars.clientBinaryPath).parentFile?.parentFile?.let {
                        java.io.File(java.io.File(it, "macos"), "rs2client").path
                    } ?: "(unknown)"
                }
                logInfo("  $os -> $expected (absent)")
            }
        }
        logInfo("Client binary CRC32: ${defaultBinary.crc} (path: ${defaultBinary.path})")
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
                    // DIAGNOSTIC (JS5 stall): INFO-level so we can see whether the 948-5 client
                    // routes post-master-index content (255/N indices, N/M groups) over HTTP to
                    // this endpoint vs. the persistent TCP JS5 socket. Decisive for TCP-vs-HTTP.
                    logInfo("JS5 HTTP request: ${call.request.local.uri}")
                    serveJs5Http(call)
                }
                post("/nxtclienterror.ws") {
                    val body = call.receiveText()
                    logWarn("=== NXT CLIENT CRASH REPORT ===")
                    // Parse URL-encoded form data
                    body.split("&").forEach { param ->
                        val parts = param.split("=", limit = 2)
                        if (parts.size == 2) {
                            val key = URLDecoder.decode(parts[0], "UTF-8")
                            val value = URLDecoder.decode(parts[1], "UTF-8")
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
                        val binaryType = call.request.queryParameters["binaryType"]?.toIntOrNull()
                        val os = binaryTypeToOs(binaryType)
                        val info = binaryForOs(os)
                        logInfo("Serving jav_config for binaryType=$binaryType -> os=$os (binary=${info.downloadName})")
                        call.respondText(
                            generateJavConfig(info),
                            ContentType.Text.Plain.withCharset(Charsets.ISO_8859_1)
                        )
                    } else if (path.contains("rs2client")) {
                        // Serve the client binary so the launcher can download it. The launcher
                        // appends ?binaryType=N (&fileName=NAME) when fetching the binary.
                        //
                        // Serve mode is OS-conditional (see BinaryInfo.downloadBytes):
                        //  - linux/windows → RAW: the rs3linux LD_PRELOAD patcher disables the
                        //    launcher's LZMA decompression flag (patcher/src/lib.rs §6 "Patch 4"),
                        //    so the launcher saves whatever bytes we send verbatim.
                        //  - macos → LZMA-alone: the RuneScape.app wrapper keeps LZMA decompression
                        //    ENABLED (the mac dylib only patches RSA + codebase regex), so we serve
                        //    a single-stream LZMA-alone payload it decompresses back to the binary.
                        //    Serving raw to the mac wrapper triggers "Error saving file (14)".
                        val binaryType = call.request.queryParameters["binaryType"]?.toIntOrNull()
                        val os = if (binaryType != null) {
                            binaryTypeToOs(binaryType)
                        } else {
                            // No binaryType — infer from fileName (rs2client.exe → windows), else linux.
                            val fileName = call.request.queryParameters["fileName"]
                            if (fileName != null && fileName.endsWith(".exe", ignoreCase = true)) "windows" else "linux"
                        }
                        val info = binaryForOs(os)
                        if (info.downloadBytes.isNotEmpty()) {
                            val mode = if (info.lzmaEncoded) "LZMA-alone" else "raw"
                            logInfo("Serving client binary (os=$os, $mode): ${info.path} (${info.size} download bytes)")
                            call.respondBytes(info.downloadBytes, ContentType.Application.OctetStream)
                        } else if (defaultBinary.downloadBytes.isNotEmpty()) {
                            logWarn("Client binary for os=$os missing at ${info.path} — falling back to linux default")
                            call.respondBytes(defaultBinary.downloadBytes, ContentType.Application.OctetStream)
                        } else {
                            call.respondText("Client binary not found", ContentType.Text.Plain, HttpStatusCode.NotFound)
                        }
                    } else if (path.contains("darkan_patcher")) {
                        // Serve the per-OS LD_PRELOAD/DLL RSA patcher side-by-side with the
                        // client binary. The launcher fetches this from the SAME custom config
                        // server before launch, so the patcher it loads always matches THIS
                        // server's RSA keys + client build (no reliance on a locally-staged
                        // copy that could be stale or absent). OS is inferred from the requested
                        // file name: .dll → windows, .dylib → macos, .so → linux.
                        val patcherName = when {
                            path.contains("darkan_patcher.dll") -> "darkan_patcher.dll"
                            path.contains("libdarkan_patcher.dylib") -> "libdarkan_patcher.dylib"
                            else -> "libdarkan_patcher.so"
                        }
                        val os = when {
                            patcherName.endsWith(".dll") -> "windows"
                            patcherName.endsWith(".dylib") -> "macos"
                            else -> "linux"
                        }
                        // clientBinaryPath defaults to ./data/client/linux/rs2client → the
                        // patcher lives at ./data/client/<os>/<patcherName>, beside the binary.
                        val clientRoot = java.io.File(EnvVars.clientBinaryPath).parentFile?.parentFile
                        val file = clientRoot?.let { java.io.File(java.io.File(it, os), patcherName) }
                        if (file != null && file.exists()) {
                            logInfo("Serving patcher (os=$os): ${file.absolutePath} (${file.length()} bytes)")
                            call.respondFile(file)
                        } else {
                            logWarn("Patcher for os=$os not found at ${file?.absolutePath ?: "(client root unresolved)"}")
                            call.respondText("Patcher not found", ContentType.Text.Plain, HttpStatusCode.NotFound)
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
            // DIAGNOSTIC (JS5 stall): a 404 here would itself stall the client; make misses loud.
            logWarn("JS5 HTTP miss: a=$archive g=$group — no cache data, returning 404")
            call.respondText("Not found", ContentType.Text.Plain, HttpStatusCode.NotFound)
            return
        }
        val response = buildJs5HttpResponse(provider, archive, group, data)
        // DIAGNOSTIC (JS5 stall): INFO-level so successful HTTP content serves are visible
        // at the default TRACE/FINER log level during a pilot run.
        logInfo("JS5 HTTP: a=$archive g=$group v=${response.version} -> ${response.body.size} bytes (container=${data.size}, suffix=cache-version)")
        call.respondBytes(response.body, ContentType.Application.OctetStream)
    }

    private fun generateJavConfig(info: BinaryInfo): String = buildString {
        // configPublicHost is the host advertised to the client (codebase/param URLs, lobby host
        // param). Defaults to localhost for local dev; set CONFIG_PUBLIC_HOST for remote serving.
        val host = EnvVars.configPublicHost
        val lobbyPort = EnvVars.lobbyPort

        fun line(s: String) { append(s); append("\n") }

        // General config
        line("title=${EnvVars.serverName}")
        line("adverturl=")
        line("codebase=http://$host:${EnvVars.configHttpPort}/")
        line("binary_name=rs2client")
        line("download_name_0=${info.downloadName}")
        line("download_crc_0=${info.crc}")
        line("download_hash_0=${info.hash}")
        line("binary_count=1")
        line("launcher_version=224")
        line("server_version=${EnvVars.majorVersion}")
        line("launcher_sub_version=1")
        line("cache_variant_suffix=")
        line("termsurl=https://legal.jagex.com/docs/terms")
        line("privacyurl=https://legal.jagex.com/docs/policies")
        line("download=${info.size}")
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
        line("param=6=${EnvVars.worldId}")
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
        line("param=25=0")                                    // ModeWhere=LIVE — httpMode param (§11); kept 0 so JS5 stays on TCP, paired with empty content-server params 35/37/40/49
        line("param=26=false")
        line("param=27=3")
        line("param=28=265964763")
        line("param=29=${EnvVars.js5ServerToken}")                // JS5 server token
        line("param=31=19435")
        line("param=32=")
        line("param=33=")
        line("param=34=547933620")
        // NOTE (2026-06-23): params 35/37/40/49 = HTTP content-server advertisement. Emptying them
        // to force JS5 over TCP (intended fix for the index-40 / AUDIO_STREAMS group-38557
        // "Verifying Cache" httpMode loop, per docs/net/js5-http-group-download.md §11) was tried
        // and BROKE early init — the client hung at "initializing resources" and never opened ANY
        // JS5 connection (it does NOT fall back to TCP at bootstrap; the content server is required
        // to construct the JS5 provider). Reverted to the working values. The real index-40 fix is
        // the precise sync→async httpMode lever (provider+0x70), NOT emptying these params.
        line("param=35=http://$host:${EnvVars.configHttpPort}")   // content server URL
        line("param=36=")
        line("param=37=$host")                                    // content server host
        line("param=38=1200")
        line("param=39=false")
        line("param=40=http://$host:${EnvVars.configHttpPort}")   // content server URL
        // Game ports for JS5/lobby bootstrap. The local world socket target is carried in the
        // lobby login-response world-target tail.
        line("param=41=$lobbyPort")                               // game port 1 (JS5 lives here)
        line("param=42=$lobbyPort")                               // game port 2 (was SSL 443)
        line("param=43=$lobbyPort")                               // game port 3
        line("param=44=$lobbyPort")                               // game port 4 (was SSL 443)
        line("param=45=$lobbyPort")                               // game port 5
        line("param=46=$lobbyPort")                               // game port 6 (was SSL 443)
        line("param=47=$lobbyPort")                               // game port 7
        line("param=48=$lobbyPort")                               // game port 8 (was SSL 443)
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
        line("param=99=${EnvVars.loginRsaModulusHex}")   // hex login RSA modulus (1024-bit) for client patcher
        // hex JS5 RSA modulus (4096-bit) for the client patcher: the launcher reads
        // this and exports DARKAN_JS5_RSA_MODULUS so the patcher swaps the client's
        // embedded JS5 key. Without it the client verifies our master index with
        // Jagex's key, fails, and disconnects right after receiving index 255/255.
        // Non-standard param (like 99) — the client ignores it; only the launcher reads it.
        line("param=100=${EnvVars.js5RsaModulusHex}")
    }

    companion object {
        internal data class Js5HttpResponse(val body: ByteArray, val version: Int)

        internal fun buildJs5HttpResponse(provider: FileProvider, index: Int, archive: Int, container: ByteArray): Js5HttpResponse {
            val version = provider.version(index, archive) ?: 0
            return Js5HttpResponse(appendJs5HttpVersion(container, version), version)
        }

        private fun appendJs5HttpVersion(container: ByteArray, version: Int): ByteArray {
            val response = container.copyOf(container.size + 2)
            response[container.size] = (version ushr 8).toByte()
            response[container.size + 1] = version.toByte()
            return response
        }

        // LZMA-alone (.lzma) encoder settings for the macos download payload. These mirror
        // what Jagex serves and what the unpatched RuneScape.app wrapper expects: 8 MiB
        // dictionary, 32 fast bytes, BT4 match finder, lc/lp/pb = 3/0/2, no end marker.
        private const val LZMA_DICTIONARY_SIZE = 8 * 1024 * 1024
        private const val LZMA_FAST_BYTES = 32

        private fun computeBinaryCrc(data: ByteArray): Long {
            val crc = CRC32()
            crc.update(data)
            return crc.value
        }

        /**
         * Encode [data] as a single-stream LZMA-alone (.lzma) payload: 5-byte coder
         * properties + 8-byte little-endian uncompressed length + the LZMA bitstream.
         * This is the format the macOS RuneScape.app wrapper decompresses on download
         * (its LZMA decompression flag is left enabled — unlike the rs3linux launcher,
         * which the LD_PRELOAD patcher patches to save raw bytes).
         */
        private fun encodeLauncherLzmaAlone(data: ByteArray): ByteArray {
            val output = ByteArrayOutputStream()
            val encoder = Encoder()
            check(encoder.setDictionarySize(LZMA_DICTIONARY_SIZE))
            check(encoder.setNumFastBytes(LZMA_FAST_BYTES))
            check(encoder.setMatchFinder(Encoder.EMatchFinderTypeBT4))
            check(encoder.setLcLpPb(3, 0, 2))
            encoder.setEndMarkerMode(false)
            encoder.writeCoderProperties(output)
            writeLittleEndianLong(output, data.size.toLong())
            ByteArrayInputStream(data).use { input ->
                encoder.code(input, output, data.size.toLong(), -1L, null)
            }
            return output.toByteArray()
        }

        private fun writeLittleEndianLong(output: ByteArrayOutputStream, value: Long) {
            repeat(8) { index ->
                output.write(((value ushr (index * 8)) and 0xFF).toInt())
            }
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
         *
         * [data] is always the RAW (decompressed) binary — both rs3linux and the macOS
         * wrapper verify the result AFTER decompression — so the same hash is correct for
         * the raw-served (linux) and LZMA-served (macos) slots.
         */
        private fun computeBinaryHash(data: ByteArray, os: String): String {
            if (data.isEmpty()) return ""

            // Compute Whirlpool hash (64 bytes)
            val wp = Whirlpool()
            wp.add(data)
            val whirlpoolHash = ByteArray(64)
            wp.finalize(whirlpoolHash)

            // Build 65-byte message: [0x01 prefix] + [64-byte Whirlpool hash]
            val message = ByteArray(65)
            message[0] = 0x01
            System.arraycopy(whirlpoolHash, 0, message, 1, 64)

            // RSA sign with the key the TARGET launcher verifies with — the signature size
            // must match the launcher's embedded key, or it can't be verified at all:
            //  - rs3linux embeds a 1024-bit key, patched from the LOGIN modulus
            //    (DARKAN_RSA_MODULUS) → sign with login (128-byte signature).
            //  - the macOS RuneScape.app wrapper embeds a 4096-bit key, patched from the JS5
            //    modulus (DARKAN_JS5_RSA_MODULUS, the 1024-hex one its dylib writes) → sign
            //    with JS5 (512-byte signature). A login-signed (128-byte) hash can't verify
            //    against the wrapper's 4096-bit key, so it rejects jav_config with
            //    "Error saving file (14)" BEFORE it ever requests the binary.
            val useJs5 = os == "macos"
            val modulus = if (useJs5) BigInteger(EnvVars.js5RsaModulus) else BigInteger(EnvVars.loginRsaModulus)
            val exponent = if (useJs5) BigInteger(EnvVars.js5RsaExponent) else BigInteger(EnvVars.loginRsaExponent)
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
            val base64 = Base64.getEncoder().withoutPadding().encodeToString(sigBytes)
            return base64.replace('+', '*').replace('/', '-')
        }
    }
}
