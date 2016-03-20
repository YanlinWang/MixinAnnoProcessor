package c0.cj;

import java.io.FileReader;
import java.io.PrintWriter;

import lombok.Obj;

@Obj public interface MemberFields {

    //need initialize with constructor parameters
    String tFile();
    FileReader in();
    PrintWriter out();
    //set default initialization value inside the constructor
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
