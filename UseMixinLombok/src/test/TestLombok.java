package test;

import lombok.Mixin;

public class TestLombok {
	public static void main(String[] args) {
		Point p = Point.of(2, 3);
		System.out.println(p.x() + " " + p.y());
		p.x(6); p.y(7);
		System.out.println(p.x() + " " + p.y());
	}

}

@Mixin
interface Point {
	int x();
	int y();
	void x(int x);
	void y(int y);
}
