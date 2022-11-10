package net.itfeng.compileannotation.demo.application;

import net.itfeng.compileannotation.demo.annotation.CustomAnnotation;
//需要idea开启File | Settings | Build, Execution, Deployment | Compiler | Annotation Processors -> Enable annotation process
import net.itfeng.compileannotation.demo.generated.GeneratedClass;

public class Test {

    @CustomAnnotation
    public static void main(String[] args) {
        System.out.println(new GeneratedClass().getMessage());
    }
}
