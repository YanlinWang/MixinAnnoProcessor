

import java.io.*;

public interface CZeroNewMain {
    public static void main(String[] args) {
        String tFile = args[0];
        FileReader in;
        try {
            in = new FileReader(tFile);
            PrintWriter out;
            out = new PrintWriter(new FileWriter(tFile.substring(0,tFile.lastIndexOf('.'))+".j"));
            Constants.codeLibrary(out);
            Parser.of(tFile, in, out).parseProgram();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
