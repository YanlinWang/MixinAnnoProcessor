/*
 * Copyright (C) 2009-2015 The Project Lombok Authors.
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
package lombok.javac;

import static lombok.javac.JavacTreeMaker.TreeTag.treeTag;
import static lombok.javac.JavacTreeMaker.TypeTag.typeTag;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeVisitor;

import lombok.javac.JavacTreeMaker.TreeTag;
import lombok.javac.JavacTreeMaker.TypeTag;

import com.sun.tools.javac.code.Source;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.main.JavaCompiler;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCLiteral;
import com.sun.tools.javac.util.JCDiagnostic.DiagnosticPosition;

/**
 * Container for static utility methods relevant to lombok's operation on javac.
 */
public class Javac {
	private Javac() {
		// prevent instantiation
	}
	
	/** Matches any of the 8 primitive names, such as {@code boolean}. */
	private static final Pattern PRIMITIVE_TYPE_NAME_PATTERN = Pattern.compile("^(boolean|byte|short|int|long|float|double|char)$");
	
	private static final Pattern VERSION_PARSER = Pattern.compile("^(\\d{1,6})\\.(\\d{1,6}).*$");
	private static final Pattern SOURCE_PARSER = Pattern.compile("^JDK(\\d{1,6})_(\\d{1,6}).*$");
	
	private static final AtomicInteger compilerVersion = new AtomicInteger(-1);
	
	/**
	 * Returns the version of this java compiler, i.e. the JDK that it shipped in. For example, for javac v1.7, this returns {@code 7}.
	 */
	public static int getJavaCompilerVersion() {
		int cv = compilerVersion.get();
		if (cv != -1) return cv;
		
		/* Main algorithm: Use JavaCompiler's intended method to do this */ {
			Matcher m = VERSION_PARSER.matcher(JavaCompiler.version());
			if (m.matches()) {
				int major = Integer.parseInt(m.group(1));
				int minor = Integer.parseInt(m.group(2));
				if (major == 1) {
					compilerVersion.set(minor);
					return minor;
				}
			}
		}
		
		/* Fallback algorithm one: Check Source's values. Lets hope oracle never releases a javac that recognizes future versions for -source */ {
			String name = Source.values()[Source.values().length - 1].name();
			Matcher m = SOURCE_PARSER.matcher(name);
			if (m.matches()) {
				int major = Integer.parseInt(m.group(1));
				int minor = Integer.parseInt(m.group(2));
				if (major == 1) {
					compilerVersion.set(minor);
					return minor;
				}
			}
		}
		
		compilerVersion.set(6);
		return 6;
	}
	
	private static final Class<?> DOCCOMMENTTABLE_CLASS;
	
	static {
		Class<?> c = null;
		try {
			c = Class.forName("com.sun.tools.javac.tree.DocCommentTable");
		} catch (Throwable ignore) {}
		DOCCOMMENTTABLE_CLASS = c;
	}
	
	public static boolean instanceOfDocCommentTable(Object o) {
		return DOCCOMMENTTABLE_CLASS != null && DOCCOMMENTTABLE_CLASS.isInstance(o);
	}
	
	/**
	 * Checks if the given expression (that really ought to refer to a type
	 * expression) represents a primitive type.
	 */
	public static boolean isPrimitive(JCExpression ref) {
		String typeName = ref.toString();
		return PRIMITIVE_TYPE_NAME_PATTERN.matcher(typeName).matches();
	}
	
	/**
	 * Turns an expression into a guessed intended literal. Only works for
	 * literals, as you can imagine.
	 * 
	 * Will for example turn a TrueLiteral into 'Boolean.valueOf(true)'.
	 */
	public static Object calculateGuess(JCExpression expr) {
		if (expr instanceof JCLiteral) {
			JCLiteral lit = (JCLiteral) expr;
			if (lit.getKind() == com.sun.source.tree.Tree.Kind.BOOLEAN_LITERAL) {
				return ((Number) lit.value).intValue() == 0 ? false : true;
			}
			return lit.value;
		} else if (expr instanceof JCIdent || expr instanceof JCFieldAccess) {
			String x = expr.toString();
			if (x.endsWith(".class")) x = x.substring(0, x.length() - 6);
			else {
				int idx = x.lastIndexOf('.');
				if (idx > -1) x = x.substring(idx + 1);
			}
			return x;
		} else
			return null;
	}
	
