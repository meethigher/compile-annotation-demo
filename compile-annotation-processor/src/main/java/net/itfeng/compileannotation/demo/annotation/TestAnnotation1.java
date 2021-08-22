package net.itfeng.compileannotation.demo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 第一个简单的注解，用于练习编译时在方法中输出一行System.out.println(注解参数)
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface TestAnnotation1 {
    String value();
}
