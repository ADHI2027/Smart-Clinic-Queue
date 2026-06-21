package com.smartclinic.dto;

import java.util.List;

public class QueueResponse {
    private PatientResponse currentlyConsulting;
    private List<PatientResponse> waitingList;
    private Long totalWaiting;
    private Long completedToday;
    private Long totalPatients;

    // Default constructor
    public QueueResponse() {}

    // Constructor with fields
    public QueueResponse(PatientResponse currentlyConsulting, List<PatientResponse> waitingList,
                        Long totalWaiting, Long completedToday, Long totalPatients) {
        this.currentlyConsulting = currentlyConsulting;
        this.waitingList = waitingList;
        this.totalWaiting = totalWaiting;
        this.completedToday = completedToday;
        this.totalPatients = totalPatients;
    }

    // Builder pattern
    public static QueueResponseBuilder builder() {
        return new QueueResponseBuilder();
    }

    public static class QueueResponseBuilder {
        private PatientResponse currentlyConsulting;
        private List<PatientResponse> waitingList;
        private Long totalWaiting;
        private Long completedToday;
        private Long totalPatients;

        public QueueResponseBuilder currentlyConsulting(PatientResponse currentlyConsulting) {
            this.currentlyConsulting = currentlyConsulting;
            return this;
        }

        public QueueResponseBuilder waitingList(List<PatientResponse> waitingList) {
            this.waitingList = waitingList;
            return this;
        }

        public QueueResponseBuilder totalWaiting(Long totalWaiting) {
            this.totalWaiting = totalWaiting;
            return this;
        }

        public QueueResponseBuilder completedToday(Long completedToday) {
            this.completedToday = completedToday;
            return this;
        }

        public QueueResponseBuilder totalPatients(Long totalPatients) {
            this.totalPatients = totalPatients;
            return this;
        }

        public QueueResponse build() {
            return new QueueResponse(currentlyConsulting, waitingList, totalWaiting, 
                                    completedToday, totalPatients);
        }
    }

    // Getters and Setters
    public PatientResponse getCurrentlyConsulting() {
        return currentlyConsulting;
    }

    public void setCurrentlyConsulting(PatientResponse currentlyConsulting) {
        this.currentlyConsulting = currentlyConsulting;
    }

    public List<PatientResponse> getWaitingList() {
        return waitingList;
    }

    public void setWaitingList(List<PatientResponse> waitingList) {
        this.waitingList = waitingList;
    }

    public Long getTotalWaiting() {
        return totalWaiting;
    }

    public void setTotalWaiting(Long totalWaiting) {
        this.totalWaiting = totalWaiting;
    }

    public Long getCompletedToday() {
        return completedToday;
    }

    public void setCompletedToday(Long completedToday) {
        this.completedToday = completedToday;
    }

    public Long getTotalPatients() {
        return totalPatients;
    }

    public void setTotalPatients(Long totalPatients) {
        this.totalPatients = totalPatients;
    }
}