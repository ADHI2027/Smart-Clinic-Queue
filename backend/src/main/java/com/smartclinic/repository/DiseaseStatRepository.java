package com.smartclinic.repository;

import com.smartclinic.model.DiseaseStat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiseaseStatRepository extends MongoRepository<DiseaseStat, String> {
    Optional<DiseaseStat> findByDisease(String disease);
}