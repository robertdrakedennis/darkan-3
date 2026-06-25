import ghidra.app.script.GhidraScript; import ghidra.program.model.listing.*; import ghidra.app.decompiler.*; import java.io.*;
public class FindCipher extends GhidraScript { public void run() throws Exception {
  String[] pats={"ScrambleKey","ScrambleCipher","SetCipher","CipherKey","InitCipher","PrepareScramble","ResetScramble","ScrambleSeed","SetScramble"};
  FunctionManager fm=currentProgram.getFunctionManager(); DecompInterface di=new DecompInterface(); di.openProgram(currentProgram);
  PrintWriter w=new PrintWriter(new FileWriter("/Users/robert/projects/darkan3-server/claude-re/findings/19b-cipher.md"));
  int n=0;
  for (Function f: fm.getFunctions(true)){ String s=f.getName(true);
    boolean hit=false; for(String p:pats) if(s.contains(p)){hit=true;break;}
    if(hit){ println("FOUND "+s+" @ "+f.getEntryPoint());
      w.println("## `"+s+"` @ "+f.getEntryPoint()+"\n```c");
      DecompileResults r=di.decompileFunction(f,60,monitor);
      w.println(r!=null&&r.decompileCompleted()?r.getDecompiledFunction().getC():"// timeout"); w.println("```"); n++; } }
  w.close(); println("CIPHER_DONE n="+n); } }
