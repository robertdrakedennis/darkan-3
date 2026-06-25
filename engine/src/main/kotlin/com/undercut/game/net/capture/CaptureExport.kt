package com.undercut.game.net.capture

import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicInteger

/**
 * Capture-export: writes live network captures in the EXACT on-disk format consumed by the
 * Darkan-3 `:tools` `framingRegression` and `wireFormatVerify` gates, so the injected sniffer can
 * replace the deprecated login proxy (no network redirection). See CLAUDE.md → "Proxy Deprecation".
 *
 * ## Format contract (must match the old LoginProxy writer + the gate readers exactly)
 * ```
 * <captureDir>/login-<yyyyMMdd-HHmmss>_s<id>/
 *   ├── raw-c2s.bin     raw ON-THE-WIRE bytes, client->server (ISAAC-encrypted opcode(s) + framing + body)
 *   ├── raw-s2c.bin     raw ON-THE-WIRE bytes, server->client (9-byte login prelude, then ISAAC frames)
 *   └── isaac-keys.txt  "ISAAC keys (decimal): a, b, c, d\n
 *                        ISAAC keys (hex): 0xAAAAAAAA, 0xBBBBBBBB, 0xCCCCCCCC, 0xDDDDDDDD\n"
 * ```
 * The gates re-derive BOTH ISAAC streams from the 4 keys (C->S stream = keys; S->C stream =
 * keys + EnvVars.ISAAC_DELTA) and re-frame the raw bytes themselves. Therefore the bytes written
 * here MUST be the raw/encrypted on-wire stream — NOT the decrypted payloads that the existing
 * [com.undercut.game.hooks.impl.ServerPacketCapture] / [com.undercut.game.hooks.impl.SendClientMessage]
 * hooks expose (those fire after ISAAC has already decoded the opcode).
 *
 * ## Status
 * The writer + session lifecycle below are complete and correct. The THREE data sources still need
 * new, individually-verified hooks against the live client. Do NOT wire speculative offsets — a hook
 * at a wrong address corrupts the prologue and SIGSEGVs the client (see the engine crash post-mortems).
 * Each source feeds one method here:
 *   1. raw-s2c bytes — hook the socket recv into the ServerConnection receive buffer, BEFORE the
 *      ISAAC opcode-decode inside `jag::ConnectionManager::TcpIn`. Feed via [feedS2c].
 *   2. raw-c2s bytes — hook the TCP write of the client's outbound (already ISAAC-encrypted) buffer.
 *      Feed via [feedC2s].
 *   3. the 4 ISAAC keys — capture the seeds the client generates for the login block (it generates
 *      them, embeds them in the RSA login block, then seeds the in/out ISAAC ciphers). Hook the
 *      ISAAC seeding / login-block builder. `OServerConnection.ISAAC_PTR (0x2B8)` is already advanced
 *      by the time the post-decode hooks run, so it cannot be read there. Feed via [setIsaacKeys].
 * The natural place to call [startSession] is the same login hook that observes step (3);
 * [endSession] on disconnect.
 *
 * ## Enabling
 * Off by default (no runtime behaviour change). Enable with `UNDERCUT_CAPTURE_EXPORT=1`; override the
 * output directory with `UNDERCUT_CAPTURE_DIR` (default `~/.undercut/captures`). Copy a produced
 * session dir under the repo's `capture/` to feed `./gradlew :tools:framingRegression :tools:wireFormatVerify`.
 */
object CaptureExport {

    val enabled: Boolean =
        System.getenv("UNDERCUT_CAPTURE_EXPORT")?.let { it == "1" || it.equals("true", ignoreCase = true) } ?: false

    private val baseDir: File =
        File(System.getenv("UNDERCUT_CAPTURE_DIR") ?: (System.getProperty("user.home") + "/.undercut/captures"))

    private val sessionCounter = AtomicInteger(0)

    @Volatile
    private var session: CaptureSession? = null

    /** Begin a new capture session (rolls any open session). No-op unless [enabled]. */
    @Synchronized
    fun startSession(): CaptureSession? {
        if (!enabled) return null
        session?.close()
        return CaptureSession(baseDir, sessionCounter.incrementAndGet()).also { session = it }
    }

    /** Write the 4 ISAAC keys for the active session (data source #3). */
    @Synchronized
    fun setIsaacKeys(keys: IntArray) {
        session?.writeIsaacKeys(keys)
    }

    /** Append raw on-the-wire client->server bytes (data source #2). */
    fun feedC2s(bytes: ByteArray, off: Int = 0, len: Int = bytes.size) {
        session?.writeC2sRaw(bytes, off, len)
    }

    /** Append raw on-the-wire server->client bytes (data source #1). */
    fun feedS2c(bytes: ByteArray, off: Int = 0, len: Int = bytes.size) {
        session?.writeS2cRaw(bytes, off, len)
    }

    /** Close the active session. */
    @Synchronized
    fun endSession() {
        session?.close()
        session = null
    }
}

/**
 * A single capture session directory. Mirrors the LoginProxy writer byte-for-byte:
 * `raw-c2s.bin` / `raw-s2c.bin` are appended raw + flushed; `isaac-keys.txt` is written once.
 */
class CaptureSession(baseDir: File, sessionId: Int) {

    val dir: File
    private val c2sRaw: FileOutputStream
    private val s2cRaw: FileOutputStream

    init {
        val ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"))
        dir = File(baseDir, "login-${ts}_s$sessionId")
        dir.mkdirs()
        c2sRaw = FileOutputStream(File(dir, "raw-c2s.bin"))
        s2cRaw = FileOutputStream(File(dir, "raw-s2c.bin"))
    }

    @Synchronized
    fun writeC2sRaw(bytes: ByteArray, off: Int, len: Int) {
        c2sRaw.write(bytes, off, len)
        c2sRaw.flush()
    }

    @Synchronized
    fun writeS2cRaw(bytes: ByteArray, off: Int, len: Int) {
        s2cRaw.write(bytes, off, len)
        s2cRaw.flush()
    }

    /** Exact format: `ISAAC keys (decimal): ...` then `ISAAC keys (hex): 0x........, ...`. */
    fun writeIsaacKeys(keys: IntArray) {
        require(keys.size == 4) { "expected 4 ISAAC keys, got ${keys.size}" }
        val decimal = keys.joinToString(", ")
        val hex = keys.joinToString(", ") { "0x%08X".format(it) }
        File(dir, "isaac-keys.txt").writeText("ISAAC keys (decimal): $decimal\nISAAC keys (hex): $hex\n")
    }

    @Synchronized
    fun close() {
        runCatching { c2sRaw.close() }
        runCatching { s2cRaw.close() }
    }
}
