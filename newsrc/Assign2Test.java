import java.util.StringTokenizer;
import junit.framework.TestCase;
import java.io.*;

public class Assign2Test extends TestCase {

  public Assign2Test (String name) {
    super(name);
  }
  
  /** The following 3 check methods create an interpreter object with the
    * specified String as the program, invoke the respective evaluation
    * method (callByValue, callByName, callByNeed), and check that the 
    * result matches the (given) expected output.  If the test fails,
    * the method prints a report as to which test failed and how many
    * points should be deducted.
    */
 
  private void valueCheck(String name, String answer, String program) {
    Interpreter interp = new Interpreter(new StringReader(program));
    assertEquals("by-value " + name, answer, interp.callByValue().toString());
  }

  private void nameCheck(String name, String answer, String program) {
    Interpreter interp = new Interpreter(new StringReader(program));
    assertEquals("by-name " + name, answer, interp.callByName().toString());
  }
   
  private void needCheck(String name, String answer, String program) {
    Interpreter interp = new Interpreter(new StringReader(program));
    assertEquals("by-need " + name, answer, interp.callByNeed().toString());
  }

  private void allCheck(String name, String answer, String program) {
    valueCheck(name, answer, program);
    nameCheck(name, answer, program);
    needCheck(name, answer, program);
  } 

  public void testNumberP() {
    try {
      String output = "number?";
      String input = "number?";
      allCheck("numberP", output, input );

    } catch (Exception e) {
      e.printStackTrace();
      fail("numberP threw " + e);
    }
  } //end of func
  
  public void testMathOp() {
    try {
      String output = "18";
      String input = "2 * 3 + 12";
      allCheck("mathOp", output, input );

    } catch (Exception e) {
      e.printStackTrace();
      fail("mathOp threw " + e);
    }
  } //end of func
  
  public void testParseException() {
    try {
      String output = "haha";
      String input = " 1 +";
      allCheck("parseException", output, input );

         fail("parseException did not throw ParseException exception");
      } catch (ParseException e) {   
         //e.printStackTrace();
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("parseException threw " + e);
    }
  } //end of func
  

  public void testEvalException() {
    try {
      String output = "mojo";
      String input = "1 + number?";
      allCheck("evalException", output, input );

         fail("evalException did not throw EvalException exception");
      } catch (EvalException e) {   
         //e.printStackTrace();
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("evalException threw " + e);
    }
  } //end of func
  

  public void testAppend() {
    try {
      String output = "(1 2 3 1 2 3)";
      String input = 
        "let Y      := map f to " +
        "                let g := map x to f(map z1,z2 to (x(x))(z1,z2)); " +
        "                in g(g); " +
        "    APPEND := map ap to " +
        "                map x,y to " +
        "                  if x = empty then y else cons(first(x), ap(rest(x), y)); " +
        "    l      := cons(1,cons(2,cons(3,empty))); " + 
        "in (Y(APPEND))(l,l)";
      allCheck("append", output, input );

    } catch (Exception e) {
      e.printStackTrace();
      fail("append threw " + e);
    }
  } //end of func
  
  public void testFib() {
    try {
      String output
        = "((0 1) (1 1) (2 2) (3 3) (4 5) (5 8) (6 13) (7 21) (8 34) (9 55) (10 89))";
      String input =
        "let      Y := map f to let g := map x to f(x(x)); in g(g); " +
        "      pair := map x,y to cons(x, cons(y, empty));" +
        "   FIBHELP := map fibhelp to map k,fn,fnm1 to if k = 0 then fn else fibhelp(k - 1, fn + fnm1, fn); " +
        "in let FFIB := map ffib to map n to if n = 0 then 1 else (Y(FIBHELP))(n - 1,1,1); " +
        "   in let FIBS := map fibs to map k,l to " +
        "                    let fibk := (Y(FFIB))(k);" +
        "                    in if k >= 0 then fibs(k - 1, cons(pair(k,fibk), l)) else l; " +
        "      in (Y(FIBS))(10, empty)";
      needCheck("fib-need", output, input);
      nameCheck("fib-name", output, input);
    }
    catch(Exception e) {
      e.printStackTrace();
      fail("[3.00] fib threw " + e);
    }
  } //end of func
  
  public void testYappend() {
    try {
      String output = "(1 2 3 1 2 3)";
      String input = "let    Y := map f to let g := map x to f(map z1,z2 to (x(x))(z1,z2)); in g(g);" +
                     "  APPEND := map ap to map x,y to if empty?(x) then y else cons(first(x), ap(rest(x), y));" +
                     "       l := cons(1,cons(2,cons(3,empty)));" +
                     "in (Y(APPEND))(l,l) ";
      allCheck("[3.00] yappend", output, input );

    } catch (Exception e) {
      e.printStackTrace();
      fail("[3.00] yappend threw " + e);
    }
  } //end of func
  
  
  public void testValueYFactorial() {
    try {
      String output = "720";
      String input = "let  Y := map f to let g := map x to f(map z to (x(x))(z)); in g(g);" +
        "  FACT := map f to map n to if n = 0 then 1 else n * f(n - 1);" +
        "in (Y(FACT))(6)";
      valueCheck("[3.00] valueYFactorial", output, input );
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("[3.00] valueYFactorial threw " + e);
    }
  } //end of func
  
  public void testNameYFactorial() {
    try {
      String output = "720";
      String input = "let   Y := map f to let g := map x to f(x(x)); in g(g);" +
        "   FACT := map f to map n to if n = 0 then 1 else n * f(n - 1); in (Y(FACT))(6)";
      nameCheck("[1.50] nameYFactorial", output, input );
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("[1.50] nameYFactorial threw " + e);
    }
  } //end of func
  

  public void testNeedYFactorial() {
    try {
      String output = "720";
      String input = "let   Y := map f to let g := map x to f(x(x)); in g(g); " +
                     "   FACT := map f to map n to if n = 0 then 1 else n * f(n - 1); in (Y(FACT))(6)";
      needCheck("[1.50] needYFactorial", output, input );

    } catch (Exception e) {
      e.printStackTrace();
      fail("[1.50] needYFactorial threw " + e);
    }
  } //end of func
}
