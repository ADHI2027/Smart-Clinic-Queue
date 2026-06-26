package com.smartclinic.controller;

import com.smartclinic.model.WhatsAppNotification;
import com.smartclinic.repository.WhatsAppNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/whatsapp")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WhatsAppController {

    private final WhatsAppNotificationRepository whatsappNotificationRepository;

    @GetMapping("/notifications")
    public ResponseEntity<List<WhatsAppNotification>> getNotifications() {
        return ResponseEntity.ok(whatsappNotificationRepository.findAll());
    }
}
