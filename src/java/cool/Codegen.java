package cool;

import java.io.PrintWriter;
import java.util.*;

public class Codegen{
	ClassIRTable classtable = new ClassIRTable();
	int vars=0;
	int loops=0;
	int ifs=0;
	int strings=0;
	String totalStrings="";
	String mainReturnType="i32";
	public Codegen(AST.program program, PrintWriter out){
		//Write Code generator code here
		printHeaders(out);
		genCodeClasses(program.classes, out);
		printMainFunc(out);
		printStringMethods(out);
		out.println(totalStrings);
        out.println("; I am a comment in LLVM-IR. Feel free to remove me.");
	}
	void printHeaders(PrintWriter out) {
		out.println("target datalayout = \"e-m:e-i64:64-f80:128-n8:16:32:64-S128\"");
		out.println("target triple = \"x86_64-unknown-linux-gnu\"");
		out.println("@Abortdivby0 = private unnamed_addr constant [22 x i8] c\"Error: Division by 0\\0A\\00\", align 1\n"
			+ "@Abortdispvoid = private unnamed_addr constant [25 x i8] c\"Error: Dispatch to void\\0A\\00\", align 1\n");
		out.println("declare i32 @printf(i8*, ...)\n"
			+ "declare i32 @scanf(i8*, ...)\n"
			+ "declare i32 @strcmp(i8*, i8*)\n"
			+ "declare i8* @strcat(i8*, i8*)\n"
			+ "declare i8* @strcpy(i8*, i8*)\n"
			+ "declare i8* @strncpy(i8*, i8*, i32)\n"
			+ "declare i64 @strlen(i8*)\n"
			+ "declare i8* @malloc(i64)\n"
			+ "declare void @exit(i32)");
		out.println("@strformatstr = private unnamed_addr constant [3 x i8] c\"%s\\00\", align 1\n"
			+ "@instrformatstr = private unnamed_addr constant [7 x i8] c\"%[^\\0A]s\\00\", align 1\n"
			+ "@intformatstr = private unnamed_addr constant [3 x i8] c\"%d\\00\", align 1\n");
	}
	private void printMainFunc(PrintWriter out) {
		out.println("define i32 @main() {\n"
			+"entry:\n"
			+"\t%0 = alloca %class.Main, align 4\n"
			+"\tcall i32 @_ZN4Main8__cons__(%class.Main* %0)\n"
			+"\tcall "+mainReturnType+" @_ZN4Main4main(%class.Main* %0)\n"
			+"\tret i32 0\n"
			+"}");
	}

	// Print methods related to object
	void printObjectMethods(PrintWriter out) {
		out.println("define i32 @_ZN6Object8__cons__( %class.Object* %self ) noreturn {\n"
			+ "entry:\n"
			+"\tret i32 0\n"
			+"}\n");

		out.println("define %class.Object* @_ZN6Object5abort( %class.Object* %self ) noreturn {\n"
			+ "entry:\n"
			+ "\tcall void @exit( i32 1 )\n"
			+ "\tret %class.Object* null\n"
			+ "}\n");

		out.println("define [1024 x i8]* @_ZN6Object9type_name( %class.Object* %self ) {\n"
			+ "entry:\n"
			+ "\t%0 = getelementptr inbounds %class.Object, %class.Object* %self, i32 0, i32 1\n"
			+ "\t%1 = load [1024 x i8]*, [1024 x i8]** %0\n"
			+ "\t%retval = call [1024 x i8]* @_ZN6String4copy( [1024 x i8]* %1 )\n"
			+ "\tret [1024 x i8]* %retval\n"
			+ "}\n");
	}

