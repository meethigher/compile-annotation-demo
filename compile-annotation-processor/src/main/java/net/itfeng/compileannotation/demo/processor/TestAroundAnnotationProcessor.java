package net.itfeng.compileannotation.demo.processor;

import com.sun.source.util.Trees;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import net.itfeng.compileannotation.demo.annotation.TestAroundAnnotation;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * 注解TTestAroundAnnotation的解释器
 */
@SupportedAnnotationTypes("net.itfeng.compileannotation.demo.annotation.TestAroundAnnotation")
public class TestAroundAnnotationProcessor extends AbstractProcessor {

    private Trees trees;
    private TreeMaker treeMaker;
    private Name.Table names;
    private Context context;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        trees = Trees.instance(processingEnv);
        context = ((JavacProcessingEnvironment)
                processingEnv).getContext();
        treeMaker = TreeMaker.instance(context);
        names = Names.instance(context).table;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        JavacElements elementUtils = (JavacElements) processingEnv.getElementUtils();
        TreeMaker treeMaker = TreeMaker.instance(context);
        for (Element element : roundEnv.getElementsAnnotatedWith(TestAroundAnnotation.class)) {
            JCTree.JCMethodDecl jcMethodDecl = (JCTree.JCMethodDecl) elementUtils.getTree(element);
            TestAroundAnnotation annotation = element.getAnnotation(TestAroundAnnotation.class);
            String annotationValue = annotation.value();
            System.out.println("TestAroundAnnotation注解Value:" + annotationValue);
            treeMaker.pos = jcMethodDecl.pos;
            JCTree.JCStatement beforeCode = makeBeforeCode(elementUtils, annotationValue);
            JCTree.JCStatement afterCode = makeAfterCode(elementUtils);
            jcMethodDecl.body = treeMaker.Block(0, List.of(
                    beforeCode, // 方法内容前面要加入的代码
                    // 生成 try{原始代码}finally{after中的代码}
                    treeMaker.Try(List.nil(),jcMethodDecl.body,List.nil(),treeMaker.Block(0, List.of(afterCode)))
            ));
        }
        return false;
    }

    private JCTree.JCStatement makeAfterCode(JavacElements elementUtils) {
        return treeMaker.Exec(
                treeMaker.Apply(
                        List.<JCTree.JCExpression>nil(),
                        treeMaker.Select(
                                treeMaker.Select(
                                        treeMaker.Ident(
                                                elementUtils.getName("net.itfeng.compileannotation.demo.util")
                                        ),
                                        elementUtils.getName("AnnotationUtil")
                                ),
                                elementUtils.getName("after")
                        ),
                        //使用beforeCode代码中定义的变量名，
                        List.of(treeMaker.Ident(getNameFromString("_trace_before_result")))
                )
        );
    }

    /**
     * 在标有注解的方法首行输出一个打印注解中的value的代码
     *
     * @param elementUtils
     * @param annotationValue
     * @return
     */
    private JCTree.JCStatement makeBeforeCode(JavacElements elementUtils, String annotationValue) {
        return makeVarDef(treeMaker.Modifiers(0), "_trace_before_result", memberAccess("java.lang.String"), treeMaker.Exec(
                treeMaker.Apply(
                        List.<JCTree.JCExpression>nil(),
                        treeMaker.Select(
                                treeMaker.Select(
                                        treeMaker.Ident(
                                                elementUtils.getName("net.itfeng.compileannotation.demo.util")
                                        ),
                                        elementUtils.getName("AnnotationUtil")
                                ),
                                elementUtils.getName("before")
                        ),
                        List.<JCTree.JCExpression>of(
                                treeMaker.Literal(annotationValue)
                        )
                )
        ).getExpression());
    }


    /**
     * 定义一个变量
     *
     * @param modifiers
     * @param name
     * @param vartype
     * @param init
     * @return
     */
    private JCTree.JCVariableDecl makeVarDef(JCTree.JCModifiers modifiers, String name, JCTree.JCExpression vartype, JCTree.JCExpression init) {
        return treeMaker.VarDef(
                modifiers,
                getNameFromString(name), //名字
                vartype, //类型
                init //初始化语句
        );
    }

    private Name getNameFromString(String s) {
        return names.fromString(s);
    }

    /**
     * 创建 域/方法 的多级访问, 方法的标识只能是最后一个
     *
     * @param components
     * @return
     */
    private JCTree.JCExpression memberAccess(String components) {
        String[] componentArray = components.split("\\.");
        JCTree.JCExpression expr = treeMaker.Ident(getNameFromString(componentArray[0]));
        for (int i = 1; i < componentArray.length; i++) {
            expr = treeMaker.Select(expr, getNameFromString(componentArray[i]));
        }
        return expr;
    }
}
