package lombok.eclipse.handlers;

import java.util.*;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.internal.compiler.ast.*;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.lookup.*;
import org.mangosdk.spi.ProviderFor;

import lombok.Obj;
import lombok.core.AnnotationValues;
import lombok.eclipse.Eclipse;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import static lombok.eclipse.Eclipse.ECLIPSE_DO_NOT_TOUCH_FLAG;
import static lombok.eclipse.handlers.EclipseHandlerUtil.*;

@ProviderFor(EclipseAnnotationHandler.class)
public class HandleObj extends EclipseAnnotationHandler<Obj> {
	
	int pS, pE;
	long p;
	EclipseNode me;
	TypeDeclaration meDecl;
	EclipseNode meAnno;
	TypeReference meType;
	ASTNode _ast;
	Annotation _src;
	String errorMsg = "";
	
	ArrayList<MethodDeclaration> ofMethods;
	ArrayList<Argument> ofArgs;
	ArrayList<FieldDeclaration> ofFields;
	String[] fieldNames;
	HashMap<String, TypeReference> fieldsMap;
	
	@Override public void handle(AnnotationValues<Obj> annotation, Annotation ast, EclipseNode annotationNode) {
		pS = ast.sourceStart;
		pE = ast.sourceEnd;
		p = (long)pS << 32 | pE;
		me = annotationNode.up();
		meDecl = (TypeDeclaration) me.get();
		_ast = annotationNode.get();
		_src = ast;
		meAnno = annotationNode;
		meType = new SingleTypeReference(meDecl.name, p);		
		if (checkValid()) genOfMethod();
		if (errorMsg.length() > 0) meAnno.addError(errorMsg);
	}
	
	private boolean checkValid() {
		if (!Util.isInterface(meDecl)) {
			throwError("In checkValid(): @Obj is only supported on an interface");
			return false;
		}
		fieldsMap = new HashMap<String, TypeReference>();
		HashMap<Method, Boolean> unresolved = new HashMap<Method, Boolean>();
		ofMethods = new ArrayList<MethodDeclaration>();
		ofArgs = new ArrayList<Argument>();
		ofFields = new ArrayList<FieldDeclaration>();
		Type thisType = mBody(me);
		if (thisType == null) {
			throwError("In checkValid(): mBody is undefined for " + String.valueOf(meDecl.name));
			return false;
		}
		for (Method m : thisType.methods) {
			if (Util.isField(m.method)) fieldsMap.put(String.valueOf(m.method.selector), copyType(m.method.returnType));
			else if (Util.isAbstractMethod(m.method)) unresolved.put(m, true);
		}
		fieldNames = fieldsMap.keySet().toArray(new String[fieldsMap.keySet().size()]);
		Arrays.sort(fieldNames);
		if (ofAlreadyExists()) {
			throwError("In checkValid(): of method already defined");
			return false;
		}
		for (int i = 0; i < fieldNames.length; i++) {
			String name = fieldNames[i];
			TypeReference type = fieldsMap.get(name);
			ofArgs.add(new Argument(("_" + name).toCharArray(), p, copyType(type), Modifier.NONE));
			ofMethods.add(genGetter(name.toCharArray(), copyType(type)));
			ofFields.add(genField(name.toCharArray(), copyType(type)));
		}
		for (Method m : unresolved.keySet()) {
			if (!unresolved.get(m)) continue;
			String name = String.valueOf(m.method.selector);
			TypeReference type = m.method.returnType;
			Argument[] args = m.method.arguments;
			if (name.equals("with") && args != null && args.length == 1 && subType(meType, type)) {
				MethodDeclaration generalWith = genGeneralWith(copyType(args[0].type));
				if (generalWith == null) {
					throwError("In checkValid(): type of with method not applicable");
					return false;
				} else {
					if (!Util.sameType(meType, type)) genAbstractGeneralWith(copyType(args[0].type));
					ofMethods.add(generalWith);
					unresolved.put(m, false);
					continue;
				}
			}
			if (fieldsMap.containsKey(name) && args != null && args.length == 1 && Util.sameType(args[0].type, fieldsMap.get(name))) {
				if (Util.isVoidMethod(m.method)) {
					ofMethods.add(genSetter(name.toCharArray(), copyType(args[0].type), false));
					unresolved.put(m, false);
					continue;
				}
				else if (subType(meType, type)) {
					if (!Util.sameType(meType, type)) genAbstractSetter(name.toCharArray(), copyType(args[0].type));
					ofMethods.add(genSetter(name.toCharArray(), copyType(args[0].type), true));
					unresolved.put(m, false);
					continue;
				}
			}
			String name_substr = "";
			if (name.length() > 4 && name.substring(0, 4).equals("with") && Character.isUpperCase(name.charAt(4))) name_substr = Util.toLowerCase(name.substring(4, name.length()));
			if (name_substr.length() > 0 && fieldsMap.containsKey(name_substr) && args != null && args.length == 1 && Util.sameType(args[0].type, fieldsMap.get(name_substr)) && subType(meType, type)) {
				if (!Util.sameType(meType, type)) genAbstractWith(name_substr.toCharArray(), copyType(args[0].type));
				ofMethods.add(genWith(name_substr.toCharArray(), copyType(args[0].type)));
				unresolved.put(m, false);
				continue;
			}
			throwError("In checkValid(): unresolved method: " + m.toString());
			return false;
		}
		return true;
	}
	
