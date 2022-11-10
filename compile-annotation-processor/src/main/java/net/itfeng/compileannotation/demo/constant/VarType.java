package net.itfeng.compileannotation.demo.constant;

import java.util.Arrays;
import java.util.List;

public enum VarType {
    STRING("string", Arrays.asList("String")),
    LONG("long", Arrays.asList("Long", "long")),
    INT("int", Arrays.asList("Integer", "int")),
    BOOLEAN("boolean", Arrays.asList("Boolean", "boolean")),

    ;

    VarType(String desc, List<String> typeList) {
        this.desc = desc;
        this.typeList = typeList;
    }

    private String desc;

    private List<String> typeList;


}
