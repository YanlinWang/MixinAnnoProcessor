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

import static lombok.javac.Javac.*;
import static lombok.javac.handlers.JavacHandlerUtil.*;

import org.mangosdk.spi.ProviderFor;

import lombok.core.LombokImmutableList;
import lombok.javac.JavacNode;
import lombok.javac.JavacTreeMaker;
import lombok.javac.handlers.JavacSingularsRecipes.JavacSingularizer;
import lombok.javac.handlers.JavacSingularsRecipes.SingularData;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCCase;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCStatement;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;

@ProviderFor(JavacSingularizer.class)
public class JavacJavaUtilListSingularizer extends JavacJavaUtilListSetSingularizer {
	@Override public LombokImmutableList<String> getSupportedTypes() {
		return LombokImmutableList.of("java.util.List", "java.util.Collection", "java.lang.Iterable");
	}
	
	@Override public void appendBuildCode(SingularData data, JavacNode builderType, JCTree source, ListBuffer<JCStatement> statements, Name targetVariableName) {
		if (useGuavaInstead(builderType)) {
			guavaListSetSingularizer.appendBuildCode(data, builderType, source, statements, targetVariableName);
			return;
		}
		
		JavacTreeMaker maker = builderType.getTreeMaker();
		List<JCExpression> jceBlank = List.nil();
		ListBuffer<JCCase> cases = new ListBuffer<JCCase>();
		
		/* case 0: (empty); break; */ {
			JCStatement assignStat; {
				// pluralName = java.util.Collections.emptyList();
				JCExpression invoke = maker.Apply(jceBlank, chainDots(builderType, "java", "util", "Collections", "emptyList"), jceBlank);
				assignStat = maker.Exec(maker.Assign(maker.Ident(data.getPluralName()), invoke));
			}
			JCStatement breakStat = maker.Break(null);
			JCCase emptyCase = maker.Case(maker.Literal(CTC_INT, 0), List.of(assignStat, breakStat));
			cases.append(emptyCase);
		}
		
		/* case 1: (singletonList); break; */ {
			JCStatement assignStat; {
				// pluralName = java.util.Collections.singletonList(this.pluralName.get(0));
				JCExpression zeroLiteral = maker.Literal(CTC_INT, 0);
				JCExpression arg = maker.Apply(jceBlank, chainDots(builderType, "this", data.getPluralName().toString(), "get"), List.of(zeroLiteral));
				List<JCExpression> args = List.of(arg);
				JCExpression invoke = maker.Apply(jceBlank, chainDots(builderType, "java", "util", "Collections", "singletonList"), args);
				assignStat = maker.Exec(maker.Assign(maker.Ident(data.getPluralName()), invoke));
			}
			JCStatement breakStat = maker.Break(null);
			JCCase singletonCase = maker.Case(maker.Literal(CTC_INT, 1), List.of(assignStat, breakStat));
			cases.append(singletonCase);
		}
		
		/* default: Create with right size, then addAll */ {
			List<JCStatement> defStats = createListCopy(maker, data, builderType, source);
			JCCase defaultCase = maker.Case(null, defStats);
			cases.append(defaultCase);
		}
		
		JCStatement switchStat = maker.Switch(getSize(maker,  builderType, data.getPluralName(), true), cases.toList());
		JCExpression localShadowerType = chainDotsString(builderType, data.getTargetFqn());
		localShadowerType = addTypeArgs(1, false, builderType, localShadowerType, data.getTypeArgs(), source);
		JCStatement varDefStat = maker.VarDef(maker.Modifiers(0), data.getPluralName(), localShadowerType, null);
		statements.append(varDefStat);
		statements.append(switchStat);
	}
	
	private List<JCStatement> createListCopy(JavacTreeMaker maker, SingularData data, JavacNode builderType, JCTree source) {
		List<JCExpression> jceBlank = List.nil();
		Name thisName = builderType.toName("this");
		
		JCExpression argToUnmodifiable; {
			 // new java.util.ArrayList<Generics>(this.pluralName);
			List<JCExpression> constructorArgs = List.nil();
			JCExpression thisDotPluralName = maker.Select(maker.Ident(thisName), data.getPluralName());
			constructorArgs = List.<JCExpression>of(thisDotPluralName);
			JCExpression targetTypeExpr = chainDots(builderType, "java", "util", "ArrayList");
			targetTypeExpr = addTypeArgs(1, false, builderType, targetTypeExpr, data.getTypeArgs(), source);
			argToUnmodifiable = maker.NewClass(null, jceBlank, targetTypeExpr, constructorArgs, null);
		}
		
		JCStatement unmodifiableStat; {
			// pluralname = Collections.unmodifiableInterfaceType(-newlist-);
			JCExpression invoke = maker.Apply(jceBlank, chainDots(builderType, "java", "util", "Collections", "unmodifiableList"), List.of(argToUnmodifiable));
			unmodifiableStat = maker.Exec(maker.Assign(maker.Ident(data.getPluralName()), invoke));
		}
		
		return List.of(unmodifiableStat);
	}
}
