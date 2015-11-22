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
package lombok.javac;

import java.util.ArrayList;
import java.util.Collection;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCImport;
import com.sun.tools.javac.util.List;

import lombok.core.ImportList;
import lombok.core.LombokInternalAliasing;

public class JavacImportList implements ImportList {
	private final JCExpression pkg;
	private final List<JCTree> defs;
	
	public JavacImportList(JCCompilationUnit cud) {
		this.pkg = cud.pid;
		this.defs = cud.defs;
	}
	
	@Override public String getFullyQualifiedNameForSimpleName(String unqualified) {
		for (JCTree def : defs) {
			if (!(def instanceof JCImport)) continue;
			JCTree qual = ((JCImport) def).qualid;
			if (!(qual instanceof JCFieldAccess)) continue;
			String simpleName = ((JCFieldAccess) qual).name.toString();
			if (simpleName.equals(unqualified)) {
				return LombokInternalAliasing.processAliases(qual.toString());
			}
		}
		
		return null;
	}
	
	@Override public boolean hasStarImport(String packageName) {
		String pkgStr = pkg == null ? null : pkg.toString();
		if (pkgStr != null && pkgStr.equals(packageName)) return true;
		if ("java.lang".equals(packageName)) return true;
		
		if (pkgStr != null) {
			Collection<String> extra = LombokInternalAliasing.IMPLIED_EXTRA_STAR_IMPORTS.get(pkgStr);
			if (extra != null && extra.contains(packageName)) return true;
		}
		
		for (JCTree def : defs) {
			if (!(def instanceof JCImport)) continue;
			if (((JCImport) def).staticImport) continue;
			JCTree qual = ((JCImport) def).qualid;
			if (!(qual instanceof JCFieldAccess)) continue;
			String simpleName = ((JCFieldAccess) qual).name.toString();
			if (!"*".equals(simpleName)) continue;
			String starImport = ((JCFieldAccess) qual).selected.toString();
			if (packageName.equals(starImport)) return true;
			Collection<String> extra = LombokInternalAliasing.IMPLIED_EXTRA_STAR_IMPORTS.get(starImport);
			if (extra != null && extra.contains(packageName)) return true;
		}
		
		return false;
	}
	
	@Override public Collection<String> applyNameToStarImports(String startsWith, String name) {
		ArrayList<String> out = new ArrayList<String>();
		
		if (pkg != null && topLevelName(pkg).equals(startsWith)) out.add(pkg.toString() + "." + name);
		
		for (JCTree def : defs) {
			if (!(def instanceof JCImport)) continue;
			if (((JCImport) def).staticImport) continue;
			JCTree qual = ((JCImport) def).qualid;
			if (!(qual instanceof JCFieldAccess)) continue;
			String simpleName = ((JCFieldAccess) qual).name.toString();
			if (!"*".equals(simpleName)) continue;
			
			String topLevelName = topLevelName(qual);
			if (topLevelName.equals(startsWith)) {
				out.add(((JCFieldAccess) qual).selected.toString() + "." + name);
			}
		}
		
		return out;
	}
	
	private String topLevelName(JCTree tree) {
		while (tree instanceof JCFieldAccess) tree = ((JCFieldAccess) tree).selected;
		return tree.toString();
	}
	
	@Override public String applyUnqualifiedNameToPackage(String unqualified) {
		if (pkg == null) return unqualified;
		return pkg.toString() + "." + unqualified;
	}
}
