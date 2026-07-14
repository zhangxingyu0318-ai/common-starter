package com.zxy.auth.model;

import java.util.List;

public record TokenValidationResponse(
        boolean valid,
        String message,
        String userId,
        String username,
        String displayName,
        List<String> roles
) {
}

