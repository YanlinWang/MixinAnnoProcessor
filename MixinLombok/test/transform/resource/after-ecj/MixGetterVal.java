import lombok.Getter;
import lombok.val;
class MixGetterVal {
  private @Getter int x;
  MixGetterVal() {
    super();
  }
  public void m(int z) {
  }
  public void test() {
    final @val int y = x;
    m(y);
    final @val int a = getX();
    m(a);
  }
  public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") int getX() {
    return this.x;
  }
}