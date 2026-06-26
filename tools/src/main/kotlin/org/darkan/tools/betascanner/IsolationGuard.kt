package org.darkan.tools.betascanner

import org.darkan.core.EnvVars
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Hard safety boundary for the beta scanner: the live game cache must NEVER be the
 * write target. Every protected location is absolutized + normalized, and the resolved
 * `--out` is rejected if it equals, sits inside, or contains any of them.
 */
object IsolationGuard {

    class ForbiddenOutputException(message: String) : RuntimeException(message)

    /** All directories the scanner must never write into (absolute, normalized). */
    fun forbiddenPaths(): List<Path> {
        val home = System.getProperty("user.home")
        val launcherRoot = "$home/.local/share/darkan-launcher"
        val raw = listOf(
            EnvVars.cachePath,                       // the server's live cache (CACHE_PATH, default ./data/cache)
            "./data/cache",
            "$home/Jagex/RuneScape",                 // NXT client cache (HOME-redirected)
            "$launcherRoot/Jagex/RuneScape",         // launcher live-mode cache
            "$launcherRoot/custom/Jagex/RuneScape",  // launcher custom-server cache
            "$launcherRoot/custom",                  // launcher custom data dir
            launcherRoot,                            // launcher data root
            "$home/.darkan3/Jagex/RuneScape",        // legacy darkan3 client cache
            "$home/.darkan3",
        )
        return raw.map { Paths.get(it).toAbsolutePath().normalize() }.distinct()
    }

    /**
     * Throws [ForbiddenOutputException] when [outAbs] (already absolutized + normalized)
     * is a protected path, lives inside one, or would contain one.
     */
    fun check(outAbs: Path) {
        for (forbidden in forbiddenPaths()) {
            if (outAbs == forbidden) {
                throw ForbiddenOutputException(
                    "--out resolves to a protected cache path: $outAbs"
                )
            }
            if (outAbs.startsWith(forbidden)) {
                throw ForbiddenOutputException(
                    "--out ($outAbs) is INSIDE a protected cache path: $forbidden"
                )
            }
            if (forbidden.startsWith(outAbs)) {
                throw ForbiddenOutputException(
                    "--out ($outAbs) CONTAINS a protected cache path: $forbidden. Pick an isolated directory."
                )
            }
        }
    }
}
