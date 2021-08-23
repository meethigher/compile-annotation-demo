# 编译时注解Demo
## 核心功能说明
本demo项目使用编译时注解实现了简单的环绕切面效果。
## 代码结构
｜------ compile-annotation-processor 自定义运行时注解及注解解释器，必须是独立的jar<p>
｜------ compile-annotation-run 使用自定义运行时注解的demo项目

## 注意事项
1. 自定义的注解和注解解释器必须是独立的模块，不能和使用注解的代码在一个模块
1. 自定义注解所在的模块pom文件中必须引入以下内容，其中systemPath指定了本地tools.jar的位置
```
   <dependency>
   <groupId>com.sun</groupId>
   <artifactId>tools</artifactId>
   <version>1.8</version>
   <scope>system</scope>
   <systemPath>${java.home}/lib/tools.jar</systemPath>
   </dependency>
```