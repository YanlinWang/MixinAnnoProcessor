package overview;

import lombok.Mixin;

//BEGIN_POINT
@Mixin
interface Point {
    int x();
    int y();
}
//END_POINT

/****** generated
//BEGIN_POINT_OF
  // inside interface Point
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
      }; 
  }  
//END_POINT_OF
*/

/******* code without @Mixin
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
}
//END_POINTIMPL
 * */

public class TestPoint {
    public static void main(String[] args) {
        Point p1 = Point.of(2, 3);
        System.out.println("p1 = Point.of(2, 3);");
    }
}
