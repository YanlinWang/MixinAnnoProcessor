public enum DataOnEnum {
	A("hello");
	private final String someField;
	@java.lang.SuppressWarnings("all")
	@javax.annotation.Generated("lombok")
	public String getSomeField() {
		return this.someField;
	}
	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	@javax.annotation.Generated("lombok")
	public java.lang.String toString() {
		return "DataOnEnum(someField=" + this.getSomeField() + ")";
	}
	@java.lang.SuppressWarnings("all")
	@javax.annotation.Generated("lombok")
	private DataOnEnum(final String someField) {
		this.someField = someField;
	}
}