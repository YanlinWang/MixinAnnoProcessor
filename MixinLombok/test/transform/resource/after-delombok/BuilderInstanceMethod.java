import java.util.List;
class BuilderInstanceMethod<T> {
	public String create(int show, final int yes, List<T> also, int $andMe) {
		return "" + show + yes + also + $andMe;
	}
	@java.lang.SuppressWarnings("all")
	@javax.annotation.Generated("lombok")
	public class StringBuilder {
		@java.lang.SuppressWarnings("all")
		@javax.annotation.Generated("lombok")
		private int show;
		@java.lang.SuppressWarnings("all")
		@javax.annotation.Generated("lombok")
		private int yes;
		@java.lang.SuppressWarnings("all")
		@javax.annotation.Generated("lombok")
		private List<T> also;
		@java.lang.SuppressWarnings("all")
		@javax.annotation.Generated("lombok")
		private int $andMe;
		@java.lang.SuppressWarnings("all")
		@javax.annotation.Generated("lombok")
		StringBuilder() {
		}
		@java.lang.SuppressWarnings("all")
		@javax.annotation.Generated("lombok")
		public StringBuilder show(final int show) {
			this.show = show;
			return this;
		}
		@java.lang.SuppressWarnings("all")
		@javax.annotation.Generated("lombok")
		public StringBuilder yes(final int yes) {
			this.yes = yes;
			return this;
		}
		@java.lang.SuppressWarnings("all")
		@javax.annotation.Generated("lombok")
		public StringBuilder also(final List<T> also) {
			this.also = also;
			return this;
		}
		@java.lang.SuppressWarnings("all")
		@javax.annotation.Generated("lombok")
		public StringBuilder $andMe(final int $andMe) {
			this.$andMe = $andMe;
			return this;
		}
		@java.lang.SuppressWarnings("all")
		@javax.annotation.Generated("lombok")
		public String build() {
			return BuilderInstanceMethod.this.create(show, yes, also, $andMe);
		}
		@java.lang.Override
		@java.lang.SuppressWarnings("all")
		@javax.annotation.Generated("lombok")
		public java.lang.String toString() {
			return "BuilderInstanceMethod.StringBuilder(show=" + this.show + ", yes=" + this.yes + ", also=" + this.also + ", $andMe=" + this.$andMe + ")";
		}
	}
	@java.lang.SuppressWarnings("all")
	@javax.annotation.Generated("lombok")
	public StringBuilder builder() {
		return new StringBuilder();
	}
}
