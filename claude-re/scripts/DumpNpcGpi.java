import ghidra.app.script.GhidraScript; import ghidra.program.model.listing.*; import ghidra.app.decompiler.*; import java.io.*;
public class DumpNpcGpi extends GhidraScript { public void run() throws Exception {
  long[] addrs={0x1d54f0L,0x183ed0L};               // NPC_INFO thunk, PLAYER_INFO_DECODE
  String[] needles={"NpcInfoDecode","NpcInfoBuild","PlayerInfoBuild"};
  PrintWriter w=new PrintWriter(new FileWriter("/Users/robert/projects/darkan3-server/claude-re/findings/16-gpi-decoders.md"));
  w.println("# GPI / NPC-info bit-loop decoders\n");
  FunctionManager fm=currentProgram.getFunctionManager(); DecompInterface di=new DecompInterface(); di.openProgram(currentProgram); int n=0;
  java.util.LinkedHashSet<Function> set=new java.util.LinkedHashSet<>();
  for (long a: addrs){ Function f=fm.getFunctionContaining(toAddr(a)); if(f!=null) set.add(f); }
  for (Function f: fm.getFunctions(true)){ String s=f.getName(true); for(String nd:needles) if(s.contains(nd)){ set.add(f); break; } }
  for (Function f: set){ w.println("\n## `"+f.getName(true)+"` @ "+f.getEntryPoint()+"\n```c");
    DecompileResults r=di.decompileFunction(f,90,monitor);
    w.println(r!=null&&r.decompileCompleted()?r.getDecompiledFunction().getC():"// timeout"); w.println("```"); n++; }
  w.close(); println("NPCGPI_DONE n="+n); } }
