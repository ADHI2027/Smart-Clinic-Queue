package com.smartclinic.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "whatsapp_notifications")
public class WhatsAppNotification {
    @Id
    private String id;
    private String phone;
    private String recipientName;
    private String message;
    private LocalDateTime sentAt;
    private String status; // SENT, SENT_TWILIO, FAILED_TWILIO
}
