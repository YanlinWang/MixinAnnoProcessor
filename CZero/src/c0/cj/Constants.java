package c0.cj;

import java.io.PrintWriter;

public interface Constants {
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
