package com.smartclinic.service;

import com.smartclinic.model.ConsultationHistory;
import com.smartclinic.model.DiseaseStat;
import com.smartclinic.repository.ConsultationHistoryRepository;
import com.smartclinic.repository.DiseaseStatRepository;
import com.smartclinic.dto.PatientRequest;
import com.smartclinic.dto.PatientResponse;
import com.smartclinic.dto.QueueResponse;
import com.smartclinic.exception.PatientNotFoundException;
import com.smartclinic.model.Patient;
import com.smartclinic.model.PatientStatus;
import com.smartclinic.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;
    private final QueueWebSocketService queueWebSocketService;
    private final ConsultationHistoryRepository historyRepository;
    private final DiseaseStatRepository diseaseStatRepository;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");
    private static final List<PatientStatus> ACTIVE_STATUSES = Arrays.asList(
            PatientStatus.WAITING, PatientStatus.CONSULTING
        );
    private static final int DEFAULT_DURATION = 10;

    // Manual logging
    private void logInfo(String message) {
        System.out.println("[INFO] " + message);
    }

   private void logDebug(String message, Object... args) {
    String formatted = String.format(message.replace("{}", "%s"), args);
    System.out.println("[DEBUG] " + formatted);
}

    /**
     * Record consultation history when patient is completed
     */
    private void recordConsultationHistory(Patient patient) {
        ConsultationHistory history = ConsultationHistory.builder()
                .patientId(patient.getId())
                .token(patient.getToken())
                .age(patient.getAge() != null ? patient.getAge() : 0)
                .gender(patient.getGender() != null ? patient.getGender() : "Not Specified")
                .doctor(patient.getDoctor() != null ? patient.getDoctor() : "Dr. Default")
                .disease(patient.getDisease())
                .dayOfWeek(LocalDateTime.now().getDayOfWeek().toString())
                .timeSlot(getTimeSlot())
                .predictedDuration(patient.getConsultationDuration())
                .actualDuration(patient.getConsultationDuration())
                .queueLength(patient.getQueuePosition() != null ? patient.getQueuePosition() : 0)
                .createdAt(LocalDateTime.now())
                .build();

        historyRepository.save(history);
    }

    private String getTimeSlot() {
        int hour = LocalDateTime.now().getHour();
        if (hour < 12) return "Morning";
        if (hour < 17) return "Afternoon";
        return "Evening";
    }

    @Transactional
