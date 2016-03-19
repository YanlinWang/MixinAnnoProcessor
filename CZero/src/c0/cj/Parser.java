package c0.cj;

import java.io.*;
import java.util.*;

import lombok.Obj;

//@Obj
public interface Parser extends Constants, MemberFields {      
    public static Parser of(String tfile, FileReader in, PrintWriter out) {
        return of(0, in, 0, out, 0, tfile, "", 0, 0, 0);
    }
    
    public static Parser of(int c, FileReader in, int nextlabel, PrintWriter out, int tcol, String tfile, String tidvalue, int tintvaue, int tkind, int tline) {
        return new Parser(){
            String _tFile = tfile;
            FileReader _in = in;
            PrintWriter _out = out;
            int _tLine;
            int _tCol;
            int _tKind;
            int _c;
            int _tIntValue;
            String _tIdValue;
            int _nextLable;
            public String tFile() { return _tFile; }
            public FileReader in() { return _in; }
            public PrintWriter out() { return _out; }
            public int tLine() { return _tLine; }
            public void tLine(int tline) { this._tLine = tline; }
            public int tCol() { return _tCol; }
            public void tCol(int tcol) { this._tCol = tcol; }
            public int tKind() { return _tKind; }
            public void tKind(int tkind) { this._tKind = tkind; }
            public int c() { return _c; }
            public void c(int value) { this._c = value; }
            public int tIntValue() { return _tIntValue; }
            public void tIntValue(int tintvalue) { this._tIntValue = tintvalue; }
            public String tIdValue() { return _tIdValue; }
            public void tIdValue(String tidvalue) { this._tIdValue = tidvalue; }
            public int nextLable() { return _nextLable; }
            public void nextLable(int nextlable) { this._nextLable = nextlable; }
       };
    }

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
            case '(': tKind(tLPAR);   nextChar(); break;
            case ')': tKind(tRPAR);   nextChar(); break;
            case ';': tKind(tSEMI);   nextChar(); break;
            case ',': tKind(tCOMMA);  nextChar(); break;
            case '+': tKind(tADD);    nextChar(); break;
            case '-': tKind(tSUB);    nextChar(); break;
            case '*': tKind(tMUL);    nextChar(); break;
            case '/': tKind(tDIV);    nextChar(); break;
            case '%': tKind(tMOD);    nextChar(); break;
            case '{': tKind(tLBRACK); nextChar(); break;
            case '}': tKind(tRBRACK); nextChar(); break;
            case '|': tKind(tOR);     nextChar(); break;
            case '&': tKind(tAND);    nextChar(); break;
            case '#': tKind(tSHARP);  nextChar(); break;
            case '.': tKind(tDOT);    nextChar(); break;
            case '\n':
            case '\r':
            case ' ': tKind(tWHITE);  nextChar(); break;
            case '\'': nextChar();
            if (c()=='\\') {
                nextChar();
                if (c()!='n') {
                    tKind(tERROR);
                    break; 
                }
                nextChar();
                if (c()!='\'') {
                    tKind(tERROR);
                    break; 
                }
                nextChar();
                tKind(tCONST);
                tIntValue(10);
                break;
            } 
            if (c()==-1 || c()=='\'') { 
                tKind(tERROR);
                break; 
            }
            tKind(tCONST);
            tIntValue(c());
            nextChar();
            if (c()!='\'') {
                tKind(tERROR);
                break;
            }
            nextChar();
            break;
            case '=': nextChar(); 
            if (c()=='=') { 
                nextChar();
                tKind(tEQ);
            } else {
                tKind(tASSIGN);
            }
            break;
            case '<': nextChar();
            if (c()=='=') {
                nextChar();
                tKind(tLEQ);
            } else {
                tKind(tLT);
            }
            break;
            case '>': nextChar();
            if (c()=='=') {
                nextChar();
                tKind(tGEQ);
            } else {
                tKind(tGT);
            }
            break;
            case '!': nextChar();
            if (c()=='=') {
                nextChar();
                tKind(tNE);
            } else {
                tKind(tNOT);
            }
            break;
            case -1: tKind(tEOF); break;
            default: if (Constants.letter(c())) {
                tKind(tID);
                tIdValue("");
                while (Constants.alpha(c())) {
                    tIdValue(tIdValue()+(char)c());
                    nextChar();
                }
                if (tIdValue().equals("int")) tKind(tINT);
                else if (tIdValue().equals("if")) tKind(tIF);
                else if (tIdValue().equals("else")) tKind(tELSE);
                else if (tIdValue().equals("while")) tKind(tWHILE);
                else if (tIdValue().equals("getchar")) tKind(tGETCHAR);
                else if (tIdValue().equals("putchar")) tKind(tPUTCHAR);
                else if (tIdValue().equals("include")) tKind(tINCLUDE);
                else if (tIdValue().equals("return")) tKind(tRETURN);
                else if (tIdValue().equals("main")) tKind(tMAIN);
            } else if (Constants.digit(c())) {
                tKind(tCONST);
                tIntValue(0);
                while (Constants.digit(c())) {
                    tIntValue(10*tIntValue()+c()-'0');
                    nextChar();
                }
            } else {
                tKind(tERROR);
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
        while (tKind()==tWHITE) nextToken(); 
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
        if (tKind()!=tID || !tIdValue().equals(s)) parseError();
        nextToken();
    }

