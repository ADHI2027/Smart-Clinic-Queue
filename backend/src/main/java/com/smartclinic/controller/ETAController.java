package com.smartclinic.controller;

import com.smartclinic.dto.ETAResponse;
import com.smartclinic.model.DiseaseStat;
import com.smartclinic.model.DoctorStat;
import com.smartclinic.repository.DiseaseStatRepository;
import com.smartclinic.repository.DoctorStatRepository;
import com.smartclinic.service.ETAService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eta")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ETAController {

    private final ETAService etaService;
    private final DiseaseStatRepository diseaseStatRepository;
    private final DoctorStatRepository doctorStatRepository;

    @GetMapping("/{token}")
    public ResponseEntity<ETAResponse> getETA(@PathVariable String token) {
        ETAResponse response = etaService.calculateETA(token);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics/disease")
    public ResponseEntity<List<DiseaseStat>> getDiseaseStats() {
        return ResponseEntity.ok(diseaseStatRepository.findAll());
    }

    @GetMapping("/statistics/doctor")
    public ResponseEntity<List<DoctorStat>> getDoctorStats() {
        return ResponseEntity.ok(doctorStatRepository.findAll());
    }
}