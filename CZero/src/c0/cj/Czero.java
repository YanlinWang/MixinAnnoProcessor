package c0.cj;
// Czero one-pass compiler from
// http://cs.au.dk/~mis/dOvs/Czero.java.
//
// Modified:
//
//   2014/02/18 by Tillmann Rendel (avoid unchecked conversions)
//   2014/03/14 by Jonathan Brachthäuser (added parseFile method and PrintWriter)



import java.io.*;
import java.util.*;;

public class Czero implements Constants{
  static String tFile;
  static int tLine;
  static int tCol;
  static int tIntValue;
  static String tIdValue;
  static int tKind;

  static FileReader in;
  static int c;

  static int nextChar() {
    try {
      c = in.read();
    } catch (Exception e) {
      c = -1;
    }
    // TODO fix line and col counting
    // (\n and \r on Windows ...)
    if (c=='\n') {
      tLine++;
      tCol = 1;
    } else tCol++;
    return c;
  }

  static PrintWriter out;
  
  static void code(String s) {
    try {
      out.write(s+"\n");
    } catch(Exception e) { e.printStackTrace(); }
  }

  static boolean letter(int c) {
    return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z');
  }

  static boolean digit(int c) {
    return ('0' <= c && c <= '9');
  }

  static boolean alpha(int c) {
    return letter(c) || digit(c);
  }

  static int nextToken() {
    switch (c) {
      case '(': tKind = tLPAR;   nextChar(); break;
      case ')': tKind = tRPAR;   nextChar(); break;
      case ';': tKind = tSEMI;   nextChar(); break;
      case ',': tKind = tCOMMA;  nextChar(); break;
      case '+': tKind = tADD;    nextChar(); break;
      case '-': tKind = tSUB;    nextChar(); break;
      case '*': tKind = tMUL;    nextChar(); break;
      case '/': tKind = tDIV;    nextChar(); break;
      case '%': tKind = tMOD;    nextChar(); break;
      case '{': tKind = tLBRACK; nextChar(); break;
      case '}': tKind = tRBRACK; nextChar(); break;
      case '|': tKind = tOR;     nextChar(); break;
      case '&': tKind = tAND;    nextChar(); break;
      case '#': tKind = tSHARP;  nextChar(); break;
      case '.': tKind = tDOT;    nextChar(); break;
      case '\n':
      case '\r':
      case ' ': tKind = tWHITE;  nextChar(); break;
      case '\'': nextChar();
                if (c=='\\') {
                  nextChar();
                  if (c!='n') {
                    tKind = tERROR; 
                    break; 
                  }
                  nextChar();
                  if (c!='\'') {
                    tKind = tERROR; 
                    break; 
                  }
                  nextChar();
                  tKind = tCONST;
                  tIntValue = 10;
                  break;
                } 
                if (c==-1 || c=='\'') { 
                  tKind = tERROR; 
                  break; 
                }
                tKind = tCONST;
                tIntValue = c;
                nextChar();
                if (c!='\'') {
                  tKind = tERROR;
                  break;
                }
                nextChar();
                break;
      case '=': nextChar(); 
                if (c=='=') { 
                  nextChar();
                  tKind = tEQ; 
                } else {
                  tKind = tASSIGN;
                }
                break;
      case '<': nextChar();
                if (c=='=') {
                  nextChar();
                  tKind = tLEQ;
                } else {
                  tKind = tLT;
                }
                break;
      case '>': nextChar();
                if (c=='=') {
                  nextChar();
                  tKind = tGEQ;
                } else {
                  tKind = tGT;
                }
                break;
      case '!': nextChar();
                if (c=='=') {
                  nextChar();
                  tKind = tNE;
                } else {
                  tKind = tNOT;
                }
                break;
      case -1: tKind = tEOF; break;
      default: if (letter(c)) {
                 tKind = tID;
                 tIdValue = "";
                 while (alpha(c)) {
                   tIdValue = tIdValue+(char)c;
                   nextChar();
                 }
                 if (tIdValue.equals("int")) tKind = tINT;
                 else if (tIdValue.equals("if")) tKind = tIF;
                 else if (tIdValue.equals("else")) tKind = tELSE;
                 else if (tIdValue.equals("while")) tKind = tWHILE;
                 else if (tIdValue.equals("getchar")) tKind = tGETCHAR;
                 else if (tIdValue.equals("putchar")) tKind = tPUTCHAR;
                 else if (tIdValue.equals("include")) tKind = tINCLUDE;
                 else if (tIdValue.equals("return")) tKind = tRETURN;
                 else if (tIdValue.equals("main")) tKind = tMAIN;
               } else if (digit(c)) {
                 tKind = tCONST;
                 tIntValue = 0;
                 while (digit(c)) {
                   tIntValue = 10*tIntValue+c-'0';
                   nextChar();
                 }
               } else {
                 tKind = tERROR;
               }
    }
    return tKind;
  }

