import ghidra.app.script.GhidraScript; import ghidra.program.model.listing.*; import ghidra.app.decompiler.*; import java.io.*;
public class DumpBitParse extends GhidraScript { public void run() throws Exception {
  String[] needles={"RebuildSceneEntry"};
  PrintWriter w=new PrintWriter(new FileWriter("/Users/robert/projects/darkan3-server/claude-re/findings/15-bitparse.md"));
  w.println("# Bit-parse helper decompiles\n");
  FunctionManager fm=currentProgram.getFunctionManager(); DecompInterface di=new DecompInterface(); di.openProgram(currentProgram); int n=0;
  for (Function f: fm.getFunctions(true)){ String full=f.getName(true);
    for (String nd: needles) if (full.contains(nd)){ w.println("\n## `"+full+"` @ "+f.getEntryPoint()+"\n```c");
      DecompileResults r=di.decompileFunction(f,60,monitor);
      w.println(r!=null&&r.decompileCompleted()?r.getDecompiledFunction().getC():"// timeout"); w.println("```"); n++; break; } }
  w.close(); println("BITPARSE_DONE n="+n); } }
