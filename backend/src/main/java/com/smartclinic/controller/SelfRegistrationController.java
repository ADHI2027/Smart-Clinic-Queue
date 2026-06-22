package com.smartclinic.controller;

import com.smartclinic.dto.PatientRequest;
import com.smartclinic.dto.PatientResponse;
import com.smartclinic.dto.SelfRegistrationRequest;
import com.smartclinic.dto.SelfRegistrationResponse;
import com.smartclinic.dto.ETAResponse;
import com.smartclinic.service.PatientService;
import com.smartclinic.service.ETAService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/self-register")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SelfRegistrationController {

    private final PatientService patientService;
    private final ETAService etaService;

    @PostMapping
    public ResponseEntity<SelfRegistrationResponse> selfRegister(@RequestBody SelfRegistrationRequest request) {
        PatientRequest patientRequest = new PatientRequest();
        patientRequest.setName(request.getName());
        patientRequest.setPhone(request.getPhone());
        patientRequest.setDisease(request.getDisease());
        patientRequest.setSymptoms(request.getSymptoms());
        patientRequest.setConsultationDuration(10);

        PatientResponse patient = patientService.addPatient(patientRequest);
        ETAResponse etaResponse = etaService.calculateETA(patient.getToken());
        
        SelfRegistrationResponse response = SelfRegistrationResponse.builder()
                .token(patient.getToken())
                .patientsAhead(etaResponse.getPatientsAhead())
                .expectedWaitMin(etaResponse.getPredictedDuration() - 3)
                .expectedWaitMax(etaResponse.getPredictedDuration() + 3)
                .estimatedTime(etaResponse.getEstimatedTime())
                .build();
        
        return ResponseEntity.ok(response);
    }
}