import ghidra.app.script.GhidraScript; import ghidra.program.model.listing.*; import ghidra.app.decompiler.*; import java.io.*;
public class FindScramble extends GhidraScript { public void run() throws Exception {
  String[] pats={"cramble","Scrambl","gScrambled"};
  FunctionManager fm=currentProgram.getFunctionManager(); DecompInterface di=new DecompInterface(); di.openProgram(currentProgram);
  PrintWriter w=new PrintWriter(new FileWriter("/Users/robert/projects/darkan3-server/claude-re/findings/19-scramble.md"));
  int n=0;
  for (Function f: fm.getFunctions(true)){ String s=f.getName(true);
    for (String p: pats) if (s.contains(p)){ println("FOUND "+s+" @ "+f.getEntryPoint());
      w.println("## `"+s+"` @ "+f.getEntryPoint()+"\n```c");
      DecompileResults r=di.decompileFunction(f,60,monitor);
      w.println(r!=null&&r.decompileCompleted()?r.getDecompiledFunction().getC():"// timeout"); w.println("```"); n++; break; } }
  w.close(); println("SCRAMBLE_DONE n="+n); } }
