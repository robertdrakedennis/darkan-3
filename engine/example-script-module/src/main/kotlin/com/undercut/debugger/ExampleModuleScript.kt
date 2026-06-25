package com.undercut.debugger

import com.undercut.game.memory.NativeAccess
import com.undercut.script.Script
import com.undercut.script.ScriptDescription
import java.lang.foreign.ValueLayout.ADDRESS
import java.lang.foreign.ValueLayout.JAVA_INT

@ScriptDescription(
    name = "ExampleModuleScript",
    version = "1.0.0",
    author = "Trent",
    description = "Example script format for jarfile exporting"
)
class ExampleModuleScript : Script() {

    override suspend fun loop() {
        val base = NativeAccess.BASE_ADDR

        val compAction = base.asSlice(0x130FBA0, ADDRESS).reinterpret(Long.MAX_VALUE)

        val callback = compAction.asSlice(0x18, ADDRESS)

        val int1 = compAction.asSlice(0x20).get(JAVA_INT, 0)
        val int2 = compAction.asSlice(0x24).get(JAVA_INT, 0)

        println("Callback: 0x${callback.address().toString(16)}")
        println("Action ID: $int1")
    }

}