	// Need to check for all subtypes, mbody is defined?
	private Type mBody(EclipseNode node) {
		if (!(node.get() instanceof TypeDeclaration)) {
			throwError("In mBody(): instanceof fails");
			return null;
		}
		TypeDeclaration decl = (TypeDeclaration) node.get();
		if (!Util.isInterface(decl)) {
			throwError("In mBody(): isInterface fails");
			return null;
		}	
		ArrayList<Method> allMethods = collectAllMethods(node, true);
		if (allMethods == null) return null;
		return new Type(allMethods.toArray(new Method[allMethods.size()]));
	}
	
	private boolean canOverride(Method m, ArrayList<Method> ms) {
		for (Method temp : ms) if (!subType(m, temp)) return false;
		return true;
	}
	
	private Method mostSpecific(ArrayList<Method> ms) {
		if (ms.size() < 1) return null;
		if (ms.size() == 1) return ms.get(0);
		Method[] msArray = ms.toArray(new Method[ms.size()]);
		Method res = msArray[0];
		if (res.method.isDefaultMethod()) return null;
		for (int i = 1; i < msArray.length; i++) {
			Method m = msArray[i];
			if (m.method.isDefaultMethod()) return null;
			if (subType(res, m)) continue;
			if (subType(m, res)) res = m;
			else return null;
		}
		return res;
	}
	
