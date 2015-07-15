package test;

import mixin.Point;

public class Main {
    public static void main(String[] args) {
        Point p = Point.of(3, 4);
        System.out.println(p.x() + "," + p.y());
    }
}
