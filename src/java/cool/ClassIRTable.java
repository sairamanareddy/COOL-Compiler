package cool;
import java.util.*;

public class ClassIRTable{
	public HashMap <String,ClassIR> irTable;

	ClassIRTable(){
		irTable=new HashMap<String,ClassIR>();
		ObjIR();
		IOIR();
		IntIR();
		StringIR();
		BoolIR();
	}

	private void ObjIR(){
		List<AST.formal> formals = new ArrayList<AST.formal>();
		formals.add(new AST.formal("this","Object",0));
		HashMap<String,AST.method> mthds= new HashMap<String,AST.method>();
		mthds.put("abort", new AST.method("abort", formals, "Object", new AST.no_expr(0), 0));
		mthds.put("type_name", new AST.method("type_name", formals, "String", new AST.no_expr(0), 0));
		mthds.put("copy", new AST.method("copy", formals, "Object", new AST.no_expr(0), 0));
		HashMap<String,String> methodIR =new HashMap<String,String>();
		methodIR.put("abort","@_ZN6Object5abort");
		methodIR.put("type_name","@_ZN6Object9type_name");
		methodIR.put("copy","@_ZN6Object4copy");
		irTable.put("Object", new ClassIR(null, new HashMap<String,AST.attr>(),mthds,methodIR, 0, 12)); 
	}

	private void IOIR(){
		List<AST.formal> formals= new ArrayList<AST.formal>();
		List<AST.formal> stringformals = new ArrayList<AST.formal>();
		List<AST.formal> intformals = new ArrayList<AST.formal>();
		formals.add(new AST.formal("this","IO",0));
		stringformals.addAll(formals);
		intformals.addAll(formals);
		stringformals.add(new AST.formal("x", "String",0));
		intformals.add(new AST.formal("x","Int",0));
		HashMap<String,AST.method> mthds = new HashMap<String,AST.method>();
		mthds.put("out_string",new AST.method("out_string",stringformals, "IO", new AST.no_expr(0),0));
		mthds.put("out_int",new AST.method("out_int",intformals, "IO", new AST.no_expr(0),0));
		mthds.put("in_string",new AST.method("in_string",formals, "String", new AST.no_expr(0),0));
		mthds.put("in_int",new AST.method("in_int",formals, "Int", new AST.no_expr(0),0));
		HashMap<String,String> methodIR = new HashMap<String,String> ();
		methodIR.putAll(irTable.get("Object").methodIR);
		methodIR.put("out_string", "@_ZN2IO10out_string");
		methodIR.put("in_string", "@_ZN2IO7out_int");
		methodIR.put("in_string", "@_ZN2IO9in_string");
		methodIR.put("in_int", "@_ZN2IO9in_int");
		irTable.put("IO", new ClassIR("Object", new HashMap<String,AST.attr>(),mthds,methodIR, 1, 12));
	}

	private void IntIR(){
		List<AST.formal> formals= new ArrayList<AST.formal>();
		formals.add(new AST.formal("this","Int",0));
		HashMap<String,AST.method> mthds = new HashMap<String,AST.method>();
		HashMap<String,String> methodIR = new HashMap<String,String> ();
		methodIR.putAll(irTable.get("Object").methodIR);
		irTable.put("Int", new ClassIR("Object", new HashMap<String,AST.attr>(),mthds,methodIR, 1, 4));
	}

	private void StringIR(){
		List<AST.formal> formals= new ArrayList<AST.formal>();
		formals.add(new AST.formal("this","String",0));
		List<AST.formal> concatformals = new ArrayList<AST.formal>();
		List<AST.formal> substrformals = new ArrayList<AST.formal>();
		concatformals.addAll(formals);
		substrformals.addAll(formals);
		concatformals.add(new AST.formal("s","String", 0));
		substrformals.add(new AST.formal("i","Int",0));
		substrformals.add(new AST.formal("i","Int",0));
		HashMap<String,AST.method> mthds = new HashMap<String,AST.method>();
		mthds.put("length",new AST.method("length",formals,"Int", new AST.no_expr(0),0));
		mthds.put("concat",new AST.method("concat",concatformals,"String", new AST.no_expr(0),0));
		mthds.put("substr",new AST.method("substr",formals,"String", new AST.no_expr(0),0));
		HashMap<String,String> methodIR = new HashMap<String,String> ();
		methodIR.putAll(irTable.get("Object").methodIR);
		methodIR.put("length", "@_ZN6String6length");
		methodIR.put("concat", "@_ZN6String6concat");
		methodIR.put("substr", "@_ZN6String6substr");
		irTable.put("String", new ClassIR("Object", new HashMap<String,AST.attr>(),mthds,methodIR, 1, 8));	
	}

	private void BoolIR(){
		List<AST.formal> formals= new ArrayList<AST.formal>();
		formals.add(new AST.formal("this","Bool",0));
		HashMap<String,AST.method> mthds = new HashMap<String,AST.method>();
		HashMap<String,String> methodIR = new HashMap<String,String> ();
		methodIR.putAll(irTable.get("Object").methodIR);
		irTable.put("Bool", new ClassIR("Object", new HashMap<String,AST.attr>(),mthds,methodIR, 1, 4));	
	}

	public void insert(AST.class_ clss){
		ClassIR par = irTable.get(clss.parent);
		ClassIR cur = new ClassIR(clss.parent, par.atrs,new HashMap<String,AST.method>(), par.methodIR,par.depth+1,0);
		int size = irTable.get(clss.parent).size;
		for(AST.feature f: clss.features){
			if(f instanceof AST.attr){
				AST.attr at = (AST.attr)f;
				cur.atrs.put(at.name,at);
				cur.aList.add(at.name);
				if(at.typeid=="Int" || at.typeid=="Bool"){
					size+=4;
				}
				else{
					size+=8;
				}
			}
			else if(f instanceof AST.method){
				AST.method m = (AST.method)f;
				cur.mtds.put(m.name,m);
				cur.methodIR.put(m.name,"@_ZN"+clss.name.length()+clss.name+m.name.length()+m.name);
			}
		}
		cur.size=size;
		irTable.put(clss.name,cur);	
	}
}