/*
 * Copyright (C) 2012-2014 The Project Lombok Authors.
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
package lombok.eclipse.agent;

import static lombok.eclipse.handlers.EclipseHandlerUtil.createAnnotation;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.core.AST.Kind;
import lombok.core.AnnotationValues;
import lombok.core.AnnotationValues.AnnotationValueDecodeFail;
import lombok.core.FieldAugment;
import lombok.eclipse.EclipseAST;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.TransformEclipseAST;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import lombok.experimental.ExtensionMethod;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.NameReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.SuperReference;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeIds;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;

public class PatchExtensionMethod {
	static class Extension {
		List<MethodBinding> extensionMethods;
		boolean suppressBaseMethods;
	}
	
	private static class PostponedNoMethodError implements PostponedError {
		private final ProblemReporter problemReporter;
		private final WeakReference<MessageSend> messageSendRef;
		private final TypeBinding recType;
		private final TypeBinding[] params;
		
		PostponedNoMethodError(ProblemReporter problemReporter, MessageSend messageSend, TypeBinding recType, TypeBinding[] params) {
			this.problemReporter = problemReporter;
			this.messageSendRef = new WeakReference<MessageSend>(messageSend);
			this.recType = recType;
			this.params = params;
		}
		
		public void fire() {
			MessageSend messageSend = messageSendRef.get();
			if (messageSend != null) problemReporter.errorNoMethodFor(messageSend, recType, params);
		}
	}
	
	private static class PostponedInvalidMethodError implements PostponedError {
		private final ProblemReporter problemReporter;
		private final WeakReference<MessageSend> messageSendRef;
		private final MethodBinding method;
		private final Scope scope;
		
		private static final Method shortMethod = getMethod("invalidMethod", MessageSend.class, MethodBinding.class);
		private static final Method longMethod = getMethod("invalidMethod", MessageSend.class, MethodBinding.class, Scope.class);
		
		private static Method getMethod(String name, Class<?>... types) {
			try {
				Method m = ProblemReporter.class.getMethod(name, types);
				m.setAccessible(true);
				return m;
			} catch (Exception e) {
				return null;
			}
		}
		
		PostponedInvalidMethodError(ProblemReporter problemReporter, MessageSend messageSend, MethodBinding method, Scope scope) {
			this.problemReporter = problemReporter;
			this.messageSendRef = new WeakReference<MessageSend>(messageSend);
			this.method = method;
			this.scope = scope;
		}
		
		static void invoke(ProblemReporter problemReporter, MessageSend messageSend, MethodBinding method, Scope scope) {
			if (messageSend != null) {
				try {
					if (shortMethod != null) shortMethod.invoke(problemReporter, messageSend, method);
					else if (longMethod != null) longMethod.invoke(problemReporter, messageSend, method, scope);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				} catch (InvocationTargetException e) {
					Throwable t = e.getCause();
					if (t instanceof Error) throw (Error) t;
					if (t instanceof RuntimeException) throw (RuntimeException) t;
					throw new RuntimeException(t);
				}
			}
		}
		
		public void fire() {
			MessageSend messageSend = messageSendRef.get();
			invoke(problemReporter, messageSend, method, scope);
		}
	}
	
	private static interface PostponedError {
		public void fire();
	}
	
	public static EclipseNode getTypeNode(TypeDeclaration decl) {
		CompilationUnitDeclaration cud = decl.scope.compilationUnitScope().referenceContext;
		EclipseAST astNode = TransformEclipseAST.getAST(cud, false);
		EclipseNode node = astNode.get(decl);
		if (node == null) {
			astNode = TransformEclipseAST.getAST(cud, true);
			node = astNode.get(decl);
		}
		return node;
	}
	
	public static Annotation getAnnotation(Class<? extends java.lang.annotation.Annotation> expectedType, EclipseNode node) {
		TypeDeclaration decl = (TypeDeclaration) node.get();
		if (decl.annotations != null) for (Annotation ann : decl.annotations) {
			if (EclipseHandlerUtil.typeMatches(expectedType, node, ann.type)) return ann;
		}
		return null;
	}
	
	static EclipseNode upToType(EclipseNode typeNode) {
		EclipseNode node = typeNode;
		do {
			node = node.up();
		} while ((node != null) && (node.getKind() != Kind.TYPE));
		return node;
	}
	
	static List<Extension> getApplicableExtensionMethods(EclipseNode typeNode, Annotation ann, TypeBinding receiverType) {
		List<Extension> extensions = new ArrayList<Extension>();
		if ((typeNode != null) && (ann != null) && (receiverType != null)) {
			BlockScope blockScope = ((TypeDeclaration) typeNode.get()).initializerScope;
			EclipseNode annotationNode = typeNode.getNodeFor(ann);
			AnnotationValues<ExtensionMethod> annotation = createAnnotation(ExtensionMethod.class, annotationNode);
			boolean suppressBaseMethods = false;
			try {
				suppressBaseMethods = annotation.getInstance().suppressBaseMethods();
			} catch (AnnotationValueDecodeFail fail) {
				fail.owner.setError(fail.getMessage(), fail.idx);
			}
			for (Object extensionMethodProvider : annotation.getActualExpressions("value")) {
				if (extensionMethodProvider instanceof ClassLiteralAccess) {
					TypeBinding binding = ((ClassLiteralAccess) extensionMethodProvider).type.resolveType(blockScope);
					if (binding == null) continue;
					if (!binding.isClass() && !binding.isEnum()) continue;
					Extension e = new Extension();
					e.extensionMethods = getApplicableExtensionMethodsDefinedInProvider(typeNode, (ReferenceBinding) binding, receiverType);
					e.suppressBaseMethods = suppressBaseMethods;
					extensions.add(e);
				}
			}
		}
		return extensions;
	}
	
	private static List<MethodBinding> getApplicableExtensionMethodsDefinedInProvider(EclipseNode typeNode, ReferenceBinding extensionMethodProviderBinding,
			TypeBinding receiverType) {
		
		List<MethodBinding> extensionMethods = new ArrayList<MethodBinding>();
		CompilationUnitScope cuScope = ((CompilationUnitDeclaration) typeNode.top().get()).scope;
		for (MethodBinding method : extensionMethodProviderBinding.methods()) {
			if (!method.isStatic()) continue;
			if (!method.isPublic()) continue;
			if (method.parameters == null || method.parameters.length == 0) continue;
			TypeBinding firstArgType = method.parameters[0];
			if (receiverType.isProvablyDistinct(firstArgType) && !receiverType.isCompatibleWith(firstArgType.erasure())) continue;
			TypeBinding[] argumentTypes = Arrays.copyOfRange(method.parameters, 1, method.parameters.length);
			if ((receiverType instanceof ReferenceBinding) && ((ReferenceBinding) receiverType).getExactMethod(method.selector, argumentTypes, cuScope) != null) continue;
			extensionMethods.add(method);
		}
		return extensionMethods;
	}
	
	private static final FieldAugment<MessageSend, PostponedError> MessageSend_postponedErrors = FieldAugment.augment(MessageSend.class, PostponedError.class, "lombok$postponedErrors");
	
	public static void errorNoMethodFor(ProblemReporter problemReporter, MessageSend messageSend, TypeBinding recType, TypeBinding[] params) {
		MessageSend_postponedErrors.set(messageSend, new PostponedNoMethodError(problemReporter, messageSend, recType, params));
	}
	
	public static void invalidMethod(ProblemReporter problemReporter, MessageSend messageSend, MethodBinding method) {
		MessageSend_postponedErrors.set(messageSend, new PostponedInvalidMethodError(problemReporter, messageSend, method, null));
	}
	
	public static void invalidMethod(ProblemReporter problemReporter, MessageSend messageSend, MethodBinding method, Scope scope) {
		MessageSend_postponedErrors.set(messageSend, new PostponedInvalidMethodError(problemReporter, messageSend, method, scope));
	}
	
	public static TypeBinding resolveType(TypeBinding resolvedType, MessageSend methodCall, BlockScope scope) {
		List<Extension> extensions = new ArrayList<Extension>();
		TypeDeclaration decl = scope.classScope().referenceContext;
		
		EclipseNode owningType = null;
		
		for (EclipseNode typeNode = getTypeNode(decl); typeNode != null; typeNode = upToType(typeNode)) {
			Annotation ann = getAnnotation(ExtensionMethod.class, typeNode);
			if (ann != null) {
				extensions.addAll(0, getApplicableExtensionMethods(typeNode, ann, methodCall.receiver.resolvedType));
				if (owningType == null) owningType = typeNode;
			}
		}
		
		boolean skip = false;
		
		if (methodCall.receiver instanceof ThisReference && (((ThisReference)methodCall.receiver).bits & ASTNode.IsImplicitThis) != 0) skip = true;
		if (methodCall.receiver instanceof SuperReference) skip = true;
		if (methodCall.receiver instanceof NameReference) {
			Binding binding = ((NameReference)methodCall.receiver).binding;
			if (binding instanceof TypeBinding) skip = true;
		}
		
		if (!skip) for (Extension extension : extensions) {
			if (!extension.suppressBaseMethods && !(methodCall.binding instanceof ProblemMethodBinding)) continue;
			for (MethodBinding extensionMethod : extension.extensionMethods) {
				if (!Arrays.equals(methodCall.selector, extensionMethod.selector)) continue;
				MessageSend_postponedErrors.clear(methodCall);
				if (methodCall.receiver instanceof ThisReference) {
					methodCall.receiver.bits &= ~ASTNode.IsImplicitThis;
				}
				List<Expression> arguments = new ArrayList<Expression>();
				arguments.add(methodCall.receiver);
				if (methodCall.arguments != null) arguments.addAll(Arrays.asList(methodCall.arguments));
				List<TypeBinding> argumentTypes = new ArrayList<TypeBinding>();
				for (Expression argument : arguments) {
					if (argument.resolvedType != null) argumentTypes.add(argument.resolvedType);
					// TODO: Instead of just skipping nulls entirely, there is probably a 'unresolved type' placeholder. THAT is what we ought to be adding here!
				}
				Expression[] originalArgs = methodCall.arguments;
				methodCall.arguments = arguments.toArray(new Expression[0]);
				MethodBinding fixedBinding = scope.getMethod(extensionMethod.declaringClass, methodCall.selector, argumentTypes.toArray(new TypeBinding[0]), methodCall);
				if (fixedBinding instanceof ProblemMethodBinding) {
					methodCall.arguments = originalArgs;
					if (fixedBinding.declaringClass != null) {
						PostponedInvalidMethodError.invoke(scope.problemReporter(), methodCall, fixedBinding, scope);
					}
				} else {
					for (int i = 0, iend = arguments.size(); i < iend; i++) {
						Expression arg = arguments.get(i);
						if (fixedBinding.parameters[i].isArrayType() != arg.resolvedType.isArrayType()) break;
						if (arg instanceof MessageSend) {
							((MessageSend) arg).valueCast = arg.resolvedType;
						}
						if (!fixedBinding.parameters[i].isBaseType() && arg.resolvedType.isBaseType()) {
							int id = arg.resolvedType.id;
							arg.implicitConversion = TypeIds.BOXING | (id + (id << 4)); // magic see TypeIds
						} else if (fixedBinding.parameters[i].isBaseType() && !arg.resolvedType.isBaseType()) {
							int id = fixedBinding.parameters[i].id;
							arg.implicitConversion = TypeIds.UNBOXING | (id + (id << 4)); // magic see TypeIds
						}
					}
					
					methodCall.receiver = createNameRef(extensionMethod.declaringClass, methodCall);
					methodCall.actualReceiverType = extensionMethod.declaringClass;
					methodCall.binding = fixedBinding;
					methodCall.resolvedType = methodCall.binding.returnType;
				}
				return methodCall.resolvedType;
			}
		}
		
		PostponedError error = MessageSend_postponedErrors.get(methodCall);
		if (error != null) error.fire();
		
		MessageSend_postponedErrors.clear(methodCall);
		return resolvedType;
	}
	
	private static NameReference createNameRef(TypeBinding typeBinding, ASTNode source) {
		long p = ((long) source.sourceStart << 32) | source.sourceEnd;
		char[] pkg = typeBinding.qualifiedPackageName();
		char[] basename = typeBinding.qualifiedSourceName();
		
		StringBuilder sb = new StringBuilder();
		if (pkg != null) sb.append(pkg);
		if (sb.length() > 0) sb.append(".");
		sb.append(basename);
		
		String tName = sb.toString();
		
		if (tName.indexOf('.') == -1) {
			return new SingleNameReference(basename, p);
		} else {
			char[][] sources;
			String[] in = tName.split("\\.");
			sources = new char[in.length][];
			for (int i = 0; i < in.length; i++) sources[i] = in[i].toCharArray();
			long[] poss = new long[in.length];
			Arrays.fill(poss, p);
			return new QualifiedNameReference(sources, poss, source.sourceStart, source.sourceEnd);
		}
	}
}
