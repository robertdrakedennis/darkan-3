import ghidra.app.script.GhidraScript; import ghidra.program.model.listing.*; import ghidra.app.decompiler.*; import java.io.*;
public class DumpRest2 extends GhidraScript { public void run() throws Exception {
  String[] cats={"Audio","Camera","Chat","Clans","Friends","NPCInfo","PlayerGroup","Social","Lobby"};
  PrintWriter w=new PrintWriter(new FileWriter("/Users/robert/projects/darkan3-server/claude-re/findings/24-other.md"));
  w.println("# Remaining handler decompilations (binary)\n");
  FunctionManager fm=currentProgram.getFunctionManager(); DecompInterface di=new DecompInterface(); di.openProgram(currentProgram); int n=0;
  for (Function f: fm.getFunctions(true)){ String full=f.getName(true);
    for (String c:cats) if (full.contains("packethandlers::"+c+"::")){ w.println("\n## `"+full+"` @ "+f.getEntryPoint()+"\n```c");
      DecompileResults r=di.decompileFunction(f,30,monitor); w.println((r!=null&&r.decompileCompleted())?r.getDecompiledFunction().getC():"// timeout"); w.println("```"); n++; break; } }
  w.close(); println("REST2_DONE n="+n); } }
