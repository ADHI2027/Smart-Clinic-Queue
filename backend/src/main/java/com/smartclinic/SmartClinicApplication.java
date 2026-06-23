package com.smartclinic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.smartclinic"})
public class SmartClinicApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmartClinicApplication.class, args);
    }
}