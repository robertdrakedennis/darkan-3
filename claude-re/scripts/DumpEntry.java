import ghidra.app.script.GhidraScript; import ghidra.program.model.listing.*; import ghidra.program.model.mem.*; import ghidra.program.model.address.*;
public class DumpEntry extends GhidraScript { public void run() throws Exception {
  long[] entries={0x015c0c00L,0x015c0f40L,0x015c0f40L}; String[] nm={"op3(IF_SETTOPLEVEL)","op22(PlayerInfo?)","op52(NpcInfo?)"};
  // op52 slot resolved below; re-read from args not needed - use known op3 to find offset
  long[] use={0x015c0c00L,0x015c0f40L}; String[] un={"op3","op22"};
  Memory mem=currentProgram.getMemory(); FunctionManager fm=currentProgram.getFunctionManager();
  for (int k=0;k<use.length;k++){ Address ea=toAddr(use[k]); println("=== "+un[k]+" entry @"+Long.toHexString(use[k])+" ===");
    for (int off=0; off<0x60; off+=8){ try { long v=mem.getLong(ea.add(off));
        Function f=fm.getFunctionContaining(toAddr(v&0xffffffffffffffL));
        String fn=(f!=null)?f.getName(true):"";
        if (v!=0) println(String.format("  +0x%02x = %016x %s",off,v,fn));
      } catch (Exception ex){ println(String.format("  +0x%02x = <unreadable>",off)); } } }
  println("ENTRY_DONE"); } }
