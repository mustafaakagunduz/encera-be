package com.pappgroup.pappapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OAuthResponse {
    private boolean needsCompletion;
    private String temporaryToken;
    private String email;
    private String message;

    // Eğer registration tamamsa JWT token
    private String token;
    private String refreshToken;
    private UserResponse user;
}