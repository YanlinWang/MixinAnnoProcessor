package test;

import annotation.Mixin;

@Mixin
public interface Point {
    
    int x();
    int y();
    
    Point withX(int x);
    
    void x(int x);
    
    void y(int x);
    
    default int distance(){
        return (int) Math.sqrt(x()*x()+y()*y());
    }
}
