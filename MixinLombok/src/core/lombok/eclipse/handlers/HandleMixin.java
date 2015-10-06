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
	ResolvedType resolvedType;
	
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
		if (!Util.isInterface(meDecl)) {
			throwError("@Mixin is only supported on an interface.");
			return;
		}
		handleMixin();
	}
	
	private void handleMixin() {
		resolvedType = resolveType(me);
		if (resolvedType.unresolved.length != 0) {
			throwError("Unresolved methods inside.");
			return;
		}
		for (int i = 0; i < resolvedType.fields.length - 1; i++) {
			for (int j = i + 1; j < resolvedType.fields.length; j++) {
				if (resolvedType.fields[i].name.compareTo(resolvedType.fields[j].name) > 0) {
					Field temp = resolvedType.fields[i];
					resolvedType.fields[i] = resolvedType.fields[j];
					resolvedType.fields[j] = temp;
				}
			}
		}
		argFields = new Argument[resolvedType.fields.length];
		for (int i = 0; i < argFields.length; i++) {
			Field f = resolvedType.fields[i];
			argFields[i] = new Argument(f.name.toCharArray(), p, copyType(f.type), Modifier.NONE);
		}
		createOfMethod();
		createSetterMethod();
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
		MethodDeclaration[] of_methods = new MethodDeclaration[3 * argFields.length + 1];
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
			mSetter.selector = arg.name;
			mSetter.arguments = new Argument[]{ arg };
			Assignment assignS = new Assignment(new SingleNameReference(("_" + String.valueOf(arg.name)).toCharArray(), p), new SingleNameReference(arg.name, p), (int)p);
			assignS.sourceStart = pS; assignS.sourceEnd = assignS.statementEnd = pE;
			ReturnStatement returnS = new ReturnStatement(new ThisReference(pS, pE), pS, pE);
			if (resolvedType.fields[i].setterType == Field.VOID_SETTER) {
				mSetter.statements = new Statement[] { assignS };
				mSetter.returnType = new SingleTypeReference(TypeBinding.VOID.simpleName, p);
			} else {
				mSetter.statements = new Statement[] { assignS, returnS };
				mSetter.returnType = new SingleTypeReference(meDecl.name, p);
			}
			of_methods[3 * i] = mSetter;
			MethodDeclaration mGetter = newMethod();
			mGetter.modifiers = ClassFileConstants.AccPublic;
			mGetter.returnType = copyType(arg.type);
			mGetter.selector = arg.name;
			ReturnStatement returnG = new ReturnStatement(new SingleNameReference(("_" + String.valueOf(arg.name)).toCharArray(), p), pS, pE);
			mGetter.statements = new Statement[] { returnG };
			of_methods[3 * i + 1] = mGetter;
			MethodDeclaration mWith = newMethod();
			mWith.modifiers = ClassFileConstants.AccPublic;
			mWith.returnType = new SingleTypeReference(meDecl.name, p);
			mWith.selector = ("with" + Util.firstUpper(String.valueOf(arg_copy.name))).toCharArray();
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
			of_methods[3 * i + 2] = mWith;
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
	
	private void createSetterMethod() {
		for (int i = 0; i < argFields.length; i++) {
			if (resolvedType.fields[i].setterType != Field.REFINE_TYPE_FLUENT_SETTER) continue;
			Argument arg = new Argument(argFields[i].name, p, copyType(argFields[i].type), Modifier.NONE);
			MethodDeclaration mSetter = newMethod();
			mSetter.returnType = new SingleTypeReference(meDecl.name, p);
			mSetter.selector = arg.name;
			mSetter.arguments = new Argument[]{ arg };
			injectMethod(me, mSetter);
		}
	}
	
	private void createWithMethod() {
		for (int i = 0; i < argFields.length; i++) {
			if (resolvedType.fields[i].withType != Field.REFINE_TYPE_WITH) continue;
			Argument arg = new Argument(argFields[i].name, p, copyType(argFields[i].type), Modifier.NONE);
			MethodDeclaration mWith = newMethod();
			mWith.returnType = new SingleTypeReference(meDecl.name, p);
			mWith.selector = ("with" + Util.firstUpper(String.valueOf(arg.name))).toCharArray();
			mWith.arguments = new Argument[]{ arg };
			injectMethod(me, mWith);
		}
	}
	
	private void createCloneMethod() {
		MethodDeclaration mClone = newMethod();
		mClone.returnType = new SingleTypeReference(meDecl.name, p);
		mClone.selector = "clone".toCharArray();
		injectMethod(me, mClone);
	}
	
	// Warning: workaround. Only check interfaces in the same file. Inner interfaces unsupported.
	private EclipseNode findTypeDecl(char[] name) {
		for (EclipseNode x : me.up().down()) {
			if (!(x.get() instanceof TypeDeclaration)) continue;
			TypeDeclaration y = (TypeDeclaration) x.get();
			if (!Util.isInterface(y)) continue;
			if (Arrays.equals(name, y.name)) return x;
		}
		return null;
	}
	
	private boolean subType(TypeReference t1, TypeReference t2) {
		if (!(t1 instanceof SingleTypeReference)) return false;
		if (!(t2 instanceof SingleTypeReference)) return false;
		if (Util.sameType(t1, t2)) return true;
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
	
	private ResolvedType resolveType(EclipseNode node) {
		Type type = analyseInterface(node);
		if (type == null) { throwError("analyseInterface() failed."); return null; }
		TypeReference thisType = new SingleTypeReference(((TypeDeclaration) node.get()).name, p);
		ArrayList<Field> fields = new ArrayList<Field>();
		HashSet<MethodDeclaration> pending = new HashSet<MethodDeclaration>();
		HashSet<MethodDeclaration> unresolved = new HashSet<MethodDeclaration>();
		for (int i = 0; i < type.methods.length; i++) {
			MethodDeclaration m = type.methods[i].method;
			if (Util.isField(m)) fields.add(new Field(String.valueOf(m.selector), copyType(m.returnType)));
			else if (!Util.isCloneMethod(m) && Util.isAbstractMethod(m)) {
				pending.add(m); unresolved.add(m);
			}
		}
		Field[] fields_array = fields.toArray(new Field[fields.size()]);		
		for (int i = 0; i < fields_array.length; i++) {
			for (MethodDeclaration m : pending) {
				int isSetter = isSetterFor(thisType, m, fields_array[i].name, fields_array[i].type);
				int isWith = isWithFor(thisType, m, fields_array[i].name, fields_array[i].type);
				if (isSetter != Field.NO_SETTER) {
					fields_array[i].setterType = isSetter;
					unresolved.remove(m);
				} else if (isWith != Field.NO_WITH) {
					fields_array[i].withType = isWith;
					unresolved.remove(m);
				}
			}
		}
		return new ResolvedType(fields_array, unresolved.toArray(new MethodDeclaration[unresolved.size()]));
	}
	
	private Type analyseInterface(EclipseNode node) {
		if (!(node.get() instanceof TypeDeclaration)) return null;
		TypeDeclaration decl = (TypeDeclaration) node.get();
		if (!Util.isInterface(decl)) return null;
		ArrayList<Method> thisMethods = new ArrayList<Method>();
		for (EclipseNode x : node.down()) {
			if (!(x.get() instanceof MethodDeclaration)) continue;
			thisMethods.add(new Method((MethodDeclaration) x.get(), new SingleTypeReference(decl.name, p)));
		}
		Type thisType = new Type(thisMethods.toArray(new Method[thisMethods.size()]));
		if (decl.superInterfaces != null) {
			Type[] superTypes = new Type[decl.superInterfaces.length];
			for (int i = 0; i < decl.superInterfaces.length; i++) {
				if (!(decl.superInterfaces[i] instanceof SingleTypeReference)) {
					throwError("SuperInterface has unexpected type.");
					return null;
				}
				char[] tempSuper = ((SingleTypeReference) decl.superInterfaces[i]).token;
				EclipseNode tempSuperDecl = findTypeDecl(tempSuper);
				if (tempSuperDecl == null) {
					throwError("SuperInterface not found.");
					return null;
				}
				superTypes[i] = analyseInterface(tempSuperDecl);
			}
			thisType = mergeType(thisType, mergeType(superTypes));
		}
		return thisType;
	}
	
	private Type mergeType(Type[] types) {
		HashSet<Method> newMethods = new HashSet<Method>();
		for (int i = 0; i < types.length; i++) {
			for (int j = 0; j < types[i].methods.length; j++) {
				Method thisMethod = types[i].methods[j];
				boolean insert = true;
				for (Method thatMethod : newMethods) {
					if (!Util.sameMethod(thisMethod.method, thatMethod.method)) continue;					
					if (Util.sameType(thisMethod.method.returnType, thatMethod.method.returnType)) {
						if (subType(thisMethod.origin, thatMethod.origin)) insert = true;
						else if (subType(thatMethod.origin, thisMethod.origin)) insert = false;
						else insert = !thatMethod.method.isDefaultMethod();
					} else insert = subType(thisMethod.method.returnType, thatMethod.method.returnType);
					if (insert) newMethods.remove(thatMethod);					
					break;
				}
				if (insert) newMethods.add(thisMethod);
			}
		}
		Method[] ms = newMethods.toArray(new Method[newMethods.size()]);
		return new Type(ms);
	}
	
	private Type mergeType(Type thisType, Type superType) {
		HashSet<Method> newMethods = new HashSet<Method>(Arrays.asList(superType.methods));
		for (Method thisMethod : thisType.methods) {
			for (Method thatMethod : newMethods) {
				if (Util.sameMethod(thisMethod.method, thatMethod.method)) {
					newMethods.remove(thatMethod); break;
				}
			}
			newMethods.add(thisMethod);
		}
		Method[] ms = newMethods.toArray(new Method[newMethods.size()]);
		return new Type(ms);
	}
	
	private int isWithFor(TypeReference thisType, MethodDeclaration m, String name, TypeReference type) {
		if (!Util.getName(m).equals("with" + Util.firstUpper(name))) return Field.NO_WITH;
		if (m.arguments == null || m.arguments.length != 1) return Field.NO_WITH;
		if (!Util.sameType(m.arguments[0].type, type)) return Field.NO_WITH;
		if (Util.sameType(thisType, m.returnType)) return Field.THIS_TYPE_WITH;
		if (subType(thisType, m.returnType)) return Field.REFINE_TYPE_WITH;
		return Field.NO_WITH;
	}
	
	private int isSetterFor(TypeReference thisType, MethodDeclaration m, String name, TypeReference type) {
		if (!Util.getName(m).equals(name)) return Field.NO_SETTER;
		if (m.arguments == null || m.arguments.length != 1) return Field.NO_SETTER;
		if (!Util.sameType(m.arguments[0].type, type)) return Field.NO_SETTER;
		if (Util.isVoidMethod(m)) return Field.VOID_SETTER;
		if (Util.sameType(m.returnType, thisType)) return Field.THIS_TYPE_FLUENT_SETTER;
		if (subType(thisType, m.returnType)) return Field.REFINE_TYPE_FLUENT_SETTER;
		return Field.NO_SETTER;
	}
	
}

class Field {
	String name;
	TypeReference type;
	int setterType = NO_SETTER;
	int withType = NO_WITH;
	Field(String name, TypeReference type) {
		this.name = name; this.type = type;
	}
	static int VOID_SETTER = 1;
	static int THIS_TYPE_FLUENT_SETTER = 2;
	static int REFINE_TYPE_FLUENT_SETTER = 3;
	static int NO_SETTER = 4;
	static int THIS_TYPE_WITH = 4;
	static int REFINE_TYPE_WITH = 5;
	static int NO_WITH = 6;
}

class Method {
	MethodDeclaration method;
	TypeReference origin;
	Method(MethodDeclaration method, TypeReference origin) {
		this.method = method; this.origin = origin;
	}
}

class Type {
	Method[] methods;
	Type(Method[] ms) {
		methods = new Method[ms.length];
		for (int i = 0; i < ms.length; i++) methods[i] = ms[i]; 
	}
}

class ResolvedType {
	Field[] fields;
	MethodDeclaration[] unresolved;
	ResolvedType(Field[] _fields, MethodDeclaration[] _unresolved) {		
		this.fields = new Field[_fields.length];
		for (int i = 0; i < _fields.length; i++) this.fields[i] = _fields[i];
		this.unresolved = new MethodDeclaration[_unresolved.length];
		for (int i = 0; i < _unresolved.length; i++) this.unresolved[i] = _unresolved[i];
	}
}

class Util {
	static boolean sameType(TypeReference t1, TypeReference t2) {
		if (!(t1 instanceof SingleTypeReference)) return false;
		if (!(t2 instanceof SingleTypeReference)) return false;
		return Arrays.equals(((SingleTypeReference) t1).token, ((SingleTypeReference) t2).token);
	}
	static boolean sameMethod(MethodDeclaration m1, MethodDeclaration m2) {
		if (!Arrays.equals(m1.selector, m2.selector)) return false;
		int length1 = 0, length2 = 0;
		if (m1.arguments != null) length1 = m1.arguments.length;
		if (m2.arguments != null) length2 = m2.arguments.length;
		if (length1 != length2) return false;
		for (int i = 0; i < length1; i++) {
			TypeReference t1 = copyType(m1.arguments[i].type);
			TypeReference t2 = copyType(m2.arguments[i].type);
			if (!sameType(t1, t2)) return false;
		}
		return true;
	}
	static boolean isVoidMethod(MethodDeclaration m) {
		if (!(m.returnType instanceof SingleTypeReference)) return false;
		return Arrays.equals(TypeConstants.VOID, ((SingleTypeReference) m.returnType).token);
	}
	static boolean isInterface(TypeDeclaration t) {
		return (t.modifiers & ClassFileConstants.AccInterface) != 0;
	}
	static String getName(MethodDeclaration m) {
		return String.valueOf(m.selector);
	}
	static boolean isField(MethodDeclaration m) {
		if (m.isDefaultMethod() || m.isStatic() || isVoidMethod(m)) return false;
		if (m.arguments != null && m.arguments.length != 0) return false;
		if (isCloneMethod(m)) return false;
		if (!Character.isLowerCase(m.selector[0])) return false;
		return true;
	}
	static boolean isAbstractMethod(MethodDeclaration m) {
		return !m.isDefaultMethod() && !m.isStatic();	
	}
	static boolean isCloneMethod(MethodDeclaration m) {
		if (m.arguments != null && m.arguments.length != 0) return false;
		return getName(m).equals("clone");
	}
	static String firstUpper(String s) {
		if (s.isEmpty()) return s;
		return s.substring(0, 1).toUpperCase() + s.substring(1, s.length());
	}
}
