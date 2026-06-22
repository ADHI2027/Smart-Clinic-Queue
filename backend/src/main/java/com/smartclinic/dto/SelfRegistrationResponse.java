package com.smartclinic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SelfRegistrationResponse {
    private String token;
    private Integer patientsAhead;
    private Integer expectedWaitMin;
    private Integer expectedWaitMax;
    private String estimatedTime;
}