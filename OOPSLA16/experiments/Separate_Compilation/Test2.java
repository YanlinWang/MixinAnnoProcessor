import java.lang.reflect.Method;


public class Test2 extends Test1 {

	public int m() { return 1; }
	
	public static void main(String[] args) {
		Method[] res = Test1.class.getMethods();
		if (res != null) {
			for (Method m : res) {
				System.out.println(m.getName()); // n included
			}
		}
	}
	
}
