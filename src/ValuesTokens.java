/* JamVal and Token Data Definitions */

import java.util.NoSuchElementException;
  
/** A data object representing a Jam value.
  * JamVal := IntConstant | BoolConstant | JamList | JamFun */
interface JamVal {
  <ResType> ResType accept(JamValVisitor<ResType> jvv);
}

/** A visitor for the JamVal type (Jam values).  */
interface JamValVisitor<ResType> {
  ResType forIntConstant(IntConstant ji);
  ResType forBoolConstant(BoolConstant jb);
  ResType forJamList(JamList jl);
  ResType forJamFun(JamFun jf);
  // ResType forJamVoid(JamVoid jf);  // Supports the addition of recursive let to Jam; change impacts JamVal comment
}

/** JamVal classes */

/** A Jam integer constant, also used to represent an integer token for parsing.  */
class IntConstant implements Token, Constant, JamVal {
  private int value;
  
  IntConstant(int i) { value = i; }
  // duplicates can occur!
  
  public int value() { return value; }
  
  public <ResType> ResType accept(ASTVisitor<ResType> v) { return v.forIntConstant(this); }
  public <ResType> ResType accept(JamValVisitor<ResType> v) { return v.forIntConstant(this); }
  /** redefines equals so that equal integers are recognized as equal */
  public boolean equals(Object other) {
    return (other != null && this.getClass() == other.getClass()) && 
      (value == ((IntConstant)other).value());
  }
  /** computes the obvious hashcode for this consistent with equals. */
  public int hashcode() { return value; }
  public String toString() { return String.valueOf(value); }
}

/** A Jam boolean constant, also used to represent a boolean token for parsing. */
class BoolConstant implements Token, Constant, JamVal {
  private boolean value;
  private BoolConstant(boolean b) { value = b; }
  
  /** Singleton pattern definitions. */
  public static final BoolConstant FALSE = new BoolConstant(false);
  public static final BoolConstant TRUE = new BoolConstant(true);
  
  /** A factory method that returns BoolConstant corresponding to b. It is atatic because
    * it does not depend on this. */
  public static BoolConstant toBoolConstant(boolean b) { 
    if (b) return TRUE; 
    else return FALSE;
  }
  
  public boolean value() { return value; }
  public BoolConstant not() { if (this == FALSE) return TRUE; else return FALSE; }
  
  public <ResType> ResType accept(ASTVisitor<ResType> av) { return av.forBoolConstant(this); }
  public <ResType> ResType accept(JamValVisitor<ResType> jv) { return jv.forBoolConstant(this); }
  public String toString() { return String.valueOf(value); }
}

/* Immutable List and Binding Classes */

/** Interface for all Pure Lists.
  * PureList<ElemType> := Empty<ElemType> | Cons<ElemType>
  */
interface PureList<ElemType> {
  abstract PureList<ElemType> cons(ElemType o);
  abstract PureList<ElemType> empty();
  abstract <ResType> ResType accept(PureListVisitor<ElemType, ResType> v);
  abstract String toStringHelp();
  abstract PureList<ElemType> append(PureList<ElemType> addedElts);
}

/** The visitor interface for the type PureList<T> */
interface PureListVisitor<ElemType, ResType> {
  ResType forEmpty(Empty<ElemType> e);
  ResType forCons(Cons<ElemType> c);
}

/** An abstract class that factors out code common to classes Empty<T> and Cons<T> */
abstract class PureListClass<ElemType> implements PureList<ElemType> {
  public PureList<ElemType> cons(ElemType o) { return new Cons<ElemType>(o,this); }
  public PureList<ElemType> empty() { return new Empty<ElemType>(); }
  public abstract <ResType> ResType accept(PureListVisitor<ElemType, ResType> v);  
  // preceding DICTATED BY BUG IN JSR-14
}

/** The empty PureList<T> class */
class Empty<ElemType> extends PureListClass<ElemType> {
  public <ResType> ResType accept(PureListVisitor<ElemType,ResType> v) { return v.forEmpty(this); }
  public PureList<ElemType> append(PureList<ElemType> addedElts) { return addedElts; }
  
  /** overrides inherited equals because Empty is not a singleton! */
  public boolean equals(Object other) { 
    return (other != null && other.getClass() == this.getClass());
  }
  
