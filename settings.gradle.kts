rootProject.name = "darkan3"

include("core")
include("lobby")
include("world")
include("tools")

// Undercut injection engine (Kotlin/JVM + C++ native bootstrap) merged in as a first-class module.
include("engine")
include("engine:example-script-module")
// Tiny pure-Java supervisor: the permanent layer that loads/unloads the engine via a disposable
// URLClassLoader for hot-reload. The ONLY engine code on the JVM system classpath.
include("engine-supervisor")