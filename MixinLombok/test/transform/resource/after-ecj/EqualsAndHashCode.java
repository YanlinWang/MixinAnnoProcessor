@lombok.EqualsAndHashCode class EqualsAndHashCode {
  int x;
  boolean[] y;
  Object[] z;
  String a;
  String b;
  EqualsAndHashCode() {
    super();
  }
  public @java.lang.Override @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") boolean equals(final java.lang.Object o) {
    if ((o == this))
        return true;
    if ((! (o instanceof EqualsAndHashCode)))
        return false;
    final EqualsAndHashCode other = (EqualsAndHashCode) o;
    if ((! other.canEqual((java.lang.Object) this)))
        return false;
    if ((this.x != other.x))
        return false;
    if ((! java.util.Arrays.equals(this.y, other.y)))
        return false;
    if ((! java.util.Arrays.deepEquals(this.z, other.z)))
        return false;
    final java.lang.Object this$a = this.a;
    final java.lang.Object other$a = other.a;
    if (((this$a == null) ? (other$a != null) : (! this$a.equals(other$a))))
        return false;
    final java.lang.Object this$b = this.b;
    final java.lang.Object other$b = other.b;
    if (((this$b == null) ? (other$b != null) : (! this$b.equals(other$b))))
        return false;
    return true;
  }
  protected @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") boolean canEqual(final java.lang.Object other) {
    return (other instanceof EqualsAndHashCode);
  }
  public @java.lang.Override @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = ((result * PRIME) + this.x);
    result = ((result * PRIME) + java.util.Arrays.hashCode(this.y));
    result = ((result * PRIME) + java.util.Arrays.deepHashCode(this.z));
    final java.lang.Object $a = this.a;
    result = ((result * PRIME) + (($a == null) ? 43 : $a.hashCode()));
    final java.lang.Object $b = this.b;
    result = ((result * PRIME) + (($b == null) ? 43 : $b.hashCode()));
    return result;
  }
}
final @lombok.EqualsAndHashCode class EqualsAndHashCode2 {
  int x;
  long y;
  float f;
  double d;
  boolean b;
  EqualsAndHashCode2() {
    super();
  }
  public @java.lang.Override @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") boolean equals(final java.lang.Object o) {
    if ((o == this))
        return true;
    if ((! (o instanceof EqualsAndHashCode2)))
        return false;
    final EqualsAndHashCode2 other = (EqualsAndHashCode2) o;
    if ((this.x != other.x))
        return false;
    if ((this.y != other.y))
        return false;
    if ((java.lang.Float.compare(this.f, other.f) != 0))
        return false;
    if ((java.lang.Double.compare(this.d, other.d) != 0))
        return false;
    if ((this.b != other.b))
        return false;
    return true;
  }
  public @java.lang.Override @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = ((result * PRIME) + this.x);
    final long $y = this.y;
    result = ((result * PRIME) + (int) ($y ^ ($y >>> 32)));
    result = ((result * PRIME) + java.lang.Float.floatToIntBits(this.f));
    final long $d = java.lang.Double.doubleToLongBits(this.d);
    result = ((result * PRIME) + (int) ($d ^ ($d >>> 32)));
    result = ((result * PRIME) + (this.b ? 79 : 97));
    return result;
  }
}
final @lombok.EqualsAndHashCode(callSuper = false) class EqualsAndHashCode3 extends EqualsAndHashCode {
  EqualsAndHashCode3() {
    super();
  }
  public @java.lang.Override @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") boolean equals(final java.lang.Object o) {
    if ((o == this))
        return true;
    if ((! (o instanceof EqualsAndHashCode3)))
        return false;
    final EqualsAndHashCode3 other = (EqualsAndHashCode3) o;
    if ((! other.canEqual((java.lang.Object) this)))
        return false;
    return true;
  }
  protected @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") boolean canEqual(final java.lang.Object other) {
    return (other instanceof EqualsAndHashCode3);
  }
  public @java.lang.Override @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") int hashCode() {
    int result = 1;
    return result;
  }
}
@lombok.EqualsAndHashCode(callSuper = true) class EqualsAndHashCode4 extends EqualsAndHashCode {
  EqualsAndHashCode4() {
    super();
  }
  public @java.lang.Override @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") boolean equals(final java.lang.Object o) {
    if ((o == this))
        return true;
    if ((! (o instanceof EqualsAndHashCode4)))
        return false;
    final EqualsAndHashCode4 other = (EqualsAndHashCode4) o;
    if ((! other.canEqual((java.lang.Object) this)))
        return false;
    if ((! super.equals(o)))
        return false;
    return true;
  }
  protected @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") boolean canEqual(final java.lang.Object other) {
    return (other instanceof EqualsAndHashCode4);
  }
  public @java.lang.Override @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = ((result * PRIME) + super.hashCode());
    return result;
  }
}
