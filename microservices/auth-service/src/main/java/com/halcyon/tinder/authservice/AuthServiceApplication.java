package com.halcyon.tinder.authservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = {
                "com.halcyon.tinder.authservice",
                "com.halcyon.tinder.jwtcore",
                "com.halcyon.tinder.exceptioncore"
        })
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
