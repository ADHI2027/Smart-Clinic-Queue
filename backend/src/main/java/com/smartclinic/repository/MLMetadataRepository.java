package com.smartclinic.repository;

import com.smartclinic.model.MLMetadata;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MLMetadataRepository extends MongoRepository<MLMetadata, String> {
    Optional<MLMetadata> findByIsActiveTrue();
}