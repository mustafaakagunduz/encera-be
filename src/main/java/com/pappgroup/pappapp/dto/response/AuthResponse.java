package com.pappgroup.pappapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String refreshToken;
    private String type = "Bearer";
    private UserResponse user;
    private String message;

    public AuthResponse(String token, String refreshToken, UserResponse user) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.user = user;
    }

    public AuthResponse(String token, String refreshToken, UserResponse user, String message) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.user = user;
        this.message = message;
    }
}