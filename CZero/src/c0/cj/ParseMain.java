package c0.cj;

import java.util.HashMap;
import java.util.Map;

public interface ParseMain extends MemberFields, BaseOpers, ParseFunctions {
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
