package lombok.eclipse.handlers;

import java.util.*;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.internal.compiler.ast.*;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.lookup.*;
import org.mangosdk.spi.ProviderFor;

import lombok.Mixin;
import lombok.core.AnnotationValues;
import lombok.eclipse.Eclipse;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import static lombok.eclipse.Eclipse.ECLIPSE_DO_NOT_TOUCH_FLAG;
import static lombok.eclipse.handlers.EclipseHandlerUtil.*;

@ProviderFor(EclipseAnnotationHandler.class)
public class HandleMixin extends EclipseAnnotationHandler<Mixin> {
	
	int pS, pE;
	long p;
	EclipseNode me;
	TypeDeclaration meDecl;
	EclipseNode thisAnno;
	ASTNode _ast;
	Annotation _src;
	Argument[] argFields;
	HashMap<String, ArrayList<TypeReference>> fieldType; 
	HashMap<String, TypeReference> hasDefault;
	
	/* Auxiliary: create a method. */
	private MethodDeclaration newMethod() {
		MethodDeclaration method = new MethodDeclaration(meDecl.compilationResult);
		method.annotations = null;
		method.modifiers = ClassFileConstants.AccDefault;
		method.typeParameters = null;
		method.returnType = null;
		method.selector = "".toCharArray();
		method.arguments = null;
		method.binding = null;
		method.thrownExceptions = null;
		method.bits |= ECLIPSE_DO_NOT_TOUCH_FLAG;
		method.statements = null;
		method.bodyStart = method.declarationSourceStart = method.sourceStart = _ast.sourceStart;
		method.bodyEnd = method.declarationSourceEnd = method.sourceEnd = _ast.sourceEnd;
		return method;
	}
	
	/* Auxiliary: throw an error. */
	private void throwError(String s) { thisAnno.addError(s); }
	
	/* Auxiliary: print out information for debugging. */
	@SuppressWarnings("unused") private void print(String s) {
		MethodDeclaration print = newMethod();
		print.modifiers = ClassFileConstants.AccStatic;
		print.returnType = new SingleTypeReference(TypeBinding.VOID.simpleName, p);
		print.selector = "print".toCharArray();
		MessageSend printInfo = new MessageSend();
		printInfo.arguments = new Expression[] {
			new StringLiteral(s.toCharArray(), _ast.sourceStart, _ast.sourceEnd, 0)
		};
		printInfo.receiver = createNameReference("System.out", _src);
		printInfo.selector = "println".toCharArray();
		print.statements = new Statement[] { printInfo };
		injectMethod(me, print);
	}
	
	@Override public void handle(AnnotationValues<Mixin> annotation, Annotation ast, EclipseNode annotationNode) {
		pS = ast.sourceStart;
		pE = ast.sourceEnd;
		p = (long)pS << 32 | pE;
		me = annotationNode.up();
		meDecl = (TypeDeclaration) me.get();
		_ast = annotationNode.get();
		_src = ast;
		thisAnno = annotationNode;
		if (!isInterface(meDecl)) {
			throwError("@Mixin is only supported on an interface.");
			return;
		}
		handleMixin();
	}
	
	private void handleMixin() {
		fieldType = new HashMap<String, ArrayList<TypeReference>>();
		hasDefault = new HashMap<String, TypeReference>();
		addFieldsToMap(me);
		Iterator<Map.Entry<String, TypeReference>> it = hasDefault.entrySet().iterator();
		while (it.hasNext()) { fieldType.remove(it.next().getKey()); }
		argFields = new Argument[fieldType.size()];
		Iterator<Map.Entry<String, ArrayList<TypeReference>>> it2 = fieldType.entrySet().iterator();
		int index = 0;
		while (it2.hasNext()) {
			Map.Entry<String, ArrayList<TypeReference>> entry = it2.next();
			String name = entry.getKey();
			TypeReference type = entry.getValue().get(entry.getValue().size() - 1);
			argFields[index++] = new Argument(name.toCharArray(), p, copyType(type), Modifier.NONE);
		}
		createOfMethod();
		createWithMethod();
		createCloneMethod();
	}
	
