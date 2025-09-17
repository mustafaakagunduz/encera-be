package com.pappgroup.pappapp.service;

import com.pappgroup.pappapp.entity.User;
import com.pappgroup.pappapp.exception.TooManyAttemptsException;
import com.pappgroup.pappapp.exception.UserNotFoundException;
import com.pappgroup.pappapp.exception.VerificationCodeExpiredException;
import com.pappgroup.pappapp.repository.UserRepository;
import com.pappgroup.pappapp.util.VerificationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PhoneVerificationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SmsService smsService; // You'll need to create this

    private static final int MAX_VERIFICATION_ATTEMPTS = 5;
    private static final int CODE_EXPIRY_MINUTES = 10;

    public void sendPhoneVerificationCode(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UserNotFoundException("User not found with phone number: " + phoneNumber));

        // Check if user has exceeded max attempts
        if (user.getMaxPhoneVerificationAttempts() != null &&
            user.getMaxPhoneVerificationAttempts() >= MAX_VERIFICATION_ATTEMPTS) {
            LocalDateTime lastAttempt = user.getLastPhoneVerificationAttempt();
            if (lastAttempt != null && lastAttempt.isAfter(LocalDateTime.now().minusHours(1))) {
                throw new TooManyAttemptsException("Too many verification attempts. Please try again later.");
            } else {
                // Reset attempts after 1 hour
                user.setMaxPhoneVerificationAttempts(0);
            }
        }

        // Generate verification code
        String verificationCode = VerificationUtils.generateCode();
        user.setPhoneVerificationCode(verificationCode);
        user.setPhoneVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(CODE_EXPIRY_MINUTES));
        user.setLastPhoneVerificationAttempt(LocalDateTime.now());

        // Increment attempts
        int currentAttempts = user.getMaxPhoneVerificationAttempts() != null ? user.getMaxPhoneVerificationAttempts() : 0;
        user.setMaxPhoneVerificationAttempts(currentAttempts + 1);

        userRepository.save(user);

        // Send SMS (implement SmsService)
        smsService.sendVerificationCode(phoneNumber, verificationCode);
    }

    public void verifyPhoneCode(String phoneNumber, String code) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UserNotFoundException("User not found with phone number: " + phoneNumber));

        if (user.getPhoneVerificationCode() == null ||
            !user.getPhoneVerificationCode().equals(code)) {
            throw new IllegalArgumentException("Invalid verification code");
        }

        if (user.getPhoneVerificationCodeExpiresAt() == null ||
            user.getPhoneVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new VerificationCodeExpiredException("Verification code has expired");
        }

        // Mark phone as verified
        user.setIsPhoneVerified(true);
        user.setPhoneVerificationCode(null);
        user.setPhoneVerificationCodeExpiresAt(null);
        user.setMaxPhoneVerificationAttempts(0);

        userRepository.save(user);
    }

    public boolean isPhoneVerified(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UserNotFoundException("User not found with phone number: " + phoneNumber));
        return user.getIsPhoneVerified() != null && user.getIsPhoneVerified();
    }
}