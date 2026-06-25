import ghidra.app.script.GhidraScript; import ghidra.program.model.listing.*; import ghidra.app.decompiler.*; import java.io.*;
public class DumpCoord extends GhidraScript { public void run() throws Exception {
  long[] addrs={0x121a30L};
  PrintWriter w=new PrintWriter(new FileWriter("/Users/robert/projects/darkan3-server/claude-re/findings/17-coord-reader.md"));
  FunctionManager fm=currentProgram.getFunctionManager(); DecompInterface di=new DecompInterface(); di.openProgram(currentProgram);
  for (long a: addrs){ Function f=fm.getFunctionContaining(toAddr(a));
    if(f!=null){ w.println("## `"+f.getName(true)+"` @ "+f.getEntryPoint()+"\n```c");
      DecompileResults r=di.decompileFunction(f,60,monitor);
      w.println(r!=null&&r.decompileCompleted()?r.getDecompiledFunction().getC():"// timeout"); w.println("```"); } }
  w.close(); println("COORD_DONE"); } }
