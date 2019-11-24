package cool;

import java.util.*;
import java.io.PrintWriter;

public class ExprGen{
    Codegen mygen;
    public ExprGen(Codegen cgparent){
        mygen = cgparent;
    }
    public String printexpr(String class_name, AST.method method, AST.expression expr, List<String> changedformals,
            List<String> blocks, PrintWriter out) {
        ClassIR ci = mygen.classtable.irTable.get(class_name);
        switch (expr.getname()) {
        case "bool_const": { // Bool const
            AST.bool_const temp = (AST.bool_const) expr;
            return "i32 " + (temp.value ? 1 : 0);
        }
        case "string_const": {
            // String const
            AST.string_const temp = (AST.string_const) expr;
            return visit(temp, out);
        }
        case "int_const": { // Int const
            AST.int_const temp = (AST.int_const) expr;
            return "i32 " + temp.value;
        }
        case "object": {
            AST.object temp = (AST.object) expr;
            return visit(temp, class_name, ci, method, changedformals, out);
        }
        case "comp": {
            AST.comp temp = (AST.comp) expr;
            String op = printexpr(class_name, method, temp, changedformals, blocks, out);
            out.println("\t%" + (++mygen.vars) + " = sub nsw i32 1, " + op.substring(4));
            return "i32 %" + mygen.vars;
        }
        case "eq": {
            AST.eq temp = (AST.eq) expr;
            String op1 = printexpr(class_name, method, temp.e1, changedformals, blocks, out);
            String op2 = printexpr(class_name, method, temp.e2, changedformals, blocks, out);
            out.println("\t%" + (++mygen.vars) + " = icmp eq i32 " + op1.substring(4) + ", " + op2.substring(4));
            return "i32 %" + mygen.vars;
        }
        case "leq": {
            AST.leq temp = (AST.leq) expr;
            String op1 = printexpr(class_name, method, temp.e1, changedformals, blocks, out);
            String op2 = printexpr(class_name, method, temp.e2, changedformals, blocks, out);
            out.println("\t%" + (++mygen.vars) + " = icmp sle i32 " + op1.substring(4) + ", " + op2.substring(4));
            return "i32 %" + mygen.vars;
        }
        case "lt": {
            AST.lt temp = (AST.lt) expr;
            String op1 = printexpr(class_name, method, temp.e1, changedformals, blocks, out);
            String op2 = printexpr(class_name, method, temp.e2, changedformals, blocks, out);
            out.println("\t%" + (++mygen.vars) + " = icmp slt i32 " + op1.substring(4) + ", " + op2.substring(4));
            return "i32 %" + mygen.vars;
        }
        case "neg": {
            AST.neg temp = (AST.neg) expr;
            String op = printexpr(class_name, method, temp.e1, changedformals, blocks, out);
            out.println("\t%" + (++mygen.vars) + " = sub nsw i32 0, " + op.substring(4));
            return "";
        }
        case "divide": {
            AST.divide temp = (AST.divide) expr;
            String op1 = printexpr(class_name, method, temp.e1, changedformals, blocks, out);
            String op2 = printexpr(class_name, method, temp.e2, changedformals, blocks, out);
            return visit(temp, blocks, out, op1, op2);
        }
        case "mul": {
            AST.mul temp = (AST.mul) expr;
            String op1 = printexpr(class_name, method, temp.e1, changedformals, blocks, out);
            String op2 = printexpr(class_name, method, temp.e2, changedformals, blocks, out);
            out.println("\t%" + (++mygen.vars) + " = mul nsw i32 " + op1.substring(4) + ", " + op2.substring(4));
            return "i32 %" + mygen.vars;
        }
        case "sub": {
            AST.sub temp = (AST.sub) expr;
            String op1 = printexpr(class_name, method, temp.e1, changedformals, blocks, out);
            String op2 = printexpr(class_name, method, temp.e2, changedformals, blocks, out);
            out.println("\t%" + (++mygen.vars) + " = sub nsw i32 " + op1.substring(4) + ", " + op2.substring(4));
            return "i32 %" + mygen.vars;
        }
        case "plus": {
            AST.plus temp = (AST.plus) expr;
            String op1 = printexpr(class_name, method, temp.e1, changedformals, blocks, out);
            String op2 = printexpr(class_name, method, temp.e2, changedformals, blocks, out);
            out.println("\t%" + (++mygen.vars) + " = add nsw i32 " + op1.substring(4) + ", " + op2.substring(4));
            return "i32 %" + mygen.vars;
        }
        case "isvoid": {
            AST.isvoid temp = (AST.isvoid) expr;
            String op = printexpr(class_name, method, temp.e1, changedformals, blocks, out);
            out.println("\t%" + (++mygen.vars) + " = icmp eq " + op + ", null");
            return "i32 %" + mygen.vars;
        }
        case "new_": {
            AST.new_ temp = (AST.new_) expr;
            return visit(temp, out);
        }
        case "assign": {
            AST.assign temp = (AST.assign) expr;
            String lvalue = printexpr(class_name, method, temp.e1, changedformals, blocks, out);
            return visit(temp, ci, lvalue, class_name, method, changedformals, out);
        }
        case "block": {
            AST.block temp = (AST.block) expr;
            String return_var = "";
            for (AST.expression e : temp.l1) {
                return_var = printexpr(class_name, method, e, changedformals, blocks, out);
            }
            return return_var;
        }
        case "loop": {
            AST.loop temp = (AST.loop) expr;
            return visit(temp, class_name, method, changedformals, out, blocks);
        }
        case "cond": {
            AST.cond temp = (AST.cond) expr;
            return visit(temp, class_name, method, blocks, changedformals, out);
        }
        case "static_dispatch": {
            AST.static_dispatch temp = (AST.static_dispatch) expr;
            return visit(temp, class_name, method, blocks, changedformals, out);
        }
        default:
            System.out.println(expr.getname());
            break;
        }
        return "";
    }