  public String toString() { return "()"; }
  public String toStringHelp() { return ""; }
}

/** The non-empty PureList<T> class */
class Cons<ElemType> extends PureListClass<ElemType> {
  ElemType first;
  PureList<ElemType> rest;
  Cons(ElemType f, PureList<ElemType> r) { first = f; rest = r; }
  
  public <ResType> ResType accept(PureListVisitor<ElemType,ResType> v) { return v.forCons(this); }
  public PureList<ElemType> append(PureList<ElemType> addedElts) { 
    return new Cons<ElemType>(first, rest.append(addedElts)); 
  }
  
  public ElemType first() { return first; }
  public PureList<ElemType> rest() { return rest; }
  
  public boolean equals(Object other) { 
    if (other == null || this.getClass() != other.getClass()) return false;
    Cons otherCons = (Cons) other;
    return first.equals(otherCons.first) && rest.equals(otherCons.rest);
  }
  
  public String toString() { return "(" + first + rest.toStringHelp() + ")"; }
  
  public String toStringHelp() { return " " + first + rest.toStringHelp(); }
}

/** The Jam List class representing JamVals that are PureLists. 
  * JamList := JamEmpty | JamCons
  */
interface JamList extends PureList<JamVal>, JamVal {
  JamEmpty empty();
  JamCons cons(JamVal v);
}

/** A singleton class extending Empty<JamVal> representing the empty JamList. */
class JamEmpty extends Empty<JamVal> implements JamList {
  public static final JamEmpty ONLY = new JamEmpty();
  private JamEmpty() {}
  
  public JamEmpty empty() { return ONLY; }
  public JamCons cons(JamVal v) { return new JamCons(v, this); }
  public <ResType> ResType accept(JamValVisitor<ResType> v) { return v.forJamList(this); }
  public <ResType> ResType accept(ASTVisitor<ResType> v) { return v.forJamEmpty(this); }
}

class JamCons extends Cons<JamVal> implements JamList {
  public JamCons(JamVal v, JamList vList) {
    super(v, vList);
  }
  public JamEmpty empty() { return JamEmpty.ONLY; }
  public JamCons cons(JamVal v) { return new JamCons(v, this); }
  
  public <ResType> ResType accept(JamValVisitor<ResType> v) { return v.forJamList(this); }
  public JamList rest() { return (JamList) super.rest(); }
}

/** The basic Jam Binding class. Can be extended to support lazy (CBN) bindings. */
abstract class Binding {
  Variable var;
  JamVal value;
  Binding(Variable v, JamVal jv) { 
    var = v; value = jv;
  }
  public Variable var() { return var; }
  public JamVal value() { return value; }
  public void setValue(JamVal v) { value = v; }
}

/* Other JamVal classes */

/** The class representing a Jam function (closure or primitive function). 
  * JamFun := JamClosure | PrimFun
  */
abstract class JamFun implements JamVal {
  public <ResType> ResType accept(JamValVisitor<ResType> jvv) { return jvv.forJamFun(this); }
  abstract public <ResType> ResType accept(JamFunVisitor<ResType> jfv);
}

/** The visitor interface for the JamFun type */
interface JamFunVisitor<ResType> {
  ResType forJamClosure(JamClosure c);
  ResType forPrimFun(PrimFun pf);
}

/** The class representing a Jam Closure. */
class JamClosure extends JamFun {
  private Map body;
  private PureList<Binding> env;
  
  JamClosure(Map b, PureList<Binding> e) { body = b; env = e; }
  Map body() { return body; }
  PureList<Binding> env() { return env; }
  public <ResType> ResType accept(JamFunVisitor<ResType> jfv) { return jfv.forJamClosure(this); }
}

/** The class representing a Jam Primitive Function. 
  * PrimFun := FunctionPPrim | NumberPPrim | ListPPrim | ConsPPrim | EmptyPPrim | 
  *            ArityPrim | ConsPrim | FirstPrim | RestPrim 
  */
abstract class PrimFun extends JamFun implements Token, Term {
  private String name;
  PrimFun(String n) { name = n; }
  public String name() { return name; }
  public <ResType> ResType accept(ASTVisitor<ResType> v) { return v.forPrimFun(this); }
  public <ResType> ResType accept(JamFunVisitor<ResType> v) { return v.forPrimFun(this); }
  abstract public <ResType> ResType accept(PrimFunVisitor<ResType> pfv);
  public String toString() { return name; }
}

