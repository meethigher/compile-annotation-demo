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
import net.itfeng.compileannotation.demo.annotation.TestAnnotation1;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * 注解TestAnotation1的解释器
 */
@SupportedAnnotationTypes("net.itfeng.compileannotation.demo.annotation.TestAnnotation1")
public class TestAnnotation1Processor extends AbstractProcessor {

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
        System.out.println("编译期注解执行拉");
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        JavacElements elementUtils = (JavacElements) processingEnv.getElementUtils();
        TreeMaker treeMaker = TreeMaker.instance(context);
        for (Element element : roundEnv.getElementsAnnotatedWith(TestAnnotation1.class)) {
            JCTree.JCMethodDecl jcMethodDecl = (JCTree.JCMethodDecl) elementUtils.getTree(element);
            TestAnnotation1 annotation = element.getAnnotation(TestAnnotation1.class);
            String annotationValue = annotation.value();
            System.out.println("TestAnnotation1注解Value:" + annotationValue);
            treeMaker.pos = jcMethodDecl.pos;
            JCTree.JCStatement beforeCode = makeFirstPrintCode(elementUtils, annotationValue);
            jcMethodDecl.body = treeMaker.Block(0, List.of(
                    beforeCode, // 方法内容前面要加入的代码
                    jcMethodDecl.body // 原方法内容
//                     ,beforeCode 放开这一行则在原方法内容后面增加要加入的代码
            ));

        }
        return false;
    }

    /**
     * 在标有注解的方法首行输出一个打印注解中的value的代码
     *
     * @param elementUtils
     * @param anotationValue
     * @return
     */
    private JCTree.JCStatement makeFirstPrintCode(JavacElements elementUtils, String anotationValue) {
//        return treeMaker.Exec(
//                treeMaker.Apply(
//                        List.<JCTree.JCExpression>nil(),
//                        treeMaker.Select(
//                                treeMaker.Select(
//                                        treeMaker.Ident(
//                                                elementUtils.getName("System")
//                                        ),
//                                        elementUtils.getName("out")
//                                ),
//                                elementUtils.getName("println")
//                        ),
//                        List.<JCTree.JCExpression>of(
//                                treeMaker.Literal(anotationValue)
//                        )
//                )
//        );
        JCTree.JCFieldAccess select = treeMaker.Select(treeMaker.Ident(elementUtils.getName("System")), elementUtils.getName("out"));
        JCTree.JCFieldAccess println = treeMaker.Select(select, elementUtils.getName("println"));
        JCTree.JCMethodInvocation apply = treeMaker.Apply(List.<JCTree.JCExpression>nil(), println, List.of(treeMaker.Literal(anotationValue)));
        return treeMaker.Exec(apply);
    }
}
