package com.halcyon.tinder.swipeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = {
                "com.halcyon.tinder.swipeservice",
                "com.halcyon.tinder.jwtcore",
                "com.halcyon.tinder.exceptioncore",
                "com.halcyon.tinder.rediscache"
        })
public class SwipeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SwipeServiceApplication.class, args);
    }
}
