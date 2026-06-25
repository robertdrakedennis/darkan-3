import ghidra.app.script.GhidraScript; import ghidra.program.model.listing.*;
import ghidra.program.model.address.*; import ghidra.program.model.symbol.*;
import ghidra.app.decompiler.*; import java.io.*; import java.util.*;
public class DumpDispatch extends GhidraScript { public void run() throws Exception {
  FunctionManager fm=currentProgram.getFunctionManager(); ReferenceManager rm=currentProgram.getReferenceManager();
  List<Function> binders=new ArrayList<>();
  for (Function f: fm.getFunctions(true)) if (f.getName(true).contains("BindHandlers")) binders.add(f);
  // slots = data addresses the binders write the handler pointers into
  Set<Address> slots=new TreeSet<>();
  for (Function bh: binders){ AddressIterator it=rm.getReferenceSourceIterator(bh.getBody(),true);
    while(it.hasNext()){ Address from=it.next();
      for (Reference r: rm.getReferencesFrom(from)){ Address to=r.getToAddress();
        if (to.isMemoryAddress() && fm.getFunctionContaining(to)==null) slots.add(to); } } }
  // rank readers of those slots (excluding the binders) = dispatch candidates
  Map<Function,Integer> cnt=new HashMap<>();
  for (Address s: slots) for (Reference r: rm.getReferencesTo(s)){
    Function f=fm.getFunctionContaining(r.getFromAddress());
    if (f!=null && !binders.contains(f)) cnt.merge(f,1,Integer::sum); }
  List<Function> ranked=new ArrayList<>(cnt.keySet());
  ranked.sort((a,b)->cnt.get(b)-cnt.get(a));
  PrintWriter w=new PrintWriter(new FileWriter("/Users/robert/projects/darkan3-server/claude-re/findings/13-dispatch.md"));
  w.println("# S2C dispatch search\n\nbinders="+binders.size()+" handler-table slots="+slots.size()+"\n");
  for (Function b: binders) w.println("- binder `"+b.getName(true)+"` @ "+b.getEntryPoint());
  w.println("\n## top reader functions (by #handler-slots referenced) — dispatch candidates\n");
  int n=0; for (Function f: ranked){ w.println("- "+cnt.get(f)+" slots: `"+f.getName(true)+"` @ "+f.getEntryPoint()); if(++n>=25) break; }
  DecompInterface di=new DecompInterface(); di.openProgram(currentProgram); n=0;
  for (Function f: ranked){ w.println("\n## `"+f.getName(true)+"` @ "+f.getEntryPoint()+" ("+cnt.get(f)+" slots)\n```c");
    DecompileResults dr=di.decompileFunction(f,45,monitor);
    w.println(dr!=null&&dr.decompileCompleted()?dr.getDecompiledFunction().getC():"// timeout"); w.println("```"); if(++n>=3) break; }
  w.close(); println("DISPATCH_DONE binders="+binders.size()+" slots="+slots.size()+" readers="+cnt.size());
} }
