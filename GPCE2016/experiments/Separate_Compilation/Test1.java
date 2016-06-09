import java.lang.reflect.Method;


public class Test1 {
	
	public int n() { return 1; }
	
	public static void main(String[] args) {
		Method[] res = Test2.class.getMethods();
		if (res != null) {
			for (Method m : res) {
				System.out.println(m.getName()); // m, n included
			}
		}
	}
	
}
