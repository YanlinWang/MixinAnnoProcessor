interface LoggerSlf4jTypesInterface {
}
@interface LoggerSlf4jTypesAnnotation {
}
enum LoggerSlf4jTypesEnum {
	;
	@java.lang.SuppressWarnings("all")
	@javax.annotation.Generated("lombok")
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LoggerSlf4jTypesEnum.class);
}
enum LoggerSlf4jTypesEnumWithElement {
	FOO;
	@java.lang.SuppressWarnings("all")
	@javax.annotation.Generated("lombok")
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LoggerSlf4jTypesEnumWithElement.class);
}
interface LoggerSlf4jTypesInterfaceOuter {
	class Inner {
		@java.lang.SuppressWarnings("all")
		@javax.annotation.Generated("lombok")
		private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Inner.class);
	}
}