// VerificationUtils.java
package com.pappgroup.pappapp.util;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class VerificationUtils {

    private static final String DIGITS = "0123456789";
    private static final SecureRandom random = new SecureRandom();

    public static String generateRandomCode(int length) {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        }
        return code.toString();
    }

    public static String generateCode() {
        return generateRandomCode(6);
    }

    public static boolean isCodeExpired(LocalDateTime expiryTime) {
        return expiryTime != null && expiryTime.isBefore(LocalDateTime.now());
    }

    public static boolean isBlocked(LocalDateTime lastAttempt, long blockTimeMillis) {
        if (lastAttempt == null) return false;

        LocalDateTime blockUntil = lastAttempt.plusSeconds(blockTimeMillis / 1000);
        return LocalDateTime.now().isBefore(blockUntil);
    }

    public static String formatRemainingTime(LocalDateTime blockUntil) {
        if (blockUntil == null || LocalDateTime.now().isAfter(blockUntil)) {
            return "0";
        }

        long seconds = java.time.Duration.between(LocalDateTime.now(), blockUntil).getSeconds();
        long minutes = seconds / 60;
        seconds = seconds % 60;

        if (minutes > 0) {
            return minutes + " dakika " + seconds + " saniye";
        } else {
            return seconds + " saniye";
        }
    }

    public static boolean isValidEmailFormat(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    public static boolean isValidVerificationCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return false;
        }

        return code.matches("^[0-9]{6}$");
    }
}