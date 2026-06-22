package com.smartclinic.repository;

import com.smartclinic.model.Patient;
import com.smartclinic.model.PatientStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends MongoRepository<Patient, String> {
    
    // ========== STATUS BASED QUERIES ==========
    
    /**
     * Find all patients with a specific status, ordered by creation time
     */
    List<Patient> findByStatusOrderByCreatedAtAsc(PatientStatus status);
    List<Patient> findByIsEmergencyTrue();
    /**
     * Find all patients with status in a list, ordered by creation time
     */
    List<Patient> findByStatusInOrderByCreatedAtAsc(List<PatientStatus> statuses);
    
    /**
     * Find the first patient with a specific status (oldest first)
     */
    Optional<Patient> findTopByStatusOrderByCreatedAtAsc(PatientStatus status);
    
    /**
     * Find all patients with status NOT in a list
     */
    List<Patient> findByStatusNotInOrderByCreatedAtAsc(List<PatientStatus> statuses);
    
    /**
     * Find all patients with a specific status
     */
    List<Patient> findByStatus(PatientStatus status);
    
    /**
     * Count patients by status
     */
    long countByStatus(PatientStatus status);
    
    /**
     * Count patients created between two dates
     */
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    // ========== TOKEN BASED QUERIES ==========
    
    /**
     * Find patient by token (for ETA and queue operations)
     */
    Optional<Patient> findByToken(String token);
    
    /**
     * Find the patient with the highest token (for generating next token)
     */
    Optional<Patient> findTopByOrderByTokenDesc();
    
    // ========== PHONE BASED QUERIES ==========
    
    /**
     * Find patients by phone number
     */
    List<Patient> findByPhone(String phone);
    
    /**
     * Find patient by phone and status
     */
    Optional<Patient> findByPhoneAndStatus(String phone, PatientStatus status);
    
    // ========== ACTIVE PATIENT QUERIES ==========
    
    /**
     * Find all active patients (WAITING or CONSULTING)
     */
    @Query("{ 'status': { $in: ['WAITING', 'CONSULTING'] } }")
    List<Patient> findAllActivePatients();
    
    /**
     * Count active patients
     */
    @Query(value = "{ 'status': { $in: ['WAITING', 'CONSULTING'] } }", count = true)
    long countActivePatients();
    
    // ========== DISEASE BASED QUERIES ==========
    
    /**
     * Find patients by disease
     */
    List<Patient> findByDisease(String disease);
    
    /**
     * Count patients by disease and status
     */
    long countByDiseaseAndStatus(String disease, PatientStatus status);
    
    // ========== DATE BASED QUERIES ==========
    
    /**
     * Find patients created on a specific date
     */
    @Query("{ 'createdAt': { $gte: ?0, $lt: ?1 } }")
    List<Patient> findPatientsByDateRange(LocalDateTime start, LocalDateTime end);
    
    /**
     * Find patients created today
     */
    default List<Patient> findPatientsCreatedToday() {
        LocalDateTime start = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusDays(1);
        return findPatientsByDateRange(start, end);
    }
    
    /**
     * Count patients created today by status
     */
    @Query("{ 'createdAt': { $gte: ?0, $lt: ?1 }, 'status': ?2 }")
    long countByDateRangeAndStatus(LocalDateTime start, LocalDateTime end, PatientStatus status);
    
    // ========== COMPLEX QUERIES ==========
    
    /**
     * Find patients with queue position greater than specified
     */
    List<Patient> findByQueuePositionGreaterThan(Integer position);
    
    /**
     * Find patients with queue position between two values
     */
    List<Patient> findByQueuePositionBetween(Integer start, Integer end);
    
    /**
     * Find patients by disease and status
     */
    List<Patient> findByDiseaseAndStatus(String disease, PatientStatus status);
    
    // ========== BULK OPERATIONS ==========
    
    /**
     * Delete all patients with a specific status
     */
    void deleteByStatus(PatientStatus status);
    
    /**
     * Delete all patients older than a date
     */
    void deleteByCreatedAtBefore(LocalDateTime date);
    
    // ========== AGGREGATION QUERIES ==========
    
    /**
     * Get average consultation duration by disease
     */
    @Query(value = "{ 'disease': ?0 }", fields = "{ 'consultationDuration': 1 }")
    List<Patient> findConsultationDurationsByDisease(String disease);
    
    /**
     * Get total patients by disease
     */
    @Query(value = "{ 'disease': ?0 }", count = true)
    long countByDisease(String disease);
    
    // ========== STATISTICS QUERIES ==========
    
    /**
     * Get patients grouped by status
     */
    @Query("{ 'status': ?0 }")
    List<Patient> findByStatusWithSort(PatientStatus status, org.springframework.data.domain.Sort sort);
    
    /**
     * Get consulting patient with doctor info
     */
    @Query(value = "{ 'status': 'CONSULTING' }", fields = "{ 'doctor': 1, 'name': 1, 'token': 1 }")
    Optional<Patient> findConsultingPatientWithDoctorInfo();
    
    // ========== PATIENT FACING QUERIES ==========
    
    /**
     * Find patients ahead of a given token
     */
    @Query("{ 'queuePosition': { $lt: ?0 }, 'status': 'WAITING' }")
    List<Patient> findPatientsAheadOfPosition(Integer position);
    
    /**
     * Get waiting time for a patient based on queue position
     */
    default int getEstimatedWaitTime(String token) {
        Optional<Patient> patient = findByToken(token);
        if (patient.isPresent()) {
            // This would need to calculate based on queue position and average durations
            return patient.get().getQueuePosition() * 10; // Simplified
        }
        return 0;
    }
}