    public String visit(AST.new_ expr, PrintWriter out) {
        String type = mygen.parseType(expr.typeid);
        int size = mygen.classtable.irTable.get(expr.typeid).size;
        out.println("\t%" + (++mygen.vars) + " = call noalias i8* @malloc(i64 " + size + ")");
        out.println("\t%" + (++mygen.vars) + " = bitcast i8* %" + (mygen.vars - 1) + " to " + type);
        out.println("\t%" + (++mygen.vars) + " = call i32 @_ZN" + expr.typeid.length() + expr.typeid + 8 + "__cons__( " + type
                + " %" + (mygen.vars - 1) + " )");
        return type + " %" + (mygen.vars - 1);
    }

    public String visit(AST.string_const expr, PrintWriter out) {
        String ty = "[" + (expr.value.length() + 1) + " x i8";
        mygen.totalStrings += "@.str" + (mygen.strings++) + " = private unnamed_addr constant " + ty + " c\"" + expr.value
                + "\\00\", align 1\n";
        out.println("\t%" + (++mygen.vars) + " = bitcast " + ty + "* @.str" + (mygen.strings - 1) + " to [1024 x i8]*");
        return "[1024 x i8]* %" + mygen.vars;
    }

    public String visit(AST.divide expr, List<String> blocks, PrintWriter out, String op1, String op2) {
        out.println("\t%" + (++mygen.vars) + " = icmp eq i32 0, " + op2.substring(4));
        mygen.ifs++;
        out.println("\tbr i1 %" + mygen.vars + ", label %if.then" + mygen.ifs + ", label %if.else" + mygen.ifs);
        out.println();
        out.println("if.then" + mygen.ifs + ":");
        blocks.add("if.then" + mygen.ifs);
        out.println("\t%" + (++mygen.vars) + " = bitcast [22 x i8]* @Abortdivby0 to [1024 x i8]*");
        out.println("\t%" + (++mygen.vars) + " = call %class.IO* @_ZN2IO10out_string( %class.IO* null, [1024 x i8]* %"
                + (mygen.vars - 1) + ")");
        out.println("\tcall void @exit(i32 1)");
        out.println("\tbr label %if.else" + mygen.ifs);
        out.println();
        out.println("if.else" + mygen.ifs + ":");
        blocks.add("if.else" + mygen.ifs);
        out.println("\t%" + (++mygen.vars) + " = sdiv i32 " + op1.substring(4) + ", " + op2.substring(4));
        return "i32 %" + mygen.vars;
    }

