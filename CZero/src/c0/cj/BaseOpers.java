package c0.cj;

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
}
