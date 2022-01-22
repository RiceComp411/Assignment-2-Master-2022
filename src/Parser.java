/** Parser for Assignment 2 */

import java.io.*;
import java.util.*;

/** Exception class for representing parsing errors. */
class ParseException extends RuntimeException {
  ParseException(String s) { super(s); }
}

/** A parser class for Jam.  Each program requires a separate parser object. */
class Parser {
  
  private Lexer in;
  
  Parser(Lexer i) {
    in = i;
  }
  
  Parser(Reader inputStream) { this(new Lexer(inputStream)); }
  
  Parser(String fileName) throws IOException { this(new FileReader(fileName)); }
  
  Lexer lexer() { return in; }
  

  /** Parses a Jam program which is simply an expression (Exp) */
  public AST parse() throws ParseException {
    /* your code goes here */
  }
  
  /** Parses:
    *   <exp> :: = if <exp> then <exp> else <exp>
    *            | let <prop-def-list> in <exp>
    *            | map <id-list> to <exp>
    *            | <term> { <biop> <term> }*  // (left associatively!)
    */
  private AST parseExp() {
    
    /* your code goes here */
 
  }
  
  /* Many more parse methods follow.  I suggest defining separate parse methods for parsing
     Term, If, Map, and Let as shown in the syntax diagrams.  You will need additional
     methods for select contructions.  Let the syntax diagrams and your personal program
     design taste be your guide.  Avoid repeating code patterns. */
}

