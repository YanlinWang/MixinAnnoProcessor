import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSortedMap;
import lombok.Singular;
@lombok.Builder class BuilderSingularGuavaMaps<K, V> {
  public static @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") class BuilderSingularGuavaMapsBuilder<K, V> {
    private @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") com.google.common.collect.ImmutableMap.Builder<K, V> battleaxes;
    private @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") com.google.common.collect.ImmutableSortedMap.Builder<Integer, V> vertices;
    private @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") com.google.common.collect.ImmutableBiMap.Builder<java.lang.Object, java.lang.Object> rawMap;
    @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") BuilderSingularGuavaMapsBuilder() {
      super();
    }
    public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") BuilderSingularGuavaMapsBuilder<K, V> battleaxe(K battleaxe$key, V battleaxe$value) {
      if ((this.battleaxes == null))
          this.battleaxes = com.google.common.collect.ImmutableMap.builder();
      this.battleaxes.put(battleaxe$key, battleaxe$value);
      return this;
    }
    public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") BuilderSingularGuavaMapsBuilder<K, V> battleaxes(java.util.Map<? extends K, ? extends V> battleaxes) {
      if ((this.battleaxes == null))
          this.battleaxes = com.google.common.collect.ImmutableMap.builder();
      this.battleaxes.putAll(battleaxes);
      return this;
    }
    public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") BuilderSingularGuavaMapsBuilder<K, V> vertex(Integer vertex$key, V vertex$value) {
      if ((this.vertices == null))
          this.vertices = com.google.common.collect.ImmutableSortedMap.naturalOrder();
      this.vertices.put(vertex$key, vertex$value);
      return this;
    }
    public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") BuilderSingularGuavaMapsBuilder<K, V> vertices(java.util.Map<? extends Integer, ? extends V> vertices) {
      if ((this.vertices == null))
          this.vertices = com.google.common.collect.ImmutableSortedMap.naturalOrder();
      this.vertices.putAll(vertices);
      return this;
    }
    public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") BuilderSingularGuavaMapsBuilder<K, V> rawMap(java.lang.Object rawMap$key, java.lang.Object rawMap$value) {
      if ((this.rawMap == null))
          this.rawMap = com.google.common.collect.ImmutableBiMap.builder();
      this.rawMap.put(rawMap$key, rawMap$value);
      return this;
    }
    public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") BuilderSingularGuavaMapsBuilder<K, V> rawMap(java.util.Map<?, ?> rawMap) {
      if ((this.rawMap == null))
          this.rawMap = com.google.common.collect.ImmutableBiMap.builder();
      this.rawMap.putAll(rawMap);
      return this;
    }
    public @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") BuilderSingularGuavaMaps<K, V> build() {
      com.google.common.collect.ImmutableMap<K, V> battleaxes = ((this.battleaxes == null) ? com.google.common.collect.ImmutableMap.<K, V>of() : this.battleaxes.build());
      com.google.common.collect.ImmutableSortedMap<Integer, V> vertices = ((this.vertices == null) ? com.google.common.collect.ImmutableSortedMap.<Integer, V>of() : this.vertices.build());
      com.google.common.collect.ImmutableBiMap<java.lang.Object, java.lang.Object> rawMap = ((this.rawMap == null) ? com.google.common.collect.ImmutableBiMap.<java.lang.Object, java.lang.Object>of() : this.rawMap.build());
      return new BuilderSingularGuavaMaps<K, V>(battleaxes, vertices, rawMap);
    }
    public @java.lang.Override @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") java.lang.String toString() {
      return (((((("BuilderSingularGuavaMaps.BuilderSingularGuavaMapsBuilder(battleaxes=" + this.battleaxes) + ", vertices=") + this.vertices) + ", rawMap=") + this.rawMap) + ")");
    }
  }
  private @Singular ImmutableMap<K, V> battleaxes;
  private @Singular ImmutableSortedMap<Integer, ? extends V> vertices;
  private @SuppressWarnings("all") @Singular("rawMap") ImmutableBiMap rawMap;
  @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") BuilderSingularGuavaMaps(final ImmutableMap<K, V> battleaxes, final ImmutableSortedMap<Integer, ? extends V> vertices, final ImmutableBiMap rawMap) {
    super();
    this.battleaxes = battleaxes;
    this.vertices = vertices;
    this.rawMap = rawMap;
  }
  public static @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") <K, V>BuilderSingularGuavaMapsBuilder<K, V> builder() {
    return new BuilderSingularGuavaMapsBuilder<K, V>();
  }
}
