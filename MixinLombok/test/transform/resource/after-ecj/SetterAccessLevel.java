class SetterAccessLevel {
  @lombok.Setter(lombok.AccessLevel.NONE) boolean isNone;
  @lombok.Setter(lombok.AccessLevel.PRIVATE) boolean isPrivate;
  @lombok.Setter(lombok.AccessLevel.PACKAGE) boolean isPackage;
  @lombok.Setter(lombok.AccessLevel.PROTECTED) boolean isProtected;
  @lombok.Setter(lombok.AccessLevel.PUBLIC) boolean isPublic;
  @lombok.Setter(value = lombok.AccessLevel.PUBLIC) boolean value;
  SetterAccessLevel() {
    super();
  }
  private @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") void setPrivate(final boolean isPrivate) {
    this.isPrivate = isPrivate;
  }
  @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") void setPackage(final boolean isPackage) {
    this.isPackage = isPackage;
  }
  protected @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") void setProtected(final boolean isProtected) {
    this.isProtected = isProtected;
  }
  public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") void setPublic(final boolean isPublic) {
    this.isPublic = isPublic;
  }
  public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") void setValue(final boolean value) {
    this.value = value;
  }
}
