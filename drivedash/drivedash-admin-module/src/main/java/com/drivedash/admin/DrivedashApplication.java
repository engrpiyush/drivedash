package com.drivedash.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.drivedash")
@EntityScan(basePackages = "com.drivedash")
public class DrivedashApplication {

    public static void main(String[] args) {
        SpringApplication.run(DrivedashApplication.class, args);
    }
}