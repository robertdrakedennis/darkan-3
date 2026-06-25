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

tasks.named<JavaExec>("run") {
    workingDir = rootProject.projectDir
}
