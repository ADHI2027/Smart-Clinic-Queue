package com.smartclinic.service;

import com.smartclinic.dto.ETAResponse;
import com.smartclinic.model.*;
import com.smartclinic.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ETAService {

    private final PatientRepository patientRepository;
    private final DiseaseStatRepository diseaseStatRepository;
    private final DoctorStatRepository doctorStatRepository;
    private final ConsultationHistoryRepository historyRepository;
    private final MLMetadataRepository mlMetadataRepository;
    private final MLPredictionService mlPredictionService; // ADDED

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");
    private static final double DISEASE_WEIGHT = 0.6;
    private static final double DOCTOR_WEIGHT = 0.4;

    public ETAResponse calculateETA(String token) {
        log.debug("Calculating ETA for token: {}", token);

        Patient patient = patientRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Patient not found with token: " + token));

        // 1. Rule-based prediction
        double rulePrediction = getRuleBasedPrediction(patient);

        // 2. ML prediction
        double mlPrediction = mlPredictionService.predictWithML(
            patient.getDisease(),
            patient.getDoctor() != null ? patient.getDoctor() : "Dr. Default",
            patient.getQueuePosition() != null ? patient.getQueuePosition() : 0,
            patient.getTimeSlot() != null ? patient.getTimeSlot() : getTimeSlot()
        );

        // 3. Get dynamic weights
        long totalRecords = historyRepository.count();
        WeightInfo weights = getDynamicWeights(totalRecords);

        // 4. Hybrid prediction
        double finalPrediction = (weights.ruleWeight * rulePrediction) 
                + (weights.mlWeight * mlPrediction);

        int roundedPrediction = (int) Math.round(finalPrediction);

        // 5. Confidence interval
        double stdDev = 4.0;
        double confidence = 1.96 * (stdDev / Math.sqrt(Math.max(totalRecords, 1)));
        int lowerBound = (int) Math.max(1, Math.round(roundedPrediction - confidence));
        int upperBound = (int) Math.round(roundedPrediction + confidence);

        // 6. Calculate probabilities
        int patientsAhead = Math.max(0, patient.getQueuePosition() - 1);
        double meanWait = (patientsAhead + 1) * roundedPrediction;
        double prob10 = Math.min(100, (10.0 / meanWait) * 100);
        double prob20 = Math.min(100, (20.0 / meanWait) * 100);
        double prob30 = Math.min(100, (30.0 / meanWait) * 100);

        return ETAResponse.builder()
                .token(token)
                .patientName(patient.getName())
                .patientsAhead(patientsAhead)
                .predictedDuration(roundedPrediction)
                .estimatedTime(formatEstimatedTime(roundedPrediction))
                .lowerBound(lowerBound)
                .upperBound(upperBound)
                .confidenceLevel(String.format("%.0f%%", Math.max(0, (1 - confidence / roundedPrediction) * 100)))
                .probabilityWithin10min(prob10)
                .probabilityWithin20min(prob20)
                .probabilityWithin30min(prob30)
                .rulePrediction(rulePrediction)
                .mlPrediction(mlPrediction)
                .ruleWeight(weights.ruleWeight * 100)
                .mlWeight(weights.mlWeight * 100)
                .totalRecords(totalRecords)
                .build();
    }

    private double getRuleBasedPrediction(Patient patient) {
        double diseaseAvg = getDiseaseAverage(patient.getDisease());
        double doctorAvg = getDoctorAverage("Dr. Default");
        return (DISEASE_WEIGHT * diseaseAvg) + (DOCTOR_WEIGHT * doctorAvg);
    }

    private double getDiseaseAverage(String disease) {
        Optional<DiseaseStat> stat = diseaseStatRepository.findByDisease(disease);
        if (stat.isPresent()) {
            return stat.get().getAverageDuration();
        }
        return 10.0;
    }

    private double getDoctorAverage(String doctor) {
        Optional<DoctorStat> stat = doctorStatRepository.findByDoctor(doctor);
        if (stat.isPresent()) {
            return stat.get().getAverageDuration();
        }
        return 12.0;
    }

    private WeightInfo getDynamicWeights(long totalRecords) {
        double ruleWeight, mlWeight;

        if (totalRecords < 100) {
            ruleWeight = 0.8;
            mlWeight = 0.2;
        } else if (totalRecords < 1000) {
            double progress = (totalRecords - 100) / 900.0;
            ruleWeight = 0.8 - (progress * 0.6);
            mlWeight = 1.0 - ruleWeight;
        } else {
            ruleWeight = 0.2;
            mlWeight = 0.8;
        }

        return new WeightInfo(ruleWeight, mlWeight);
    }

    private String formatEstimatedTime(int minutes) {
        LocalDateTime estimated = LocalDateTime.now().plusMinutes(minutes);
        return estimated.format(TIME_FORMATTER);
    }

    private String getTimeSlot() {
        int hour = LocalDateTime.now().getHour();
        if (hour < 12) return "Morning";
        if (hour < 17) return "Afternoon";
        return "Evening";
    }

    private static class WeightInfo {
        double ruleWeight;
        double mlWeight;
        WeightInfo(double rule, double ml) {
            this.ruleWeight = rule;
            this.mlWeight = ml;
        }
    }
}