package com.smartclinic.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "QUEUE SOLVED Backend is running!");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Welcome to QUEUE SOLVED API");
        response.put("endpoints", new String[]{
            "/health",
            "/api/patient/queue",
            "/api/patient",
            "/api/eta/{token}"
        });
        return response;
    }
}