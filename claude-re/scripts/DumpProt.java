import ghidra.app.script.GhidraScript;
import ghidra.program.model.listing.*;
import ghidra.app.decompiler.*;
import java.io.*;
import java.util.*;

public class DumpProt extends GhidraScript {
    public void run() throws Exception {
        File out = new File("/Users/robert/projects/darkan3-server/claude-re/findings/10-prot-registration.md");
        PrintWriter w = new PrintWriter(new FileWriter(out));
        w.println("# Opcode registration / packet-handler functions (binary, headless RE)\n");
        FunctionManager fm = currentProgram.getFunctionManager();
        String[] pats = {"registerall","bindhandler","binddecoder","clientprot","serverprot",
                         "packethandler","registerprot","bindprot","prothandler","opcode"};
        List<Function> all = new ArrayList<>();
        List<Function> regs = new ArrayList<>();
        for (Function f : fm.getFunctions(true)) {
            String nm = f.getName().toLowerCase();
            boolean hit=false; for (String p: pats) if (nm.contains(p)) { hit=true; break; }
            if (hit) { all.add(f);
                if (nm.contains("registerall")||nm.contains("bindhandler")||nm.contains("bindhandlers")
                    ||(nm.contains("register")&&(nm.contains("prot")||nm.contains("handler")))) regs.add(f);
            }
        }
        w.println("## Candidate functions ("+all.size()+")\n");
        for (Function f: all) w.println("- `"+f.getName()+"` @ "+f.getEntryPoint());
        DecompInterface di = new DecompInterface(); di.openProgram(currentProgram);
        int dec=0;
        for (Function f: regs) {
            if (dec>=6) break;  // targeted: cap decompiles
            w.println("\n## DECOMP `"+f.getName()+"` @ "+f.getEntryPoint()+"\n```c");
            DecompileResults r = di.decompileFunction(f, 120, monitor);
            if (r!=null && r.decompileCompleted()) w.println(r.getDecompiledFunction().getC());
            else w.println("// decompile failed/timeout");
            w.println("```"); dec++;
        }
        w.close();
        println("OPC_DONE candidates="+all.size()+" decompiled="+dec);
    }
}
