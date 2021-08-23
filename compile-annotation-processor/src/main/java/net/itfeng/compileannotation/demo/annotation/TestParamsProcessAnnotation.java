package net.itfeng.compileannotation.demo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 参数处理注解，
 * 该注解作用到方法上，注解处理器会将标有该注解的方法的入参封装到List集合中，并生成到代码内
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface TestParamsProcessAnnotation {
    /**
     * 常量参数
     * @return
     */
    String value();

    /**
     * 参数索引值，需要封装哪些参数，{0,1}表示封装第1个和第二个参数
     * @return
     */
    int[] indexs();
}
