package com.pappgroup.pappapp.service;

import com.pappgroup.pappapp.dto.request.LoginRequest;
import com.pappgroup.pappapp.dto.request.RegisterRequest;
import com.pappgroup.pappapp.dto.response.AuthResponse;
import com.pappgroup.pappapp.dto.response.UserResponse;
import com.pappgroup.pappapp.entity.User;
import com.pappgroup.pappapp.enums.Role;
import com.pappgroup.pappapp.exception.InvalidCredentialsException;
import com.pappgroup.pappapp.exception.UserNotFoundException;
import com.pappgroup.pappapp.repository.UserRepository;
import com.pappgroup.pappapp.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private EmailVerificationService emailVerificationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserService userService;

    public AuthResponse register(RegisterRequest request) {
        // Email kontrolü
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Kullanıcı oluştur
        User user = new User();
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        user.setEnabled(false); // Email doğrulanana kadar pasif
        user.setIsVerified(false); // Email doğrulanmamış

        User savedUser = userRepository.save(user);

        // Doğrulama kodu gönder
        emailVerificationService.sendVerificationCode(savedUser.getEmail());

        // Response oluştur (token yok çünkü henüz doğrulanmamış)
        UserResponse userResponse = userService.convertToUserResponse(savedUser);

        // AuthResponse'a message alanı ekleyeceğiz
        return new AuthResponse(null, null, userResponse, "Kayıt başarılı. Doğrulama kodu email adresinize gönderildi.");
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Şifre kontrolü
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        // Email doğrulanmış mı kontrolü
        if (!user.getIsVerified()) {
            throw new RuntimeException("Email doğrulaması yapılmamış. Lütfen emailinizi kontrol edin.");
        }

        // Kullanıcı aktif mi?
        if (!user.isEnabled()) {
            throw new RuntimeException("User account is disabled");
        }

        // Token oluştur
        String token = jwtTokenProvider.generateToken(
                user.getEmail(),
                user.getRole().name(),
                user.getId()
        );

        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        // Response oluştur
        UserResponse userResponse = userService.convertToUserResponse(user);

        return new AuthResponse(token, refreshToken, userResponse);
    }

    public AuthResponse refreshToken(String refreshToken) {
        // Refresh token doğrula
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Yeni access token oluştur
        String newToken = jwtTokenProvider.generateToken(
                user.getEmail(),
                user.getRole().name(),
                user.getId()
        );

        // Response oluştur
        UserResponse userResponse = userService.convertToUserResponse(user);

        return new AuthResponse(newToken, refreshToken, userResponse);
    }
}