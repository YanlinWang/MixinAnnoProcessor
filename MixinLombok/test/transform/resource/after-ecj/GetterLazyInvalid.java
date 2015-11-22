class GetterLazyInvalidNotFinal {
  private @lombok.Getter(lazy = true) String fieldName = "";
  GetterLazyInvalidNotFinal() {
    super();
  }
}
class GetterLazyInvalidNotPrivate {
  final @lombok.Getter(lazy = true) String fieldName = "";
  GetterLazyInvalidNotPrivate() {
    super();
  }
}
class GetterLazyInvalidNotPrivateFinal {
  @lombok.Getter(lazy = true) String fieldName = "";
  GetterLazyInvalidNotPrivateFinal() {
    super();
  }
}
class GetterLazyInvalidNone {
  private final @lombok.Getter(lazy = true,value = lombok.AccessLevel.NONE) String fieldName = "";
  GetterLazyInvalidNone() {
    super();
  }
}
@lombok.Getter(lazy = true) class GetterLazyInvalidClass {
  private final String fieldName = "";
  GetterLazyInvalidClass() {
    super();
  }
  public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") String getFieldName() {
    return this.fieldName;
  }
}
class GetterLazyInvalidNoInit {
  private final @lombok.Getter(lazy = true) String fieldName;
  GetterLazyInvalidNoInit() {
    super();
    this.fieldName = "foo";
  }
}
