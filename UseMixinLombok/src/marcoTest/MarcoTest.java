package marcoTest;

import lombok.Mixin;

@Mixin 
interface Point{ int x(); int Y();//Is this a bug? should we require field lowercase, and with followed by upperCase
  Point withX(int x); Point withY(int y); //need discussion: whether this line is needed.
  default String toS(){return "["+X()+","+Y()+"]";}
  }
interface Colored{int Color(); Colored withColor(int val);}
@Mixin interface CPoint extends Point,Colored{}
public class MarcoTest {
  public static void main(String[] arg){
	  CPoint p=CPoint.of(10,20,0);
	  Point p2=p.withX(12);
	  p=p.withX(12);//Bug, withX is not overridden here
	  System.out.println(p.toS());
    }
}
