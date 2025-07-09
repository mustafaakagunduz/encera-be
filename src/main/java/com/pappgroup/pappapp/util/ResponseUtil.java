package com.pappgroup.pappapp.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {

    public static ResponseEntity<Object> success(Object data) {
        return ResponseEntity.ok(data);
    }

    public static ResponseEntity<Object> success(String message) {
        return ResponseEntity.ok(new SuccessResponse(message));
    }

    public static ResponseEntity<Object> error(String message, HttpStatus status) {
        return ResponseEntity.status(status).body(new ErrorResponse(message));
    }

    public static ResponseEntity<Object> badRequest(String message) {
        return ResponseEntity.badRequest().body(new ErrorResponse(message));
    }

    public static ResponseEntity<Object> unauthorized(String message) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(message));
    }

    public static ResponseEntity<Object> forbidden(String message) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(message));
    }

    public static ResponseEntity<Object> notFound(String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(message));
    }

    public static class SuccessResponse {
        private String message;

        public SuccessResponse(String message) {
            this.message = message;
        }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }
}