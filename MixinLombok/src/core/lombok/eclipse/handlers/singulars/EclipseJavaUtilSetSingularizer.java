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
package lombok.eclipse.handlers.singulars;

import java.util.List;

import lombok.core.LombokImmutableList;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseSingularsRecipes.EclipseSingularizer;
import lombok.eclipse.handlers.EclipseSingularsRecipes.SingularData;

import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.mangosdk.spi.ProviderFor;

@ProviderFor(EclipseSingularizer.class)
public class EclipseJavaUtilSetSingularizer extends EclipseJavaUtilListSetSingularizer {
	@Override public LombokImmutableList<String> getSupportedTypes() {
		return LombokImmutableList.of("java.util.Set", "java.util.SortedSet", "java.util.NavigableSet");
	}
	
	@Override public void appendBuildCode(SingularData data, EclipseNode builderType, List<Statement> statements, char[] targetVariableName) {
		if (useGuavaInstead(builderType)) {
			guavaListSetSingularizer.appendBuildCode(data, builderType, statements, targetVariableName);
			return;
		}
		
		if (data.getTargetFqn().equals("java.util.Set")) {
			statements.addAll(createJavaUtilSetMapInitialCapacitySwitchStatements(data, builderType, false, "emptySet", "singleton", "LinkedHashSet"));
		} else {
			statements.addAll(createJavaUtilSimpleCreationAndFillStatements(data, builderType, false, true, false, true, "TreeSet"));
		}
	}
}