public PatientResponse addPatient(PatientRequest request) {
    logInfo("Adding new patient: " + request.getName());

    String token = generateNextToken();
    Patient patient = new Patient();
    patient.setToken(token);
    patient.setName(request.getName());
    patient.setPhone(request.getPhone());
    patient.setDisease(request.getDisease());
    patient.setStatus(PatientStatus.WAITING);

    // Use disease average from database
    int predictedDuration = getDiseaseAverageFromDB(request.getDisease());
    patient.setConsultationDuration(predictedDuration);

    patient.setCreatedAt(LocalDateTime.now());

    Patient savedPatient = patientRepository.save(patient);
    
    // Recalculate all ETAs
    recalculateEstimatedTimes();
    broadcastQueueUpdate();

    return convertToResponse(savedPatient);
}

    /**
     * Get disease average from database - DYNAMIC, NO HARDCODING
     */
    private int getDiseaseAverageFromDB(String disease) {
        Optional<DiseaseStat> stat = diseaseStatRepository.findByDisease(disease);
        if (stat.isPresent()) {
            double avg = stat.get().getAverageDuration();
            logDebug("Found disease '" + disease + "' in DB with avg duration: " + avg);
            return (int) Math.round(avg);
        }
        logInfo("Disease '" + disease + "' not found in database, using default duration: " + DEFAULT_DURATION);
        return DEFAULT_DURATION;
    }

    private String generateNextToken() {
        Patient lastPatient = patientRepository.findTopByOrderByTokenDesc().orElse(null);
        if (lastPatient == null) {
            return "A001";
        }
        String lastToken = lastPatient.getToken();
        int number = Integer.parseInt(lastToken.substring(1)) + 1;
        return String.format("A%03d", number);
    }

    public QueueResponse getQueue() {
        logDebug("Fetching current queue");

        Patient currentlyConsulting = patientRepository
                .findTopByStatusOrderByCreatedAtAsc(PatientStatus.CONSULTING)
                .orElse(null);

        List<Patient> waitingPatients = patientRepository
                .findByStatusOrderByCreatedAtAsc(PatientStatus.WAITING);

        List<PatientResponse> waitingResponses = waitingPatients.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        long completedToday = patientRepository.countByCreatedAtBetween(
                LocalDateTime.now().withHour(0).withMinute(0).withSecond(0),
                LocalDateTime.now()
        );

        long totalPatients = patientRepository.count();

        return QueueResponse.builder()
                .currentlyConsulting(currentlyConsulting != null ?
                        convertToResponse(currentlyConsulting) : null)
                .waitingList(waitingResponses)
                .totalWaiting((long) waitingResponses.size())
                .completedToday(completedToday)
                .totalPatients(totalPatients)
                .build();
    }

    @Transactional
    public PatientResponse callNext() {
        logInfo("Calling next patient");

        patientRepository.findTopByStatusOrderByCreatedAtAsc(PatientStatus.CONSULTING)
                .ifPresent(consulting -> {
                    consulting.setStatus(PatientStatus.COMPLETED);
                    patientRepository.save(consulting);
                });

        Patient nextPatient = patientRepository
                .findTopByStatusOrderByCreatedAtAsc(PatientStatus.WAITING)
                .orElseThrow(() -> new IllegalStateException("No patients in waiting queue"));

        nextPatient.setStatus(PatientStatus.CONSULTING);
        Patient savedPatient = patientRepository.save(nextPatient);

        recalculateEstimatedTimes();
        broadcastQueueUpdate();

        return convertToResponse(savedPatient);
    }

    @Transactional
    public PatientResponse skipPatient(String id) {
        logInfo("Skipping patient with id: " + id);

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(id, null));

        if (patient.getStatus() != PatientStatus.WAITING) {
            throw new IllegalStateException("Only waiting patients can be skipped");
        }

        patient.setStatus(PatientStatus.SKIPPED);
        Patient savedPatient = patientRepository.save(patient);

        recalculateEstimatedTimes();
        broadcastQueueUpdate();

        return convertToResponse(savedPatient);
    }

    @Transactional
    public PatientResponse completePatient(String id) {
        logInfo("Completing patient with id: " + id);

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(id, null));

        if (patient.getStatus() != PatientStatus.CONSULTING) {
            throw new IllegalStateException("Only consulting patients can be completed");
        }

        patient.setStatus(PatientStatus.COMPLETED);
        Patient savedPatient = patientRepository.save(patient);

        // Record history for ML
        recordConsultationHistory(savedPatient);

        try {
            callNext();
        } catch (IllegalStateException e) {
            logInfo("No more patients in queue");
        }

        broadcastQueueUpdate();
        return convertToResponse(savedPatient);
    }

    @Transactional
    public void deletePatient(String id) {
        logInfo("Deleting patient with id: " + id);

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(id, null));

        patientRepository.delete(patient);
        recalculateEstimatedTimes();
        broadcastQueueUpdate();
    }

    public List<PatientResponse> searchByPhone(String phone) {
        logDebug("Searching patients by phone: " + phone);

        return patientRepository.findByPhone(phone)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

@Transactional
public void recalculateEstimatedTimes() {
    logDebug("Recalculating estimated times");

    List<Patient> waitingPatients = patientRepository
            .findByStatusOrderByCreatedAtAsc(PatientStatus.WAITING);

    Patient consulting = patientRepository
            .findTopByStatusOrderByCreatedAtAsc(PatientStatus.CONSULTING)
            .orElse(null);

    LocalDateTime currentTime = LocalDateTime.now();
    LocalDateTime startTime;

    if (consulting != null) {
        int consultingDuration = consulting.getConsultationDuration() != null ? 
                consulting.getConsultationDuration() : DEFAULT_DURATION;
        
        long elapsedMinutes = java.time.Duration.between(consulting.getCreatedAt(), currentTime).toMinutes();
        
        int remainingMinutes = (int) Math.max(1, consultingDuration - elapsedMinutes);
        
        logDebug("Consulting: " + consulting.getToken() + ", Duration: " + consultingDuration + 
                 ", Elapsed: " + elapsedMinutes + ", Remaining: " + remainingMinutes);
        
        consulting.setConsultationDuration(remainingMinutes);
        patientRepository.save(consulting);
        
        startTime = currentTime.plusMinutes(remainingMinutes);
        
    } else {
        startTime = currentTime;
    }

    for (int i = 0; i < waitingPatients.size(); i++) {
        Patient patient = waitingPatients.get(i);
        int duration = patient.getConsultationDuration() != null ? 
                patient.getConsultationDuration() : DEFAULT_DURATION;
        
        String estimatedTime = startTime.format(TIME_FORMATTER);
        patient.setEstimatedTime(estimatedTime);
        patient.setQueuePosition(i + 1);
        
        logDebug("Patient: " + patient.getToken() + ", Duration: " + duration + 
                 ", ETA: " + estimatedTime + ", Position: " + (i + 1));
        
        patientRepository.save(patient);
        startTime = startTime.plusMinutes(duration);
    }
}

    private void broadcastQueueUpdate() {
        QueueResponse queueResponse = getQueue();
        queueWebSocketService.broadcastQueueUpdate(queueResponse);
    }

    private PatientResponse convertToResponse(Patient patient) {
        return PatientResponse.builder()
                .id(patient.getId())
                .token(patient.getToken())
                .name(patient.getName())
                .phone(patient.getPhone())
                .disease(patient.getDisease())
                .status(patient.getStatus())
                .consultationDuration(patient.getConsultationDuration())
                .estimatedTime(patient.getEstimatedTime())
                .createdAt(patient.getCreatedAt())
                .queuePosition(patient.getQueuePosition())
                .build();
    }

    public PatientResponse getConsultingPatient() {
        Patient consulting = patientRepository
                .findTopByStatusOrderByCreatedAtAsc(PatientStatus.CONSULTING)
                .orElse(null);
        return consulting != null ? convertToResponse(consulting) : null;
    }
}