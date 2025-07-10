// VerificationCodeExpiredException.java
package com.pappgroup.pappapp.exception;

public class VerificationCodeExpiredException extends RuntimeException {
    public VerificationCodeExpiredException(String message) {
        super(message);
    }
}