	// Didn't take static methods into consideration.
	private ArrayList<Method> collectAllMethods(EclipseNode node, boolean root) {
		if (!(node.get() instanceof TypeDeclaration)) {
			throwError("In collectAllMethods(): instanceof_1 fails");
			return null;
		}
		TypeDeclaration decl = (TypeDeclaration) node.get();
		if (!Util.isInterface(decl)) {
			throwError("In collectAllMethods(): isInterface fails");
			return null;
		}
		ArrayList<Method> allMethods = new ArrayList<Method>();		
		if (decl.superInterfaces != null) {
			for (int i = 0; i < decl.superInterfaces.length; i++) {
				if (!(decl.superInterfaces[i] instanceof SingleTypeReference)) {
					throwError("In collectAllMethods(): instanceof_2 fails");
					return null;
				}
				ArrayList<Method> getMethods = collectAllMethods(getTypeDecl(((SingleTypeReference) decl.superInterfaces[i]).token), false);
				allMethods.addAll(getMethods);
			}
		}
		if (!root) {
			for (EclipseNode x : node.down()) {
				if (!(x.get() instanceof MethodDeclaration)) continue;
				Method m = new Method((MethodDeclaration) x.get(), new SingleTypeReference(decl.name, p));
				if (!m.method.isStatic()) allMethods.add(m);
			}
			return allMethods;
		} else {
			ArrayList<Method> res = new ArrayList<Method>();
			HashMap<Method, ArrayList<Method>> map = shadow(allMethods);
			Set<Method> keySet = map.keySet();
			for (EclipseNode x : node.down()) {
				if (!(x.get() instanceof MethodDeclaration)) continue;
				Method m = new Method((MethodDeclaration) x.get(), new SingleTypeReference(decl.name, p));
				if (m.method.isStatic()) continue; // ?
				Method key = null;
				for (Method temp : map.keySet()) if (temp.equals(m)) {key = temp; break;}
				if (key == null) res.add(m);
				else if (canOverride(m, map.get(key))) {res.add(m); keySet.remove(key);}
				else return null;
			}
			for (Method m : keySet) {
				if (map.get(m).size() < 1) continue;
				Method mostSpecific = mostSpecific(map.get(m));			
				if (mostSpecific == null) {
					throwError("In collectAllMethods(): conflicts with method " + m.toString());
					return null;
				}
				res.add(mostSpecific);
			}
			return res;
		}
	}
	
	private HashMap<Method, ArrayList<Method>> shadow(ArrayList<Method> allMethods) {
		HashMap<Method, ArrayList<Method>> res = new HashMap<Method, ArrayList<Method>>();
		for (Method m : allMethods) {
			boolean add = true;
			for (Method key : res.keySet()) {
				if (key.equals(m)) {
					ArrayList<Method> value = res.get(key);
					ArrayList<Method> newValue = new ArrayList<Method>();
					boolean add2 = true;
					for (Method temp : value) {
						if (subType(temp.origin, m.origin)) {add2 = false; break;}
						if (!subType(m.origin, temp.origin)) newValue.add(temp);
					}
					if (add2) {newValue.add(m); res.put(key, newValue);}
					add = false;
					break;
				}
			}
			ArrayList<Method> singleton = new ArrayList<Method>();
			singleton.add(m);
			if (add) res.put(m, singleton);
		}
		return res;
	}
	
	private FieldDeclaration genField(char[] name, TypeReference type) {
		FieldDeclaration f = new FieldDeclaration(name, pS, pE);
		f.bits |= Eclipse.ECLIPSE_DO_NOT_TOUCH_FLAG;
		f.modifiers = ClassFileConstants.AccDefault;
		f.type = copyType(type);
		f.initialization = new SingleNameReference(("_" + String.valueOf(name)).toCharArray(), p);
		return f;
	}
	
	private MethodDeclaration genGetter(char[] name, TypeReference type) {
		MethodDeclaration mGetter = newMethod();
		mGetter.modifiers = ClassFileConstants.AccPublic;
		mGetter.returnType = copyType(type);
		mGetter.selector = name;
		ReturnStatement returnG = new ReturnStatement(new SingleNameReference(name, p), pS, pE);
		mGetter.statements = new Statement[] { returnG };
		return mGetter;
	}
	
	private MethodDeclaration genSetter(char[] name, TypeReference type, boolean fluent) {
		MethodDeclaration mSetter = newMethod();
		Argument arg = new Argument("val".toCharArray(), p, copyType(type), Modifier.NONE);
		mSetter.modifiers = ClassFileConstants.AccPublic;
		mSetter.selector = name;
		mSetter.arguments = new Argument[]{ arg };
		Assignment assignS = new Assignment(new SingleNameReference(name, p), new SingleNameReference("val".toCharArray(), p), (int)p);
		assignS.sourceStart = pS; assignS.sourceEnd = assignS.statementEnd = pE;
		ReturnStatement returnS = new ReturnStatement(new ThisReference(pS, pE), pS, pE);
		if (fluent) {
			mSetter.statements = new Statement[] { assignS, returnS };
			mSetter.returnType = copyType(meType);
		} else {
			mSetter.statements = new Statement[] { assignS };
			mSetter.returnType = new SingleTypeReference(TypeBinding.VOID.simpleName, p);
		}
		return mSetter;
	}
	
