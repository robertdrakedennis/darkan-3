package org.darkan.lobby.server

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.darkan.core.EnvVars
import org.darkan.core.Logger.logInfo

class ConfigServer {
    private lateinit var server: EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration>

    fun start() {
        server = embeddedServer(Netty, port = EnvVars.configHttpPort) {
            routing {
                get("{...}") {
                    val path = call.request.local.uri
                    if (path.contains("jav_config.ws")) {
                        call.respondText(generateJavConfig(), ContentType.Text.Plain)
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

    private fun generateJavConfig(): String = buildString {
        val host = "localhost"
        val port = EnvVars.lobbyPort

        // General config
        appendLine("title=${EnvVars.serverName}")
        appendLine("adverturl=")
        appendLine("codebase=http://$host:${EnvVars.configHttpPort}/")
        appendLine("binary_name=rs2client")
        appendLine("download_name_0=rs2client")
        appendLine("download_crc_0=0")
        appendLine("download_hash_0=")
        appendLine("binary_count=1")
        appendLine("launcher_version=224")
        appendLine("server_version=${EnvVars.majorVersion}")
        appendLine("launcher_sub_version=1")
        appendLine("cache_variant_suffix=")
        appendLine("termsurl=https://legal.jagex.com/docs/terms")
        appendLine("privacyurl=https://legal.jagex.com/docs/policies")
        appendLine("download=0")
        appendLine("window_preferredwidth=1024")
        appendLine("window_preferredheight=768")
        appendLine("advert_height=96")
        appendLine("applet_minwidth=765")
        appendLine("applet_minheight=540")
        appendLine("applet_maxwidth=3840")
        appendLine("applet_maxheight=2160")

        // Messages
        appendLine("msg=lang0=English")
        appendLine("msg=lang1=Deutsch")
        appendLine("msg=lang2=Français")
        appendLine("msg=lang3=Português")
        appendLine("msg=progress_downloading_client=Downloading Client...")
        appendLine("msg=nxt_confirm_quit=Are you sure you want to quit?")
        appendLine("msg=nxt_bad_version=Incorrect Launcher version")
        appendLine("msg=nxt_cannot_move_file=Could not move %s")
        appendLine("msg=nxt_cannot_move_file_to_file=Could not move %s to %s")
        appendLine("msg=nxt_retry_move_at_exit=We will try to move it again when you quit")
        appendLine("msg=nxt_moving_files=Moving files to the new location")
        appendLine("msg=nxt_not_enough_space=There is not enough space on the target drive to move the files")
        appendLine("msg=nxt_check_security=Please ensure that access to %s is not blocked by security permissions and is not already open by another instance of RuneScape")
        appendLine("msg=nxt_single_instance_options=You may only change RuneScape Launcher options when a single instance is running")
        appendLine("msg=nxt_graphics_prompt1=The RuneScape client suffered from an error")
        appendLine("msg=nxt_graphics_prompt1_init=The RuneScape client suffered from an error during graphics initialisation")
        appendLine("msg=nxt_graphics_prompt2=This could be due to graphics driver issues and may be improved by switching graphics mode or updating drivers.")
        appendLine("msg=nxt_graphics_prompt3=The graphics drivers on your %s are at version %s, which is %s days old.")
        appendLine("msg=nxt_graphics_prompt4=You are currently using the %s graphics mode. Would you like to switch to %s instead?")
        appendLine("msg=nxt_graphics_prompt5=Alternatively, you may instruct RuneScape to load with default graphics settings the next time it is run.")
        appendLine("msg=nxt_driver_warning_small=Your graphics drivers are %d days old.")
        appendLine("msg=nxt_driver_warning=The graphics drivers on your %s are at version %s, which is %d days old.")
        appendLine("msg=nxt_driver_update=You can update them here.")
        appendLine("msg=nxt_dialog_graphics_mode=Graphics Mode")
        appendLine("msg=nxt_dialog_graphics_auto=Auto (%s)")
        appendLine("msg=nxt_dialog_graphics_normal=Normal Mode")
        appendLine("msg=nxt_dialog_graphics_compat=Compatibility Mode")
        appendLine("msg=nxt_use_default=Use Default Settings")
        appendLine("msg=nxt_never_ask=Do not ask me again")
        appendLine("msg=nxt_button_switch=Switch")
        appendLine("msg=new_version=")
        appendLine("msg=new_version_linktext=")
        appendLine("msg=new_version_link=")
        appendLine("msg=tandc=")
        appendLine("msg=options=Options")
        appendLine("msg=language=Language")
        appendLine("msg=changes_on_restart=Your changes will take effect when you next start this program.")
        appendLine("msg=loading_app_resources=Loading application resources")
        appendLine("msg=loading_app=Loading application")
        appendLine("msg=err_save_file=Error saving file")
        appendLine("msg=err_downloading=Error downloading")
        appendLine("msg=ok=OK")
        appendLine("msg=cancel=Cancel")
        appendLine("msg=message=Message")
        appendLine("msg=information=Information")
        appendLine("msg=err_get_file=Error getting file")

        // Params (numbered parameters passed as CLI args to the client)
        appendLine("param=1=0")
        appendLine("param=2=1101")
        appendLine("param=3=$host")                                    // lobby host
        appendLine("param=4=0")
        appendLine("param=5=0")
        appendLine("param=6=0")
        appendLine("param=7=0")
        appendLine("param=8=false")
        appendLine("param=10=${EnvVars.loginServerToken}")              // login server token
        appendLine("param=11=225")
        appendLine("param=13=false")
        appendLine("param=14=false")
        appendLine("param=15=")
        appendLine("param=16=.$host")
        appendLine("param=17=false")
        appendLine("param=18=-1187320092")
        appendLine("param=19=")
        appendLine("param=20=false")
        appendLine("param=21=halign=true|valign=true|image=rs_logo.gif,0,-43|rotatingimage=rs3_loading_spinner.gif,0,47,9.6|progress=true,Verdana,13,0xFFFFFF,0,51")
        appendLine("param=22=")
        appendLine("param=23=false")
        appendLine("param=24=true")
        appendLine("param=25=0")
        appendLine("param=26=false")
        appendLine("param=27=3")
        appendLine("param=28=265964763")
        appendLine("param=29=${EnvVars.js5ServerToken}")                // JS5 server token
        appendLine("param=31=19435")
        appendLine("param=32=")
        appendLine("param=33=")
        appendLine("param=34=547933620")
        appendLine("param=35=http://$host:${EnvVars.configHttpPort}")   // world server URL
        appendLine("param=36=")
        appendLine("param=37=$host")                                    // content server host
        appendLine("param=38=1200")
        appendLine("param=39=false")
        appendLine("param=40=http://$host:${EnvVars.configHttpPort}")   // world server URL
        appendLine("param=41=$port")                                    // game port 1
        appendLine("param=42=$port")                                    // game port 2 (was SSL 443)
        appendLine("param=43=$port")                                    // game port 3
        appendLine("param=44=$port")                                    // game port 4 (was SSL 443)
        appendLine("param=45=$port")                                    // game port 5
        appendLine("param=46=$port")                                    // game port 6 (was SSL 443)
        appendLine("param=47=$port")                                    // game port 7
        appendLine("param=48=$port")                                    // game port 8 (was SSL 443)
        appendLine("param=49=$host")                                    // content server host
        appendLine("param=50=0")
        appendLine("param=51=0")
        appendLine("param=52=0")
        appendLine("param=53=https://auth.jagex.com/")
        appendLine("param=54=https://payments.jagex.com/")
        appendLine("param=55=")
        appendLine("param=56=https://social.auth.jagex.com/")
        appendLine("param=57=6124")
        appendLine("param=58=https://account.jagex.com/")
        appendLine("param=59=https://auth.runescape.com/")
        appendLine("param=60=0")
    }
}