	// Print methods related to string
	void printStringMethods(PrintWriter out) {
		out.println("define i32 @_ZN6String6length( [1024 x i8]* %self ) {\n"
			+ "\tentry:\n"
			+ "\t%0 = bitcast [1024 x i8]* %self to i8*\n"
			+ "\t%1 = call i64 @strlen( i8* %0 )\n"
			+ "\t%retval = trunc i64 %1 to i32\n"
			+ "\tret i32 %retval\n"
			+ "}\n");

		out.println("define [1024 x i8]* @_ZN6String6concat( [1024 x i8]* %self, [1024 x i8]* %that ) {\n"
			+ "entry:\n"
			+ "\t%retval = call [1024 x i8]* @_ZN6String4copy( [1024 x i8]* %self )\n"
			+ "\t%0 = bitcast [1024 x i8]* %retval to i8*\n"
			+ "\t%1 = bitcast [1024 x i8]* %that to i8*\n"
			+ "\t%2 = call i8* @strcat( i8* %0, i8* %1 )\n"
			+ "\tret [1024 x i8]* %retval\n"
			+ "}\n");

		out.println("define [1024 x i8]* @_ZN6String4copy( [1024 x i8]* %self ) {\n"
			+ "entry:\n"
			+ "\t%0 = call i8* @malloc( i64 1024 )\n"
			+ "\t%retval = bitcast i8* %0 to [1024 x i8]*\n"
			+ "\t%1 = bitcast [1024 x i8]* %self to i8*\n"
			+ "\t%2 = bitcast [1024 x i8]* %retval to i8*\n"
			+ "\t%3 = call i8* @strcpy( i8* %2, i8* %1)\n"
			+ "\tret [1024 x i8]* %retval\n"
			+ "}\n");

		out.println("define [1024 x i8]* @_ZN6String6substr( [1024 x i8]* %self, i32 %start, i32 %len ) {\n"
			+ "entry:\n"
			+ "\t%0 = getelementptr inbounds [1024 x i8], [1024 x i8]* %self, i32 0, i32 %start\n"
			+ "\t%1 = call i8* @malloc( i64 1024 )\n"
			+ "\t%retval = bitcast i8* %1 to [1024 x i8]*\n"
			+ "\t%2 = bitcast [1024 x i8]* %retval to i8*\n"
			+ "\t%3 = call i8* @strncpy( i8* %2, i8* %0, i32 %len )\n"
			+ "\t%4 = getelementptr inbounds [1024 x i8], [1024 x i8]* %retval, i32 0, i32 %len\n"
			+ "\tstore i8 0, i8* %4\n"
			+ "\tret [1024 x i8]* %retval\n"
			+ "}\n");
	}

	// Print IO methods
	void printIOMethods(PrintWriter out) {
		out.println("define i32 @_ZN2IO8__cons__( %class.IO* %self ) noreturn {\n"
			+ "entry:\n"
			+"\tret i32 0\n"
			+"}\n");

		out.println("define %class.IO* @_ZN2IO10out_string( %class.IO* %self, [1024 x i8]* %str ) {\n"
			+ "entry:\n"
			+ "\t%0 = call i32 (i8*, ...) @printf( i8* bitcast ( [3 x i8]* @strformatstr to i8* ), [1024 x i8]* %str )\n"
			+ "\tret %class.IO* %self\n"
			+ "}\n");
	
		out.println("define %class.IO* @_ZN2IO7out_int( %class.IO* %self, i32 %int ) {\n"
			+ "entry:\n"
			+ "\t%0 = call i32 (i8*, ...) @printf( i8* bitcast ( [3 x i8]* @intformatstr to i8* ), i32 %int )\n"
			+ "\tret %class.IO* %self\n"
			+ "}\n");
	
		out.println("define [1024 x i8]* @_ZN2IO9in_string( %class.IO* %self ) {\n"
			+ "entry:\n"
			+ "\t%0 = call i8* @malloc( i64 1024 )\n"
			+ "\t%retval = bitcast i8* %0 to [1024 x i8]*\n"
			+ "\t%1 = call i32 (i8*, ...) @scanf( i8* bitcast ( [3 x i8]* @strformatstr to i8* ), [1024 x i8]* %retval )\n"
			+ "\tret [1024 x i8]* %retval\n"
			+ "}\n");
	
		out.println("define i32 @_ZN2IO6in_int( %class.IO* %self ) {\n"
			+ "entry:\n"
			+ "\t%0 = call i8* @malloc( i64 4 )\n"
			+ "\t%1 = bitcast i8* %0 to i32*\n"
			+ "\t%2 = call i32 (i8*, ...) @scanf( i8* bitcast ( [3 x i8]* @intformatstr to i8* ), i32* %1 )\n"
			+ "\t%retval = load i32, i32* %1\n"
			+ "\tret i32 %retval\n"
			+ "}\n");
	}
	String parseType(String t){
		String intUtil = "i32";
		String stringUtil = "[1024 x i8]*";
		if(t.equals("Int")){
			return intUtil;
		}
		else if(t.equals("Bool")){
			return intUtil;
		}
		else if(t.equals("String")){
			return stringUtil;
		}
		else{
			String ret = "%class."+t+"*";
			return ret;
		}
	}

