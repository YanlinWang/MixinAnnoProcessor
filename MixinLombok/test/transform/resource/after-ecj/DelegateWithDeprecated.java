import lombok.experimental.Delegate;
class DelegateWithDeprecated {
  private interface Bar {
    @Deprecated void deprecatedAnnotation();
    void deprecatedComment();
    void notDeprecated();
  }
  private @Delegate Bar bar;
  DelegateWithDeprecated() {
    super();
  }
  public @java.lang.Deprecated @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") void deprecatedAnnotation() {
    this.bar.deprecatedAnnotation();
  }
  public @java.lang.Deprecated @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") void deprecatedComment() {
    this.bar.deprecatedComment();
  }
  public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") void notDeprecated() {
    this.bar.notDeprecated();
  }
}
