package org.darkan.core

import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import java.math.BigInteger

object EnvVars {
    private val dotenv: Dotenv = dotenv {
        ignoreIfMissing = true
    }

    //server settings
    val debug: Boolean = dotenv.get("DEBUG", "false").toBooleanStrict()
    val members: Boolean = dotenv.get("MEMBERS", "true").toBooleanStrict()
    val serverName: String = dotenv.get("SERVER_NAME", "Darkan")
    val multiLogWhitelist: String = dotenv.get("MULTILOG_WHITELIST", "localhost,127.0.0.1,::1,0:0:0:0:0:0:0:1")
    val multiLogLimit: Int = dotenv.get("MULTILOG_LIMIT", "3").toInt()
    val js5ServerToken: String = dotenv.get("JS5_SERVER_TOKEN", "ev9+VAp5/tMKeNR/7MOuH6lKWS+rGkHK")
    val loginServerToken: String = dotenv.get("LOGIN_SERVER_TOKEN", "wwGlrZHF5gKN6D3mDdihco3oPeYN2KFybL9hUUFqOvk")
    val clientKey: String = dotenv.get("CLIENT_KEY", "29EDD9FDC775629058FBBF106C5B0E0A3A8028FE0037D1737B8EC3EA2F4E8B8FD6F54EF2F4E65862")
    val js5RsaModulus: String = dotenv.get("RSA_JS5_MODULUS", "117525752735533423040644219776209926525585489242340044375332234679786347045466594509203355398209678968096551043842518449703703964361320462967286756268851663407950384008240524570966471744081769815157355561961607944067477858512067883877129283799853947605780903005188603658779539811385137666347647991072028080201")
    val js5RsaExponent: String = dotenv.get("RSA_JS5_EXPONENT", "45769714620275867926001532284788836149236590657678028481492967724067121406860916606777808563536714166085238449913676219414798301454048585933351540049893959827785868628572203706265915752274580525376826724019249600701154664022299724373133271944352291456503171589594996734220177420375212353960806722706846977073")
    val worldRsaModulus: String = dotenv.get("RSA_WORLD_MODULUS", js5RsaModulus)
    val worldRsaExponent: String = dotenv.get("RSA_WORLD_EXPONENT", js5RsaExponent)
    val rsaPublicModulusHex: String get() = BigInteger(js5RsaModulus).toString(16)
    val lobbyPort: Int = dotenv.get("LOBBY_PORT", "43594").toInt()
    val worldPort: Int = dotenv.get("WORLD_PORT", "43595").toInt()
    val cachePath: String = dotenv.get("CACHE_PATH", "./data/cache")
    val memCache: Boolean = dotenv.get("MEM_CACHE", "false").toBooleanStrict()
    val majorVersion: Int = dotenv.get("MAJOR_VERSION", "946").toInt()
    val minorVersion: Int = dotenv.get("MINOR_VERSION", "5").toInt()
    val cacheThreadUsage: Double = dotenv.get("CACHE_THREAD_USAGE", "1.0").toDouble()
    val configHttpPort: Int = dotenv.get("CONFIG_HTTP_PORT", "8829").toInt()
    val clientBinaryPath: String = dotenv.get("CLIENT_BINARY_PATH", "./data/client/rs2client")
    val logLevel: String = dotenv.get("LOG_LEVEL", "TRACE")
}
