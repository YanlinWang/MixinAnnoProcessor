//skip compare content
@lombok.experimental.Builder
class BuilderInvalidUse {
	private int something;

	@lombok.Getter @lombok.Setter @lombok.experimental.FieldDefaults(makeFinal = true) @lombok.experimental.Wither @lombok.Data @lombok.ToString @lombok.EqualsAndHashCode
	@lombok.AllArgsConstructor
	public static class BuilderInvalidUseBuilder {

	}
}

@lombok.experimental.Builder
class AlsoInvalid {
	@lombok.Value
	public static class AlsoInvalidBuilder {

	}
}