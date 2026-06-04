package com.example.multi_tenant_notifcation_ankit_kathait.exception;

public class RateLimitExceededException extends RuntimeException {
    public RateLimitExceededException(String message) {
        super(message);
    }
}