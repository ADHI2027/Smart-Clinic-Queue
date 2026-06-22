package com.smartclinic.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "ml_metadata")
public class MLMetadata {
    @Id
    private String id;
    private String modelVersion;
    private String algorithm;
    private LocalDateTime trainingDate;
    private Integer trainingRecords;
    private Double accuracy;
    private Double mae;
    private Boolean isActive;
    private Map<String, Double> featureWeights;
    private Double baselinePrediction;
    private Double learningRate;
}
