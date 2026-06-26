import ghidra.app.script.GhidraScript;
import ghidra.program.model.listing.*;
import ghidra.app.decompiler.*;
import java.io.*;
import java.util.*;
public class DumpCat extends GhidraScript {
    public void run() throws Exception {
        LinkedHashMap<String,String> cats = new LinkedHashMap<>();
        cats.put("Interfaces","20-interfaces.md");
        cats.put("PlayerInfo","21-player.md"); cats.put("PlayerList","21-player.md"); cats.put("PlayerGroup","21-player.md");
        cats.put("Inventory","22-inventory.md");
        String base="/Users/robert/projects/darkan3-server/claude-re/findings/";
        HashMap<String,PrintWriter> ws = new HashMap<>();
        for (String fn : new LinkedHashSet<>(cats.values())) { PrintWriter w=new PrintWriter(new FileWriter(base+fn)); w.println("# Handler decompilations from the binary (headless RE)\n"); ws.put(fn,w);}    
        FunctionManager fm = currentProgram.getFunctionManager();
        DecompInterface di = new DecompInterface(); di.openProgram(currentProgram);
        int n=0;
        for (Function f : fm.getFunctions(true)) {
            String full = f.getName(true);
            for (Map.Entry<String,String> e : cats.entrySet()) {
                if (full.contains("packethandlers::"+e.getKey()+"::")) {
                    PrintWriter w = ws.get(e.getValue());
                    w.println("\n## `"+full+"` @ "+f.getEntryPoint()+"\n```c");
                    DecompileResults r = di.decompileFunction(f, 45, monitor);
                    w.println((r!=null && r.decompileCompleted())? r.getDecompiledFunction().getC() : "// decompile failed/timeout");
                    w.println("```"); n++; break;
                }
            }
        }
        for (PrintWriter w: ws.values()) w.close();
        println("CAT_DONE n="+n);
    }
}
