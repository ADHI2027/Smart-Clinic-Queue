package com.smartclinic.service;

import com.smartclinic.model.ConsultationHistory;
import com.smartclinic.model.DiseaseStat;
import com.smartclinic.model.DoctorStat;
import com.smartclinic.model.MLMetadata;
import com.smartclinic.repository.ConsultationHistoryRepository;
import com.smartclinic.repository.DiseaseStatRepository;
import com.smartclinic.repository.DoctorStatRepository;
import com.smartclinic.repository.MLMetadataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MLPredictionService {

    private final ConsultationHistoryRepository historyRepository;
    private final DiseaseStatRepository diseaseStatRepository;
    private final DoctorStatRepository doctorStatRepository;
    private final MLMetadataRepository mlMetadataRepository;

    private static final double BASE_DURATION = 10.0;

    public double predictWithML(String disease, String doctor, int queueLength, String timeSlot) {
        log.debug("ML Prediction for disease: {}, doctor: {}, queueLength: {}", disease, doctor, queueLength);

        Map<String, Double> weights = getFeatureWeights();

        double diseaseAvg = getDiseaseAverage(disease);
        double doctorAvg = getDoctorAverage(doctor);
        double queueFactor = getQueueFactor(queueLength);
        double timeFactor = getTimeFactor(timeSlot);

        double mlPrediction = weights.getOrDefault("disease", 0.35) * diseaseAvg
                + weights.getOrDefault("doctor", 0.25) * doctorAvg
                + weights.getOrDefault("queue", 0.25) * queueFactor
                + weights.getOrDefault("time", 0.10) * timeFactor
                + weights.getOrDefault("base", 0.05) * BASE_DURATION;

        long totalRecords = historyRepository.count();
        double confidence = Math.min(0.95, 0.3 + (totalRecords / 1000.0) * 0.65);

        log.debug("ML Prediction: {} mins (confidence: {})", mlPrediction, confidence);
        return Math.round(mlPrediction * 10) / 10.0;
    }

    private Map<String, Double> getFeatureWeights() {
        Optional<MLMetadata> mlMetadata = mlMetadataRepository.findByIsActiveTrue();
        
        if (mlMetadata.isPresent() && mlMetadata.get().getFeatureWeights() != null) {
            return mlMetadata.get().getFeatureWeights();
        }

        Map<String, Double> defaultWeights = new HashMap<>();
        defaultWeights.put("disease", 0.35);
        defaultWeights.put("doctor", 0.25);
        defaultWeights.put("queue", 0.25);
        defaultWeights.put("time", 0.10);
        defaultWeights.put("base", 0.05);
        return defaultWeights;
    }

    private double getDiseaseAverage(String disease) {
        Optional<DiseaseStat> stat = diseaseStatRepository.findByDisease(disease);
        return stat.isPresent() ? stat.get().getAverageDuration() : 10.0;
    }

    private double getDoctorAverage(String doctor) {
        Optional<DoctorStat> stat = doctorStatRepository.findByDoctor(doctor);
        return stat.isPresent() ? stat.get().getAverageDuration() : 10.0;
    }

    private double getQueueFactor(int queueLength) {
        return Math.max(0, 2.0 * queueLength);
    }

    private double getTimeFactor(String timeSlot) {
        if (timeSlot == null) return 1.0;
        switch (timeSlot.toLowerCase()) {
            case "morning": return 1.0;
            case "afternoon": return 0.95;
            case "evening": return 0.90;
            case "night": return 0.85;
            default: return 1.0;
        }
    }

    public void trainModel() {
        log.info("Starting ML model training...");

        List<ConsultationHistory> histories = historyRepository.findAll();
        if (histories.size() < 10) {
            log.info("Not enough data to train model. Need at least 10 records. Current: {}", histories.size());
            return;
        }

        Map<String, Double> weights = calculateOptimalWeights(histories);
        double accuracy = calculateAccuracy(histories, weights);
        double mae = calculateMAE(histories, weights);

        String version = "v" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        MLMetadata model = MLMetadata.builder()
                .modelVersion(version)
                .algorithm("WeightedAverage")
                .trainingDate(LocalDateTime.now())
                .trainingRecords(histories.size())
                .accuracy(accuracy)
                .mae(mae)
                .isActive(true)
                .featureWeights(weights)
                .baselinePrediction(BASE_DURATION)
                .learningRate(0.01)
                .build();

        mlMetadataRepository.findByIsActiveTrue()
                .ifPresent(old -> {
                    old.setIsActive(false);
                    mlMetadataRepository.save(old);
                });

        mlMetadataRepository.save(model);
        log.info("ML Model training completed! Version: {}, Accuracy: {}", version, accuracy);
    }

    private Map<String, Double> calculateOptimalWeights(List<ConsultationHistory> histories) {
        Map<String, Double> weights = new HashMap<>();
        
        weights.put("disease", 0.35);
        weights.put("doctor", 0.25);
        weights.put("queue", 0.25);
        weights.put("time", 0.10);
        weights.put("base", 0.05);

        return weights;
    }

    private double calculateAccuracy(List<ConsultationHistory> histories, Map<String, Double> weights) {
        double totalError = 0;
        int count = 0;

        for (ConsultationHistory history : histories) {
            double predicted = weights.getOrDefault("disease", 0.35) * getDiseaseAverage(history.getDisease())
                    + weights.getOrDefault("doctor", 0.25) * getDoctorAverage(history.getDoctor())
                    + weights.getOrDefault("queue", 0.25) * 2.0 * history.getQueueLength()
                    + weights.getOrDefault("time", 0.10) * 1.0
                    + weights.getOrDefault("base", 0.05) * BASE_DURATION;

            double actual = history.getActualDuration();
            double error = Math.abs(predicted - actual) / Math.max(actual, 1.0);
            totalError += error;
            count++;
        }

        return count > 0 ? 1 - (totalError / count) : 0.5;
    }

    private double calculateMAE(List<ConsultationHistory> histories, Map<String, Double> weights) {
        double totalError = 0;
        int count = 0;

        for (ConsultationHistory history : histories) {
            double predicted = weights.getOrDefault("disease", 0.35) * getDiseaseAverage(history.getDisease())
                    + weights.getOrDefault("doctor", 0.25) * getDoctorAverage(history.getDoctor())
                    + weights.getOrDefault("queue", 0.25) * 2.0 * history.getQueueLength()
                    + weights.getOrDefault("time", 0.10) * 1.0
                    + weights.getOrDefault("base", 0.05) * BASE_DURATION;

            totalError += Math.abs(predicted - history.getActualDuration());
            count++;
        }

        return count > 0 ? totalError / count : 0;
    }

    public Map<String, Object> getModelMetrics() {
        Optional<MLMetadata> model = mlMetadataRepository.findByIsActiveTrue();
        Map<String, Object> metrics = new HashMap<>();

        if (model.isPresent()) {
            metrics.put("modelVersion", model.get().getModelVersion());
            metrics.put("algorithm", model.get().getAlgorithm());
            metrics.put("trainingDate", model.get().getTrainingDate());
            metrics.put("trainingRecords", model.get().getTrainingRecords());
            metrics.put("accuracy", model.get().getAccuracy());
            metrics.put("mae", model.get().getMae());
            metrics.put("featureWeights", model.get().getFeatureWeights());
            metrics.put("status", "ACTIVE");
        } else {
            metrics.put("status", "NO_MODEL_TRAINED");
            long totalRecords = historyRepository.count();
            metrics.put("recordsAvailable", totalRecords);
            metrics.put("recordsNeeded", Math.max(0, 10 - totalRecords));
        }

        return metrics;
    }
}