	public static final TypeTag CTC_BOOLEAN = typeTag("BOOLEAN");
	public static final TypeTag CTC_INT = typeTag("INT");
	public static final TypeTag CTC_DOUBLE = typeTag("DOUBLE");
	public static final TypeTag CTC_FLOAT = typeTag("FLOAT");
	public static final TypeTag CTC_SHORT = typeTag("SHORT");
	public static final TypeTag CTC_BYTE = typeTag("BYTE");
	public static final TypeTag CTC_LONG = typeTag("LONG");
	public static final TypeTag CTC_CHAR = typeTag("CHAR");
	public static final TypeTag CTC_VOID = typeTag("VOID");
	public static final TypeTag CTC_NONE = typeTag("NONE");
	public static final TypeTag CTC_BOT = typeTag("BOT");
	public static final TypeTag CTC_ERROR = typeTag("ERROR");
	public static final TypeTag CTC_UNKNOWN = typeTag("UNKNOWN");
	public static final TypeTag CTC_UNDETVAR = typeTag("UNDETVAR");
	public static final TypeTag CTC_CLASS = typeTag("CLASS");
	
	public static final TreeTag CTC_NOT_EQUAL = treeTag("NE");
	public static final TreeTag CTC_LESS_THAN = treeTag("LT");
	public static final TreeTag CTC_GREATER_THAN = treeTag("GT");
	public static final TreeTag CTC_LESS_OR_EQUAL= treeTag("LE");
	public static final TreeTag CTC_GREATER_OR_EQUAL = treeTag("GE");
	public static final TreeTag CTC_POS = treeTag("POS");
	public static final TreeTag CTC_NEG = treeTag("NEG");
	public static final TreeTag CTC_NOT = treeTag("NOT");
	public static final TreeTag CTC_COMPL = treeTag("COMPL");
	public static final TreeTag CTC_BITXOR = treeTag("BITXOR");
	public static final TreeTag CTC_UNSIGNED_SHIFT_RIGHT = treeTag("USR");
	public static final TreeTag CTC_MUL = treeTag("MUL");
	public static final TreeTag CTC_DIV = treeTag("DIV");
	public static final TreeTag CTC_PLUS = treeTag("PLUS");
	public static final TreeTag CTC_MINUS = treeTag("MINUS");
	public static final TreeTag CTC_EQUAL = treeTag("EQ");
	public static final TreeTag CTC_PREINC = treeTag("PREINC");
	public static final TreeTag CTC_PREDEC = treeTag("PREDEC");
	public static final TreeTag CTC_POSTINC = treeTag("POSTINC");
	public static final TreeTag CTC_POSTDEC = treeTag("POSTDEC");
	
	private static final Method getExtendsClause, getEndPosition, storeEnd;
	
	static {
		getExtendsClause = getMethod(JCClassDecl.class, "getExtendsClause", new Class<?>[0]);
		getExtendsClause.setAccessible(true);
		
		if (getJavaCompilerVersion() < 8) {
			getEndPosition = getMethod(DiagnosticPosition.class, "getEndPosition", java.util.Map.class);
			storeEnd = getMethod(java.util.Map.class, "put", Object.class, Object.class);
		} else {
			getEndPosition = getMethod(DiagnosticPosition.class, "getEndPosition", "com.sun.tools.javac.tree.EndPosTable");
			Method storeEndMethodTemp;
			Class<?> endPosTable;
			try {
				endPosTable = Class.forName("com.sun.tools.javac.tree.EndPosTable");
			} catch (ClassNotFoundException ex) {
				throw sneakyThrow(ex);
			}
			try {
				storeEndMethodTemp = endPosTable.getMethod("storeEnd", JCTree.class, int.class);
			} catch (NoSuchMethodException e) {
				try {
					endPosTable = Class.forName("com.sun.tools.javac.parser.JavacParser$AbstractEndPosTable");
					storeEndMethodTemp = endPosTable.getDeclaredMethod("storeEnd", JCTree.class, int.class);
				} catch (NoSuchMethodException ex) {
					throw sneakyThrow(ex);
				} catch (ClassNotFoundException ex) {
					throw sneakyThrow(ex);
				}
			}
			storeEnd = storeEndMethodTemp;
		}
		getEndPosition.setAccessible(true);
		storeEnd.setAccessible(true);
	}
	
	private static Method getMethod(Class<?> clazz, String name, Class<?>... paramTypes) {
		try {
			return clazz.getMethod(name, paramTypes);
		} catch (NoSuchMethodException e) {
			throw sneakyThrow(e);
		}
	}
	
	private static Method getMethod(Class<?> clazz, String name, String... paramTypes) {
		try {
			Class<?>[] c = new Class[paramTypes.length];
			for (int i = 0; i < paramTypes.length; i++) c[i] = Class.forName(paramTypes[i]);
			return clazz.getMethod(name, c);
		} catch (NoSuchMethodException e) {
			throw sneakyThrow(e);
		} catch (ClassNotFoundException e) {
			throw sneakyThrow(e);
		}
	}
	
