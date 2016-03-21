

public interface BaseOpers extends MemberFields, Constants {
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
