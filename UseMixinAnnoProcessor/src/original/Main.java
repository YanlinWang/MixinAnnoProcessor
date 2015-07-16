package original;

import annotation.Mixin;

// BASIC EXAMPLE
//@Mixin
interface Point0{
    int x();
    int y();
    Point withX(int x);
    default int distance(){
        return (int) Math.sqrt(x()*x()+y()*y());
    }
}

// expand to:
//@Mixin
interface Point {
    int x();
    int y();
    Point withX(int x);
    
    default int distance(){
        return (int) Math.sqrt(x()*x()+y()*y());
    }
    
    static Point of(int x,int y){return new Point(){
        public int x(){return x;}
        public int y(){return y;}
        public Point withX(int x){return of(x,y());}
    };}
}

public class Main {
    public static void main(String[] args) {
        Point p = Point.of(3, 4);
        System.out.println(p.x() + "," + p.y());
        Point q = p.withX(7);
        System.out.println(q.x() + "," + q.y());
    }
}
