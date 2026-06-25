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

tasks.register<Copy>("copyJarToUndercutScripts") {
    from(layout.buildDirectory.dir("libs")) {
        include("*.jar")
    }
    // user.home resolved lazily (configuration-cache safe).
    into(providers.systemProperty("user.home").map { "$it/.undercut/scripts" })
}

tasks.named("build") {
    finalizedBy("copyJarToUndercutScripts")
}