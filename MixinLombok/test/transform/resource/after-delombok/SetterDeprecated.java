class SetterDeprecated {
	@Deprecated
	int annotation;
	/**
	 * @deprecated
	 */
	int javadoc;
	@java.lang.Deprecated
	@java.lang.SuppressWarnings("all")
	@javax.annotation.Generated("lombok")
	public void setAnnotation(final int annotation) {
		this.annotation = annotation;
	}
	/**
	 * @deprecated
	 */
	@java.lang.Deprecated
	@java.lang.SuppressWarnings("all")
	@javax.annotation.Generated("lombok")
	public void setJavadoc(final int javadoc) {
		this.javadoc = javadoc;
	}
}