	private void genAbstractSetter(char[] name, TypeReference type) {
		Argument arg = new Argument("val".toCharArray(), p, copyType(type), Modifier.NONE);
		MethodDeclaration mSetter = newMethod();
		mSetter.returnType = copyType(meType);
		mSetter.selector = name;
		mSetter.arguments = new Argument[]{ arg };
		injectMethod(me, mSetter);
	}
	
	private MethodDeclaration genWith(char[] name, TypeReference type) {
		MethodDeclaration mWith = newMethod();
		Argument arg = new Argument("val".toCharArray(), p, copyType(type), Modifier.NONE);
		mWith.modifiers = ClassFileConstants.AccPublic;
		mWith.returnType = copyType(meType);
		mWith.selector = ("with" + Util.toUpperCase(String.valueOf(name))).toCharArray();
		mWith.arguments = new Argument[]{ arg };
		MessageSend invokeOf = new MessageSend();
		invokeOf.receiver = new SingleNameReference(meDecl.name, p);
		invokeOf.selector = "of".toCharArray();
		invokeOf.arguments = new Expression[fieldNames.length];
		for (int i = 0; i < fieldNames.length; i++) {
			if (fieldNames[i].equals(String.valueOf(name))) invokeOf.arguments[i] = new SingleNameReference("val".toCharArray(), p);
			else {
				MessageSend tempM = new MessageSend();
				tempM.receiver = new ThisReference(pS, pE);
				tempM.selector = fieldNames[i].toCharArray();
				tempM.arguments = null;
				invokeOf.arguments[i] = tempM;
			}
		}
		ReturnStatement returnW = new ReturnStatement(invokeOf, pS, pE);
		mWith.statements = new Statement[] { returnW };
		return mWith;
	}
	
	private void genAbstractWith(char[] name, TypeReference type) {
		Argument arg = new Argument("val".toCharArray(), p, copyType(type), Modifier.NONE);
		MethodDeclaration mWith = newMethod();
		mWith.returnType = copyType(meType);
		mWith.selector = ("with" + Util.toUpperCase(String.valueOf(name))).toCharArray();
		mWith.arguments = new Argument[]{ arg };
		injectMethod(me, mWith);
	}
	
	private MethodDeclaration genGeneralWith(TypeReference type) {
		if (!(type instanceof SingleTypeReference)) return null;
		Type t = mBody(getTypeDecl(((SingleTypeReference) type).token));
		if (t == null) return null;
		HashMap<String, TypeReference> fieldsFromType = new HashMap<String, TypeReference>();
		for (Method m : t.methods)
			if (Util.isField(m.method) && Util.sameType(m.method.returnType, fieldsMap.get(String.valueOf(m.method.selector))))
				fieldsFromType.put(String.valueOf(m.method.selector), copyType(m.method.returnType));
		MethodDeclaration mWith = newMethod();
		Argument arg = new Argument("val".toCharArray(), p, copyType(type), Modifier.NONE);
		mWith.modifiers = ClassFileConstants.AccPublic;
		mWith.returnType = copyType(meType);
		mWith.selector = "with".toCharArray();
		mWith.arguments = new Argument[]{ arg };
		MessageSend invokeOf = new MessageSend();
		invokeOf.receiver = new SingleNameReference(meDecl.name, p);
		invokeOf.selector = "of".toCharArray();
		invokeOf.arguments = new Expression[fieldNames.length];
		for (int i = 0; i < fieldNames.length; i++) {
			MessageSend tempM = new MessageSend();
			tempM.receiver = new ThisReference(pS, pE);
			if (fieldsFromType.containsKey(fieldNames[i])) tempM.receiver = new SingleNameReference("val".toCharArray(), p);
			tempM.selector = fieldNames[i].toCharArray();
			tempM.arguments = null;
			invokeOf.arguments[i] = tempM;
		}		
		InstanceOfExpression instanceOfExp = new InstanceOfExpression(new SingleNameReference("val".toCharArray(), p), copyType(meType));
		CastExpression castExp = makeCastExpression(new SingleNameReference("val".toCharArray(), p), copyType(meType), _ast);
		ReturnStatement returnCast = new ReturnStatement(castExp, pS, pE);
		IfStatement ifStmt = new IfStatement(instanceOfExp, returnCast, pS, pE);
		ReturnStatement returnW = new ReturnStatement(invokeOf, pS, pE);
		mWith.statements = new Statement[] { ifStmt, returnW };
		return mWith;
	}
	
