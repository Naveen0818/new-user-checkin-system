package com.credit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CreditEligibilityApplication {
    public static void main(String[] args) {
        SpringApplication.run(CreditEligibilityApplication.class, args);
    }
} 