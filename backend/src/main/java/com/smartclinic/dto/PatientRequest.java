package com.smartclinic.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientRequest {
    
    @NotBlank(message = "Patient name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
    private String phone;
    
    @NotBlank(message = "Disease/Reason is required")
    private String disease;
    
    private Integer consultationDuration;
    
    private String symptoms;  // ADD THIS
    
    private Boolean isEmergency;
    
    private Boolean priorityApproved;
    
    private String emergencyReason;
}