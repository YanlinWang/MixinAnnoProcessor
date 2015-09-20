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
    default int distance() {
        return (int) Math.sqrt(X() * X() + Y() * Y());
    }
}

/****** generated
    // inside interface Point
    // Yanlin: it generated withX, withY and clone methods even if users are not 
    // requiring them. I think this may not be a proper design decision, because 
    // it enforces users to have this methods, which are breaking encapsulation.
    // I think refined withX withY clone methods should be generated inside 
    // Point3D only Point is declaring these methods. 
    default Point withX(int X) {
        return of(X, Y());
    }
    default Point withY(int Y) {
        return of(X(), Y);
    }
    Point clone();
    static Point of(int X, int Y) {
        return new Point() {
            int _X = X;
            public int X() {
                return _X;
            }
            int _Y = Y;
            public int Y() {
                return _Y;
            }
            public void X(int X) {
                _X = X;
            }
            public void Y(int Y) {
                _Y = Y;
            }
            @Override
            public Point clone() {
                return of(X(), Y());
            }
        }; 
    }
*/

//BEGIN_POINTIMPL
class PointImpl implements Point {
    private int _X;
    private int _Y;
    public PointImpl(int X, int Y) {
        this._X = X;
        this._Y = Y;
    }
    public int X() {
        return _X;
    }
    public int Y() {
        return _Y;
    }
    public Point withX(int X) {
        X(X);
        return this;
    }
    public void X(int X) {
        _X = X;
    }
    public void Y(int Y) {
        _Y = Y;
    }
    public Point withY(int Y) {
        Y(Y);
        return this;
    }
    public Point clone() {
        return new PointImpl(_X, _Y);
    }
}
//END_POINTIMPL