plugins {
    // kotlin.jvm + java-library come from the root `subprojects {}` block; repositories from
    // `allprojects {}`. Only the application plugin is module-specific here.
    application
}

group = "com.undercut"

application {
    mainClass.set("com.undercut.ApplicationKt")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    implementation(project(":engine"))
}

val isWindows: Boolean
    get() = System.getProperty("os.name").contains("Windows", ignoreCase = true)

val wslHome: String by lazy {
    // This retrieves the WSL home directory using the `wsl` command
    "wsl echo ~".runCommand().trim()
}

// Utility function to run shell commands from Gradle
fun String.runCommand(): String {
    return ProcessBuilder(*this.split(" ").toTypedArray())
        .redirectErrorStream(true)
        .start()
        .inputStream
        .bufferedReader()
        .readText()
}

tasks.register("copyToWSLHome") {
    onlyIf { isWindows && wslHome.isNotEmpty() }
    doLast {
        val sourceDir = "build/libs/debugger.jar"
        val targetDir = "$wslHome/"
        ProcessBuilder("wsl", "cp", sourceDir, targetDir)
            .inheritIO()
            .start()
            .waitFor()
        println("Copied files to WSL directory: $targetDir")
    }
}

tasks.register<Copy>("copyJarToUndercutScripts") {
    onlyIf { !isWindows }

    from(layout.buildDirectory.dir("libs")) {
        include("*.jar")
    }
    // user.home resolved lazily (configuration-cache safe).
    into(providers.systemProperty("user.home").map { "$it/.undercut/scripts" })
}

tasks.named("build") {
    finalizedBy("copyToWSLHome") // Run copyToWSLHome after the build task
    finalizedBy("copyJarToUndercutScripts")
}