	String reverseParseTypeValue(String t){
		String deflt = "[1024 x i8]*";
		if(t.length()>12 && t.substring(0,12).equals(deflt)){
			return deflt;
		}
		else{
			deflt="";
			int n=t.length();
			for(int i=0;i<n;i++){
				if(t.charAt(i)!=' '){
					deflt+=t.charAt(i);
					continue;
				}
				break;
			}
			return deflt;
		}
	}

	String reverseParseTypeValueVar(String t){
		String ret="";
		int n=t.length();
		for(int i=0;i<n;i++){
			if(t.charAt(i)==' '){
				ret="";
			}
			else{
				ret+=t.charAt(i);
			}
		}
		return ret;
	}

	private void genCodeClasses(List<AST.class_> classes, PrintWriter out){
		HashMap<String,AST.class_> name2class = new HashMap<String,AST.class_>();
		HashMap < String, ArrayList <String> > edges = new HashMap < String, ArrayList <String> >();
		edges.put("Object", new ArrayList<String>());
		edges.put("IO", new ArrayList<String>());
		
		for (AST.class_ c : classes) {
			edges.put(c.name, new ArrayList <String> ());
			name2class.put(c.name, c);
		}
		edges.get("Object").add("IO");
		for (AST.class_ c : classes){
			if (c.parent != null) edges.get(c.parent).add(c.name);
		}
		Queue <String> q = new LinkedList<String>();
		q.offer("Object");
		while(!q.isEmpty()){
			String c = q.poll();
			if (!c.equals("Object") && !c.equals("IO")) 
				classtable.insert(name2class.get(c));
			genCodeClass(c, out);
			for (String s : edges.get(c)){
				q.offer(s);
			}
		}
		out.println();
		q.clear();
		q.offer("Object");
		while(!q.isEmpty()){
			String c = q.poll();
			genCodeClassMethods(c, out);
			for (String s : edges.get(c)){
				q.offer(s);
			}
		}
		q.clear();
	}

	public void genCodeClass(String c, PrintWriter out){
		if (c.equals("Object")) {
			out.println("%class.Object = type { i32, [1024 x i8] * }");
		}
		else{
			ClassIR cur = classtable.irTable.get(c);
			String result = "";
		    for (String at : cur.aList) {
		    	if (at.equals("_Par")) {
		    		result += "%class."+cur.parent+", ";
		    		continue;
		    	}
		    	AST.attr attribute = cur.atrs.get(at);
		    	result += parseType(attribute.typeid)+", ";
		    }
		    if (result.length() >= 2) result = result.substring(0, result.length()-2);
		    out.println("%class."+c+" = type { " + result + " }");
		}
	}

