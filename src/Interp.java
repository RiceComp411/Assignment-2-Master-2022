/** Call-by-value, Call-by-name, and Call-by-need Jam interpreter */

import java.io.IOException;
import java.io.Reader;

/** Interpreter Classes */

/** A class that implements call-by-value, call-by-name, and call-by-need interpretation of Jam programs. */
class Interpreter {
  
  /** program for this interpeter, initialized in constructors. */
  AST prog;
  
  /** Constructor for a program given in a file. */
  Interpreter(String fileName) throws IOException { 
    Parser p = new Parser(fileName);
    prog = p.parse();
  }
  
  /** Constructor for a program already embedded in a Parser object. */
  Interpreter(Parser p) { 
    prog = p.parse();
  }  
  
  /** Constructor for a program embedded in a Reader. */
  Interpreter(Reader reader) { 
    Parser p = new Parser(reader);
    prog = p.parse();
  }
  
  /** Parses and CBV interprets the input embeded in parser */
  public JamVal callByValue() { return prog.accept(valueEvalVisitor); }
  
  /** Parses and CBNm interprets the input embeded in parser */
  public JamVal callByName() { return prog.accept(nameEvalVisitor); }
  
  /** Parses and CBNd interprets the input embeded in parser */
  public JamVal callByNeed() { return prog.accept(needEvalVisitor); }
   
  /** Some static inner classes and interfaces  can go here ... */
  
  /** Note: a static inner class or interface nested inside the Interpreter class is just like a top-level class except 
    * that it can only be accessed from other classes by using a qualified name Interpreter.<classname>.  If you declare
    * them as private then they are hidden from code outside of the Interpreter class.  I find the Java command line
    * convention of one-class per file very awkward.  In this way we can put the entire interpreter in a single file and
    * we can even use pubic component classes if we choose. Note that the Interpreter class cannot be public unless you 
    * rename this file as Interpreter.java. */
  
 
  /** The interface supporting various evaluation policies (CBV, CBNm, CBNd) for map applications and variable 
    * lookups. The EvalVisitor parameter appears in each method because the environment is carried by a EvalVisitor.
    * Hence as an EvalPolicy is used to interpret an expression, The passed EvalVisitor will change (!) as the
    * environment changes.  An EvalPolicy should NOT contain an EvalVisitor field.
    */
  interface EvalPolicy {
    /** Evaluates the let construct composed of var, exps, and body */
    JamVal letEval(Variable[] vars, AST[] exps, AST body, EvalVisitor ev);
    
    /** Constructs a BinOpVisitor with the specified unevaluated arguments and EvalVisitor */
    BinOpVisitor<JamVal> newBinOpVisitor(AST arg1, AST arg2, EvalVisitor ev);

    /** Constructs the appropriate binding object for this, binding var to ast in the evaluator ev */
    Binding newBinding(Variable var, AST ast, EvalVisitor ev);
  }
  
  /** An ASTVisitor class for evaluating ASTs where details of behavior are determined by an embedded EvalPolicy. */
  static class EvalVisitor implements ASTVisitor<JamVal> {
    
    /* The code in this class assumes that:
     * * OpTokens are unique; 
     * * Variable objects are unique: v1.name.equals(v.name) => v1 == v2; and
     * * The only objects used as boolean values are BoolConstant.TRUE and BoolConstant.FALSE.
     * Hence,  == can be used to compare Variable objects, OpTokens, and BoolConstants. */
    
    PureList<Binding> env;  // the embdedded environment
    EvalPolicy evalPolicy;  // the embedded EvalPolicy
    
    /** Constructor for recursive calls. */
    private EvalVisitor(PureList<Binding> e, EvalPolicy ep) { 
      env = e; 
      evalPolicy = ep; 
    }
    
    /** Top level constructor. */
    public EvalVisitor(EvalPolicy ep) { this(new Empty<Binding>(), ep); }
    
    /* Your code for EvalVisitor and other static inner classes follows ...
	 * You obviously need to define the forXXXX methods for the EvalVisitor class, and define the methods 
     * in your EvalPolicy subclasses encapsulating the differences among the three forms of interpretation.
	 */
  }
    
  /** Top-level EvalVisitors implementing CBV, CBNm, and CBNd evaluation. */
  static EvalVisitor valueEvalVisitor = new EvalVisitor(CallByValue.ONLY);   // EvalVisitor customized by the CallByValue.ONLY evaluation policy
  static EvalVisitor nameEvalVisitor = new EvalVisitor(CallByName.ONLY);     // EvalVisitor customized by the CallByName.ONLY evaluation policy
  static EvalVisitor needEvalVisitor = new EvalVisitor(CallByNeed.ONLY);     // EvalVisitor customized by the CallByNeed.ONLY evaluation policy
  
   /* Your code following the definition of EvalVisitor will presumably include definitions of the supporting static 
    * inner classes and interfaces required to implement the interpreters.  We recommend using a visitor or Java enum
    * to handle the collection of UnOps, the collection of BinOps, and the collection of PrimFuns.  For each collection,
    * you need to perform a simple case split, which can be performed either by visitor acceptance or enum switch case).  
    * The interp.java file for the class solution contains under 500 lines.  Java is very wordy so the actual logic is 
	* reasonably simple if you cleanly factor out the differences among the three forms of evaluation.  Much of the code
    * in the interpreter is the repetitive implementation of the primitive operations (UnOps, BinOps, and PrimFuns) of Jam.  
	* It may be helpful to initially implement call-by-value interpretation for a small subset of these primitive operations
    * and use/create test data only involving this subset.  After you get call-by-value working add call-by-name and 
	* call-by-need (which is only slightly different from call-by-name).  Once you have all three interpreters working for
	* the small subset of operations, add the implementations for the missing primitive operations and tests that use them.  
	* Note that the class AST.java in the support code contains nearly 350 lines, so less than 500 lines of Java is a modest
	* amount of Java code.  Don't be concerned if you need 600+ lines to write your solution.
	*
    * Hint: each different form of evaluation involves environments (list of binding objects) tailored to support the
    * appropriate argument evaluation behavior.  The call-by-value, call-by-name, and call-by-need protocols evaluate
    * argument expressions at different points in the evaluation process. */
  
  /* Many more supporting static inner classes and static constants ...  */
  
}

class EvalException extends RuntimeException {
  EvalException(String msg) { super(msg); }
}
