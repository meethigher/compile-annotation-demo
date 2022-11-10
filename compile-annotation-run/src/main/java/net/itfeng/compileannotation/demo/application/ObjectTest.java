package net.itfeng.compileannotation.demo.application;

import net.itfeng.compileannotation.demo.annotation.Mock;

import java.util.Date;

@Mock
public class ObjectTest {

    private String name;

    private Date id;

    private boolean share;

    private Boolean share1;

    private Long long1;

    private int int1;

    private Integer int2;

    private ObjectTest objectTest;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
