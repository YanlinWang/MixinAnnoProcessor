class SetterWithDollar1 {
	int $i;
	
	@java.lang.SuppressWarnings("all")
	
	@javax.annotation.Generated("lombok")
	public void set$i(final int $i) {
		this.$i = $i;
	}
}
class SetterWithDollar2 {
	int $i;
	int i;
	
	@java.lang.SuppressWarnings("all")
	
	@javax.annotation.Generated("lombok")
	public void set$i(final int $i) {
		this.$i = $i;
	}
	@java.lang.SuppressWarnings("all")
	@javax.annotation.Generated("lombok")
	public void setI(final int i) {
		this.i = i;
	}
}