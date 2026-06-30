plugins {
    application
}

application {
    mainClass.set(providers.gradleProperty("mainClass").getOrElse("org.darkan.tools.cachedownloader.MainKt"))
}

dependencies {
    implementation(project(":core"))

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.io.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.io)
    // client-updater tool: decode the LZMA-alone stream Jagex serves the NXT binary in
    // (same lzma.sdk decoder the cache library uses for container type 3).
    implementation(libs.lzma.java)
}

tasks.register<JavaExec>("rsaKeyGen") {
    mainClass.set("org.darkan.tools.keygen.RsaKeyGenKt")
    classpath = sourceSets["main"].runtimeClasspath
}

// JS5 beta-cache scanner — isolated tool that scans/diffs/downloads a JS5 host's cache
// without ever touching the live game cache. Pass flags via -Pargs="...".
//   ./gradlew :tools:betaScanner -Pargs="--host content.runescape.com --scan"
tasks.register<JavaExec>("betaScanner") {
    mainClass.set("org.darkan.tools.betascanner.MainKt")
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = rootProject.projectDir
    val rawArgs = providers.gradleProperty("args").getOrElse("")
    if (rawArgs.isNotBlank()) args(rawArgs.split(Regex("\\s+")).filter { it.isNotBlank() })
}

// OpenRS2 flat-file export -> NXT SQLite cache importer. Converts a `<index>/<group>.dat` flat
// export into the js5-N.jcache SQLite databases SQLiteCache.load reads (strips the OpenRS2 version
// suffix, stores 255/N.dat as each index's ref table). Pass flags via -Pargs="...".
//   ./gradlew :tools:openrs2Import -Pargs="--source ~/darkan-3/openrs2-948/cache --out ~/darkan-3/948-sqlite-cache"
tasks.register<JavaExec>("openrs2Import") {
    mainClass.set("org.darkan.tools.openrs2.OpenRs2ImporterKt")
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = rootProject.projectDir
    val rawArgs = providers.gradleProperty("args").getOrElse("")
    if (rawArgs.isNotBlank()) args(rawArgs.split(Regex("\\s+")).filter { it.isNotBlank() })
}

// Cache smoke-test — load a built SQLite cache (default ./data/cache), report index count and
// decode well-known definitions (canonical spot-check: item 315 == "Shrimps"). Exits non-zero on
// any failure, so it doubles as a regen gate.
//   ./gradlew :tools:cacheVerify -Pargs="~/darkan-3/948-sqlite-cache"
tasks.register<JavaExec>("cacheVerify") {
    mainClass.set("org.darkan.tools.openrs2.CacheVerifyKt")
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = rootProject.projectDir
    val rawArgs = providers.gradleProperty("args").getOrElse("")
    if (rawArgs.isNotBlank()) args(rawArgs.split(Regex("\\s+")).filter { it.isNotBlank() })
}

// Appearance cache inspector — dump the human Body/Wearpos def (slot/colour/style counts) and validate
// the default identitykit + worn item ids the PLAYER_INFO APPEARANCE encoder emits.
//   ./gradlew :tools:appearanceInspect -Pargs="/Users/robert/darkan-3/948-sqlite-cache"
tasks.register<JavaExec>("appearanceInspect") {
    mainClass.set("org.darkan.tools.AppearanceCacheInspectKt")
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = rootProject.projectDir
    val rawArgs = providers.gradleProperty("args").getOrElse("")
    if (rawArgs.isNotBlank()) args(rawArgs.split(Regex("\\s+")).filter { it.isNotBlank() })
}

// Avatar kit/model resolution verifier (THROWAWAY) — decode each default identitykit and resolve its
// model ids against the MODELS index (7) in OUR served cache; prints a decisive avatar-invisible verdict.
//   ./gradlew :tools:avatarModelVerify -Pargs="/Users/robert/darkan-3/948-sqlite-cache"
tasks.register<JavaExec>("avatarModelVerify") {
    mainClass.set("org.darkan.tools.AvatarModelResolveVerifyKt")
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = rootProject.projectDir
    val rawArgs = providers.gradleProperty("args").getOrElse("")
    if (rawArgs.isNotBlank()) args(rawArgs.split(Regex("\\s+")).filter { it.isNotBlank() })
}

