import ghidra.app.decompiler.DecompInterface;
import ghidra.app.decompiler.DecompileResults;
import ghidra.app.script.GhidraScript;
import ghidra.program.model.listing.Function;
import ghidra.program.model.listing.FunctionManager;
import java.io.FileWriter;
import java.io.PrintWriter;

public class DumpJs5 extends GhidraScript {
    @Override
    public void run() throws Exception {
        DecompInterface decompiler = new DecompInterface();
        decompiler.openProgram(currentProgram);
        FunctionManager functions = currentProgram.getFunctionManager();
        try (PrintWriter out = new PrintWriter(new FileWriter("/Users/robert/projects/darkan3-server/claude-re/findings/24-js5.md"))) {
            out.println("# JS5 functions");
            int count = 0;
            for (Function function : functions.getFunctions(true)) {
                String name = function.getName(true);
                if (!name.contains("Js5")) {
                    continue;
                }
                out.println();
                out.println("## `" + name + "` @ " + function.getEntryPoint());
                out.println();
                out.println("```c");
                DecompileResults result = decompiler.decompileFunction(function, 30, monitor);
                if (result != null && result.decompileCompleted()) {
                    out.println(result.getDecompiledFunction().getC());
                } else {
                    out.println("// decompile failed");
                }
                out.println("```");
                count++;
            }
            out.println();
            out.println("count=" + count);
            println("JS5_DONE count=" + count);
        }
    }
}
