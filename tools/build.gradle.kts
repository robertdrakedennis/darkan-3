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
    implementation(libs.ktor.io)
}

tasks.register<JavaExec>("rsaKeyGen") {
    mainClass.set("org.darkan.tools.keygen.RsaKeyGenKt")
    classpath = sourceSets["main"].runtimeClasspath
}

// Converts an openrs2 directory-format cache into NXT js5-*.jcache files.
// Usage: ./gradlew :tools:openRS2Import -PimportArgs="<inputCacheDir> <outputDir>"
tasks.register<JavaExec>("openRS2Import") {
    mainClass.set("org.darkan.tools.OpenRS2ImportKt")
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = rootProject.projectDir
    providers.gradleProperty("importArgs").orNull?.let { args(it.split(" ")) }
}

// Loads a converted js5-*.jcache cache through SQLiteCache.load and asserts consistency.
// Usage: ./gradlew :tools:openRS2ImportVerify -PverifyArgs="<outputDir> <expectedIndexCount>"
tasks.register<JavaExec>("openRS2ImportVerify") {
    mainClass.set("org.darkan.tools.OpenRS2ImportVerifyKt")
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = rootProject.projectDir
    providers.gradleProperty("verifyArgs").orNull?.let { args(it.split(" ")) }
}

tasks.named<JavaExec>("run") {
    workingDir = rootProject.projectDir
}

// Offline deframer for libdarkan_recorder.dylib captures: reads the binary
// capture + ISAAC seeds, reconstructs per-fd streams, ISAAC-deframes the game
// stream and emits an annotated JSONL transcript via register948().
// Usage:
//   ./gradlew :tools:recorderDeframe \
//     -PdeframeArgs="<capture.bin> --out transcript.jsonl --strict [--seeds s0,s1,s2,s3] [--isaac-offset auto]"
tasks.register<JavaExec>("recorderDeframe") {
    group = "verification"
    description = "Deframe a recorder capture into an annotated JSONL packet transcript."
    mainClass.set("org.darkan.tools.recorder.RecorderDeframe")
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = rootProject.projectDir
    providers.gradleProperty("deframeArgs").orNull?.let { args(it.split(" ")) }
}

// Synthetic round-trip self-test: encodes a known ServerProt/ClientProt sequence
// the exact way the server does, wraps it in a capture file, runs the deframer,
// and asserts the decode matches. Proves the pipeline without the client/dylib.
// Usage: ./gradlew :tools:recorderSelfTest
tasks.register<JavaExec>("recorderSelfTest") {
    group = "verification"
    description = "Synthetic capture → deframe → assert decoded == known input (no client needed)."
    mainClass.set("org.darkan.tools.recorder.RecorderSelfTestKt")
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = rootProject.projectDir
}

// Wire-level HTTP-exchange analyzer for the macOS recorder capture: streams the
// capture and isolates /ms HTTP exchanges by fd, preserving recv() boundaries,
// connect/close lifecycle, and the exact Content-Length-vs-body comparison.
// Usage:
//   ./gradlew :tools:httpExchangeAnalyze -PhttpArgs="<capture.bin> [--filter a=40] [--max 8] [--port 8829]"
tasks.register<JavaExec>("httpExchangeAnalyze") {
    group = "verification"
    description = "Stream a recorder capture and analyze /ms HTTP exchanges (Content-Length vs body, lifecycle, cadence)."
    mainClass.set("org.darkan.tools.recorder.HttpExchangeAnalyze")
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = rootProject.projectDir
    providers.gradleProperty("httpArgs").orNull?.let { args(it.split(" ")) }
}

// Import Undercut macOS JSONL recorder output as a redacted, phase-labelled protocol oracle.
// Usage:
//   ./gradlew :tools:undercutLoginFlowImport \
//     -PundercutFlowArgs="/Users/robert/.undercut/recordings/login-.../events.jsonl --out build/undercut-flow.jsonl --require-full-login --require-world-traffic"
tasks.register<JavaExec>("undercutLoginFlowImport") {
    group = "verification"
    description = "Summarize Undercut login/session JSONL into phase-labelled packet/socket evidence."
    mainClass.set("org.darkan.tools.recorder.UndercutLoginFlowImport")
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = rootProject.projectDir
    providers.gradleProperty("undercutFlowArgs").orNull?.let { args(it.split(" ")) }
}

