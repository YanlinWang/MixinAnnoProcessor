/*
 * Copyright (C) 2012-2013 The Project Lombok Authors.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package lombok.experimental;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Generates a lot of code which fits with a class that is a representation of an immutable entity.
 * <p>
 * Equivalent to {@code @Getter @FieldDefaults(makeFinal=true, level=AccessLevel.PRIVATE) @RequiredArgsConstructor @ToString @EqualsAndHashCode}.
 * <p>
 * Complete documentation is found at <a href="https://projectlombok.org/features/experimental/Value.html">the project lombok features page for &#64;Value</a>.
 * 
 * @see lombok.Getter
 * @see Wither
 * @see lombok.RequiredArgsConstructor
 * @see lombok.ToString
 * @see lombok.EqualsAndHashCode
 * @see lombok.Data
 * @deprecated {@link lombok.Value} has been promoted to the main package, so use that one instead.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Deprecated
public @interface Value {
	/**
	 * If you specify a static constructor name, then the generated constructor will be private, and
	 * instead a static factory method is created that other classes can use to create instances.
	 * We suggest the name: "of", like so:
	 * 
	 * <pre>
	 *     public @Data(staticConstructor = "of") class Point { final int x, y; }
	 * </pre>
	 * 
	 * Default: No static constructor, instead the normal constructor is public.
	 */
	String staticConstructor() default "";
}
