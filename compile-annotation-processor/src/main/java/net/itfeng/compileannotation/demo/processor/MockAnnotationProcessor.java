package net.itfeng.compileannotation.demo.processor;

import com.google.auto.service.AutoService;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import net.itfeng.compileannotation.demo.annotation.Mock;

import javax.annotation.processing.*;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;
import java.util.function.Predicate;

@AutoService(Processor.class)
@SupportedAnnotationTypes("net.itfeng.compileannotation.demo.annotation.Mock")
public class MockAnnotationProcessor extends AbstractProcessor {
    private JavacTrees javacTrees;

    private TreeMaker treeMaker;

    private Name.Table names;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        javacTrees = JavacTrees.instance(processingEnv);
        treeMaker = TreeMaker.instance(((JavacProcessingEnvironment)
                processingEnv).getContext());
        names = Names.instance(((JavacProcessingEnvironment)
                processingEnv).getContext()).table;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Mock.class);
        for (Element ele : elements) {
            JCTree.JCClassDecl jcClassDecl = (JCTree.JCClassDecl) javacTrees.getTree(ele);
            for (JCTree jcTree : jcClassDecl.defs) {
                if (jcTree.getKind().equals(Tree.Kind.VARIABLE)) {
                    initVariable((JCTree.JCVariableDecl) jcTree);

                }
            }
        }
        return false;
    }

    private void initVariable(JCTree.JCVariableDecl jcVariableDecl) {
        System.out.println(jcVariableDecl.vartype);
    }
}
