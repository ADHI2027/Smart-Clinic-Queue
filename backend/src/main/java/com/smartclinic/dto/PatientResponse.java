package com.smartclinic.dto;

import com.smartclinic.model.PatientStatus;

import java.time.LocalDateTime;

public class PatientResponse {
    private String id;
    private String token;
    private String name;
    private String phone;
    private String disease;
    private PatientStatus status;
    private Integer consultationDuration;
    private String estimatedTime;
    private LocalDateTime createdAt;
    private Integer queuePosition;

    // Default constructor
    public PatientResponse() {}

    // Constructor with fields
    public PatientResponse(String id, String token, String name, String phone, 
                          String disease, PatientStatus status, Integer consultationDuration,
                          String estimatedTime, LocalDateTime createdAt, Integer queuePosition) {
        this.id = id;
        this.token = token;
        this.name = name;
        this.phone = phone;
        this.disease = disease;
        this.status = status;
        this.consultationDuration = consultationDuration;
        this.estimatedTime = estimatedTime;
        this.createdAt = createdAt;
        this.queuePosition = queuePosition;
    }

    // Builder pattern
    public static PatientResponseBuilder builder() {
        return new PatientResponseBuilder();
    }

    public static class PatientResponseBuilder {
        private String id;
        private String token;
        private String name;
        private String phone;
        private String disease;
        private PatientStatus status;
        private Integer consultationDuration;
        private String estimatedTime;
        private LocalDateTime createdAt;
        private Integer queuePosition;

        public PatientResponseBuilder id(String id) {
            this.id = id;
            return this;
        }

        public PatientResponseBuilder token(String token) {
            this.token = token;
            return this;
        }

        public PatientResponseBuilder name(String name) {
            this.name = name;
            return this;
        }

        public PatientResponseBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public PatientResponseBuilder disease(String disease) {
            this.disease = disease;
            return this;
        }

        public PatientResponseBuilder status(PatientStatus status) {
            this.status = status;
            return this;
        }

        public PatientResponseBuilder consultationDuration(Integer consultationDuration) {
            this.consultationDuration = consultationDuration;
            return this;
        }

        public PatientResponseBuilder estimatedTime(String estimatedTime) {
            this.estimatedTime = estimatedTime;
            return this;
        }

        public PatientResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public PatientResponseBuilder queuePosition(Integer queuePosition) {
            this.queuePosition = queuePosition;
            return this;
        }

        public PatientResponse build() {
            return new PatientResponse(id, token, name, phone, disease, status, 
                                      consultationDuration, estimatedTime, createdAt, queuePosition);
        }
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public PatientStatus getStatus() {
        return status;
    }

    public void setStatus(PatientStatus status) {
        this.status = status;
    }

    public Integer getConsultationDuration() {
        return consultationDuration;
    }

    public void setConsultationDuration(Integer consultationDuration) {
        this.consultationDuration = consultationDuration;
    }

    public String getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(String estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getQueuePosition() {
        return queuePosition;
    }

    public void setQueuePosition(Integer queuePosition) {
        this.queuePosition = queuePosition;
    }
}