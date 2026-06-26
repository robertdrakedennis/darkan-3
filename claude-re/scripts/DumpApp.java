import ghidra.app.script.GhidraScript; import ghidra.program.model.listing.*; import ghidra.app.decompiler.*; import java.io.*;
public class DumpApp extends GhidraScript { public void run() throws Exception {
  long[] a={0x14d5b0L,0x15e290L,0x14eaf0L};
  PrintWriter w=new PrintWriter(new FileWriter("/Users/robert/projects/darkan3-server/claude-re/findings/18-appearance.md"));
  FunctionManager fm=currentProgram.getFunctionManager(); DecompInterface di=new DecompInterface(); di.openProgram(currentProgram);
  for (long x: a){ Function f=fm.getFunctionContaining(toAddr(x));
    if(f!=null){ w.println("\n## `"+f.getName(true)+"` @ "+f.getEntryPoint()+"\n```c");
      DecompileResults r=di.decompileFunction(f,90,monitor);
      w.println(r!=null&&r.decompileCompleted()?r.getDecompiledFunction().getC():"// timeout"); w.println("```"); } }
  w.close(); println("APP_DONE"); } }
