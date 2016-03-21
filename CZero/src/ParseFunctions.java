

import java.util.HashMap;
import java.util.Map;

public interface ParseFunctions extends MemberFields, BaseOpers, ParseStatements {
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
