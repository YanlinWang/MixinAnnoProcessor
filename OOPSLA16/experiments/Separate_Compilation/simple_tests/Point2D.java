import lombok.Delegate;

@Delegate
public interface Point2D {
	int x(); int y();
	Point2D withX(int val);
	Point2D withY(int val);
}