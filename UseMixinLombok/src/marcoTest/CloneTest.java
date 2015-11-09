package marcoTest;

import lombok.Mixin;


public @Mixin interface CloneTest{
  int foo();
  default CloneTest clone(){ return of(2);}
  public static void main(String[] arg){
    CloneTest t=CloneTest.of(3);
    t=t.clone();
    System.out.println(t.foo());
  } 
}