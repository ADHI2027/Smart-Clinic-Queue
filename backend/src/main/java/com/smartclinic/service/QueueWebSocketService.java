package com.smartclinic.service;

import com.smartclinic.dto.QueueResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueueWebSocketService {
    private final SimpMessagingTemplate messagingTemplate;
    private static final String QUEUE_TOPIC = "/topic/queue";
    
    public void broadcastQueueUpdate(QueueResponse queueResponse) {
        System.out.println("[DEBUG] Broadcasting queue update to " + QUEUE_TOPIC);
        messagingTemplate.convertAndSend(QUEUE_TOPIC, queueResponse);
    }
}