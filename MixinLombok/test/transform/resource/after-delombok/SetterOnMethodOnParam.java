class SetterOnMethodOnParam {
	int i;
	int j;
	int k;
	public @interface Test {
	}
	@Deprecated
	@java.lang.SuppressWarnings("all")
	@javax.annotation.Generated("lombok")
	public void setI(final int i) {
		this.i = i;
	}
	@java.lang.Deprecated
	@Test
	@java.lang.SuppressWarnings("all")
	@javax.annotation.Generated("lombok")
	public void setJ(@Test final int j) {
		this.j = j;
	}
	@java.lang.Deprecated
	@Test
	@java.lang.SuppressWarnings("all")
	@javax.annotation.Generated("lombok")
	public void setK(@Test final int k) {
		this.k = k;
	}
}
