package annotation;

import java.beans.Introspector;
import java.io.IOException;
import java.lang.reflect.Executable;
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
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes(value={"annotation.Mixin"})
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class MixinProcessor extends AbstractProcessor {
    private ProcessingEnvironment processingEnv;
    private Filer filer;
    public static final String TAB = "\t";
    public static final String TAB2 = "\t\t";
    public static final String TAB3 = "\t\t\t";

    @Override
    public void init(ProcessingEnvironment env){
        processingEnv = env;
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

    private Map<String, TypeMirror> getFields(Element element) {
        Map<String, TypeMirror> fields = new HashMap<String, TypeMirror>();
        List<? extends Element> le = element.getEnclosedElements();
        for (Element e: le){
            if (isFieldMethod(e)) {
                String methodName = e.getSimpleName().toString();
                ExecutableElement ee = ((ExecutableElement)e);
                TypeMirror returntype = ee.getReturnType();
                fields.put(methodName, returntype);
            }
        }
        return fields;
    }
    
    String createMixinClass(String folder, Element element, String[] lTypeArgs, String typeArgs) {
        String algName = element.getSimpleName().toString();
        String baseName = getPackage(element) + "." + algName;

        String classContent = "package " + folder + ";\n\n"
                + "public interface " + algName + " extends " + baseName + " {\n\n";


        Map<String, TypeMirror> fields = getFields(element); 


        

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
        
        // begin: deal with `withX` methods
        classContent += genWithMethod(element, algName, fields);
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

    private boolean noArgs(ExecutableElement e) { return e.getParameters().size() == 0; }
    private boolean isFieldMethod(Element e) { return isAbstractMethod(e) && noArgs((ExecutableElement)e); }
    private boolean isMethod(Element e) { return e.getKind() == ElementKind.METHOD; }
    private boolean isAbstract(Element e) { return e.getModifiers().contains(Modifier.ABSTRACT); }
    private boolean isAbstractMethod(Element e) { return isMethod(e) && isAbstract(e); }
    private boolean isWithMethod(Element e) { return isMethod(e) && e.getSimpleName().toString().startsWith("with"); }
    
    private String genWithMethod(Element element, String algName, Map<String, TypeMirror> fields) {
        String res = "";
        List<? extends Element> le = element.getEnclosedElements();
        for (Element e : le) {
            if (isWithMethod(e)) {
                String methodName = e.getSimpleName().toString();
                String name = methodName.substring(4, methodName.length());
                name = Introspector.decapitalize(name);
               
                if (fields.containsKey(name)) {
                    ExecutableElement ee = (ExecutableElement)e;
                    List<? extends VariableElement> params = ee.getParameters();
                    if (params.size() == 1 && params.get(0).asType().equals(fields.get(name))) {
                        String pName = params.get(0).getSimpleName().toString();
                        String pType = params.get(0).asType().toString();
                        res += TAB2 + "public " + algName + " " + methodName + "(" + pType + " " + pName + ") {\n" ;
                        res += TAB3 + "return of(";
                        
                        boolean firstField1 = true;
                        for (String field : fields.keySet()) {
                            if (!firstField1) { res += ", "; }
                            if (field.equals(pName)) {                   
                                res += field;
                            } else {
                                res += field + "()";
                            }
                            firstField1 = false;
                        }
                        
                        res += ");\n" + TAB2 + "}\n";
                    } else {
                        processingEnv.getMessager().printMessage(Kind.ERROR, "with method error!", e);
                    }
                } else {
                    processingEnv.getMessager().printMessage(Kind.ERROR, "with method error!", e);
                }
            }
        }
        return res;
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
