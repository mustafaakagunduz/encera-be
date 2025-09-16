package com.pappgroup.pappapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OAuthCompleteRequest {
    @NotBlank(message = "Ad gereklidir")
    private String firstName;

    @NotBlank(message = "Soyad gereklidir")
    private String lastName;

    private String oauthToken;
}