package com.pappgroup.pappapp.dto.response;

import com.pappgroup.pappapp.enums.Role;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Role role;
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Profile fields
    private String bio;
    private String location;
    private String profilePictureUrl;

    // Verification status
    private Boolean isVerified;
    private Boolean isPhoneVerified;

    // User preferences
    private String preferredLanguage;
    private String themePreference;
    private Boolean emailNotificationsEnabled;
    private Boolean smsNotificationsEnabled;
    private Boolean newListingAlertsEnabled;
    private Boolean priceChangeAlertsEnabled;
    private Boolean marketingEmailsEnabled;
}