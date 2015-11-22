/*
 * Copyright (C) 2015 The Project Lombok Authors.
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
package lombok.javac.handlers.singulars;

import org.mangosdk.spi.ProviderFor;

import lombok.core.LombokImmutableList;
import lombok.javac.handlers.JavacSingularsRecipes.JavacSingularizer;

@ProviderFor(JavacSingularizer.class)
public class JavacGuavaTableSingularizer extends JavacGuavaSingularizer {
	private static final LombokImmutableList<String> SUFFIXES =
		LombokImmutableList.of("rowKey", "columnKey", "value");
	private static final LombokImmutableList<String> SUPPORTED_TYPES =
		LombokImmutableList.of("com.google.common.collect.ImmutableTable");
	
	@Override public LombokImmutableList<String> getSupportedTypes() {
		return SUPPORTED_TYPES;
	}
	
	@Override protected LombokImmutableList<String> getArgumentSuffixes() {
		return SUFFIXES;
	}
	
	@Override protected String getAddMethodName() {
		return "put";
	}
	
	@Override protected String getAddAllTypeName() {
		return "com.google.common.collect.Table";
	}
}