// Gameval JSON exporter — decode cache index 67 (gameval/RSCM) into our own prettified per-type JSON.
//   ./gradlew :tools:gamevalExport -Pargs="--cache ./data/betacache --out re-resources/gamevals"
tasks.register<JavaExec>("gamevalExport") {
    mainClass.set("org.darkan.tools.gamevalexport.MainKt")
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = rootProject.projectDir
    val rawArgs = providers.gradleProperty("args").getOrElse("")
    if (rawArgs.isNotBlank()) args(rawArgs.split(Regex("\\s+")).filter { it.isNotBlank() })
}

// Offline session enricher — turn a recorder session dir into named/decoded/filtered JSONL.
//   ./gradlew :tools:enrichSession -Pargs="~/.undercut/recordings/session-<...>"
tasks.register<JavaExec>("enrichSession") {
    mainClass.set("org.darkan.tools.recorder.EnrichSessionKt")
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = rootProject.projectDir
    val rawArgs = providers.gradleProperty("args").getOrElse("")
    if (rawArgs.isNotBlank()) args(rawArgs.split(Regex("\\s+")).filter { it.isNotBlank() })
}

// Capture trust report — prove a recorder session is complete and coherent (byte-accounting +
// clean-decode + sanity), emitting <session>/trust-report.{md,json} with a PASS/WARN/FAIL verdict.
//   ./gradlew :tools:trustReport -Pargs="~/.undercut/recordings/session-<...>"
tasks.register<JavaExec>("trustReport") {
    mainClass.set("org.darkan.tools.recorder.TrustReportKt")
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = rootProject.projectDir
    val rawArgs = providers.gradleProperty("args").getOrElse("")
    if (rawArgs.isNotBlank()) args(rawArgs.split(Regex("\\s+")).filter { it.isNotBlank() })
}

// Self-test for the trust verifier (synthesizes byte-exact sessions, asserts each verdict). The
// :tools module has no JUnit harness, so this follows the module's main()-with-counters convention.
//   ./gradlew :tools:trustReportSelfTest
tasks.register<JavaExec>("trustReportSelfTest") {
    mainClass.set("org.darkan.tools.recorder.TrustReportSelfTestKt")
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = rootProject.projectDir
}

// Handler-fingerprint naming agreement PROOF — names opcodes by their handler's byte-sig (rev-stable)
// and checks the result agrees with register948 on 948. Runs synthetic-from-authority by default, or
// against a real capture's prot-table.json when given one.
//   ./gradlew :tools:handlerNamingProof [-Pargs="<session-dir>"]
tasks.register<JavaExec>("handlerNamingProof") {
    mainClass.set("org.darkan.tools.recorder.HandlerNamingProofKt")
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = rootProject.projectDir
    val rawArgs = providers.gradleProperty("args").getOrElse("")
    if (rawArgs.isNotBlank()) args(rawArgs.split(Regex("\\s+")).filter { it.isNotBlank() })
}

// Self-test for handler-fingerprint naming: synthetic table -> correct names, RENUMBERED-opcode still
// named correctly (cross-rev guarantee), no-prot-table -> register948 fallback. main()-with-counters.
//   ./gradlew :tools:handlerNamerSelfTest
tasks.register<JavaExec>("handlerNamerSelfTest") {
    mainClass.set("org.darkan.tools.recorder.HandlerNamerSelfTestKt")
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = rootProject.projectDir
}

// FAITHFUL-REPLICATION walk/stop ext-info probe — decodes what the server sends the LOCAL player per
// op22 tick (movementType + ext-info bit-0x20 MOVEMENT_ANIM + bit-0x80 FORCED_MOVEMENT) from a
// recorder capture's framed-s2c.jsonl, deriving the local slot index from the wire.
//   ./gradlew :tools:walkExtInfo -Pargs="<captureDir> [<captureDir2> ...]"
tasks.register<JavaExec>("walkExtInfo") {
    mainClass.set("org.darkan.tools.recorder.WalkExtInfoProbeKt")
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = rootProject.projectDir
    val rawArgs = providers.gradleProperty("args").getOrElse("")
    if (rawArgs.isNotBlank()) args(rawArgs.split(Regex("\\s+")).filter { it.isNotBlank() })
}

tasks.named<JavaExec>("run") {
    workingDir = rootProject.projectDir
}
