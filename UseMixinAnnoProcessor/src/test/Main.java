package test;

import test.Point;

public class Main {
    private static void print(Point p) { 
        System.out.println("(" + p.x() + ", " + p.y() + ")");
    }
    
    public static void main(String[] args) {
	Point p = mixin.Point.of(3, 4);
        print(p);
        p.x(9); 
        p.y(8);
        print(p);
	print(p.withX(6));
    }
}
