package com.smartclinic.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "disease_statistics")
public class DiseaseStat {
    @Id
    private String id;
    @Indexed(unique = true)
    private String disease;
    private Double averageDuration;
    private Integer minDuration;
    private Integer maxDuration;
    private Integer totalPatients;
    private LocalDateTime lastUpdated;
}