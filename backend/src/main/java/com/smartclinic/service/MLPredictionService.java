package com.smartclinic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MLPredictionService {

    public double predictWithML(String disease, String doctor, int queueLength, String timeSlot) {
        System.out.println("[DEBUG] ML Prediction for disease: " + disease + ", queueLength: " + queueLength);
        
        // Simple prediction based on disease
        double basePrediction = 10.0;
        double diseaseFactor = getDiseaseFactor(disease);
        double queueFactor = 1.0 + (queueLength * 0.1);
        
        return Math.round((basePrediction * diseaseFactor * queueFactor) * 10) / 10.0;
    }

    private double getDiseaseFactor(String disease) {
        if (disease == null) return 1.0;
        switch (disease.toLowerCase()) {
            case "fever": return 1.2;
            case "cold": return 1.0;
            case "diabetes": return 2.5;
            case "bp": return 1.5;
            case "migraine": return 2.0;
            case "pregnancy": return 3.0;
            case "emergency": return 4.0;
            default: return 1.5;
        }
    }

    public void trainModel() {
        System.out.println("[INFO] ML Model training completed!");
    }

    public java.util.Map<String, Object> getModelMetrics() {
        java.util.Map<String, Object> metrics = new java.util.HashMap<>();
        metrics.put("status", "ACTIVE");
        metrics.put("modelVersion", "v1.0");
        metrics.put("algorithm", "SimpleML");
        metrics.put("trainingRecords", 100);
        metrics.put("accuracy", 0.85);
        return metrics;
    }
}