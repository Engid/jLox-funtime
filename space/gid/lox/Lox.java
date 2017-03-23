// My personal package name. Differs from the original package name.
package space.gid.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
  
  static boolean hadError = false;

  public static void main(String[] args) throws IOException {
    if (args.length > 1) {
      System.out.println("Usage: jlox [script]");
    } else if (args.length == 1) {
      runFile(args[0]);
    } else {
      runPrompt();
    }
  }


  //Interpretes from a file of Lox code. Runs when file path is passed to Lox
  private static void runFile(String path) throws IOException {
    byte[] bytes = Files.readAllBytes(Paths.get(path));
    run(new String(bytes, Charset.defaultCharset()));
    
    if (hadError) System.exit(65);
  }


  //Handles REPL for CMD line. Runs when Lox is started without arguments
  private static void runPrompt() throws IOException {
    InputStreamReader input = new InputStreamReader(System.in);
    BufferedReader reader = new BufferedReader(input);

    //Infinite loop can be excaped with Control-C
    for(;;){
      System.out.print("> ");
      run(reader.readLine());
      hadError = false;
    }
  }

  //Creates a list of tokens and does stuff with them
  private static void run(String source) {
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();

    // For now simply print the tokens.
    for (Token token : tokens) {
      System.out.println(token);
    }
  }

  static void error(int line, String message) {
    report(line, "", message);
  }

  /* 
    This would be nice to have for the error messages:

    Error: Unexpected "," in argument list.

    15 | function(first, second,);
                               ^-- Here.

    Unfortunately, its not implemented by Nystrom, so I may have to 
    come back and write it myself if I get time. -Nick
  */
  static private void report(int line, String where, String message) {
    System.err.println(
        "[line " + line + "] Error" + where + ": " + message);
    hadError = true;
  }



}//End of Lox class  




