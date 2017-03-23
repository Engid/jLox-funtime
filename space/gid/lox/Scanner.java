package space.gid.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// Note from the author: 
// "I know static imports are considered bad style by some, but they save me
// from having to sprinkle `TokenType.` all over the scanner and parser. 
// Forgive me, but every character counts in a book." -Bob Nystrom
import static space.gid.lox.TokenType.*;

class Scanner {
  private final String source;
  private final List<Token> tokens = new ArrayList<>();
  
  private static final Map<String, TokenType> keywords;

  static {
    keywords = new HashMap<>();
    keywords.put("and",     AND);
    keywords.put("class",   CLASS);
    keywords.put("else",    ELSE);
    keywords.put("false",   FALSE);
    keywords.put("for",     FOR);
    keywords.put("fun",     FUN);
    keywords.put("if",      IF);
    keywords.put("nil",     NIL);
    keywords.put("or",      OR);
    keywords.put("print",   PRINT);
    keywords.put("return",  RETURN);
    keywords.put("super",   SUPER);
    keywords.put("this",    THIS);
    keywords.put("true",    TRUE);
    keywords.put("var",     VAR);
    keywords.put("while",   WHILE);
  }


  private int start = 0;
  private int current = 0;
  private int line = 1;

  Scanner(String source) {
    this.source = source;
  }

  List<Token> scanTokens() {
    while (!isAtEnd()) {
      // We are at the beginning of the next lexeme.
      start = current;
      scanToken();
    }

    tokens.add(new Token(EOF, "", null, line));
    return tokens;
  }

  private void scanToken(){
    char c = advance();
    switch(c){
      case '(': addToken(LEFT_PAREN); break;
      case ')': addToken(RIGHT_PAREN); break;
      case '{': addToken(LEFT_BRACE); break;
      case '}': addToken(RIGHT_BRACE); break;
      case ',': addToken(COMMA); break;
      case '.': addToken(DOT); break;
      case '-': addToken(MINUS); break;
      case '+': addToken(PLUS); break;
      case ';': addToken(SEMICOLON); break;
      case '*': addToken(STAR); break;
      
      case '!': addToken(match('=') ? BANG_EQUAL : BANG); break;
      case '=': addToken(match('=') ? EQUAL_EQUAL : EQUAL); break;
      case '<': addToken(match('=') ? LESS_EQUAL : LESS); break;
      case '>': addToken(match('=') ? GREATER_EQUAL : GREATER); break;
      
      case '/':
        if (match('/')) {
          // A comment goes until the end of the line.
          while (peek() != '\n' && !isAtEnd()) advance();
        } else {
          addToken(SLASH);
        }
        break;

      case ' ':
      case '\r':
      case '\t':
        // Ignore whitespace.
        break;

      case '\n':
        line++;
        break;
        
      case '"': string(); break;

      default:

        if (isDigit(c)) {
          number();
        } else if (isAlpha(c)) {
          identifier();
        } else {
          Lox.error(line, "Unexpected character.");
        }
        /*
          Note from the author: 
          The code reports each invalid character separately, so this shotguns 
          the user with a blast of errors if they accidentally paste a big blob
          of weird text. Coalescing a run of invalid characters into a single 
          error would give a nicer user experience.  
        */
        break;
    }
  } // End of scanToken()

  // scanToken delegates to identifier() for user-defined names
  private void identifier() {
    while (isAlphaNumeric(peek())) advance();

    // See if the identifier is a reserved word.
    String text = source.substring(start, current);

    TokenType type = keywords.get(text);
    if (type == null) type = IDENTIFIER;
    addToken(type);
  }

  // scanToken delegates to number() to grab number literals
  private void number() {
    while (isDigit(peek())) advance();

    // Look for a frational part.
    if (peek() == '.' && isDigit(peekNext())) {
      // Consume the '.'
      advance();

      while (isDigit(peek())) advance();
    }

    addToken(NUMBER,
        Double.parseDouble(source.substring(start, current)));
  }

  // scanToken delegates to string() to grab string literals
  private void string() {
    while (peek() != '"' && !isAtEnd()) {
      // Note: this allows for multi-line strings in Lox.
      if (peek() == '\n') line++;
      advance();
    }

    // Unterminated string.
    if (isAtEnd()) {
      Lox.error(line, "Unterminated string.");
      return;
    }

    // The closing ".
    advance();

    // Trim the surrounding quotes. If Lox supported escapes sequences like 
    // the \n char, we'd unescape those here.
    String value = source.substring(start + 1, current - 1);
    addToken(STRING, value);
  
  } // End of string()


  //******HELPER FUNCTIONS******//

  private boolean match(char expected) {
    if (isAtEnd()) return false;
    if (source.charAt(current) != expected) return false;
    
    current++;
    return true;
  }

  private char peek() {
    if (current >= source.length()) return '\0';
    return source.charAt(current);
  }

  private char peekNext() {
    if (current + 1 >= source.length()) return '\0';
    return source.charAt(current + 1);
  }

  private boolean isAlpha(char c) {
    return (c >= 'a' && c <= 'z') ||
           (c >= 'A' && c <= 'Z') ||
            c == '_'; 
  }

  private boolean isAlphaNumeric(char c) {
    return isAlpha(c) || isDigit(c);
  }

  private boolean isDigit(char c) {
    return c >= '0' && c <= '9';
  }

  private boolean isAtEnd() {
    return current >= source.length();
  }

  private char advance() {
    current++;
    return source.charAt(current - 1);
  }

  private void addToken(TokenType type) {
    addToken(type, null);
  }

  private void addToken(TokenType type, Object literal) {
    String text = source.substring(start, current);
    tokens.add(new Token(type, text, literal, line));
  }

} // End of Scanner class












