package marcoTest;

public interface StaticTest {
  int foo();
  class Foo{private static StaticTest of(int foo){
    return new StaticTest(){public int foo(){return foo;}};
  }}
  static StaticTest of(int foo){
    if(foo<0)foo=-foo;
    return Foo.of(foo);
  }
  
}

interface StaticSee extends StaticTest{
  static StaticTest boom(){
    return StaticTest.Foo.of(-2);
    }
  }