## Annotation processing

Any interface annotate with @Mixin is processed in the following way:

Examining the code of the interface and the code of the implemented
interface(transitivelly) we get the following data for all the methods in the
interface:
* a) MethodName,
* b) isAbstract/isDefaultImplemented (this is possibly a non trivial point)
* c) number of parameters and their types
* d) return type
* e) interface where this method was last "overridden" (this is a non trivial
point)



### processing
```
@Mixin interface Point { ... }
interface HasColor { ... }
@Mixin interface ColoredPoint extends Point,HasColor{ }
```

When generating the new interface ColoredPoint, use the generated version of
interfaces that are annotated with `@Mixin`.

#### Generate the `of` method:

#### Generate `T withX(Tx _)` method:

* if there's no `x` field, or `Tx` doesn't match, then generate error. else:  
* implement `withX` using the `of` method.

#### Generate get method: `Tx x()`:

* generate member filed `_x`, initialized with `x`
* generate method `public Tx x() { return _x; }`

#### Generate set method: `void x(Tx x)`:

* check if exist method `Tx x()`  
* inside the inner class, generate
  ```
  pubic void x(Tx x) { this.x = x; }
  ```

