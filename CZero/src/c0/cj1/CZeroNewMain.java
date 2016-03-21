package c0.cj1;
// Czero one-pass compiler from
// http://cs.au.dk/~mis/dOvs/Czero.java.
//
// Modified:
//
//   2014/02/18 by Tillmann Rendel (avoid unchecked conversions)
//   2014/03/14 by Jonathan Brachthäuser (added parseFile method and PrintWriter)

import java.io.*;
import java.util.*;
import lombok.Obj;

public interface CZeroNewMain {
    public static void main(String[] args) {
        String tFile = args[0];
        FileReader in;
        try {
            in = new FileReader(tFile);
            PrintWriter out;
            out = new PrintWriter(new FileWriter(tFile.substring(0,tFile.lastIndexOf('.'))+".j"));
            Constants.codeLibrary(out);
            Parser.of(tFile, in, out).parseProgram();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

interface BaseOpers extends MemberFields, Constants {
    default int nextChar() {
        try {
            c(in().read());
        } catch (Exception e) {
            c(-1);
        }
        // fix line and col counting
        // (\n and \r on Windows ...)
        if (c()=='\n') {
            tLine(tLine()+1);
            tCol(1);
        } else tCol(tCol()+1);
        return c();
    }

    default int nextToken() {
        switch (c()) {
            case '(': tKind(tLPAR());   nextChar(); break;
            case ')': tKind(tRPAR());   nextChar(); break;
            case ';': tKind(tSEMI());   nextChar(); break;
            case ',': tKind(tCOMMA());  nextChar(); break;
            case '+': tKind(tADD());    nextChar(); break;
            case '-': tKind(tSUB());    nextChar(); break;
            case '*': tKind(tMUL());    nextChar(); break;
            case '/': tKind(tDIV());    nextChar(); break;
            case '%': tKind(tMOD());    nextChar(); break;
            case '{': tKind(tLBRACK()); nextChar(); break;
            case '}': tKind(tRBRACK()); nextChar(); break;
            case '|': tKind(tOR());     nextChar(); break;
            case '&': tKind(tAND());    nextChar(); break;
            case '#': tKind(tSHARP());  nextChar(); break;
            case '.': tKind(tDOT());    nextChar(); break;
            case '\n':
            case '\r':
            case ' ': tKind(tWHITE());  nextChar(); break;
            case '\'': nextChar();
            if (c()=='\\') {
                nextChar();
                if (c()!='n') {
                    tKind(tERROR());
                    break; 
                }
                nextChar();
                if (c()!='\'') {
                    tKind(tERROR());
                    break; 
                }
                nextChar();
                tKind(tCONST());
                tIntValue(10);
                break;
            } 
            if (c()==-1 || c()=='\'') { 
                tKind(tERROR());
                break; 
            }
            tKind(tCONST());
            tIntValue(c());
            nextChar();
            if (c()!='\'') {
                tKind(tERROR());
                break;
            }
            nextChar();
            break;
            case '=': nextChar(); 
            if (c()=='=') { 
                nextChar();
                tKind(tEQ());
            } else {
                tKind(tASSIGN());
            }
            break;
            case '<': nextChar();
            if (c()=='=') {
                nextChar();
                tKind(tLEQ());
            } else {
                tKind(tLT());
            }
            break;
            case '>': nextChar();
            if (c()=='=') {
                nextChar();
                tKind(tGEQ());
            } else {
                tKind(tGT());
            }
            break;
            case '!': nextChar();
            if (c()=='=') {
                nextChar();
                tKind(tNE());
            } else {
                tKind(tNOT());
            }
            break;
            case -1: tKind(tEOF()); break;
            default: if (Constants.letter(c())) {
                tKind(tID());
                tIdValue("");
                while (Constants.alpha(c())) {
                    tIdValue(tIdValue()+(char)c());
                    nextChar();
                }
                if (tIdValue().equals("int")) tKind(tINT());
                else if (tIdValue().equals("if")) tKind(tIF());
                else if (tIdValue().equals("else")) tKind(tELSE());
                else if (tIdValue().equals("while")) tKind(tWHILE());
                else if (tIdValue().equals("getchar")) tKind(tGETCHAR());
                else if (tIdValue().equals("putchar")) tKind(tPUTCHAR());
                else if (tIdValue().equals("include")) tKind(tINCLUDE());
                else if (tIdValue().equals("return")) tKind(tRETURN());
                else if (tIdValue().equals("main")) tKind(tMAIN());
            } else if (Constants.digit(c())) {
                tKind(tCONST());
                tIntValue(0);
                while (Constants.digit(c())) {
                    tIntValue(10*tIntValue()+c()-'0');
                    nextChar();
                }
            } else {
                tKind(tERROR());
            }
        }
        return tKind();
    }

    default void parseError() {
        System.out.print("*** "+tFile()+":"+tLine()+":"+tCol()+": unexpected token: ");
        printToken();
        System.out.println();
        System.exit(1);
    }

    default void compileError(String s) {
        System.out.println("*** "+tFile()+":"+tLine()+":"+tCol()+": "+s);
        System.exit(1);
    }
    default void skip() {
        while (tKind()==tWHITE()) nextToken(); 
    }

    default void checkToken(int k) {
        if (tKind()!=k) parseError();
        nextToken();
    }

    default void skipToken(int k) {
        skip();
        checkToken(k);
    }

    default void checkToken(String s) {
        if (tKind()!=tID() || !tIdValue().equals(s)) parseError();
        nextToken();
    }

    default void printToken() {
           if (tKind() == tLPAR()) System.out.print("("); 
            else if (tKind() == tRPAR()) System.out.print(")"); 
            else if (tKind() == tASSIGN()) System.out.print("="); 
            else if (tKind() == tSEMI()) System.out.print(";"); 
            else if (tKind() == tCOMMA()) System.out.print(","); 
            else if (tKind() == tEQ()) System.out.print("=="); 
            else if (tKind() == tNE()) System.out.print("!="); 
            else if (tKind() == tID()) System.out.print(tIdValue()); 
            else if (tKind() == tCONST()) System.out.print(tIntValue()); 
            else if (tKind() == tCHAR()) System.out.print("char"); 
            else if (tKind() == tADD()) System.out.print("+"); 
            else if (tKind() == tSUB()) System.out.print("-"); 
            else if (tKind() == tMUL()) System.out.print("*"); 
            else if (tKind() == tDIV()) System.out.print("/"); 
            else if (tKind() == tMOD()) System.out.print("%"); 
            else if (tKind() == tLBRACK()) System.out.print("{"); 
            else if (tKind() == tRBRACK()) System.out.print("}"); 
            else if (tKind() == tOR()) System.out.print("|"); 
            else if (tKind() == tAND()) System.out.print("&"); 
            else if (tKind() == tNOT()) System.out.print("!"); 
            else if (tKind() == tLT()) System.out.print("<"); 
            else if (tKind() == tGT()) System.out.print(">"); 
            else if (tKind() == tLEQ()) System.out.print("<="); 
            else if (tKind() == tGEQ()) System.out.print(">="); 
            else if (tKind() == tSHARP()) System.out.print("#"); 
            else if (tKind() == tDOT()) System.out.print("."); 
            else if (tKind() == tINT()) System.out.print("int"); 
            else if (tKind() == tIF()) System.out.print("if"); 
            else if (tKind() == tELSE()) System.out.print("else"); 
            else if (tKind() == tWHILE()) System.out.print("while"); 
            else if (tKind() == tGETCHAR()) System.out.print("getchar"); 
            else if (tKind() == tPUTCHAR()) System.out.print("putchar"); 
            else if (tKind() == tINCLUDE()) System.out.print("include"); 
            else if (tKind() == tRETURN()) System.out.print("return"); 
            else if (tKind() == tMAIN()) System.out.print("main"); 
            else if (tKind() == tEOF()) System.out.print("eof"); 
            else if (tKind() == tERROR()) System.out.print("error"); 
            else if (tKind() == tWHITE()) System.out.print(" "); 
    }
}

interface Constants {
    default int tLPAR() { return 0; }
    default int tRPAR() { return 1; }
    default int tASSIGN() { return 2; }
    default int tSEMI() { return 3; }
    default int tCOMMA() { return 4; }
    default int tEQ() { return 5; }
    default int tNE() { return 6; }
    default int tID() { return 7; }
    default int tCONST() { return 8; }
    default int tCHAR() { return 9; }
    default int tADD() { return 10; }
    default int tSUB() { return 11; }
    default int tMUL() { return 12; }
    default int tDIV() { return 13; }
    default int tMOD() { return 14; }
    default int tLBRACK() { return 15; }
    default int tRBRACK() { return 16; }
    default int tOR() { return 17; }
    default int tAND() { return 18; }
    default int tNOT() { return 19; }
    default int tLT() { return 20; }
    default int tGT() { return 21; }
    default int tLEQ() { return 22; }
    default int tGEQ() { return 23; }
    default int tSHARP() { return 24; }
    default int tDOT() { return 25; }
    default int tINT() { return 26; }
    default int tIF() { return 27; }
    default int tELSE() { return 28; }
    default int tWHILE() { return 29; }
    default int tGETCHAR() { return 30; }
    default int tPUTCHAR() { return 31; }
    default int tINCLUDE() { return 32; }
    default int tRETURN() { return 33; }
    default int tMAIN() { return 34; }
    default int tEOF() { return 35; }
    default int tERROR() { return 36; }
    default int tWHITE() { return 37; }

    public static void codeLibrary(PrintWriter out) {
        code(".method mul_", out);
        code(".args 3", out);
        code(".locals 2", out);
        code("bipush 1", out);
        code("istore 4", out);
        code("bipush 0", out);
        code("iload 1", out);
        code("isub", out);
        code("iflt mul0", out);
        code("bipush 0", out);
        code("iload 1 ", out);
        code("isub", out);
        code("istore 1", out);
        code("bipush 0", out);
        code("istore 4", out);
        code("mul0:", out);
        code("bipush 0", out);
        code("istore 3", out);
        code("mul1:", out);
        code("iload 1", out);
        code("ifeq mul2", out);
        code("iload 1", out);
        code("bipush 1", out);
        code("isub", out);
        code("istore 1", out);
        code("iload 3", out);
        code("iload 2", out);
        code("iadd", out);
        code("istore 3", out);
        code("goto mul1", out);
        code("mul2:", out);
        code("bipush 1", out);
        code("iload 4", out);
        code("isub ", out);
        code("ifeq mul3", out);
        code("bipush 0", out);
        code("iload 3", out);
        code("isub", out);
        code("istore 3", out);
        code("mul3:", out);
        code("iload 3", out);
        code("ireturn", out);
        code("", out);
        code(".method div_", out);
        code(".args 3", out);
        code(".locals 2", out);
        code("bipush 1", out);
        code("istore 4", out);
        code("bipush 0", out);
        code("iload 1", out);
        code("isub", out);
        code("iflt div0", out);
        code("bipush 0", out);
        code("iload 1 ", out);
        code("isub", out);
        code("istore 1", out);
        code("bipush 0", out);
        code("istore 4", out);
        code("div0:", out);
        code("bipush 0", out);
        code("iload 2", out);
        code("isub", out);
        code("iflt div1", out);
        code("bipush 0", out);
        code("iload 2 ", out);
        code("isub", out);
        code("istore 2", out);
        code("iload 4", out);
        code("bipush 1", out);
        code("iadd", out);
        code("istore 4", out);
        code("div1:", out);
        code("bipush 0", out);
        code("istore 3", out);
        code("div2:", out);
        code("iload 1", out);
        code("iload 2", out);
        code("isub", out);
        code("iflt div3", out);
        code("iload 1", out);
        code("iload 2", out);
        code("isub", out);
        code("istore 1", out);
        code("iload 3", out);
        code("bipush 1", out);
        code("iadd", out);
        code("istore 3", out);
        code("goto div2", out);
        code("div3:", out);
        code("bipush 0", out);
        code("iload 4", out);
        code("isub ", out);
        code("iflt div4", out);
        code("bipush 0", out);
        code("iload 3", out);
        code("isub", out);
        code("istore 3", out);
        code("div4:", out);
        code("iload 3", out); 
        code("ireturn", out); 
        code("", out);
        code(".method mod_", out);
        code(".args 3", out);
        code(".locals 1", out);
        code("bipush 44", out);
        code("iload 1", out);
        code("iload 2", out);
        code("invokevirtual div_", out);
        code("istore 3", out);
        code("iload 1", out);
        code("bipush 44", out);
        code("iload 3", out);
        code("iload 2", out);
        code("invokevirtual mul_", out);
        code("isub", out);
        code("ireturn", out); 
        code("", out);
        code(".method not_", out);
        code(".args 2", out);
        code("iload 1", out);
        code("ifeq not1", out);
        code("bipush 0", out);
        code("goto not0", out);
        code("not1:", out);
        code("bipush 1", out);
        code("not0:", out);
        code("", out);
        code(".method eq_", out);
        code(".args 3", out);
        code("iload 1", out);
        code("iload 2", out);
        code("isub", out);
        code("ifeq eq1", out);
        code("bipush 0", out);
        code("ireturn", out);
        code("eq1:", out);
        code("bipush 1", out);
        code("ireturn", out);
        code("", out);
        code(".method ne_", out);
        code(".args 3", out);
        code("iload 1", out);
        code("iload 2", out);
        code("isub", out);
        code("ifeq ne0", out);
        code("bipush 1", out);
        code("ireturn", out);
        code("ne0:", out);
        code("bipush 0", out);
        code("ireturn", out);
        code("", out);
        code(".method lt_", out);
        code(".args 3", out);
        code("iload 1", out);
        code("iload 2", out);
        code("isub", out);
        code("iflt lt1", out);
        code("bipush 0", out);
        code("ireturn", out);
        code("lt1:", out);
        code("bipush 1", out);
        code("ireturn", out);
        code("", out);
        code(".method gt_", out);
        code(".args 3", out);
        code("iload 1", out);
        code("iload 2", out);
        code("isub", out);
        code("iflt gt0", out);
        code("bipush 1", out);
        code("ireturn", out);
        code("gt0:", out);
        code("bipush 0", out);
        code("ireturn", out);
        code("", out);
        code(".method leq_", out);
        code(".args 3", out);
        code("iload 1", out);
        code("iload 2", out);
        code("isub", out);
        code("dup", out);
        code("ifeq leqpop1", out);
        code("iflt leq1", out);
        code("bipush 0", out);
        code("ireturn", out);
        code("leq1:", out);
        code("bipush 1", out);
        code("ireturn", out);
        code("leqpop1:", out);
        code("pop", out);
        code("bipush 1", out);
        code("ireturn", out);
        code("", out);
        code(".method geq_", out);
        code(".args 3", out);
        code("iload 1", out);
        code("iload 2", out);
        code("isub", out);
        code("dup", out);
        code("ifeq geqpop1", out);
        code("iflt geq0", out);
        code("bipush 1", out);
        code("ireturn", out);
        code("geq0:", out);
        code("bipush 0", out);
        code("ireturn", out);
        code("geqpop1:", out);
        code("pop", out);
        code("bipush 1", out);
        code("ireturn", out);
        code("", out);
    }

    public static void code(String s, PrintWriter out) {
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
}

@Obj interface MemberFields {
    String tFile();//
    FileReader in();//
    PrintWriter out();//
    int tLine();
    void tLine(int tline);
    int tCol();
    void tCol(int tcol);
    int tKind();
    void tKind(int tkind);
    int c();
    void c(int value);
    int tIntValue();
    void tIntValue(int tintvalue);
    String tIdValue();
    void tIdValue(String tidvalue);
    int nextLable(); //static int nextLabel = 0;
    void nextLable(int nextlable);
}
interface ParseExpression extends MemberFields, BaseOpers {
    default void parseExpression(Map<String, Integer> vars, Map<String, Integer> funcs, Map<String, Integer> prototypes) {
        parseExp3(vars,funcs,prototypes);
    }

    default void parseExp0(Map<String, Integer> vars, Map<String, Integer> funcs, Map<String, Integer> prototypes) {
        if (tKind()==tCONST()) {
            Constants.code("ldc_w "+tIntValue(), out());
            nextToken();
        } else if (tKind()==tGETCHAR()) {
            nextToken();
            skipToken(tLPAR());
            skipToken(tRPAR());
            Constants.code("bipush 44", out());
            Constants.code("invokevirtual getchar", out());
        } else if (tKind()==tLPAR()) {
            nextToken();
            parseExpression(vars,funcs,prototypes);
            skipToken(tRPAR());
        } else if (tKind()==tID()) {
            String name = tIdValue();
            nextToken();
            skip();
            if (tKind()==tLPAR()) {
                nextToken();
                skip();
                Constants.code("bipush 44", out());
                int args = parseActuals(vars,funcs,prototypes);
                Constants.code("invokevirtual "+name, out());
                skipToken(tRPAR());
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
                Constants.code("iload "+vars.get(name), out());
            }
        } else if (tKind()==tSUB()) {
            nextToken();
            skip();
            Constants.code("bipush 0", out());
            parseExp0(vars,funcs,prototypes);
            Constants.code("isub", out());
        } else if (tKind()==tNOT()) {
            nextToken();
            skip();
            Constants.code("bipush 44", out());
            parseExp0(vars,funcs,prototypes);
            Constants.code("invokevirtual not_", out());
        } else parseError();
    }

    default void parseExp1(Map<String, Integer> vars, Map<String, Integer> funcs, Map<String, Integer> prototypes) {
        parseExp0(vars,funcs,prototypes);
        skip();
        while (tKind()==tMUL() || tKind()==tDIV() || tKind()==tAND() || tKind()==tMOD()) {
            int op = tKind();
            nextToken();
            skip();
            if (op==tMUL() || op==tDIV() || op==tMOD()) {
                Constants.code("bipush 44", out());
                Constants.code("swap", out());
            }
            parseExp0(vars,funcs,prototypes);
            if (op == tMUL()) Constants.code("invokevirtual mul_", out());
            else if (op == tDIV()) Constants.code("invokevirtual div_", out());
            else if (op == tAND()) Constants.code("iand", out());
            else if (op == tMOD()) Constants.code("invokevirtual mod_", out());
        }
    }

    default void parseExp2(Map<String, Integer> vars, Map<String, Integer> funcs, Map<String, Integer> prototypes) {
        parseExp1(vars,funcs,prototypes);
        skip();
        while (tKind()==tADD() || tKind()==tSUB() || tKind()==tOR()) {
            int op = tKind();
            nextToken();
            skip();
            parseExp1(vars,funcs,prototypes);
            if (op == tADD()) Constants.code("iadd", out());
            else if (op == tSUB()) Constants.code("isub", out());
            else if (op == tOR()) Constants.code("ior", out());
        }
    }

    default void parseExp3(Map<String, Integer> vars, Map<String, Integer> funcs, Map<String, Integer> prototypes) {
        parseExp2(vars,funcs,prototypes);
        skip();
        while (tKind()==tEQ() || tKind()==tNE() || tKind()==tLT() || tKind()==tGT() || tKind()==tLEQ() || tKind()==tGEQ()) {
            int op = tKind();
            nextToken();
            skip();
            Constants.code("bipush 44", out());
            Constants.code("swap", out());
            parseExp2(vars,funcs,prototypes);
            if (op == tEQ()) Constants.code("invokevirtual eq_", out());
            else if (op == tNE()) Constants.code("invokevirtual ne_", out());
            else if (op == tLT()) Constants.code("invokevirtual lt_", out());
            else if (op == tGT()) Constants.code("invokevirtual gt_", out());
            else if (op == tLEQ()) Constants.code("invokevirtual leq_", out());
            else if (op == tGEQ()) Constants.code("invokevirtual geq_", out());
        }
    }
    
    default int parseActuals(Map<String, Integer> vars, Map<String, Integer> funcs, Map<String, Integer> prototypes) {
        boolean more = false;
        int args = 0;
        skip();
        while (tKind()!=tRPAR()) {
            parseExpression(vars,funcs,prototypes);
            args++;
            skip();
            if (tKind()==tCOMMA()) {
                nextToken();
                skip();
                more = true;
            } else
                more = false;
        }
        if (more) parseError();
        return args;
    }
}
interface ParseFunctions extends MemberFields, BaseOpers, ParseStatements {
    default void parseFunctions(Map<String, Integer> funcs, Map<String, Integer> prototypes) {
        skip();
        while (tKind()==tINT()) {
            parseFunction(funcs,prototypes);
            skip();
        }
    }

    default void parseFunction(Map<String, Integer> funcs, Map<String, Integer> prototypes) {
        Map<String, Integer> vars = new HashMap<String, Integer>();
        String name;
        checkToken(tINT());
        skipToken(tID());
        name = tIdValue();
        skipToken(tLPAR());
        int args = parseFormals(vars);
        skipToken(tRPAR());
        skip();
        if (tKind()==tSEMI()) {
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
            Constants.code(".method "+name, out());
            Constants.code(".args "+(args+1), out());
            parseBody(args,vars,funcs,prototypes);
            Constants.code("bipush 0", out());
            Constants.code("ireturn", out());
        }
    }

    default int parseFormals(Map<String, Integer> vars) {
        int offset = 0;
        boolean more = false;
        skip();
        while (tKind()==tINT()) {
            nextToken();
            skipToken(tID());
            offset++;
            vars.put(tIdValue(),offset);
            skip();
            if (tKind()==tCOMMA()) {
                nextToken();
                skip();
                more = true;
            } else
                more = false;
        }
        if (more) parseError();
        return offset;
    }

    default void parseBody(int offset, Map<String, Integer> vars, Map<String, Integer> funcs, Map<String, Integer> prototypes) {
        skipToken(tLBRACK());
        parseDeclarations(offset,0,vars);
        parseStatements(vars,funcs,prototypes); 
        skipToken(tRBRACK());
    }

    default void parseDeclarations(int offset, int locals, Map<String, Integer> vars) {
        int initValue = 0;
        boolean initialized = false;
        skip();
        if (tKind()==tINT()) {
            nextToken();
            skipToken(tID());
            offset++;
            vars.put(tIdValue(),offset);
            skip();
            if (tKind()==tASSIGN()) {
                nextToken();
                skipToken(tCONST());
                initialized = true;
                initValue = tIntValue();
            }
            checkToken(tSEMI());
            parseDeclarations(offset,locals+1,vars);
        } else {
            if (locals>0)
                Constants.code(".locals "+locals, out());
        }
        if (initialized) {
            Constants.code("bipush "+initValue, out());
            Constants.code("istore "+offset, out());
        }
    }
}
interface ParseMain extends MemberFields, BaseOpers, ParseFunctions {
    default void parseMain(Map<String, Integer> funcs, Map<String, Integer> prototypes) {
        Map<String, Integer> vars = new HashMap<String, Integer>();
        skipToken(tMAIN());
        skipToken(tLPAR());
        skipToken(tRPAR());
        Constants.code(".method main", out());
        Constants.code(".args 1", out());
        parseBody(0,vars,funcs,prototypes);
        Constants.code("bipush 0", out());
        Constants.code("ireturn", out());
        skipToken(tEOF());
    }
}
@Obj interface Parser extends Constants, MemberFields, BaseOpers, ParseExpression, ParseStatements, ParseFunctions, ParseMain {      
    public static Parser of(String tfile, FileReader in, PrintWriter out) {
        return of(0, in, 0, out, 0, tfile, "", 0, 0, 0);
    }

    default void parseProgram() {
        nextChar(); //=======YANLIN: begin added
        nextToken();//=======YANLIN: end added
        Map<String, Integer> funcs = new HashMap<String, Integer>();
        Map<String, Integer> prototypes = new HashMap<String, Integer>();
        skipToken(tSHARP());
        checkToken(tINCLUDE());
        skipToken(tLT());
        checkToken("stdio");
        checkToken(tDOT());
        checkToken("h");
        checkToken(tGT());
        parseFunctions(funcs,prototypes);
        parseMain(funcs,prototypes);
        Iterator<String> i = prototypes.keySet().iterator();
        while (i.hasNext()) {
            String s = i.next();
            if (!funcs.containsKey(s))
                compileError("missing implementation of "+s);
        }
    }
}
interface ParseStatements extends MemberFields, BaseOpers, ParseExpression {
default void parseStatements(Map<String, Integer> vars, Map<String, Integer> funcs, Map<String, Integer> prototypes) {
        skip();
        while (tKind()!=tRBRACK()) {
            parseStatement(vars,funcs,prototypes);
            skip();
        }
    }

    default void parseStatement(Map<String, Integer> vars, Map<String, Integer> funcs, Map<String, Integer> prototypes) {
        if (tKind()==tID()) {
            String name = tIdValue();
            nextToken();
            skipToken(tASSIGN());
            if (!vars.containsKey(name))
                compileError("undeclared variable: "+name);
            skip();
            parseExpression(vars,funcs,prototypes);
            skipToken(tSEMI());
            Constants.code("istore "+vars.get(name), out());
        } else if (tKind()==tRETURN()) {
            nextToken();
            skip();
            parseExpression(vars,funcs,prototypes);
            skipToken(tSEMI());
            Constants.code("ireturn", out());
        } else if (tKind()==tIF()) {
            int elselabel = nextLable(); nextLable(nextLable()+1);
            int donelabel = nextLable(); nextLable(nextLable()+1);
            nextToken();
            skipToken(tLPAR());
            skip();
            parseExpression(vars,funcs,prototypes);
            skipToken(tRPAR());
            Constants.code("ifeq L"+elselabel, out());
            skipToken(tLBRACK());
            parseStatements(vars,funcs,prototypes);
            skipToken(tRBRACK());
            Constants.code("goto L"+donelabel, out());
            Constants.code("L"+elselabel+":", out());
            skip();
            if (tKind()==tELSE()) {
                nextToken();
                skipToken(tLBRACK());
                skip();
                parseStatements(vars,funcs,prototypes);
                skipToken(tRBRACK());
            }
            Constants.code("L"+donelabel+":", out());
        } else if (tKind()==tWHILE()) {
            int looplabel = nextLable(); nextLable(nextLable()+1);
            int donelabel = nextLable(); nextLable(nextLable()+1);
            Constants.code("L"+looplabel+":", out());
            nextToken();
            skipToken(tLPAR());
            skip();
            parseExpression(vars,funcs,prototypes);
            skipToken(tRPAR());
            Constants.code("ifeq L"+donelabel, out());
            skipToken(tLBRACK());
            skip();
            parseStatements(vars,funcs,prototypes); 
            skipToken(tRBRACK());
            Constants.code("goto L"+looplabel, out());
            Constants.code("L"+donelabel+":", out());
        } else if (tKind()==tPUTCHAR()) {
            nextToken();
            skipToken(tLPAR());
            Constants.code("bipush 44", out());
            skip();
            parseExpression(vars,funcs,prototypes);
            Constants.code("invokevirtual putchar", out());
            skipToken(tRPAR());
            skipToken(tSEMI());
        } else parseError();
    }
}
