package cool;

import java.io.PrintWriter;
import java.util.*;

public class Codegen {
	public ClassIRTable classtable = new ClassIRTable();
	public int vars = 0;
	public int loops = 0;
	public int ifs = 0;
	public int strings = 0;
	public String totalStrings = "";
	public String mainReturnType = "i32";
	public ExprGen egen;Printer print_util;

	public Codegen(AST.program program, PrintWriter out) {
		// Write Code generator code here
		egen = new ExprGen(this);
		print_util = new Printer(out);
		genCodeClasses(program.classes, out);
		out.println(totalStrings);
		out.println("; I am a comment in LLVM-IR. Feel free to remove me.");
	}

	String gettype(String t) {
		String intUtil = "i32";
		String stringUtil = "[1024 x i8]*";
		if (t.equals("Int")) {
			return intUtil;
		} else if (t.equals("Bool")) {
			return intUtil;
		} else if (t.equals("String")) {
			return stringUtil;
		} else {
			String ret = "%class." + t + "*";
			return ret;
		}
	}

	String getvar(String t) {
		String deflt = "[1024 x i8]*";
		if (t.length() > 12 && t.substring(0, 12).equals(deflt)) {
			return deflt;
		} else {
			deflt = "";
			int n = t.length();
			for (int i = 0; i < n; i++) {
				if (t.charAt(i) != ' ') {
					deflt += t.charAt(i);
					continue;
				}
				break;
			}
			return deflt;
		}
	}

	String getvarvalue(String t) {
		String ret = "";
		int n = t.length();
		for (int i = 0; i < n; i++) {
			if (t.charAt(i) == ' ') {
				ret = "";
			} else {
				ret += t.charAt(i);
			}
		}
		return ret;
	}

	private void genCodeClasses(List<AST.class_> classes, PrintWriter out) {
		HashMap<String, AST.class_> name2class = new HashMap<String, AST.class_>();
		HashMap<String, ArrayList<String>> edges = new HashMap<String, ArrayList<String>>();
		edges.put("Object", new ArrayList<String>());
		edges.put("IO", new ArrayList<String>());

		for (AST.class_ c : classes) {
			edges.put(c.name, new ArrayList<String>());
			name2class.put(c.name, c);
		}
		edges.get("Object").add("IO");
		for (AST.class_ c : classes) {
			if (c.parent != null)
				edges.get(c.parent).add(c.name);
		}
		Queue<String> q = new LinkedList<String>();
		q.offer("Object");
		while (!q.isEmpty()) {
			String c = q.poll();
			if (!c.equals("Object") && !c.equals("IO"))
				classtable.insert(name2class.get(c));
			genCodeClass(c, out);
			for (String s : edges.get(c)) {
				q.offer(s);
			}
		}
		out.println();
		q.clear();
		q.offer("Object");
		while (!q.isEmpty()) {
			String c = q.poll();
			genCodeClassMethods(c, out);
			for (String s : edges.get(c)) {
				q.offer(s);
			}
		}
		q.clear();
	}

	public void genCodeClass(String c, PrintWriter out) {
		if (c.equals("Object")) {
			out.println("%class.Object = type { i32, [1024 x i8] * }");
		} else {
			ClassIR cur = classtable.irTable.get(c);
			String result = "";
			for (String at : cur.aList) {
				if (at.equals("_Par")) {
					result += "%class." + cur.parent + ", ";
					continue;
				}
				AST.attr attribute = cur.atrs.get(at);
				result += gettype(attribute.typeid) + ", ";
			}
			if (result.length() >= 2)
				result = result.substring(0, result.length() - 2);
			out.println("%class." + c + " = type { " + result + " }");
		}
	}

