package test;

import lombok.Mixin;

public class TestAnimal {
	
	public static void main(String[] args) {
		Horse horse = Horse.of(Point2D.of(0, 0));
		System.out.print("horse initialized at ");
		print(horse.point());
		horse.run();
		System.out.print("horse.run(); now at ");
		print(horse.point());
		System.out.println();
		
		Bird bird = Bird.of(Point3D.of(10, 10, 10));
		System.out.print("bird initialized at ");
		print(bird.point());
		bird.fly();
		System.out.print("bird.fly(); now at ");
		print(bird.point());
		System.out.println();
		
		Pegasus pegasus = Pegasus.of(Point3D.of(100, 100, 100));
		System.out.print("pegasus initialized at ");
		print(pegasus.point());
		pegasus.run();
		System.out.print("pegasus.run(); now at ");
		print(pegasus.point());
		pegasus.fly();
		System.out.print("pegasus.fly(); now at ");
		print(pegasus.point());
	}
	
	static void print(Point2D p) {
		System.out.println(p.x() + ", " + p.y());
	}
	
	static void print(Point3D p) {
		System.out.println(p.x() + ", " + p.y() + ", " + p.z());
	}
	
}

@Mixin 
interface Point2D {
	int x();
	int y();
}

@Mixin
interface Point3D extends Point2D {
	int z();
}

interface Animal {
	Point2D point();
	void point(Point2D val);
}

@Mixin
interface Horse extends Animal {
	default void run() {
		point(point().withX(point().x() + 20));
	}
}

@Mixin
interface Bird extends Animal {
	Point3D point();
	void point(Point3D val);
	default void point(Point2D val) {
		if(val instanceof Point3D) { point((Point3D) val); }
		Point3D newPoint = point().withX(val.x()).withY(val.y());
		point(newPoint);
	}
	default void fly() {
		point(point().withX(point().x() + 40));
	}
}

@Mixin
interface Pegasus extends Horse, Bird {}