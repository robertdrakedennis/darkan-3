package org.darkan.tools.clientupdater

import org.darkan.tools.util.JavConfig
import java.io.File
import java.net.HttpURLConnection
import java.net.URI
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.zip.CRC32

/**
 * One downloadable NXT client target. The `binaryType` query param on jav_config.ws selects the
 * platform; each returns its own `codebase` host, `download_name_0`, `download_crc_0` (which is the
 * CRC32 of the *decompressed* binary) and `server_version`.
 *
 * `localPath` is the path, relative to the client root (./data/client), where this OS's binary
 * lives. Linux + Windows-64 keep the legacy top-level names already used by the server/version dirs
 * (`rs2client`, `rs2client.exe`); macOS and the legacy 32-bit Windows build go under subdirectories
 * because their `download_name` collides with the primary ones.
 */
enum class OsTarget(
    val key: String,
    val binaryType: Int,
    val label: String,
    val localPath: String,
    val magicName: String,
    val magic: ByteArray,
    val inDefaultSet: Boolean,
    /** Native executable for this host? (ELF/Mach-O get +x; Windows .exe files don't, matching the version dirs.) */
    val executable: Boolean,
) {
    LINUX("linux", 4, "Linux x86-64", "rs2client", "ELF", byteArrayOf(0x7F, 0x45, 0x4C, 0x46), true, true),
    WIN64("win64", 2, "Windows 64-bit", "rs2client.exe", "PE/MZ", byteArrayOf(0x4D, 0x5A), true, false),
    MACOS("macos", 3, "macOS x86-64", "macos/rs2client", "Mach-O", byteArrayOf(0xCF.toByte(), 0xFA.toByte(), 0xED.toByte(), 0xFE.toByte()), true, true),
    WIN32("win32", 1, "Windows 32-bit (legacy)", "win32/rs2client.exe", "PE/MZ", byteArrayOf(0x4D, 0x5A), false, false);

    companion object {
        fun byKey(key: String): OsTarget? = entries.firstOrNull { it.key.equals(key, ignoreCase = true) }
        val defaultSet: List<OsTarget> = entries.filter { it.inDefaultSet }
    }
}

enum class Status { UP_TO_DATE, OUTDATED, MISSING, ERROR }
enum class Action { NONE, INSTALLED, REPLACED, SKIPPED_OUTDATED, FAILED }

/** Result of checking (and optionally updating) one OS target. */
data class TargetResult(
    val target: OsTarget,
    val status: Status,
    var action: Action = Action.NONE,
    val serverVersion: String? = null,
    val latestCrc: Long? = null,
    val localCrc: Long? = null,
    val installedAt: File? = null,
    val sizeBytes: Long? = null,
    val sha256: String? = null,
    val sourceUrl: String? = null,
    val note: String? = null,
)

/** Knobs controlling what [ClientUpdater] does with each target. */
data class UpdateOptions(
    val rootDir: File,
    val checkOnly: Boolean,     // never write anything (pure dry run)
    val update: Boolean,        // replace OUTDATED existing binaries (MISSING are always filled unless checkOnly)
    val force: Boolean,         // re-download even when UP_TO_DATE
)

class ClientUpdater(private val options: UpdateOptions) {

