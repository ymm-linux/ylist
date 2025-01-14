package com.yao.ylist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <p>
 * 启动类
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-10-10 10:10
 */
@SpringBootApplication
public class SpringBootDemoTemplateThymeleafApplication {

    public static void main(String[] args) {
        System.out.println("Hello world!");
        SpringApplication.run(SpringBootDemoTemplateThymeleafApplication.class, args);
        System.out.println("Hello world end!");
    }
}