	private void genAbstractGeneralWith(TypeReference type) {
		Argument arg = new Argument("val".toCharArray(), p, copyType(type), Modifier.NONE);
		MethodDeclaration mWith = newMethod();
		mWith.returnType = copyType(meType);
		mWith.selector = "with".toCharArray();
		mWith.arguments = new Argument[]{ arg };
		injectMethod(me, mWith);
	}
	
	private void genOfMethod() {
		MethodDeclaration of = newMethod();
		of.modifiers = ClassFileConstants.AccStatic;
		of.returnType = copyType(meType);
		of.selector = "of".toCharArray();
		of.arguments = ofArgs.size() == 0 ? null : ofArgs.toArray(new Argument[ofArgs.size()]);		
		TypeDeclaration anonymous = new TypeDeclaration(meDecl.compilationResult);
		anonymous.bits |= (ASTNode.IsAnonymousType | ASTNode.IsLocalType);
		anonymous.name = CharOperation.NO_CHAR;
		anonymous.typeParameters = null;
		anonymous.fields = ofFields.size() == 0 ? null : ofFields.toArray(new FieldDeclaration[ofFields.size()]);
		anonymous.methods = ofMethods.size() == 0 ? null : ofMethods.toArray(new MethodDeclaration[ofMethods.size()]);		
		QualifiedAllocationExpression alloc = new QualifiedAllocationExpression(anonymous);
		alloc.type = copyType(meType);
		ReturnStatement returnStmt = new ReturnStatement(alloc, pS, pE);
		of.statements = new Statement[] { returnStmt };
		injectMethod(me, of);
	}
	
	private boolean ofAlreadyExists() {
		for (EclipseNode x : me.down()) {
			if (!(x.get() instanceof MethodDeclaration)) continue;
			MethodDeclaration m = (MethodDeclaration) x.get();
			if (!String.valueOf(m.selector).equals("of")) continue;
			if (m.arguments == null || m.arguments.length == 0) {
				if (fieldNames == null || fieldNames.length == 0) return true;
				else continue;
			} else {
				if (fieldNames == null || fieldNames.length != m.arguments.length) continue;
				else {
					for (int i = 0; i < fieldNames.length; i++)
						if (!Util.sameType(fieldsMap.get(fieldNames[i]), m.arguments[i].type)) continue;
					return true;
				}
			}
		}
		return false;
	}
	
	/* Auxiliary: get type declaration from its name. */
	private EclipseNode getTypeDecl(char[] name) {
		for (EclipseNode x : me.up().down()) {
			if (!(x.get() instanceof TypeDeclaration)) continue;
			TypeDeclaration y = (TypeDeclaration) x.get();
			if (!Util.isInterface(y)) continue;
			if (Arrays.equals(name, y.name)) return x;
		}
		return null;
	}

