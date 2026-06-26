import org.gradle.language.jvm.tasks.ProcessResources

plugins {
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    // api: these types appear in core's public API (buffer/codec/model/mongo signatures),
    // so dependent modules (lobby/world/tools) need them on their compile classpath.
    api(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.serialization.json)
    api(libs.kotlinx.io.core)
    api(libs.ktor.io)
    api(libs.mongodb.driver.kotlin.coroutine)
    api(libs.fastutil)

    implementation(libs.kotlin.reflect)
    implementation(libs.argon2.jvm)
    implementation(libs.dotenv.kotlin)
    implementation(libs.lzma.java)
    implementation(libs.classgraph)
    implementation(libs.sqlite.jdbc)

    // Embedded MongoDB for dev runs (EMBEDDED_MONGO=true). Downloads + manages a
    // real mongod binary inside the JVM so :lobby:run is self-contained on dev
    // hosts that don't have mongod installed. Stays out of the prod path.
    implementation(libs.flapdoodle.embed.mongo)

    testImplementation(kotlin("test"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

// Bundle the RE gameval id<->name dictionaries (re-resources/gamevals/*.json, a submodule) into
// :core's jar under /gamevals so the Gameval facility (world.gregs.voidps.gameval) can load them
// from the classpath at runtime. This makes the names available to the server AND to the injected
// engine shadowJar (which has no repo files at runtime) without any external file dependency.
tasks.named<ProcessResources>("processResources") {
    from(rootProject.file("re-resources/gamevals")) {
        into("gamevals")
        include("*.json")
    }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}
