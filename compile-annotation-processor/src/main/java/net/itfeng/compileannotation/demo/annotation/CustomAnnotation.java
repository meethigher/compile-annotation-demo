package net.itfeng.compileannotation.demo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * https://blog.csdn.net/Gaugamela/article/details/79694302
 * https://blog.csdn.net/dap769815768/article/details/90448451
 * https://blog.csdn.net/vector_M/article/details/105037394
 * @author zhangjian on 18-3-23.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface CustomAnnotation {
}
