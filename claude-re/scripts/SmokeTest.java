import ghidra.app.script.GhidraScript;
public class SmokeTest extends GhidraScript {
    public void run() throws Exception {
        println("SMOKE_PROGRAM=" + currentProgram.getName());
        println("SMOKE_IMAGEBASE=" + currentProgram.getImageBase());
        println("SMOKE_FUNCCOUNT=" + currentProgram.getFunctionManager().getFunctionCount());
    }
}
