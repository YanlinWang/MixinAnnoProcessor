package marcoTest;

import lombok.Mixin;

@Mixin
interface Point {
    int x();
    int y();
    Point withX(int x);
    Point withY(int y); //need discussion: whether this line is needed.
    default String toS() {
        return "[" + x() + "," + y() + "]";
    }
}
interface Colored {
    int color();
    Colored withColor(int val);
}
@Mixin interface CPoint extends Point,Colored{}
public class MarcoTest {
    public static void main(String[] arg) {
        CPoint p = CPoint.of(10, 20, 0);
        Point p2 = p.withX(12);
        p = p.withX(12);
        System.out.println(p.toS());
    }
}
