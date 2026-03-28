package com.drivemond.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.drivemond")
@EntityScan(basePackages = "com.drivemond")
public class DrivemondApplication {

    public static void main(String[] args) {
        SpringApplication.run(DrivemondApplication.class, args);
    }
}