  static void parseError() {
    System.out.print("*** "+tFile+":"+tLine+":"+tCol+": unexpected token: ");
    printToken();
    System.out.println();
    System.exit(1);
  }

  static void compileError(String s) {
    System.out.println("*** "+tFile+":"+tLine+":"+tCol+": "+s);
    System.exit(1);
  }

  static void skip() {
    while (tKind==tWHITE) nextToken(); 
  }

  static void checkToken(int k) {
    if (tKind!=k) parseError();
    nextToken();
  }

  static void skipToken(int k) {
    skip();
    checkToken(k);
  }

  static void checkToken(String s) {
    if (tKind!=tID || !tIdValue.equals(s)) parseError();
    nextToken();
  }

  static void printToken() {
    switch (tKind) {
      case tLPAR: System.out.print("("); break;
      case tRPAR: System.out.print(")"); break;
      case tASSIGN: System.out.print("="); break;
      case tSEMI: System.out.print(";"); break;
      case tCOMMA: System.out.print(","); break;
      case tEQ: System.out.print("=="); break;
      case tNE: System.out.print("!="); break;
      case tID: System.out.print(tIdValue); break;
      case tCONST: System.out.print(tIntValue); break;
      case tCHAR: System.out.print("char"); break;
      case tADD: System.out.print("+"); break;
      case tSUB: System.out.print("-"); break;
      case tMUL: System.out.print("*"); break;
      case tDIV: System.out.print("/"); break;
      case tMOD: System.out.print("%"); break;
      case tLBRACK: System.out.print("{"); break;
      case tRBRACK: System.out.print("}"); break;
      case tOR: System.out.print("|"); break;
      case tAND: System.out.print("&"); break;
      case tNOT: System.out.print("!"); break;
      case tLT: System.out.print("<"); break;
      case tGT: System.out.print(">"); break;
      case tLEQ: System.out.print("<="); break;
      case tGEQ: System.out.print(">="); break;
      case tSHARP: System.out.print("#"); break;
      case tDOT: System.out.print("."); break;
      case tINT: System.out.print("int"); break;
      case tIF: System.out.print("if"); break;
      case tELSE: System.out.print("else"); break;
      case tWHILE: System.out.print("while"); break;
      case tGETCHAR: System.out.print("getchar"); break;
      case tPUTCHAR: System.out.print("putchar"); break;
      case tINCLUDE: System.out.print("include"); break;
      case tRETURN: System.out.print("return"); break;
      case tMAIN: System.out.print("main"); break;
      case tEOF: System.out.print("eof"); break;
      case tERROR: System.out.print("error"); break;
      case tWHITE: System.out.print(" "); break;
    }
  }

  static int nextLabel = 0;

  static void parseProgram() {
    Map<String, Integer> funcs = new HashMap<String, Integer>();
    Map<String, Integer> prototypes = new HashMap<String, Integer>();
    skipToken(tSHARP);
    checkToken(tINCLUDE);
    skipToken(tLT);
    checkToken("stdio");
    checkToken(tDOT);
    checkToken("h");
    checkToken(tGT);
    parseFunctions(funcs,prototypes);
    parseMain(funcs,prototypes);
    Iterator<String> i = prototypes.keySet().iterator();
    while (i.hasNext()) {
      String s = i.next();
      if (!funcs.containsKey(s))
        compileError("missing implementation of "+s);
    }
  }

  static void parseMain(Map<String, Integer> funcs, Map<String, Integer> prototypes) {
    Map<String, Integer> vars = new HashMap<String, Integer>();
    skipToken(tMAIN);
    skipToken(tLPAR);
    skipToken(tRPAR);
    code(".method main");
    code(".args 1");
    parseBody(0,vars,funcs,prototypes);
    code("bipush 0");
    code("ireturn");
    skipToken(tEOF);
  }

  static void parseFunctions(Map<String, Integer> funcs, Map<String, Integer> prototypes) {
    skip();
    while (tKind==tINT) {
      parseFunction(funcs,prototypes);
      skip();
    }
  }

