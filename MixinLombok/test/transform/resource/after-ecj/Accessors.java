class AccessorsFluent {
  private @lombok.Getter @lombok.Setter @lombok.experimental.Accessors(fluent = true) String fieldName = "";
  AccessorsFluent() {
    super();
  }
  public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") String fieldName() {
    return this.fieldName;
  }
  public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") AccessorsFluent fieldName(final String fieldName) {
    this.fieldName = fieldName;
    return this;
  }
}
@lombok.experimental.Accessors(fluent = true) @lombok.Getter class AccessorsFluentOnClass {
  private @lombok.Setter String fieldName = "";
  private @lombok.experimental.Accessors String otherFieldWithOverride = "";
  AccessorsFluentOnClass() {
    super();
  }
  public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") AccessorsFluentOnClass fieldName(final String fieldName) {
    this.fieldName = fieldName;
    return this;
  }
  public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") String fieldName() {
    return this.fieldName;
  }
  public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") String getOtherFieldWithOverride() {
    return this.otherFieldWithOverride;
  }
}
class AccessorsChain {
  private @lombok.Setter @lombok.experimental.Accessors(chain = true) boolean isRunning;
  AccessorsChain() {
    super();
  }
  public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") AccessorsChain setRunning(final boolean isRunning) {
    this.isRunning = isRunning;
    return this;
  }
}
@lombok.experimental.Accessors(prefix = "f") class AccessorsPrefix {
  private @lombok.Setter String fieldName;
  private @lombok.Setter String fActualField;
  AccessorsPrefix() {
    super();
  }
  public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") void setActualField(final String fActualField) {
    this.fActualField = fActualField;
  }
}
@lombok.experimental.Accessors(prefix = {"f", ""}) class AccessorsPrefix2 {
  private @lombok.Setter String fieldName;
  private @lombok.Setter String fActualField;
  AccessorsPrefix2() {
    super();
  }
  public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") void setFieldName(final String fieldName) {
    this.fieldName = fieldName;
  }
  public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") void setActualField(final String fActualField) {
    this.fActualField = fActualField;
  }
}
@lombok.experimental.Accessors(prefix = "f") @lombok.ToString @lombok.EqualsAndHashCode class AccessorsPrefix3 {
  private String fName;
  AccessorsPrefix3() {
    super();
  }
  private String getName() {
    return fName;
  }
  public @java.lang.Override @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") java.lang.String toString() {
    return (("AccessorsPrefix3(fName=" + this.getName()) + ")");
  }
  public @java.lang.Override @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") boolean equals(final java.lang.Object o) {
    if ((o == this))
        return true;
    if ((! (o instanceof AccessorsPrefix3)))
        return false;
    final AccessorsPrefix3 other = (AccessorsPrefix3) o;
    if ((! other.canEqual((java.lang.Object) this)))
        return false;
    final java.lang.Object this$fName = this.getName();
    final java.lang.Object other$fName = other.getName();
    if (((this$fName == null) ? (other$fName != null) : (! this$fName.equals(other$fName))))
        return false;
    return true;
  }
  protected @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") boolean canEqual(final java.lang.Object other) {
    return (other instanceof AccessorsPrefix3);
  }
  public @java.lang.Override @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final java.lang.Object $fName = this.getName();
    result = ((result * PRIME) + (($fName == null) ? 43 : $fName.hashCode()));
    return result;
  }
}
class AccessorsFluentGenerics<T extends Number> {
  private @lombok.Setter @lombok.experimental.Accessors(fluent = true) String name;
  AccessorsFluentGenerics() {
    super();
  }
  public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") AccessorsFluentGenerics<T> name(final String name) {
    this.name = name;
    return this;
  }
}
class AccessorsFluentNoChaining {
  private @lombok.Setter @lombok.experimental.Accessors(fluent = true,chain = false) String name;
  AccessorsFluentNoChaining() {
    super();
  }
  public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") void name(final String name) {
    this.name = name;
  }
}
class AccessorsFluentStatic<T extends Number> {
  private static @lombok.Setter @lombok.experimental.Accessors(fluent = true) String name;
  <clinit>() {
  }
  AccessorsFluentStatic() {
    super();
  }
  public static @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") void name(final String name) {
    AccessorsFluentStatic.name = name;
  }
}
