package com.smartclinic.repository;

import com.smartclinic.model.DoctorStat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorStatRepository extends MongoRepository<DoctorStat, String> {
    Optional<DoctorStat> findByDoctor(String doctor);
}