	public void genCodeClassMethods(String c, PrintWriter out) {
		if (c.equals("Object")) {
			print_util.printObjectMethods(out);
			return;
		}
		if (c.equals("IO")) {
			print_util.printIOMethods(out);
			return;
		}
		ClassIR cur = classtable.irTable.get(c);
		String formals = gettype(c) + " %self";
		out.println("define i32 @_ZN" + c.length() + c + 8 + "__cons__( " + formals + " ) {");
		out.println("entry:");
		vars = -1;
		loops = -1;
		ifs = -1;
		List<String> blocks = new ArrayList<String>();
		for (Map.Entry<String, AST.attr> entry : cur.atrs.entrySet()) {
			AST.attr at = entry.getValue();
			if (!(at.value instanceof AST.no_expr)) {
				AST.assign exp = new AST.assign(at.name, at.value, 0);
				exp.type = at.typeid;
				egen.printexpr(c, null, exp, new ArrayList<>(), blocks, out);
			} else if (at.typeid.equals("Int")) {
				AST.assign exp = new AST.assign(at.name, new AST.int_const(0, 0), 0);
				exp.type = "Int";
				egen.printexpr(c, null, exp, new ArrayList<>(), blocks, out);
			} else if (at.typeid.equals("Bool")) {
				AST.assign exp = new AST.assign(at.name, new AST.bool_const(false, 0), 0);
				exp.type = "Bool";
				egen.printexpr(c, null, exp, new ArrayList<>(), blocks, out);
			} else if (at.typeid.equals("String")) {
				AST.assign exp = new AST.assign(at.name, new AST.string_const("", 0), 0);
				exp.type = "String";
				egen.printexpr(c, null, exp, new ArrayList<>(), blocks, out);
			} else {
				int attrinfo = cur.aList.indexOf(at.name);
				String ctype = gettype(c);
				ctype = ctype.substring(0, ctype.length() - 1);
				out.println("\t%" + (++vars) + " = getelementptr inbounds " + ctype + ", " + ctype
						+ "* %self, i32 0, i32 " + attrinfo);
				out.println("\tstore " + gettype(at.typeid) + " null, " + gettype(at.typeid) + "* %" + vars
						+ ", align 4");
			}
		}
		String prev = gettype(c) + " %self";
		ClassIR temp = cur;

		while (!getvar(prev).equals(gettype("Object"))) {
			String par = gettype(temp.parent);
			par = par.substring(0, par.length() - 1);
			String next = getvar(prev);
			next = next.substring(0, next.length() - 1);
			out.println("\t%" + (++vars) + " = getelementptr inbounds " + next + ", " + next + "* "
					+ getvarvalue(prev) + ", i32 0, i32 0");
			prev = par + "* %" + vars;
			temp = classtable.irTable.get(temp.parent);
		}
		out.println("\t%" + (++vars) + " = getelementptr inbounds %class.Object, %class.Object* "
				+ getvarvalue(prev) + ", i32 0, i32 0");
		out.println("\tstore i32 " + cur.size + ", i32* %" + vars);
		String next = "[" + (c.length() + 1) + " x i8]";
		totalStrings += "@.str" + (strings++) + " = private unnamed_addr constant " + next + " c\"" + c
				+ "\\00\", align 1\n";
		out.println("\t%" + (++vars) + " = bitcast " + next + "* @.str" + (strings - 1) + " to [1024 x i8]*");
		out.println("\t%" + (++vars) + " = getelementptr inbounds %class.Object, %class.Object* "
				+ getvarvalue(prev) + ", i32 0, i32 1");
		out.println("\tstore [1024 x i8]* %" + (vars - 1) + ", [1024 x i8]** %" + vars);
		out.println("\tret i32 0");
		out.println("}\n");
		for (Map.Entry<String, AST.method> entry : cur.mtds.entrySet()) {
			AST.method m = entry.getValue();
			formals = "%class." + c + "* %self";
			for (AST.formal f : m.formals) {
				formals += ", " + gettype(f.typeid) + " %" + f.name;
			}
			blocks.clear();
			// Set return type for main function in Main class
			if (c.equals("Main") && entry.getKey().equals("main"))
				mainReturnType = gettype(m.typeid);

			// start defining the function
			out.println("define " + gettype(m.typeid) + " " + cur.methodIR.get(entry.getKey()) + "( " + formals + " )"
					+ "{");
			out.println("entry:");
			blocks.add("entry");
			vars = -1;
			loops = -1;
			ifs = -1;
			List<String> new_formals = new ArrayList<>();
			// Allot all formals in the stack
			for (AST.formal f : m.formals) {
				String f_typ = gettype(f.typeid);
				out.println("%" + f.name + ".addr = alloca " + f_typ + ", align 4");
				new_formals.add(f.name);
				out.println("\tstore " + f_typ + " %" + f.name + ", " + f_typ + "* %" + f.name + ".addr, align 4");
			}
			// Print LLVM-IR for body of the function
			String result = egen.printexpr(c, m, m.body, new_formals, blocks, out);
			String resulttype = getvar(result);
			// Print return statement
			if (!resulttype.equals(gettype(m.typeid))) {
				if (resulttype.equals("i32")) {
					out.println("\t%" + (++vars) + " = call noalias i8* @malloc(i64 8)"); // Object size
					out.println("\t%" + (++vars) + " = bitcast i8* %" + (vars - 1) + " to " + gettype(m.typeid));
				} else {
					out.println("\t%" + (++vars) + " = bitcast " + result + " to " + gettype(m.typeid));
				}
				result = gettype(m.typeid) + " %" + vars;
			}
			out.println("\tret " + result);
			out.println("}\n");
		}
	}

	public String reverseParseType(String t) {
		if (t.equals("i32")) {
			System.out.println("I will never come here reverseParseType");
			return "Int";
		} else if (t.equals("[1024 x i8]*")) {
			return "String";
		} else {
			return t.substring(7, t.length() - 1);
		}
	}

}
