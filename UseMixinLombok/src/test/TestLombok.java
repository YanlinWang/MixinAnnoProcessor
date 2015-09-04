package test;

import lombok.Mixin;

public class TestLombok {
	
	public static void main(String[] args) {
		Point p1 = Point.of(2, 3);
		System.out.println("p1 = Point.of(2, 3);");
		Point p2 = p1.clone();
		System.out.println("p2 = p1.clone();");
		System.out.print("print p1: ");
		print(p1);
		p1.X(4); p1.Y(5);
		System.out.println("p1.X(4); p1.Y(5);");
		System.out.print("print p1: ");
		print(p1);
		p1 = p1.withX(6);
		System.out.println("p1 = p1.withX(6);");
		System.out.print("print p1: ");
		print(p1);
		System.out.print("print p2: ");
		print(p2);	
	}
	
	static void print(Point p) {
		System.out.println(p.X() + " " + p.Y());
	}

}

@Mixin
interface Point {
    int X();
	int Y();
    void X(int x);
    void Y(int y);
    Point withX(int x);
    Point withY(int y);
    Point clone();
}