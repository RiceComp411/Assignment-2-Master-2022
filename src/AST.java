/** AST ::= BoolConstant | IntConstant | JamEmpty | Variable | PrimFun | UnOpApp | BinOpApp | App | Map | If | Let */

/** AST class definitions */

/** The AST type which support a visitor interface */
interface AST {
  public <ResType> ResType accept(ASTVisitor<ResType> v);
}

interface ASTVisitor<ResType> {
  ResType forBoolConstant(BoolConstant b);
  ResType forIntConstant(IntConstant i);
  ResType forEmptyConstant(EmptyConstant n);
  ResType forJamEmpty(JamEmpty je);
  ResType forVariable(Variable v);
  ResType forPrimFun(PrimFun f);
  ResType forUnOpApp(UnOpApp u);
  ResType forBinOpApp(BinOpApp b);
  ResType forApp(App a);
  ResType forMap(Map m);
  ResType forIf(If i);
  ResType forLet(Let l);
}

/* The Term interface correspond to AST's that when output as concrete syntax by toString() can be used as terms 
 * in an iterative binary expression without enclosing parentheses. We enclose all binary expressions in parentheses
 * so we do not presume either left or right associativity for infix binary operators. */
/** Term ::= Constant | PrimFun | Variable | UnOpApp |  App */
interface Term extends AST {}

/* NOTE: all Constant objects belong to the types Token and AST; Constant tokens evaluate to themselves.
 * The variant classes (IntConstant, BoolConstant, JamEmpty) are defined in the file  ValuesTokens.java */

/** Constant ::= IntConstant | BoolConstant | EmptyConstant */
interface Constant extends Term {}

/** UnOp ::= UnOpPlus | UnOpMinus | OpTilde */
/** Class representing the unary operator within a UnOppApp. */
abstract class UnOp {
  String name;
  public UnOp(String s) { name = s; }
  public String toString() { return name; }
  public abstract <ResType> ResType accept(UnOpVisitor<ResType> v);
}

/** Visitor for the UnOp union type. */
interface UnOpVisitor<ResType> {
  ResType forUnOpPlus(UnOpPlus op);
  ResType forUnOpMinus(UnOpMinus op);
  ResType forOpTilde(OpTilde op);
  // ResType forOpBang(OpBang op);  // Supports ref cell extension to Jam
  // ResType forOpRef(OpRef op);    // Supports ref cell extension to Jam
}

/** BinOp ::= BinOpPlus | BinOpMinus | OpTimes | OpDivide | OpEquals | OpNotEquals | OpLessThan | OpGreaterThan |
  *           OpLessThanOrEquals OpGreaterThanOrEquals | OpAnd | OpOr */

/** Class representing the binary operator within a BinOppApp. */
abstract class BinOp {
  String name;
  public BinOp(String s) { name = s; }
  public String toString() { return name; }
  public abstract <ResType> ResType accept(BinOpVisitor<ResType> v);
}

/** Visitor for the BinOp union type. */
interface BinOpVisitor<ResType> {
  ResType forBinOpPlus(BinOpPlus op);
  ResType forBinOpMinus(BinOpMinus op);
  ResType forOpTimes(OpTimes op);
  ResType forOpDivide(OpDivide op);
  ResType forOpEquals(OpEquals op);
  ResType forOpNotEquals(OpNotEquals op);
  ResType forOpLessThan(OpLessThan op);
  ResType forOpGreaterThan(OpGreaterThan op);
  ResType forOpLessThanEquals(OpLessThanEquals op);
  ResType forOpGreaterThanEquals(OpGreaterThanEquals op);
  ResType forOpAnd(OpAnd op);
  ResType forOpOr(OpOr op);
  // ResType forOpGets(OpGets op);  // Supports the ref cell extension to Jam
}

class UnOpPlus extends UnOp {
  public static final UnOpPlus ONLY = new UnOpPlus();
  private UnOpPlus() { super("+"); }
  public <ResType> ResType accept(UnOpVisitor<ResType> v) {
    return v.forUnOpPlus(this); 
  }
}

class UnOpMinus extends UnOp {
  public static final UnOpMinus ONLY = new UnOpMinus();
  private UnOpMinus() { super("-"); }
  public <ResType> ResType accept(UnOpVisitor<ResType> v) {
    return v.forUnOpMinus(this); 
  }
}

