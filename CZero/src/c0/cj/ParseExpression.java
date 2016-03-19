package c0.cj;

import java.util.Map;

public interface ParseExpression extends MemberFields, BaseOpers {
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
