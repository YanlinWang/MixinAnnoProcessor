import lombok.Data;
@lombok.Data class Data1 {
  final int x;
  String name;
  public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") int getX() {
    return this.x;
  }
  public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") String getName() {
    return this.name;
  }
  public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") void setName(final String name) {
    this.name = name;
  }
  public @java.lang.Override @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") boolean equals(final java.lang.Object o) {
    if ((o == this))
        return true;
    if ((! (o instanceof Data1)))
        return false;
    final Data1 other = (Data1) o;
    if ((! other.canEqual((java.lang.Object) this)))
        return false;
    if ((this.getX() != other.getX()))
        return false;
    final java.lang.Object this$name = this.getName();
    final java.lang.Object other$name = other.getName();
    if (((this$name == null) ? (other$name != null) : (! this$name.equals(other$name))))
        return false;
    return true;
  }
  protected @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") boolean canEqual(final java.lang.Object other) {
    return (other instanceof Data1);
  }
  public @java.lang.Override @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = ((result * PRIME) + this.getX());
    final java.lang.Object $name = this.getName();
    result = ((result * PRIME) + (($name == null) ? 43 : $name.hashCode()));
    return result;
  }
  public @java.lang.Override @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") java.lang.String toString() {
    return (((("Data1(x=" + this.getX()) + ", name=") + this.getName()) + ")");
  }
  public @java.beans.ConstructorProperties({"x"}) @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") Data1(final int x) {
    super();
    this.x = x;
  }
}
@Data class Data2 {
  final int x;
  String name;
  public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") int getX() {
    return this.x;
  }
  public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") String getName() {
    return this.name;
  }
  public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") void setName(final String name) {
    this.name = name;
  }
  public @java.lang.Override @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") boolean equals(final java.lang.Object o) {
    if ((o == this))
        return true;
    if ((! (o instanceof Data2)))
        return false;
    final Data2 other = (Data2) o;
    if ((! other.canEqual((java.lang.Object) this)))
        return false;
    if ((this.getX() != other.getX()))
        return false;
    final java.lang.Object this$name = this.getName();
    final java.lang.Object other$name = other.getName();
    if (((this$name == null) ? (other$name != null) : (! this$name.equals(other$name))))
        return false;
    return true;
  }
  protected @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") boolean canEqual(final java.lang.Object other) {
    return (other instanceof Data2);
  }
  public @java.lang.Override @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = ((result * PRIME) + this.getX());
    final java.lang.Object $name = this.getName();
    result = ((result * PRIME) + (($name == null) ? 43 : $name.hashCode()));
    return result;
  }
  public @java.lang.Override @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") java.lang.String toString() {
    return (((("Data2(x=" + this.getX()) + ", name=") + this.getName()) + ")");
  }
  public @java.beans.ConstructorProperties({"x"}) @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") Data2(final int x) {
    super();
    this.x = x;
  }
}
final @Data class Data3 {
  final int x;
  String name;
  public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") int getX() {
    return this.x;
  }
  public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") String getName() {
    return this.name;
  }
  public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") void setName(final String name) {
    this.name = name;
  }
  public @java.lang.Override @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") boolean equals(final java.lang.Object o) {
    if ((o == this))
        return true;
    if ((! (o instanceof Data3)))
        return false;
    final Data3 other = (Data3) o;
    if ((this.getX() != other.getX()))
        return false;
    final java.lang.Object this$name = this.getName();
    final java.lang.Object other$name = other.getName();
    if (((this$name == null) ? (other$name != null) : (! this$name.equals(other$name))))
        return false;
    return true;
  }
  public @java.lang.Override @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = ((result * PRIME) + this.getX());
    final java.lang.Object $name = this.getName();
    result = ((result * PRIME) + (($name == null) ? 43 : $name.hashCode()));
    return result;
  }
  public @java.lang.Override @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") java.lang.String toString() {
    return (((("Data3(x=" + this.getX()) + ", name=") + this.getName()) + ")");
  }
  public @java.beans.ConstructorProperties({"x"}) @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") Data3(final int x) {
    super();
    this.x = x;
  }
}
final @Data @lombok.EqualsAndHashCode(callSuper = true) class Data4 extends java.util.Timer {
  int x;
  Data4() {
    super();
  }
  public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") int getX() {
    return this.x;
  }
  public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") void setX(final int x) {
    this.x = x;
  }
  public @java.lang.Override @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") java.lang.String toString() {
    return (("Data4(x=" + this.getX()) + ")");
  }
  public @java.lang.Override @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") boolean equals(final java.lang.Object o) {
    if ((o == this))
        return true;
    if ((! (o instanceof Data4)))
        return false;
    final Data4 other = (Data4) o;
    if ((! other.canEqual((java.lang.Object) this)))
        return false;
    if ((! super.equals(o)))
        return false;
    if ((this.getX() != other.getX()))
        return false;
    return true;
  }
  protected @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") boolean canEqual(final java.lang.Object other) {
    return (other instanceof Data4);
  }
  public @java.lang.Override @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = ((result * PRIME) + super.hashCode());
    result = ((result * PRIME) + this.getX());
    return result;
  }
}
@Data class Data5 {
  public @java.lang.Override @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") boolean equals(final java.lang.Object o) {
    if ((o == this))
        return true;
    if ((! (o instanceof Data5)))
        return false;
    final Data5 other = (Data5) o;
    if ((! other.canEqual((java.lang.Object) this)))
        return false;
    return true;
  }
  protected @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") boolean canEqual(final java.lang.Object other) {
    return (other instanceof Data5);
  }
  public @java.lang.Override @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") int hashCode() {
    int result = 1;
    return result;
  }
  public @java.lang.Override @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") java.lang.String toString() {
    return "Data5()";
  }
  public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") Data5() {
    super();
  }
}
final @Data class Data6 {
  public @java.lang.Override @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") boolean equals(final java.lang.Object o) {
    if ((o == this))
        return true;
    if ((! (o instanceof Data6)))
        return false;
    return true;
  }
  public @java.lang.Override @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") int hashCode() {
    int result = 1;
    return result;
  }
  public @java.lang.Override @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") java.lang.String toString() {
    return "Data6()";
  }
  public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") Data6() {
    super();
  }
}
