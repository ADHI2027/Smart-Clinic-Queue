package com.smartclinic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import jakarta.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@ComponentScan(basePackages = {"com.smartclinic"})
public class SmartClinicApplication {
    
    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
        System.out.println("Set JVM Default TimeZone to Asia/Kolkata. Current time: " + java.time.LocalDateTime.now());
    }

    public static void main(String[] args) {
        SpringApplication.run(SmartClinicApplication.class, args);
    }
}