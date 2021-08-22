package net.itfeng.compileannotation.demo.util;

/**
 * 这里编写处理注解的业务代码
 *
 */
public class AnnotationUtil {
    public static String before(String value){
        System.out.println("执行before方法，这是执行before方法输出的入参："+value);
        return "这是before方法的返回值";
    }

    public static String after(String value){
        System.out.println("执行after方法，这是执行after方法输出的入参："+value);
        return "这是after方法的返回值";
    }
}