    default void printToken() {
        switch (tKind()) {
            case tLPAR: System.out.print("("); break;
            case tRPAR: System.out.print(")"); break;
            case tASSIGN: System.out.print("="); break;
            case tSEMI: System.out.print(";"); break;
            case tCOMMA: System.out.print(","); break;
            case tEQ: System.out.print("=="); break;
            case tNE: System.out.print("!="); break;
            case tID: System.out.print(tIdValue()); break;
            case tCONST: System.out.print(tIntValue()); break;
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

    default void parseProgram() {
        //=======YANLIN: begin added
        tLine(1);
        tCol(0);
        nextChar();
        nextToken();
        nextLable(0);
        //=======YANLIN: end added
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

    default void parseMain(Map<String, Integer> funcs, Map<String, Integer> prototypes) {
        Map<String, Integer> vars = new HashMap<String, Integer>();
        skipToken(tMAIN);
        skipToken(tLPAR);
        skipToken(tRPAR);
        Constants.code(".method main", out());
        Constants.code(".args 1", out());
        parseBody(0,vars,funcs,prototypes);
        Constants.code("bipush 0", out());
        Constants.code("ireturn", out());
        skipToken(tEOF);
    }

    default void parseFunctions(Map<String, Integer> funcs, Map<String, Integer> prototypes) {
        skip();
        while (tKind()==tINT) {
            parseFunction(funcs,prototypes);
            skip();
        }
    }

    default void parseFunction(Map<String, Integer> funcs, Map<String, Integer> prototypes) {
        Map<String, Integer> vars = new HashMap<String, Integer>();
        String name;
        checkToken(tINT);
        skipToken(tID);
        name = tIdValue();
        skipToken(tLPAR);
        int args = parseFormals(vars);
        skipToken(tRPAR);
        skip();
        if (tKind()==tSEMI) {
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
        while (tKind()==tINT) {
            nextToken();
            skipToken(tID);
            offset++;
            vars.put(tIdValue(),offset);
            skip();
            if (tKind()==tCOMMA) {
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
        skipToken(tLBRACK);
        parseDeclarations(offset,0,vars);
        parseStatements(vars,funcs,prototypes); 
        skipToken(tRBRACK);
    }

    default void parseDeclarations(int offset, int locals, Map<String, Integer> vars) {
        int initValue = 0;
        boolean initialized = false;
        skip();
        if (tKind()==tINT) {
            nextToken();
            skipToken(tID);
            offset++;
            vars.put(tIdValue(),offset);
            skip();
            if (tKind()==tASSIGN) {
                nextToken();
                skipToken(tCONST);
                initialized = true;
                initValue = tIntValue();
            }
            checkToken(tSEMI);
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

    default void parseStatements(Map<String, Integer> vars, Map<String, Integer> funcs, Map<String, Integer> prototypes) {
        skip();
        while (tKind()!=tRBRACK) {
            parseStatement(vars,funcs,prototypes);
            skip();
        }
    }

    default void parseStatement(Map<String, Integer> vars, Map<String, Integer> funcs, Map<String, Integer> prototypes) {
        if (tKind()==tID) {
            String name = tIdValue();
            nextToken();
            skipToken(tASSIGN);
            if (!vars.containsKey(name))
                compileError("undeclared variable: "+name);
            skip();
            parseExpression(vars,funcs,prototypes);
            skipToken(tSEMI);
            Constants.code("istore "+vars.get(name), out());
        } else if (tKind()==tRETURN) {
            nextToken();
            skip();
            parseExpression(vars,funcs,prototypes);
            skipToken(tSEMI);
            Constants.code("ireturn", out());
        } else if (tKind()==tIF) {
            int elselabel = nextLable(); nextLable(nextLable()+1);
            int donelabel = nextLable(); nextLable(nextLable()+1);
            nextToken();
            skipToken(tLPAR);
            skip();
            parseExpression(vars,funcs,prototypes);
            skipToken(tRPAR);
            Constants.code("ifeq L"+elselabel, out());
            skipToken(tLBRACK);
            parseStatements(vars,funcs,prototypes);
            skipToken(tRBRACK);
            Constants.code("goto L"+donelabel, out());
            Constants.code("L"+elselabel+":", out());
            skip();
            if (tKind()==tELSE) {
                nextToken();
                skipToken(tLBRACK);
                skip();
                parseStatements(vars,funcs,prototypes);
                skipToken(tRBRACK);
            }
            Constants.code("L"+donelabel+":", out());
        } else if (tKind()==tWHILE) {
            int looplabel = nextLable(); nextLable(nextLable()+1);
            int donelabel = nextLable(); nextLable(nextLable()+1);
            Constants.code("L"+looplabel+":", out());
            nextToken();
            skipToken(tLPAR);
            skip();
            parseExpression(vars,funcs,prototypes);
            skipToken(tRPAR);
            Constants.code("ifeq L"+donelabel, out());
            skipToken(tLBRACK);
            skip();
            parseStatements(vars,funcs,prototypes); 
            skipToken(tRBRACK);
            Constants.code("goto L"+looplabel, out());
            Constants.code("L"+donelabel+":", out());
        } else if (tKind()==tPUTCHAR) {
            nextToken();
            skipToken(tLPAR);
            Constants.code("bipush 44", out());
            skip();
            parseExpression(vars,funcs,prototypes);
            Constants.code("invokevirtual putchar", out());
            skipToken(tRPAR);
            skipToken(tSEMI);
        } else parseError();
    }

    default void parseExpression(Map<String, Integer> vars, Map<String, Integer> funcs, Map<String, Integer> prototypes) {
        parseExp3(vars,funcs,prototypes);
    }

    default void parseExp0(Map<String, Integer> vars, Map<String, Integer> funcs, Map<String, Integer> prototypes) {
        if (tKind()==tCONST) {
            Constants.code("ldc_w "+tIntValue(), out());
            nextToken();
        } else if (tKind()==tGETCHAR) {
            nextToken();
            skipToken(tLPAR);
            skipToken(tRPAR);
            Constants.code("bipush 44", out());
            Constants.code("invokevirtual getchar", out());
        } else if (tKind()==tLPAR) {
            nextToken();
            parseExpression(vars,funcs,prototypes);
            skipToken(tRPAR);
        } else if (tKind()==tID) {
            String name = tIdValue();
            nextToken();
            skip();
            if (tKind()==tLPAR) {
                nextToken();
                skip();
                Constants.code("bipush 44", out());
                int args = parseActuals(vars,funcs,prototypes);
                Constants.code("invokevirtual "+name, out());
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
                Constants.code("iload "+vars.get(name), out());
            }
        } else if (tKind()==tSUB) {
            nextToken();
            skip();
            Constants.code("bipush 0", out());
            parseExp0(vars,funcs,prototypes);
            Constants.code("isub", out());
        } else if (tKind()==tNOT) {
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
        while (tKind()==tMUL || tKind()==tDIV || tKind()==tAND || tKind()==tMOD) {
            int op = tKind();
            nextToken();
            skip();
            if (op==tMUL || op==tDIV || op==tMOD) {
                Constants.code("bipush 44", out());
                Constants.code("swap", out());
            }
            parseExp0(vars,funcs,prototypes);
            switch (op) {
                case tMUL: Constants.code("invokevirtual mul_", out()); break;
                case tDIV: Constants.code("invokevirtual div_", out()); break;
                case tAND: Constants.code("iand", out()); break;
                case tMOD: Constants.code("invokevirtual mod_", out()); break;
            }
        }
    }

    default void parseExp2(Map<String, Integer> vars, Map<String, Integer> funcs, Map<String, Integer> prototypes) {
        parseExp1(vars,funcs,prototypes);
        skip();
        while (tKind()==tADD || tKind()==tSUB || tKind()==tOR) {
            int op = tKind();
            nextToken();
            skip();
            parseExp1(vars,funcs,prototypes);
            switch (op) {
                case tADD: Constants.code("iadd", out()); break;
                case tSUB: Constants.code("isub", out()); break;
                case tOR:  Constants.code("ior", out()); break;
            }
        }
    }

    default void parseExp3(Map<String, Integer> vars, Map<String, Integer> funcs, Map<String, Integer> prototypes) {
        parseExp2(vars,funcs,prototypes);
        skip();
        while (tKind()==tEQ || tKind()==tNE || tKind()==tLT || tKind()==tGT || tKind()==tLEQ || tKind()==tGEQ) {
            int op = tKind();
            nextToken();
            skip();
            Constants.code("bipush 44", out());
            Constants.code("swap", out());
            parseExp2(vars,funcs,prototypes);
            switch (op) {
                case tEQ:  Constants.code("invokevirtual eq_", out()); break;
                case tNE:  Constants.code("invokevirtual ne_", out()); break;
                case tLT:  Constants.code("invokevirtual lt_", out()); break;
                case tGT:  Constants.code("invokevirtual gt_", out()); break;
                case tLEQ: Constants.code("invokevirtual leq_", out()); break;
                case tGEQ: Constants.code("invokevirtual geq_", out()); break;
            }
        }
    }

    default int parseActuals(Map<String, Integer> vars, Map<String, Integer> funcs, Map<String, Integer> prototypes) {
        boolean more = false;
        int args = 0;
        skip();
        while (tKind()!=tRPAR) {
            parseExpression(vars,funcs,prototypes);
            args++;
            skip();
            if (tKind()==tCOMMA) {
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
