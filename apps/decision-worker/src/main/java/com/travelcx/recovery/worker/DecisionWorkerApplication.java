package com.travelcx.recovery.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(
        scanBasePackages = "com.travelcx.recovery",
        exclude = DataSourceAutoConfiguration.class)
public class DecisionWorkerApplication {
    public static void main(String[] args) {
        SpringApplication.run(DecisionWorkerApplication.class, args);
    }
}
