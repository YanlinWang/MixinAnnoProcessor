/*
 * Copyright (C) 2013-2014 The Project Lombok Authors.
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

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The builder annotation creates a so-called 'builder' aspect to the class that is annotated or the class
 * that contains a member which is annotated with {@code @Builder}.
 * <p>
 * If a member is annotated, it must be either a constructor or a method. If a class is annotated,
 * then a private constructor is generated with all fields as arguments
 * (as if {@code @AllArgsConstructor(AccessLevel.PRIVATE)} is present
 * on the class), and it is as if this constructor has been annotated with {@code @Builder} instead.
 * <p>
 * The effect of {@code @Builder} is that an inner class is generated named <code><strong>T</strong>Builder</code>,
 * with a private constructor. Instances of <code><strong>T</strong>Builder</code> are made with the
 * method named {@code builder()} which is also generated for you in the class itself (not in the builder class).
 * <p>
 * The <code><strong>T</strong>Builder</code> class contains 1 method for each parameter of the annotated
 * constructor / method (each field, when annotating a class), which returns the builder itself.
 * The builder also has a <code>build()</code> method which returns a completed instance of the original type,
 * created by passing all parameters as set via the various other methods in the builder to the constructor
 * or method that was annotated with {@code @Builder}. The return type of this method will be the same
 * as the relevant class, unless a method has been annotated, in which case it'll be equal to the
 * return type of that method.
 * <p>
 * Complete documentation is found at <a href="https://projectlombok.org/features/experimental/Builder.html">the project lombok features page for &#64;Builder</a>.
 * <p>
 * <p>
 * Before:
 * 
 * <pre>
 * &#064;Builder
 * class Example {
 * 	private int foo;
 * 	private final String bar;
 * }
 * </pre>
 * 
 * After:
 * 
 * <pre>
 * class Example&lt;T&gt; {
 * 	private T foo;
 * 	private final String bar;
 * 	
 * 	private Example(T foo, String bar) {
 * 		this.foo = foo;
 * 		this.bar = bar;
 * 	}
 * 	
 * 	public static &lt;T&gt; ExampleBuilder&lt;T&gt; builder() {
 * 		return new ExampleBuilder&lt;T&gt;();
 * 	}
 * 	
 * 	public static class ExampleBuilder&lt;T&gt; {
 * 		private T foo;
 * 		private String bar;
 * 		
 * 		private ExampleBuilder() {}
 * 		
 * 		public ExampleBuilder foo(T foo) {
 * 			this.foo = foo;
 * 			return this;
 * 		}
 * 		
 * 		public ExampleBuilder bar(String bar) {
 * 			this.bar = bar;
 * 			return this;
 * 		}
 * 		
 * 		&#064;java.lang.Override public String toString() {
 * 			return "ExampleBuilder(foo = " + foo + ", bar = " + bar + ")";
 * 		}
 * 		
 * 		public Example build() {
 * 			return new Example(foo, bar);
 * 		}
 * 	}
 * }
 * </pre>
 */
@Target({TYPE, METHOD, CONSTRUCTOR})
@Retention(SOURCE)
public @interface Builder {
	/** Name of the method that creates a new builder instance. Default: {@code builder}. */
	String builderMethodName() default "builder";
	
	/** Name of the method in the builder class that creates an instance of your {@code @Builder}-annotated class. */
	String buildMethodName() default "build";
	
	/**
	 * Name of the builder class.
	 * 
	 * Default for {@code @Builder} on types and constructors: {@code (TypeName)Builder}.
	 * <p>
	 * Default for {@code @Builder} on methods: {@code (ReturnTypeName)Builder}.
	 */
	String builderClassName() default "";
	
	/**
	 * If true, generate an instance method to obtain a builder that is initialized with the values of this instance.
	 * Legal only if {@code @Builder} is used on a constructor, on the type itself, or on a static method that returns
	 * an instance of the declaring type.
	 */
	boolean toBuilder() default false;
	
	/**
	 * Put on a field (in case of {@code @Builder} on a type) or a parameter (for {@code @Builder} on a constructor or static method) to
	 * indicate how lombok should obtain a value for this field or parameter given an instance; this is only relevant if {@code toBuilder} is {@code true}.
	 * 
	 * You do not need to supply an {@code @ObtainVia} annotation unless you wish to change the default behaviour: Use a field with the same name.
	 * <p>
	 * Note that one of {@code field} or {@code method} should be set, or an error is generated.
	 * <p>
	 * The default behaviour is to obtain a value by referencing the name of the parameter as a field on 'this'.
	 */
	public @interface ObtainVia {
		/** Tells lombok to obtain a value with the expression {@code this.value}. */
		String field() default "";
		
		/** Tells lombok to obtain a value with the expression {@code this.method()}. */
		String method() default "";
		
		/** Tells lombok to obtain a value with the expression {@code SelfType.method(this)}; requires {@code method} to be set. */
		boolean isStatic() default false;
	}
}
