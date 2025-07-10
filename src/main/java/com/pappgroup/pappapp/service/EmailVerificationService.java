package com.pappgroup.pappapp.service;

import com.pappgroup.pappapp.dto.request.EmailVerificationRequest;
import com.pappgroup.pappapp.dto.response.VerificationResponse;
import com.pappgroup.pappapp.entity.User;
import com.pappgroup.pappapp.repository.UserRepository;
import com.pappgroup.pappapp.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class EmailVerificationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtTokenProvider jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Value("${app.verification.code.expiry:900000}") // 15 dakika default
    private long codeExpiryTime;

    @Value("${app.verification.max.attempts:3}")
    private int maxAttempts;

    @Value("${app.verification.block.time:900000}") // 15 dakika block
    private long blockTime;

    public VerificationResponse sendVerificationCode(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            return new VerificationResponse(false, "Bu email adresi ile kayıtlı kullanıcı bulunamadı.");
        }

        User user = userOptional.get();

        if (user.getIsVerified()) {
            return new VerificationResponse(false, "Bu hesap zaten doğrulanmış.");
        }

        // Rate limiting kontrolü
        if (isBlocked(user)) {
            return new VerificationResponse(false, "Çok fazla deneme yapıldı. Lütfen 15 dakika sonra tekrar deneyin.");
        }

        // Yeni doğrulama kodu üret
        String verificationCode = emailService.generateVerificationCode();
        LocalDateTime expiryTime = LocalDateTime.now().plusSeconds(codeExpiryTime / 1000);

        user.setVerificationCode(verificationCode);
        user.setVerificationCodeExpiresAt(expiryTime);
        user.setMaxVerificationAttempts(0); // Yeni kod gönderildiğinde deneme sayısını sıfırla

        userRepository.save(user);

        // Email gönder
        try {
            emailService.sendVerificationEmail(user.getEmail(), user.getFirstName(), verificationCode);
            return new VerificationResponse(true, "Doğrulama kodu email adresinize gönderildi.");
        } catch (Exception e) {
            return new VerificationResponse(false, "Email gönderilirken hata oluştu. Lütfen tekrar deneyin.");
        }
    }

    public VerificationResponse verifyEmail(EmailVerificationRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            return new VerificationResponse(false, "Bu email adresi ile kayıtlı kullanıcı bulunamadı.");
        }

        User user = userOptional.get();

        if (user.getIsVerified()) {
            return new VerificationResponse(false, "Bu hesap zaten doğrulanmış.");
        }

        // Rate limiting kontrolü
        if (isBlocked(user)) {
            return new VerificationResponse(false, "Çok fazla hatalı deneme yapıldı. Lütfen 15 dakika sonra tekrar deneyin.");
        }

        // Kod kontrolü
        if (user.getVerificationCode() == null || !user.getVerificationCode().equals(request.getVerificationCode())) {
            // Hatalı deneme sayısını artır
            user.setMaxVerificationAttempts(user.getMaxVerificationAttempts() + 1);
            user.setLastVerificationAttempt(LocalDateTime.now());
            userRepository.save(user);

            return new VerificationResponse(false, "Geçersiz doğrulama kodu.");
        }

        // Kod süresi kontrolü
        if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            return new VerificationResponse(false, "Doğrulama kodunun süresi dolmuş. Yeni kod talep edin.");
        }

        // Doğrulama başarılı
        user.setIsVerified(true);
        user.setEnabled(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiresAt(null);
        user.setMaxVerificationAttempts(0);
        user.setLastVerificationAttempt(null);

        userRepository.save(user);

        // JWT token üret
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(user.getEmail(),user.getRole().toString(),user.getId());

        // Security context'e kullanıcıyı set et
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Hoş geldin emaili gönder
        try {
            emailService.sendWelcomeEmail(user.getEmail(), user.getFirstName());
        } catch (Exception e) {
            // Hoş geldin emaili gönderilmese de doğrulama başarılı sayılsın
            System.err.println("Hoş geldin emaili gönderilemedi: " + e.getMessage());
        }

        return new VerificationResponse(true, "Email doğrulaması başarılı. Hoş geldiniz!", token, user);
    }

    private boolean isBlocked(User user) {
        if (user.getMaxVerificationAttempts() >= maxAttempts && user.getLastVerificationAttempt() != null) {
            LocalDateTime blockUntil = user.getLastVerificationAttempt().plusSeconds(blockTime / 1000);
            return LocalDateTime.now().isBefore(blockUntil);
        }
        return false;
    }
}