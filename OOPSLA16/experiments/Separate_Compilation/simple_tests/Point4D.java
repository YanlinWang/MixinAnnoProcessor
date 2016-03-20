import lombok.Delegate;

@Delegate(types = Point3D.class)
public interface Point4D extends Point3D {
	int t();
	void t(int val);
	Point4D with(Point3D val);
//	Point3D withT(int val); // Duplicate method?
}
