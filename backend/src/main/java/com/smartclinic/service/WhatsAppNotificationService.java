package com.smartclinic.service;

import com.smartclinic.model.WhatsAppNotification;
import com.smartclinic.repository.WhatsAppNotificationRepository;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WhatsAppNotificationService {

    private final WhatsAppNotificationRepository notificationRepository;

    @Value("${twilio.account.sid:}")
    private String accountSid;

    @Value("${twilio.auth.token:}")
    private String authToken;

    @Value("${twilio.whatsapp.from:}")
    private String whatsappFrom;

    @Value("${twilio.whatsapp.template.sid:HXb5b62575e6e4ff6129ad7c8efe1f983e}")
    private String templateSid;

    private boolean isTwilioConfigured = false;

    @PostConstruct
    public void init() {
        if (accountSid != null && !accountSid.trim().isEmpty() &&
            authToken != null && !authToken.trim().isEmpty() &&
            whatsappFrom != null && !whatsappFrom.trim().isEmpty()) {
            try {
                Twilio.init(accountSid, authToken);
                isTwilioConfigured = true;
                System.out.println("✅ [TWILIO] Successfully initialized Twilio WhatsApp Service.");
            } catch (Exception e) {
                System.err.println("❌ [TWILIO] Failed to initialize Twilio: " + e.getMessage());
            }
        } else {
            System.out.println("⚠️ [TWILIO] Credentials not fully configured. Using Mock SMS/WhatsApp mode.");
        }
    }

    public void sendWhatsAppMessage(String phone, String recipientName, String token, String timeValue, String messageContent) {
        String status = "SENT";
        
        // Clean phone number (Twilio requires E.164 format: e.g. +91XXXXXXXXXX)
        String formattedPhone = phone;
        if (formattedPhone == null || formattedPhone.trim().isEmpty()) {
            formattedPhone = "Unknown";
        } else {
            formattedPhone = formattedPhone.trim();
            if (!formattedPhone.startsWith("+")) {
                if (formattedPhone.startsWith("91") && formattedPhone.length() == 12) {
                    formattedPhone = "+" + formattedPhone;
                } else if (formattedPhone.length() == 10) {
                    formattedPhone = "+91" + formattedPhone;
                } else {
                    formattedPhone = "+" + formattedPhone;
                }
            }
        }

        System.out.println("📱 [WHATSAPP] Sending message to " + recipientName + " (" + formattedPhone + "): " + messageContent);

        if (isTwilioConfigured && !"Unknown".equals(formattedPhone)) {
            try {
                String to = "whatsapp:" + formattedPhone;
                String from = "whatsapp:" + whatsappFrom;
                if (!from.startsWith("whatsapp:")) {
                    from = "whatsapp:" + from;
                }
                
                String variablesJson = String.format("{\"1\":\"%s\",\"2\":\"%s\"}", token, timeValue);
                System.out.println("💡 [TWILIO] Template variables: " + variablesJson);

                Message message = Message.creator(
                        new PhoneNumber(to),
                        new PhoneNumber(from),
                        messageContent
                )
                .setContentSid(templateSid)
                .setContentVariables(variablesJson)
                .create();
                
                System.out.println("🚀 [TWILIO] Message sent successfully. SID: " + message.getSid());
                status = "SENT_TWILIO";
            } catch (Exception e) {
                System.err.println("❌ [TWILIO] Failed to send message via Twilio: " + e.getMessage());
                e.printStackTrace();
                status = "FAILED_TWILIO";
            }
        }

        WhatsAppNotification notification = WhatsAppNotification.builder()
                .phone(formattedPhone)
                .recipientName(recipientName)
                .message(messageContent)
                .sentAt(LocalDateTime.now())
                .status(status)
                .build();

        notificationRepository.save(notification);
    }

    public void sendETAChangeNotification(String phone, String name, String token, String oldTime, String newTime) {
        String msg = String.format("Hi %s, your estimated consultation time for token %s has changed from %s to %s. Please check the live dashboard.", name, token, oldTime, newTime);
        sendWhatsAppMessage(phone, name, token, newTime, msg);
    }

    public void send10MinReminder(String phone, String name, String token, String estimatedTime) {
        String msg = String.format("Hi %s, your consultation for token %s is scheduled in about 10 minutes at %s. Please make sure you are at the clinic.", name, token, estimatedTime);
        sendWhatsAppMessage(phone, name, token, estimatedTime, msg);
    }
}
