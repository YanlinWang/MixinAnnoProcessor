package test;

import annotation.Mixin;

//@Mixin
public interface HasColor{
    Color color();
    void color(Color val);
    HasColor clone();
    default void mergeColor(HasColor other){
        color(color().compose(other.color()));
    }
}
