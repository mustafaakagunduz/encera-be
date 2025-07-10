// TooManyAttemptsException.java
package com.pappgroup.pappapp.exception;

public class TooManyAttemptsException extends RuntimeException {
    public TooManyAttemptsException(String message) {
        super(message);
    }
}