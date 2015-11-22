import java.util.List;
class BuilderInstanceMethod<T> {
  public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") class StringBuilder {
    private @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") int show;
    private @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") int yes;
    private @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") List<T> also;
    private @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") int $andMe;
    @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") StringBuilder() {
      super();
    }
    public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") StringBuilder show(final int show) {
      this.show = show;
      return this;
    }
    public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") StringBuilder yes(final int yes) {
      this.yes = yes;
      return this;
    }
    public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") StringBuilder also(final List<T> also) {
      this.also = also;
      return this;
    }
    public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") StringBuilder $andMe(final int $andMe) {
      this.$andMe = $andMe;
      return this;
    }
    public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") String build() {
      return BuilderInstanceMethod.this.create(show, yes, also, $andMe);
    }
    public @java.lang.Override @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") java.lang.String toString() {
      return (((((((("BuilderInstanceMethod.StringBuilder(show=" + this.show) + ", yes=") + this.yes) + ", also=") + this.also) + ", $andMe=") + this.$andMe) + ")");
    }
  }
  BuilderInstanceMethod() {
    super();
  }
  public @lombok.Builder String create(int show, final int yes, List<T> also, int $andMe) {
    return (((("" + show) + yes) + also) + $andMe);
  }
  public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") StringBuilder builder() {
    return new StringBuilder();
  }
}
