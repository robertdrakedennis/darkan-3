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
    // Shared networking protocol (packet opcodes/sizes/names + structured codecs + Isaac) lives in
    // :core — the engine consumes it instead of duplicating it. The server-only transitive deps
    // (MongoDB driver, embedded mongod, argon2) are excluded: the engine never touches the DB/auth
    // layer, so they would only bloat the dlopen-injected shadow JAR.
    implementation(project(":core")) {
        exclude(group = "org.mongodb")
        exclude(group = "de.flapdoodle.embed")
        exclude(group = "de.mkammerer")
    }

    // EngineHandle (the hot-reload boundary type) is provided at runtime by the supervisor jar on
    // the system classpath — compileOnly so it is NOT bundled into the shadow jar, otherwise the
    // child URLClassLoader would define its own copy and the (EngineHandle) cast would fail.
    compileOnly(project(":engine-supervisor"))

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

val javaHomeProvider = providers.environmentVariable("JAVA_HOME")
    .orElse(providers.systemProperty("java.home"))

tasks.register<Exec>("configureNativeBootstrap") {
    // Invokes cmake via Exec, feeding JAVA_HOME (for the JNI headers) read through the providers
    // API so the value becomes a configuration-cache input rather than a captured script reference.
    workingDir = file("./native-bootstrap")
    commandLine = listOf("cmake", "-B", "build", "-DCMAKE_BUILD_TYPE=Debug")
    environment("JAVA_HOME", javaHomeProvider.get())
}

tasks.register<Exec>("buildNativeBootstrap") {
    dependsOn("configureNativeBootstrap")
    workingDir = file("./native-bootstrap/build")

    commandLine = listOf("cmake", "--build", ".", "--target", "all")

    // Exec streams cmake output to the console by default; assigning System.out/err here would
    // capture non-serializable PrintStreams and force the configuration cache to be discarded.
    isIgnoreExitValue = true
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
    // EngineHandle must exist ONLY in the supervisor jar (the shared parent loader). If the shadow
    // jar also carried it, the child loader would define a second copy → ClassCastException on the
    // (EngineHandle) cast in Supervisor.
    exclude("com/undercut/supervisor/**")
}

// Place the pure-Java supervisor jar next to the engine shadow jar (UNDERCUT_HOME_DIR) under a
// fixed name so the native bootstrap can put it (and only it) on the JVM system classpath.
// Declare a precise single-file output (NOT a Copy into build/libs, which would claim the whole
// dir as output and trip Gradle's implicit-dependency check against startScripts/dist tasks that
// read the engine jars from the same dir).
val supervisorJarFile = project(":engine-supervisor").tasks.named<Jar>("jar").flatMap { it.archiveFile }
tasks.register("copySupervisorJar") {
    val src = supervisorJarFile
    val dst = layout.buildDirectory.file("libs/undercut-supervisor.jar")
    inputs.file(src)
    outputs.file(dst)
    doLast {
        src.get().asFile.copyTo(dst.get().asFile.apply { parentFile.mkdirs() }, overwrite = true)
    }
}

tasks.named("assemble") { dependsOn("copySupervisorJar") }
