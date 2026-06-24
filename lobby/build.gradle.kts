plugins {
    application
}

application {
    mainClass.set("org.darkan.lobby.MainKt")
}

tasks.named<JavaExec>("run") {
    workingDir = rootProject.projectDir
}

dependencies {
    implementation(project(":core"))

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.ktor.http)
    implementation(libs.ktor.network)
    implementation(libs.ktor.websockets)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.websockets)
    implementation(libs.lzma.java)

    testImplementation(kotlin("test"))
    testImplementation(libs.kotlinx.coroutines.core)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
    // EnvVars resolves dev-default secrets (e.g. worldLoginTokenSecret, used by buildLobbyData via
    // LoginToken.issueCompact) only when DEBUG=true; otherwise requireSecret() throws. Tests run with
    // no .env, so flag the test JVM as DEBUG. dotenv-kotlin falls through to System.getenv for lookups.
    environment("DEBUG", "true")
}
