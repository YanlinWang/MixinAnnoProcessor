/*
 * Copyright (C) 2010-2013 The Project Lombok Authors.
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
package lombok;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Generates a no-args constructor.
 * Will generate an error message if such a constructor cannot be written due to the existence of final fields.
 * <p>
 * Complete documentation is found at <a href="https://projectlombok.org/features/Constructor.html">the project lombok features page for &#64;Constructor</a>.
 * <p>
 * Even though it is not listed, this annotation also has the {@code onConstructor} parameter. See the full documentation for more details.
 * <p>
 * NB: Fields with constraints such as {@code @NonNull} will <em>NOT</em> be checked in a {@code @NoArgsConstructor} constructor, of course!
 * 
 * @see RequiredArgsConstructor
 * @see AllArgsConstructor
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface NoArgsConstructor {
	/**
	 * If set, the generated constructor will be private, and an additional static 'constructor'
	 * is generated with the same argument list that wraps the real constructor.
	 * 
	 * Such a static 'constructor' is primarily useful as it infers type arguments.
	 */
	String staticName() default "";
	
	/**
	 * Any annotations listed here are put on the generated constructor. The syntax for this feature is: {@code @NoArgsConstructor(onConstructor=@__({@AnnotationsGoHere}))}
	 */
	AnyAnnotation[] onConstructor() default {};
	
	/**
	 * Sets the access level of the constructor. By default, generated constructors are {@code public}.
	 */
	AccessLevel access() default lombok.AccessLevel.PUBLIC;
	
	/**
	  * Placeholder annotation to enable the placement of annotations on the generated code.
	  * @deprecated Don't use this annotation, ever - Read the documentation.
	  */
	@Deprecated
	@Retention(RetentionPolicy.SOURCE)
	@Target({})
	@interface AnyAnnotation {}
}
