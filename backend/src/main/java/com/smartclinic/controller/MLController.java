package com.smartclinic.controller;

import com.smartclinic.service.MLPredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ml")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MLController {

    private final MLPredictionService mlPredictionService;

    @PostMapping("/train")
    public ResponseEntity<Map<String, Object>> trainModel() {
        mlPredictionService.trainModel();
        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("message", "Model training completed");
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getMetrics() {
        return ResponseEntity.ok(mlPredictionService.getModelMetrics());
    }

    @GetMapping("/predict")
    public ResponseEntity<Map<String, Object>> predict(
            @RequestParam String disease,
            @RequestParam(defaultValue = "Dr. Default") String doctor,
            @RequestParam(defaultValue = "0") int queueLength,
            @RequestParam(defaultValue = "Morning") String timeSlot) {
        
        double prediction = mlPredictionService.predictWithML(disease, doctor, queueLength, timeSlot);
        
        Map<String, Object> response = new HashMap<>();
        response.put("prediction", prediction);
        response.put("disease", disease);
        response.put("doctor", doctor);
        response.put("queueLength", queueLength);
        response.put("timeSlot", timeSlot);
        response.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }
}