	public static JCTree getExtendsClause(JCClassDecl decl) {
		try {
			return (JCTree) getExtendsClause.invoke(decl);
		} catch (IllegalAccessException e) {
			throw sneakyThrow(e);
		} catch (InvocationTargetException e) {
			throw sneakyThrow(e.getCause());
		}
	}
	
	public static Object getDocComments(JCCompilationUnit cu) {
		try {
			return JCCOMPILATIONUNIT_DOCCOMMENTS.get(cu);
		} catch (IllegalAccessException e) {
			throw sneakyThrow(e);
		}
	}
	
	public static void initDocComments(JCCompilationUnit cu) {
		try {
			JCCOMPILATIONUNIT_DOCCOMMENTS.set(cu, new HashMap<Object, String>());
		} catch (IllegalArgumentException e) {
			// That's fine - we're on JDK8, we'll fix that later.
		} catch (IllegalAccessException e) {
			throw sneakyThrow(e);
		}
	}
	
	public static int getEndPosition(DiagnosticPosition pos, JCCompilationUnit top) {
		try {
			Object endPositions = JCCOMPILATIONUNIT_ENDPOSITIONS.get(top);
			return (Integer) getEndPosition.invoke(pos, endPositions);
		} catch (IllegalAccessException e) {
			throw sneakyThrow(e);
		} catch (InvocationTargetException e) {
			throw sneakyThrow(e.getCause());
		}
	}
	
	public static void storeEnd(JCTree tree, int pos, JCCompilationUnit top) {
		try {
			Object endPositions = JCCOMPILATIONUNIT_ENDPOSITIONS.get(top);
			storeEnd.invoke(endPositions, tree, pos);
		} catch (IllegalAccessException e) {
			throw sneakyThrow(e);
		} catch (InvocationTargetException e) {
			throw sneakyThrow(e.getCause());
		}
	}

	private static final Class<?> JC_VOID_TYPE, JC_NO_TYPE;
	
	static {
		Class<?> c = null;
		try {
			c = Class.forName("com.sun.tools.javac.code.Type$JCVoidType");
		} catch (Throwable ignore) {}
		JC_VOID_TYPE = c;
		c = null;
		try {
			c = Class.forName("com.sun.tools.javac.code.Type$JCNoType");
		} catch (Throwable ignore) {}
		JC_NO_TYPE = c;
	}
	
	public static Type createVoidType(JavacTreeMaker maker, TypeTag tag) {
		if (Javac.getJavaCompilerVersion() < 8) {
			return new JCNoType(((Integer) tag.value).intValue());
		} else {
			try {
				if (CTC_VOID.equals(tag)) {
					return (Type) JC_VOID_TYPE.newInstance();
				} else {
					return (Type) JC_NO_TYPE.newInstance();
				}
			} catch (IllegalAccessException e) {
				throw sneakyThrow(e);
			} catch (InstantiationException e) {
				throw sneakyThrow(e);
			}
		}
	}
	
	private static class JCNoType extends Type implements NoType {
		public JCNoType(int tag) {
			super(tag, null);
		}
		
		@Override
		public TypeKind getKind() {
			if (tag == ((Integer) CTC_VOID.value).intValue()) return TypeKind.VOID;
			if (tag == ((Integer) CTC_NONE.value).intValue()) return TypeKind.NONE;
			throw new AssertionError("Unexpected tag: " + tag);
		}
		
		@Override
		public <R, P> R accept(TypeVisitor<R, P> v, P p) {
			return v.visitNoType(this, p);
		}
	}
	
	private static final Field JCCOMPILATIONUNIT_ENDPOSITIONS, JCCOMPILATIONUNIT_DOCCOMMENTS;
	static {
		Field f = null;
		try {
			f = JCCompilationUnit.class.getDeclaredField("endPositions");
		} catch (NoSuchFieldException e) {}
		JCCOMPILATIONUNIT_ENDPOSITIONS = f;
		
		f = null;
		try {
			f = JCCompilationUnit.class.getDeclaredField("docComments");
		} catch (NoSuchFieldException e) {}
		JCCOMPILATIONUNIT_DOCCOMMENTS = f;
	}
	
	static RuntimeException sneakyThrow(Throwable t) {
		if (t == null) throw new NullPointerException("t");
		Javac.<RuntimeException>sneakyThrow0(t);
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends Throwable> void sneakyThrow0(Throwable t) throws T {
		throw (T)t;
	}
}