	public void genCodeClassMethods(String c,PrintWriter out){
		if(c.equals("Object")){
			printObjectMethods(out);
			return;
		}
		if(c.equals("IO")){
			printIOMethods(out);
			return;
		}
		ClassIR cur = classtable.irTable.get(c);
		String formals = parseType(c)+" %self";
	    out.println("define i32 @_ZN"+c.length()+c+8+"__cons__( "+formals+" ) {");
	    out.println("entry:");
	    vars=-1;
	    loops=-1;
	    ifs=-1;
	    List<String> blocks=new ArrayList<String>();
	    for(Map.Entry<String, AST.attr> entry: cur.atrs.entrySet()){
	    	AST.attr at = entry.getValue();
	    	if (!(at.value instanceof AST.no_expr)) {
		    	AST.assign exp = new AST.assign(at.name, at.value, 0);
		    	exp.type = at.typeid;
		    	printexpr(c, null, exp, new ArrayList<>(), blocks, out);
		    } else if (at.typeid.equals("Int")) {
		    	AST.assign exp = new AST.assign(at.name, new AST.int_const(0, 0), 0);
		    	exp.type = "Int";
		    	printexpr(c, null, exp, new ArrayList<>(), blocks, out);
		    } else if (at.typeid.equals("Bool")) {
		    	AST.assign exp = new AST.assign(at.name, new AST.bool_const(false, 0), 0);
		    	exp.type = "Bool";
		    	printexpr(c, null, exp, new ArrayList<>(), blocks, out);
		    } else if (at.typeid.equals("String")) {
		    	AST.assign exp = new AST.assign(at.name, new AST.string_const("", 0), 0);
		    	exp.type = "String";
		    	printexpr(c, null, exp, new ArrayList<>(), blocks, out);
		    } else {
		    	int attrinfo = cur.aList.indexOf(at.name);
		    	String ctype = parseType(c);
		    	ctype = ctype.substring(0, ctype.length()-1);
		    	out.println("\t%"+(++vars)+" = getelementptr inbounds "+ctype+", "+ctype+"* %self, i32 0, i32 "+attrinfo);
		    	out.println("\tstore "+parseType(at.typeid)+" null, "+parseType(at.typeid)+"* %"+vars+", align 4");
		    }
	    }
	    String prev = parseType(c)+" %self";
		ClassIR temp = cur;
		
	    while (!reverseParseTypeValue(prev).equals(parseType("Object"))) {
			// debug only
			System.out.println(temp.parent);
			String par = parseType(temp.parent);
			par = par.substring(0, par.length()-1);
			String next = reverseParseTypeValue(prev);
			next = next.substring(0, next.length()-1);
			out.println("\t%"+(++vars)+" = getelementptr inbounds "+next+", "+next+"* "+reverseParseTypeValueVar(prev)+", i32 0, i32 0");
			prev = par+"* %"+vars;
			temp = classtable.irTable.get(temp.parent);
		}
		out.println("\t%"+(++vars)+" = getelementptr inbounds %class.Object, %class.Object* "+reverseParseTypeValueVar(prev)+", i32 0, i32 0");
		out.println("\tstore i32 "+cur.size+", i32* %"+vars);
		String next = "["+(c.length()+1)+" x i8]";
		totalStrings += "@.str"+(strings++)+" = private unnamed_addr constant "+next+" c\""+c+"\\00\", align 1\n";
		out.println("\t%"+(++vars)+" = bitcast "+next+"* @.str"+(strings-1)+" to [1024 x i8]*");
		out.println("\t%"+(++vars)+" = getelementptr inbounds %class.Object, %class.Object* "+reverseParseTypeValueVar(prev)+", i32 0, i32 1");
		out.println("\tstore [1024 x i8]* %"+(vars-1)+", [1024 x i8]** %"+vars);
		out.println("\tret i32 0");
	    out.println("}\n");
	    for (Map.Entry<String, AST.method> entry : cur.mtds.entrySet()) {
	    	AST.method m = entry.getValue();
	    	formals = "%class."+c+"* %self";
	    	for (AST.formal f : m.formals) {
	    		formals += ", "+parseType(f.typeid)+" %"+f.name;
	    	}
	    	blocks.clear();
	    	// Set return type for main function in Main class
	    	if (c.equals("Main") && entry.getKey().equals("main"))
	    		mainReturnType = parseType(m.typeid);

	    	// start defining the function
	        out.println("define "+parseType(m.typeid)+" "+cur.methodIR.get(entry.getKey())+"( "+formals+" )" + "{");
	        out.println("entry:");
	        blocks.add("entry");
	        vars = -1;
	        loops = -1;
	        ifs = -1;
	        List<String> changedFormals = new ArrayList<>();
	        // Allot all formals in the stack
			for (AST.formal f : m.formals) {
				String f_typ = parseType(f.typeid);
				out.println("%"+f.name+".addr = alloca "+f_typ+", align 4");
				changedFormals.add(f.name);
				out.println("\tstore "+f_typ+" %"+f.name+", "+f_typ+"* %"+f.name+".addr, align 4");
			}
			// Print LLVM-IR for body of the function
	        String result = printexpr(c, m, m.body, changedFormals, blocks, out);
	        String resulttype = reverseParseTypeValue(result);
	        // Print return statement
	        if (!resulttype.equals(parseType(m.typeid))) {
	        	if (resulttype.equals("i32")) {
	        		out.println("\t%"+(++vars)+" = call noalias i8* @malloc(i64 8)"); // Object size
	        		out.println("\t%"+(++vars)+" = bitcast i8* %"+(vars-1)+" to "+parseType(m.typeid));
	        	} else {
	        		out.println("\t%"+(++vars)+" = bitcast "+result+" to "+parseType(m.typeid));	        		
	        	}
	        	result = parseType(m.typeid)+" %"+vars;
	        }
	        out.println("\tret "+result);
	        out.println("}\n");
	    }
	}
	public String printexpr(String class_name, AST.method method, AST.expression expr, List<String> changedformals, List<String> blocks, PrintWriter out) {
		ClassIR ci = classtable.irTable.get(class_name);
		switch(expr.getname()){
			case "bool_const":
			{	//Bool const
				AST.bool_const temp = (AST.bool_const) expr;
				return "i32 " + (temp.value ? 1 : 0);
			}
			case "string_const":
			{
				//String const
				AST.string_const temp = (AST.string_const) expr;
				return visit(temp, out);
			}
			case "int_const":
			{	//Int const
				AST.int_const temp = (AST.int_const) expr;
				return "i32 " + temp.value;
			}
			case "object":
			{
				AST.object temp = (AST.object) expr;
				return visit(temp, class_name, ci, method, changedformals, out);
			}
			case "comp":
			{
				AST.comp temp = (AST.comp) expr;
				String op = printexpr(class_name, method, temp, changedformals, blocks, out);
				out.println("\t%" + (++vars) + " = sub nsw i32 1, " + op.substring(4));
				return "i32 %" + vars;
			}
			case "eq":
			{
				AST.eq temp = (AST.eq) expr;
				String op1 = printexpr(class_name, method, temp.e1, changedformals, blocks, out);
				String op2 = printexpr(class_name, method, temp.e2, changedformals, blocks, out);
				out.println("\t%" + (++vars) + " = icmp eq i32 " + op1.substring(4) + ", " + op2.substring(4));
				return "i32 %" + vars;
			}
			case "leq":
			{
				AST.leq temp = (AST.leq) expr;
				String op1 = printexpr(class_name, method, temp.e1, changedformals, blocks, out);
				String op2 = printexpr(class_name, method, temp.e2, changedformals, blocks, out);
				out.println("\t%" + (++vars) + " = icmp sle i32 " + op1.substring(4) + ", " + op2.substring(4));
				return "i32 %" + vars;
			}
			case "lt":
			{
				AST.lt temp = (AST.lt) expr;
				String op1 = printexpr(class_name, method, temp.e1, changedformals, blocks, out);
				String op2 = printexpr(class_name, method, temp.e2, changedformals, blocks, out);
				out.println("\t%" + (++vars) + " = icmp slt i32 " + op1.substring(4) + ", " + op2.substring(4));
				return "i32 %" + vars;
			}
			case "neg":
			{
				AST.neg temp = (AST.neg) expr;
				String op = printexpr(class_name, method, temp.e1, changedformals, blocks, out);
				out.println("\t%" + (++vars) + " = sub nsw i32 0, " + op.substring(4));
				return "";
			}
			case "divide":
			{	
				AST.divide temp = (AST.divide) expr;
				String op1 = printexpr(class_name, method, temp.e1, changedformals, blocks, out);
				String op2 = printexpr(class_name, method, temp.e2, changedformals, blocks, out);
				return visit(temp, blocks, out, op1, op2);
			}
			case "mul":
			{
				AST.mul temp = (AST.mul) expr;
				String op1 = printexpr(class_name, method, temp.e1, changedformals, blocks, out);
				String op2 = printexpr(class_name, method, temp.e2, changedformals, blocks, out);
				out.println("\t%" + (++vars) + " = mul nsw i32 " + op1.substring(4) + ", " + op2.substring(4));
				return "i32 %" + vars;
			}
			case "sub":
			{
				AST.sub temp = (AST.sub) expr;
				String op1 = printexpr(class_name, method, temp.e1, changedformals, blocks, out);
				String op2 = printexpr(class_name, method, temp.e2, changedformals, blocks, out);
				out.println("\t%" + (++vars) + " = sub nsw i32 " + op1.substring(4) + ", " + op2.substring(4));
				return "i32 %" + vars;
			}
			case "plus":
			{
				AST.plus temp = (AST.plus) expr;
				String op1 = printexpr(class_name, method, temp.e1, changedformals, blocks, out);
				String op2 = printexpr(class_name, method, temp.e2, changedformals, blocks, out);
				out.println("\t%" + (++vars) + " = add nsw i32 " + op1.substring(4) + ", " + op2.substring(4));
				return "i32 %" + vars;
			}
			case "isvoid":
			{
				AST.isvoid temp = (AST.isvoid) expr;
				String op = printexpr(class_name, method, temp.e1, changedformals, blocks, out);
				out.println("\t%" + (++vars) + " = icmp eq " + op + ", null");
				return "i32 %" + vars;
			}
			case "new_":
			{
				AST.new_ temp = (AST.new_) expr;
				return visit(temp, out);
			}
			case "temp":
			{
				AST.assign temp = (AST.assign)expr;
				String lvalue = printexpr(class_name, method, temp.e1, changedformals, blocks, out);
				return visit(temp, ci, lvalue, class_name, method, changedformals, out);
			}
			case "block":
			{
				AST.block temp = (AST.block) expr;
				String return_var = "";
				for(AST.expression e : temp.l1){
					return_var = printexpr(class_name, method, e, changedformals, blocks, out);
				}
				return return_var;
			}
			case "loop":
			{
				AST.loop temp = (AST.loop) expr;
				return visit(temp, class_name, method, changedformals, out, blocks);
			}
			case "cond":
			{
				AST.cond temp = (AST.cond) expr;
				return visit(temp, class_name, method, blocks, changedformals, out);
			}
			case "static_dispatch":
			{
			AST.static_dispatch temp = (AST.static_dispatch) expr;
			return visit(temp, class_name, method, blocks, changedformals, out);
			}
			default:
			System.out.println("Error!");
		}
		return "";
	}
	public String visit(AST.new_ expr, PrintWriter out){
		String type = parseType(expr.typeid);
		int size = classtable.irTable.get(expr.typeid).size;
		out.println("\t%" + (++vars) + " = call noalias i8* @malloc(i64 " + size + ")");
		out.println("\t%" + (++vars) + " = bitcast i8* %" + (vars - 1) + " to " + type);
		out.println("\t%" + (++vars) + " = call i32 @_ZN" + expr.typeid.length() + expr.typeid + 8 + "__cons__( " + type + " %" + (vars - 1) + " )");
		return type + " %" + (vars - 1);
	}
	public String visit(AST.string_const expr, PrintWriter out){
		String ty = "[" + (expr.value.length() + 1) + " x i8";
		totalStrings += "@.str" + (strings++) + " = private unnamed_addr constant " + ty + " c\"" + expr.value + "\\00\", align 1\n";
		out.println("\t%" + (++vars) + " = bitcast " + ty + "* @.str" + (strings - 1) + " to [1024 x i8]*");
		return "[1024 x i8]* %" + vars;
	}
	public String visit(AST.divide expr, List<String> blocks, PrintWriter out, String op1, String op2){
		out.println("\t%" + (++vars) + " = icmp eq i32 0, " + op2.substring(4));
		ifs++;
		out.println("\tbr i1 %" + vars + ", label %if.then" + ifs + ", label %if.else" + ifs);
		out.println();
		out.println("if.then" + ifs + ":");
		blocks.add("if.then" + ifs);
		out.println("\t%" + (++vars) + " = bitcast [22 x i8]* @Abortdivby0 to [1024 x i8]*");
		out.println("\t%" + (++vars) + " = call %class.IO* @_ZN2IO10out_string( %class.IO* null, [1024 x i8]* %"
				+ (vars - 1) + ")");
		out.println("\tcall void @exit(i32 1)");
		out.println("\tbr label %if.else" + ifs);
		out.println();
		out.println("if.else" + ifs + ":");
		blocks.add("if.else" + ifs);
		out.println("\t%" + (++vars) + " = sdiv i32 " + op1.substring(4) + ", " + op2.substring(4));
		return "i32 %" + vars;
	}
	public String visit(AST.assign expr, ClassIR ci, String lvalue, String class_name, AST.method method, List<String> changedformals, PrintWriter out){
		int attri = ci.aList.indexOf(expr.name);
		if(method != null){
			for(AST.formal f : method.formals){
				if(!f.name.equals(expr.name)) continue;
				else{
					attri = -1;
					break;
				}
			}
		}
		String type = parseType(expr.type);
		String ctype = parseType(class_name);
		ctype = ctype.substring(0, ctype.length() - 1);
		String ltype = reverseParseTypeValue(lvalue);
		if(!ltype.equals(type) && ltype.equals("i32")){
			out.println("\t%" + (++vars) + " = call noalias i8* @malloc(i64 8)");
			out.println("\t%" + (++vars) + " = bitcast i8* %" + (vars - 1) + " to " + type);
			lvalue = type + " %" + vars;
		}
		else if(!ltype.equals(type)){
			out.println("\t%" + (++vars) + " = bitcast " + lvalue + " to " + type);
			lvalue = type + " %" + vars;
		}
		if(attri == -1){
			if(changedformals.indexOf(expr.name) == -1){
				out.println("%" + expr.name + ".addr = alloca " + type + ", align 4");
				changedformals.add(expr.name);
			}
			out.println("\tstore " + lvalue + ", " + type + "* %" + expr.name + ".addr, align 4");
			return lvalue;
		}
		else{
			out.println("\t%" + (++vars) + " = getelementptr inbounds " + ctype + ", " + ctype
						+ "* %self, i32 0, i32 " + attri);
			out.println("\tstore " + lvalue + ", " + type + "* %" + vars + ", align 4");
			return lvalue;
		}
	}
	public String visit(AST.loop expr, String class_name, AST.method method, List<String> changedformals, PrintWriter out, List<String> blocks){
		int loopcount = ++loops;
		out.println("\tbr label %loop.cond" + loopcount);
		out.println("loop.cond" + loopcount + ":");
		blocks.add("loop.cond" + loopcount);
		String pred = printexpr(class_name, method, expr.predicate, changedformals, blocks, out);
		out.println("\tbr i1 " + pred.substring(4) + ", label %loop.body" + loopcount + " , label %loop.end" + loopcount);
		out.println();
		out.println("loop.body" + loopcount + ":");
		blocks.add("loop.body" + loopcount);
		String body = printexpr(class_name, method, expr.body, changedformals, blocks, out);
		out.println("\tbr label %loop.cond" + loopcount);
		out.println();
		out.println("loop.end" + loopcount + ":");
		blocks.add("loop.end" + loopcount);
		return body;
	}
	public String visit(AST.cond expr, String class_name, AST.method method, List<String> blocks, List<String> changedformals, PrintWriter out){
		int condcount = ++ifs;
		String pred = printexpr(class_name, method, expr.predicate, changedformals, blocks, out);
		out.println("\tbr i1 " + pred.substring(4) + ", label %if.then" + ifs + ", label %if.else" + condcount);
		out.println();
		out.println("if.then" + ifs + ":");
		blocks.add("if.then" + ifs);
		String ifbody = printexpr(class_name, method, expr.ifbody, changedformals, blocks, out);
		String ifbodylabel = blocks.get(blocks.size() - 1);
		ifbody = reverseParseTypeValueVar(ifbody);
		out.println("\tbr label %if.end" + ifs);
		out.println();
		out.println("if.else" + ifs + ":");
		blocks.add("if.else" + ifs);
		String elsebody = printexpr(class_name, method, expr.elsebody, changedformals, blocks, out);
		String elsebodylabel = blocks.get(blocks.size() - 1);
		elsebody = reverseParseTypeValueVar(elsebody);
		out.println("\tbr label %if.end" + ifs);
		out.println();
		out.println("if.end" + ifs + ":");
		blocks.add("if.end" + ifs);
		out.println("\t%" + (++vars) + " = phi " + parseType(expr.type) + " [" + ifbody + ", %" + ifbodylabel
				+ "], [" + elsebody + ", %" + elsebodylabel + "]");
		return parseType(expr.type) + " %" + vars;
	}
	public String visit(AST.static_dispatch expr, String class_name, AST.method method, List<String> blocks, List<String> changedformals, PrintWriter out){
		String clr = printexpr(class_name, method, expr.caller, changedformals, blocks, out);
		List<String> actuals = new ArrayList<>();
		for(AST.expression e : expr.actuals){
			String a = printexpr(class_name, method, e, changedformals, blocks, out);
			actuals.add(a);
		}
		ifs++;
		out.println("\t%" + (++vars) + " = icmp eq " + clr + ", null");
		out.println("\tbr i1 %" + vars + ", label %if.then" + ifs + ", label %if.else" + ifs);
		out.println();
		out.println("if.then" + ifs + ":");
		blocks.add("if.then" + ifs);
		out.println("\t%" + (++vars) + " = bitcast [25 x i8]* @Abortdispvoid to [1024 x i8]*");
		out.println("\t%" + (++vars) + " = call %class.IO* @_ZN2IO10out_string( %class.IO* null, [1024 x i8]* %"
				+ (vars - 1) + ")");
		out.println("\tcall void @exit(i32 1)");
		out.println("\tbr label %if.else" + ifs);
		out.println();
		out.println("if.else" + ifs + ":");
		blocks.add("if.else" + ifs);
		String funcname = "@_ZN" + expr.typeid.length() + expr.typeid + expr.name.length() + expr.name;
		ClassIR table2 = classtable.irTable.get(reverseParseType(reverseParseTypeValue(clr)));
		while (!reverseParseTypeValue(clr).equals(parseType(expr.typeid))) {
			String par = parseType(table2.parent);
			par = par.substring(0, par.length() - 1);
			String ty = reverseParseTypeValue(clr);
			ty = ty.substring(0, ty.length() - 1);
			out.println("\t%" + (++vars) + " = getelementptr inbounds " + ty + ", " + ty + "* "
					+ reverseParseTypeValueVar(clr) + ", i32 0, i32 0");
			clr = par + "* %" + vars;
			table2 = classtable.irTable.get(table2.parent);
		}
		String actstr = clr;
		for(int i=0;i<actuals.size();i++){
			actstr = actstr + ", " + actuals.get(i);
		}
		out.println("\t%" + (++vars) + " = call " + parseType(expr.type) + " " + funcname + "(" + actstr + ")");
		return parseType(expr.type) + " %" + vars;
	}

