class SimpleTypeResolutionFail {
	@Getter
	private int x;
}
class SimpleTypeResolutionSuccess {
	private int x;
	@java.lang.SuppressWarnings("all")
	@javax.annotation.Generated("lombok")
	public int getX() {
		return this.x;
	}
}
