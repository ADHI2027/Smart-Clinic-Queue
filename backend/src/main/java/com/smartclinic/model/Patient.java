package com.smartclinic.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "patients")
public class Patient {
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String token;
    
    private String name;
    
    @Indexed
    private String phone;
    
    private String disease;
    
    private PatientStatus status;
    
    private Integer consultationDuration;
    
    private String estimatedTime;
    
    private LocalDateTime createdAt;
    
    private Integer queuePosition;

    // ML Fields
    private Integer age;
    private String gender;
    private String doctor;
    private String dayOfWeek;
    private String timeSlot;
    private Integer queueLength;

    // Emergency Fields - ONLY ONCE!
    private Boolean isEmergency = false;
    private String emergencyReason;
    private Boolean priorityApproved = false;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getDisease() { return disease; }
    public void setDisease(String disease) { this.disease = disease; }

    public PatientStatus getStatus() { return status; }
    public void setStatus(PatientStatus status) { this.status = status; }

    public Integer getConsultationDuration() { return consultationDuration; }
    public void setConsultationDuration(Integer consultationDuration) { this.consultationDuration = consultationDuration; }

    public String getEstimatedTime() { return estimatedTime; }
    public void setEstimatedTime(String estimatedTime) { this.estimatedTime = estimatedTime; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Integer getQueuePosition() { return queuePosition; }
    public void setQueuePosition(Integer queuePosition) { this.queuePosition = queuePosition; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getDoctor() { return doctor; }
    public void setDoctor(String doctor) { this.doctor = doctor; }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }

    public Integer getQueueLength() { return queueLength; }
    public void setQueueLength(Integer queueLength) { this.queueLength = queueLength; }

    public Boolean getIsEmergency() { return isEmergency; }
    public void setIsEmergency(Boolean isEmergency) { this.isEmergency = isEmergency; }

    public String getEmergencyReason() { return emergencyReason; }
    public void setEmergencyReason(String emergencyReason) { this.emergencyReason = emergencyReason; }

    public Boolean getPriorityApproved() { return priorityApproved; }
    public void setPriorityApproved(Boolean priorityApproved) { this.priorityApproved = priorityApproved; }
}