import lombok.Getter;
public class CommentsInterspersed {
  private int x;
  private @Getter String test = "foo";
  public CommentsInterspersed() {
    super();
  }
  public native void gwtTest();
  public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") String getTest() {
    return this.test;
  }
}
