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
			thisAnno.addError("@Mixin is only supported on an interface.");
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
	}
	// For debugging only.
	@SuppressWarnings("unused") private void print(String s) {
		MethodDeclaration print = new MethodDeclaration(meDecl.compilationResult);
		print.annotations = null;
		print.modifiers = ClassFileConstants.AccStatic;
		print.typeParameters = null;
		print.returnType = new SingleTypeReference(TypeBinding.VOID.simpleName, p);
		print.selector = "print".toCharArray();
		print.arguments = null;
		print.binding = null;
		print.thrownExceptions = null;
		print.bits |= ECLIPSE_DO_NOT_TOUCH_FLAG;
		MessageSend printInfo = new MessageSend();
		printInfo.arguments = new Expression[] {
				new StringLiteral(s.toCharArray(), _ast.sourceStart, _ast.sourceEnd, 0)
		};
		printInfo.receiver = createNameReference("System.out", _src);
		printInfo.selector = "println".toCharArray();
		print.statements = new Statement[] { printInfo };
		injectMethod(me, print);
	}
	private void createOfMethod() {
		MethodDeclaration of = new MethodDeclaration(meDecl.compilationResult);
		of.annotations = null;
		of.modifiers = ClassFileConstants.AccStatic;
		of.typeParameters = null;
		of.returnType = new SingleTypeReference(meDecl.name, p);
		of.selector = "of".toCharArray();
		of.arguments = argFields.length == 0 ? null : argFields;
		of.binding = null;
		of.thrownExceptions = null;
		of.bits |= ECLIPSE_DO_NOT_TOUCH_FLAG;
		of.bodyStart = of.declarationSourceStart = of.sourceStart = _ast.sourceStart;
		of.bodyEnd = of.declarationSourceEnd = of.sourceEnd = _ast.sourceEnd;
		TypeDeclaration anonymous = genAnonymous();
		QualifiedAllocationExpression alloc = new QualifiedAllocationExpression(anonymous);
		alloc.type = copyType(of.returnType);
		ReturnStatement returnStmt = new ReturnStatement(alloc, pS, pE);
		of.statements = new Statement[] { returnStmt };
		injectMethod(me, of);
	}
	private TypeDeclaration genAnonymous() {
		FieldDeclaration[] of_fields = new FieldDeclaration[argFields.length];
		MethodDeclaration[] of_methods = new MethodDeclaration[2 * argFields.length];
		for (int i = 0; i < argFields.length; i++) {
			Argument arg = new Argument(argFields[i].name, p, copyType(argFields[i].type), Modifier.NONE);
			FieldDeclaration f = new FieldDeclaration(("_" + String.valueOf(arg.name)).toCharArray(), pS, pE);
			f.bits |= Eclipse.ECLIPSE_DO_NOT_TOUCH_FLAG;
			f.modifiers = ClassFileConstants.AccDefault;
			f.type = copyType(arg.type);
			f.initialization = new SingleNameReference(arg.name, p);
			of_fields[i] = f;
			MethodDeclaration mSetter = new MethodDeclaration(meDecl.compilationResult);
			mSetter.annotations = null;
			mSetter.modifiers = ClassFileConstants.AccPublic;
			mSetter.typeParameters = null;
			mSetter.returnType = new SingleTypeReference(TypeBinding.VOID.simpleName, p);
			mSetter.selector = arg.name;
			mSetter.arguments = new Argument[]{ arg };
			mSetter.binding = null;
			mSetter.thrownExceptions = null;
			mSetter.bits |= ECLIPSE_DO_NOT_TOUCH_FLAG;
			mSetter.bodyStart = mSetter.declarationSourceStart = mSetter.sourceStart = _ast.sourceStart;
			mSetter.bodyEnd = mSetter.declarationSourceEnd = mSetter.sourceEnd = _ast.sourceEnd;
			Assignment assignS = new Assignment(new SingleNameReference(("_" + String.valueOf(arg.name)).toCharArray(), p), new SingleNameReference(arg.name, p), (int)p);
			assignS.sourceStart = pS; assignS.sourceEnd = assignS.statementEnd = pE;
			mSetter.statements = new Statement[] { assignS };
			of_methods[2 * i] = mSetter;
			MethodDeclaration mGetter = new MethodDeclaration(meDecl.compilationResult);
			mGetter.annotations = null;
			mGetter.modifiers = ClassFileConstants.AccPublic;
			mGetter.typeParameters = null;
			mGetter.returnType = copyType(arg.type);
			mGetter.selector = arg.name;
			mGetter.arguments = null;
			mGetter.binding = null;
			mGetter.thrownExceptions = null;
			mGetter.bits |= ECLIPSE_DO_NOT_TOUCH_FLAG;
			mGetter.bodyStart = mGetter.declarationSourceStart = mGetter.sourceStart = _ast.sourceStart;
			mGetter.bodyEnd = mGetter.declarationSourceEnd = mGetter.sourceEnd = _ast.sourceEnd;
			ReturnStatement returnS = new ReturnStatement(new SingleNameReference(("_" + String.valueOf(arg.name)).toCharArray(), p), pS, pE);
			mGetter.statements = new Statement[] { returnS };
			of_methods[2 * i + 1] = mGetter;
		}
		TypeDeclaration anonymous = new TypeDeclaration(meDecl.compilationResult);
		anonymous.bits |= (ASTNode.IsAnonymousType | ASTNode.IsLocalType);
		anonymous.name = CharOperation.NO_CHAR;
		anonymous.typeParameters = null;
		anonymous.fields = of_fields.length == 0 ? null : of_fields;
		anonymous.methods = of_methods.length == 0 ? null : of_methods;
		return anonymous;
	}
	@SuppressWarnings("unused") private boolean sameType(TypeReference t1, TypeReference t2) {
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
	// Example 1: (T1 <: T)
	// interface A { T x(); }
	// interface B extends A { T1 x(); }
	// interface C extends A {}
	// interface D extends B, C {}
	// D.of(T1 x).
	// Example 2: (T1 <: T)
	// interface A { T x(); }
	// interface B extends A { default T1 x() {...} }
	// interface C extends A {}
	// interface D extends B, C {}
	// D.of().
	// Example 3: (T1 <: T, T2 <: T)
	// interface A { T x(); }
	// interface B extends A { default T1 x() {...} }
	// interface C extends A { default T2 x() {...} }
	// interface D extends B, C {}
	// ERROR.
	// Example 4: (T1 <: T2 <: T)
	// interface A { T x(); }
	// interface B extends A { T1 x(); }
	// interface C extends A { T2 x(); }
	// interface D extends B, C {}
	// D.of(???).
	private void addFieldsToMap(EclipseNode node) {
		if (!(node.get() instanceof TypeDeclaration)) return;
		TypeDeclaration decl = (TypeDeclaration) node.get();
		if (!isInterface(decl)) return;
		if (decl.superInterfaces != null) {
			for (int i = 0; i < decl.superInterfaces.length; i++) {
				if (!(decl.superInterfaces[i] instanceof SingleTypeReference)) {
					thisAnno.addError("SuperInterface unresolved.");
					return;
				}
				char[] tempSuper = ((SingleTypeReference) decl.superInterfaces[i]).token;
				EclipseNode tempSuperDecl = findTypeDecl(tempSuper);
				if (tempSuperDecl == null) {
					thisAnno.addError("SuperInterface not found.");
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
			TypeReference type = copyType(y.returnType);
			ArrayList<TypeReference> l = new ArrayList<TypeReference>();
			if (!fieldType.containsKey(name)) {
				l.add(type);
			} else {
				l = fieldType.get(name);
				// Overridden with the new returnType <: old returnType.
				// To be validated.
				if (!l.contains(type)) l.add(type);
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
