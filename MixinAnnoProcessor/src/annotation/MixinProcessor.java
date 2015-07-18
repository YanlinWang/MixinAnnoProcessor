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

        String res = "package " + folder + ";\n\n"
                + "public interface " + algName + " extends " + baseName + " {\n\n";


        Map<String, TypeMirror> fields = getFields(element);

        res += TAB + "static " + algName + " of(" + fieldsString(fields) + ") { "
                + "return new " + algName + "() {\n";

        res += genGetters(fields);
       
        List<? extends Element> le = element.getEnclosedElements();
        for (Element e : le) {
            String name = e.getSimpleName().toString();
            if (isSetter(e, fields)) res += genSetter(name, fields.get(name));
            else if (isWithMethod(e)) res += genWithMethod(element, (ExecutableElement) e, algName, fields);
            else if (isCloneMethod(e)) res += genCloneMethod(algName, (ExecutableElement) e, fields);
        }
               
        res += "};}\n";
        res += "}";
        return res;
    }

   
    private String memberFieldNames(Map<String, TypeMirror> fields) {
        String res = "";
        boolean firstField = true;
        for (String field : fields.keySet()) {
            if (!firstField) { res += ", "; }
            res += memberName(field);
            firstField = false;
        }
        return res;
    }
    private String fieldsString(Map<String, TypeMirror> fields) {
        String res = "";
        boolean firstField = true;
        for (String field : fields.keySet()) {
            if (!firstField) { res += ", "; }
            res += fields.get(field) + " " + field;
            firstField = false;
        }
        return res;
    }
    
    private String genCloneMethod(String intfName, ExecutableElement e, Map<String, TypeMirror> fields) {
        return TAB2 + "public " + intfName + " clone() { return of(" + memberFieldNames(fields) + "); }\n";
    }

    private String genSetter(String name, TypeMirror tm) {
        String res = "";
        res += TAB2 + "public void " + name + "(" + tm.toString() + " " + name + ") {" + memberName(name) + " = " + name + "; }\n";
        return res;
    }

    private String genGetters(Map<String, TypeMirror> fields) {
        String res = "";
        for (String field : fields.keySet()) {
            res += TAB2 + fields.get(field) + " " + memberName(field) + " = " + field + ";\n";
            res += TAB2 + "public " + fields.get(field) + " " + field + "() { return " + memberName(field) + "; }\n";
        }
        return res;
    }
    
    private String memberName(String name) { return "_" + name; }

    private boolean noArgs(ExecutableElement e) { return e.getParameters().size() == 0; }
    private boolean isFieldMethod(Element e) { return !isCloneMethod(e) 
            && isAbstractMethod(e) && noArgs((ExecutableElement)e); }
    private boolean isMethod(Element e) { return e.getKind() == ElementKind.METHOD; }
    private boolean isAbstract(Element e) { return e.getModifiers().contains(Modifier.ABSTRACT); }
    private boolean isAbstractMethod(Element e) { return isMethod(e) && isAbstract(e); }
    private boolean isWithMethod(Element e) { return isMethod(e) && e.getSimpleName().toString().startsWith("with"); }
    private boolean isSetter(Element e, Map<String, TypeMirror> fields) {
        if (isAbstractMethod(e)) {
            ExecutableElement ee = (ExecutableElement)e;
            String methodName = ee.getSimpleName().toString();
            List<? extends VariableElement> params = ee.getParameters();
            if (ee.getReturnType().toString().equals("void")) { // return type is void
                if (fields.containsKey(methodName)) {   // setter name ok
                    if (params.size() == 1 && params.get(0).asType().equals(fields.get(methodName))) {
                        return true;
                    }
                }
            }
      
        }
        return false;
    }
    private boolean isCloneMethod(Element e) { return isAbstractMethod(e) && e.getSimpleName().toString().equals("clone"); }
    
    private String decapitalize(String string) {
        return Character.toLowerCase(string.charAt(0)) + (string.length() > 1 ? string.substring(1) : "");
    }

    private String genWithMethod(Element element, ExecutableElement withMethod, String algName, Map<String, TypeMirror> fields) {
        String baseName = getPackage(element) + "." + algName;

        String res = "";

        String methodName = withMethod.getSimpleName().toString();
        String name = methodName.substring(4, methodName.length());
        name = decapitalize(name);

        if (fields.containsKey(name)) {
            List<? extends VariableElement> params = withMethod.getParameters();
            if (withMethod.getReturnType().toString().equals(baseName)) {
                if (params.size() == 1 && params.get(0).asType().equals(fields.get(name))) {
                    String pType = params.get(0).asType().toString();
                    res += TAB2 + "public " + algName + " " + methodName + "(" + pType + " " + name + ") {\n" ;
                    res += TAB3 + "return of(";

                    boolean firstField1 = true;
                    for (String field : fields.keySet()) {
                        if (!firstField1) { res += ", "; }
                        if (field.equals(name)) {                  
                            res += field;
                        } else {
                            res += field + "()";
                        }
                        firstField1 = false;
                    }

                    res += ");\n" + TAB2 + "}\n";
                } else processingEnv.getMessager().printMessage(Kind.ERROR, "method parameter error!", withMethod);
            } else processingEnv.getMessager().printMessage(Kind.ERROR, "method return type error!", withMethod);
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