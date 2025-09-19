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
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        loadInitialData();
    }

    private void loadInitialData() {
        // Create or update Encera admin user
        createOrUpdateEnceraUser();
    }

    private void createOrUpdateEnceraUser() {
        String enceraEmail = "admin@encera.com";

        User enceraUser = userRepository.findByEmail(enceraEmail)
                .orElse(null);

        if (enceraUser == null) {
            // Create new Encera user
            enceraUser = new User();
            enceraUser.setEmail(enceraEmail);
            enceraUser.setFirstName("Encera");
            enceraUser.setLastName("");
            enceraUser.setPhoneNumber("5356021168");
            enceraUser.setPassword(passwordEncoder.encode("encera123"));
            enceraUser.setRole(Role.ADMIN);
            enceraUser.setEnabled(true);
            enceraUser.setIsVerified(true);
            enceraUser.setIsPhoneVerified(true);

            userRepository.save(enceraUser);
            log.info("Created Encera admin user with phone number: 5356021168");
        } else {
            // Update existing Encera user phone number if not set
            if (enceraUser.getPhoneNumber() == null || enceraUser.getPhoneNumber().isEmpty()) {
                enceraUser.setPhoneNumber("5356021168");
                enceraUser.setIsPhoneVerified(true);
                userRepository.save(enceraUser);
                log.info("Updated Encera admin user with phone number: 5356021168");
            } else {
                log.info("Encera admin user already has phone number: {}", enceraUser.getPhoneNumber());
            }
        }
    }
}