package test;

import annotation.Mixin;

@Mixin
public interface Point {
    
    int x();
    
    int y();
    
    Point withX(int x); 
    
    Point withY(int w);
    
    default int distance(){
        return (int) Math.sqrt(x()*x()+y()*y());
    }
}