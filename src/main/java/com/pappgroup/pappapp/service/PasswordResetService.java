package com.pappgroup.pappapp.service;

import com.pappgroup.pappapp.dto.request.ForgotPasswordRequest;
import com.pappgroup.pappapp.dto.request.ResetPasswordRequest;
import com.pappgroup.pappapp.dto.response.PasswordResetResponse;
import com.pappgroup.pappapp.entity.User;
import com.pappgroup.pappapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.password.reset.token.expiry:3600000}") // 1 saat default
    private long resetTokenExpiryTime;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int TOKEN_LENGTH = 32;
    private final SecureRandom random = new SecureRandom();

    public PasswordResetResponse sendResetPasswordEmail(ForgotPasswordRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            // Güvenlik için hep başarılı mesaj döndürüyoruz
            return new PasswordResetResponse(true,
                    "Eğer bu email adresi sistemde kayıtlı ise, şifre sıfırlama linki gönderilmiştir.");
        }

        User user = userOptional.get();

        // Reset token oluştur
        String resetToken = generateResetToken();
        LocalDateTime expiryTime = LocalDateTime.now().plusSeconds(resetTokenExpiryTime / 1000);

        user.setResetToken(resetToken);
        user.setResetTokenExpiresAt(expiryTime);

        userRepository.save(user);

        // Email gönder
        try {
            emailService.sendPasswordResetEmail(user.getEmail(), user.getFirstName(), resetToken);
            return new PasswordResetResponse(true,
                    "Eğer bu email adresi sistemde kayıtlı ise, şifre sıfırlama linki gönderilmiştir.");
        } catch (Exception e) {
            // Log the error but don't expose it to user
            System.err.println("Password reset email could not be sent: " + e.getMessage());
            return new PasswordResetResponse(false,
                    "Email gönderilirken hata oluştu. Lütfen tekrar deneyin.");
        }
    }

    public PasswordResetResponse resetPassword(ResetPasswordRequest request) {
        Optional<User> userOptional = userRepository.findByResetToken(request.getToken());

        if (userOptional.isEmpty()) {
            return new PasswordResetResponse(false, "Geçersiz veya süresi dolmuş reset token.");
        }

        User user = userOptional.get();

        // Token süresi kontrolü
        if (user.getResetTokenExpiresAt().isBefore(LocalDateTime.now())) {
            // Süresi dolmuş token'ı temizle
            user.setResetToken(null);
            user.setResetTokenExpiresAt(null);
            userRepository.save(user);

            return new PasswordResetResponse(false, "Reset token'ın süresi dolmuş. Yeni reset talebi oluşturun.");
        }

        // Şifreyi güncelle
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiresAt(null);

        userRepository.save(user);

        // Başarılı şifre değişikliği emaili gönder
        try {
            emailService.sendPasswordChangeConfirmationEmail(user.getEmail(), user.getFirstName());
        } catch (Exception e) {
            // Email gönderilmese de şifre değişikliği başarılı sayılsın
            System.err.println("Password change confirmation email could not be sent: " + e.getMessage());
        }

        return new PasswordResetResponse(true, "Şifreniz başarıyla güncellendi. Yeni şifrenizle giriş yapabilirsiniz.");
    }

    public PasswordResetResponse validateResetToken(String token) {
        Optional<User> userOptional = userRepository.findByResetToken(token);

        if (userOptional.isEmpty()) {
            return new PasswordResetResponse(false, "Geçersiz reset token.");
        }

        User user = userOptional.get();

        if (user.getResetTokenExpiresAt().isBefore(LocalDateTime.now())) {
            return new PasswordResetResponse(false, "Reset token'ın süresi dolmuş.");
        }

        return new PasswordResetResponse(true, "Token geçerli.");
    }

    private String generateResetToken() {
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < TOKEN_LENGTH; i++) {
            token.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return token.toString();
    }
}