package testHardCases;

public class Test {
  public static void main(String[] arg){
	  A.m();
	  C.m();
  }
}

interface A1{ default C1 m(){return this.m();}}
interface C1{}
interface C2 extends C1{}
interface A2 extends A1{C1 m();}
interface A0 {C2 m();}
interface A3 extends A2,A0,A1{}
/*interface A{static String m(){return "A";}}
interface C extends A{
	default String dm(){
		  this.m();
		  A.m();
		  C.m();
		}
}*/
