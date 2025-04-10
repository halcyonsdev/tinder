package com.halcyon.tinder.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = {
                "com.halcyon.tinder.userservice",
                "com.halcyon.tinder.rediscache",
                "com.halcyon.tinder.jwtcore",
                "com.halcyon.tinder.exceptioncore"
        })
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