    fun process(target: OsTarget): TargetResult {
        val cfg = try {
            JavConfig.fetch(javConfigUrl(target.binaryType))
        } catch (e: Exception) {
            return TargetResult(target, Status.ERROR, Action.FAILED, note = "jav_config fetch failed: ${e.message}")
        }

        val codebase = cfg.settings["codebase"]
        val name = cfg.settings["download_name_0"]
        val latestCrc = cfg.settings["download_crc_0"]?.toLongOrNull()
        val serverVersion = cfg.settings["server_version"]
        if (codebase.isNullOrBlank() || name.isNullOrBlank() || latestCrc == null) {
            return TargetResult(
                target, Status.ERROR, Action.FAILED, serverVersion = serverVersion,
                note = "jav_config missing codebase/download_name_0/download_crc_0",
            )
        }

        val localFile = File(options.rootDir, target.localPath)
        val localCrc = if (localFile.isFile) crc32(localFile.readBytes()) else null
        val status = when {
            localCrc == null -> Status.MISSING
            localCrc == latestCrc -> Status.UP_TO_DATE
            else -> Status.OUTDATED
        }

        val base = TargetResult(
            target, status, serverVersion = serverVersion, latestCrc = latestCrc, localCrc = localCrc,
        )

        // Decide whether to fetch the binary.
        val shouldDownload = when {
            options.checkOnly -> false
            options.force -> true
            status == Status.MISSING -> true
            status == Status.OUTDATED -> options.update
            else -> false
        }
        if (!shouldDownload) {
            val action = if (status == Status.OUTDATED && !options.checkOnly) Action.SKIPPED_OUTDATED else Action.NONE
            return base.copy(action = action)
        }

        val url = downloadUrl(codebase, target.binaryType, name, latestCrc)
        return try {
            println("  ↓ ${target.key}: downloading $url")
            val compressed = httpGet(url)
            val binary = LzmaAlone.decompress(compressed)

            val actualCrc = crc32(binary)
            require(actualCrc == latestCrc) {
                "CRC32 mismatch after decompress: expected $latestCrc (download_crc_0), got $actualCrc"
            }
            require(startsWith(binary, target.magic)) {
                "Unexpected magic: not a ${target.magicName} binary (got ${hexPrefix(binary)})"
            }

            val replacing = localFile.isFile
            writeAtomically(localFile, binary, target.executable)
            base.copy(
                action = if (replacing) Action.REPLACED else Action.INSTALLED,
                localCrc = actualCrc,
                installedAt = localFile,
                sizeBytes = binary.size.toLong(),
                sha256 = sha256(binary),
                sourceUrl = url,
            )
        } catch (e: Exception) {
            base.copy(action = Action.FAILED, sourceUrl = url, note = e.message)
        }
    }

    private fun writeAtomically(dest: File, data: ByteArray, executable: Boolean) {
        dest.parentFile?.mkdirs()
        val tmp = File.createTempFile(dest.name + ".", ".part", dest.parentFile)
        try {
            tmp.writeBytes(data)
            if (executable) tmp.setExecutable(true, true)
            Files.move(tmp.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING)
        } finally {
            tmp.delete()
        }
    }

    private fun httpGet(url: String): ByteArray {
        val conn = URI(url).toURL().openConnection() as HttpURLConnection
        conn.connectTimeout = 15_000
        conn.readTimeout = 60_000
        conn.instanceFollowRedirects = true
        conn.setRequestProperty("User-Agent", "Mozilla/5.0")
        try {
            val code = conn.responseCode
            if (code != 200) throw IllegalStateException("HTTP $code for $url")
            return conn.inputStream.use { it.readBytes() }
        } finally {
            conn.disconnect()
        }
    }

    companion object {
        const val JAV_CONFIG_BASE = "https://www.runescape.com/k=5/l=0/jav_config.ws"

        fun javConfigUrl(binaryType: Int) = "$JAV_CONFIG_BASE?binaryType=$binaryType"

        /** Verified live URL scheme: `{codebase}client?binaryType=N&fileName=NAME&crc=CRC`. */
        fun downloadUrl(codebase: String, binaryType: Int, name: String, crc: Long): String {
            val base = if (codebase.endsWith("/")) codebase else "$codebase/"
            return "${base}client?binaryType=$binaryType&fileName=$name&crc=$crc"
        }

        fun crc32(data: ByteArray): Long = CRC32().apply { update(data) }.value

        fun sha256(data: ByteArray): String =
            java.security.MessageDigest.getInstance("SHA-256").digest(data)
                .joinToString("") { "%02x".format(it) }

        private fun startsWith(data: ByteArray, prefix: ByteArray): Boolean {
            if (data.size < prefix.size) return false
            for (i in prefix.indices) if (data[i] != prefix[i]) return false
            return true
        }

        private fun hexPrefix(data: ByteArray, n: Int = 4): String =
            data.take(n).joinToString(" ") { "%02x".format(it) }
    }
}
