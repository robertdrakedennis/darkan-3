plugins {
    application
}

application {
    mainClass.set("org.darkan.lobby.MainKt")
}

dependencies {
    implementation(project(":core"))
}
