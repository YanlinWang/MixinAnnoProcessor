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

//BEGIN_POINT2D
@Mixin 
interface Point2D {
	int x();
	int y();
	Point2D withX(int x);
	Point2D withY(int y);
}
//END_POINT2D

//BEGIN_POINT3D
@Mixin
interface Point3D extends Point2D {
	int z(); Point3D with(Point val);
	Point3D withZ(int z);
}
//END_POINT3D

//BEGIN_ANIMAL
interface Animal {
	Point2D point();
	void point(Point2D val);
}
//END_ANIMAL

//BEGIN_HORSE
@Mixin
interface Horse extends Animal {
	default void run() {
		point(point().withX(point().x() + 20));
	}
}
//END_HORSE

//BEGIN_BIRD
@Mixin
interface Bird extends Animal {
	Point3D point();
	void point(Point3D val);
	default void point(Point2D val) { point(point().with(val));}
	default void fly() {
		point(point().withX(point().x() + 40));
	}
}
//END_BIRD





//BEGIN_PEGASUS
@Mixin
interface Pegasus extends Horse, Bird {}
//END_PEGASUS
