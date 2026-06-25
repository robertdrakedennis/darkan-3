rootProject.name = "darkan3"

include("core")
include("lobby")
include("world")
include("tools")

// Undercut injection engine (Kotlin/JVM + C++ native bootstrap) merged in as a first-class module.
include("engine")
include("engine:example-script-module")