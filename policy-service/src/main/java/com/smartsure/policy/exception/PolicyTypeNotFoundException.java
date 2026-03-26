package com.smartsure.policy.exception;

public class PolicyTypeNotFoundException extends RuntimeException {
    public PolicyTypeNotFoundException(String message) {
        super(message);
    }
}