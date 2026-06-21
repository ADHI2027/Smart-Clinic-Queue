package com.smartclinic.repository;

import com.smartclinic.model.ConsultationHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsultationHistoryRepository extends MongoRepository<ConsultationHistory, String> {
    List<ConsultationHistory> findByDisease(String disease);
    List<ConsultationHistory> findByDoctor(String doctor);
    long count();
}