// Deframe Undercut macOS JSONL socket captures through the same ISAAC/login pipeline
// as recorderDeframe. Pass server-logged seeds for Darkan/private runs.
// Usage:
//   ./gradlew :tools:undercutSocketDeframe \
//     -PundercutSocketArgs="/Users/robert/.undercut/recordings/login-.../events.jsonl --out build/undercut-socket.jsonl --seeds s0,s1,s2,s3 --strict"
tasks.register<JavaExec>("undercutSocketDeframe") {
    group = "verification"
    description = "Deframe Undercut JSONL socket streams into an annotated packet transcript."
    mainClass.set("org.darkan.tools.recorder.UndercutSocketDeframe")
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = rootProject.projectDir
    providers.gradleProperty("undercutSocketArgs").orNull?.let { args(it.split(" ")) }
}

tasks.register<JavaExec>("undercutSocketDeframeSelfTest") {
    group = "verification"
    description = "Synthetic Undercut JSONL socket capture -> deframe -> assert decoded packets."
    mainClass.set("org.darkan.tools.recorder.UndercutSocketDeframeSelfTestKt")
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = rootProject.projectDir
}

// Protocol-level world-login probe — drives the lobby→world handshake against a LIVE world server
// (the headless stand-in for a human "Play Now" click). Asserts the 9-byte INIT, GAMELOGIN accept,
// SUCCESS(2), and login-data. Usage:
//   ./gradlew :tools:worldLoginProbe [-PworldHost=localhost -PworldPort=43597 -PprobeUser=probeplayer]
tasks.register<JavaExec>("worldLoginProbe") {
    group = "verification"
    description = "Run the lobby→world login handshake against a live world server and assert each milestone."
    mainClass.set("org.darkan.tools.WorldLoginProbeKt")
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = rootProject.projectDir
    providers.gradleProperty("worldHost").orNull?.let { systemProperty("worldHost", it) }
    providers.gradleProperty("worldPort").orNull?.let { systemProperty("worldPort", it) }
    providers.gradleProperty("probeUser").orNull?.let { systemProperty("probeUser", it) }
}

// The capture gates need the (gitignored) capture/ directory. On machines without capture
// data they skip with a warning instead of failing the build — UNLESS -PrequireCaptures is
// set (CI machines that are expected to have captures), in which case they hard-fail as before.

// Framing-regression hard gate: decodes real captures in BOTH directions and asserts every
// packet SIZE is correct (zero desync, full byte consumption). Nonzero exit on any failure.
tasks.register<JavaExec>("framingRegression") {
    group = "verification"
    description = "Decode captures in both directions and assert zero desync + full byte consumption."
    mainClass.set("org.darkan.tools.FramingRegressionKt")
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = rootProject.projectDir
    // Forward CLI args (e.g. an explicit capture session dir): -PframingArgs="capture/login-..._s1"
    providers.gradleProperty("framingArgs").orNull?.let { args(it.split(" ")) }
    val captureRoot = rootProject.projectDir.resolve("capture")
    val requireCaptures = providers.gradleProperty("requireCaptures").isPresent
    onlyIf("capture sessions present or -PrequireCaptures set") { task ->
        val present = captureRoot.isDirectory &&
            captureRoot.listFiles()?.any { it.isDirectory && it.resolve("isaac-keys.txt").isFile } == true
        if (!present && !requireCaptures) {
            task.logger.warn("framingRegression SKIPPED: no capture sessions under $captureRoot (pass -PrequireCaptures to hard-fail instead)")
        }
        present || requireCaptures
    }
}

// Phase 4 wire-format gate: byte-verify that every registered encoder/decoder reproduces the
// LIVE capture's bytes exactly. Nonzero exit on any FAIL (a FAIL is a real wire-format bug).
tasks.register<JavaExec>("wireFormatVerify") {
    group = "verification"
    description = "Byte-verify registered encoders/decoders against the live capture's bytes."
    mainClass.set("org.darkan.tools.WireFormatVerifyKt")
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = rootProject.projectDir
    // Override the capture session dir: -PwireFormatCapture="capture/login-..._s1"
    providers.gradleProperty("wireFormatCapture").orNull?.let { args(it) }
    val captureRoot = rootProject.projectDir.resolve("capture")
    val requireCaptures = providers.gradleProperty("requireCaptures").isPresent
    onlyIf("capture sessions present or -PrequireCaptures set") { task ->
        val present = captureRoot.isDirectory &&
            captureRoot.listFiles()?.any { it.isDirectory && it.resolve("isaac-keys.txt").isFile } == true
        if (!present && !requireCaptures) {
            task.logger.warn("wireFormatVerify SKIPPED: no capture sessions under $captureRoot (pass -PrequireCaptures to hard-fail instead)")
        }
        present || requireCaptures
    }
}

// HARD GATE: `./gradlew check` (and CI) must run both capture-regression gates. Either failing
// (a size desync or a wire-format byte mismatch against the committed live capture) fails the build.
// On machines without capture/ the gates skip with a warning (see above) unless -PrequireCaptures.
tasks.named("check") {
    dependsOn("framingRegression", "wireFormatVerify")
}
