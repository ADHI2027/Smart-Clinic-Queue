package com.smartclinic.service;

import com.smartclinic.dto.ETAResponse;
import com.smartclinic.model.Patient;
import com.smartclinic.model.PatientStatus;
import com.smartclinic.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ETAService {

    private final PatientRepository patientRepository;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");
    private static final int DEFAULT_DURATION = 10;

    public ETAResponse calculateETA(String token) {
        System.out.println("[DEBUG] Calculating ETA for token: " + token);

        Patient patient = patientRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Patient not found with token: " + token));

        int predictedDuration = patient.getConsultationDuration() != null ? 
                patient.getConsultationDuration() : DEFAULT_DURATION;

        // Calculate patients ahead
        List<Patient> waitingPatients = patientRepository.findByStatusOrderByCreatedAtAsc(PatientStatus.WAITING);
        int patientsAhead = 0;
        for (Patient p : waitingPatients) {
            if (p.getToken().equals(token)) {
                break;
            }
            patientsAhead++;
        }

        String estimatedTime = LocalDateTime.now().plusMinutes(predictedDuration).format(TIME_FORMATTER);

        return ETAResponse.builder()
                .token(token)
                .patientName(patient.getName())
                .patientsAhead(patientsAhead)
                .predictedDuration(predictedDuration)
                .estimatedTime(estimatedTime)
                .lowerBound(Math.max(1, predictedDuration - 3))
                .upperBound(predictedDuration + 3)
                .confidenceLevel("85%")
                .probabilityWithin10min(50.0)
                .probabilityWithin20min(80.0)
                .probabilityWithin30min(95.0)
                .rulePrediction((double) predictedDuration)
                .mlPrediction((double) predictedDuration)
                .ruleWeight(80.0)
                .mlWeight(20.0)
                .totalRecords(100L)
                .build();
    }
}