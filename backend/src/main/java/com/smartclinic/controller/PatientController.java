package com.smartclinic.controller;

import com.smartclinic.dto.PatientRequest;
import com.smartclinic.dto.PatientResponse;
import com.smartclinic.dto.QueueResponse;
import com.smartclinic.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patient")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class PatientController {
    private final PatientService patientService;
    
    @PostMapping
    public ResponseEntity<PatientResponse> addPatient(@Valid @RequestBody PatientRequest request) {
        PatientResponse response = patientService.addPatient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/queue")
    public ResponseEntity<QueueResponse> getQueue() {
        QueueResponse response = patientService.getQueue();
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/next")
    public ResponseEntity<PatientResponse> callNext() {
        PatientResponse response = patientService.callNext();
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/skip")
    public ResponseEntity<PatientResponse> skipPatient(@PathVariable String id) {
        PatientResponse response = patientService.skipPatient(id);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/complete")
    public ResponseEntity<PatientResponse> completePatient(@PathVariable String id) {
        PatientResponse response = patientService.completePatient(id);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable String id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<PatientResponse>> searchByPhone(@RequestParam String phone) {
        List<PatientResponse> patients = patientService.searchByPhone(phone);
        return ResponseEntity.ok(patients);
    }
    
    @GetMapping("/consulting")
    public ResponseEntity<PatientResponse> getConsulting() {
        PatientResponse consulting = patientService.getConsultingPatient();
        return ResponseEntity.ok(consulting);
    }
}