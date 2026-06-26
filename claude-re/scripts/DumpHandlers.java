import ghidra.app.script.GhidraScript;
import ghidra.program.model.listing.*;
import java.io.*;
import java.util.*;
public class DumpHandlers extends GhidraScript {
    public void run() throws Exception {
        PrintWriter w = new PrintWriter(new FileWriter("/Users/robert/projects/darkan3-server/claude-re/findings/11-handlers-all.md"));
        w.println("# Named packet handlers from the binary (the packet inventory)\n");
        FunctionManager fm = currentProgram.getFunctionManager();
        TreeMap<String,TreeMap<String,String>> byCat = new TreeMap<>();
        int n=0;
        for (Function f : fm.getFunctions(true)) {
            String full = f.getName(true);
            String low = full.toLowerCase();
            if (low.contains("packethandlers")) {
                // category = component after 'packethandlers::'
                String cat="?";
                int i = full.indexOf("packethandlers::");
                if (i>=0) { String rest=full.substring(i+16); int j=rest.indexOf("::"); cat = j>=0?rest.substring(0,j):rest; }
                byCat.computeIfAbsent(cat,k->new TreeMap<>()).put(full, f.getEntryPoint().toString());
                n++;
            }
        }
        for (Map.Entry<String,TreeMap<String,String>> c : byCat.entrySet()) {
            w.println("\n## "+c.getKey()+" ("+c.getValue().size()+")\n");
            for (Map.Entry<String,String> e : c.getValue().entrySet()) w.println("- `"+e.getKey()+"` @ "+e.getValue());
        }
        w.println("\n**Total: "+n+" packethandlers functions in "+byCat.size()+" categories**");
        w.close();
        println("HANDLERS_DONE n="+n+" cats="+byCat.size());
    }
}
