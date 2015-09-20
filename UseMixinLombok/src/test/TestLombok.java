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
		p1.x(4); p1.y(5);
		System.out.println("p1.x(4); p1.y(5);");
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
		System.out.println(p.x() + " " + p.y());
	}

}

//BEGIN_POINT
@Mixin
interface Point {
    int x();
    int y();
    void x(int x);
    void y(int y);
    default int distance() {
        return (int) Math.sqrt(x() * x() + y() * y());
    }
}
//END_POINT

/****** generated
//BEGIN_POINT_OF
    // inside interface Point
    // Yanlin: it generated withX, withY and clone methods even if users are not 
    // requiring them. I think this may not be a proper design decision, because 
    // it enforces users to have this methods, which are breaking encapsulation.
    // I think refined withX withY clone methods should be generated inside 
    // Point3D only Point is declaring these methods. 
    default Point withX(int x) {
        return of(x, y());
    }
    default Point withY(int y) {
        return of(x(), y);
    }
    Point clone();
    static Point of(int x, int y) {
        return new Point() {
            int _x = x;
            public int x() {
                return _x;
            }
            int _y = y;
            public int y() {
                return _y;
            }
            public void X(int x) {
                _x = x;
            }
            public void Y(int y) {
                _y = y;
            }
            @Override
            public Point clone() {
                return of(x(), y());
            }
        }; 
    }  
//END_POINT_OF
*/

//BEGIN_POINTIMPL
class PointImpl implements Point {
    private int _x;
    private int _y;
    public PointImpl(int x, int y) {
        this._x = x;
        this._y = y;
    }
    public int x() {
        return _x;
    }
    public int y() {
        return _y;
    }
    public Point withX(int x) {
        x(x);
        return this;
    }
    public void x(int x) {
        _x = x;
    }
    public void y(int y) {
        _y = y;
    }
    public Point withY(int y) {
        y(y);
        return this;
    }
    public Point clone() {
        return new PointImpl(_x, _y);
    }
}
//END_POINTIMPL