package com.pappgroup.pappapp.service;

import com.pappgroup.pappapp.dto.request.ChangePasswordRequest;
import com.pappgroup.pappapp.dto.request.ProfileUpdateRequest;
import com.pappgroup.pappapp.dto.request.UserPreferencesUpdateRequest;
import com.pappgroup.pappapp.dto.response.UserResponse;
import com.pappgroup.pappapp.entity.User;
import com.pappgroup.pappapp.repository.UserRepository;
import com.pappgroup.pappapp.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserResponse getCurrentUser() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return convertToUserResponse(user);
    }

    public UserResponse updateProfile(ProfileUpdateRequest request) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if email is changing and if it's already taken
        if (!user.getEmail().equals(request.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email is already in use");
            }
            user.setEmail(request.getEmail());
            user.setIsVerified(false); // Reset email verification
        }

        // Check if phone is changing and if it's already taken
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().equals(user.getPhoneNumber())) {
            if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                throw new RuntimeException("Phone number is already in use");
            }
            user.setPhoneNumber(request.getPhoneNumber());
            user.setIsPhoneVerified(false); // Reset phone verification
        }

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setBio(request.getBio());
        user.setLocation(request.getLocation());

        User updatedUser = userRepository.save(user);
        return convertToUserResponse(updatedUser);
    }

    public UserResponse updatePreferences(UserPreferencesUpdateRequest request) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getPreferredLanguage() != null) {
            user.setPreferredLanguage(request.getPreferredLanguage());
        }
        if (request.getThemePreference() != null) {
            user.setThemePreference(request.getThemePreference());
        }
        if (request.getEmailNotificationsEnabled() != null) {
            user.setEmailNotificationsEnabled(request.getEmailNotificationsEnabled());
        }
        if (request.getSmsNotificationsEnabled() != null) {
            user.setSmsNotificationsEnabled(request.getSmsNotificationsEnabled());
        }
        if (request.getNewListingAlertsEnabled() != null) {
            user.setNewListingAlertsEnabled(request.getNewListingAlertsEnabled());
        }
        if (request.getPriceChangeAlertsEnabled() != null) {
            user.setPriceChangeAlertsEnabled(request.getPriceChangeAlertsEnabled());
        }
        if (request.getMarketingEmailsEnabled() != null) {
            user.setMarketingEmailsEnabled(request.getMarketingEmailsEnabled());
        }

        User updatedUser = userRepository.save(user);
        return convertToUserResponse(updatedUser);
    }

    public void updateProfilePicture(String profilePictureUrl) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setProfilePictureUrl(profilePictureUrl);
        userRepository.save(user);
    }

    public void changePassword(ChangePasswordRequest request) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Mevcut şifre kontrolü
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Yeni şifre set et
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public UserResponse convertToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setRole(user.getRole());
        response.setEnabled(user.isEnabled());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());

        // Profile fields
        response.setBio(user.getBio());
        response.setLocation(user.getLocation());
        response.setProfilePictureUrl(user.getProfilePictureUrl());

        // Verification status
        response.setIsVerified(user.getIsVerified());
        response.setIsPhoneVerified(user.getIsPhoneVerified());

        // User preferences
        response.setPreferredLanguage(user.getPreferredLanguage());
        response.setThemePreference(user.getThemePreference());
        response.setEmailNotificationsEnabled(user.getEmailNotificationsEnabled());
        response.setSmsNotificationsEnabled(user.getSmsNotificationsEnabled());
        response.setNewListingAlertsEnabled(user.getNewListingAlertsEnabled());
        response.setPriceChangeAlertsEnabled(user.getPriceChangeAlertsEnabled());
        response.setMarketingEmailsEnabled(user.getMarketingEmailsEnabled());

        return response;
    }
}