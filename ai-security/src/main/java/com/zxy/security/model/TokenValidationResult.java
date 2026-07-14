package com.zxy.security.model;

public record TokenValidationResult(
        boolean valid,
        String message,
        SecurityUser user
) {

    public static TokenValidationResult success(SecurityUser user) {
        return new TokenValidationResult(true, "OK", user);
    }

    public static TokenValidationResult failure(String message) {
        return new TokenValidationResult(false, message, null);
    }
}