class OpTilde extends UnOp {
  public static final OpTilde ONLY = new OpTilde();
  private OpTilde() { super("~"); }
  public <ResType> ResType accept(UnOpVisitor<ResType> v) {
    return v.forOpTilde(this); 
  }
}

/*  Supports ref cell extension to Jam
  class OpBang extends UnOp {
  public static final OpBang ONLY = new OpBang();
  private OpBang() { super("!"); }
  public <ResType> ResType accept(UnOpVisitor<ResType> v) {
    return v.forOpBang(this); 
  }
}

class OpRef extends UnOp {
  public static final OpRef ONLY = new OpRef();
  private OpRef() { super("ref"); }
  public <ResType> ResType accept(UnOpVisitor<ResType> v) {
    return v.forOpRef(this); 
  }
}
*/

class BinOpPlus extends BinOp {
  public static final BinOpPlus ONLY = new BinOpPlus();
  private BinOpPlus() { super("+"); }
  public <ResType> ResType accept(BinOpVisitor<ResType> v) {
    return v.forBinOpPlus(this); 
  }
}

class BinOpMinus extends BinOp {
  public static final BinOpMinus ONLY = new BinOpMinus();
  private BinOpMinus() { super("-"); }
  public <ResType> ResType accept(BinOpVisitor<ResType> v) {
    return v.forBinOpMinus(this); 
  }
}

class OpTimes extends BinOp {
  public static final OpTimes ONLY = new OpTimes();
  private OpTimes() { super("*"); }
  public <ResType> ResType accept(BinOpVisitor<ResType> v) {
    return v.forOpTimes(this); 
  }
}

class OpDivide extends BinOp {
  public static final OpDivide ONLY = new OpDivide();
  private OpDivide() { super("/"); }
  public <ResType> ResType accept(BinOpVisitor<ResType> v) {
    return v.forOpDivide(this); 
  }
}

class OpEquals extends BinOp {
  public static final OpEquals ONLY = new OpEquals();
  private OpEquals() { super("="); }
  public <ResType> ResType accept(BinOpVisitor<ResType> v) {
    return v.forOpEquals(this); 
  }
}

class OpNotEquals extends BinOp {
  public static final OpNotEquals ONLY = new OpNotEquals();
  private OpNotEquals() { super("!="); }
  public <ResType> ResType accept(BinOpVisitor<ResType> v) {
    return v.forOpNotEquals(this); 
  }
}

class OpLessThan extends BinOp {
  public static final OpLessThan ONLY = new OpLessThan();
  private OpLessThan() { super("<"); }
  public <ResType> ResType accept(BinOpVisitor<ResType> v) {
    return v.forOpLessThan(this); 
  }
}

class OpGreaterThan extends BinOp {
  public static final OpGreaterThan ONLY = new OpGreaterThan();
  private OpGreaterThan() { super(">"); }
  public <ResType> ResType accept(BinOpVisitor<ResType> v) {
    return v.forOpGreaterThan(this); 
  }
}

class OpLessThanEquals extends BinOp {
  public static final OpLessThanEquals ONLY = new OpLessThanEquals();
  private OpLessThanEquals() { super("<="); }
  public <ResType> ResType accept(BinOpVisitor<ResType> v) {
    return v.forOpLessThanEquals(this); 
  }
}

class OpGreaterThanEquals extends BinOp {
  public static final OpGreaterThanEquals ONLY = new OpGreaterThanEquals();
  private OpGreaterThanEquals() { super(">="); }
  public <ResType> ResType accept(BinOpVisitor<ResType> v) {
    return v.forOpGreaterThanEquals(this); 
  }
}

class OpAnd extends BinOp {
  public static final OpAnd ONLY = new OpAnd();
  private OpAnd() { super("&"); }
  public <ResType> ResType accept(BinOpVisitor<ResType> v) {
    return v.forOpAnd(this); 
  }
}

class OpOr extends BinOp {
  public static final OpOr ONLY = new OpOr();
  private OpOr() { super("|"); }
  public <ResType> ResType accept(BinOpVisitor<ResType> v) {
    return v.forOpOr(this); 
  }
}

