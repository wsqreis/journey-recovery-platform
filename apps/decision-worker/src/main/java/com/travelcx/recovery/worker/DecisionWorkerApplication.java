package com.travelcx.recovery.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.travelcx.recovery")
public class DecisionWorkerApplication {
    public static void main(String[] args) {
        SpringApplication.run(DecisionWorkerApplication.class, args);
    }
}