///** A dummy Jam value used to implement recursive let. */
// class JamVoid implements JamVal {
//  public static final JamVoid ONLY = new JamVoid();
//  private JamVoid() {}
//  public <ResType> ResType accept(JamValVisitor<ResType> jvv) { return jvv.forJamVoid(this); }
//}

/** A visitor for the singleton PrimFun classes. */
interface PrimFunVisitor<ResType> {
  ResType forFunctionPPrim();
  ResType forNumberPPrim();
  ResType forListPPrim();
  ResType forConsPPrim();
  ResType forEmptyPPrim();
  ResType forArityPrim();
  ResType forConsPrim();
  ResType forFirstPrim();
  ResType forRestPrim();
}

/* The singleton Classes Representing Primitive Function Values */

/** A singleton class representing the primitive operation 'function?'. */
class FunctionPPrim extends PrimFun {
  public static final FunctionPPrim ONLY = new FunctionPPrim();
  private FunctionPPrim() { super("function?"); }
  public <ResType> ResType accept(PrimFunVisitor<ResType> pfv) { return pfv.forFunctionPPrim(); }
}

/** A singleton class representing the primitive operation 'number?'. */
class NumberPPrim extends PrimFun {
  public static final NumberPPrim ONLY = new NumberPPrim();
  private NumberPPrim() { super("number?"); }
  public <ResType> ResType accept(PrimFunVisitor<ResType> pfv) { return pfv.forNumberPPrim(); }
}

/** A singleton class representing the primitive operation 'list?'. */
class ListPPrim extends PrimFun {
  public static final ListPPrim ONLY = new ListPPrim();
  private ListPPrim() { super("list?"); }
  public <ResType> ResType accept(PrimFunVisitor<ResType> pfv) { return pfv.forListPPrim(); }
}

/** A singleton class representing the primitive operation 'cons?'. */
class ConsPPrim extends PrimFun {
  public static final ConsPPrim ONLY = new ConsPPrim();
  private ConsPPrim() { super("cons?"); }
  public <ResType> ResType accept(PrimFunVisitor<ResType> pfv) { return pfv.forConsPPrim(); }
}

/** A singleton class representing the primitive operation 'empty?'. */
class EmptyPPrim extends PrimFun {
  public static final EmptyPPrim ONLY = new EmptyPPrim();
  private EmptyPPrim() { super("empty?"); }
  public <ResType> ResType accept(PrimFunVisitor<ResType> pfv) { return pfv.forEmptyPPrim(); }
}

///** A singleton class representing the primitive operation 'ref?'. */
//class RefPPrim extends PrimFun {
//  public static final RefPPrim ONLY = new RefPPrim();
//  private RefPPrim() { super("ref?"); }
//  public <ResType> ResType accept(PrimFunVisitor<ResType> pfv) { return pfv.forRefPPrim(); }
//}

/** A singleton class representing the primitive operation 'arity'. */
class ArityPrim extends PrimFun {
  public static final ArityPrim ONLY = new ArityPrim();
  private ArityPrim() { super("arity"); }
  public <ResType> ResType accept(PrimFunVisitor<ResType> pfv) { return pfv.forArityPrim(); }
}

/** A singleton class representing the primitive operation 'cons'. */
class ConsPrim extends PrimFun {
  public static final ConsPrim ONLY = new ConsPrim();
  private ConsPrim() { super("cons"); }
  public <ResType> ResType accept(PrimFunVisitor<ResType> pfv) { return pfv.forConsPrim(); }
}

/** A singleton class representing the primitive operation 'first'. */
class FirstPrim extends PrimFun {
  public static final FirstPrim ONLY = new FirstPrim();
  private FirstPrim() { super("first"); }
  public <ResType> ResType accept(PrimFunVisitor<ResType> pfv) { return pfv.forFirstPrim(); }
}

/** A singleton class representing the primitive operation 'rest'. */
class RestPrim extends PrimFun {
  public static final RestPrim ONLY = new RestPrim();
  private RestPrim() { super("rest"); }
  public <ResType> ResType accept(PrimFunVisitor<ResType> pfv) { return pfv.forRestPrim(); }
}

/* The Jam Token classes */

