

import java.util.Map;

public interface ParseStatements extends MemberFields, BaseOpers, ParseExpression {
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
