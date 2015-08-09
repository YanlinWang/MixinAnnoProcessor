/*
 * Copyright (C) 2012 The Project Lombok Authors.
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import lombok.Lombok;

import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;


public class PatchExtensionMethodCompletionProposalPortal {
	private static final String COMPLETION_PROPOSAL_COLLECTOR = "org.eclipse.jdt.ui.text.java.CompletionProposalCollector";
	private static final String I_JAVA_COMPLETION_PROPOSAL_ARRAY = "[Lorg.eclipse.jdt.ui.text.java.IJavaCompletionProposal;";
	
	public static IJavaCompletionProposal[] getJavaCompletionProposals(Object[] javaCompletionProposals, Object completionProposalCollector) {
		try {
			return (IJavaCompletionProposal[]) ReflectionForUi.getJavaCompletionProposals.invoke(null, javaCompletionProposals, completionProposalCollector);
		} catch (NoClassDefFoundError e) {
			//ignore, we don't have access to the correct ECJ classes, so lombok can't possibly
			//do anything useful here.
			return (IJavaCompletionProposal[]) javaCompletionProposals;
		} catch (IllegalAccessException e) {
			throw Lombok.sneakyThrow(e);
		} catch (InvocationTargetException e) {
			throw Lombok.sneakyThrow(e.getCause());
		} catch (NullPointerException e) {
			if (!"false".equals(System.getProperty("lombok.debug.reflection", "false"))) {
				e.initCause(ReflectionForUi.problem);
				throw e;
			}
			//ignore, we don't have access to the correct ECJ classes, so lombok can't possibly
			//do anything useful here.
			return (IJavaCompletionProposal[]) javaCompletionProposals;
		}
	}
	
	private static final class ReflectionForUi {
		public static final Method getJavaCompletionProposals;
		public static final Throwable problem;
		
		static {
			Method p = null;
			Throwable problem_ = null;
			try {
				p = PatchExtensionMethodCompletionProposal.class.getMethod("getJavaCompletionProposals", Class.forName(I_JAVA_COMPLETION_PROPOSAL_ARRAY), Class.forName(COMPLETION_PROPOSAL_COLLECTOR));
			} catch (Throwable t) {
				// That's problematic, but as long as no local classes are used we don't actually need it.
				// Better fail on local classes than crash altogether.
				problem_ = t;
			}
			getJavaCompletionProposals = p;
			problem = problem_;
		}
	}
}
