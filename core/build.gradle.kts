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

    testImplementation(kotlin("test"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}
