package com.pappgroup.pappapp.entity;

import com.pappgroup.pappapp.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(nullable = true)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Column(nullable = false)
    private boolean enabled = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "verification_code")
    private String verificationCode;

    @Column(name = "verification_code_expires_at")
    private LocalDateTime verificationCodeExpiresAt;

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;

    @Column(name = "max_verification_attempts")
    private Integer maxVerificationAttempts = 0;

    @Column(name = "last_verification_attempt")
    private LocalDateTime lastVerificationAttempt;

    @Column(name = "reset_token")
    private String resetToken;

    @Column(name = "reset_token_expires_at")
    private LocalDateTime resetTokenExpiresAt;

    @Column(name = "oauth_provider")
    private String oauthProvider;

    @Column(name = "oauth_id")
    private String oauthId;

    @Column(name = "is_oauth_user")
    private Boolean isOauthUser = false;

    // Profile fields
    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "location")
    private String location;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    // Phone verification fields
    @Column(name = "phone_verification_code")
    private String phoneVerificationCode;

    @Column(name = "phone_verification_code_expires_at")
    private LocalDateTime phoneVerificationCodeExpiresAt;

    @Column(name = "is_phone_verified", columnDefinition = "boolean default false")
    private Boolean isPhoneVerified = false;

    @Column(name = "max_phone_verification_attempts", columnDefinition = "integer default 0")
    private Integer maxPhoneVerificationAttempts = 0;

    @Column(name = "last_phone_verification_attempt")
    private LocalDateTime lastPhoneVerificationAttempt;

    // User preferences
    @Column(name = "preferred_language", columnDefinition = "varchar(10) default 'tr'")
    private String preferredLanguage = "tr";

    @Column(name = "theme_preference", columnDefinition = "varchar(20) default 'light'")
    private String themePreference = "light";

    @Column(name = "email_notifications_enabled", columnDefinition = "boolean default true")
    private Boolean emailNotificationsEnabled = true;

    @Column(name = "sms_notifications_enabled", columnDefinition = "boolean default false")
    private Boolean smsNotificationsEnabled = false;

    @Column(name = "new_listing_alerts_enabled", columnDefinition = "boolean default true")
    private Boolean newListingAlertsEnabled = true;

    @Column(name = "price_change_alerts_enabled", columnDefinition = "boolean default true")
    private Boolean priceChangeAlertsEnabled = true;

    @Column(name = "marketing_emails_enabled", columnDefinition = "boolean default false")
    private Boolean marketingEmailsEnabled = false;
}