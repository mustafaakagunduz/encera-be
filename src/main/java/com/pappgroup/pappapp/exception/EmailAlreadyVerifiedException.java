// EmailAlreadyVerifiedException.java
package com.pappgroup.pappapp.exception;

public class EmailAlreadyVerifiedException extends RuntimeException {
    public EmailAlreadyVerifiedException(String message) {
        super(message);
    }
}