  static void parseFunction(Map<String, Integer> funcs, Map<String, Integer> prototypes) {
    Map<String, Integer> vars = new HashMap<String, Integer>();
    String name;
    checkToken(tINT);
    skipToken(tID);
    name = tIdValue;
    skipToken(tLPAR);
    int args = parseFormals(vars);
    skipToken(tRPAR);
    skip();
    if (tKind==tSEMI) {
      nextToken();
      if (prototypes.containsKey(name)) 
        compileError("duplicate declaration of "+name);
      if (funcs.containsKey(name) && args!=funcs.get(name))
        compileError("conflicting declaration of "+name);
      prototypes.put(name,args);
    } else {
      if (funcs.containsKey(name))
        compileError("duplicate implementation of "+name);
      if (prototypes.containsKey(name) && args!=prototypes.get(name))
        compileError("conflicting implementation of "+name);
      funcs.put(name,args);
      code(".method "+name);
      code(".args "+(args+1));
      parseBody(args,vars,funcs,prototypes);
      code("bipush 0");
      code("ireturn");
    }
  }

  static int parseFormals(Map<String, Integer> vars) {
    int offset = 0;
    boolean more = false;
    skip();
    while (tKind==tINT) {
      nextToken();
      skipToken(tID);
      offset++;
      vars.put(tIdValue,offset);
      skip();
      if (tKind==tCOMMA) {
        nextToken();
        skip();
        more = true;
      } else
        more = false;
    }
    if (more) parseError();
    return offset;
  }

  static void parseBody(int offset, Map<String, Integer> vars, Map<String, Integer> funcs, Map<String, Integer> prototypes) {
    skipToken(tLBRACK);
    parseDeclarations(offset,0,vars);
    parseStatements(vars,funcs,prototypes); 
    skipToken(tRBRACK);
  }

  static void parseDeclarations(int offset, int locals, Map<String, Integer> vars) {
    int initValue = 0;
    boolean initialized = false;
    skip();
    if (tKind==tINT) {
      nextToken();
      skipToken(tID);
      offset++;
      vars.put(tIdValue,offset);
      skip();
      if (tKind==tASSIGN) {
        nextToken();
        skipToken(tCONST);
        initialized = true;
        initValue = tIntValue;
      }
      checkToken(tSEMI);
      parseDeclarations(offset,locals+1,vars);
    } else {
      if (locals>0)
        code(".locals "+locals);
    }
    if (initialized) {
      code("bipush "+initValue);
      code("istore "+offset);
    }
  }

  static void parseStatements(Map<String, Integer> vars, Map<String, Integer> funcs, Map<String, Integer> prototypes) {
    skip();
    while (tKind!=tRBRACK) {
      parseStatement(vars,funcs,prototypes);
      skip();
    }
  }

  static void parseStatement(Map<String, Integer> vars, Map<String, Integer> funcs, Map<String, Integer> prototypes) {
    if (tKind==tID) {
      String name = tIdValue;
      nextToken();
      skipToken(tASSIGN);
      if (!vars.containsKey(name))
        compileError("undeclared variable: "+name);
      skip();
      parseExpression(vars,funcs,prototypes);
      skipToken(tSEMI);
      code("istore "+vars.get(name));
    } else if (tKind==tRETURN) {
      nextToken();
      skip();
      parseExpression(vars,funcs,prototypes);
      skipToken(tSEMI);
      code("ireturn");
    } else if (tKind==tIF) {
      int elselabel = nextLabel++;
      int donelabel = nextLabel++;
      nextToken();
      skipToken(tLPAR);
      skip();
      parseExpression(vars,funcs,prototypes);
      skipToken(tRPAR);
      code("ifeq L"+elselabel);
      skipToken(tLBRACK);
      parseStatements(vars,funcs,prototypes);
      skipToken(tRBRACK);
      code("goto L"+donelabel);
      code("L"+elselabel+":");
      skip();
      if (tKind==tELSE) {
        nextToken();
        skipToken(tLBRACK);
        skip();
        parseStatements(vars,funcs,prototypes);
        skipToken(tRBRACK);
      }
      code("L"+donelabel+":");
    } else if (tKind==tWHILE) {
      int looplabel = nextLabel++;
      int donelabel = nextLabel++;
      code("L"+looplabel+":");
      nextToken();
      skipToken(tLPAR);
      skip();
      parseExpression(vars,funcs,prototypes);
      skipToken(tRPAR);
      code("ifeq L"+donelabel);
      skipToken(tLBRACK);
      skip();
      parseStatements(vars,funcs,prototypes); 
      skipToken(tRBRACK);
      code("goto L"+looplabel);
      code("L"+donelabel+":");
    } else if (tKind==tPUTCHAR) {
      nextToken();
      skipToken(tLPAR);
      code("bipush 44");
      skip();
      parseExpression(vars,funcs,prototypes);
      code("invokevirtual putchar");
      skipToken(tRPAR);
      skipToken(tSEMI);
    } else parseError();
  }

  static void parseExpression(Map<String, Integer> vars, Map<String, Integer> funcs, Map<String, Integer> prototypes) {
    parseExp3(vars,funcs,prototypes);
  }

