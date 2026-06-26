import ghidra.app.script.GhidraScript; import ghidra.program.model.listing.*;
public class FindAppearance extends GhidraScript { public void run() throws Exception {
  String[] pats={"ppearance","vatar","omposite","dentkit","ecolour","ecolor","BodyPart","PlayerEntity","Composite","DecodeAppear","ReadAppear","PlayerBuild","InfoBuild","Identkit"};
  FunctionManager fm=currentProgram.getFunctionManager(); int n=0;
  for (Function f: fm.getFunctions(true)){ String s=f.getName(true);
    for (String p: pats) if (s.contains(p)){ println("  "+s+" @ "+f.getEntryPoint()); n++; break; } }
  println("FINDAPP_DONE n="+n); } }
