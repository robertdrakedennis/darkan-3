plugins {
    application
}

application {
    mainClass.set(providers.gradleProperty("mainClass").getOrElse("org.darkan.tools.cachedownloader.MainKt"))
}

dependencies {
    implementation(project(":core"))
}

tasks.register<JavaExec>("rsaKeyGen") {
    mainClass.set("org.darkan.tools.keygen.RsaKeyGenKt")
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.named<JavaExec>("run") {
    workingDir = rootProject.projectDir
}

// Framing-regression hard gate: decodes real captures in BOTH directions and asserts every
// packet SIZE is correct (zero desync, full byte consumption). Nonzero exit on any failure.
// Not wired into `build` yet — Phase 5 does the merge-gate wiring.
tasks.register<JavaExec>("framingRegression") {
    group = "verification"
    description = "Decode captures in both directions and assert zero desync + full byte consumption."
    mainClass.set("org.darkan.tools.FramingRegressionKt")
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = rootProject.projectDir
    // Forward CLI args (e.g. an explicit capture session dir): -PframingArgs="capture/login-..._s1"
    (project.findProperty("framingArgs") as String?)?.let { args(it.split(" ")) }
}

// Phase 4 wire-format gate: byte-verify that every registered encoder/decoder reproduces the
// LIVE capture's bytes exactly. Nonzero exit on any FAIL (a FAIL is a real wire-format bug).
tasks.register<JavaExec>("wireFormatVerify") {
    group = "verification"
    description = "Byte-verify registered encoders/decoders against the live capture's bytes."
    mainClass.set("org.darkan.tools.WireFormatVerifyKt")
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = rootProject.projectDir
}

// HARD GATE: `./gradlew check` (and CI) must run both capture-regression gates. Either failing
// (a size desync or a wire-format byte mismatch against the committed live capture) fails the build.
// This is what makes silent protocol regressions impossible after a future revision bump or edit.
tasks.named("check") {
    dependsOn("framingRegression", "wireFormatVerify")
}