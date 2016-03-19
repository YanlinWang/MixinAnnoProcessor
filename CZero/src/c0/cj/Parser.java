package c0.cj;

import java.io.*;
import java.util.*;

import lombok.Obj;

//@Obj
public interface Parser extends Constants, MemberFields, BaseOpers, ParseExpression, ParseStatements, ParseFunctions, ParseMain {      
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

    default void parseProgram() {
        //=======YANLIN: begin added
        nextChar();
        nextToken();
        //=======YANLIN: end added
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
