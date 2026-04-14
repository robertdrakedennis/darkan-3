plugins {
    application
}

application {
    mainClass.set("org.darkan.world.MainKt")
}

tasks.named<JavaExec>("run") {
    workingDir = rootProject.projectDir
}

dependencies {
    implementation(project(":core"))
}
