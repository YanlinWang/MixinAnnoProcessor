package annotation;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes(value={"annotation.Mixin"})
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class MixinProcessor extends AbstractProcessor {
    private Filer filer;
    public static final String TAB = "\t";
    public static final String TAB2 = "\t\t";
    
    @Override
    public void init(ProcessingEnvironment env){
        filer = env.getFiler();
    }
     
    @Override
    public boolean process(Set<? extends TypeElement> annotations,
            RoundEnvironment env) {
        String folder = null;
        String classContent = null;
        String algName;
        JavaFileObject jfo = null;
        for (Element element: env.getElementsAnnotatedWith(Mixin.class)) {            
            // Initialization.
            TypeMirror tm = element.asType();
            String typeArgs = tm.accept(new DeclaredTypeVisitor(), element);
            String[] lTypeArgs = toList(typeArgs);
            algName = element.getSimpleName().toString();
            
            // Create Reflective Algebra classes "ReflAlgName". E.g. ExpAlg -> ReflExpAlg
            // One issue here. Using "java.util.List" instead of "List".
            folder = "mixin";
            classContent = createMixinClass(folder, element, lTypeArgs, typeArgs);
            jfo = null;
            try{
                jfo = filer.createSourceFile(folder + "/" + algName, element);
                jfo.openWriter().append(classContent).close();
            }catch(IOException ioe){
                ioe.printStackTrace();
            }
        }
        return true;        
    }
    
    String createMixinClass(String folder, Element element, String[] lTypeArgs, String typeArgs) {
        String algName = element.getSimpleName().toString();
        String baseName = getPackage(element) + "." + algName;
        
        String classContent = "package " + folder + ";\n\n"
                + "public interface " + algName + " extends " + baseName + " {\n\n";
        
        List<? extends Element> le = element.getEnclosedElements();
        
        Map<String, String> fields = new HashMap<String, String>();
        
        for (Element e: le){
            
            if (isAbstractMethod(e)) {
                String methodName = e.getSimpleName().toString();
                String[] args = {methodName, typeArgs, algName};
                String returntype = e.asType().accept(new ReturnTypeVisitor(), args);; //TODO:  how to get method return type
                fields.put(methodName, returntype);
            }
        }
                
        classContent += TAB + "static " + algName + " of(";
        boolean firstField = true;
        for (String field : fields.keySet()) {
            if (!firstField) { classContent += ", "; }
            classContent += fields.get(field) + " " + field;
            firstField = false;
        }
        classContent += ") { return new " + algName + "() {\n";
        for (String field : fields.keySet()) {
            classContent += TAB2 + "public " + fields.get(field) + " " + field + "() { return " + field + "; }\n";
        }
        classContent += "};}\n";
                
        
             
            
            
            //----------- debug code begin --------------
//            classContent += "e.getKind(): " + e.getKind() + "\n";
//            classContent += "e.getClass(): " + e.getClass() + "\n";
//            classContent += "e.getKind(): " + e.getKind() + "\n";
//            classContent += "e.getModifiers(): " + e.getModifiers() + "\n";
//            classContent += "Element e: " + e + "\n";
//            classContent += "e.asType(): " + e.asType().toString() + "\n";
//            classContent += "typeArgs: " + typeArgs + "\n";
//            classContent += "lTypeArgs: " + lTypeArgs.toString() + "\n\n\n"; 
          //----------- debug code end -------------- 
        classContent += "}";
        return classContent;
    }
    
    private boolean isAbstractMethod(Element e) {
        return e.getKind() == ElementKind.METHOD && e.getModifiers().contains(Modifier.ABSTRACT);
    }  
    
    
    private String[] toList(String message) {
        return message.split(",");
    }

    @Override
    public SourceVersion getSupportedSourceVersion(){
        return SourceVersion.latestSupported();
    }
    
    private String getPackage(Element element) {
        return ((PackageElement)element.getEnclosingElement()).getQualifiedName().toString();
    }
}
