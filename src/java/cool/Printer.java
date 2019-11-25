
package cool;

import java.io.PrintWriter;
class Printer{
    public Printer(PrintWriter out){
        printHeaders(out);
        printMainFunc(out);
        printStringMethods(out);
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
            +"\tcall i32"+" @_ZN4Main4main(%class.Main* %0)\n"
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
}