/** The interface for Jam Tokens. Most Token classes are singletons.
  * Token := JamEmpty | Variable | OpToken | KeyWord | LeftParen | RightParen | LeftBrack | RightBrack | 
  *          Comma | Semicolon | EndOfFile
  */
interface Token {}

/** Empty constant class. Part of AST and Token composite hierarchies. */
class EmptyConstant implements Token, Constant {
  public static final EmptyConstant ONLY = new EmptyConstant();
  private EmptyConstant() {}
  public <T> T accept(ASTVisitor<T> v) { return v.forEmptyConstant(this); }
  public String toString() { return "empty"; }
}

/** A class representing a Jam Variable.  Each distinct variable name is uniquely represented by a Variable object. */
class Variable implements Token, Term {
  private String name;
  Variable(String n) { name = n; }
  
  public String name() { return name; }
  public <ResType> ResType accept(ASTVisitor<ResType> v) { return v.forVariable(this); }
  public String toString() { return name; }
}

/* A class representing a Jam operator Token. */
class OpToken implements Token {
  private String symbol;
  private boolean isUnOp;
  private boolean isBinOp;
  /** the corresponding unary operator in UnOp */
  private UnOp unOp;
  /** the corresponding binary operator in BinOp */
  private BinOp binOp;
  
  private OpToken(String s, boolean iu, boolean ib, UnOp u, BinOp b) {
    symbol = s; isUnOp = iu; isBinOp = ib; unOp = u; binOp = b; 
  }
  
  /** factory method for constructing OpToken serving as both UnOp and BinOp */
  public static OpToken newBothOpToken(UnOp u, BinOp b) {
    return new OpToken(u.toString(), true, true, u, b);
  }
  
  /** factory method for constructing OpToken serving as BinOp only */
  public static OpToken newBinOpToken(BinOp b) {
    return new OpToken(b.toString(), false, true, null, b);
  }
  
  /** factory method for constructing OpToken serving as UnOp only */
  public static OpToken newUnOpToken(UnOp u) {
    return new OpToken(u.toString(), true, false, u, null);
  }
  public String symbol() { return symbol; }
  public boolean isUnOp() { return isUnOp; }
  public boolean isBinOp() { return isBinOp; }
  public UnOp toUnOp() { 
    if (unOp == null) 
      throw new NoSuchElementException("OpToken " + this + " does not denote a unary operator");
    return unOp;
  }
  
  public BinOp toBinOp() { 
    if (binOp == null) 
      throw new NoSuchElementException("OpToken " + this + " does not denote a binary operator");
    return binOp; 
  }
  public String toString() { return symbol; }
}

class KeyWord implements Token {
  private String name;
  
  KeyWord(String n) { name = n; }
  public String name() { return name; }
  public String toString() { return name; }
}

class LeftParen implements Token {
  public String toString() { return "("; }
  private LeftParen() {}
  public static final LeftParen ONLY = new LeftParen();
}

class RightParen implements Token {
  public String toString() { return ")"; }
  private RightParen() {}
  public static final RightParen ONLY = new RightParen();
}

class LeftBrack implements Token {
  public String toString() { return "["; }
  private LeftBrack() {}
  public static final LeftBrack ONLY = new LeftBrack();
}

class RightBrack implements Token {
  public String toString() { return "]"; }
  private RightBrack() {}
  public static final RightBrack ONLY = new RightBrack();
}

///* Supports the addition of blocks to Jam. Uncommenting affects the comment for Token. */
//  class LeftBrace implements Token {
//  public String toString() { return "{"; }
//  private LeftBrace() {}
//  public static final LeftBrace ONLY = new LeftBrace();
//}
//
//class RightBrace implements Token {
//  public String toString() { return "}"; }
//  private RightBrace() {}
//  public static final RightBrace ONLY = new RightBrace();
//}

class Comma implements Token {
  public String toString() { return ","; }
  private Comma() {}
  public static final Comma ONLY = new Comma();
}

class SemiColon implements Token {
  public String toString() { return ";"; }
  private SemiColon() {}
  public static final SemiColon ONLY = new SemiColon();
}

class EndOfFile implements Token {
  public String toString() { return "*EOF*"; }
  private EndOfFile() {}
  public static final EndOfFile ONLY = new EndOfFile();
}


