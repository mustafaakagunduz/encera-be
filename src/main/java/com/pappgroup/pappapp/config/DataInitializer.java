package com.pappgroup.pappapp.config;

import com.pappgroup.pappapp.entity.User;
import com.pappgroup.pappapp.enums.Role;
import com.pappgroup.pappapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initializeUsers();
    }

    private void initializeUsers() {
        String defaultPassword = "Sifre123!";
        String encodedPassword = passwordEncoder.encode(defaultPassword);

        // 2 USER rolünde kullanıcı
        createUserIfNotExists("user1@example.com", "Ahmet", "Yılmaz", Role.USER, encodedPassword);
        createUserIfNotExists("user2@example.com", "Ayşe", "Kaya", Role.USER, encodedPassword);

        // 2 ADMIN rolünde kullanıcı
        createUserIfNotExists("admin1@example.com", "Mehmet", "Admin", Role.ADMIN, encodedPassword);
        createUserIfNotExists("admin2@example.com", "Fatma", "Manager", Role.ADMIN, encodedPassword);

        log.info("Kullanıcılar başarıyla oluşturuldu veya zaten mevcut. Şifre: {}", defaultPassword);
    }

    private void createUserIfNotExists(String email, String firstName, String lastName, Role role, String encodedPassword) {
        if (!userRepository.existsByEmail(email)) {
            User user = new User();
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setRole(role);
            user.setPassword(encodedPassword);
            user.setEnabled(true);
            user.setIsVerified(true);
            user.setPhoneNumber("+90555" + String.valueOf((int)(Math.random() * 1000000)));
            user.setIsPhoneVerified(false);

            userRepository.save(user);
            log.info("{} rolünde {} kullanıcısı oluşturuldu", role, email);
        } else {
            log.info("{} kullanıcısı zaten mevcut", email);
        }
    }
}