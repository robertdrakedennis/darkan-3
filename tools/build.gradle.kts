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

// Gameval JSON exporter — decode cache index 67 (gameval/RSCM) into our own prettified per-type JSON.
//   ./gradlew :tools:gamevalExport -Pargs="--cache ./data/betacache --out re-resources/gamevals"
tasks.register<JavaExec>("gamevalExport") {
    mainClass.set("org.darkan.tools.gamevalexport.MainKt")
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = rootProject.projectDir
    val rawArgs = providers.gradleProperty("args").getOrElse("")
    if (rawArgs.isNotBlank()) args(rawArgs.split(Regex("\\s+")).filter { it.isNotBlank() })
}

tasks.named<JavaExec>("run") {
    workingDir = rootProject.projectDir
}