	private void createOfMethod() {
		MethodDeclaration of = newMethod();
		of.modifiers = ClassFileConstants.AccStatic;
		of.returnType = new SingleTypeReference(meDecl.name, p);
		of.selector = "of".toCharArray();
		of.arguments = argFields.length == 0 ? null : argFields;
		TypeDeclaration anonymous = genAnonymous();
		QualifiedAllocationExpression alloc = new QualifiedAllocationExpression(anonymous);
		alloc.type = copyType(of.returnType);
		ReturnStatement returnStmt = new ReturnStatement(alloc, pS, pE);
		of.statements = new Statement[] { returnStmt };
		injectMethod(me, of);
	}
	
	private TypeDeclaration genAnonymous() {
		FieldDeclaration[] of_fields = new FieldDeclaration[argFields.length];
		MethodDeclaration[] of_methods = new MethodDeclaration[2 * argFields.length + 1];
		MessageSend invokeOfInClone = new MessageSend();
		invokeOfInClone.receiver = new SingleNameReference(meDecl.name, p);
		invokeOfInClone.selector = "of".toCharArray();
		invokeOfInClone.arguments = new Expression[argFields.length];
		for (int i = 0; i < argFields.length; i++) {
			Argument arg = new Argument(argFields[i].name, p, copyType(argFields[i].type), Modifier.NONE);
			Argument arg_copy = new Argument(argFields[i].name, p, copyType(argFields[i].type), Modifier.NONE);
			FieldDeclaration f = new FieldDeclaration(("_" + String.valueOf(arg.name)).toCharArray(), pS, pE);
			f.bits |= Eclipse.ECLIPSE_DO_NOT_TOUCH_FLAG;
			f.modifiers = ClassFileConstants.AccDefault;
			f.type = copyType(arg.type);
			f.initialization = new SingleNameReference(arg.name, p);
			of_fields[i] = f;
			MethodDeclaration mSetter = newMethod();
			mSetter.modifiers = ClassFileConstants.AccPublic;
			mSetter.returnType = new SingleTypeReference(TypeBinding.VOID.simpleName, p);
			mSetter.selector = arg.name;
			mSetter.arguments = new Argument[]{ arg };
			Assignment assignS = new Assignment(new SingleNameReference(("_" + String.valueOf(arg.name)).toCharArray(), p), new SingleNameReference(arg.name, p), (int)p);
			assignS.sourceStart = pS; assignS.sourceEnd = assignS.statementEnd = pE;
			mSetter.statements = new Statement[] { assignS };
			of_methods[2 * i] = mSetter;
			MethodDeclaration mGetter = newMethod();
			mGetter.modifiers = ClassFileConstants.AccPublic;
			mGetter.returnType = copyType(arg.type);
			mGetter.selector = arg.name;
			ReturnStatement returnS = new ReturnStatement(new SingleNameReference(("_" + String.valueOf(arg.name)).toCharArray(), p), pS, pE);
			mGetter.statements = new Statement[] { returnS };
			of_methods[2 * i + 1] = mGetter;
			MethodDeclaration mWith = newMethod();
			mWith.modifiers = ClassFileConstants.AccPublic;
			mWith.returnType = new SingleTypeReference(meDecl.name, p);
			mWith.selector = ("with" + String.valueOf(arg_copy.name)).toCharArray();
			mWith.arguments = new Argument[]{ arg_copy };
			MessageSend invokeOf = new MessageSend();
			invokeOf.receiver = new SingleNameReference(meDecl.name, p);
			invokeOf.selector = "of".toCharArray();
			invokeOf.arguments = new Expression[argFields.length];
			for (int j = 0; j < argFields.length; j++) {
				if (j == i) invokeOf.arguments[j] = new SingleNameReference(argFields[j].name, p);
				else {
					MessageSend tempM = new MessageSend();
					tempM.receiver = new ThisReference(pS, pE);
					tempM.selector = argFields[j].name;
					tempM.arguments = null;
					invokeOf.arguments[j] = tempM;
				}
			}
			ReturnStatement returnW = new ReturnStatement(invokeOf, pS, pE);
			mWith.statements = new Statement[] { returnW };
			// of_methods[3 * i + 2] = mWith;
			MessageSend tempC = new MessageSend();
			tempC.receiver = new ThisReference(pS, pE);
			tempC.selector = argFields[i].name;
			tempC.arguments = null;
			invokeOfInClone.arguments[i] = tempC;
		}
		MethodDeclaration mClone = newMethod();
		mClone.annotations = new Annotation[] { makeMarkerAnnotation(TypeConstants.JAVA_LANG_OVERRIDE, _ast) };
		mClone.modifiers = ClassFileConstants.AccPublic;
		mClone.returnType = new SingleTypeReference(meDecl.name, p);
		mClone.selector = "clone".toCharArray();
		ReturnStatement returnC = new ReturnStatement(invokeOfInClone, pS, pE);
		mClone.statements = new Statement[] { returnC };
		of_methods[of_methods.length - 1] = mClone;
		TypeDeclaration anonymous = new TypeDeclaration(meDecl.compilationResult);
		anonymous.bits |= (ASTNode.IsAnonymousType | ASTNode.IsLocalType);
		anonymous.name = CharOperation.NO_CHAR;
		anonymous.typeParameters = null;
		anonymous.fields = of_fields.length == 0 ? null : of_fields;
		anonymous.methods = of_methods.length == 0 ? null : of_methods;
		return anonymous;
	}
	
