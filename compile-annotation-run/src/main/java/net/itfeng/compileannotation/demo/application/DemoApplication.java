package net.itfeng.compileannotation.demo.application;

import net.itfeng.compileannotation.demo.annotation.TestAnnotation1;
import net.itfeng.compileannotation.demo.annotation.TestAroundAnnotation;
import net.itfeng.compileannotation.demo.annotation.TestParamsProcessAnnotation;

/**
 * 测试类，执行compile后反编译class文件，即可看到编译后的文件增加了内容
 */
public class DemoApplication {
    public static void main(String[] args) {
        DemoApplication demo =new DemoApplication();
        demo.testAnnotation1();
        demo.testAroundAnnotation();
    }
    @TestAnnotation1("Hello compile Annotation!")
    public void testAnnotation1(){
        System.out.println("Annotation 123456");
    }

    @TestAroundAnnotation("Hello compile AroundAnnotation!")
    public void testAroundAnnotation(){
        System.out.println("AroundAnnotation 123456");
    }

    @TestParamsProcessAnnotation(value = "Hello compile TestParamsProcessAnnotation!",indexs = {1,2})
    public void testParamsProcessAnnotation(String param1,String param2,String param3,int param4){
        System.out.println("AroundAnnotation 123456");
    }
}
