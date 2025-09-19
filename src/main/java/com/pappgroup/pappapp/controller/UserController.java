package com.pappgroup.pappapp.controller;

import com.pappgroup.pappapp.dto.request.ChangePasswordRequest;
import com.pappgroup.pappapp.dto.request.ProfileUpdateRequest;
import com.pappgroup.pappapp.dto.request.UserPreferencesUpdateRequest;
import com.pappgroup.pappapp.dto.request.PhoneVerificationRequest;
import com.pappgroup.pappapp.dto.request.PhoneVerificationCodeRequest;
import com.pappgroup.pappapp.dto.response.UserResponse;
import com.pappgroup.pappapp.dto.response.ErrorResponse;
import com.pappgroup.pappapp.dto.response.SuccessResponse;
import com.pappgroup.pappapp.service.UserService;
import com.pappgroup.pappapp.service.PhoneVerificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"}, allowCredentials = "true")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PhoneVerificationService phoneVerificationService;

    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getProfile() {
        try {
            UserResponse response = userService.getCurrentUser();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse("Failed to get profile", e.getMessage())
            );
        }
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody ProfileUpdateRequest request) {
        try {
            UserResponse response = userService.updateProfile(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse("Failed to update profile", e.getMessage())
            );
        }
    }

    @PutMapping("/preferences")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> updatePreferences(@Valid @RequestBody UserPreferencesUpdateRequest request) {
        try {
            UserResponse response = userService.updatePreferences(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse("Failed to update preferences", e.getMessage())
            );
        }
    }

    @PostMapping("/change-password")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        try {
            userService.changePassword(request);
            return ResponseEntity.ok(new SuccessResponse("Password changed successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse("Failed to change password", e.getMessage())
            );
        }
    }

    @PostMapping("/send-phone-verification")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> sendPhoneVerification(@Valid @RequestBody PhoneVerificationRequest request) {
        try {
            phoneVerificationService.sendPhoneVerificationCode(request.getPhoneNumber());
            return ResponseEntity.ok(new SuccessResponse("Verification code sent to your phone"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse("Failed to send verification code", e.getMessage())
            );
        }
    }

    @PostMapping("/verify-phone")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> verifyPhone(@Valid @RequestBody PhoneVerificationCodeRequest request) {
        try {
            phoneVerificationService.verifyPhoneCode(request.getPhoneNumber(), request.getVerificationCode());
            return ResponseEntity.ok(new SuccessResponse("Phone number verified successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse("Failed to verify phone number", e.getMessage())
            );
        }
    }

    @PostMapping("/upload-profile-picture")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> uploadProfilePicture(@RequestParam("profilePictureUrl") String profilePictureUrl) {
        try {
            userService.updateProfilePicture(profilePictureUrl);
            return ResponseEntity.ok(new SuccessResponse("Profile picture updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse("Failed to update profile picture", e.getMessage())
            );
        }
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        try {
            UserResponse userResponse = userService.getUserById(userId);
            return ResponseEntity.ok(userResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse("Failed to get user", e.getMessage())
            );
        }
    }
}