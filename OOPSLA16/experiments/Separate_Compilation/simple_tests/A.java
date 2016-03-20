
public class A {
	public static void main(String args[]) {
		Point3D p = Point3D.of(1, 2, 3);
		System.out.println(p.x() + " " + p.y() + " " + p.z());
		p = p.withX(4).withY(5).withZ(6);
		System.out.println(p.x() + " " + p.y() + " " + p.z());
		Point4D q = Point4D.of(0, 0, 0, 0);
		q = q.with(p);
		System.out.println(q.x() + " " + q.y() + " " + q.z() + " " + q.t());
		q.t(8);
		System.out.println(q.x() + " " + q.y() + " " + q.z() + " " + q.t());
	}
}