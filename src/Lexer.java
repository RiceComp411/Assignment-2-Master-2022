/** Jam lexer class for all assignments.              
  * Given a Lexer object, the next token in that input stream being processed by the Lexer is returned by the
  * readToken() method; it throws a ParseException (a form of RuntimeException) if it encounters a syntax error.  
  * Calling readToken() advances the cursor in the input stream to the next token.
  * 
  * The method peek() in the Lexer class has the same behavior as readToken() except for the fact that it does not
  * advance the cursor.
  */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.HashMap;

class Lexer extends StreamTokenizer {
  
  /* short names for StreamTokenizer codes */
  
  public static final int WORD = StreamTokenizer.TT_WORD; 
  public static final int NUMBER = StreamTokenizer.TT_NUMBER; 
  public static final int EOF = StreamTokenizer.TT_EOF; 
  public static final int EOL = StreamTokenizer.TT_EOL; 
  
  /* operator Tokens */
  
  // <unop>  ::= <sign> | ~   | ! 
  // <binop> ::= <sign> | "*" | / | = | != | < | > | <= | >= | & | "|" | <- 
  // <sign>  ::= "+" | -
  
  //  Note: there is no class distinction between <unop> and <binop> at 
  //  lexical level because of ambiguity; <sign> belongs to both
  
  public static final OpToken PLUS = OpToken.newBothOpToken(UnOpPlus.ONLY, BinOpPlus.ONLY); 
  public static final OpToken MINUS = OpToken.newBothOpToken(UnOpMinus.ONLY, BinOpMinus.ONLY);
  public static final OpToken TIMES = OpToken.newBinOpToken(OpTimes.ONLY);
  public static final OpToken DIVIDE = OpToken.newBinOpToken(OpDivide.ONLY);
  public static final OpToken EQUALS = OpToken.newBinOpToken(OpEquals.ONLY);
  public static final OpToken NOT_EQUALS = OpToken.newBinOpToken(OpNotEquals.ONLY);
  public static final OpToken LESS_THAN = OpToken.newBinOpToken(OpLessThan.ONLY);
  public static final OpToken GREATER_THAN = OpToken.newBinOpToken(OpGreaterThan.ONLY);
  public static final OpToken LESS_THAN_EQUALS = OpToken.newBinOpToken(OpLessThanEquals.ONLY);
  public static final OpToken GREATER_THAN_EQUALS = OpToken.newBinOpToken(OpGreaterThanEquals.ONLY);
  public static final OpToken NOT = OpToken.newUnOpToken(OpTilde.ONLY);
  public static final OpToken AND = OpToken.newBinOpToken(OpAnd.ONLY);
  public static final OpToken OR = OpToken.newBinOpToken(OpOr.ONLY);
  
  /* Used to support reference cells. */
//  public static final OpToken BANG = OpToken.newUnOpToken(OpBang.ONLY);
//  public static final OpToken GETS = OpToken.newBinOpToken(OpGets.ONLY);
//  public static final OpToken REF = OpToken.newUnOpToken(OpRef.ONLY);
  
  /* Keywords */

  public static final KeyWord IF     = new KeyWord("if");
  public static final KeyWord THEN   = new KeyWord("then");
  public static final KeyWord ELSE   = new KeyWord("else");
  public static final KeyWord LET    = new KeyWord("let");
//  public static final KeyWord LETREC = new KeyWord("letrec");   // Used to support letrec extension
  public static final KeyWord IN     = new KeyWord("in");
  public static final KeyWord MAP    = new KeyWord("map");
  public static final KeyWord TO     = new KeyWord("to");
  public static final KeyWord BIND   = new KeyWord(":=");
  
  // wordtable for classifying words in token stream
  public HashMap<String,Token>  wordTable = new HashMap<String,Token>();

  /** The buffer holding the next token in the intput stream; it is used to support the peek() operation, which cannot
    * be implemented using StreamTokenizer pushBack because some Tokens are composed of two StreamTokenizer tokens. */
  Token buffer;
 
  /* constructors */

  /** Constructs a Lexer for the specified inputStream */
  Lexer(Reader inputStream) {
    super(new BufferedReader(inputStream));
    initLexer();
  }

  /** Constructs a Lexer for the contents of the specified file */
  Lexer(String fileName) throws IOException { this(new FileReader(fileName)); }

  /** Constructs a Lexer for the default console input stream System.in */  
  Lexer() {
    super(new BufferedReader(new InputStreamReader(System.in)));
    initLexer();
  }

  /* Initializes lexer tables and the StreamTokenizer that the lexer extends */
  private void initLexer() {

    // configure StreamTokenizer portion of this
    resetSyntax();
    parseNumbers();
    ordinaryChar('-');
    slashSlashComments(true);
    wordChars('0', '9');
    wordChars('a', 'z');
    wordChars('A', 'Z');
    wordChars('_', '_');
    wordChars('?', '?');
    whitespaceChars(0, ' '); 

    // `+' `-' `*' `/' `~' `=' `<' `>' `&' `|' `:' `;' `,' '!'
    // `(' `)' `[' `]' are ordinary characters (self-delimiting)

    initWordTable();
    buffer = null;  // buffer initially empty
  }

