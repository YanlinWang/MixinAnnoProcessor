package lombok.javac.handlers;

import org.mangosdk.spi.ProviderFor;

import com.sun.tools.javac.tree.JCTree.*;

import lombok.Mixin;
import lombok.core.AnnotationValues;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;

@ProviderFor(JavacAnnotationHandler.class)
public class HandleMixin extends JavacAnnotationHandler<Mixin> {	
	@Override public void handle(AnnotationValues<Mixin> annotation, JCAnnotation ast, JavacNode annotationNode) {	
	}
}
