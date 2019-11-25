# Code Gen



## Design of our CodeGen

We first traverse through the AST and construct a ClassIRTable which consists of information about each class that is the features and attributes for each class. An Adjacency list is constructed based on the parent of a class and its class which is used to traverse the inheritance graph in a breadth-first search manner. While traversing through the classes we print the particular class definitions in LLVM-IR and then print the Attributes and methods in the class. The attributes of the class CodeGen are used to keep track of the number of variables,strings ,if_conditions and loops in the code up until the point where we use them so that we dont repeat the same variable name in LLVM-IR twice.

Printer.java prints the built-in declarations of the classes IO,Object, String and their methods.
ExprGen.java is used to generate LLVM-IR code for each subclass of AST.expr in the AST.We use an overloaded function to check the kind of subclass it is present in so that we print IR based on the type of the expression.