package com.pappgroup.pappapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PhoneVerificationCodeRequest {

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+90[0-9]{10}$", message = "Phone number must be in Turkish format (+90XXXXXXXXXX)")
    private String phoneNumber;

    @NotBlank(message = "Verification code is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "Verification code must be 6 digits")
    private String verificationCode;
}