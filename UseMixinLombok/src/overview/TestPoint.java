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

public class TestPoint {
    public static void main(String[] args) {
        Point p1 = Point.of(2, 3);
        System.out.println("p1 = Point.of(2, 3);");
    }
}
