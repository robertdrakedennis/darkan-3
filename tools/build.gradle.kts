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
    // client-updater tool: decode the LZMA-alone stream Jagex serves the NXT binary in
    // (same lzma.sdk decoder the cache library uses for container type 3).
    implementation(libs.lzma.java)
}

tasks.register<JavaExec>("rsaKeyGen") {
    mainClass.set("org.darkan.tools.keygen.RsaKeyGenKt")
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.named<JavaExec>("run") {
    workingDir = rootProject.projectDir
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
