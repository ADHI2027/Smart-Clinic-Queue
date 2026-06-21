package com.smartclinic.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "consultation_history")
public class ConsultationHistory {
    @Id
    private String id;
    private String patientId;
    private String token;
    private Integer age;
    private String gender;
    private String doctor;
    private String disease;
    private String dayOfWeek;
    private String timeSlot;
    private Integer predictedDuration;
    private Integer actualDuration;
    private Integer queueLength;
    private LocalDateTime createdAt;
}