    public String visit(AST.assign expr, ClassIR ci, String lvalue, String class_name, AST.method method,
            List<String> changedformals, PrintWriter out) {
        int attri = ci.aList.indexOf(expr.name);
        if (method != null) {
            for (AST.formal f : method.formals) {
                if (!f.name.equals(expr.name))
                    continue;
                else {
                    attri = -1;
                    break;
                }
            }
        }
        String type = mygen.parseType(expr.type);
        String ctype = mygen.parseType(class_name);
        ctype = ctype.substring(0, ctype.length() - 1);
        String ltype = mygen.reverseParseTypeValue(lvalue);
        if (!ltype.equals(type) && ltype.equals("i32")) {
            out.println("\t%" + (++mygen.vars) + " = call noalias i8* @malloc(i64 8)");
            out.println("\t%" + (++mygen.vars) + " = bitcast i8* %" + (mygen.vars - 1) + " to " + type);
            lvalue = type + " %" + mygen.vars;
        } else if (!ltype.equals(type)) {
            out.println("\t%" + (++mygen.vars) + " = bitcast " + lvalue + " to " + type);
            lvalue = type + " %" + mygen.vars;
        }
        if (attri == -1) {
            if (changedformals.indexOf(expr.name) == -1) {
                out.println("%" + expr.name + ".addr = alloca " + type + ", align 4");
                changedformals.add(expr.name);
            }
            out.println("\tstore " + lvalue + ", " + type + "* %" + expr.name + ".addr, align 4");
            return lvalue;
        } else {
            out.println("\t%" + (++mygen.vars) + " = getelementptr inbounds " + ctype + ", " + ctype + "* %self, i32 0, i32 "
                    + attri);
            out.println("\tstore " + lvalue + ", " + type + "* %" + mygen.vars + ", align 4");
            return lvalue;
        }
    }

