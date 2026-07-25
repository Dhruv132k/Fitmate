package com.fitmate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class FitMateApplication {

    public static void main(String[] args) {
        SpringApplication.run(FitMateApplication.class, args);
    }
}
