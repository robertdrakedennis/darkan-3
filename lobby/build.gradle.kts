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
}