	/* 
	 * With-methods are created as default interface methods.
	 * Otherwise they will be inherited by sub-interfaces and cause trouble. 
	 */
	private void createWithMethod() {
		for (int i = 0; i < argFields.length; i++) {
			Argument arg = new Argument(argFields[i].name, p, copyType(argFields[i].type), Modifier.NONE);
			MethodDeclaration mWith = new MethodDeclaration(meDecl.compilationResult);
			mWith.modifiers |= 0x10000;
			mWith.returnType = new SingleTypeReference(meDecl.name, p);
			mWith.selector = ("with" + String.valueOf(arg.name)).toCharArray();
			mWith.arguments = new Argument[]{ arg };
			MessageSend invokeOf = new MessageSend();
			invokeOf.receiver = new SingleNameReference(meDecl.name, p);
			invokeOf.selector = "of".toCharArray();
			invokeOf.arguments = new Expression[argFields.length];
			for (int j = 0; j < argFields.length; j++) {
				if (j == i) invokeOf.arguments[j] = new SingleNameReference(argFields[j].name, p);
				else {
					MessageSend tempM = new MessageSend();
					tempM.receiver = new ThisReference(pS, pE);
					tempM.selector = argFields[j].name;
					tempM.arguments = null;
					invokeOf.arguments[j] = tempM;
				}
			}
			ReturnStatement returnW = new ReturnStatement(invokeOf, pS, pE);
			mWith.statements = new Statement[] { returnW };
			injectMethod(me, mWith);
		}
	}
	
	/*
	 * Clone-methods are created as abstract methods, and overridden in the anonymous class.
	 * Otherwise an exception will be thrown by java compiler.
	 */
	private void createCloneMethod() {
		MethodDeclaration mClone = newMethod();
		mClone.returnType = new SingleTypeReference(meDecl.name, p);
		mClone.selector = "clone".toCharArray();
		injectMethod(me, mClone);
	}
	
	private boolean sameType(TypeReference t1, TypeReference t2) {
		if (!(t1 instanceof SingleTypeReference)) return false;
		if (!(t2 instanceof SingleTypeReference)) return false;
		return Arrays.equals(((SingleTypeReference) t1).token, ((SingleTypeReference) t2).token);
	}
	
