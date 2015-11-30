package test;

import lombok.Obj;

public class TestAnimal {
	
	public static String runTest() {
//BEGIN_USINGHORSE
Point2D p = Point2D.of(0, 0);
Horse horse = Horse.of(p);
horse.location(p.withX(42));
//END_USINGHORSE
		String res = "";
		res += "horse initialized at " + print(horse.location()) + "\n";
		horse.run();
		res += "horse.run(); now at " + print(horse.location()) + "\n\n";
		Bird bird = Bird.of(Point3D.of(10, 10, 10));
		res += "bird initialized at " + print(bird.location()) + "\n";
		bird.fly();
		res += "bird.fly(); now at " + print(bird.location()) + "\n\n";
		Pegasus pegasus = Pegasus.of(Point3D.of(100, 100, 100));
		res += "pegasus initialized at " + print(pegasus.location()) + "\n";
		pegasus.run();
		res += "pegasus.run(); now at " + print(pegasus.location()) + "\n";
		pegasus.fly();
		res += "pegasus.fly(); now at " + print(pegasus.location()) + "\n";
		return res;
	}
	
	static String print(Point2D p) {
		return p.x() + ", " + p.y();
	}
	
	static String print(Point3D p) {
		return p.x() + ", " + p.y() + ", " + p.z();
	}
	
}

//BEGIN_POINT2D
@Obj 
interface Point2D {
	int x();
	int y();
	Point2D withX(int x);
	Point2D withY(int y);
}
//END_POINT2D

//BEGIN_POINT3D
@Obj
interface Point3D extends Point2D {
	int z();
	Point3D withZ(int z);
	Point3D with(Point2D val);
}
//END_POINT3D
//BEGIN_ANIMAL
interface Animal {Point2D location(); void location(Point2D val);}
//END_ANIMAL

//BEGIN_HORSE
@Obj interface Horse extends Animal {
	default void run() {location(location().withX(location().x() + 20));}}
//END_HORSE

//BEGIN_BIRD
@Obj interface Bird extends Animal {
	Point3D location();	
	void location(Point3D val);
	default void location(Point2D val) { location(location().with(val));}
	default void fly() {location(location().withX(location().x() + 40));}}
//END_BIRD

//BEGIN_PEGASUS
@Obj interface Pegasus extends Horse, Bird {}
//END_PEGASUS
