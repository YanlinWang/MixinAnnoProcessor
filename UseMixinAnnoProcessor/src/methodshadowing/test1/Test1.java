package methodshadowing.test1;

interface D0 {
    int m();
}

interface D1 extends D0 {
    default int m() {
        return 1;
    }
}

interface D2 extends D0, D1 {
    default int n() {
        return m();
    }
}

public class Test1 {
    public static void main(String[] args) {
        D2 d2 = new D2() {

        };

        System.out.println(d2.m());
    }
}
