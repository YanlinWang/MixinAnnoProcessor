package test;

import annotation.Mixin;

@Mixin
public interface Point {
    
    int x();
    int y();
    
    Point withX(int x);
    
//    void x(int x);
    
//    void x(int x); 
    /*
     * public void x(int x) {
     *      this.x = x;
     * }
     */
    
    default int distance(){
        return (int) Math.sqrt(x()*x()+y()*y());
    }
}
