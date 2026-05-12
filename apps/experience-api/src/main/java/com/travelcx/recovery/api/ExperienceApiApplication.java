package com.travelcx.recovery.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@ConfigurationPropertiesScan
@SpringBootApplication(scanBasePackages = "com.travelcx.recovery")
public class ExperienceApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExperienceApiApplication.class, args);
    }
}
