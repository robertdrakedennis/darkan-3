import ghidra.app.script.GhidraScript; import ghidra.program.model.listing.*; import ghidra.program.model.mem.*; import ghidra.program.model.address.*;
public class DumpEntry2 extends GhidraScript { public void run() throws Exception {
  long[] use={0x013a16e0L,0x013a1ce0L,0x013a1be0L,0x015c0f40L}; String[] un={"op52(0x13a)","op0(0x13a)","op9(0x13a)","op22(0x15c)"};
  Memory mem=currentProgram.getMemory(); FunctionManager fm=currentProgram.getFunctionManager();
  for (int k=0;k<use.length;k++){ Address ea=toAddr(use[k]); println("=== "+un[k]+" @"+Long.toHexString(use[k])+" ===");
    int nz=0;
    for (int off=0; off<0x50; off+=8){ try { long v=mem.getLong(ea.add(off));
        Function f=fm.getFunctionContaining(toAddr(v&0xffffffffffffffL));
        if (v!=0){ nz++; println(String.format("  +0x%02x = %016x %s",off,v,(f!=null)?f.getName(true):"")); }
      } catch (Exception ex){ println(String.format("  +0x%02x <unreadable>",off)); } }
    if (nz==0) println("  (all zero = .bss, runtime-initialized)"); }
  println("ENTRY2_DONE"); } }
