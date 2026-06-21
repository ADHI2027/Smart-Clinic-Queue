package com.smartclinic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ETAResponse {
    private String token;
    private String patientName;
    private Integer patientsAhead;
    private Integer predictedDuration;
    private String estimatedTime;
    private Integer lowerBound;
    private Integer upperBound;
    private String confidenceLevel;
    private Double probabilityWithin10min;
    private Double probabilityWithin20min;
    private Double probabilityWithin30min;
    private Double rulePrediction;
    private Double mlPrediction;
    private Double ruleWeight;
    private Double mlWeight;
    private Long totalRecords;
}