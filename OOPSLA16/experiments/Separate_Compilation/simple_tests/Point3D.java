import lombok.Delegate;

@Delegate(types = Point2D.class)
public interface Point3D extends Point2D {
	int z();
	Point3D withZ(int val);
//	public static void main(String[] args) {
//		Point3D p = Point3D.of(1, 2, 3);
//		System.out.println(p.x() + " " + p.y() + " " + p.z());
//		p = p.withX(5).withZ(7);
//		System.out.println(p.x() + " " + p.y() + " " + p.z());
	
//		Point2D p2 = Point2D.of(1, 2); // of method invisible outside Point3D? No, but just cannot put main method here.
//	}	
}