    public String visit(AST.loop expr, String class_name, AST.method method, List<String> changedformals,
            PrintWriter out, List<String> blocks) {
        int loopcount = ++mygen.loops;
        out.println("\tbr label %loop.cond" + loopcount);
        out.println("loop.cond" + loopcount + ":");
        blocks.add("loop.cond" + loopcount);
        String pred = printexpr(class_name, method, expr.predicate, changedformals, blocks, out);
        out.println(
                "\tbr i1 " + pred.substring(4) + ", label %loop.body" + loopcount + " , label %loop.end" + loopcount);
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

    public String visit(AST.cond expr, String class_name, AST.method method, List<String> blocks,
            List<String> changedformals, PrintWriter out) {
        int condcount = ++mygen.ifs;
        String pred = printexpr(class_name, method, expr.predicate, changedformals, blocks, out);
        out.println("\tbr i1 " + pred.substring(4) + ", label %if.then" + mygen.ifs + ", label %if.else" + condcount);
        out.println();
        out.println("if.then" + mygen.ifs + ":");
        blocks.add("if.then" + mygen.ifs);
        String ifbody = printexpr(class_name, method, expr.ifbody, changedformals, blocks, out);
        String ifbodylabel = blocks.get(blocks.size() - 1);
        ifbody = mygen.reverseParseTypeValueVar(ifbody);
        out.println("\tbr label %if.end" + mygen.ifs);
        out.println();
        out.println("if.else" + mygen.ifs + ":");
        blocks.add("if.else" + mygen.ifs);
        String elsebody = printexpr(class_name, method, expr.elsebody, changedformals, blocks, out);
        String elsebodylabel = blocks.get(blocks.size() - 1);
        elsebody = mygen.reverseParseTypeValueVar(elsebody);
        out.println("\tbr label %if.end" + mygen.ifs);
        out.println();
        out.println("if.end" + mygen.ifs + ":");
        blocks.add("if.end" + mygen.ifs);
        out.println("\t%" + (++mygen.vars) + " = phi " + mygen.parseType(expr.type) + " [" + ifbody + ", %" + ifbodylabel + "], ["
                + elsebody + ", %" + elsebodylabel + "]");
        return mygen.parseType(expr.type) + " %" + mygen.vars;
    }

    public String visit(AST.static_dispatch expr, String class_name, AST.method method, List<String> blocks,
            List<String> changedformals, PrintWriter out) {
        String clr = printexpr(class_name, method, expr.caller, changedformals, blocks, out);
        List<String> actuals = new ArrayList<>();
        for (AST.expression e : expr.actuals) {
            String a = printexpr(class_name, method, e, changedformals, blocks, out);
            actuals.add(a);
        }
        mygen.ifs++;
        out.println("\t%" + (++mygen.vars) + " = icmp eq " + clr + ", null");
        out.println("\tbr i1 %" + mygen.vars + ", label %if.then" + mygen.ifs + ", label %if.else" + mygen.ifs);
        out.println();
        out.println("if.then" + mygen.ifs + ":");
        blocks.add("if.then" + mygen.ifs);
        out.println("\t%" + (++mygen.vars) + " = bitcast [25 x i8]* @Abortdispvoid to [1024 x i8]*");
        out.println("\t%" + (++mygen.vars) + " = call %class.IO* @_ZN2IO10out_string( %class.IO* null, [1024 x i8]* %"
                + (mygen.vars - 1) + ")");
        out.println("\tcall void @exit(i32 1)");
        out.println("\tbr label %if.else" + mygen.ifs);
        out.println();
        out.println("if.else" + mygen.ifs + ":");
        blocks.add("if.else" + mygen.ifs);
        String funcname = "@_ZN" + expr.typeid.length() + expr.typeid + expr.name.length() + expr.name;
        ClassIR table2 = mygen.classtable.irTable.get(mygen.reverseParseType(mygen.reverseParseTypeValue(clr)));
        while (!mygen.reverseParseTypeValue(clr).equals(mygen.parseType(expr.typeid))) {
            String par = mygen.parseType(table2.parent);
            par = par.substring(0, par.length() - 1);
            String ty = mygen.reverseParseTypeValue(clr);
            ty = ty.substring(0, ty.length() - 1);
            out.println("\t%" + (++mygen.vars) + " = getelementptr inbounds " + ty + ", " + ty + "* "
                    + mygen.reverseParseTypeValueVar(clr) + ", i32 0, i32 0");
            clr = par + "* %" + mygen.vars;
            table2 = mygen.classtable.irTable.get(table2.parent);
        }
        String actstr = clr;
        for (int i = 0; i < actuals.size(); i++) {
            actstr = actstr + ", " + actuals.get(i);
        }
        out.println("\t%" + (++mygen.vars) + " = call " + mygen.parseType(expr.type) + " " + funcname + "(" + actstr + ")");
        return mygen.parseType(expr.type) + " %" + mygen.vars;
    }

    public String visit(AST.object expr, String class_name, ClassIR ci, AST.method method, List<String> changedformals,
            PrintWriter out) {
        int atr = ci.aList.indexOf(expr.name);
        for (AST.formal f : method.formals) {
            if (f.name.equals(expr.name)) {
                atr = -1;
                break;
            }
        }
        if (atr == -1) {
            if (changedformals.indexOf(expr.name) == -1)
                return mygen.parseType(expr.type) + " %" + expr.name;
            else {
                String type = mygen.parseType(expr.type);
                out.println("\t%" + (++mygen.vars) + " = load " + type + ", " + type + "* %" + expr.name + ".addr, align 4");
                return type + " %" + mygen.vars;
            }
        }
        String typename = mygen.parseType(class_name);
        typename = typename.substring(0, typename.length() - 1);
        out.println("\t%" + (++mygen.vars) + " = getelementptr inbounds " + typename + ", " + typename
                + "* %self, i32 0, i32 " + atr);
        out.println("\t%" + (++mygen.vars) + " = load " + mygen.parseType(expr.type) + ", " + mygen.parseType(expr.type) + "* %"
                + (mygen.vars - 1) + ", align 4");
        return mygen.parseType(expr.type) + " %" + mygen.vars;
    }
}