  static void parseExp0(Map<String, Integer> vars, Map<String, Integer> funcs, Map<String, Integer> prototypes) {
    if (tKind==tCONST) {
      code("ldc_w "+tIntValue);
      nextToken();
    } else if (tKind==tGETCHAR) {
      nextToken();
      skipToken(tLPAR);
      skipToken(tRPAR);
      code("bipush 44");
      code("invokevirtual getchar");
    } else if (tKind==tLPAR) {
      nextToken();
      parseExpression(vars,funcs,prototypes);
      skipToken(tRPAR);
    } else if (tKind==tID) {
      String name = tIdValue;
      nextToken();
      skip();
      if (tKind==tLPAR) {
        nextToken();
        skip();
        code("bipush 44");
        int args = parseActuals(vars,funcs,prototypes);
        code("invokevirtual "+name);
        skipToken(tRPAR);
        if (funcs.containsKey(name)) {
          if (args!=funcs.get(name))
            compileError("incorrect number of arguments: "+name);
        } else if (prototypes.containsKey(name)) {
          if (args!=prototypes.get(name))
            compileError("incorrect number of arguments: "+name);
        } else {
          compileError("undeclared function: "+name);
        }
      } else {
        if (!vars.containsKey(name))
          compileError("undeclared variable: "+name);
        code("iload "+vars.get(name));
      }
    } else if (tKind==tSUB) {
      nextToken();
      skip();
      code("bipush 0");
      parseExp0(vars,funcs,prototypes);
      code("isub");
    } else if (tKind==tNOT) {
      nextToken();
      skip();
      code("bipush 44");
      parseExp0(vars,funcs,prototypes);
      code("invokevirtual not_");
    } else parseError();
  }

  static void parseExp1(Map<String, Integer> vars, Map<String, Integer> funcs, Map<String, Integer> prototypes) {
    parseExp0(vars,funcs,prototypes);
    skip();
    while (tKind==tMUL || tKind==tDIV || tKind==tAND || tKind==tMOD) {
      int op = tKind;
      nextToken();
      skip();
      if (op==tMUL || op==tDIV || op==tMOD) {
        code("bipush 44");
        code("swap");
      }
      parseExp0(vars,funcs,prototypes);
      switch (op) {
        case tMUL: code("invokevirtual mul_"); break;
        case tDIV: code("invokevirtual div_"); break;
        case tAND: code("iand"); break;
        case tMOD: code("invokevirtual mod_"); break;
      }
    }
  }

  static void parseExp2(Map<String, Integer> vars, Map<String, Integer> funcs, Map<String, Integer> prototypes) {
    parseExp1(vars,funcs,prototypes);
    skip();
    while (tKind==tADD || tKind==tSUB || tKind==tOR) {
      int op = tKind;
      nextToken();
      skip();
      parseExp1(vars,funcs,prototypes);
      switch (op) {
        case tADD: code("iadd"); break;
        case tSUB: code("isub"); break;
        case tOR:  code("ior"); break;
      }
    }
  }

  static void parseExp3(Map<String, Integer> vars, Map<String, Integer> funcs, Map<String, Integer> prototypes) {
    parseExp2(vars,funcs,prototypes);
    skip();
    while (tKind==tEQ || tKind==tNE || tKind==tLT || tKind==tGT || tKind==tLEQ || tKind==tGEQ) {
      int op = tKind;
      nextToken();
      skip();
      code("bipush 44");
      code("swap");
      parseExp2(vars,funcs,prototypes);
      switch (op) {
        case tEQ:  code("invokevirtual eq_"); break;
        case tNE:  code("invokevirtual ne_"); break;
        case tLT:  code("invokevirtual lt_"); break;
        case tGT:  code("invokevirtual gt_"); break;
        case tLEQ: code("invokevirtual leq_"); break;
        case tGEQ: code("invokevirtual geq_"); break;
      }
    }
  }

  static int parseActuals(Map<String, Integer> vars, Map<String, Integer> funcs, Map<String, Integer> prototypes) {
    boolean more = false;
    int args = 0;
    skip();
    while (tKind!=tRPAR) {
      parseExpression(vars,funcs,prototypes);
      args++;
      skip();
      if (tKind==tCOMMA) {
        nextToken();
        skip();
        more = true;
      } else
        more = false;
    }
    if (more) parseError();
    return args;
  }

  public static void main(String[] args) {
    try {
      tFile = args[0];
      in = new FileReader(tFile);
      out = new PrintWriter(new FileWriter(tFile.substring(0,tFile.lastIndexOf('.'))+".j"));
      tLine = 1;
      tCol = 0;
      Constants.codeLibrary(out);
      nextChar();
      nextToken();
      parseProgram();
      out.close();
    } catch (Exception e) { e.printStackTrace(); }
  }
}
