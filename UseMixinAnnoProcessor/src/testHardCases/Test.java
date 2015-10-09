package testHardCases;

public class Test {
  public static void main(String[] arg){
	//  A.m();
	//  C.m();
  }
}

/*interface A1{ default C1 m(){return this.m();}}
interface C1{}
interface C2 extends C1{}
interface A2 extends A1{C1 m();}
interface A0 {C2 m();}
interface A3 extends A2,A0,A1{}*/
/*interface A{static String m(){return "A";}}
interface C extends A{
	default String dm(){
		  this.m();
		  A.m();
		  C.m();
		}
		
interface T{}
interface C1 extends C2 { default T m() { return null;}}
interface C2 { default T m() {return null;}}
interface C extends C1, C2 { }
}*/
/*
interface T{}
interface C1 { default T m() { return null;}}
interface C2 { default T m() {return null;}}
interface C extends C1, C2 { T m(); }
*/

interface A1 {  int m();}
interface B2  {  int m();}
interface B3 extends B2{   default int m() { return 1;}}
interface D extends B3,A1 {}

