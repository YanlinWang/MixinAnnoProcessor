class GetterLazy {
  static class ValueType {
    ValueType() {
      super();
    }
  }
  private final @lombok.Getter(lazy = true) java.util.concurrent.atomic.AtomicReference<java.lang.Object> fieldName = new java.util.concurrent.atomic.AtomicReference<java.lang.Object>();
  GetterLazy() {
    super();
  }
  public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") ValueType getFieldName() {
    java.lang.Object value = this.fieldName.get();
    if ((value == null))
        {
          synchronized (this.fieldName)
            {
              value = this.fieldName.get();
              if ((value == null))
                  {
                    final ValueType actualValue = new ValueType();
                    value = ((actualValue == null) ? this.fieldName : actualValue);
                    this.fieldName.set(value);
                  }
            }
        }
    return (ValueType) ((value == this.fieldName) ? null : value);
  }
}
