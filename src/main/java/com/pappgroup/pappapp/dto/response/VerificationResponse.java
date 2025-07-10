package com.pappgroup.pappapp.dto.response;

import lombok.Data;

@Data
public class VerificationResponse {
    private boolean success;
    private String message;
    private String token;
    private Object user;

    // Constructors
    public VerificationResponse() {}

    public VerificationResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public VerificationResponse(boolean success, String message, String token, Object user) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.user = user;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Object getUser() {
        return user;
    }

    public void setUser(Object user) {
        this.user = user;
    }
}