/* Supports the ref cell extension to Jam
class OpGets extends BinOp {
  public static final OpGets ONLY = new OpGets();
  private OpGets() { super("<-"); }
  public <ResType> ResType accept(BinOpVisitor<ResType> v) {
    return v.forOpGets(this); 
  }
}
*/

/* UnOpApp is a Term because it does not need enclosing parentheses when appearing in a binary expression */
class UnOpApp implements Term {
  private UnOp rator;
  private AST arg;
  
  UnOpApp(UnOp r, AST a) { rator = r; arg = a; }
  
  public UnOp rator() { return rator; }
  public AST arg() { return arg; }
  public <ResType> ResType accept(ASTVisitor<ResType> v) { return v.forUnOpApp(this); }
  public String toString() { return rator + " " + arg; }
}

class BinOpApp implements Term {
  private BinOp rator;
  private AST arg1, arg2;
  
  BinOpApp(BinOp r, AST a1, AST a2) { rator = r; arg1 = a1; arg2 = a2; }
  
  public BinOp rator() { return rator; }
  public AST arg1() { return arg1; }
  public AST arg2() { return arg2; }
  public <ResType> ResType accept(ASTVisitor<ResType> v) { return v.forBinOpApp(this); }
  public String toString() {
    return "(" + toString(arg1) + " " + rator + " " + toString(arg2) + ")"; 
  }
  private String toString(AST arg) {
    String argString = arg.toString();
    if (! (arg instanceof Term)) return "(" + argString + ")";
    else return argString;
  }
}

class Map implements AST {
  private Variable[] vars;
  private AST body;
  
  Map(Variable[] v, AST b) { vars = v; body = b; }
  public Variable[] vars() { return vars; }
  public AST body() { return body; }
  public <ResType> ResType accept(ASTVisitor<ResType> v) { return v.forMap(this); }
  public String toString() { 
    return "map " + ToString.toString(vars,",") + " to " + body;
  }
}  

class App implements Term {
  private AST rator;
  private AST[] args;
  
  App(AST r, AST[] a) { rator = r; args = a; }
  
  public AST rator() { return rator; }
  public AST[] args() { return args; }
  
  public <ResType> ResType accept(ASTVisitor<ResType> v) { return v.forApp(this); }
  public String toString() { 
    if ((rator instanceof PrimFun) || (rator instanceof Variable))
      return rator + "(" + ToString.toString(args,", ") + ")"; 
    else
      return "(" +  rator + ")(" + ToString.toString(args,", ") + ")"; 
  }
}  

class If implements AST {
  private AST test, conseq, alt;
  If(AST t, AST c, AST a) { test = t; conseq = c; alt = a; }
  
  public AST test() { return test; }
  public AST conseq() { return conseq; }
  public AST alt() { return alt; }
  public <ResType> ResType accept(ASTVisitor<ResType> v) { return v.forIf(this); }
  public String toString() { 
    return "if " + test + " then " + conseq + " else " + alt ; 
  }
}  

class Let implements AST {
  private Def[] defs;
  private AST body;
  Let(Def[] d, AST b) { defs = d; body = b; }
  
  public <ResType> ResType accept(ASTVisitor<ResType> v) { return v.forLet(this); }
  public Def[] defs() { return defs; }
  public AST body() { return body; }
  public String toString() { 
    return "let " + ToString.toString(defs," ") + " in " + body; 
  }
}  

/** Def class representing a definition embedded inside a Let. */
class Def {
  private Variable lhs;
  private AST rhs;  
  
  Def(Variable l, AST r) { lhs = l; rhs = r; }
  public Variable lhs() { return lhs; }
  public AST rhs() { return rhs; }
  
  public String toString() { return lhs + " := " + rhs + ";"; }
}

/** Dummy class containing an improved toString method for arrays. */
class ToString {
  
  public static String toString(Object[] a, String s) {
    StringBuffer result = new StringBuffer();
    for (int i = 0; i < a.length; i++) {
      if (i > 0) result.append(s);
      Object elt = a[i];
      String eltString = (elt instanceof Object[]) ?
        toString((Object[]) elt, s) : elt.toString();
      result.append(eltString);
    }
    return result.toString();
  }
}