  /** Reads tokens until next end-of-line */
  public void flush() throws IOException {
    eolIsSignificant(true);
    while (nextToken() != EOL) ; // eat tokens until EOL
    eolIsSignificant(false);
  }

  /** Returns the next token in the input stream without consuming it */
  public Token peek() { 
    if (buffer == null) buffer = readToken();
    return buffer;
  }
    
  /** Reads the next token as defined by StreamTokenizer in the input stream (consuming it). */
  private int getToken() {
    // synonymous with nextToken() except for throwing an unchecked 
    // ParseException instead of a checked IOException
    try {
      int tokenType = nextToken();
      return tokenType;
    } catch(IOException e) {
      throw new ParseException("IOException " + e + "thrown by nextToken()");
    }
  }

  /** Reads the next Token in the input stream (consuming it) */
  public Token readToken() {
    
    /* Uses getToken() to read next token and constructs the Token object representing that token.
     * NOTE: token representations for all Token classes except IntConstant are unique; a HashMap 
     * is used to avoid duplication.  Hence, == can safely be used to compare all Tokens except 
     * IntConstants for equality (assuming that code does not gratuitously create Tokens).  When the
     * stream is reduced to EOT, returns null instead of a Token.
     */
    
    if (buffer != null) {
      Token token = buffer;
      buffer = null;          // clear buffer
      return token;
    }
    
    int tokenType = getToken();
    
    switch (tokenType) {
      
      case NUMBER:
        int value = (int) nval;
        if (nval == (double) value) return new IntConstant(value);
        throw new ParseException("The number " + nval + " is not a 32 bit integer");
      case WORD:
        Token regToken = wordTable.get(sval);
        if (regToken == null) {
          // must be new variable name
          Variable newVar = new Variable(sval);
          wordTable.put(sval, newVar);
          return newVar;
        }
        return regToken;
        
      case EOF: return null;
      case '(': return LeftParen.ONLY;
      case ')': return RightParen.ONLY;
      case '[': return LeftBrack.ONLY;
      case ']': return RightBrack.ONLY;
      // case '{': return LeftBrace.ONLY;
      // case '}': return RightBrace.ONLY;
      case ',': return Comma.ONLY;
      case ';': return SemiColon.ONLY;
      
      case '+': return PLUS;  
      case '-': return MINUS;  
      case '*': return TIMES;  
      case '/': return DIVIDE;  
      case '~': return NOT;  
      case '=': return EQUALS;
      
      case '<': 
        tokenType = getToken();
        if (tokenType == '=') return LESS_THAN_EQUALS;  
//      if (tokenType == '-') return GETS;    // Used to support reference cells
        pushBack();
        return LESS_THAN; 
        
      case '>': 
        tokenType = getToken();
        if (tokenType == '=') return GREATER_THAN_EQUALS;  
        pushBack();
        return GREATER_THAN;
        
      case '!': 
        tokenType = getToken();
        if (tokenType == '=') return NOT_EQUALS;  
        else throw new ParseException("!" + ((char) tokenType) + " is not a legal token"); 
        
        /* this alternate else clause supports reference cells */
//        pushBack();
//        return BANG;  
     
      case '&': return AND;  
      case '|': return OR;  
      case ':': {
        tokenType = getToken();
        if (tokenType == '=') return BIND;   // ":=" is a keyword
        pushBack();
        throw new ParseException("':' is not a legal token");
      }
      default:  
        throw new 
        ParseException("'" + ((char) tokenType) + "' is not a legal token");
    }
  }
    
  /** Initializes the table of Strings used to recognize Tokens */
  private void initWordTable() {
    // initialize wordTable
    
    // constants
    // <empty>  ::= empty
    // <bool>  ::= true | false
    
    wordTable.put("empty", EmptyConstant.ONLY);
    wordTable.put("true",  BoolConstant.TRUE);
    wordTable.put("false", BoolConstant.FALSE); 
    
    // Install primitive functions
    // <prim>  ::= number? | function? | list? | empty? | cons? | ref? | arity | cons | first | rest 
    // Note: ref? is added in Assignment 4
    
    wordTable.put("number?",   NumberPPrim.ONLY);
    wordTable.put("function?", FunctionPPrim.ONLY);
    wordTable.put("list?",     ListPPrim.ONLY);
    wordTable.put("empty?",    EmptyPPrim.ONLY);
    wordTable.put("cons?",     ConsPPrim.ONLY);
//    wordTable.put("ref?",      RefPPrim.ONLY);  // used to support reference cells
    wordTable.put("arity",     ArityPrim.ONLY);  
    wordTable.put("cons",      ConsPrim.ONLY);
    wordTable.put("first",     FirstPrim.ONLY);
    wordTable.put("rest",      RestPrim.ONLY);
    
    
    // keywords: if then else let in map to := 
    wordTable.put("if",   Lexer.IF);
    wordTable.put("then", Lexer.THEN);
    wordTable.put("else", Lexer.ELSE);
    wordTable.put("let",  Lexer.LET);
    wordTable.put("in",   Lexer.IN);
    wordTable.put("map",  Lexer.MAP);
    wordTable.put("to",   Lexer.TO);    
  }
}