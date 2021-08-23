package net.itfeng.compileannotation.demo.processor;

import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import net.itfeng.compileannotation.demo.annotation.TestAroundAnnotation;
import net.itfeng.compileannotation.demo.annotation.TestParamsProcessAnnotation;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Arrays;
import java.util.Set;

/**
 * 注解TTestAroundAnnotation的解释器
 */
@SupportedAnnotationTypes("net.itfeng.compileannotation.demo.annotation.TestParamsProcessAnnotation")
public class TestParamsProcessAnnotationProcessor extends AbstractProcessor {

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
        for (Element element : roundEnv.getElementsAnnotatedWith(TestParamsProcessAnnotation.class)) {
            JCTree.JCMethodDecl jcMethodDecl = (JCTree.JCMethodDecl) elementUtils.getTree(element);
            TestParamsProcessAnnotation annotation = element.getAnnotation(TestParamsProcessAnnotation.class);
            String annotationValue = annotation.value();
            System.out.println("TestAroundAnnotation注解Value:" + annotationValue);
            treeMaker.pos = jcMethodDecl.pos;
            JCTree.JCStatement beforeCode = makeBeforeCode(elementUtils, annotationValue);
            //创建一个List对象
            JCTree.JCStatement newCode = makeNewObjectCode(elementUtils,annotationValue);
            //强转参数标记
            Symbol.MethodSymbol methodSymbol = (Symbol.MethodSymbol)element;
            // 获取到方法的参数定义
            List<Symbol.VarSymbol> varSymbols = methodSymbol.params();
            // 定义要插入的连续的代码行，每一个项为一行
            List codeList = List.of(beforeCode,newCode);
            if(varSymbols!=null && !varSymbols.isEmpty()) {
                // 如果有参数，则生成一个ArratList临时变量，并将参数逐个添加到list
                List addCodes = makeAddList(elementUtils, "_trace_before_params", varSymbols);
                codeList = codeList.appendList(addCodes);
            }
            // 创建最后执行的代码
            JCTree.JCStatement afterCode = makeAfterCode(elementUtils);
            jcMethodDecl.body = treeMaker.Block(0,
                codeList.append(
                    // 生成 try{原始代码}finally{afterCode}
                    treeMaker.Try(List.nil(),jcMethodDecl.body,List.nil(),treeMaker.Block(0, List.of(afterCode)))
                )
            );
        }
        // 默认return false,表示执行完当前注解后，还有其他注解则继续处理
        // true 表示执行完当前注解不再执行其他注解
        return false;
    }

    private List makeAddList(JavacElements elementUtils,String addVarName, List<Symbol.VarSymbol> varSymbols) {
        List addCodes = List.nil();
        for(Symbol.VarSymbol varSymbol:varSymbols){
            addCodes = addCodes.append(makeListAddMethod(elementUtils,elementUtils.getName(addVarName),varSymbol.name));
        }
        return addCodes;
    }

    /**
     * 返回 varName.add(paramName)
     * @param elementUtils
     * @param varName 变量名
     * @param paramName add参数变量名
     * @return
     */
    private JCTree.JCStatement makeListAddMethod(JavacElements elementUtils,Name varName,Name paramName){
        return treeMaker.Exec(
                treeMaker.Apply(
                        List.<JCTree.JCExpression>nil(),
                        treeMaker.Select(
                                treeMaker.Ident(varName),
                                elementUtils.getName("add")
                        ),
                        List.of(treeMaker.Ident(paramName))
                )
        );
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
     * 返回代码：List _trace_before_params = new ArrayList();
     * @param elementUtils
     * @param annotationValue
     * @return
     */
    private JCTree.JCStatement makeNewObjectCode(JavacElements elementUtils, String annotationValue) {
        return makeVarDef(treeMaker.Modifiers(0), "_trace_before_params", memberAccess("java.util.List"),treeMaker.Exec(
                treeMaker.NewClass(
                        null,List.nil(),treeMaker.Select(
                                treeMaker.Ident(
                                        elementUtils.getName("java.util")
                                ),
                                elementUtils.getName("ArrayList")
                        ),List.nil(),null
                )
                ).getExpression()
        );
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
