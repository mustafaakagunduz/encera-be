package com.pappgroup.pappapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);

    public void sendVerificationCode(String phoneNumber, String verificationCode) {
        // TODO: Integrate with real SMS provider (Twilio, AWS SNS, etc.)
        // For now, we'll just log the code
        logger.info("Sending SMS verification code to {}: {}", phoneNumber, verificationCode);

        // In development, you could also store this in a test table or send to console
        System.out.println("=== SMS VERIFICATION ===");
        System.out.println("Phone: " + phoneNumber);
        System.out.println("Code: " + verificationCode);
        System.out.println("========================");

        // Simulate SMS sending delay
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void sendPasswordResetCode(String phoneNumber, String resetCode) {
        logger.info("Sending SMS password reset code to {}: {}", phoneNumber, resetCode);

        System.out.println("=== SMS PASSWORD RESET ===");
        System.out.println("Phone: " + phoneNumber);
        System.out.println("Reset Code: " + resetCode);
        System.out.println("===========================");
    }

    public void sendNotification(String phoneNumber, String message) {
        logger.info("Sending SMS notification to {}: {}", phoneNumber, message);

        System.out.println("=== SMS NOTIFICATION ===");
        System.out.println("Phone: " + phoneNumber);
        System.out.println("Message: " + message);
        System.out.println("========================");
    }
}