package net.itfeng.compileannotation.demo.processor;

import com.google.auto.service.AutoService;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;
import net.itfeng.compileannotation.demo.annotation.Data;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * @author https://gitee.com/terryge/annotation
 * @since 2022/11/14 11:02
 */
@SupportedAnnotationTypes("net.itfeng.compileannotation.demo.annotation.Data")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class DataAnnotationProcessor extends AbstractProcessor {
    private JavacTrees javacTrees;
    private DataProcessor dataProcessor;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        javacTrees = JavacTrees.instance(processingEnv);
        dataProcessor = new DataProcessor(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> set = roundEnv.getElementsAnnotatedWith(Data.class);
        for (Element element : set) {
            // 获取当前类的抽象语法树
            JCTree tree = javacTrees.getTree(element);
            // 获取抽象语法树的所有节点
            // Visitor 抽象内部类，内部定义了访问各种语法节点的方法
            tree.accept(new TreeTranslator() {
                @Override
                public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                    jcClassDecl.defs.stream()
                            // 过滤，只处理变量类型
                            .filter(it -> it.getKind().equals(Tree.Kind.VARIABLE))
                            // 类型强转
                            .map(it -> (JCTree.JCVariableDecl) it)
                            .forEach(it -> {
                                // 添加get方法
                                JCTree.JCMethodDecl getterMethod = dataProcessor.genGetterMethod(it);
                                jcClassDecl.defs = jcClassDecl.defs.prepend(getterMethod);
                                // 添加set方法
                                JCTree.JCMethodDecl setterMethod = dataProcessor.genSetterMethod(it);
                                jcClassDecl.defs = jcClassDecl.defs.prepend(setterMethod);
                            });

                    super.visitClassDef(jcClassDecl);
                }
            });
        }
        return false;
    }
}
