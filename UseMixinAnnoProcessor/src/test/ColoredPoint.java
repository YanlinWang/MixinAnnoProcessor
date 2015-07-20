package test;

import annotation.Mixin;

//@Mixin
public interface ColoredPoint extends Point,HasColor{ }


/*

package mixin;

public interface ColoredPoint extends test.ColoredPoint {

    static ColoredPoint of() { return new ColoredPoint() {
};}
}

*/