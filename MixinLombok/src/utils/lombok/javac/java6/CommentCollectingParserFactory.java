/*
 * Copyright (C) 2013-2014 The Project Lombok Authors.
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
package lombok.javac.java6;

import java.lang.reflect.Field;

import com.sun.tools.javac.main.JavaCompiler;
import com.sun.tools.javac.parser.Lexer;
import com.sun.tools.javac.parser.Parser;
import com.sun.tools.javac.util.Context;

public class CommentCollectingParserFactory extends Parser.Factory {
	static Context.Key<Parser.Factory> key() {
		return parserFactoryKey;
	}
	
	protected CommentCollectingParserFactory(Context context) {
		super(context);
	}
	
	@Override public Parser newParser(Lexer S, boolean keepDocComments, boolean genEndPos) {
		Object x = new CommentCollectingParser(this, S, true);
		return (Parser) x;
		// CCP is based on a stub which extends nothing, but at runtime the stub is replaced with either
		//javac6's EndPosParser which extends Parser, or javac7's EndPosParser which implements Parser.
		//Either way this will work out.
	}
	
	public static void setInCompiler(JavaCompiler compiler, Context context) {
		context.put(CommentCollectingParserFactory.key(), (Parser.Factory)null);
		Field field;
		try {
			field = JavaCompiler.class.getDeclaredField("parserFactory");
			field.setAccessible(true);
			field.set(compiler, new CommentCollectingParserFactory(context));
		} catch (Exception e) {
			throw new IllegalStateException("Could not set comment sensitive parser in the compiler", e);
		}
	}
}