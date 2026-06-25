plugins {
    application
    // kotlin.jvm + java-library are applied to every module by the root `subprojects {}` block,
    // and the Kotlin version comes from the shared version catalog (2.3.20). Only the
    // engine-specific plugins are declared here.
    alias(libs.plugins.kotlin.serialization)
    id("com.gradleup.shadow") version "9.3.1"
}

group = "com.undercut"
version = "1.0.0"

// Preserve the standalone artifact name (com.undercut-1.0.0-all.jar) referenced by the inject
// script and the unified launcher, even though this is now the :engine subproject of darkan3.
base { archivesName.set("com.undercut") }

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

application {
    mainClass.set("com.undercut.ApplicationKt")
    applicationDefaultJvmArgs = listOf("--enable-preview")
}

// repositories are declared centrally by the root `allprojects {}` block (mavenLocal + mavenCentral).

dependencies {
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("io.github.classgraph:classgraph:4.8.177")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    implementation("org.xerial:sqlite-jdbc:3.46.1.3")
    implementation("org.apache.commons:commons-compress:1.28.0")
    implementation("com.github.weisj:darklaf-core:3.0.2")
    implementation("com.formdev:flatlaf:3.4")
    implementation("com.formdev:flatlaf-intellij-themes:3.2")
    // MCP server for live memory diagnostics (accessed by Claude Code)
    implementation("io.modelcontextprotocol:kotlin-sdk:0.8.4")
    implementation("io.ktor:ktor-server-cio:3.1.2")
    implementation("io.ktor:ktor-server-sse:3.1.2")
    implementation("com.microsoft.onnxruntime:onnxruntime:1.17.0")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs("--enable-preview")
}

val isWindows: Boolean
    get() = System.getProperty("os.name").contains("Windows", ignoreCase = true)

val wslHome: String by lazy {
    "wsl echo ~".runCommand().trim()
}

fun String.runCommand(): String {
    return ProcessBuilder(*this.split(" ").toTypedArray())
        .redirectErrorStream(true)
        .start()
        .inputStream
        .bufferedReader()
        .readText()
}

val javaHomeProvider = providers.environmentVariable("JAVA_HOME")
    .orElse(providers.systemProperty("java.home"))

tasks.register<Exec>("configureNativeBootstrap") {
    onlyIf { !isWindows }
    // Invokes cmake via Exec and feeds JAVA_HOME (for the JNI headers). External-process tasks
    // aren't configuration-cache cacheable; this only disables the cache when the task is actually
    // SCHEDULED, so server-module builds (which merely configure :engine) keep their cache.
    // JAVA_HOME is read through the providers API so even configuring this task doesn't break
    // those builds' configuration cache.
    notCompatibleWithConfigurationCache("invokes cmake via Exec with a JAVA_HOME environment")
    workingDir = file("./native-bootstrap")
    commandLine = listOf("cmake", "-B", "build", "-DCMAKE_BUILD_TYPE=Debug")
    environment("JAVA_HOME", javaHomeProvider.get())
}

tasks.register<Exec>("buildNativeBootstrap") {
    onlyIf { !isWindows }
    notCompatibleWithConfigurationCache("runs the cmake native build via Exec and streams to System.out")
    dependsOn("configureNativeBootstrap")
    workingDir = file("./native-bootstrap/build")

    commandLine = listOf("cmake", "--build", ".", "--target", "all")

    isIgnoreExitValue = true
    standardOutput = System.out
    errorOutput = System.err
}

tasks.named("buildNativeBootstrap") {
    finalizedBy("copyNativeLibToBuild")
}

tasks.register<Copy>("copyNativeLibToBuild") {
    from(file("./native-bootstrap/build/libundercutbootstrap.so"))
    into(file("build/libs"))

    dependsOn("buildNativeBootstrap")
}

tasks.withType<Zip> {
    dependsOn("copyNativeLibToBuild")
}

tasks.withType<Tar> {
    dependsOn("copyNativeLibToBuild")
}

tasks.named("startShadowScripts") {
    dependsOn("copyNativeLibToBuild")
}

tasks.register("copyToWSLHome") {
    onlyIf { isWindows && wslHome.isNotEmpty() }
    notCompatibleWithConfigurationCache("Windows/WSL copy via ProcessBuilder")
    doLast {
        val sourceDir = "build/libs/com.undercut-1.0.0-all.jar"
        val targetDir = "$wslHome/"
        ProcessBuilder("wsl", "cp", sourceDir, targetDir)
            .inheritIO()
            .start()
            .waitFor()
        println("Copied files to WSL directory: $targetDir")
    }
}

tasks.named("build") {
    //dependsOn("buildNativeBootstrap")
    finalizedBy("copyToWSLHome")
}

tasks.register<JavaExec>("dumpQuestVarbits") {
    group = "build"
    description = "Dump per-quest MasterQuestVar data from the cache to developer-info/quest-varbits-dump.txt"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.undercut.tools.cachedump.DumpQuestVarbitsMain")
    workingDir = projectDir
    jvmArgs = listOf("--enable-preview")
}

tasks.register<JavaExec>("inspectNpcs") {
    group = "build"
    description = "Print NPCType data for specific NPC ids"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.undercut.tools.cachedump.InspectNpcsMain")
    workingDir = projectDir
    jvmArgs = listOf("--enable-preview")
}


tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    from("src/main/resources") {
        include("**/*")
    }
}
