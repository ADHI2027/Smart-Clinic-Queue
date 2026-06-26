package com.smartclinic.repository;

import com.smartclinic.model.WhatsAppNotification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WhatsAppNotificationRepository extends MongoRepository<WhatsAppNotification, String> {
}