	private boolean isVoidMethod(MethodDeclaration m) {
		if (!(m.returnType instanceof SingleTypeReference)) return false;
		return Arrays.equals(TypeConstants.VOID, ((SingleTypeReference) m.returnType).token);
	}
	
	private boolean isInterface(TypeDeclaration t) {
		return (t.modifiers & ClassFileConstants.AccInterface) != 0;
	}
	
	// Warning: workaround. Only check interfaces in the same file. Inner interfaces unsupported.
	private EclipseNode findTypeDecl(char[] name) {
		for (EclipseNode x : me.up().down()) {
			if (!(x.get() instanceof TypeDeclaration)) continue;
			TypeDeclaration y = (TypeDeclaration) x.get();
			if (!isInterface(y)) continue;
			if (Arrays.equals(name, y.name)) return x;
		}
		return null;
	}
	
	private boolean subType(TypeReference t1, TypeReference t2) {
		if (!(t1 instanceof SingleTypeReference)) return false;
		if (!(t2 instanceof SingleTypeReference)) return false;
		if (sameType(t1, t2)) return true;
		EclipseNode d1 = findTypeDecl(((SingleTypeReference) t1).token);
		if (d1 == null) return false;
		if (!(d1.get() instanceof TypeDeclaration)) return false;
		TypeDeclaration decl = (TypeDeclaration) d1.get();
		if (decl.superclass != null & subType(decl.superclass, t2)) return true;
		if (decl.superInterfaces != null) {
			for (TypeReference x : ((TypeDeclaration) d1.get()).superInterfaces)
				if (subType(x, t2)) return true;
		}
		return false;
	}
	
	private void addFieldsToMap(EclipseNode node) {
		if (!(node.get() instanceof TypeDeclaration)) return;
		TypeDeclaration decl = (TypeDeclaration) node.get();
		if (!isInterface(decl)) return;
		if (decl.superInterfaces != null) {
			for (int i = 0; i < decl.superInterfaces.length; i++) {
				if (!(decl.superInterfaces[i] instanceof SingleTypeReference)) {
					throwError("SuperInterface unresolved.");
					return;
				}
				char[] tempSuper = ((SingleTypeReference) decl.superInterfaces[i]).token;
				EclipseNode tempSuperDecl = findTypeDecl(tempSuper);
				if (tempSuperDecl == null) {
					throwError("SuperInterface not found.");
					return;
				}
				addFieldsToMap(tempSuperDecl);
			}
		}
		for (EclipseNode x : node.down()) {
			if (!(x.get() instanceof MethodDeclaration)) continue;
			MethodDeclaration y = (MethodDeclaration) x.get();
			if (y.isDefaultMethod() || isVoidMethod(y)) continue;
			if (y.arguments != null && y.arguments.length != 0) continue;
			String name = String.valueOf(y.selector);
			// Skip the clone method.
			if (name.equals("clone")) continue;
			TypeReference type = copyType(y.returnType);
			ArrayList<TypeReference> l = new ArrayList<TypeReference>();
			if (!fieldType.containsKey(name)) {
				l.add(type);
			} else {
				l = fieldType.get(name);
				if (!l.contains(type)) {
					int index = l.size() - 1;
					while (index >= 0) {
						if (!subType(l.get(index), type)) break;
						else index--;
					}
					// List(T1, T2, T3, ...) satisfies: T1 >: T2 >: T3 >: ...
					l.add(index + 1, type);
				}
			}
			fieldType.put(name, l);
		}
		for (EclipseNode x : node.down()) {
			if (!(x.get() instanceof MethodDeclaration)) continue;
			MethodDeclaration y = (MethodDeclaration) x.get();
			if (!y.isDefaultMethod() || isVoidMethod(y)) continue;
			if (y.arguments != null && y.arguments.length != 0) continue;
			String name = String.valueOf(y.selector);
			if (fieldType.containsKey(name)) {
				hasDefault.put(name, copyType(y.returnType));
			}
		}
	}
	
}
