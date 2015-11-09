package marcoTest;

import lombok.Mixin;


//now mixin generate clone always. This prevent the user 
//to define its own
/*public @Mixin interface CloneTest{
  int foo();
  default CloneTest clone(){ return of(2);}
  public static void main(String[] arg){
    CloneTest t=CloneTest.of(3);
    t=t.clone();
    System.out.println(t.foo());
  } 
}*/

public interface CloneTest{
  int foo();
  public static CloneTest of(int val){
    return new CloneTest(){
      public int foo(){return val;}
      //public CloneTest clone(){//This is what we should generate. it look sort of sad and hard to explain...
      //  return CloneTest.super.clone();}
      //we may want to do similar thing for toString.... may be we just write it in future work?
      };
  }
  default CloneTest clone(){ return of(2);}
  public static void main(String[] arg){
    CloneTest t=CloneTest.of(3);
    t=t.clone();
    System.out.println(t.foo());
  } 
}