	/* Auxiliary: subtyping. */
	private boolean subType(TypeReference t1, TypeReference t2) {
		if (!(t1 instanceof SingleTypeReference)) return false;
		if (!(t2 instanceof SingleTypeReference)) return false;
		if (Util.sameType(t1, t2)) return true;
		EclipseNode d1 = getTypeDecl(((SingleTypeReference) t1).token);
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
	
	/* Auxiliary: subtyping. */
	private boolean subType(Method m1, Method m2) {
		return subType(m1.method.returnType, m2.method.returnType);
	}
	
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
	private void throwError(String s) { errorMsg = s; }
	
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
	
}

class Util {
	static boolean sameType(TypeReference t1, TypeReference t2) {
		if (!(t1 instanceof SingleTypeReference)) return false;
		if (!(t2 instanceof SingleTypeReference)) return false;
		return Arrays.equals(((SingleTypeReference) t1).token, ((SingleTypeReference) t2).token);
	}
	static boolean isInterface(TypeDeclaration t) {
		return (t.modifiers & ClassFileConstants.AccInterface) != 0;
	}
	static String toString(TypeReference t) {
		return (t instanceof SingleTypeReference) ? String.valueOf(((SingleTypeReference) t).token) : "***";
	}
	static String toString(MethodDeclaration m) {
		String args = "";
		if (m.arguments != null) {
			for (int i = 0; i < m.arguments.length; i++)
				args += ", " + toString(m.arguments[i].type) + " " + String.valueOf(m.arguments[i].name);
			if (args.length() > 0) args = args.substring(2, args.length());
		}
		String modifier = "";
		if (m.isDefaultMethod()) modifier = "default ";
		if (m.isStatic()) modifier = "static ";
		return modifier + toString(m.returnType) + " " + String.valueOf(m.selector) + "(" + args + ");";
	}
	static boolean isField(MethodDeclaration m) {
		if (m.isDefaultMethod() || m.isStatic() || isVoidMethod(m)) return false;
		if (m.arguments != null && m.arguments.length != 0) return false;
		if (!Character.isLowerCase(m.selector[0])) return false;
		return true;
	}
	static boolean isAbstractMethod(MethodDeclaration m) {
		return !m.isDefaultMethod() && !m.isStatic();	
	}
	static boolean isVoidMethod(MethodDeclaration m) {
		if (!(m.returnType instanceof SingleTypeReference)) return false;
		return Arrays.equals(TypeConstants.VOID, ((SingleTypeReference) m.returnType).token);
	}
	static String toUpperCase(String s) {
		return s.isEmpty() ? s : s.substring(0, 1).toUpperCase() + s.substring(1, s.length());
	}
	static String toLowerCase(String s) {
		return s.isEmpty() ? s : s.substring(0, 1).toLowerCase() + s.substring(1, s.length());
	}
}

class Method {
	MethodDeclaration method;
	TypeReference origin;
	Method(MethodDeclaration method, TypeReference origin) { this.method = method; this.origin = origin; }
	boolean equals(Method _m) {
		MethodDeclaration m = _m.method;
		if (!Arrays.equals(m.selector, method.selector)) return false;
		int length1 = 0, length2 = 0;
		if (m.arguments != null) length1 = m.arguments.length;
		if (method.arguments != null) length2 = method.arguments.length;
		if (length1 != length2) return false;
		for (int i = 0; i < length1; i++) {
			TypeReference t1 = copyType(m.arguments[i].type);
			TypeReference t2 = copyType(method.arguments[i].type);
			if (!Util.sameType(t1, t2)) return false;
		}
		return true;
	}
	@Override public String toString() {
		return Util.toString(method);
	}
}

class Type {
	Method[] methods;
	Type(Method[] ms) {
		methods = new Method[ms.length];
		for (int i = 0; i < ms.length; i++) methods[i] = ms[i]; 
	}
	@Override public String toString() {
		String res = "";
		for (int i = 0; i < methods.length; i++) res += methods[i].toString() + "\n";
		return res;
	}
}
