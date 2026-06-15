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
}
