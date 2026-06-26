package com.smartclinic.service;

import com.smartclinic.model.ConsultationHistory;
import com.smartclinic.model.DiseaseStat;
import com.smartclinic.model.DoctorStat;
import com.smartclinic.model.Patient;
import com.smartclinic.model.PatientStatus;
import com.smartclinic.repository.ConsultationHistoryRepository;
import com.smartclinic.repository.DiseaseStatRepository;
import com.smartclinic.repository.DoctorStatRepository;
import com.smartclinic.repository.PatientRepository;
import com.smartclinic.dto.PatientRequest;
import com.smartclinic.dto.PatientResponse;
import com.smartclinic.dto.QueueResponse;
import com.smartclinic.exception.PatientNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
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
    private final DoctorStatRepository doctorStatRepository;
    private final WhatsAppNotificationService whatsappNotificationService;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");
    private static final List<PatientStatus> ACTIVE_STATUSES = Arrays.asList(
            PatientStatus.WAITING, PatientStatus.CONSULTING
        );
    private static final int DEFAULT_DURATION = 10;

    // Emergency symptoms list
    private static final List<String> EMERGENCY_SYMPTOMS = Arrays.asList(
        "chest pain",
        "breathing difficulty",
        "heavy bleeding",
        "stroke",
        "severe burns",
        "seizures",
        "high fever"
    );

    // Manual logging
    private void logInfo(String message) {
        System.out.println("[INFO] " + message);
    }

    private void logDebug(String message) {
        System.out.println("[DEBUG] " + message);
    }

    public boolean isEmergencyCase(String symptoms) {
        if (symptoms == null || symptoms.isEmpty()) return false;
        String lowerSymptoms = symptoms.toLowerCase();
        return EMERGENCY_SYMPTOMS.stream()
            .anyMatch(lowerSymptoms::contains);
    }

    private void recordConsultationHistory(Patient patient) {
        ConsultationHistory history = ConsultationHistory.builder()
                .patientId(patient.getId())
                .token(patient.getToken())
                .age(0)
                .gender("Not Specified")
                .doctor("Dr. Default")
                .disease(patient.getDisease())
                .dayOfWeek(LocalDateTime.now().getDayOfWeek().toString())
                .timeSlot(getTimeSlot())
                .predictedDuration(patient.getConsultationDuration())
                .actualDuration(patient.getConsultationDuration())
                .queueLength(0)
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
        System.out.println("🔍 Symptoms received: " + request.getSymptoms());
        System.out.println("🔍 isEmergency flag: " + request.getIsEmergency());
        System.out.println("🔍 priorityApproved flag: " + request.getPriorityApproved());
        String token = generateNextToken();
        Patient patient = new Patient();
        patient.setToken(token);
        patient.setName(request.getName());
        patient.setPhone(request.getPhone());
        patient.setDisease(request.getDisease());
        patient.setStatus(PatientStatus.WAITING);
        patient.setCreatedAt(LocalDateTime.now());

        // Check for emergency - NO DUPLICATE DECLARATIONS
        String symptoms = request.getSymptoms();
        boolean isEmergency = isEmergencyCase(symptoms);
        boolean priorityApproved = request.getPriorityApproved() != null && request.getPriorityApproved();
        System.out.println("🚨 isEmergency: " + isEmergency);
        System.out.println("✅ priorityApproved: " + priorityApproved);
        if (isEmergency && priorityApproved) {
            patient.setIsEmergency(true);
            patient.setEmergencyReason(request.getDisease());
            patient.setPriorityApproved(true);
            logInfo("🚨 EMERGENCY PRIORITY APPROVED for: " + request.getName());
        }

        int predictedDuration = getDiseaseAverageFromDB(request.getDisease());
        patient.setConsultationDuration(predictedDuration);

        patient.setAge(0);
        patient.setGender("Not Specified");
        patient.setDoctor("Dr. Default");
        patient.setDayOfWeek(LocalDateTime.now().getDayOfWeek().toString());
        patient.setTimeSlot(getTimeSlot());
        patient.setQueueLength(0);

        Patient savedPatient = patientRepository.save(patient);
        
        // If emergency and approved, move to front
        if (isEmergency && priorityApproved) {
            moveToFrontOfQueue(savedPatient);
        }
        
        recalculateEstimatedTimes();
        broadcastQueueUpdate();

        recordConsultationHistory(savedPatient);

        return convertToResponse(savedPatient);
    }

    private void moveToFrontOfQueue(Patient emergencyPatient) {
        logInfo("Moving emergency patient " + emergencyPatient.getToken() + " to front of queue");
        
        List<Patient> waitingPatients = patientRepository
                .findByStatusOrderByCreatedAtAsc(PatientStatus.WAITING);
        
        waitingPatients.removeIf(p -> p.getId().equals(emergencyPatient.getId()));
        
        int position = 1;
        for (Patient p : waitingPatients) {
            p.setQueuePosition(position + 1);
            patientRepository.save(p);
        }
        
        emergencyPatient.setQueuePosition(1);
        patientRepository.save(emergencyPatient);
        
        logInfo("Emergency patient " + emergencyPatient.getToken() + " moved to front");
    }

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
                    logInfo("Completed consulting patient: " + consulting.getToken());
                });

        Patient nextPatient = patientRepository
                .findTopByStatusOrderByCreatedAtAsc(PatientStatus.WAITING)
                .orElseThrow(() -> new IllegalStateException("No patients in waiting queue"));

        nextPatient.setStatus(PatientStatus.CONSULTING);
        nextPatient.setCreatedAt(LocalDateTime.now());
        Patient savedPatient = patientRepository.save(nextPatient);

        logInfo("Called next patient: " + savedPatient.getToken() + " - " + savedPatient.getName());

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

        long actualDuration = java.time.Duration.between(patient.getCreatedAt(), LocalDateTime.now()).toMinutes();
        logInfo("Patient " + patient.getToken() + " completed. Actual duration: " + actualDuration + " minutes");

        patient.setStatus(PatientStatus.COMPLETED);
        patient.setConsultationDuration((int) Math.max(1, actualDuration));
        Patient savedPatient = patientRepository.save(patient);

        recordConsultationHistory(savedPatient);
        updateDiseaseStatistics(savedPatient.getDisease());
        updateDoctorStatistics(savedPatient.getDoctor());

        try {
            long waitingCount = patientRepository.countByStatus(PatientStatus.WAITING);
            if (waitingCount > 0) {
                callNext();
            } else {
                logInfo("No more patients in queue");
            }
        } catch (IllegalStateException e) {
            logInfo("No more patients in queue");
        }

        broadcastQueueUpdate();
        return convertToResponse(savedPatient);
    }

    private void updateDiseaseStatistics(String disease) {
        if (disease == null || disease.isEmpty()) return;
        try {
            List<ConsultationHistory> historyList = historyRepository.findByDisease(disease);
            if (historyList.isEmpty()) return;

            double avg = historyList.stream()
                    .filter(h -> h.getActualDuration() != null)
                    .mapToInt(ConsultationHistory::getActualDuration)
                    .average()
                    .orElse(DEFAULT_DURATION);

            int min = historyList.stream()
                    .filter(h -> h.getActualDuration() != null)
                    .mapToInt(ConsultationHistory::getActualDuration)
                    .min()
                    .orElse(DEFAULT_DURATION);

            int max = historyList.stream()
                    .filter(h -> h.getActualDuration() != null)
                    .mapToInt(ConsultationHistory::getActualDuration)
                    .max()
                    .orElse(DEFAULT_DURATION);

            int total = historyList.size();

            DiseaseStat stat = diseaseStatRepository.findByDisease(disease)
                    .orElse(new DiseaseStat());

            stat.setDisease(disease);
            stat.setAverageDuration(avg);
            stat.setMinDuration(min);
            stat.setMaxDuration(max);
            stat.setTotalPatients(total);
            stat.setLastUpdated(LocalDateTime.now());

            diseaseStatRepository.save(stat);
            logInfo("Updated DiseaseStat for '" + disease + "': avg=" + avg + ", total=" + total);
        } catch (Exception e) {
            System.err.println("Failed to update disease statistics: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateDoctorStatistics(String doctor) {
        if (doctor == null || doctor.isEmpty()) return;
        try {
            List<ConsultationHistory> historyList = historyRepository.findByDoctor(doctor);
            if (historyList.isEmpty()) return;

            double avg = historyList.stream()
                    .filter(h -> h.getActualDuration() != null)
                    .mapToInt(ConsultationHistory::getActualDuration)
                    .average()
                    .orElse(DEFAULT_DURATION);

            int min = historyList.stream()
                    .filter(h -> h.getActualDuration() != null)
                    .mapToInt(ConsultationHistory::getActualDuration)
                    .min()
                    .orElse(DEFAULT_DURATION);

            int max = historyList.stream()
                    .filter(h -> h.getActualDuration() != null)
                    .mapToInt(ConsultationHistory::getActualDuration)
                    .max()
                    .orElse(DEFAULT_DURATION);

            int total = historyList.size();

            DoctorStat stat = doctorStatRepository.findByDoctor(doctor)
                    .orElse(new DoctorStat());

            stat.setDoctor(doctor);
            stat.setAverageDuration(avg);
            stat.setMinDuration(min);
            stat.setMaxDuration(max);
            stat.setTotalPatients(total);
            stat.setLastUpdated(LocalDateTime.now());

            doctorStatRepository.save(stat);
            logInfo("Updated DoctorStat for '" + doctor + "': avg=" + avg + ", total=" + total);
        } catch (Exception e) {
            System.err.println("Failed to update doctor statistics: " + e.getMessage());
            e.printStackTrace();
        }
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
            int remainingMinutes = (int) Math.max(0, consultingDuration - elapsedMinutes);
            
            logDebug("Consulting: " + consulting.getToken() + 
                     ", Duration: " + consultingDuration + 
                     ", Elapsed: " + elapsedMinutes + 
                     ", Remaining: " + remainingMinutes);
            
            consulting.setConsultationDuration(Math.max(1, remainingMinutes));
            patientRepository.save(consulting);
            startTime = currentTime.plusMinutes(remainingMinutes);
        } else {
            startTime = currentTime;
        }

        for (int i = 0; i < waitingPatients.size(); i++) {
            Patient patient = waitingPatients.get(i);
            int duration = patient.getConsultationDuration() != null ?
                    patient.getConsultationDuration() : DEFAULT_DURATION;
            
            LocalDateTime etaTime = startTime.plusMinutes(duration);
            String estimatedTime = etaTime.format(TIME_FORMATTER);
            
            String oldEstimatedTime = patient.getEstimatedTime();
            
            patient.setEstimatedTime(estimatedTime);
            patient.setEstimatedDateTime(etaTime);
            patient.setQueuePosition(i + 1);
            
            // If the ETA gets pushed out past 10 minutes from now, allow sending reminder again
            if (patient.getSent10MinReminder() != null && patient.getSent10MinReminder() && etaTime.isAfter(LocalDateTime.now().plusMinutes(10))) {
                patient.setSent10MinReminder(false);
            }
            
            patientRepository.save(patient);
            
            // Notify only if ETA has changed and old estimated time was set
            if (oldEstimatedTime != null && !oldEstimatedTime.equals(estimatedTime)) {
                if (patient.getPhone() != null && !patient.getPhone().trim().isEmpty()) {
                    try {
                        whatsappNotificationService.sendETAChangeNotification(
                                patient.getPhone(),
                                patient.getName(),
                                patient.getToken(),
                                oldEstimatedTime,
                                estimatedTime
                        );
                    } catch (Exception e) {
                        System.err.println("Failed to send ETA change notification: " + e.getMessage());
                    }
                }
            }
            
            logDebug("Patient: " + patient.getToken() + 
                     ", Duration: " + duration + 
                     ", ETA: " + estimatedTime + 
                     ", Position: " + (i + 1));
            
            startTime = etaTime;
        }
    }

    @Scheduled(fixedRate = 60000) // Run every 60 seconds
    @Transactional
    public void checkAndSend10MinReminders() {
        logDebug("Running scheduled 10-minute ETA reminder check");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reminderThreshold = now.plusMinutes(10);

        List<Patient> waitingPatients = patientRepository.findByStatusOrderByCreatedAtAsc(PatientStatus.WAITING);
        for (Patient patient : waitingPatients) {
            if (patient.getEstimatedDateTime() != null) {
                boolean isWithin10Mins = !patient.getEstimatedDateTime().isAfter(reminderThreshold);
                boolean hasNotSentYet = patient.getSent10MinReminder() == null || !patient.getSent10MinReminder();
                
                if (isWithin10Mins && hasNotSentYet) {
                    patient.setSent10MinReminder(true);
                    patientRepository.save(patient);
                    
                    if (patient.getPhone() != null && !patient.getPhone().trim().isEmpty()) {
                        try {
                            whatsappNotificationService.send10MinReminder(
                                    patient.getPhone(),
                                    patient.getName(),
                                    patient.getToken(),
                                    patient.getEstimatedTime()
                            );
                        } catch (Exception e) {
                            System.err.println("Failed to send 10-minute WhatsApp reminder: " + e.getMessage());
                        }
                    }
                }
            }
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