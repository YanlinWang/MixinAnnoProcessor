class SetterWithDollar1 {
  @lombok.Setter int $i;
  SetterWithDollar1() {
    super();
  }
  public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") void set$i(final int $i) {
    this.$i = $i;
  }
}
class SetterWithDollar2 {
  @lombok.Setter int $i;
  @lombok.Setter int i;
  SetterWithDollar2() {
    super();
  }
  public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") void set$i(final int $i) {
    this.$i = $i;
  }
  public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") void setI(final int i) {
    this.i = i;
  }
}
