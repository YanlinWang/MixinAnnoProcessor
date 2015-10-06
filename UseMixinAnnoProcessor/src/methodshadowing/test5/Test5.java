package methodshadowing.test5;


interface A1 {
    int m();
}
interface B1 {
    default int m() {
        return 0;
    }
}
interface B2 extends B1 {

    int m();
}
interface B3 extends B2 {

    default int m() {
        return 1;
    }
}
interface D extends A1, B3 {}
interface E extends A1, B1 {}

public class Test5 {
    public static void main(String[] args) {
        D d2 = new D() {

        };

        System.out.println(d2.m());
    }
}
