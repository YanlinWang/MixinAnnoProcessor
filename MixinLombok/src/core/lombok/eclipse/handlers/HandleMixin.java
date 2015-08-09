package lombok.eclipse.handlers;

import java.util.ArrayList;
import java.util.Arrays;

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
	ASTNode _ast;
	Annotation _src;
	Argument[] argFields;
	ArrayList<MethodDeclaration> fields;
	String info;
	
	@Override public void handle(AnnotationValues<Mixin> annotation, Annotation ast, EclipseNode annotationNode) {
		pS = ast.sourceStart;
		pE = ast.sourceEnd;
		p = (long)pS << 32 | pE;
		me = annotationNode.up();
		meDecl = (TypeDeclaration) me.get();
		_ast = annotationNode.get();
		_src = ast;
		handleMixin();
		createOfMethod();
	}
	private void handleMixin() {
		info = "";
		// Check: the annotated type is an interface.
		// Check if a method is static. Has no type parameters.
		fields = new ArrayList<MethodDeclaration>();
		for (EclipseNode x : me.down()) {
			if (!(x.get() instanceof MethodDeclaration)) continue;
			MethodDeclaration y = (MethodDeclaration) x.get();
			if (y.isDefaultMethod()) continue;
			if (!(y.returnType instanceof SingleTypeReference)) continue;
			if (!Arrays.equals(TypeConstants.VOID, ((SingleTypeReference) y.returnType).token)) {
				if (y.arguments != null && y.arguments.length != 0) continue;
				fields.add(y);
			}
		}
		// Check if a field has its setter correspondingly.
		argFields = new Argument[fields.size()];		
		for (int i = 0; i < fields.size(); i++) {
			MethodDeclaration elem = fields.get(i);
			argFields[i] = new Argument(elem.selector, p, copyType(elem.returnType), Modifier.NONE);
		}
		// TODO: two interfaces.
	}
	
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
		of.arguments = argFields;
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
		FieldDeclaration[] of_fields = new FieldDeclaration[fields.size()];
		MethodDeclaration[] of_methods = new MethodDeclaration[2 * fields.size()];
		int index1 = 0, index2 = 0;
		for (int i = 0; i < fields.size(); i++) {
			MethodDeclaration elem = fields.get(i);
			Argument arg = new Argument(elem.selector, p, copyType(elem.returnType), Modifier.NONE);
			FieldDeclaration f = new FieldDeclaration(("_" + String.valueOf(arg.name)).toCharArray(), pS, pE);
			f.bits |= Eclipse.ECLIPSE_DO_NOT_TOUCH_FLAG;
			f.modifiers = ClassFileConstants.AccDefault;
			f.type = copyType(arg.type);
			f.initialization = new SingleNameReference(arg.name, p);
			of_fields[index1++] = f;
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
			of_methods[index2++] = mSetter;
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
			of_methods[index2++] = mGetter;
		}
		TypeDeclaration anonymous = new TypeDeclaration(meDecl.compilationResult);
		anonymous.bits |= (ASTNode.IsAnonymousType | ASTNode.IsLocalType);
		anonymous.name = CharOperation.NO_CHAR;
		anonymous.typeParameters = null;
		anonymous.fields = of_fields;
		anonymous.methods = of_methods;
		return anonymous;
	}
}
