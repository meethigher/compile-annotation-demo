package net.itfeng.compileannotation.demo.application;

public class MockTest {

    public static void main(String[] args) {
        ObjectTest test = new ObjectTest();
        test.setName("test");
        String name = test.getName();
        String typeName = name.getClass().getTypeName();
        System.out.println();
    }

}