	public String visit(AST.object expr, String class_name, ClassIR ci, AST.method method, List<String> changedformals, PrintWriter out){
		int atr = ci.aList.indexOf(expr.name);
		for(AST.formal f : method.formals){
			if(f.name.equals(expr.name)){
				atr = -1;
				break;
			}
		}
		if(atr == -1){
			if(changedformals.indexOf(expr.name) == -1)
				return parseType(expr.type) + " %" + expr.name;
			else {
				String type = parseType(expr.type);
				out.println("\t%"+(++vars)+" = load "+type+", "+type+"* %"+expr.name+".addr, align 4");
				return type+" %"+vars;
			}
		}
		String typename = parseType(class_name);
		typename = typename.substring(0, typename.length() - 1);
		out.println("\t%"+(++vars)+" = getelementptr inbounds "+typename+", "+typename+"* %self, i32 0, i32 "+atr);
		out.println("\t%"+(++vars)+" = load "+parseType(expr.type)+", "+parseType(expr.type)+"* %"+(vars-1)+", align 4");
		return parseType(expr.type)+" %"+vars;
	}
	String reverseParseType(String t){
		if (t.equals("i32")) {
			System.out.println("I will never come here reverseParseType");
			return "Int";
		} else if (t.equals("[1024 x i8]*")) {
			return "String";
		} else {
			return t.substring(7, t.length()-1);
		}
	}

}
