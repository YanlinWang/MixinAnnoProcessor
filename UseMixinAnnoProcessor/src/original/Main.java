package original;

import annotation.Mixin;

// BASIC EXAMPLE
//@Mixin
interface Point0{
    int x();
    int y();
    Point withX(int x);
    default int distance(){
        return (int) Math.sqrt(x()*x()+y()*y());
    }
}

// expand to:
//@Mixin
interface Point {
    int x();
    int y();
    Point withX(int x);

    default int distance(){
        return (int) Math.sqrt(x()*x()+y()*y());
    }

    static Point of(int x,int y){return new Point(){
        public int x(){return x;}
        public int y(){return y;}
        public Point withX(int x){return of(x,y());}
    };}
}

///// EXAMPLE FOR "INHERITANCE" AND STATE
interface HasColor{
    Color color();
    void color(Color val);
    HasColor clone();
    default void mergeColor(HasColor other){
        color(color().compose(other.color()));
    }
}

//@Mixin 
//interface ColoredPoint extends Point,HasColor{ }

//expands to
//@Mixin 
interface ColoredPoint extends Point,HasColor{
//    ColoredPoint withX(int x);
    ColoredPoint clone();
    static ColoredPoint of(int x, int y, Color color){return new ColoredPoint(){
        public int x(){return x;}
        public int y(){return y;}
        Color _color=color;
        public Color color(){return _color;}
        public void color(Color val){_color=val;}
        public ColoredPoint withX(int x){return of(x,y(),color());}
        public ColoredPoint clone(){return of(x(),y(),color());}
    };}
}

class Color {
    int rep;
    public Color(int rep) { this.rep = rep; }
    public Color compose(Color that) { 
        return new Color(this.rep + that.rep); 
    }
}

public class Main {
    public static void main(String[] args) {
        // test Point
        Point p = Point.of(3, 4);
        System.out.println(p.x() + "," + p.y());
        Point q = p.withX(7);
        System.out.println(q.x() + "," + q.y());

        // test ColoredPoint
        Color color = new Color(1);
        ColoredPoint cp = ColoredPoint.of(3, 4, color);
        System.out.println("distance: " + cp.distance() +
                " color: " + cp.color().rep);
    }
}
