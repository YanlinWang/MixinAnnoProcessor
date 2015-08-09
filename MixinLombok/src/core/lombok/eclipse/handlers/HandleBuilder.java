/*
 * Copyright (C) 2013-2015 The Project Lombok Authors.
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
package lombok.eclipse.handlers;

import static lombok.eclipse.Eclipse.*;
import static lombok.core.handlers.HandlerUtil.*;
import static lombok.eclipse.handlers.EclipseHandlerUtil.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FalseLiteral;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.OperatorIds;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.ast.TrueLiteral;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.UnaryExpression;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.TypeIds;
import org.mangosdk.spi.ProviderFor;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.ConfigurationKeys;
import lombok.Singular;
import lombok.core.AST.Kind;
import lombok.core.handlers.HandlerUtil;
import lombok.core.AnnotationValues;
import lombok.core.HandlerPriority;
import lombok.eclipse.Eclipse;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseSingularsRecipes.EclipseSingularizer;
import lombok.eclipse.handlers.EclipseSingularsRecipes.SingularData;
import lombok.eclipse.handlers.HandleConstructor.SkipIfConstructorExists;
import lombok.experimental.NonFinal;

@ProviderFor(EclipseAnnotationHandler.class)
@HandlerPriority(-1024) //-2^10; to ensure we've picked up @FieldDefault's changes (-2048) but @Value hasn't removed itself yet (-512), so that we can error on presence of it on the builder classes.
public class HandleBuilder extends EclipseAnnotationHandler<Builder> {
	private static final char[] CLEAN_FIELD_NAME = "$lombokUnclean".toCharArray();
	private static final char[] CLEAN_METHOD_NAME = "$lombokClean".toCharArray();
	
	private static final boolean toBoolean(Object expr, boolean defaultValue) {
		if (expr == null) return defaultValue;
		if (expr instanceof FalseLiteral) return false;
		if (expr instanceof TrueLiteral) return true;
		return ((Boolean) expr).booleanValue();
	}
	
	private static class BuilderFieldData {
		TypeReference type;
		char[] name;
		SingularData singularData;
		
		List<EclipseNode> createdFields = new ArrayList<EclipseNode>();
	}
	
	@Override public void handle(AnnotationValues<Builder> annotation, Annotation ast, EclipseNode annotationNode) {
		long p = (long) ast.sourceStart << 32 | ast.sourceEnd;
		
		Builder builderInstance = annotation.getInstance();
		
		// These exist just to support the 'old' lombok.experimental.Builder, which had these properties. lombok.Builder no longer has them.
		boolean fluent = toBoolean(annotation.getActualExpression("fluent"), true);
		boolean chain = toBoolean(annotation.getActualExpression("chain"), true);
		
		String builderMethodName = builderInstance.builderMethodName();
		String buildMethodName = builderInstance.buildMethodName();
		String builderClassName = builderInstance.builderClassName();
		
		if (builderMethodName == null) builderMethodName = "builder";
		if (buildMethodName == null) builderMethodName = "build";
		if (builderClassName == null) builderClassName = "";
		
		if (!checkName("builderMethodName", builderMethodName, annotationNode)) return;
		if (!checkName("buildMethodName", buildMethodName, annotationNode)) return;
		if (!builderClassName.isEmpty()) {
			if (!checkName("builderClassName", builderClassName, annotationNode)) return;
		}
		
		EclipseNode parent = annotationNode.up();
		
		List<BuilderFieldData> builderFields = new ArrayList<BuilderFieldData>();
		TypeReference returnType;
		TypeParameter[] typeParams;
		TypeReference[] thrownExceptions;
		char[] nameOfStaticBuilderMethod;
		EclipseNode tdParent;
		
		EclipseNode fillParametersFrom = parent.get() instanceof AbstractMethodDeclaration ? parent : null;
		boolean addCleaning = false;
		
		if (parent.get() instanceof TypeDeclaration) {
			tdParent = parent;
			TypeDeclaration td = (TypeDeclaration) tdParent.get();
			
			List<EclipseNode> allFields = new ArrayList<EclipseNode>();
			@SuppressWarnings("deprecation")
			boolean valuePresent = (hasAnnotation(lombok.Value.class, parent) || hasAnnotation(lombok.experimental.Value.class, parent));
			for (EclipseNode fieldNode : HandleConstructor.findAllFields(tdParent)) {
				FieldDeclaration fd = (FieldDeclaration) fieldNode.get();
				// final fields with an initializer cannot be written to, so they can't be 'builderized'. Unfortunately presence of @Value makes
				// non-final fields final, but @Value's handler hasn't done this yet, so we have to do this math ourselves.
				// Value will only skip making a field final if it has an explicit @NonFinal annotation, so we check for that.
				if (fd.initialization != null && valuePresent && !hasAnnotation(NonFinal.class, fieldNode)) continue;
				BuilderFieldData bfd = new BuilderFieldData();
				bfd.name = removePrefixFromField(fieldNode);
				bfd.type = fd.type;
				bfd.singularData = getSingularData(fieldNode, ast);
				builderFields.add(bfd);
				allFields.add(fieldNode);
			}
			
			new HandleConstructor().generateConstructor(tdParent, AccessLevel.PACKAGE, allFields, null, SkipIfConstructorExists.I_AM_BUILDER, null,
					Collections.<Annotation>emptyList(), annotationNode);
			
			returnType = namePlusTypeParamsToTypeReference(td.name, td.typeParameters, p);
			typeParams = td.typeParameters;
			thrownExceptions = null;
			nameOfStaticBuilderMethod = null;
			if (builderClassName.isEmpty()) builderClassName = new String(td.name) + "Builder";
		} else if (parent.get() instanceof ConstructorDeclaration) {
			ConstructorDeclaration cd = (ConstructorDeclaration) parent.get();
			if (cd.typeParameters != null && cd.typeParameters.length > 0) {
				annotationNode.addError("@Builder is not supported on constructors with constructor type parameters.");
				return;
			}
			
			tdParent = parent.up();
			TypeDeclaration td = (TypeDeclaration) tdParent.get();
			returnType = namePlusTypeParamsToTypeReference(td.name, td.typeParameters, p);
			typeParams = td.typeParameters;
			thrownExceptions = cd.thrownExceptions;
			nameOfStaticBuilderMethod = null;
			if (builderClassName.isEmpty()) builderClassName = new String(cd.selector) + "Builder";
		} else if (parent.get() instanceof MethodDeclaration) {
			MethodDeclaration md = (MethodDeclaration) parent.get();
			tdParent = parent.up();
			if (!md.isStatic()) {
				annotationNode.addError("@Builder is only supported on types, constructors, and static methods.");
				return;
			}
			returnType = copyType(md.returnType, ast);
			typeParams = md.typeParameters;
			thrownExceptions = md.thrownExceptions;
			nameOfStaticBuilderMethod = md.selector;
			if (builderClassName.isEmpty()) {
				char[] token;
				if (md.returnType instanceof QualifiedTypeReference) {
					char[][] tokens = ((QualifiedTypeReference) md.returnType).tokens;
					token = tokens[tokens.length - 1];
				} else if (md.returnType instanceof SingleTypeReference) {
					token = ((SingleTypeReference) md.returnType).token;
					if (!(md.returnType instanceof ParameterizedSingleTypeReference) && typeParams != null) {
						for (TypeParameter tp : typeParams) {
							if (Arrays.equals(tp.name, token)) {
								annotationNode.addError("@Builder requires specifying 'builderClassName' if used on methods with a type parameter as return type.");
								return;
							}
						}
					}
				} else {
					annotationNode.addError("Unexpected kind of return type on annotated method. Specify 'builderClassName' to solve this problem.");
					return;
				}
				
				if (Character.isLowerCase(token[0])) {
					char[] newToken = new char[token.length];
					System.arraycopy(token, 1, newToken, 1, token.length - 1);
					newToken[0] = Character.toTitleCase(token[0]);
					token = newToken;
				}
				
				builderClassName = new String(token) + "Builder";
			}
		} else {
			annotationNode.addError("@Builder is only supported on types, constructors, and static methods.");
			return;
		}
		
		if (fillParametersFrom != null) {
			for (EclipseNode param : fillParametersFrom.down()) {
				if (param.getKind() != Kind.ARGUMENT) continue;
				BuilderFieldData bfd = new BuilderFieldData();
				Argument arg = (Argument) param.get();
				bfd.name = arg.name;
				bfd.type = arg.type;
				bfd.singularData = getSingularData(param, ast);
				builderFields.add(bfd);
			}
		}
		
		EclipseNode builderType = findInnerClass(tdParent, builderClassName);
		if (builderType == null) {
			builderType = makeBuilderClass(tdParent, builderClassName, typeParams, ast);
		} else {
			sanityCheckForMethodGeneratingAnnotationsOnBuilderClass(builderType, annotationNode);
			/* generate errors for @Singular BFDs that have one already defined node. */ {
				for (BuilderFieldData bfd : builderFields) {
					SingularData sd = bfd.singularData;
					if (sd == null) continue;
					EclipseSingularizer singularizer = sd.getSingularizer();
					if (singularizer == null) continue;
					if (singularizer.checkForAlreadyExistingNodesAndGenerateError(builderType, sd)) {
						bfd.singularData = null;
					}
				}
			}
		}
		
		for (BuilderFieldData bfd : builderFields) {
			if (bfd.singularData != null && bfd.singularData.getSingularizer() != null) {
				if (bfd.singularData.getSingularizer().requiresCleaning()) {
					addCleaning = true;
					break;
				}
			}
		}
		
		generateBuilderFields(builderType, builderFields, ast);
		if (addCleaning) {
			FieldDeclaration cleanDecl = new FieldDeclaration(CLEAN_FIELD_NAME, 0, -1);
			cleanDecl.declarationSourceEnd = -1;
			cleanDecl.modifiers = ClassFileConstants.AccPrivate;
			cleanDecl.type = TypeReference.baseTypeReference(TypeIds.T_boolean, 0);
			System.out.println("INJECTING: cleaning");
			injectFieldAndMarkGenerated(builderType, cleanDecl);
		}
		
		if (constructorExists(builderType) == MemberExistsResult.NOT_EXISTS) {
			ConstructorDeclaration cd = HandleConstructor.createConstructor(
					AccessLevel.PACKAGE, builderType, Collections.<EclipseNode>emptyList(), null,
					annotationNode, Collections.<Annotation>emptyList());
			if (cd != null) injectMethod(builderType, cd);
		}
		
		for (BuilderFieldData bfd : builderFields) {
			makeSetterMethodsForBuilder(builderType, bfd, annotationNode, fluent, chain);
		}
		
		if (methodExists(buildMethodName, builderType, -1) == MemberExistsResult.NOT_EXISTS) {
			MethodDeclaration md = generateBuildMethod(buildMethodName, nameOfStaticBuilderMethod, returnType, builderFields, builderType, thrownExceptions, addCleaning, ast);
			if (md != null) injectMethod(builderType, md);
		}
		
		if (methodExists("toString", builderType, 0) == MemberExistsResult.NOT_EXISTS) {
			List<EclipseNode> fieldNodes = new ArrayList<EclipseNode>();
			for (BuilderFieldData bfd : builderFields) {
				fieldNodes.addAll(bfd.createdFields);
			}
			MethodDeclaration md = HandleToString.createToString(builderType, fieldNodes, true, false, ast, FieldAccess.ALWAYS_FIELD);
			if (md != null) injectMethod(builderType, md);
		}
		
		if (addCleaning) {
			MethodDeclaration cleanMethod = generateCleanMethod(builderFields, builderType, ast);
			if (cleanMethod != null) injectMethod(builderType, cleanMethod);
		}
		
		if (methodExists(builderMethodName, tdParent, -1) == MemberExistsResult.NOT_EXISTS) {
			MethodDeclaration md = generateBuilderMethod(builderMethodName, builderClassName, tdParent, typeParams, ast);
			if (md != null) injectMethod(tdParent, md);
		}
	}
	
	private MethodDeclaration generateCleanMethod(List<BuilderFieldData> builderFields, EclipseNode builderType, ASTNode source) {
		List<Statement> statements = new ArrayList<Statement>();
		
		for (BuilderFieldData bfd : builderFields) {
			if (bfd.singularData != null && bfd.singularData.getSingularizer() != null) {
				bfd.singularData.getSingularizer().appendCleaningCode(bfd.singularData, builderType, statements);
			}
		}
		
		FieldReference thisUnclean = new FieldReference(CLEAN_FIELD_NAME, 0);
		thisUnclean.receiver = new ThisReference(0, 0);
		statements.add(new Assignment(thisUnclean, new FalseLiteral(0, 0), 0));
		MethodDeclaration decl = new MethodDeclaration(((CompilationUnitDeclaration) builderType.top().get()).compilationResult);
		decl.selector = CLEAN_METHOD_NAME;
		decl.modifiers = ClassFileConstants.AccPrivate;
		decl.bits |= ECLIPSE_DO_NOT_TOUCH_FLAG;
		decl.returnType = TypeReference.baseTypeReference(TypeIds.T_void, 0);
		decl.statements = statements.toArray(new Statement[0]);
		decl.traverse(new SetGeneratedByVisitor(source), (ClassScope) null);
		return decl;
	}
	
	public MethodDeclaration generateBuildMethod(String name, char[] staticName, TypeReference returnType, List<BuilderFieldData> builderFields, EclipseNode type, TypeReference[] thrownExceptions, boolean addCleaning, ASTNode source) {
		MethodDeclaration out = new MethodDeclaration(
				((CompilationUnitDeclaration) type.top().get()).compilationResult);
		out.bits |= ECLIPSE_DO_NOT_TOUCH_FLAG;
		List<Statement> statements = new ArrayList<Statement>();
		
		if (addCleaning) {
			FieldReference thisUnclean = new FieldReference(CLEAN_FIELD_NAME, 0);
			thisUnclean.receiver = new ThisReference(0, 0);
			Expression notClean = new UnaryExpression(thisUnclean, OperatorIds.NOT);
			MessageSend invokeClean = new MessageSend();
			invokeClean.selector = CLEAN_METHOD_NAME;
			statements.add(new IfStatement(notClean, invokeClean, 0, 0));
		}
		
		for (BuilderFieldData bfd : builderFields) {
			if (bfd.singularData != null && bfd.singularData.getSingularizer() != null) {
				bfd.singularData.getSingularizer().appendBuildCode(bfd.singularData, type, statements, bfd.name);
			}
		}
		
		List<Expression> args = new ArrayList<Expression>();
		for (BuilderFieldData bfd : builderFields) {
			args.add(new SingleNameReference(bfd.name, 0L));
		}
		
		if (addCleaning) {
			FieldReference thisUnclean = new FieldReference(CLEAN_FIELD_NAME, 0);
			thisUnclean.receiver = new ThisReference(0, 0);
			statements.add(new Assignment(thisUnclean, new TrueLiteral(0, 0), 0));
		}
		
		out.modifiers = ClassFileConstants.AccPublic;
		out.selector = name.toCharArray();
		out.thrownExceptions = copyTypes(thrownExceptions);
		out.bits |= ECLIPSE_DO_NOT_TOUCH_FLAG;
		out.returnType = returnType;
		
		if (staticName == null) {
			AllocationExpression allocationStatement = new AllocationExpression();
			allocationStatement.type = copyType(out.returnType);
			allocationStatement.arguments = args.isEmpty() ? null : args.toArray(new Expression[args.size()]);
			statements.add(new ReturnStatement(allocationStatement, 0, 0));
		} else {
			MessageSend invoke = new MessageSend();
			invoke.selector = staticName;
			invoke.receiver = new SingleNameReference(type.up().getName().toCharArray(), 0);
			TypeParameter[] tps = ((TypeDeclaration) type.get()).typeParameters;
			if (tps != null) {
				TypeReference[] trs = new TypeReference[tps.length];
				for (int i = 0; i < trs.length; i++) {
					trs[i] = new SingleTypeReference(tps[i].name, 0);
				}
				invoke.typeArguments = trs;
			}
			invoke.arguments = args.isEmpty() ? null : args.toArray(new Expression[args.size()]);
			if (returnType instanceof SingleTypeReference && Arrays.equals(TypeConstants.VOID, ((SingleTypeReference) returnType).token)) {
				statements.add(invoke);
			} else {
				statements.add(new ReturnStatement(invoke, 0, 0));
			}
		}
		out.statements = statements.isEmpty() ? null : statements.toArray(new Statement[statements.size()]);
		out.traverse(new SetGeneratedByVisitor(source), (ClassScope) null);
		return out;
	}
	
	public MethodDeclaration generateBuilderMethod(String builderMethodName, String builderClassName, EclipseNode type, TypeParameter[] typeParams, ASTNode source) {
		int pS = source.sourceStart, pE = source.sourceEnd;
		long p = (long) pS << 32 | pE;
		
		MethodDeclaration out = new MethodDeclaration(
				((CompilationUnitDeclaration) type.top().get()).compilationResult);
		out.selector = builderMethodName.toCharArray();
		out.modifiers = ClassFileConstants.AccPublic | ClassFileConstants.AccStatic;
		out.bits |= ECLIPSE_DO_NOT_TOUCH_FLAG;
		out.returnType = namePlusTypeParamsToTypeReference(builderClassName.toCharArray(), typeParams, p);
		out.typeParameters = copyTypeParams(typeParams, source);
		AllocationExpression invoke = new AllocationExpression();
		invoke.type = namePlusTypeParamsToTypeReference(builderClassName.toCharArray(), typeParams, p);
		out.statements = new Statement[] {new ReturnStatement(invoke, pS, pE)};
		
		out.traverse(new SetGeneratedByVisitor(source), ((TypeDeclaration) type.get()).scope);
		return out;
	}
	
	public void generateBuilderFields(EclipseNode builderType, List<BuilderFieldData> builderFields, ASTNode source) {
		List<EclipseNode> existing = new ArrayList<EclipseNode>();
		for (EclipseNode child : builderType.down()) {
			if (child.getKind() == Kind.FIELD) existing.add(child);
		}
		
		top:
		for (BuilderFieldData bfd : builderFields) {
			if (bfd.singularData != null && bfd.singularData.getSingularizer() != null) {
				bfd.createdFields.addAll(bfd.singularData.getSingularizer().generateFields(bfd.singularData, builderType));
			} else {
				for (EclipseNode exists : existing) {
					char[] n = ((FieldDeclaration) exists.get()).name;
					if (Arrays.equals(n, bfd.name)) {
						bfd.createdFields.add(exists);
						continue top;
					}
				}
				
				FieldDeclaration fd = new FieldDeclaration(bfd.name, 0, 0);
				fd.bits |= Eclipse.ECLIPSE_DO_NOT_TOUCH_FLAG;
				fd.modifiers = ClassFileConstants.AccPrivate;
				fd.type = copyType(bfd.type);
				fd.traverse(new SetGeneratedByVisitor(source), (MethodScope) null);
				bfd.createdFields.add(injectFieldAndMarkGenerated(builderType, fd));
			}
		}
	}
	
	private static final AbstractMethodDeclaration[] EMPTY = {};
	
	public void makeSetterMethodsForBuilder(EclipseNode builderType, BuilderFieldData bfd, EclipseNode sourceNode, boolean fluent, boolean chain) {
		if (bfd.singularData == null || bfd.singularData.getSingularizer() == null) {
			makeSimpleSetterMethodForBuilder(builderType, bfd.createdFields.get(0), sourceNode, fluent, chain);
		} else {
			bfd.singularData.getSingularizer().generateMethods(bfd.singularData, builderType, fluent, chain);
		}
	}
	
	private void makeSimpleSetterMethodForBuilder(EclipseNode builderType, EclipseNode fieldNode, EclipseNode sourceNode, boolean fluent, boolean chain) {
		TypeDeclaration td = (TypeDeclaration) builderType.get();
		AbstractMethodDeclaration[] existing = td.methods;
		if (existing == null) existing = EMPTY;
		int len = existing.length;
		FieldDeclaration fd = (FieldDeclaration) fieldNode.get();
		char[] name = fd.name;
		
		for (int i = 0; i < len; i++) {
			if (!(existing[i] instanceof MethodDeclaration)) continue;
			char[] existingName = existing[i].selector;
			if (Arrays.equals(name, existingName)) return;
		}
		
		String setterName = fluent ? fieldNode.getName() : HandlerUtil.buildAccessorName("set", fieldNode.getName());
		
		MethodDeclaration setter = HandleSetter.createSetter(td, fieldNode, setterName, chain, ClassFileConstants.AccPublic,
				sourceNode, Collections.<Annotation>emptyList(), Collections.<Annotation>emptyList());
		injectMethod(builderType, setter);
	}
	
	public EclipseNode findInnerClass(EclipseNode parent, String name) {
		char[] c = name.toCharArray();
		for (EclipseNode child : parent.down()) {
			if (child.getKind() != Kind.TYPE) continue;
			TypeDeclaration td = (TypeDeclaration) child.get();
			if (Arrays.equals(td.name, c)) return child;
		}
		return null;
	}
	
	public EclipseNode makeBuilderClass(EclipseNode tdParent, String builderClassName, TypeParameter[] typeParams, ASTNode source) {
		TypeDeclaration parent = (TypeDeclaration) tdParent.get();
		TypeDeclaration builder = new TypeDeclaration(parent.compilationResult);
		builder.bits |= Eclipse.ECLIPSE_DO_NOT_TOUCH_FLAG;
		builder.modifiers |= ClassFileConstants.AccPublic | ClassFileConstants.AccStatic;
		builder.typeParameters = copyTypeParams(typeParams, source);
		builder.name = builderClassName.toCharArray();
		builder.traverse(new SetGeneratedByVisitor(source), (ClassScope) null);
		return injectType(tdParent, builder);
	}
	
	/**
	 * Returns the explicitly requested singular annotation on this node (field
	 * or parameter), or null if there's no {@code @Singular} annotation on it.
	 * 
	 * @param node The node (field or method param) to inspect for its name and potential {@code @Singular} annotation.
	 */
	private SingularData getSingularData(EclipseNode node, ASTNode source) {
		for (EclipseNode child : node.down()) {
			if (child.getKind() == Kind.ANNOTATION && annotationTypeMatches(Singular.class, child)) {
				char[] pluralName = node.getKind() == Kind.FIELD ? removePrefixFromField(node) : ((AbstractVariableDeclaration) node.get()).name;
				AnnotationValues<Singular> ann = createAnnotation(Singular.class, child);
				String explicitSingular = ann.getInstance().value();
				if (explicitSingular.isEmpty()) {
					if (Boolean.FALSE.equals(node.getAst().readConfiguration(ConfigurationKeys.SINGULAR_AUTO))) {
						node.addError("The singular must be specified explicitly (e.g. @Singular(\"task\")) because auto singularization is disabled.");
						explicitSingular = new String(pluralName);
					} else {
						explicitSingular = autoSingularize(node.getName());
						if (explicitSingular == null) {
							node.addError("Can't singularize this name; please specify the singular explicitly (i.e. @Singular(\"sheep\"))");
							explicitSingular = new String(pluralName);
						}
					}
				}
				char[] singularName = explicitSingular.toCharArray();
				
				TypeReference type = ((AbstractVariableDeclaration) node.get()).type;
				TypeReference[] typeArgs = null;
				String typeName;
				if (type instanceof ParameterizedSingleTypeReference) {
					typeArgs = ((ParameterizedSingleTypeReference) type).typeArguments;
					typeName = new String(((ParameterizedSingleTypeReference) type).token);
				} else if (type instanceof ParameterizedQualifiedTypeReference) {
					TypeReference[][] tr = ((ParameterizedQualifiedTypeReference) type).typeArguments;
					if (tr != null) typeArgs = tr[tr.length - 1];
					char[][] tokens = ((ParameterizedQualifiedTypeReference) type).tokens;
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < tokens.length; i++) {
						if (i > 0) sb.append(".");
						sb.append(tokens[i]);
					}
					typeName = sb.toString();
				} else {
					typeName = type.toString();
				}
				
				String targetFqn = EclipseSingularsRecipes.get().toQualified(typeName);
				EclipseSingularizer singularizer = EclipseSingularsRecipes.get().getSingularizer(targetFqn);
				if (singularizer == null) {
					node.addError("Lombok does not know how to create the singular-form builder methods for type '" + typeName + "'; they won't be generated.");
					return null;
				}
				
				return new SingularData(child, singularName, pluralName, typeArgs == null ? Collections.<TypeReference>emptyList() : Arrays.asList(typeArgs), targetFqn, singularizer, source);
			}
		}
		
		return null;
	}
}
